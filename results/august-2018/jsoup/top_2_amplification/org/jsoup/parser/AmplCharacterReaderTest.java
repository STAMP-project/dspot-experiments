package org.jsoup.parser;


import java.io.StringReader;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplCharacterReaderTest {
    @Test(timeout = 10000)
    public void consume_mg7940_failAssert39() throws Exception {
        try {
            CharacterReader r = new CharacterReader("one");
            r.pos();
            r.current();
            r.consume();
            r.pos();
            r.current();
            r.pos();
            r.pos();
            r.consume();
            r.consume();
            r.isEmpty();
            r.consume();
            r.isEmpty();
            r.isEmpty();
            r.consume();
            r.consume();
            r.toString();
            Assert.fail("consume_mg7940 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consume_mg10880_failAssert6() throws Exception {
        try {
            CharacterReader r = new CharacterReader("one");
            r.pos();
            r.current();
            r.consume();
            r.pos();
            r.current();
            r.pos();
            r.pos();
            r.consume();
            r.consume();
            r.isEmpty();
            r.consume();
            r.isEmpty();
            r.isEmpty();
            r.consume();
            r.consume();
            r.toString();
            org.junit.Assert.fail("consume_mg10880 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unconsume_mg131296_failAssert86() throws Exception {
        try {
            CharacterReader r = new CharacterReader("one");
            r.consume();
            r.current();
            r.unconsume();
            r.unconsume();
            r.unconsume();
            r.current();
            r.consume();
            r.consume();
            r.consume();
            r.consume();
            r.isEmpty();
            r.isEmpty();
            r.current();
            r.consume();
            r.consume();
            r.isEmpty();
            r.isEmpty();
            r.consume();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.current();
            r.isEmpty();
            org.junit.Assert.fail("unconsume_mg131296 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unconsumelitString131269_failAssert79() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            r.consume();
            r.current();
            r.unconsume();
            r.current();
            r.consume();
            r.consume();
            r.consume();
            r.consume();
            r.isEmpty();
            r.unconsume();
            r.isEmpty();
            r.current();
            r.consume();
            r.consume();
            r.isEmpty();
            r.isEmpty();
            r.consume();
            r.unconsume();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.current();
            org.junit.Assert.fail("unconsumelitString131269 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void mark_remove32297_mg32487_failAssert124() throws Exception {
        try {
            CharacterReader r = new CharacterReader("one");
            r.consume();
            r.mark();
            r.consume();
            r.consume();
            r.isEmpty();
            r.consume();
            r.consume();
            r.toString();
            Assert.fail("mark_remove32297_mg32487 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToEnd_mg23259_mg23422_failAssert87() throws Exception {
        try {
            String in = "one two three";
            CharacterReader r = new CharacterReader(in);
            String toEnd = r.consumeToEnd();
            r.isEmpty();
            r.advance();
            r.toString();
            Assert.fail("consumeToEnd_mg23259_mg23422 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void mark_remove49413_mg49745_failAssert28() throws Exception {
        try {
            CharacterReader r = new CharacterReader("one");
            r.consume();
            r.mark();
            r.consume();
            r.consume();
            r.isEmpty();
            r.consume();
            r.consume();
            r.toString();
            org.junit.Assert.fail("mark_remove49413_mg49745 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToEnd_mg31041_mg31190_failAssert18() throws Exception {
        try {
            String in = "one two three";
            CharacterReader r = new CharacterReader(in);
            String toEnd = r.consumeToEnd();
            r.isEmpty();
            r.advance();
            r.toString();
            org.junit.Assert.fail("consumeToEnd_mg31041_mg31190 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitChar50001_failAssert148() throws Exception {
        try {
            String in = "blah blah";
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf('x');
            r.nextIndexOf('h');
            String pull = r.consumeTo('i');
            r.consume();
            r.nextIndexOf('l');
            r.consumeToEnd();
            r.nextIndexOf('x');
            r.nextIndexOf('x');
            Assert.fail("nextIndexOfCharlitChar50001 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString86891_failAssert44() throws Exception {
        try {
            String in = "One Two something Two Three Four";
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf("Foo");
            r.nextIndexOf("");
            r.consumeTo("something");
            r.nextIndexOf("Two");
            r.consumeToEnd();
            r.nextIndexOf("Two");
            org.junit.Assert.fail("nextIndexOfStringlitString86891 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString86899_failAssert45() throws Exception {
        try {
            String in = "One Two something Two Three Four";
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf("Foo");
            r.nextIndexOf("Two");
            r.consumeTo("");
            r.nextIndexOf("Two");
            r.consumeToEnd();
            r.nextIndexOf("Two");
            org.junit.Assert.fail("nextIndexOfStringlitString86899 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString86907_failAssert46() throws Exception {
        try {
            String in = "One Two something Two Three Four";
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf("Foo");
            r.nextIndexOf("Two");
            r.consumeTo("something");
            r.nextIndexOf("");
            r.consumeToEnd();
            r.nextIndexOf("Two");
            org.junit.Assert.fail("nextIndexOfStringlitString86907 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString86915_failAssert47() throws Exception {
        try {
            String in = "One Two something Two Three Four";
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf("Foo");
            r.nextIndexOf("Two");
            r.consumeTo("something");
            r.nextIndexOf("Two");
            r.consumeToEnd();
            r.nextIndexOf("");
            org.junit.Assert.fail("nextIndexOfStringlitString86915 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitString49973_failAssert143() throws Exception {
        try {
            String in = "";
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf('x');
            r.nextIndexOf('h');
            String pull = r.consumeTo('h');
            r.consume();
            r.nextIndexOf('l');
            r.consumeToEnd();
            r.nextIndexOf('x');
            r.nextIndexOf('x');
            Assert.fail("nextIndexOfCharlitString49973 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitString91426_failAssert50() throws Exception {
        try {
            CharacterReader r = new CharacterReader("<[[one]]");
            r.nextIndexOf("");
            org.junit.Assert.fail("nextIndexOfUnmatchedlitString91426 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString38424_failAssert20() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Two Four");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("");
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToStringlitString38424 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString38432_failAssert21() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Two Four");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consume();
            r.consumeTo("");
            org.junit.Assert.fail("consumeToStringlitString38432 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString57612_failAssert157() throws Exception {
        try {
            String in = "One Two something Two Three Four";
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf("Foo");
            r.nextIndexOf("Two");
            r.consumeTo("something");
            r.nextIndexOf("Two");
            r.consumeToEnd();
            r.nextIndexOf("");
            Assert.fail("nextIndexOfStringlitString57612 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void advance_mg19litString252_failAssert3() throws Exception {
        try {
            CharacterReader r = new CharacterReader(":");
            r.consume();
            r.advance();
            r.consume();
            r.toString();
            org.junit.Assert.fail("advance_mg19litString252 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void advancelitString6_mg176_failAssert0() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            r.consume();
            r.advance();
            r.consume();
            r.toString();
            org.junit.Assert.fail("advancelitString6_mg176 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToCharlitChar16887_failAssert65() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Three");
            r.consumeTo('T');
            r.consumeTo(':');
            r.consume();
            r.consumeTo('T');
            r.consume();
            r.consume();
            r.consumeTo('T');
            Assert.fail("consumeToCharlitChar16887 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString12130_failAssert7() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One &bar; qux");
            r.consumeLetterSequence();
            r.consumeTo("");
            r.consumeLetterSequence();
            r.consumeToEnd();
            org.junit.Assert.fail("consumeLetterSequencelitString12130 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToCharlitString16871_failAssert54() throws Exception {
        try {
            CharacterReader r = new CharacterReader("one");
            r.consumeTo('T');
            r.consumeTo('T');
            r.consume();
            r.consumeTo('T');
            r.consume();
            r.consume();
            r.consumeTo('T');
            Assert.fail("consumeToCharlitString16871 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString24311_failAssert91() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            Assert.fail("consumeToStringlitString24311 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString24312_failAssert92() throws Exception {
        try {
            CharacterReader r = new CharacterReader("\n");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            Assert.fail("consumeToStringlitString24312 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void containsIgnoreCaselitString40404_failAssert26() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One TWO three");
            r.containsIgnoreCase("two");
            r.containsIgnoreCase("");
            r.containsIgnoreCase("one");
            org.junit.Assert.fail("containsIgnoreCaselitString40404 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString24320_failAssert100() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Two Four");
            r.consumeTo("\n");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            Assert.fail("consumeToStringlitString24320 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void advancelitString8_mg236_failAssert2() throws Exception {
        try {
            CharacterReader r = new CharacterReader(":");
            r.consume();
            r.advance();
            r.consume();
            r.toString();
            Assert.fail("advancelitString8_mg236 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToAny_mg11241litString13195_failAssert45() throws Exception {
        try {
            char __DSPOT_c_700 = '.';
            CharacterReader r = new CharacterReader("one two three");
            r.consumeToAny('&', ';');
            r.matches('&');
            r.matches("&bar;");
            r.consume();
            r.consumeToAny('&', ';');
            r.consume();
            r.consumeToAny('&', ';');
            r.consumeTo(__DSPOT_c_700);
            Assert.fail("consumeToAny_mg11241litString13195 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void rangeEqualslitNum91978_failAssert60() throws Exception {
        try {
            CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE");
            r.rangeEquals(0, 5, "Check");
            r.rangeEquals(0, 5, "CHOKE");
            r.rangeEquals(0, 5, "Chec");
            r.rangeEquals(Integer.MAX_VALUE, 5, "Check");
            r.rangeEquals(6, 5, "Chuck");
            r.rangeEquals(12, 5, "Check");
            r.rangeEquals(12, 5, "Cheeky");
            r.rangeEquals(18, 5, "CHOKE");
            r.rangeEquals(18, 5, "CHIKE");
            org.junit.Assert.fail("rangeEqualslitNum91978 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("2147483647", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void rangeEqualslitString91860_failAssert58() throws Exception {
        try {
            CharacterReader r = new CharacterReader("]]>");
            r.rangeEquals(0, 5, "Check");
            r.rangeEquals(0, 5, "CHOKE");
            r.rangeEquals(0, 5, "Chec");
            r.rangeEquals(6, 5, "Check");
            r.rangeEquals(6, 5, "Chuck");
            r.rangeEquals(12, 5, "Check");
            r.rangeEquals(12, 5, "Cheeky");
            r.rangeEquals(18, 5, "CHOKE");
            r.rangeEquals(18, 5, "CHIKE");
            org.junit.Assert.fail("rangeEqualslitString91860 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("6", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToAny_mg11240_mg12923_failAssert42() throws Exception {
        try {
            char __DSPOT_c_746 = 'B';
            CharacterReader r = new CharacterReader("One &bar; qux");
            r.consumeToAny('&', ';');
            r.matches('&');
            r.matches("&bar;");
            r.consume();
            r.consumeToAny('&', ';');
            r.consume();
            r.consumeToAny('&', ';');
            r.advance();
            r.consumeTo(__DSPOT_c_746);
            Assert.fail("consumeToAny_mg11240_mg12923 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Ignore
    @Test
    public void notEmptyAtBufferSplitPoint() {
        CharacterReader r = new CharacterReader(new StringReader("How about now"), 3);
        Assert.assertEquals("How", r.consumeTo(' '));
        Assert.assertFalse("Should not be empty", r.isEmpty());
        Assert.assertEquals(' ', r.consume());
        Assert.assertFalse(r.isEmpty());

    }

    @Test(timeout = 10000)
    public void consumeToAnylitChar11220_mg12423_failAssert52() throws Exception {
        try {
            char __DSPOT_c_718 = 'L';
            CharacterReader r = new CharacterReader("One &bar; qux");
            r.consumeToAny('&', ';');
            r.matches('&');
            r.matches("&bar;");
            r.consume();
            r.consumeToAny('&', '\n');
            r.consume();
            r.consumeToAny('&', ';');
            r.consumeTo(__DSPOT_c_718);
            Assert.fail("consumeToAnylitChar11220_mg12423 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString10008_failAssert40() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One &bar; qux");
            r.consumeLetterSequence();
            r.consumeTo("");
            r.consumeLetterSequence();
            r.consumeToEnd();
            Assert.fail("consumeLetterSequencelitString10008 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString10606_failAssert41() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            r.consumeLetterThenDigitSequence();
            r.consume();
            r.consumeLetterThenDigitSequence();
            r.consumeToEnd();
            Assert.fail("consumeLetterThenDigitSequencelitString10606 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matcheslitString34178_failAssert135() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
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
            Assert.fail("matcheslitString34178 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString41565_failAssert141() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
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
            Assert.fail("matchesIgnoreCaselitString41565 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matchesAnylitString39100_mg40085_failAssert139() throws Exception {
        try {
            char __DSPOT_c_2206 = 'k';
            char[] scan = new char[]{ ' ', '\n', '\t' };
            CharacterReader r = new CharacterReader("+[)]nyh5P1h]+");
            r.matchesAny(scan);
            r.consumeToAny(scan);
            r.matchesAny(scan);
            r.consume();
            r.matchesAny(scan);
            r.matchesAny(scan);
            r.consumeTo(__DSPOT_c_2206);
            Assert.fail("matchesAnylitString39100_mg40085 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cachesStringslitString1202_failAssert8() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            String one = r.consumeTo('\t');
            r.consume();
            String two = r.consumeTo('\t');
            r.consume();
            String three = r.consumeTo('\t');
            r.consume();
            String four = r.consumeTo('\t');
            r.consume();
            String five = r.consumeTo('\t');
            boolean boolean_24 = one == two;
            boolean boolean_25 = two == three;
            boolean boolean_26 = three != four;
            boolean boolean_27 = four != five;
            Assert.fail("cachesStringslitString1202 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cachesStringslitChar1213_failAssert19() throws Exception {
        try {
            CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
            String one = r.consumeTo('\t');
            r.consume();
            String two = r.consumeTo('\n');
            r.consume();
            String three = r.consumeTo('\t');
            r.consume();
            String four = r.consumeTo('\t');
            r.consume();
            String five = r.consumeTo('\t');
            boolean boolean_68 = one == two;
            boolean boolean_69 = two == three;
            boolean boolean_70 = three != four;
            boolean boolean_71 = four != five;
            Assert.fail("cachesStringslitChar1213 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }
}

