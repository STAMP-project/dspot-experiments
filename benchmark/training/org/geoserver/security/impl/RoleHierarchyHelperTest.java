/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class RoleHierarchyHelperTest {
    @Test
    public void testValidTree() throws Exception {
        Map<String, String> map = createFromArray(new String[][]{ new String[]{ "node1", null }, new String[]{ "node11", "node1" }, new String[]{ "node12", "node1" }, new String[]{ "node111", "node11" }, new String[]{ "node112", "node11" } });
        RoleHierarchyHelper helper = new RoleHierarchyHelper(map);
        Assert.assertFalse(helper.containsRole("abc"));
        Assert.assertTrue(helper.containsRole("node11"));
        Assert.assertFalse(helper.isRoot("node11"));
        Assert.assertTrue(helper.isRoot("node1"));
        Assert.assertEquals(1, helper.getRootRoles().size());
        Assert.assertTrue(helper.getRootRoles().contains("node1"));
        Assert.assertEquals(3, helper.getLeafRoles().size());
        Assert.assertTrue(helper.getLeafRoles().contains("node111"));
        Assert.assertTrue(helper.getLeafRoles().contains("node112"));
        Assert.assertTrue(helper.getLeafRoles().contains("node12"));
        Assert.assertEquals("node1", helper.getParent("node11"));
        Assert.assertNull(helper.getParent("node1"));
        Assert.assertEquals(0, helper.getAncestors("node1").size());
        Assert.assertEquals(1, helper.getAncestors("node12").size());
        Assert.assertTrue(helper.getAncestors("node12").contains("node1"));
        Assert.assertEquals(2, helper.getAncestors("node112").size());
        Assert.assertTrue(helper.getAncestors("node112").contains("node11"));
        Assert.assertTrue(helper.getAncestors("node112").contains("node1"));
        Assert.assertEquals(2, helper.getChildren("node1").size());
        Assert.assertTrue(helper.getChildren("node1").contains("node11"));
        Assert.assertTrue(helper.getChildren("node1").contains("node12"));
        Assert.assertEquals(0, helper.getChildren("node12").size());
        Assert.assertEquals(2, helper.getChildren("node11").size());
        Assert.assertTrue(helper.getChildren("node11").contains("node111"));
        Assert.assertTrue(helper.getChildren("node11").contains("node112"));
        Assert.assertEquals(4, helper.getDescendants("node1").size());
        Assert.assertTrue(helper.getDescendants("node1").contains("node11"));
        Assert.assertTrue(helper.getDescendants("node1").contains("node12"));
        Assert.assertTrue(helper.getDescendants("node1").contains("node111"));
        Assert.assertTrue(helper.getDescendants("node1").contains("node112"));
        Assert.assertEquals(0, helper.getDescendants("node12").size());
        Assert.assertEquals(2, helper.getDescendants("node11").size());
        Assert.assertTrue(helper.getDescendants("node11").contains("node111"));
        Assert.assertTrue(helper.getDescendants("node11").contains("node112"));
        Assert.assertTrue(helper.isValidParent("node11", null));
        Assert.assertTrue(helper.isValidParent("node11", "node12"));
        Assert.assertFalse(helper.isValidParent("node11", "node11"));
        Assert.assertFalse(helper.isValidParent("node1", "node111"));
        boolean fail = true;
        try {
            helper.isRoot("abc");
        } catch (RuntimeException e) {
            fail = false;
        }
        if (fail)
            Assert.fail("No Exception");

    }

    @Test
    public void testInValidTree1() throws Exception {
        Map<String, String> map = createFromArray(new String[][]{ new String[]{ "node1", "node1" } });
        RoleHierarchyHelper helper = new RoleHierarchyHelper(map);
        boolean fail;
        fail = true;
        try {
            helper.getParent("node1");
        } catch (RuntimeException e) {
            fail = false;
        }
        if (fail)
            Assert.fail("No Exception");

        fail = true;
        try {
            helper.getAncestors("node1");
        } catch (RuntimeException e) {
            fail = false;
        }
        if (fail)
            Assert.fail("No Exception");

        fail = true;
        try {
            helper.getChildren("node1");
        } catch (RuntimeException e) {
            fail = false;
        }
        if (fail)
            Assert.fail("No Exception");

        fail = true;
        try {
            helper.getDescendants("node1");
        } catch (RuntimeException e) {
            fail = false;
        }
        if (fail)
            Assert.fail("No Exception");

    }

    @Test
    public void testInValidTree2() throws Exception {
        Map<String, String> map = createFromArray(new String[][]{ new String[]{ "node1", "node2" }, new String[]{ "node2", "node1" } });
        RoleHierarchyHelper helper = new RoleHierarchyHelper(map);
        boolean fail;
        helper.getParent("node1");// ok

        fail = true;
        try {
            helper.getAncestors("node1");
        } catch (RuntimeException e) {
            fail = false;
        }
        if (fail)
            Assert.fail("No Exception");

        helper.getChildren("node1");// ok

        fail = true;
        try {
            helper.getDescendants("node1");
        } catch (RuntimeException e) {
            fail = false;
        }
        if (fail)
            Assert.fail("No Exception");

    }
}

