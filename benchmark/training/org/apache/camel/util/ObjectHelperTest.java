/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.util;


import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.bean.MyOtherFooBean;
import org.apache.camel.component.bean.MyStaticClass;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.CamelContextHelper;
import org.apache.camel.support.ObjectHelper;
import org.apache.camel.support.org.apache.camel.util.ObjectHelper;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ObjectHelperTest extends Assert {
    @Test
    public void testLoadResourceAsStream() {
        InputStream res1 = org.apache.camel.util.ObjectHelper.loadResourceAsStream("org/apache/camel/util/ObjectHelperResourceTestFile.properties");
        InputStream res2 = org.apache.camel.util.ObjectHelper.loadResourceAsStream("/org/apache/camel/util/ObjectHelperResourceTestFile.properties");
        Assert.assertNotNull("Cannot load resource without leading \"/\"", res1);
        Assert.assertNotNull("Cannot load resource with leading \"/\"", res2);
        IOHelper.close(res1, res2);
    }

    @Test
    public void testLoadResource() {
        URL url1 = org.apache.camel.util.ObjectHelper.loadResourceAsURL("org/apache/camel/util/ObjectHelperResourceTestFile.properties");
        URL url2 = org.apache.camel.util.ObjectHelper.loadResourceAsURL("/org/apache/camel/util/ObjectHelperResourceTestFile.properties");
        Assert.assertNotNull("Cannot load resource without leading \"/\"", url1);
        Assert.assertNotNull("Cannot load resource with leading \"/\"", url2);
    }

    @Test
    public void testGetPropertyName() throws Exception {
        Method method = getClass().getMethod("setCheese", String.class);
        Assert.assertNotNull("should have found a method!", method);
        String name = org.apache.camel.util.ObjectHelper.getPropertyName(method);
        Assert.assertEquals("Property name", "cheese", name);
    }

    @Test
    public void testContains() throws Exception {
        String[] array = new String[]{ "foo", "bar" };
        Collection<String> collection = Arrays.asList(array);
        Assert.assertTrue(ObjectHelper.contains(array, "foo"));
        Assert.assertTrue(ObjectHelper.contains(collection, "foo"));
        Assert.assertTrue(ObjectHelper.contains("foo", "foo"));
        Assert.assertFalse(ObjectHelper.contains(array, "xyz"));
        Assert.assertFalse(ObjectHelper.contains(collection, "xyz"));
        Assert.assertFalse(ObjectHelper.contains("foo", "xyz"));
    }

    @Test
    public void testContainsStringBuilder() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello World");
        Assert.assertTrue(ObjectHelper.contains(sb, "World"));
        Assert.assertTrue(ObjectHelper.contains(sb, new StringBuffer("World")));
        Assert.assertTrue(ObjectHelper.contains(sb, new StringBuilder("World")));
        Assert.assertFalse(ObjectHelper.contains(sb, "Camel"));
        Assert.assertFalse(ObjectHelper.contains(sb, new StringBuffer("Camel")));
        Assert.assertFalse(ObjectHelper.contains(sb, new StringBuilder("Camel")));
    }

    @Test
    public void testContainsStringBuffer() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("Hello World");
        Assert.assertTrue(ObjectHelper.contains(sb, "World"));
        Assert.assertTrue(ObjectHelper.contains(sb, new StringBuffer("World")));
        Assert.assertTrue(ObjectHelper.contains(sb, new StringBuilder("World")));
        Assert.assertFalse(ObjectHelper.contains(sb, "Camel"));
        Assert.assertFalse(ObjectHelper.contains(sb, new StringBuffer("Camel")));
        Assert.assertFalse(ObjectHelper.contains(sb, new StringBuilder("Camel")));
    }

    @Test
    public void testEqual() {
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equal(null, null));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equal("", ""));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equal(" ", " "));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equal("Hello", "Hello"));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equal(123, 123));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equal(true, true));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equal(null, ""));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equal("", null));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equal(" ", "    "));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equal("Hello", "World"));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equal(true, false));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equal(new Object(), new Object()));
        byte[] a = new byte[]{ 40, 50, 60 };
        byte[] b = new byte[]{ 40, 50, 60 };
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equal(a, b));
        a = new byte[]{ 40, 50, 60 };
        b = new byte[]{ 40, 50, 60, 70 };
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equal(a, b));
    }

    @Test
    public void testEqualByteArray() {
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equalByteArray("Hello".getBytes(), "Hello".getBytes()));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equalByteArray("Hello".getBytes(), "World".getBytes()));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equalByteArray("Hello Thai Elephant \u0e08".getBytes(), "Hello Thai Elephant \u0e08".getBytes()));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equalByteArray(null, null));
        byte[] empty = new byte[0];
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equalByteArray(empty, empty));
        byte[] a = new byte[]{ 40, 50, 60 };
        byte[] b = new byte[]{ 40, 50, 60 };
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equalByteArray(a, b));
        a = new byte[]{ 40, 50, 60 };
        b = new byte[]{ 40, 50, 60, 70 };
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equalByteArray(a, b));
        a = new byte[]{ 40, 50, 60, 70 };
        b = new byte[]{ 40, 50, 60 };
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equalByteArray(a, b));
        a = new byte[]{ 40, 50, 60 };
        b = new byte[0];
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equalByteArray(a, b));
        a = new byte[0];
        b = new byte[]{ 40, 50, 60 };
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equalByteArray(a, b));
        a = new byte[]{ 40, 50, 60 };
        b = null;
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equalByteArray(a, b));
        a = null;
        b = new byte[]{ 40, 50, 60 };
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.equalByteArray(a, b));
        a = null;
        b = null;
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.equalByteArray(a, b));
    }

    @Test
    public void testCreateIterator() {
        List<String> list = new ArrayList<>();
        Iterator<String> iterator = list.iterator();
        Assert.assertSame("Should return the same iterator", iterator, ObjectHelper.createIterator(iterator));
    }

    @Test
    public void testCreateIteratorAllowEmpty() {
        String s = "a,b,,c";
        Iterator<?> it = ObjectHelper.createIterator(s, ",", true);
        Assert.assertEquals("a", it.next());
        Assert.assertEquals("b", it.next());
        Assert.assertEquals("", it.next());
        Assert.assertEquals("c", it.next());
    }

    @Test
    public void testCreateIteratorPattern() {
        String s = "a\nb\rc";
        Iterator<?> it = ObjectHelper.createIterator(s, "\n|\r", false, true);
        Assert.assertEquals("a", it.next());
        Assert.assertEquals("b", it.next());
        Assert.assertEquals("c", it.next());
    }

    @Test
    public void testCreateIteratorWithStringAndCommaSeparator() {
        String s = "a,b,c";
        Iterator<?> it = ObjectHelper.createIterator(s, ",");
        Assert.assertEquals("a", it.next());
        Assert.assertEquals("b", it.next());
        Assert.assertEquals("c", it.next());
    }

    @Test
    public void testCreateIteratorWithStringAndCommaSeparatorEmptyString() {
        String s = "";
        Iterator<?> it = ObjectHelper.createIterator(s, ",", true);
        Assert.assertEquals("", it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertEquals("no more element available for '' at the index 1", nsee.getMessage());
        }
    }

    @Test
    public void testCreateIteratorWithStringAndSemiColonSeparator() {
        String s = "a;b;c";
        Iterator<?> it = ObjectHelper.createIterator(s, ";");
        Assert.assertEquals("a", it.next());
        Assert.assertEquals("b", it.next());
        Assert.assertEquals("c", it.next());
    }

    @Test
    public void testCreateIteratorWithStringAndCommaInParanthesesSeparator() {
        String s = "bean:foo?method=bar('A','B','C')";
        Iterator<?> it = ObjectHelper.createIterator(s, ",");
        Assert.assertEquals("bean:foo?method=bar('A','B','C')", it.next());
    }

    @Test
    public void testCreateIteratorWithStringAndCommaInParanthesesSeparatorTwo() {
        String s = "bean:foo?method=bar('A','B','C'),bean:bar?method=cool('A','Hello,World')";
        Iterator<?> it = ObjectHelper.createIterator(s, ",");
        Assert.assertEquals("bean:foo?method=bar('A','B','C')", it.next());
        Assert.assertEquals("bean:bar?method=cool('A','Hello,World')", it.next());
    }

    // CHECKSTYLE:OFF
    @Test
    public void testCreateIteratorWithPrimitiveArrayTypes() {
        Iterator<?> it = ObjectHelper.createIterator(new byte[]{ 13, Byte.MAX_VALUE, 7, Byte.MIN_VALUE }, null);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Byte.valueOf(((byte) (13))), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Byte.MAX_VALUE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Byte.valueOf(((byte) (7))), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Byte.MIN_VALUE, it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[B@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 4"));
        }
        it = ObjectHelper.createIterator(new byte[]{  }, null);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[B@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 0"));
        }
        it = ObjectHelper.createIterator(new short[]{ 13, Short.MAX_VALUE, 7, Short.MIN_VALUE }, null);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Short.valueOf(((short) (13))), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Short.MAX_VALUE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Short.valueOf(((short) (7))), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Short.MIN_VALUE, it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[S@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 4"));
        }
        it = ObjectHelper.createIterator(new short[]{  }, null);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[S@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 0"));
        }
        it = ObjectHelper.createIterator(new int[]{ 13, Integer.MAX_VALUE, 7, Integer.MIN_VALUE }, null);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Integer.valueOf(13), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Integer.MAX_VALUE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Integer.valueOf(7), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Integer.MIN_VALUE, it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[I@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 4"));
        }
        it = ObjectHelper.createIterator(new int[]{  }, null);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[I@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 0"));
        }
        it = ObjectHelper.createIterator(new long[]{ 13L, Long.MAX_VALUE, 7L, Long.MIN_VALUE }, null);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Long.valueOf(13), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Long.MAX_VALUE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Long.valueOf(7), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Long.MIN_VALUE, it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[J@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 4"));
        }
        it = ObjectHelper.createIterator(new long[]{  }, null);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[J@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 0"));
        }
        it = ObjectHelper.createIterator(new float[]{ 13.7F, Float.MAX_VALUE, 7.13F, Float.MIN_VALUE }, null);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Float.valueOf(13.7F), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Float.MAX_VALUE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Float.valueOf(7.13F), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Float.MIN_VALUE, it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[F@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 4"));
        }
        it = ObjectHelper.createIterator(new float[]{  }, null);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[F@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 0"));
        }
        it = ObjectHelper.createIterator(new double[]{ 13.7, Double.MAX_VALUE, 7.13, Double.MIN_VALUE }, null);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Double.valueOf(13.7), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Double.MAX_VALUE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Double.valueOf(7.13), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Double.MIN_VALUE, it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[D@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 4"));
        }
        it = ObjectHelper.createIterator(new double[]{  }, null);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[D@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 0"));
        }
        it = ObjectHelper.createIterator(new char[]{ 'C', 'a', 'm', 'e', 'l' }, null);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Character.valueOf('C'), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Character.valueOf('a'), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Character.valueOf('m'), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Character.valueOf('e'), it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Character.valueOf('l'), it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[C@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 5"));
        }
        it = ObjectHelper.createIterator(new char[]{  }, null);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[C@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 0"));
        }
        it = ObjectHelper.createIterator(new boolean[]{ false, true, false, true, true }, null);
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Boolean.FALSE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Boolean.TRUE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Boolean.FALSE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Boolean.TRUE, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(Boolean.TRUE, it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[Z@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 5"));
        }
        it = ObjectHelper.createIterator(new boolean[]{  }, null);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for '[Z@"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 0"));
        }
    }

    // CHECKSTYLE:ON
    @Test
    public void testArrayAsIterator() throws Exception {
        String[] data = new String[]{ "a", "b" };
        Iterator<?> iter = ObjectHelper.createIterator(data);
        Assert.assertTrue("should have next", iter.hasNext());
        Object a = iter.next();
        Assert.assertEquals("a", "a", a);
        Assert.assertTrue("should have next", iter.hasNext());
        Object b = iter.next();
        Assert.assertEquals("b", "b", b);
        Assert.assertFalse("should not have a next", iter.hasNext());
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isEmpty(null));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isEmpty(""));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isEmpty(" "));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isEmpty("A"));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isEmpty(" A"));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isEmpty(" A "));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isEmpty(new Object()));
    }

    @Test
    public void testIsNotEmpty() {
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNotEmpty(null));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNotEmpty(""));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNotEmpty(" "));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isNotEmpty("A"));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isNotEmpty(" A"));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isNotEmpty(" A "));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isNotEmpty(new Object()));
    }

    @Test
    public void testIteratorWithComma() {
        Iterator<?> it = ObjectHelper.createIterator("Claus,Jonathan");
        Assert.assertEquals("Claus", it.next());
        Assert.assertEquals("Jonathan", it.next());
        Assert.assertEquals(false, it.hasNext());
    }

    @Test
    public void testIteratorWithOtherDelimiter() {
        Iterator<?> it = ObjectHelper.createIterator("Claus#Jonathan", "#");
        Assert.assertEquals("Claus", it.next());
        Assert.assertEquals("Jonathan", it.next());
        Assert.assertEquals(false, it.hasNext());
    }

    @Test
    public void testIteratorEmpty() {
        Iterator<?> it = ObjectHelper.createIterator("");
        Assert.assertEquals(false, it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertEquals("no more element available for '' at the index 0", nsee.getMessage());
        }
        it = ObjectHelper.createIterator("    ");
        Assert.assertEquals(false, it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertEquals("no more element available for '    ' at the index 0", nsee.getMessage());
        }
        it = ObjectHelper.createIterator(null);
        Assert.assertEquals(false, it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
        }
    }

    @Test
    public void testIteratorIdempotentNext() {
        Iterator<?> it = ObjectHelper.createIterator("a");
        Assert.assertTrue(it.hasNext());
        Assert.assertTrue(it.hasNext());
        it.next();
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertEquals("no more element available for 'a' at the index 1", nsee.getMessage());
        }
    }

    @Test
    public void testIteratorIdempotentNextWithNodeList() {
        NodeList nodeList = new NodeList() {
            public Node item(int index) {
                return null;
            }

            public int getLength() {
                return 1;
            }
        };
        Iterator<?> it = ObjectHelper.createIterator(nodeList);
        Assert.assertTrue(it.hasNext());
        Assert.assertTrue(it.hasNext());
        it.next();
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().startsWith("no more element available for 'org.apache.camel.util.ObjectHelperTest$"));
            Assert.assertTrue(nsee.getMessage(), nsee.getMessage().endsWith("at the index 1"));
        }
    }

    @Test
    public void testGetCamelContextPropertiesWithPrefix() {
        CamelContext context = new DefaultCamelContext();
        Map<String, String> properties = context.getGlobalOptions();
        properties.put("camel.object.helper.test1", "test1");
        properties.put("camel.object.helper.test2", "test2");
        properties.put("camel.object.test", "test");
        Properties result = CamelContextHelper.getCamelPropertiesWithPrefix("camel.object.helper.", context);
        Assert.assertEquals("Get a wrong size properties", 2, result.size());
        Assert.assertEquals("It should contain the test1", "test1", result.get("test1"));
        Assert.assertEquals("It should contain the test2", "test2", result.get("test2"));
    }

    @Test
    public void testEvaluateAsPredicate() throws Exception {
        Assert.assertEquals(false, org.apache.camel.util.ObjectHelper.evaluateValuePredicate(null));
        Assert.assertEquals(true, org.apache.camel.util.ObjectHelper.evaluateValuePredicate(123));
        Assert.assertEquals(true, org.apache.camel.util.ObjectHelper.evaluateValuePredicate("true"));
        Assert.assertEquals(true, org.apache.camel.util.ObjectHelper.evaluateValuePredicate("TRUE"));
        Assert.assertEquals(false, org.apache.camel.util.ObjectHelper.evaluateValuePredicate("false"));
        Assert.assertEquals(false, org.apache.camel.util.ObjectHelper.evaluateValuePredicate("FALSE"));
        Assert.assertEquals(true, org.apache.camel.util.ObjectHelper.evaluateValuePredicate("foobar"));
        Assert.assertEquals(true, org.apache.camel.util.ObjectHelper.evaluateValuePredicate(""));
        Assert.assertEquals(true, org.apache.camel.util.ObjectHelper.evaluateValuePredicate(" "));
        List<String> list = new ArrayList<>();
        Assert.assertEquals(false, org.apache.camel.util.ObjectHelper.evaluateValuePredicate(list));
        list.add("foo");
        Assert.assertEquals(true, org.apache.camel.util.ObjectHelper.evaluateValuePredicate(list));
    }

    @Test
    public void testIsPrimitiveArrayType() {
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(byte[].class));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(short[].class));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(int[].class));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(long[].class));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(float[].class));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(double[].class));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(char[].class));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(boolean[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Object[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Byte[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Short[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Integer[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Long[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Float[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Double[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Character[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Boolean[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(Void[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(CamelContext[].class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isPrimitiveArrayType(null));
    }

    @Test
    public void testGetDefaultCharSet() {
        Assert.assertNotNull(org.apache.camel.util.ObjectHelper.getDefaultCharacterSet());
    }

    @Test
    public void testConvertPrimitiveTypeToWrapper() {
        Assert.assertEquals("java.lang.Integer", org.apache.camel.util.ObjectHelper.convertPrimitiveTypeToWrapperType(int.class).getName());
        Assert.assertEquals("java.lang.Long", org.apache.camel.util.ObjectHelper.convertPrimitiveTypeToWrapperType(long.class).getName());
        Assert.assertEquals("java.lang.Double", org.apache.camel.util.ObjectHelper.convertPrimitiveTypeToWrapperType(double.class).getName());
        Assert.assertEquals("java.lang.Float", org.apache.camel.util.ObjectHelper.convertPrimitiveTypeToWrapperType(float.class).getName());
        Assert.assertEquals("java.lang.Short", org.apache.camel.util.ObjectHelper.convertPrimitiveTypeToWrapperType(short.class).getName());
        Assert.assertEquals("java.lang.Byte", org.apache.camel.util.ObjectHelper.convertPrimitiveTypeToWrapperType(byte.class).getName());
        Assert.assertEquals("java.lang.Boolean", org.apache.camel.util.ObjectHelper.convertPrimitiveTypeToWrapperType(boolean.class).getName());
        Assert.assertEquals("java.lang.Character", org.apache.camel.util.ObjectHelper.convertPrimitiveTypeToWrapperType(char.class).getName());
        // non primitive just fall through
        Assert.assertEquals("java.lang.Object", org.apache.camel.util.ObjectHelper.convertPrimitiveTypeToWrapperType(Object.class).getName());
    }

    @Test
    public void testAsString() {
        String[] args = new String[]{ "foo", "bar" };
        String out = org.apache.camel.util.ObjectHelper.asString(args);
        Assert.assertNotNull(out);
        Assert.assertEquals("{foo, bar}", out);
    }

    @Test
    public void testName() {
        Assert.assertEquals("java.lang.Integer", org.apache.camel.util.ObjectHelper.name(Integer.class));
        Assert.assertEquals(null, org.apache.camel.util.ObjectHelper.name(null));
    }

    @Test
    public void testClassName() {
        Assert.assertEquals("java.lang.Integer", org.apache.camel.util.ObjectHelper.className(Integer.valueOf("5")));
        Assert.assertEquals(null, org.apache.camel.util.ObjectHelper.className(null));
    }

    @Test
    public void testGetSystemPropertyDefault() {
        Assert.assertEquals("foo", org.apache.camel.util.ObjectHelper.getSystemProperty("CamelFooDoesNotExist", "foo"));
    }

    @Test
    public void testGetSystemPropertyBooleanDefault() {
        Assert.assertEquals(true, org.apache.camel.util.ObjectHelper.getSystemProperty("CamelFooDoesNotExist", Boolean.TRUE));
    }

    @Test
    public void testMatches() {
        List<Object> data = new ArrayList<>();
        data.add("foo");
        data.add("bar");
        Assert.assertEquals(true, org.apache.camel.util.ObjectHelper.matches(data));
        data.clear();
        data.add(Boolean.FALSE);
        data.add("bar");
        Assert.assertEquals(false, org.apache.camel.util.ObjectHelper.matches(data));
        data.clear();
        Assert.assertEquals(false, org.apache.camel.util.ObjectHelper.matches(data));
    }

    @Test
    public void testToBoolean() {
        Assert.assertEquals(Boolean.TRUE, org.apache.camel.util.ObjectHelper.toBoolean(Boolean.TRUE));
        Assert.assertEquals(Boolean.TRUE, org.apache.camel.util.ObjectHelper.toBoolean("true"));
        Assert.assertEquals(Boolean.TRUE, org.apache.camel.util.ObjectHelper.toBoolean(Integer.valueOf("1")));
        Assert.assertEquals(Boolean.FALSE, org.apache.camel.util.ObjectHelper.toBoolean(Integer.valueOf("0")));
        Assert.assertEquals(null, org.apache.camel.util.ObjectHelper.toBoolean(new Date()));
    }

    @Test
    public void testIteratorWithMessage() {
        Message msg = new org.apache.camel.support.DefaultMessage(new DefaultCamelContext());
        msg.setBody("a,b,c");
        Iterator<?> it = ObjectHelper.createIterator(msg);
        Assert.assertEquals("a", it.next());
        Assert.assertEquals("b", it.next());
        Assert.assertEquals("c", it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
        }
    }

    @Test
    public void testIteratorWithEmptyMessage() {
        Message msg = new org.apache.camel.support.DefaultMessage(new DefaultCamelContext());
        msg.setBody("");
        Iterator<?> it = ObjectHelper.createIterator(msg);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
            Assert.assertEquals("no more element available for '' at the index 0", nsee.getMessage());
        }
    }

    @Test
    public void testIteratorWithNullMessage() {
        Message msg = new org.apache.camel.support.DefaultMessage(new DefaultCamelContext());
        msg.setBody(null);
        Iterator<?> it = ObjectHelper.createIterator(msg);
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
        }
    }

    @Test
    public void testIterable() {
        final List<String> data = new ArrayList<>();
        data.add("A");
        data.add("B");
        data.add("C");
        Iterable<String> itb = new Iterable<String>() {
            public Iterator<String> iterator() {
                return data.iterator();
            }
        };
        Iterator<?> it = ObjectHelper.createIterator(itb);
        Assert.assertEquals("A", it.next());
        Assert.assertEquals("B", it.next());
        Assert.assertEquals("C", it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchElementException nsee) {
            // expected
        }
    }

    @Test
    public void testLookupConstantFieldValue() {
        Assert.assertEquals("CamelFileName", org.apache.camel.util.ObjectHelper.lookupConstantFieldValue(Exchange.class, "FILE_NAME"));
        Assert.assertEquals(null, org.apache.camel.util.ObjectHelper.lookupConstantFieldValue(Exchange.class, "XXX"));
        Assert.assertEquals(null, org.apache.camel.util.ObjectHelper.lookupConstantFieldValue(null, "FILE_NAME"));
    }

    @Test
    public void testHasDefaultPublicNoArgConstructor() {
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.hasDefaultPublicNoArgConstructor(ObjectHelperTest.class));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.hasDefaultPublicNoArgConstructor(MyStaticClass.class));
    }

    @Test
    public void testIdentityHashCode() {
        MyDummyObject dummy = new MyDummyObject("Camel");
        String code = org.apache.camel.util.ObjectHelper.getIdentityHashCode(dummy);
        String code2 = org.apache.camel.util.ObjectHelper.getIdentityHashCode(dummy);
        Assert.assertEquals(code, code2);
        MyDummyObject dummyB = new MyDummyObject("Camel");
        String code3 = org.apache.camel.util.ObjectHelper.getIdentityHashCode(dummyB);
        Assert.assertNotSame(code, code3);
    }

    @Test
    public void testIsNaN() throws Exception {
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isNaN(Float.NaN));
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isNaN(Double.NaN));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNaN(null));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNaN(""));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNaN("1.0"));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNaN(1));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNaN(1.5F));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNaN(1.5));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNaN(false));
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isNaN(true));
    }

    @Test
    public void testNotNull() {
        Long expected = 3L;
        Long actual = org.apache.camel.util.ObjectHelper.notNull(expected, "expected");
        Assert.assertSame("Didn't get the same object back!", expected, actual);
        Long actual2 = org.apache.camel.util.ObjectHelper.notNull(expected, "expected", "holder");
        Assert.assertSame("Didn't get the same object back!", expected, actual2);
        Long expected2 = null;
        try {
            org.apache.camel.util.ObjectHelper.notNull(expected2, "expected2");
            Assert.fail("Should have thrown exception");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("expected2 must be specified", iae.getMessage());
        }
        try {
            org.apache.camel.util.ObjectHelper.notNull(expected2, "expected2", "holder");
            Assert.fail("Should have thrown exception");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("expected2 must be specified on: holder", iae.getMessage());
        }
    }

    @Test
    public void testSameMethodIsOverride() throws Exception {
        Method m = MyOtherFooBean.class.getMethod("toString", Object.class);
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isOverridingMethod(m, m, false));
    }

    @Test
    public void testOverloadIsNotOverride() throws Exception {
        Method m1 = MyOtherFooBean.class.getMethod("toString", Object.class);
        Method m2 = MyOtherFooBean.class.getMethod("toString", String.class);
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isOverridingMethod(m2, m1, false));
    }

    @Test
    public void testOverrideEquivalentSignatureFromSiblingClassIsNotOverride() throws Exception {
        Method m1 = Double.class.getMethod("intValue");
        Method m2 = Float.class.getMethod("intValue");
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isOverridingMethod(m2, m1, false));
    }

    @Test
    public void testOverrideEquivalentSignatureFromUpperClassIsOverride() throws Exception {
        Method m1 = Double.class.getMethod("intValue");
        Method m2 = Number.class.getMethod("intValue");
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isOverridingMethod(m2, m1, false));
    }

    @Test
    public void testInheritedMethodCanOverrideInterfaceMethod() throws Exception {
        Method m1 = MyOtherFooBean.AbstractClassSize.class.getMethod("size");
        Method m2 = MyOtherFooBean.InterfaceSize.class.getMethod("size");
        Assert.assertTrue(org.apache.camel.util.ObjectHelper.isOverridingMethod(MyOtherFooBean.Clazz.class, m2, m1, false));
    }

    @Test
    public void testNonInheritedMethodCantOverrideInterfaceMethod() throws Exception {
        Method m1 = MyOtherFooBean.AbstractClassSize.class.getMethod("size");
        Method m2 = MyOtherFooBean.InterfaceSize.class.getMethod("size");
        Assert.assertFalse(org.apache.camel.util.ObjectHelper.isOverridingMethod(MyOtherFooBean.InterfaceSize.class, m2, m1, false));
    }
}

