/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.jdbc;


import FeedbackMessage.ERROR;
import FeedbackMessage.INFO;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.security.jdbc.config.JDBCSecurityServiceConfig;
import org.geoserver.security.jdbc.config.JDBCUserGroupServiceConfig;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.geoserver.web.FormTestPage;
import org.junit.Assert;
import org.junit.Test;


public class JDBCConnectionPanelTest extends AbstractSecurityWicketTestSupport {
    JDBCConnectionPanel<JDBCSecurityServiceConfig> current;

    String relBase = "panel:cxPanelContainer:cxPanel:";

    String base = "form:" + (relBase);

    JDBCSecurityServiceConfig config;

    @Test
    public void testJNDI() throws Exception {
        setupPanel(true);
        tester.assertRenderedPage(FormTestPage.class);
        Assert.assertTrue(config.isJndi());
        assertVisibility(true);
        FormTester ftester = tester.newFormTester("form");
        ftester.setValue(((relBase) + "jndiName"), "jndiurl");
        ftester.submit();
        tester.assertNoErrorMessage();
        Assert.assertEquals("jndiurl", config.getJndiName());
    }

    @Test
    public void testConnectionTestJNDI() throws Exception {
        JDBCUserGroupServiceConfig theConfig = new JDBCUserGroupServiceConfig();
        theConfig.setJndi(true);
        theConfig.setJndiName("jndiurl");
        setupPanel(theConfig);
        tester.assertRenderedPage(FormTestPage.class);
        tester.clickLink("form:panel:cxTest", true);
        Assert.assertEquals(1, tester.getMessages(ERROR).size());
    }

    @Test
    public void testBasic() throws Exception {
        setupPanel(false);
        tester.assertRenderedPage(FormTestPage.class);
        Assert.assertFalse(config.isJndi());
        assertVisibility(false);
        FormTester ftester = tester.newFormTester("form");
        ftester.setValue(((relBase) + "userName"), "user1");
        ftester.setValue(((relBase) + "password"), "pw");
        ftester.setValue(((relBase) + "driverClassName"), "org.h2.Driver");
        ftester.setValue(((relBase) + "connectURL"), "jdbc:h2");
        ftester.submit();
        tester.assertNoErrorMessage();
        Assert.assertEquals("user1", config.getUserName());
        Assert.assertEquals("pw", config.getPassword());
        Assert.assertEquals("org.h2.Driver", config.getDriverClassName());
        Assert.assertEquals("jdbc:h2", config.getConnectURL());
    }

    @Test
    public void testConncetionTestBasic() throws Exception {
        JDBCUserGroupServiceConfig theConfig = new JDBCUserGroupServiceConfig();
        theConfig.setUserName("user1");
        theConfig.setPassword("pw");
        theConfig.setDriverClassName("org.h2.Driver");
        theConfig.setConnectURL("jdbc:foo");
        setupPanel(theConfig);
        tester.assertRenderedPage(FormTestPage.class);
        tester.clickLink("form:panel:cxTest", true);
        Assert.assertEquals(1, tester.getMessages(ERROR).size());
    }

    @Test
    public void testConnectionTestBasicOK() throws Exception {
        JDBCUserGroupServiceConfig theConfig = new JDBCUserGroupServiceConfig();
        theConfig.setUserName("user1");
        theConfig.setPassword("pw");
        theConfig.setDriverClassName("org.h2.Driver");
        theConfig.setConnectURL("jdbc:h2:file:target/db");
        setupPanel(theConfig);
        tester.assertRenderedPage(FormTestPage.class);
        tester.clickLink("form:panel:cxTest", true);
        Assert.assertEquals(1, tester.getMessages(INFO).size());
    }
}

