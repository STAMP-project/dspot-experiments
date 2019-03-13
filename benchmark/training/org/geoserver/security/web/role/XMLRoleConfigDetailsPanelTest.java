/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.role;


import XMLRoleService.DEFAULT_LOCAL_ADMIN_ROLE;
import XMLRoleService.DEFAULT_LOCAL_GROUP_ADMIN_ROLE;
import org.geoserver.security.web.AbstractSecurityNamedServicePanelTest;
import org.geoserver.security.web.SecurityNamedServiceEditPage;
import org.geoserver.security.web.SecurityNamedServiceNewPage;
import org.geoserver.security.xml.XMLRoleService;
import org.geoserver.security.xml.XMLRoleServiceConfig;
import org.junit.Assert;
import org.junit.Test;


public class XMLRoleConfigDetailsPanelTest extends AbstractSecurityNamedServicePanelTest {
    @Test
    public void testAddModifyRemove() throws Exception {
        initializeForXML();
        activatePanel();
        Assert.assertEquals(2, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        Assert.assertNotNull(getSecurityNamedServiceConfig("test"));
        Assert.assertNull(getSecurityNamedServiceConfig("xxxxxxxx"));
        // Test simple add
        clickAddNew();
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        // detailsPage = (RoleTabbedPage) tester.getLastRenderedPage();
        newFormTester();
        setSecurityConfigClassName(XMLRoleServicePanelInfo.class);
        newFormTester();
        setSecurityConfigName("default2");
        setFileName("abc.xml");
        setCheckInterval(5000);
        setValidating(true);
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        Assert.assertEquals(2, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        clickAddNew();
        newFormTester();
        setSecurityConfigClassName(XMLRoleServicePanelInfo.class);
        newFormTester();
        setSecurityConfigName("default2");
        setFileName("abc.xml");
        setCheckInterval(5000);
        setValidating(true);
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        clickSave();
        tester.assertRenderedPage(basePage.getClass());
        Assert.assertEquals(3, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        XMLRoleServiceConfig xmlConfig = ((XMLRoleServiceConfig) (getSecurityNamedServiceConfig("default2")));
        Assert.assertNotNull(xmlConfig);
        Assert.assertEquals("default2", xmlConfig.getName());
        Assert.assertEquals(XMLRoleService.class.getName(), xmlConfig.getClassName());
        Assert.assertNull(xmlConfig.getAdminRoleName());
        Assert.assertEquals("abc.xml", xmlConfig.getFileName());
        Assert.assertEquals(5000, xmlConfig.getCheckInterval());
        Assert.assertEquals(true, xmlConfig.isValidating());
        // reload from manager
        xmlConfig = ((XMLRoleServiceConfig) (getSecurityManager().loadRoleServiceConfig("default2")));
        Assert.assertNotNull(xmlConfig);
        Assert.assertEquals("default2", xmlConfig.getName());
        Assert.assertEquals(XMLRoleService.class.getName(), xmlConfig.getClassName());
        Assert.assertNull(xmlConfig.getAdminRoleName());
        Assert.assertEquals("abc.xml", xmlConfig.getFileName());
        Assert.assertEquals(5000, xmlConfig.getCheckInterval());
        Assert.assertEquals(true, xmlConfig.isValidating());
        // test add with name clash
        clickAddNew();
        // detailsPage = (RoleTabbedPage) tester.getLastRenderedPage();
        newFormTester();
        setSecurityConfigClassName(XMLRoleServicePanelInfo.class);
        newFormTester();
        setSecurityConfigName("default2");
        clickSave();// should not work

        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        testErrorMessagesWithRegExp(".*default2.*");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        // end test add with name clash
        // start test modify
        clickNamedServiceConfig("default");
        tester.assertRenderedPage(SecurityNamedServiceEditPage.class);
        // detailsPage = (RoleTabbedPage) tester.getLastRenderedPage();
        newFormTester("panel:panel:panel:form");
        tester.debugComponentTrees();
        formTester.setValue("panel:adminRoleName", "ROLE_ADMINISTRATOR");
        // setFileName("abcd.xml");
        formTester.setValue("panel:checkInterval", "5001");
        // setCheckInterval(5001);
        formTester.setValue("panel:validating", true);
        // setValidating(true);
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        xmlConfig = ((XMLRoleServiceConfig) (getSecurityNamedServiceConfig("default")));
        Assert.assertEquals(DEFAULT_LOCAL_ADMIN_ROLE, xmlConfig.getAdminRoleName());
        Assert.assertEquals(DEFAULT_LOCAL_GROUP_ADMIN_ROLE, xmlConfig.getGroupAdminRoleName());
        Assert.assertEquals("roles.xml", xmlConfig.getFileName());
        Assert.assertEquals(10000, xmlConfig.getCheckInterval());
        Assert.assertEquals(true, xmlConfig.isValidating());
        clickNamedServiceConfig("default2");
        // detailsPage = (RoleTabbedPage) tester.getLastRenderedPage();
        newFormTester("panel:panel:panel:form");
        formTester.setValue("panel:adminRoleName", null);
        // setFileName("abcd.xml");
        formTester.setValue("panel:checkInterval", "5001");
        // setCheckInterval(5001);
        formTester.setValue("panel:validating", false);
        clickSave();
        tester.assertRenderedPage(basePage.getClass());
        xmlConfig = ((XMLRoleServiceConfig) (getSecurityNamedServiceConfig("default2")));
        Assert.assertNull(xmlConfig.getAdminRoleName());
        Assert.assertEquals("abc.xml", xmlConfig.getFileName());
        Assert.assertEquals(5001, xmlConfig.getCheckInterval());
        Assert.assertEquals(false, xmlConfig.isValidating());
        // reload from manager
        xmlConfig = ((XMLRoleServiceConfig) (getSecurityManager().loadRoleServiceConfig("default2")));
        Assert.assertNull(xmlConfig.getAdminRoleName());
        Assert.assertEquals("abc.xml", xmlConfig.getFileName());
        Assert.assertEquals(5001, xmlConfig.getCheckInterval());
        Assert.assertEquals(false, xmlConfig.isValidating());
        // doRemove("tabbedPanel:panel:removeSelected");
    }

    @Test
    public void testRemove() throws Exception {
        initializeForXML();
        XMLRoleServiceConfig config = new XMLRoleServiceConfig();
        config.setName("default2");
        config.setClassName(XMLRoleService.class.getCanonicalName());
        config.setFileName("foo.xml");
        getSecurityManager().saveRoleService(config);
        activatePanel();
        doRemove(null, "default2");
        Assert.assertNull(getSecurityManager().loadRoleService("default2"));
    }
}

