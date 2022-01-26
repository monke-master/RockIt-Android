package com.brigade.rockit.database;

import android.util.Log;

import com.brigade.rockit.data.Album;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Genre;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.data.TimeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;

public class RecommendationManager {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private String uid;
    private UserManager userManager;
    private ContentManager contentManager;
    private TimeManager timeManager;

    public RecommendationManager() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.
                getInstance(Constants.STORAGE_PATH);
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        userManager = new UserManager();
        contentManager = new ContentManager();
        timeManager = new TimeManager();
    }

    // Получение новых песен от исполнителей, на которых подписан пользователь
    public void getNewSongs(GetObjectListener listener) {
        // Получение подписок пользователя
        ArrayList<String> performers = Data.getCurUser().getFollowingList();
        ArrayList<Song> newSongs = new ArrayList<>();
        ArrayList<String> searchedUsers = new ArrayList<>();
        for (String id: performers) {
            userManager.getUserMusic(id, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<String> songs = (ArrayList<String>) object;
                    ArrayList<String> addedSongs = new ArrayList<>();
                    if (songs.size() == 0)
                        searchedUsers.add(id);
                    if (searchedUsers.size() == performers.size()) {
                        sortSongsByDate(newSongs);
                        listener.onComplete(newSongs);
                    }
                    // Проходим по песням пользователя
                    for (String songId: songs) {
                        // Получение данных о песне
                        contentManager.getSong(songId, new GetObjectListener() {
                            @Override
                            public void onComplete(Object object) {
                                Song song = (Song) object;
                                // Если песня была опубликована пользователем недавно,
                                // добавляем ее в рекомендации
                                if ((Math.abs(timeManager.getDayDiff(song.getDate(),
                                        timeManager.getDate())) <= 120) && song.getAuthor().getId().equals(id)) {
                                    newSongs.add(song);
                                }
                                addedSongs.add(songId);
                                if (addedSongs.size() == songs.size())
                                    searchedUsers.add(id);
                                if (searchedUsers.size() == performers.size()) {
                                    sortSongsByDate(newSongs);
                                    listener.onComplete(newSongs);
                                }
                            }
                            @Override
                            public void onFailure(Exception e) {
                                listener.onFailure(e);
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    listener.onFailure(e);
                }
            });
        }
    }

    // СОртировка песен по дате публикации
    private void sortSongsByDate(ArrayList<Song> songs) {
        Collections.sort(songs, (o1, o2) -> {
            int diff = (int)timeManager.getDayDiff(o1.getDate(), o2.getDate());
            if (diff > 0)
                return -1;
            else if (diff < 0)
                return 1;
            return 0;
        });
    }

    // Получение новых альбомов у исполнителей, на которых подписан пользователь
    public void getNewAlbums(GetObjectListener listener) {
        // Получение подписок пользователя
        ArrayList<String> performers = Data.getCurUser().getFollowingList();
        ArrayList<Album> newAlbums = new ArrayList<>();
        ArrayList<String> searchedUsers = new ArrayList<>();
        for (String id: performers) {
            // Получение плейлистов пользователя
            userManager.getUserPlaylists(id, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<String> playlists = (ArrayList<String>) object;
                    ArrayList<String> addedAlbums = new ArrayList<>();
                    if (playlists.size() == 0) {
                        searchedUsers.add(id);
                    }
                    if (searchedUsers.size() == performers.size()) {
                        sortAlbumsByDate(newAlbums);
                        listener.onComplete(newAlbums);
                    }
                    // Проходим по плейлистам
                    for (String playlistId: playlists) {
                        // Если это недавно добавленный эти пользователем альбом, то
                        // добавляем в рекомендации
                        if (playlistId.contains("albums/")) {
                            playlistId = playlistId.substring(playlistId.indexOf("/") + 1);
                            contentManager.getAlbum(playlistId, new GetObjectListener() {
                                @Override
                                public void onComplete(Object object) {
                                    Album album = (Album) object;
                                    if ((Math.abs(timeManager.getDayDiff(album.getDate(),
                                            timeManager.getDate())) <= 120) && album.getAuthor().getId().equals(id)) {
                                        album.setId("albums/" + album.getId());
                                        newAlbums.add(album);
                                    }
                                    Log.d("something", "wrong with uoy");
                                    addedAlbums.add(album.getId());
                                    if (addedAlbums.size() == playlists.size())
                                        searchedUsers.add(id);
                                    if (searchedUsers.size() == performers.size()) {
                                        sortAlbumsByDate(newAlbums);
                                        listener.onComplete(newAlbums);
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    listener.onFailure(e);
                                }
                            });
                        }
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    listener.onFailure(e);
                }
            });
        }
    }

    // Сортировка альбомов по дате публикации
    private void sortAlbumsByDate(ArrayList<Album> albums) {
        Collections.sort(albums, (o1, o2) -> {
            int diff = (int) timeManager.getDayDiff(o1.getDate(), o2.getDate());
            if (diff > 0)
                return -1;
            else if (diff < 0)
                return 1;
            return 0;
        });
    }

    // Получение песен, которые могут понравиться пользователю
    public void getMayLikeSongs(GetObjectListener listener) {
        // Получаем все песни
        firestore.collection("songs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Песни с любимыми жанрами пользователя
                ArrayList<Song> withFavGenreSongs = new ArrayList<>();
                // Песни с любимыми жанрами
                ArrayList<Song> mayLikeSongs = new ArrayList<>();
                // ПРросмотренные песни
                ArrayList<String> searchedSongs = new ArrayList<>();
                QuerySnapshot result = task.getResult();
                // Проходим по песням
                for (DocumentSnapshot doc: result) {
                    String genreId = doc.get("genre").toString();
                    // Получаем жанр песни
                    contentManager.getGenre(genreId, new GetObjectListener() {
                        @Override
                        public void onComplete(Object object) {
                            Genre genre = (Genre) object;
                            // ПРоверка на то, может ли пользователю эта песня понравиться
                            ArrayList<String> favouriteGenres = Data.getCurUser().getFavouriteGenres();
                            boolean mayLike = favouriteGenres.contains(genre.getParentId()) ||
                                    favouriteGenres.contains(genre.getId());
                            for (String id : genre.getSubgenres()) {
                                if (favouriteGenres.contains(id)) {
                                    mayLike = true;
                                    break;
                                }
                            }
                            // Если может
                            if (mayLike) {
                                contentManager.getSong(doc.getId(), new GetObjectListener() {
                                    @Override
                                    public void onComplete(Object object) {
                                        Song song = (Song) object;
                                        if (favouriteGenres.contains(genre.getId()) &&
                                                (withFavGenreSongs.size() < Constants.MAX_MAY_LIKE_SIZE))
                                            withFavGenreSongs.add(song);
                                        else if (mayLikeSongs.size() < Constants.MAX_MAY_LIKE_SIZE)
                                            mayLikeSongs.add(song);
                                        // Если набрали максимум подходящих песен, то
                                        if ((mayLikeSongs.size() == Constants.MAX_MAY_LIKE_SIZE) &&
                                                (withFavGenreSongs.size() == Constants.MAX_MAY_LIKE_SIZE)) {
                                            // Сортируем по популярности
                                            sortSongsByPopularity(withFavGenreSongs);
                                            sortSongsByPopularity(mayLikeSongs);
                                            // Объединяем результат
                                            withFavGenreSongs.addAll(mayLikeSongs);
                                            listener.onComplete(withFavGenreSongs);
                                        }
                                        searchedSongs.add(doc.getId());
                                        // Если прошлись по всем песням, то
                                        if (searchedSongs.size() == result.size()) {
                                            // Сортируем по популярности
                                            sortSongsByPopularity(withFavGenreSongs);
                                            sortSongsByPopularity(mayLikeSongs);
                                            // Объединяем результат
                                            withFavGenreSongs.addAll(mayLikeSongs);
                                            listener.onComplete(withFavGenreSongs);
                                        }

                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        listener.onFailure(e);
                                    }
                                });
                            } else {
                                searchedSongs.add(doc.getId());
                                // Если прошлись по всем песням, то
                                if (searchedSongs.size() == result.size()) {
                                    // Сортируем по популярности
                                    sortSongsByPopularity(withFavGenreSongs);
                                    sortSongsByPopularity(mayLikeSongs);
                                    // Объединяем результат
                                    withFavGenreSongs.addAll(mayLikeSongs);
                                    listener.onComplete(withFavGenreSongs);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            listener.onFailure(e);
                        }
                    });
                }

            } else
                listener.onFailure(task.getException());
        });
    }

    // Сортировка песен по популярности
    private void sortSongsByPopularity(ArrayList<Song> songs) {
        Collections.sort(songs, (o1, o2) -> {
            float p1, p2;
            try {
                p1 = (float) o1.getAdded() / o1.getAuditions();
            } catch (ArithmeticException exception) {
                p1 = 0;
            }
            try {
                p2 = (float)o2.getAdded() / o2.getAuditions();
            } catch (ArithmeticException exception) {
                p2 = 0;
            }
            if (p2 - p1 > 0)
                return -1;
            else if (p2 - p1 < 0)
                return 1;
            return 0;
        });
    }

    // Получение альбомов, которые могут понравиться пользователю
    public void getMayLikeAlbums(GetObjectListener listener) {
        // Получаем все альбомы
        firestore.collection("albums").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Альбомы с жанрами, которые нравятся пользователю
                ArrayList<Album> withFavGenreAlbums= new ArrayList<>();
                // Альбомы с жанрами, которые могут понравиться полльзователю
                ArrayList<Album> mayLikeAlbums = new ArrayList<>();
                ArrayList<String> searchedAlbums = new ArrayList<>();
                QuerySnapshot result = task.getResult();
                // Проходим по альбомам
                for (DocumentSnapshot doc: result) {
                    String genreId = doc.get("genre").toString();
                    // Получаем жанр альбома
                    contentManager.getGenre(genreId, new GetObjectListener() {
                        @Override
                        public void onComplete(Object object) {
                            Genre genre = (Genre) object;
                            ArrayList<String> favouriteGenres = Data.getCurUser().getFavouriteGenres();
                            // Проверка на то, может ли альбом понравиться пользователю
                            boolean mayLike = favouriteGenres.contains(genre.getParentId()) ||
                                    favouriteGenres.contains(genre.getId());
                            for (String id : genre.getSubgenres()) {
                                if (favouriteGenres.contains(id)) {
                                    mayLike = true;
                                    break;
                                }
                            }
                            // Если может
                            if (mayLike) {
                                contentManager.getAlbum(doc.getId(), new GetObjectListener() {
                                    @Override
                                    public void onComplete(Object object) {
                                        Album album = (Album) object;
                                        album.setId("albums/" + doc.getId());
                                        if (favouriteGenres.contains(genre.getId()) &&
                                                (withFavGenreAlbums.size() < Constants.MAX_MAY_LIKE_SIZE))
                                            withFavGenreAlbums.add(album);
                                        else if (mayLikeAlbums.size() < Constants.MAX_MAY_LIKE_SIZE)
                                            mayLikeAlbums.add(album);
                                        // Если набрали максимум подходящих песен, то
                                        if ((mayLikeAlbums.size() == Constants.MAX_MAY_LIKE_SIZE) &&
                                                (withFavGenreAlbums.size() == Constants.MAX_MAY_LIKE_SIZE)) {
                                            // Сортируем по популярности
                                            sortAlbumsByPopularity(withFavGenreAlbums);
                                            sortAlbumsByPopularity(mayLikeAlbums);
                                            // Объединяем результат
                                            withFavGenreAlbums.addAll(mayLikeAlbums);
                                            listener.onComplete(withFavGenreAlbums);
                                        }
                                        searchedAlbums.add(doc.getId());
                                        // Если прошлись по всем песням, то
                                        if (searchedAlbums.size() == result.size()) {
                                            // Сортируем по популярности
                                            sortAlbumsByPopularity(withFavGenreAlbums);
                                            sortAlbumsByPopularity(mayLikeAlbums);
                                            // Объединяем результат
                                            withFavGenreAlbums.addAll(mayLikeAlbums);
                                            listener.onComplete(withFavGenreAlbums);
                                        }

                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        listener.onFailure(e);
                                    }
                                });
                            } else {
                                searchedAlbums.add(doc.getId());
                                // Если прошлись по всем песням, то
                                if (searchedAlbums.size() == result.size()) {
                                    // Сортируем по популярности
                                    sortAlbumsByPopularity(withFavGenreAlbums);
                                    sortAlbumsByPopularity(mayLikeAlbums);
                                    // Объединяем результат
                                    withFavGenreAlbums.addAll(mayLikeAlbums);
                                    listener.onComplete(withFavGenreAlbums);
                                }
                            }



                        }

                        @Override
                        public void onFailure(Exception e) {
                            listener.onFailure(e);
                        }
                    });
                }

            } else
                listener.onFailure(task.getException());
        });
    }

    // Сортировка альбомов по популярности
    private void sortAlbumsByPopularity(ArrayList<Album> albums) {
        Collections.sort(albums, (o1, o2) -> {
            long p1 = o1.getAuditions();
            long p2 = o2.getAuditions();
            if (p2 - p1 > 0)
                return 1;
            else if (p2 - p1 < 0)
                return -1;
            return 0;
        });
    }
}
