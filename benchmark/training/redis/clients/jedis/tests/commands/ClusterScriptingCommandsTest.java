package redis.clients.jedis.tests.commands;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisClusterException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.tests.HostAndPortUtil;
import redis.clients.jedis.tests.JedisTestBase;


public class ClusterScriptingCommandsTest extends JedisTestBase {
    private Jedis node1;

    private static Jedis node2;

    private static Jedis node3;

    private HostAndPort nodeInfo1 = HostAndPortUtil.getClusterServers().get(0);

    private HostAndPort nodeInfo2 = HostAndPortUtil.getClusterServers().get(1);

    private HostAndPort nodeInfo3 = HostAndPortUtil.getClusterServers().get(2);

    private final Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();

    JedisCluster jedisCluster;

    @SuppressWarnings("unchecked")
    @Test(expected = JedisClusterException.class)
    public void testJedisClusterException() {
        String script = "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2],ARGV[3]}";
        List<String> keys = new ArrayList<String>();
        keys.add("key1");
        keys.add("key2");
        List<String> args = new ArrayList<String>();
        args.add("first");
        args.add("second");
        args.add("third");
        jedisCluster.eval(script, keys, args);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEval2() {
        String script = "return redis.call('set',KEYS[1],'bar')";
        int numKeys = 1;
        String[] args = new String[]{ "foo" };
        jedisCluster.eval(script, numKeys, args);
        Assert.assertEquals(jedisCluster.get("foo"), "bar");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testScriptLoadAndScriptExists() {
        String sha1 = jedisCluster.scriptLoad("return redis.call('get','foo')", "key1");
        Assert.assertTrue(jedisCluster.scriptExists(sha1, "key1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEvalsha() {
        String sha1 = jedisCluster.scriptLoad("return 10", "key1");
        Object o = jedisCluster.evalsha(sha1, 1, "key1");
        Assert.assertEquals("10", o.toString());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = JedisClusterException.class)
    public void testJedisClusterException2() {
        byte[] script = "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2],ARGV[3]}".getBytes();
        List<byte[]> keys = new ArrayList<byte[]>();
        keys.add("key1".getBytes());
        keys.add("key2".getBytes());
        List<byte[]> args = new ArrayList<byte[]>();
        args.add("first".getBytes());
        args.add("second".getBytes());
        args.add("third".getBytes());
        jedisCluster.eval(script, keys, args);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBinaryEval() {
        byte[] script = "return redis.call('set',KEYS[1],'bar')".getBytes();
        byte[] args = "foo".getBytes();
        jedisCluster.eval(script, 1, args);
        Assert.assertEquals(jedisCluster.get("foo"), "bar");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBinaryScriptFlush() {
        byte[] byteKey = "key1".getBytes();
        byte[] sha1 = jedisCluster.scriptLoad("return redis.call('get','foo')".getBytes(), byteKey);
        Assert.assertEquals("OK", jedisCluster.scriptFlush(byteKey));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = JedisDataException.class)
    public void testBinaryScriptKill() {
        byte[] byteKey = "key1".getBytes();
        jedisCluster.scriptKill(byteKey);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBinaryScriptExists() {
        byte[] byteKey = "key1".getBytes();
        byte[] sha1 = jedisCluster.scriptLoad("return redis.call('get','foo')".getBytes(), byteKey);
        byte[][] arraySha1 = new byte[][]{ sha1 };
        Long result = 1L;
        List<Long> listResult = new ArrayList();
        listResult.add(result);
        Assert.assertEquals(listResult, jedisCluster.scriptExists(byteKey, arraySha1));
    }
}

