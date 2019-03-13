package generic;


import Container.ExecResult;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;


public class CmdModifierTest {
    // hostname {
    @Rule
    public GenericContainer theCache = new GenericContainer("redis:3.0.2").withCreateContainerCmdModifier(( cmd) -> cmd.withHostName("the-cache"));

    // }
    // memory {
    @Rule
    public GenericContainer memoryLimitedRedis = new GenericContainer("redis:3.0.2").withCreateContainerCmdModifier(( cmd) -> cmd.withMemory(((((long) (4)) * 1024) * 1024))).withCreateContainerCmdModifier(( cmd) -> cmd.withMemorySwap(((((long) (4)) * 1024) * 1024)));

    // }
    @Test
    public void testHostnameModified() throws IOException, InterruptedException {
        final Container.ExecResult execResult = theCache.execInContainer("hostname");
        Assert.assertEquals("the-cache", execResult.getStdout().trim());
    }

    @Test
    public void testMemoryLimitModified() throws IOException, InterruptedException {
        final Container.ExecResult execResult = memoryLimitedRedis.execInContainer("cat", "/sys/fs/cgroup/memory/memory.limit_in_bytes");
        Assert.assertEquals("4194304", execResult.getStdout().trim());
    }
}

