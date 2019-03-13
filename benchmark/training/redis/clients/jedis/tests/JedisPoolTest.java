package redis.clients.jedis.tests;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class JedisPoolTest extends Assert {
    private static HostAndPort hnp = HostAndPortUtil.getRedisServers().get(0);

    @Test
    public void checkConnections() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000);
        Jedis jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.set("foo", "bar");
        Assert.assertEquals("bar", jedis.get("foo"));
        jedis.close();
        pool.destroy();
        Assert.assertTrue(pool.isClosed());
    }

    @Test
    public void checkCloseableConnections() throws Exception {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000);
        Jedis jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.set("foo", "bar");
        Assert.assertEquals("bar", jedis.get("foo"));
        jedis.close();
        pool.close();
        Assert.assertTrue(pool.isClosed());
    }

    @Test
    public void checkConnectionWithDefaultPort() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort());
        Jedis jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.set("foo", "bar");
        Assert.assertEquals("bar", jedis.get("foo"));
        jedis.close();
        pool.destroy();
        Assert.assertTrue(pool.isClosed());
    }

    @Test
    public void checkJedisIsReusedWhenReturned() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort());
        Jedis jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.set("foo", "0");
        jedis.close();
        jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.incr("foo");
        jedis.close();
        pool.destroy();
        Assert.assertTrue(pool.isClosed());
    }

    @Test
    public void checkPoolRepairedWhenJedisIsBroken() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort());
        Jedis jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.quit();
        jedis.close();
        jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.incr("foo");
        jedis.close();
        pool.destroy();
        Assert.assertTrue(pool.isClosed());
    }

    @Test(expected = JedisConnectionException.class)
    public void checkPoolOverflow() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setBlockWhenExhausted(false);
        JedisPool pool = new JedisPool(config, JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort());
        Jedis jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.set("foo", "0");
        Jedis newJedis = pool.getResource();
        newJedis.auth("foobared");
        newJedis.incr("foo");
    }

    @Test
    public void securePool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        JedisPool pool = new JedisPool(config, JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "foobared");
        Jedis jedis = pool.getResource();
        jedis.set("foo", "bar");
        jedis.close();
        pool.destroy();
        Assert.assertTrue(pool.isClosed());
    }

    @Test
    public void nonDefaultDatabase() {
        JedisPool pool0 = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "foobared");
        Jedis jedis0 = pool0.getResource();
        jedis0.set("foo", "bar");
        Assert.assertEquals("bar", jedis0.get("foo"));
        jedis0.close();
        pool0.destroy();
        Assert.assertTrue(pool0.isClosed());
        JedisPool pool1 = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "foobared", 1);
        Jedis jedis1 = pool1.getResource();
        Assert.assertNull(jedis1.get("foo"));
        jedis1.close();
        pool1.destroy();
        Assert.assertTrue(pool1.isClosed());
    }

    @Test
    public void startWithUrlString() {
        Jedis j = new Jedis("localhost", 6380);
        j.auth("foobared");
        j.select(2);
        j.set("foo", "bar");
        JedisPool pool = new JedisPool("redis://:foobared@localhost:6380/2");
        Jedis jedis = pool.getResource();
        Assert.assertEquals("PONG", jedis.ping());
        Assert.assertEquals("bar", jedis.get("foo"));
    }

    @Test
    public void startWithUrl() throws URISyntaxException {
        Jedis j = new Jedis("localhost", 6380);
        j.auth("foobared");
        j.select(2);
        j.set("foo", "bar");
        JedisPool pool = new JedisPool(new URI("redis://:foobared@localhost:6380/2"));
        Jedis jedis = pool.getResource();
        Assert.assertEquals("PONG", jedis.ping());
        Assert.assertEquals("bar", jedis.get("foo"));
    }

    @Test(expected = InvalidURIException.class)
    public void shouldThrowInvalidURIExceptionForInvalidURI() throws URISyntaxException {
        JedisPool pool = new JedisPool(new URI("localhost:6380"));
    }

    @Test
    public void allowUrlWithNoDBAndNoPassword() throws URISyntaxException {
        new JedisPool("redis://localhost:6380");
        new JedisPool(new URI("redis://localhost:6380"));
    }

    @Test
    public void selectDatabaseOnActivation() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "foobared");
        Jedis jedis0 = pool.getResource();
        Assert.assertEquals(0, jedis0.getDB());
        jedis0.select(1);
        Assert.assertEquals(1, jedis0.getDB());
        jedis0.close();
        Jedis jedis1 = pool.getResource();
        Assert.assertTrue("Jedis instance was not reused", (jedis1 == jedis0));
        Assert.assertEquals(0, jedis1.getDB());
        jedis1.close();
        pool.destroy();
        Assert.assertTrue(pool.isClosed());
    }

    @Test
    public void customClientName() {
        JedisPool pool0 = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "foobared", 0, "my_shiny_client_name");
        Jedis jedis = pool0.getResource();
        Assert.assertEquals("my_shiny_client_name", jedis.clientGetname());
        jedis.close();
        pool0.destroy();
        Assert.assertTrue(pool0.isClosed());
    }

    @Test
    public void returnResourceDestroysResourceOnException() {
        class CrashingJedis extends Jedis {
            @Override
            public void resetState() {
                throw new RuntimeException();
            }
        }
        final AtomicInteger destroyed = new AtomicInteger(0);
        class CrashingJedisPooledObjectFactory implements PooledObjectFactory<Jedis> {
            @Override
            public PooledObject<Jedis> makeObject() throws Exception {
                return new org.apache.commons.pool2.impl.DefaultPooledObject<Jedis>(new CrashingJedis());
            }

            @Override
            public void destroyObject(PooledObject<Jedis> p) throws Exception {
                destroyed.incrementAndGet();
            }

            @Override
            public boolean validateObject(PooledObject<Jedis> p) {
                return true;
            }

            @Override
            public void activateObject(PooledObject<Jedis> p) throws Exception {
            }

            @Override
            public void passivateObject(PooledObject<Jedis> p) throws Exception {
            }
        }
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        JedisPool pool = new JedisPool(config, JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "foobared");
        pool.initPool(config, new CrashingJedisPooledObjectFactory());
        Jedis crashingJedis = pool.getResource();
        try {
            crashingJedis.close();
        } catch (Exception ignored) {
        }
        Assert.assertEquals(destroyed.get(), 1);
    }

    @Test
    public void returnResourceShouldResetState() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setBlockWhenExhausted(false);
        JedisPool pool = new JedisPool(config, JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "foobared");
        Jedis jedis = pool.getResource();
        try {
            jedis.set("hello", "jedis");
            Transaction t = jedis.multi();
            t.set("hello", "world");
        } finally {
            jedis.close();
        }
        Jedis jedis2 = pool.getResource();
        try {
            Assert.assertTrue((jedis == jedis2));
            Assert.assertEquals("jedis", jedis2.get("hello"));
        } finally {
            jedis2.close();
        }
        pool.destroy();
        Assert.assertTrue(pool.isClosed());
    }

    @Test
    public void checkResourceIsCloseable() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1);
        config.setBlockWhenExhausted(false);
        JedisPool pool = new JedisPool(config, JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "foobared");
        Jedis jedis = pool.getResource();
        try {
            jedis.set("hello", "jedis");
        } finally {
            jedis.close();
        }
        Jedis jedis2 = pool.getResource();
        try {
            Assert.assertEquals(jedis, jedis2);
        } finally {
            jedis2.close();
        }
    }

    @Test
    public void getNumActiveIsNegativeWhenPoolIsClosed() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "foobared", 0, "my_shiny_client_name");
        pool.destroy();
        Assert.assertTrue(((pool.getNumActive()) < 0));
    }

    @Test
    public void getNumActiveReturnsTheCorrectNumber() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000);
        Jedis jedis = pool.getResource();
        jedis.auth("foobared");
        jedis.set("foo", "bar");
        Assert.assertEquals("bar", jedis.get("foo"));
        Assert.assertEquals(1, pool.getNumActive());
        Jedis jedis2 = pool.getResource();
        jedis.auth("foobared");
        jedis.set("foo", "bar");
        Assert.assertEquals(2, pool.getNumActive());
        jedis.close();
        Assert.assertEquals(1, pool.getNumActive());
        jedis2.close();
        Assert.assertEquals(0, pool.getNumActive());
        pool.destroy();
    }

    @Test
    public void testAddObject() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000);
        pool.addObjects(1);
        Assert.assertEquals(pool.getNumIdle(), 1);
        pool.destroy();
    }

    @Test
    public void testCloseConnectionOnMakeObject() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        JedisPool pool = new JedisPool(new JedisPoolConfig(), JedisPoolTest.hnp.getHost(), JedisPoolTest.hnp.getPort(), 2000, "wrong pass");
        Jedis jedis = new Jedis("redis://:foobared@localhost:6379/");
        int currentClientCount = getClientCount(jedis.clientList());
        try {
            pool.getResource();
            Assert.fail("Should throw exception as password is incorrect.");
        } catch (Exception e) {
            Assert.assertEquals(currentClientCount, getClientCount(jedis.clientList()));
        }
    }
}

