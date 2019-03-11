package com.fishercoder;


import _675.Solution1;
import com.fishercoder.common.utils.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _675Test {
    private static Solution1 solution1;

    private static List<List<Integer>> forest;

    @Test
    public void test1() {
        _675Test.forest = new ArrayList<>();
        _675Test.forest.add(Arrays.asList(1, 2, 3));
        _675Test.forest.add(Arrays.asList(0, 0, 4));
        _675Test.forest.add(Arrays.asList(7, 6, 5));
        Assert.assertEquals(6, _675Test.solution1.cutOffTree(_675Test.forest));
    }

    @Test
    public void test2() {
        _675Test.forest = new ArrayList<>();
        _675Test.forest.add(Arrays.asList(1, 2, 3));
        _675Test.forest.add(Arrays.asList(0, 0, 0));
        _675Test.forest.add(Arrays.asList(7, 6, 5));
        Assert.assertEquals((-1), _675Test.solution1.cutOffTree(_675Test.forest));
    }

    @Test
    public void test3() {
        _675Test.forest = new ArrayList<>();
        _675Test.forest.add(Arrays.asList(2, 3, 4));
        _675Test.forest.add(Arrays.asList(0, 0, 5));
        _675Test.forest.add(Arrays.asList(8, 7, 6));
        Assert.assertEquals(6, _675Test.solution1.cutOffTree(_675Test.forest));
    }

    @Test
    public void test4() {
        _675Test.forest = ArrayUtils.buildList(new int[][]{ new int[]{ 2, 3, 4, 9 }, new int[]{ 0, 0, 5, 10 }, new int[]{ 8, 7, 6, 12 } });
        Assert.assertEquals(13, _675Test.solution1.cutOffTree(_675Test.forest));
    }

    @Test
    public void test5() {
        _675Test.forest = ArrayUtils.buildList(new int[][]{ new int[]{ 0, 0, 0, 3528, 2256, 9394, 3153 }, new int[]{ 8740, 1758, 6319, 3400, 4502, 7475, 6812 }, new int[]{ 0, 0, 3079, 6312, 0, 0, 0 }, new int[]{ 6828, 0, 0, 0, 0, 0, 8145 }, new int[]{ 6964, 4631, 0, 0, 0, 4811, 0 }, new int[]{ 0, 0, 0, 0, 9734, 4696, 4246 }, new int[]{ 3413, 8887, 0, 4766, 0, 0, 0 }, new int[]{ 7739, 0, 0, 2920, 0, 5321, 2250 }, new int[]{ 3032, 0, 3015, 0, 3269, 8582, 0 } });
        Assert.assertEquals((-1), _675Test.solution1.cutOffTree(_675Test.forest));
    }

    @Test
    public void test6() {
        _675Test.forest = ArrayUtils.buildList(new int[][]{ new int[]{ 54581641, 64080174, 24346381, 69107959 }, new int[]{ 86374198, 61363882, 68783324, 79706116 }, new int[]{ 668150, 92178815, 89819108, 94701471 }, new int[]{ 83920491, 22724204, 46281641, 47531096 }, new int[]{ 89078499, 18904913, 25462145, 60813308 } });
        Assert.assertEquals(57, _675Test.solution1.cutOffTree(_675Test.forest));
    }
}

