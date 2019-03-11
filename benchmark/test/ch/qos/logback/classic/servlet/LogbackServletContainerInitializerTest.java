package ch.qos.logback.classic.servlet;


import CoreConstants.DISABLE_SERVLET_CONTAINER_INITIALIZER_KEY;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class LogbackServletContainerInitializerTest {
    LogbackServletContainerInitializer lsci = new LogbackServletContainerInitializer();

    @Test
    public void testOnStartup() throws ServletException {
        ServletContext mockedServletContext = Mockito.mock(ServletContext.class);
        lsci.onStartup(null, mockedServletContext);
        Mockito.verify(mockedServletContext).addListener(ArgumentMatchers.any(LogbackServletContextListener.class));
    }

    @Test
    public void noListenerShouldBeAddedWhenDisabled() throws ServletException {
        ServletContext mockedServletContext = Mockito.mock(ServletContext.class);
        Mockito.when(mockedServletContext.getInitParameter(DISABLE_SERVLET_CONTAINER_INITIALIZER_KEY)).thenReturn("true");
        lsci.onStartup(null, mockedServletContext);
        Mockito.verify(mockedServletContext, Mockito.times(0)).addListener(ArgumentMatchers.any(LogbackServletContextListener.class));
    }
}

