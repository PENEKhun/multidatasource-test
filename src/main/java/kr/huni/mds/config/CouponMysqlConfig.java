package kr.huni.mds.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class CouponMysqlConfig {

    @Bean(name = "couponMysqlProperties")
    @ConfigurationProperties(prefix = "spring.datasource.coupon")
    public DataSourceProperties couponMysqlProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "couponMysqlDataSource", initMethod = "init", destroyMethod = "close")
    public DataSource couponMysqlDataSource(
            @Qualifier("couponMysqlProperties") DataSourceProperties properties) {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("couponMysqlDataSource");
        dataSource.setXaDataSource(MysqlXaDataSourceFactory.create(properties, "coupon"));
        dataSource.setMinPoolSize(1);
        dataSource.setMaxPoolSize(10);
        dataSource.setBorrowConnectionTimeout(30);
        return dataSource;
    }

    @Bean(name = "couponMysqlJdbcTemplate")
    public JdbcTemplate couponMysqlJdbcTemplate(
            @Qualifier("couponMysqlDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
