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
        char[] scan = new char[]{ ' ' , '\n' , '\t' };
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_literalMutationString5 */
    @org.junit.Test(timeout = 10000)
    public void advance_literalMutationString5_sd109() {
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
        java.lang.String o_advance_literalMutationString5_sd109__10 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("scbCS@!x*z", o_advance_literalMutationString5_sd109__10);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('s', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#advance */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd8 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#advance_sd8_sd152 */
    @org.junit.Test(timeout = 10000)
    public void advance_sd8_sd152_sd2143() {
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
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_advance_sd8__6 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" Two Three", o_advance_sd8__6);
        // AssertGenerator create local variable with return value of invocation
        int o_advance_sd8_sd152__14 = // StatementAdd: add invocation of a method
        r.pos();
        // AssertGenerator create local variable with return value of invocation
        int o_advance_sd8_sd152_sd2143__18 = // StatementAdd: add invocation of a method
        r.pos();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (o_advance_sd8_sd152_sd2143__18)));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(' ', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(3, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(" Two Three", o_advance_sd8__6);
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
    public void consumeToAny_literalMutationString30494() {
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
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30494__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationString30494__5 = r.matches("&bar;");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30494__5);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30494__6 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30494__6)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30494__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30494__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationString30494__8 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30494__8)));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationString30494__9 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30494__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationString30494__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30494__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("Hi4$b,|`z!^. ", o_consumeToAny_literalMutationString30494__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(15, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30494__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_consumeToAny_literalMutationString30494__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (o_consumeToAny_literalMutationString30494__8)));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_sd30546 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_sd30546_literalMutationChar34184() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30546__3 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30546__4 = r.matches('&');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_sd30546__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30546__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30546__7 = r.consumeToAny('&', ':');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar; qux", o_consumeToAny_sd30546__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_sd30546__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_sd30546__9 = r.consumeToAny('&', ';');
        // StatementAdd: add invocation of a method
        r.isEmpty();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("bar; qux", o_consumeToAny_sd30546__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One ", o_consumeToAny_sd30546__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_sd30546__9);
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_sd30547 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_sd30547_literalMutationString34211_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_2094 = 'S';
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_sd30547__4 = r.consumeToAny('&', ';');
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_sd30547__5 = r.matches('&');
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_sd30547__6 = r.matches("&bar;");
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_sd30547__7 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_sd30547__8 = r.consumeToAny('&', ';');
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_sd30547__9 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_sd30547__10 = r.consumeToAny('&', ';');
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_sd30547__11 = // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_2094);
            org.junit.Assert.fail("consumeToAny_sd30547_literalMutationString34211 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30537 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30537_literalMutationString33596 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30537_literalMutationString33596_literalMutationChar45525() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("<span>Hello <div>there</div> <span>now</span></span>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('<', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30537__3 = r.consumeToAny('&', ';');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30537__4 = r.matches('\n');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30537__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30537__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30537__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30537__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30537__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30537__9 = r.consumeToAny('\n', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30537__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30537__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<span>Hello <div>there</div> <span>now</span></span>", o_consumeToAny_literalMutationChar30537__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(54, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30513 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30513_literalMutationChar32186 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30513_literalMutationChar32186_literalMutationString43925() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30513__3 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30513__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30513__4 = r.matches('\n');
        // AssertGenerator create local variable with return value of invocation
        boolean o_consumeToAny_literalMutationChar30513__5 = r.matches("&bar;");
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30513__6 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30513__7 = r.consumeToAny('&', ';');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30513__7);
        // AssertGenerator create local variable with return value of invocation
        char o_consumeToAny_literalMutationChar30513__8 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToAny_literalMutationChar30513__9 = r.consumeToAny('&', ':');
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30513__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30513__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToAny_literalMutationChar30513__7);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30532 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToAny_literalMutationChar30532_literalMutationChar33329 */
    @org.junit.Test(timeout = 10000)
    public void consumeToAny_literalMutationChar30532_literalMutationChar33329_sd38424_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One &bar; qux");
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_literalMutationChar30532__3 = r.consumeToAny('&', ';');
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_literalMutationChar30532__4 = r.matches('&');
            // AssertGenerator create local variable with return value of invocation
            boolean o_consumeToAny_literalMutationChar30532__5 = r.matches("&bar;");
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_literalMutationChar30532__6 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_literalMutationChar30532__7 = r.consumeToAny('&', '<');
            // AssertGenerator create local variable with return value of invocation
            char o_consumeToAny_literalMutationChar30532__8 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToAny_literalMutationChar30532__9 = r.consumeToAny('\u0000', ';');
            // StatementAdd: add invocation of a method
            r.toString();
            org.junit.Assert.fail("consumeToAny_literalMutationChar30532_literalMutationChar33329_sd38424 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_literalMutationString58265 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_literalMutationString58265_sd58381() {
        java.lang.String in = "one tw o three";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one tw o three", in);
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('o', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        java.lang.String toEnd = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one tw o three", toEnd);
        r.isEmpty();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_literalMutationString58265_sd58381__7 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_literalMutationString58265_sd58381__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one tw o three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one tw o three", toEnd);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(14, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58269 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58269_sd58438 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58269_sd58438_sd60860_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            char __DSPOT_c_3370 = 'u';
            char __DSPOT_c_3094 = '.';
            java.lang.String in = "one two three";
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader(in);
            java.lang.String toEnd = r.consumeToEnd();
            r.isEmpty();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_consumeToEnd_sd58269__8 = // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_3094);
            // StatementAdd: add invocation of a method
            r.advance();
            // StatementAdd: add invocation of a method
            r.consumeTo(__DSPOT_c_3370);
            org.junit.Assert.fail("consumeToEnd_sd58269_sd58438_sd60860 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58268 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#consumeToEnd_sd58268_sd58423 */
    @org.junit.Test(timeout = 10000)
    public void consumeToEnd_sd58268_sd58423_sd60582() {
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
        char o_consumeToEnd_sd58268_sd58423__9 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_consumeToEnd_sd58268_sd58423_sd60582__13 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_consumeToEnd_sd58268_sd58423_sd60582__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", in);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("one two three", toEnd);
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79431 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79431_literalMutationString79645() {
        char[] __DSPOT_chars_4405 = new char[]{ '1' , 'c' , '[' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_literalMutationString79424 */
    @org.junit.Test(timeout = 10000)
    public void mark_literalMutationString79424_sd79537() {
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
        java.lang.String o_mark_literalMutationString79424_sd79537__18 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("e", o_mark_literalMutationString79424_sd79537__18);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('e', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79431 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#mark_sd79431_literalMutationChar79639 */
    @org.junit.Test(timeout = 10000)
    public void mark_sd79431_literalMutationChar79639_literalMutationString82793() {
        char[] __DSPOT_chars_4405 = new char[]{ '1' , 'c' , '\u0000' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84053 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84053_literalMutationString85474_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84053__3 = r.matches('O');
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84053__4 = r.matches("One Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84053__5 = r.matches("Oe");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84053__6 = r.matches("one");
            // AssertGenerator create local variable with return value of invocation
            char o_matches_literalMutationString84053__7 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84053__8 = r.matches("One");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84053__9 = r.matches("ne Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84053__10 = r.matches("ne Two Three Four");
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_matches_literalMutationString84053__11 = r.consumeToEnd();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84053__12 = r.matches("ne");
            org.junit.Assert.fail("matches_literalMutationString84053_literalMutationString85474 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_sd84083 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_sd84083_literalMutationString87198 */
    @org.junit.Test(timeout = 10000)
    public void matches_sd84083_literalMutationString87198_literalMutationString98380() {
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
        boolean o_matches_sd84083__6 = r.matches("hne");
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
        // AssertGenerator create local variable with return value of invocation
        boolean o_matches_sd84083__12 = r.matches("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matches_sd84083__13 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matches_sd84083__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(52, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("span>Hello <div>there</div> <span>now</span></span>", o_matches_sd84083__11);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matches */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84080 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matches_literalMutationString84080_literalMutationString87019 */
    @org.junit.Test(timeout = 10000)
    public void matches_literalMutationString84080_literalMutationString87019_failAssert1_literalMutationString99675() {
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
            boolean o_matches_literalMutationString84080__3 = r.matches('O');
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__4 = r.matches("One Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matches_literalMutationString84080__5 = r.matches("Oe");
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationString100438 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationString100438_literalMutationChar101047() {
        char[] scan = new char[]{ ' ' , '\n' , ' ' };
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
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100438__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_literalMutationString100438__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_literalMutationString100438__8 = r.matchesAny(scan);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_literalMutationString100438__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100445 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100445_literalMutationChar101248() {
        char[] scan = new char[]{ '\u0000' , '\n' , '\t' };
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
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100445__5);
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
        org.junit.Assert.assertEquals("Two\tThree", o_matchesAny_sd100445__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_sd100445__5);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100448 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_sd100448_literalMutationChar101350 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_sd100448_literalMutationChar101350_literalMutationString104501() {
        char[] scan = new char[]{ '!' , '\n' , '\t' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100448__4 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesAny_sd100448__5 = r.consumeToAny(scan);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100448__6 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100448__7 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesAny_sd100448__8 = r.matchesAny(scan);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesAny_sd100448__9 = // StatementAdd: add invocation of a method
        r.current();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesAny_sd100448__5);
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100435 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesAny_literalMutationChar100435_sd100997 */
    @org.junit.Test(timeout = 10000)
    public void matchesAny_literalMutationChar100435_sd100997_literalMutationChar104940() {
        char[] scan = new char[]{ 'D' , '\n' , '\n' };
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
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('T', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("One", o_matchesAny_literalMutationChar100435__5);
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
    public void matchesIgnoreCase_literalMutationString109209() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__3 = r.matchesIgnoreCase("O");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__4 = r.matchesIgnoreCase("o");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__5 = r.matches('O');
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__6 = r.matches('o');
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109209__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__8);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__9 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__9);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__10 = r.matchesIgnoreCase(">ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109209__10);
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109209__11 = r.consume();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_literalMutationString109209__11)));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__12 = r.matchesIgnoreCase("One");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109209__12);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__13);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__14 = r.matchesIgnoreCase("ne Two Three Four");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109209__14);
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109209__15 = r.consumeToEnd();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109209__15);
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109209__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109209__16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__8);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__13);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (o_matchesIgnoreCase_literalMutationString109209__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__5);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__4);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__9);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_matchesIgnoreCase_literalMutationString109209__7);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109209__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109209__12);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109209__14);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109209__15);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_matchesIgnoreCase_literalMutationString109209__10);
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109229 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109229_literalMutationChar114249 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109229_literalMutationChar114249_sd130680() {
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("One Two Three");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('O', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__3 = r.matchesIgnoreCase("O");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__4 = r.matchesIgnoreCase("o");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__5 = r.matches('\n');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__6 = r.matches('o');
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__7 = r.matchesIgnoreCase("One Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__8 = r.matchesIgnoreCase("ONE two THREE");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__9 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__10 = r.matchesIgnoreCase("one");
        // AssertGenerator create local variable with return value of invocation
        char o_matchesIgnoreCase_literalMutationString109229__11 = r.consume();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__12 = r.matchesIgnoreCase("One");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__13 = r.matchesIgnoreCase("NE Two Three");
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__14 = r.matchesIgnoreCase("ne Two Three For");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109229__15 = r.consumeToEnd();
        // AssertGenerator create local variable with return value of invocation
        boolean o_matchesIgnoreCase_literalMutationString109229__16 = r.matchesIgnoreCase("ne");
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_matchesIgnoreCase_literalMutationString109229_literalMutationChar114249_sd130680__45 = // StatementAdd: add invocation of a method
        r.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_matchesIgnoreCase_literalMutationString109229_literalMutationChar114249_sd130680__45);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(13, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ne Two Three", o_matchesIgnoreCase_literalMutationString109229__15);
    }

    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109205 */
    /* amplification of org.jsoup.parser.CharacterReaderTest#matchesIgnoreCase_literalMutationString109205_literalMutationString112460 */
    @org.junit.Test(timeout = 10000)
    public void matchesIgnoreCase_literalMutationString109205_literalMutationString112460_literalMutationString125518_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__3 = r.matchesIgnoreCase("O");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__4 = r.matchesIgnoreCase("o");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__5 = r.matches('O');
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__6 = r.matches('o');
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__7 = r.matchesIgnoreCase("One Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__8 = r.matchesIgnoreCase("ONL two THREE");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__9 = r.matchesIgnoreCase("Ovne");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__10 = r.matchesIgnoreCase("one");
            // AssertGenerator create local variable with return value of invocation
            char o_matchesIgnoreCase_literalMutationString109205__11 = r.consume();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__12 = r.matchesIgnoreCase("One");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__13 = r.matchesIgnoreCase("NE Two Three");
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__14 = r.matchesIgnoreCase("ne Two Three Four");
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_matchesIgnoreCase_literalMutationString109205__15 = r.consumeToEnd();
            // AssertGenerator create local variable with return value of invocation
            boolean o_matchesIgnoreCase_literalMutationString109205__16 = r.matchesIgnoreCase("ne");
            org.junit.Assert.fail("matchesIgnoreCase_literalMutationString109205_literalMutationString112460_literalMutationString125518 should have thrown StringIndexOutOfBoundsException");
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
    /* amplification of org.jsoup.parser.CharacterReaderTest#unconsume_sd214294 */
    @org.junit.Test(timeout = 10000)
    public void unconsume_sd214294_sd214424() {
        char[] __DSPOT_chars_8473 = new char[]{ '1' , 'V' , 's' , '@' };
        org.jsoup.parser.CharacterReader r = new org.jsoup.parser.CharacterReader("one");
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
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_unconsume_sd214294_sd214424__43 = // StatementAdd: add invocation of a method
        r.consumeToAny(__DSPOT_chars_8473);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", o_unconsume_sd214294_sd214424__43);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsoup.parser.CharacterReader)r).isEmpty());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals('\uffff', ((char) (((org.jsoup.parser.CharacterReader)r).current())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(4, ((int) (((org.jsoup.parser.CharacterReader)r).pos())));
    }
}

