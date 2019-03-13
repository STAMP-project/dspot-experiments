package com.fishercoder;


import _716.Solution1.MaxStack;
import org.junit.Assert;
import org.junit.Test;


public class _716Test {
    private static MaxStack maxStackSolution1;

    private static _716.Solution2.MaxStack maxStackSolution2;

    @Test
    public void test1() {
        _716Test.maxStackSolution1.push(5);
        Assert.assertEquals(5, _716Test.maxStackSolution1.peekMax());
        Assert.assertEquals(5, _716Test.maxStackSolution1.popMax());
    }

    @Test
    public void test2() {
        _716Test.maxStackSolution1.push(5);
        _716Test.maxStackSolution1.push(1);
        Assert.assertEquals(5, _716Test.maxStackSolution1.popMax());
        Assert.assertEquals(1, _716Test.maxStackSolution1.peekMax());
    }

    @Test
    public void test3() {
        _716Test.maxStackSolution1.push(74);
        Assert.assertEquals(74, _716Test.maxStackSolution1.popMax());
        _716Test.maxStackSolution1.push(89);
        _716Test.maxStackSolution1.push(67);
        Assert.assertEquals(89, _716Test.maxStackSolution1.popMax());
        Assert.assertEquals(67, _716Test.maxStackSolution1.pop());
        _716Test.maxStackSolution1.push(61);
        _716Test.maxStackSolution1.push((-77));
        Assert.assertEquals(61, _716Test.maxStackSolution1.peekMax());
        Assert.assertEquals(61, _716Test.maxStackSolution1.popMax());
        _716Test.maxStackSolution1.push(81);
        Assert.assertEquals(81, _716Test.maxStackSolution1.peekMax());
        Assert.assertEquals(81, _716Test.maxStackSolution1.popMax());
        _716Test.maxStackSolution1.push(81);
        Assert.assertEquals(81, _716Test.maxStackSolution1.pop());
        _716Test.maxStackSolution1.push((-71));
        _716Test.maxStackSolution1.push(32);
    }

    @Test
    public void test4() {
        _716Test.maxStackSolution2.push(5);
        Assert.assertEquals(5, _716Test.maxStackSolution2.peekMax());
        Assert.assertEquals(5, _716Test.maxStackSolution2.popMax());
    }

    @Test
    public void test5() {
        _716Test.maxStackSolution2.push(5);
        _716Test.maxStackSolution2.push(1);
        Assert.assertEquals(5, _716Test.maxStackSolution2.popMax());
        Assert.assertEquals(1, _716Test.maxStackSolution2.peekMax());
    }

    @Test
    public void test6() {
        _716Test.maxStackSolution2.push(74);
        Assert.assertEquals(74, _716Test.maxStackSolution2.popMax());
        _716Test.maxStackSolution2.push(89);
        _716Test.maxStackSolution2.push(67);
        Assert.assertEquals(89, _716Test.maxStackSolution2.popMax());
        Assert.assertEquals(67, _716Test.maxStackSolution2.pop());
        _716Test.maxStackSolution2.push(61);
        _716Test.maxStackSolution2.push((-77));
        Assert.assertEquals(61, _716Test.maxStackSolution2.peekMax());
        Assert.assertEquals(61, _716Test.maxStackSolution2.popMax());
        _716Test.maxStackSolution2.push(81);
        Assert.assertEquals(81, _716Test.maxStackSolution2.peekMax());
        Assert.assertEquals(81, _716Test.maxStackSolution2.popMax());
        _716Test.maxStackSolution2.push(81);
        Assert.assertEquals(81, _716Test.maxStackSolution2.pop());
        _716Test.maxStackSolution2.push((-71));
        _716Test.maxStackSolution2.push(32);
    }
}

