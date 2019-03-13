package com.github.javafaker.matchers;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class IsANumberTest {
    @Test
    public void testNumberShouldBeANumber() {
        MatcherAssert.assertThat(new IsANumber().matchesSafely("34"), Is.is(true));
    }

    @Test
    public void testBlankIsNotANumber() {
        MatcherAssert.assertThat(new IsANumber().matchesSafely(""), Is.is(false));
    }

    @Test
    public void testOtherCharsIsNotANumber() {
        MatcherAssert.assertThat(new IsANumber().matchesSafely("df3DF-="), Is.is(false));
    }

    @Test
    public void testEmptyStringIsNotANumber() {
        MatcherAssert.assertThat(new IsANumber().matchesSafely("   "), Is.is(false));
    }
}

