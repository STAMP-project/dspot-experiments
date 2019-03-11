package redis.clients.jedis.tests.commands;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.MigrateParams;


public class MigrateTest extends JedisCommandTestBase {
    private static final byte[] bfoo = new byte[]{ 1, 2, 3 };

    private static final byte[] bbar = new byte[]{ 4, 5, 6 };

    private static final byte[] bfoo1 = new byte[]{ 7, 8, 1 };

    private static final byte[] bbar1 = new byte[]{ 9, 0, 1 };

    private static final byte[] bfoo2 = new byte[]{ 7, 8, 2 };

    private static final byte[] bbar2 = new byte[]{ 9, 0, 2 };

    private static final byte[] bfoo3 = new byte[]{ 7, 8, 3 };

    private static final byte[] bbar3 = new byte[]{ 9, 0, 3 };

    private Jedis dest;

    private String host;

    private int port;

    private final int destDB = 2;

    private final int timeout = Protocol.DEFAULT_TIMEOUT;

    private String pass;

    @Test
    public void nokey() {
        Assert.assertEquals("NOKEY", jedis.migrate(host, port, "foo", destDB, timeout));
        Assert.assertEquals("NOKEY", jedis.migrate(host, port, MigrateTest.bfoo, destDB, timeout));
        Assert.assertEquals("NOKEY", jedis.migrate(host, port, destDB, timeout, new MigrateParams(), "foo1", "foo2", "foo3"));
        Assert.assertEquals("NOKEY", jedis.migrate(host, port, destDB, timeout, new MigrateParams(), MigrateTest.bfoo1, MigrateTest.bfoo2, MigrateTest.bfoo3));
    }

    @Test
    public void migrate() {
        jedis.set("foo", "bar");
        Assert.assertEquals("OK", jedis.migrate(host, port, "foo", destDB, timeout));
        Assert.assertEquals("bar", dest.get("foo"));
        Assert.assertNull(jedis.get("foo"));
        jedis.set(MigrateTest.bfoo, MigrateTest.bbar);
        Assert.assertEquals("OK", jedis.migrate(host, port, MigrateTest.bfoo, destDB, timeout));
        Assert.assertArrayEquals(MigrateTest.bbar, dest.get(MigrateTest.bfoo));
        Assert.assertNull(jedis.get(MigrateTest.bfoo));
    }

    @Test
    public void migrateMulti() {
        jedis.mset("foo1", "bar1", "foo2", "bar2", "foo3", "bar3");
        Assert.assertEquals("OK", jedis.migrate(host, port, destDB, timeout, new MigrateParams(), "foo1", "foo2", "foo3"));
        Assert.assertEquals("bar1", dest.get("foo1"));
        Assert.assertEquals("bar2", dest.get("foo2"));
        Assert.assertEquals("bar3", dest.get("foo3"));
        jedis.mset(MigrateTest.bfoo1, MigrateTest.bbar1, MigrateTest.bfoo2, MigrateTest.bbar2, MigrateTest.bfoo3, MigrateTest.bbar3);
        Assert.assertEquals("OK", jedis.migrate(host, port, destDB, timeout, new MigrateParams(), MigrateTest.bfoo1, MigrateTest.bfoo2, MigrateTest.bfoo3));
        Assert.assertArrayEquals(MigrateTest.bbar1, dest.get(MigrateTest.bfoo1));
        Assert.assertArrayEquals(MigrateTest.bbar2, dest.get(MigrateTest.bfoo2));
        Assert.assertArrayEquals(MigrateTest.bbar3, dest.get(MigrateTest.bfoo3));
    }

    @Test
    public void migrateConflict() {
        jedis.mset("foo1", "bar1", "foo2", "bar2", "foo3", "bar3");
        dest.set("foo2", "bar");
        try {
            jedis.migrate(host, port, destDB, timeout, new MigrateParams(), "foo1", "foo2", "foo3");
            Assert.fail("Should get BUSYKEY error");
        } catch (JedisDataException jde) {
            Assert.assertTrue(jde.getMessage().contains("BUSYKEY"));
        }
        Assert.assertEquals("bar1", dest.get("foo1"));
        Assert.assertEquals("bar", dest.get("foo2"));
        Assert.assertEquals("bar3", dest.get("foo3"));
        jedis.mset(MigrateTest.bfoo1, MigrateTest.bbar1, MigrateTest.bfoo2, MigrateTest.bbar2, MigrateTest.bfoo3, MigrateTest.bbar3);
        dest.set(MigrateTest.bfoo2, MigrateTest.bbar);
        try {
            jedis.migrate(host, port, destDB, timeout, new MigrateParams(), MigrateTest.bfoo1, MigrateTest.bfoo2, MigrateTest.bfoo3);
            Assert.fail("Should get BUSYKEY error");
        } catch (JedisDataException jde) {
            Assert.assertTrue(jde.getMessage().contains("BUSYKEY"));
        }
        Assert.assertArrayEquals(MigrateTest.bbar1, dest.get(MigrateTest.bfoo1));
        Assert.assertArrayEquals(MigrateTest.bbar, dest.get(MigrateTest.bfoo2));
        Assert.assertArrayEquals(MigrateTest.bbar3, dest.get(MigrateTest.bfoo3));
    }

    @Test
    public void migrateCopyReplace() {
        jedis.set("foo", "bar1");
        dest.set("foo", "bar2");
        Assert.assertEquals("OK", jedis.migrate(host, port, destDB, timeout, new MigrateParams().copy().replace(), "foo"));
        Assert.assertEquals("bar1", jedis.get("foo"));
        Assert.assertEquals("bar1", dest.get("foo"));
        jedis.set(MigrateTest.bfoo, MigrateTest.bbar1);
        dest.set(MigrateTest.bfoo, MigrateTest.bbar2);
        Assert.assertEquals("OK", jedis.migrate(host, port, destDB, timeout, new MigrateParams().copy().replace(), MigrateTest.bfoo));
        Assert.assertArrayEquals(MigrateTest.bbar1, jedis.get(MigrateTest.bfoo));
        Assert.assertArrayEquals(MigrateTest.bbar1, dest.get(MigrateTest.bfoo));
    }
}

