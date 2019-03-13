/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import AccessMode.READ;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Properties;
import junit.framework.TestCase;
import org.geoserver.security.AccessMode;
import org.junit.Test;


public class DataAccessRuleDAOTest extends TestCase {
    DataAccessRuleDAO dao;

    Properties props;

    @Test
    public void testRulesForRole() {
        TestCase.assertEquals(0, dao.getRulesAssociatedWithRole("CHALLENGE").size());
        TestCase.assertEquals(0, dao.getRulesAssociatedWithRole("NOTEXISTEND").size());
        TestCase.assertEquals(1, dao.getRulesAssociatedWithRole("ROLE_TSW").size());
        TestCase.assertEquals(1, dao.getRulesAssociatedWithRole("ROLE_TW").size());
        TestCase.assertEquals(1, dao.getRulesAssociatedWithRole("ROLE_GROUP").size());
    }

    @Test
    public void testParseGlobalLayerGroupRule() {
        DataAccessRule r = dao.parseDataAccessRule("group.r", "ROLE_GROUP_OWNER");
        TestCase.assertEquals(r.getRoot(), "group");
        TestCase.assertNull(r.getLayer());
        TestCase.assertTrue(r.isGlobalGroupRule());
        TestCase.assertEquals(READ, r.getAccessMode());
    }

    @Test
    public void testParse() {
        TestCase.assertEquals(4, dao.getRules().size());
        // check the first rule
        DataAccessRule rule = dao.getRules().get(0);
        TestCase.assertEquals("*.*.r", rule.getKey());
        TestCase.assertEquals(1, rule.getRoles().size());
        TestCase.assertEquals("*", rule.getRoles().iterator().next());
    }

    @Test
    public void testAdd() {
        TestCase.assertEquals(4, dao.getRules().size());
        DataAccessRule newRule = dao.parseDataAccessRule("*.*.w", "ROLE_GENERIC_W");
        TestCase.assertTrue(dao.addRule(newRule));
        TestCase.assertEquals(5, dao.getRules().size());
        TestCase.assertEquals(newRule, dao.getRules().get(1));
        TestCase.assertFalse(dao.addRule(newRule));
    }

    @Test
    public void testRemove() {
        TestCase.assertEquals(4, dao.getRules().size());
        DataAccessRule newRule = dao.parseDataAccessRule("*.*.w", "ROLE_GENERIC_W");
        TestCase.assertFalse(dao.removeRule(newRule));
        DataAccessRule first = dao.getRules().get(0);
        TestCase.assertTrue(dao.removeRule(first));
        TestCase.assertFalse(dao.removeRule(first));
        TestCase.assertEquals(3, dao.getRules().size());
    }

    @Test
    public void testStore() {
        Properties newProps = dao.toProperties();
        // properties equality does not seem to work...
        TestCase.assertEquals(newProps.size(), props.size());
        for (Object key : newProps.keySet()) {
            Object newValue = newProps.get(key);
            Object oldValue = newProps.get(key);
            TestCase.assertEquals(newValue, oldValue);
        }
    }

    @Test
    public void testParsePlain() {
        DataAccessRule rule = dao.parseDataAccessRule("a.b.r", "ROLE_WHO_CARES");
        TestCase.assertEquals("a", rule.getRoot());
        TestCase.assertEquals("b", rule.getLayer());
        TestCase.assertFalse(rule.isGlobalGroupRule());
        TestCase.assertEquals(READ, rule.getAccessMode());
    }

    @Test
    public void testParseSpaces() {
        DataAccessRule rule = dao.parseDataAccessRule(" a  . b . r ", "ROLE_WHO_CARES");
        TestCase.assertEquals("a", rule.getRoot());
        TestCase.assertEquals("b", rule.getLayer());
        TestCase.assertFalse(rule.isGlobalGroupRule());
        TestCase.assertEquals(READ, rule.getAccessMode());
    }

    @Test
    public void testParseEscapedDots() {
        DataAccessRule rule = dao.parseDataAccessRule("w. a\\.b . r ", "ROLE_WHO_CARES");
        TestCase.assertEquals("w", rule.getRoot());
        TestCase.assertEquals("a.b", rule.getLayer());
        TestCase.assertFalse(rule.isGlobalGroupRule());
        TestCase.assertEquals(READ, rule.getAccessMode());
    }

    @Test
    public void testStoreEscapedDots() throws Exception {
        dao.clear();
        dao.addRule(new DataAccessRule("it.geosolutions", "layer.dots", AccessMode.READ, Collections.singleton("ROLE_ABC")));
        Properties ps = dao.toProperties();
        TestCase.assertEquals(2, ps.size());
        TestCase.assertEquals("ROLE_ABC", ps.getProperty("it\\.geosolutions.layer\\.dots.r"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ps.store(bos, null);
    }
}

