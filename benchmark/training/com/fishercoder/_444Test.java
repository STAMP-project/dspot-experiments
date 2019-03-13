package com.fishercoder;


import com.fishercoder.solutions._444;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/3/17.
 */
public class _444Test {
    private static _444 test;

    private static int[] org;

    private static List<List<Integer>> seqs;

    @Test
    public void test1() {
        _444Test.org = new int[]{ 1, 2, 3 };
        _444Test.seqs = new ArrayList<>();
        _444Test.seqs.add(Arrays.asList(1, 2));
        _444Test.seqs.add(Arrays.asList(1, 3));
        Assert.assertEquals(false, _444Test.test.sequenceReconstruction(_444Test.org, _444Test.seqs));
    }
}

