package org.jsoup.parser;


import org.junit.Assert;
import org.junit.Test;


public class AmplCharacterReaderTest {
    @Test(timeout = 10000)
    public void consume_mg5207_failAssert48() throws Exception {
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
            org.junit.Assert.fail("consume_mg5207 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToEndnull25179_failAssert104() throws Exception {
        try {
            String in = null;
            CharacterReader r = new CharacterReader(in);
            String toEnd = r.consumeToEnd();
            r.isEmpty();
            org.junit.Assert.fail("consumeToEndnull25179 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitString69023_failAssert204() throws Exception {
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
            org.junit.Assert.fail("nextIndexOfCharlitString69023 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharnull69081_failAssert212() throws Exception {
        try {
            String in = null;
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf('x');
            r.nextIndexOf('h');
            String pull = r.consumeTo('h');
            r.consume();
            r.nextIndexOf('l');
            r.consumeToEnd();
            r.nextIndexOf('x');
            r.nextIndexOf('x');
            org.junit.Assert.fail("nextIndexOfCharnull69081 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitChar69052_failAssert210() throws Exception {
        try {
            String in = "blah blah";
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf('x');
            r.nextIndexOf('h');
            String pull = r.consumeTo('g');
            r.consume();
            r.nextIndexOf('l');
            r.consumeToEnd();
            r.nextIndexOf('x');
            r.nextIndexOf('x');
            org.junit.Assert.fail("nextIndexOfCharlitChar69052 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharnull69113_failAssert218litString69415_failAssert221() throws Exception {
        try {
            try {
                String in = "";
                CharacterReader r = new CharacterReader(in);
                r.nextIndexOf('x');
                r.nextIndexOf('h');
                String pull = r.consumeTo('h');
                r.consume();
                r.nextIndexOf('l');
                r.consumeToEnd();
                r.nextIndexOf(null);
                org.junit.Assert.fail("nextIndexOfCharnull69113 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("nextIndexOfCharnull69113_failAssert218litString69415 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: -1", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharnull69113_failAssert218null70282_failAssert219() throws Exception {
        try {
            try {
                String in = null;
                CharacterReader r = new CharacterReader(in);
                r.nextIndexOf('x');
                r.nextIndexOf('h');
                String pull = r.consumeTo('h');
                r.consume();
                r.nextIndexOf('l');
                r.consumeToEnd();
                r.nextIndexOf(null);
                org.junit.Assert.fail("nextIndexOfCharnull69113 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("nextIndexOfCharnull69113_failAssert218null70282 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharnull69113_failAssert218litChar69778_failAssert225() throws Exception {
        try {
            try {
                String in = "blah blah";
                CharacterReader r = new CharacterReader(in);
                r.nextIndexOf('x');
                r.nextIndexOf('h');
                String pull = r.consumeTo('#');
                r.consume();
                r.nextIndexOf('l');
                r.consumeToEnd();
                r.nextIndexOf(null);
                org.junit.Assert.fail("nextIndexOfCharnull69113 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("nextIndexOfCharnull69113_failAssert218litChar69778 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: -1", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringnull76578_failAssert240() throws Exception {
        try {
            String in = "One Two something Two Three Four";
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf("Foo");
            r.nextIndexOf("Two");
            r.consumeTo("something");
            r.nextIndexOf(null);
            r.consumeToEnd();
            r.nextIndexOf("Two");
            org.junit.Assert.fail("nextIndexOfStringnull76578 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringnull76557_failAssert235() throws Exception {
        try {
            String in = null;
            CharacterReader r = new CharacterReader(in);
            r.nextIndexOf("Foo");
            r.nextIndexOf("Two");
            r.consumeTo("something");
            r.nextIndexOf("Two");
            r.consumeToEnd();
            r.nextIndexOf("Two");
            org.junit.Assert.fail("nextIndexOfStringnull76557 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Object must not be null", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringnull76573_failAssert239null77360_failAssert243() throws Exception {
        try {
            try {
                String in = null;
                CharacterReader r = new CharacterReader(in);
                r.nextIndexOf("Foo");
                r.nextIndexOf("Two");
                r.consumeTo(null);
                r.nextIndexOf("Two");
                r.consumeToEnd();
                r.nextIndexOf("Two");
                org.junit.Assert.fail("nextIndexOfStringnull76573 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("nextIndexOfStringnull76573_failAssert239null77360 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected_1) {
            Assert.assertEquals("Object must not be null", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToCharlitString22060_failAssert70() throws Exception {
        try {
            CharacterReader r = new CharacterReader("le}z;wuWR@,|:");
            r.consumeTo('T');
            r.consumeTo('T');
            r.consume();
            r.consumeTo('T');
            r.consume();
            r.consume();
            r.consumeTo('T');
            org.junit.Assert.fail("consumeToCharlitString22060 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToCharlitChar22073_failAssert81() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Three");
            r.consumeTo('T');
            r.consumeTo('U');
            r.consume();
            r.consumeTo('T');
            r.consume();
            r.consume();
            r.consumeTo('T');
            org.junit.Assert.fail("consumeToCharlitChar22073 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToCharnull22129_failAssert93litString22417_failAssert103() throws Exception {
        try {
            try {
                CharacterReader r = new CharacterReader("N(h9M8y-pP_2,");
                r.consumeTo('T');
                r.consumeTo('T');
                r.consume();
                r.consumeTo('T');
                r.consume();
                r.consume();
                r.consumeTo(null);
                org.junit.Assert.fail("consumeToCharnull22129 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("consumeToCharnull22129_failAssert93litString22417 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: -1", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToCharnull22129_failAssert93litString22427_failAssert96() throws Exception {
        try {
            try {
                CharacterReader r = new CharacterReader("\n");
                r.consumeTo('T');
                r.consumeTo('T');
                r.consume();
                r.consumeTo('T');
                r.consume();
                r.consume();
                r.consumeTo(null);
                org.junit.Assert.fail("consumeToCharnull22129 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("consumeToCharnull22129_failAssert93litString22427 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: -1", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString26490_failAssert115() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToStringlitString26490 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString26499_failAssert124() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Two Four");
            r.consumeTo("\n");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToStringlitString26499 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString26491_failAssert116() throws Exception {
        try {
            CharacterReader r = new CharacterReader("\n");
            r.consumeTo("Two");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToStringlitString26491 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToAnynull8994_failAssert67() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One &bar; qux");
            r.consumeToAny('&', ';');
            r.matches('&');
            r.matches(null);
            r.consume();
            r.consumeToAny('&', ';');
            r.consume();
            r.consumeToAny('&', ';');
            org.junit.Assert.fail("consumeToAnynull8994 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencenull6517_failAssert55() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One &bar; qux");
            r.consumeLetterSequence();
            r.consumeTo(null);
            r.consumeLetterSequence();
            r.consumeToEnd();
            org.junit.Assert.fail("consumeLetterSequencenull6517 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString8568_failAssert62() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            r.consumeLetterThenDigitSequence();
            r.consume();
            r.consumeLetterThenDigitSequence();
            r.consumeToEnd();
            org.junit.Assert.fail("consumeLetterThenDigitSequencelitString8568 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matcheslitString36559_failAssert154() throws Exception {
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
            org.junit.Assert.fail("matcheslitString36559 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matchesnull36678_failAssert163() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Three");
            r.matches('O');
            r.matches("One Two Three");
            r.matches("One");
            r.matches("one");
            r.consume();
            r.matches("One");
            r.matches("ne Two Three");
            r.matches("ne Two Three Four");
            r.consumeToEnd();
            r.matches(null);
            org.junit.Assert.fail("matchesnull36678 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matcheslitString36559_failAssert154null37625_failAssert167() throws Exception {
        try {
            try {
                CharacterReader r = new CharacterReader("");
                r.matches('O');
                r.matches("One Two Three");
                r.matches("One");
                r.matches(null);
                r.consume();
                r.matches("One");
                r.matches("ne Two Three");
                r.matches("ne Two Three Four");
                r.consumeToEnd();
                r.matches("ne");
                org.junit.Assert.fail("matcheslitString36559 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("matcheslitString36559_failAssert154null37625 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCasenull48852_failAssert194() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Three");
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
            r.matchesIgnoreCase(null);
            org.junit.Assert.fail("matchesIgnoreCasenull48852 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString48689_failAssert181() throws Exception {
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
            org.junit.Assert.fail("matchesIgnoreCaselitString48689 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString48689_failAssert181_mg50629null66723_failAssert200() throws Exception {
        try {
            try {
                char[] __DSPOT_chars_1836 = new char[]{ 'R' };
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
                r.matchesIgnoreCase(null);
                r.matchesIgnoreCase("NE Two Three");
                r.matchesIgnoreCase("ne Two Three Four");
                r.consumeToEnd();
                r.matchesIgnoreCase("ne");
                org.junit.Assert.fail("matchesIgnoreCaselitString48689 should have thrown StringIndexOutOfBoundsException");
                r.consumeToAny(__DSPOT_chars_1836);
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("matchesIgnoreCaselitString48689_failAssert181_mg50629null66723 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matchesAnynull39265_failAssert173() throws Exception {
        try {
            char[] scan = new char[]{ ' ', '\n', '\t' };
            CharacterReader r = new CharacterReader("One\nTwo\tThree");
            r.matchesAny(scan);
            r.consumeToAny(scan);
            r.matchesAny(scan);
            r.consume();
            r.matchesAny(null);
            org.junit.Assert.fail("matchesAnynull39265 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matchesAnynull39253_failAssert171() throws Exception {
        try {
            char[] scan = new char[]{ ' ', '\n', '\t' };
            CharacterReader r = new CharacterReader("One\nTwo\tThree");
            r.matchesAny(scan);
            r.consumeToAny(null);
            r.matchesAny(scan);
            r.consume();
            r.matchesAny(scan);
            r.matchesAny(scan);
            org.junit.Assert.fail("matchesAnynull39253 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cachesStringslitString872_failAssert3() throws Exception {
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
            org.junit.Assert.fail("cachesStringslitString872 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cachesStringslitChar884_failAssert15() throws Exception {
        try {
            CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
            String one = r.consumeTo('\t');
            r.consume();
            String two = r.consumeTo('\b');
            r.consume();
            String three = r.consumeTo('\t');
            r.consume();
            String four = r.consumeTo('\t');
            r.consume();
            String five = r.consumeTo('\t');
            boolean boolean_72 = one == two;
            boolean boolean_73 = two == three;
            boolean boolean_74 = three != four;
            boolean boolean_75 = four != five;
            org.junit.Assert.fail("cachesStringslitChar884 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cachesStringsnull932_failAssert27litString1232_failAssert30() throws Exception {
        try {
            try {
                CharacterReader r = new CharacterReader("");
                String one = r.consumeTo('\t');
                r.consume();
                r.consume();
                r.consume();
                r.consume();
                String two = r.consumeTo('\t');
                String three = r.consumeTo(null);
                String four = r.consumeTo('\t');
                String five = r.consumeTo('\t');
                boolean boolean_264 = one == two;
                boolean boolean_265 = two == three;
                boolean boolean_266 = three != four;
                boolean boolean_267 = four != five;
                org.junit.Assert.fail("cachesStringsnull932 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("cachesStringsnull932_failAssert27litString1232 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: -4", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void cachesStringsnull935_failAssert28litString1298_failAssert38() throws Exception {
        try {
            try {
                CharacterReader r = new CharacterReader("\n");
                String one = r.consumeTo('\t');
                r.consume();
                r.consume();
                r.consume();
                r.consume();
                String two = r.consumeTo('\t');
                String three = r.consumeTo('\t');
                String four = r.consumeTo(null);
                String five = r.consumeTo('\t');
                boolean boolean_276 = one == two;
                boolean boolean_277 = two == three;
                boolean boolean_278 = three != four;
                boolean boolean_279 = four != five;
                org.junit.Assert.fail("cachesStringsnull935 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("cachesStringsnull935_failAssert28litString1298 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("String index out of range: -4", expected_1.getMessage());
        }
    }
}

