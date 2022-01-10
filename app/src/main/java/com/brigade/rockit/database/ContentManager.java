package com.brigade.rockit.database;

import android.net.Uri;
import android.util.Log;

import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Music;
import com.brigade.rockit.data.Playlist;
import com.brigade.rockit.data.Post;
import com.brigade.rockit.data.User;
import com.google.android.gms.tasks.Task;
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
    public void uploadMusic(Music music, TaskListener listener) {
        // Получение id автора
        String authorID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Перенос данных музыки в тип музыки для бд
        DatabaseMusic dbMusic = new DatabaseMusic();
        dbMusic.setAuthorId(authorID);
        dbMusic.setArtist(music.getArtist());
        dbMusic.setName(music.getName());
        dbMusic.setDuration(music.getDuration());
        // Добавление песни в базу данных
        firestore.collection("songs").add(dbMusic).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Получение id песни
                String id = task.getResult().getId();
                // Загрузка аудио-файла
                uploadUriFile(music.getUri(), "music/" + id, new TaskListener() {
                    @Override
                    public void onComplete() {
                        // Получение пути к обложке
                        String coverPath = "song_covers/default.png";
                        if (music.getCover() != null) {
                            coverPath = "song_covers/" + id;
                            String finalCoverPath = coverPath;
                            uploadUriFile(music.getCover(), coverPath, new TaskListener() {
                                @Override
                                public void onComplete() {
                                    addSongCover(id, finalCoverPath, new TaskListener() {
                                        @Override
                                        public void onComplete() {
                                            addToMyMusic(id, new TaskListener() {
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
                                @Override
                                public void onFailure(Exception e) {
                                    listener.onFailure(e);
                                }
                            });
                        } else {
                            addSongCover(id, coverPath, new TaskListener() {
                                @Override
                                public void onComplete() {
                                    addToMyMusic(id, new TaskListener() {
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
            } else {
                listener.onFailure(task.getException());
            }

        });
    }

    // Добавление в "Мою музыку"
    public void addToMyMusic(String musicId, TaskListener listener) {
        firestore.collection("users").document(uid).
                update("music", FieldValue.arrayUnion(musicId)).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        listener.onComplete();
                    else
                        listener.onFailure(task.getException());
        });
    }

    // Добавлние обложки музыки
    public void addSongCover(String musicId, String coverPath, TaskListener listener) {
        firestore.collection("songs").document(musicId).update("cover", coverPath).
                addOnCompleteListener(task -> {
            if (task.isSuccessful())
                listener.onComplete();
            else
                listener.onFailure(task.getException());
        });
    }

    // Удаление песни из "Моей музыки"
    public void deleteSongFromMyMusic(Music music, TaskListener listener) {
        DocumentReference userRef = firestore.collection("users").document(uid);
        // Получение списка песен
        userRef.update("music", FieldValue.arrayRemove(music.getId())).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                listener.onComplete();
            else {
                listener.onFailure(task.getException());
            }

        });
    }


    // Удаление песни из бд
    public void deleteSong(Music music, TaskListener listener) {
        deleteSongFromMyMusic(music, new TaskListener() {
            @Override
            public void onComplete() {
                // Получение данных о песни
                DocumentReference songRef = firestore.collection("songs").document(music.getId());
                songRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Удаление аудио-файла
                        deleteFile("music/" + music.getId(), new TaskListener() {
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
        DateManager manager = new DateManager();
        dbPost.setDate(manager.getDate());
        // Получение id прикрепленных файлов
        dbPost.setMusicIds(post.getMusicIds());
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

    // Получение текстовой информации о посте
    public void getPostTextInfo(String postId, GetObjectListener listener) {
        firestore.collection("posts").document(postId).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot doc = task1.getResult();
                DatabasePost dbPost = new DatabasePost();
                dbPost.setAuthorId(doc.get("authorId").toString());
                dbPost.setDate(doc.get("date").toString());
                dbPost.setText(doc.get("text").toString());
                Post post = new Post();
                post.setText(dbPost.getText());
                post.setDate(dbPost.getDate());
                UserManager userManager = new UserManager();
                userManager.getUser(dbPost.getAuthorId(), new GetObjectListener() {
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

    // Получение списка id изображений поста
    public void getImageIds(String postId, GetObjectListener listener) {
        firestore.collection("posts").document(postId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete(task.getResult().get("imageIds"));
            } else
                listener.onFailure(task.getException());
        });
    }

    // Получение списка id песен поста
    public void getMusicIds(String postId, GetObjectListener listener) {
        firestore.collection("posts").document(postId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete(task.getResult().get("musicIds"));
            } else
                listener.onFailure(task.getException());
        });
    }

    // Получение uri файла
    public void getUri(String path, GetObjectListener listener) {
        storage.getReference(path).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onComplete(task.getResult());
            } else
                listener.onFailure(task.getException());
        });

    }

    // Получение данных о песне
    public void getMusic(String musicId, GetObjectListener listener) {
        firestore.collection("songs").document(musicId).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               DocumentSnapshot doc = task.getResult();
               Music music = new Music();
               music.setName(doc.get("name").toString());
               music.setArtist(doc.get("artist").toString());
               music.setAuthorId(doc.get("authorId").toString());
               music.setDuration(doc.get("duration").toString());
               music.setId(musicId);
               getUri("music/" + music.getId(), new GetObjectListener() {
                   @Override
                   public void onComplete(Object object) {
                       music.setUri((Uri)object);
                       getUri(doc.get("cover").toString(), new GetObjectListener() {
                           @Override
                           public void onComplete(Object object) {
                               music.setCover((Uri)object);
                               listener.onComplete(music);
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
                    if (playlist.getSongs().size() > 0)
                        playlist.setCoverUri(playlist.getSongs().get(0).getCover());
                    else
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
                                    addPlaylist(task.getResult().getId(), listener);
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
                    if (task2.isSuccessful())
                        listener.onComplete();
                    else
                        listener.onFailure(task2.getException());
                });
    }

    // Получение плейлиста
    public void getPlaylist(String id, GetObjectListener listener) {
        firestore.collection("playlists").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                DatabasePlaylist playlist = new DatabasePlaylist();
                playlist.setName(result.get("name").toString());
                playlist.setAuthor(result.get("author").toString());
                playlist.setCover(result.get("cover").toString());
                playlist.setDate(result.get("date").toString());
                playlist.setSongs((ArrayList<String>) result.get("songs"));
                listener.onComplete(playlist);
            } else
                listener.onFailure(task.getException());
        });
    }

    // Удаление плейлиста из списка пользователя
    public void deleteFromMyPlaylists(String playlistId, TaskListener listener) {
        firestore.collection("users").document(uid).update("playlists",
                FieldValue.arrayRemove(playlistId)).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                listener.onComplete();
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
}
