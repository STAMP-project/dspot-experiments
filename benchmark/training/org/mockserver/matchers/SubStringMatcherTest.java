package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class SubStringMatcherTest {
    @Test
    public void shouldMatchMatchingString() {
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertTrue(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val")).matches(null, NottableString.not("some_value")));
        Assert.assertTrue(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), "me_val")).matches(null, NottableString.not("some_value")));
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertFalse(new SubStringMatcher(new MockServerLogger(), "me_val").matches(null, NottableString.not("some_value")));
        Assert.assertFalse(matches(null, "some_value"));
        Assert.assertFalse(matches(null, "some_value"));
        Assert.assertFalse(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val"))).matches(null, NottableString.not("some_value")));
    }

    @Test
    public void shouldMatchNotMatchingString() {
        Assert.assertFalse(matches(null, "some_other_value"));
        Assert.assertFalse(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val")).matches(null, NottableString.not("some_other_value")));
        Assert.assertFalse(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), "me_val")).matches(null, NottableString.not("some_other_value")));
        Assert.assertFalse(matches(null, "some_other_value"));
        Assert.assertTrue(new SubStringMatcher(new MockServerLogger(), "me_val").matches(null, NottableString.not("some_other_value")));
        Assert.assertTrue(matches(null, "some_other_value"));
        Assert.assertTrue(matches(null, "some_other_value"));
        Assert.assertTrue(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val"))).matches(null, NottableString.not("some_other_value")));
    }

    @Test
    public void shouldMatchNullMatcher() {
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertTrue(new SubStringMatcher(new MockServerLogger(), NottableString.not(null)).matches(null, NottableString.not("some_value")));
        Assert.assertTrue(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), ((String) (null)))).matches(null, NottableString.not("some_value")));
        Assert.assertTrue(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), NottableString.string(null))).matches(null, NottableString.not("some_value")));
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertFalse(new SubStringMatcher(new MockServerLogger(), ((String) (null))).matches(null, NottableString.not("some_value")));
        Assert.assertFalse(new SubStringMatcher(new MockServerLogger(), NottableString.string(null)).matches(null, NottableString.not("some_value")));
        Assert.assertFalse(matches(null, "some_value"));
        Assert.assertFalse(matches(null, "some_value"));
        Assert.assertFalse(matches(null, "some_value"));
        Assert.assertFalse(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), NottableString.not(null))).matches(null, NottableString.not("some_value")));
    }

    @Test
    public void shouldMatchNullMatched() {
        Assert.assertFalse(matches(null, ((String) (null))));
        Assert.assertFalse(new SubStringMatcher(new MockServerLogger(), "me_val").matches(null, NottableString.string(null)));
        Assert.assertFalse(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val")).matches(null, NottableString.not(null)));
        Assert.assertFalse(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), "me_val")).matches(null, NottableString.not(null)));
        Assert.assertFalse(matches(null, ((String) (null))));
        Assert.assertFalse(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val"))).matches(null, NottableString.string(null)));
        Assert.assertTrue(new SubStringMatcher(new MockServerLogger(), "me_val").matches(null, NottableString.not(null)));
        Assert.assertTrue(matches(null, ((String) (null))));
        Assert.assertTrue(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), "me_val")).matches(null, NottableString.string(null)));
        Assert.assertTrue(matches(null, ((String) (null))));
        Assert.assertTrue(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val")).matches(null, NottableString.string(null)));
        Assert.assertTrue(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val"))).matches(null, NottableString.not(null)));
    }

    @Test
    public void shouldMatchEmptyMatcher() {
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertTrue(new SubStringMatcher(new MockServerLogger(), NottableString.not("")).matches(null, NottableString.not("some_value")));
        Assert.assertTrue(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), "")).matches(null, NottableString.not("some_value")));
        Assert.assertTrue(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), NottableString.string(""))).matches(null, NottableString.not("some_value")));
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertFalse(new SubStringMatcher(new MockServerLogger(), "").matches(null, NottableString.not("some_value")));
        Assert.assertFalse(new SubStringMatcher(new MockServerLogger(), NottableString.string("")).matches(null, NottableString.not("some_value")));
        Assert.assertFalse(matches(null, "some_value"));
        Assert.assertFalse(matches(null, "some_value"));
        Assert.assertFalse(matches(null, "some_value"));
        Assert.assertFalse(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), NottableString.not(""))).matches(null, NottableString.not("some_value")));
    }

    @Test
    public void shouldMatchEmptyMatched() {
        Assert.assertFalse(matches(null, ""));
        Assert.assertFalse(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val")).matches(null, NottableString.not("")));
        Assert.assertFalse(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), "me_val")).matches(null, NottableString.not("")));
        Assert.assertFalse(matches(null, ""));
        Assert.assertTrue(new SubStringMatcher(new MockServerLogger(), "me_val").matches(null, NottableString.not("")));
        Assert.assertTrue(matches(null, ""));
        Assert.assertTrue(matches(null, ""));
        Assert.assertTrue(NotMatcher.not(new SubStringMatcher(new MockServerLogger(), NottableString.not("me_val"))).matches(null, NottableString.not("")));
    }
}

