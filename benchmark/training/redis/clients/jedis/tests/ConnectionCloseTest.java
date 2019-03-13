package redis.clients.jedis.tests;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Connection;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class ConnectionCloseTest extends Assert {
    private Connection client;

    @Test(expected = JedisConnectionException.class)
    public void checkUnkownHost() {
        client.setHost("someunknownhost");
        client.connect();
    }

    @Test(expected = JedisConnectionException.class)
    public void checkWrongPort() {
        client.setHost("localhost");
        client.setPort(55665);
        client.connect();
    }

    @Test
    public void connectIfNotConnectedWhenSettingTimeoutInfinite() {
        client.setHost("localhost");
        client.setPort(6379);
        client.setTimeoutInfinite();
    }
}

