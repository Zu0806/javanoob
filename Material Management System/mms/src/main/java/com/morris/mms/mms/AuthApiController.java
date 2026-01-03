package com.morris.mms.mms;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class AuthApiController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthApiController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // === Register ===
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (isBlank(req.username) || isBlank(req.password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username/password 必填");
        }

        Optional<User> existing = userRepo.findByUsername(req.username);
        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "此帳號已被使用");
        }

        User user = new User();
        user.setUsername(req.username);
        user.setPassword(passwordEncoder.encode(req.password));

        // 你的 User.java 有 displayName ✅
        if (!isBlank(req.displayName)) user.setDisplayName(req.displayName);

        userRepo.save(user);

        // 不做 token，先回 user 即可（你前端會 router.push）
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "user", Map.of(
                        "username", user.getUsername(),
                        "displayName", user.getDisplayName()
                )
        ));
    }

    // === Login ===
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpSession session) {
        if (isBlank(req.username) || isBlank(req.password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username/password 必填");
        }

        User user = userRepo.findByUsername(req.username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "帳號或密碼錯誤"));

        if (!passwordEncoder.matches(req.password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "帳號或密碼錯誤");
        }

        // 先用 session 記住（不強制用也行）
        session.setAttribute("loginUser", user.getUsername());

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "user", Map.of(
                        "username", user.getUsername(),
                        "displayName", user.getDisplayName()
                )
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("ok", true));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static class RegisterRequest {
        public String displayName;
        public String username;
        public String password;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }
}
