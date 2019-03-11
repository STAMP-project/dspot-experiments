package com.annimon.stream.test.hamcrest;


import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;


public class CommonMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(CommonMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testHasOnlyPrivateConstructors() {
        Assert.assertThat(CommonMatcherTest.class, CoreMatchers.not(CommonMatcher.hasOnlyPrivateConstructors()));
        Matcher matcher = CommonMatcher.hasOnlyPrivateConstructors();
        Assert.assertThat(matcher, CommonMatcher.description(CoreMatchers.is("has only private constructors")));
    }

    @Test
    public void testDescription() {
        Matcher matcher = CommonMatcher.description(CoreMatchers.is("test"));
        Assert.assertThat(matcher, CommonMatcher.description(CoreMatchers.allOf(CoreMatchers.containsString("description is"), CoreMatchers.containsString("test"))));
        Assert.assertThat(matcher, CommonMatcher.description(CoreMatchers.not(CoreMatchers.equalTo("test"))));
    }
}

