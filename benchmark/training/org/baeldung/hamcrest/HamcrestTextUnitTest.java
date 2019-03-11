package org.baeldung.hamcrest;


import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringEndsWith;
import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Test;


public class HamcrestTextUnitTest {
    @Test
    public final void whenTwoStringsAreEqual_thenCorrect() {
        String first = "hello";
        String second = "Hello";
        Assert.assertThat(first, equalToIgnoringCase(second));
    }

    @Test
    public final void whenTwoStringsAreEqualWithWhiteSpace_thenCorrect() {
        String first = "hello";
        String second = "   Hello   ";
        Assert.assertThat(first, equalToIgnoringWhiteSpace(second));
    }

    @Test
    public final void whenStringIsBlank_thenCorrect() {
        String first = "  ";
        String second = null;
        Assert.assertThat(first, blankString());
        Assert.assertThat(first, blankOrNullString());
        Assert.assertThat(second, blankOrNullString());
    }

    @Test
    public final void whenStringIsEmpty_thenCorrect() {
        String first = "";
        String second = null;
        Assert.assertThat(first, emptyString());
        Assert.assertThat(first, emptyOrNullString());
        Assert.assertThat(second, emptyOrNullString());
    }

    @Test
    public final void whenStringMatchPattern_thenCorrect() {
        String first = "hello";
        Assert.assertThat(first, matchesPattern("[a-z]+"));
    }

    @Test
    public final void whenVerifyStringContains_thenCorrect() {
        String first = "hello";
        Assert.assertThat(first, StringContains.containsString("lo"));
        Assert.assertThat(first, StringContains.containsStringIgnoringCase("EL"));
    }

    @Test
    public final void whenVerifyStringContainsInOrder_thenCorrect() {
        String first = "hello";
        Assert.assertThat(first, stringContainsInOrder("e", "l", "o"));
    }

    @Test
    public final void whenVerifyStringStartsWith_thenCorrect() {
        String first = "hello";
        Assert.assertThat(first, StringStartsWith.startsWith("he"));
        Assert.assertThat(first, StringStartsWith.startsWithIgnoringCase("HEL"));
    }

    @Test
    public final void whenVerifyStringEndsWith_thenCorrect() {
        String first = "hello";
        Assert.assertThat(first, StringEndsWith.endsWith("lo"));
        Assert.assertThat(first, StringEndsWith.endsWithIgnoringCase("LO"));
    }
}

