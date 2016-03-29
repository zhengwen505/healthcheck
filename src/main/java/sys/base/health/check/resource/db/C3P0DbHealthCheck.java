package sys.base.health.check.resource.db;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * if c3p0
 * @author zhengwen
 *
 */
public class C3P0DbHealthCheck extends DefaultDbHealthCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(C3P0DbHealthCheck.class);

    protected void handleDifferentResourceWhenGetResourceFailIfYouWantSomeSpecificRequest(int tryCount, Exception e) {
        DataSource dataSource = getRealMonitorEntity();
        if (dataSource instanceof ComboPooledDataSource) {
            ComboPooledDataSource comboPooledDataSource = (ComboPooledDataSource) dataSource;
            LOGGER.error("datasource get connection fail,datasource info is : {},current try conut is : {}",
                    comboPooledDataSource.getJdbcUrl(), tryCount);
        }
    }

}
