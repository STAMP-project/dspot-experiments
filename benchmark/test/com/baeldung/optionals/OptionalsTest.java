package com.baeldung.optionals;


import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Optional.of;


public class OptionalsTest {
    @Test
    public void givenOptional_whenEmptyValue_thenCustomMessage() {
        Assert.assertEquals(Optional.of("Name not provided"), Optionals.getName(Optional.ofNullable(null)));
    }

    @Test
    public void givenOptional_whenValue_thenOptional() {
        String name = "Filan Fisteku";
        Optional<String> optionalString = Optional.ofNullable(name);
        Assert.assertEquals(optionalString, Optionals.getName(optionalString));
    }

    @Test
    public void givenOptional_whenValue_thenOptionalGeneralMethod() {
        String name = "Filan Fisteku";
        String missingOptional = "Name not provided";
        Optional<String> optionalString = Optional.ofNullable(name);
        Optional<String> fallbackOptionalString = Optional.ofNullable(missingOptional);
        Assert.assertEquals(optionalString, Optionals.or(optionalString, fallbackOptionalString));
    }

    @Test
    public void givenEmptyOptional_whenValue_thenOptionalGeneralMethod() {
        String missingOptional = "Name not provided";
        Optional<String> optionalString = Optional.empty();
        Optional<String> fallbackOptionalString = Optional.ofNullable(missingOptional);
        Assert.assertEquals(fallbackOptionalString, Optionals.or(optionalString, fallbackOptionalString));
    }

    @Test
    public void givenGuavaOptional_whenInvoke_thenOptional() {
        String name = "Filan Fisteku";
        com.google.common.base.Optional<String> stringOptional = of(name);
        Assert.assertEquals(stringOptional, Optionals.getOptionalGuavaName(stringOptional));
    }

    @Test
    public void givenGuavaOptional_whenNull_thenDefaultText() {
        Assert.assertEquals(of("Name not provided"), Optionals.getOptionalGuavaName(fromNullable(null)));
    }
}

