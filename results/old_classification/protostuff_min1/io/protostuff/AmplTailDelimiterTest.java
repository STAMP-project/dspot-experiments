

package io.protostuff;


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
        boolean o_testEmptyBar2__3 = bars.add(new io.protostuff.Bar());
        org.junit.Assert.assertTrue(o_testEmptyBar2__3);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testEmptyBar2__7 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
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
        int o_testEmptyList__5 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyList__5, 0);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    public void testBar_literalMutation7() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        boolean o_testBar_literalMutation7__3 = bars.add(io.protostuff.SerializableObjects.bar);
        org.junit.Assert.assertTrue(o_testBar_literalMutation7__3);
        boolean o_testBar_literalMutation7__4 = bars.add(io.protostuff.SerializableObjects.negativeBar);
        org.junit.Assert.assertTrue(o_testBar_literalMutation7__4);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testBar_literalMutation7__7 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        org.junit.Assert.assertEquals(o_testBar_literalMutation7__7, 148);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testBar_cf61_failAssert6_add374() throws java.lang.Exception {
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(io.protostuff.SerializableObjects.bar);
            bars.add(io.protostuff.SerializableObjects.negativeBar);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testBar_cf61_failAssert6_add374__9 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            org.junit.Assert.assertEquals(o_testBar_cf61_failAssert6_add374__9, 148);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            io.protostuff.DelimiterTest vc_14 = ((io.protostuff.DelimiterTest) (null));
            vc_14.testBarTooLarge3();
            java.lang.Object o_20_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testBar_cf61 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyBar_add1260() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        boolean o_testEmptyBar_add1260__3 = bars.add(new io.protostuff.Bar());
        org.junit.Assert.assertTrue(o_testEmptyBar_add1260__3);
        boolean o_testEmptyBar_add1260__5 = bars.add(new io.protostuff.Bar());
        org.junit.Assert.assertTrue(o_testEmptyBar_add1260__5);
        boolean o_testEmptyBar_add1260__8 = bars.add(new io.protostuff.Bar());
        org.junit.Assert.assertTrue(o_testEmptyBar_add1260__8);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testEmptyBar_add1260__12 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyBar_add1260__12, 4);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyBar_cf1319_failAssert6_add1648() throws java.lang.Exception {
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(new io.protostuff.Bar());
            bars.add(new io.protostuff.Bar());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testEmptyBar_cf1319_failAssert6_add1648__11 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            org.junit.Assert.assertEquals(o_testEmptyBar_cf1319_failAssert6_add1648__11, 3);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            io.protostuff.DelimiterTest vc_422 = ((io.protostuff.DelimiterTest) (null));
            vc_422.testBarTooLarge3();
            java.lang.Object o_22_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testEmptyBar_cf1319 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    public void testEmptyBar2_literalMutation2361() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        boolean o_testEmptyBar2_literalMutation2361__3 = bars.add(new io.protostuff.Bar());
        org.junit.Assert.assertTrue(o_testEmptyBar2_literalMutation2361__3);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testEmptyBar2_literalMutation2361__7 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyBar2_literalMutation2361__7, 2);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyBar2_cf2411_failAssert4_add2654() throws java.lang.Exception {
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(new io.protostuff.Bar());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testEmptyBar2_cf2411_failAssert4_add2654__9 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            org.junit.Assert.assertEquals(o_testEmptyBar2_cf2411_failAssert4_add2654__9, 2);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            io.protostuff.DelimiterTest vc_754 = ((io.protostuff.DelimiterTest) (null));
            vc_754.testBar();
            java.lang.Object o_20_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testEmptyBar2_cf2411 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    public void testEmptyBarInner_literalMutation3413() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        boolean o_testEmptyBarInner_literalMutation3413__7 = bars.add(bar);
        org.junit.Assert.assertTrue(o_testEmptyBarInner_literalMutation3413__7);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testEmptyBarInner_literalMutation3413__10 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyBarInner_literalMutation3413__10, 4);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyBarInner_cf3475_failAssert10_add3837() throws java.lang.Exception {
        try {
            io.protostuff.Bar bar = new io.protostuff.Bar();
            bar.setSomeBaz(new io.protostuff.Baz());
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(bar);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testEmptyBarInner_cf3475_failAssert10_add3837__12 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            org.junit.Assert.assertEquals(o_testEmptyBarInner_cf3475_failAssert10_add3837__12, 4);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            io.protostuff.DelimiterTest vc_1078 = ((io.protostuff.DelimiterTest) (null));
            vc_1078.testFooTooLarge();
            java.lang.Object o_23_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testEmptyBarInner_cf3475 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo_add4410() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        boolean o_testEmptyFoo_add4410__3 = foos.add(new io.protostuff.Foo());
        org.junit.Assert.assertTrue(o_testEmptyFoo_add4410__3);
        boolean o_testEmptyFoo_add4410__5 = foos.add(new io.protostuff.Foo());
        org.junit.Assert.assertTrue(o_testEmptyFoo_add4410__5);
        boolean o_testEmptyFoo_add4410__8 = foos.add(new io.protostuff.Foo());
        org.junit.Assert.assertTrue(o_testEmptyFoo_add4410__8);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testEmptyFoo_add4410__12 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyFoo_add4410__12, 4);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo_cf4465_failAssert4_add4780() throws java.lang.Exception {
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            foos.add(new io.protostuff.Foo());
            foos.add(new io.protostuff.Foo());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testEmptyFoo_cf4465_failAssert4_add4780__11 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            org.junit.Assert.assertEquals(o_testEmptyFoo_cf4465_failAssert4_add4780__11, 3);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            io.protostuff.DelimiterTest vc_1354 = ((io.protostuff.DelimiterTest) (null));
            vc_1354.testBar();
            java.lang.Object o_22_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFoo_cf4465 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo2_add5685() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        boolean o_testEmptyFoo2_add5685__3 = foos.add(new io.protostuff.Foo());
        org.junit.Assert.assertTrue(o_testEmptyFoo2_add5685__3);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testEmptyFoo2_add5685__7 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyFoo2_add5685__7, 2);
        int o_testEmptyFoo2_add5685__10 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyFoo2_add5685__10, 2);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo2_cf5741_failAssert5_add5990() throws java.lang.Exception {
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            foos.add(new io.protostuff.Foo());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testEmptyFoo2_cf5741_failAssert5_add5990__9 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            org.junit.Assert.assertEquals(o_testEmptyFoo2_cf5741_failAssert5_add5990__9, 2);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            io.protostuff.DelimiterTest vc_1764 = ((io.protostuff.DelimiterTest) (null));
            vc_1764.testBarTooLarge2();
            java.lang.Object o_20_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFoo2_cf5741 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    public void testEmptyFooInner_literalMutation6793() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        boolean o_testEmptyFooInner_literalMutation6793__3 = bars.add(new io.protostuff.Bar());
        org.junit.Assert.assertTrue(o_testEmptyFooInner_literalMutation6793__3);
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        boolean o_testEmptyFooInner_literalMutation6793__10 = foos.add(foo);
        org.junit.Assert.assertTrue(o_testEmptyFooInner_literalMutation6793__10);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testEmptyFooInner_literalMutation6793__13 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyFooInner_literalMutation6793__13, 4);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner_cf6851_failAssert8_add7276() throws java.lang.Exception {
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(new io.protostuff.Bar());
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            io.protostuff.Foo foo = new io.protostuff.Foo();
            foo.setSomeBar(bars);
            foos.add(foo);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testEmptyFooInner_cf6851_failAssert8_add7276__15 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            org.junit.Assert.assertEquals(o_testEmptyFooInner_cf6851_failAssert8_add7276__15, 4);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            io.protostuff.DelimiterTest vc_2106 = ((io.protostuff.DelimiterTest) (null));
            vc_2106.testFoo();
            java.lang.Object o_26_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFooInner_cf6851 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner2_add8288() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        boolean o_testEmptyFooInner2_add8288__7 = bars.add(bar);
        org.junit.Assert.assertTrue(o_testEmptyFooInner2_add8288__7);
        boolean o_testEmptyFooInner2_add8288__9 = bars.add(bar);
        org.junit.Assert.assertTrue(o_testEmptyFooInner2_add8288__9);
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        boolean o_testEmptyFooInner2_add8288__15 = foos.add(foo);
        org.junit.Assert.assertTrue(o_testEmptyFooInner2_add8288__15);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testEmptyFooInner2_add8288__18 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyFooInner2_add8288__18, 10);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner2_cf8355_failAssert9_add8873() throws java.lang.Exception {
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
            int o_testEmptyFooInner2_cf8355_failAssert9_add8873__18 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            org.junit.Assert.assertEquals(o_testEmptyFooInner2_cf8355_failAssert9_add8873__18, 6);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            io.protostuff.DelimiterTest vc_2564 = ((io.protostuff.DelimiterTest) (null));
            vc_2564.testFooEmpty();
            java.lang.Object o_29_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFooInner2_cf8355 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    public void testEmptyList_literalMutation9762() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testEmptyList_literalMutation9762__5 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        org.junit.Assert.assertEquals(o_testEmptyList_literalMutation9762__5, 0);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = -1;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyList_cf9817_failAssert2_add10242() throws java.lang.Exception {
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testEmptyList_cf9817_failAssert2_add10242__7 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            org.junit.Assert.assertEquals(o_testEmptyList_cf9817_failAssert2_add10242__7, 0);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            io.protostuff.DelimiterTest vc_2966 = ((io.protostuff.DelimiterTest) (null));
            vc_2966.testBarTooLarge3();
            java.lang.Object o_18_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyList_cf9817 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testEmptyList_literalMutation9763_cf10155_failAssert15_add10507() throws java.lang.Exception {
        try {
            java.lang.Object o_7_1 = 0;
            org.junit.Assert.assertEquals(o_7_1, 0);
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testEmptyList_literalMutation9763__5 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            java.lang.Object o_7_0 = o_testEmptyList_literalMutation9763__5;
            org.junit.Assert.assertEquals(o_7_0, 0);
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            io.protostuff.DelimiterTest vc_3090 = ((io.protostuff.DelimiterTest) (null));
            org.junit.Assert.assertNull(vc_3090);
            vc_3090.testFoo();
            vc_3090.testFoo();
            java.lang.Object o_20_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyList_literalMutation9763_cf10155 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    public void testFoo_literalMutation10536() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        boolean o_testFoo_literalMutation10536__3 = foos.add(io.protostuff.SerializableObjects.foo);
        org.junit.Assert.assertTrue(o_testFoo_literalMutation10536__3);
        boolean o_testFoo_literalMutation10536__4 = foos.add(io.protostuff.SerializableObjects.foo);
        org.junit.Assert.assertTrue(o_testFoo_literalMutation10536__4);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int o_testFoo_literalMutation10536__7 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        org.junit.Assert.assertEquals(o_testFoo_literalMutation10536__7, 509);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    @org.junit.Test(timeout = 10000)
    public void testFoo_cf10598_failAssert10_add10955() throws java.lang.Exception {
        try {
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            foos.add(io.protostuff.SerializableObjects.foo);
            foos.add(io.protostuff.SerializableObjects.foo);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int o_testFoo_cf10598_failAssert10_add10955__9 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            org.junit.Assert.assertEquals(o_testFoo_cf10598_failAssert10_add10955__9, 509);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            io.protostuff.DelimiterTest vc_3190 = ((io.protostuff.DelimiterTest) (null));
            vc_3190.testFooTooLarge();
            java.lang.Object o_20_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testFoo_cf10598 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testBar */
    /* amplification of io.protostuff.TailDelimiterTest#testBar_cf63 */
    @org.junit.Test(timeout = 10000)
    public void testBar_cf63_failAssert7_add383() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(io.protostuff.SerializableObjects.bar);
            bars.add(io.protostuff.SerializableObjects.negativeBar);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testBar_cf63_failAssert7_add383__9 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testBar_cf63_failAssert7_add383__9, 148);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_16 = (io.protostuff.DelimiterTest)null;
            // StatementAdderMethod cloned existing statement
            vc_16.testBaz();
            // MethodAssertGenerator build local variable
            Object o_20_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testBar_cf63 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBarInner */
    @org.junit.Test(timeout = 10000)
    public void testEmptyBarInner_add3408() throws java.lang.Exception {
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        // AssertGenerator replace invocation
        boolean o_testEmptyBarInner_add3408__7 = // MethodCallAdder
bars.add(bar);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyBarInner_add3408__7);
        // AssertGenerator replace invocation
        boolean o_testEmptyBarInner_add3408__9 = bars.add(bar);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyBarInner_add3408__9);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBarInner_add3408__12 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBarInner_add3408__12, 7);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBar */
    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBar_cf1321 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyBar_cf1321_failAssert7_add1657() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
            bars.add(new io.protostuff.Bar());
            bars.add(new io.protostuff.Bar());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyBar_cf1321_failAssert7_add1657__11 = // MethodCallAdder
writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyBar_cf1321_failAssert7_add1657__11, 3);
            writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_424 = (io.protostuff.DelimiterTest)null;
            // StatementAdderMethod cloned existing statement
            vc_424.testBaz();
            // MethodAssertGenerator build local variable
            Object o_22_0 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (io.protostuff.Bar b : parsedBars)
                io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
            
            org.junit.Assert.fail("testEmptyBar_cf1321 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyBar */
    public void testEmptyBar_literalMutation1265() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        // AssertGenerator replace invocation
        boolean o_testEmptyBar_literalMutation1265__3 = bars.add(new io.protostuff.Bar());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyBar_literalMutation1265__3);
        // AssertGenerator replace invocation
        boolean o_testEmptyBar_literalMutation1265__5 = bars.add(new io.protostuff.Bar());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyBar_literalMutation1265__5);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyBar_literalMutation1265__9 = writeListTo(out, bars, io.protostuff.SerializableObjects.bar.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyBar_literalMutation1265__9, 3);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Bar> parsedBars = parseListFrom(in, io.protostuff.SerializableObjects.bar.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedBars.size()) == (bars.size())));
        int i = 0;
        for (io.protostuff.Bar b : parsedBars)
            io.protostuff.SerializableObjects.assertEquals(bars.get((i++)), b);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner2 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner2_add8291() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        io.protostuff.Bar bar = new io.protostuff.Bar();
        bar.setSomeBaz(new io.protostuff.Baz());
        // AssertGenerator replace invocation
        boolean o_testEmptyFooInner2_add8291__7 = bars.add(bar);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFooInner2_add8291__7);
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        // AssertGenerator replace invocation
        boolean o_testEmptyFooInner2_add8291__13 = foos.add(foo);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFooInner2_add8291__13);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFooInner2_add8291__16 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner2_add8291__16, 6);
        // AssertGenerator replace invocation
        int o_testEmptyFooInner2_add8291__19 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner2_add8291__19, 6);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner2 */
    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner2_cf8349 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner2_cf8349_failAssert6_add8840() throws java.lang.Exception {
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
            int o_testEmptyFooInner2_cf8349_failAssert6_add8840__18 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyFooInner2_cf8349_failAssert6_add8840__18, 6);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_2558 = (io.protostuff.DelimiterTest)null;
            // StatementAdderMethod cloned existing statement
            vc_2558.testBarTooLarge3();
            // MethodAssertGenerator build local variable
            Object o_29_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFooInner2_cf8349 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner_add6788() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Bar> bars = new java.util.ArrayList<io.protostuff.Bar>();
        // AssertGenerator replace invocation
        boolean o_testEmptyFooInner_add6788__3 = bars.add(new io.protostuff.Bar());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFooInner_add6788__3);
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        io.protostuff.Foo foo = new io.protostuff.Foo();
        foo.setSomeBar(bars);
        // AssertGenerator replace invocation
        boolean o_testEmptyFooInner_add6788__10 = // MethodCallAdder
foos.add(foo);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFooInner_add6788__10);
        // AssertGenerator replace invocation
        boolean o_testEmptyFooInner_add6788__12 = foos.add(foo);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFooInner_add6788__12);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFooInner_add6788__15 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFooInner_add6788__15, 7);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner */
    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFooInner_cf6847 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFooInner_cf6847_failAssert6_add7256() throws java.lang.Exception {
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
            int o_testEmptyFooInner_cf6847_failAssert6_add7256__15 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testEmptyFooInner_cf6847_failAssert6_add7256__15, 4);
            writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_2102 = (io.protostuff.DelimiterTest)null;
            // StatementAdderMethod cloned existing statement
            vc_2102.testBarTooLarge3();
            // MethodAssertGenerator build local variable
            Object o_26_0 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyFooInner_cf6847 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyFoo */
    @org.junit.Test(timeout = 10000)
    public void testEmptyFoo_add4411() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        // AssertGenerator replace invocation
        boolean o_testEmptyFoo_add4411__3 = foos.add(new io.protostuff.Foo());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFoo_add4411__3);
        // AssertGenerator replace invocation
        boolean o_testEmptyFoo_add4411__5 = foos.add(new io.protostuff.Foo());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEmptyFoo_add4411__5);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyFoo_add4411__9 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFoo_add4411__9, 3);
        // AssertGenerator replace invocation
        int o_testEmptyFoo_add4411__12 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyFoo_add4411__12, 3);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList */
    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList_literalMutation9762 */
    @org.junit.Test(timeout = 10000)
    public void testEmptyList_literalMutation9762_cf10088_failAssert9_add10469() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_7_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_1, 0);
            java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            // AssertGenerator replace invocation
            int o_testEmptyList_literalMutation9762__5 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testEmptyList_literalMutation9762__5;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_0, 0);
            byte[] data = out.toByteArray();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
            java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
            // StatementAdderOnAssert create null value
            io.protostuff.DelimiterTest vc_3066 = (io.protostuff.DelimiterTest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3066);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_3066.testFoo();
            // StatementAdderMethod cloned existing statement
            vc_3066.testFoo();
            // MethodAssertGenerator build local variable
            Object o_20_0 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (io.protostuff.Foo f : parsedFoos)
                io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
            
            org.junit.Assert.fail("testEmptyList_literalMutation9762_cf10088 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.TailDelimiterTest#testEmptyList */
    public void testEmptyList_literalMutation9763() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testEmptyList_literalMutation9763__5 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testEmptyList_literalMutation9763__5, 0);
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
    public void testFoo_add10532() throws java.lang.Exception {
        java.util.ArrayList<io.protostuff.Foo> foos = new java.util.ArrayList<io.protostuff.Foo>();
        // AssertGenerator replace invocation
        boolean o_testFoo_add10532__3 = foos.add(io.protostuff.SerializableObjects.foo);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testFoo_add10532__3);
        // AssertGenerator replace invocation
        boolean o_testFoo_add10532__4 = foos.add(io.protostuff.SerializableObjects.foo);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testFoo_add10532__4);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        // AssertGenerator replace invocation
        int o_testFoo_add10532__7 = // MethodCallAdder
writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFoo_add10532__7, 509);
        // AssertGenerator replace invocation
        int o_testFoo_add10532__10 = writeListTo(out, foos, io.protostuff.SerializableObjects.foo.cachedSchema());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testFoo_add10532__10, 509);
        byte[] data = out.toByteArray();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.util.List<io.protostuff.Foo> parsedFoos = parseListFrom(in, io.protostuff.SerializableObjects.foo.cachedSchema());
        junit.framework.TestCase.assertTrue(((parsedFoos.size()) == (foos.size())));
        int i = 0;
        for (io.protostuff.Foo f : parsedFoos)
            io.protostuff.SerializableObjects.assertEquals(foos.get((i++)), f);
        
    }
}

