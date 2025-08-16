package kr.huni.mds;

import kr.huni.mds.config.TestContainersConfig;
import kr.huni.mds.service.UserRegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestContainersConfig.class})
class DistributedTransactionTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    @Qualifier("accountMysqlJdbcTemplate")
    private JdbcTemplate accountJdbcTemplate;

    @Autowired
    @Qualifier("couponMysqlJdbcTemplate")
    private JdbcTemplate couponJdbcTemplate;

    @Test
    @DisplayName("회원가입 성공시 쿠폰까지 잘 생성된다.")
    void testUserRegistrationWithWelcomeCoupon() {
        // Given
        String username = "testuser1";
        String email = "user3@example.com";

        // When
        userRegistrationService.registerUserWithWelcomeCoupon(username, email);

        // Then
        // 회원가입 체크~
        String userSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer userCount = accountJdbcTemplate.queryForObject(userSql, Integer.class, username);
        assertEquals(1, userCount);

        // 쿠폰 체크~
        String couponSql = "SELECT COUNT(*) FROM coupons WHERE user_id = ? AND name = '가입 축하 쿠폰'";
        Integer couponCount = couponJdbcTemplate.queryForObject(couponSql, Integer.class, 1);
        assertEquals(1, couponCount);
    }
} 
