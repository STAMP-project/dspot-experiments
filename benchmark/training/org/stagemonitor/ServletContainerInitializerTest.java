package org.stagemonitor;


import ServletPlugin.Initializer;
import java.util.stream.Collectors;
import org.junit.Test;
import org.stagemonitor.web.servlet.initializer.ServletContainerInitializerUtil;


public class ServletContainerInitializerTest {
    @Test
    public void testServletContainerInitializer() throws Exception {
        assertThat(ServletContainerInitializerUtil.getStagemonitorSCIs().stream().map(StagemonitorServletContainerInitializer::getClass).collect(Collectors.toList())).contains(Initializer.class);
    }
}

