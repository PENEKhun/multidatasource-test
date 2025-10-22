package kr.huni.mds.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

    private static final MySQLContainer<?> accountMysqlContainer;
    private static final MySQLContainer<?> couponMysqlContainer;

    static {
        accountMysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
                .withDatabaseName("account_db")
                .withUsername("test")
                .withPassword("test")
                .withInitScript("init-account.sql");

        couponMysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
                .withDatabaseName("coupon_db")
                .withUsername("test")
                .withPassword("test")
                .withInitScript("init-coupon.sql");

        accountMysqlContainer.start();
        couponMysqlContainer.start();
    }

    public static MySQLContainer<?> accountMysqlContainerRef() {
        return accountMysqlContainer;
    }

    public static MySQLContainer<?> couponMysqlContainerRef() {
        return couponMysqlContainer;
    }

    @Bean
    public MySQLContainer<?> accountMysqlContainer() {
        return accountMysqlContainer;
    }

    @Bean
    public MySQLContainer<?> couponMysqlContainer() {
        return couponMysqlContainer;
    }
}
