package com.shekhargulati.tadm.ch03;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class Problem3_1Test {
    private final Problem3_1 checker = new Problem3_1();

    @Test
    public void shouldSayTrueForEmptyString() throws Exception {
        final String input = "";
        boolean balanced = checker.isBalancedString(input);
        Assert.assertTrue(balanced);
    }

    @Test
    public void shouldSayTrueWhenStringIsBalanced() throws Exception {
        final String input = "((())())()";
        boolean balanced = checker.isBalancedString(input);
        Assert.assertTrue(balanced);
    }

    @Test
    public void shouldSayFalseWhenStringIsNotBalanced() throws Exception {
        final String input = ")()(";
        boolean balanced = checker.isBalancedString(input);
        Assert.assertFalse(balanced);
    }

    @Test
    public void shouldSayFalseWhenStringIsNotBalanced_anotherInput() throws Exception {
        final String input = "())";
        boolean balanced = checker.isBalancedString(input);
        Assert.assertFalse(balanced);
    }

    @Test
    public void shouldFindPositionOfFirstOffendingParenthesis() throws Exception {
        final String input = ")()(";
        int position = checker.firstOffendingParenthesis(input);
        Assert.assertThat(position, CoreMatchers.equalTo(0));
    }

    @Test
    public void shouldFindPositionOfFirstOffendingParenthesis_input2() throws Exception {
        final String input = "(())(";
        int position = checker.firstOffendingParenthesis(input);
        Assert.assertThat(position, CoreMatchers.equalTo(4));
    }

    @Test
    public void shouldFindPositionOfFirstOffendingParenthesis_input3() throws Exception {
        final String input = "())";
        int position = checker.firstOffendingParenthesis(input);
        Assert.assertThat(position, CoreMatchers.equalTo(2));
    }
}

