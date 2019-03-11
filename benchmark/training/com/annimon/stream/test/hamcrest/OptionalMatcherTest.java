package com.annimon.stream.test.hamcrest;


import com.annimon.stream.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OptionalMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(OptionalMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsPresent() {
        Optional<Integer> optional = Optional.of(5);
        Assert.assertThat(optional, OptionalMatcher.isPresent());
        Assert.assertThat(optional, CoreMatchers.not(OptionalMatcher.isEmpty()));
        Assert.assertThat(OptionalMatcher.isPresent(), CommonMatcher.description(CoreMatchers.is("Optional value should be present")));
    }

    @Test
    public void testIsEmpty() {
        Optional<Integer> optional = Optional.empty();
        Assert.assertThat(optional, OptionalMatcher.isEmpty());
        Assert.assertThat(optional, CoreMatchers.not(OptionalMatcher.isPresent()));
        Assert.assertThat(OptionalMatcher.isEmpty(), CommonMatcher.description(CoreMatchers.is("Optional value should be empty")));
    }

    @Test
    public void testHasValue() {
        Optional<String> optional = Optional.of("text");
        Assert.assertThat(optional, OptionalMatcher.hasValue("text"));
        Assert.assertThat(optional, CoreMatchers.not(OptionalMatcher.hasValue("test")));
        Assert.assertThat(OptionalMatcher.hasValue(42), CommonMatcher.description(CoreMatchers.is("Optional value is <42>")));
    }

    @Test
    public void testHasValueThat() {
        Optional<String> optional = Optional.of("text");
        Assert.assertThat(optional, OptionalMatcher.hasValueThat(CoreMatchers.startsWith("te")));
        Assert.assertThat(OptionalMatcher.hasValueThat(CoreMatchers.is(42)), CommonMatcher.description(CoreMatchers.is("Optional value is <42>")));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnEmptyOptional() {
        Assert.assertThat(Optional.<String>empty(), OptionalMatcher.hasValue(""));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        Assert.assertThat(null, OptionalMatcher.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testIsPresentOnNullValue() {
        Assert.assertThat(null, OptionalMatcher.isPresent());
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnNullValue() {
        Assert.assertThat(null, OptionalMatcher.hasValue(""));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueThatOnNullValue() {
        Assert.assertThat(null, OptionalMatcher.hasValueThat(CoreMatchers.is("")));
    }
}

