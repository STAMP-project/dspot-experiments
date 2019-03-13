package org.web3j.utils;


import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StringsTest {
    @Test
    public void testToCsv() {
        Assert.assertThat(Strings.toCsv(Collections.<String>emptyList()), CoreMatchers.is(""));
        Assert.assertThat(Strings.toCsv(Collections.singletonList("a")), CoreMatchers.is("a"));
        Assert.assertThat(Strings.toCsv(Arrays.asList("a", "b", "c")), CoreMatchers.is("a, b, c"));
    }

    @Test
    public void testJoin() {
        Assert.assertThat(Strings.join(Arrays.asList("a", "b"), "|"), CoreMatchers.is("a|b"));
        Assert.assertNull(Strings.join(null, "|"));
        Assert.assertThat(Strings.join(Collections.singletonList("a"), "|"), CoreMatchers.is("a"));
    }

    @Test
    public void testCapitaliseFirstLetter() {
        Assert.assertThat(Strings.capitaliseFirstLetter(""), CoreMatchers.is(""));
        Assert.assertThat(Strings.capitaliseFirstLetter("a"), CoreMatchers.is("A"));
        Assert.assertThat(Strings.capitaliseFirstLetter("aa"), CoreMatchers.is("Aa"));
        Assert.assertThat(Strings.capitaliseFirstLetter("A"), CoreMatchers.is("A"));
        Assert.assertThat(Strings.capitaliseFirstLetter("Ab"), CoreMatchers.is("Ab"));
    }

    @Test
    public void testLowercaseFirstLetter() {
        Assert.assertThat(Strings.lowercaseFirstLetter(""), CoreMatchers.is(""));
        Assert.assertThat(Strings.lowercaseFirstLetter("A"), CoreMatchers.is("a"));
        Assert.assertThat(Strings.lowercaseFirstLetter("AA"), CoreMatchers.is("aA"));
        Assert.assertThat(Strings.lowercaseFirstLetter("a"), CoreMatchers.is("a"));
        Assert.assertThat(Strings.lowercaseFirstLetter("aB"), CoreMatchers.is("aB"));
    }

    @Test
    public void testRepeat() {
        Assert.assertThat(Strings.repeat('0', 0), CoreMatchers.is(""));
        Assert.assertThat(Strings.repeat('1', 3), CoreMatchers.is("111"));
    }

    @Test
    public void testZeros() {
        Assert.assertThat(Strings.zeros(0), CoreMatchers.is(""));
        Assert.assertThat(Strings.zeros(3), CoreMatchers.is("000"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testEmptyString() {
        Assert.assertTrue(Strings.isEmpty(null));
        Assert.assertTrue(Strings.isEmpty(""));
        Assert.assertFalse(Strings.isEmpty("hello world"));
    }
}

