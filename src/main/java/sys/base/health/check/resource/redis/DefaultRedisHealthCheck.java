package sys.base.health.check.resource.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import sys.base.health.check.AbstractResourceCheck;

public class DefaultRedisHealthCheck extends AbstractResourceCheck {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRedisHealthCheck.class);
    // 对redis检查连接是否存在最大尝试次数，超过则报警
    private int getConnectionRetryMaxCount = 5;
    // 对redis检查连接是否存在连接失败后，重试时间间隔，5秒重试
    private long getConnectionFailRetryTimeGap = 5000L;
    // 系统ID
    private static String APP_ID = System.getProperty("APPID");
    private static final Exception REDIS_EXCEPTION = new Exception("GET_REDIS_CONNECTION_EXCEPITON");

    public boolean isSafe() {
        return getRedisConnection(getRealMonitorEntity(), 0);
    }

    private boolean getRedisConnection(JedisPool jedisPool, int tryCount) {
        if (tryCount >= getConnectionRetryMaxCount || jedisPool == null) {
            // 打点，redis连接不存在
            LOG.info("SYSTEM" + APP_ID + "lost dataSource connection");
            return false;

        }
        tryCount++;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            afterGetConnectionFail(jedisPool, tryCount, e);
        }
        if (jedis == null) {
            afterGetConnectionFail(jedisPool, tryCount, REDIS_EXCEPTION);
        }
        LOG.info("get connection success");
        releaseConnection(jedis);
        return true;
    }

    private void afterGetConnectionFail(JedisPool jedisPool, int tryCount, Exception e) {
        LOG.error("get connection fail,current try count is " + tryCount, e);
        currentThreadSleep(getConnectionFailRetryTimeGap);
        getRedisConnection(jedisPool, tryCount);
    }

    private void releaseConnection(Jedis jedis) {
        try {
            JedisPool jedisPool = getRealMonitorEntity();
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
        }
    }

    public Object afterCheckSuccess() {
        return null;
    }

    public Object afterCheckFail() {
        return null;
    }

    private JedisPool getRealMonitorEntity() {
        JedisPool jedisPool = (JedisPool) this.getResource().getMonitorEntity();
        return jedisPool;

    }

}
