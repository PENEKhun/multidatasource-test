package kr.huni.mds.controller;

import kr.huni.mds.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            log.info("회원가입 요청: username={}, email={}", request.getUsername(), request.getEmail());
            
            userRegistrationService.registerUserWithWelcomeCoupon(request.getUsername(), request.getEmail());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "회원가입이 완료되었습니다. 축하 쿠폰이 발급되었습니다.",
                "username", request.getUsername(),
                "email", request.getEmail()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("회원가입 실패: username={}, error={}", request.getUsername(), e.getMessage());
            
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "회원가입에 실패했습니다: " + e.getMessage()
            );
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 내부 클래스로 요청 DTO 정의
    public static class UserRegistrationRequest {
        private String username;
        private String email;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
} 