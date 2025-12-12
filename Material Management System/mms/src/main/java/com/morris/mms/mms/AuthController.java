package com.morris.mms.mms;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("loginUser") != null) {
            return "redirect:/items";
        }
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) return "register";

        Optional<User> existing = userRepository.findByUsername(user.getUsername());
        if (existing.isPresent()) {
            model.addAttribute("error", "此帳號已被使用");
            return "register";
        }

     
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session, Model model) {
        if (session.getAttribute("loginUser") != null) {
            return "redirect:/items";
        }
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute("loginRequest") LoginRequest loginRequest,
                          HttpSession session,
                          Model model) {

        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "login";
        }

        User user = userOpt.get();

       
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "login";
        }

        session.setAttribute("loginUser", user);
        return "redirect:/items";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
