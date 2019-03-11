package com.annimon.stream.test.hamcrest;


import com.annimon.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


public class StreamMatcherTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(StreamMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsEmpty() {
        Assert.assertThat(Stream.empty(), StreamMatcher.isEmpty());
        StringDescription description = new StringDescription();
        StreamMatcher.isEmpty().describeTo(description);
        Assert.assertThat(description.toString(), CoreMatchers.is("an empty stream"));
    }

    @Test
    public void testHasElements() {
        Assert.assertThat(Stream.of(1, 2), StreamMatcher.hasElements());
        Assert.assertThat(Stream.empty(), CoreMatchers.not(StreamMatcher.hasElements()));
        StringDescription description = new StringDescription();
        StreamMatcher.hasElements().describeTo(description);
        Assert.assertThat(description.toString(), CoreMatchers.is("a non-empty stream"));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        Assert.assertThat(null, StreamMatcher.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testHasElementsOnNullValue() {
        Assert.assertThat(null, StreamMatcher.hasElements());
    }

    @Test
    public void testElements() {
        final Stream<Integer> stream = Stream.range(0, 5);
        final Integer[] expected = new Integer[]{ 0, 1, 2, 3, 4 };
        final Matcher<Stream<Integer>> matcher = StreamMatcher.elements(Matchers.contains(expected));
        Assert.assertThat(stream, matcher);
        Assert.assertTrue(matcher.matches(stream));
        Assert.assertFalse(StreamMatcher.elements(Matchers.contains(expected)).matches(Stream.<Integer>empty()));
        Assert.assertThat(matcher, CommonMatcher.description(CoreMatchers.allOf(CoreMatchers.containsString("Stream elements iterable "), CoreMatchers.containsString("<0>, <1>, <2>, <3>, <4>"))));
    }

    @Test
    public void testAssertIsEmptyOperator() {
        Stream.<Integer>empty().custom(StreamMatcher.<Integer>assertIsEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testAssertIsEmptyOperatorOnEmptyStream() {
        Stream.of(1, 2).custom(StreamMatcher.<Integer>assertIsEmpty());
    }

    @Test
    public void testAssertHasElementsOperator() {
        Stream.of(1, 2).custom(StreamMatcher.<Integer>assertHasElements());
    }

    @Test(expected = AssertionError.class)
    public void testAssertHasElementsOperatorOnEmptyStream() {
        Stream.<Integer>empty().custom(StreamMatcher.<Integer>assertHasElements());
    }

    @Test
    public void testAssertElementsOperator() {
        Stream.range(0, 5).custom(StreamMatcher.assertElements(Matchers.contains(0, 1, 2, 3, 4)));
    }
}

