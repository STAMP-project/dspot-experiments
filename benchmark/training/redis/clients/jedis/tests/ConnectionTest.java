package redis.clients.jedis.tests;


import Command.HMSET;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Connection;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class ConnectionTest extends Assert {
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

    @Test
    public void checkCloseable() {
        client.setHost("localhost");
        client.setPort(6379);
        client.connect();
        client.close();
    }

    @Test
    public void getErrorAfterConnectionReset() throws Exception {
        class TestConnection extends Connection {
            public TestConnection() {
                super("localhost", 6379);
            }

            @Override
            protected Connection sendCommand(ProtocolCommand cmd, byte[]... args) {
                return super.sendCommand(cmd, args);
            }
        }
        TestConnection conn = new TestConnection();
        try {
            conn.sendCommand(HMSET, new byte[(1024 * 1024) + 1][0]);
            Assert.fail("Should throw exception");
        } catch (JedisConnectionException jce) {
            Assert.assertEquals("ERR Protocol error: invalid multibulk length", jce.getMessage());
        }
    }
}

