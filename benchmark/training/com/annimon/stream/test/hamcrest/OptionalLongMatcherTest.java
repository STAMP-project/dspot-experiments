package com.annimon.stream.test.hamcrest;


import com.annimon.stream.OptionalLong;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OptionalLongMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(OptionalLongMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsPresent() {
        OptionalLong optional = OptionalLong.of(5L);
        Assert.assertThat(optional, OptionalLongMatcher.isPresent());
        Assert.assertThat(optional, CoreMatchers.not(OptionalLongMatcher.isEmpty()));
        Assert.assertThat(OptionalLongMatcher.isPresent(), CommonMatcher.description(CoreMatchers.is("OptionalLong value should be present")));
    }

    @Test
    public void testIsEmpty() {
        OptionalLong optional = OptionalLong.empty();
        Assert.assertThat(optional, OptionalLongMatcher.isEmpty());
        Assert.assertThat(optional, CoreMatchers.not(OptionalLongMatcher.isPresent()));
        Assert.assertThat(OptionalLongMatcher.isEmpty(), CommonMatcher.description(CoreMatchers.is("OptionalLong value should be empty")));
    }

    @Test
    public void testHasValue() {
        OptionalLong optional = OptionalLong.of(42);
        Assert.assertThat(optional, OptionalLongMatcher.hasValue(42));
        Assert.assertThat(optional, CoreMatchers.not(OptionalLongMatcher.hasValue(13)));
        Assert.assertThat(OptionalLongMatcher.hasValue(42L), CommonMatcher.description(CoreMatchers.is("OptionalLong value is <42L>")));
    }

    @Test
    public void testHasValueThat() {
        OptionalLong optional = OptionalLong.of(42L);
        Assert.assertThat(optional, OptionalLongMatcher.hasValueThat(CoreMatchers.is(CoreMatchers.not(17L))));
        Assert.assertThat(OptionalLongMatcher.hasValueThat(CoreMatchers.is(42L)), CommonMatcher.description(CoreMatchers.is("OptionalLong value is <42L>")));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnEmptyOptional() {
        Assert.assertThat(OptionalLong.empty(), OptionalLongMatcher.hasValue(0));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        Assert.assertThat(null, OptionalLongMatcher.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testIsPresentOnNullValue() {
        Assert.assertThat(null, OptionalLongMatcher.isPresent());
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnNullValue() {
        Assert.assertThat(null, OptionalLongMatcher.hasValue(0));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueThatOnNullValue() {
        Assert.assertThat(null, OptionalLongMatcher.hasValueThat(CoreMatchers.is(0L)));
    }
}

