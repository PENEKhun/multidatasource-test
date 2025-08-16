package kr.huni.mds.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class AccountMysqlConfig {

    @Bean(name = "accountMysqlDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.account")
    public DataSource accountMysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "accountMysqlJdbcTemplate")
    @Primary
    public JdbcTemplate accountMysqlJdbcTemplate() {
        return new JdbcTemplate(accountMysqlDataSource());
    }
} 