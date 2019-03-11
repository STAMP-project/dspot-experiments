package roboguice.event.eventListener;


import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for the RunnableAsyncTaskAdaptor class
 *
 * @author John Ericksen
 */
public class RunnableAsyncTaskAdaptorTest {
    @SuppressWarnings("rawtypes")
    protected EventListenerRunnable runnable;

    protected RunnableAsyncTaskAdaptor runnableAdaptor;

    @Test
    public void test() throws Exception {
        Mockito.reset(runnable);
        runnable.run();
        runnableAdaptor.call();
        Mockito.verify(runnable, Mockito.atLeastOnce()).run();
    }
}

