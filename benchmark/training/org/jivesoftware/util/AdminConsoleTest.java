/**
 * Copyright (C) 2004-2008 Jive Software. All rights reserved.
 */
package org.jivesoftware.util;


import java.io.InputStream;
import java.util.Collection;
import org.dom4j.Element;
import org.jivesoftware.admin.AdminConsole;
import org.junit.Assert;
import org.junit.Test;


public class AdminConsoleTest {
    @Test
    public void testModifyGlobalProps() throws Exception {
        // Add a new stream to the AdminConsole:
        try (InputStream in = getClass().getResourceAsStream("/org/jivesoftware/admin/AdminConsoleTest.admin-sidebar-01.xml")) {
            AdminConsole.addModel("test1", in);
        }
        String name = AdminConsole.getAppName();
        Assert.assertEquals("Foo Bar", name);
        String img = AdminConsole.getLogoImage();
        Assert.assertEquals("foo.gif", img);
    }

    @Test
    public void testNewTabs() throws Exception {
        // Add a new stream to the AdminConsole:
        try (InputStream in = getClass().getResourceAsStream("/org/jivesoftware/admin/AdminConsoleTest.admin-sidebar-02.xml")) {
            AdminConsole.addModel("test2", in);
        }
        Collection tabs = AdminConsole.getModel().selectNodes("//tab");
        Assert.assertNotNull(tabs);
        Assert.assertTrue(((tabs.size()) > 0));
        boolean found = false;
        for (Object tab1 : tabs) {
            Element tab = ((Element) (tab1));
            if ("foobar".equals(tab.attributeValue("id"))) {
                found = true;
                Assert.assertEquals("Foo Bar", tab.attributeValue("name"));
                Assert.assertEquals("Click to see foo bar", tab.attributeValue("description"));
            }
        }
        if (!found) {
            Assert.fail("Expected new item 'foobar' was not found.");
        }
    }

    @Test
    public void testTabOverwrite() throws Exception {
        // Add a new stream to the AdminConsole:
        try (InputStream in = getClass().getResourceAsStream("/org/jivesoftware/admin/AdminConsoleTest.admin-sidebar-03.xml")) {
            AdminConsole.addModel("test3", in);
        }
        boolean found = false;
        for (Object o : AdminConsole.getModel().selectNodes("//tab")) {
            Element tab = ((Element) (o));
            if ("server".equals(tab.attributeValue("id"))) {
                found = true;
                Assert.assertEquals("New Server Title", tab.attributeValue("name"));
                Assert.assertEquals("Testing 1 2 3", tab.attributeValue("description"));
            }
        }
        if (!found) {
            Assert.fail("Failed to overwrite 'server' tab with new properties.");
        }
    }
}

