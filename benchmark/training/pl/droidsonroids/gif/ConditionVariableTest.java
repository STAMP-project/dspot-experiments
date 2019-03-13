package pl.droidsonroids.gif;


import java.util.concurrent.TimeUnit;
import net.jodah.concurrentunit.Waiter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ConditionVariableTest {
    private static final int TEST_TIMEOUT = 500;

    private static final int BLOCK_DURATION = 200;

    @Rule
    public Timeout timeout = new Timeout(ConditionVariableTest.TEST_TIMEOUT, TimeUnit.MILLISECONDS);

    private ConditionVariable conditionVariable;

    private Waiter waiter;

    @Test
    public void testBlock() throws Exception {
        blockAndWait();
    }

    @Test
    public void testOpen() throws Exception {
        new Thread() {
            @Override
            public void run() {
                conditionVariable.open();
                waiter.resume();
            }
        }.start();
        conditionVariable.block();
        waiter.await();
    }

    @Test
    public void testInitiallyOpened() throws Exception {
        conditionVariable.set(true);
        conditionVariable.block();
    }

    @Test
    public void testInitiallyClosed() throws Exception {
        conditionVariable.set(false);
        blockAndWait();
    }

    @Test
    public void testClose() throws Exception {
        conditionVariable.close();
        blockAndWait();
    }
}

