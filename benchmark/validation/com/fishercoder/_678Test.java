package com.fishercoder;


import _678.Solution1;
import _678.Solution2;
import _678.Solution3;
import org.junit.Assert;
import org.junit.Test;


public class _678Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    @Test
    public void test1() {
        Assert.assertEquals(true, _678Test.solution1.checkValidString("()"));
        Assert.assertEquals(true, _678Test.solution2.checkValidString("()"));
        Assert.assertEquals(true, _678Test.solution3.checkValidString("()"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(true, _678Test.solution1.checkValidString("(*)"));
        Assert.assertEquals(true, _678Test.solution2.checkValidString("(*)"));
        Assert.assertEquals(true, _678Test.solution3.checkValidString("(*)"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(true, _678Test.solution1.checkValidString("(*))"));
        Assert.assertEquals(true, _678Test.solution2.checkValidString("(*))"));
        Assert.assertEquals(true, _678Test.solution3.checkValidString("(*))"));
    }

    @Test
    public void test4() {
        Assert.assertEquals(false, _678Test.solution1.checkValidString("(((()))())))*))())()(**(((())(()(*()((((())))*())(())*(*(()(*)))()*())**((()(()))())(*(*))*))())"));
        Assert.assertEquals(false, _678Test.solution2.checkValidString("(((()))())))*))())()(**(((())(()(*()((((())))*())(())*(*(()(*)))()*())**((()(()))())(*(*))*))())"));
        Assert.assertEquals(false, _678Test.solution3.checkValidString("(((()))())))*))())()(**(((())(()(*()((((())))*())(())*(*(()(*)))()*())**((()(()))())(*(*))*))())"));
    }

    @Test
    public void test5() {
        Assert.assertEquals(true, _678Test.solution1.checkValidString("(((******)))"));
        Assert.assertEquals(true, _678Test.solution2.checkValidString("(((******)))"));
        Assert.assertEquals(true, _678Test.solution3.checkValidString("(((******)))"));
    }
}

