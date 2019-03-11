package io.hawt.jmx;


import org.junit.Test;


public class JmxTreeWatcherTest {
    JmxTreeWatcher treeWatcher = new JmxTreeWatcher();

    @Test
    public void testNotificationsOnNewMBeans() throws Exception {
        long value1 = treeWatcher.getCounter();
        // now lets register a new mbean
        About about = new About();
        about.init();
        long value2 = treeWatcher.getCounter();
        assertCounterGreater(value1, value2);
        about.destroy();
        long value3 = treeWatcher.getCounter();
        assertCounterGreater(value2, value3);
    }
}

