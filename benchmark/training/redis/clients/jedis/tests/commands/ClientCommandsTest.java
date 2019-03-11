package redis.clients.jedis.tests.commands;


import SkipMe.NO;
import SkipMe.YES;
import Type.NORMAL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ClientKillParams;


public class ClientCommandsTest extends JedisCommandTestBase {
    private final String clientName = "fancy_jedis_name";

    private final Pattern pattern = Pattern.compile((("\\bname=" + (clientName)) + "\\b"));

    private Jedis client;

    @Test
    public void nameString() {
        String name = "string";
        client.clientSetname(name);
        Assert.assertEquals(name, client.clientGetname());
    }

    @Test
    public void nameBinary() {
        byte[] name = "binary".getBytes();
        client.clientSetname(name);
        Assert.assertArrayEquals(name, client.clientGetnameBinary());
    }

    @Test
    public void killIdString() {
        String info = findInClientList();
        Matcher matcher = Pattern.compile("\\bid=(\\d+)\\b").matcher(info);
        matcher.find();
        String id = matcher.group(1);
        long clients = jedis.clientKill(new ClientKillParams().id(id));
        Assert.assertEquals(1, clients);
        assertDisconnected(client);
    }

    @Test
    public void killIdBinary() {
        String info = findInClientList();
        Matcher matcher = Pattern.compile("\\bid=(\\d+)\\b").matcher(info);
        matcher.find();
        byte[] id = matcher.group(1).getBytes();
        long clients = jedis.clientKill(new ClientKillParams().id(id));
        Assert.assertEquals(1, clients);
        assertDisconnected(client);
    }

    @Test
    public void killTypeNormal() {
        long clients = jedis.clientKill(new ClientKillParams().type(NORMAL));
        Assert.assertTrue((clients > 0));
        assertDisconnected(client);
    }

    @Test
    public void killSkipmeNo() {
        jedis.clientKill(new ClientKillParams().type(NORMAL).skipMe(NO));
        assertDisconnected(client);
        assertDisconnected(jedis);
    }

    @Test
    public void killSkipmeYesNo() {
        jedis.clientKill(new ClientKillParams().type(NORMAL).skipMe(YES));
        assertDisconnected(client);
        long clients = jedis.clientKill(new ClientKillParams().type(NORMAL).skipMe(NO));
        Assert.assertEquals(1, clients);
        assertDisconnected(jedis);
    }

    @Test
    public void killAddrString() {
        String info = findInClientList();
        Matcher matcher = Pattern.compile("\\baddr=(\\S+)\\b").matcher(info);
        matcher.find();
        String addr = matcher.group(1);
        long clients = jedis.clientKill(new ClientKillParams().addr(addr));
        Assert.assertEquals(1, clients);
        assertDisconnected(client);
    }

    @Test
    public void killAddrBinary() {
        String info = findInClientList();
        Matcher matcher = Pattern.compile("\\baddr=(\\S+)\\b").matcher(info);
        matcher.find();
        String addr = matcher.group(1);
        long clients = jedis.clientKill(new ClientKillParams().addr(addr));
        Assert.assertEquals(1, clients);
        assertDisconnected(client);
    }

    @Test
    public void killAddrIpPort() {
        String info = findInClientList();
        Matcher matcher = Pattern.compile("\\baddr=(\\S+)\\b").matcher(info);
        matcher.find();
        String addr = matcher.group(1);
        String[] hp = HostAndPort.extractParts(addr);
        long clients = jedis.clientKill(new ClientKillParams().addr(hp[0], Integer.parseInt(hp[1])));
        Assert.assertEquals(1, clients);
        assertDisconnected(client);
    }
}

