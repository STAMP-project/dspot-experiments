package redis.clients.jedis.tests;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Module;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.tests.commands.JedisCommandTestBase;
import redis.clients.jedis.util.SafeEncoder;


public class ModuleTest extends JedisCommandTestBase {
    static enum ModuleCommand implements ProtocolCommand {

        SIMPLE("testmodule.simple");
        private final byte[] raw;

        ModuleCommand(String alt) {
            raw = SafeEncoder.encode(alt);
        }

        @Override
        public byte[] getRaw() {
            return raw;
        }
    }

    @Test
    public void testModules() {
        String res = jedis.moduleLoad("/tmp/testmodule.so");
        Assert.assertEquals("OK", res);
        List<Module> modules = jedis.moduleList();
        Assert.assertEquals("testmodule", modules.get(0).getName());
        jedis.getClient().sendCommand(ModuleTest.ModuleCommand.SIMPLE);
        Long out = jedis.getClient().getIntegerReply();
        Assert.assertTrue((out > 0));
        res = jedis.moduleUnload("testmodule");
        Assert.assertEquals("OK", res);
    }
}

