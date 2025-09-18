package com.example.demo.Service;

import com.example.demo.DAO.UserDAO;
import com.example.demo.Entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * ========== LOGIN (USERNAME + PASSWORD) ==========
     */
    public Optional<User> login(String username, String password) {
        // Ensure UserDAO has a corresponding login method
        return userDAO.login(username, password);
    }

    /**
     * ========== GET ALL USERS ==========
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * ========== FIND USER BY ID ==========
     */
    public Optional<User> findUserById(String userId) {
        return userDAO.findById(userId);
    }

    /**
     * ========== SEARCH USERS BY KEYWORD ==========
     */
    public List<User> findUsersByKeyword(String keyword) {
        return userDAO.findAll().stream()
                .filter(user ->
                        (user.getUsername() != null && user.getUsername().toLowerCase().contains(keyword.toLowerCase())) ||
                                (user.getEmail() != null && user.getEmail().toLowerCase().contains(keyword.toLowerCase()))
                ).toList();
    }

    /**
     * ========== SAVE USER ==========
     */
    public void saveUser(User user) {
        userDAO.save(user);
    }

    /**
     * ========== UPDATE USER ==========
     */
    public void updateUser(User user) {
        userDAO.update(user);
    }

    /**
     * ========== DELETE USER ==========
     */
    public void deleteUser(String userId) {
        userDAO.delete(userId);
    }

    /**
     * ========== VALIDATE USER BEFORE SAVE/UPDATE ==========
     */
    public boolean validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) return false;
        if (user.getPassword() == null || user.getPassword().isBlank()) return false;
        if (user.getEmail() == null || user.getEmail().isBlank()) return false;
        return true;
    }
}