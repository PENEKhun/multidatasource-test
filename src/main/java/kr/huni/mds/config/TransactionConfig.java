package kr.huni.mds.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean(name = "accountTransactionManager")
    public PlatformTransactionManager accountTransactionManager(
            @Qualifier("accountMysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "couponTransactionManager")
    public PlatformTransactionManager couponTransactionManager(
            @Qualifier("couponMysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
} 