package com.bluelinelabs.conductor;


import com.bluelinelabs.conductor.util.TestController;
import org.junit.Assert;
import org.junit.Test;


public class BackstackTests {
    private Backstack backstack;

    @Test
    public void testPush() {
        Assert.assertEquals(0, backstack.size());
        backstack.push(RouterTransaction.with(new TestController()));
        Assert.assertEquals(1, backstack.size());
    }

    @Test
    public void testPop() {
        backstack.push(RouterTransaction.with(new TestController()));
        backstack.push(RouterTransaction.with(new TestController()));
        Assert.assertEquals(2, backstack.size());
        backstack.pop();
        Assert.assertEquals(1, backstack.size());
        backstack.pop();
        Assert.assertEquals(0, backstack.size());
    }

    @Test
    public void testPeek() {
        RouterTransaction transaction1 = RouterTransaction.with(new TestController());
        RouterTransaction transaction2 = RouterTransaction.with(new TestController());
        backstack.push(transaction1);
        Assert.assertEquals(transaction1, backstack.peek());
        backstack.push(transaction2);
        Assert.assertEquals(transaction2, backstack.peek());
        backstack.pop();
        Assert.assertEquals(transaction1, backstack.peek());
    }

    @Test
    public void testPopTo() {
        RouterTransaction transaction1 = RouterTransaction.with(new TestController());
        RouterTransaction transaction2 = RouterTransaction.with(new TestController());
        RouterTransaction transaction3 = RouterTransaction.with(new TestController());
        backstack.push(transaction1);
        backstack.push(transaction2);
        backstack.push(transaction3);
        Assert.assertEquals(3, backstack.size());
        backstack.popTo(transaction1);
        Assert.assertEquals(1, backstack.size());
        Assert.assertEquals(transaction1, backstack.peek());
    }
}

