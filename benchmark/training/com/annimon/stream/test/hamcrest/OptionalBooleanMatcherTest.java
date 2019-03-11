package com.annimon.stream.test.hamcrest;


import com.annimon.stream.OptionalBoolean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OptionalBooleanMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(OptionalBooleanMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsPresent() {
        OptionalBoolean optional = OptionalBoolean.of(true);
        Assert.assertThat(optional, OptionalBooleanMatcher.isPresent());
        Assert.assertThat(optional, CoreMatchers.not(OptionalBooleanMatcher.isEmpty()));
        Assert.assertThat(OptionalBooleanMatcher.isPresent(), CommonMatcher.description(CoreMatchers.is("OptionalBoolean value should be present")));
    }

    @Test
    public void testIsEmpty() {
        OptionalBoolean optional = OptionalBoolean.empty();
        Assert.assertThat(optional, OptionalBooleanMatcher.isEmpty());
        Assert.assertThat(optional, CoreMatchers.not(OptionalBooleanMatcher.isPresent()));
        Assert.assertThat(OptionalBooleanMatcher.isEmpty(), CommonMatcher.description(CoreMatchers.is("OptionalBoolean value should be empty")));
    }

    @Test
    public void testHasValue() {
        OptionalBoolean optional = OptionalBoolean.of(false);
        Assert.assertThat(optional, OptionalBooleanMatcher.hasValue(false));
        Assert.assertThat(optional, CoreMatchers.not(OptionalBooleanMatcher.hasValue(true)));
        Assert.assertThat(OptionalBooleanMatcher.hasValue(false), CommonMatcher.description(CoreMatchers.is("OptionalBoolean value is <false>")));
    }

    @Test
    public void testHasValueThat() {
        OptionalBoolean optional = OptionalBoolean.of(true);
        Assert.assertThat(optional, OptionalBooleanMatcher.hasValueThat(CoreMatchers.is(CoreMatchers.not(false))));
        Assert.assertThat(OptionalBooleanMatcher.hasValueThat(CoreMatchers.is(true)), CommonMatcher.description(CoreMatchers.is("OptionalBoolean value is <true>")));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnEmptyOptional() {
        Assert.assertThat(OptionalBoolean.empty(), OptionalBooleanMatcher.hasValue(true));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        Assert.assertThat(null, OptionalBooleanMatcher.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testIsPresentOnNullValue() {
        Assert.assertThat(null, OptionalBooleanMatcher.isPresent());
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnNullValue() {
        Assert.assertThat(null, OptionalBooleanMatcher.hasValue(true));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueThatOnNullValue() {
        Assert.assertThat(null, OptionalBooleanMatcher.hasValueThat(CoreMatchers.is(false)));
    }
}

