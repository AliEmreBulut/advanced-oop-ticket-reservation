package com.project;

public class UserFactory {
    public static User createUser(String type, String username, String password) {
        if (type.equalsIgnoreCase("admin")) {
            return new AdminUser(username, password);
        } else if (type.equalsIgnoreCase("normal")) {
            return new NormalUser(username, password);
        } else {
            throw new IllegalArgumentException("invalid user type.");
        }
    }
}
