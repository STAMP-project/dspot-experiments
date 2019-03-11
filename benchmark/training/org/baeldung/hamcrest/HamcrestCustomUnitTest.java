package org.baeldung.hamcrest;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class HamcrestCustomUnitTest {
    @Test
    public final void givenAString_whenIsOnlyDigits_thenCorrect() {
        String digits = "123";
        MatcherAssert.assertThat(digits, Matchers.is(onlyDigits()));
    }

    @Test
    public final void givenAString_whenIsNotOnlyDigits_thenCorrect() {
        String aphanumeric = "123ABC";
        MatcherAssert.assertThat(aphanumeric, Matchers.is(Matchers.not(onlyDigits())));
    }

    @Test
    public final void givenAString_whenIsUppercase_thenCorrect() {
        String uppercaseWord = "HELLO";
        MatcherAssert.assertThat(uppercaseWord, Matchers.is(uppercase()));
    }

    @Test
    public final void givenAnEvenInteger_whenDivisibleByTwo_thenCorrect() {
        Integer ten = 10;
        Integer two = 2;
        MatcherAssert.assertThat(ten, Matchers.is(divisibleBy(two)));
    }

    @Test
    public final void givenAnOddInteger_whenNotDivisibleByTwo_thenCorrect() {
        Integer eleven = 11;
        Integer two = 2;
        MatcherAssert.assertThat(eleven, Matchers.is(Matchers.not(divisibleBy(two))));
    }
}

