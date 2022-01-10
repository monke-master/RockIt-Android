package com.brigade.rockit.database;



import com.brigade.rockit.adapter.MusicAdapter;
import com.brigade.rockit.adapter.PlaylistAdapter;
import com.brigade.rockit.adapter.PostAdapter;
import com.brigade.rockit.adapter.UserAdapter;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterManager {
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    public AdapterManager() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.
                getInstance(Constants.STORAGE_PATH);
    }


    // Поиск музыки
    public void showSearchedMusic(MusicAdapter adapter, String searching, TaskListener listener){
        // Получение песен по введенному имени
        firestore.collection("songs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document: task.getResult()) {
                    String name = document.get("name").toString().toLowerCase(Locale.ROOT);
                    String artist = document.get("artist").toString().toLowerCase(Locale.ROOT);
                    if ((name.contains(searching) || artist.contains(searching)) &&
                            !adapter.getMusicList().contains(document.getId())) {
                                adapter.addItem(document.getId());
                    }
                }
            } else
                listener.onFailure(task.getException());
        });
    }

    // Отображение постов пользователя
    public void showUserPosts(PostAdapter adapter, User user, TaskListener listener) {
        firestore.collection("users").document(user.getId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().get("posts") != null) {
                    ArrayList<String> posts = (ArrayList<String>)
                            task.getResult().get("posts");
                    for (int i = posts.size() - 1; i > -1; i--)
                        adapter.addItem(posts.get(i));
                }
            } else
                listener.onFailure(task.getException());
        });
    }

    // Поиск пользователей
    public void showSearchedUser(UserAdapter adapter, String searching, TaskListener listener) {
        firestore.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> ids = new ArrayList<>();
                for (DocumentSnapshot document: task.getResult()) {
                    String name = document.get("name").toString().toLowerCase(Locale.ROOT);
                    String surname = document.get("surname").toString().toLowerCase(Locale.ROOT);
                    String login = document.get("login").toString().toLowerCase(Locale.ROOT);
                    if ((name.contains(searching) || surname.contains(searching) ||
                            login.startsWith(searching)) &&
                            !ids.contains(document.getId())) {
                        ids.add(document.getId());
                    }
                }
                showUsers(adapter, ids, listener);
            } else
                listener.onFailure(task.getException());
        });
        firestore.collection("users").orderBy("name").startAt(searching).
                endAt(searching + "\uf8ff").get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            ArrayList<String> usersIds = new ArrayList<>();
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot doc: documents) {
                                usersIds.add(doc.getId());
                            }
                            showUsers(adapter, usersIds, listener);
                        }

                    } else
                        listener.onFailure(task.getException());
        });
    }

    // Отображение пользователей
    public void showUsers(UserAdapter adapter, ArrayList<String> ids, TaskListener listener) {
        for (String id: ids) {
            firestore.collection("users").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    User user = new User();
                    user.setName(doc.get("name").toString());
                    user.setSurname(doc.get("surname").toString());
                    user.setLogin(doc.get("login").toString());
                    user.setBio(doc.get("bio").toString());
                    user.setId(doc.getId());
                    user.setFollowingList((ArrayList<String>) doc.get("followingList"));
                    user.setFollowersList((ArrayList<String>) doc.get("followersList"));
                    StorageReference picRef = storage.getReference(
                            doc.get("profilePicture").toString());
                    picRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            user.setPictureUri(task1.getResult());
                            addUser(adapter, user);
                        } else
                            listener.onFailure(task1.getException());
                    });
                } else
                    listener.onFailure(task.getException());
            });
        }
    }


    private void addUser(UserAdapter adapter, User user) {
        if (!adapter.getUsersList().contains(user)) {
            adapter.addItem(user);
        }

    }

    // Отображение новых постов в ленте
    public void showNewPosts(PostAdapter adapter, User user, TaskListener listener) {
        ArrayList<ArrayList<String>> newPosts = new ArrayList<>();
        ContentManager manager = new ContentManager();
        for (String id: user.getFollowingList()) {
            manager.getUserPosts(id, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    newPosts.add((ArrayList<String>) object);
                    for (int pos = 0; pos <= Constants.NEWS_FEED_SIZE; pos++) {
                        for (ArrayList<String> userPosts: newPosts) {
                            if (pos < userPosts.size()) {
                                if (!adapter.getPostIds().contains(userPosts.get(pos)))
                                    adapter.addItem(userPosts.get(pos));
                            }
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

    // Поиск плейлистов
    public void showSearchedPlaylists(PlaylistAdapter adapter, String searching, TaskListener listener) {
        firestore.collection("playlists").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document: task.getResult()) {
                    String name = document.get("name").toString().toLowerCase(Locale.ROOT);
                    // Отображение подходящего плейлиста
                    if ((name.contains(searching)) &&
                            !adapter.getPlaylistIds().contains(document.getId())) {
                        adapter.addItem(document.getId());
                    }
                }
            } else
                listener.onFailure(task.getException());
        });
    }

}
