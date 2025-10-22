package kr.huni.mds.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

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
        MysqlXADataSource xaDataSource = new MysqlXADataSource();
        String url = properties.determineUrl();
        xaDataSource.setUrl(url);

        String username = properties.getUsername();
        if (username == null || username.isBlank()) {
            username = properties.determineUsername();
        }
        if (username != null) {
            xaDataSource.setUser(username);
        }

        String password = properties.getPassword();
        if (password == null) {
            password = properties.determinePassword();
        }
        if (password != null) {
            xaDataSource.setPassword(password);
        }

        try {
            xaDataSource.setPinGlobalTxToPhysicalConnection(true);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to enable XA pinning for coupon datasource", e);
        }

        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("couponMysqlDataSource");
        dataSource.setXaDataSource(xaDataSource);
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
