package com.brigade.rockit.database;

import android.net.Uri;

import com.brigade.rockit.data.Album;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Genre;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.data.Playlist;
import com.brigade.rockit.data.Post;
import com.brigade.rockit.data.TimeManager;
import com.brigade.rockit.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

public class ContentManager {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private String uid;

    public ContentManager() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance(Constants.STORAGE_PATH);
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
    }

    // Загрузка uri файла
    public void uploadUriFile(Uri uri, String path, TaskListener listener) {
        StorageReference reference = storage.getReference(path);
        reference.putFile(uri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete();
            } else
                listener.onFailure(task.getException());
        });
    }

    // Удаление файла
    public void deleteFile(String path, TaskListener listener) {
        storage.getReference(path).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful())
                listener.onComplete();
            else
                listener.onFailure(task.getException());
        });
    }

    // Загрузка музыки
    public void uploadSong(Song song, TaskListener listener) {
        // Генерация id
        String id = UUID.randomUUID().toString();
        song.setId(id);
        // Загрузка аудио-файла
        uploadUriFile(song.getUri(), "music/" + id, new TaskListener() {
            @Override
            public void onComplete() {
                // Загрузка обложки
                if (song.getCoverUri() == null) {
                    song.setCoverPath("song_covers/default.png");
                    // Создание документа с данными песни
                    createSongDoc(song, listener);
                } else {
                    if (song.getCoverPath() == null)
                        song.setCoverPath("song_covers/" + id);
                    uploadUriFile(song.getCoverUri(), song.getCoverPath(), new TaskListener() {
                        @Override
                        public void onComplete() {
                            // Создание документа с данными песни
                            createSongDoc(song, listener);
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

    public void createSongDoc(Song song, TaskListener listener) {
        DatabaseSong dbSong = new DatabaseSong(song);
        firestore.collection("songs").document(song.getId()).set(dbSong).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addToMyMusic(song.getId(), listener);
            } else
                listener.onFailure(task.getException());
        });
    }

    // Добавление в "Мою музыку"
    public void addToMyMusic(String musicId, TaskListener listener) {
        firestore.collection("users").document(uid).
                update("music", FieldValue.arrayUnion(musicId)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete();
                increaseAdded("songs", musicId);
            }
            else
                listener.onFailure(task.getException());
        });

    }

    public void increaseAdded(String collection, String id) {
        firestore.collection(collection).document(id).update("added", FieldValue.increment(1));
    }

    public void increaseAuditions(String collection, String id) {
        firestore.collection(collection).document(id).update("auditions",
                FieldValue.increment(1));
    }

    public void decreaseAdded(String colection, String id) {
        firestore.collection(colection).document(id).update("added", FieldValue.increment(-1));
    }

    // Удаление песни из "Моей музыки"
    public void deleteSongFromMyMusic(Song song, TaskListener listener) {
        DocumentReference userRef = firestore.collection("users").document(uid);
        // Получение списка песен
        userRef.update("music", FieldValue.arrayRemove(song.getId())).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete();
                decreaseAdded("songs", song.getId());
            }
            else {
                listener.onFailure(task.getException());
            }

        });
    }

    // Удаление песни из бд
    public void deleteSong(Song song, TaskListener listener) {
        deleteSongFromMyMusic(song, new TaskListener() {
            @Override
            public void onComplete() {
                // Получение данных о песни
                DocumentReference songRef = firestore.collection("songs").document(song.getId());
                songRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Удаление аудио-файла
                        deleteFile("music/" + song.getId(), new TaskListener() {
                            @Override
                            public void onComplete() {
                                if (!task.getResult().get("cover").toString().equals("song_covers/default.png")) {
                                    // Удаление обложки
                                    deleteFile(task.getResult().get("cover").toString(), new TaskListener() {
                                        @Override
                                        public void onComplete() {
                                            songRef.delete().addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful())
                                                    listener.onComplete();
                                                else
                                                    listener.onFailure(task1.getException());
                                            });
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            listener.onFailure(e);
                                        }
                                    });
                                } else {
                                    songRef.delete().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful())
                                            listener.onComplete();
                                        else
                                            listener.onFailure(task1.getException());
                                    });
                                }
                            }
                            @Override
                            public void onFailure(Exception e) {
                                listener.onFailure(e);
                            }
                        });
                    } else
                        listener.onFailure(task.getException());
                });
            }
            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });

    }


    // Загрузка поста
    public void uploadPost(Post post, TaskListener listener) {
        // Преобразование данных для загрузки в бд
        DatabasePost dbPost = new DatabasePost();
        dbPost.setAuthorId(auth.getUid());
        dbPost.setText(post.getText());
        // Преобразование даты
        TimeManager manager = new TimeManager();
        dbPost.setDate(manager.getDate());
        // Получение id прикрепленных файлов
        dbPost.setSongsIds(post.getSongsIds());
        // Загрузка в бд
        firestore.collection("posts").add(dbPost).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String postId = task.getResult().getId();
                addPost(postId, new TaskListener() {
                    @Override
                    public void onComplete() {
                        // Загрузка фото
                        for (Uri uri: post.getImagesList()) {
                            String path = "post_images/" + postId + "/" + UUID.randomUUID().toString();
                            uploadUriFile(uri, path, new TaskListener() {
                                @Override
                                public void onComplete() {
                                    dbPost.getImageIds().add(path);
                                    if (dbPost.getImageIds().size() == post.getImagesList().size()) {
                                        addPostImages(dbPost.getImageIds(), postId, new TaskListener() {
                                            @Override
                                            public void onComplete() {
                                                addPost(postId, new TaskListener() {
                                                    @Override
                                                    public void onComplete() {
                                                        listener.onComplete();
                                                    }

                                                    @Override
                                                    public void onFailure(Exception e) {
                                                        listener.onFailure(e);
                                                    }
                                                });
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
                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });
            }
        });
    }

    // Загрузка списка изображений в документ поста в бд
    public void addPostImages(ArrayList<String> ids, String postId, TaskListener listener) {
        firestore.collection("posts").document(postId).update("imageIds", ids).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        listener.onComplete();
                    else
                        listener.onFailure(task.getException());
                });
    }

    // Добавление поста пользователя
    private void addPost(String postId, TaskListener listener) {
        firestore.collection("users").document(uid).update("posts",
                FieldValue.arrayUnion(postId)).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        listener.onComplete();
                    else
                        listener.onFailure(task.getException());
        });
    }

    // Удаление поста
    public void deletePost(Post post, TaskListener listener) {
        firestore.collection("users").document(auth.getUid()).update("posts",
                FieldValue.arrayRemove(post.getId())).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference postRef = firestore.collection("posts").document(post.getId());
                postRef.get().addOnCompleteListener(task1 -> {
                    ArrayList<String> imagesIds = (ArrayList<String>) task1.getResult().get("imageIds");
                    ArrayList<String> deletedImages = new ArrayList<>();
                    if (task1.isSuccessful()) {
                        postRef.delete().addOnCompleteListener(task2 -> {
                            for (String path: imagesIds) {
                                deleteFile(path, new TaskListener() {
                                    @Override
                                    public void onComplete() {
                                        deletedImages.add(path);
                                        if (imagesIds.size() == deletedImages.size()) {
                                            listener.onComplete();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Exception e) {
                                        listener.onFailure(e);
                                    }
                                });
                            }
                        });
                    } else {
                        listener.onFailure(task1.getException());
                    }
                });
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    public void getPost(String postId, GetObjectListener listener) {
        firestore.collection("posts").document(postId).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot doc = task1.getResult();
                Post post = new Post();
                post.setText(doc.get("text").toString());
                post.setDate(doc.get("date").toString());
                post.setImagesIds((ArrayList<String>) doc.get("imageIds"));
                post.setSongsIds((ArrayList<String>) doc.get("songsIds"));
                post.setId(postId);
                UserManager userManager = new UserManager();
                userManager.getUser(doc.get("authorId").toString(), new GetObjectListener() {
                    @Override
                    public void onComplete(Object object) {
                        post.setAuthor((User) object);
                        listener.onComplete(post);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });
            }
        });
    }

    // Получение uri файла
    public void getUri(String path, GetObjectListener listener) {
        storage.getReference(path).getDownloadUrl().addOnSuccessListener(listener::onComplete).addOnFailureListener(listener::onFailure);
    }

    // Получение данных о песне
    public void getSong(String musicId, GetObjectListener listener) {
        firestore.collection("songs").document(musicId).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               DocumentSnapshot doc = task.getResult();
               Song song = new Song();
               song.setName(doc.get("name").toString());
               song.setDuration(doc.get("duration").toString());
               song.setAdded((long)doc.get("added"));
               song.setDate(doc.get("date").toString());
               song.setAuditions((long)doc.get("auditions"));
               song.setId(musicId);
               if (doc.get("album") != null)
                   song.setAlbum(doc.get("album").toString());
               getUri("music/" + song.getId(), new GetObjectListener() {
                   @Override
                   public void onComplete(Object object) {
                       song.setUri((Uri) object);
                       getUri(doc.get("cover").toString(), new GetObjectListener() {
                           @Override
                           public void onComplete(Object object) {
                               song.setCoverUri((Uri)object);
                               new UserManager().getUser(doc.get("authorId").toString(), new GetObjectListener() {

                                   @Override
                                   public void onComplete(Object object) {
                                       song.setAuthor((User)object);
                                       getGenre(doc.get("genre").toString(), new GetObjectListener() {
                                           @Override
                                           public void onComplete(Object object) {
                                               song.setGenre((Genre) object);
                                               listener.onComplete(song);
                                           }

                                           @Override
                                           public void onFailure(Exception e) {
                                               listener.onFailure(e);
                                           }
                                       });


                                   }

                                   @Override
                                   public void onFailure(Exception e) {
                                       listener.onFailure(e);
                                   }
                               });

                           }

                           @Override
                           public void onFailure(Exception e) {
                               listener.onFailure(e);
                           }
                       });
                   }

                   @Override
                   public void onFailure(Exception e) {
                       listener.onFailure(e);
                   }
               });
           }
        });
    }

    // Получение списка постов пользователя
    public void getUserPosts(String userId, GetObjectListener listener) {
        firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> result = (ArrayList<String>) task.getResult().get("posts");
                if (result != null) {
                    listener.onComplete(result);
                }
            } else
                listener.onFailure(task.getException());
        });
    }

    // Загрузка плейлиста
    public void uploadPlaylist(Playlist playlist, TaskListener listener) {
        DatabasePlaylist dbPlaylist = new DatabasePlaylist(playlist);
        // Создание документа с данными
        firestore.collection("playlists").add(dbPlaylist).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String coverPath = "";
                if (playlist.getCoverUri() == null) {
                    coverPath = "song_covers/default.png";
                } else
                    coverPath = "playlist_covers/" + task.getResult().getId();
                String finalCoverPath = coverPath;
                if (playlist.getCoverUri() != null) {
                    // Загрузка обложки
                    uploadUriFile(playlist.getCoverUri(), coverPath, new TaskListener() {
                        @Override
                        public void onComplete() {
                            task.getResult().update("cover", finalCoverPath).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    addPlaylist("playlists/" + task.getResult().getId(), listener);
                                } else
                                    listener.onFailure(task1.getException());
                            });
                        }


                        @Override
                        public void onFailure(Exception e) {
                            listener.onFailure(e);
                        }
                    });
                } else {
                    task.getResult().update("cover", finalCoverPath).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            addPlaylist(task.getResult().getId(), listener);
                        } else
                            listener.onFailure(task1.getException());
                    });
                }
            } else
                listener.onFailure(task.getException());
        });

    }

    // Добавление плейлиста в список плейлистов пользователя
    public void addPlaylist(String playlistId, TaskListener listener) {
        firestore.collection("users").document(uid).update(
                "playlists", FieldValue.arrayUnion(playlistId)).
                addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        listener.onComplete();
                        if (playlistId.contains("playlists")) {
                            increaseAdded("playlists", playlistId);
                            firestore.collection("playlists").document(playlistId).get().
                                    addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            ArrayList<String> songsIds = (ArrayList<String>) task.getResult().get("songs");
                                            for (String id : songsIds)
                                                increaseAdded("songs", id);
                                        }
                                    });
                        }
                    }
                    else
                        listener.onFailure(task2.getException());
                });
    }

    // Получение плейлиста
    public void getPlaylist(String id, GetObjectListener listener) {
        firestore.collection("playlists").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                Playlist playlist = new Playlist();
                playlist.setId(id);
                playlist.setName(result.get("name").toString());
                playlist.setDate(result.get("date").toString());
                playlist.setSongIds((ArrayList<String>) result.get("songs"));
                playlist.setDuration(result.get("duration").toString());
                playlist.setAdded((long)result.get("added"));
                new UserManager().getUser(result.get("author").toString(), new GetObjectListener() {
                    @Override
                    public void onComplete(Object object) {
                        playlist.setAuthor((User)object);
                        getUri(result.get("cover").toString(), new GetObjectListener() {
                            @Override
                            public void onComplete(Object object) {
                                playlist.setCoverUri((Uri)object);
                                listener.onComplete(playlist);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                listener.onFailure(e);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });

            } else
                listener.onFailure(task.getException());
        });
    }

    // Удаление плейлиста из списка пользователя
    public void deleteFromMyPlaylists(String playlistId, TaskListener listener) {
        firestore.collection("users").document(uid).update("playlists",
                FieldValue.arrayRemove(playlistId)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete();
                decreaseAdded(playlistId.substring(0, playlistId.indexOf("/")),
                        playlistId.substring(playlistId.indexOf("/") + 1));
            }
            else
                listener.onFailure(task.getException());
        });
    }

    // Удаление плейлиста из бд
    public void deletePlaylist(String playlistId, TaskListener listener) {
        // Удаление плейлиста из списка пользователя
        deleteFromMyPlaylists(playlistId, new TaskListener() {
            @Override
            public void onComplete() {
                DocumentReference playlistRef = firestore.collection("playlists").document(playlistId);
                playlistRef.get().
                        addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String coverPath = task.getResult().get("cover").toString();
                        playlistRef.delete().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                if (coverPath.contains("playlist_covers/"))
                                    deleteFile(coverPath, listener);
                                else
                                    listener.onComplete();
                            }
                            else
                                listener.onFailure(task1.getException());
                        });
                    } else
                        listener.onFailure(task.getException());
                });
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });

    }

    // Обновление плейлиста
    public void updatePlaylist(Playlist playlist, boolean coverChanged, TaskListener listener) {
        DatabasePlaylist dbPlaylist = new DatabasePlaylist(playlist);
        dbPlaylist.setCover("playlist_covers/" + playlist.getId());
        firestore.collection("playlists").document(playlist.getId()).set(dbPlaylist).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (coverChanged)
                    uploadUriFile(playlist.getCoverUri(), dbPlaylist.getCover(), listener);
                else
                    listener.onComplete();
            } else
                listener.onFailure(task.getException());
        });
    }

    public void getGenresList(GetObjectListener listener) {
        firestore.collection("genres").get().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               ArrayList<String> ids = new ArrayList<>();
               for (DocumentSnapshot documentSnapshot: task.getResult().getDocuments())
                   ids.add(documentSnapshot.getId());
               listener.onComplete(ids);
           } else
               listener.onFailure(task.getException());
        });
    }

    public void getGenre(String id, GetObjectListener listener) {
        firestore.collection("genres").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                if (result.get("name") != null) {
                    Genre genre = new Genre();
                    genre.setId(id);
                    genre.setName(result.get("name").toString());
                    genre.setSubgenres((ArrayList<String>) result.get("subgenres"));
                    getUri(result.get("picture").toString(), new GetObjectListener() {
                        @Override
                        public void onComplete(Object object) {
                            genre.setPicture((Uri) object);
                            listener.onComplete(genre);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            listener.onFailure(e);
                        }
                    });
                } else {
                    getSubgenre(id, listener);
                }
            } else
                listener.onFailure(task.getException());
        });
    }

    public void getSubgenre(String id, GetObjectListener listener) {
        firestore.collection("subgenres").document(id).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot result = task1.getResult();
                Genre genre = new Genre();
                genre.setId(id);
                genre.setName(result.get("name").toString());
                genre.setSubgenres((ArrayList<String>) result.get("subgenres"));
                genre.setParentId(result.get("parent").toString());
                getUri(result.get("picture").toString(), new GetObjectListener() {
                    @Override
                    public void onComplete(Object object) {
                        genre.setPicture((Uri) object);
                        listener.onComplete(genre);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });
            } else
                listener.onFailure(task1.getException());
        });
    }

    public void uploadAlbum(Album album, TaskListener listener) {
        // Генерация id
        String id = UUID.randomUUID().toString();
        album.setId(id);
        // Загрузка песен
        ArrayList<Song> uploadedSongs = new ArrayList<>();
        for (Song song: album.getSongs()) {
            song.setAlbum(album.getId());
            song.setCoverPath("song_covers/" + id);
            uploadSong(song, new TaskListener() {
                @Override
                public void onComplete() {
                    uploadedSongs.add(song);
                    // Когда все песни загрузились в дб, загружаем обложку
                    if (uploadedSongs.size() == album.getSongs().size()) {
                        if (album.getCoverUri() != null) {
                            album.setCoverPath("song_covers/" + album.getId());
                            uploadUriFile(album.getCoverUri(), album.getCoverPath(), new TaskListener() {
                                @Override
                                public void onComplete() {
                                    createAlbumDoc(album, listener);
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        } else {
                            album.setCoverPath("song_covers/default.png");
                            createAlbumDoc(album, listener);
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

    public void createAlbumDoc(Album album, TaskListener listener) {
        DatabaseAlbum dbAlbum = new DatabaseAlbum(album);
        firestore.collection("albums").document(album.getId()).set(dbAlbum).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addPlaylist("albums/" + album.getId(), listener);
            } else
                listener.onFailure(task.getException());
        });
    }

    public void getAlbum(String albumId, GetObjectListener listener) {
        firestore.collection("albums").document(albumId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                Album album = new Album();
                album.setId(albumId);
                album.setName(result.get("name").toString());
                album.setAuditions((long)result.get("auditions"));
                album.setDate(result.get("date").toString());
                album.setDuration(result.get("duration").toString());
                album.setSongIds((ArrayList<String>) result.get("songs"));
                album.setCoverPath(result.get("cover").toString());
                UserManager userManager = new UserManager();
                userManager.getUser(result.get("author").toString(), new GetObjectListener() {
                    @Override
                    public void onComplete(Object object) {
                        album.setAuthor((User) object);
                        getUri(result.get("cover").toString(), new GetObjectListener() {
                            @Override
                            public void onComplete(Object object) {
                                album.setCoverUri((Uri)object);
                                getGenre(result.get("genre").toString(), new GetObjectListener() {
                                    @Override
                                    public void onComplete(Object object) {
                                        Genre genre = (Genre)object;
                                        album.setGenre(genre);
                                        listener.onComplete(album);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        listener.onFailure(e);
                                    }
                                });

                            }

                            @Override
                            public void onFailure(Exception e) {
                                listener.onFailure(e);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

            } else
                listener.onFailure(task.getException());
        });
    }

    public void deleteAlbum(Album album, TaskListener listener) {
        // Удаление альбома у пользователя
        firestore.collection("users").document(album.getAuthor().getId()).
                update("playlists", FieldValue.arrayRemove("albums/" + album.getId())).
                addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Удаление песен из альбома
                ArrayList<String> deletedSongs = new ArrayList<>();
                for (Song song: album.getSongs()) {
                    deleteSong(song, new TaskListener() {
                        @Override
                        public void onComplete() {
                            deletedSongs.add(song.getId());
                            if (deletedSongs.size() == album.getSongs().size()) {
                                firestore.collection("albums").document(album.getId()).
                                        delete().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        if (!album.getCoverPath().equals("song_covers/default.png")) {
                                            // Удаление обложки альбома из хранилища
                                            deleteFile(album.getCoverPath(), new TaskListener() {
                                                @Override
                                                public void onComplete() {
                                                    listener.onComplete();
                                                }

                                                @Override
                                                public void onFailure(Exception e) {
                                                    listener.onFailure(e);
                                                }
                                            });
                                        } else
                                            listener.onComplete();
                                    } else
                                        listener.onFailure(task.getException());
                                });

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


    // Удаление альбома у всех пользователей
//    firestore.collection("users").whereArrayContains("playlists",
//                                                             "albums/" + album.getId()).get().addOnCompleteListener(task -> {
//        if (task.isSuccessful()) {
//            QuerySnapshot docs = task.getResult();
//            ArrayList<String> deleted = new ArrayList<>();
//            for (DocumentSnapshot doc: docs.getDocuments()) {
//                String id = doc.getId();
//                firestore.collection("users").document(id).update("")
//            }
//        }
//    })


}
