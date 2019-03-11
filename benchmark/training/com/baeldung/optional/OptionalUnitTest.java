package com.baeldung.optional;


import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link Optional} in Java 11.
 */
public class OptionalUnitTest {
    @Test
    public void givenAnEmptyOptional_isEmpty_thenBehavesAsExpected() {
        Optional<String> opt = Optional.of("Baeldung");
        Assert.assertFalse(opt.isEmpty());
        opt = Optional.ofNullable(null);
        Assert.assertTrue(opt.isEmpty());
    }
}

