package com.fishercoder;


import _388.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _388Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(10, _388Test.solution1.lengthLongestPath("dir\\n\\tsubdir1\\n\\t\\tfile1.ext\\n\\t\\tsubsubdir1\\n\\tsubdir2\\n\\t\\tsubsubdir2\\n\\t\\t\\tfile2.ext"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(9, _388Test.solution1.lengthLongestPath("dir\\n\\tsubdir1\\n\\tsubdir2\\n\\t\\tfile.ext"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(7, _388Test.solution1.lengthLongestPath("aaaaaaaaaaaaaaaaaaaaa/sth.png"));
    }

    @Test
    public void test4() {
        Assert.assertEquals(9, _388Test.solution1.lengthLongestPath("a/aa/aaa/file1.txt"));
    }

    @Test
    public void test5() {
        Assert.assertEquals(25, _388Test.solution1.lengthLongestPath("file name with  space.txt"));
    }

    @Test
    public void test6() {
        Assert.assertEquals(13, _388Test.solution1.lengthLongestPath("dir\\n    file.txt"));
    }

    @Test
    public void test7() {
        Assert.assertEquals(12, _388Test.solution1.lengthLongestPath("dir\n    file.txt"));
    }

    @Test
    public void test8() {
        Assert.assertEquals(7, _388Test.solution1.lengthLongestPath("a\\n\\tb1\\n\\t\\tf1.txt\\n\\taaaaa\\n\\t\\tf2.txt"));
    }
}

