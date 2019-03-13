package com.vaadin.tests.server.component.ui;


import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CustomUIClassLoaderTest {
    /**
     * Stub root
     */
    public static class MyUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
            // Nothing to see here
        }
    }

    /**
     * Dummy ClassLoader that just saves the name of the requested class before
     * delegating to the default implementation.
     */
    public class LoggingClassLoader extends ClassLoader {
        private final List<String> requestedClasses = new ArrayList<>();

        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            requestedClasses.add(name);
            return super.loadClass(name, resolve);
        }
    }

    /**
     * Tests that a UI class can be loaded even if no classloader has been
     * provided.
     *
     * @throws Exception
     * 		if thrown
     */
    @Test
    public void testWithDefaultClassLoader() throws Exception {
        VaadinSession application = createStubApplication();
        application.setConfiguration(CustomUIClassLoaderTest.createConfigurationMock());
        DefaultUIProvider uiProvider = new DefaultUIProvider();
        Class<? extends UI> uiClass = uiProvider.getUIClass(new com.vaadin.server.UIClassSelectionEvent(CustomUIClassLoaderTest.createRequestMock(getClass().getClassLoader())));
        Assert.assertEquals(CustomUIClassLoaderTest.MyUI.class, uiClass);
    }

    /**
     * Tests that the ClassLoader passed in the ApplicationStartEvent is used to
     * load UI classes.
     *
     * @throws Exception
     * 		if thrown
     */
    @Test
    public void testWithClassLoader() throws Exception {
        CustomUIClassLoaderTest.LoggingClassLoader loggingClassLoader = new CustomUIClassLoaderTest.LoggingClassLoader();
        DefaultUIProvider uiProvider = new DefaultUIProvider();
        Class<? extends UI> uiClass = uiProvider.getUIClass(new com.vaadin.server.UIClassSelectionEvent(CustomUIClassLoaderTest.createRequestMock(loggingClassLoader)));
        Assert.assertEquals(CustomUIClassLoaderTest.MyUI.class, uiClass);
        Assert.assertEquals(1, loggingClassLoader.requestedClasses.size());
        Assert.assertEquals(CustomUIClassLoaderTest.MyUI.class.getName(), loggingClassLoader.requestedClasses.get(0));
    }
}

