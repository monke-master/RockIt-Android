package com.brigade.rockit.database;


import android.net.Uri;


import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.User;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class UserManager {
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private String uid;


    public UserManager() {
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.
                getInstance(Constants.STORAGE_PATH);
        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getUid();
    }

    // Проверка почты на занятость
    public void checkEmail(String email, AvailableListener listener) {
        firestore.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Проверка занятости почты
                listener.onComplete(task.getResult().size() == 0);
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    // Проверка логина на занятость
    public void checkLogin(String login, AvailableListener listener) {
        firestore.collection("users").whereEqualTo("login", login).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Проверка занятости логина
                listener.onComplete(task.getResult().size() == 0);
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    // Создание пользователя
    public void createUser(DatabaseUser user, String password, TaskListener listener) {
        auth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseFirestore firebase = FirebaseFirestore.getInstance();
                firebase.collection("users").document(auth.getCurrentUser().
                        getUid()).set(user).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful())
                        listener.onComplete();
                    else
                        listener.onFailure(task.getException());

                });
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    public void verifyEmail(TaskListener listener) {
        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
           if (task.isSuccessful())
               listener.onComplete();
           else
               listener.onFailure(task.getException());
        });
    }

    public boolean emailVerified() {
        return auth.getCurrentUser().isEmailVerified();
    }

    // Вход по емэйлу
    public void signInWithEmail(String email, String password, TaskListener listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            // Успешный вход
            if (task.isSuccessful()) {
                listener.onComplete();
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    // Вход по логину
    public void signInWithLogin(String login, String password, TaskListener listener) {
        // Получение соответствующей логину почты
        firestore.collection("users").whereEqualTo("login", login).get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String email = "defaultEmail@mail.ru";
                        if (task.getResult().size() == 1) {
                            for (QueryDocumentSnapshot snapshot: task.getResult())
                                email = snapshot.get("email").toString();

                        }
                        signInWithEmail(email, password, listener); // Вход по почте
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    // Получение данных текущего пользователя
    public void getUserData(TaskListener listener) {
        firestore.collection("users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null) {
                    String profilePic = doc.get("profilePicture").toString();
                    // Сохранение в памяти данных текущего пользователя
                    Data.setCurUser(new User());
                    Data.getCurUser().setName(doc.get("name").toString());
                    Data.getCurUser().setSurname(doc.get("surname").toString());
                    Data.getCurUser().setLogin(doc.get("login").toString());
                    Data.getCurUser().setEmail(doc.get("email").toString());
                    Data.getCurUser().setBio(doc.get("bio").toString());
                    Data.getCurUser().setId(uid);
                    ArrayList<String> followingList = new ArrayList<>();
                    ArrayList<String> followersList = new ArrayList<>();
                    if (doc.get("followingList") != null)
                        followingList = (ArrayList<String>) doc.get("followingList");
                    if (doc.get("followersList") != null)
                        followersList = (ArrayList<String>) doc.get("followersList");
                    Data.getCurUser().setFollowingList(followingList);
                    Data.getCurUser().setFollowersList(followersList);

                    storage.getReference(profilePic).getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Data.getCurUser().setPictureUri(task1.getResult());
                            listener.onComplete();
                        } else
                            listener.onFailure(task1.getException());
                    }).addOnFailureListener(e -> listener.onFailure(e));

                }
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    // Изменение фото профиля
    public void changeProfilePicture(Uri imageUri, TaskListener listener) {
        ContentManager manager = new ContentManager();
        manager.uploadUriFile(imageUri, "profile_pictures/" + uid, new TaskListener() {
            @Override
            public void onComplete() {
                // Замена в базе данных ссылки на фото профиля текущего пользователя
                DocumentReference userRef = firestore.collection("users").document(uid);
                userRef.update("profilePicture", "profile_pictures/" + uid).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Data.getCurUser().setPictureUri(imageUri);
                        listener.onComplete();
                    }
                    else
                        listener.onFailure(task1.getException());
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    // Смена логина
    public void changeLogin(String login, AvailableListener listener) {
        checkLogin(login, new AvailableListener() {
            @Override
            public void onComplete(boolean available) {
                if (available) {
                    DocumentReference userRef = firestore.collection("users").document(uid);
                    userRef.update("login", login).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Data.getCurUser().setLogin(login);
                            listener.onComplete(true);
                        } else
                            listener.onFailure(task.getException());

                    });
                } else
                    listener.onComplete(false);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });

    }

    // Смена почты
    public void changeEmail(String curEmail, String newEmail, String password, AvailableListener listener) {
        auth.signInWithEmailAndPassword(curEmail, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                checkEmail(newEmail, new AvailableListener() {
                    @Override
                    public void onComplete(boolean available) {
                        if (available) {
                            auth.getCurrentUser().updateEmail(newEmail).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    firestore.collection("users").document(uid).
                                            update("email", newEmail).addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Data.getCurUser().setEmail(newEmail);
                                            listener.onComplete(true);
                                        } else
                                            listener.onFailure(task2.getException());
                                    });
                                } else {
                                    listener.onFailure(task.getException());
                                }
                            });
                        } else
                            listener.onComplete(false);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });
            } else {
                task.getException().printStackTrace();
                listener.onFailure(task.getException());
            }

        });

    }

    // Смена логина
    public void changePassword(String curPassword, String password, TaskListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(Data.getCurUser().getEmail(), curPassword).addOnCompleteListener(task -> {
            // Если пароль верный
            if (task.isSuccessful()) {
                auth.getCurrentUser().updatePassword(password).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        listener.onComplete();
                    } else { // Непредвиденная ошибка
                        listener.onFailure(task1.getException());
                    }
                });
            } else {
                listener.onFailure(task.getException());
            }
        });

    }

    // Смена имени
    public void changeName(String name, TaskListener listener) {
        DocumentReference userRef = firestore.collection("users").document(uid);
        userRef.update("name", name).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Data.getCurUser().setName(name);
                listener.onComplete();
            } else
                listener.onFailure(task.getException());
        });
    }

    // Смена фамилии
    public void changeSurname(String surname, TaskListener listener) {
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();
        DocumentReference userRef = firebase.collection("users").document(uid);
        userRef.update("surname", surname).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Data.getCurUser().setSurname(surname);
                listener.onComplete();
            } else
                listener.onFailure(task.getException());
        });
    }

    // Редактирование краткой информации
    public void changeBio(String bio, TaskListener listener) {
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();
        DocumentReference userRef = firebase.collection("users").document(uid);
        userRef.update("bio", bio).addOnCompleteListener(task -> {
            Data.getCurUser().setBio(bio);
            listener.onComplete();
        });
    }

    // Оформление подписки на пользователя
    public void followUser(User user, TaskListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(auth.getUid()).update("followingList",
                FieldValue.arrayUnion(user.getId())).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firestore.collection("users").document(user.getId()).
                        update("followersList", FieldValue.arrayUnion(auth.getUid()));
                listener.onComplete();
            } else
                listener.onFailure(task.getException());
        });
    }

    // Отписка от пользователя
    public void unfollowUser(User user, TaskListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(auth.getUid()).update("followingList",
                FieldValue.arrayRemove(user.getId())).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firestore.collection("users").document(user.getId()).
                        update("followersList", FieldValue.arrayRemove(auth.getUid()));
                listener.onComplete();
            } else
                listener.onFailure(task.getException());
        });
    }

    // Получение кол-ва подписчиков и подписок
    public void getFollowInfo(User user, TaskListener listener) {
        firestore.collection("users").document(user.getId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                ArrayList<String> followingList = new ArrayList<>();
                ArrayList<String> followersList = new ArrayList<>();
                if (doc.get("followingList") != null)
                    followingList = (ArrayList<String>) doc.get("followingList");
                if (doc.get("followersList") != null)
                    followersList = (ArrayList<String>) doc.get("followersList");
                user.setFollowingList(followingList);
                user.setFollowersList(followersList);
                listener.onComplete();
            } else
                listener.onFailure(task.getException());
        });
    }

    // Получение списка песен пользователя
    public void getUserMusic(String userId, GetObjectListener listener) {
        DocumentReference userRef = firestore.collection("users").document(userId);
        // Получение списка песен
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() ) {
                ArrayList<String> musicIds = (ArrayList<String>) task.getResult().get("music");
                if (musicIds == null)
                    musicIds = new ArrayList<>();
                listener.onComplete(musicIds);


            } else {
                listener.onFailure(task.getException());
            }
        });

    }

    public void getUserPlaylists(String userId, GetObjectListener listener) {
        DocumentReference userRef = firestore.collection("users").document(userId);
        // Получение списка песен
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() ) {
                ArrayList<String> playlists = (ArrayList<String>) task.getResult().get("playlists");
                if (playlists == null)
                    playlists = new ArrayList<>();
                listener.onComplete(playlists);

            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    public void getUser(String id, GetObjectListener listener) {
        ContentManager contentManager = new ContentManager();
        firestore.collection("users").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                User user = new User();
                user.setLogin(result.get("login").toString());
                user.setName(result.get("name").toString());
                user.setSurname(result.get("surname").toString());
                user.setBio(result.get("bio").toString());
                user.setId(result.getId());
                user.setFollowingList((ArrayList<String>) result.get("followingList"));
                user.setFollowersList((ArrayList<String>) result.get("followersList"));
                contentManager.getUri(result.get("profilePicture").toString(), new GetObjectListener() {
                    @Override
                    public void onComplete(Object object) {
                        user.setPictureUri((Uri) object);
                        listener.onComplete(user);
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

}
