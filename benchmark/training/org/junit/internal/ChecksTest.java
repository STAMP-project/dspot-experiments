package org.junit.internal;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Checks}.
 */
public class ChecksTest {
    @Test
    public void notNullShouldReturnNonNullValues() {
        Double value = Double.valueOf(3.14);
        Double result = Checks.notNull(value);
        Assert.assertSame(value, result);
    }

    @Test
    public void notNullShouldThrowOnNullValues() {
        try {
            Checks.notNull(null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            Assert.assertNull("message should be null", e.getMessage());
        }
    }

    @Test
    public void notNullWithMessageShouldReturnNonNullValues() {
        Float value = Float.valueOf(3.14F);
        Float result = Checks.notNull(value, "woops");
        Assert.assertSame(value, result);
    }

    @Test
    public void notNullWithMessageShouldThrowOnNullValues() {
        try {
            Checks.notNull(null, "woops");
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            Assert.assertEquals("message does not match", "woops", e.getMessage());
        }
    }

    @Test
    public void notNullWithNullMessageShouldThrowOnNullValues() {
        try {
            Checks.notNull(null, null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            Assert.assertNull("message should be null", e.getMessage());
        }
    }
}

