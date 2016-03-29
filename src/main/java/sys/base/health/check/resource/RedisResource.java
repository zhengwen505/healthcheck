package sys.base.health.check.resource;

import redis.clients.jedis.JedisPool;

public class RedisResource implements Resource {
    private JedisPool jedisPool;

    public Object getMonitorEntity() {
        return getJedisPool();
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

}
