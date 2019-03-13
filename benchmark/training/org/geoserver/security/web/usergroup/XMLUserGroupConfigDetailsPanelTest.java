/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.usergroup;


import PasswordValidatorImpl.DEFAULT_NAME;
import PasswordValidatorImpl.MASTERPASSWORD_NAME;
import org.geoserver.security.web.AbstractSecurityNamedServicePanelTest;
import org.geoserver.security.web.SecurityNamedServiceEditPage;
import org.geoserver.security.web.SecurityNamedServiceNewPage;
import org.geoserver.security.xml.XMLUserGroupService;
import org.geoserver.security.xml.XMLUserGroupServiceConfig;
import org.junit.Assert;
import org.junit.Test;


public class XMLUserGroupConfigDetailsPanelTest extends AbstractSecurityNamedServicePanelTest {
    @Test
    public void testAddModify() throws Exception {
        initializeForXML();
        activatePanel();
        Assert.assertEquals(2, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        Assert.assertNotNull(getSecurityNamedServiceConfig("test"));
        Assert.assertNull(getSecurityNamedServiceConfig("xxxxxxxx"));
        // Test simple add
        clickAddNew();
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        setSecurityConfigClassName(XMLUserGroupServicePanelInfo.class);
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
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        setSecurityConfigClassName(XMLUserGroupServicePanelInfo.class);
        newFormTester();
        setPasswordEncoderName(getDigestPasswordEncoder().getName());
        setPasswordPolicy("default");
        setSecurityConfigName("default2");
        setFileName("abc.xml");
        setCheckInterval(5000);
        setValidating(true);
        clickSave();
        tester.assertNoErrorMessage();
        tester.assertRenderedPage(basePage.getClass());
        Assert.assertEquals(3, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        XMLUserGroupServiceConfig xmlConfig = ((XMLUserGroupServiceConfig) (getSecurityNamedServiceConfig("default2")));
        Assert.assertNotNull(xmlConfig);
        Assert.assertEquals("default2", xmlConfig.getName());
        Assert.assertEquals(XMLUserGroupService.class.getName(), xmlConfig.getClassName());
        Assert.assertEquals(getDigestPasswordEncoder().getName(), xmlConfig.getPasswordEncoderName());
        Assert.assertEquals(DEFAULT_NAME, xmlConfig.getPasswordPolicyName());
        Assert.assertEquals("abc.xml", xmlConfig.getFileName());
        Assert.assertEquals(5000, xmlConfig.getCheckInterval());
        Assert.assertEquals(true, xmlConfig.isValidating());
        // reload from manager
        xmlConfig = ((XMLUserGroupServiceConfig) (getSecurityManager().loadUserGroupServiceConfig("default2")));
        Assert.assertNotNull(xmlConfig);
        Assert.assertEquals("default2", xmlConfig.getName());
        Assert.assertEquals(getDigestPasswordEncoder().getName(), xmlConfig.getPasswordEncoderName());
        Assert.assertEquals(DEFAULT_NAME, xmlConfig.getPasswordPolicyName());
        Assert.assertEquals("abc.xml", xmlConfig.getFileName());
        Assert.assertEquals(5000, xmlConfig.getCheckInterval());
        Assert.assertEquals(true, xmlConfig.isValidating());
        // test add with name clash
        clickAddNew();
        // detailsPage = (UserGroupTabbedPage) tester.getLastRenderedPage();
        newFormTester();
        setSecurityConfigClassName(XMLUserGroupServicePanelInfo.class);
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
        // detailsPage = (UserGroupTabbedPage) tester.getLastRenderedPage();
        newFormTester("panel:panel:panel:form");
        formTester.setValue("panel:passwordPolicyName", MASTERPASSWORD_NAME);
        formTester.setValue("panel:passwordEncoderName", getPlainTextPasswordEncoder().getName());
        Assert.assertEquals(getDigestPasswordEncoder().getName(), xmlConfig.getPasswordEncoderName());
        Assert.assertEquals(DEFAULT_NAME, xmlConfig.getPasswordPolicyName());
        formTester.setValue("panel:checkInterval", "5001");
        formTester.setValue("panel:validating", true);
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        xmlConfig = ((XMLUserGroupServiceConfig) (getSecurityNamedServiceConfig("default")));
        Assert.assertEquals(getDigestPasswordEncoder().getName(), xmlConfig.getPasswordEncoderName());
        Assert.assertEquals(DEFAULT_NAME, xmlConfig.getPasswordPolicyName());
        Assert.assertEquals("users.xml", xmlConfig.getFileName());
        Assert.assertEquals(10000, xmlConfig.getCheckInterval());
        Assert.assertEquals(true, xmlConfig.isValidating());
        clickNamedServiceConfig("default2");
        // detailsPage = (UserGroupTabbedPage) tester.getLastRenderedPage();
        newFormTester("panel:panel:panel:form");
        // setPasswordPolicy(PasswordValidatorImpl.MASTERPASSWORD_NAME);
        formTester.setValue("panel:passwordPolicyName", MASTERPASSWORD_NAME);
        // setPasswordEncoderName(GeoserverPlainTextPasswordEncoder.BeanName);
        formTester.setValue("panel:checkInterval", "5001");
        // setCheckInterval(5001);
        formTester.setValue("panel:validating", false);
        // setValidating(false);
        clickSave();
        tester.assertNoErrorMessage();
        tester.assertRenderedPage(basePage.getClass());
        xmlConfig = ((XMLUserGroupServiceConfig) (getSecurityNamedServiceConfig("default2")));
        Assert.assertEquals(getDigestPasswordEncoder().getName(), xmlConfig.getPasswordEncoderName());
        Assert.assertEquals(MASTERPASSWORD_NAME, xmlConfig.getPasswordPolicyName());
        Assert.assertEquals("abc.xml", xmlConfig.getFileName());
        Assert.assertEquals(5001, xmlConfig.getCheckInterval());
        Assert.assertEquals(false, xmlConfig.isValidating());
        // reload from manager
        xmlConfig = ((XMLUserGroupServiceConfig) (getSecurityManager().loadUserGroupServiceConfig("default2")));
        Assert.assertEquals(getDigestPasswordEncoder().getName(), xmlConfig.getPasswordEncoderName());
        Assert.assertEquals(MASTERPASSWORD_NAME, xmlConfig.getPasswordPolicyName());
        Assert.assertEquals("abc.xml", xmlConfig.getFileName());
        Assert.assertEquals(5001, xmlConfig.getCheckInterval());
        Assert.assertEquals(false, xmlConfig.isValidating());
    }

    @Test
    public void testRemove() throws Exception {
        initializeForXML();
        XMLUserGroupServiceConfig config = new XMLUserGroupServiceConfig();
        config.setName("default3");
        config.setClassName(XMLUserGroupService.class.getCanonicalName());
        config.setPasswordEncoderName(getPlainTextPasswordEncoder().getName());
        config.setPasswordPolicyName("default");
        config.setFileName("foo.xml");
        getSecurityManager().saveUserGroupService(config);
        activatePanel();
        doRemove("tabbedPanel:panel:removeSelected", "default3");
        Assert.assertNull(getSecurityManager().loadUserGroupService("default3"));
    }
}

