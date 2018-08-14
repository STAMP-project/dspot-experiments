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
        byte[] array_2098187935 = new byte[]{2, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 7, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 7};
        	byte[] array_653917512 = (byte[])o_testBar_add11__9;
        	for(int ii = 0; ii <array_2098187935.length; ii++) {
        		org.junit.Assert.assertEquals(array_2098187935[ii], array_653917512[ii]);
        	};
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_20 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        Assert.assertTrue(o_testBar_add11__3);
        Assert.assertTrue(o_testBar_add11__4);
        Assert.assertEquals(148, ((int) (o_testBar_add11__7)));
        byte[] array_1601397512 = new byte[]{2, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 7, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 7};
        	byte[] array_995075168 = (byte[])o_testBar_add11__9;
        	for(int ii = 0; ii <array_1601397512.length; ii++) {
        		org.junit.Assert.assertEquals(array_1601397512[ii], array_995075168[ii]);
        	};
    }

    @Test(timeout = 10000)
    public void testBarlitNum4_failAssert3() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            bars.add(SerializableObjects.bar);
            bars.add(SerializableObjects.negativeBar);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_13 = (parsedBars.size()) == (bars.size());
            int i = Integer.MIN_VALUE;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testBarlitNum4 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2147483648", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBar_mg28() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testBar_mg28__3 = bars.add(SerializableObjects.bar);
        Assert.assertTrue(o_testBar_mg28__3);
        boolean o_testBar_mg28__4 = bars.add(SerializableObjects.negativeBar);
        Assert.assertTrue(o_testBar_mg28__4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testBar_mg28__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(148, ((int) (o_testBar_mg28__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_37 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
            b.getSomeEnum();
        }
        Assert.assertTrue(o_testBar_mg28__3);
        Assert.assertTrue(o_testBar_mg28__4);
        Assert.assertEquals(148, ((int) (o_testBar_mg28__7)));
    }

    @Test(timeout = 10000)
    public void testBarlitNum2_failAssert1() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            bars.add(SerializableObjects.bar);
            bars.add(SerializableObjects.negativeBar);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_11 = (parsedBars.size()) == (bars.size());
            int i = -1;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testBarlitNum2 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
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
    public void testBarnull61_failAssert15null5356() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testBarnull61_failAssert15null5356__5 = bars.add(SerializableObjects.bar);
            Assert.assertTrue(o_testBarnull61_failAssert15null5356__5);
            boolean o_testBarnull61_failAssert15null5356__6 = bars.add(SerializableObjects.negativeBar);
            Assert.assertTrue(o_testBarnull61_failAssert15null5356__6);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testBarnull61_failAssert15null5356__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(148, ((int) (o_testBarnull61_failAssert15null5356__9)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = null;
            boolean boolean_70 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testBarnull61 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testBar_add11_add1451_failAssert18() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testBar_add11__3 = bars.add(SerializableObjects.bar);
            boolean o_testBar_add11__4 = bars.add(SerializableObjects.negativeBar);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testBar_add11__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] o_testBar_add11__9 = out.toByteArray();
            byte[] array_2098187935 = new byte[]{2, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 7, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 7};
            	byte[] array_653917512 = (byte[])o_testBar_add11__9;
            	for(int ii = 0; ii <array_2098187935.length; ii++) {
            		org.junit.Assert.assertEquals(array_2098187935[ii], array_653917512[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_20 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (Bar b : parsedBars) {
                bars.get((i++));
                bars.get((i++));
            }
            byte[] array_1601397512 = new byte[]{2, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 7, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 7};
            	byte[] array_995075168 = (byte[])o_testBar_add11__9;
            	for(int ii = 0; ii <array_1601397512.length; ii++) {
            		org.junit.Assert.assertEquals(array_1601397512[ii], array_995075168[ii]);
            	};
            org.junit.Assert.fail("testBar_add11_add1451 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            Assert.assertEquals("Index: 2, Size: 2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBar_add11_add1451_failAssert18_add8258() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testBar_add11__3 = bars.add(SerializableObjects.bar);
            boolean o_testBar_add11__4 = bars.add(SerializableObjects.negativeBar);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testBar_add11_add1451_failAssert18_add8258__13 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(148, ((int) (o_testBar_add11_add1451_failAssert18_add8258__13)));
            int o_testBar_add11__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] o_testBar_add11__9 = out.toByteArray();
            byte[] array_2098187935 = new byte[]{2, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 7, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 7};
            	byte[] array_653917512 = (byte[])o_testBar_add11__9;
            	for(int ii = 0; ii <array_2098187935.length; ii++) {
            		org.junit.Assert.assertEquals(array_2098187935[ii], array_653917512[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_20 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (Bar b : parsedBars) {
                bars.get((i++));
                bars.get((i++));
            }
            byte[] array_1601397512 = new byte[]{2, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 7, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 7};
            	byte[] array_995075168 = (byte[])o_testBar_add11__9;
            	for(int ii = 0; ii <array_1601397512.length; ii++) {
            		org.junit.Assert.assertEquals(array_1601397512[ii], array_995075168[ii]);
            	};
            org.junit.Assert.fail("testBar_add11_add1451 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBarnull11904() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBarnull11904__3 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyBarnull11904__3);
        boolean o_testEmptyBarnull11904__5 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyBarnull11904__5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBarnull11904__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyBarnull11904__9)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_149 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        Assert.assertTrue(o_testEmptyBarnull11904__3);
        Assert.assertTrue(o_testEmptyBarnull11904__5);
        Assert.assertEquals(3, ((int) (o_testEmptyBarnull11904__9)));
    }

    @Test(timeout = 10000)
    public void testEmptyBarlitNum11840_failAssert29() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            bars.add(new Bar());
            bars.add(new Bar());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_85 = (parsedBars.size()) == (bars.size());
            int i = -1;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testEmptyBarlitNum11840 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBarnull11896_failAssert41null13536() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBarnull11896_failAssert41null13536__5 = bars.add(new Bar());
            Assert.assertTrue(o_testEmptyBarnull11896_failAssert41null13536__5);
            boolean o_testEmptyBarnull11896_failAssert41null13536__7 = bars.add(new Bar());
            Assert.assertTrue(o_testEmptyBarnull11896_failAssert41null13536__7);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBarnull11896_failAssert41null13536__11 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(3, ((int) (o_testEmptyBarnull11896_failAssert41null13536__11)));
            byte[] data = null;
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = null;
            boolean boolean_141 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testEmptyBarnull11896 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBar_add11849_add13275litNum24157_failAssert65() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBar_add11849__3 = bars.add(new Bar());
            boolean o_testEmptyBar_add11849__5 = bars.add(new Bar());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBar_add11849__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] o_testEmptyBar_add11849__11 = out.toByteArray();
            byte[] array_2112626535 = new byte[]{2, 7, 7};
            	byte[] array_2086770861 = (byte[])o_testEmptyBar_add11849__11;
            	for(int ii = 0; ii <array_2112626535.length; ii++) {
            		org.junit.Assert.assertEquals(array_2112626535[ii], array_2086770861[ii]);
            	};
            byte[] o_testEmptyBar_add11849_add13275__22 = out.toByteArray();
            byte[] array_884375420 = new byte[]{2, 7, 7};
            	byte[] array_1339557612 = (byte[])o_testEmptyBar_add11849_add13275__22;
            	for(int ii = 0; ii <array_884375420.length; ii++) {
            		org.junit.Assert.assertEquals(array_884375420[ii], array_1339557612[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_94 = (parsedBars.size()) == (bars.size());
            int i = Integer.MIN_VALUE;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            byte[] array_617160023 = new byte[]{2, 7, 7};
            	byte[] array_670942997 = (byte[])o_testEmptyBar_add11849__11;
            	for(int ii = 0; ii <array_617160023.length; ii++) {
            		org.junit.Assert.assertEquals(array_617160023[ii], array_670942997[ii]);
            	};
            byte[] array_419537939 = new byte[]{2, 7, 7};
            	byte[] array_1945454734 = (byte[])o_testEmptyBar_add11849_add13275__22;
            	for(int ii = 0; ii <array_419537939.length; ii++) {
            		org.junit.Assert.assertEquals(array_419537939[ii], array_1945454734[ii]);
            	};
            org.junit.Assert.fail("testEmptyBar_add11849_add13275litNum24157 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2147483648", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBar_add11849_add13275_add24184() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBar_add11849__3 = bars.add(new Bar());
        boolean o_testEmptyBar_add11849__5 = bars.add(new Bar());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBar_add11849_add13275_add24184__13 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyBar_add11849_add13275_add24184__13)));
        int o_testEmptyBar_add11849__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        byte[] o_testEmptyBar_add11849__11 = out.toByteArray();
        byte[] array_2112626535 = new byte[]{2, 7, 7};
        	byte[] array_2086770861 = (byte[])o_testEmptyBar_add11849__11;
        	for(int ii = 0; ii <array_2112626535.length; ii++) {
        		org.junit.Assert.assertEquals(array_2112626535[ii], array_2086770861[ii]);
        	};
        byte[] o_testEmptyBar_add11849_add13275__22 = out.toByteArray();
        byte[] array_884375420 = new byte[]{2, 7, 7};
        	byte[] array_1339557612 = (byte[])o_testEmptyBar_add11849_add13275__22;
        	for(int ii = 0; ii <array_884375420.length; ii++) {
        		org.junit.Assert.assertEquals(array_884375420[ii], array_1339557612[ii]);
        	};
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_94 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        byte[] array_617160023 = new byte[]{2, 7, 7};
        	byte[] array_670942997 = (byte[])o_testEmptyBar_add11849__11;
        	for(int ii = 0; ii <array_617160023.length; ii++) {
        		org.junit.Assert.assertEquals(array_617160023[ii], array_670942997[ii]);
        	};
        byte[] array_419537939 = new byte[]{2, 7, 7};
        	byte[] array_1945454734 = (byte[])o_testEmptyBar_add11849_add13275__22;
        	for(int ii = 0; ii <array_419537939.length; ii++) {
        		org.junit.Assert.assertEquals(array_419537939[ii], array_1945454734[ii]);
        	};
        Assert.assertEquals(3, ((int) (o_testEmptyBar_add11849_add13275_add24184__13)));
    }

    @Test(timeout = 10000)
    public void testEmptyBar_add11849_add13275litNum24147_failAssert73() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBar_add11849__3 = bars.add(new Bar());
            boolean o_testEmptyBar_add11849__5 = bars.add(new Bar());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBar_add11849__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] o_testEmptyBar_add11849__11 = out.toByteArray();
            byte[] array_2112626535 = new byte[]{2, 7, 7};
            	byte[] array_2086770861 = (byte[])o_testEmptyBar_add11849__11;
            	for(int ii = 0; ii <array_2112626535.length; ii++) {
            		org.junit.Assert.assertEquals(array_2112626535[ii], array_2086770861[ii]);
            	};
            byte[] o_testEmptyBar_add11849_add13275__22 = out.toByteArray();
            byte[] array_884375420 = new byte[]{2, 7, 7};
            	byte[] array_1339557612 = (byte[])o_testEmptyBar_add11849_add13275__22;
            	for(int ii = 0; ii <array_884375420.length; ii++) {
            		org.junit.Assert.assertEquals(array_884375420[ii], array_1339557612[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_94 = (parsedBars.size()) == (bars.size());
            int i = -1;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            byte[] array_617160023 = new byte[]{2, 7, 7};
            	byte[] array_670942997 = (byte[])o_testEmptyBar_add11849__11;
            	for(int ii = 0; ii <array_617160023.length; ii++) {
            		org.junit.Assert.assertEquals(array_617160023[ii], array_670942997[ii]);
            	};
            byte[] array_419537939 = new byte[]{2, 7, 7};
            	byte[] array_1945454734 = (byte[])o_testEmptyBar_add11849_add13275__22;
            	for(int ii = 0; ii <array_419537939.length; ii++) {
            		org.junit.Assert.assertEquals(array_419537939[ii], array_1945454734[ii]);
            	};
            org.junit.Assert.fail("testEmptyBar_add11849_add13275litNum24147 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBar2() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBar2__3 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyBar2__3);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBar2__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyBar2__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_2 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        Assert.assertTrue(o_testEmptyBar2__3);
        Assert.assertEquals(2, ((int) (o_testEmptyBar2__7)));
    }

    @Test(timeout = 10000)
    public void testEmptyBar2null25309() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBar2null25309__3 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyBar2null25309__3);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBar2null25309__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyBar2null25309__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_224 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
        }
        Assert.assertTrue(o_testEmptyBar2null25309__3);
        Assert.assertEquals(2, ((int) (o_testEmptyBar2null25309__7)));
    }

    @Test(timeout = 10000)
    public void testEmptyBar2litNum25243_failAssert94() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            bars.add(new Bar());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_159 = (parsedBars.size()) == (bars.size());
            int i = -1;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testEmptyBar2litNum25243 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBar2_add25252_add26466() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBar2_add25252__3 = bars.add(new Bar());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBar2_add25252_add26466__9 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyBar2_add25252_add26466__9)));
        int o_testEmptyBar2_add25252__7 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> o_testEmptyBar2_add25252__13 = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_168 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        Assert.assertEquals(2, ((int) (o_testEmptyBar2_add25252_add26466__9)));
    }

    @Test(timeout = 10000)
    public void testEmptyBarInnerlitNum36063_failAssert142() throws Exception {
        try {
            Bar bar = new Bar();
            bar.setSomeBaz(new Baz());
            ArrayList<Bar> bars = new ArrayList<Bar>();
            bars.add(bar);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_229 = (parsedBars.size()) == (bars.size());
            int i = -1;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testEmptyBarInnerlitNum36063 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBarInner_remove36078() throws Exception {
        Bar bar = new Bar();
        Assert.assertNull(((Bar) (bar)).getSomeBaz());
        Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
        Assert.assertNull(((Bar) (bar)).getSomeBytes());
        Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
        Assert.assertNull(((Bar) (bar)).getSomeEnum());
        Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
        Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
        Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
        Assert.assertNull(((Bar) (bar)).getSomeString());
        Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
        Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyBarInner_remove36078__5 = bars.add(bar);
        Assert.assertTrue(o_testEmptyBarInner_remove36078__5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyBarInner_remove36078__8 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyBarInner_remove36078__8)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
        boolean boolean_244 = (parsedBars.size()) == (bars.size());
        int i = 0;
        for (Bar b : parsedBars) {
            bars.get((i++));
        }
        Assert.assertNull(((Bar) (bar)).getSomeBaz());
        Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
        Assert.assertNull(((Bar) (bar)).getSomeBytes());
        Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
        Assert.assertNull(((Bar) (bar)).getSomeEnum());
        Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
        Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
        Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
        Assert.assertNull(((Bar) (bar)).getSomeString());
        Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
        Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
        Assert.assertTrue(o_testEmptyBarInner_remove36078__5);
        Assert.assertEquals(2, ((int) (o_testEmptyBarInner_remove36078__8)));
    }

    @Test(timeout = 10000)
    public void testEmptyBarInnernull36149_failAssert155null55326() throws Exception {
        try {
            Bar bar = new Bar();
            Assert.assertNull(((Bar) (bar)).getSomeBaz());
            Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
            Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
            Assert.assertNull(((Bar) (bar)).getSomeEnum());
            Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
            Assert.assertNull(((Bar) (bar)).getSomeBytes());
            Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
            Assert.assertNull(((Bar) (bar)).getSomeString());
            Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
            Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
            Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
            bar.setSomeBaz(null);
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBarInnernull36149_failAssert155null55326__8 = bars.add(bar);
            Assert.assertTrue(o_testEmptyBarInnernull36149_failAssert155null55326__8);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBarInnernull36149_failAssert155null55326__11 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(2, ((int) (o_testEmptyBarInnernull36149_failAssert155null55326__11)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = null;
            List<Bar> parsedBars = parseListFrom(in, SerializableObjects.bar.cachedSchema());
            boolean boolean_315 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testEmptyBarInnernull36149 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyBarInnernull36150_failAssert156_add51507_add71402() throws Exception {
        try {
            Bar bar = new Bar();
            Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
            Assert.assertNull(((Bar) (bar)).getSomeString());
            Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
            Assert.assertNull(((Bar) (bar)).getSomeBytes());
            Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
            Assert.assertNull(((Bar) (bar)).getSomeEnum());
            Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
            Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
            Assert.assertNull(((Bar) (bar)).getSomeBaz());
            Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
            Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
            bar.setSomeBaz(new Baz());
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyBarInnernull36150_failAssert156_add51507__9 = bars.add(bar);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyBarInnernull36150_failAssert156_add51507_add71402__14 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            Assert.assertEquals(4, ((int) (o_testEmptyBarInnernull36150_failAssert156_add51507_add71402__14)));
            int o_testEmptyBarInnernull36150_failAssert156_add51507__12 = writeListTo(out, bars, SerializableObjects.bar.cachedSchema());
            byte[] o_testEmptyBarInnernull36150_failAssert156_add51507__14 = out.toByteArray();
            byte[] array_294941060 = new byte[]{1, 27, 28, 7};
            	byte[] array_1653026266 = (byte[])o_testEmptyBarInnernull36150_failAssert156_add51507__14;
            	for(int ii = 0; ii <array_294941060.length; ii++) {
            		org.junit.Assert.assertEquals(array_294941060[ii], array_1653026266[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Bar> parsedBars = null;
            boolean boolean_316 = (parsedBars.size()) == (bars.size());
            int i = 0;
            for (Bar b : parsedBars) {
                bars.get((i++));
            }
            org.junit.Assert.fail("testEmptyBarInnernull36150 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
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
    public void testFoolitNum243423_failAssert313() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            foos.add(SerializableObjects.foo);
            foos.add(SerializableObjects.foo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_806 = (parsedFoos.size()) == (foos.size());
            int i = Integer.MIN_VALUE;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testFoolitNum243423 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2147483648", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFoo_mg243448() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testFoo_mg243448__3 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo_mg243448__3);
        boolean o_testFoo_mg243448__4 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo_mg243448__4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testFoo_mg243448__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(509, ((int) (o_testFoo_mg243448__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_831 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
            f.getSomeFloat();
        }
        Assert.assertTrue(o_testFoo_mg243448__3);
        Assert.assertTrue(o_testFoo_mg243448__4);
        Assert.assertEquals(509, ((int) (o_testFoo_mg243448__7)));
    }

    @Test(timeout = 10000)
    public void testFoolitNum243421_failAssert311() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            foos.add(SerializableObjects.foo);
            foos.add(SerializableObjects.foo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_804 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testFoolitNum243421 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFoo_add243430() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testFoo_add243430__3 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo_add243430__3);
        boolean o_testFoo_add243430__4 = foos.add(SerializableObjects.foo);
        Assert.assertTrue(o_testFoo_add243430__4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testFoo_add243430__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(509, ((int) (o_testFoo_add243430__7)));
        byte[] o_testFoo_add243430__9 = out.toByteArray();
        byte[] array_1883619919 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
        	byte[] array_510928587 = (byte[])o_testFoo_add243430__9;
        	for(int ii = 0; ii <array_1883619919.length; ii++) {
        		org.junit.Assert.assertEquals(array_1883619919[ii], array_510928587[ii]);
        	};
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_813 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertTrue(o_testFoo_add243430__3);
        Assert.assertTrue(o_testFoo_add243430__4);
        Assert.assertEquals(509, ((int) (o_testFoo_add243430__7)));
        byte[] array_2097368655 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
        	byte[] array_1056298466 = (byte[])o_testFoo_add243430__9;
        	for(int ii = 0; ii <array_2097368655.length; ii++) {
        		org.junit.Assert.assertEquals(array_2097368655[ii], array_1056298466[ii]);
        	};
    }

    @Test(timeout = 10000)
    public void testFoo_add243430litNum249498_failAssert331() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testFoo_add243430__3 = foos.add(SerializableObjects.foo);
            boolean o_testFoo_add243430__4 = foos.add(SerializableObjects.foo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testFoo_add243430__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] o_testFoo_add243430__9 = out.toByteArray();
            byte[] array_1883619919 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
            	byte[] array_510928587 = (byte[])o_testFoo_add243430__9;
            	for(int ii = 0; ii <array_1883619919.length; ii++) {
            		org.junit.Assert.assertEquals(array_1883619919[ii], array_510928587[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_813 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            byte[] array_2097368655 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
            	byte[] array_1056298466 = (byte[])o_testFoo_add243430__9;
            	for(int ii = 0; ii <array_2097368655.length; ii++) {
            		org.junit.Assert.assertEquals(array_2097368655[ii], array_1056298466[ii]);
            	};
            org.junit.Assert.fail("testFoo_add243430litNum249498 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFoo_add243430litNum249503_failAssert328() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testFoo_add243430__3 = foos.add(SerializableObjects.foo);
            boolean o_testFoo_add243430__4 = foos.add(SerializableObjects.foo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testFoo_add243430__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] o_testFoo_add243430__9 = out.toByteArray();
            byte[] array_1883619919 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
            	byte[] array_510928587 = (byte[])o_testFoo_add243430__9;
            	for(int ii = 0; ii <array_1883619919.length; ii++) {
            		org.junit.Assert.assertEquals(array_1883619919[ii], array_510928587[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_813 = (parsedFoos.size()) == (foos.size());
            int i = Integer.MIN_VALUE;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            byte[] array_2097368655 = new byte[]{2, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7, 8, -30, -64, 5, 8, -98, -65, -6, -1, -1, -1, -1, -1, -1, 1, 8, 0, 18, 2, 97, 98, 18, 2, 99, 100, 27, 8, -6, 6, 18, 3, 98, 97, 114, 27, 8, -73, 4, 18, 3, 98, 97, 122, 24, -22, -86, -86, 96, 28, 32, 1, 42, 2, 98, 50, 48, 1, 61, 14, 13, 22, 67, 65, -42, -59, 109, 52, 0, 64, -97, 64, 72, -97, -64, -65, -112, 1, 28, 27, 8, -12, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 114, 27, 8, -55, -5, -1, -1, -1, -1, -1, -1, -1, 1, 18, 11, 110, 101, 103, 97, 116, 105, 118, 101, 66, 97, 122, 24, -106, -43, -43, -97, -1, -1, -1, -1, -1, 1, 28, 32, 1, 42, 2, 97, 49, 48, 1, 61, -16, 7, 2, -61, 65, -42, -59, 109, 52, 0, 64, -113, -64, 72, -53, -22, -22, -49, -1, -1, -1, -1, -1, 1, 28, 32, 0, 32, 2, 42, 2, 101, 102, 42, 2, 103, 104, 48, 1, 48, 0, 61, -44, 77, -102, 68, 61, -44, 77, -102, -60, 61, 0, 0, 0, 0, 65, 89, -92, 12, -36, 41, -116, 103, 65, 65, 89, -92, 12, -36, 41, -116, 103, -63, 65, 0, 0, 0, 0, 0, 0, 0, 0, 72, -7, -41, -42, -74, -66, -51, 1, 72, -121, -88, -87, -55, -63, -78, -2, -1, -1, 1, 72, 0, 7};
            	byte[] array_1056298466 = (byte[])o_testFoo_add243430__9;
            	for(int ii = 0; ii <array_2097368655.length; ii++) {
            		org.junit.Assert.assertEquals(array_2097368655[ii], array_1056298466[ii]);
            	};
            org.junit.Assert.fail("testFoo_add243430litNum249503 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2147483648", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFoolitNum83352_failAssert162() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            foos.add(new Foo());
            foos.add(new Foo());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_333 = (parsedFoos.size()) == (foos.size());
            int i = Integer.MIN_VALUE;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testEmptyFoolitNum83352 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2147483648", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFoo_add83360() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testEmptyFoo_add83360__3 = foos.add(new Foo());
        Assert.assertTrue(o_testEmptyFoo_add83360__3);
        boolean o_testEmptyFoo_add83360__5 = foos.add(new Foo());
        Assert.assertTrue(o_testEmptyFoo_add83360__5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFoo_add83360__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add83360__9)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> o_testEmptyFoo_add83360__15 = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        Assert.assertFalse(o_testEmptyFoo_add83360__15.isEmpty());
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_341 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertTrue(o_testEmptyFoo_add83360__3);
        Assert.assertTrue(o_testEmptyFoo_add83360__5);
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add83360__9)));
        Assert.assertFalse(o_testEmptyFoo_add83360__15.isEmpty());
    }

    @Test(timeout = 10000)
    public void testEmptyFoo_add83359litNum84930_failAssert181() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testEmptyFoo_add83359__3 = foos.add(new Foo());
            boolean o_testEmptyFoo_add83359__5 = foos.add(new Foo());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFoo_add83359__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] o_testEmptyFoo_add83359__11 = out.toByteArray();
            byte[] array_61624023 = new byte[]{2, 7, 7};
            	byte[] array_468383062 = (byte[])o_testEmptyFoo_add83359__11;
            	for(int ii = 0; ii <array_61624023.length; ii++) {
            		org.junit.Assert.assertEquals(array_61624023[ii], array_468383062[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_340 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            byte[] array_729144110 = new byte[]{2, 7, 7};
            	byte[] array_994480743 = (byte[])o_testEmptyFoo_add83359__11;
            	for(int ii = 0; ii <array_729144110.length; ii++) {
            		org.junit.Assert.assertEquals(array_729144110[ii], array_994480743[ii]);
            	};
            org.junit.Assert.fail("testEmptyFoo_add83359litNum84930 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFoo_add83359_add84968() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testEmptyFoo_add83359__3 = foos.add(new Foo());
        boolean o_testEmptyFoo_add83359__5 = foos.add(new Foo());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFoo_add83359_add84968__13 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add83359_add84968__13)));
        int o_testEmptyFoo_add83359__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        byte[] o_testEmptyFoo_add83359__11 = out.toByteArray();
        byte[] array_61624023 = new byte[]{2, 7, 7};
        	byte[] array_468383062 = (byte[])o_testEmptyFoo_add83359__11;
        	for(int ii = 0; ii <array_61624023.length; ii++) {
        		org.junit.Assert.assertEquals(array_61624023[ii], array_468383062[ii]);
        	};
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_340 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        byte[] array_729144110 = new byte[]{2, 7, 7};
        	byte[] array_994480743 = (byte[])o_testEmptyFoo_add83359__11;
        	for(int ii = 0; ii <array_729144110.length; ii++) {
        		org.junit.Assert.assertEquals(array_729144110[ii], array_994480743[ii]);
        	};
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add83359_add84968__13)));
    }

    @Test(timeout = 10000)
    public void testEmptyFoo_add83359_add84981_add92203() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testEmptyFoo_add83359__3 = foos.add(new Foo());
        boolean o_testEmptyFoo_add83359__5 = foos.add(new Foo());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFoo_add83359_add84981_add92203__13 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add83359_add84981_add92203__13)));
        int o_testEmptyFoo_add83359__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        byte[] o_testEmptyFoo_add83359__11 = out.toByteArray();
        byte[] array_61624023 = new byte[]{2, 7, 7};
        	byte[] array_468383062 = (byte[])o_testEmptyFoo_add83359__11;
        	for(int ii = 0; ii <array_61624023.length; ii++) {
        		org.junit.Assert.assertEquals(array_61624023[ii], array_468383062[ii]);
        	};
        byte[] o_testEmptyFoo_add83359_add84981__22 = out.toByteArray();
        byte[] array_287363849 = new byte[]{2, 7, 7};
        	byte[] array_1199763947 = (byte[])o_testEmptyFoo_add83359_add84981__22;
        	for(int ii = 0; ii <array_287363849.length; ii++) {
        		org.junit.Assert.assertEquals(array_287363849[ii], array_1199763947[ii]);
        	};
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_340 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        byte[] array_729144110 = new byte[]{2, 7, 7};
        	byte[] array_994480743 = (byte[])o_testEmptyFoo_add83359__11;
        	for(int ii = 0; ii <array_729144110.length; ii++) {
        		org.junit.Assert.assertEquals(array_729144110[ii], array_994480743[ii]);
        	};
        byte[] array_1681245080 = new byte[]{2, 7, 7};
        	byte[] array_272370712 = (byte[])o_testEmptyFoo_add83359_add84981__22;
        	for(int ii = 0; ii <array_1681245080.length; ii++) {
        		org.junit.Assert.assertEquals(array_1681245080[ii], array_272370712[ii]);
        	};
        Assert.assertEquals(3, ((int) (o_testEmptyFoo_add83359_add84981_add92203__13)));
    }

    @Test(timeout = 10000)
    public void testEmptyFoo2litNum93548_failAssert212() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            foos.add(new Foo());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_407 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testEmptyFoo2litNum93548 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFoo2litNum93551() throws Exception {
        ArrayList<Foo> foos = new ArrayList<Foo>();
        boolean o_testEmptyFoo2litNum93551__3 = foos.add(new Foo());
        Assert.assertTrue(o_testEmptyFoo2litNum93551__3);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFoo2litNum93551__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyFoo2litNum93551__7)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_410 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertTrue(o_testEmptyFoo2litNum93551__3);
        Assert.assertEquals(2, ((int) (o_testEmptyFoo2litNum93551__7)));
    }

    @Test(timeout = 10000)
    public void testEmptyFoo2_add93556litNum98720_failAssert234() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testEmptyFoo2_add93556__3 = foos.add(new Foo());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFoo2_add93556__7 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] o_testEmptyFoo2_add93556__9 = out.toByteArray();
            byte[] array_1524818943 = new byte[]{1, 7};
            	byte[] array_415256272 = (byte[])o_testEmptyFoo2_add93556__9;
            	for(int ii = 0; ii <array_1524818943.length; ii++) {
            		org.junit.Assert.assertEquals(array_1524818943[ii], array_415256272[ii]);
            	};
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_415 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            byte[] array_1304881359 = new byte[]{1, 7};
            	byte[] array_1633179626 = (byte[])o_testEmptyFoo2_add93556__9;
            	for(int ii = 0; ii <array_1304881359.length; ii++) {
            		org.junit.Assert.assertEquals(array_1304881359[ii], array_1633179626[ii]);
            	};
            org.junit.Assert.fail("testEmptyFoo2_add93556litNum98720 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFoo2null93604_failAssert224_mg97588() throws Exception {
        try {
            ArrayList<Foo> foos = new ArrayList<Foo>();
            boolean o_testEmptyFoo2null93604_failAssert224_mg97588__5 = foos.add(new Foo());
            Assert.assertTrue(o_testEmptyFoo2null93604_failAssert224_mg97588__5);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFoo2null93604_failAssert224_mg97588__9 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            Assert.assertEquals(2, ((int) (o_testEmptyFoo2null93604_failAssert224_mg97588__9)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = null;
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_463 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (Foo f : parsedFoos) {
                Foo __DSPOT_obj_17816 = new Foo(Collections.<Integer>emptyList(), Collections.singletonList("; ^yZko@t8N1xJ64m6CP"), Collections.singletonList(new Bar()), Collections.<Foo.EnumSample>emptyList(), Collections.<ByteString>emptyList(), Collections.<Boolean>emptyList(), Collections.<Float>emptyList(), Collections.<Double>emptyList(), Collections.<Long>emptyList());
                foos.get((i++));
                f.equals(__DSPOT_obj_17816);
            }
            org.junit.Assert.fail("testEmptyFoo2null93604 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFooInnerlitNum104767_failAssert239() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            bars.add(new Bar());
            ArrayList<Foo> foos = new ArrayList<Foo>();
            Foo foo = new Foo();
            foo.setSomeBar(bars);
            foos.add(foo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_479 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testEmptyFooInnerlitNum104767 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner_remove104784() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyFooInner_remove104784__3 = bars.add(new Bar());
        Assert.assertTrue(o_testEmptyFooInner_remove104784__3);
        ArrayList<Foo> foos = new ArrayList<Foo>();
        Foo foo = new Foo();
        Assert.assertNull(((Foo) (foo)).getSomeBar());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
        boolean o_testEmptyFooInner_remove104784__9 = foos.add(foo);
        Assert.assertTrue(o_testEmptyFooInner_remove104784__9);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFooInner_remove104784__12 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyFooInner_remove104784__12)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_496 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            foos.get((i++));
        }
        Assert.assertTrue(o_testEmptyFooInner_remove104784__3);
        Assert.assertNull(((Foo) (foo)).getSomeBar());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
        Assert.assertTrue(o_testEmptyFooInner_remove104784__9);
        Assert.assertEquals(2, ((int) (o_testEmptyFooInner_remove104784__12)));
    }

    @Test(timeout = 10000)
    public void testEmptyFooInnernull104866_failAssert257null112586() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyFooInnernull104866_failAssert257null112586__5 = bars.add(new Bar());
            Assert.assertTrue(o_testEmptyFooInnernull104866_failAssert257null112586__5);
            ArrayList<Foo> foos = new ArrayList<Foo>();
            Foo foo = new Foo();
            Assert.assertNull(((Foo) (foo)).getSomeString());
            Assert.assertNull(((Foo) (foo)).getSomeBoolean());
            Assert.assertNull(((Foo) (foo)).getSomeBar());
            Assert.assertNull(((Foo) (foo)).getSomeInt());
            Assert.assertNull(((Foo) (foo)).getSomeEnum());
            Assert.assertNull(((Foo) (foo)).getSomeLong());
            Assert.assertNull(((Foo) (foo)).getSomeFloat());
            Assert.assertNull(((Foo) (foo)).getSomeBytes());
            Assert.assertNull(((Foo) (foo)).getSomeDouble());
            Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
            Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
            foo.setSomeBar(null);
            boolean o_testEmptyFooInnernull104866_failAssert257null112586__12 = foos.add(foo);
            Assert.assertTrue(o_testEmptyFooInnernull104866_failAssert257null112586__12);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFooInnernull104866_failAssert257null112586__15 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            Assert.assertEquals(2, ((int) (o_testEmptyFooInnernull104866_failAssert257null112586__15)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, null);
            boolean boolean_578 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testEmptyFooInnernull104866 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner_mg104787_mg115216litNum146706_failAssert262() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            boolean o_testEmptyFooInner_mg104787__3 = bars.add(new Bar());
            ArrayList<Foo> foos = new ArrayList<Foo>();
            Foo foo = new Foo();
            foo.setSomeBar(bars);
            boolean o_testEmptyFooInner_mg104787__10 = foos.add(foo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFooInner_mg104787__13 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_499 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (Foo f : parsedFoos) {
                Foo __DSPOT_obj_21394 = new Foo(Collections.singletonList(820736397), Collections.<String>emptyList(), Collections.<Bar>emptyList(), Collections.<Foo.EnumSample>emptyList(), Collections.<ByteString>emptyList(), Collections.singletonList(true), Collections.<Float>emptyList(), Collections.singletonList(0.5917006858363648), Collections.<Long>emptyList());
                foos.get((i++));
                f.equals(__DSPOT_obj_21394);
            }
            Schema<Foo> o_testEmptyFooInner_mg104787__31 = foo.cachedSchema();
            org.junit.Assert.fail("testEmptyFooInner_mg104787_mg115216litNum146706 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner_mg104787_mg115216_add147070() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        boolean o_testEmptyFooInner_mg104787__3 = bars.add(new Bar());
        ArrayList<Foo> foos = new ArrayList<Foo>();
        Foo foo = new Foo();
        Assert.assertNull(((Foo) (foo)).getSomeBar());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
        foo.setSomeBar(bars);
        boolean o_testEmptyFooInner_mg104787__10 = foos.add(foo);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFooInner_mg104787_mg115216_add147070__17 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(4, ((int) (o_testEmptyFooInner_mg104787_mg115216_add147070__17)));
        int o_testEmptyFooInner_mg104787__13 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_499 = (parsedFoos.size()) == (foos.size());
        int i = 0;
        for (Foo f : parsedFoos) {
            Foo __DSPOT_obj_21394 = new Foo(Collections.singletonList(820736397), Collections.<String>emptyList(), Collections.<Bar>emptyList(), Collections.<Foo.EnumSample>emptyList(), Collections.<ByteString>emptyList(), Collections.singletonList(true), Collections.<Float>emptyList(), Collections.singletonList(0.5917006858363648), Collections.<Long>emptyList());
            foos.get((i++));
            f.equals(__DSPOT_obj_21394);
        }
        Schema<Foo> o_testEmptyFooInner_mg104787__31 = foo.cachedSchema();
        Assert.assertFalse(((Foo) (o_testEmptyFooInner_mg104787__31)).getSomeBar().isEmpty());
        Assert.assertNull(((Foo) (o_testEmptyFooInner_mg104787__31)).getSomeBoolean());
        Assert.assertNull(((Foo) (o_testEmptyFooInner_mg104787__31)).getSomeString());
        Assert.assertNull(((Foo) (o_testEmptyFooInner_mg104787__31)).getSomeDouble());
        Assert.assertNull(((Foo) (o_testEmptyFooInner_mg104787__31)).getSomeBytes());
        Assert.assertNull(((Foo) (o_testEmptyFooInner_mg104787__31)).getSomeFloat());
        Assert.assertNull(((Foo) (o_testEmptyFooInner_mg104787__31)).getSomeLong());
        Assert.assertNull(((Foo) (o_testEmptyFooInner_mg104787__31)).getSomeEnum());
        Assert.assertNull(((Foo) (o_testEmptyFooInner_mg104787__31)).getSomeInt());
        Assert.assertEquals("Foo [someBar=[Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]], someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (o_testEmptyFooInner_mg104787__31)).toString());
        Assert.assertEquals(-1560683800, ((int) (((Foo) (o_testEmptyFooInner_mg104787__31)).hashCode())));
        Assert.assertFalse(((Foo) (foo)).getSomeBar().isEmpty());
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertEquals("Foo [someBar=[Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]], someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-1560683800, ((int) (((Foo) (foo)).hashCode())));
        Assert.assertEquals(4, ((int) (o_testEmptyFooInner_mg104787_mg115216_add147070__17)));
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner2_remove151318() throws Exception {
        ArrayList<Bar> bars = new ArrayList<Bar>();
        Bar bar = new Bar();
        Assert.assertNull(((Bar) (bar)).getSomeBaz());
        Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
        Assert.assertNull(((Bar) (bar)).getSomeBytes());
        Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
        Assert.assertNull(((Bar) (bar)).getSomeEnum());
        Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
        Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
        Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
        Assert.assertNull(((Bar) (bar)).getSomeString());
        Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
        Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
        bar.setSomeBaz(new Baz());
        ArrayList<Foo> foos = new ArrayList<Foo>();
        Foo foo = new Foo();
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertNull(((Foo) (foo)).getSomeBar());
        Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
        foo.setSomeBar(bars);
        boolean o_testEmptyFooInner2_remove151318__12 = foos.add(foo);
        Assert.assertTrue(o_testEmptyFooInner2_remove151318__12);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int o_testEmptyFooInner2_remove151318__15 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
        Assert.assertEquals(2, ((int) (o_testEmptyFooInner2_remove151318__15)));
        byte[] data = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
        boolean boolean_608 = (parsedFoos.size()) == (foos.size());
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
        Assert.assertNull(((Bar) (bar)).getSomeBytes());
        Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
        Assert.assertNull(((Bar) (bar)).getSomeEnum());
        Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
        Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
        Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
        Assert.assertNull(((Bar) (bar)).getSomeString());
        Assert.assertEquals("Bar [someBaz=Baz [id=0, name=null, timestamp=0], someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
        Assert.assertEquals(-284628983, ((int) (((Bar) (bar)).hashCode())));
        Assert.assertNull(((Foo) (foo)).getSomeBoolean());
        Assert.assertNull(((Foo) (foo)).getSomeBytes());
        Assert.assertNull(((Foo) (foo)).getSomeDouble());
        Assert.assertNull(((Foo) (foo)).getSomeEnum());
        Assert.assertNull(((Foo) (foo)).getSomeFloat());
        Assert.assertNull(((Foo) (foo)).getSomeInt());
        Assert.assertNull(((Foo) (foo)).getSomeLong());
        Assert.assertNull(((Foo) (foo)).getSomeString());
        Assert.assertTrue(((Foo) (foo)).getSomeBar().isEmpty());
        Assert.assertEquals("Foo [someBar=[], someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
        Assert.assertEquals(-2003967968, ((int) (((Foo) (foo)).hashCode())));
        Assert.assertTrue(o_testEmptyFooInner2_remove151318__12);
        Assert.assertEquals(2, ((int) (o_testEmptyFooInner2_remove151318__15)));
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner2litNum151300_failAssert266() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            Bar bar = new Bar();
            bar.setSomeBaz(new Baz());
            bars.add(bar);
            ArrayList<Foo> foos = new ArrayList<Foo>();
            Foo foo = new Foo();
            foo.setSomeBar(bars);
            foos.add(foo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = parseListFrom(in, SerializableObjects.foo.cachedSchema());
            boolean boolean_590 = (parsedFoos.size()) == (foos.size());
            int i = -1;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testEmptyFooInner2litNum151300 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testEmptyFooInner2null151428_failAssert283null159170() throws Exception {
        try {
            ArrayList<Bar> bars = new ArrayList<Bar>();
            Bar bar = new Bar();
            Assert.assertNull(((Bar) (bar)).getSomeString());
            Assert.assertFalse(((Bar) (bar)).getSomeBoolean());
            Assert.assertEquals(0.0, ((double) (((Bar) (bar)).getSomeDouble())), 0.1);
            Assert.assertEquals(0, ((int) (((Bar) (bar)).getSomeInt())));
            Assert.assertNull(((Bar) (bar)).getSomeBaz());
            Assert.assertNull(((Bar) (bar)).getSomeEnum());
            Assert.assertEquals(0L, ((long) (((Bar) (bar)).getSomeLong())));
            Assert.assertEquals(0.0F, ((float) (((Bar) (bar)).getSomeFloat())), 0.1);
            Assert.assertNull(((Bar) (bar)).getSomeBytes());
            Assert.assertEquals("Bar [someBaz=null, someBoolean=false, someBytes=null, someDouble=0.0, someEnum=null, someFloat=0.0, someInt=0, someLong=0, someString=null]", ((Bar) (bar)).toString());
            Assert.assertEquals(-413711702, ((int) (((Bar) (bar)).hashCode())));
            bar.setSomeBaz(new Baz());
            boolean o_testEmptyFooInner2null151428_failAssert283null159170__9 = bars.add(bar);
            Assert.assertTrue(o_testEmptyFooInner2null151428_failAssert283null159170__9);
            ArrayList<Foo> foos = new ArrayList<Foo>();
            Foo foo = new Foo();
            Assert.assertNull(((Foo) (foo)).getSomeString());
            Assert.assertNull(((Foo) (foo)).getSomeBoolean());
            Assert.assertNull(((Foo) (foo)).getSomeDouble());
            Assert.assertNull(((Foo) (foo)).getSomeBar());
            Assert.assertNull(((Foo) (foo)).getSomeInt());
            Assert.assertNull(((Foo) (foo)).getSomeEnum());
            Assert.assertNull(((Foo) (foo)).getSomeLong());
            Assert.assertNull(((Foo) (foo)).getSomeFloat());
            Assert.assertNull(((Foo) (foo)).getSomeBytes());
            Assert.assertEquals("Foo [someBar=null, someBoolean=null, someBytes=null, someDouble=null, someEnum=null, someFloat=null, someInt=null, someLong=null, someString=null]", ((Foo) (foo)).toString());
            Assert.assertEquals(-196513505, ((int) (((Foo) (foo)).hashCode())));
            foo.setSomeBar(null);
            boolean o_testEmptyFooInner2null151428_failAssert283null159170__15 = foos.add(foo);
            Assert.assertTrue(o_testEmptyFooInner2null151428_failAssert283null159170__15);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int o_testEmptyFooInner2null151428_failAssert283null159170__18 = writeListTo(out, foos, SerializableObjects.foo.cachedSchema());
            Assert.assertEquals(2, ((int) (o_testEmptyFooInner2null151428_failAssert283null159170__18)));
            byte[] data = out.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            List<Foo> parsedFoos = null;
            boolean boolean_718 = (parsedFoos.size()) == (foos.size());
            int i = 0;
            for (Foo f : parsedFoos) {
                foos.get((i++));
            }
            org.junit.Assert.fail("testEmptyFooInner2null151428 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }
}

