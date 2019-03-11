package com.annimon.stream.test.hamcrest;


import com.annimon.stream.Collectors;
import com.annimon.stream.LongStream;
import com.annimon.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


public class LongStreamMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(LongStreamMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsEmpty() {
        Assert.assertThat(LongStream.empty(), LongStreamMatcher.isEmpty());
        StringDescription description = new StringDescription();
        LongStreamMatcher.isEmpty().describeTo(description);
        Assert.assertThat(description.toString(), CoreMatchers.is("an empty stream"));
    }

    @Test
    public void testHasElements() {
        Assert.assertThat(LongStream.of(1, 2), LongStreamMatcher.hasElements());
        Assert.assertThat(LongStream.empty(), CoreMatchers.not(LongStreamMatcher.hasElements()));
        StringDescription description = new StringDescription();
        LongStreamMatcher.hasElements().describeTo(description);
        Assert.assertThat(description.toString(), CoreMatchers.is("a non-empty stream"));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        Assert.assertThat(null, LongStreamMatcher.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testHasElementsOnNullValue() {
        Assert.assertThat(null, LongStreamMatcher.hasElements());
    }

    @Test
    public void testElements() {
        final LongStream stream = LongStream.of((-813), 123456, Integer.MAX_VALUE);
        final Long[] expected = new Long[]{ -813L, 123456L, ((long) (Integer.MAX_VALUE)) };
        final Matcher<LongStream> matcher = LongStreamMatcher.elements(Matchers.arrayContaining(expected));
        Assert.assertThat(stream, matcher);
        Assert.assertTrue(matcher.matches(stream));
        Assert.assertFalse(LongStreamMatcher.elements(Matchers.arrayContaining(expected)).matches(LongStream.empty()));
        Assert.assertThat(matcher, CommonMatcher.description(CoreMatchers.allOf(CoreMatchers.containsString("LongStream elements"), CoreMatchers.containsString(Stream.of(expected).map(new com.annimon.stream.function.Function<Long, String>() {
            @Override
            public String apply(Long t) {
                return String.format("<%sL>", t.toString());
            }
        }).collect(Collectors.joining(", "))))));
    }

    @Test
    public void testAssertIsEmptyOperator() {
        LongStream.empty().custom(LongStreamMatcher.assertIsEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testAssertIsEmptyOperatorOnEmptyStream() {
        LongStream.of(1, 2).custom(LongStreamMatcher.assertIsEmpty());
    }

    @Test
    public void testAssertHasElementsOperator() {
        LongStream.of(1, 2).custom(LongStreamMatcher.assertHasElements());
    }

    @Test(expected = AssertionError.class)
    public void testAssertHasElementsOperatorOnEmptyStream() {
        LongStream.empty().custom(LongStreamMatcher.assertHasElements());
    }

    @Test
    public void testAssertElementsOperator() {
        LongStream.of((-813), 123456, Integer.MAX_VALUE).custom(LongStreamMatcher.assertElements(Matchers.arrayContaining(new Long[]{ -813L, 123456L, ((long) (Integer.MAX_VALUE)) })));
    }
}

