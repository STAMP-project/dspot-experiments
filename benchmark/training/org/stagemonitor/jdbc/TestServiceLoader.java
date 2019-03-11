package org.stagemonitor.jdbc;


import org.junit.Test;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.jdbc.ReflectiveConnectionMonitoringTransformer.ConnectionMonitorAddingSpanEventListener;


public class TestServiceLoader {
    @Test
    public void testLoadConnectionMonitorAddingSpanEventListener() throws Exception {
        Stagemonitor.init();
        assertThat(getFactoryClasses()).contains(ConnectionMonitorAddingSpanEventListener.class);
    }
}

