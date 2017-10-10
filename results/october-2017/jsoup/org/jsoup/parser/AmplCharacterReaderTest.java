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

    @org.junit.Test(timeout = 10000)
    public void consumeToEnd() {
        java.lang.String in = "one two three";
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
    }

    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence__3 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence__4 = r.consumeTo("bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &", o_consumeLetterSequence__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence__5 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &", o_consumeLetterSequence__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence__3);
    }

    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase__3 = r.containsIgnoreCase("two");
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase__4 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase__5 = r.containsIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase__3);
    }

    @org.junit.Test(timeout = 10000)
    public void matches() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__3 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__5 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__6 = r.matches("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matches__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__8 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__9 = r.matches("ne Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__10 = r.matches("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches__12 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches__7)));
    }

    @org.junit.Test(timeout = 10000)
    public void consumeToChar() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar__3 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar__4 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar__4);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar__5)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar__6 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToChar__6);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar__8 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hree", o_consumeToChar__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToChar__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar__3);
    }

    @org.junit.Test(timeout = 10000)
    public void cachesStrings() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String one = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings__5)));
        java.lang.String two = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings__8 = r.consume();
        java.lang.String three = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", three);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings__11)));
        java.lang.String four = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CHOKE", four);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings__14)));
        java.lang.String five = r.consumeTo('\t');
        boolean boolean_0 = one == two;
        boolean boolean_1 = two == three;
        boolean boolean_2 = three != four;
        boolean boolean_3 = four != five;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", three);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CHOKE", four);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A string that is longer than 16 chars", five);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(61, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings__11)));
    }

    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__3 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__4 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__5 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__6 = r.matches('o');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__9 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__10 = r.matchesIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__10);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase__11)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__12 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase__7);
    }

    @org.junit.Test(timeout = 10000)
    public void advance() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_advance__3)));
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_advance__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_advance__3)));
    }

    @org.junit.Test(timeout = 10000)
    public void consumeToString() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString__3 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString__4)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString__5 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString__7 = r.consumeTo("Qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString__6)));
    }

    @org.junit.Test(timeout = 10000)
    public void consumeToAny() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny__4 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny__5 = r.matches("&bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    @org.junit.Test(timeout = 10000)
    public void rangeEquals() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__6 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__7 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__9 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__10 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals__11 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals__7);
    }

    @org.junit.Test(timeout = 10000)
    public void unconsume() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume__3)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__4 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume__4)));
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__6 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume__6)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume__7)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume__8)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume__9)));
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__13 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume__13)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume__14)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__16 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume__19 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume__19)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume__16)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume__13)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume__7)));
    }

    @org.junit.Test(timeout = 10000)
    public void matchesAny() {
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny__4 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny__5 = r.consumeToAny(scan);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny__6 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny__5);
    }

    @org.junit.Test(timeout = 10000)
    public void mark() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark__5)));
        // AssertGenerator create local variable with return value of invocation
        char o_mark__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark__6)));
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_mark__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark__5)));
    }

    @org.junit.Test(timeout = 10000)
    public void consumeLetterThenDigitSequence() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One12 Two &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence__3 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence__4)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence__5 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(19, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    @org.junit.Test(timeout = 10000)
    public void advance_sd13() {
        char[] __DSPOT_chars_1 = new char[]{ ',' , 'y' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_advance_sd13__4)));
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_advance_sd13__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd13__7 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" Two Three", o_advance_sd13__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_advance_sd13__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_advance_sd13__6)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString2() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString2__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_advance_literalMutationString2__3)));
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString2__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_advance_literalMutationString2__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_advance_literalMutationString2__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    @org.junit.Test(timeout = 10000)
    public void advance_sd8() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd8__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd8__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_advance_sd8__5)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd8__6 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" Two Three", o_advance_sd8__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_advance_sd8__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_advance_sd8__5)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString3() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString3__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (o_advance_literalMutationString3__3)));
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString3__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('p', ((char) (o_advance_literalMutationString3__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('a', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (o_advance_literalMutationString3__3)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    @org.junit.Test(timeout = 10000)
    public void advance_sd10() {
        char __DSPOT_c_0 = 'H';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_advance_sd10__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd10__7 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" Two Three", o_advance_sd10__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_advance_sd10__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_advance_sd10__4)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd13 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd13_literalMutationString224() {
        char[] __DSPOT_chars_1 = new char[]{ ',' , 'y' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd13__7 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_advance_sd13__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd10 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd10_literalMutationChar171() {
        char __DSPOT_c_0 = 'G';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd10__7 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" Two Three", o_advance_sd10__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd10 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd10_literalMutationString175() {
        char __DSPOT_c_0 = 'H';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd10__7 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("an>", o_advance_sd10__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('H', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd13 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd13_literalMutationString229() {
        char[] __DSPOT_chars_1 = new char[]{ ',' , 'y' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("z5*yC=M]:bMoV");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('z', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd13__7 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_advance_sd13__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('y', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString3 */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString3_sd88() {
        char[] __DSPOT_chars_7 = new char[]{ 'g' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString3__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString3__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_literalMutationString3_sd88__11 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("an>Hello <div>there</div> <span>now</span></span>", o_advance_literalMutationString3_sd88__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd13 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd13_literalMutationChar213() {
        char[] __DSPOT_chars_1 = new char[]{ ' ' , 'y' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd13__7 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_advance_sd13__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd10 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd10_literalMutationChar168() {
        char __DSPOT_c_0 = ' ';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd10__7 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_advance_sd10__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd10 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd10_literalMutationString174_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_0 = 'H';
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator create local variable with return value of invocation
            char o_advance_sd10__4 = r.consume();
            r.advance();
            // AssertGenerator create local variable with return value of invocation
            char o_advance_sd10__6 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_advance_sd10__7 = // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_0);
            org.junit.Assert.fail("advance_sd10_literalMutationString174 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString5 */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString5_literalMutationString102() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString5__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString5__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString4 */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString4_sd99() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Tw* Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString4__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString4__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString4_sd99__10 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_advance_literalMutationString4_sd99__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd11 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd11_sd198() {
        char[] __DSPOT_chars_23 = new char[]{ '(' , 'd' , 'M' , '7' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd11__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd11__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd11__6 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd11_sd198__15 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_23);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" Two Three", o_advance_sd11_sd198__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd12 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd12_sd206() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd12__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd12__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        int o_advance_sd12__6 = // StatementAdd: add invocation of a method
        r.pos();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd12_sd206__14 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" Two Three", o_advance_sd12_sd206__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString5 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString5_literalMutationString106 */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString5_literalMutationString106_sd1481() {
        char[] __DSPOT_chars_131 = new char[]{ ',' , 'I' , 'K' , ')' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Gdhscb1CS@!x*z");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('G', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString5__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString5__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_literalMutationString5_literalMutationString106_sd1481__11 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_131);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("scb1CS@!x*z", o_advance_literalMutationString5_literalMutationString106_sd1481__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd13 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd13_literalMutationChar219 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd13_literalMutationChar219_sd3274() {
        char[] __DSPOT_chars_1 = new char[]{ ',' , ' ' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd13__7 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_advance_sd13__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd13_literalMutationChar219_sd3274__15 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" Two Three", o_advance_sd13_literalMutationChar219_sd3274__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_advance_sd13__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString5 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString5_sd112 */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString5_sd112_sd1562() {
        char __DSPOT_c_142 = '@';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("GdhscbCS@!x*z");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('G', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString5__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString5__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString5_sd112__10 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_literalMutationString5_sd112_sd1562__15 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_142);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("scbCS", o_advance_literalMutationString5_sd112_sd1562__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('@', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString5 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString5_literalMutationString103 */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString5_literalMutationString103_sd1441() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString5__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString5__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        int o_advance_literalMutationString5_literalMutationString103_sd1441__10 = // StatementAdd: add invocation of a method
        r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (o_advance_literalMutationString5_literalMutationString103_sd1441__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('a', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd12 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd12_sd205 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd12_sd205_sd2984() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd12__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd12__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        int o_advance_sd12__6 = // StatementAdd: add invocation of a method
        r.pos();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd12_sd205_sd2984__16 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two Three", o_advance_sd12_sd205_sd2984__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd10 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd10_literalMutationChar167 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd10_literalMutationChar167_literalMutationString2361() {
        char __DSPOT_c_0 = '\u0000';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd10__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd10__7 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("an>Hello <div>there</div> <span>now</span></span>", o_advance_sd10__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd10 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd10_literalMutationChar172 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd10_literalMutationChar172_literalMutationString2455_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_0 = '\n';
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator create local variable with return value of invocation
            char o_advance_sd10__4 = r.consume();
            r.advance();
            // AssertGenerator create local variable with return value of invocation
            char o_advance_sd10__6 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_advance_sd10__7 = // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_0);
            org.junit.Assert.fail("advance_sd10_literalMutationChar172_literalMutationString2455 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd13 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd13_literalMutationString224 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd13_literalMutationString224_literalMutationChar3380() {
        char[] __DSPOT_chars_1 = new char[]{ '\u0000' , 'y' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd13__7 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_advance_sd13__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString4 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString4_literalMutationString90 */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString4_literalMutationString90_sd1262() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString4__3 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString4__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_literalMutationString4_literalMutationString90_sd1262__10 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_advance_literalMutationString4_literalMutationString90_sd1262__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd13 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd13_literalMutationString226 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd13_literalMutationString226_literalMutationChar3428() {
        char[] __DSPOT_chars_1 = new char[]{ ' ' , 'y' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Tpo Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__4 = r.consume();
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_advance_sd13__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd13__7 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_advance_sd13__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#cachesStrings */
    @org.junit.Test(timeout = 10000)
    public void cachesStrings_literalMutationChar4387_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
            java.lang.String one = r.consumeTo('\t');
            r.consume();
            java.lang.String two = r.consumeTo('\u0000');
            r.consume();
            java.lang.String three = r.consumeTo('\t');
            r.consume();
            java.lang.String four = r.consumeTo('\t');
            r.consume();
            java.lang.String five = r.consumeTo('\t');
            boolean boolean_48 = one == two;
            boolean boolean_49 = two == three;
            boolean boolean_50 = three != four;
            boolean boolean_51 = four != five;
            org.junit.Assert.fail("cachesStrings_literalMutationChar4387 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#cachesStrings */
    @org.junit.Test(timeout = 10000)
    public void cachesStrings_literalMutationString4375_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            java.lang.String one = r.consumeTo('\t');
            r.consume();
            java.lang.String two = r.consumeTo('\t');
            r.consume();
            java.lang.String three = r.consumeTo('\t');
            r.consume();
            java.lang.String four = r.consumeTo('\t');
            r.consume();
            java.lang.String five = r.consumeTo('\t');
            boolean boolean_4 = one == two;
            boolean boolean_5 = two == three;
            boolean boolean_6 = three != four;
            boolean boolean_7 = four != five;
            org.junit.Assert.fail("cachesStrings_literalMutationString4375 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#cachesStrings */
    @org.junit.Test(timeout = 10000)
    public void cachesStrings_literalMutationChar4388_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
            java.lang.String one = r.consumeTo('\t');
            r.consume();
            java.lang.String two = r.consumeTo(' ');
            r.consume();
            java.lang.String three = r.consumeTo('\t');
            r.consume();
            java.lang.String four = r.consumeTo('\t');
            r.consume();
            java.lang.String five = r.consumeTo('\t');
            boolean boolean_52 = one == two;
            boolean boolean_53 = two == three;
            boolean boolean_54 = three != four;
            boolean boolean_55 = four != five;
            org.junit.Assert.fail("cachesStrings_literalMutationChar4388 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#cachesStrings */
    @org.junit.Test(timeout = 10000)
    public void cachesStrings_sd4417() {
        char[] __DSPOT_chars_397 = new char[]{ ')' , 'd' , 'Y' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String one = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4417__6 = r.consume();
        java.lang.String two = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4417__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4417__9)));
        java.lang.String three = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", three);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4417__12 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4417__12)));
        java.lang.String four = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CHOKE", four);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4417__15 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4417__15)));
        java.lang.String five = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A string that is longer than 16 chars", five);
        boolean boolean_152 = one == two;
        boolean boolean_153 = two == three;
        boolean boolean_154 = three != four;
        boolean boolean_155 = four != five;
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_cachesStrings_sd4417__22 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_397);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_cachesStrings_sd4417__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4417__15)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", three);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A string that is longer than 16 chars", five);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4417__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4417__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4417__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(61, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CHOKE", four);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#cachesStrings */
    @org.junit.Test(timeout = 10000)
    public void cachesStrings_sd4411() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String one = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4411__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4411__5)));
        java.lang.String two = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4411__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4411__8)));
        java.lang.String three = r.consumeTo('\t');
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4411__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4411__11)));
        java.lang.String four = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CHOKE", four);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4411__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4411__14)));
        java.lang.String five = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A string that is longer than 16 chars", five);
        boolean boolean_128 = one == two;
        boolean boolean_129 = two == three;
        boolean boolean_130 = three != four;
        boolean boolean_131 = four != five;
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", three);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4411__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(62, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A string that is longer than 16 chars", five);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4411__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4411__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CHOKE", four);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4411__5)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#cachesStrings */
    @org.junit.Test(timeout = 10000)
    public void cachesStrings_literalMutationChar4400() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String one = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_literalMutationChar4400__5 = r.consume();
        java.lang.String two = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_literalMutationChar4400__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_literalMutationChar4400__8)));
        java.lang.String three = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", three);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_literalMutationChar4400__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_literalMutationChar4400__11)));
        java.lang.String four = r.consumeTo(' ');
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_literalMutationChar4400__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_cachesStrings_literalMutationChar4400__14)));
        java.lang.String five = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("string that is longer than 16 chars", five);
        boolean boolean_92 = one == two;
        boolean boolean_93 = two == three;
        boolean boolean_94 = three != four;
        boolean boolean_95 = four != five;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_literalMutationChar4400__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("string that is longer than 16 chars", five);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(61, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_literalMutationChar4400__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_cachesStrings_literalMutationChar4400__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CHOKE\tA", four);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", three);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_literalMutationChar4400__11)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#cachesStrings */
    @org.junit.Test(timeout = 10000)
    public void cachesStrings_sd4412() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String one = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4412__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4412__5)));
        java.lang.String two = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4412__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4412__8)));
        java.lang.String three = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", three);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4412__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4412__11)));
        java.lang.String four = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CHOKE", four);
        // AssertGenerator create local variable with return value of invocation
        char o_cachesStrings_sd4412__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4412__14)));
        java.lang.String five = r.consumeTo('\t');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A string that is longer than 16 chars", five);
        boolean boolean_132 = one == two;
        boolean boolean_133 = two == three;
        boolean boolean_134 = three != four;
        boolean boolean_135 = four != five;
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_cachesStrings_sd4412__21 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_cachesStrings_sd4412__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4412__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CHOKE", four);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(61, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", one);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4412__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4412__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", two);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("A string that is longer than 16 chars", five);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_cachesStrings_sd4412__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check", three);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consume */
    @org.junit.Test(timeout = 10000)
    public void consume_sd16101_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_874 = 'e';
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
            r.pos();
            r.current();
            r.consume();
            r.pos();
            r.current();
            r.pos();
            r.consume();
            r.consume();
            r.isEmpty();
            r.consume();
            r.isEmpty();
            r.consume();
            // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_874);
            org.junit.Assert.fail("consume_sd16101 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consume */
    @org.junit.Test(timeout = 10000)
    public void consume_sd16098() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_sd16098__3 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_consume_sd16098__3)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16098__4 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16098__4)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16098__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16098__5)));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_sd16098__6 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16098__6)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16098__7 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16098__7)));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_sd16098__8 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16098__8)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16098__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16098__9)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16098__10 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_consume_sd16098__10)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16098__12 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16098__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16098__14)));
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16098__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16098__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16098__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16098__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_consume_sd16098__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16098__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16098__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16098__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16098__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_consume_sd16098__10)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consume */
    @org.junit.Test(timeout = 10000)
    public void consume_literalMutationString16092() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_literalMutationString16092__3 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_consume_literalMutationString16092__3)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_literalMutationString16092__4 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__4)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_literalMutationString16092__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        int o_consume_literalMutationString16092__6 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_literalMutationString16092__6)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_literalMutationString16092__7 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__7)));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_literalMutationString16092__8 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_literalMutationString16092__8)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_literalMutationString16092__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__9)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_literalMutationString16092__10 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__10)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consume_literalMutationString16092__12 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__12)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consume_literalMutationString16092__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_literalMutationString16092__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_literalMutationString16092__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_consume_literalMutationString16092__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(5, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_literalMutationString16092__12)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consume */
    @org.junit.Test(timeout = 10000)
    public void consume_sd16099_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
            r.pos();
            r.current();
            r.consume();
            r.pos();
            r.current();
            r.pos();
            r.consume();
            r.consume();
            r.isEmpty();
            r.consume();
            r.isEmpty();
            r.consume();
            // StatementAdd: add invocation of a method
            r.toString();
            org.junit.Assert.fail("consume_sd16099 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consume */
    @org.junit.Test(timeout = 10000)
    public void consume_sd16104() {
        char[] __DSPOT_chars_875 = new char[]{ '`' , 'i' , '#' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_sd16104__4 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_consume_sd16104__4)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16104__5 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16104__5)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16104__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        int o_consume_sd16104__7 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16104__7)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16104__8 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16104__8)));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_sd16104__9 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16104__9)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16104__10 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16104__10)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16104__11 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16104__13 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16104__13)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16104__15 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16104__15)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consume_sd16104__16 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_875);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consume_sd16104__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16104__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16104__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(5, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16104__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16104__15)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_consume_sd16104__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16104__13)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16104__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16104__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_consume_sd16104__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16104__5)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consume */
    @org.junit.Test(timeout = 10000)
    public void consume_sd16102() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_sd16102__3 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_consume_sd16102__3)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16102__4 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16102__4)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16102__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16102__5)));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_sd16102__6 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16102__6)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16102__7 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16102__7)));
        // AssertGenerator create local variable with return value of invocation
        int o_consume_sd16102__8 = r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16102__8)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16102__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16102__9)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16102__10 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_consume_sd16102__10)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16102__12 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16102__12)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16102__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16102__14)));
        // AssertGenerator create local variable with return value of invocation
        char o_consume_sd16102__15 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16102__15)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16102__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16102__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(5, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_consume_sd16102__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (o_consume_sd16102__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_consume_sd16102__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16102__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_consume_sd16102__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16102__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_consume_sd16102__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consume_sd16102__14)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_literalMutationString20659() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20659__3 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_literalMutationString20659__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20659__4 = r.consumeTo("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterSequence_literalMutationString20659__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20659__5 = r.consumeLetterSequence();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20659__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20659__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_literalMutationString20659__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterSequence_literalMutationString20659__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20659__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_literalMutationString20658_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
            r.consumeLetterSequence();
            r.consumeTo("");
            r.consumeLetterSequence();
            r.consumeToEnd();
            org.junit.Assert.fail("consumeLetterSequence_literalMutationString20658 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_sd20670() {
        char[] __DSPOT_chars_1277 = new char[]{ 'i' , 'o' , '+' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20670__4 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_sd20670__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20670__5 = r.consumeTo("bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &", o_consumeLetterSequence_sd20670__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20670__6 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence_sd20670__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20670__7 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence_sd20670__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20670__8 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_1277);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_sd20670__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence_sd20670__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence_sd20670__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &", o_consumeLetterSequence_sd20670__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_sd20670__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_literalMutationString20660() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20660__3 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_literalMutationString20660__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20660__4 = r.consumeTo("dar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterSequence_literalMutationString20660__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20660__5 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20660__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20660__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20660__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_literalMutationString20660__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20660__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterSequence_literalMutationString20660__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_literalMutationString20653() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20653__3 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20653__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20653__4 = r.consumeTo("bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_consumeLetterSequence_literalMutationString20653__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20653__5 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20653__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20653__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20653__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20653__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20653__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_consumeLetterSequence_literalMutationString20653__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_sd20665() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20665__3 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_sd20665__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20665__4 = r.consumeTo("bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &", o_consumeLetterSequence_sd20665__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20665__5 = r.consumeLetterSequence();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20665__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence_sd20665__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20665__7 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_sd20665__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence_sd20665__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_sd20665__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence_sd20665__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &", o_consumeLetterSequence_sd20665__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_sd20664() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20664__3 = r.consumeLetterSequence();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20664__4 = r.consumeTo("bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &", o_consumeLetterSequence_sd20664__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20664__5 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence_sd20664__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20664__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence_sd20664__6);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence_sd20664__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence_sd20664__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_sd20664__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &", o_consumeLetterSequence_sd20664__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_literalMutationString20662() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20662__3 = r.consumeLetterSequence();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20662__4 = r.consumeTo("br;");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterSequence_literalMutationString20662__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20662__5 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20662__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20662__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20662__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterSequence_literalMutationString20662__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20662__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_literalMutationString20662__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_sd20667() {
        char __DSPOT_c_1276 = 'x';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20667__4 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_sd20667__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20667__5 = r.consumeTo("bar;");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20667__6 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence_sd20667__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20667__7 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence_sd20667__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_sd20667__8 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_1276);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_sd20667__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence_sd20667__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_consumeLetterSequence_sd20667__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence_sd20667__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &", o_consumeLetterSequence_sd20667__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_literalMutationString20656() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("AdX({me,aDo]b");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('A', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20656__3 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("AdX", o_consumeLetterSequence_literalMutationString20656__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20656__4 = r.consumeTo("bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("({me,aDo]b", o_consumeLetterSequence_literalMutationString20656__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20656__5 = r.consumeLetterSequence();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20656__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20656__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterSequence_literalMutationString20656__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("({me,aDo]b", o_consumeLetterSequence_literalMutationString20656__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("AdX", o_consumeLetterSequence_literalMutationString20656__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterSequence_literalMutationString20655() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("On[ &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20655__3 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("On", o_consumeLetterSequence_literalMutationString20655__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20655__4 = r.consumeTo("bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("[ &", o_consumeLetterSequence_literalMutationString20655__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20655__5 = r.consumeLetterSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence_literalMutationString20655__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterSequence_literalMutationString20655__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("; qux", o_consumeLetterSequence_literalMutationString20655__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("On", o_consumeLetterSequence_literalMutationString20655__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("[ &", o_consumeLetterSequence_literalMutationString20655__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeLetterSequence_literalMutationString20655__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterThenDigitSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_literalMutationString26586_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            r.consumeLetterThenDigitSequence();
            r.consume();
            r.consumeLetterThenDigitSequence();
            r.consumeToEnd();
            org.junit.Assert.fail("consumeLetterThenDigitSequence_literalMutationString26586 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterThenDigitSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_sd26598() {
        char[] __DSPOT_chars_1717 = new char[0];
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One12 Two &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26598__4 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26598__4);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence_sd26598__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26598__6 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence_sd26598__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26598__7 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26598__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26598__8 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_1717);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterThenDigitSequence_sd26598__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(19, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26598__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26598__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence_sd26598__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence_sd26598__6);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterThenDigitSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_literalMutationString26588() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("G0+B=!H.GkJd)[RsB+E");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('G', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_literalMutationString26588__3 = r.consumeLetterThenDigitSequence();
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence_literalMutationString26588__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('+', ((char) (o_consumeLetterThenDigitSequence_literalMutationString26588__4)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_literalMutationString26588__5 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("B", o_consumeLetterThenDigitSequence_literalMutationString26588__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_literalMutationString26588__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("=!H.GkJd)[RsB+E", o_consumeLetterThenDigitSequence_literalMutationString26588__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("G0", o_consumeLetterThenDigitSequence_literalMutationString26588__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(19, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('+', ((char) (o_consumeLetterThenDigitSequence_literalMutationString26588__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("B", o_consumeLetterThenDigitSequence_literalMutationString26588__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterThenDigitSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_literalMutationString26587() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_literalMutationString26587__3 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterThenDigitSequence_literalMutationString26587__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence_literalMutationString26587__4 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_literalMutationString26587__5 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span", o_consumeLetterThenDigitSequence_literalMutationString26587__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_literalMutationString26587__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(">Hello <div>there</div> <span>now</span></span>", o_consumeLetterThenDigitSequence_literalMutationString26587__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span", o_consumeLetterThenDigitSequence_literalMutationString26587__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (o_consumeLetterThenDigitSequence_literalMutationString26587__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterThenDigitSequence_literalMutationString26587__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterThenDigitSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_sd26592() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One12 Two &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26592__3 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26592__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence_sd26592__4 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26592__5 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence_sd26592__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26592__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26592__6);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence_sd26592__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(20, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26592__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26592__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence_sd26592__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterThenDigitSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_sd26593() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One12 Two &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26593__3 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26593__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence_sd26593__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence_sd26593__4)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26593__5 = r.consumeLetterThenDigitSequence();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26593__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26593__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26593__7 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterThenDigitSequence_sd26593__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence_sd26593__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26593__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence_sd26593__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(19, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26593__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterThenDigitSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_sd26596() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One12 Two &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26596__3 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26596__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence_sd26596__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence_sd26596__4)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26596__5 = r.consumeLetterThenDigitSequence();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26596__6 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26596__6);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence_sd26596__7 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeLetterThenDigitSequence_sd26596__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26596__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26596__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence_sd26596__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence_sd26596__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(19, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeLetterThenDigitSequence */
    @org.junit.Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_sd26595() {
        char __DSPOT_c_1716 = '<';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One12 Two &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26595__4 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26595__4);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeLetterThenDigitSequence_sd26595__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence_sd26595__5)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26595__6 = r.consumeLetterThenDigitSequence();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence_sd26595__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26595__7 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26595__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeLetterThenDigitSequence_sd26595__8 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_1716);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeLetterThenDigitSequence_sd26595__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two", o_consumeLetterThenDigitSequence_sd26595__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" &bar; qux", o_consumeLetterThenDigitSequence_sd26595__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(19, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeLetterThenDigitSequence_sd26595__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_sd26595__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_sd30544() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30544__3 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30544__4 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30544__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30544__5 = r.matches("&bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30544__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30544__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_sd30544__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30544__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30544__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30544__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_sd30544__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30544__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30544__9);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30544__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30544__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30544__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30544__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_sd30544__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30544__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_sd30544__8)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationString30490() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &`ar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30490__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationString30490__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30490__4 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_literalMutationString30490__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30490__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30490__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_literalMutationString30490__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30490__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("`ar", o_consumeToAny_literalMutationString30490__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30490__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_literalMutationString30490__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30490__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationString30490__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30490__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("`ar", o_consumeToAny_literalMutationString30490__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_literalMutationString30490__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_literalMutationString30490__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_literalMutationString30490__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationString30490__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationString30491() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30491__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30491__4 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30491__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30491__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30491__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30491__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30491__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30491__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30491__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30491__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30491__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30491__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30491__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30491__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_sd30545() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30545__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30545__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30545__4 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30545__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30545__5 = r.matches("&bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30545__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30545__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_sd30545__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30545__7 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30545__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_sd30545__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30545__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30545__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30545__10 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_sd30545__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_sd30545__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30545__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30545__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30545__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30545__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_sd30545__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30545__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationString30492() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30492__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_consumeToAny_literalMutationString30492__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30492__4 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30492__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30492__5 = r.matches("&bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30492__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30492__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30492__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30492__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30492__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30492__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30492__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30492__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(54, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30492__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30492__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30492__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30492__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_consumeToAny_literalMutationString30492__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30492__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30500() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30500__3 = r.consumeToAny('%', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One &bar", o_consumeToAny_literalMutationChar30500__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30500__4 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationChar30500__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30500__5 = r.matches("&bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationChar30500__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30500__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_literalMutationChar30500__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30500__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30500__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30500__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationChar30500__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30500__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30500__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30500__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_literalMutationChar30500__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationChar30500__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationChar30500__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One &bar", o_consumeToAny_literalMutationChar30500__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationChar30500__8)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30525() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30525__3 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30525__4 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_literalMutationChar30525__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30525__5 = r.matches("&bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_literalMutationChar30525__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30525__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_literalMutationChar30525__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30525__7 = r.consumeToAny('\n', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30525__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30525__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30525__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30525__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30525__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_literalMutationChar30525__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_literalMutationChar30525__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_literalMutationChar30525__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_literalMutationChar30525__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30525__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationString30515() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30515__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationString30515__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30515__4 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_literalMutationString30515__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30515__5 = r.matches("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30515__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30515__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_literalMutationString30515__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30515__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationString30515__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30515__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_literalMutationString30515__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30515__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationString30515__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_literalMutationString30515__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationString30515__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_literalMutationString30515__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30515__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_literalMutationString30515__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationString30515__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_sd30547() {
        char __DSPOT_c_2094 = 'S';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30547__4 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30547__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30547__5 = r.matches('&');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30547__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30547__6 = r.matches("&bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30547__6);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30547__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30547__8 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30547__8);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30547__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_sd30547__9)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30547__10 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30547__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30547__11 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_2094);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_sd30547__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30547__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30547__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_consumeToAny_sd30547__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(';', ((char) (o_consumeToAny_sd30547__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30547__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (o_consumeToAny_sd30547__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30547__10);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30505 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30505_sd31701() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30505__3 = r.consumeToAny('&', '<');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30505__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30505__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30505__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30505__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30505__7 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30505__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30505__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30505__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30505_sd31701__24 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30505_sd31701__24);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30505__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30505__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30505__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_sd30545 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_sd30545_literalMutationChar34124_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_sd30545__3 = r.consumeToAny('&', ';');
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_sd30545__4 = r.matches('&');
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_sd30545__5 = r.matches("&bar;");
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_sd30545__6 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_sd30545__7 = r.consumeToAny('&', '\n');
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_sd30545__8 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_sd30545__9 = r.consumeToAny('&', ';');
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_sd30545__10 = // StatementAdd: add invocation of a method
            r.toString();
            org.junit.Assert.fail("consumeToAny_sd30545_literalMutationChar34124 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30508 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30508_literalMutationChar31838() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30508__3 = r.consumeToAny('\'', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One &bar", o_consumeToAny_literalMutationChar30508__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30508__4 = r.matches('\u0000');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30508__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30508__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30508__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30508__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30508__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30508__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30508__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One &bar", o_consumeToAny_literalMutationChar30508__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30508__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationString30492 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationString30492_literalMutationChar30893() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30492__3 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30492__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30492__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30492__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30492__7 = r.consumeToAny('&', ':');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30492__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30492__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30492__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_consumeToAny_literalMutationString30492__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(54, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30492__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30529 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30529_sd33164_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_2174 = '`';
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_literalMutationChar30529__3 = r.consumeToAny('&', ';');
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_literalMutationChar30529__4 = r.matches('&');
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_literalMutationChar30529__5 = r.matches("&bar;");
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_literalMutationChar30529__6 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_literalMutationChar30529__7 = r.consumeToAny('&', '<');
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_literalMutationChar30529__8 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_literalMutationChar30529__9 = r.consumeToAny('&', ';');
            // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_2174);
            org.junit.Assert.fail("consumeToAny_literalMutationChar30529_sd33164 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30523 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30523_literalMutationString32741() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30523__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30523__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30523__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30523__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30523__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30523__7 = r.consumeToAny('\'', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30523__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30523__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30523__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30523__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30523__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30523__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30533 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30533_literalMutationChar33386() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30533__3 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30533__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30533__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30533__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30533__7 = r.consumeToAny('\n', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30533__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30533__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30533__9 = r.consumeToAny(' ', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30533__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(9, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30533__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30533__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_sd30544 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_sd30544_literalMutationChar34073() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30544__3 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30544__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30544__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30544__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30544__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30544__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30544__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30544__9 = r.consumeToAny('&', '<');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30544__9);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30544__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30544__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30544__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_sd30547 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_sd30547_literalMutationChar34234() {
        char __DSPOT_c_2094 = 'S';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30547__4 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30547__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30547__5 = r.matches('\n');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30547__6 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30547__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30547__8 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30547__8);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30547__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30547__10 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30547__11 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_2094);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_sd30547__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30547__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30547__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30547__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationString30517 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationString30517_literalMutationString32380() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("&KPBl6hE9&ef[");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30517__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30517__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30517__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30517__5 = r.matches("&ba;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30517__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30517__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("KPBl6hE9", o_consumeToAny_literalMutationString30517__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30517__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30517__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ef[", o_consumeToAny_literalMutationString30517__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("KPBl6hE9", o_consumeToAny_literalMutationString30517__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30517__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30512 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30512_literalMutationString32097() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30512__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30512__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30512__4 = r.matches('%');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30512__5 = r.matches("");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30512__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30512__7 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30512__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30512__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30512__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30512__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30512__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30529 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30529_sd33164 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30529_sd33164_failAssert0_literalMutationString47091() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_2174 = '`';
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_literalMutationChar30529__3 = r.consumeToAny('&', ';');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30529__3);
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_literalMutationChar30529__4 = r.matches('&');
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_literalMutationChar30529__5 = r.matches("&bar;");
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_literalMutationChar30529__6 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_literalMutationChar30529__7 = r.consumeToAny('&', '<');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30529__7);
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_literalMutationChar30529__8 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_literalMutationChar30529__9 = r.consumeToAny('&', ';');
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30529__9);
            // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_2174);
            org.junit.Assert.fail("consumeToAny_literalMutationChar30529_sd33164 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationString30494 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationString30494_literalMutationChar31008 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationString30494_literalMutationChar31008_literalMutationString40506() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Hi4$b,|`z!^. ");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('H', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30494__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Hi4$b,|`z!^. ", o_consumeToAny_literalMutationString30494__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30494__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30494__5 = r.matches("&Fbar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30494__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30494__7 = r.consumeToAny('\'', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30494__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30494__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30494__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30494__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(15, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30494__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Hi4$b,|`z!^. ", o_consumeToAny_literalMutationString30494__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationString30517 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationString30517_literalMutationString32380 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationString30517_literalMutationString32380_literalMutationChar39599() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("&KPBl6hE9&ef[");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('&', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30517__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30517__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30517__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30517__5 = r.matches("&ba;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30517__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30517__7 = r.consumeToAny('&', '\n');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("KPBl6hE9", o_consumeToAny_literalMutationString30517__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30517__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30517__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ef[", o_consumeToAny_literalMutationString30517__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30517__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("KPBl6hE9", o_consumeToAny_literalMutationString30517__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationString30491 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationString30491_sd30851 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationString30491_sd30851_literalMutationString45287() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30491__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30491__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30491__5 = r.matches("|[/}5");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30491__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30491__7 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30491__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30491__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__9);
        // AssertGenerator create local variable with return value of invocation
        int o_consumeToAny_literalMutationString30491_sd30851__24 = // StatementAdd: add invocation of a method
        r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30491__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30541 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30541_sd33896 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30541_sd33896_literalMutationChar40639() {
        char __DSPOT_c_2198 = '(';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30541__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30541__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30541__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30541__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30541__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30541__7 = r.consumeToAny('*', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30541__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30541__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30541__9 = r.consumeToAny('&', '<');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30541__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30541_sd33896__25 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_2198);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30541_sd33896__25);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30541__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30541__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30541__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30530 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30530_literalMutationChar33217 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30530_literalMutationChar33217_literalMutationChar44085() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30530__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30530__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30530__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30530__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30530__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30530__7 = r.consumeToAny('&', ',');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30530__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30530__9 = r.consumeToAny('&', ' ');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30530__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar; qux", o_consumeToAny_literalMutationChar30530__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30530__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30512 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30512_literalMutationString32097 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30512_literalMutationString32097_literalMutationString35303() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("-:{4#()=IW{fu");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('-', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30512__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("-:{4#()=IW{fu", o_consumeToAny_literalMutationChar30512__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30512__4 = r.matches('%');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30512__5 = r.matches("");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30512__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30512__7 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30512__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30512__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30512__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(15, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("-:{4#()=IW{fu", o_consumeToAny_literalMutationChar30512__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30512__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_sd30544 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_sd30544_literalMutationString34048 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_sd30544_literalMutationString34048_sd44709() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30544__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30544__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30544__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30544__5 = r.matches("&bGar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30544__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30544__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30544__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30544__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30544__9 = r.consumeToAny('&', ';');
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30544_literalMutationString34048_sd44709__26 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_sd30544_literalMutationString34048_sd44709__26)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_sd30544__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30544__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_sd30544__9);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30524 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30524_literalMutationChar32848 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30524_literalMutationChar32848_sd41516() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30524__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30524__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30524__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30524__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30524__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30524__7 = r.consumeToAny('%', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30524__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30524__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30524__9 = r.consumeToAny('%', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30524__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30524_literalMutationChar32848_sd41516__24 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30524_literalMutationChar32848_sd41516__24);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_literalMutationChar30524__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar", o_consumeToAny_literalMutationChar30524__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" qux", o_consumeToAny_literalMutationChar30524__9);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToChar */
    @org.junit.Test(timeout = 10000)
    public void consumeToChar_sd47964() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47964__3 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_sd47964__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47964__4 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_sd47964__4);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_sd47964__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47964__5)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47964__6 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToChar_sd47964__6);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_sd47964__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47964__7)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47964__8 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hree", o_consumeToChar_sd47964__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47964__9 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_sd47964__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_sd47964__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hree", o_consumeToChar_sd47964__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_sd47964__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47964__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47964__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToChar_sd47964__6);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToChar */
    @org.junit.Test(timeout = 10000)
    public void consumeToChar_literalMutationChar47951_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
            r.consumeTo('T');
            r.consumeTo('T');
            r.consume();
            r.consumeTo('\u0000');
            r.consume();
            r.consumeTo('T');
            org.junit.Assert.fail("consumeToChar_literalMutationChar47951 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToChar */
    @org.junit.Test(timeout = 10000)
    public void consumeToChar_sd47963() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47963__3 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_sd47963__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47963__4 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_sd47963__4);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_sd47963__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47963__5)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47963__6 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToChar_sd47963__6);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_sd47963__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47963__8 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hree", o_consumeToChar_sd47963__8);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hree", o_consumeToChar_sd47963__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47963__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47963__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToChar_sd47963__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_sd47963__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_sd47963__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToChar */
    @org.junit.Test(timeout = 10000)
    public void consumeToChar_sd47969() {
        char[] __DSPOT_chars_2619 = new char[]{ 't' , 'm' , '%' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47969__4 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_sd47969__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47969__5 = r.consumeTo('T');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_sd47969__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47969__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47969__7 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToChar_sd47969__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_sd47969__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47969__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47969__9 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hree", o_consumeToChar_sd47969__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_sd47969__10 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_2619);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_sd47969__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_sd47969__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_sd47969__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToChar_sd47969__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47969__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_sd47969__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hree", o_consumeToChar_sd47969__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToChar */
    @org.junit.Test(timeout = 10000)
    public void consumeToChar_literalMutationChar47953() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_literalMutationChar47953__3 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_literalMutationChar47953__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_literalMutationChar47953__4 = r.consumeTo('T');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_literalMutationChar47953__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_literalMutationChar47953__5)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_literalMutationChar47953__6 = r.consumeTo('r');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Th", o_consumeToChar_literalMutationChar47953__6);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_literalMutationChar47953__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('r', ((char) (o_consumeToChar_literalMutationChar47953__7)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_literalMutationChar47953__8 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ee", o_consumeToChar_literalMutationChar47953__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('r', ((char) (o_consumeToChar_literalMutationChar47953__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Th", o_consumeToChar_literalMutationChar47953__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_literalMutationChar47953__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_literalMutationChar47953__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_literalMutationChar47953__5)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToChar */
    @org.junit.Test(timeout = 10000)
    public void consumeToChar_literalMutationChar47952() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_literalMutationChar47952__3 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_literalMutationChar47952__3);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_literalMutationChar47952__4 = r.consumeTo('T');
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_literalMutationChar47952__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_literalMutationChar47952__5)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_literalMutationChar47952__6 = r.consumeTo(' ');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo", o_consumeToChar_literalMutationChar47952__6);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToChar_literalMutationChar47952__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeToChar_literalMutationChar47952__7)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToChar_literalMutationChar47952__8 = r.consumeTo('T');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_literalMutationChar47952__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_consumeToChar_literalMutationChar47952__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo", o_consumeToChar_literalMutationChar47952__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToChar_literalMutationChar47952__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToChar_literalMutationChar47952__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToChar_literalMutationChar47952__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToChar */
    @org.junit.Test(timeout = 10000)
    public void consumeToChar_literalMutationString47934_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
            r.consumeTo('T');
            r.consumeTo('T');
            r.consume();
            r.consumeTo('T');
            r.consume();
            r.consumeTo('T');
            org.junit.Assert.fail("consumeToChar_literalMutationString47934 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToChar */
    @org.junit.Test(timeout = 10000)
    public void consumeToChar_literalMutationChar47947_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
            r.consumeTo('T');
            r.consumeTo('7');
            r.consume();
            r.consumeTo('T');
            r.consume();
            r.consumeTo('T');
            org.junit.Assert.fail("consumeToChar_literalMutationChar47947 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58266() {
        java.lang.String in = "one two three";
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58267() {
        java.lang.String in = "one two three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58267__7 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_literalMutationString58263() {
        java.lang.String in = "one two thre";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thre", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thre", toEnd);
        r.isEmpty();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thre", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(12, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thre", toEnd);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58269() {
        char __DSPOT_c_3094 = '.';
        java.lang.String in = "one two three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58269__8 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_3094);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58269__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58270() {
        java.lang.String in = "one two three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToEnd_sd58270__7 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToEnd_sd58270__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58272() {
        char[] __DSPOT_chars_3095 = new char[]{ 'u' };
        java.lang.String in = "one two three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58272__8 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_3095);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58272__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_literalMutationString58264 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_literalMutationString58264_sd58370() {
        char __DSPOT_c_3104 = 'v';
        java.lang.String in = "6EdJDRbY[bn^R";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('6', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", toEnd);
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_literalMutationString58264_sd58370__8 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_3104);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_literalMutationString58264_sd58370__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58269 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58269_literalMutationString58432() {
        char __DSPOT_c_3094 = '.';
        java.lang.String in = "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", toEnd);
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58269__8 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_3094);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58269__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_literalMutationString58265 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_literalMutationString58265_literalMutationString58374() {
        java.lang.String in = "";
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", toEnd);
        r.isEmpty();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", in);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_literalMutationString58264 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_literalMutationString58264_literalMutationString58365() {
        java.lang.String in = "Fc]c3Eqj[H!2k";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Fc]c3Eqj[H!2k", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('F', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Fc]c3Eqj[H!2k", toEnd);
        r.isEmpty();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Fc]c3Eqj[H!2k", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Fc]c3Eqj[H!2k", in);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_literalMutationString58263 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_literalMutationString58263_literalMutationString58353() {
        java.lang.String in = "pS_# jDDkoc1";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("pS_# jDDkoc1", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('p', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        r.isEmpty();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("pS_# jDDkoc1", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("pS_# jDDkoc1", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(12, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_literalMutationString58261 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_literalMutationString58261_sd58334() {
        char[] __DSPOT_chars_3099 = new char[]{ 'O' , '*' };
        java.lang.String in = "<span>Hello <div>there</div> <span>now</span></span>";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", toEnd);
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_literalMutationString58261_sd58334__8 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_3099);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_literalMutationString58261_sd58334__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58266_literalMutationString58390() {
        java.lang.String in = "one 2wo three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one 2wo three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        r.isEmpty();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one 2wo three", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one 2wo three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58266_sd58399() {
        char[] __DSPOT_chars_3109 = new char[]{ '`' , 'B' , '[' , '<' };
        java.lang.String in = "one two three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58266_sd58399__10 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_3109);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58266_sd58399__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_literalMutationString58264 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_literalMutationString58264_sd58368() {
        java.lang.String in = "6EdJDRbY[bn^R";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('6', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_literalMutationString58264_sd58368__7 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_literalMutationString58264_sd58368__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58268 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58268_sd58424 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58268_sd58424_sd60594() {
        java.lang.String in = "one two three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        // StatementAdd: add invocation of a method
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        int o_consumeToEnd_sd58268_sd58424__9 = // StatementAdd: add invocation of a method
        r.pos();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266_sd58396 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58266_sd58396_failAssert1_literalMutationString61752() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_3108 = 't';
            java.lang.String in = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("", in);
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
            java.lang.String toEnd = r.consumeToEnd();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("", toEnd);
            r.isEmpty();
            // StatementAdd: add invocation of a method
            r.advance();
            // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_3108);
            org.junit.Assert.fail("consumeToEnd_sd58266_sd58396 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58269 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58269_literalMutationChar58430 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58269_literalMutationChar58430_sd60711() {
        char __DSPOT_c_3354 = 'y';
        char __DSPOT_c_3094 = '-';
        java.lang.String in = "one two three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58269__8 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_3094);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58269__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58269_literalMutationChar58430_sd60711__13 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_3354);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58269_literalMutationChar58430_sd60711__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58269__8);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58267 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58267_literalMutationString58403 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58267_literalMutationString58403_sd60289() {
        java.lang.String in = "oPe two three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("oPe two three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58267__7 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58267_literalMutationString58403_sd60289__11 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267_literalMutationString58403_sd60289__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("oPe two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("oPe two three", toEnd);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266_sd58395 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58266_sd58395_sd60179_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_3286 = '(';
            java.lang.String in = "one two three";
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
            java.lang.String toEnd = r.consumeToEnd();
            r.isEmpty();
            // StatementAdd: add invocation of a method
            r.advance();
            // StatementAdd: add invocation of a method
            r.isEmpty();
            // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_3286);
            org.junit.Assert.fail("consumeToEnd_sd58266_sd58395_sd60179 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_literalMutationString58264 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_literalMutationString58264_sd58373 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_literalMutationString58264_sd58373_literalMutationChar59869() {
        char[] __DSPOT_chars_3105 = new char[]{ ',' , 'N' };
        java.lang.String in = "6EdJDRbY[bn^R";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('6', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", toEnd);
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_literalMutationString58264_sd58373__8 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_3105);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_literalMutationString58264_sd58373__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("6EdJDRbY[bn^R", in);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266_literalMutationString58392 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58266_literalMutationString58392_literalMutationString60147() {
        java.lang.String in = "CuJ<qpVJRt Q";
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CuJ<qpVJRt Q", toEnd);
        r.isEmpty();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CuJ<qpVJRt Q", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("CuJ<qpVJRt Q", in);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58267 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58267_sd58407 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58267_sd58407_sd60343() {
        char __DSPOT_c_3308 = 'S';
        java.lang.String in = "one two three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58267__7 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58267_sd58407__11 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267_sd58407__11);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58267_sd58407_sd60343__16 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_3308);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267_sd58407_sd60343__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267_sd58407__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58268 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58268_literalMutationString58414 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58268_literalMutationString58414_sd60462() {
        java.lang.String in = "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        r.isEmpty();
        // StatementAdd: add invocation of a method
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToEnd_sd58268_literalMutationString58414_sd60462__9 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToEnd_sd58268_literalMutationString58414_sd60462__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", toEnd);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58266_literalMutationString58391 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58266_literalMutationString58391_sd60143() {
        char[] __DSPOT_chars_3281 = new char[]{ '<' , '_' };
        java.lang.String in = "one two thr]ee";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thr]ee", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thr]ee", toEnd);
        r.isEmpty();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58266_literalMutationString58391_sd60143__10 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_3281);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58266_literalMutationString58391_sd60143__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thr]ee", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(15, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thr]ee", toEnd);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58267 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58267_literalMutationString58402 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58267_literalMutationString58402_sd60279() {
        java.lang.String in = "one two thre";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thre", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thre", toEnd);
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58267__7 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToEnd_sd58267_literalMutationString58402_sd60279__11 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToEnd_sd58267_literalMutationString58402_sd60279__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(12, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thre", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two thre", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58267__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62365_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
            r.consumeTo("");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToString_literalMutationString62365 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_sd62389() {
        char[] __DSPOT_chars_3477 = new char[]{ '`' , 'o' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62389__4 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_sd62389__4);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_sd62389__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62389__5)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62389__6 = r.consumeTo("Two");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_sd62389__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62389__7)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62389__8 = r.consumeTo("Qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString_sd62389__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62389__9 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_3477);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToString_sd62389__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62389__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_sd62389__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62389__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString_sd62389__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString_sd62389__8);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62367_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
            r.consumeTo("T0wo");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToString_literalMutationString62367 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62378() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_literalMutationString62378__3 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_literalMutationString62378__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_literalMutationString62378__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_literalMutationString62378__4)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_literalMutationString62378__5 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString_literalMutationString62378__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_literalMutationString62378__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_literalMutationString62378__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_literalMutationString62378__7 = r.consumeTo("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString_literalMutationString62378__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString_literalMutationString62378__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_literalMutationString62378__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_literalMutationString62378__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_literalMutationString62378__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62375_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("To");
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToString_literalMutationString62375 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62374_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("-wo");
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToString_literalMutationString62374 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62371_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("");
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToString_literalMutationString62371 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62372_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("<span>Hello <div>there</div> <span>now</span></span>");
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToString_literalMutationString62372 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62359_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToString_literalMutationString62359 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_sd62383() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62383__3 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_sd62383__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_sd62383__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62383__4)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62383__5 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString_sd62383__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_sd62383__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62383__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62383__7 = r.consumeTo("Qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString_sd62383__7);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62383__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString_sd62383__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(17, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62383__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_sd62383__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString_sd62383__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62369_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
            r.consumeTo("?wo");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToString_literalMutationString62369 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_sd62384() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62384__3 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_sd62384__3);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_sd62384__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62384__4)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62384__5 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString_sd62384__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_sd62384__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62384__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62384__7 = r.consumeTo("Qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString_sd62384__7);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62384__8 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToString_sd62384__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString_sd62384__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString_sd62384__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62384__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62384__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_sd62384__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_literalMutationString62362_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("kNi,yY6C!jRSf82p");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToString_literalMutationString62362 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToString */
    @org.junit.Test(timeout = 10000)
    public void consumeToString_sd62386() {
        char __DSPOT_c_3476 = '2';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Two Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62386__4 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_sd62386__4);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_sd62386__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62386__5)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62386__6 = r.consumeTo("Two");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString_sd62386__6);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToString_sd62386__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62386__8 = r.consumeTo("Qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString_sd62386__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToString_sd62386__9 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_3476);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToString_sd62386__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo ", o_consumeToString_sd62386__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("wo Four", o_consumeToString_sd62386__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62386__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (o_consumeToString_sd62386__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToString_sd62386__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_literalMutationString70742_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
            r.containsIgnoreCase("");
            r.containsIgnoreCase("three");
            r.containsIgnoreCase("one");
            org.junit.Assert.fail("containsIgnoreCase_literalMutationString70742 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_literalMutationString70749() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70749__3 = r.containsIgnoreCase("two");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70749__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70749__4 = r.containsIgnoreCase("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70749__5 = r.containsIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70749__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70749__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70749__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_sd70766() {
        char[] __DSPOT_chars_3941 = new char[]{ 'W' , 'r' , '(' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70766__4 = r.containsIgnoreCase("two");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70766__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70766__5 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70766__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70766__6 = r.containsIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_sd70766__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_containsIgnoreCase_sd70766__7 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_3941);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One T", o_containsIgnoreCase_sd70766__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70766__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('W', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(5, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70766__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_sd70766__6);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_literalMutationString70748_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
            r.containsIgnoreCase("two");
            r.containsIgnoreCase("");
            r.containsIgnoreCase("one");
            org.junit.Assert.fail("containsIgnoreCase_literalMutationString70748 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_literalMutationString70755() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70755__3 = r.containsIgnoreCase("two");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70755__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70755__4 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70755__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70755__5 = r.containsIgnoreCase("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70755__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70755__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70755__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_sd70763() {
        char __DSPOT_c_3940 = '!';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70763__4 = r.containsIgnoreCase("two");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70763__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70763__5 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70763__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70763__6 = r.containsIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_sd70763__6);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_containsIgnoreCase_sd70763__7 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_3940);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One TWO three", o_containsIgnoreCase_sd70763__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70763__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_sd70763__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70763__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_literalMutationString70736() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70736__3 = r.containsIgnoreCase("two");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70736__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70736__4 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70736__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70736__5 = r.containsIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70736__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70736__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70736__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_literalMutationString70757() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70757__3 = r.containsIgnoreCase("two");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70757__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70757__4 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70757__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70757__5 = r.containsIgnoreCase("oe");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70757__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70757__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_literalMutationString70757__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_sd70761() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70761__3 = r.containsIgnoreCase("two");
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70761__4 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70761__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70761__5 = r.containsIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_sd70761__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_containsIgnoreCase_sd70761__6 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One TWO three", o_containsIgnoreCase_sd70761__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70761__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_sd70761__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70761__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_literalMutationString70740() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("3$n3u q`A33@>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('3', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70740__3 = r.containsIgnoreCase("two");
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70740__4 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70740__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_literalMutationString70740__5 = r.containsIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70740__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70740__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_literalMutationString70740__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('3', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#containsIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void containsIgnoreCase_sd70760() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One TWO three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70760__3 = r.containsIgnoreCase("two");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70760__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70760__4 = r.containsIgnoreCase("three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70760__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_containsIgnoreCase_sd70760__5 = r.containsIgnoreCase("one");
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_containsIgnoreCase_sd70760__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70760__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_containsIgnoreCase_sd70760__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79431() {
        char[] __DSPOT_chars_4405 = new char[]{ '1' , 'c' , '[' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_mark_sd79431__4)));
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark_sd79431__7)));
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__10 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79431__10)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79431__11 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4405);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_mark_sd79431__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79431__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark_sd79431__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79431__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_mark_sd79431__4)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79426() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79426__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_mark_sd79426__3)));
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79426__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79426__5)));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79426__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark_sd79426__6)));
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79426__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79426__10 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_mark_sd79426__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark_sd79426__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79426__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79426__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_mark_sd79426__3)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79428() {
        char __DSPOT_c_4404 = '[';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_mark_sd79428__4)));
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79428__6)));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark_sd79428__7)));
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__10 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79428__10)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79428__11 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_4404);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_mark_sd79428__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79428__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79428__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark_sd79428__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_mark_sd79428__4)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79425() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_mark_sd79425__3)));
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79425__5)));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark_sd79425__6)));
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79425__9)));
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_mark_sd79425__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_mark_sd79425__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79425__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_mark_sd79425__5)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79420() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79420__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_mark_literalMutationString79420__3)));
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79420__5 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_mark_literalMutationString79420__5)));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79420__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79420__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_mark_literalMutationString79420__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_mark_literalMutationString79420__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_mark_literalMutationString79420__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_mark_literalMutationString79420__3)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79430 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79430_literalMutationString79615() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79430__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79430__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79430__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79430__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        int o_mark_sd79430__10 = // StatementAdd: add invocation of a method
        r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79429 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79429_sd79608() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__10 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79429_sd79608__22 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_mark_sd79429_sd79608__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79425 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79425_sd79552() {
        char __DSPOT_c_4418 = '1';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__9 = r.consume();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79425_sd79552__21 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_4418);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_mark_sd79425_sd79552__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79425 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79425_sd79553() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425__9 = r.consume();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79425_sd79553__20 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_mark_sd79425_sd79553__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79431 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79431_literalMutationString79647() {
        char[] __DSPOT_chars_4405 = new char[]{ '1' , 'c' , '[' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("oe");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__4 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__7 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__10 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79431__11 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4405);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_mark_sd79431__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79431 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79431_literalMutationString79649() {
        char[] __DSPOT_chars_4405 = new char[]{ '1' , 'c' , '[' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("oGne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__4 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__7 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__10 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79431__11 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4405);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne", o_mark_sd79431__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79429 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79429_sd79610() {
        char __DSPOT_c_4426 = 's';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79429__10 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79429_sd79610__23 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_4426);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_mark_sd79429_sd79610__23);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79427 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79427_sd79581() {
        char[] __DSPOT_chars_4423 = new char[]{ '|' , 'J' , 'd' , 'v' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79427__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79427__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79427__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79427__9 = r.consume();
        // StatementAdd: add invocation of a method
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79427_sd79581__21 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4423);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_mark_sd79427_sd79581__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79419 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79419_sd79483() {
        char[] __DSPOT_chars_4407 = new char[0];
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("oKne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79419__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79419__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79419__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79419__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_literalMutationString79419_sd79483__19 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4407);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne", o_mark_literalMutationString79419_sd79483__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79428 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79428_literalMutationString79589() {
        char __DSPOT_c_4404 = '[';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__4 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__7 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__10 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79428__11 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_4404);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("pan>Hello <div>there</div> <span>now</span></span>", o_mark_sd79428__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79420 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79420_sd79493() {
        char[] __DSPOT_chars_4409 = new char[]{ 'v' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79420__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79420__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79420__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79420__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_literalMutationString79420_sd79493__19 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4409);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_mark_literalMutationString79420_sd79493__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79424 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79424_sd79539 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79424_sd79539_literalMutationString81097() {
        char __DSPOT_c_4416 = '$';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79424__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79424__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79424__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79424__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_literalMutationString79424_sd79539__19 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_4416);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("pan>Hello <div>there</div> <span>now</span></span>", o_mark_literalMutationString79424_sd79539__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79419 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79419_literalMutationString79473 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79419_literalMutationString79473_sd80250() {
        char[] __DSPOT_chars_4437 = new char[]{ 's' , '(' , 'I' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79419__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79419__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79419__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79419__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_literalMutationString79419_literalMutationString79473_sd80250__19 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4437);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("pan>Hello <div>there</div> <", o_mark_literalMutationString79419_literalMutationString79473_sd80250__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('s', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(30, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79424 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79424_sd79542 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79424_sd79542_sd81151() {
        char[] __DSPOT_chars_4417 = new char[]{ '6' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Xne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('X', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79424__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79424__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79424__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79424__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_literalMutationString79424_sd79542__19 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4417);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_mark_literalMutationString79424_sd79542__19);
        // AssertGenerator create local variable with return value of invocation
        int o_mark_literalMutationString79424_sd79542_sd81151__23 = // StatementAdd: add invocation of a method
        r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (o_mark_literalMutationString79424_sd79542_sd81151__23)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_mark_literalMutationString79424_sd79542__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79423 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79423_literalMutationString79518 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79423_literalMutationString79518_literalMutationString80816() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79428 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79428_literalMutationChar79583 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79428_literalMutationChar79583_literalMutationString81734() {
        char __DSPOT_c_4404 = ' ';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("oRne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__4 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__7 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__10 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79428__11 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_4404);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne", o_mark_sd79428__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79428 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79428_literalMutationString79592 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79428_literalMutationString79592_literalMutationString81883() {
        char __DSPOT_c_4404 = '[';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("IY]J");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('I', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__4 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__7 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79428__10 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79428__11 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_4404);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("]J", o_mark_sd79428__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79423 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79423_sd79529 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79423_sd79529_literalMutationString80965() {
        char[] __DSPOT_chars_4415 = new char[]{ 'D' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_literalMutationString79423_sd79529__19 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4415);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_mark_literalMutationString79423_sd79529__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79427 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79427_literalMutationString79571 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79427_literalMutationString79571_sd81543() {
        char[] __DSPOT_chars_4627 = new char[0];
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("oe");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79427__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79427__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79427__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79427__9 = r.consume();
        // StatementAdd: add invocation of a method
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79427_literalMutationString79571_sd81543__21 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4627);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_mark_sd79427_literalMutationString79571_sd81543__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79423 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79423_sd79527 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79423_sd79527_sd80940() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("p#n");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('p', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423_sd79527__18 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_literalMutationString79423_sd79527_sd80940__22 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("n", o_mark_literalMutationString79423_sd79527_sd80940__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79423 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79423_sd79528 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79423_sd79528_sd80952() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("p#n");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('p', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__3 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__5 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_literalMutationString79423__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        int o_mark_literalMutationString79423_sd79528__18 = // StatementAdd: add invocation of a method
        r.pos();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79431 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79431_literalMutationString79646 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79431_literalMutationString79646_literalMutationChar83002() {
        char[] __DSPOT_chars_4405 = new char[]{ '1' , 'c' , ' ' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__4 = r.consume();
        r.mark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__7 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        // AssertGenerator create local variable with return value of invocation
        char o_mark_sd79431__10 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_mark_sd79431__11 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4405);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("pan>Hello", o_mark_sd79431__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(11, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#mark */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79425 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79425_literalMutationString79547 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79425_literalMutationString79547_sd81208_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_4580 = 'q';
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("oe");
            // AssertGenerator create local variable with return value of invocation
            char o_mark_sd79425__3 = r.consume();
            r.mark();
            // AssertGenerator create local variable with return value of invocation
            char o_mark_sd79425__5 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            char o_mark_sd79425__6 = r.consume();
            r.isEmpty();
            r.rewindToMark();
            // AssertGenerator create local variable with return value of invocation
            char o_mark_sd79425__9 = r.consume();
            // StatementAdd: add invocation of a method
            r.advance();
            // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_4580);
            org.junit.Assert.fail("mark_sd79425_literalMutationString79547_sd81208 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    @org.junit.Test(timeout = 10000)
    public void matches_sd84085() {
        char __DSPOT_c_4806 = '8';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__4 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84085__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__5 = r.matches("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84085__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__6 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84085__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__7 = r.matches("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84085__7);
        // AssertGenerator create local variable with return value of invocation
        char o_matches_sd84085__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_sd84085__8)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__9 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84085__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__10 = r.matches("ne Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84085__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__11 = r.matches("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84085__11);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84085__12 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84085__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__13 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84085__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84085__14 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_4806);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_sd84085__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84085__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84085__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84085__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84085__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_sd84085__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84085__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84085__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84085__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84085__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84085__12);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    @org.junit.Test(timeout = 10000)
    public void matches_sd84082() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__3 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84082__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__4 = r.matches("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84082__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__5 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84082__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__6 = r.matches("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84082__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matches_sd84082__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_sd84082__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__8 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84082__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__9 = r.matches("ne Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84082__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__10 = r.matches("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84082__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84082__11 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__12 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84082__12);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84082__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84082__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84082__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84082__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84082__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84082__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84082__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_sd84082__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84082__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84082__11);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    @org.junit.Test(timeout = 10000)
    public void matches_sd84083() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__3 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84083__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__4 = r.matches("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84083__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__5 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84083__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__6 = r.matches("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_sd84083__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_sd84083__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__8 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84083__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__9 = r.matches("ne Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84083__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__10 = r.matches("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84083__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84083__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84083__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__12 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84083__12);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84083__13 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_sd84083__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84083__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84083__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84083__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84083__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84083__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84083__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84083__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_sd84083__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84083__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84083__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84052() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84052__3 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_literalMutationString84052__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84052__4 = r.matches("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_literalMutationString84052__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84052__5 = r.matches("]ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84052__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84052__6 = r.matches("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84052__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84052__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_literalMutationString84052__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84052__8 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84052__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84052__9 = r.matches("ne Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_literalMutationString84052__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84052__10 = r.matches("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84052__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84052__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84052__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84052__12 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84052__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_literalMutationString84052__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84052__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84052__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84052__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84052__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_literalMutationString84052__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84052__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_literalMutationString84052__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_literalMutationString84052__3);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84032() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84032__3 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84032__4 = r.matches("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84032__5 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84032__6 = r.matches("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84032__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84032__8 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84032__9 = r.matches("ne Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84032__10 = r.matches("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__10);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84032__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matches_literalMutationString84032__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84032__12 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (o_matches_literalMutationString84032__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matches_literalMutationString84032__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_literalMutationString84032__10);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84031_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            r.matches('O');
            r.matches("One Two Three");
            r.matches("One");
            r.matches("one");
            r.consume();
            r.matches("One");
            r.matches("ne Two Three");
            r.matches("ne Two Three Four");
            r.consumeToEnd();
            r.matches("ne");
            org.junit.Assert.fail("matches_literalMutationString84031 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    @org.junit.Test(timeout = 10000)
    public void matches_sd84088() {
        char[] __DSPOT_chars_4807 = new char[]{ 'w' , '6' , 'l' , 'Z' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84088__4 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84088__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84088__5 = r.matches("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84088__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84088__6 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84088__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84088__7 = r.matches("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_sd84088__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_sd84088__8)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84088__9 = r.matches("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84088__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84088__10 = r.matches("ne Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84088__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84088__11 = r.matches("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84088__11);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84088__12 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84088__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84088__13 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84088__13);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84088__14 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4807);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_sd84088__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84088__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84088__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84088__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84088__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84088__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84088__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84088__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matches_sd84088__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matches_sd84088__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matches_sd84088__8)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_sd84082 */
    @org.junit.Test(timeout = 10000)
    public void matches_sd84082_literalMutationString87158() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__5 = r.matches("Oe");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__6 = r.matches("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_sd84082__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__8 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__9 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__10 = r.matches("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84082__11 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84082__12 = r.matches("ne");
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84082__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84080 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84080_literalMutationString87019_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__3 = r.matches('O');
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__4 = r.matches("One Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__5 = r.matches("One");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__6 = r.matches("one");
            // AssertGenerator create local variable with return value of invocation
            char o_matches_literalMutationString84080__7 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__8 = r.matches("One");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__9 = r.matches("ne Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__10 = r.matches("ne Two Three Four");
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_matches_literalMutationString84080__11 = r.consumeToEnd();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__12 = r.matches("<span>Hello <div>there</div> <span>now</span></span>");
            org.junit.Assert.fail("matches_literalMutationString84080_literalMutationString87019 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84065 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84065_literalMutationString86176() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84065__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84065__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84065__5 = r.matches("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84065__6 = r.matches("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84065__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84065__8 = r.matches("Oe");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84065__9 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84065__10 = r.matches("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84065__11 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84065__12 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84065__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_sd84083 */
    @org.junit.Test(timeout = 10000)
    public void matches_sd84083_literalMutationString87198() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__5 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__6 = r.matches("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_sd84083__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__8 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__9 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__10 = r.matches("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84083__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matches_sd84083__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__12 = r.matches("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84083__13 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_sd84083__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matches_sd84083__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84057 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84057_sd85754() {
        char[] __DSPOT_chars_4859 = new char[]{ 'G' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84057__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84057__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84057__5 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84057__6 = r.matches("oe");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84057__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84057__8 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84057__9 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84057__10 = r.matches("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84057__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84057__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84057__12 = r.matches("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84057_sd85754__34 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_4859);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_literalMutationString84057_sd85754__34);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84057__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84031 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84031_failAssert0_literalMutationString87600() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87600__5 = r.matches('O');
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matches_literalMutationString84031_failAssert0_literalMutationString87600__5);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87600__6 = r.matches("One Two Three");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matches_literalMutationString84031_failAssert0_literalMutationString87600__6);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87600__7 = r.matches("One");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matches_literalMutationString84031_failAssert0_literalMutationString87600__7);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87600__8 = r.matches("gne");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matches_literalMutationString84031_failAssert0_literalMutationString87600__8);
            // AssertGenerator create local variable with return value of invocation
            char o_matches_literalMutationString84031_failAssert0_literalMutationString87600__9 = r.consume();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('\uffff', ((char) (o_matches_literalMutationString84031_failAssert0_literalMutationString87600__9)));
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87600__10 = r.matches("One");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matches_literalMutationString84031_failAssert0_literalMutationString87600__10);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87600__11 = r.matches("ne Two Three");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matches_literalMutationString84031_failAssert0_literalMutationString87600__11);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87600__12 = r.matches("ne Two Three Four");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matches_literalMutationString84031_failAssert0_literalMutationString87600__12);
            r.consumeToEnd();
            r.matches("ne");
            org.junit.Assert.fail("matches_literalMutationString84031 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_sd84085 */
    @org.junit.Test(timeout = 10000)
    public void matches_sd84085_literalMutationString87360() {
        char __DSPOT_c_4806 = '8';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__4 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__5 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__6 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__7 = r.matches("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_sd84085__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__9 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__10 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__11 = r.matches("");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84085__12 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84085__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84085__13 = r.matches("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84085__14 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_4806);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_sd84085__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_sd84085__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84055 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84055_literalMutationString85615 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84055_literalMutationString85615_sd91097() {
        char __DSPOT_c_5020 = '&';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84055__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84055__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84055__5 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84055__6 = r.matches("");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84055__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84055__8 = r.matches("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84055__9 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84055__10 = r.matches("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84055__11 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84055__12 = r.matches("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84055_literalMutationString85615_sd91097__34 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5020);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_literalMutationString84055_literalMutationString85615_sd91097__34);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84055__11);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84033 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84033_sd84372 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84033_sd84372_literalMutationString90380() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One vwo Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84033__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84033__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84033__5 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84033__6 = r.matches("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84033__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84033__8 = r.matches("Oine");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84033__9 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84033__10 = r.matches("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84033__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne vwo Three", o_matches_literalMutationString84033__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84033__12 = r.matches("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84033_sd84372__33 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_literalMutationString84033_sd84372__33);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne vwo Three", o_matches_literalMutationString84033__11);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84048 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84048_literalMutationString85217 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84048_literalMutationString85217_sd93569() {
        char[] __DSPOT_chars_5105 = new char[0];
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84048__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84048__4 = r.matches("One TwoThree");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84048__5 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84048__6 = r.matches("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84048__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84048__8 = r.matches("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84048__9 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84048__10 = r.matches("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84048__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84048__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84048__12 = r.matches("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84048_literalMutationString85217_sd93569__34 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_5105);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_literalMutationString84048_literalMutationString85217_sd93569__34);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84048__11);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84058 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84058_sd85806 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84058_sd85806_literalMutationString97877() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__5 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__6 = r.matches("+ ?");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84058__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__8 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__9 = r.matches("n` Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__10 = r.matches("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84058__11 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__12 = r.matches("ne");
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84058__11);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84059 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84059_literalMutationString85856 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84059_literalMutationString85856_literalMutationString93658() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84059__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84059__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84059__5 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84059__6 = r.matches("xne");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84059__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84059__8 = r.matches("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84059__9 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84059__10 = r.matches("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84059__11 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84059__12 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matches_literalMutationString84059__11);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84031 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84031_failAssert0_literalMutationString87621_literalMutationString90209() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87621__5 = r.matches('O');
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87621__6 = r.matches("One Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87621__7 = r.matches("One");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87621__8 = r.matches("one");
            // AssertGenerator create local variable with return value of invocation
            char o_matches_literalMutationString84031_failAssert0_literalMutationString87621__9 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87621__10 = r.matches("One");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87621__11 = r.matches("ne Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84031_failAssert0_literalMutationString87621__12 = r.matches("");
            r.consumeToEnd();
            r.matches("5");
            org.junit.Assert.fail("matches_literalMutationString84031 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84058 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84058_literalMutationString85756 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84058_literalMutationString85756_literalMutationString92766() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></s%pan>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__3 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__4 = r.matches("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__5 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__6 = r.matches("+ ?");
        // AssertGenerator create local variable with return value of invocation
        char o_matches_literalMutationString84058__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__8 = r.matches("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__9 = r.matches("ne Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__10 = r.matches("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_literalMutationString84058__11 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></s%pan>", o_matches_literalMutationString84058__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_literalMutationString84058__12 = r.matches("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></s%pan>", o_matches_literalMutationString84058__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(53, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100426() {
        char[] scan = new char[]{ ' ' , '\u0000' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__4 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationChar100426__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100426__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo", o_matchesAny_literalMutationChar100426__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100426__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_matchesAny_literalMutationChar100426__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationChar100426__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationChar100426__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_literalMutationChar100426__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo", o_matchesAny_literalMutationChar100426__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\t', ((char) (o_matchesAny_literalMutationChar100426__7)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100445() {
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100445__4 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100445__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100445__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100445__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100445__6 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_sd100445__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100445__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_sd100445__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100445__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100445__8);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100445__9 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two\tThree", o_matchesAny_sd100445__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100445__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_sd100445__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_sd100445__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100445__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100445__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100447() {
        char __DSPOT_c_5324 = ',';
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100447__5 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100447__5);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100447__6 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100447__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100447__7 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100447__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_sd100447__8)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100447__9 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100447__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100447__10 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5324);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two\tThree", o_matchesAny_sd100447__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100447__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100447__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100447__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_sd100447__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_sd100447__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100444() {
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100444__4 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100444__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100444__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100444__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100444__6 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_sd100444__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100444__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_sd100444__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100444__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100444__8);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('w', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(5, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100444__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100444__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_sd100444__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_sd100444__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100444__8);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationString100439() {
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100439__4 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100439__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationString100439__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello", o_matchesAny_literalMutationString100439__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100439__6 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_literalMutationString100439__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationString100439__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_matchesAny_literalMutationString100439__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100439__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100439__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello", o_matchesAny_literalMutationString100439__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_literalMutationString100439__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100439__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(12, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (o_matchesAny_literalMutationString100439__7)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationString100438() {
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100438__4 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100438__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationString100438__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationString100438__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100438__6 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100438__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationString100438__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_matchesAny_literalMutationString100438__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100438__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100438__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_matchesAny_literalMutationString100438__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100438__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100438__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationString100438__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100450() {
        char[] __DSPOT_chars_5325 = new char[]{ '5' };
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100450__5 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100450__6 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100450__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100450__7 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_sd100450__7);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100450__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_sd100450__8)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100450__9 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100450__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100450__10 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_5325);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two\tThree", o_matchesAny_sd100450__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100450__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100450__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_sd100450__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_sd100450__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_sd100450__8)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100433() {
        char[] scan = new char[]{ ' ' , '\n' , ' ' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100433__4 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationChar100433__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100433__5 = r.consumeToAny(scan);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100433__6 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_literalMutationChar100433__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100433__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_literalMutationChar100433__7)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100433__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationChar100433__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_literalMutationChar100433__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesAny_literalMutationChar100433__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationChar100433__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\n', ((char) (o_matchesAny_literalMutationChar100433__7)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationString100443() {
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("sXItvvs!/+O6S");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('s', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100443__4 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100443__4);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationString100443__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("sXItvvs!/+O6S", o_matchesAny_literalMutationString100443__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100443__6 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100443__6);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationString100443__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100443__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100443__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_matchesAny_literalMutationString100443__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100443__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("sXItvvs!/+O6S", o_matchesAny_literalMutationString100443__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesAny_literalMutationString100443__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100446 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100446_sd101306() {
        char __DSPOT_c_5374 = '&';
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100446__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100446__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100446__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100446__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100446__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100446__8 = r.matchesAny(scan);
        // StatementAdd: add invocation of a method
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100446_sd101306__22 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5374);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two\tThree", o_matchesAny_sd100446_sd101306__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100446__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100434 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100434_sd100968() {
        char __DSPOT_c_5352 = 'e';
        char[] scan = new char[]{ ' ' , '\n' , 'T' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100434__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100434__5 = r.consumeToAny(scan);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100434__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100434__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100434__8 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100434_sd100968__20 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5352);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two\tThr", o_matchesAny_literalMutationChar100434_sd100968__20);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(11, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_literalMutationChar100434__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100427 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100427_sd100785() {
        char[] __DSPOT_chars_5341 = new char[0];
        char[] scan = new char[]{ ' ' , ' ' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100427__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100427__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo", o_matchesAny_literalMutationChar100427__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100427__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100427__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100427__8 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100427_sd100785__20 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_5341);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Three", o_matchesAny_literalMutationChar100427_sd100785__20);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo", o_matchesAny_literalMutationChar100427__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100435 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100435_sd100997() {
        char[] scan = new char[]{ ' ' , '\n' , '\n' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100435__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100435__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_literalMutationChar100435__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100435__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100435__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100435__8 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100435_sd100997__19 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two\tThree", o_matchesAny_literalMutationChar100435_sd100997__19);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_literalMutationChar100435__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100425 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100425_literalMutationChar100696() {
        char[] scan = new char[]{ '\u000b' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100425__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100425__5 = r.consumeToAny(scan);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100425__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100425__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100425__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_literalMutationChar100425__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationString100438 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationString100438_sd101055() {
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100438__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationString100438__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationString100438__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100438__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationString100438__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100438__8 = r.matchesAny(scan);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationString100438__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationString100438 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationString100438_sd101058_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_5358 = 'Y';
            char[] scan = new char[]{ ' ' , '\n' , '\t' };
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesAny_literalMutationString100438__4 = r.matchesAny(scan);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_matchesAny_literalMutationString100438__5 = r.consumeToAny(scan);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesAny_literalMutationString100438__6 = r.matchesAny(scan);
            // AssertGenerator create local variable with return value of invocation
            char o_matchesAny_literalMutationString100438__7 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesAny_literalMutationString100438__8 = r.matchesAny(scan);
            // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_5358);
            org.junit.Assert.fail("matchesAny_literalMutationString100438_sd101058 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100426 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100426_literalMutationString100742() {
        char[] scan = new char[]{ ' ' , '\u0000' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100426__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationChar100426__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100426__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationChar100426__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100446 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100446_literalMutationString101299() {
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100446__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100446__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello", o_matchesAny_sd100446__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100446__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100446__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100446__8 = r.matchesAny(scan);
        // StatementAdd: add invocation of a method
        r.isEmpty();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello", o_matchesAny_sd100446__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(12, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100426 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100426_literalMutationChar100737() {
        char[] scan = new char[]{ ' ' , '\u0000' , ' ' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100426__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo\tThree", o_matchesAny_literalMutationChar100426__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100426__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo\tThree", o_matchesAny_literalMutationChar100426__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100430 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100430_sd100875 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100430_sd100875_literalMutationChar102625() {
        char __DSPOT_c_5346 = 'Y';
        char[] scan = new char[]{ ' ' , '\t' , '\n' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100430__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100430__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_literalMutationChar100430__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100430__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100430__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100430__8 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100430_sd100875__20 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5346);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two\tThree", o_matchesAny_literalMutationChar100430_sd100875__20);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_literalMutationChar100430__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100435 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100435_literalMutationChar100984 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100435_literalMutationChar100984_literalMutationString105758() {
        char[] scan = new char[]{ ' ' , '\n' , '\u0000' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100435__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100435__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationChar100435__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100435__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100435__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100435__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationChar100435__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100446 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100446_sd101306 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100446_sd101306_sd105522() {
        char __DSPOT_c_5596 = 'b';
        char __DSPOT_c_5374 = '&';
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100446__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100446__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100446__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100446__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100446__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100446__8 = r.matchesAny(scan);
        // StatementAdd: add invocation of a method
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100446_sd101306__22 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5374);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100446_sd101306_sd105522__27 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5596);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_sd100446_sd101306_sd105522__27);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Two\tThree", o_matchesAny_sd100446_sd101306__22);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100446__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100422 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100422_sd100630 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100422_sd100630_literalMutationString103802() {
        char[] __DSPOT_chars_5331 = new char[]{ '[' , 'C' };
        char[] scan = new char[]{ 'G' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100422__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100422__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_matchesAny_literalMutationChar100422__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100422__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100422__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100422__8 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100422_sd100630__20 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_5331);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationChar100422_sd100630__20);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(53, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_matchesAny_literalMutationChar100422__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationString100443 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationString100443_literalMutationChar101200 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationString100443_literalMutationChar101200_literalMutationChar103156() {
        char[] scan = new char[]{ ' ' , '\t' , 'C' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("sXItvvs!/+O6S");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('s', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100443__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationString100443__5 = r.consumeToAny(scan);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100443__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationString100443__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100443__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("sXItvvs!/+O6S", o_matchesAny_literalMutationString100443__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100450 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100450_literalMutationChar101423 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100450_literalMutationChar101423_sd104229() {
        char[] __DSPOT_chars_5325 = new char[]{ '5' };
        char[] scan = new char[]{ ' ' , 'B' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100450__5 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100450__6 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo", o_matchesAny_sd100450__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100450__7 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100450__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100450__9 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100450__10 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_5325);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100450_literalMutationChar101423_sd104229__24 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_sd100450_literalMutationChar101423_sd104229__24);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Three", o_matchesAny_sd100450__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo", o_matchesAny_sd100450__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100435 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100435_literalMutationChar100984 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100435_literalMutationChar100984_literalMutationChar105742() {
        char[] scan = new char[]{ 'O' , '\n' , '\u0000' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100435__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100435__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationChar100435__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100435__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100435__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100435__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationChar100435__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100426 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100426_literalMutationChar100735 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100426_literalMutationChar100735_literalMutationChar103303() {
        char[] scan = new char[]{ '`' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100426__5 = r.consumeToAny(scan);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100426__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100426__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_literalMutationChar100426__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100445 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100445_sd101275 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100445_sd101275_literalMutationChar104741() {
        char __DSPOT_c_5372 = 'h';
        char[] scan = new char[]{ ' ' , ' ' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100445__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100445__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo", o_matchesAny_sd100445__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100445__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100445__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100445__8 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100445__9 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Three", o_matchesAny_sd100445__9);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100445_sd101275__24 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5372);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("T", o_matchesAny_sd100445_sd101275__24);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Three", o_matchesAny_sd100445__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One\nTwo", o_matchesAny_sd100445__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('h', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(9, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100428 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100428_literalMutationString100804 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100428_literalMutationString100804_sd106689() {
        char[] scan = new char[]{ ' ' , ']' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100428__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_literalMutationChar100428__5 = r.consumeToAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationChar100428__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100428__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationChar100428__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationChar100428__8 = r.matchesAny(scan);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationChar100428__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100445 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100445_literalMutationChar101254 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100445_literalMutationChar101254_literalMutationChar108260_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char[] scan = new char[]{ ' ' , '\u0000' , '2' };
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One\nTwo\tThree");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesAny_sd100445__4 = r.matchesAny(scan);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_matchesAny_sd100445__5 = r.consumeToAny(scan);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesAny_sd100445__6 = r.matchesAny(scan);
            // AssertGenerator create local variable with return value of invocation
            char o_matchesAny_sd100445__7 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesAny_sd100445__8 = r.matchesAny(scan);
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_matchesAny_sd100445__9 = // StatementAdd: add invocation of a method
            r.toString();
            org.junit.Assert.fail("matchesAny_sd100445_literalMutationChar101254_literalMutationChar108260 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109165_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            r.matchesIgnoreCase("O");
            r.matchesIgnoreCase("o");
            r.matches('O');
            r.matches('o');
            r.matchesIgnoreCase("One Two Three");
            r.matchesIgnoreCase("ONE two THREE");
            r.matchesIgnoreCase("One");
            r.matchesIgnoreCase("one");
            r.consume();
            r.matchesIgnoreCase("One");
            r.matchesIgnoreCase("NE Two Three");
            r.matchesIgnoreCase("ne Two Three Four");
            r.consumeToEnd();
            r.matchesIgnoreCase("ne");
            org.junit.Assert.fail("matchesIgnoreCase_literalMutationString109165 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_sd109237() {
        char __DSPOT_c_5784 = '!';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__4 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__5 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__6 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__7 = r.matches('o');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109237__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__8 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__9 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__10 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__11 = r.matchesIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__11);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_sd109237__12 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_sd109237__12)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__13 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109237__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__14 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__15 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109237__15);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109237__16 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109237__16);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__17 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109237__17);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109237__18 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5784);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_sd109237__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109237__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109237__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_sd109237__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109237__17);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109237__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109237__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109237__10);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109191() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__3 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__4 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__5 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__7 = r.matchesIgnoreCase("One WTwo Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109191__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__9 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__10 = r.matchesIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__10);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109191__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_literalMutationString109191__11)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__12 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109191__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109191__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109191__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109191__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109191__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109191__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109191__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109191__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109191__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109191__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109191__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109191__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_literalMutationString109191__11)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109170() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("!qa)F<}q Xh!A");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('!', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__3 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__4 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__5 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__6 = r.matches('o');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__9 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__10 = r.matchesIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__10);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109170__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('!', ((char) (o_matchesIgnoreCase_literalMutationString109170__11)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__12 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109170__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("qa)F<}q Xh!A", o_matchesIgnoreCase_literalMutationString109170__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("qa)F<}q Xh!A", o_matchesIgnoreCase_literalMutationString109170__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('!', ((char) (o_matchesIgnoreCase_literalMutationString109170__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109170__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_sd109240() {
        char[] __DSPOT_chars_5785 = new char[]{ 'X' , 'C' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__4 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__5 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__6 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__7 = r.matches('o');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109240__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__8 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__9 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__10 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__11 = r.matchesIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__11);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_sd109240__12 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_sd109240__12)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__13 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109240__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__14 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__14);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__15 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109240__15);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109240__16 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109240__16);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109240__17 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109240__17);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109240__18 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_5785);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_sd109240__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109240__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109240__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109240__17);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_sd109240__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109240__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109240__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109240__6);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109166() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__3 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__4 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__5 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__6 = r.matches('o');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__9 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__10 = r.matchesIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__10);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109166__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__12 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109166__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matchesIgnoreCase_literalMutationString109166__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matchesIgnoreCase_literalMutationString109166__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (o_matchesIgnoreCase_literalMutationString109166__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109166__13);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_sd109235() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__3 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__4 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__5 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__6 = r.matches('o');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109235__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__9 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__10 = r.matchesIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__10);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_sd109235__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_sd109235__11)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__12 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109235__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109235__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109235__15 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109235__16);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109235__17 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_sd109235__17);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109235__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109235__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_sd109235__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109235__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109235__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109235__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109235__12);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_sd109234() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__3 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__4 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__5 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__6 = r.matches('o');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109234__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__9 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__10 = r.matchesIgnoreCase("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__10);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_sd109234__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_sd109234__11)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__12 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109234__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109234__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109234__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109234__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__16 = r.matchesIgnoreCase("ne");
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109234__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109234__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109234__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_sd109234__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_sd109234__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109234__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_sd109234__8);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109165 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__5 = r.matchesIgnoreCase("O");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__5);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__6 = r.matchesIgnoreCase("o");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__6);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__7 = r.matches('O');
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__7);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__8 = r.matches('o');
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__8);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__9 = r.matchesIgnoreCase("One Two Three");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__9);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__10 = r.matchesIgnoreCase("ONE two THREE");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__10);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__11 = r.matchesIgnoreCase("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__11);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__12 = r.matchesIgnoreCase("one");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__12);
            // AssertGenerator create local variable with return value of invocation
            char o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__13 = r.consume();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('\uffff', ((char) (o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__13)));
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__14 = r.matchesIgnoreCase("One");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__14);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__15 = r.matchesIgnoreCase("NE Two Three");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__15);
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__16 = r.matchesIgnoreCase("ne Two Three Four");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109165_failAssert0_literalMutationString115198__16);
            r.consumeToEnd();
            r.matchesIgnoreCase("ne");
            org.junit.Assert.fail("matchesIgnoreCase_literalMutationString109165 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_sd109237 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_sd109237_literalMutationChar114871() {
        char __DSPOT_c_5784 = '!';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__4 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__5 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__6 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__7 = r.matches('n');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__8 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__9 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__10 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__11 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_sd109237__12 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__13 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__14 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__15 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109237__16 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109237__16);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109237__17 = r.matchesIgnoreCase("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109237__18 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5784);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_sd109237__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109237__16);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_sd109235 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_sd109235_literalMutationString114698() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__3 = r.matchesIgnoreCase("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_sd109235__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109235__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109235__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109235__17 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_sd109235__17);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109235__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109195 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109195_literalMutationString111676() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__8 = r.matchesIgnoreCase("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109195__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109195__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matchesIgnoreCase_literalMutationString109195__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109195__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matchesIgnoreCase_literalMutationString109195__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_sd109234 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_sd109234_literalMutationString114640() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__7 = r.matchesIgnoreCase("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_sd109234__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109234__15 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109234__16 = r.matchesIgnoreCase("ne");
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109234__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109209 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109209_literalMutationString112729() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__10 = r.matchesIgnoreCase(">ne");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109209__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109209__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matchesIgnoreCase_literalMutationString109209__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matchesIgnoreCase_literalMutationString109209__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109216 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109216_sd113325() {
        char[] __DSPOT_chars_5887 = new char[]{ 'm' , 'K' , '?' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109216__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__12 = r.matchesIgnoreCase("Oe");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109216__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109216__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109216_sd113325__46 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_5887);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_literalMutationString109216_sd113325__46);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109216__15);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109208 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109208_literalMutationString112658() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__3 = r.matchesIgnoreCase("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__10 = r.matchesIgnoreCase("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109208__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109208__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109208__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109208__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109208__15);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_sd109235 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_sd109235_literalMutationString114716 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_sd109235_literalMutationString114716_literalMutationString129278() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__7 = r.matchesIgnoreCase("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__8 = r.matchesIgnoreCase("OONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_sd109235__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109235__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109235__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_sd109235__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_sd109235__17 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_sd109235__17);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_sd109235__15);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109202 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109202_literalMutationString112239 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109202_literalMutationString112239_literalMutationString124731() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__9 = r.matchesIgnoreCase("<span>Hello <div>there</div> <spa^>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__10 = r.matchesIgnoreCase("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109202__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109202__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109202__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109202__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109202__15);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109170 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109170_sd109841 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109170_sd109841_literalMutationString129072() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("!qa)F<}q Xh!A");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('!', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109170__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__12 = r.matchesIgnoreCase("ODne");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109170__15 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109170__16 = r.matchesIgnoreCase("ne");
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("qa)F<}q Xh!A", o_matchesIgnoreCase_literalMutationString109170__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109216 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109216_sd113325 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109216_sd113325_literalMutationChar127564() {
        char[] __DSPOT_chars_5887 = new char[]{ 'm' , 'K' , '@' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109216__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__12 = r.matchesIgnoreCase("Oe");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109216__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109216__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109216__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109216_sd113325__46 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_5887);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_literalMutationString109216_sd113325__46);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109216__15);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109166 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109166_literalMutationChar109480 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109166_literalMutationChar109480_literalMutationString121099() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__5 = r.matches('\u0000');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__7 = r.matchesIgnoreCase("On Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109166__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109166__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matchesIgnoreCase_literalMutationString109166__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109166__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matchesIgnoreCase_literalMutationString109166__15);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109233 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109233_literalMutationString114548 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109233_literalMutationString114548_literalMutationString123312() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__4 = r.matchesIgnoreCase("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109233__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__13 = r.matchesIgnoreCase("NE Two Tree");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109233__15 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109233__16 = r.matchesIgnoreCase("u");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109233__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109168 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109168_literalMutationString109645 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109168_literalMutationString109645_sd117789() {
        char __DSPOT_c_5988 = '&';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Tfo Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__5 = r.matches('O');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__7 = r.matchesIgnoreCase("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109168__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109168__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Tfo Three", o_matchesIgnoreCase_literalMutationString109168__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109168__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109168_literalMutationString109645_sd117789__46 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_5988);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_literalMutationString109168_literalMutationString109645_sd117789__46);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Tfo Three", o_matchesIgnoreCase_literalMutationString109168__15);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationChar109187 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationChar109187_literalMutationString111070 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationChar109187_literalMutationString111070_failAssert0_literalMutationString130793() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__3 = r.matchesIgnoreCase("O");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__4 = r.matchesIgnoreCase("o");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__5 = r.matches('O');
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__6 = r.matches('n');
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__7 = r.matchesIgnoreCase("One Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__8 = r.matchesIgnoreCase("sA^}azB)FdJ:C");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__9 = r.matchesIgnoreCase("One");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__10 = r.matchesIgnoreCase("one");
            // AssertGenerator create local variable with return value of invocation
            char o_matchesIgnoreCase_literalMutationChar109187__11 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__12 = r.matchesIgnoreCase("One");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__13 = r.matchesIgnoreCase("NE Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__14 = r.matchesIgnoreCase("ne Two Three Four");
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_matchesIgnoreCase_literalMutationChar109187__15 = r.consumeToEnd();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationChar109187__16 = r.matchesIgnoreCase("ne");
            org.junit.Assert.fail("matchesIgnoreCase_literalMutationChar109187_literalMutationString111070 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_sd158677() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158677__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158677__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158677__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158677__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158677__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158677__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158677__6 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158677__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158677__7 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158677__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158677__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158677__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158677__9 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158677__10 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158677__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158677__11 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158677__11);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_rangeEquals_sd158677__12 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", o_rangeEquals_sd158677__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158677__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158677__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158677__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158677__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158677__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158677__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158677__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158677__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158677__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_sd158676() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158676__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158676__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158676__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158676__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158676__6 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158676__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158676__7 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158676__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158676__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158676__9 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158676__10 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158676__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158676__11 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__11);
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158676__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('h', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158676__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158676__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158676__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158676__8);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_sd158682() {
        char[] __DSPOT_chars_7755 = new char[]{ 'K' , '(' , '(' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__4 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158682__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__5 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__6 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__7 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158682__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__8 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__9 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158682__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__10 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__11 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158682__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__12 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__12);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_rangeEquals_sd158682__13 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_7755);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check\tCheck\tCheck\tCHO", o_rangeEquals_sd158682__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158682__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158682__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158682__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('K', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(21, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158682__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158682__4);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationNumber158580() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158580__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158580__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158580__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158580__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158580__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158580__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158580__6 = r.rangeEquals(// TestDataMutator on numbers
        0, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158580__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158580__8 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158580__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158580__9 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158580__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158580__10 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158580__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158580__11 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158580__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158580__12 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158580__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158580__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158580__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158580__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158580__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158580__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158580__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158580__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158580__10);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_sd158679() {
        char __DSPOT_c_7754 = 'z';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158679__4 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158679__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158679__5 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158679__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158679__6 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158679__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158679__7 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158679__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158679__8 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158679__9 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158679__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158679__10 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158679__10);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158679__11 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158679__11);
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158679__12 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158679__12);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_rangeEquals_sd158679__13 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_7754);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", o_rangeEquals_sd158679__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158679__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158679__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158679__11);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158679__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158679__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158679__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(23, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_rangeEquals_sd158679__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158679__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_rangeEquals_sd158679__9);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_literalMutationString158643 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationString158643_sd175717() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158643__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158643__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158643__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158643__6 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158643__7 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158643__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158643__9 = r.rangeEquals(12, 5, "Cheky");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158643__10 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158643__11 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_rangeEquals_literalMutationString158643_sd175717__30 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", o_rangeEquals_literalMutationString158643_sd175717__30);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_literalMutationString158607 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationString158607_sd170341() {
        char __DSPOT_c_7900 = 'I';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158607__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158607__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158607__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158607__6 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158607__7 = r.rangeEquals(6, 5, "");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158607__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158607__9 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158607__10 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158607__11 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_rangeEquals_literalMutationString158607_sd170341__31 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_7900);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", o_rangeEquals_literalMutationString158607_sd170341__31);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(23, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_literalMutationNumber158663 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__5 = r.rangeEquals(// TestDataMutator on numbers
            1, 5, "Check");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__5);
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__7 = r.rangeEquals(0, 5, "CHOKE");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__7);
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__8 = r.rangeEquals(0, 5, "Chec");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__8);
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__9 = r.rangeEquals(6, 5, "Check");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__9);
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__10 = r.rangeEquals(6, 5, "Chuck");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__10);
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__11 = r.rangeEquals(12, 5, "Check");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__11);
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__12 = r.rangeEquals(12, 5, "Cheeky");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__12);
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__13 = r.rangeEquals(18, 5, "CHOKE");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_rangeEquals_literalMutationNumber158663_failAssert5_literalMutationNumber182206__13);
            r.rangeEquals(// TestDataMutator on numbers
            36, 5, "CHIKE");
            org.junit.Assert.fail("rangeEquals_literalMutationNumber158663 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_literalMutationString158656 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationString158656_sd177558() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158656__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158656__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158656__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158656__6 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158656__7 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158656__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158656__9 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158656__10 = r.rangeEquals(18, 5, "<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationString158656__11 = r.rangeEquals(18, 5, "CHIKE");
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('h', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_literalMutationNumber158665 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationNumber158665_sd178795() {
        char[] __DSPOT_chars_8011 = new char[]{ 'X' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__6 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__7 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__9 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__10 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__11 = r.rangeEquals(18, // TestDataMutator on numbers
        4, "CHIKE");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_rangeEquals_literalMutationNumber158665_sd178795__32 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_8011);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", o_rangeEquals_literalMutationNumber158665_sd178795__32);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(23, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_literalMutationNumber158648 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationNumber158648_literalMutationNumber176251() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158648__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158648__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158648__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158648__6 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158648__7 = r.rangeEquals(// TestDataMutator on numbers
        0, 5, "Chuck");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158648__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158648__9 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158648__10 = r.rangeEquals(// TestDataMutator on numbers
        19, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158648__12 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_literalMutationNumber158665 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationNumber158665_literalMutationNumber178694() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__3 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__4 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__5 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__6 = r.rangeEquals(// TestDataMutator on numbers
        5, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__7 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__8 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__9 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__10 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_literalMutationNumber158665__11 = r.rangeEquals(18, // TestDataMutator on numbers
        4, "CHIKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_literalMutationString158529 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationString158529_failAssert0_literalMutationNumber181453() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
            r.rangeEquals(0, 5, "Check");
            r.rangeEquals(// TestDataMutator on numbers
            0, 5, "CHOKE");
            r.rangeEquals(0, 5, "Chec");
            r.rangeEquals(6, 5, "Check");
            r.rangeEquals(6, 5, "Chuck");
            r.rangeEquals(12, 5, "Check");
            r.rangeEquals(12, 5, "Cheeky");
            r.rangeEquals(18, 5, "CHOKE");
            r.rangeEquals(18, 5, "CHIKE");
            org.junit.Assert.fail("rangeEquals_literalMutationString158529 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_literalMutationString158670 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_literalMutationString158670_literalMutationNumber179528_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationString158670__3 = r.rangeEquals(0, 5, "Check");
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationString158670__4 = r.rangeEquals(0, 5, "CHOKE");
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationString158670__5 = r.rangeEquals(0, 5, "Chec");
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationString158670__6 = r.rangeEquals(6, 5, "Check");
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationString158670__7 = r.rangeEquals(6, 5, "Chuck");
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationString158670__8 = r.rangeEquals(12, 5, "Check");
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationString158670__9 = r.rangeEquals(12, 5, "Cheeky");
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationString158670__10 = r.rangeEquals(// TestDataMutator on numbers
            36, 5, "CHOKE");
            // AssertGenerator create local variable with return value of invocation
            boolean o_rangeEquals_literalMutationString158670__11 = r.rangeEquals(18, 5, "");
            org.junit.Assert.fail("rangeEquals_literalMutationString158670_literalMutationNumber179528 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals */
    /* amplification of org.jsoup.parser.CharacterReaderTest#rangeEquals_sd158682 */
    @org.junit.Test(timeout = 10000)
    public void rangeEquals_sd158682_literalMutationChar181270() {
        char[] __DSPOT_chars_7755 = new char[]{ 'K' , ')' , '(' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("Check\tCheck\tCheck\tCHOKE");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('C', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__4 = r.rangeEquals(0, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__5 = r.rangeEquals(0, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__6 = r.rangeEquals(0, 5, "Chec");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__7 = r.rangeEquals(6, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__8 = r.rangeEquals(6, 5, "Chuck");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__9 = r.rangeEquals(12, 5, "Check");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__10 = r.rangeEquals(12, 5, "Cheeky");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__11 = r.rangeEquals(18, 5, "CHOKE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_rangeEquals_sd158682__12 = r.rangeEquals(18, 5, "CHIKE");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_rangeEquals_sd158682__13 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_7755);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Check\tCheck\tCheck\tCHO", o_rangeEquals_sd158682__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('K', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(21, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214295() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214295__3)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__4 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214295__4)));
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__6 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214295__6)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214295__7)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214295__8)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214295__9)));
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__13 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214295__13)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214295__14)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__16 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214295__16)));
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__19 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214295__19)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214295__20 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214295__20);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214295__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214295__13)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214295__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214295__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214295__19)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214295__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214295__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214295__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214295__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214295__16)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214294() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214294__3)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__4 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214294__4)));
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__6 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214294__6)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214294__7)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214294__9)));
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__13 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214294__13)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214294__14)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__16 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214294__16)));
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__19 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214294__19)));
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214294__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214294__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214294__19)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214294__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214294__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214294__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214294__16)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214294__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214294__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214294__13)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214297() {
        char __DSPOT_c_8458 = 'S';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214297__4)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__5 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214297__5)));
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__7 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214297__7)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214297__8)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214297__9)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__10 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214297__10)));
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__14 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214297__14)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__15 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214297__15)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__17 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214297__17)));
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__20 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214297__21 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_8458);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214297__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214297__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214297__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214297__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214297__15)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214297__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214297__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214297__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214297__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214297__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214297__17)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214298() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__3 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214298__3)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__4 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214298__4)));
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__6 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214298__6)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__7 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214298__7)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214298__8)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__9 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__13 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214298__13)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__14 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214298__14)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__16 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214298__16)));
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__19 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214298__19)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214298__20 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214298__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214298__19)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214298__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214298__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214298__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214298__13)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214298__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214298__16)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214298__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214298__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214298__4)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214300() {
        char[] __DSPOT_chars_8459 = new char[]{ 'd' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__4 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214300__4)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__5 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214300__5)));
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__7 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214300__7)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214300__8)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__9 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214300__9)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__10 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214300__10)));
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__14 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214300__14)));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__15 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214300__15)));
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__17 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214300__17)));
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__20 = r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214300__20)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214300__21 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_8459);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214300__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214300__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214300__15)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214300__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214300__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214300__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214300__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('n', ((char) (o_unconsume_sd214300__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (o_unconsume_sd214300__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (o_unconsume_sd214300__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_unconsume_sd214300__17)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_sd214297 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214297_literalMutationString214457_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_8458 = 'S';
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__4 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__5 = r.current();
            r.unconsume();
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__7 = r.current();
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__8 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__9 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__10 = r.consume();
            r.isEmpty();
            r.unconsume();
            r.isEmpty();
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__14 = r.current();
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__15 = r.consume();
            r.isEmpty();
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__17 = r.consume();
            r.unconsume();
            r.isEmpty();
            // AssertGenerator create local variable with return value of invocation
            char o_unconsume_sd214297__20 = r.current();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_unconsume_sd214297__21 = // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_8458);
            org.junit.Assert.fail("unconsume_sd214297_literalMutationString214457 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_sd214295 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214295_sd214432() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__3 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__4 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__6 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__9 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__13 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__14 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__16 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214295__19 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214295__20 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214295__20);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214295_sd214432__44 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214295_sd214432__44);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214295__20);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_literalMutationString214292 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_literalMutationString214292_sd214393() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("otne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__3 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__4 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__6 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__9 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__13 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__14 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__16 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__19 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_literalMutationString214292_sd214393__40 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_unconsume_literalMutationString214292_sd214393__40);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_sd214297 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214297_sd214466() {
        char __DSPOT_c_8478 = 'o';
        char __DSPOT_c_8458 = 'S';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__4 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__5 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__7 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__10 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__14 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__15 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__17 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__20 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214297__21 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_8458);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214297__21);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214297_sd214466__46 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_8478);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214297_sd214466__46);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214297__21);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_literalMutationString214293 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_literalMutationString214293_sd214411() {
        char[] __DSPOT_chars_8471 = new char[]{ 'v' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("xne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('x', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__3 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__4 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__6 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__9 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__13 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__14 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__16 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214293__19 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_literalMutationString214293_sd214411__41 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_8471);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_literalMutationString214293_sd214411__41);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_sd214297 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214297_literalMutationString214458() {
        char __DSPOT_c_8458 = 'S';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__4 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__5 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__7 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__10 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__14 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__15 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__17 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214297__20 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214297__21 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_8458);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("an>Hello <div>there</div> <span>now</span></span>", o_unconsume_sd214297__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_sd214294 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214294_literalMutationString214415() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("oaT");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__3 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__4 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__6 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__9 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__13 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__14 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__16 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214294__19 = r.current();
        // StatementAdd: add invocation of a method
        r.advance();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_literalMutationString214292 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_literalMutationString214292_sd214395() {
        char __DSPOT_c_8468 = 'b';
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("otne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__3 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__4 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__6 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__9 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__13 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__14 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__16 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_literalMutationString214292__19 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_literalMutationString214292_sd214395__41 = // StatementAdd: add invocation of a method
        r.consumeTo(__DSPOT_c_8468);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_unconsume_literalMutationString214292_sd214395__41);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_sd214300 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214300_literalMutationString214505() {
        char[] __DSPOT_chars_8459 = new char[]{ 'd' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("o<ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__4 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__5 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__7 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__10 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__14 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__15 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__17 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__20 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214300__21 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_8459);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_unconsume_sd214300__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_sd214300 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214300_literalMutationString214502() {
        char[] __DSPOT_chars_8459 = new char[]{ 'd' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__4 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__5 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__7 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__10 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__14 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__15 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__17 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__20 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214300__21 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_8459);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214300__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume */
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_sd214300 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214300_literalMutationString214503() {
        char[] __DSPOT_chars_8459 = new char[]{ 'd' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__4 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__5 = r.current();
        r.unconsume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__7 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__9 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__10 = r.consume();
        r.isEmpty();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__14 = r.current();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__15 = r.consume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__17 = r.consume();
        r.unconsume();
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        char o_unconsume_sd214300__20 = r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214300__21 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_8459);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("an>Hello <", o_unconsume_sd214300__21);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('d', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }
}

