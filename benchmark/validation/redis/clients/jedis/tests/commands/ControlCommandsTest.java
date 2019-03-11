package redis.clients.jedis.tests.commands;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisMonitor;
import redis.clients.jedis.exceptions.JedisDataException;


public class ControlCommandsTest extends JedisCommandTestBase {
    @Test
    public void save() {
        try {
            String status = jedis.save();
            Assert.assertEquals("OK", status);
        } catch (JedisDataException e) {
            Assert.assertTrue("ERR Background save already in progress".equalsIgnoreCase(e.getMessage()));
        }
    }

    @Test
    public void bgsave() {
        try {
            String status = jedis.bgsave();
            Assert.assertEquals("Background saving started", status);
        } catch (JedisDataException e) {
            Assert.assertTrue("ERR Background save already in progress".equalsIgnoreCase(e.getMessage()));
        }
    }

    @Test
    public void bgrewriteaof() {
        String scheduled = "Background append only file rewriting scheduled";
        String started = "Background append only file rewriting started";
        String status = jedis.bgrewriteaof();
        boolean ok = (status.equals(scheduled)) || (status.equals(started));
        Assert.assertTrue(ok);
    }

    @Test
    public void lastsave() throws InterruptedException {
        long saved = jedis.lastsave();
        Assert.assertTrue((saved > 0));
    }

    @Test
    public void info() {
        String info = jedis.info();
        Assert.assertNotNull(info);
        info = jedis.info("server");
        Assert.assertNotNull(info);
    }

    @Test
    public void readonly() {
        try {
            jedis.readonly();
        } catch (JedisDataException e) {
            Assert.assertTrue("ERR This instance has cluster support disabled".equalsIgnoreCase(e.getMessage()));
        }
    }

    @Test
    public void monitor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // sleep 100ms to make sure that monitor thread runs first
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                Jedis j = new Jedis("localhost");
                j.auth("foobared");
                for (int i = 0; i < 5; i++) {
                    j.incr("foobared");
                }
                j.disconnect();
            }
        }).start();
        jedis.monitor(new JedisMonitor() {
            private int count = 0;

            @Override
            public void onCommand(String command) {
                if (command.contains("INCR")) {
                    (count)++;
                }
                if ((count) == 5) {
                    client.disconnect();
                }
            }
        });
    }

    @Test
    public void configGet() {
        List<String> info = jedis.configGet("m*");
        Assert.assertNotNull(info);
    }

    @Test
    public void configSet() {
        List<String> info = jedis.configGet("maxmemory");
        String memory = info.get(1);
        String status = jedis.configSet("maxmemory", "200");
        Assert.assertEquals("OK", status);
        jedis.configSet("maxmemory", memory);
    }

    @Test
    public void sync() {
        jedis.sync();
    }

    @Test
    public void debug() {
        jedis.set("foo", "bar");
        String resp = jedis.debug(DebugParams.OBJECT("foo"));
        Assert.assertNotNull(resp);
        resp = jedis.debug(DebugParams.RELOAD());
        Assert.assertNotNull(resp);
    }

    @Test
    public void waitReplicas() {
        Long replicas = jedis.waitReplicas(1, 100);
        Assert.assertEquals(1, replicas.longValue());
    }
}

