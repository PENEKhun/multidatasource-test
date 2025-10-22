package kr.huni.mds.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class AccountMysqlConfig {

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
}
