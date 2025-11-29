package com.project;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private static Map<String, User> users = new HashMap<>();

    static {
        users.put("admin1", UserFactory.createUser("admin", "admin1", "1234"));
        users.put("user1", UserFactory.createUser("normal", "user1", "abcd"));
    }

    public static User getUser(String username) {
        return users.get(username);
    }

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static boolean exists(String username, String password) {
        User user = users.get(username);
        return user != null && user.checkPassword(password);
    }

    public static boolean registerUser(String username, String email, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, UserFactory.createUser("normal", username, password));
        return true;
    }

    public static boolean validateUser(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    public static boolean addUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, new NormalUser(username, password));
        return true;
    }
}

