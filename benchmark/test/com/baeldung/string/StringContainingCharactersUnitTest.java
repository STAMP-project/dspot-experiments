package com.baeldung.string;


import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.junit.Test;


public class StringContainingCharactersUnitTest {
    private static final Pattern[] inputRegexes = new Pattern[4];

    private static final String regex = "^(?=.*?\\p{Lu})(?=.*?\\p{Ll})(?=.*?\\d)(?=.*?[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:\'\",<.>/?]).*$";

    static {
        StringContainingCharactersUnitTest.inputRegexes[0] = Pattern.compile(".*[A-Z].*");
        StringContainingCharactersUnitTest.inputRegexes[1] = Pattern.compile(".*[a-z].*");
        StringContainingCharactersUnitTest.inputRegexes[2] = Pattern.compile(".*\\d.*");
        StringContainingCharactersUnitTest.inputRegexes[3] = Pattern.compile(".*[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:\'\",<.>/?].*");
    }

    @Test
    public void givenRegexes_whenMatchingCorrectString_thenMatches() {
        String validInput = "Ab3;";
        TestCase.assertTrue(StringContainingCharactersUnitTest.isMatchingRegex(validInput));
    }

    @Test
    public void givenRegexes_whenMatchingWrongStrings_thenNotMatching() {
        String invalidInput = "Ab3";
        TestCase.assertFalse(StringContainingCharactersUnitTest.isMatchingRegex(invalidInput));
        invalidInput = "Ab;";
        TestCase.assertFalse(StringContainingCharactersUnitTest.isMatchingRegex(invalidInput));
        invalidInput = "A3;";
        TestCase.assertFalse(StringContainingCharactersUnitTest.isMatchingRegex(invalidInput));
        invalidInput = "b3;";
        TestCase.assertFalse(StringContainingCharactersUnitTest.isMatchingRegex(invalidInput));
    }

    @Test
    public void givenValidString_whenChecking_thenCorrect() {
        String validInput = "Ab3;";
        TestCase.assertTrue(StringContainingCharactersUnitTest.checkString(validInput));
    }

    @Test
    public void givenInvalidStrings_whenChecking_thenNotCorrect() {
        String invalidInput = "Ab3";
        TestCase.assertFalse(StringContainingCharactersUnitTest.checkString(invalidInput));
        invalidInput = "Ab;";
        TestCase.assertFalse(StringContainingCharactersUnitTest.checkString(invalidInput));
        invalidInput = "A3;";
        TestCase.assertFalse(StringContainingCharactersUnitTest.checkString(invalidInput));
        invalidInput = "b3;";
        TestCase.assertFalse(StringContainingCharactersUnitTest.checkString(invalidInput));
    }

    @Test
    public void givenSingleRegex_whenMatchingCorrectString_thenMatches() {
        String validInput = "Ab3;";
        TestCase.assertTrue(Pattern.compile(StringContainingCharactersUnitTest.regex).matcher(validInput).matches());
    }

    @Test
    public void givenSingleRegex_whenMatchingWrongStrings_thenNotMatching() {
        String invalidInput = "Ab3";
        TestCase.assertFalse(Pattern.compile(StringContainingCharactersUnitTest.regex).matcher(invalidInput).matches());
        invalidInput = "Ab;";
        TestCase.assertFalse(Pattern.compile(StringContainingCharactersUnitTest.regex).matcher(invalidInput).matches());
        invalidInput = "A3;";
        TestCase.assertFalse(Pattern.compile(StringContainingCharactersUnitTest.regex).matcher(invalidInput).matches());
        invalidInput = "b3;";
        TestCase.assertFalse(Pattern.compile(StringContainingCharactersUnitTest.regex).matcher(invalidInput).matches());
    }
}

