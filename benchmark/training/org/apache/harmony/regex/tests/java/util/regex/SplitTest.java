package org.apache.harmony.regex.tests.java.util.regex;


import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import junit.framework.TestCase;


/**
 * TODO Type description
 */
public class SplitTest extends TestCase {
    public void testSimple() {
        Pattern p = Pattern.compile("/");
        String[] results = p.split("have/you/done/it/right");
        String[] expected = new String[]{ "have", "you", "done", "it", "right" };
        assertArraysEqual(expected, results);
    }

    public void testEmptySplits() {
        // Trailing empty matches are removed.
        assertArraysEqual(new String[0], "hello".split("."));
        assertArraysEqual(new String[]{ "1", "2" }, "1:2:".split(":"));
        // ...including when that results in an empty result.
        assertArraysEqual(new String[0], ":".split(":"));
        // ...but not when limit < 0.
        assertArraysEqual(new String[]{ "1", "2", "" }, "1:2:".split(":", (-1)));
        // Leading empty matches are retained.
        assertArraysEqual(new String[]{ "", "", "o" }, "hello".split(".."));
        // A separator that doesn't occur in the input gets you the input.
        assertArraysEqual(new String[]{ "hello" }, "hello".split("not-present-in-test"));
        // ...including when the input is the empty string.
        // (Perl returns an empty list instead.)
        assertArraysEqual(new String[]{ "" }, "".split("not-present-in-test"));
        assertArraysEqual(new String[]{ "" }, "".split("A?"));
        // The limit argument controls the size of the result.
        // If l == 0, the result is as long as needed, except trailing empty matches are dropped.
        // If l < 0, the result is as long as needed, and trailing empty matches are retained.
        // If l > 0, the result contains the first l matches, plus one string containing the remaining input.
        // Examples without a trailing separator (and hence without a trailing empty match):
        assertArraysEqual(new String[]{ "a", "b", "c" }, "a,b,c".split(",", 0));
        assertArraysEqual(new String[]{ "a,b,c" }, "a,b,c".split(",", 1));
        assertArraysEqual(new String[]{ "a", "b,c" }, "a,b,c".split(",", 2));
        assertArraysEqual(new String[]{ "a", "b", "c" }, "a,b,c".split(",", 3));
        assertArraysEqual(new String[]{ "a", "b", "c" }, "a,b,c".split(",", Integer.MAX_VALUE));
        // Examples with a trailing separator (and hence possibly with a trailing empty match):
        assertArraysEqual(new String[]{ "a", "b", "c" }, "a,b,c,".split(",", 0));
        assertArraysEqual(new String[]{ "a,b,c," }, "a,b,c,".split(",", 1));
        assertArraysEqual(new String[]{ "a", "b,c," }, "a,b,c,".split(",", 2));
        assertArraysEqual(new String[]{ "a", "b", "c," }, "a,b,c,".split(",", 3));
        assertArraysEqual(new String[]{ "a", "b", "c", "" }, "a,b,c,".split(",", Integer.MAX_VALUE));
        assertArraysEqual(new String[]{ "a", "b", "c", "" }, "a,b,c,".split(",", (-1)));
    }

    public void testSplit1() throws PatternSyntaxException {
        Pattern p = Pattern.compile(" ");
        String input = "poodle zoo";
        String[] tokens;
        tokens = p.split(input, 1);
        TestCase.assertEquals(1, tokens.length);
        TestCase.assertTrue(tokens[0].equals(input));
        tokens = p.split(input, 2);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        tokens = p.split(input, 5);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        tokens = p.split(input, (-2));
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        tokens = p.split(input, 0);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        tokens = p.split(input);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poodle", tokens[0]);
        TestCase.assertEquals("zoo", tokens[1]);
        p = Pattern.compile("d");
        tokens = p.split(input, 1);
        TestCase.assertEquals(1, tokens.length);
        TestCase.assertTrue(tokens[0].equals(input));
        tokens = p.split(input, 2);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input, 5);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input, (-2));
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input, 0);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        tokens = p.split(input);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("poo", tokens[0]);
        TestCase.assertEquals("le zoo", tokens[1]);
        p = Pattern.compile("o");
        tokens = p.split(input, 1);
        TestCase.assertEquals(1, tokens.length);
        TestCase.assertTrue(tokens[0].equals(input));
        tokens = p.split(input, 2);
        TestCase.assertEquals(2, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertEquals("odle zoo", tokens[1]);
        tokens = p.split(input, 5);
        TestCase.assertEquals(5, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertTrue(tokens[1].equals(""));
        TestCase.assertEquals("dle z", tokens[2]);
        TestCase.assertTrue(tokens[3].equals(""));
        TestCase.assertTrue(tokens[4].equals(""));
        tokens = p.split(input, (-2));
        TestCase.assertEquals(5, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertTrue(tokens[1].equals(""));
        TestCase.assertEquals("dle z", tokens[2]);
        TestCase.assertTrue(tokens[3].equals(""));
        TestCase.assertTrue(tokens[4].equals(""));
        tokens = p.split(input, 0);
        TestCase.assertEquals(3, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertTrue(tokens[1].equals(""));
        TestCase.assertEquals("dle z", tokens[2]);
        tokens = p.split(input);
        TestCase.assertEquals(3, tokens.length);
        TestCase.assertEquals("p", tokens[0]);
        TestCase.assertTrue(tokens[1].equals(""));
        TestCase.assertEquals("dle z", tokens[2]);
    }

    public void testSplit2() {
        Pattern p = Pattern.compile("");
        String[] s;
        s = p.split("a", (-1));
        TestCase.assertEquals(3, s.length);
        TestCase.assertEquals("", s[0]);
        TestCase.assertEquals("a", s[1]);
        TestCase.assertEquals("", s[2]);
        s = p.split("", (-1));
        TestCase.assertEquals(1, s.length);
        TestCase.assertEquals("", s[0]);
        s = p.split("abcd", (-1));
        TestCase.assertEquals(6, s.length);
        TestCase.assertEquals("", s[0]);
        TestCase.assertEquals("a", s[1]);
        TestCase.assertEquals("b", s[2]);
        TestCase.assertEquals("c", s[3]);
        TestCase.assertEquals("d", s[4]);
        TestCase.assertEquals("", s[5]);
        // Regression test for Android
        TestCase.assertEquals("GOOG,23,500".split("|").length, 12);
    }

    public void testSplitSupplementaryWithEmptyString() {
        /* See http://www.unicode.org/reports/tr18/#Supplementary_Characters
        We have to treat text as code points not code units.
         */
        Pattern p = Pattern.compile("");
        String[] s;
        s = p.split("a\ud869\uded6b", (-1));
        TestCase.assertEquals(5, s.length);
        TestCase.assertEquals("", s[0]);
        TestCase.assertEquals("a", s[1]);
        TestCase.assertEquals("\ud869\uded6", s[2]);
        TestCase.assertEquals("b", s[3]);
        TestCase.assertEquals("", s[4]);
    }
}

