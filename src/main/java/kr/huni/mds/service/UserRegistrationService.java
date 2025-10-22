package kr.huni.mds.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class UserRegistrationService {
    private final JdbcTemplate accountJdbcTemplate;
    private final JdbcTemplate couponJdbcTemplate;

    public UserRegistrationService(@Qualifier("accountMysqlJdbcTemplate") JdbcTemplate accountJdbcTemplate,
                                   @Qualifier("couponMysqlJdbcTemplate") JdbcTemplate couponJdbcTemplate) {
        this.accountJdbcTemplate = accountJdbcTemplate;
        this.couponJdbcTemplate = couponJdbcTemplate;
    }

    @Transactional
    public void registerUserWithWelcomeCoupon(String username, String email) {
        log.info("회원가입 시작: username={}, email={}", username, email);

        try {
            // 1. Account DB에 사용자 생성
            Long userId = createUserInAccountDB(username, email);
            log.info("사용자 생성 완료: userId={}", userId);

            // 2. Coupon DB에 축하 쿠폰 발급
            createWelcomeCouponInCouponDB(userId);
            log.info("축하 쿠폰 발급 완료: userId={}", userId);

            log.info("회원가입 및 쿠폰 발급 완료: userId={}", userId);

        } catch (Exception e) {
            log.error("회원가입 중 오류 발생: username={}, error={}", username, e.getMessage());
            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.", e);
        }
    }

    private Long createUserInAccountDB(String username, String email) {
        String sql = "INSERT INTO users (username, email, status) VALUES (?, ?, 'ACTIVE')";

        accountJdbcTemplate.update(sql, username, email);

        // 생성된 사용자 ID 조회
        String selectSql = "SELECT id FROM users WHERE username = ?";
        return accountJdbcTemplate.queryForObject(selectSql, Long.class, username);
    }

    private void createWelcomeCouponInCouponDB(Long userId) {
        String sql = "INSERT INTO coupons (user_id, coupon_code, name, discount_amount, minimum_order_amount, valid_from, valid_to, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";

        String couponCode = generateCouponCode();
        String couponName = "가입 축하 쿠폰";
        BigDecimal discountAmount = new BigDecimal("5000");
        BigDecimal minimumOrderAmount = new BigDecimal("10000");
        LocalDateTime validFrom = LocalDateTime.now();
        LocalDateTime validTo = validFrom.plusMonths(3); // 3개월 유효

        couponJdbcTemplate.update(sql, userId, couponCode, couponName, discountAmount,
                minimumOrderAmount, validFrom, validTo);
    }

    private String generateCouponCode() {
        return "WELCOME_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 
