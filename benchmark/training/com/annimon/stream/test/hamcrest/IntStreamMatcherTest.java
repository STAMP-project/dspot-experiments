package com.annimon.stream.test.hamcrest;


import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


public class IntStreamMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(IntStreamMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsEmpty() {
        Assert.assertThat(IntStream.empty(), IntStreamMatcher.isEmpty());
        StringDescription description = new StringDescription();
        IntStreamMatcher.isEmpty().describeTo(description);
        Assert.assertThat(description.toString(), CoreMatchers.is("an empty stream"));
    }

    @Test
    public void testHasElements() {
        Assert.assertThat(IntStream.of(1, 2), IntStreamMatcher.hasElements());
        Assert.assertThat(IntStream.empty(), CoreMatchers.not(IntStreamMatcher.hasElements()));
        StringDescription description = new StringDescription();
        IntStreamMatcher.hasElements().describeTo(description);
        Assert.assertThat(description.toString(), CoreMatchers.is("a non-empty stream"));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        Assert.assertThat(null, IntStreamMatcher.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testHasElementsOnNullValue() {
        Assert.assertThat(null, IntStreamMatcher.hasElements());
    }

    @Test
    public void testElements() {
        final IntStream stream = IntStream.of((-813), 123456, Short.MAX_VALUE);
        final Integer[] expected = new Integer[]{ -813, 123456, ((int) (Short.MAX_VALUE)) };
        final Matcher<IntStream> matcher = IntStreamMatcher.elements(Matchers.arrayContaining(expected));
        Assert.assertThat(stream, matcher);
        Assert.assertTrue(matcher.matches(stream));
        Assert.assertFalse(IntStreamMatcher.elements(Matchers.arrayContaining(expected)).matches(IntStream.empty()));
        Assert.assertThat(matcher, CommonMatcher.description(CoreMatchers.allOf(CoreMatchers.containsString("IntStream elements"), CoreMatchers.containsString(Stream.of(expected).map(new com.annimon.stream.function.Function<Integer, String>() {
            @Override
            public String apply(Integer t) {
                return String.format("<%s>", t.toString());
            }
        }).collect(Collectors.joining(", "))))));
    }

    @Test
    public void testAssertIsEmptyOperator() {
        IntStream.empty().custom(IntStreamMatcher.assertIsEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testAssertIsEmptyOperatorOnEmptyStream() {
        IntStream.of(1, 2).custom(IntStreamMatcher.assertIsEmpty());
    }

    @Test
    public void testAssertHasElementsOperator() {
        IntStream.of(1, 2).custom(IntStreamMatcher.assertHasElements());
    }

    @Test(expected = AssertionError.class)
    public void testAssertHasElementsOperatorOnEmptyStream() {
        IntStream.empty().custom(IntStreamMatcher.assertHasElements());
    }

    @Test
    public void testAssertElementsOperator() {
        IntStream.of((-813), 123456, Short.MAX_VALUE).custom(IntStreamMatcher.assertElements(Matchers.arrayContaining(new Integer[]{ -813, 123456, ((int) (Short.MAX_VALUE)) })));
    }
}

