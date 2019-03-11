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
package org.eclipse.jetty.jndi.java;


import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
import org.eclipse.jetty.jndi.NamingUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class TestLocalJNDI {
    public static class FruitFactory implements ObjectFactory {
        public FruitFactory() {
        }

        @Override
        public Object getObjectInstance(Object obj, Name name, Context ctx, Hashtable env) throws Exception {
            if (!(env.containsKey("flavour")))
                throw new Exception("No flavour!");

            if (obj instanceof Reference) {
                Reference ref = ((Reference) (obj));
                if (ref.getClassName().equals(TestLocalJNDI.Fruit.class.getName())) {
                    RefAddr addr = ref.get("fruit");
                    if (addr != null) {
                        return new TestLocalJNDI.Fruit(((String) (addr.getContent())));
                    }
                }
            }
            return null;
        }
    }

    public static class Fruit implements Referenceable {
        String fruit;

        public Fruit(String f) {
            fruit = f;
        }

        @Override
        public Reference getReference() throws NamingException {
            return new Reference(TestLocalJNDI.Fruit.class.getName(), new StringRefAddr("fruit", fruit), TestLocalJNDI.FruitFactory.class.getName(), null);// Factory location

        }

        @Override
        public String toString() {
            return fruit;
        }
    }

    @Test
    public void testLocalReferenceable() throws Exception {
        Hashtable<String, String> env1 = new Hashtable<String, String>();
        env1.put("flavour", "orange");
        InitialContext ic1 = new InitialContext(env1);
        ic1.bind("valencia", new TestLocalJNDI.Fruit("orange"));
        Object o = ic1.lookup("valencia");
        Hashtable<String, String> env2 = new Hashtable<String, String>();
        InitialContext ic2 = new InitialContext(env2);
        try {
            o = ic2.lookup("valencia");
            Assertions.fail("Constructed object from reference without correct environment");
        } catch (Exception e) {
            Assertions.assertEquals("No flavour!", e.getMessage());
        }
    }

    @Test
    public void testLocalEnvironment() throws Exception {
        Hashtable<String, String> env1 = new Hashtable<String, String>();
        env1.put("make", "holden");
        env1.put("model", "commodore");
        Object car1 = new Object();
        InitialContext ic = new InitialContext(env1);
        ic.bind("car1", car1);
        Assertions.assertNotNull(ic.lookup("car1"));
        Assertions.assertEquals(car1, ic.lookup("car1"));
        Context carz = ic.createSubcontext("carz");
        Assertions.assertNotNull(carz);
        Hashtable ht = carz.getEnvironment();
        Assertions.assertNotNull(ht);
        Assertions.assertEquals("holden", ht.get("make"));
        Assertions.assertEquals("commodore", ht.get("model"));
        Hashtable<String, String> env2 = new Hashtable<String, String>();
        env2.put("flavour", "strawberry");
        InitialContext ic2 = new InitialContext(env2);
        Assertions.assertEquals(car1, ic2.lookup("car1"));
        Context c = ((Context) (ic2.lookup("carz")));
        Assertions.assertNotNull(c);
        ht = c.getEnvironment();
        Assertions.assertEquals("holden", ht.get("make"));
        Assertions.assertEquals("commodore", ht.get("model"));
        Context icecreamz = ic2.createSubcontext("icecreamz");
        ht = icecreamz.getEnvironment();
        Assertions.assertNotNull(ht);
        Assertions.assertEquals("strawberry", ht.get("flavour"));
        Context hatchbackz = ic2.createSubcontext("carz/hatchbackz");
        Assertions.assertNotNull(hatchbackz);
        ht = hatchbackz.getEnvironment();
        Assertions.assertNotNull(ht);
        Assertions.assertEquals("holden", ht.get("make"));
        Assertions.assertEquals("commodore", ht.get("model"));
        Assertions.assertEquals(null, ht.get("flavour"));
        c = ((Context) (ic.lookup("carz/hatchbackz")));
        Assertions.assertNotNull(c);
        Assertions.assertEquals(hatchbackz, c);
    }

    @Test
    public void testLocal() throws Exception {
        InitialContext ic = new InitialContext();
        NameParser parser = ic.getNameParser("");
        ic.bind("foo", "xxx");
        Object o = ic.lookup("foo");
        Assertions.assertNotNull(o);
        Assertions.assertEquals("xxx", ((String) (o)));
        ic.unbind("foo");
        try {
            ic.lookup("foo");
            Assertions.fail("Foo exists");
        } catch (NameNotFoundException e) {
            // expected
        }
        Name name = parser.parse("a");
        name.addAll(parser.parse("b/c/d"));
        NamingUtil.bind(ic, name.toString(), "333");
        Assertions.assertNotNull(ic.lookup("a"));
        Assertions.assertNotNull(ic.lookup("a/b"));
        Assertions.assertNotNull(ic.lookup("a/b/c"));
        Context c = ((Context) (ic.lookup("a/b/c")));
        o = c.lookup("d");
        Assertions.assertNotNull(o);
        Assertions.assertEquals("333", ((String) (o)));
        Assertions.assertEquals("333", ic.lookup(name));
        ic.destroySubcontext("a");
        try {
            ic.lookup("a");
            Assertions.fail("context a was not destroyed");
        } catch (NameNotFoundException e) {
            // expected
        }
        name = parser.parse("");
        name.add("x");
        Name suffix = parser.parse("y/z");
        name.addAll(suffix);
        NamingUtil.bind(ic, name.toString(), "555");
        Assertions.assertEquals("555", ic.lookup(name));
        ic.destroySubcontext("x");
    }
}

