package com.fishercoder;


import com.fishercoder.solutions._656;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/25/17.
 */
public class _656Test {
    private static _656 test;

    private static int[] A;

    private static List<Integer> expected;

    @Test
    public void test1() {
        _656Test.A = new int[]{ 1, 2, 4, -1, 2 };
        _656Test.expected = new ArrayList<>(Arrays.asList(1, 3, 5));
        Assert.assertEquals(_656Test.expected, _656Test.test.cheapestJump(_656Test.A, 2));
    }
}

