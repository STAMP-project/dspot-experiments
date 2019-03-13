package io.hawt.system;


import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.servlet.ServletContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ConfigManagerTest {
    @Mock
    private Context jndiContext;

    @Mock
    private ServletContext servletContext;

    @Test
    public void testConstructor() {
        new ConfigManager();
    }

    @Test
    public void testGetNoJndiContext() {
        System.setProperty("hawtio.foo", "bar");
        final ConfigManager underTest = new ConfigManager();
        underTest.init(servletContext);
        Assert.assertEquals("bar", underTest.get("foo", null));
    }

    @Test
    public void testGetWithoutJndiContextWithDefaultValue() {
        final ConfigManager underTest = new ConfigManager();
        underTest.init(servletContext);
        Assert.assertEquals("default", underTest.get("foo", "default"));
    }

    @Test
    public void testGetWithCustomProvider() throws Exception {
        final ConfigManager underTest = new ConfigManager(( x) -> "foo".equals(x) ? "bar" : null);
        underTest.init(servletContext);
        Assert.assertEquals("bar", underTest.get("foo", "default"));
    }

    @Test
    public void testGetWithCustomProviderOverriddenBySystemProperty() throws Exception {
        System.setProperty("hawtio.foo", "system");
        final ConfigManager underTest = new ConfigManager(( x) -> "foo".equals(x) ? "bar" : null);
        underTest.init(servletContext);
        Assert.assertEquals("system", underTest.get("foo", "default"));
    }

    @Test
    public void testGetWithJndiContext() throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, ConfigManagerTest.TestInitialContextFactory.class.getName());
        Mockito.when(jndiContext.lookup("java:comp/env")).thenReturn(jndiContext);
        Mockito.when(jndiContext.lookup("hawtio/foo")).thenReturn("bar");
        final ConfigManager underTest = new ConfigManager();
        underTest.init(servletContext);
        Assert.assertEquals("bar", underTest.get("foo", null));
    }

    @Test
    public void testGetWithJndiContextDefaultValue() throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, ConfigManagerTest.TestInitialContextFactory.class.getName());
        Mockito.when(jndiContext.lookup("java:comp/env")).thenReturn(jndiContext);
        final ConfigManager underTest = new ConfigManager();
        underTest.init(servletContext);
        Assert.assertEquals("foobar", underTest.get("foo", "foobar"));
        Mockito.verify(jndiContext).lookup("hawtio/foo");
    }

    @Test
    public void testGetWithJndiContextForceSystemProperties() throws Exception {
        System.setProperty("hawtio.foo", "systemBar");
        System.setProperty("hawtio.forceProperties", "true");
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, ConfigManagerTest.TestInitialContextFactory.class.getName());
        Mockito.when(jndiContext.lookup("java:comp/env")).thenReturn(jndiContext);
        Mockito.when(jndiContext.lookup("hawtio/foo")).thenReturn("jndiBar");
        final ConfigManager underTest = new ConfigManager();
        underTest.init(servletContext);
        Assert.assertEquals("systemBar", underTest.get("foo", null));
    }

    @Test
    public void testDestroyNoJndiContext() {
        final ConfigManager underTest = new ConfigManager();
        underTest.init(servletContext);
        underTest.destroy();
        Mockito.verifyZeroInteractions(jndiContext);
    }

    @Test
    public void testDestroyWithJndiContext() throws Exception {
        Mockito.when(jndiContext.lookup("java:comp/env")).thenReturn(jndiContext);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, ConfigManagerTest.TestInitialContextFactory.class.getName());
        final ConfigManager underTest = new ConfigManager();
        underTest.init(servletContext);
        underTest.destroy();
        Mockito.verify(jndiContext).close();
    }

    public static class TestInitialContextFactory implements InitialContextFactory {
        private static final ThreadLocal<Context> CTX = new ThreadLocal<>();

        @Override
        public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
            return ConfigManagerTest.TestInitialContextFactory.CTX.get();
        }

        public static void setCurrentContext(Context context) {
            ConfigManagerTest.TestInitialContextFactory.CTX.set(context);
        }

        public static void clearCurrentContext() {
            ConfigManagerTest.TestInitialContextFactory.CTX.remove();
        }
    }
}

