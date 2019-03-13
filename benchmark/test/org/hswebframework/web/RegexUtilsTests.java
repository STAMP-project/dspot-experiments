package org.hswebframework.web;


import java.util.Arrays;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


public class RegexUtilsTests {
    @Test
    public void test() {
        Arrays.asList('\\', '$', '(', ')', '*', '+', '.', '[', ']', '?', '^', '{', '}', '|').forEach(( s) -> Assert.assertEquals(RegexUtils.escape(String.valueOf(s)), ("\\" + s)));
    }
}

