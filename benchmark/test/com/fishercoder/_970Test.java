package com.fishercoder;


import _970.Solution1;
import _970.Solution2;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _970Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        Assert.assertEquals(Arrays.asList(2, 3, 4, 5, 7, 9, 10), _970Test.solution1.powerfulIntegers(2, 3, 10));
        Assert.assertEquals(Arrays.asList(2, 3, 4, 5, 7, 9, 10), _970Test.solution2.powerfulIntegers(2, 3, 10));
    }

    @Test
    public void test2() {
        Assert.assertEquals(Arrays.asList(2, 4, 6, 8, 10, 14), _970Test.solution1.powerfulIntegers(3, 5, 15));
        Assert.assertEquals(Arrays.asList(2, 4, 6, 8, 10, 14), _970Test.solution2.powerfulIntegers(3, 5, 15));
    }

    @Test
    public void test3() {
        Assert.assertEquals(Arrays.asList(2, 3, 5, 7, 8, 9, 10), _970Test.solution1.powerfulIntegers(2, 6, 12));
        Assert.assertEquals(Arrays.asList(2, 3, 5, 7, 8, 9, 10), _970Test.solution2.powerfulIntegers(2, 6, 12));
    }

    @Test
    public void test4() {
        Assert.assertEquals(Arrays.asList(2, 3, 5, 9, 10, 11), _970Test.solution1.powerfulIntegers(2, 9, 12));
        Assert.assertEquals(Arrays.asList(2, 3, 5, 9, 10, 11), _970Test.solution2.powerfulIntegers(2, 9, 12));
    }

    @Test
    public void test5() {
        Assert.assertEquals(Arrays.asList(2, 91, 180, 8101, 8190, 16200, 729001, 729090, 737100), _970Test.solution1.powerfulIntegers(90, 90, 1000000));
        List<Integer> actual = _970Test.solution2.powerfulIntegers(90, 90, 1000000);
        Collections.sort(actual);
        Assert.assertEquals(Arrays.asList(2, 91, 180, 8101, 8190, 16200, 729001, 729090, 737100), actual);
    }
}

