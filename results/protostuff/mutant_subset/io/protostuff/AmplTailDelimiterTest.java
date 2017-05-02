/**
 * ========================================================================
 */
/**
 * Copyright 2007-2010 David Yu dyuproject@gmail.com
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */


package io.protostuff;


/**
 * Test case for tail-delimited protostuff messages.
 *
 * @author David Yu
 * @created Oct 5, 2010
 */
public class AmplTailDelimiterTest extends io.protostuff.AbstractTest {
    public <T> int writeListTo(java.io.OutputStream out, java.util.List<T> messages, io.protostuff.Schema<T> schema) throws java.io.IOException {
        return io.protostuff.ProtostuffIOUtil.writeListTo(out, messages, schema, new io.protostuff.LinkedBuffer(io.protostuff.LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    public <T> java.util.List<T> parseListFrom(java.io.InputStream in, io.protostuff.Schema<T> schema) throws java.io.IOException {
        return io.protostuff.ProtostuffIOUtil.parseListFrom(in, schema);
    }

    public void testBar() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(io.protostuff.SerializableObjects.bar);
        bars.add(io.protostuff.SerializableObjects.negativeBar);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    public void testEmptyBar() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        bars.add(new io.protostuff.Bar());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    public void testEmptyBar2() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    public void testEmptyBarInner() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(bar);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    public void testFoo() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        foos.add(io.protostuff.SerializableObjects.foo);
        foos.add(io.protostuff.SerializableObjects.foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    public void testEmptyFoo() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        foos.add(new io.protostuff.Foo());
        foos.add(new io.protostuff.Foo());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    public void testEmptyFoo2() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        foos.add(new io.protostuff.Foo());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    public void testEmptyFooInner() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        foos.add(foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    public void testEmptyFooInner2() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        bars.add(bar);
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        foos.add(foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    public void testEmptyList() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyList__5 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyList__5, 0);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList */
    public void testEmptyList_literalMutation9616() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyList_literalMutation9616__5 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyList_literalMutation9616__5, 0);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 1;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList */
    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList_cf9674 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyList_cf9674_failAssert3_add10104() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyList_cf9674_failAssert3_add10104__7 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyList_cf9674_failAssert3_add10104__7, 0);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_3215 = (io.protostuff.DelimiterTest)null;
            // StatementAdderMethod cloned existing statement
            vc_3215.testBaz();
            // MethodAssertGenerator build local variable
            Object o_18_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyList_cf9674 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList */
    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList_add9615 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyList_add9615_cf9811_failAssert15_literalMutation10570() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_7_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_1, 0);
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyList_add9615__5 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testEmptyList_add9615__5;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_0, 0);
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_3265 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3265);
            // StatementAdderMethod cloned existing statement
            vc_3265.testFoo();
            // MethodAssertGenerator build local variable
            Object o_20_0 = (parsedFoos.size()) == (foos.size());
            int i = 2;
            for (io.protostuff.Foo f : parsedFoos) {
                // MethodCallAdder
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            }
            org.junit.Assert.fail("testEmptyList_add9615_cf9811 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

