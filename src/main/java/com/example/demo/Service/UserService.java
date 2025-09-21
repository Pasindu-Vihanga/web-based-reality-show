package com.example.demo.Service;

import com.example.demo.DAO.UserDAO;
import com.example.demo.Entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /* ================== LOGIN ================== */
    public Optional<User> login(String username, String rawPassword) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(rawPassword, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    /* ================== CRUD ================== */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public Optional<User> findUserById(String userId) {
        return userDAO.findById(userId);
    }

    public List<User> findUsersByKeyword(String keyword) {
        // ✅ delegate to DAO (instead of filtering all in memory)
        return userDAO.searchUsers(keyword);
    }

    public void saveUser(User user) {
        // ✅ hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDAO.save(user);
    }

    public void updateUser(User user) {
        // ✅ hash password only if changed
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userDAO.update(user);
    }

    public void deleteUser(String userId) {
        userDAO.delete(userId);
    }

    /* ================== VALIDATION ================== */
    public boolean validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) return false;
        if (user.getPassword() == null || user.getPassword().isBlank()) return false;
        if (user.getEmail() == null || user.getEmail().isBlank()) return false;

        // ✅ ensure unique username, email, phone
        if (userDAO.findByUsername(user.getUsername()).isPresent()) return false;
        if (userDAO.findByEmail(user.getEmail()).isPresent()) return false;
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            List<User> users = userDAO.findAll();
            if (users.stream().anyMatch(u -> user.getPhoneNumber().equals(u.getPhoneNumber()))) {
                return false;
            }
        }
        return true;
    }
}
