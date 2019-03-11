package com.annimon.stream.test.hamcrest;


import com.annimon.stream.Collectors;
import com.annimon.stream.DoubleStream;
import com.annimon.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


public class DoubleStreamMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(DoubleStreamMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsEmpty() {
        Assert.assertThat(DoubleStream.empty(), DoubleStreamMatcher.isEmpty());
        StringDescription description = new StringDescription();
        DoubleStreamMatcher.isEmpty().describeTo(description);
        Assert.assertThat(description.toString(), CoreMatchers.is("an empty stream"));
    }

    @Test
    public void testHasElements() {
        Assert.assertThat(DoubleStream.of(1, 2), DoubleStreamMatcher.hasElements());
        Assert.assertThat(DoubleStream.empty(), CoreMatchers.not(DoubleStreamMatcher.hasElements()));
        StringDescription description = new StringDescription();
        DoubleStreamMatcher.hasElements().describeTo(description);
        Assert.assertThat(description.toString(), CoreMatchers.is("a non-empty stream"));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        Assert.assertThat(null, DoubleStreamMatcher.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testHasElementsOnNullValue() {
        Assert.assertThat(null, DoubleStreamMatcher.hasElements());
    }

    @Test
    public void testElements() {
        final DoubleStream stream = DoubleStream.of((-0.987), 1.234, Math.PI, 1.618);
        final Double[] expected = new Double[]{ -0.987, 1.234, Math.PI, 1.618 };
        final Matcher<DoubleStream> matcher = DoubleStreamMatcher.elements(Matchers.arrayContaining(expected));
        Assert.assertThat(stream, matcher);
        Assert.assertTrue(matcher.matches(stream));
        Assert.assertFalse(DoubleStreamMatcher.elements(Matchers.arrayContaining(expected)).matches(DoubleStream.empty()));
        Assert.assertThat(matcher, CommonMatcher.description(CoreMatchers.allOf(CoreMatchers.containsString("DoubleStream elements"), CoreMatchers.containsString(Stream.of(expected).map(new com.annimon.stream.function.Function<Double, String>() {
            @Override
            public String apply(Double t) {
                return String.format("<%s>", t.toString());
            }
        }).collect(Collectors.joining(", "))))));
    }

    @Test
    public void testAssertIsEmptyOperator() {
        DoubleStream.empty().custom(DoubleStreamMatcher.assertIsEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testAssertIsEmptyOperatorOnEmptyStream() {
        DoubleStream.of(1, 2).custom(DoubleStreamMatcher.assertIsEmpty());
    }

    @Test
    public void testAssertHasElementsOperator() {
        DoubleStream.of(1, 2).custom(DoubleStreamMatcher.assertHasElements());
    }

    @Test(expected = AssertionError.class)
    public void testAssertHasElementsOperatorOnEmptyStream() {
        DoubleStream.empty().custom(DoubleStreamMatcher.assertHasElements());
    }

    @Test
    public void testAssertElementsOperator() {
        DoubleStream.of((-0.987), 1.234, Math.PI, 1.618).custom(DoubleStreamMatcher.assertElements(Matchers.arrayContaining(new Double[]{ -0.987, 1.234, Math.PI, 1.618 })));
    }
}

