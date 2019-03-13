package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class P06Test {
    @Test
    public void shouldReturnTrueWhenListIsPalindrome() throws Exception {
        Assert.assertTrue(P06.isPalindrome(Arrays.asList("x", "a", "m", "a", "x")));
    }

    @Test
    public void shouldReturnFalseWhenListIsNotPalindrome() throws Exception {
        Assert.assertFalse(P06.isPalindrome(Arrays.asList(1, 2, 3, 4, 5)));
    }

    @Test
    public void shouldReturnTrueWhenListIsPalindrome_IntStream() throws Exception {
        Assert.assertTrue(P06.isPalindrome(Arrays.asList("x", "a", "m", "a", "x")));
    }

    @Test
    public void shouldReturnFalseWhenListIsNotPalindrome_IntStream() throws Exception {
        Assert.assertFalse(P06.isPalindrome(Arrays.asList(1, 2, 3, 4, 5)));
    }
}

