package com.baeldung.java.regex;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


public class RegexUnitTest {
    private static Pattern pattern;

    private static Matcher matcher;

    @Test
    public void givenText_whenSimpleRegexMatches_thenCorrect() {
        Pattern pattern = Pattern.compile("foo");
        Matcher matcher = pattern.matcher("foo");
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void givenText_whenSimpleRegexMatchesTwice_thenCorrect() {
        Pattern pattern = Pattern.compile("foo");
        Matcher matcher = pattern.matcher("foofoo");
        int matches = 0;
        while (matcher.find())
            matches++;

        Assert.assertEquals(matches, 2);
    }

    @Test
    public void givenText_whenMatchesWithDotMetach_thenCorrect() {
        int matches = RegexUnitTest.runTest(".", "foo");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRepeatedText_whenMatchesOnceWithDotMetach_thenCorrect() {
        int matches = RegexUnitTest.runTest("foo.", "foofoo");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenORSet_whenMatchesAny_thenCorrect() {
        int matches = RegexUnitTest.runTest("[abc]", "b");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenORSet_whenMatchesAnyAndAll_thenCorrect() {
        int matches = RegexUnitTest.runTest("[abc]", "cab");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 3);
    }

    @Test
    public void givenORSet_whenMatchesAllCombinations_thenCorrect() {
        int matches = RegexUnitTest.runTest("[bcr]at", "bat cat rat");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 3);
    }

    @Test
    public void givenNORSet_whenMatchesNon_thenCorrect() {
        int matches = RegexUnitTest.runTest("[^abc]", "g");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenNORSet_whenMatchesAllExceptElements_thenCorrect() {
        int matches = RegexUnitTest.runTest("[^bcr]at", "sat mat eat");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenUpperCaseRange_whenMatchesUpperCase_thenCorrect() {
        int matches = RegexUnitTest.runTest("[A-Z]", "Two Uppercase alphabets 34 overall");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 2);
    }

    @Test
    public void givenLowerCaseRange_whenMatchesLowerCase_thenCorrect() {
        int matches = RegexUnitTest.runTest("[a-z]", "Two Uppercase alphabets 34 overall");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 26);
    }

    @Test
    public void givenBothLowerAndUpperCaseRange_whenMatchesAllLetters_thenCorrect() {
        int matches = RegexUnitTest.runTest("[a-zA-Z]", "Two Uppercase alphabets 34 overall");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 28);
    }

    @Test
    public void givenNumberRange_whenMatchesAccurately_thenCorrect() {
        int matches = RegexUnitTest.runTest("[1-5]", "Two Uppercase alphabets 34 overall");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 2);
    }

    @Test
    public void givenNumberRange_whenMatchesAccurately_thenCorrect2() {
        int matches = RegexUnitTest.runTest("[30-35]", "Two Uppercase alphabets 34 overall");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenTwoSets_whenMatchesUnion_thenCorrect() {
        int matches = RegexUnitTest.runTest("[1-3[7-9]]", "123456789");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 6);
    }

    @Test
    public void givenTwoSets_whenMatchesIntersection_thenCorrect() {
        int matches = RegexUnitTest.runTest("[1-6&&[3-9]]", "123456789");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 4);
    }

    @Test
    public void givenSetWithSubtraction_whenMatchesAccurately_thenCorrect() {
        int matches = RegexUnitTest.runTest("[0-9&&[^2468]]", "123456789");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 5);
    }

    @Test
    public void givenDigits_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\d", "123");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 3);
    }

    @Test
    public void givenNonDigits_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\D", "a6c");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 2);
    }

    @Test
    public void givenWhiteSpace_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\s", "a c");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenNonWhiteSpace_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\S", "a c");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 2);
    }

    @Test
    public void givenWordCharacter_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\w", "hi!");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 2);
    }

    @Test
    public void givenNonWordCharacter_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\W", "hi!");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenZeroOrOneQuantifier_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\a?", "hi");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 3);
    }

    @Test
    public void givenZeroOrOneQuantifier_whenMatches_thenCorrect2() {
        int matches = RegexUnitTest.runTest("\\a{0,1}", "hi");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 3);
    }

    @Test
    public void givenZeroOrManyQuantifier_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\a*", "hi");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 3);
    }

    @Test
    public void givenZeroOrManyQuantifier_whenMatches_thenCorrect2() {
        int matches = RegexUnitTest.runTest("\\a{0,}", "hi");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 3);
    }

    @Test
    public void givenOneOrManyQuantifier_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\a+", "hi");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenOneOrManyQuantifier_whenMatches_thenCorrect2() {
        int matches = RegexUnitTest.runTest("\\a{1,}", "hi");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenBraceQuantifier_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("a{3}", "aaaaaa");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 2);
    }

    @Test
    public void givenBraceQuantifier_whenFailsToMatch_thenCorrect() {
        int matches = RegexUnitTest.runTest("a{3}", "aa");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenBraceQuantifierWithRange_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("a{2,3}", "aaaa");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenBraceQuantifierWithRange_whenMatchesLazily_thenCorrect() {
        int matches = RegexUnitTest.runTest("a{2,3}?", "aaaa");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 2);
    }

    @Test
    public void givenCapturingGroup_whenMatches_thenCorrect() {
        int matches = RegexUnitTest.runTest("(\\d\\d)", "12");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenCapturingGroup_whenMatches_thenCorrect2() {
        int matches = RegexUnitTest.runTest("(\\d\\d)", "1212");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 2);
    }

    @Test
    public void givenCapturingGroup_whenMatches_thenCorrect3() {
        int matches = RegexUnitTest.runTest("(\\d\\d)(\\d\\d)", "1212");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenCapturingGroup_whenMatchesWithBackReference_thenCorrect() {
        int matches = RegexUnitTest.runTest("(\\d\\d)\\1", "1212");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenCapturingGroup_whenMatchesWithBackReference_thenCorrect2() {
        int matches = RegexUnitTest.runTest("(\\d\\d)\\1\\1\\1", "12121212");
        Assert.assertTrue((matches > 0));
        Assert.assertEquals(matches, 1);
    }

    @Test
    public void givenCapturingGroupAndWrongInput_whenMatchFailsWithBackReference_thenCorrect() {
        int matches = RegexUnitTest.runTest("(\\d\\d)\\1", "1213");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenText_whenMatchesAtBeginning_thenCorrect() {
        int matches = RegexUnitTest.runTest("^dog", "dogs are friendly");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenTextAndWrongInput_whenMatchFailsAtBeginning_thenCorrect() {
        int matches = RegexUnitTest.runTest("^dog", "are dogs are friendly?");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenText_whenMatchesAtEnd_thenCorrect() {
        int matches = RegexUnitTest.runTest("dog$", "Man's best friend is a dog");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenTextAndWrongInput_whenMatchFailsAtEnd_thenCorrect() {
        int matches = RegexUnitTest.runTest("dog$", "is a dog man's best friend?");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenText_whenMatchesAtWordBoundary_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\bdog\\b", "a dog is friendly");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenText_whenMatchesAtWordBoundary_thenCorrect2() {
        int matches = RegexUnitTest.runTest("\\bdog\\b", "dog is man's best friend");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenWrongText_whenMatchFailsAtWordBoundary_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\bdog\\b", "snoop dogg is a rapper");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenText_whenMatchesAtWordAndNonBoundary_thenCorrect() {
        int matches = RegexUnitTest.runTest("\\bdog\\B", "snoop dogg is a rapper");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRegexWithoutCanonEq_whenMatchFailsOnEquivalentUnicode_thenCorrect() {
        int matches = RegexUnitTest.runTest("\u00e9", "e\u0301");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenRegexWithCanonEq_whenMatchesOnEquivalentUnicode_thenCorrect() {
        int matches = RegexUnitTest.runTest("\u00e9", "e\u0301", Pattern.CANON_EQ);
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRegexWithDefaultMatcher_whenMatchFailsOnDifferentCases_thenCorrect() {
        int matches = RegexUnitTest.runTest("dog", "This is a Dog");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenRegexWithCaseInsensitiveMatcher_whenMatchesOnDifferentCases_thenCorrect() {
        int matches = RegexUnitTest.runTest("dog", "This is a Dog", Pattern.CASE_INSENSITIVE);
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRegexWithEmbeddedCaseInsensitiveMatcher_whenMatchesOnDifferentCases_thenCorrect() {
        int matches = RegexUnitTest.runTest("(?i)dog", "This is a Dog");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRegexWithComments_whenMatchFailsWithoutFlag_thenCorrect() {
        int matches = RegexUnitTest.runTest("dog$  #check for word dog at end of text", "This is a dog");
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenRegexWithComments_whenMatchesWithFlag_thenCorrect() {
        int matches = RegexUnitTest.runTest("dog$  #check for word dog at end of text", "This is a dog", Pattern.COMMENTS);
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRegexWithComments_whenMatchesWithEmbeddedFlag_thenCorrect() {
        int matches = RegexUnitTest.runTest("(?x)dog$  #check for word dog at end of text", "This is a dog");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRegexWithLineTerminator_whenMatchFails_thenCorrect() {
        Pattern pattern = Pattern.compile("(.*)");
        Matcher matcher = pattern.matcher((("this is a text" + (System.getProperty("line.separator"))) + " continued on another line"));
        matcher.find();
        Assert.assertEquals("this is a text", matcher.group(1));
    }

    @Test
    public void givenRegexWithLineTerminator_whenMatchesWithDotall_thenCorrect() {
        Pattern pattern = Pattern.compile("(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher((("this is a text" + (System.getProperty("line.separator"))) + " continued on another line"));
        matcher.find();
        Assert.assertEquals((("this is a text" + (System.getProperty("line.separator"))) + " continued on another line"), matcher.group(1));
    }

    @Test
    public void givenRegexWithLineTerminator_whenMatchesWithEmbeddedDotall_thenCorrect() {
        Pattern pattern = Pattern.compile("(?s)(.*)");
        Matcher matcher = pattern.matcher((("this is a text" + (System.getProperty("line.separator"))) + " continued on another line"));
        matcher.find();
        Assert.assertEquals((("this is a text" + (System.getProperty("line.separator"))) + " continued on another line"), matcher.group(1));
    }

    @Test
    public void givenRegex_whenMatchesWithoutLiteralFlag_thenCorrect() {
        int matches = RegexUnitTest.runTest("(.*)", "text");
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRegex_whenMatchFailsWithLiteralFlag_thenCorrect() {
        int matches = RegexUnitTest.runTest("(.*)", "text", Pattern.LITERAL);
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenRegex_whenMatchesWithLiteralFlag_thenCorrect() {
        int matches = RegexUnitTest.runTest("(.*)", "text(.*)", Pattern.LITERAL);
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRegex_whenMatchFailsWithoutMultilineFlag_thenCorrect() {
        int matches = RegexUnitTest.runTest("dog$", (("This is a dog" + (System.getProperty("line.separator"))) + "this is a fox"));
        Assert.assertFalse((matches > 0));
    }

    @Test
    public void givenRegex_whenMatchesWithMultilineFlag_thenCorrect() {
        int matches = RegexUnitTest.runTest("dog$", (("This is a dog" + (System.getProperty("line.separator"))) + "this is a fox"), Pattern.MULTILINE);
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenRegex_whenMatchesWithEmbeddedMultilineFlag_thenCorrect() {
        int matches = RegexUnitTest.runTest("(?m)dog$", (("This is a dog" + (System.getProperty("line.separator"))) + "this is a fox"));
        Assert.assertTrue((matches > 0));
    }

    @Test
    public void givenMatch_whenGetsIndices_thenCorrect() {
        Pattern pattern = Pattern.compile("dog");
        Matcher matcher = pattern.matcher("This dog is mine");
        matcher.find();
        Assert.assertEquals(5, matcher.start());
        Assert.assertEquals(8, matcher.end());
    }

    @Test
    public void whenStudyMethodsWork_thenCorrect() {
        Pattern pattern = Pattern.compile("dog");
        Matcher matcher = pattern.matcher("dogs are friendly");
        Assert.assertTrue(matcher.lookingAt());
        Assert.assertFalse(matcher.matches());
    }

    @Test
    public void whenMatchesStudyMethodWorks_thenCorrect() {
        Pattern pattern = Pattern.compile("dog");
        Matcher matcher = pattern.matcher("dog");
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void whenReplaceFirstWorks_thenCorrect() {
        Pattern pattern = Pattern.compile("dog");
        Matcher matcher = pattern.matcher("dogs are domestic animals, dogs are friendly");
        String newStr = matcher.replaceFirst("cat");
        Assert.assertEquals("cats are domestic animals, dogs are friendly", newStr);
    }

    @Test
    public void whenReplaceAllWorks_thenCorrect() {
        Pattern pattern = Pattern.compile("dog");
        Matcher matcher = pattern.matcher("dogs are domestic animals, dogs are friendly");
        String newStr = matcher.replaceAll("cat");
        Assert.assertEquals("cats are domestic animals, cats are friendly", newStr);
    }
}

