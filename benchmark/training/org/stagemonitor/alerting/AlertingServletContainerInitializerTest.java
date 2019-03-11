package org.stagemonitor.alerting;


import AlertingPlugin.Initializer;
import java.util.stream.Collectors;
import org.junit.Test;
import org.stagemonitor.web.servlet.initializer.ServletContainerInitializerUtil;


public class AlertingServletContainerInitializerTest {
    @Test
    public void testServletContainerInitializer() throws Exception {
        assertThat(ServletContainerInitializerUtil.getStagemonitorSCIs().stream().map(StagemonitorServletContainerInitializer::getClass).collect(Collectors.toList())).contains(Initializer.class);
    }
}

