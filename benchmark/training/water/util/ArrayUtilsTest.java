package water.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test FrameUtils interface.
 */
public class ArrayUtilsTest {
    @Test
    public void testAppendBytes() {
        byte[] sut = new byte[]{ 1, 2, 3 };
        byte[] sut2 = new byte[]{ 3, 4 };
        byte[] expected = new byte[]{ 1, 2, 3, 3, 4 };
        byte[] empty = new byte[]{  };
        Assert.assertArrayEquals(null, ArrayUtils.append(((byte[]) (null)), null));
        Assert.assertArrayEquals(sut, ArrayUtils.append(null, sut));
        Assert.assertArrayEquals(sut, ArrayUtils.append(sut, null));
        Assert.assertArrayEquals(empty, ArrayUtils.append(null, empty));
        Assert.assertArrayEquals(empty, ArrayUtils.append(empty, null));
        Assert.assertArrayEquals(sut, ArrayUtils.append(empty, sut));
        Assert.assertArrayEquals(sut, ArrayUtils.append(sut, empty));
        Assert.assertArrayEquals(expected, ArrayUtils.append(sut, sut2));
    }

    @Test
    public void testAppendInts() {
        int[] sut = new int[]{ 1, 2, 3 };
        int[] sut2 = new int[]{ 3, 4 };
        int[] expected = new int[]{ 1, 2, 3, 3, 4 };
        int[] empty = new int[]{  };
        Assert.assertArrayEquals(null, ArrayUtils.append(((int[]) (null)), null));
        Assert.assertArrayEquals(sut, ArrayUtils.append(null, sut));
        Assert.assertArrayEquals(sut, ArrayUtils.append(sut, null));
        Assert.assertArrayEquals(empty, ArrayUtils.append(null, empty));
        Assert.assertArrayEquals(empty, ArrayUtils.append(empty, null));
        Assert.assertArrayEquals(sut, ArrayUtils.append(empty, sut));
        Assert.assertArrayEquals(sut, ArrayUtils.append(sut, empty));
        Assert.assertArrayEquals(expected, ArrayUtils.append(sut, sut2));
    }

    @Test
    public void testAppendLongs() {
        long[] sut = new long[]{ 1, 2, 3 };
        long[] sut2 = new long[]{ 3, 4 };
        long[] expected = new long[]{ 1, 2, 3, 3, 4 };
        long[] empty = new long[]{  };
        Assert.assertArrayEquals(null, ArrayUtils.append(((int[]) (null)), null));
        Assert.assertArrayEquals(sut, ArrayUtils.append(null, sut));
        Assert.assertArrayEquals(sut, ArrayUtils.append(sut, null));
        Assert.assertArrayEquals(empty, ArrayUtils.append(null, empty));
        Assert.assertArrayEquals(empty, ArrayUtils.append(empty, null));
        Assert.assertArrayEquals(sut, ArrayUtils.append(empty, sut));
        Assert.assertArrayEquals(sut, ArrayUtils.append(sut, empty));
        Assert.assertArrayEquals(expected, ArrayUtils.append(sut, sut2));
    }

    @Test
    public void testRemoveOneObject() {
        Integer[] sut = new Integer[]{ 1, 2, 3 };
        Integer[] sutWithout1 = new Integer[]{ 2, 3 };
        Integer[] sutWithout2 = new Integer[]{ 1, 3 };
        Integer[] sutWithout3 = new Integer[]{ 1, 2 };
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MIN_VALUE));
        Assert.assertArrayEquals("Should not have deleted ", sut, ArrayUtils.remove(sut, (-1)));
        Assert.assertArrayEquals("Should have deleted first", sutWithout1, ArrayUtils.remove(sut, 0));
        Assert.assertArrayEquals("Should have deleted second", sutWithout2, ArrayUtils.remove(sut, 1));
        Assert.assertArrayEquals("Should have deleted third", sutWithout3, ArrayUtils.remove(sut, 2));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, 3));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MAX_VALUE));
    }

    @Test
    public void testRemoveOneObjectFromSingleton() {
        Integer[] sut = new Integer[]{ 1 };
        Integer[] sutWithout1 = new Integer[]{  };
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MIN_VALUE));
        Assert.assertArrayEquals("Should not have deleted ", sut, ArrayUtils.remove(sut, (-1)));
        Assert.assertArrayEquals("Should have deleted first", sutWithout1, ArrayUtils.remove(sut, 0));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, 1));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MAX_VALUE));
    }

    @Test
    public void testRemoveOneObjectFromEmpty() {
        Integer[] sut = new Integer[]{  };
        Assert.assertArrayEquals("Nothing to remove", sut, ArrayUtils.remove(sut, (-1)));
        Assert.assertArrayEquals("Nothing to remove", sut, ArrayUtils.remove(sut, 0));
        Assert.assertArrayEquals("Nothing to remove", sut, ArrayUtils.remove(sut, 1));
    }

    @Test
    public void testRemoveOneByte() {
        byte[] sut = new byte[]{ 1, 2, 3 };
        byte[] sutWithout1 = new byte[]{ 2, 3 };
        byte[] sutWithout2 = new byte[]{ 1, 3 };
        byte[] sutWithout3 = new byte[]{ 1, 2 };
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MIN_VALUE));
        Assert.assertArrayEquals("Should not have deleted ", sut, ArrayUtils.remove(sut, (-1)));
        Assert.assertArrayEquals("Should have deleted first", sutWithout1, ArrayUtils.remove(sut, 0));
        Assert.assertArrayEquals("Should have deleted second", sutWithout2, ArrayUtils.remove(sut, 1));
        Assert.assertArrayEquals("Should have deleted third", sutWithout3, ArrayUtils.remove(sut, 2));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, 3));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MAX_VALUE));
    }

    @Test
    public void testRemoveOneByteFromSingleton() {
        byte[] sut = new byte[]{ 1 };
        byte[] sutWithout1 = new byte[]{  };
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MIN_VALUE));
        Assert.assertArrayEquals("Should not have deleted ", sut, ArrayUtils.remove(sut, (-1)));
        Assert.assertArrayEquals("Should have deleted first", sutWithout1, ArrayUtils.remove(sut, 0));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, 1));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MAX_VALUE));
    }

    @Test
    public void testRemoveOneByteFromEmpty() {
        byte[] sut = new byte[]{  };
        Assert.assertArrayEquals("Nothing to remove", sut, ArrayUtils.remove(sut, (-1)));
        Assert.assertArrayEquals("Nothing to remove", sut, ArrayUtils.remove(sut, 0));
        Assert.assertArrayEquals("Nothing to remove", sut, ArrayUtils.remove(sut, 1));
    }

    @Test
    public void testRemoveOneInt() {
        int[] sut = new int[]{ 1, 2, 3 };
        int[] sutWithout1 = new int[]{ 2, 3 };
        int[] sutWithout2 = new int[]{ 1, 3 };
        int[] sutWithout3 = new int[]{ 1, 2 };
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MIN_VALUE));
        Assert.assertArrayEquals("Should not have deleted ", sut, ArrayUtils.remove(sut, (-1)));
        Assert.assertArrayEquals("Should have deleted first", sutWithout1, ArrayUtils.remove(sut, 0));
        Assert.assertArrayEquals("Should have deleted second", sutWithout2, ArrayUtils.remove(sut, 1));
        Assert.assertArrayEquals("Should have deleted third", sutWithout3, ArrayUtils.remove(sut, 2));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, 3));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MAX_VALUE));
    }

    @Test
    public void testRemoveOneIntFromSingleton() {
        int[] sut = new int[]{ 1 };
        int[] sutWithout1 = new int[]{  };
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MIN_VALUE));
        Assert.assertArrayEquals("Should not have deleted ", sut, ArrayUtils.remove(sut, (-1)));
        Assert.assertArrayEquals("Should have deleted first", sutWithout1, ArrayUtils.remove(sut, 0));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, 1));
        Assert.assertArrayEquals("Should have not deleted", sut, ArrayUtils.remove(sut, Integer.MAX_VALUE));
    }

    @Test
    public void testRemoveOneIntFromEmpty() {
        int[] sut = new int[]{  };
        Assert.assertArrayEquals("Nothing to remove", sut, ArrayUtils.remove(sut, (-1)));
        Assert.assertArrayEquals("Nothing to remove", sut, ArrayUtils.remove(sut, 0));
        Assert.assertArrayEquals("Nothing to remove", sut, ArrayUtils.remove(sut, 1));
    }

    @Test
    public void testCountNonZeroes() {
        double[] empty = new double[]{  };
        Assert.assertEquals(0, ArrayUtils.countNonzeros(empty));
        double[] singlenz = new double[]{ 1.0 };
        Assert.assertEquals(1, ArrayUtils.countNonzeros(singlenz));
        double[] threeZeroes = new double[]{ 0.0, 0.0, 0.0 };
        Assert.assertEquals(0, ArrayUtils.countNonzeros(threeZeroes));
        double[] somenz = new double[]{ -1.0, Double.MIN_VALUE, 0.0, Double.MAX_VALUE, 0.001, 0.0, 42.0 };
        Assert.assertEquals(5, ArrayUtils.countNonzeros(somenz));
    }

    @Test
    public void testAddWithCoefficients() {
        float[] a = new float[]{ 1.0F, 2.0F, 3.0F };
        float[] b = new float[]{ 100.0F, 200.0F, 300.0F };
        float[] result = ArrayUtils.add(10.0F, a, 2.0F, b);
        Assert.assertTrue((result == a));
        Assert.assertArrayEquals(new float[]{ 210.0F, 420.0F, 630.0F }, a, 0.001F);
    }

    @Test
    public void test_encodeAsInt() {
        byte[] bs = new byte[]{ 0, 0, 0, 0, 1 };
        Assert.assertEquals(0, ArrayUtils.encodeAsInt(bs, 0));
        Assert.assertEquals(16777216, ArrayUtils.encodeAsInt(bs, 1));
        try {
            ArrayUtils.encodeAsInt(bs, 2);
            Assert.fail("Should not work");
        } catch (Throwable ignore) {
        }
        bs[0] = ((byte) (254));
        Assert.assertEquals(254, ArrayUtils.encodeAsInt(bs, 0));
        bs[1] = ((byte) (202));
        Assert.assertEquals(51966, ArrayUtils.encodeAsInt(bs, 0));
        bs[2] = ((byte) (222));
        Assert.assertEquals(14600958, ArrayUtils.encodeAsInt(bs, 0));
        bs[3] = ((byte) (10));
        Assert.assertEquals(182373118, ArrayUtils.encodeAsInt(bs, 0));
        Assert.assertEquals(17489610, ArrayUtils.encodeAsInt(bs, 1));
    }

    @Test
    public void test_decodeAsInt() {
        byte[] bs = new byte[]{ 1, 2, 3, 4, 5 };
        Assert.assertArrayEquals(new byte[]{ 0, 0, 0, 0, 5 }, ArrayUtils.decodeAsInt(0, bs, 0));
        try {
            ArrayUtils.decodeAsInt(1, bs, 3);
            Assert.fail("Should not work");
        } catch (Throwable ignore) {
        }
        try {
            ArrayUtils.decodeAsInt(256, bs, 4);
            Assert.fail("Should not work");
        } catch (Throwable ignore) {
        }
        Assert.assertArrayEquals(new byte[]{ ((byte) (254)), 0, 0, 0, 5 }, ArrayUtils.decodeAsInt(254, bs, 0));
        Assert.assertArrayEquals(new byte[]{ ((byte) (254)), ((byte) (202)), 0, 0, 5 }, ArrayUtils.decodeAsInt(51966, bs, 0));
        Assert.assertArrayEquals(new byte[]{ ((byte) (254)), ((byte) (202)), ((byte) (222)), 0, 5 }, ArrayUtils.decodeAsInt(14600958, bs, 0));
        Assert.assertArrayEquals(new byte[]{ ((byte) (254)), ((byte) (202)), ((byte) (222)), ((byte) (128)), 5 }, ArrayUtils.decodeAsInt(-2132882690, bs, 0));
    }

    @Test
    public void testFloatsToDouble() {
        Assert.assertNull(ArrayUtils.toDouble(((float[]) (null))));
        Assert.assertArrayEquals(new double[]{ 1.0, 2.2 }, ArrayUtils.toDouble(new float[]{ 1.0F, 2.2F }), 1.0E-7);
    }

    @Test
    public void testIntsToDouble() {
        Assert.assertNull(ArrayUtils.toDouble(((int[]) (null))));
        Assert.assertArrayEquals(new double[]{ 1.0, 42.0 }, ArrayUtils.toDouble(new int[]{ 1, 42 }), 0);
    }
}

