package org.jsoup.parser;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test suite for character reader.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class AmplCharacterReaderTest {
    @Test(timeout = 10000)
    public void consumeToEnd() {
        String in = "one two three";
        CharacterReader r = new CharacterReader(in);
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        // AssertGenerator add assertion
        Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertEquals("one two three", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequence() {
        CharacterReader r = new CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        String o_consumeLetterSequence__3 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        Assert.assertEquals("One", o_consumeLetterSequence__3);
        // AssertGenerator create local variable with return value of invocation
        String o_consumeLetterSequence__4 = r.consumeTo("bar;");
        // AssertGenerator add assertion
        Assert.assertEquals(" &", o_consumeLetterSequence__4);
        // AssertGenerator create local variable with return value of invocation
        String o_consumeLetterSequence__5 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        Assert.assertEquals("bar", o_consumeLetterSequence__5);
        // AssertGenerator create local variable with return value of invocation
        String o_consumeLetterSequence__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        Assert.assertEquals("; qux", o_consumeLetterSequence__6);
        // AssertGenerator add assertion
        Assert.assertEquals("bar", o_consumeLetterSequence__5);
        // AssertGenerator add assertion
        Assert.assertEquals(" &", o_consumeLetterSequence__4);
        // AssertGenerator add assertion
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertEquals("One", o_consumeLetterSequence__3);
    }

    @Test(timeout = 10000)
    public void nextIndexOfString() {
        String in = "One Two something Two Three Four";
        // AssertGenerator add assertion
        Assert.assertEquals("One Two something Two Three Four", in);
        CharacterReader r = new CharacterReader(in);
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        int o_nextIndexOfString__4 = r.nextIndexOf("Foo");
        // AssertGenerator add assertion
        Assert.assertEquals(-1, ((int) (o_nextIndexOfString__4)));
        // AssertGenerator create local variable with return value of invocation
        int o_nextIndexOfString__5 = r.nextIndexOf("Two");
        // AssertGenerator add assertion
        Assert.assertEquals(4, ((int) (o_nextIndexOfString__5)));
        // AssertGenerator create local variable with return value of invocation
        String o_nextIndexOfString__6 = r.consumeTo("something");
        // AssertGenerator add assertion
        Assert.assertEquals("One Two ", o_nextIndexOfString__6);
        // AssertGenerator create local variable with return value of invocation
        int o_nextIndexOfString__7 = r.nextIndexOf("Two");
        // AssertGenerator add assertion
        Assert.assertEquals(10, ((int) (o_nextIndexOfString__7)));
        // AssertGenerator create local variable with return value of invocation
        String o_nextIndexOfString__8 = r.consumeToEnd();
        // AssertGenerator add assertion
        Assert.assertEquals("something Two Three Four", o_nextIndexOfString__8);
        // AssertGenerator create local variable with return value of invocation
        int o_nextIndexOfString__9 = r.nextIndexOf("Two");
        // AssertGenerator add assertion
        Assert.assertEquals(-1, ((int) (o_nextIndexOfString__9)));
        // AssertGenerator add assertion
        Assert.assertEquals(10, ((int) (o_nextIndexOfString__7)));
        // AssertGenerator add assertion
        Assert.assertEquals("One Two ", o_nextIndexOfString__6);
        // AssertGenerator add assertion
        Assert.assertEquals("something Two Three Four", o_nextIndexOfString__8);
        // AssertGenerator add assertion
        Assert.assertEquals(4, ((int) (o_nextIndexOfString__5)));
        // AssertGenerator add assertion
        Assert.assertEquals(-1, ((int) (o_nextIndexOfString__4)));
        // AssertGenerator add assertion
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(32, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertEquals("One Two something Two Three Four", in);
    }

    @Test(timeout = 10000)
    public void containsIgnoreCase() {
        CharacterReader r = new CharacterReader("One TWO three");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase__3 = r.containsIgnoreCase("two");
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase__4 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        Assert.assertTrue(o_containsIgnoreCase__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase__5 = r.containsIgnoreCase("one");
        // AssertGenerator add assertion
        Assert.assertFalse(o_containsIgnoreCase__5);
        // AssertGenerator add assertion
        Assert.assertTrue(o_containsIgnoreCase__4);
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertTrue(o_containsIgnoreCase__3);
    }

    @Test(timeout = 10000)
    public void cachesStrings() {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        String one = r.consumeTo('\t');
        // AssertGenerator add assertion
        Assert.assertEquals("Check", one);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings__5 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('\t', ((char) (o_cachesStrings__5)));
        String two = r.consumeTo('\t');
        // AssertGenerator add assertion
        Assert.assertEquals("Check", two);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings__8 = r.consume();
        String three = r.consumeTo('\t');
        // AssertGenerator add assertion
        Assert.assertEquals("Check", three);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings__11 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('\t', ((char) (o_cachesStrings__11)));
        String four = r.consumeTo('\t');
        // AssertGenerator add assertion
        Assert.assertEquals("CHOKE", four);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings__14 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('\t', ((char) (o_cachesStrings__14)));
        String five = r.consumeTo('\t');
        boolean boolean_0 = one == two;
        boolean boolean_1 = two == three;
        boolean boolean_2 = three != four;
        boolean boolean_3 = four != five;
        // AssertGenerator add assertion
        Assert.assertEquals("Check", three);
        // AssertGenerator add assertion
        Assert.assertEquals("CHOKE", four);
        // AssertGenerator add assertion
        Assert.assertEquals("Check", one);
        // AssertGenerator add assertion
        Assert.assertEquals('\t', ((char) (o_cachesStrings__5)));
        // AssertGenerator add assertion
        Assert.assertEquals('\t', ((char) (o_cachesStrings__8)));
        // AssertGenerator add assertion
        Assert.assertEquals("Check", two);
        // AssertGenerator add assertion
        Assert.assertEquals("A string that is longer than 16 chars", five);
        // AssertGenerator add assertion
        Assert.assertEquals('\t', ((char) (o_cachesStrings__14)));
        // AssertGenerator add assertion
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(61, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertEquals('\t', ((char) (o_cachesStrings__11)));
    }

    @Test(timeout = 10000)
    public void consumeToChar() {
        CharacterReader r = new CharacterReader("One Two Three");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        String o_consumeToChar__3 = r.consumeTo('T');
        // AssertGenerator add assertion
        Assert.assertEquals("One ", o_consumeToChar__3);
        // AssertGenerator create local variable with return value of invocation
        String o_consumeToChar__4 = r.consumeTo('T');
        // AssertGenerator add assertion
        Assert.assertEquals("", o_consumeToChar__4);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar__5 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('T', ((char) (o_consumeToChar__5)));
        // AssertGenerator create local variable with return value of invocation
        String o_consumeToChar__6 = r.consumeTo('T');
        // AssertGenerator add assertion
        Assert.assertEquals("wo ", o_consumeToChar__6);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        String o_consumeToChar__8 = r.consumeTo('T');
        // AssertGenerator add assertion
        Assert.assertEquals("hree", o_consumeToChar__8);
        // AssertGenerator add assertion
        Assert.assertEquals('T', ((char) (o_consumeToChar__7)));
        // AssertGenerator add assertion
        Assert.assertEquals("wo ", o_consumeToChar__6);
        // AssertGenerator add assertion
        Assert.assertEquals('T', ((char) (o_consumeToChar__5)));
        // AssertGenerator add assertion
        Assert.assertEquals("", o_consumeToChar__4);
        // AssertGenerator add assertion
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertEquals("One ", o_consumeToChar__3);
    }

    @Test(timeout = 10000)
    public void matches() {
        CharacterReader r = new CharacterReader("One Two Three");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__3 = r.matches('O');
        // AssertGenerator add assertion
        Assert.assertTrue(o_matches__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__5 = r.matches("One");
        // AssertGenerator add assertion
        Assert.assertTrue(o_matches__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__6 = r.matches("one");
        // AssertGenerator add assertion
        Assert.assertFalse(o_matches__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matches__7 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (o_matches__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__8 = r.matches("One");
        // AssertGenerator add assertion
        Assert.assertFalse(o_matches__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__9 = r.matches("ne Two Three");
        // AssertGenerator add assertion
        Assert.assertTrue(o_matches__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__10 = r.matches("ne Two Three Four");
        // AssertGenerator add assertion
        Assert.assertFalse(o_matches__10);
        // AssertGenerator create local variable with return value of invocation
        String o_matches__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        Assert.assertEquals("ne Two Three", o_matches__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__12 = r.matches("ne");
        // AssertGenerator add assertion
        Assert.assertFalse(o_matches__12);
        // AssertGenerator add assertion
        Assert.assertFalse(o_matches__6);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matches__5);
        // AssertGenerator add assertion
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertTrue(o_matches__3);
        // AssertGenerator add assertion
        Assert.assertFalse(o_matches__8);
        // AssertGenerator add assertion
        Assert.assertEquals("ne Two Three", o_matches__11);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matches__9);
        // AssertGenerator add assertion
        Assert.assertFalse(o_matches__10);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matches__4);
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (o_matches__7)));
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCase() {
        CharacterReader r = new CharacterReader("One Two Three");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__3 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__4 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__5 = r.matches('O');
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__6 = r.matches('o');
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesIgnoreCase__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__9 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__10 = r.matchesIgnoreCase("one");
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__10);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase__11 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (o_matchesIgnoreCase__11)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__12 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesIgnoreCase__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesIgnoreCase__14);
        // AssertGenerator create local variable with return value of invocation
        String o_matchesIgnoreCase__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        Assert.assertEquals("ne Two Three", o_matchesIgnoreCase__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesIgnoreCase__16);
        // AssertGenerator add assertion
        Assert.assertEquals("ne Two Three", o_matchesIgnoreCase__15);
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesIgnoreCase__14);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__3);
        // AssertGenerator add assertion
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__5);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__9);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__4);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__10);
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesIgnoreCase__6);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__13);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__8);
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesIgnoreCase__12);
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (o_matchesIgnoreCase__11)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesIgnoreCase__7);
    }

    @Test(timeout = 10000)
    public void consumeToString() {
        CharacterReader r = new CharacterReader("One Two Two Four");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        String o_consumeToString__3 = r.consumeTo("Two");
        // AssertGenerator add assertion
        Assert.assertEquals("One ", o_consumeToString__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString__4 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('T', ((char) (o_consumeToString__4)));
        // AssertGenerator create local variable with return value of invocation
        String o_consumeToString__5 = r.consumeTo("Two");
        // AssertGenerator add assertion
        Assert.assertEquals("wo ", o_consumeToString__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString__6 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('T', ((char) (o_consumeToString__6)));
        // AssertGenerator create local variable with return value of invocation
        String o_consumeToString__7 = r.consumeTo("Qux");
        // AssertGenerator add assertion
        Assert.assertEquals("wo Four", o_consumeToString__7);
        // AssertGenerator add assertion
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(16, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertEquals("One ", o_consumeToString__3);
        // AssertGenerator add assertion
        Assert.assertEquals('T', ((char) (o_consumeToString__4)));
        // AssertGenerator add assertion
        Assert.assertEquals("wo ", o_consumeToString__5);
        // AssertGenerator add assertion
        Assert.assertEquals('T', ((char) (o_consumeToString__6)));
    }

    @Test(timeout = 10000)
    public void rangeEquals() {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator add assertion
        Assert.assertTrue(o_rangeEquals__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator add assertion
        Assert.assertFalse(o_rangeEquals__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator add assertion
        Assert.assertFalse(o_rangeEquals__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__6 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator add assertion
        Assert.assertTrue(o_rangeEquals__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__7 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator add assertion
        Assert.assertFalse(o_rangeEquals__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator add assertion
        Assert.assertTrue(o_rangeEquals__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__9 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator add assertion
        Assert.assertFalse(o_rangeEquals__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__10 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator add assertion
        Assert.assertTrue(o_rangeEquals__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__11 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator add assertion
        Assert.assertFalse(o_rangeEquals__11);
        // AssertGenerator add assertion
        Assert.assertTrue(o_rangeEquals__6);
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertTrue(o_rangeEquals__3);
        // AssertGenerator add assertion
        Assert.assertFalse(o_rangeEquals__9);
        // AssertGenerator add assertion
        Assert.assertFalse(o_rangeEquals__4);
        // AssertGenerator add assertion
        Assert.assertTrue(o_rangeEquals__10);
        // AssertGenerator add assertion
        Assert.assertFalse(o_rangeEquals__5);
        // AssertGenerator add assertion
        Assert.assertTrue(o_rangeEquals__8);
        // AssertGenerator add assertion
        Assert.assertFalse(o_rangeEquals__7);
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatched() {
        CharacterReader r = new CharacterReader("<[[one]]");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        int o_nextIndexOfUnmatched__3 = r.nextIndexOf("]]>");
        // AssertGenerator add assertion
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatched__3)));
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    @Test(timeout = 10000)
    public void matchesAny() {
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny__4 = r.matchesAny(scan);
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesAny__4);
        // AssertGenerator create local variable with return value of invocation
        String o_matchesAny__5 = r.consumeToAny(scan);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny__6 = r.matchesAny(scan);
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesAny__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny__7 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('\n', ((char) (o_matchesAny__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesAny__8);
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertFalse(o_matchesAny__4);
        // AssertGenerator add assertion
        Assert.assertEquals('\n', ((char) (o_matchesAny__7)));
        // AssertGenerator add assertion
        Assert.assertTrue(o_matchesAny__6);
        // AssertGenerator add assertion
        Assert.assertEquals("One", o_matchesAny__5);
    }

    @Test(timeout = 10000)
    public void mark() {
        CharacterReader r = new CharacterReader("one");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark__5 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('n', ((char) (o_mark__5)));
        // AssertGenerator create local variable with return value of invocation
        char o_mark__6 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('e', ((char) (o_mark__6)));
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark__9 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals('n', ((char) (o_mark__9)));
        // AssertGenerator add assertion
        Assert.assertEquals('o', ((char) (o_mark__3)));
        // AssertGenerator add assertion
        Assert.assertEquals('e', ((char) (o_mark__6)));
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('e', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertEquals('n', ((char) (o_mark__5)));
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence() {
        CharacterReader r = new CharacterReader("One12 Two &bar; qux");
        // AssertGenerator add assertion
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        String o_consumeLetterThenDigitSequence__3 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        Assert.assertEquals("One12", o_consumeLetterThenDigitSequence__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence__4 = r.consume();
        // AssertGenerator add assertion
        Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence__4)));
        // AssertGenerator create local variable with return value of invocation
        String o_consumeLetterThenDigitSequence__5 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        Assert.assertEquals("Two", o_consumeLetterThenDigitSequence__5);
        // AssertGenerator create local variable with return value of invocation
        String o_consumeLetterThenDigitSequence__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence__6);
        // AssertGenerator add assertion
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        Assert.assertEquals(19, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence__4)));
        // AssertGenerator add assertion
        Assert.assertEquals("One12", o_consumeLetterThenDigitSequence__3);
        // AssertGenerator add assertion
        Assert.assertEquals("Two", o_consumeLetterThenDigitSequence__5);
    }
}

