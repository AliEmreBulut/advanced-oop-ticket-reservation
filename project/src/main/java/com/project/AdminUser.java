package com.project;
public class AdminUser extends User {
    public AdminUser(String username, String password) {
        super(username, password);
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Admin user");
    }
    
    
}
