package sys.base.health.check.resource.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sys.base.health.check.AbstractResourceCheck;

/**
 * default datasource health check
 * @author zhengwen
 *
 */
public class DefaultDbHealthCheck extends AbstractResourceCheck {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDbHealthCheck.class);
    // 对一个数据库检查连接是否存在最大尝试次数，超过则报警
    private int getConnectionRetryMaxCount = 5;
    // 对一个数据库检查连接是否存在连接失败后，重试时间间隔，5秒重试
    private long getConnectionFailRetryTimeGap = 5000L;
    // 对所有数据源检测一次后到下次检测的时间间隔,一分钟
    private SQLException SQL_EXCEPTION = new SQLException("GET_CONNECTION_RESULT_NULL");
    // 系统ID
    private static String APP_ID = System.getProperty("APPID");

    static {
        if (APP_ID == null) {
            APP_ID = "";
        }
    }

    public boolean isSafe() {
        DataSource dataSource = getRealMonitorEntity();
        return getDataSourceConnect(dataSource, 0);
    }

    private boolean getDataSourceConnect(DataSource dataSource, int tryCount) {
        if (tryCount >= getConnectionRetryMaxCount || dataSource == null) {
            // 打点，数据库连接丢失
            LOG.info("SYSTEM" + APP_ID + "lost dataSource connection");
            return false;

        }
        tryCount++;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            afterGetConnectionFail(dataSource, tryCount, e);
        }
        if (connection == null) {
            afterGetConnectionFail(dataSource, tryCount, SQL_EXCEPTION);
        }
        LOG.info("get connection success");
        releaseConnection(connection);
        return true;
    }

    private void releaseConnection(Connection connection) {
        try {
            connection.close();
            LOG.info("close connection success");
        } catch (SQLException e) {
            LOG.error("release connetion fail");
        }
    }

    /**
     * afte get connection fail
     * 
     * @param dataSource
     * @param tryCount
     * @param e just for log
     */
    private void afterGetConnectionFail(DataSource dataSource, int tryCount, SQLException e) {
        handleDifferentResourceWhenGetResourceFailIfYouWantSomeSpecificRequest(tryCount, e);
        LOG.error("get connection fail,current try count is " + tryCount, e);
        currentThreadSleep(getConnectionFailRetryTimeGap);
        getDataSourceConnect(dataSource, tryCount);
    }

    public Object afterCheckSuccess() {
        return null;
    }

    public Object afterCheckFail() {
        return null;
    }

    public DataSource getRealMonitorEntity() {
        DataSource dataSource = (DataSource) getResource().getMonitorEntity();
        return dataSource;
    }

    public int getGetConnectionRetryMaxCount() {
        return getConnectionRetryMaxCount;
    }

    public void setGetConnectionRetryMaxCount(int getConnectionRetryMaxCount) {
        this.getConnectionRetryMaxCount = getConnectionRetryMaxCount;
    }

    public long getGetConnectionFailRetryTimeGap() {
        return getConnectionFailRetryTimeGap;
    }

    public void setGetConnectionFailRetryTimeGap(long getConnectionFailRetryTimeGap) {
        this.getConnectionFailRetryTimeGap = getConnectionFailRetryTimeGap;
    }

}
