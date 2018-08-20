package io.protostuff;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AmplTailDelimiterTest extends AbstractTest {
    public <T> int writeListTo(OutputStream out, List<T> messages, Schema<T> schema) throws IOException {
        return ProtostuffIOUtil.writeListTo(out, messages, schema, new LinkedBuffer(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    public <T> List<T> parseListFrom(InputStream in, Schema<T> schema) throws IOException {
        return ProtostuffIOUtil.parseListFrom(in, schema);
    }

    @Test(timeout = 10000)
    public void testBar_mg24() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testBar_mg24__3 = bars.add(SerializableObjects.bar);
        Assert.assertTrue(o_testBar_mg24__3);
        boolean o_testBar_mg24__4 = bars.add(SerializableObjects.negativeBar);
        Assert.assertTrue(o_testBar_mg24__4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testBar_mg24__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(148, ((int) (o_testBar_mg24__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_10 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            Bar __DSPOT_message_3 = new Bar();
            bars.get((i++));
            b.isInitialized(__DSPOT_message_3);
        }
        Assert.assertTrue(o_testBar_mg24__3);
        Assert.assertTrue(o_testBar_mg24__4);
        Assert.assertEquals(148, ((int) (o_testBar_mg24__7)));
    }

    @Test(timeout = 10000)
    public void testBar_add11() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testBar_add11__3 = bars.add(SerializableObjects.bar);
        Assert.assertTrue(o_testBar_add11__3);
        boolean o_testBar_add11__4 = bars.add(SerializableObjects.negativeBar);
        Assert.assertTrue(o_testBar_add11__4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testBar_add11__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(148, ((int) (o_testBar_add11__7)));
        byte[] o_testBar_add11__9 = out.toByteArray();
        byte[] array_1482001999 = new byte[]{2, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 7, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 7};
        	byte[] array_1636306375 = (byte[])o_testBar_add11__9;
        	for(int ii = 0; ii <array_1482001999.length; ii++) {
        		org.junit.Assert.assertEquals(array_1482001999[ii], array_1636306375[ii]);
        	};
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_37 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        Assert.assertTrue(o_testBar_add11__3);
        Assert.assertTrue(o_testBar_add11__4);
        Assert.assertEquals(148, ((int) (o_testBar_add11__7)));
        byte[] array_1913680303 = new byte[]{2, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 7, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 7};
        	byte[] array_792779551 = (byte[])o_testBar_add11__9;
        	for(int ii = 0; ii <array_1913680303.length; ii++) {
        		org.junit.Assert.assertEquals(array_1913680303[ii], array_792779551[ii]);
        	};
    }

    @Test(timeout = 10000)
    public void testBar_remove18() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testBar_remove18__3 = bars.add(SerializableObjects.bar);
        Assert.assertTrue(o_testBar_remove18__3);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testBar_remove18__6 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(53, ((int) (o_testBar_remove18__6)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_27 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        Assert.assertTrue(o_testBar_remove18__3);
        Assert.assertEquals(53, ((int) (o_testBar_remove18__6)));
    }

    @Test(timeout = 10000)
    public void testBarlitNum1_failAssert2_rv2328() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testBarlitNum1_failAssert2_rv2328__5 = bars.add(SerializableObjects.bar);
            Assert.assertTrue(o_testBarlitNum1_failAssert2_rv2328__5);
            boolean o_testBarlitNum1_failAssert2_rv2328__6 = bars.add(SerializableObjects.negativeBar);
            Assert.assertTrue(o_testBarlitNum1_failAssert2_rv2328__6);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testBarlitNum1_failAssert2_rv2328__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(148, ((int) (o_testBarlitNum1_failAssert2_rv2328__9)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_32 = (parsedBars.size()) == (bars.size());
            int i = 1;
            for (Bar b : parsedBars) {
                Bar __DSPOT_invoc_25 = bars.get((i++));
                __DSPOT_invoc_25.getSomeDouble();
            }
            org.junit.Assert.fail("testBarlitNum1 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBar() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBar__3 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyBar__3);
        boolean o_testEmptyBar__5 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyBar__5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBar__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyBar__9)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_1 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        Assert.assertTrue(o_testEmptyBar__3);
        Assert.assertTrue(o_testEmptyBar__5);
        Assert.assertEquals(3, ((int) (o_testEmptyBar__9)));
    }

    @Test(timeout = 10000)
    public void testEmptyBar_add8528() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBar_add8528__3 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyBar_add8528__3);
        boolean o_testEmptyBar_add8528__5 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyBar_add8528__5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBar_add8528__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyBar_add8528__9)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> o_testEmptyBar_add8528__15 = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        Assert.assertFalse(o_testEmptyBar_add8528__15.isEmpty());
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_75 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        Assert.assertTrue(o_testEmptyBar_add8528__3);
        Assert.assertTrue(o_testEmptyBar_add8528__5);
        Assert.assertEquals(3, ((int) (o_testEmptyBar_add8528__9)));
        Assert.assertFalse(o_testEmptyBar_add8528__15.isEmpty());
    }

    @Test(timeout = 10000)
    public void testEmptyBarnull8554_failAssert91_add9252() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBarnull8554_failAssert91_add9252__5 = bars.add(new Bar());
            Assert.assertTrue(o_testEmptyBarnull8554_failAssert91_add9252__5);
            boolean o_testEmptyBarnull8554_failAssert91_add9252__7 = bars.add(new Bar());
            Assert.assertTrue(o_testEmptyBarnull8554_failAssert91_add9252__7);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBarnull8554_failAssert91_add9252__11 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(3, ((int) (o_testEmptyBarnull8554_failAssert91_add9252__11)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            parseListFrom(null, SerializableObjects.bar.cachedSchema());
            List<Bar> parsedBars = parseListFrom(null, SerializableObjects.bar.cachedSchema());
            boolean boolean_83 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testEmptyBarnull8554 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBar_add8532_failAssert90null11150_failAssert114litNum12072() throws Exception {
        try {
            try {
                ArrayList<Bar> bars = new ArrayList<Bar>();
                boolean o_testEmptyBar_add8532_failAssert90null11150_failAssert114litNum12072__7 = bars.add(new Bar());
                Assert.assertTrue(o_testEmptyBar_add8532_failAssert90null11150_failAssert114litNum12072__7);
                boolean o_testEmptyBar_add8532_failAssert90null11150_failAssert114litNum12072__9 = bars.add(new Bar());
                Assert.assertTrue(o_testEmptyBar_add8532_failAssert90null11150_failAssert114litNum12072__9);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int o_testEmptyBar_add8532_failAssert90null11150_failAssert114litNum12072__13 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
                Assert.assertEquals(3, ((int) (o_testEmptyBar_add8532_failAssert90null11150_failAssert114litNum12072__13)));
                byte[] data = out.toByteArray();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                List<Bar> parsedBars = parseListFrom(null, SerializableObjects.bar.cachedSchema());
                boolean boolean_77 = (parsedBars.size()) == (bars.size());
                int i = 1;
                for (Bar b : parsedBars) {
                    bars.get((i++));
                    bars.get((i++));
                }
                org.junit.Assert.fail("testEmptyBar_add8532 should have thrown IndexOutOfBoundsException");
            } catch (IndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testEmptyBar_add8532_failAssert90null11150 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBar2_mg19150() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBar2_mg19150__3 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyBar2_mg19150__3);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBar2_mg19150__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyBar2_mg19150__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_88 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
            b.cachedSchema();
        }
        Assert.assertTrue(o_testEmptyBar2_mg19150__3);
        Assert.assertEquals(2, ((int) (o_testEmptyBar2_mg19150__7)));
    }

    @Test(timeout = 10000)
    public void testEmptyBar2litNum19133_failAssert160_add19757() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBar2litNum19133_failAssert160_add19757__5 = bars.add(new Bar());
            Assert.assertTrue(o_testEmptyBar2litNum19133_failAssert160_add19757__5);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBar2litNum19133_failAssert160_add19757__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(2, ((int) (o_testEmptyBar2litNum19133_failAssert160_add19757__9)));
            int o_testEmptyBar2litNum19133_failAssert160_add19757__11 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(2, ((int) (o_testEmptyBar2litNum19133_failAssert160_add19757__11)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_105 = (parsedBars.size()) == (bars.size());
            int i = 1;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testEmptyBar2litNum19133 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBar2_mg19158litNum19313_failAssert205_add23353() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBar2_mg19158__3 = bars.add(new Bar());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBar2_mg19158litNum19313_failAssert205_add23353__11 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(2, ((int) (o_testEmptyBar2_mg19158litNum19313_failAssert205_add23353__11)));
            int o_testEmptyBar2_mg19158__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_90 = (parsedBars.size()) == (bars.size());
            int i = 1;
            for (Bar b : parsedBars) {
                Baz __DSPOT_baz_5971 = new Baz(-426952978, "oRyZmy|aE/Abf&qp0%YO", 1175741927L);
                bars.get((i++));
                b.setSomeBaz(__DSPOT_baz_5971);
            }
            org.junit.Assert.fail("testEmptyBar2_mg19158litNum19313 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBarInner_mg28325() throws Exception {
        int __DSPOT_number_8785 = -2002312314;
        Bar bar = new Bar();
        Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
        Assert.assertNull(((Bar) (bar)).getSomeString());
        Assert.assertNull(((Bar) (bar)).getSomeBaz());
        Assert.assertNull(((Bar) (bar)).getSomeEnum());
        Assert.assertNull(((Bar) (bar)).getSomeBytes());
        Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
        Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
        Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
        Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
        Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
        Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
        bar.setSomeBaz(new Baz());
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBarInner_mg28325__8 = bars.add(bar);
        Assert.assertTrue(o_testEmptyBarInner_mg28325__8);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBarInner_mg28325__11 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(4, ((int) (o_testEmptyBarInner_mg28325__11)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_137 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        bar.getFieldName(__DSPOT_number_8785);
    }

    @Test(timeout = 10000)
    public void testEmptyBarInnerlitNum28306_failAssert240_rv34113() throws Exception {
        try {
            Bar bar = new Bar();
            Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
            Assert.assertNull(((Bar) (bar)).getSomeEnum());
            Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
            Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
            Assert.assertNull(((Bar) (bar)).getSomeBytes());
            Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
            Assert.assertNull(((Bar) (bar)).getSomeBaz());
            Assert.assertNull(((Bar) (bar)).getSomeString());
            Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
            Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
            Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
            bar.setSomeBaz(new Baz());
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBarInnerlitNum28306_failAssert240_rv34113__9 = bars.add(bar);
            Assert.assertTrue(o_testEmptyBarInnerlitNum28306_failAssert240_rv34113__9);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBarInnerlitNum28306_failAssert240_rv34113__12 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(4, ((int) (o_testEmptyBarInnerlitNum28306_failAssert240_rv34113__12)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_149 = (parsedBars.size()) == (bars.size());
            int i = Integer.MAX_VALUE;
            for (Bar b : parsedBars) {
                String __DSPOT_name_10562 = ":|B+w]>r193F%VG#W-}P";
                Bar __DSPOT_invoc_28 = bars.get((i++));
                __DSPOT_invoc_28.getFieldNumber(__DSPOT_name_10562);
            }
            org.junit.Assert.fail("testEmptyBarInnerlitNum28306 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBarInner_mg28326litNum28841_failAssert249_add42077() throws Exception {
        try {
            String __DSPOT_name_8786 = "+FJm&#F78#/y%WVa#U:U";
            Bar bar = new Bar();
            Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
            Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
            Assert.assertNull(((Bar) (bar)).getSomeBaz());
            Assert.assertNull(((Bar) (bar)).getSomeBytes());
            Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
            Assert.assertNull(((Bar) (bar)).getSomeEnum());
            Assert.assertNull(((Bar) (bar)).getSomeString());
            Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
            Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
            Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
            Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
            bar.setSomeBaz(new Baz());
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBarInner_mg28326__8 = bars.add(bar);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBarInner_mg28326litNum28841_failAssert249_add42077__15 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(4, ((int) (o_testEmptyBarInner_mg28326litNum28841_failAssert249_add42077__15)));
            int o_testEmptyBarInner_mg28326__11 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_172 = (parsedBars.size()) == (bars.size());
            int i = 1;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            int o_testEmptyBarInner_mg28326__29 = bar.getFieldNumber(__DSPOT_name_8786);
            org.junit.Assert.fail("testEmptyBarInner_mg28326litNum28841 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testFoo() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testFoo__3 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo__3);
        boolean o_testFoo__4 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo__4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testFoo__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(509, ((int) (o_testFoo__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_9 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertTrue(o_testFoo__3);
        Assert.assertTrue(o_testFoo__4);
        Assert.assertEquals(509, ((int) (o_testFoo__7)));
    }

    @Test(timeout = 10000)
    public void testFoo_add153831() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testFoo_add153831__3 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo_add153831__3);
        boolean o_testFoo_add153831__4 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo_add153831__4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testFoo_add153831__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(509, ((int) (o_testFoo_add153831__7)));
        byte[] o_testFoo_add153831__9 = out.toByteArray();
        byte[] array_2032028253 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
        	byte[] array_1232388386 = (byte[])o_testFoo_add153831__9;
        	for(int ii = 0; ii <array_2032028253.length; ii++) {
        		org.junit.Assert.assertEquals(array_2032028253[ii], array_1232388386[ii]);
        	};
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_467 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertTrue(o_testFoo_add153831__3);
        Assert.assertTrue(o_testFoo_add153831__4);
        Assert.assertEquals(509, ((int) (o_testFoo_add153831__7)));
        byte[] array_1517723286 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
        	byte[] array_1098029394 = (byte[])o_testFoo_add153831__9;
        	for(int ii = 0; ii <array_1517723286.length; ii++) {
        		org.junit.Assert.assertEquals(array_1517723286[ii], array_1098029394[ii]);
        	};
    }

    @Test(timeout = 10000)
    public void testFoo_mg153852() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testFoo_mg153852__3 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo_mg153852__3);
        boolean o_testFoo_mg153852__4 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo_mg153852__4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testFoo_mg153852__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(509, ((int) (o_testFoo_mg153852__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_450 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            List<Foo.EnumSample> __DSPOT_someEnum_49746 = Collections.<Foo.EnumSample>emptyList();
            foos.get((i++));
            f.setSomeEnum(__DSPOT_someEnum_49746);
        }
        Assert.assertTrue(o_testFoo_mg153852__3);
        Assert.assertTrue(o_testFoo_mg153852__4);
        Assert.assertEquals(509, ((int) (o_testFoo_mg153852__7)));
    }

    @Test(timeout = 10000)
    public void testFoo_add153836_failAssert706litNum154173() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testFoo_add153836_failAssert706litNum154173__5 = foos.add(SerializableObjects.foo);
            Assert.assertTrue(o_testFoo_add153836_failAssert706litNum154173__5);
            boolean o_testFoo_add153836_failAssert706litNum154173__6 = foos.add(SerializableObjects.foo);
            Assert.assertTrue(o_testFoo_add153836_failAssert706litNum154173__6);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testFoo_add153836_failAssert706litNum154173__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            Assert.assertEquals(509, ((int) (o_testFoo_add153836_failAssert706litNum154173__9)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_464 = (parsedFoos.size()) == (foos.size());
            int i = 413215109;
            for (Foo f : parsedFoos) {
                foos.get((i++));
                foos.get((i++));
            }
            org.junit.Assert.fail("testFoo_add153836 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testFoo_add153831litNum154152_failAssert753() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testFoo_add153831__3 = foos.add(SerializableObjects.foo);
            boolean o_testFoo_add153831__4 = foos.add(SerializableObjects.foo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testFoo_add153831__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] o_testFoo_add153831__9 = out.toByteArray();
            byte[] array_2032028253 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
            	byte[] array_1232388386 = (byte[])o_testFoo_add153831__9;
            	for(int ii = 0; ii <array_2032028253.length; ii++) {
            		org.junit.Assert.assertEquals(array_2032028253[ii], array_1232388386[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_467 = (parsedFoos.size()) == (foos.size());
            int i = Integer.MAX_VALUE;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            byte[] array_1517723286 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
            	byte[] array_1098029394 = (byte[])o_testFoo_add153831__9;
            	for(int ii = 0; ii <array_1517723286.length; ii++) {
            		org.junit.Assert.assertEquals(array_1517723286[ii], array_1098029394[ii]);
            	};
            org.junit.Assert.fail("testFoo_add153831litNum154152 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testFoo_mg153851null156560_failAssert729_add158085() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testFoo_mg153851__3 = foos.add(SerializableObjects.foo);
            boolean o_testFoo_mg153851__4 = foos.add(SerializableObjects.foo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testFoo_mg153851null156560_failAssert729_add158085__13 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            Assert.assertEquals(509, ((int) (o_testFoo_mg153851null156560_failAssert729_add158085__13)));
            int o_testFoo_mg153851__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(null, SerializableObjects.foo.cachedSchema());
            boolean boolean_435 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (Foo f : parsedFoos) {
                List<Double> __DSPOT_someDouble_49745 = Collections.singletonList(0.19727791867658429);
                foos.get((i++));
                f.setSomeDouble(__DSPOT_someDouble_49745);
            }
            org.junit.Assert.fail("testFoo_mg153851null156560 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFoo_add55559() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testEmptyFoo_add55559__3 = foos.add(new Foo());
        Assert.assertTrue(o_testEmptyFoo_add55559__3);
        boolean o_testEmptyFoo_add55559__5 = foos.add(new Foo());
        Assert.assertTrue(o_testEmptyFoo_add55559__5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFoo_add55559__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add55559__9)));
        int o_testEmptyFoo_add55559__11 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add55559__11)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_205 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertTrue(o_testEmptyFoo_add55559__3);
        Assert.assertTrue(o_testEmptyFoo_add55559__5);
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add55559__9)));
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add55559__11)));
    }

    @Test(timeout = 10000)
    public void testEmptyFoo_add55559_add56127() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testEmptyFoo_add55559__3 = foos.add(new Foo());
        boolean o_testEmptyFoo_add55559__5 = foos.add(new Foo());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFoo_add55559__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        int o_testEmptyFoo_add55559_add56127__17 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add55559_add56127__17)));
        int o_testEmptyFoo_add55559__11 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_205 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add55559_add56127__17)));
    }

    @Test(timeout = 10000)
    public void testEmptyFoo_mg55571litNum55777_failAssert343_add59870() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testEmptyFoo_mg55571__3 = foos.add(new Foo());
            boolean o_testEmptyFoo_mg55571__5 = foos.add(new Foo());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFoo_mg55571litNum55777_failAssert343_add59870__15 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            Assert.assertEquals(3, ((int) (o_testEmptyFoo_mg55571litNum55777_failAssert343_add59870__15)));
            int o_testEmptyFoo_mg55571__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_188 = (parsedFoos.size()) == (foos.size());
            int i = 1382262238;
            for (Foo f : parsedFoos) {
                Object __DSPOT_obj_17167 = new Object();
                foos.get((i++));
                f.equals(__DSPOT_obj_17167);
            }
            org.junit.Assert.fail("testEmptyFoo_mg55571litNum55777 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFoo2_add66020() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testEmptyFoo2_add66020__3 = foos.add(new Foo());
        Assert.assertTrue(o_testEmptyFoo2_add66020__3);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFoo2_add66020__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyFoo2_add66020__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> o_testEmptyFoo2_add66020__13 = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        Assert.assertFalse(o_testEmptyFoo2_add66020__13.isEmpty());
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_243 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertTrue(o_testEmptyFoo2_add66020__3);
        Assert.assertEquals(2, ((int) (o_testEmptyFoo2_add66020__7)));
        Assert.assertFalse(o_testEmptyFoo2_add66020__13.isEmpty());
    }

    @Test(timeout = 10000)
    public void testEmptyFoo2null66047_failAssert423litNum66349() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testEmptyFoo2null66047_failAssert423litNum66349__5 = foos.add(new Foo());
            Assert.assertTrue(o_testEmptyFoo2null66047_failAssert423litNum66349__5);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFoo2null66047_failAssert423litNum66349__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            Assert.assertEquals(2, ((int) (o_testEmptyFoo2null66047_failAssert423litNum66349__9)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(null, SerializableObjects.foo.cachedSchema());
            boolean boolean_254 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testEmptyFoo2null66047 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFoo2litNum66010_failAssert418null68666_failAssert444_add70212() throws Exception {
        try {
            try {
                ArrayList<Foo> foos = new ArrayList<Foo>();
                boolean o_testEmptyFoo2litNum66010_failAssert418null68666_failAssert444_add70212__7 = foos.add(new Foo());
                Assert.assertTrue(o_testEmptyFoo2litNum66010_failAssert418null68666_failAssert444_add70212__7);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int o_testEmptyFoo2litNum66010_failAssert418null68666_failAssert444_add70212__11 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
                Assert.assertEquals(2, ((int) (o_testEmptyFoo2litNum66010_failAssert418null68666_failAssert444_add70212__11)));
                byte[] data = out.toByteArray();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                parseListFrom(null, SerializableObjects.foo.cachedSchema());
                List<Foo> parsedFoos = parseListFrom(null, SerializableObjects.foo.cachedSchema());
                boolean boolean_240 = (parsedFoos.size()) == (foos.size());
                int i = 1;
                for (Foo f : parsedFoos) {
                    foos.get((i++));
                }
                org.junit.Assert.fail("testEmptyFoo2litNum66010 should have thrown IndexOutOfBoundsException");
            } catch (IndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testEmptyFoo2litNum66010_failAssert418null68666 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner_remove75681() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        ArrayList<Foo> foos = new ArrayList<Foo>();
        Foo foo = new Foo();
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeBar());
        Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
        foo.setSomeBar(bars);
        boolean o_testEmptyFooInner_remove75681__8 = foos.add(foo);
        Assert.assertTrue(o_testEmptyFooInner_remove75681__8);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFooInner_remove75681__11 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyFooInner_remove75681__11)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_278 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertTrue(((Foo) (foo)).getSomeBar().isEmpty());
        Assert.assertEquals("Foo [someBar=[], someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-2003967968, ((int) (((Foo) (foo)).hashCode())));
        Assert.assertTrue(o_testEmptyFooInner_remove75681__8);
        Assert.assertEquals(2, ((int) (o_testEmptyFooInner_remove75681__11)));
    }

    @Test(timeout = 10000)
    public void testEmptyFooInnerlitNum75666_failAssert493_add78019() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyFooInnerlitNum75666_failAssert493_add78019__5 = bars.add(new Bar());
            Assert.assertTrue(o_testEmptyFooInnerlitNum75666_failAssert493_add78019__5);
            ArrayList<Foo> foos = new ArrayList<Foo>();
            Foo foo = new Foo();
            Assert.assertNull(((Foo) (foo)).getSomeInt());
            Assert.assertNull(((Foo) (foo)).getSomeFloat());
            Assert.assertNull(((Foo) (foo)).getSomeBar());
            Assert.assertNull(((Foo) (foo)).getSomeString());
            Assert.assertNull(((Foo) (foo)).getSomeBoolean());
            Assert.assertNull(((Foo) (foo)).getSomeBytes());
            Assert.assertNull(((Foo) (foo)).getSomeDouble());
            Assert.assertNull(((Foo) (foo)).getSomeEnum());
            Assert.assertNull(((Foo) (foo)).getSomeLong());
            Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
            Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
            foo.setSomeBar(bars);
            boolean o_testEmptyFooInnerlitNum75666_failAssert493_add78019__12 = foos.add(foo);
            Assert.assertTrue(o_testEmptyFooInnerlitNum75666_failAssert493_add78019__12);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFooInnerlitNum75666_failAssert493_add78019__15 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            Assert.assertEquals(4, ((int) (o_testEmptyFooInnerlitNum75666_failAssert493_add78019__15)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            int o_testEmptyFooInnerlitNum75666_failAssert493_add78019__24 = parsedFoos.size();
            Assert.assertEquals(1, ((int) (o_testEmptyFooInnerlitNum75666_failAssert493_add78019__24)));
            boolean boolean_284 = (parsedFoos.size()) == (foos.size());
            int i = Integer.MAX_VALUE;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testEmptyFooInnerlitNum75666 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner_mg75712litBool76324_add84708() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyFooInner_mg75712__3 = bars.add(new Bar());
        ArrayList<Foo> foos = new ArrayList<Foo>();
        Foo foo = new Foo();
        Assert.assertNull(((Foo) (foo)).getSomeBar());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
        foo.setSomeBar(bars);
        boolean o_testEmptyFooInner_mg75712__10 = foos.add(foo);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFooInner_mg75712litBool76324_add84708__17 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(4, ((int) (o_testEmptyFooInner_mg75712litBool76324_add84708__17)));
        int o_testEmptyFooInner_mg75712__13 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_303 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            List<Boolean> __DSPOT_someBoolean_24053 = Collections.singletonList(false);
            foos.get((i++));
            f.setSomeBoolean(__DSPOT_someBoolean_24053);
        }
        Assert.assertFalse(((Foo) (foo)).getSomeBar().isEmpty());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertEquals("Foo [someBar=[Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]], someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-1560683800, ((int) (((Foo) (foo)).hashCode())));
        Assert.assertEquals(4, ((int) (o_testEmptyFooInner_mg75712litBool76324_add84708__17)));
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner2_remove105993() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        Bar bar = new Bar();
        Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
        Assert.assertNull(((Bar) (bar)).getSomeString());
        Assert.assertNull(((Bar) (bar)).getSomeBaz());
        Assert.assertNull(((Bar) (bar)).getSomeEnum());
        Assert.assertNull(((Bar) (bar)).getSomeBytes());
        Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
        Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
        Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
        Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
        Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
        Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
        bar.setSomeBaz(new Baz());
        ArrayList<Foo> foos = new ArrayList<Foo>();
        Foo foo = new Foo();
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeBar());
        Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
        foo.setSomeBar(bars);
        boolean o_testEmptyFooInner2_remove105993__12 = foos.add(foo);
        Assert.assertTrue(o_testEmptyFooInner2_remove105993__12);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFooInner2_remove105993__15 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyFooInner2_remove105993__15)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_340 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
        Assert.assertNull(((Bar) (bar)).getSomeString());
        Assert.assertEquals(0L, ((long) (((Baz) (((Bar) (bar)).getSomeBaz())).getTimestamp())));
        Assert.assertEquals("Baz [id=0, name=null, timestamp=0]", ((Baz) (((Bar) (bar)).getSomeBaz())).toString());
        Assert.assertEquals(29791, ((int) (((Baz) (((Bar) (bar)).getSomeBaz())).hashCode())));
        Assert.assertNull(((Baz) (((Bar) (bar)).getSomeBaz())).getName());
        Assert.assertEquals(0, ((int) (((Baz) (((Bar) (bar)).getSomeBaz())).getId())));
        Assert.assertNull(((Bar) (bar)).getSomeEnum());
        Assert.assertNull(((Bar) (bar)).getSomeBytes());
        Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
        Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
        Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
        Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
        Assert.assertEquals("Bar [someBaz=Baz [id=0, name=null, timestamp=0], someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
        Assert.assertEquals(-284628983, ((int) (((Bar) (bar)).hashCode())));
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertTrue(((Foo) (foo)).getSomeBar().isEmpty());
        Assert.assertEquals("Foo [someBar=[], someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-2003967968, ((int) (((Foo) (foo)).hashCode())));
        Assert.assertTrue(o_testEmptyFooInner2_remove105993__12);
        Assert.assertEquals(2, ((int) (o_testEmptyFooInner2_remove105993__15)));
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner2null106054_failAssert586_mg115152() throws Exception {
        try {
            String __DSPOT_someString_37063 = "E.#0>us&=9p5.1w?Ej!g";
            ArrayList<Bar> bars = new ArrayList<Bar>();
            Bar bar = new Bar();
            Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
            Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
            Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
            Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
            Assert.assertNull(((Bar) (bar)).getSomeEnum());
            Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
            Assert.assertNull(((Bar) (bar)).getSomeBaz());
            Assert.assertNull(((Bar) (bar)).getSomeBytes());
            Assert.assertNull(((Bar) (bar)).getSomeString());
            Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
            Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
            bar.setSomeBaz(new Baz());
            boolean o_testEmptyFooInner2null106054_failAssert586_mg115152__10 = bars.add(bar);
            Assert.assertTrue(o_testEmptyFooInner2null106054_failAssert586_mg115152__10);
            ArrayList<Foo> foos = new ArrayList<Foo>();
            Foo foo = new Foo();
            Assert.assertNull(((Foo) (foo)).getSomeDouble());
            Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
            Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
            Assert.assertNull(((Foo) (foo)).getSomeInt());
            Assert.assertNull(((Foo) (foo)).getSomeEnum());
            Assert.assertNull(((Foo) (foo)).getSomeBar());
            Assert.assertNull(((Foo) (foo)).getSomeLong());
            Assert.assertNull(((Foo) (foo)).getSomeBytes());
            Assert.assertNull(((Foo) (foo)).getSomeString());
            Assert.assertNull(((Foo) (foo)).getSomeFloat());
            Assert.assertNull(((Foo) (foo)).getSomeBoolean());
            foo.setSomeBar(bars);
            boolean o_testEmptyFooInner2null106054_failAssert586_mg115152__16 = foos.add(foo);
            Assert.assertTrue(o_testEmptyFooInner2null106054_failAssert586_mg115152__16);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFooInner2null106054_failAssert586_mg115152__19 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            Assert.assertEquals(6, ((int) (o_testEmptyFooInner2null106054_failAssert586_mg115152__19)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(null, SerializableObjects.foo.cachedSchema());
            boolean boolean_364 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testEmptyFooInner2null106054 should have thrown NullPointerException");
            bar.setSomeString(__DSPOT_someString_37063);
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner2_mg106008_remove111364_add124105() throws Exception {
        float __DSPOT_someFloat_34354 = 0.96136194F;
        ArrayList<Bar> bars = new ArrayList<Bar>();
        Bar bar = new Bar();
        Assert.assertNull(((Bar) (bar)).getSomeBaz());
        Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
        Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
        Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
        Assert.assertNull(((Bar) (bar)).getSomeBytes());
        Assert.assertNull(((Bar) (bar)).getSomeString());
        Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
        Assert.assertNull(((Bar) (bar)).getSomeEnum());
        Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
        Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
        Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
        bar.setSomeBaz(new Baz());
        boolean o_testEmptyFooInner2_mg106008__8 = bars.add(bar);
        ArrayList<Foo> foos = new ArrayList<Foo>();
        Foo foo = new Foo();
        Assert.assertNull(((Foo) (foo)).getSomeBar());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
        foo.setSomeBar(bars);
        boolean o_testEmptyFooInner2_mg106008__14 = foos.add(foo);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFooInner2_mg106008_remove111364_add124105__21 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(6, ((int) (o_testEmptyFooInner2_mg106008_remove111364_add124105__21)));
        int o_testEmptyFooInner2_mg106008__17 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_390 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertEquals(0L, ((long) (((Baz) (((Bar) (bar)).getSomeBaz())).getTimestamp())));
        Assert.assertEquals("Baz [id=0, name=null, timestamp=0]", ((Baz) (((Bar) (bar)).getSomeBaz())).toString());
        Assert.assertEquals(29791, ((int) (((Baz) (((Bar) (bar)).getSomeBaz())).hashCode())));
        Assert.assertNull(((Baz) (((Bar) (bar)).getSomeBaz())).getName());
        Assert.assertEquals(0, ((int) (((Baz) (((Bar) (bar)).getSomeBaz())).getId())));
        Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
        Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
        Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
        Assert.assertNull(((Bar) (bar)).getSomeBytes());
        Assert.assertNull(((Bar) (bar)).getSomeString());
        Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
        Assert.assertNull(((Bar) (bar)).getSomeEnum());
        Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
        Assert.assertEquals("Bar [someBaz=Baz [id=0, name=null, timestamp=0], someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
        Assert.assertEquals(-284628983, ((int) (((Bar) (bar)).hashCode())));
        Assert.assertFalse(((Foo) (foo)).getSomeBar().isEmpty());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertEquals("Foo [someBar=[Bar [someBaz=Baz [id=0, name=null, timestamp=0], someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]], someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-1969508025, ((int) (((Foo) (foo)).hashCode())));
        Assert.assertEquals(6, ((int) (o_testEmptyFooInner2_mg106008_remove111364_add124105__21)));
    }
}

