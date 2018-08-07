package org.jsoup.parser;


import org.junit.Assert;
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
            org.junit.Assert.fail("consume_mg7940 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -3", expected.getMessage());
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
            org.junit.Assert.fail("mark_remove32297_mg32487 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("consumeToEnd_mg23259_mg23422 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("nextIndexOfCharlitChar50001 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
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
            org.junit.Assert.fail("nextIndexOfCharlitString49973 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
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
            org.junit.Assert.fail("nextIndexOfStringlitString57612 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: 0", expected.getMessage());
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
            org.junit.Assert.fail("consumeToCharlitChar16887 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
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
            org.junit.Assert.fail("consumeToCharlitString16871 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("consumeToStringlitString24311 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("consumeToStringlitString24312 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
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
            org.junit.Assert.fail("consumeToStringlitString24320 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("advancelitString8_mg236 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("consumeToAny_mg11241litString13195 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -2", expected.getMessage());
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
            org.junit.Assert.fail("consumeToAny_mg11240_mg12923 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
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
            org.junit.Assert.fail("consumeToAnylitChar11220_mg12423 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("consumeLetterSequencelitString10008 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("consumeLetterThenDigitSequencelitString10606 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("matcheslitString34178 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("matchesIgnoreCaselitString41565 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("matchesAnylitString39100_mg40085 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("cachesStringslitString1202 should have thrown StringIndexOutOfBoundsException");
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
            org.junit.Assert.fail("cachesStringslitChar1213 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }
}

