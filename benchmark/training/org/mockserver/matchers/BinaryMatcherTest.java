package org.mockserver.matchers;


import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class BinaryMatcherTest {
    @Test
    public void shouldMatchMatchingString() {
        Assert.assertTrue(matches(null, "some_value".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldNotMatchMatchingString() {
        Assert.assertFalse(matches(null, "some_value".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldMatchNullExpectation() {
        Assert.assertTrue(matches(null, "some_value".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldNotMatchNullExpectation() {
        Assert.assertFalse(matches(null, "some_value".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldMatchEmptyExpectation() {
        Assert.assertTrue(matches(null, "some_value".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldNotMatchEmptyExpectation() {
        Assert.assertFalse(matches(null, "some_value".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldNotMatchIncorrectString() {
        Assert.assertFalse(matches(null, "not_matching".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldMatchIncorrectString() {
        Assert.assertTrue(matches(null, "not_matching".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldNotMatchNullTest() {
        Assert.assertFalse(new BinaryMatcher(new org.mockserver.logging.MockServerLogger(), "some_value".getBytes(StandardCharsets.UTF_8)).matches(null, null));
    }

    @Test
    public void shouldMatchNullTest() {
        Assert.assertTrue(NotMatcher.not(new BinaryMatcher(new org.mockserver.logging.MockServerLogger(), "some_value".getBytes(StandardCharsets.UTF_8))).matches(null, null));
    }

    @Test
    public void shouldNotMatchEmptyTest() {
        Assert.assertFalse(matches(null, "".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldMatchEmptyTest() {
        Assert.assertTrue(matches(null, "".getBytes(StandardCharsets.UTF_8)));
    }
}

