package com.baeldung.commons.lang3;


import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilsUnitTest {
    @Test
    public void givenString_whenCheckingContainsAny_thenCorrect() {
        String string = "baeldung.com";
        boolean contained1 = StringUtils.containsAny(string, 'a', 'b', 'c');
        boolean contained2 = StringUtils.containsAny(string, 'x', 'y', 'z');
        boolean contained3 = StringUtils.containsAny(string, "abc");
        boolean contained4 = StringUtils.containsAny(string, "xyz");
        Assert.assertTrue(contained1);
        Assert.assertFalse(contained2);
        Assert.assertTrue(contained3);
        Assert.assertFalse(contained4);
    }

    @Test
    public void givenString_whenCheckingContainsIgnoreCase_thenCorrect() {
        String string = "baeldung.com";
        boolean contained = StringUtils.containsIgnoreCase(string, "BAELDUNG");
        Assert.assertTrue(contained);
    }

    @Test
    public void givenString_whenCountingMatches_thenCorrect() {
        String string = "welcome to www.baeldung.com";
        int charNum = StringUtils.countMatches(string, 'w');
        int stringNum = StringUtils.countMatches(string, "com");
        Assert.assertEquals(4, charNum);
        Assert.assertEquals(2, stringNum);
    }

    @Test
    public void givenString_whenAppendingAndPrependingIfMissing_thenCorrect() {
        String string = "baeldung.com";
        String stringWithSuffix = StringUtils.appendIfMissing(string, ".com");
        String stringWithPrefix = StringUtils.prependIfMissing(string, "www.");
        Assert.assertEquals("baeldung.com", stringWithSuffix);
        Assert.assertEquals("www.baeldung.com", stringWithPrefix);
    }

    @Test
    public void givenString_whenSwappingCase_thenCorrect() {
        String originalString = "baeldung.COM";
        String swappedString = StringUtils.swapCase(originalString);
        Assert.assertEquals("BAELDUNG.com", swappedString);
    }

    @Test
    public void givenString_whenCapitalizing_thenCorrect() {
        String originalString = "baeldung";
        String capitalizedString = StringUtils.capitalize(originalString);
        Assert.assertEquals("Baeldung", capitalizedString);
    }

    @Test
    public void givenString_whenUncapitalizing_thenCorrect() {
        String originalString = "Baeldung";
        String uncapitalizedString = StringUtils.uncapitalize(originalString);
        Assert.assertEquals("baeldung", uncapitalizedString);
    }

    @Test
    public void givenString_whenReversingCharacters_thenCorrect() {
        String originalString = "baeldung";
        String reversedString = StringUtils.reverse(originalString);
        Assert.assertEquals("gnudleab", reversedString);
    }

    @Test
    public void givenString_whenReversingWithDelimiter_thenCorrect() {
        String originalString = "www.baeldung.com";
        String reversedString = StringUtils.reverseDelimited(originalString, '.');
        Assert.assertEquals("com.baeldung.www", reversedString);
    }

    @Test
    public void givenString_whenRotatingTwoPositions_thenCorrect() {
        String originalString = "baeldung";
        String rotatedString = StringUtils.rotate(originalString, 4);
        Assert.assertEquals("dungbael", rotatedString);
    }

    @Test
    public void givenTwoStrings_whenComparing_thenCorrect() {
        String tutorials = "Baeldung Tutorials";
        String courses = "Baeldung Courses";
        String diff1 = StringUtils.difference(tutorials, courses);
        String diff2 = StringUtils.difference(courses, tutorials);
        Assert.assertEquals("Courses", diff1);
        Assert.assertEquals("Tutorials", diff2);
    }
}

