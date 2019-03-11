package org.stagemonitor.web.servlet.spring;


import SpringBootWebPluginInitializer.StagemonitorServletContextInitializer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizerBeanPostProcessor;
import org.springframework.context.ApplicationContext;


public class SpringBootServletPluginInitializerTest {
    private EmbeddedServletContainerCustomizerBeanPostProcessor postProcessor;

    @Test
    public void addInitializer() throws Exception {
        final ConfigurableEmbeddedServletContainer mock = Mockito.mock(ConfigurableEmbeddedServletContainer.class);
        postProcessor.setApplicationContext(Mockito.mock(ApplicationContext.class));
        postProcessor.postProcessBeforeInitialization(mock, null);
        Mockito.verify(mock).addInitializers(ArgumentMatchers.any(StagemonitorServletContextInitializer.class));
    }
}

