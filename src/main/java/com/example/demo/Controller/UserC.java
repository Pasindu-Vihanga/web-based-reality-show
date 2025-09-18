package com.example.demo.Controller;

import com.example.demo.Entity.User;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class UserC {

    private final UserService userService;

    public UserC(UserService userService) {
        this.userService = userService;
    }

    /* ================== LOGIN & REGISTER ================== */

    @GetMapping("/loginU")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/dashboardU";
        }
        return "loginU";
    }

    @GetMapping("/registerU")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/dashboardU";
        }
        return "registerU";
    }

    @PostMapping("/loginUser")
    public String loginProcess(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Optional<User> user = userService.login(username, password);
        if (user.isPresent() && password.equals(user.get().getPassword())) {
            session.setAttribute("loggedInUser", user.get());
            return "redirect:/dashboardU";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "loginU";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/loginU";
    }

    /* ================== DASHBOARD ================== */

    @GetMapping("/dashboardU")
    public String dashboardPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        model.addAttribute("user", loggedInUser);
        return "dashboardU";
    }

    /* ================== USER REGISTRATION ================== */

    @PostMapping("/userAdd")
    public String addUser(@ModelAttribute User user,
                          @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        // ✅ Handle profile photo upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) uploadPath.mkdirs();

                String fileName = user.getUserId() + "_" + imageFile.getOriginalFilename();
                String filePath = uploadDir + fileName;

                imageFile.transferTo(new File(filePath));

                user.setImagePath("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // ✅ Assign default avatar if no image uploaded
            user.setImagePath("/images/default-avatar.png");
        }

        if (userService.validateUser(user)) {
            userService.saveUser(user);
        }

        return "redirect:/loginU";
    }

    /* ================== PROFILE (VIEW & EDIT) ================== */

    @GetMapping("/profile/{id}")
    public String viewUserProfile(@PathVariable("id") String userId, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "userProfile";
        } else {
            return "redirect:/dashboardU";
        }
    }

    @GetMapping("/editProfile")
    public String editProfileForm(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        model.addAttribute("user", loggedInUser);
        return "editProfile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute User updatedUser,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        updatedUser.setUserId(loggedInUser.getUserId());

        // ✅ Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) uploadPath.mkdirs();

                String fileName = updatedUser.getUserId() + "_" + imageFile.getOriginalFilename();
                String filePath = uploadDir + fileName;

                imageFile.transferTo(new File(filePath));

                updatedUser.setImagePath("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            updatedUser.setImagePath(loggedInUser.getImagePath());
        }

        userService.updateUser(updatedUser);
        session.setAttribute("loggedInUser", updatedUser);

        return "redirect:/dashboardU";
    }

    /* ================== ADMIN FUNCTIONS ================== */

    @GetMapping("/userView")
    public String viewAllUsers(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        List<User> users = userService.getAllUsers();
        model.addAttribute("userList", users);
        return "userView";
    }

    @GetMapping("/userSearch")
    public String searchUsers(@RequestParam String keyword, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        List<User> found = userService.findUsersByKeyword(keyword);
        model.addAttribute("userList", found);
        model.addAttribute("searchKeyword", keyword);
        return "userView";
    }

    @GetMapping("/userEdit/{id}")
    public String editUserForm(@PathVariable("id") String userId, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "editUser";
        } else {
            return "redirect:/userView";
        }
    }

    @PostMapping("/userUpdate")
    public String updateUser(@ModelAttribute User user, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        if (userService.validateUser(user)) {
            userService.updateUser(user);
        }
        return "redirect:/userView";
    }

    @PostMapping("/userDelete/{id}")
    public String deleteUser(@PathVariable("id") String userId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/loginU";

        userService.deleteUser(userId);
        return "redirect:/userView";
    }
}
