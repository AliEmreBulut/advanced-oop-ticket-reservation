package com.project;

public class SingletonUser {
    private static SingletonUser instance;
    private User currentUser;

    private SingletonUser() {}

    public static SingletonUser getInstance() {
        if (instance == null) {
            instance = new SingletonUser();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        System.out.println(user.getUsername() + " user sign in.");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println(currentUser.getUsername() + " sign out.");
            currentUser = null;
        }
    }
}

