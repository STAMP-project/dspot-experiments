/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows.util;


import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class OwsUtilsTest extends TestCase {
    public void testSimple() throws Exception {
        OwsUtilsTest.Foo foo = new OwsUtilsTest.Foo();
        foo.setA("a");
        TestCase.assertEquals("a", OwsUtils.get(foo, "a"));
        TestCase.assertNull(OwsUtils.get(foo, "b"));
        OwsUtils.set(foo, "b", 5);
        TestCase.assertEquals(5, OwsUtils.get(foo, "b"));
        TestCase.assertEquals(0.0F, OwsUtils.get(foo, "c"));
        OwsUtils.set(foo, "c", 5.0F);
        TestCase.assertEquals(5.0F, OwsUtils.get(foo, "c"));
    }

    public void testExtended() throws Exception {
        OwsUtilsTest.Bar bar = new OwsUtilsTest.Bar();
        TestCase.assertNull(OwsUtils.get(bar, "foo"));
        TestCase.assertNull(OwsUtils.get(bar, "foo.a"));
        OwsUtilsTest.Foo foo = new OwsUtilsTest.Foo();
        bar.setFoo(foo);
        TestCase.assertEquals(foo, OwsUtils.get(bar, "foo"));
        TestCase.assertNull(OwsUtils.get(bar, "foo.a"));
        foo.setA("abc");
        TestCase.assertEquals("abc", OwsUtils.get(bar, "foo.a"));
        OwsUtils.set(bar, "foo.b", 123);
        TestCase.assertEquals(123, OwsUtils.get(bar, "foo.b"));
    }

    public void testPut() throws Exception {
        OwsUtilsTest.Baz baz = new OwsUtilsTest.Baz();
        try {
            OwsUtils.put(baz, "map", "k", "v");
            TestCase.fail("null map should cause exception");
        } catch (NullPointerException e) {
        }
        baz.map = new HashMap();
        try {
            OwsUtils.put(baz, "xyz", "k", "v");
            TestCase.fail("bad property should cause exception");
        } catch (IllegalArgumentException e) {
        }
        TestCase.assertTrue(baz.map.isEmpty());
        OwsUtils.put(baz, "map", "k", "v");
        TestCase.assertEquals("v", baz.map.get("k"));
    }

    class Foo {
        String a;

        Integer b;

        float c;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public Integer getB() {
            return b;
        }

        public void setB(Integer b) {
            this.b = b;
        }

        public float getC() {
            return c;
        }

        public void setC(float c) {
            this.c = c;
        }
    }

    class Bar {
        OwsUtilsTest.Foo foo;

        Double d;

        public OwsUtilsTest.Foo getFoo() {
            return foo;
        }

        public void setFoo(OwsUtilsTest.Foo foo) {
            this.foo = foo;
        }

        public Double getD() {
            return d;
        }

        public void setD(Double d) {
            this.d = d;
        }
    }

    class Baz {
        Map map;

        public Map getMap() {
            return map;
        }
    }
}

