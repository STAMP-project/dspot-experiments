package com.baeldung.commons.lang3.test;


import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;


public class ArrayUtilsUnitTest {
    @Test
    public void givenArrayUtilsClass_whenCalledtoString_thenCorrect() {
        String[] array = new String[]{ "a", "b", "c" };
        assertThat(ArrayUtils.toString(array)).isEqualTo("{a,b,c}");
    }

    @Test
    public void givenArrayUtilsClass_whenCalledtoStringIfArrayisNull_thenCorrect() {
        String[] array = null;
        assertThat(ArrayUtils.toString(array, "Array is null")).isEqualTo("Array is null");
    }

    @Test
    public void givenArrayUtilsClass_whenCalledhashCode_thenCorrect() {
        String[] array = new String[]{ "a", "b", "c" };
        assertThat(ArrayUtils.hashCode(array)).isEqualTo(997619);
    }

    @Test
    public void givenArrayUtilsClass_whenCalledtoMap_thenCorrect() {
        String[][] array = new String[][]{ new String[]{ "1", "one" }, new String[]{ "2", "two" }, new String[]{ "3", "three" } };
        Map map = new HashMap();
        map.put("1", "one");
        map.put("2", "two");
        map.put("3", "three");
        assertThat(ArrayUtils.toMap(array)).isEqualTo(map);
    }

    @Test
    public void givenArrayUtilsClass_whenCallednullToEmptyStringArray_thenCorrect() {
        String[] array = null;
        assertThat(ArrayUtils.nullToEmpty(array)).isEmpty();
    }

    @Test
    public void givenArrayUtilsClass_whenCallednullToEmptyObjectArray_thenCorrect() {
        Object[] array = null;
        assertThat(ArrayUtils.nullToEmpty(array)).isEmpty();
    }

    @Test
    public void givenArrayUtilsClass_whenCalledsubarray_thenCorrect() {
        int[] array = new int[]{ 1, 2, 3 };
        int[] expected = new int[]{ 1 };
        assertThat(ArrayUtils.subarray(array, 0, 1)).isEqualTo(expected);
    }

    @Test
    public void givenArrayUtilsClass_whenCalledisSameLength_thenCorrect() {
        int[] array1 = new int[]{ 1, 2, 3 };
        int[] array2 = new int[]{ 1, 2, 3 };
        assertThat(ArrayUtils.isSameLength(array1, array2)).isTrue();
    }

    @Test
    public void givenArrayUtilsClass_whenCalledreverse_thenCorrect() {
        int[] array1 = new int[]{ 1, 2, 3 };
        int[] array2 = new int[]{ 3, 2, 1 };
        ArrayUtils.reverse(array1);
        assertThat(array1).isEqualTo(array2);
    }

    @Test
    public void givenArrayUtilsClass_whenCalledIndexOf_thenCorrect() {
        int[] array = new int[]{ 1, 2, 3 };
        assertThat(ArrayUtils.indexOf(array, 1, 0)).isEqualTo(0);
    }

    @Test
    public void givenArrayUtilsClass_whenCalledcontains_thenCorrect() {
        int[] array = new int[]{ 1, 2, 3 };
        assertThat(ArrayUtils.contains(array, 1)).isTrue();
    }
}

