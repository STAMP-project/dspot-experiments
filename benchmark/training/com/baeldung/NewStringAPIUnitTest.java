package com.baeldung;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NewStringAPIUnitTest {
    @Test
    public void whenRepeatStringTwice_thenGetStringTwice() {
        String output = (repeat(2)) + "Land";
        CoreMatchers.is(output).equals("La La Land");
    }

    @Test
    public void whenStripString_thenReturnStringWithoutWhitespaces() {
        CoreMatchers.is(strip()).equals("hello");
    }

    @Test
    public void whenTrimAdvanceString_thenReturnStringWithWhitespaces() {
        CoreMatchers.is("\n\t  hello   \u2005".trim()).equals("hello   \u2005");
    }

    @Test
    public void whenBlankString_thenReturnTrue() {
        Assert.assertTrue(isBlank());
    }

    @Test
    public void whenMultilineString_thenReturnNonEmptyLineCount() {
        String multilineStr = "This is\n \n a multiline\n string.";
        long lineCount = lines().filter(String::isBlank).count();
        CoreMatchers.is(lineCount).equals(3L);
    }
}

