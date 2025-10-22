package kr.huni.mds.config;

import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.util.StringUtils;

import java.sql.SQLException;

final class MysqlXaDataSourceFactory {

    private MysqlXaDataSourceFactory() {
    }

    static MysqlXADataSource create(DataSourceProperties properties, String resourceId) {
        MysqlXADataSource xaDataSource = properties.initializeDataSourceBuilder()
                .type(MysqlXADataSource.class)
                .build();

        if (!StringUtils.hasText(xaDataSource.getUrl())) {
            throw new IllegalStateException(resourceId + " datasource URL is not configured");
        }

        try {
            xaDataSource.setPinGlobalTxToPhysicalConnection(true);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to enable XA pinning for " + resourceId + " datasource", e);
        }

        return xaDataSource;
    }
}
