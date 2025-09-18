package com.example.demo.Controller;

import com.example.demo.Entity.User;
import com.example.demo.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class UserC {

    private final UserService userService;

    public UserC(UserService userService) {
        this.userService = userService;
    }

    /** ========== LOGIN PAGE ========== */
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Thymeleaf login.html
    }

    /** ========== LOGIN PROCESS ========== */
    @PostMapping("/login")
    public String loginProcess(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        Optional<User> user = userService.login(username, password);
        if (user.isPresent()) {
            // If login is successful, redirect to profile
            return "redirect:/profile/" + user.get().getUserId();
        } else {
            // If login fails, redirect back with error message
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    /** ========== VIEW ALL USERS (Admin) ========== */
    @GetMapping("/userView")
    public String viewAllAdmin(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("userList", users);
        return "userView"; // Thymeleaf template
    }

    /** ========== VIEW PROFILE (User) ========== */
    @GetMapping("/profile/{id}")
    public String viewUserProfile(@PathVariable("id") String userId, Model model) {
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "userProfile"; // Thymeleaf template
        } else {
            return "redirect:/userView";
        }
    }

    /** ========== SEARCH USER (Admin) ========== */
    @GetMapping("/userSearch")
    public String searchUsers(@RequestParam String keyword, Model model) {
        List<User> found = userService.findUsersByKeyword(keyword);
        model.addAttribute("userList", found);
        model.addAttribute("searchKeyword", keyword);
        return "userView";
    }

    /** ========== ADD NEW USER (Form Submission) ========== */
    @PostMapping("/userAdd")
    public String addUser(@ModelAttribute User user) {
        if (userService.validateUser(user)) {
            userService.saveUser(user);
        }
        return "redirect:/userView";
    }

    /** ========== EDIT USER FORM ========== */
    @GetMapping("/userEdit/{id}")
    public String editUserForm(@PathVariable("id") String userId, Model model) {
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "editUser"; // Thymeleaf template for editing
        } else {
            return "redirect:/userView";
        }
    }

    /** ========== UPDATE USER ========== */
    @PostMapping("/userUpdate")
    public String updateUser(@ModelAttribute User user) {
        if (userService.validateUser(user)) {
            userService.updateUser(user);
        }
        return "redirect:/userView";
    }

    /** ========== DELETE USER ========== */
    @PostMapping("/userDelete/{id}")
    public String deleteUser(@PathVariable("id") String userId) {
        userService.deleteUser(userId);
        return "redirect:/userView";
    }
}
