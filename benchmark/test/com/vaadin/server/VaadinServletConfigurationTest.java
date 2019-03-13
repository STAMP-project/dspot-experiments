package com.vaadin.server;


import com.vaadin.ui.UI;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class VaadinServletConfigurationTest {
    @Test
    public void testEnclosingUIClass() throws Exception {
        MockUIContainingServlet.ServletInUI servlet = new MockUIContainingServlet.ServletInUI();
        init(new MockServletConfig());
        Class<? extends UI> uiClass = new DefaultUIProvider().getUIClass(new UIClassSelectionEvent(new VaadinServletRequest(EasyMock.createMock(HttpServletRequest.class), getService())));
        Assert.assertEquals(MockUIContainingServlet.class, uiClass);
    }

    @Test
    public void testValuesFromAnnotation() throws ServletException {
        TestServlet servlet = new TestServlet();
        init(new MockServletConfig());
        DeploymentConfiguration configuration = getService().getDeploymentConfiguration();
        Assert.assertTrue(configuration.isProductionMode());
        Assert.assertTrue(configuration.isCloseIdleSessions());
        Assert.assertEquals(1234, configuration.getHeartbeatInterval());
        Assert.assertEquals(4321, configuration.getResourceCacheTime());
        Class<? extends UI> uiClass = new DefaultUIProvider().getUIClass(new UIClassSelectionEvent(new VaadinServletRequest(EasyMock.createMock(HttpServletRequest.class), getService())));
        Assert.assertEquals(MockUIContainingServlet.class, uiClass);
    }

    @Test
    public void testValuesOverriddenForServlet() throws ServletException {
        Properties servletInitParams = new Properties();
        servletInitParams.setProperty("productionMode", "false");
        servletInitParams.setProperty("heartbeatInterval", "1111");
        TestServlet servlet = new TestServlet();
        init(new MockServletConfig(servletInitParams));
        DeploymentConfiguration configuration = getService().getDeploymentConfiguration();
        // Values from servlet init params take precedence
        Assert.assertEquals(1111, configuration.getHeartbeatInterval());
        Assert.assertFalse(configuration.isProductionMode());
        // Other params are as defined in the annotation
        Assert.assertTrue(configuration.isCloseIdleSessions());
        Assert.assertEquals(4321, configuration.getResourceCacheTime());
        Class<? extends UI> uiClass = new DefaultUIProvider().getUIClass(new UIClassSelectionEvent(new VaadinServletRequest(EasyMock.createMock(HttpServletRequest.class), getService())));
        Assert.assertEquals(MockUIContainingServlet.class, uiClass);
    }
}

