package com.shekhargulati.leetcode.algorithms;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class Problem05Test {
    @Test
    public void shouldDetectLongestPalindrome() throws Exception {
        final String input = "saasdcjkbkjnjnnknvjfknjnfjkvnjkfdnvjknfdkvjnkjfdnvkjnvjknjkgnbjkngkjvnjkgnbvkjngfreyh67ujrtyhytju789oijtnuk789oikmtul0oymmmmmmmmmmmmmmmm";
        String l = Problem05.longestPalindrome(input);
        Assert.assertThat(l, CoreMatchers.equalTo("mmmmmmmmmmmmmmmm"));
    }

    @Test
    public void shouldFindLongestPalindromeString() throws Exception {
        final String input = "racecar";
        String s = Problem05.longestPalindrome1(input);
        Assert.assertThat(s, CoreMatchers.equalTo("racecar"));
    }

    @Test
    public void shouldFindLongestPalindromeString_2() throws Exception {
        final String input = "saas";
        String s = Problem05.longestPalindrome1(input);
        Assert.assertThat(s, CoreMatchers.equalTo("saas"));
    }

    @Test
    public void shouldFindLongestPalindromeString_3() throws Exception {
        final String input = "forgeeksskeegfor";
        String s = Problem05.longestPalindrome1(input);
        Assert.assertThat(s, CoreMatchers.equalTo("geeksskeeg"));
    }

    @Test
    public void shouldFindLongestPalindromeString_4() throws Exception {
        final String input = "abaccddccefe";
        String s = Problem05.longestPalindrome1(input);
        Assert.assertThat(s, CoreMatchers.equalTo("ccddcc"));
    }
}

