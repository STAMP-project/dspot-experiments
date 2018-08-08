package org.jsoup.parser;


import java.io.StringReader;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplCharacterReaderTest {
    public static final int maxBufferLen = CharacterReader.maxBufferLen;

    @Test(timeout = 10000)
    public void consume_mg4291_failAssert9() throws Exception {
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
            org.junit.Assert.fail("consume_mg4291 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -3", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unconsumelitString155472_failAssert74() throws Exception {
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
            org.junit.Assert.fail("unconsumelitString155472 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void mark_remove52811_mg53182_failAssert35() throws Exception {
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
            org.junit.Assert.fail("mark_remove52811_mg53182 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -2", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void advancelitString6_mg185_failAssert1() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            r.consume();
            r.advance();
            r.consume();
            r.toString();
            org.junit.Assert.fail("advancelitString6_mg185 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void rangeEqualslitString116596_failAssert66() throws Exception {
        try {
            CharacterReader r = new CharacterReader("blah blah");
            r.rangeEquals(0, 5, "Check");
            r.rangeEquals(0, 5, "CHOKE");
            r.rangeEquals(0, 5, "Chec");
            r.rangeEquals(6, 5, "Check");
            r.rangeEquals(6, 5, "Chuck");
            r.rangeEquals(12, 5, "Check");
            r.rangeEquals(12, 5, "Cheeky");
            r.rangeEquals(18, 5, "CHOKE");
            r.rangeEquals(18, 5, "CHIKE");
            org.junit.Assert.fail("rangeEqualslitString116596 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("12", expected.getMessage());
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
}

