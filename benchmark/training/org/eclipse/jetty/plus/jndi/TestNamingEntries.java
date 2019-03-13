/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.plus.jndi;


import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestNamingEntries {
    public class ScopeA {
        @Override
        public String toString() {
            return ((this.getClass().getName()) + "@") + (super.hashCode());
        }
    }

    public class ScopeB extends TestNamingEntries.ScopeA {}

    public static class SomeObject {
        private int value;

        public SomeObject(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static class SomeObjectFactory implements ObjectFactory {
        public SomeObjectFactory() {
        }

        @Override
        public Object getObjectInstance(Object arg0, Name arg1, Context arg2, Hashtable arg3) throws Exception {
            Reference ref = ((Reference) (arg0));
            RefAddr refAddr = ref.get(0);
            String valueName = refAddr.getType();
            if (!(valueName.equalsIgnoreCase("val")))
                throw new RuntimeException(("Unrecognized refaddr type = " + valueName));

            String value = ((String) (refAddr.getContent()));
            return new TestNamingEntries.SomeObject(Integer.parseInt(value.trim()));
        }
    }

    public static class SomeOtherObject extends TestNamingEntries.SomeObject implements Referenceable {
        public SomeOtherObject(String value) {
            super(Integer.parseInt(value.trim()));
        }

        @Override
        public Reference getReference() throws NamingException {
            RefAddr refAddr = new StringRefAddr("val", String.valueOf(getValue()));
            return new Reference(TestNamingEntries.SomeOtherObject.class.getName(), refAddr, TestNamingEntries.SomeOtherObjectFactory.class.getName(), null);
        }
    }

    public static class SomeOtherObjectFactory implements ObjectFactory {
        public SomeOtherObjectFactory() {
        }

        @Override
        public Object getObjectInstance(Object arg0, Name arg1, Context arg2, Hashtable arg3) throws Exception {
            Reference ref = ((Reference) (arg0));
            RefAddr refAddr = ref.get(0);
            String valueName = refAddr.getType();
            if (!(valueName.equalsIgnoreCase("val")))
                throw new RuntimeException(("Unrecognized refaddr type = " + valueName));

            String value = ((String) (refAddr.getContent()));
            return new TestNamingEntries.SomeOtherObject(value.trim());
        }
    }

    private TestNamingEntries.SomeObject someObject;

    @Test
    public void testEnvEntryNoScope() throws Exception {
        EnvEntry ee = new EnvEntry("nameZ", "zstring", true);
        List list = NamingEntryUtil.lookupNamingEntries(null, EnvEntry.class);
        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(1, list.size());
        Object o = list.get(0);
        Assertions.assertTrue((o instanceof EnvEntry));
        EnvEntry eo = ((EnvEntry) (o));
        Assertions.assertEquals("nameZ", eo.getJndiName());
    }

    @Test
    public void testEnvEntryOverride() throws Exception {
        TestNamingEntries.ScopeA scope = new TestNamingEntries.ScopeA();
        EnvEntry ee = new EnvEntry(scope, "nameA", someObject, true);
        NamingEntry ne = NamingEntryUtil.lookupNamingEntry(scope, "nameA");
        Assertions.assertNotNull(ne);
        Assertions.assertTrue((ne instanceof EnvEntry));
        Assertions.assertTrue(isOverrideWebXml());
        Context scopeContext = NamingEntryUtil.getContextForScope(scope);
        Assertions.assertNotNull(scopeContext);
        Context namingEntriesContext = NamingEntryUtil.getContextForNamingEntries(scope);
        Assertions.assertNotNull(namingEntriesContext);
        Assertions.assertEquals(someObject, scopeContext.lookup("nameA"));
    }

    @Test
    public void testEnvEntryNonOverride() throws Exception {
        TestNamingEntries.ScopeA scope = new TestNamingEntries.ScopeA();
        EnvEntry ee = new EnvEntry(scope, "nameA", someObject, false);
        NamingEntry ne = NamingEntryUtil.lookupNamingEntry(scope, "nameA");
        Assertions.assertNotNull(ne);
        Assertions.assertTrue((ne instanceof EnvEntry));
        Assertions.assertFalse(isOverrideWebXml());
        Context scopeContext = NamingEntryUtil.getContextForScope(scope);
        Assertions.assertNotNull(scopeContext);
        Context namingEntriesContext = NamingEntryUtil.getContextForNamingEntries(scope);
        Assertions.assertNotNull(namingEntriesContext);
        Assertions.assertEquals(someObject, scopeContext.lookup("nameA"));
    }

    @Test
    public void testResource() throws Exception {
        InitialContext icontext = new InitialContext();
        Resource resource = new Resource(null, "resourceA/b/c", someObject);
        NamingEntry ne = NamingEntryUtil.lookupNamingEntry(null, "resourceA/b/c");
        Assertions.assertNotNull(ne);
        Assertions.assertTrue((ne instanceof Resource));
        Assertions.assertEquals(icontext.lookup("resourceA/b/c"), someObject);
        Object scope = new TestNamingEntries.ScopeA();
        Resource resource2 = new Resource(scope, "resourceB", someObject);
        ne = NamingEntryUtil.lookupNamingEntry(scope, "resourceB");
        Assertions.assertNotNull(ne);
        Assertions.assertTrue((ne instanceof Resource));
        ne = NamingEntryUtil.lookupNamingEntry(null, "resourceB");
        Assertions.assertNull(ne);
        ne = NamingEntryUtil.lookupNamingEntry(new TestNamingEntries.ScopeB(), "resourceB");
        Assertions.assertNull(ne);
        testLink();
    }

    @Test
    public void testNullJndiName() throws Exception {
        try {
            InitialContext icontext = new InitialContext();
            Resource resource = new Resource(null, "foo");
            Assertions.fail("Null jndi name should not be permitted");
        } catch (NamingException e) {
            // expected
        }
    }

    @Test
    public void testNullObject() throws Exception {
        InitialContext icontext = new InitialContext();
        Resource resource = new Resource("foo/bar", null);
        NamingEntry ne = NamingEntryUtil.lookupNamingEntry(null, "foo/bar");
        Assertions.assertNotNull(ne);
        Object o = icontext.lookup("foo/bar");
        Assertions.assertNull(o);
    }

    @Test
    public void testLink() throws Exception {
        TestNamingEntries.ScopeA scope = new TestNamingEntries.ScopeA();
        InitialContext icontext = new InitialContext();
        Link link = new Link("linked-resourceA", "resourceB");
        NamingEntry ne = NamingEntryUtil.lookupNamingEntry(null, "linked-resourceA");
        Assertions.assertNotNull(ne);
        Assertions.assertTrue((ne instanceof Link));
        Assertions.assertEquals(icontext.lookup("linked-resourceA"), "resourceB");
        link = new Link(scope, "jdbc/linked-resourceX", "jdbc/linked-resourceY");
        ne = NamingEntryUtil.lookupNamingEntry(scope, "jdbc/linked-resourceX");
        Assertions.assertNotNull(ne);
        Assertions.assertTrue((ne instanceof Link));
    }

    @Test
    public void testResourceReferenceable() throws Exception {
        TestNamingEntries.SomeOtherObject someOtherObj = new TestNamingEntries.SomeOtherObject("100");
        InitialContext icontext = new InitialContext();
        Resource res = new Resource("resourceByReferenceable", someOtherObj);
        Object o = icontext.lookup("resourceByReferenceable");
        Assertions.assertNotNull(o);
        Assertions.assertTrue((o instanceof TestNamingEntries.SomeOtherObject));
        Assertions.assertEquals(((TestNamingEntries.SomeOtherObject) (o)).getValue(), 100);
    }

    @Test
    public void testResourceReference() throws Exception {
        RefAddr refAddr = new StringRefAddr("val", "10");
        Reference ref = new Reference(TestNamingEntries.SomeObject.class.getName(), refAddr, TestNamingEntries.SomeObjectFactory.class.getName(), null);
        InitialContext icontext = new InitialContext();
        Resource resource = new Resource(null, "resourceByRef", ref);
        NamingEntry ne = NamingEntryUtil.lookupNamingEntry(null, "resourceByRef");
        Assertions.assertNotNull(ne);
        Assertions.assertTrue((ne instanceof Resource));
        Object o = icontext.lookup("resourceByRef");
        Assertions.assertNotNull(o);
        Assertions.assertTrue((o instanceof TestNamingEntries.SomeObject));
        Assertions.assertEquals(((TestNamingEntries.SomeObject) (o)).getValue(), 10);
    }
}

