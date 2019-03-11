package com.fishercoder;


import _36.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _36Test {
    private static Solution1 solution1;

    private static char[][] board;

    @Test
    public void test1() {
        _36Test.board = new char[][]{ new char[]{ '4', '3', '5', '2', '6', '9', '7', '8', '1' }, new char[]{ '6', '8', '2', '5', '7', '1', '4', '9', '3' }, new char[]{ '1', '9', '7', '8', '3', '4', '5', '6', '2' }, new char[]{ '8', '2', '6', '1', '9', '5', '3', '4', '7' }, new char[]{ '3', '7', '4', '6', '8', '2', '9', '1', '5' }, new char[]{ '9', '5', '1', '7', '4', '3', '6', '2', '8' }, new char[]{ '5', '1', '9', '3', '2', '6', '8', '7', '4' }, new char[]{ '2', '4', '8', '9', '5', '7', '1', '3', '6' }, new char[]{ '7', '6', '3', '4', '1', '8', '2', '5', '9' } };
        TestCase.assertEquals(true, _36Test.solution1.isValidSudoku(_36Test.board));
    }

    @Test
    public void test2() {
        _36Test.board = new char[][]{ new char[]{ '.', '8', '7', '6', '5', '4', '3', '2', '1' }, new char[]{ '2', '.', '.', '.', '.', '.', '.', '.', '.' }, new char[]{ '3', '.', '.', '.', '.', '.', '.', '.', '.' }, new char[]{ '4', '.', '.', '.', '.', '.', '.', '.', '.' }, new char[]{ '5', '.', '.', '.', '.', '.', '.', '.', '.' }, new char[]{ '6', '.', '.', '.', '.', '.', '.', '.', '.' }, new char[]{ '7', '.', '.', '.', '.', '.', '.', '.', '.' }, new char[]{ '8', '.', '.', '.', '.', '.', '.', '.', '.' }, new char[]{ '9', '.', '.', '.', '.', '.', '.', '.', '.' } };
        TestCase.assertEquals(true, _36Test.solution1.isValidSudoku(_36Test.board));
    }

    @Test
    public void test3() {
        _36Test.board = new char[][]{ new char[]{ '.', '.', '.', '.', '5', '.', '.', '1', '.' }, // this upper right corner 3*3 square is invalid, '1' appears twice
        new char[]{ '.', '4', '.', '3', '.', '.', '.', '.', '.' }, new char[]{ '.', '.', '.', '.', '.', '3', '.', '.', '1' }, new char[]{ '8', '.', '.', '.', '.', '.', '.', '2', '.' }, new char[]{ '.', '.', '2', '.', '7', '.', '.', '.', '.' }, new char[]{ '.', '1', '5', '.', '.', '.', '.', '.', '.' }, new char[]{ '.', '.', '.', '.', '.', '2', '.', '.', '.' }, new char[]{ '.', '2', '.', '9', '.', '.', '.', '.', '.' }, new char[]{ '.', '.', '4', '.', '.', '.', '.', '.', '.' } };
        TestCase.assertEquals(false, _36Test.solution1.isValidSudoku(_36Test.board));
    }

    @Test
    public void test4() {
        _36Test.board = new char[][]{ new char[]{ '.', '.', '4', '.', '.', '.', '6', '3', '.' }, new char[]{ '.', '.', '.', '.', '.', '.', '.', '.', '.' }, new char[]{ '5', '.', '.', '.', '.', '.', '.', '9', '.' }, new char[]{ '.', '.', '.', '5', '6', '.', '.', '.', '.' }, new char[]{ '4', '.', '3', '.', '.', '.', '.', '.', '1' }, new char[]{ '.', '.', '.', '7', '.', '.', '.', '.', '.' }, new char[]{ '.', '.', '.', '5', '.', '.', '.', '.', '.' }, new char[]{ '.', '.', '.', '.', '.', '.', '.', '.', '.' }, new char[]{ '.', '.', '.', '.', '.', '.', '.', '.', '.' } };
        TestCase.assertEquals(false, _36Test.solution1.isValidSudoku(_36Test.board));
    }
}

