package cn.hutool.core.convert;


import cn.hutool.core.convert.impl.ArrayConverter;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????????<br>
 * ?????
 *
 * @author Looly
 */
public class ConvertToArrayTest {
    @Test
    public void toIntArrayTest() {
        String[] b = new String[]{ "1", "2", "3", "4" };
        Integer[] integerArray = Convert.toIntArray(b);
        Assert.assertArrayEquals(integerArray, new Integer[]{ 1, 2, 3, 4 });
        int[] intArray = Convert.convert(int[].class, b);
        Assert.assertArrayEquals(intArray, new int[]{ 1, 2, 3, 4 });
        long[] c = new long[]{ 1, 2, 3, 4, 5 };
        Integer[] intArray2 = Convert.toIntArray(c);
        Assert.assertArrayEquals(intArray2, new Integer[]{ 1, 2, 3, 4, 5 });
    }

    @Test
    public void toLongArrayTest() {
        String[] b = new String[]{ "1", "2", "3", "4" };
        Long[] longArray = Convert.toLongArray(b);
        Assert.assertArrayEquals(longArray, new Long[]{ 1L, 2L, 3L, 4L });
        long[] longArray2 = Convert.convert(long[].class, b);
        Assert.assertArrayEquals(longArray2, new long[]{ 1L, 2L, 3L, 4L });
        int[] c = new int[]{ 1, 2, 3, 4, 5 };
        Long[] intArray2 = Convert.toLongArray(c);
        Assert.assertArrayEquals(intArray2, new Long[]{ 1L, 2L, 3L, 4L, 5L });
    }

    @Test
    public void toDoubleArrayTest() {
        String[] b = new String[]{ "1", "2", "3", "4" };
        Double[] doubleArray = Convert.toDoubleArray(b);
        Assert.assertArrayEquals(doubleArray, new Double[]{ 1.0, 2.0, 3.0, 4.0 });
        double[] doubleArray2 = Convert.convert(double[].class, b);
        Assert.assertArrayEquals(doubleArray2, new double[]{ 1.0, 2.0, 3.0, 4.0 }, 2);
        int[] c = new int[]{ 1, 2, 3, 4, 5 };
        Double[] intArray2 = Convert.toDoubleArray(c);
        Assert.assertArrayEquals(intArray2, new Double[]{ 1.0, 2.0, 3.0, 4.0, 5.0 });
    }

    @Test
    public void toPrimitiveArrayTest() {
        // ???????
        int[] a = new int[]{ 1, 2, 3, 4 };
        long[] result = ConverterRegistry.getInstance().convert(long[].class, a);
        Assert.assertArrayEquals(new long[]{ 1L, 2L, 3L, 4L }, result);
        // ???????
        byte[] resultBytes = ConverterRegistry.getInstance().convert(byte[].class, a);
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 4 }, resultBytes);
        // ??????
        String arrayStr = "1,2,3,4,5";
        // ??Converter????2????????Converter??
        ArrayConverter c3 = new ArrayConverter(int[].class);
        int[] result3 = ((int[]) (c3.convert(arrayStr, null)));
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, result3);
    }

    @Test
    public void collectionToArrayTest() {
        ArrayList<Object> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        String[] result = Convert.toStrArray(list);
        Assert.assertEquals(list.get(0), result[0]);
        Assert.assertEquals(list.get(1), result[1]);
        Assert.assertEquals(list.get(2), result[2]);
    }

    @Test
    public void strToCharArrayTest() {
        String testStr = "abcde";
        Character[] array = Convert.toCharArray(testStr);
        // ??????
        Assert.assertEquals(new Character('a'), array[0]);
        Assert.assertEquals(new Character('b'), array[1]);
        Assert.assertEquals(new Character('c'), array[2]);
        Assert.assertEquals(new Character('d'), array[3]);
        Assert.assertEquals(new Character('e'), array[4]);
        // ??????
        char[] array2 = Convert.convert(char[].class, testStr);
        Assert.assertEquals('a', array2[0]);
        Assert.assertEquals('b', array2[1]);
        Assert.assertEquals('c', array2[2]);
        Assert.assertEquals('d', array2[3]);
        Assert.assertEquals('e', array2[4]);
    }
}

