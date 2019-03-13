package com.shekhargulati.leetcode;


import com.shekhargulati.leetcode.algorithms.Problem03;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class Problem03Test {
    @Test
    public void shouldFindLongestSubstring() throws Exception {
        final String input = "abcabc";
        String substring = Problem03.substring(input);
        Assert.assertThat(substring, CoreMatchers.equalTo("abc"));
    }

    @Test
    public void shouldFindLongestSubstringIn_bbbbb() throws Exception {
        final String input = "bbbb";
        String substring = Problem03.substring(input);
        Assert.assertThat(substring, CoreMatchers.equalTo("b"));
    }
}

