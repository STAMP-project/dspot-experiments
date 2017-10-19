package org.jsoup.parser;


/**
 * Test suite for character reader.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class AmplCharacterReaderTest {
    @org.junit.Test
    public void consume() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        org.junit.Assert.assertEquals(0, r.pos());
        org.junit.Assert.assertEquals('o', r.current());
        org.junit.Assert.assertEquals('o', r.consume());
        org.junit.Assert.assertEquals(1, r.pos());
        org.junit.Assert.assertEquals('n', r.current());
        org.junit.Assert.assertEquals(1, r.pos());
        org.junit.Assert.assertEquals('n', r.consume());
        org.junit.Assert.assertEquals('e', r.consume());
        org.junit.Assert.assertTrue(r.isEmpty());
        org.junit.Assert.assertEquals(org.jsoup.parser.CharacterReader.EOF, r.consume());
        org.junit.Assert.assertTrue(r.isEmpty());
        org.junit.Assert.assertEquals(org.jsoup.parser.CharacterReader.EOF, r.consume());
    }

    @org.junit.Test
    public void unconsume() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        org.junit.Assert.assertEquals('o', r.consume());
        org.junit.Assert.assertEquals('n', r.current());
        r.unconsume();
        org.junit.Assert.assertEquals('o', r.current());
        org.junit.Assert.assertEquals('o', r.consume());
        org.junit.Assert.assertEquals('n', r.consume());
        org.junit.Assert.assertEquals('e', r.consume());
        org.junit.Assert.assertTrue(r.isEmpty());
        r.unconsume();
        org.junit.Assert.assertFalse(r.isEmpty());
        org.junit.Assert.assertEquals('e', r.current());
        org.junit.Assert.assertEquals('e', r.consume());
        org.junit.Assert.assertTrue(r.isEmpty());
        org.junit.Assert.assertEquals(org.jsoup.parser.CharacterReader.EOF, r.consume());
        r.unconsume();
        org.junit.Assert.assertTrue(r.isEmpty());
        org.junit.Assert.assertEquals(org.jsoup.parser.CharacterReader.EOF, r.current());
    }

    @org.junit.Test
    public void mark() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        r.consume();
        r.mark();
        org.junit.Assert.assertEquals('n', r.consume());
        org.junit.Assert.assertEquals('e', r.consume());
        org.junit.Assert.assertTrue(r.isEmpty());
        r.rewindToMark();
        org.junit.Assert.assertEquals('n', r.consume());
    }

    @org.junit.Test
    public void consumeToEnd() {
        java.lang.String in = "one two three";
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        java.lang.String toEnd = r.consumeToEnd();
        org.junit.Assert.assertEquals(in, toEnd);
        org.junit.Assert.assertTrue(r.isEmpty());
    }

    @org.junit.Test
    public void nextIndexOfChar() {
        java.lang.String in = "blah blah";
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        org.junit.Assert.assertEquals((-1), r.nextIndexOf('x'));
        org.junit.Assert.assertEquals(3, r.nextIndexOf('h'));
        java.lang.String pull = r.consumeTo('h');
        org.junit.Assert.assertEquals("bla", pull);
        r.consume();
        org.junit.Assert.assertEquals(2, r.nextIndexOf('l'));
        org.junit.Assert.assertEquals(" blah", r.consumeToEnd());
        org.junit.Assert.assertEquals((-1), r.nextIndexOf('x'));
    }

    @org.junit.Test
    public void nextIndexOfString() {
        java.lang.String in = "One Two something Two Three Four";
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        org.junit.Assert.assertEquals((-1), r.nextIndexOf("Foo"));
        org.junit.Assert.assertEquals(4, r.nextIndexOf("Two"));
        org.junit.Assert.assertEquals("One Two ", r.consumeTo("something"));
        org.junit.Assert.assertEquals(10, r.nextIndexOf("Two"));
        org.junit.Assert.assertEquals("something Two Three Four", r.consumeToEnd());
        org.junit.Assert.assertEquals((-1), r.nextIndexOf("Two"));
    }

    @org.junit.Test
    public void nextIndexOfUnmatched() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<[[one]]");
        org.junit.Assert.assertEquals((-1), r.nextIndexOf("]]>"));
    }

    @org.junit.Test
    public void consumeToChar() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        org.junit.Assert.assertEquals("One ", r.consumeTo('T'));
        org.junit.Assert.assertEquals("", r.consumeTo('T'));// on Two

        org.junit.Assert.assertEquals('T', r.consume());
        org.junit.Assert.assertEquals("wo ", r.consumeTo('T'));
        org.junit.Assert.assertEquals('T', r.consume());
        org.junit.Assert.assertEquals("hree", r.consumeTo('T'));// consume to end

    }

    @org.junit.Test
    public void consumeToString() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
        org.junit.Assert.assertEquals("One ", r.consumeTo("Two"));
        org.junit.Assert.assertEquals('T', r.consume());
        org.junit.Assert.assertEquals("wo ", r.consumeTo("Two"));
        org.junit.Assert.assertEquals('T', r.consume());
        org.junit.Assert.assertEquals("wo Four", r.consumeTo("Qux"));
    }

    @org.junit.Test
    public void advance() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        org.junit.Assert.assertEquals('O', r.consume());
        r.advance();
        org.junit.Assert.assertEquals('e', r.consume());
    }

    @org.junit.Test
    public void consumeToAny() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        org.junit.Assert.assertEquals("One ", r.consumeToAny('&', ';'));
        org.junit.Assert.assertTrue(r.matches('&'));
        org.junit.Assert.assertTrue(r.matches("&bar;"));
        org.junit.Assert.assertEquals('&', r.consume());
        org.junit.Assert.assertEquals("bar", r.consumeToAny('&', ';'));
        org.junit.Assert.assertEquals(';', r.consume());
        org.junit.Assert.assertEquals(" qux", r.consumeToAny('&', ';'));
    }

    @org.junit.Test
    public void consumeLetterSequence() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        org.junit.Assert.assertEquals("One", r.consumeLetterSequence());
        org.junit.Assert.assertEquals(" &", r.consumeTo("bar;"));
        org.junit.Assert.assertEquals("bar", r.consumeLetterSequence());
        org.junit.Assert.assertEquals("; qux", r.consumeToEnd());
    }

    @org.junit.Test
    public void consumeLetterThenDigitSequence() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One12 Two &bar; qux");
        org.junit.Assert.assertEquals("One12", r.consumeLetterThenDigitSequence());
        org.junit.Assert.assertEquals(' ', r.consume());
        org.junit.Assert.assertEquals("Two", r.consumeLetterThenDigitSequence());
        org.junit.Assert.assertEquals(" &bar; qux", r.consumeToEnd());
    }

    @org.junit.Test
    public void matches() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        org.junit.Assert.assertTrue(r.matches('O'));
        org.junit.Assert.assertTrue(r.matches("One Two Three"));
        org.junit.Assert.assertTrue(r.matches("One"));
        org.junit.Assert.assertFalse(r.matches("one"));
        org.junit.Assert.assertEquals('O', r.consume());
        org.junit.Assert.assertFalse(r.matches("One"));
        org.junit.Assert.assertTrue(r.matches("ne Two Three"));
        org.junit.Assert.assertFalse(r.matches("ne Two Three Four"));
        org.junit.Assert.assertEquals("ne Two Three", r.consumeToEnd());
        org.junit.Assert.assertFalse(r.matches("ne"));
    }

    @org.junit.Test
    public void matchesIgnoreCase() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        org.junit.Assert.assertTrue(r.matchesIgnoreCase("O"));
        org.junit.Assert.assertTrue(r.matchesIgnoreCase("o"));
        org.junit.Assert.assertTrue(r.matches('O'));
        org.junit.Assert.assertFalse(r.matches('o'));
        org.junit.Assert.assertTrue(r.matchesIgnoreCase("One Two Three"));
        org.junit.Assert.assertTrue(r.matchesIgnoreCase("ONE two THREE"));
        org.junit.Assert.assertTrue(r.matchesIgnoreCase("One"));
        org.junit.Assert.assertTrue(r.matchesIgnoreCase("one"));
        org.junit.Assert.assertEquals('O', r.consume());
        org.junit.Assert.assertFalse(r.matchesIgnoreCase("One"));
        org.junit.Assert.assertTrue(r.matchesIgnoreCase("NE Two Three"));
        org.junit.Assert.assertFalse(r.matchesIgnoreCase("ne Two Three Four"));
        org.junit.Assert.assertEquals("ne Two Three", r.consumeToEnd());
        org.junit.Assert.assertFalse(r.matchesIgnoreCase("ne"));
    }

    @org.junit.Test
    public void containsIgnoreCase() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
        org.junit.Assert.assertTrue(r.containsIgnoreCase("two"));
        org.junit.Assert.assertTrue(r.containsIgnoreCase("three"));
        // weird one: does not find one, because it scans for consistent case only
        org.junit.Assert.assertFalse(r.containsIgnoreCase("one"));
    }

    @org.junit.Test
    public void matchesAny() {
        char[] scan = new char[]{ ' ', '\n', '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        org.junit.Assert.assertFalse(r.matchesAny(scan));
        org.junit.Assert.assertEquals("One", r.consumeToAny(scan));
        org.junit.Assert.assertTrue(r.matchesAny(scan));
        org.junit.Assert.assertEquals('\n', r.consume());
        org.junit.Assert.assertFalse(r.matchesAny(scan));
    }

    @org.junit.Test
    public void cachesStrings() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        java.lang.String one = r.consumeTo('\t');
        r.consume();
        java.lang.String two = r.consumeTo('\t');
        r.consume();
        java.lang.String three = r.consumeTo('\t');
        r.consume();
        java.lang.String four = r.consumeTo('\t');
        r.consume();
        java.lang.String five = r.consumeTo('\t');
        org.junit.Assert.assertEquals("Check", one);
        org.junit.Assert.assertEquals("Check", two);
        org.junit.Assert.assertEquals("Check", three);
        org.junit.Assert.assertEquals("CHOKE", four);
        org.junit.Assert.assertTrue((one == two));
        org.junit.Assert.assertTrue((two == three));
        org.junit.Assert.assertTrue((three != four));
        org.junit.Assert.assertTrue((four != five));
        org.junit.Assert.assertEquals(five, "A string that is longer than 16 chars");
    }

    @org.junit.Test
    public void rangeEquals() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        org.junit.Assert.assertTrue(r.rangeEquals(0, 5, "Check"));
        org.junit.Assert.assertFalse(r.rangeEquals(0, 5, "CHOKE"));
        org.junit.Assert.assertFalse(r.rangeEquals(0, 5, "Chec"));
        org.junit.Assert.assertTrue(r.rangeEquals(6, 5, "Check"));
        org.junit.Assert.assertFalse(r.rangeEquals(6, 5, "Chuck"));
        org.junit.Assert.assertTrue(r.rangeEquals(12, 5, "Check"));
        org.junit.Assert.assertFalse(r.rangeEquals(12, 5, "Cheeky"));
        org.junit.Assert.assertTrue(r.rangeEquals(18, 5, "CHOKE"));
        org.junit.Assert.assertFalse(r.rangeEquals(18, 5, "CHIKE"));
    }
}

