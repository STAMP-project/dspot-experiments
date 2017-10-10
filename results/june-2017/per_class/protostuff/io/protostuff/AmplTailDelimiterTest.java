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

    public void testFoo() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        // AssertGenerator replace invocation
        boolean o_testFoo__3 = foos.add(io.protostuff.SerializableObjects.foo);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testFoo__3);
        // AssertGenerator replace invocation
        boolean o_testFoo__4 = foos.add(io.protostuff.SerializableObjects.foo);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testFoo__4);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testFoo__7 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFoo__7, 509);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    public void testEmptyBar2() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        // AssertGenerator replace invocation
        boolean o_testEmptyBar2__3 = bars.add(new io.protostuff.Bar());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyBar2__3);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBar2__7 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBar2__7, 2);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
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

    /* amplification of io.protostuff.TailDelimiterTest#testBar */
    @org.junit.Test(timeout = 10000)
    public void testBar_add3() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(io.protostuff.SerializableObjects.bar);
        bars.add(io.protostuff.SerializableObjects.negativeBar);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testBar_add3__7 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testBar_add3__7, 148);
        writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testBar */
    @org.junit.Test(timeout = 10000)
    public void testBar_cf67_failAssert9_add343() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(io.protostuff.SerializableObjects.bar);
            bars.add(io.protostuff.SerializableObjects.negativeBar);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testBar_cf67_failAssert9_add343__9 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testBar_cf67_failAssert9_add343__9, 148);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_20 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_20);
            // StatementAdderMethod cloned existing statement
            vc_20.testFooEmpty();
            // MethodAssertGenerator build local variable
            Object o_20_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testBar_cf67 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBar */
    @org.junit.Test(timeout = 10000)
    public void testEmptyBar_add5544() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        bars.add(new io.protostuff.Bar());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBar_add5544__9 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBar_add5544__9, 3);
        writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBar */
    @org.junit.Test(timeout = 10000)
    public void testEmptyBar_cf5610_failAssert10_add5909() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(new io.protostuff.Bar());
            bars.add(new io.protostuff.Bar());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyBar_cf5610_failAssert10_add5909__11 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyBar_cf5610_failAssert10_add5909__11, 3);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_1294 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1294);
            // StatementAdderMethod cloned existing statement
            vc_1294.testFooTooLarge();
            // MethodAssertGenerator build local variable
            Object o_22_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testEmptyBar_cf5610 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBar2 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyBar2_add10074() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBar2_add10074__7 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBar2_add10074__7, 2);
        writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBar2 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyBar2_cf10132_failAssert6_add10324() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(new io.protostuff.Bar());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyBar2_cf10132_failAssert6_add10324__9 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyBar2_cf10132_failAssert6_add10324__9, 2);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_2318 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2318);
            // StatementAdderMethod cloned existing statement
            vc_2318.testBarTooLarge3();
            // MethodAssertGenerator build local variable
            Object o_20_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testEmptyBar2_cf10132 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBarInner */
    @org.junit.Test(timeout = 10000)
    public void testEmptyBarInner_add15567() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(bar);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBarInner_add15567__10 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBarInner_add15567__10, 4);
        writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBarInner */
    @org.junit.Test(timeout = 10000)
    public void testEmptyBarInner_cf15625_failAssert6_add15826() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.Bar bar = new io.protostuff.Bar();
            bar.setSomeBaz(new io.protostuff.Baz());
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(bar);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyBarInner_cf15625_failAssert6_add15826__12 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyBarInner_cf15625_failAssert6_add15826__12, 4);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_3590 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3590);
            // StatementAdderMethod cloned existing statement
            vc_3590.testBarTooLarge3();
            // MethodAssertGenerator build local variable
            Object o_23_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testEmptyBarInner_cf15625 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFoo */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo_add21626() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        foos.add(new io.protostuff.Foo());
        foos.add(new io.protostuff.Foo());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFoo_add21626__9 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFoo_add21626__9, 3);
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFoo */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo_cf21684_failAssert6_add21955() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            foos.add(new io.protostuff.Foo());
            foos.add(new io.protostuff.Foo());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyFoo_cf21684_failAssert6_add21955__11 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyFoo_cf21684_failAssert6_add21955__11, 3);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_4982 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4982);
            // StatementAdderMethod cloned existing statement
            vc_4982.testBarTooLarge3();
            // MethodAssertGenerator build local variable
            Object o_22_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFoo_cf21684 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFoo2 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo2_add26050() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        foos.add(new io.protostuff.Foo());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFoo2_add26050__7 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFoo2_add26050__7, 2);
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFoo2 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo2_cf26110_failAssert7_add26308() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            foos.add(new io.protostuff.Foo());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyFoo2_cf26110_failAssert7_add26308__9 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyFoo2_cf26110_failAssert7_add26308__9, 2);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_5992 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5992);
            // StatementAdderMethod cloned existing statement
            vc_5992.testBaz();
            // MethodAssertGenerator build local variable
            Object o_20_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFoo2_cf26110 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner_add31640() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        foos.add(foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFooInner_add31640__13 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner_add31640__13, 4);
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner_cf31700_failAssert7_add31989() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(new io.protostuff.Bar());
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            io.protostuff.Foo foo = new io.protostuff.Foo();
            foo.setSomeBar(bars);
            foos.add(foo);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyFooInner_cf31700_failAssert7_add31989__15 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyFooInner_cf31700_failAssert7_add31989__15, 4);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_7288 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7288);
            // StatementAdderMethod cloned existing statement
            vc_7288.testBaz();
            // MethodAssertGenerator build local variable
            Object o_26_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFooInner_cf31700 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner2 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner2_add36251() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        bars.add(bar);
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        foos.add(foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFooInner2_add36251__16 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner2_add36251__16, 6);
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner2 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner2_cf36313_failAssert8_add36622() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            io.protostuff.Bar bar = new io.protostuff.Bar();
            bar.setSomeBaz(new io.protostuff.Baz());
            bars.add(bar);
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            io.protostuff.Foo foo = new io.protostuff.Foo();
            foo.setSomeBar(bars);
            foos.add(foo);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyFooInner2_cf36313_failAssert8_add36622__18 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyFooInner2_cf36313_failAssert8_add36622__18, 6);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_8322 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8322);
            // StatementAdderMethod cloned existing statement
            vc_8322.testFoo();
            // MethodAssertGenerator build local variable
            Object o_29_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFooInner2_cf36313 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList */
    @org.junit.Test(timeout = 10000)
    public void testEmptyList_add41324() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyList_add41324__5 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyList_add41324__5, 0);
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList */
    @org.junit.Test(timeout = 10000)
    public void testEmptyList_cf41382_failAssert2_add41676() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyList_cf41382_failAssert2_add41676__7 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyList_cf41382_failAssert2_add41676__7, 0);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_9446 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_9446);
            // StatementAdderMethod cloned existing statement
            vc_9446.testBarTooLarge3();
            // MethodAssertGenerator build local variable
            Object o_18_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyList_cf41382 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testFoo */
    @org.junit.Test(timeout = 10000)
    public void testFoo_add44816() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        foos.add(io.protostuff.SerializableObjects.foo);
        foos.add(io.protostuff.SerializableObjects.foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testFoo_add44816__7 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFoo_add44816__7, 509);
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testFoo */
    @org.junit.Test(timeout = 10000)
    public void testFoo_cf44872_failAssert5_add45136() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            foos.add(io.protostuff.SerializableObjects.foo);
            foos.add(io.protostuff.SerializableObjects.foo);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testFoo_cf44872_failAssert5_add45136__9 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testFoo_cf44872_failAssert5_add45136__9, 509);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_10284 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_10284);
            // StatementAdderMethod cloned existing statement
            vc_10284.testBarTooLarge2();
            // MethodAssertGenerator build local variable
            Object o_20_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testFoo_cf44872 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

