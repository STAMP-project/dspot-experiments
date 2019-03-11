package com.fishercoder;


import _604.StringIterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/10/17.
 */
public class _604Test {
    private static StringIterator test;

    @Test
    public void test1() {
        _604Test.test = new StringIterator("L1e2t1C1o1d1e1");
        System.out.println(_604Test.test.hasNext());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.hasNext());
    }

    @Test
    public void test2() {
        _604Test.test = new StringIterator("L10e2t1C1o1d1e11");
        System.out.println(_604Test.test.hasNext());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.hasNext());
    }

    @Test
    public void test3() {
        _604Test.test = new StringIterator("x6");
        System.out.println(_604Test.test.hasNext());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.hasNext());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.hasNext());
    }

    @Test
    public void test4() {
        _604Test.test = new StringIterator("X15D18V8");
        System.out.println(_604Test.test.hasNext());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        System.out.println(_604Test.test.next());
        Assert.assertEquals(true, _604Test.test.hasNext());
        System.out.println(_604Test.test.next());
        Assert.assertEquals(true, _604Test.test.hasNext());
    }
}

