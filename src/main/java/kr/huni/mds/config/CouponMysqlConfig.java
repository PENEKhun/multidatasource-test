package kr.huni.mds.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class CouponMysqlConfig {

    @Bean(name = "couponMysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.coupon")
    public DataSource couponMysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "couponMysqlJdbcTemplate")
    public JdbcTemplate couponMysqlJdbcTemplate() {
        return new JdbcTemplate(couponMysqlDataSource());
    }
} 