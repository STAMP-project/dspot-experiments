package com.annimon.stream.test.hamcrest;


import com.annimon.stream.OptionalDouble;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class OptionalDoubleMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(OptionalDoubleMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsPresent() {
        OptionalDouble optional = OptionalDouble.of(12.34);
        Assert.assertThat(optional, OptionalDoubleMatcher.isPresent());
        Assert.assertThat(optional, Matchers.not(OptionalDoubleMatcher.isEmpty()));
        Assert.assertThat(OptionalDoubleMatcher.isPresent(), CommonMatcher.description(Matchers.is("OptionalDouble value should be present")));
    }

    @Test
    public void testIsEmpty() {
        OptionalDouble optional = OptionalDouble.empty();
        Assert.assertThat(optional, OptionalDoubleMatcher.isEmpty());
        Assert.assertThat(optional, Matchers.not(OptionalDoubleMatcher.isPresent()));
        Assert.assertThat(OptionalDoubleMatcher.isEmpty(), CommonMatcher.description(Matchers.is("OptionalDouble value should be empty")));
    }

    @Test
    public void testHasValue() {
        OptionalDouble optional = OptionalDouble.of(12.34);
        Assert.assertThat(optional, OptionalDoubleMatcher.hasValue(12.34));
        Assert.assertThat(optional, Matchers.not(OptionalDoubleMatcher.hasValue(13)));
        Assert.assertThat(OptionalDoubleMatcher.hasValue(12.34), CommonMatcher.description(Matchers.is("OptionalDouble value is <12.34>")));
    }

    @Test
    public void testHasValueThat() {
        OptionalDouble optional = OptionalDouble.of(42.0);
        Assert.assertThat(optional, OptionalDoubleMatcher.hasValueThat(Matchers.is(Matchers.not(Math.PI))));
        Assert.assertThat(OptionalDoubleMatcher.hasValueThat(Matchers.closeTo(42.0, 1.0E-5)), CommonMatcher.description(Matchers.containsString("<42.0>")));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnEmptyOptional() {
        Assert.assertThat(OptionalDouble.empty(), OptionalDoubleMatcher.hasValue(0));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        Assert.assertThat(null, OptionalDoubleMatcher.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testIsPresentOnNullValue() {
        Assert.assertThat(null, OptionalDoubleMatcher.isPresent());
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnNullValue() {
        Assert.assertThat(null, OptionalDoubleMatcher.hasValue(0.0));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueThatOnNullValue() {
        Assert.assertThat(null, OptionalDoubleMatcher.hasValueThat(Matchers.is(0.0)));
    }
}

