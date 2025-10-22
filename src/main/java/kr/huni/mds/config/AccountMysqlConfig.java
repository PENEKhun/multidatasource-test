package kr.huni.mds.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class AccountMysqlConfig {

    private final Environment environment;

    public AccountMysqlConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "accountMysqlProperties")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.account")
    public DataSourceProperties accountMysqlProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "accountMysqlDataSource", initMethod = "init", destroyMethod = "close")
    @Primary
    public DataSource accountMysqlDataSource(
            @Qualifier("accountMysqlProperties") DataSourceProperties properties) {
        MysqlXADataSource xaDataSource = new MysqlXADataSource();
        String url = resolveUrl(properties, "spring.datasource.account.url", "spring.datasource.account.jdbc-url");
        xaDataSource.setUrl(url);

        String username = resolveUsername(properties, "spring.datasource.account.username");
        if (username != null) {
            xaDataSource.setUser(username);
        }

        String password = resolvePassword(properties, "spring.datasource.account.password");
        if (password != null) {
            xaDataSource.setPassword(password);
        }

        try {
            xaDataSource.setPinGlobalTxToPhysicalConnection(true);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to enable XA pinning for account datasource", e);
        }

        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("accountMysqlDataSource");
        dataSource.setXaDataSource(xaDataSource);
        dataSource.setMinPoolSize(1);
        dataSource.setMaxPoolSize(10);
        dataSource.setBorrowConnectionTimeout(30);
        return dataSource;
    }

    @Bean(name = "accountMysqlJdbcTemplate")
    @Primary
    public JdbcTemplate accountMysqlJdbcTemplate(
            @Qualifier("accountMysqlDataSource") DataSource dataSource) {
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
            throw new IllegalStateException("Account datasource URL is not configured");
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
