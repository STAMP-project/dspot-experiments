package com.fishercoder;


import _79.Solution1;
import _79.Solution2;
import junit.framework.TestCase;
import org.junit.Test;


public class _79Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static char[][] board;

    @Test
    public void test1() {
        _79Test.board = new char[][]{ new char[]{ 'A', 'B', 'C', 'E' }, new char[]{ 'S', 'F', 'E', 'S' }, new char[]{ 'A', 'D', 'E', 'E' } };
        TestCase.assertEquals(true, _79Test.solution1.exist(_79Test.board, "ABCEFSADEESE"));
        TestCase.assertEquals(true, _79Test.solution2.exist(_79Test.board, "ABCEFSADEESE"));
    }

    @Test
    public void test2() {
        _79Test.board = new char[][]{ new char[]{ 'A', 'B', 'C', 'E' }, new char[]{ 'S', 'F', 'C', 'S' }, new char[]{ 'A', 'D', 'E', 'E' } };
        TestCase.assertEquals(true, _79Test.solution1.exist(_79Test.board, "ABCCED"));
        TestCase.assertEquals(true, _79Test.solution2.exist(_79Test.board, "ABCCED"));
        TestCase.assertEquals(true, _79Test.solution1.exist(_79Test.board, "SEE"));
        TestCase.assertEquals(true, _79Test.solution2.exist(_79Test.board, "SEE"));
        TestCase.assertEquals(false, _79Test.solution1.exist(_79Test.board, "ABCD"));
        TestCase.assertEquals(false, _79Test.solution2.exist(_79Test.board, "ABCD"));
    }

    @Test
    public void test3() {
        _79Test.board = new char[][]{ new char[]{ 'a' }, new char[]{ 'a' } };
        TestCase.assertEquals(false, _79Test.solution1.exist(_79Test.board, "aaa"));
        TestCase.assertEquals(false, _79Test.solution2.exist(_79Test.board, "aaa"));
    }
}

