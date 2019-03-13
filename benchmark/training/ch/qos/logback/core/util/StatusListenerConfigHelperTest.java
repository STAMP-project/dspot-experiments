package ch.qos.logback.core.util;


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusManager;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class StatusListenerConfigHelperTest {
    Context context = new ContextBase();

    StatusManager sm = context.getStatusManager();

    @Test
    public void addOnConsoleListenerInstanceShouldNotStartSecondListener() {
        OnConsoleStatusListener ocl0 = new OnConsoleStatusListener();
        OnConsoleStatusListener ocl1 = new OnConsoleStatusListener();
        StatusListenerConfigHelper.addOnConsoleListenerInstance(context, ocl0);
        {
            List<StatusListener> listeners = sm.getCopyOfStatusListenerList();
            Assert.assertEquals(1, listeners.size());
            Assert.assertTrue(ocl0.isStarted());
        }
        // second listener should not have been started
        StatusListenerConfigHelper.addOnConsoleListenerInstance(context, ocl1);
        {
            List<StatusListener> listeners = sm.getCopyOfStatusListenerList();
            Assert.assertEquals(1, listeners.size());
            Assert.assertFalse(ocl1.isStarted());
        }
    }
}

