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
    public void testBar_add2_add173_add361() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        // AssertGenerator replace invocation
        boolean o_testBar_add2_add173__3 = // MethodCallAdder
bars.add(io.protostuff.SerializableObjects.bar);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testBar_add2_add173__3);
        bars.add(io.protostuff.SerializableObjects.bar);
        // AssertGenerator replace invocation
        boolean o_testBar_add2__4 = // MethodCallAdder
bars.add(io.protostuff.SerializableObjects.negativeBar);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testBar_add2__4);
        bars.add(io.protostuff.SerializableObjects.negativeBar);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testBar_add2_add173_add361__15 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testBar_add2_add173_add361__15, 295);
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
    public void testEmptyBar_add877() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        bars.add(new io.protostuff.Bar());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBar_add877__9 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBar_add877__9, 3);
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
    public void testEmptyBar_add875_add963() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        // AssertGenerator replace invocation
        boolean o_testEmptyBar_add875__3 = // MethodCallAdder
bars.add(new io.protostuff.Bar());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyBar_add875__3);
        bars.add(new io.protostuff.Bar());
        bars.add(new io.protostuff.Bar());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBar_add875_add963__13 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBar_add875_add963__13, 4);
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
    public void testEmptyBar2_add1750() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBar2_add1750__7 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBar2_add1750__7, 2);
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
    public void testEmptyBarInner_add2279() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(bar);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBarInner_add2279__10 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBarInner_add2279__10, 4);
        writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFoo */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo_add2816() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        foos.add(new io.protostuff.Foo());
        foos.add(new io.protostuff.Foo());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFoo_add2816__9 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFoo_add2816__9, 3);
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
    public void testEmptyFoo_add2814_add2901_add3167() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        // AssertGenerator replace invocation
        boolean o_testEmptyFoo_add2814__3 = // MethodCallAdder
foos.add(new io.protostuff.Foo());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFoo_add2814__3);
        foos.add(new io.protostuff.Foo());
        // AssertGenerator replace invocation
        boolean o_testEmptyFoo_add2814_add2901__9 = // MethodCallAdder
foos.add(new io.protostuff.Foo());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFoo_add2814_add2901__9);
        foos.add(new io.protostuff.Foo());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFoo_add2814_add2901_add3167__17 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFoo_add2814_add2901_add3167__17, 5);
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
    public void testEmptyFoo2_add3603() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        foos.add(new io.protostuff.Foo());
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFoo2_add3603__7 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFoo2_add3603__7, 2);
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
    public void testEmptyFooInner_add4133() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        bars.add(new io.protostuff.Bar());
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        foos.add(foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFooInner_add4133__13 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner_add4133__13, 4);
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
    public void testEmptyFooInner_add4130_add4220() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        // AssertGenerator replace invocation
        boolean o_testEmptyFooInner_add4130__3 = // MethodCallAdder
bars.add(new io.protostuff.Bar());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFooInner_add4130__3);
        bars.add(new io.protostuff.Bar());
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        foos.add(foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFooInner_add4130_add4220__17 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner_add4130_add4220__17, 6);
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
    public void testEmptyFooInner2_add4933() throws java.lang.Exception {
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
        int o_testEmptyFooInner2_add4933__16 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner2_add4933__16, 6);
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
    public void testEmptyFooInner2_add4930_add5021_add5294() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        // AssertGenerator replace invocation
        boolean o_testEmptyFooInner2_add4930__7 = // MethodCallAdder
bars.add(bar);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFooInner2_add4930__7);
        bars.add(bar);
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        foos.add(foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFooInner2_add4930_add5021__20 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner2_add4930_add5021__20, 10);
        // AssertGenerator replace invocation
        int o_testEmptyFooInner2_add4930_add5021_add5294__24 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner2_add4930_add5021_add5294__24, 10);
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
    public void testEmptyList_add5651() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyList_add5651__5 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyList_add5651__5, 0);
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
    public void testEmptyList_literalMutation5653_add5820_add6320() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyList_literalMutation5653_add5820_add6320__5 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyList_literalMutation5653_add5820_add6320__5, 0);
        writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 1;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(i, 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(i, 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(i, 1);
        for (io.protostuff.Foo f : parsedFoos) {
            // MethodCallAdder
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testFoo */
    @org.junit.Test(timeout = 10000)
    public void testFoo_add6490() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        foos.add(io.protostuff.SerializableObjects.foo);
        foos.add(io.protostuff.SerializableObjects.foo);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testFoo_add6490__7 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFoo_add6490__7, 509);
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
    public void testFoo_add6488_add6576_add7114_failAssert21() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            // AssertGenerator replace invocation
            boolean o_testFoo_add6488__3 = // MethodCallAdder
foos.add(io.protostuff.SerializableObjects.foo);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testFoo_add6488__3);
            foos.add(io.protostuff.SerializableObjects.foo);
            foos.add(io.protostuff.SerializableObjects.foo);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testFoo_add6488_add6576__11 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testFoo_add6488_add6576__11, 763);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // MethodAssertGenerator build local variable
            Object o_24_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos) {
                // MethodAssertGenerator build local variable
                Object o_31_0 = foos.get((i++));
                // MethodAssertGenerator build local variable
                Object o_35_0 = foos.get((i++));
            }
            org.junit.Assert.fail("testFoo_add6488_add6576_add7114 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }
}

