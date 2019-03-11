package com.annimon.stream.test.hamcrest;


import com.annimon.stream.OptionalInt;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OptionalIntMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(OptionalIntMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsPresent() {
        OptionalInt optional = OptionalInt.of(5);
        Assert.assertThat(optional, OptionalIntMatcher.isPresent());
        Assert.assertThat(optional, CoreMatchers.not(OptionalIntMatcher.isEmpty()));
        Assert.assertThat(OptionalIntMatcher.isPresent(), CommonMatcher.description(CoreMatchers.is("OptionalInt value should be present")));
    }

    @Test
    public void testIsEmpty() {
        OptionalInt optional = OptionalInt.empty();
        Assert.assertThat(optional, OptionalIntMatcher.isEmpty());
        Assert.assertThat(optional, CoreMatchers.not(OptionalIntMatcher.isPresent()));
        Assert.assertThat(OptionalIntMatcher.isEmpty(), CommonMatcher.description(CoreMatchers.is("OptionalInt value should be empty")));
    }

    @Test
    public void testHasValue() {
        OptionalInt optional = OptionalInt.of(42);
        Assert.assertThat(optional, OptionalIntMatcher.hasValue(42));
        Assert.assertThat(optional, CoreMatchers.not(OptionalIntMatcher.hasValue(13)));
        Assert.assertThat(OptionalIntMatcher.hasValue(42), CommonMatcher.description(CoreMatchers.is("OptionalInt value is <42>")));
    }

    @Test
    public void testHasValueThat() {
        OptionalInt optional = OptionalInt.of(42);
        Assert.assertThat(optional, OptionalIntMatcher.hasValueThat(CoreMatchers.is(CoreMatchers.not(17))));
        Assert.assertThat(OptionalIntMatcher.hasValueThat(CoreMatchers.is(42)), CommonMatcher.description(CoreMatchers.is("OptionalInt value is <42>")));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnEmptyOptional() {
        Assert.assertThat(OptionalInt.empty(), OptionalIntMatcher.hasValue(0));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        Assert.assertThat(null, OptionalIntMatcher.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testIsPresentOnNullValue() {
        Assert.assertThat(null, OptionalIntMatcher.isPresent());
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnNullValue() {
        Assert.assertThat(null, OptionalIntMatcher.hasValue(0));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueThatOnNullValue() {
        Assert.assertThat(null, OptionalIntMatcher.hasValueThat(CoreMatchers.is(0)));
    }
}

