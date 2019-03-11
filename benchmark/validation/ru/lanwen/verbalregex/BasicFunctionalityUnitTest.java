package ru.lanwen.verbalregex;


import VerbalExpression.Builder;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import ru.lanwen.verbalregex.matchers.EqualToRegexMatcher;
import ru.lanwen.verbalregex.matchers.TestMatchMatcher;
import ru.lanwen.verbalregex.matchers.TestsExactMatcher;


public class BasicFunctionalityUnitTest {
    @Test
    public void testSomething() {
        VerbalExpression testRegex = new VerbalExpression.Builder().something().build();
        Assert.assertThat("Null object doesn't have something", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo(null)));
        Assert.assertThat("empty string doesn't have something", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("")));
        Assert.assertThat("a", testRegex, TestMatchMatcher.matchesTo("a"));
    }

    @Test
    public void testAnything() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().anything().build();
        Assert.assertThat(testRegex, TestMatchMatcher.matchesTo("what"));
        Assert.assertThat(testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("")));
        Assert.assertThat(testRegex, TestMatchMatcher.matchesTo(" "));
    }

    @Test
    public void testAnythingBut() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().anythingBut("w").build();
        Assert.assertFalse("starts with w", testRegex.testExact("what"));
        Assert.assertTrue("Not contain w", testRegex.testExact("that"));
        Assert.assertTrue("Not contain w", testRegex.testExact(" "));
        Assert.assertFalse("Null object", testRegex.testExact(null));
    }

    @Test
    public void testSomethingBut() {
        VerbalExpression testRegex = new VerbalExpression.Builder().somethingButNot("a").build();
        Assert.assertFalse("Null string", testRegex.testExact(null));
        Assert.assertFalse("empty string doesn't have something", testRegex.testExact(""));
        Assert.assertTrue("doesn't contain a", testRegex.testExact("b"));
        Assert.assertFalse("Contain a", testRegex.testExact("a"));
    }

    @Test
    public void testStartOfLine() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().then("a").build();
        Assert.assertFalse("Null string", testRegex.testExact(null));
        Assert.assertFalse("empty string doesn't have something", testRegex.testExact(""));
        Assert.assertThat("Starts with a", testRegex, TestMatchMatcher.matchesTo("a"));
        Assert.assertThat("Starts with a", testRegex, TestMatchMatcher.matchesTo("ab"));
        Assert.assertThat("Doesn't start with a", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("ba")));
    }

    @Test
    public void testStartOfLineFalse() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine(false).then("a").build();
        Assert.assertThat(testRegex, TestMatchMatcher.matchesTo("ba"));
        Assert.assertThat(testRegex, TestMatchMatcher.matchesTo("ab"));
    }

    @Test
    public void testRangeWithMultiplyRanges() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().range("a", "z", "A", "Z").build();
        Assert.assertThat("Regex with multi-range differs from expected", regex.toString(), CoreMatchers.equalTo("[a-zA-Z]"));
        Assert.assertThat("Regex don't matches letter", regex, TestMatchMatcher.matchesTo("b"));
        Assert.assertThat("Regex matches digit, but should match only letter", regex, CoreMatchers.not(TestMatchMatcher.matchesTo("1")));
    }

    @Test
    public void testEndOfLine() {
        VerbalExpression testRegex = new VerbalExpression.Builder().find("a").endOfLine().build();
        Assert.assertThat("Ends with a", testRegex, TestMatchMatcher.matchesTo("bba"));
        Assert.assertThat("Ends with a", testRegex, TestMatchMatcher.matchesTo("a"));
        Assert.assertThat("Ends with a", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo(null)));
        Assert.assertThat("Doesn't end with a", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("ab")));
    }

    @Test
    public void testEndOfLineIsFalse() {
        VerbalExpression testRegex = VerbalExpression.regex().find("a").endOfLine(false).build();
        Assert.assertThat(testRegex, TestMatchMatcher.matchesTo("ba"));
        Assert.assertThat(testRegex, TestMatchMatcher.matchesTo("ab"));
    }

    @Test
    public void testMaybe() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().then("a").maybe("b").build();
        Assert.assertThat("Regex isn't correct", testRegex.toString(), CoreMatchers.equalTo("^(?:a)(?:b)?"));
        Assert.assertThat("Maybe has a 'b' after an 'a'", testRegex, TestMatchMatcher.matchesTo("acb"));
        Assert.assertThat("Maybe has a 'b' after an 'a'", testRegex, TestMatchMatcher.matchesTo("abc"));
        Assert.assertThat("Maybe has a 'b' after an 'a'", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("cab")));
    }

    @Test
    public void testAnyOf() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().then("a").anyOf("xyz").build();
        Assert.assertThat("Has an x, y, or z after a", testRegex, TestMatchMatcher.matchesTo("ay"));
        Assert.assertThat("Doesn't have an x, y, or z after a", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("abc")));
    }

    @Test
    public void testAnySameAsAnyOf() {
        VerbalExpression any = VerbalExpression.regex().any("abc").build();
        VerbalExpression anyOf = VerbalExpression.regex().anyOf("abc").build();
        Assert.assertThat("any differs from anyOf", any.toString(), CoreMatchers.equalTo(anyOf.toString()));
    }

    @Test
    public void testOr() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().then("abc").or("def").build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Doesn't start with abc or def", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("xyzabc")));
    }

    @Test
    public void testLineBreak() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().then("abc").lineBreak().then("def").build();
        Assert.assertThat("abc then line break then def", testRegex, TestMatchMatcher.matchesTo("abc\r\ndef"));
        Assert.assertThat("abc then line break then def", testRegex, TestMatchMatcher.matchesTo("abc\ndef"));
        Assert.assertThat("abc then line break then space then def", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("abc\r\n def")));
    }

    @Test
    public void testMacintoshLineBreak() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().then("abc").lineBreak().then("def").build();
        Assert.assertThat("abc then line break then def", testRegex, TestMatchMatcher.matchesTo("abc\r\rdef"));
    }

    @Test
    public void testBr() {
        VerbalExpression testRegexBr = new VerbalExpression.Builder().startOfLine().then("abc").br().then("def").build();
        VerbalExpression testRegexLineBr = new VerbalExpression.Builder().startOfLine().then("abc").lineBreak().then("def").build();
        Assert.assertThat(".br() differs from .lineBreak()", testRegexBr.toString(), CoreMatchers.equalTo(testRegexLineBr.toString()));
    }

    @Test
    public void testTab() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().tab().then("abc").build();
        Assert.assertThat("tab then abc", testRegex, TestMatchMatcher.matchesTo("\tabc"));
        Assert.assertThat("no tab then abc", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("abc")));
    }

    @Test
    public void testWord() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().word().build();
        Assert.assertThat("word", testRegex, TestMatchMatcher.matchesTo("abc123"));
        Assert.assertThat("non-word", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("@#")));
    }

    @Test
    public void testMultipleNoRange() {
        VerbalExpression testRegexStringOnly = new VerbalExpression.Builder().startOfLine().multiple("abc").build();
        VerbalExpression testRegexStringAndNull = new VerbalExpression.Builder().startOfLine().multiple("abc", null).build();
        VerbalExpression testRegexMoreThan2Ints = new VerbalExpression.Builder().startOfLine().multiple("abc", 2, 4, 8).build();
        VerbalExpression[] testRegexesSameBehavior = new VerbalExpression[]{ testRegexStringOnly, testRegexStringAndNull, testRegexMoreThan2Ints };
        for (VerbalExpression testRegex : testRegexesSameBehavior) {
            Assert.assertThat("abc once", testRegex, TestMatchMatcher.matchesTo("abc"));
            Assert.assertThat("abc more than once", testRegex, TestMatchMatcher.matchesTo("abcabcabc"));
            Assert.assertThat("no abc", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("xyz")));
        }
    }

    @Test
    public void testMultipleFrom() {
        VerbalExpression testRegexFrom = new VerbalExpression.Builder().startOfLine().multiple("abc", 2).build();
        Assert.assertThat("no abc", testRegexFrom, CoreMatchers.not(TestMatchMatcher.matchesTo("xyz")));
        Assert.assertThat("abc less than 2 times", testRegexFrom, CoreMatchers.not(TestMatchMatcher.matchesTo("abc")));
        Assert.assertThat("abc exactly 2 times", testRegexFrom, TestMatchMatcher.matchesTo("abcabc"));
        Assert.assertThat("abc more than 2 times", testRegexFrom, TestMatchMatcher.matchesTo("abcabcabc"));
    }

    @Test
    public void testMultipleFromTo() {
        VerbalExpression testRegexFromTo = new VerbalExpression.Builder().startOfLine().multiple("abc", 2, 4).build();
        Assert.assertThat("no abc", testRegexFromTo, CoreMatchers.not(TestMatchMatcher.matchesTo("xyz")));
        Assert.assertThat("abc less than 2 times", testRegexFromTo, CoreMatchers.not(TestMatchMatcher.matchesTo("abc")));
        Assert.assertThat("abc exactly 2 times", testRegexFromTo, TestMatchMatcher.matchesTo("abcabc"));
        Assert.assertThat("abc between 2 and 4 times", testRegexFromTo, TestMatchMatcher.matchesTo("abcabcabc"));
        Assert.assertThat("abc exactly 4 times", testRegexFromTo, TestMatchMatcher.matchesTo("abcabcabcabc"));
        Assert.assertThat("abc more than 4 times", testRegexFromTo, CoreMatchers.not(TestsExactMatcher.matchesExactly("abcabcabcabcabc")));
    }

    @Test
    public void testWithAnyCase() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().then("a").build();
        Assert.assertThat("not case insensitive", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("A")));
        testRegex = new VerbalExpression.Builder().startOfLine().then("a").withAnyCase().build();
        Assert.assertThat("case insensitive", testRegex, TestMatchMatcher.matchesTo("A"));
        Assert.assertThat("case insensitive", testRegex, TestMatchMatcher.matchesTo("a"));
    }

    @Test
    public void testWithAnyCaseTurnOnThenTurnOff() {
        VerbalExpression testRegex = VerbalExpression.regex().withAnyCase().startOfLine().then("a").withAnyCase(false).build();
        Assert.assertThat(testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("A")));
    }

    @Test
    public void testWithAnyCaseIsFalse() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().then("a").withAnyCase(false).build();
        Assert.assertThat(testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("A")));
    }

    @Test
    public void testSearchOneLine() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().then("a").br().then("b").endOfLine().build();
        Assert.assertThat("b is on the second line", testRegex, TestMatchMatcher.matchesTo("a\nb"));
        testRegex = new VerbalExpression.Builder().startOfLine().then("a").br().then("b").endOfLine().searchOneLine(true).build();
        Assert.assertThat("b is on the second line but we are only searching the first", testRegex, TestMatchMatcher.matchesTo("a\nb"));
    }

    @Test
    public void testGetText() {
        String testString = "123 https://www.google.com 456";
        VerbalExpression testRegex = new VerbalExpression.Builder().add("http").maybe("s").then("://").then("www.").anythingBut(" ").add("com").build();
        Assert.assertEquals(testRegex.getText(testString), "https://www.google.com");
    }

    @Test
    public void testStartCapture() {
        String text = "aaabcd";
        VerbalExpression regex = VerbalExpression.regex().find("a").count(3).capture().find("b").anything().build();
        Assert.assertThat("regex don't match string", regex.getText(text), CoreMatchers.equalTo(text));
        Assert.assertThat("can't get first captured group", regex.getText(text, 1), CoreMatchers.equalTo("bcd"));
    }

    @Test
    public void testStartNamedCapture() {
        String text = "test@example.com";
        String captureName = "domain";
        VerbalExpression regex = VerbalExpression.regex().find("@").capture(captureName).anything().build();
        Assert.assertThat(("can't get captured group named " + captureName), regex.getText(text, captureName), CoreMatchers.equalTo("example.com"));
    }

    @Test
    public void captIsSameAsCapture() {
        Assert.assertThat("Capt produce different than capture regex", VerbalExpression.regex().capt().build().toString(), CoreMatchers.equalTo(VerbalExpression.regex().capture().build().toString()));
    }

    @Test
    public void namedCaptIsSameAsNamedCapture() {
        String name = "test";
        Assert.assertThat("Named-capt produce different than named-capture regex", VerbalExpression.regex().capt(name).build().toString(), CoreMatchers.equalTo(VerbalExpression.regex().capture(name).build().toString()));
    }

    @Test
    public void shouldReturnEmptyStringWhenNoGroupFound() {
        String text = "abc";
        VerbalExpression regex = VerbalExpression.regex().find("d").capture().find("e").build();
        Assert.assertThat("regex don't match string", regex.getText(text), CoreMatchers.equalTo(""));
        Assert.assertThat("first captured group not empty string", regex.getText(text, 1), CoreMatchers.equalTo(""));
        Assert.assertThat("second captured group not empty string", regex.getText(text, 2), CoreMatchers.equalTo(""));
    }

    @Test
    public void testCountWithRange() {
        String text4c = "abcccce";
        String text2c = "abcce";
        String text1c = "abce";
        VerbalExpression regex = VerbalExpression.regex().find("c").count(2, 3).build();
        Assert.assertThat("regex don't match string", regex.getText(text4c), CoreMatchers.equalTo("ccc"));
        Assert.assertThat("regex don't match string", regex.getText(text2c), CoreMatchers.equalTo("cc"));
        Assert.assertThat("regex don't match string", regex, CoreMatchers.not(TestMatchMatcher.matchesTo(text1c)));
    }

    @Test
    public void testEndCapture() {
        String text = "aaabcd";
        VerbalExpression regex = VerbalExpression.regex().find("a").capture().find("b").anything().endCapture().then("cd").build();
        Assert.assertThat(regex.getText(text), CoreMatchers.equalTo("abcd"));
        Assert.assertThat("can't get first captured group", regex.getText(text, 1), CoreMatchers.equalTo("b"));
    }

    @Test
    public void testEndNamedCapture() {
        String text = "aaabcd";
        String captureName = "str";
        VerbalExpression regex = VerbalExpression.regex().find("a").capture(captureName).find("b").anything().endCapture().then("cd").build();
        Assert.assertThat(regex.getText(text), CoreMatchers.equalTo("abcd"));
        Assert.assertThat(("can't get captured group named " + captureName), regex.getText(text, captureName), CoreMatchers.equalTo("b"));
    }

    @Test
    public void testMultiplyCapture() {
        String text = "aaabcd";
        VerbalExpression regex = VerbalExpression.regex().find("a").count(1).capture().find("b").endCapture().anything().capture().find("d").build();
        Assert.assertThat("can't get first captured group", regex.getText(text, 1), CoreMatchers.equalTo("b"));
        Assert.assertThat("can't get second captured group", regex.getText(text, 2), CoreMatchers.equalTo("d"));
    }

    @Test
    public void testMultiplyNamedCapture() {
        String text = "aaabcd";
        String captureName1 = "str1";
        String captureName2 = "str2";
        VerbalExpression regex = VerbalExpression.regex().find("a").count(1).capture(captureName1).find("b").endCapture().anything().capture(captureName2).find("d").build();
        Assert.assertThat(("can't get captured group named " + captureName1), regex.getText(text, captureName1), CoreMatchers.equalTo("b"));
        Assert.assertThat(("can't get captured group named " + captureName2), regex.getText(text, captureName2), CoreMatchers.equalTo("d"));
    }

    @Test
    public void testOrWithCapture() {
        VerbalExpression testRegex = VerbalExpression.regex().capture().find("abc").or("def").build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        Assert.assertThat("Doesn't start with abc or def", testRegex, CoreMatchers.not(TestsExactMatcher.matchesExactly("xyzabcefg")));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcnull"));
        Assert.assertThat(testRegex.getText("xxxdefzzz", 1), CoreMatchers.equalTo("null"));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcnull"));
    }

    @Test
    public void testOrWithNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex = VerbalExpression.regex().capture(captureName).find("abc").or("def").build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        Assert.assertThat("Doesn't start with abc or def", testRegex, CoreMatchers.not(TestsExactMatcher.matchesExactly("xyzabcefg")));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcnull"));
        Assert.assertThat(testRegex.getText("xxxdefzzz", captureName), CoreMatchers.equalTo("null"));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcnull"));
    }

    @Test
    public void testOrWithClosedCapture() {
        VerbalExpression testRegex = VerbalExpression.regex().capture().find("abc").endCapt().or("def").build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        Assert.assertThat("Doesn't start with abc or def", testRegex, CoreMatchers.not(TestsExactMatcher.matchesExactly("xyzabcefg")));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcnull"));
        Assert.assertThat(testRegex.getText("xxxdefzzz", 1), CoreMatchers.equalTo("null"));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcnull"));
    }

    @Test
    public void testOrWithClosedNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex = VerbalExpression.regex().capture(captureName).find("abc").endCapt().or("def").build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        Assert.assertThat("Doesn't start with abc or def", testRegex, CoreMatchers.not(TestsExactMatcher.matchesExactly("xyzabcefg")));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcnull"));
        Assert.assertThat(testRegex.getText("xxxdefzzz", captureName), CoreMatchers.equalTo("null"));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcnull"));
    }

    @Test
    public void addRegexBuilderWrapsItWithUnsavedGroup() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().add(VerbalExpression.regex().capt().find("string").count(2).endCapt().count(1).digit()).count(2).build();
        Assert.assertThat("Added regex builder don't wrapped with unsaved group", regex.toString(), CoreMatchers.startsWith("(?:((?:string"));
        String example = "stringstring1";
        String example2digit = "stringstring11";
        Assert.assertThat(regex, TestsExactMatcher.matchesExactly((example + example)));
        Assert.assertThat(regex, CoreMatchers.not(TestsExactMatcher.matchesExactly(example2digit)));
    }

    @Test
    public void multiplyWith1NumProduceSameAsCountResult() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().multiple("a", 1).build();
        Assert.assertThat(regex, EqualToRegexMatcher.equalToRegex(VerbalExpression.regex().find("a").count(1)));
    }

    @Test
    public void multiplyWith2NumProduceSameAsCountRangeResult() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().multiple("a", 1, 2).build();
        Assert.assertThat(regex, EqualToRegexMatcher.equalToRegex(VerbalExpression.regex().find("a").count(1, 2)));
    }

    @Test
    public void atLeast1HaveSameEffectAsOneOrMore() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().find("a").atLeast(1).build();
        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";
        Assert.assertThat(regex, TestsExactMatcher.matchesExactly(matched));
        Assert.assertThat(regex, TestsExactMatcher.matchesExactly(oneMatchedExactly));
        Assert.assertThat(regex, CoreMatchers.not(TestsExactMatcher.matchesExactly(oneMatched)));
        Assert.assertThat(regex, TestMatchMatcher.matchesTo(oneMatched));
        Assert.assertThat(regex, CoreMatchers.not(TestMatchMatcher.matchesTo(empty)));
    }

    @Test
    public void oneOreMoreSameAsAtLeast1() throws Exception {
        VerbalExpression regexWithOneOrMore = VerbalExpression.regex().find("a").oneOrMore().build();
        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";
        Assert.assertThat(regexWithOneOrMore, TestsExactMatcher.matchesExactly(matched));
        Assert.assertThat(regexWithOneOrMore, TestsExactMatcher.matchesExactly(oneMatchedExactly));
        Assert.assertThat(regexWithOneOrMore, CoreMatchers.not(TestsExactMatcher.matchesExactly(oneMatched)));
        Assert.assertThat(regexWithOneOrMore, TestMatchMatcher.matchesTo(oneMatched));
        Assert.assertThat(regexWithOneOrMore, CoreMatchers.not(TestMatchMatcher.matchesTo(empty)));
    }

    @Test
    public void atLeast0HaveSameEffectAsZeroOrMore() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().find("a").atLeast(0).build();
        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";
        Assert.assertThat(regex, TestsExactMatcher.matchesExactly(matched));
        Assert.assertThat(regex, TestsExactMatcher.matchesExactly(oneMatchedExactly));
        Assert.assertThat(regex, CoreMatchers.not(TestsExactMatcher.matchesExactly(oneMatched)));
        Assert.assertThat(regex, TestMatchMatcher.matchesTo(empty));
        Assert.assertThat(regex, TestsExactMatcher.matchesExactly(empty));
    }

    @Test
    public void zeroOreMoreSameAsAtLeast0() throws Exception {
        VerbalExpression regexWithOneOrMore = VerbalExpression.regex().find("a").zeroOrMore().build();
        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";
        Assert.assertThat(regexWithOneOrMore, TestsExactMatcher.matchesExactly(matched));
        Assert.assertThat(regexWithOneOrMore, TestsExactMatcher.matchesExactly(oneMatchedExactly));
        Assert.assertThat(regexWithOneOrMore, CoreMatchers.not(TestsExactMatcher.matchesExactly(oneMatched)));
        Assert.assertThat(regexWithOneOrMore, TestMatchMatcher.matchesTo(oneMatched));
        Assert.assertThat(regexWithOneOrMore, TestMatchMatcher.matchesTo(empty));
        Assert.assertThat(regexWithOneOrMore, TestsExactMatcher.matchesExactly(empty));
    }

    @Test
    public void testOneOf() {
        VerbalExpression testRegex = new VerbalExpression.Builder().startOfLine().oneOf("abc", "def").build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        Assert.assertThat("Doesn't start with abc nor def", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("xyzabc")));
    }

    @Test
    public void testOneOfWithCapture() {
        VerbalExpression testRegex = VerbalExpression.regex().capture().oneOf("abc", "def").build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        Assert.assertThat("Doesn't start with abc or def", testRegex, CoreMatchers.not(TestsExactMatcher.matchesExactly("xyzabcefg")));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcdef"));
        Assert.assertThat(testRegex.getText("xxxdefzzz", 1), CoreMatchers.equalTo("def"));
    }

    @Test
    public void testOneOfWithNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex = VerbalExpression.regex().capture(captureName).oneOf("abc", "def").build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        Assert.assertThat("Doesn't start with abc or def", testRegex, CoreMatchers.not(TestsExactMatcher.matchesExactly("xyzabcefg")));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcdef"));
        Assert.assertThat(testRegex.getText("xxxdefzzz", captureName), CoreMatchers.equalTo("def"));
    }

    @Test
    public void testOneOfWithClosedCapture() {
        VerbalExpression testRegex = VerbalExpression.regex().capture().oneOf("abc", "def").endCapt().build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        Assert.assertThat("Doesn't start with abc or def", testRegex, CoreMatchers.not(TestsExactMatcher.matchesExactly("xyzabcefg")));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcdef"));
        Assert.assertThat(testRegex.getText("xxxdefzzz", 1), CoreMatchers.equalTo("def"));
    }

    @Test
    public void testOneOfWithClosedNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex = VerbalExpression.regex().capture(captureName).oneOf("abc", "def").endCapt().build();
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        Assert.assertThat("Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        Assert.assertThat("Doesn't start with abc or def", testRegex, CoreMatchers.not(TestsExactMatcher.matchesExactly("xyzabcefg")));
        Assert.assertThat(testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcdef"));
        Assert.assertThat(testRegex.getText("xxxdefzzz", captureName), CoreMatchers.equalTo("def"));
    }

    @Test
    public void shouldAddMaybeWithOneOfFromAnotherBuilder() {
        VerbalExpression.Builder namePrefix = VerbalExpression.regex().oneOf("Mr.", "Ms.");
        VerbalExpression name = VerbalExpression.regex().maybe(namePrefix).space().zeroOrMore().word().oneOrMore().build();
        Assert.assertThat("Is a name with prefix", name, TestMatchMatcher.matchesTo("Mr. Bond"));
        Assert.assertThat("Is a name without prefix", name, TestMatchMatcher.matchesTo("James"));
    }

    @Test
    public void testListOfTextGroups() {
        String text = "SampleHelloWorldString";
        VerbalExpression regex = VerbalExpression.regex().capt().oneOf("Hello", "World").endCapt().maybe("String").build();
        List<String> groups0 = regex.getTextGroups(text, 0);
        Assert.assertThat(groups0.get(0), CoreMatchers.equalTo("Hello"));
        Assert.assertThat(groups0.get(1), CoreMatchers.equalTo("WorldString"));
        List<String> groups1 = regex.getTextGroups(text, 1);
        Assert.assertThat(groups1.get(0), CoreMatchers.equalTo("Hello"));
        Assert.assertThat(groups1.get(1), CoreMatchers.equalTo("World"));
    }
}

