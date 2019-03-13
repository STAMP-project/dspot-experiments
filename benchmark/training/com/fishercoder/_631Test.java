package com.fishercoder;


import _631.Excel;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Created by stevesun on 7/26/17.
 */
public class _631Test {
    private static Excel excel;

    @Test
    public void test1() {
        _631Test.excel = new Excel(3, 'C');
        TestCase.assertEquals(0, _631Test.excel.get(1, 'A'));
        _631Test.excel.set(1, 'A', 1);
        TestCase.assertEquals(1, _631Test.excel.get(1, 'A'));
    }

    @Test
    public void test2() {
        _631Test.excel = new Excel(3, 'C');
        TestCase.assertEquals(0, _631Test.excel.sum(1, 'A', new String[]{ "A2" }));
        _631Test.excel.set(2, 'A', 1);
        TestCase.assertEquals(1, _631Test.excel.get(1, 'A'));
    }
}

