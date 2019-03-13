package generic;


import Container.ExecResult;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;


public class ExecTest {
    @Rule
    public GenericContainer container = new GenericContainer("alpine:3.6").withCommand("top");

    @Test
    public void testSimpleExec() throws IOException, InterruptedException {
        // standaloneExec {
        container.execInContainer("touch", "/somefile.txt");
        // }
        // execReadingStdout {
        Container.ExecResult lsResult = container.execInContainer("ls", "-al", "/");
        String stdout = lsResult.getStdout();
        Assert.assertTrue(stdout.contains("somefile.txt"));
        // }
    }
}

