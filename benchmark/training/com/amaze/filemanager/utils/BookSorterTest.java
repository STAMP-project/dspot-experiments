package com.amaze.filemanager.utils;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * compare title's length and path's lengh. .. Ignore Case
 * two string array's first para : title, second para : path
 */
public class BookSorterTest {
    private BookSorter bookSorter = new BookSorter();

    /**
     * Purpose: when LHS title's length bigger than RHS title's length, result is positive
     * Input: compare(lhs,rhs) lhs title's length > rhs title's length
     * Expected:
     * return positive integer
     */
    @Test
    public void testCompareLHSTitleBigAndPathBigger() {
        String[] lhs = new String[]{ "abc1", "C:\\AmazeFileManager\\app\\abc1" };
        String[] rhs = new String[]{ "abc", "C:\\AmazeFileManager\\abc" };
        Assert.assertThat(bookSorter.compare(lhs, rhs), Matchers.greaterThan(0));
    }

    /**
     * Purpose: when LHS title's length smaller than RHS title's length, result is negative
     * Input: compare(lhs,rhs) lhs title's length < rhs title's length
     * Expected:
     * return negative integer
     */
    @Test
    public void testCompareRHSTitleBigAndPathBigger() {
        String[] lhs = new String[]{ "abc", "C:\\AmazeFileManager\\abc" };
        String[] rhs = new String[]{ "abc2", "C:\\AmazeFileManager\\app\\abc2" };
        Assert.assertThat(bookSorter.compare(lhs, rhs), Matchers.lessThan(0));
    }

    /**
     * Purpose: when LHS and RHS title's length are same but LHS path's length bigger than RHS path's length, , result is positive
     * Input: compare(lhs,rhs) lhs title's length = rhs title's length and lhs path's length > path title's length
     * Expected:
     * return positive integer
     */
    @Test
    public void testCompareTitleSameAndRHSPathBigger() {
        String[] lhs = new String[]{ "abc", "C:\\AmazeFileManager\\app\\abc" };
        String[] rhs = new String[]{ "abc", "C:\\AmazeFileManager\\abc" };
        Assert.assertThat(bookSorter.compare(lhs, rhs), Matchers.greaterThan(0));
    }

    /**
     * Purpose: when LHS and RHS title 's length are same but LHS path's length smaller than RHS path's length, result is negative
     * Input: compare(lhs,rhs) lhs title's length = rhs title's length and lhs path's length < path title's length
     * Expected:
     * return negative integer
     */
    @Test
    public void testCompareTitleSameAndLHSPathBigger() {
        String[] lhs = new String[]{ "abc", "C:\\AmazeFileManager\\abc" };
        String[] rhs = new String[]{ "abc", "C:\\AmazeFileManager\\app\\abc" };
        Assert.assertThat(bookSorter.compare(lhs, rhs), Matchers.lessThan(0));
    }

    /**
     * this case's expected real result is failure(same name can't exist)
     *
     * Purpose: when LHS and RHS title 's length are same, LHS and RHS path's length are same, result is zero
     * Input: compare(lhs,rhs) lhs title's length = rhs title's length and lhs path's length = path title's length
     * Expected:
     * return zero
     */
    @Test
    public void testCompareTitleSameAndPathSame() {
        String[] lhs = new String[]{ "abc", "C:\\AmazeFileManager\\abc" };
        String[] rhs = new String[]{ "abc", "C:\\AmazeFileManager\\abc" };
        Assert.assertEquals(bookSorter.compare(lhs, rhs), 0);
    }

    /**
     * Purpose: when LHS and RHS title 's length are same but Case difference,  LHS path's length bigger than RHS path's length, result is positive
     * Input: compare(lhs,rhs) lhs title's length = rhs title's length(but Case difference) and lhs path's length > path title's length
     * Expected:
     * return positive integer
     */
    @Test
    public void testCompareTitleNotSameCaseAndLHSPathBigger() {
        String[] lhs = new String[]{ "ABC", "C:\\AmazeFileManager\\app\\ABC" };
        String[] rhs = new String[]{ "abc", "C:\\AmazeFileManager\\abc" };
        Assert.assertThat(bookSorter.compare(lhs, rhs), Matchers.greaterThan(0));
    }

    /**
     * Purpose: when LHS and RHS title 's length are same but Case difference,  LHS path's length smaller than RHS path's length, result is negative
     * Input: compare(lhs,rhs) lhs title's length = rhs title's length(but Case difference) and lhs path's length < path title's length
     * Expected:
     * return negative integer
     */
    @Test
    public void testCompareTitleNotSameCaseAndRHSPathBigger() {
        String[] lhs = new String[]{ "ABC", "C:\\AmazeFileManager\\ABC" };
        String[] rhs = new String[]{ "abc", "C:\\AmazeFileManager\\app\\abc" };
        Assert.assertThat(bookSorter.compare(lhs, rhs), Matchers.lessThan(0));
    }

    /**
     * this case's expected real result is failure(same name can't exist)
     *
     * Purpose: when LHS and RHS title 's length are same but Case difference, LHS and RHS path's length are same, result is zero
     * Input: compare(lhs,rhs) lhs title's length = rhs title's length(but Case difference) and lhs path's length = path title's length
     * Expected:
     * return zero
     */
    @Test
    public void testCompareTitleNotSameCaseAndPathSame() {
        String[] lhs = new String[]{ "ABC", "C:\\AmazeFileManager\\ABC" };
        String[] rhs = new String[]{ "abc", "C:\\AmazeFileManager\\abc" };
        Assert.assertEquals(bookSorter.compare(lhs, rhs), 0);
    }
}

