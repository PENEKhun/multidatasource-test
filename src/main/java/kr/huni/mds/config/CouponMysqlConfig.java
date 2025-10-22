package kr.huni.mds.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class CouponMysqlConfig {

    private final Environment environment;

    public CouponMysqlConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "couponMysqlProperties")
    @ConfigurationProperties(prefix = "spring.datasource.coupon")
    public DataSourceProperties couponMysqlProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "couponMysqlDataSource", initMethod = "init", destroyMethod = "close")
    public DataSource couponMysqlDataSource(
            @Qualifier("couponMysqlProperties") DataSourceProperties properties) {
        MysqlXADataSource xaDataSource = new MysqlXADataSource();
        String url = resolveUrl(properties, "spring.datasource.coupon.url", "spring.datasource.coupon.jdbc-url");
        xaDataSource.setUrl(url);

        String username = resolveUsername(properties, "spring.datasource.coupon.username");
        if (username != null) {
            xaDataSource.setUser(username);
        }

        String password = resolvePassword(properties, "spring.datasource.coupon.password");
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

    private String resolveUrl(DataSourceProperties properties, String... environmentKeys) {
        for (String key : environmentKeys) {
            String candidate = environment.getProperty(key);
            if (StringUtils.hasText(candidate)) {
                return candidate;
            }
        }

        String url = properties.determineUrl();
        if (!StringUtils.hasText(url) || url.startsWith("jdbc:h2:")) {
            throw new IllegalStateException("Coupon datasource URL is not configured");
        }
        return url;
    }

    private String resolveUsername(DataSourceProperties properties, String environmentKey) {
        String username = environment.getProperty(environmentKey);
        if (!StringUtils.hasText(username)) {
            username = properties.getUsername();
        }
        return StringUtils.hasText(username) ? username : null;
    }

    private String resolvePassword(DataSourceProperties properties, String environmentKey) {
        String password = environment.getProperty(environmentKey);
        if (!StringUtils.hasText(password)) {
            password = properties.getPassword();
        }
        return StringUtils.hasText(password) ? password : null;
    }
}
