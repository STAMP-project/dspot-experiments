package spark.globalstate;


import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


@RunWith(PowerMockRunner.class)
public class ServletFlagTest {
    @Test
    public void testRunFromServlet_whenDefault() throws Exception {
        AtomicBoolean isRunningFromServlet = Whitebox.getInternalState(ServletFlag.class, "isRunningFromServlet");
        Assert.assertFalse("Should be false because it is the default value", isRunningFromServlet.get());
    }

    @Test
    public void testRunFromServlet_whenExecuted() throws Exception {
        ServletFlag.runFromServlet();
        AtomicBoolean isRunningFromServlet = Whitebox.getInternalState(ServletFlag.class, "isRunningFromServlet");
        Assert.assertTrue("Should be true because it flag has been set after runFromServlet", isRunningFromServlet.get());
    }

    @Test
    public void testIsRunningFromServlet_whenDefault() throws Exception {
        Assert.assertFalse("Should be false because it is the default value", ServletFlag.isRunningFromServlet());
    }

    @Test
    public void testIsRunningFromServlet_whenRunningFromServlet() throws Exception {
        ServletFlag.runFromServlet();
        Assert.assertTrue("Should be true because call to runFromServlet has been made", ServletFlag.isRunningFromServlet());
    }
}

