package com.shekhargulati.ninetynine_problems._03_logic_and_codes;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P49Test {
    @Test
    public void shouldFindGrayCodeWhenNIs1() throws Exception {
        List<String> graySequence = P49.gray(1);
        Assert.assertThat(graySequence, contains("0", "1"));
    }

    @Test
    public void shouldFindGrayCodeWhenNIs2() throws Exception {
        List<String> graySequence = P49.gray(2);
        Assert.assertThat(graySequence, contains("00", "01", "11", "10"));
    }

    @Test
    public void shouldFindGrayCodeWhenNIs3() throws Exception {
        List<String> graySequence = P49.gray(3);
        Assert.assertThat(graySequence, contains("000", "001", "011", "010", "110", "111", "101", "100"));
    }
}

