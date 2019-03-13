package cn.hutool.core.util;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link ArrayUtil} ????????
 *
 * @author Looly
 */
public class ArrayUtilTest {
    @Test
    public void isEmptyTest() {
        int[] a = new int[]{  };
        Assert.assertTrue(ArrayUtil.isEmpty(a));
        Assert.assertTrue(ArrayUtil.isEmpty(((Object) (a))));
        int[] b = null;
        Assert.assertTrue(ArrayUtil.isEmpty(b));
        Object c = null;
        Assert.assertTrue(ArrayUtil.isEmpty(c));
    }

    @Test
    public void isNotEmptyTest() {
        int[] a = new int[]{ 1, 2 };
        Assert.assertTrue(ArrayUtil.isNotEmpty(a));
    }

    @Test
    public void newArrayTest() {
        String[] newArray = ArrayUtil.newArray(String.class, 3);
        Assert.assertEquals(3, newArray.length);
    }

    @Test
    public void cloneTest() {
        Integer[] b = new Integer[]{ 1, 2, 3 };
        Integer[] cloneB = ArrayUtil.clone(b);
        Assert.assertArrayEquals(b, cloneB);
        int[] a = new int[]{ 1, 2, 3 };
        int[] clone = ArrayUtil.clone(a);
        Assert.assertArrayEquals(a, clone);
    }

    @Test
    public void filterTest() {
        Integer[] a = new Integer[]{ 1, 2, 3, 4, 5, 6 };
        Integer[] filter = ArrayUtil.filter(a, new cn.hutool.core.lang.Editor<Integer>() {
            @Override
            public Integer edit(Integer t) {
                return (t % 2) == 0 ? t : null;
            }
        });
        Assert.assertArrayEquals(filter, new Integer[]{ 2, 4, 6 });
    }

    @Test
    public void filterTestForFilter() {
        Integer[] a = new Integer[]{ 1, 2, 3, 4, 5, 6 };
        Integer[] filter = ArrayUtil.filter(a, new cn.hutool.core.lang.Filter<Integer>() {
            @Override
            public boolean accept(Integer t) {
                return (t % 2) == 0;
            }
        });
        Assert.assertArrayEquals(filter, new Integer[]{ 2, 4, 6 });
    }

    @Test
    public void filterTestForEditor() {
        Integer[] a = new Integer[]{ 1, 2, 3, 4, 5, 6 };
        Integer[] filter = ArrayUtil.filter(a, new cn.hutool.core.lang.Editor<Integer>() {
            @Override
            public Integer edit(Integer t) {
                return (t % 2) == 0 ? t * 10 : t;
            }
        });
        Assert.assertArrayEquals(filter, new Integer[]{ 1, 20, 3, 40, 5, 60 });
    }

    @Test
    public void indexOfTest() {
        Integer[] a = new Integer[]{ 1, 2, 3, 4, 5, 6 };
        int index = ArrayUtil.indexOf(a, 3);
        Assert.assertEquals(2, index);
        long[] b = new long[]{ 1, 2, 3, 4, 5, 6 };
        int index2 = ArrayUtil.indexOf(b, 3);
        Assert.assertEquals(2, index2);
    }

    @Test
    public void lastIndexOfTest() {
        Integer[] a = new Integer[]{ 1, 2, 3, 4, 3, 6 };
        int index = ArrayUtil.lastIndexOf(a, 3);
        Assert.assertEquals(4, index);
        long[] b = new long[]{ 1, 2, 3, 4, 3, 6 };
        int index2 = ArrayUtil.lastIndexOf(b, 3);
        Assert.assertEquals(4, index2);
    }

    @Test
    public void containsTest() {
        Integer[] a = new Integer[]{ 1, 2, 3, 4, 3, 6 };
        boolean contains = ArrayUtil.contains(a, 3);
        Assert.assertTrue(contains);
        long[] b = new long[]{ 1, 2, 3, 4, 3, 6 };
        boolean contains2 = ArrayUtil.contains(b, 3);
        Assert.assertTrue(contains2);
    }

    @Test
    public void mapTest() {
        String[] keys = new String[]{ "a", "b", "c" };
        Integer[] values = new Integer[]{ 1, 2, 3 };
        Map<String, Integer> map = ArrayUtil.zip(keys, values, true);
        Assert.assertEquals(map.toString(), "{a=1, b=2, c=3}");
    }

    @Test
    public void castTest() {
        Object[] values = new Object[]{ "1", "2", "3" };
        String[] cast = ((String[]) (ArrayUtil.cast(String.class, values)));
        Assert.assertEquals(values[0], cast[0]);
        Assert.assertEquals(values[1], cast[1]);
        Assert.assertEquals(values[2], cast[2]);
    }

    @Test
    public void rangeTest() {
        int[] range = ArrayUtil.range(0, 10);
        Assert.assertEquals(0, range[0]);
        Assert.assertEquals(1, range[1]);
        Assert.assertEquals(2, range[2]);
        Assert.assertEquals(3, range[3]);
        Assert.assertEquals(4, range[4]);
        Assert.assertEquals(5, range[5]);
        Assert.assertEquals(6, range[6]);
        Assert.assertEquals(7, range[7]);
        Assert.assertEquals(8, range[8]);
        Assert.assertEquals(9, range[9]);
    }

    @Test
    public void maxTest() {
        int max = ArrayUtil.max(1, 2, 13, 4, 5);
        Assert.assertEquals(13, max);
        long maxLong = ArrayUtil.max(1L, 2L, 13L, 4L, 5L);
        Assert.assertEquals(13, maxLong);
        double maxDouble = ArrayUtil.max(1.0, 2.4, 13.0, 4.55, 5.0);
        Assert.assertEquals(13.0, maxDouble, 2);
    }

    @Test
    public void minTest() {
        int min = ArrayUtil.min(1, 2, 13, 4, 5);
        Assert.assertEquals(1, min);
        long minLong = ArrayUtil.min(1L, 2L, 13L, 4L, 5L);
        Assert.assertEquals(1, minLong);
        double minDouble = ArrayUtil.min(1.0, 2.4, 13.0, 4.55, 5.0);
        Assert.assertEquals(1.0, minDouble, 2);
    }

    @Test
    public void appendTest() {
        String[] a = new String[]{ "1", "2", "3", "4" };
        String[] b = new String[]{ "a", "b", "c" };
        String[] result = ArrayUtil.append(a, b);
        Assert.assertArrayEquals(new String[]{ "1", "2", "3", "4", "a", "b", "c" }, result);
    }

    @Test
    public void insertTest() {
        String[] a = new String[]{ "1", "2", "3", "4" };
        String[] b = new String[]{ "a", "b", "c" };
        // ?-1?????????3????
        String[] result = ArrayUtil.insert(a, (-1), b);
        Assert.assertArrayEquals(new String[]{ "1", "2", "3", "a", "b", "c", "4" }, result);
        // ??0?????????????
        result = ArrayUtil.insert(a, 0, b);
        Assert.assertArrayEquals(new String[]{ "a", "b", "c", "1", "2", "3", "4" }, result);
        // ??2???????"3"??
        result = ArrayUtil.insert(a, 2, b);
        Assert.assertArrayEquals(new String[]{ "1", "2", "a", "b", "c", "3", "4" }, result);
        // ??4???????"4"????????
        result = ArrayUtil.insert(a, 4, b);
        Assert.assertArrayEquals(new String[]{ "1", "2", "3", "4", "a", "b", "c" }, result);
        // ??5?????????????4????null
        result = ArrayUtil.insert(a, 5, b);
        Assert.assertArrayEquals(new String[]{ "1", "2", "3", "4", null, "a", "b", "c" }, result);
    }

    @Test
    public void joinTest() {
        String[] array = new String[]{ "aa", "bb", "cc", "dd" };
        String join = ArrayUtil.join(array, ",", "[", "]");
        Assert.assertEquals("[aa],[bb],[cc],[dd]", join);
    }

    @Test
    public void getArrayTypeTest() {
        Class<?> arrayType = ArrayUtil.getArrayType(int.class);
        Assert.assertEquals(int[].class, arrayType);
        arrayType = ArrayUtil.getArrayType(String.class);
        Assert.assertEquals(String[].class, arrayType);
    }
}

