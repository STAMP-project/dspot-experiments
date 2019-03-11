/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.passwd;


import org.geoserver.security.config.PasswordPolicyConfig;
import org.geoserver.security.validation.PasswordValidatorImpl;
import org.geoserver.security.web.AbstractSecurityNamedServicePanelTest;
import org.geoserver.security.web.SecurityNamedServiceEditPage;
import org.geoserver.security.web.SecurityNamedServiceNewPage;
import org.junit.Assert;
import org.junit.Test;


public class PasswordPolicyDetailsPanelTest extends AbstractSecurityNamedServicePanelTest {
    @Test
    public void testAddModify() throws Exception {
        initializeForXML();
        activatePanel();
        Assert.assertEquals(2, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        Assert.assertNull(getSecurityNamedServiceConfig("xxxxxxxx"));
        // Test simple add
        clickAddNew();
        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        // detailsPage = (PasswordPolicyPage) tester.getLastRenderedPage();
        newFormTester();
        setSecurityConfigClassName(PasswordPolicyPanelInfo.class);
        newFormTester();
        setSecurityConfigName("default2");
        setMinLength(5);
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        Assert.assertEquals(2, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        Assert.assertNotNull(getSecurityNamedServiceConfig("master"));
        clickAddNew();
        // detailsPage = (PasswordPolicyPage) tester.getLastRenderedPage();
        newFormTester();
        setSecurityConfigClassName(PasswordPolicyPanelInfo.class);
        setUnlimted(false);
        tester.assertVisible("form:panel:content:maxLength:maxLength");
        newFormTester();
        setSecurityConfigName("default2");
        setDigitRequired(true);
        setUpperCaseRequired(true);
        setLowerCaseRequired(true);
        setMinLength(2);
        setMaxLength(4);
        clickSave();
        tester.assertRenderedPage(basePage.getClass());
        Assert.assertEquals(3, countItems());
        Assert.assertNotNull(getSecurityNamedServiceConfig("default"));
        Assert.assertNotNull(getSecurityNamedServiceConfig("master"));
        PasswordPolicyConfig pwConfig = ((PasswordPolicyConfig) (getSecurityNamedServiceConfig("default2")));
        Assert.assertNotNull(pwConfig);
        Assert.assertEquals("default2", pwConfig.getName());
        Assert.assertEquals(PasswordValidatorImpl.class.getName(), pwConfig.getClassName());
        Assert.assertTrue(pwConfig.isDigitRequired());
        Assert.assertTrue(pwConfig.isLowercaseRequired());
        Assert.assertTrue(pwConfig.isUppercaseRequired());
        Assert.assertEquals(2, pwConfig.getMinLength());
        Assert.assertEquals(4, pwConfig.getMaxLength());
        // reload from manager
        pwConfig = ((PasswordPolicyConfig) (getSecurityManager().loadPasswordPolicyConfig("default2")));
        Assert.assertNotNull(pwConfig);
        Assert.assertEquals("default2", pwConfig.getName());
        Assert.assertEquals(PasswordValidatorImpl.class.getName(), pwConfig.getClassName());
        Assert.assertTrue(pwConfig.isDigitRequired());
        Assert.assertTrue(pwConfig.isLowercaseRequired());
        Assert.assertTrue(pwConfig.isUppercaseRequired());
        Assert.assertEquals(2, pwConfig.getMinLength());
        Assert.assertEquals(4, pwConfig.getMaxLength());
        // test add with name clash
        clickAddNew();
        newFormTester();
        setSecurityConfigClassName(PasswordPolicyPanelInfo.class);
        newFormTester();
        setSecurityConfigName("default2");
        clickSave();// should not work

        tester.assertRenderedPage(SecurityNamedServiceNewPage.class);
        testErrorMessagesWithRegExp(".*default2.*");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        // end test add with name clash
        // start test modify
        clickNamedServiceConfig("default2");
        tester.assertRenderedPage(SecurityNamedServiceEditPage.class);
        newFormTester("panel:panel:form");
        formTester.setValue("panel:maxLength:maxLength", "27");
        clickCancel();
        tester.assertRenderedPage(basePage.getClass());
        pwConfig = ((PasswordPolicyConfig) (getSecurityNamedServiceConfig("default2")));
        Assert.assertEquals(4, pwConfig.getMaxLength());
        clickNamedServiceConfig("default2");
        newFormTester("panel:panel:form");
        // setUnlimted(true);
        formTester.setValue("panel:unlimitedMaxLength", true);
        tester.executeAjaxEvent("panel:panel:form:panel:unlimitedMaxLength", "click");
        tester.assertInvisible("panel:panel:form:panel:maxLength:maxLength");
        newFormTester("panel:panel:form");
        // setDigitRequired(false);
        formTester.setValue("panel:digitRequired", false);
        // setUpperCaseRequired(false);
        formTester.setValue("panel:uppercaseRequired", false);
        // setLowerCaseRequired(false);
        formTester.setValue("panel:lowercaseRequired", false);
        formTester.setValue("panel:minLength", "3");
        // setMinLength(3);
        clickSave();
        tester.assertRenderedPage(basePage.getClass());
        pwConfig = ((PasswordPolicyConfig) (getSecurityNamedServiceConfig("default2")));
        Assert.assertFalse(pwConfig.isDigitRequired());
        Assert.assertFalse(pwConfig.isLowercaseRequired());
        Assert.assertFalse(pwConfig.isUppercaseRequired());
        Assert.assertEquals(3, pwConfig.getMinLength());
        Assert.assertEquals((-1), pwConfig.getMaxLength());
        pwConfig = getSecurityManager().loadPasswordPolicyConfig("default2");
        Assert.assertFalse(pwConfig.isDigitRequired());
        Assert.assertFalse(pwConfig.isLowercaseRequired());
        Assert.assertFalse(pwConfig.isUppercaseRequired());
        Assert.assertEquals(3, pwConfig.getMinLength());
        Assert.assertEquals((-1), pwConfig.getMaxLength());
        // doRemove("tabbedPanel:panel:removeSelected");
    }

    @Test
    public void testRemove() throws Exception {
        initializeForXML();
        PasswordPolicyConfig config = new PasswordPolicyConfig();
        config.setName("default3");
        config.setClassName(PasswordValidatorImpl.class.getCanonicalName());
        getSecurityManager().savePasswordPolicy(config);
        activatePanel();
        doRemove(null, "default3");
        Assert.assertNull(getSecurityManager().loadPasswordPolicyConfig("default3"));
    }
}

