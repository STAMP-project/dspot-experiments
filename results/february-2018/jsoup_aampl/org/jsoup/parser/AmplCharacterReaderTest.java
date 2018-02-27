package org.jsoup.parser;


import org.junit.Assert;
import org.junit.Test;


public class AmplCharacterReaderTest {
    @Test(timeout = 10000)
    public void consumeToEnd() {
        String in = "one two three";
        Assert.assertEquals("one two three", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        String toEnd = r.consumeToEnd();
        Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        Assert.assertEquals("one two three", in);
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertEquals("one two three", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequence() {
        CharacterReader r = new CharacterReader("One &bar; qux");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        String o_consumeLetterSequence__3 = r.consumeLetterSequence();
        Assert.assertEquals("One", o_consumeLetterSequence__3);
        String o_consumeLetterSequence__4 = r.consumeTo("bar;");
        Assert.assertEquals(" &", o_consumeLetterSequence__4);
        String o_consumeLetterSequence__5 = r.consumeLetterSequence();
        Assert.assertEquals("bar", o_consumeLetterSequence__5);
        String o_consumeLetterSequence__6 = r.consumeToEnd();
        Assert.assertEquals("; qux", o_consumeLetterSequence__6);
        Assert.assertEquals("bar", o_consumeLetterSequence__5);
        Assert.assertEquals(" &", o_consumeLetterSequence__4);
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertEquals("One", o_consumeLetterSequence__3);
    }

    @Test(timeout = 10000)
    public void containsIgnoreCase() {
        CharacterReader r = new CharacterReader("One TWO three");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        boolean o_containsIgnoreCase__3 = r.containsIgnoreCase("two");
        Assert.assertTrue(o_containsIgnoreCase__3);
        boolean o_containsIgnoreCase__4 = r.containsIgnoreCase("three");
        Assert.assertTrue(o_containsIgnoreCase__4);
        boolean o_containsIgnoreCase__5 = r.containsIgnoreCase("one");
        Assert.assertFalse(o_containsIgnoreCase__5);
        Assert.assertTrue(o_containsIgnoreCase__4);
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertTrue(o_containsIgnoreCase__3);
    }

    @Test(timeout = 10000)
    public void cachesStrings() {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        String one = r.consumeTo('\t');
        Assert.assertEquals("Check", one);
        char o_cachesStrings__5 = r.consume();
        Assert.assertEquals('\t', ((char) (o_cachesStrings__5)));
        String two = r.consumeTo('\t');
        Assert.assertEquals("Check", two);
        char o_cachesStrings__8 = r.consume();
        Assert.assertEquals('\t', ((char) (o_cachesStrings__8)));
        String three = r.consumeTo('\t');
        Assert.assertEquals("Check", three);
        char o_cachesStrings__11 = r.consume();
        Assert.assertEquals('\t', ((char) (o_cachesStrings__11)));
        String four = r.consumeTo('\t');
        Assert.assertEquals("CHOKE", four);
        char o_cachesStrings__14 = r.consume();
        Assert.assertEquals('\t', ((char) (o_cachesStrings__14)));
        String five = r.consumeTo('\t');
        Assert.assertEquals("A string that is longer than 16 chars", five);
        boolean boolean_0 = one == two;
        boolean boolean_1 = two == three;
        boolean boolean_2 = three != four;
        boolean boolean_3 = four != five;
        Assert.assertEquals("Check", three);
        Assert.assertEquals("CHOKE", four);
        Assert.assertEquals("Check", one);
        Assert.assertEquals('\t', ((char) (o_cachesStrings__5)));
        Assert.assertEquals('\t', ((char) (o_cachesStrings__8)));
        Assert.assertEquals("Check", two);
        Assert.assertEquals("A string that is longer than 16 chars", five);
        Assert.assertEquals('\t', ((char) (o_cachesStrings__14)));
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(61, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertEquals('\t', ((char) (o_cachesStrings__11)));
    }

    @Test(timeout = 10000)
    public void matches() {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        boolean o_matches__3 = r.matches('O');
        Assert.assertTrue(o_matches__3);
        boolean o_matches__4 = r.matches("One Two Three");
        Assert.assertTrue(o_matches__4);
        boolean o_matches__5 = r.matches("One");
        Assert.assertTrue(o_matches__5);
        boolean o_matches__6 = r.matches("one");
        Assert.assertFalse(o_matches__6);
        char o_matches__7 = r.consume();
        Assert.assertEquals('O', ((char) (o_matches__7)));
        boolean o_matches__8 = r.matches("One");
        Assert.assertFalse(o_matches__8);
        boolean o_matches__9 = r.matches("ne Two Three");
        Assert.assertTrue(o_matches__9);
        boolean o_matches__10 = r.matches("ne Two Three Four");
        Assert.assertFalse(o_matches__10);
        String o_matches__11 = r.consumeToEnd();
        Assert.assertEquals("ne Two Three", o_matches__11);
        boolean o_matches__12 = r.matches("ne");
        Assert.assertFalse(o_matches__12);
        Assert.assertFalse(o_matches__6);
        Assert.assertTrue(o_matches__5);
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertTrue(o_matches__3);
        Assert.assertFalse(o_matches__8);
        Assert.assertEquals("ne Two Three", o_matches__11);
        Assert.assertTrue(o_matches__9);
        Assert.assertFalse(o_matches__10);
        Assert.assertTrue(o_matches__4);
        Assert.assertEquals('O', ((char) (o_matches__7)));
    }

    @Test(timeout = 10000)
    public void consumeToChar() {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        String o_consumeToChar__3 = r.consumeTo('T');
        Assert.assertEquals("One ", o_consumeToChar__3);
        String o_consumeToChar__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar__4);
        char o_consumeToChar__5 = r.consume();
        Assert.assertEquals('T', ((char) (o_consumeToChar__5)));
        String o_consumeToChar__6 = r.consumeTo('T');
        Assert.assertEquals("wo ", o_consumeToChar__6);
        char o_consumeToChar__7 = r.consume();
        Assert.assertEquals('T', ((char) (o_consumeToChar__7)));
        String o_consumeToChar__8 = r.consumeTo('T');
        Assert.assertEquals("hree", o_consumeToChar__8);
        Assert.assertEquals('T', ((char) (o_consumeToChar__7)));
        Assert.assertEquals("wo ", o_consumeToChar__6);
        Assert.assertEquals('T', ((char) (o_consumeToChar__5)));
        Assert.assertEquals("", o_consumeToChar__4);
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertEquals("One ", o_consumeToChar__3);
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCase() {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        boolean o_matchesIgnoreCase__3 = r.matchesIgnoreCase("O");
        Assert.assertTrue(o_matchesIgnoreCase__3);
        boolean o_matchesIgnoreCase__4 = r.matchesIgnoreCase("o");
        Assert.assertTrue(o_matchesIgnoreCase__4);
        boolean o_matchesIgnoreCase__5 = r.matches('O');
        Assert.assertTrue(o_matchesIgnoreCase__5);
        boolean o_matchesIgnoreCase__6 = r.matches('o');
        Assert.assertFalse(o_matchesIgnoreCase__6);
        boolean o_matchesIgnoreCase__7 = r.matchesIgnoreCase("One Two Three");
        Assert.assertTrue(o_matchesIgnoreCase__7);
        boolean o_matchesIgnoreCase__8 = r.matchesIgnoreCase("ONE two THREE");
        Assert.assertTrue(o_matchesIgnoreCase__8);
        boolean o_matchesIgnoreCase__9 = r.matchesIgnoreCase("One");
        Assert.assertTrue(o_matchesIgnoreCase__9);
        boolean o_matchesIgnoreCase__10 = r.matchesIgnoreCase("one");
        Assert.assertTrue(o_matchesIgnoreCase__10);
        char o_matchesIgnoreCase__11 = r.consume();
        Assert.assertEquals('O', ((char) (o_matchesIgnoreCase__11)));
        boolean o_matchesIgnoreCase__12 = r.matchesIgnoreCase("One");
        Assert.assertFalse(o_matchesIgnoreCase__12);
        boolean o_matchesIgnoreCase__13 = r.matchesIgnoreCase("NE Two Three");
        Assert.assertTrue(o_matchesIgnoreCase__13);
        boolean o_matchesIgnoreCase__14 = r.matchesIgnoreCase("ne Two Three Four");
        Assert.assertFalse(o_matchesIgnoreCase__14);
        String o_matchesIgnoreCase__15 = r.consumeToEnd();
        Assert.assertEquals("ne Two Three", o_matchesIgnoreCase__15);
        boolean o_matchesIgnoreCase__16 = r.matchesIgnoreCase("ne");
        Assert.assertFalse(o_matchesIgnoreCase__16);
        Assert.assertEquals("ne Two Three", o_matchesIgnoreCase__15);
        Assert.assertFalse(o_matchesIgnoreCase__14);
        Assert.assertTrue(o_matchesIgnoreCase__3);
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertTrue(o_matchesIgnoreCase__5);
        Assert.assertTrue(o_matchesIgnoreCase__9);
        Assert.assertTrue(o_matchesIgnoreCase__4);
        Assert.assertTrue(o_matchesIgnoreCase__10);
        Assert.assertFalse(o_matchesIgnoreCase__6);
        Assert.assertTrue(o_matchesIgnoreCase__13);
        Assert.assertTrue(o_matchesIgnoreCase__8);
        Assert.assertFalse(o_matchesIgnoreCase__12);
        Assert.assertEquals('O', ((char) (o_matchesIgnoreCase__11)));
        Assert.assertTrue(o_matchesIgnoreCase__7);
    }

    @Test(timeout = 10000)
    public void consumeToString() {
        CharacterReader r = new CharacterReader("One Two Two Four");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        String o_consumeToString__3 = r.consumeTo("Two");
        Assert.assertEquals("One ", o_consumeToString__3);
        char o_consumeToString__4 = r.consume();
        Assert.assertEquals('T', ((char) (o_consumeToString__4)));
        String o_consumeToString__5 = r.consumeTo("Two");
        Assert.assertEquals("wo ", o_consumeToString__5);
        char o_consumeToString__6 = r.consume();
        Assert.assertEquals('T', ((char) (o_consumeToString__6)));
        String o_consumeToString__7 = r.consumeTo("Qux");
        Assert.assertEquals("wo Four", o_consumeToString__7);
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(16, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertEquals("One ", o_consumeToString__3);
        Assert.assertEquals('T', ((char) (o_consumeToString__4)));
        Assert.assertEquals("wo ", o_consumeToString__5);
        Assert.assertEquals('T', ((char) (o_consumeToString__6)));
    }

    @Test(timeout = 10000)
    public void rangeEquals() {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        boolean o_rangeEquals__3 = r.rangeEquals(0, 5, "Check");
        Assert.assertTrue(o_rangeEquals__3);
        boolean o_rangeEquals__4 = r.rangeEquals(0, 5, "CHOKE");
        Assert.assertFalse(o_rangeEquals__4);
        boolean o_rangeEquals__5 = r.rangeEquals(0, 5, "Chec");
        Assert.assertFalse(o_rangeEquals__5);
        boolean o_rangeEquals__6 = r.rangeEquals(6, 5, "Check");
        Assert.assertTrue(o_rangeEquals__6);
        boolean o_rangeEquals__7 = r.rangeEquals(6, 5, "Chuck");
        Assert.assertFalse(o_rangeEquals__7);
        boolean o_rangeEquals__8 = r.rangeEquals(12, 5, "Check");
        Assert.assertTrue(o_rangeEquals__8);
        boolean o_rangeEquals__9 = r.rangeEquals(12, 5, "Cheeky");
        Assert.assertFalse(o_rangeEquals__9);
        boolean o_rangeEquals__10 = r.rangeEquals(18, 5, "CHOKE");
        Assert.assertTrue(o_rangeEquals__10);
        boolean o_rangeEquals__11 = r.rangeEquals(18, 5, "CHIKE");
        Assert.assertFalse(o_rangeEquals__11);
        Assert.assertTrue(o_rangeEquals__6);
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertTrue(o_rangeEquals__3);
        Assert.assertFalse(o_rangeEquals__9);
        Assert.assertFalse(o_rangeEquals__4);
        Assert.assertTrue(o_rangeEquals__10);
        Assert.assertFalse(o_rangeEquals__5);
        Assert.assertTrue(o_rangeEquals__8);
        Assert.assertFalse(o_rangeEquals__7);
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatched() {
        CharacterReader r = new CharacterReader("<[[one]]");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        int o_nextIndexOfUnmatched__3 = r.nextIndexOf("]]>");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatched__3)));
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    @Test(timeout = 10000)
    public void unconsume() {
        CharacterReader r = new CharacterReader("one");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        char o_unconsume__3 = r.consume();
        Assert.assertEquals('o', ((char) (o_unconsume__3)));
        char o_unconsume__4 = r.current();
        Assert.assertEquals('n', ((char) (o_unconsume__4)));
        r.unconsume();
        char o_unconsume__6 = r.current();
        Assert.assertEquals('o', ((char) (o_unconsume__6)));
        char o_unconsume__7 = r.consume();
        Assert.assertEquals('o', ((char) (o_unconsume__7)));
        char o_unconsume__8 = r.consume();
        Assert.assertEquals('n', ((char) (o_unconsume__8)));
        char o_unconsume__9 = r.consume();
        Assert.assertEquals('e', ((char) (o_unconsume__9)));
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        char o_unconsume__13 = r.current();
        Assert.assertEquals('e', ((char) (o_unconsume__13)));
        char o_unconsume__14 = r.consume();
        Assert.assertEquals('e', ((char) (o_unconsume__14)));
        r.isEmpty();
        char o_unconsume__16 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_unconsume__16)));
        r.unconsume();
        r.isEmpty();
        char o_unconsume__19 = r.current();
        Assert.assertEquals('\uffff', ((char) (o_unconsume__19)));
        Assert.assertEquals('o', ((char) (o_unconsume__6)));
        Assert.assertEquals('\uffff', ((char) (o_unconsume__16)));
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertEquals('o', ((char) (o_unconsume__3)));
        Assert.assertEquals('e', ((char) (o_unconsume__9)));
        Assert.assertEquals('n', ((char) (o_unconsume__4)));
        Assert.assertEquals('e', ((char) (o_unconsume__13)));
        Assert.assertEquals('e', ((char) (o_unconsume__14)));
        Assert.assertEquals('n', ((char) (o_unconsume__8)));
        Assert.assertEquals('o', ((char) (o_unconsume__7)));
    }

    @Test(timeout = 10000)
    public void matchesAny() {
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader("One\nTwo\tThree");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        boolean o_matchesAny__4 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAny__4);
        String o_matchesAny__5 = r.consumeToAny(scan);
        Assert.assertEquals("One", o_matchesAny__5);
        boolean o_matchesAny__6 = r.matchesAny(scan);
        Assert.assertTrue(o_matchesAny__6);
        char o_matchesAny__7 = r.consume();
        Assert.assertEquals('\n', ((char) (o_matchesAny__7)));
        boolean o_matchesAny__8 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAny__8);
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertFalse(o_matchesAny__4);
        Assert.assertEquals('\n', ((char) (o_matchesAny__7)));
        Assert.assertTrue(o_matchesAny__6);
        Assert.assertEquals("One", o_matchesAny__5);
    }

    @Test(timeout = 10000)
    public void mark() {
        CharacterReader r = new CharacterReader("one");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        char o_mark__3 = r.consume();
        Assert.assertEquals('o', ((char) (o_mark__3)));
        r.mark();
        char o_mark__5 = r.consume();
        Assert.assertEquals('n', ((char) (o_mark__5)));
        char o_mark__6 = r.consume();
        Assert.assertEquals('e', ((char) (o_mark__6)));
        r.isEmpty();
        r.rewindToMark();
        char o_mark__9 = r.consume();
        Assert.assertEquals('n', ((char) (o_mark__9)));
        Assert.assertEquals('o', ((char) (o_mark__3)));
        Assert.assertEquals('e', ((char) (o_mark__6)));
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('e', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertEquals('n', ((char) (o_mark__5)));
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence() {
        CharacterReader r = new CharacterReader("One12 Two &bar; qux");
        Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        String o_consumeLetterThenDigitSequence__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("One12", o_consumeLetterThenDigitSequence__3);
        char o_consumeLetterThenDigitSequence__4 = r.consume();
        Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence__4)));
        String o_consumeLetterThenDigitSequence__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("Two", o_consumeLetterThenDigitSequence__5);
        String o_consumeLetterThenDigitSequence__6 = r.consumeToEnd();
        Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence__6);
        Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        Assert.assertEquals(19, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence__4)));
        Assert.assertEquals("One12", o_consumeLetterThenDigitSequence__3);
        Assert.assertEquals("Two", o_consumeLetterThenDigitSequence__5);
    }
}

