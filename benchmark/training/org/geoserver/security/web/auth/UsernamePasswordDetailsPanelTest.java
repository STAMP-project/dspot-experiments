/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.auth;


import org.geoserver.security.auth.UsernamePasswordAuthenticationProvider;
import org.geoserver.security.config.UsernamePasswordAuthenticationProviderConfig;
import org.geoserver.security.web.AbstractSecurityNamedServicePanelTest;
import org.geoserver.security.web.SecurityNamedServiceEditPage;
import org.geoserver.security.web.SecurityNamedServiceNewPage;
import org.junit.Assert;
import org.junit.Test;


public class UsernamePasswordDetailsPanelTest extends AbstractSecurityNamedServicePanelTest {
    @Test
    public void testAddModifyRemove() throws Exception {
        initializeForXML();
        activatePanel();
        Assert.assertEquals(1, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        Assert.assertNull(getSecurityNamedServiceConfig("xxxxxxxx"));
        // Test simple add
        clickAddNew();
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        setSecurityConfigClassName(UsernamePasswordAuthProviderPanelInfo.class);
        newFormTester();
        setSecurityConfigName("default2");
        setUGName("default");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        Assert.assertEquals(1, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        clickAddNew();
        newFormTester();
        setSecurityConfigClassName(UsernamePasswordAuthProviderPanelInfo.class);
        newFormTester();
        setSecurityConfigName("default2");
        setUGName("default");
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        clickSave();
        tester.assertRenderedPage(basePage.getClass());
        Assert.assertEquals(2, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        UsernamePasswordAuthenticationProviderConfig authConfig = ((UsernamePasswordAuthenticationProviderConfig) (getSecurityNamedServiceConfig("default2")));
        Assert.assertNotNull(authConfig);
        Assert.assertEquals("default2", authConfig.getName());
        Assert.assertEquals(UsernamePasswordAuthenticationProvider.class.getName(), authConfig.getClassName());
        Assert.assertEquals("default", authConfig.getUserGroupServiceName());
        // reload from manager
        authConfig = ((UsernamePasswordAuthenticationProviderConfig) (getSecurityManager().loadAuthenticationProviderConfig("default2")));
        Assert.assertNotNull(authConfig);
        Assert.assertEquals("default2", authConfig.getName());
        Assert.assertEquals(UsernamePasswordAuthenticationProvider.class.getName(), authConfig.getClassName());
        Assert.assertEquals("default", authConfig.getUserGroupServiceName());
        // test add with name clash
        clickAddNew();
        newFormTester();
        setSecurityConfigClassName(UsernamePasswordAuthProviderPanelInfo.class);
        newFormTester();
        setSecurityConfigName("default2");
        setUGName("default");
        clickSave();// should not work

        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        testErrorMessagesWithRegExp(".*default2.*");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        // end test add with name clash
        // start test modify
        clickNamedServiceConfig("default");
        tester.assertRenderedPage(SecurityNamedServiceEditPage.class);
        tester.debugComponentTrees();
        newFormTester("panel:panel:form");
        formTester.setValue("panel:userGroupServiceName", "test");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        authConfig = ((UsernamePasswordAuthenticationProviderConfig) (getSecurityNamedServiceConfig("default")));
        Assert.assertEquals("default", authConfig.getUserGroupServiceName());
        clickNamedServiceConfig("default2");
        newFormTester("panel:panel:form");
        formTester.setValue("panel:userGroupServiceName", "test");
        clickSave();
        tester.assertRenderedPage(basePage.getClass());
        authConfig = ((UsernamePasswordAuthenticationProviderConfig) (getSecurityNamedServiceConfig("default2")));
        Assert.assertEquals("test", authConfig.getUserGroupServiceName());
        // reload from manager
        authConfig = ((UsernamePasswordAuthenticationProviderConfig) (getSecurityManager().loadAuthenticationProviderConfig("default2")));
        Assert.assertEquals("test", authConfig.getUserGroupServiceName());
    }

    @Test
    public void testMultipleAuthProviders() throws Exception {
        initializeForXML();
        activatePanel();
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        Assert.assertNull(getSecurityNamedServiceConfig("xxxxxxxx"));
        // Test add 1
        clickAddNew();
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        setSecurityConfigClassName(UsernamePasswordAuthProviderPanelInfo.class);
        newFormTester();
        setSecurityConfigName("default_001");
        setUGName("default");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        Assert.assertEquals(1, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        clickAddNew();
        newFormTester();
        setSecurityConfigClassName(UsernamePasswordAuthProviderPanelInfo.class);
        newFormTester();
        setSecurityConfigName("default_001");
        setUGName("default");
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        clickSave();
        // Test add 2
        clickAddNew();
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        setSecurityConfigClassName(UsernamePasswordAuthProviderPanelInfo.class);
        newFormTester();
        setSecurityConfigName("default_002");
        setUGName("default");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        Assert.assertEquals(2, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        clickAddNew();
        newFormTester();
        setSecurityConfigClassName(UsernamePasswordAuthProviderPanelInfo.class);
        newFormTester();
        setSecurityConfigName("default_002");
        setUGName("default");
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        clickSave();
        // start test modify
        clickNamedServiceConfig("default_001");
        tester.assertRenderedPage(SecurityNamedServiceEditPage.class);
        tester.debugComponentTrees();
        newFormTester("panel:panel:form");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        clickNamedServiceConfig("default_002");
        tester.assertRenderedPage(SecurityNamedServiceEditPage.class);
        tester.debugComponentTrees();
        newFormTester("panel:panel:form");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        doRemove(null, "default_001");
        doRemove(null, "default_002");
    }

    @Test
    public void testRemove() throws Exception {
        initializeForXML();
        UsernamePasswordAuthenticationProviderConfig config = new UsernamePasswordAuthenticationProviderConfig();
        config.setName("default2");
        config.setClassName(UsernamePasswordAuthenticationProvider.class.getCanonicalName());
        config.setUserGroupServiceName("default");
        getSecurityManager().saveAuthenticationProvider(config);
        activatePanel();
        doRemove(null, "default2");
        Assert.assertNull(getSecurityManager().loadAuthenticationProvider("default2"));
    }
}

