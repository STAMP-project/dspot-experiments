package org.jsoup.parser;


import java.io.StringReader;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class AmplCharacterReaderTest {
    public static final int maxBufferLen = CharacterReader.maxBufferLen;

    @Test(timeout = 10000)
    public void consumelitString17768() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_consumelitString17768__3 = r.pos();
        Assert.assertEquals(0, ((int) (o_consumelitString17768__3)));
        char o_consumelitString17768__4 = r.current();
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__4)));
        char o_consumelitString17768__5 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__5)));
        int o_consumelitString17768__6 = r.pos();
        Assert.assertEquals(1, ((int) (o_consumelitString17768__6)));
        char o_consumelitString17768__7 = r.current();
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__7)));
        int o_consumelitString17768__8 = r.pos();
        Assert.assertEquals(1, ((int) (o_consumelitString17768__8)));
        int o_consumelitString17768__9 = r.pos();
        Assert.assertEquals(1, ((int) (o_consumelitString17768__9)));
        char o_consumelitString17768__10 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__10)));
        char o_consumelitString17768__11 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__11)));
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_consumelitString17768__13 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__13)));
        char o_consumelitString17768__16 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__16)));
        char o_consumelitString17768__17 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__17)));
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(0, ((int) (o_consumelitString17768__3)));
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__4)));
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__5)));
        Assert.assertEquals(1, ((int) (o_consumelitString17768__6)));
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__7)));
        Assert.assertEquals(1, ((int) (o_consumelitString17768__8)));
        Assert.assertEquals(1, ((int) (o_consumelitString17768__9)));
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__10)));
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__11)));
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__13)));
        Assert.assertEquals('\uffff', ((char) (o_consumelitString17768__16)));
    }

    @Test(timeout = 10000)
    public void consume_add17776() throws Exception {
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_consume_add17776__3 = r.pos();
        Assert.assertEquals(0, ((int) (o_consume_add17776__3)));
        char o_consume_add17776__4 = r.current();
        Assert.assertEquals('o', ((char) (o_consume_add17776__4)));
        char o_consume_add17776__5 = r.consume();
        Assert.assertEquals('o', ((char) (o_consume_add17776__5)));
        int o_consume_add17776__6 = r.pos();
        Assert.assertEquals(1, ((int) (o_consume_add17776__6)));
        char o_consume_add17776__7 = r.current();
        Assert.assertEquals('n', ((char) (o_consume_add17776__7)));
        int o_consume_add17776__8 = r.pos();
        Assert.assertEquals(1, ((int) (o_consume_add17776__8)));
        int o_consume_add17776__9 = r.pos();
        Assert.assertEquals(1, ((int) (o_consume_add17776__9)));
        int o_consume_add17776__10 = r.pos();
        Assert.assertEquals(1, ((int) (o_consume_add17776__10)));
        char o_consume_add17776__11 = r.consume();
        Assert.assertEquals('n', ((char) (o_consume_add17776__11)));
        char o_consume_add17776__12 = r.consume();
        Assert.assertEquals('e', ((char) (o_consume_add17776__12)));
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_consume_add17776__14 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consume_add17776__14)));
        char o_consume_add17776__17 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consume_add17776__17)));
        char o_consume_add17776__18 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consume_add17776__18)));
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(0, ((int) (o_consume_add17776__3)));
        Assert.assertEquals('o', ((char) (o_consume_add17776__4)));
        Assert.assertEquals('o', ((char) (o_consume_add17776__5)));
        Assert.assertEquals(1, ((int) (o_consume_add17776__6)));
        Assert.assertEquals('n', ((char) (o_consume_add17776__7)));
        Assert.assertEquals(1, ((int) (o_consume_add17776__8)));
        Assert.assertEquals(1, ((int) (o_consume_add17776__9)));
        Assert.assertEquals(1, ((int) (o_consume_add17776__10)));
        Assert.assertEquals('n', ((char) (o_consume_add17776__11)));
        Assert.assertEquals('e', ((char) (o_consume_add17776__12)));
        Assert.assertEquals('\uffff', ((char) (o_consume_add17776__14)));
        Assert.assertEquals('\uffff', ((char) (o_consume_add17776__17)));
    }

    @Test(timeout = 10000)
    public void consume_mg17784() throws Exception {
        char __DSPOT_c_940 = 'r';
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_consume_mg17784__4 = r.pos();
        Assert.assertEquals(0, ((int) (o_consume_mg17784__4)));
        char o_consume_mg17784__5 = r.current();
        Assert.assertEquals('o', ((char) (o_consume_mg17784__5)));
        char o_consume_mg17784__6 = r.consume();
        Assert.assertEquals('o', ((char) (o_consume_mg17784__6)));
        int o_consume_mg17784__7 = r.pos();
        Assert.assertEquals(1, ((int) (o_consume_mg17784__7)));
        char o_consume_mg17784__8 = r.current();
        Assert.assertEquals('n', ((char) (o_consume_mg17784__8)));
        int o_consume_mg17784__9 = r.pos();
        Assert.assertEquals(1, ((int) (o_consume_mg17784__9)));
        int o_consume_mg17784__10 = r.pos();
        Assert.assertEquals(1, ((int) (o_consume_mg17784__10)));
        char o_consume_mg17784__11 = r.consume();
        Assert.assertEquals('n', ((char) (o_consume_mg17784__11)));
        char o_consume_mg17784__12 = r.consume();
        Assert.assertEquals('e', ((char) (o_consume_mg17784__12)));
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_consume_mg17784__14 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consume_mg17784__14)));
        char o_consume_mg17784__17 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consume_mg17784__17)));
        char o_consume_mg17784__18 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consume_mg17784__18)));
        String o_consume_mg17784__19 = r.consumeTo(__DSPOT_c_940);
        Assert.assertEquals("", o_consume_mg17784__19);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(0, ((int) (o_consume_mg17784__4)));
        Assert.assertEquals('o', ((char) (o_consume_mg17784__5)));
        Assert.assertEquals('o', ((char) (o_consume_mg17784__6)));
        Assert.assertEquals(1, ((int) (o_consume_mg17784__7)));
        Assert.assertEquals('n', ((char) (o_consume_mg17784__8)));
        Assert.assertEquals(1, ((int) (o_consume_mg17784__9)));
        Assert.assertEquals(1, ((int) (o_consume_mg17784__10)));
        Assert.assertEquals('n', ((char) (o_consume_mg17784__11)));
        Assert.assertEquals('e', ((char) (o_consume_mg17784__12)));
        Assert.assertEquals('\uffff', ((char) (o_consume_mg17784__14)));
        Assert.assertEquals('\uffff', ((char) (o_consume_mg17784__17)));
        Assert.assertEquals('\uffff', ((char) (o_consume_mg17784__18)));
    }

    @Test(timeout = 10000)
    public void consume_add17776_mg18606() throws Exception {
        char __DSPOT_c_962 = '|';
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_consume_add17776__3 = r.pos();
        char o_consume_add17776__4 = r.current();
        char o_consume_add17776__5 = r.consume();
        int o_consume_add17776__6 = r.pos();
        char o_consume_add17776__7 = r.current();
        int o_consume_add17776__8 = r.pos();
        int o_consume_add17776__9 = r.pos();
        int o_consume_add17776__10 = r.pos();
        char o_consume_add17776__11 = r.consume();
        char o_consume_add17776__12 = r.consume();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_consume_add17776__14 = r.consume();
        char o_consume_add17776__17 = r.consume();
        char o_consume_add17776__18 = r.consume();
        String o_consume_add17776_mg18606__46 = r.consumeTo(__DSPOT_c_962);
        Assert.assertEquals("", o_consume_add17776_mg18606__46);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consume_add17775litString17918() throws Exception {
        CharacterReader r = new CharacterReader("Two");
        Assert.assertEquals("Two", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_consume_add17775__3 = r.pos();
        char o_consume_add17775__4 = r.current();
        char o_consume_add17775__5 = r.consume();
        int o_consume_add17775__6 = r.pos();
        char o_consume_add17775__7 = r.current();
        char o_consume_add17775__8 = r.current();
        int o_consume_add17775__9 = r.pos();
        int o_consume_add17775__10 = r.pos();
        char o_consume_add17775__11 = r.consume();
        char o_consume_add17775__12 = r.consume();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_consume_add17775__14 = r.consume();
        char o_consume_add17775__17 = r.consume();
        char o_consume_add17775__18 = r.consume();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consume_mg17785litString17963() throws Exception {
        char[] __DSPOT_chars_941 = new char[]{ 'x' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_consume_mg17785__4 = r.pos();
        char o_consume_mg17785__5 = r.current();
        char o_consume_mg17785__6 = r.consume();
        int o_consume_mg17785__7 = r.pos();
        char o_consume_mg17785__8 = r.current();
        int o_consume_mg17785__9 = r.pos();
        int o_consume_mg17785__10 = r.pos();
        char o_consume_mg17785__11 = r.consume();
        char o_consume_mg17785__12 = r.consume();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_consume_mg17785__14 = r.consume();
        char o_consume_mg17785__17 = r.consume();
        char o_consume_mg17785__18 = r.consume();
        String o_consume_mg17785__19 = r.consumeToAny(__DSPOT_chars_941);
        Assert.assertEquals("", o_consume_mg17785__19);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consumelitString17768_add18344() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_consumelitString17768__3 = r.pos();
        char o_consumelitString17768__4 = r.current();
        char o_consumelitString17768__5 = r.consume();
        int o_consumelitString17768__6 = r.pos();
        char o_consumelitString17768__7 = r.current();
        int o_consumelitString17768__8 = r.pos();
        int o_consumelitString17768__9 = r.pos();
        char o_consumelitString17768__10 = r.consume();
        char o_consumelitString17768__11 = r.consume();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_consumelitString17768__13 = r.consume();
        char o_consumelitString17768__16 = r.consume();
        char o_consumelitString17768__17 = r.consume();
        ((CharacterReader) (r)).isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consume_mg17785_add18390litString19733() throws Exception {
        char[] __DSPOT_chars_941 = new char[]{ 'x' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_consume_mg17785__4 = r.pos();
        char o_consume_mg17785__5 = r.current();
        char o_consume_mg17785__6 = r.consume();
        int o_consume_mg17785__7 = r.pos();
        char o_consume_mg17785__8 = r.current();
        int o_consume_mg17785__9 = r.pos();
        int o_consume_mg17785__10 = r.pos();
        char o_consume_mg17785_add18390__25 = r.consume();
        char o_consume_mg17785__11 = r.consume();
        char o_consume_mg17785__12 = r.consume();
        r.isEmpty();
        char o_consume_mg17785__14 = r.consume();
        r.isEmpty();
        r.isEmpty();
        char o_consume_mg17785__17 = r.consume();
        char o_consume_mg17785__18 = r.consume();
        String o_consume_mg17785__19 = r.consumeToAny(__DSPOT_chars_941);
        Assert.assertEquals("", o_consume_mg17785__19);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consume_mg17785litString17963_mg25702() throws Exception {
        char[] __DSPOT_chars_941 = new char[]{ 'x' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_consume_mg17785__4 = r.pos();
        char o_consume_mg17785__5 = r.current();
        char o_consume_mg17785__6 = r.consume();
        int o_consume_mg17785__7 = r.pos();
        char o_consume_mg17785__8 = r.current();
        int o_consume_mg17785__9 = r.pos();
        int o_consume_mg17785__10 = r.pos();
        char o_consume_mg17785__11 = r.consume();
        char o_consume_mg17785__12 = r.consume();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_consume_mg17785__14 = r.consume();
        char o_consume_mg17785__17 = r.consume();
        char o_consume_mg17785__18 = r.consume();
        String o_consume_mg17785__19 = r.consumeToAny(__DSPOT_chars_941);
        Assert.assertEquals("", o_consume_mg17785__19);
        int o_consume_mg17785litString17963_mg25702__46 = r.pos();
        Assert.assertEquals(6, ((int) (o_consume_mg17785litString17963_mg25702__46)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consume_mg17785__19);
    }

    @Test(timeout = 10000)
    public void consume_mg17784_remove18550litChar20361() throws Exception {
        char __DSPOT_c_940 = '\n';
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_consume_mg17784__4 = r.pos();
        char o_consume_mg17784__5 = r.current();
        char o_consume_mg17784__6 = r.consume();
        int o_consume_mg17784__7 = r.pos();
        char o_consume_mg17784__8 = r.current();
        int o_consume_mg17784__9 = r.pos();
        int o_consume_mg17784__10 = r.pos();
        char o_consume_mg17784__11 = r.consume();
        char o_consume_mg17784__12 = r.consume();
        r.isEmpty();
        char o_consume_mg17784__14 = r.consume();
        r.isEmpty();
        char o_consume_mg17784__17 = r.consume();
        char o_consume_mg17784__18 = r.consume();
        String o_consume_mg17784__19 = r.consumeTo(__DSPOT_c_940);
        Assert.assertEquals("", o_consume_mg17784__19);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consume_mg17786_remove18544litString19853() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_consume_mg17786__3 = r.pos();
        char o_consume_mg17786__4 = r.current();
        char o_consume_mg17786__5 = r.consume();
        int o_consume_mg17786__6 = r.pos();
        char o_consume_mg17786__7 = r.current();
        int o_consume_mg17786__8 = r.pos();
        int o_consume_mg17786__9 = r.pos();
        char o_consume_mg17786__10 = r.consume();
        char o_consume_mg17786__11 = r.consume();
        r.isEmpty();
        r.isEmpty();
        char o_consume_mg17786__13 = r.consume();
        char o_consume_mg17786__16 = r.consume();
        char o_consume_mg17786__17 = r.consume();
        char o_consume_mg17786__18 = r.current();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consumelitString17768_add18344_add22211() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).toString();
        int o_consumelitString17768__3 = r.pos();
        char o_consumelitString17768__4 = r.current();
        char o_consumelitString17768__5 = r.consume();
        int o_consumelitString17768__6 = r.pos();
        char o_consumelitString17768__7 = r.current();
        int o_consumelitString17768__8 = r.pos();
        int o_consumelitString17768__9 = r.pos();
        char o_consumelitString17768__10 = r.consume();
        char o_consumelitString17768__11 = r.consume();
        r.isEmpty();
        char o_consumelitString17768__13 = r.consume();
        r.isEmpty();
        r.isEmpty();
        char o_consumelitString17768__16 = r.consume();
        char o_consumelitString17768__17 = r.consume();
        ((CharacterReader) (r)).isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consume_add17777litString17876_remove24898() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_consume_add17777__3 = r.pos();
        char o_consume_add17777__4 = r.current();
        char o_consume_add17777__5 = r.consume();
        int o_consume_add17777__6 = r.pos();
        char o_consume_add17777__7 = r.current();
        int o_consume_add17777__8 = r.pos();
        int o_consume_add17777__9 = r.pos();
        char o_consume_add17777__10 = r.consume();
        char o_consume_add17777__11 = r.consume();
        char o_consume_add17777__12 = r.consume();
        r.isEmpty();
        char o_consume_add17777__14 = r.consume();
        r.isEmpty();
        char o_consume_add17777__17 = r.consume();
        char o_consume_add17777__18 = r.consume();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void unconsume_add276441() throws Exception {
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_unconsume_add276441__3 = r.consume();
        Assert.assertEquals('o', ((char) (o_unconsume_add276441__3)));
        char o_unconsume_add276441__4 = r.current();
        Assert.assertEquals('n', ((char) (o_unconsume_add276441__4)));
        r.unconsume();
        r.unconsume();
        r.unconsume();
        char o_unconsume_add276441__6 = r.current();
        Assert.assertEquals('o', ((char) (o_unconsume_add276441__6)));
        char o_unconsume_add276441__7 = r.consume();
        Assert.assertEquals('o', ((char) (o_unconsume_add276441__7)));
        char o_unconsume_add276441__8 = r.consume();
        Assert.assertEquals('n', ((char) (o_unconsume_add276441__8)));
        char o_unconsume_add276441__9 = r.consume();
        Assert.assertEquals('e', ((char) (o_unconsume_add276441__9)));
        char o_unconsume_add276441__10 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__10)));
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_unconsume_add276441__14 = r.current();
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__14)));
        char o_unconsume_add276441__15 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__15)));
        char o_unconsume_add276441__16 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__16)));
        char o_unconsume_add276441__19 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__19)));
        char o_unconsume_add276441__24 = r.current();
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__24)));
        char o_unconsume_add276441__25 = r.current();
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__25)));
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals('o', ((char) (o_unconsume_add276441__3)));
        Assert.assertEquals('n', ((char) (o_unconsume_add276441__4)));
        Assert.assertEquals('o', ((char) (o_unconsume_add276441__6)));
        Assert.assertEquals('o', ((char) (o_unconsume_add276441__7)));
        Assert.assertEquals('n', ((char) (o_unconsume_add276441__8)));
        Assert.assertEquals('e', ((char) (o_unconsume_add276441__9)));
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__10)));
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__14)));
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__15)));
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__16)));
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__19)));
        Assert.assertEquals('\uffff', ((char) (o_unconsume_add276441__24)));
    }

    @Test(timeout = 10000)
    public void unconsumelitString276422_failAssert968() throws Exception {
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
            org.junit.Assert.fail("unconsumelitString276422 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unconsume_remove276442litString276673_failAssert1017() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            char o_unconsume_remove276442__3 = r.consume();
            char o_unconsume_remove276442__4 = r.current();
            char o_unconsume_remove276442__5 = r.current();
            char o_unconsume_remove276442__6 = r.consume();
            char o_unconsume_remove276442__7 = r.consume();
            char o_unconsume_remove276442__8 = r.consume();
            char o_unconsume_remove276442__9 = r.consume();
            r.isEmpty();
            r.unconsume();
            r.isEmpty();
            char o_unconsume_remove276442__13 = r.current();
            char o_unconsume_remove276442__14 = r.consume();
            char o_unconsume_remove276442__15 = r.consume();
            r.isEmpty();
            r.isEmpty();
            char o_unconsume_remove276442__18 = r.consume();
            r.unconsume();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            char o_unconsume_remove276442__23 = r.current();
            org.junit.Assert.fail("unconsume_remove276442litString276673 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unconsume_add276428litString276612() throws Exception {
        CharacterReader r = new CharacterReader(" blah");
        Assert.assertEquals(" blah", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_unconsume_add276428__3 = r.consume();
        char o_unconsume_add276428__4 = r.current();
        r.unconsume();
        r.unconsume();
        r.unconsume();
        char o_unconsume_add276428__6 = r.current();
        char o_unconsume_add276428__7 = r.current();
        char o_unconsume_add276428__8 = r.consume();
        char o_unconsume_add276428__9 = r.consume();
        char o_unconsume_add276428__10 = r.consume();
        char o_unconsume_add276428__11 = r.consume();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_unconsume_add276428__15 = r.current();
        char o_unconsume_add276428__16 = r.consume();
        char o_unconsume_add276428__17 = r.consume();
        char o_unconsume_add276428__20 = r.consume();
        char o_unconsume_add276428__25 = r.current();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void unconsume_add276434litString276600() throws Exception {
        CharacterReader r = new CharacterReader("cs(");
        Assert.assertEquals("cs(", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_unconsume_add276434__3 = r.consume();
        char o_unconsume_add276434__4 = r.current();
        r.unconsume();
        r.unconsume();
        r.unconsume();
        char o_unconsume_add276434__6 = r.current();
        char o_unconsume_add276434__7 = r.consume();
        char o_unconsume_add276434__8 = r.consume();
        char o_unconsume_add276434__9 = r.consume();
        char o_unconsume_add276434__10 = r.consume();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        char o_unconsume_add276434__15 = r.current();
        char o_unconsume_add276434__16 = r.consume();
        char o_unconsume_add276434__17 = r.consume();
        char o_unconsume_add276434__20 = r.consume();
        char o_unconsume_add276434__25 = r.current();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void unconsume_remove276442_mg277914_mg285817() throws Exception {
        char __DSPOT_c_10242 = 'l';
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_unconsume_remove276442__3 = r.consume();
        char o_unconsume_remove276442__4 = r.current();
        char o_unconsume_remove276442__5 = r.current();
        char o_unconsume_remove276442__6 = r.consume();
        char o_unconsume_remove276442__7 = r.consume();
        char o_unconsume_remove276442__8 = r.consume();
        char o_unconsume_remove276442__9 = r.consume();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.unconsume();
        r.unconsume();
        char o_unconsume_remove276442__13 = r.current();
        char o_unconsume_remove276442__14 = r.consume();
        char o_unconsume_remove276442__15 = r.consume();
        char o_unconsume_remove276442__18 = r.consume();
        char o_unconsume_remove276442__23 = r.current();
        r.advance();
        String o_unconsume_remove276442_mg277914_mg285817__50 = r.consumeTo(__DSPOT_c_10242);
        Assert.assertEquals("", o_unconsume_remove276442_mg277914_mg285817__50);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void unconsume_remove276442_add277187litString278615_failAssert1067() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            char o_unconsume_remove276442__3 = r.consume();
            char o_unconsume_remove276442__4 = r.current();
            char o_unconsume_remove276442__5 = r.current();
            char o_unconsume_remove276442__6 = r.consume();
            char o_unconsume_remove276442__7 = r.consume();
            char o_unconsume_remove276442__8 = r.consume();
            char o_unconsume_remove276442__9 = r.consume();
            r.isEmpty();
            r.unconsume();
            r.isEmpty();
            char o_unconsume_remove276442__13 = r.current();
            char o_unconsume_remove276442__14 = r.consume();
            char o_unconsume_remove276442__15 = r.consume();
            r.isEmpty();
            r.isEmpty();
            char o_unconsume_remove276442__18 = r.consume();
            r.unconsume();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            char o_unconsume_remove276442__23 = r.current();
            org.junit.Assert.fail("unconsume_remove276442_add277187litString278615 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void unconsume_add276436_mg277890_failAssert1031litString279422() throws Exception {
        try {
            char __DSPOT_c_10102 = '}';
            CharacterReader r = new CharacterReader("");
            Assert.assertEquals("", ((CharacterReader) (r)).toString());
            Assert.assertTrue(((CharacterReader) (r)).isEmpty());
            char o_unconsume_add276436__3 = r.consume();
            char o_unconsume_add276436__4 = r.current();
            r.unconsume();
            r.unconsume();
            r.unconsume();
            char o_unconsume_add276436__6 = r.current();
            char o_unconsume_add276436__7 = r.consume();
            char o_unconsume_add276436__8 = r.consume();
            char o_unconsume_add276436__9 = r.consume();
            char o_unconsume_add276436__10 = r.consume();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            char o_unconsume_add276436__14 = r.current();
            char o_unconsume_add276436__15 = r.consume();
            char o_unconsume_add276436__16 = r.consume();
            char o_unconsume_add276436__17 = r.consume();
            char o_unconsume_add276436__20 = r.consume();
            char o_unconsume_add276436__25 = r.current();
            r.consumeTo(__DSPOT_c_10102);
            org.junit.Assert.fail("unconsume_add276436_mg277890 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void unconsume_add276437_mg277865_failAssert1030litChar279625() throws Exception {
        try {
            char __DSPOT_c_10092 = ' ';
            CharacterReader r = new CharacterReader("one");
            Assert.assertEquals("one", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            char o_unconsume_add276437__3 = r.consume();
            char o_unconsume_add276437__4 = r.current();
            r.unconsume();
            r.unconsume();
            r.unconsume();
            char o_unconsume_add276437__6 = r.current();
            char o_unconsume_add276437__7 = r.consume();
            char o_unconsume_add276437__8 = r.consume();
            char o_unconsume_add276437__9 = r.consume();
            char o_unconsume_add276437__10 = r.consume();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            char o_unconsume_add276437__14 = r.current();
            char o_unconsume_add276437__15 = r.consume();
            char o_unconsume_add276437__16 = r.consume();
            char o_unconsume_add276437__20 = r.consume();
            char o_unconsume_add276437__25 = r.current();
            r.consumeTo(__DSPOT_c_10092);
            org.junit.Assert.fail("unconsume_add276437_mg277865 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void mark_mg103261() throws Exception {
        char[] __DSPOT_chars_5635 = new char[]{ '|' };
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_mark_mg103261__4 = r.consume();
        Assert.assertEquals('o', ((char) (o_mark_mg103261__4)));
        r.mark();
        char o_mark_mg103261__6 = r.consume();
        Assert.assertEquals('n', ((char) (o_mark_mg103261__6)));
        char o_mark_mg103261__7 = r.consume();
        Assert.assertEquals('e', ((char) (o_mark_mg103261__7)));
        r.isEmpty();
        r.rewindToMark();
        char o_mark_mg103261__10 = r.consume();
        Assert.assertEquals('n', ((char) (o_mark_mg103261__10)));
        char o_mark_mg103261__11 = r.consume();
        Assert.assertEquals('e', ((char) (o_mark_mg103261__11)));
        String o_mark_mg103261__12 = r.consumeToAny(__DSPOT_chars_5635);
        Assert.assertEquals("", o_mark_mg103261__12);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals('o', ((char) (o_mark_mg103261__4)));
        Assert.assertEquals('n', ((char) (o_mark_mg103261__6)));
        Assert.assertEquals('e', ((char) (o_mark_mg103261__7)));
        Assert.assertEquals('n', ((char) (o_mark_mg103261__10)));
        Assert.assertEquals('e', ((char) (o_mark_mg103261__11)));
    }

    @Test(timeout = 10000)
    public void mark_remove103257() throws Exception {
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_mark_remove103257__3 = r.consume();
        Assert.assertEquals('o', ((char) (o_mark_remove103257__3)));
        char o_mark_remove103257__4 = r.consume();
        Assert.assertEquals('n', ((char) (o_mark_remove103257__4)));
        char o_mark_remove103257__5 = r.consume();
        Assert.assertEquals('e', ((char) (o_mark_remove103257__5)));
        r.isEmpty();
        r.rewindToMark();
        char o_mark_remove103257__8 = r.consume();
        Assert.assertEquals('o', ((char) (o_mark_remove103257__8)));
        char o_mark_remove103257__9 = r.consume();
        Assert.assertEquals('n', ((char) (o_mark_remove103257__9)));
        Assert.assertEquals("e", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals('o', ((char) (o_mark_remove103257__3)));
        Assert.assertEquals('n', ((char) (o_mark_remove103257__4)));
        Assert.assertEquals('e', ((char) (o_mark_remove103257__5)));
        Assert.assertEquals('o', ((char) (o_mark_remove103257__8)));
    }

    @Test(timeout = 10000)
    public void mark_add103255() throws Exception {
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_mark_add103255__3 = r.consume();
        Assert.assertEquals('o', ((char) (o_mark_add103255__3)));
        r.mark();
        char o_mark_add103255__5 = r.consume();
        Assert.assertEquals('n', ((char) (o_mark_add103255__5)));
        char o_mark_add103255__6 = r.consume();
        Assert.assertEquals('e', ((char) (o_mark_add103255__6)));
        r.isEmpty();
        r.rewindToMark();
        char o_mark_add103255__9 = r.consume();
        Assert.assertEquals('n', ((char) (o_mark_add103255__9)));
        char o_mark_add103255__10 = r.consume();
        Assert.assertEquals('e', ((char) (o_mark_add103255__10)));
        char o_mark_add103255__11 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_mark_add103255__11)));
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals('o', ((char) (o_mark_add103255__3)));
        Assert.assertEquals('n', ((char) (o_mark_add103255__5)));
        Assert.assertEquals('e', ((char) (o_mark_add103255__6)));
        Assert.assertEquals('n', ((char) (o_mark_add103255__9)));
        Assert.assertEquals('e', ((char) (o_mark_add103255__10)));
    }

    @Test(timeout = 10000)
    public void marklitString103248_mg103863() throws Exception {
        char __DSPOT_c_5656 = 'X';
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_marklitString103248__3 = r.consume();
        r.mark();
        char o_marklitString103248__5 = r.consume();
        char o_marklitString103248__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_marklitString103248__9 = r.consume();
        char o_marklitString103248__10 = r.consume();
        String o_marklitString103248_mg103863__22 = r.consumeTo(__DSPOT_c_5656);
        Assert.assertEquals("", o_marklitString103248_mg103863__22);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void marklitString103246_mg103880() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_marklitString103246__3 = r.consume();
        r.mark();
        char o_marklitString103246__5 = r.consume();
        char o_marklitString103246__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_marklitString103246__9 = r.consume();
        char o_marklitString103246__10 = r.consume();
        char o_marklitString103246_mg103880__21 = r.current();
        Assert.assertEquals('\uffff', ((char) (o_marklitString103246_mg103880__21)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void mark_add103255litString103386() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_mark_add103255__3 = r.consume();
        r.mark();
        char o_mark_add103255__5 = r.consume();
        char o_mark_add103255__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_mark_add103255__9 = r.consume();
        char o_mark_add103255__10 = r.consume();
        char o_mark_add103255__11 = r.consume();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void mark_mg103260_remove103796() throws Exception {
        char __DSPOT_c_5634 = '[';
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_mark_mg103260__4 = r.consume();
        char o_mark_mg103260__6 = r.consume();
        char o_mark_mg103260__7 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_mark_mg103260__10 = r.consume();
        char o_mark_mg103260__11 = r.consume();
        String o_mark_mg103260__12 = r.consumeTo(__DSPOT_c_5634);
        Assert.assertEquals("e", o_mark_mg103260__12);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void mark_add103254_add103477() throws Exception {
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).toString();
        char o_mark_add103254__3 = r.consume();
        r.mark();
        char o_mark_add103254__5 = r.consume();
        char o_mark_add103254__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        r.rewindToMark();
        char o_mark_add103254__10 = r.consume();
        char o_mark_add103254__11 = r.consume();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void marklitString103246_mg103879() throws Exception {
        char[] __DSPOT_chars_5663 = new char[]{ 'M', 'i', 'e' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_marklitString103246__3 = r.consume();
        r.mark();
        char o_marklitString103246__5 = r.consume();
        char o_marklitString103246__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_marklitString103246__9 = r.consume();
        char o_marklitString103246__10 = r.consume();
        String o_marklitString103246_mg103879__22 = r.consumeToAny(__DSPOT_chars_5663);
        Assert.assertEquals("", o_marklitString103246_mg103879__22);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void mark_remove103256_remove103765() throws Exception {
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_mark_remove103256__4 = r.consume();
        char o_mark_remove103256__5 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_mark_remove103256__8 = r.consume();
        char o_mark_remove103256__9 = r.consume();
        Assert.assertEquals("e", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void mark_mg103262_add103698litString104899() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_mark_mg103262__3 = r.consume();
        r.mark();
        char o_mark_mg103262__5 = r.consume();
        char o_mark_mg103262__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_mark_mg103262_add103698__15 = r.consume();
        char o_mark_mg103262__9 = r.consume();
        char o_mark_mg103262__10 = r.consume();
        char o_mark_mg103262__11 = r.current();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void marklitString103243_add103662_remove108601() throws Exception {
        CharacterReader r = new CharacterReader("o2ne");
        Assert.assertEquals("o2ne", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_marklitString103243__3 = r.consume();
        char o_marklitString103243__5 = r.consume();
        char o_marklitString103243__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_marklitString103243__9 = r.consume();
        char o_marklitString103243_add103662__18 = r.consume();
        char o_marklitString103243__10 = r.consume();
        Assert.assertEquals("e", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void marklitString103246_mg103879litChar105524() throws Exception {
        char[] __DSPOT_chars_5663 = new char[]{ 'M', 'j', 'e' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_marklitString103246__3 = r.consume();
        r.mark();
        char o_marklitString103246__5 = r.consume();
        char o_marklitString103246__6 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_marklitString103246__9 = r.consume();
        char o_marklitString103246__10 = r.consume();
        String o_marklitString103246_mg103879__22 = r.consumeToAny(__DSPOT_chars_5663);
        Assert.assertEquals("", o_marklitString103246_mg103879__22);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void mark_mg103260_add103689_remove108575() throws Exception {
        char __DSPOT_c_5634 = '[';
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_mark_mg103260__4 = r.consume();
        r.mark();
        char o_mark_mg103260__6 = r.consume();
        char o_mark_mg103260__7 = r.consume();
        r.isEmpty();
        char o_mark_mg103260__10 = r.consume();
        char o_mark_mg103260__11 = r.consume();
        String o_mark_mg103260__12 = r.consumeTo(__DSPOT_c_5634);
        Assert.assertEquals("", o_mark_mg103260__12);
        ((CharacterReader) (r)).isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_mark_mg103260__12);
    }

    @Test(timeout = 10000)
    public void mark_mg103260_add103689_remove108573() throws Exception {
        char __DSPOT_c_5634 = '[';
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_mark_mg103260__4 = r.consume();
        char o_mark_mg103260__6 = r.consume();
        char o_mark_mg103260__7 = r.consume();
        r.isEmpty();
        r.rewindToMark();
        char o_mark_mg103260__10 = r.consume();
        char o_mark_mg103260__11 = r.consume();
        String o_mark_mg103260__12 = r.consumeTo(__DSPOT_c_5634);
        Assert.assertEquals("e", o_mark_mg103260__12);
        ((CharacterReader) (r)).isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("e", o_mark_mg103260__12);
    }

    @Test(timeout = 10000)
    public void marklitString103241_remove103795_remove108413() throws Exception {
        CharacterReader r = new CharacterReader("three");
        Assert.assertEquals("three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_marklitString103241__3 = r.consume();
        char o_marklitString103241__5 = r.consume();
        char o_marklitString103241__6 = r.consume();
        r.isEmpty();
        char o_marklitString103241__9 = r.consume();
        char o_marklitString103241__10 = r.consume();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consumeToEnd() throws Exception {
        String in = "one two three";
        Assert.assertEquals("one two three", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("one two three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        Assert.assertEquals("one two three", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("one two three", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeToEndlitString63916() throws Exception {
        String in = "\n";
        Assert.assertEquals("\n", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals("\n", toEnd);
        r.isEmpty();
        Assert.assertEquals("\n", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeToEnd_mg63924() throws Exception {
        String in = "one two three";
        Assert.assertEquals("one two three", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("one two three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        int o_consumeToEnd_mg63924__7 = r.pos();
        Assert.assertEquals(13, ((int) (o_consumeToEnd_mg63924__7)));
        Assert.assertEquals("one two three", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("one two three", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeToEnd_mg63920_mg64231() throws Exception {
        char __DSPOT_c_3336 = '[';
        String in = "one two three";
        Assert.assertEquals("one two three", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("one two three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals("one two three", toEnd);
        r.isEmpty();
        r.advance();
        String o_consumeToEnd_mg63920_mg64231__9 = r.consumeTo(__DSPOT_c_3336);
        Assert.assertEquals("", o_consumeToEnd_mg63920_mg64231__9);
        Assert.assertEquals("one two three", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("one two three", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeToEnd_mg63921litString64008() throws Exception {
        char __DSPOT_c_3312 = '[';
        String in = "one wo three";
        Assert.assertEquals("one wo three", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("one wo three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals("one wo three", toEnd);
        r.isEmpty();
        String o_consumeToEnd_mg63921__8 = r.consumeTo(__DSPOT_c_3312);
        Assert.assertEquals("", o_consumeToEnd_mg63921__8);
        Assert.assertEquals("one wo three", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("one wo three", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeToEnd_mg63920litString64004() throws Exception {
        String in = ":";
        Assert.assertEquals(":", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals(":", toEnd);
        r.isEmpty();
        r.advance();
        Assert.assertEquals(":", in);
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(":", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeToEnd_mg63920litString64001() throws Exception {
        String in = "}h7V.yR/4}{OC";
        Assert.assertEquals("}h7V.yR/4}{OC", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("}h7V.yR/4}{OC", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals("}h7V.yR/4}{OC", toEnd);
        r.isEmpty();
        r.advance();
        Assert.assertEquals("}h7V.yR/4}{OC", in);
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("}h7V.yR/4}{OC", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeToEnd_mg63920_remove64170_mg68090() throws Exception {
        char __DSPOT_c_3384 = '#';
        String in = "one two three";
        Assert.assertEquals("one two three", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("one two three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals("one two three", toEnd);
        r.advance();
        String o_consumeToEnd_mg63920_remove64170_mg68090__8 = r.consumeTo(__DSPOT_c_3384);
        Assert.assertEquals("", o_consumeToEnd_mg63920_remove64170_mg68090__8);
        Assert.assertEquals("one two three", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("one two three", toEnd);
    }

    @Test(timeout = 10000)
    public void consumeToEndlitString63917_mg64186_mg68578() throws Exception {
        char __DSPOT_c_3318 = '4';
        String in = ":";
        Assert.assertEquals(":", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals(":", toEnd);
        r.isEmpty();
        String o_consumeToEndlitString63917_mg64186__8 = r.consumeTo(__DSPOT_c_3318);
        Assert.assertEquals("", o_consumeToEndlitString63917_mg64186__8);
        int o_consumeToEndlitString63917_mg64186_mg68578__11 = r.pos();
        Assert.assertEquals(1, ((int) (o_consumeToEndlitString63917_mg64186_mg68578__11)));
        Assert.assertEquals(":", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(":", toEnd);
        Assert.assertEquals("", o_consumeToEndlitString63917_mg64186__8);
    }

    @Test(timeout = 10000)
    public void consumeToEnd_mg63920_remove64170_remove67780() throws Exception {
        String in = "one two three";
        Assert.assertEquals("one two three", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("one two three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String toEnd = r.consumeToEnd();
        Assert.assertEquals("one two three", toEnd);
        Assert.assertEquals("one two three", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitString170055() throws Exception {
        String in = ":";
        Assert.assertEquals(":", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfCharlitString170055__4 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170055__4)));
        int o_nextIndexOfCharlitString170055__5 = r.nextIndexOf('h');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170055__5)));
        String pull = r.consumeTo('h');
        Assert.assertEquals(":", pull);
        char o_nextIndexOfCharlitString170055__8 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_nextIndexOfCharlitString170055__8)));
        int o_nextIndexOfCharlitString170055__9 = r.nextIndexOf('l');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170055__9)));
        String o_nextIndexOfCharlitString170055__10 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfCharlitString170055__10);
        int o_nextIndexOfCharlitString170055__11 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170055__11)));
        int o_nextIndexOfCharlitString170055__12 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170055__12)));
        Assert.assertEquals(":", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170055__4)));
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170055__5)));
        Assert.assertEquals(":", pull);
        Assert.assertEquals('\uffff', ((char) (o_nextIndexOfCharlitString170055__8)));
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170055__9)));
        Assert.assertEquals("", o_nextIndexOfCharlitString170055__10);
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170055__11)));
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitString170053() throws Exception {
        String in = "";
        Assert.assertEquals("", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfCharlitString170053__4 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170053__4)));
        int o_nextIndexOfCharlitString170053__5 = r.nextIndexOf('h');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170053__5)));
        String pull = r.consumeTo('h');
        Assert.assertEquals("", pull);
        char o_nextIndexOfCharlitString170053__8 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_nextIndexOfCharlitString170053__8)));
        int o_nextIndexOfCharlitString170053__9 = r.nextIndexOf('l');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170053__9)));
        String o_nextIndexOfCharlitString170053__10 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfCharlitString170053__10);
        int o_nextIndexOfCharlitString170053__11 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170053__11)));
        int o_nextIndexOfCharlitString170053__12 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170053__12)));
        Assert.assertEquals("", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170053__4)));
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170053__5)));
        Assert.assertEquals("", pull);
        Assert.assertEquals('\uffff', ((char) (o_nextIndexOfCharlitString170053__8)));
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170053__9)));
        Assert.assertEquals("", o_nextIndexOfCharlitString170053__10);
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitString170053__11)));
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitChar170074() throws Exception {
        String in = "blah blah";
        Assert.assertEquals("blah blah", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("blah blah", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfCharlitChar170074__4 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170074__4)));
        int o_nextIndexOfCharlitChar170074__5 = r.nextIndexOf('t');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170074__5)));
        String pull = r.consumeTo('h');
        Assert.assertEquals("bla", pull);
        char o_nextIndexOfCharlitChar170074__8 = r.consume();
        Assert.assertEquals('h', ((char) (o_nextIndexOfCharlitChar170074__8)));
        int o_nextIndexOfCharlitChar170074__9 = r.nextIndexOf('l');
        Assert.assertEquals(2, ((int) (o_nextIndexOfCharlitChar170074__9)));
        String o_nextIndexOfCharlitChar170074__10 = r.consumeToEnd();
        Assert.assertEquals(" blah", o_nextIndexOfCharlitChar170074__10);
        int o_nextIndexOfCharlitChar170074__11 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170074__11)));
        int o_nextIndexOfCharlitChar170074__12 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170074__12)));
        Assert.assertEquals("blah blah", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170074__4)));
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170074__5)));
        Assert.assertEquals("bla", pull);
        Assert.assertEquals('h', ((char) (o_nextIndexOfCharlitChar170074__8)));
        Assert.assertEquals(2, ((int) (o_nextIndexOfCharlitChar170074__9)));
        Assert.assertEquals(" blah", o_nextIndexOfCharlitChar170074__10);
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170074__11)));
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitChar170078() throws Exception {
        String in = "blah blah";
        Assert.assertEquals("blah blah", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("blah blah", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfCharlitChar170078__4 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170078__4)));
        int o_nextIndexOfCharlitChar170078__5 = r.nextIndexOf('h');
        Assert.assertEquals(3, ((int) (o_nextIndexOfCharlitChar170078__5)));
        String pull = r.consumeTo('\u0000');
        Assert.assertEquals("blah blah", pull);
        char o_nextIndexOfCharlitChar170078__8 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_nextIndexOfCharlitChar170078__8)));
        int o_nextIndexOfCharlitChar170078__9 = r.nextIndexOf('l');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170078__9)));
        String o_nextIndexOfCharlitChar170078__10 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfCharlitChar170078__10);
        int o_nextIndexOfCharlitChar170078__11 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170078__11)));
        int o_nextIndexOfCharlitChar170078__12 = r.nextIndexOf('x');
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170078__12)));
        Assert.assertEquals("blah blah", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170078__4)));
        Assert.assertEquals(3, ((int) (o_nextIndexOfCharlitChar170078__5)));
        Assert.assertEquals("blah blah", pull);
        Assert.assertEquals('\uffff', ((char) (o_nextIndexOfCharlitChar170078__8)));
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170078__9)));
        Assert.assertEquals("", o_nextIndexOfCharlitChar170078__10);
        Assert.assertEquals(-1, ((int) (o_nextIndexOfCharlitChar170078__11)));
    }

    @Test(timeout = 10000)
    public void nextIndexOfChar_mg170108_add172889() throws Exception {
        String in = "blah blah";
        Assert.assertEquals("blah blah", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("blah blah", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).isEmpty();
        int o_nextIndexOfChar_mg170108__4 = r.nextIndexOf('x');
        int o_nextIndexOfChar_mg170108__5 = r.nextIndexOf('h');
        String pull = r.consumeTo('h');
        Assert.assertEquals("bla", pull);
        char o_nextIndexOfChar_mg170108__8 = r.consume();
        int o_nextIndexOfChar_mg170108__9 = r.nextIndexOf('l');
        String o_nextIndexOfChar_mg170108__10 = r.consumeToEnd();
        Assert.assertEquals(" blah", o_nextIndexOfChar_mg170108__10);
        int o_nextIndexOfChar_mg170108__11 = r.nextIndexOf('x');
        int o_nextIndexOfChar_mg170108__12 = r.nextIndexOf('x');
        int o_nextIndexOfChar_mg170108__13 = r.pos();
        Assert.assertEquals("blah blah", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("bla", pull);
        Assert.assertEquals(" blah", o_nextIndexOfChar_mg170108__10);
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitNum170056litString170387() throws Exception {
        String in = "";
        Assert.assertEquals("", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfCharlitNum170056__4 = r.nextIndexOf('x');
        int o_nextIndexOfCharlitNum170056__5 = r.nextIndexOf('h');
        String pull = r.consumeTo('h');
        Assert.assertEquals("", pull);
        char o_nextIndexOfCharlitNum170056__8 = r.consume();
        int o_nextIndexOfCharlitNum170056__9 = r.nextIndexOf('l');
        String o_nextIndexOfCharlitNum170056__10 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfCharlitNum170056__10);
        int o_nextIndexOfCharlitNum170056__11 = r.nextIndexOf('x');
        Assert.assertEquals("", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", pull);
        Assert.assertEquals("", o_nextIndexOfCharlitNum170056__10);
    }

    @Test(timeout = 10000)
    public void nextIndexOfChar_mg170107litString170637() throws Exception {
        String in = ":";
        Assert.assertEquals(":", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfChar_mg170107__4 = r.nextIndexOf('x');
        int o_nextIndexOfChar_mg170107__5 = r.nextIndexOf('h');
        String pull = r.consumeTo('h');
        Assert.assertEquals(":", pull);
        char o_nextIndexOfChar_mg170107__8 = r.consume();
        int o_nextIndexOfChar_mg170107__9 = r.nextIndexOf('l');
        String o_nextIndexOfChar_mg170107__10 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfChar_mg170107__10);
        int o_nextIndexOfChar_mg170107__11 = r.nextIndexOf('x');
        int o_nextIndexOfChar_mg170107__12 = r.nextIndexOf('x');
        char o_nextIndexOfChar_mg170107__13 = r.current();
        Assert.assertEquals(":", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(":", pull);
        Assert.assertEquals("", o_nextIndexOfChar_mg170107__10);
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitNum170056litString170386() throws Exception {
        String in = "J[BD)x,&]";
        Assert.assertEquals("J[BD)x,&]", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("J[BD)x,&]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfCharlitNum170056__4 = r.nextIndexOf('x');
        int o_nextIndexOfCharlitNum170056__5 = r.nextIndexOf('h');
        String pull = r.consumeTo('h');
        Assert.assertEquals("J[BD)x,&]", pull);
        char o_nextIndexOfCharlitNum170056__8 = r.consume();
        int o_nextIndexOfCharlitNum170056__9 = r.nextIndexOf('l');
        String o_nextIndexOfCharlitNum170056__10 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfCharlitNum170056__10);
        int o_nextIndexOfCharlitNum170056__11 = r.nextIndexOf('x');
        Assert.assertEquals("J[BD)x,&]", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("J[BD)x,&]", pull);
        Assert.assertEquals("", o_nextIndexOfCharlitNum170056__10);
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitNum170057litChar171079litString174601() throws Exception {
        String in = "bTlah blah";
        Assert.assertEquals("bTlah blah", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("bTlah blah", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfCharlitNum170057__4 = r.nextIndexOf('x');
        int o_nextIndexOfCharlitNum170057__5 = r.nextIndexOf('h');
        String pull = r.consumeTo('Z');
        Assert.assertEquals("bTlah blah", pull);
        char o_nextIndexOfCharlitNum170057__8 = r.consume();
        int o_nextIndexOfCharlitNum170057__9 = r.nextIndexOf('l');
        String o_nextIndexOfCharlitNum170057__10 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfCharlitNum170057__10);
        int o_nextIndexOfCharlitNum170057__11 = r.nextIndexOf('x');
        Assert.assertEquals("bTlah blah", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("bTlah blah", pull);
        Assert.assertEquals("", o_nextIndexOfCharlitNum170057__10);
    }

    @Test(timeout = 10000)
    public void nextIndexOfChar_mg170105litChar172015litString174157() throws Exception {
        char __DSPOT_c_7774 = '#';
        String in = "\n";
        Assert.assertEquals("\n", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfChar_mg170105__5 = r.nextIndexOf('x');
        int o_nextIndexOfChar_mg170105__6 = r.nextIndexOf('h');
        String pull = r.consumeTo('h');
        Assert.assertEquals("\n", pull);
        char o_nextIndexOfChar_mg170105__9 = r.consume();
        int o_nextIndexOfChar_mg170105__10 = r.nextIndexOf('h');
        String o_nextIndexOfChar_mg170105__11 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfChar_mg170105__11);
        int o_nextIndexOfChar_mg170105__12 = r.nextIndexOf('x');
        int o_nextIndexOfChar_mg170105__13 = r.nextIndexOf('x');
        String o_nextIndexOfChar_mg170105__14 = r.consumeTo(__DSPOT_c_7774);
        Assert.assertEquals("", o_nextIndexOfChar_mg170105__14);
        Assert.assertEquals("\n", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", pull);
        Assert.assertEquals("", o_nextIndexOfChar_mg170105__11);
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitChar170069litString170539_mg182530() throws Exception {
        char __DSPOT_c_7990 = '-';
        String in = "";
        Assert.assertEquals("", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfCharlitChar170069__4 = r.nextIndexOf('y');
        int o_nextIndexOfCharlitChar170069__5 = r.nextIndexOf('h');
        String pull = r.consumeTo('h');
        Assert.assertEquals("", pull);
        char o_nextIndexOfCharlitChar170069__8 = r.consume();
        int o_nextIndexOfCharlitChar170069__9 = r.nextIndexOf('l');
        String o_nextIndexOfCharlitChar170069__10 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfCharlitChar170069__10);
        int o_nextIndexOfCharlitChar170069__11 = r.nextIndexOf('x');
        String o_nextIndexOfCharlitChar170069litString170539_mg182530__25 = r.consumeTo(__DSPOT_c_7990);
        Assert.assertEquals("", o_nextIndexOfCharlitChar170069litString170539_mg182530__25);
        Assert.assertEquals("", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", pull);
        Assert.assertEquals("", o_nextIndexOfCharlitChar170069__10);
    }

    @Test(timeout = 10000)
    public void nextIndexOfCharlitChar170066_mg173331_remove182301() throws Exception {
        String in = "blah blah";
        Assert.assertEquals("blah blah", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("blah blah", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfCharlitChar170066__4 = r.nextIndexOf('\u0000');
        int o_nextIndexOfCharlitChar170066__5 = r.nextIndexOf('h');
        String pull = r.consumeTo('h');
        Assert.assertEquals("bla", pull);
        char o_nextIndexOfCharlitChar170066__8 = r.consume();
        int o_nextIndexOfCharlitChar170066__9 = r.nextIndexOf('l');
        String o_nextIndexOfCharlitChar170066__10 = r.consumeToEnd();
        Assert.assertEquals(" blah", o_nextIndexOfCharlitChar170066__10);
        int o_nextIndexOfCharlitChar170066__11 = r.nextIndexOf('x');
        Assert.assertEquals("blah blah", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("bla", pull);
        Assert.assertEquals(" blah", o_nextIndexOfCharlitChar170066__10);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183944() throws Exception {
        String in = ":";
        Assert.assertEquals(":", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183944__4 = r.nextIndexOf("Foo");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183944__4)));
        int o_nextIndexOfStringlitString183944__5 = r.nextIndexOf("Two");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183944__5)));
        String o_nextIndexOfStringlitString183944__6 = r.consumeTo("something");
        Assert.assertEquals(":", o_nextIndexOfStringlitString183944__6);
        int o_nextIndexOfStringlitString183944__7 = r.nextIndexOf("Two");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183944__7)));
        String o_nextIndexOfStringlitString183944__8 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfStringlitString183944__8);
        int o_nextIndexOfStringlitString183944__9 = r.nextIndexOf("Two");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183944__9)));
        Assert.assertEquals(":", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183944__4)));
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183944__5)));
        Assert.assertEquals(":", o_nextIndexOfStringlitString183944__6);
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183944__7)));
        Assert.assertEquals("", o_nextIndexOfStringlitString183944__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183942() throws Exception {
        String in = "";
        Assert.assertEquals("", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183942__4 = r.nextIndexOf("Foo");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183942__4)));
        int o_nextIndexOfStringlitString183942__5 = r.nextIndexOf("Two");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183942__5)));
        String o_nextIndexOfStringlitString183942__6 = r.consumeTo("something");
        Assert.assertEquals("", o_nextIndexOfStringlitString183942__6);
        int o_nextIndexOfStringlitString183942__7 = r.nextIndexOf("Two");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183942__7)));
        String o_nextIndexOfStringlitString183942__8 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfStringlitString183942__8);
        int o_nextIndexOfStringlitString183942__9 = r.nextIndexOf("Two");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183942__9)));
        Assert.assertEquals("", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183942__4)));
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183942__5)));
        Assert.assertEquals("", o_nextIndexOfStringlitString183942__6);
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183942__7)));
        Assert.assertEquals("", o_nextIndexOfStringlitString183942__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183961() throws Exception {
        String in = "One Two something Two Three Four";
        Assert.assertEquals("One Two something Two Three Four", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("One Two something Two Three Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183961__4 = r.nextIndexOf("Foo");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183961__4)));
        int o_nextIndexOfStringlitString183961__5 = r.nextIndexOf("Two");
        Assert.assertEquals(4, ((int) (o_nextIndexOfStringlitString183961__5)));
        String o_nextIndexOfStringlitString183961__6 = r.consumeTo("One Two ");
        Assert.assertEquals("", o_nextIndexOfStringlitString183961__6);
        int o_nextIndexOfStringlitString183961__7 = r.nextIndexOf("Two");
        Assert.assertEquals(4, ((int) (o_nextIndexOfStringlitString183961__7)));
        String o_nextIndexOfStringlitString183961__8 = r.consumeToEnd();
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183961__8);
        int o_nextIndexOfStringlitString183961__9 = r.nextIndexOf("Two");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183961__9)));
        Assert.assertEquals("One Two something Two Three Four", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183961__4)));
        Assert.assertEquals(4, ((int) (o_nextIndexOfStringlitString183961__5)));
        Assert.assertEquals("", o_nextIndexOfStringlitString183961__6);
        Assert.assertEquals(4, ((int) (o_nextIndexOfStringlitString183961__7)));
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183961__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183967() throws Exception {
        String in = "One Two something Two Three Four";
        Assert.assertEquals("One Two something Two Three Four", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("One Two something Two Three Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183967__4 = r.nextIndexOf("Foo");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183967__4)));
        int o_nextIndexOfStringlitString183967__5 = r.nextIndexOf("Two");
        Assert.assertEquals(4, ((int) (o_nextIndexOfStringlitString183967__5)));
        String o_nextIndexOfStringlitString183967__6 = r.consumeTo("\n");
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183967__6);
        int o_nextIndexOfStringlitString183967__7 = r.nextIndexOf("Two");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183967__7)));
        String o_nextIndexOfStringlitString183967__8 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfStringlitString183967__8);
        int o_nextIndexOfStringlitString183967__9 = r.nextIndexOf("Two");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183967__9)));
        Assert.assertEquals("One Two something Two Three Four", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183967__4)));
        Assert.assertEquals(4, ((int) (o_nextIndexOfStringlitString183967__5)));
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183967__6);
        Assert.assertEquals(-1, ((int) (o_nextIndexOfStringlitString183967__7)));
        Assert.assertEquals("", o_nextIndexOfStringlitString183967__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183960litString185796() throws Exception {
        String in = "One Two something Two Three Four";
        Assert.assertEquals("One Two something Two Three Four", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("One Two something Two Three Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183960__4 = r.nextIndexOf("Foo");
        int o_nextIndexOfStringlitString183960__5 = r.nextIndexOf(":");
        String o_nextIndexOfStringlitString183960__6 = r.consumeTo("\n");
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183960__6);
        int o_nextIndexOfStringlitString183960__7 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183960__8 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfStringlitString183960__8);
        int o_nextIndexOfStringlitString183960__9 = r.nextIndexOf("Two");
        Assert.assertEquals("One Two something Two Three Four", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183960__6);
        Assert.assertEquals("", o_nextIndexOfStringlitString183960__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringnull184007_failAssert700_add187138() throws Exception {
        try {
            String in = "One Two something Two Three Four";
            Assert.assertEquals("One Two something Two Three Four", in);
            CharacterReader r = new CharacterReader(in);
            Assert.assertEquals("One Two something Two Three Four", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            r.nextIndexOf(null);
            r.nextIndexOf("Two");
            r.consumeTo("something");
            r.consumeTo("something");
            r.nextIndexOf("Two");
            r.consumeToEnd();
            r.nextIndexOf("Two");
            org.junit.Assert.fail("nextIndexOfStringnull184007 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183961_add186730() throws Exception {
        String in = "One Two something Two Three Four";
        Assert.assertEquals("One Two something Two Three Four", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("One Two something Two Three Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183961__4 = r.nextIndexOf("Foo");
        int o_nextIndexOfStringlitString183961__5 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183961__6 = r.consumeTo("One Two ");
        Assert.assertEquals("", o_nextIndexOfStringlitString183961__6);
        int o_nextIndexOfStringlitString183961__7 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183961_add186730__16 = r.consumeToEnd();
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183961_add186730__16);
        String o_nextIndexOfStringlitString183961__8 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfStringlitString183961__8);
        int o_nextIndexOfStringlitString183961__9 = r.nextIndexOf("Two");
        Assert.assertEquals("One Two something Two Three Four", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_nextIndexOfStringlitString183961__6);
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183961_add186730__16);
        Assert.assertEquals("", o_nextIndexOfStringlitString183961__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183962litString186120() throws Exception {
        String in = "One Two something Two Three Four";
        Assert.assertEquals("One Two something Two Three Four", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("One Two something Two Three Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183962__4 = r.nextIndexOf("Foo");
        int o_nextIndexOfStringlitString183962__5 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183962__6 = r.consumeTo("so<ething");
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183962__6);
        int o_nextIndexOfStringlitString183962__7 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183962__8 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfStringlitString183962__8);
        int o_nextIndexOfStringlitString183962__9 = r.nextIndexOf("Tcwo");
        Assert.assertEquals("One Two something Two Three Four", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183962__6);
        Assert.assertEquals("", o_nextIndexOfStringlitString183962__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183968_mg187353litChar192322() throws Exception {
        char[] __DSPOT_chars_8357 = new char[]{ '@', ' ' };
        String in = "One Two something Two Three Four";
        Assert.assertEquals("One Two something Two Three Four", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("One Two something Two Three Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183968__4 = r.nextIndexOf("Foo");
        int o_nextIndexOfStringlitString183968__5 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183968__6 = r.consumeTo(":");
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183968__6);
        int o_nextIndexOfStringlitString183968__7 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183968__8 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfStringlitString183968__8);
        int o_nextIndexOfStringlitString183968__9 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183968_mg187353__23 = r.consumeToAny(__DSPOT_chars_8357);
        Assert.assertEquals("", o_nextIndexOfStringlitString183968_mg187353__23);
        Assert.assertEquals("One Two something Two Three Four", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183968__6);
        Assert.assertEquals("", o_nextIndexOfStringlitString183968__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183961_add186733_remove194195() throws Exception {
        String in = "One Two something Two Three Four";
        Assert.assertEquals("One Two something Two Three Four", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("One Two something Two Three Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183961__4 = r.nextIndexOf("Foo");
        int o_nextIndexOfStringlitString183961__5 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183961__6 = r.consumeTo("One Two ");
        Assert.assertEquals("", o_nextIndexOfStringlitString183961__6);
        int o_nextIndexOfStringlitString183961__7 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183961__8 = r.consumeToEnd();
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183961__8);
        int o_nextIndexOfStringlitString183961__9 = r.nextIndexOf("Two");
        Assert.assertEquals("One Two something Two Three Four", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_nextIndexOfStringlitString183961__6);
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183961__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringlitString183964_add187097_remove194193() throws Exception {
        String in = "One Two something Two Three Four";
        Assert.assertEquals("One Two something Two Three Four", in);
        CharacterReader r = new CharacterReader(in);
        Assert.assertEquals("One Two something Two Three Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfStringlitString183964__4 = r.nextIndexOf("Foo");
        int o_nextIndexOfStringlitString183964__5 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183964__6 = r.consumeTo("somthing");
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183964__6);
        int o_nextIndexOfStringlitString183964__7 = r.nextIndexOf("Two");
        String o_nextIndexOfStringlitString183964__8 = r.consumeToEnd();
        Assert.assertEquals("", o_nextIndexOfStringlitString183964__8);
        int o_nextIndexOfStringlitString183964__9 = r.nextIndexOf("Two");
        Assert.assertEquals("One Two something Two Three Four", in);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two something Two Three Four", o_nextIndexOfStringlitString183964__6);
        Assert.assertEquals("", o_nextIndexOfStringlitString183964__8);
    }

    @Test(timeout = 10000)
    public void nextIndexOfStringnull184007_failAssert700_add187139litString188668() throws Exception {
        try {
            String in = "One Two something Two three Four";
            Assert.assertEquals("One Two something Two three Four", in);
            CharacterReader r = new CharacterReader(in);
            Assert.assertEquals("One Two something Two three Four", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            r.nextIndexOf(null);
            r.nextIndexOf("Two");
            r.consumeTo("something");
            r.nextIndexOf("Two");
            r.nextIndexOf("Two");
            r.consumeToEnd();
            r.nextIndexOf("Two");
            org.junit.Assert.fail("nextIndexOfStringnull184007 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitString196221() throws Exception {
        CharacterReader r = new CharacterReader("<[[one]]");
        Assert.assertEquals("<[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfUnmatchedlitString196221__3 = r.nextIndexOf("\n");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatchedlitString196221__3)));
        Assert.assertEquals("<[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitString196213() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfUnmatchedlitString196213__3 = r.nextIndexOf("]]>");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatchedlitString196213__3)));
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatched_mg196229() throws Exception {
        CharacterReader r = new CharacterReader("<[[one]]");
        Assert.assertEquals("<[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfUnmatched_mg196229__3 = r.nextIndexOf("]]>");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatched_mg196229__3)));
        r.advance();
        Assert.assertEquals("[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatched_mg196229__3)));
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitString196214litString196429() throws Exception {
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfUnmatchedlitString196214__3 = r.nextIndexOf("one");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitString196222_add196674() throws Exception {
        CharacterReader r = new CharacterReader("<[[one]]");
        Assert.assertEquals("<[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfUnmatchedlitString196222_add196674__3 = r.nextIndexOf(":");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatchedlitString196222_add196674__3)));
        int o_nextIndexOfUnmatchedlitString196222__3 = r.nextIndexOf(":");
        Assert.assertEquals("<[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatchedlitString196222_add196674__3)));
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitString196214_mg196779() throws Exception {
        char[] __DSPOT_chars_8845 = new char[0];
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfUnmatchedlitString196214__3 = r.nextIndexOf("]]>");
        String o_nextIndexOfUnmatchedlitString196214_mg196779__7 = r.consumeToAny(__DSPOT_chars_8845);
        Assert.assertEquals(":", o_nextIndexOfUnmatchedlitString196214_mg196779__7);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitString196207_mg196797() throws Exception {
        CharacterReader r = new CharacterReader("Two");
        Assert.assertEquals("Two", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfUnmatchedlitString196207__3 = r.nextIndexOf("]]>");
        r.advance();
        Assert.assertEquals("wo", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitNum196224_add196598litString198212() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).isEmpty();
        int o_nextIndexOfUnmatchedlitNum196224__3 = r.nextIndexOf("]]>");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitNum196224_add196599_mg201573() throws Exception {
        CharacterReader r = new CharacterReader("<[[one]]");
        Assert.assertEquals("<[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfUnmatchedlitNum196224_add196599__3 = r.nextIndexOf("]]>");
        int o_nextIndexOfUnmatchedlitNum196224__3 = r.nextIndexOf("]]>");
        r.advance();
        Assert.assertEquals("[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatchedlitNum196225litString196404_add200295() throws Exception {
        CharacterReader r = new CharacterReader("<[[one]]");
        Assert.assertEquals("<[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        int o_nextIndexOfUnmatchedlitNum196225litString196404_add200295__3 = r.nextIndexOf(":");
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatchedlitNum196225litString196404_add200295__3)));
        int o_nextIndexOfUnmatchedlitNum196225__3 = r.nextIndexOf(":");
        Assert.assertEquals("<[[one]]", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(-1, ((int) (o_nextIndexOfUnmatchedlitNum196225litString196404_add200295__3)));
    }

    @Test(timeout = 10000)
    public void nextIndexOfUnmatched_mg196230_add196716litString198292() throws Exception {
        char __DSPOT_c_8824 = 'D';
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).isEmpty();
        int o_nextIndexOfUnmatched_mg196230__4 = r.nextIndexOf("]]>");
        String o_nextIndexOfUnmatched_mg196230__5 = r.consumeTo(__DSPOT_c_8824);
        Assert.assertEquals("\n", o_nextIndexOfUnmatched_mg196230__5);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consumeToChar_mg53380() throws Exception {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToChar_mg53380__3 = r.consumeTo('T');
        Assert.assertEquals("One ", o_consumeToChar_mg53380__3);
        String o_consumeToChar_mg53380__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar_mg53380__4);
        char o_consumeToChar_mg53380__5 = r.consume();
        Assert.assertEquals('T', ((char) (o_consumeToChar_mg53380__5)));
        String o_consumeToChar_mg53380__6 = r.consumeTo('T');
        Assert.assertEquals("wo ", o_consumeToChar_mg53380__6);
        char o_consumeToChar_mg53380__7 = r.consume();
        Assert.assertEquals('T', ((char) (o_consumeToChar_mg53380__7)));
        char o_consumeToChar_mg53380__8 = r.consume();
        Assert.assertEquals('h', ((char) (o_consumeToChar_mg53380__8)));
        String o_consumeToChar_mg53380__9 = r.consumeTo('T');
        Assert.assertEquals("ree", o_consumeToChar_mg53380__9);
        int o_consumeToChar_mg53380__10 = r.pos();
        Assert.assertEquals(13, ((int) (o_consumeToChar_mg53380__10)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One ", o_consumeToChar_mg53380__3);
        Assert.assertEquals("", o_consumeToChar_mg53380__4);
        Assert.assertEquals('T', ((char) (o_consumeToChar_mg53380__5)));
        Assert.assertEquals("wo ", o_consumeToChar_mg53380__6);
        Assert.assertEquals('T', ((char) (o_consumeToChar_mg53380__7)));
        Assert.assertEquals('h', ((char) (o_consumeToChar_mg53380__8)));
        Assert.assertEquals("ree", o_consumeToChar_mg53380__9);
    }

    @Test(timeout = 10000)
    public void consumeToCharlitChar53346() throws Exception {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToCharlitChar53346__3 = r.consumeTo('\u0000');
        Assert.assertEquals("One Two Three", o_consumeToCharlitChar53346__3);
        String o_consumeToCharlitChar53346__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53346__4);
        char o_consumeToCharlitChar53346__5 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitChar53346__5)));
        String o_consumeToCharlitChar53346__6 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53346__6);
        char o_consumeToCharlitChar53346__7 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitChar53346__7)));
        char o_consumeToCharlitChar53346__8 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitChar53346__8)));
        String o_consumeToCharlitChar53346__9 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53346__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two Three", o_consumeToCharlitChar53346__3);
        Assert.assertEquals("", o_consumeToCharlitChar53346__4);
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitChar53346__5)));
        Assert.assertEquals("", o_consumeToCharlitChar53346__6);
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitChar53346__7)));
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitChar53346__8)));
    }

    @Test(timeout = 10000)
    public void consumeToCharlitString53344() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToCharlitString53344__3 = r.consumeTo('T');
        Assert.assertEquals("\n", o_consumeToCharlitString53344__3);
        String o_consumeToCharlitString53344__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitString53344__4);
        char o_consumeToCharlitString53344__5 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitString53344__5)));
        String o_consumeToCharlitString53344__6 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitString53344__6);
        char o_consumeToCharlitString53344__7 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitString53344__7)));
        char o_consumeToCharlitString53344__8 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitString53344__8)));
        String o_consumeToCharlitString53344__9 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitString53344__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", o_consumeToCharlitString53344__3);
        Assert.assertEquals("", o_consumeToCharlitString53344__4);
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitString53344__5)));
        Assert.assertEquals("", o_consumeToCharlitString53344__6);
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitString53344__7)));
        Assert.assertEquals('\uffff', ((char) (o_consumeToCharlitString53344__8)));
    }

    @Test(timeout = 10000)
    public void consumeToChar_add53375litString53521() throws Exception {
        CharacterReader r = new CharacterReader("One Tw Three");
        Assert.assertEquals("One Tw Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToChar_add53375__3 = r.consumeTo('T');
        Assert.assertEquals("One ", o_consumeToChar_add53375__3);
        String o_consumeToChar_add53375__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar_add53375__4);
        char o_consumeToChar_add53375__5 = r.consume();
        String o_consumeToChar_add53375__6 = r.consumeTo('T');
        Assert.assertEquals("w ", o_consumeToChar_add53375__6);
        char o_consumeToChar_add53375__7 = r.consume();
        char o_consumeToChar_add53375__8 = r.consume();
        String o_consumeToChar_add53375__9 = r.consumeTo('T');
        Assert.assertEquals("ree", o_consumeToChar_add53375__9);
        String o_consumeToChar_add53375__10 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar_add53375__10);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One ", o_consumeToChar_add53375__3);
        Assert.assertEquals("", o_consumeToChar_add53375__4);
        Assert.assertEquals("w ", o_consumeToChar_add53375__6);
        Assert.assertEquals("ree", o_consumeToChar_add53375__9);
    }

    @Test(timeout = 10000)
    public void consumeToCharlitChar53348litString53650() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeToCharlitChar53348__3 = r.consumeTo('(');
        Assert.assertEquals("", o_consumeToCharlitChar53348__3);
        String o_consumeToCharlitChar53348__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53348__4);
        char o_consumeToCharlitChar53348__5 = r.consume();
        String o_consumeToCharlitChar53348__6 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53348__6);
        char o_consumeToCharlitChar53348__7 = r.consume();
        char o_consumeToCharlitChar53348__8 = r.consume();
        String o_consumeToCharlitChar53348__9 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53348__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeToCharlitChar53348__3);
        Assert.assertEquals("", o_consumeToCharlitChar53348__4);
        Assert.assertEquals("", o_consumeToCharlitChar53348__6);
    }

    @Test(timeout = 10000)
    public void consumeToCharlitChar53348litString53652() throws Exception {
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToCharlitChar53348__3 = r.consumeTo('(');
        Assert.assertEquals(":", o_consumeToCharlitChar53348__3);
        String o_consumeToCharlitChar53348__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53348__4);
        char o_consumeToCharlitChar53348__5 = r.consume();
        String o_consumeToCharlitChar53348__6 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53348__6);
        char o_consumeToCharlitChar53348__7 = r.consume();
        char o_consumeToCharlitChar53348__8 = r.consume();
        String o_consumeToCharlitChar53348__9 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53348__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(":", o_consumeToCharlitChar53348__3);
        Assert.assertEquals("", o_consumeToCharlitChar53348__4);
        Assert.assertEquals("", o_consumeToCharlitChar53348__6);
    }

    @Test(timeout = 10000)
    public void consumeToCharlitChar53349litChar53993_add62103() throws Exception {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToCharlitChar53349__3 = r.consumeTo('U');
        Assert.assertEquals("One Two Three", o_consumeToCharlitChar53349__3);
        String o_consumeToCharlitChar53349__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53349__4);
        char o_consumeToCharlitChar53349__5 = r.consume();
        String o_consumeToCharlitChar53349__6 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53349__6);
        char o_consumeToCharlitChar53349__7 = r.consume();
        char o_consumeToCharlitChar53349__8 = r.consume();
        String o_consumeToCharlitChar53349__9 = r.consumeTo('U');
        Assert.assertEquals("", o_consumeToCharlitChar53349__9);
        ((CharacterReader) (r)).isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two Three", o_consumeToCharlitChar53349__3);
        Assert.assertEquals("", o_consumeToCharlitChar53349__4);
        Assert.assertEquals("", o_consumeToCharlitChar53349__6);
        Assert.assertEquals("", o_consumeToCharlitChar53349__9);
    }

    @Test(timeout = 10000)
    public void consumeToCharlitChar53360litString53674litChar58828() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeToCharlitChar53360__3 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53360__3);
        String o_consumeToCharlitChar53360__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53360__4);
        char o_consumeToCharlitChar53360__5 = r.consume();
        String o_consumeToCharlitChar53360__6 = r.consumeTo('4');
        Assert.assertEquals("", o_consumeToCharlitChar53360__6);
        char o_consumeToCharlitChar53360__7 = r.consume();
        char o_consumeToCharlitChar53360__8 = r.consume();
        String o_consumeToCharlitChar53360__9 = r.consumeTo('\u0000');
        Assert.assertEquals("", o_consumeToCharlitChar53360__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeToCharlitChar53360__3);
        Assert.assertEquals("", o_consumeToCharlitChar53360__4);
        Assert.assertEquals("", o_consumeToCharlitChar53360__6);
    }

    @Test(timeout = 10000)
    public void consumeToChar_add53371_mg54984litString56883() throws Exception {
        char __DSPOT_c_2826 = '5';
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToChar_add53371__3 = r.consumeTo('T');
        Assert.assertEquals("\n", o_consumeToChar_add53371__3);
        String o_consumeToChar_add53371__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar_add53371__4);
        String o_consumeToChar_add53371__5 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar_add53371__5);
        char o_consumeToChar_add53371__6 = r.consume();
        String o_consumeToChar_add53371__7 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar_add53371__7);
        char o_consumeToChar_add53371__8 = r.consume();
        char o_consumeToChar_add53371__9 = r.consume();
        String o_consumeToChar_add53371__10 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar_add53371__10);
        String o_consumeToChar_add53371_mg54984__28 = r.consumeTo(__DSPOT_c_2826);
        Assert.assertEquals("", o_consumeToChar_add53371_mg54984__28);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", o_consumeToChar_add53371__3);
        Assert.assertEquals("", o_consumeToChar_add53371__4);
        Assert.assertEquals("", o_consumeToChar_add53371__5);
        Assert.assertEquals("", o_consumeToChar_add53371__7);
        Assert.assertEquals("", o_consumeToChar_add53371__10);
    }

    @Test(timeout = 10000)
    public void consumeToCharlitChar53366litString53610_mg62574() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeToCharlitChar53366__3 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53366__3);
        String o_consumeToCharlitChar53366__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53366__4);
        char o_consumeToCharlitChar53366__5 = r.consume();
        String o_consumeToCharlitChar53366__6 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToCharlitChar53366__6);
        char o_consumeToCharlitChar53366__7 = r.consume();
        char o_consumeToCharlitChar53366__8 = r.consume();
        String o_consumeToCharlitChar53366__9 = r.consumeTo('A');
        Assert.assertEquals("", o_consumeToCharlitChar53366__9);
        int o_consumeToCharlitChar53366litString53610_mg62574__24 = r.pos();
        Assert.assertEquals(3, ((int) (o_consumeToCharlitChar53366litString53610_mg62574__24)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeToCharlitChar53366__3);
        Assert.assertEquals("", o_consumeToCharlitChar53366__4);
        Assert.assertEquals("", o_consumeToCharlitChar53366__6);
        Assert.assertEquals("", o_consumeToCharlitChar53366__9);
    }

    @Test(timeout = 10000)
    public void consumeToChar_add53375_add54511_remove62280() throws Exception {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToChar_add53375__3 = r.consumeTo('T');
        Assert.assertEquals("One ", o_consumeToChar_add53375__3);
        String o_consumeToChar_add53375__4 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar_add53375__4);
        char o_consumeToChar_add53375__5 = r.consume();
        String o_consumeToChar_add53375__6 = r.consumeTo('T');
        Assert.assertEquals("wo ", o_consumeToChar_add53375__6);
        char o_consumeToChar_add53375__7 = r.consume();
        char o_consumeToChar_add53375__8 = r.consume();
        String o_consumeToChar_add53375__9 = r.consumeTo('T');
        Assert.assertEquals("ree", o_consumeToChar_add53375__9);
        String o_consumeToChar_add53375__10 = r.consumeTo('T');
        Assert.assertEquals("", o_consumeToChar_add53375__10);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One ", o_consumeToChar_add53375__3);
        Assert.assertEquals("", o_consumeToChar_add53375__4);
        Assert.assertEquals("wo ", o_consumeToChar_add53375__6);
        Assert.assertEquals("ree", o_consumeToChar_add53375__9);
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77322() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToStringlitString77322__3 = r.consumeTo("Two");
        Assert.assertEquals("\n", o_consumeToStringlitString77322__3);
        char o_consumeToStringlitString77322__4 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77322__4)));
        String o_consumeToStringlitString77322__5 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToStringlitString77322__5);
        char o_consumeToStringlitString77322__6 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77322__6)));
        char o_consumeToStringlitString77322__7 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77322__7)));
        String o_consumeToStringlitString77322__8 = r.consumeTo("Qux");
        Assert.assertEquals("", o_consumeToStringlitString77322__8);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", o_consumeToStringlitString77322__3);
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77322__4)));
        Assert.assertEquals("", o_consumeToStringlitString77322__5);
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77322__6)));
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77322__7)));
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77321() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeToStringlitString77321__3 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToStringlitString77321__3);
        char o_consumeToStringlitString77321__4 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77321__4)));
        String o_consumeToStringlitString77321__5 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToStringlitString77321__5);
        char o_consumeToStringlitString77321__6 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77321__6)));
        char o_consumeToStringlitString77321__7 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77321__7)));
        String o_consumeToStringlitString77321__8 = r.consumeTo("Qux");
        Assert.assertEquals("", o_consumeToStringlitString77321__8);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeToStringlitString77321__3);
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77321__4)));
        Assert.assertEquals("", o_consumeToStringlitString77321__5);
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77321__6)));
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77321__7)));
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77327() throws Exception {
        CharacterReader r = new CharacterReader("One Two Two Four");
        Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToStringlitString77327__3 = r.consumeTo("To");
        Assert.assertEquals("One Two Two Four", o_consumeToStringlitString77327__3);
        char o_consumeToStringlitString77327__4 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77327__4)));
        String o_consumeToStringlitString77327__5 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToStringlitString77327__5);
        char o_consumeToStringlitString77327__6 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77327__6)));
        char o_consumeToStringlitString77327__7 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77327__7)));
        String o_consumeToStringlitString77327__8 = r.consumeTo("Qux");
        Assert.assertEquals("", o_consumeToStringlitString77327__8);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two Two Four", o_consumeToStringlitString77327__3);
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77327__4)));
        Assert.assertEquals("", o_consumeToStringlitString77327__5);
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77327__6)));
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77327__7)));
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77328() throws Exception {
        CharacterReader r = new CharacterReader("One Two Two Four");
        Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToStringlitString77328__3 = r.consumeTo("u//");
        Assert.assertEquals("One Two Two Four", o_consumeToStringlitString77328__3);
        char o_consumeToStringlitString77328__4 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77328__4)));
        String o_consumeToStringlitString77328__5 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToStringlitString77328__5);
        char o_consumeToStringlitString77328__6 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77328__6)));
        char o_consumeToStringlitString77328__7 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77328__7)));
        String o_consumeToStringlitString77328__8 = r.consumeTo("Qux");
        Assert.assertEquals("", o_consumeToStringlitString77328__8);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two Two Four", o_consumeToStringlitString77328__3);
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77328__4)));
        Assert.assertEquals("", o_consumeToStringlitString77328__5);
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77328__6)));
        Assert.assertEquals('\uffff', ((char) (o_consumeToStringlitString77328__7)));
    }

    @Test(timeout = 10000)
    public void consumeToString_add77350() throws Exception {
        CharacterReader r = new CharacterReader("One Two Two Four");
        Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToString_add77350__3 = r.consumeTo("Two");
        Assert.assertEquals("One ", o_consumeToString_add77350__3);
        char o_consumeToString_add77350__4 = r.consume();
        Assert.assertEquals('T', ((char) (o_consumeToString_add77350__4)));
        String o_consumeToString_add77350__5 = r.consumeTo("Two");
        Assert.assertEquals("wo ", o_consumeToString_add77350__5);
        String o_consumeToString_add77350__6 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToString_add77350__6);
        char o_consumeToString_add77350__7 = r.consume();
        Assert.assertEquals('T', ((char) (o_consumeToString_add77350__7)));
        char o_consumeToString_add77350__8 = r.consume();
        Assert.assertEquals('w', ((char) (o_consumeToString_add77350__8)));
        String o_consumeToString_add77350__9 = r.consumeTo("Qux");
        Assert.assertEquals("o Four", o_consumeToString_add77350__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One ", o_consumeToString_add77350__3);
        Assert.assertEquals('T', ((char) (o_consumeToString_add77350__4)));
        Assert.assertEquals("wo ", o_consumeToString_add77350__5);
        Assert.assertEquals("", o_consumeToString_add77350__6);
        Assert.assertEquals('T', ((char) (o_consumeToString_add77350__7)));
        Assert.assertEquals('w', ((char) (o_consumeToString_add77350__8)));
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77316null78996_failAssert271() throws Exception {
        try {
            CharacterReader r = new CharacterReader("Foo");
            String o_consumeToStringlitString77316__3 = r.consumeTo("Two");
            char o_consumeToStringlitString77316__4 = r.consume();
            String o_consumeToStringlitString77316__5 = r.consumeTo("Two");
            char o_consumeToStringlitString77316__6 = r.consume();
            char o_consumeToStringlitString77316__7 = r.consume();
            String o_consumeToStringlitString77316__8 = r.consumeTo(null);
            org.junit.Assert.fail("consumeToStringlitString77316null78996 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77318litString78145() throws Exception {
        CharacterReader r = new CharacterReader("One T|wo Two Four");
        Assert.assertEquals("One T|wo Two Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToStringlitString77318__3 = r.consumeTo("How about now");
        Assert.assertEquals("One T|wo Two Four", o_consumeToStringlitString77318__3);
        char o_consumeToStringlitString77318__4 = r.consume();
        String o_consumeToStringlitString77318__5 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToStringlitString77318__5);
        char o_consumeToStringlitString77318__6 = r.consume();
        char o_consumeToStringlitString77318__7 = r.consume();
        String o_consumeToStringlitString77318__8 = r.consumeTo("Qux");
        Assert.assertEquals("", o_consumeToStringlitString77318__8);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One T|wo Two Four", o_consumeToStringlitString77318__3);
        Assert.assertEquals("", o_consumeToStringlitString77318__5);
    }

    @Test(timeout = 10000)
    public void consumeToStringnull77358_failAssert255_mg78904() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Two Four");
            Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            r.consumeTo(null);
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToStringnull77358 should have thrown NullPointerException");
            r.pos();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77337_failAssert258_add78685() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Two Four");
            Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            String o_consumeToStringlitString77337_failAssert258_add78685__5 = r.consumeTo("Two");
            Assert.assertEquals("One ", o_consumeToStringlitString77337_failAssert258_add78685__5);
            String o_consumeToStringlitString77337_failAssert258_add78685__6 = r.consumeTo("Two");
            Assert.assertEquals("", o_consumeToStringlitString77337_failAssert258_add78685__6);
            char o_consumeToStringlitString77337_failAssert258_add78685__7 = r.consume();
            Assert.assertEquals('T', ((char) (o_consumeToStringlitString77337_failAssert258_add78685__7)));
            r.consumeTo("");
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToStringlitString77337 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void consumeToString_add77349_add78294() throws Exception {
        CharacterReader r = new CharacterReader("One Two Two Four");
        Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToString_add77349__3 = r.consumeTo("Two");
        Assert.assertEquals("One ", o_consumeToString_add77349__3);
        char o_consumeToString_add77349_add78294__6 = r.consume();
        Assert.assertEquals('T', ((char) (o_consumeToString_add77349_add78294__6)));
        char o_consumeToString_add77349__4 = r.consume();
        char o_consumeToString_add77349__5 = r.consume();
        String o_consumeToString_add77349__6 = r.consumeTo("Two");
        Assert.assertEquals(" ", o_consumeToString_add77349__6);
        char o_consumeToString_add77349__7 = r.consume();
        char o_consumeToString_add77349__8 = r.consume();
        String o_consumeToString_add77349__9 = r.consumeTo("Qux");
        Assert.assertEquals("o Four", o_consumeToString_add77349__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One ", o_consumeToString_add77349__3);
        Assert.assertEquals('T', ((char) (o_consumeToString_add77349_add78294__6)));
        Assert.assertEquals(" ", o_consumeToString_add77349__6);
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77330_add78370() throws Exception {
        CharacterReader r = new CharacterReader("One Two Two Four");
        Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToStringlitString77330__3 = r.consumeTo("\n");
        Assert.assertEquals("One Two Two Four", o_consumeToStringlitString77330__3);
        char o_consumeToStringlitString77330__4 = r.consume();
        String o_consumeToStringlitString77330__5 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToStringlitString77330__5);
        char o_consumeToStringlitString77330__6 = r.consume();
        char o_consumeToStringlitString77330__7 = r.consume();
        String o_consumeToStringlitString77330_add78370__18 = r.consumeTo("Qux");
        Assert.assertEquals("", o_consumeToStringlitString77330_add78370__18);
        String o_consumeToStringlitString77330__8 = r.consumeTo("Qux");
        Assert.assertEquals("", o_consumeToStringlitString77330__8);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two Two Four", o_consumeToStringlitString77330__3);
        Assert.assertEquals("", o_consumeToStringlitString77330__5);
        Assert.assertEquals("", o_consumeToStringlitString77330_add78370__18);
    }

    @Test(timeout = 10000)
    public void consumeToStringnull77359_failAssert256_add78671_mg84853() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Two Four");
            Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            String o_consumeToStringnull77359_failAssert256_add78671__5 = r.consumeTo("Two");
            Assert.assertEquals("One ", o_consumeToStringnull77359_failAssert256_add78671__5);
            String o_consumeToStringnull77359_failAssert256_add78671__6 = r.consumeTo("Two");
            Assert.assertEquals("", o_consumeToStringnull77359_failAssert256_add78671__6);
            char o_consumeToStringnull77359_failAssert256_add78671__7 = r.consume();
            r.consumeTo(null);
            r.consume();
            r.consume();
            r.consumeTo("Qux");
            org.junit.Assert.fail("consumeToStringnull77359 should have thrown NullPointerException");
            r.current();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void consumeToString_add77351_mg78706litString80401() throws Exception {
        char __DSPOT_c_4192 = '4';
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToString_add77351__3 = r.consumeTo("Two");
        Assert.assertEquals("\n", o_consumeToString_add77351__3);
        char o_consumeToString_add77351__4 = r.consume();
        String o_consumeToString_add77351__5 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToString_add77351__5);
        char o_consumeToString_add77351__6 = r.consume();
        char o_consumeToString_add77351__7 = r.consume();
        char o_consumeToString_add77351__8 = r.consume();
        String o_consumeToString_add77351__9 = r.consumeTo("Qux");
        Assert.assertEquals("", o_consumeToString_add77351__9);
        String o_consumeToString_add77351_mg78706__25 = r.consumeTo(__DSPOT_c_4192);
        Assert.assertEquals("", o_consumeToString_add77351_mg78706__25);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", o_consumeToString_add77351__3);
        Assert.assertEquals("", o_consumeToString_add77351__5);
        Assert.assertEquals("", o_consumeToString_add77351__9);
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77329_failAssert257litString78226_mg85088() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Two Four");
            Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            r.consumeTo("");
            r.consume();
            r.consumeTo("Two");
            r.consume();
            r.consume();
            r.consumeTo("@ux");
            org.junit.Assert.fail("consumeToStringlitString77329 should have thrown StringIndexOutOfBoundsException");
            r.current();
        } catch (StringIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void consumeToStringlitString77330_add78371_remove84470() throws Exception {
        CharacterReader r = new CharacterReader("One Two Two Four");
        Assert.assertEquals("One Two Two Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToStringlitString77330__3 = r.consumeTo("\n");
        Assert.assertEquals("One Two Two Four", o_consumeToStringlitString77330__3);
        char o_consumeToStringlitString77330__4 = r.consume();
        String o_consumeToStringlitString77330__5 = r.consumeTo("Two");
        Assert.assertEquals("", o_consumeToStringlitString77330__5);
        char o_consumeToStringlitString77330__6 = r.consume();
        char o_consumeToStringlitString77330__7 = r.consume();
        String o_consumeToStringlitString77330__8 = r.consumeTo("Qux");
        Assert.assertEquals("", o_consumeToStringlitString77330__8);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One Two Two Four", o_consumeToStringlitString77330__3);
        Assert.assertEquals("", o_consumeToStringlitString77330__5);
    }

    @Test(timeout = 10000)
    public void advance_remove12() throws Exception {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_advance_remove12__3 = r.consume();
        Assert.assertEquals('O', ((char) (o_advance_remove12__3)));
        char o_advance_remove12__4 = r.consume();
        Assert.assertEquals('n', ((char) (o_advance_remove12__4)));
        Assert.assertEquals("e Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals('O', ((char) (o_advance_remove12__3)));
    }

    @Test(timeout = 10000)
    public void advancelitString6_mg361() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_advancelitString6__3 = r.consume();
        r.advance();
        char o_advancelitString6__5 = r.consume();
        int o_advancelitString6_mg361__10 = r.pos();
        Assert.assertEquals(3, ((int) (o_advancelitString6_mg361__10)));
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advance_mg14litChar168() throws Exception {
        char __DSPOT_c_0 = 'T';
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_advance_mg14__4 = r.consume();
        r.advance();
        char o_advance_mg14__6 = r.consume();
        String o_advance_mg14__7 = r.consumeTo(__DSPOT_c_0);
        Assert.assertEquals(" ", o_advance_mg14__7);
        Assert.assertEquals("Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advancelitString6_mg360() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_advancelitString6__3 = r.consume();
        r.advance();
        char o_advancelitString6__5 = r.consume();
        char o_advancelitString6_mg360__10 = r.current();
        Assert.assertEquals('\uffff', ((char) (o_advancelitString6_mg360__10)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advance_mg15litString106() throws Exception {
        char[] __DSPOT_chars_1 = new char[]{ 'p', 'b', 'L', '[' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_advance_mg15__4 = r.consume();
        r.advance();
        char o_advance_mg15__6 = r.consume();
        String o_advance_mg15__7 = r.consumeToAny(__DSPOT_chars_1);
        Assert.assertEquals("", o_advance_mg15__7);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advancelitString2_add250() throws Exception {
        CharacterReader r = new CharacterReader("One Two khree");
        Assert.assertEquals("One Two khree", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_advancelitString2__3 = r.consume();
        r.advance();
        r.advance();
        char o_advancelitString2__5 = r.consume();
        Assert.assertEquals("Two khree", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advancelitString6_add240() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_advancelitString6__3 = r.consume();
        r.advance();
        char o_advancelitString6__5 = r.consume();
        ((CharacterReader) (r)).isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advance_mg14litString124() throws Exception {
        char __DSPOT_c_0 = 'S';
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_advance_mg14__4 = r.consume();
        r.advance();
        char o_advance_mg14__6 = r.consume();
        String o_advance_mg14__7 = r.consumeTo(__DSPOT_c_0);
        Assert.assertEquals("", o_advance_mg14__7);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advance_add11litString90_mg4984() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_advance_add11__3 = r.consume();
        r.advance();
        char o_advance_add11__5 = r.consume();
        char o_advance_add11__6 = r.consume();
        char o_advance_add11litString90_mg4984__13 = r.current();
        Assert.assertEquals('\uffff', ((char) (o_advance_add11litString90_mg4984__13)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advance_mg13_mg388litString1691() throws Exception {
        char __DSPOT_c_32 = 'Q';
        CharacterReader r = new CharacterReader("one");
        Assert.assertEquals("one", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_advance_mg13__3 = r.consume();
        r.advance();
        r.advance();
        char o_advance_mg13__5 = r.consume();
        String o_advance_mg13_mg388__12 = r.consumeTo(__DSPOT_c_32);
        Assert.assertEquals("", o_advance_mg13_mg388__12);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advance_mg15litString106litChar2142() throws Exception {
        char[] __DSPOT_chars_1 = new char[]{ 'p', ' ', 'L', '[' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        char o_advance_mg15__4 = r.consume();
        r.advance();
        char o_advance_mg15__6 = r.consume();
        String o_advance_mg15__7 = r.consumeToAny(__DSPOT_chars_1);
        Assert.assertEquals("", o_advance_mg15__7);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void advance_mg14_mg383litChar1910() throws Exception {
        char __DSPOT_c_30 = ']';
        char __DSPOT_c_0 = 'T';
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        char o_advance_mg14__4 = r.consume();
        r.advance();
        char o_advance_mg14__6 = r.consume();
        String o_advance_mg14__7 = r.consumeTo(__DSPOT_c_0);
        Assert.assertEquals(" ", o_advance_mg14__7);
        String o_advance_mg14_mg383__15 = r.consumeTo(__DSPOT_c_30);
        Assert.assertEquals("Two Three", o_advance_mg14_mg383__15);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(" ", o_advance_mg14__7);
    }

    @Test(timeout = 10000)
    public void advance_add9_add177litString1050_failAssert0() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            char o_advance_add9__3 = r.consume();
            char o_advance_add9__4 = r.consume();
            r.advance();
            char o_advance_add9__6 = r.consume();
            ((CharacterReader) (r)).toString();
            org.junit.Assert.fail("advance_add9_add177litString1050 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void advance_add9_add177_add2648() throws Exception {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).toString();
        char o_advance_add9__3 = r.consume();
        char o_advance_add9__4 = r.consume();
        r.advance();
        char o_advance_add9__6 = r.consume();
        ((CharacterReader) (r)).toString();
        Assert.assertEquals("Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void consumeToAnylitString38577() throws Exception {
        CharacterReader r = new CharacterReader("Q/}]N6t)v*3Z ");
        Assert.assertEquals("Q/}]N6t)v*3Z ", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitString38577__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("Q/}]N6t)v*3Z ", o_consumeToAnylitString38577__3);
        boolean o_consumeToAnylitString38577__4 = r.matches('&');
        Assert.assertFalse(o_consumeToAnylitString38577__4);
        boolean o_consumeToAnylitString38577__5 = r.matches("&bar;");
        Assert.assertFalse(o_consumeToAnylitString38577__5);
        char o_consumeToAnylitString38577__6 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38577__6)));
        String o_consumeToAnylitString38577__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38577__7);
        char o_consumeToAnylitString38577__8 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38577__8)));
        String o_consumeToAnylitString38577__9 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38577__9);
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("Q/}]N6t)v*3Z ", o_consumeToAnylitString38577__3);
        Assert.assertFalse(o_consumeToAnylitString38577__4);
        Assert.assertFalse(o_consumeToAnylitString38577__5);
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38577__6)));
        Assert.assertEquals("", o_consumeToAnylitString38577__7);
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38577__8)));
    }

    @Test(timeout = 10000)
    public void consumeToAnylitString38578() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitString38578__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38578__3);
        boolean o_consumeToAnylitString38578__4 = r.matches('&');
        Assert.assertFalse(o_consumeToAnylitString38578__4);
        boolean o_consumeToAnylitString38578__5 = r.matches("&bar;");
        Assert.assertFalse(o_consumeToAnylitString38578__5);
        char o_consumeToAnylitString38578__6 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38578__6)));
        String o_consumeToAnylitString38578__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38578__7);
        char o_consumeToAnylitString38578__8 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38578__8)));
        String o_consumeToAnylitString38578__9 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38578__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeToAnylitString38578__3);
        Assert.assertFalse(o_consumeToAnylitString38578__4);
        Assert.assertFalse(o_consumeToAnylitString38578__5);
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38578__6)));
        Assert.assertEquals("", o_consumeToAnylitString38578__7);
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38578__8)));
    }

    @Test(timeout = 10000)
    public void consumeToAnylitChar38605() throws Exception {
        CharacterReader r = new CharacterReader("One &bar; qux");
        Assert.assertEquals("One &bar; qux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitChar38605__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("One ", o_consumeToAnylitChar38605__3);
        boolean o_consumeToAnylitChar38605__4 = r.matches('%');
        Assert.assertFalse(o_consumeToAnylitChar38605__4);
        boolean o_consumeToAnylitChar38605__5 = r.matches("&bar;");
        Assert.assertTrue(o_consumeToAnylitChar38605__5);
        char o_consumeToAnylitChar38605__6 = r.consume();
        Assert.assertEquals('&', ((char) (o_consumeToAnylitChar38605__6)));
        String o_consumeToAnylitChar38605__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("bar", o_consumeToAnylitChar38605__7);
        char o_consumeToAnylitChar38605__8 = r.consume();
        Assert.assertEquals(';', ((char) (o_consumeToAnylitChar38605__8)));
        String o_consumeToAnylitChar38605__9 = r.consumeToAny('&', ';');
        Assert.assertEquals(" qux", o_consumeToAnylitChar38605__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One ", o_consumeToAnylitChar38605__3);
        Assert.assertFalse(o_consumeToAnylitChar38605__4);
        Assert.assertTrue(o_consumeToAnylitChar38605__5);
        Assert.assertEquals('&', ((char) (o_consumeToAnylitChar38605__6)));
        Assert.assertEquals("bar", o_consumeToAnylitChar38605__7);
        Assert.assertEquals(';', ((char) (o_consumeToAnylitChar38605__8)));
    }

    @Test(timeout = 10000)
    public void consumeToAnylitString38579() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitString38579__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("\n", o_consumeToAnylitString38579__3);
        boolean o_consumeToAnylitString38579__4 = r.matches('&');
        Assert.assertFalse(o_consumeToAnylitString38579__4);
        boolean o_consumeToAnylitString38579__5 = r.matches("&bar;");
        Assert.assertFalse(o_consumeToAnylitString38579__5);
        char o_consumeToAnylitString38579__6 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38579__6)));
        String o_consumeToAnylitString38579__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38579__7);
        char o_consumeToAnylitString38579__8 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38579__8)));
        String o_consumeToAnylitString38579__9 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38579__9);
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", o_consumeToAnylitString38579__3);
        Assert.assertFalse(o_consumeToAnylitString38579__4);
        Assert.assertFalse(o_consumeToAnylitString38579__5);
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38579__6)));
        Assert.assertEquals("", o_consumeToAnylitString38579__7);
        Assert.assertEquals('\uffff', ((char) (o_consumeToAnylitString38579__8)));
    }

    @Test(timeout = 10000)
    public void consumeToAnylitString38578_mg42763() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitString38578__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38578__3);
        boolean o_consumeToAnylitString38578__4 = r.matches('&');
        boolean o_consumeToAnylitString38578__5 = r.matches("&bar;");
        char o_consumeToAnylitString38578__6 = r.consume();
        String o_consumeToAnylitString38578__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38578__7);
        char o_consumeToAnylitString38578__8 = r.consume();
        String o_consumeToAnylitString38578__9 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38578__9);
        int o_consumeToAnylitString38578_mg42763__24 = r.pos();
        Assert.assertEquals(2, ((int) (o_consumeToAnylitString38578_mg42763__24)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeToAnylitString38578__3);
        Assert.assertEquals("", o_consumeToAnylitString38578__7);
        Assert.assertEquals("", o_consumeToAnylitString38578__9);
    }

    @Test(timeout = 10000)
    public void consumeToAny_mg38638_mg42830() throws Exception {
        char __DSPOT_c_2374 = 'G';
        CharacterReader r = new CharacterReader("One &bar; qux");
        Assert.assertEquals("One &bar; qux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAny_mg38638__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("One ", o_consumeToAny_mg38638__3);
        boolean o_consumeToAny_mg38638__4 = r.matches('&');
        boolean o_consumeToAny_mg38638__5 = r.matches("&bar;");
        char o_consumeToAny_mg38638__6 = r.consume();
        String o_consumeToAny_mg38638__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("bar", o_consumeToAny_mg38638__7);
        char o_consumeToAny_mg38638__8 = r.consume();
        String o_consumeToAny_mg38638__9 = r.consumeToAny('&', ';');
        Assert.assertEquals(" qux", o_consumeToAny_mg38638__9);
        r.advance();
        String o_consumeToAny_mg38638_mg42830__26 = r.consumeTo(__DSPOT_c_2374);
        Assert.assertEquals("", o_consumeToAny_mg38638_mg42830__26);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One ", o_consumeToAny_mg38638__3);
        Assert.assertEquals("bar", o_consumeToAny_mg38638__7);
        Assert.assertEquals(" qux", o_consumeToAny_mg38638__9);
    }

    @Test(timeout = 10000)
    public void consumeToAnylitChar38624litString39509() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitChar38624__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("\n", o_consumeToAnylitChar38624__3);
        boolean o_consumeToAnylitChar38624__4 = r.matches('&');
        boolean o_consumeToAnylitChar38624__5 = r.matches("&bar;");
        char o_consumeToAnylitChar38624__6 = r.consume();
        String o_consumeToAnylitChar38624__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitChar38624__7);
        char o_consumeToAnylitChar38624__8 = r.consume();
        String o_consumeToAnylitChar38624__9 = r.consumeToAny('\n', ';');
        Assert.assertEquals("", o_consumeToAnylitChar38624__9);
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", o_consumeToAnylitChar38624__3);
        Assert.assertEquals("", o_consumeToAnylitChar38624__7);
    }

    @Test(timeout = 10000)
    public void consumeToAnylitString38577_add42247() throws Exception {
        CharacterReader r = new CharacterReader("Q/}]N6t)v*3Z ");
        Assert.assertEquals("Q/}]N6t)v*3Z ", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitString38577__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("Q/}]N6t)v*3Z ", o_consumeToAnylitString38577__3);
        boolean o_consumeToAnylitString38577__4 = r.matches('&');
        boolean o_consumeToAnylitString38577__5 = r.matches("&bar;");
        char o_consumeToAnylitString38577__6 = r.consume();
        String o_consumeToAnylitString38577__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38577__7);
        char o_consumeToAnylitString38577__8 = r.consume();
        String o_consumeToAnylitString38577__9 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38577__9);
        ((CharacterReader) (r)).isEmpty();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("Q/}]N6t)v*3Z ", o_consumeToAnylitString38577__3);
        Assert.assertEquals("", o_consumeToAnylitString38577__7);
        Assert.assertEquals("", o_consumeToAnylitString38577__9);
    }

    @Test(timeout = 10000)
    public void consumeToAnylitString38574_add42190() throws Exception {
        CharacterReader r = new CharacterReader("One &ba@; qux");
        Assert.assertEquals("One &ba@; qux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitString38574__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("One ", o_consumeToAnylitString38574__3);
        boolean o_consumeToAnylitString38574__4 = r.matches('&');
        boolean o_consumeToAnylitString38574__5 = r.matches("&bar;");
        char o_consumeToAnylitString38574__6 = r.consume();
        String o_consumeToAnylitString38574__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("ba@", o_consumeToAnylitString38574__7);
        char o_consumeToAnylitString38574_add42190__18 = r.consume();
        Assert.assertEquals(';', ((char) (o_consumeToAnylitString38574_add42190__18)));
        char o_consumeToAnylitString38574__8 = r.consume();
        String o_consumeToAnylitString38574__9 = r.consumeToAny('&', ';');
        Assert.assertEquals("qux", o_consumeToAnylitString38574__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One ", o_consumeToAnylitString38574__3);
        Assert.assertEquals("ba@", o_consumeToAnylitString38574__7);
        Assert.assertEquals(';', ((char) (o_consumeToAnylitString38574_add42190__18)));
    }

    @Test(timeout = 10000)
    public void consumeToAnylitString38577null42987_failAssert93() throws Exception {
        try {
            CharacterReader r = new CharacterReader("Q/}]N6t)v*3Z ");
            String o_consumeToAnylitString38577__3 = r.consumeToAny('&', ';');
            boolean o_consumeToAnylitString38577__4 = r.matches('&');
            boolean o_consumeToAnylitString38577__5 = r.matches(null);
            char o_consumeToAnylitString38577__6 = r.consume();
            String o_consumeToAnylitString38577__7 = r.consumeToAny('&', ';');
            char o_consumeToAnylitString38577__8 = r.consume();
            String o_consumeToAnylitString38577__9 = r.consumeToAny('&', ';');
            org.junit.Assert.fail("consumeToAnylitString38577null42987 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeToAnylitChar38625_add42110litString44416() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitChar38625_add42110__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitChar38625_add42110__3);
        String o_consumeToAnylitChar38625__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitChar38625__3);
        boolean o_consumeToAnylitChar38625__4 = r.matches('&');
        boolean o_consumeToAnylitChar38625__5 = r.matches("&bar;");
        char o_consumeToAnylitChar38625__6 = r.consume();
        String o_consumeToAnylitChar38625__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitChar38625__7);
        char o_consumeToAnylitChar38625__8 = r.consume();
        String o_consumeToAnylitChar38625__9 = r.consumeToAny('&', '\u0000');
        Assert.assertEquals("", o_consumeToAnylitChar38625__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeToAnylitChar38625_add42110__3);
        Assert.assertEquals("", o_consumeToAnylitChar38625__3);
        Assert.assertEquals("", o_consumeToAnylitChar38625__7);
    }

    @Test(timeout = 10000)
    public void consumeToAnylitChar38624litString39509_mg52269() throws Exception {
        char __DSPOT_c_2674 = 'x';
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitChar38624__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("\n", o_consumeToAnylitChar38624__3);
        boolean o_consumeToAnylitChar38624__4 = r.matches('&');
        boolean o_consumeToAnylitChar38624__5 = r.matches("&bar;");
        char o_consumeToAnylitChar38624__6 = r.consume();
        String o_consumeToAnylitChar38624__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitChar38624__7);
        char o_consumeToAnylitChar38624__8 = r.consume();
        String o_consumeToAnylitChar38624__9 = r.consumeToAny('\n', ';');
        Assert.assertEquals("", o_consumeToAnylitChar38624__9);
        String o_consumeToAnylitChar38624litString39509_mg52269__25 = r.consumeTo(__DSPOT_c_2674);
        Assert.assertEquals("", o_consumeToAnylitChar38624litString39509_mg52269__25);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", o_consumeToAnylitChar38624__3);
        Assert.assertEquals("", o_consumeToAnylitChar38624__7);
        Assert.assertEquals("", o_consumeToAnylitChar38624__9);
    }

    @Test(timeout = 10000)
    public void consumeToAnylitChar38618litChar40426litString44836() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitChar38618__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("\n", o_consumeToAnylitChar38618__3);
        boolean o_consumeToAnylitChar38618__4 = r.matches('&');
        boolean o_consumeToAnylitChar38618__5 = r.matches("&bar;");
        char o_consumeToAnylitChar38618__6 = r.consume();
        String o_consumeToAnylitChar38618__7 = r.consumeToAny('&', '\n');
        Assert.assertEquals("", o_consumeToAnylitChar38618__7);
        char o_consumeToAnylitChar38618__8 = r.consume();
        String o_consumeToAnylitChar38618__9 = r.consumeToAny('%', ';');
        Assert.assertEquals("", o_consumeToAnylitChar38618__9);
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", o_consumeToAnylitChar38618__3);
        Assert.assertEquals("", o_consumeToAnylitChar38618__7);
    }

    @Test(timeout = 10000)
    public void consumeToAnylitString38577_mg42793_add51078() throws Exception {
        CharacterReader r = new CharacterReader("Q/}]N6t)v*3Z ");
        Assert.assertEquals("Q/}]N6t)v*3Z ", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).toString();
        String o_consumeToAnylitString38577__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("Q/}]N6t)v*3Z ", o_consumeToAnylitString38577__3);
        boolean o_consumeToAnylitString38577__4 = r.matches('&');
        boolean o_consumeToAnylitString38577__5 = r.matches("&bar;");
        char o_consumeToAnylitString38577__6 = r.consume();
        String o_consumeToAnylitString38577__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38577__7);
        char o_consumeToAnylitString38577__8 = r.consume();
        String o_consumeToAnylitString38577__9 = r.consumeToAny('&', ';');
        Assert.assertEquals("", o_consumeToAnylitString38577__9);
        int o_consumeToAnylitString38577_mg42793__24 = r.pos();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("Q/}]N6t)v*3Z ", o_consumeToAnylitString38577__3);
        Assert.assertEquals("", o_consumeToAnylitString38577__7);
        Assert.assertEquals("", o_consumeToAnylitString38577__9);
    }

    @Test(timeout = 10000)
    public void consumeToAnylitChar38619litString39081_add51063() throws Exception {
        CharacterReader r = new CharacterReader("One &bar; qux");
        Assert.assertEquals("One &bar; qux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeToAnylitChar38619__3 = r.consumeToAny('&', ';');
        Assert.assertEquals("One ", o_consumeToAnylitChar38619__3);
        boolean o_consumeToAnylitChar38619__4 = r.matches('&');
        boolean o_consumeToAnylitChar38619__5 = r.matches("Hbar;");
        char o_consumeToAnylitChar38619__6 = r.consume();
        String o_consumeToAnylitChar38619__7 = r.consumeToAny('&', ';');
        Assert.assertEquals("bar", o_consumeToAnylitChar38619__7);
        char o_consumeToAnylitChar38619litString39081_add51063__18 = r.consume();
        Assert.assertEquals(';', ((char) (o_consumeToAnylitChar38619litString39081_add51063__18)));
        char o_consumeToAnylitChar38619__8 = r.consume();
        String o_consumeToAnylitChar38619__9 = r.consumeToAny('\u0000', ';');
        Assert.assertEquals("qux", o_consumeToAnylitChar38619__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One ", o_consumeToAnylitChar38619__3);
        Assert.assertEquals("bar", o_consumeToAnylitChar38619__7);
        Assert.assertEquals(';', ((char) (o_consumeToAnylitChar38619litString39081_add51063__18)));
    }

    @Test(timeout = 10000)
    public void consumeToAnylitString38578null42985_failAssert94litChar48540() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            Assert.assertEquals("", ((CharacterReader) (r)).toString());
            Assert.assertTrue(((CharacterReader) (r)).isEmpty());
            String o_consumeToAnylitString38578__3 = r.consumeToAny('&', ':');
            Assert.assertEquals("", o_consumeToAnylitString38578__3);
            boolean o_consumeToAnylitString38578__4 = r.matches('&');
            boolean o_consumeToAnylitString38578__5 = r.matches(null);
            char o_consumeToAnylitString38578__6 = r.consume();
            String o_consumeToAnylitString38578__7 = r.consumeToAny('&', ';');
            char o_consumeToAnylitString38578__8 = r.consume();
            String o_consumeToAnylitString38578__9 = r.consumeToAny('&', ';');
            org.junit.Assert.fail("consumeToAnylitString38578null42985 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString26525() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequencelitString26525__3 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26525__3);
        String o_consumeLetterSequencelitString26525__4 = r.consumeTo("bar;");
        Assert.assertEquals("", o_consumeLetterSequencelitString26525__4);
        String o_consumeLetterSequencelitString26525__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26525__5);
        String o_consumeLetterSequencelitString26525__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequencelitString26525__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterSequencelitString26525__3);
        Assert.assertEquals("", o_consumeLetterSequencelitString26525__4);
        Assert.assertEquals("", o_consumeLetterSequencelitString26525__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequence_add26537() throws Exception {
        CharacterReader r = new CharacterReader("One &bar; qux");
        Assert.assertEquals("One &bar; qux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequence_add26537__3 = r.consumeLetterSequence();
        Assert.assertEquals("One", o_consumeLetterSequence_add26537__3);
        String o_consumeLetterSequence_add26537__4 = r.consumeTo("bar;");
        Assert.assertEquals(" &", o_consumeLetterSequence_add26537__4);
        String o_consumeLetterSequence_add26537__5 = r.consumeTo("bar;");
        Assert.assertEquals("", o_consumeLetterSequence_add26537__5);
        String o_consumeLetterSequence_add26537__6 = r.consumeLetterSequence();
        Assert.assertEquals("bar", o_consumeLetterSequence_add26537__6);
        String o_consumeLetterSequence_add26537__7 = r.consumeToEnd();
        Assert.assertEquals("; qux", o_consumeLetterSequence_add26537__7);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One", o_consumeLetterSequence_add26537__3);
        Assert.assertEquals(" &", o_consumeLetterSequence_add26537__4);
        Assert.assertEquals("", o_consumeLetterSequence_add26537__5);
        Assert.assertEquals("bar", o_consumeLetterSequence_add26537__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString26527() throws Exception {
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequencelitString26527__3 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26527__3);
        String o_consumeLetterSequencelitString26527__4 = r.consumeTo("bar;");
        Assert.assertEquals(":", o_consumeLetterSequencelitString26527__4);
        String o_consumeLetterSequencelitString26527__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26527__5);
        String o_consumeLetterSequencelitString26527__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequencelitString26527__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterSequencelitString26527__3);
        Assert.assertEquals(":", o_consumeLetterSequencelitString26527__4);
        Assert.assertEquals("", o_consumeLetterSequencelitString26527__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString26535() throws Exception {
        CharacterReader r = new CharacterReader("One &bar; qux");
        Assert.assertEquals("One &bar; qux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequencelitString26535__3 = r.consumeLetterSequence();
        Assert.assertEquals("One", o_consumeLetterSequencelitString26535__3);
        String o_consumeLetterSequencelitString26535__4 = r.consumeTo(":");
        Assert.assertEquals(" &bar; qux", o_consumeLetterSequencelitString26535__4);
        String o_consumeLetterSequencelitString26535__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26535__5);
        String o_consumeLetterSequencelitString26535__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequencelitString26535__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One", o_consumeLetterSequencelitString26535__3);
        Assert.assertEquals(" &bar; qux", o_consumeLetterSequencelitString26535__4);
        Assert.assertEquals("", o_consumeLetterSequencelitString26535__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequence_mg26544litString26779() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequence_mg26544__3 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26544__3);
        String o_consumeLetterSequence_mg26544__4 = r.consumeTo("bar;");
        Assert.assertEquals("", o_consumeLetterSequence_mg26544__4);
        String o_consumeLetterSequence_mg26544__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26544__5);
        String o_consumeLetterSequence_mg26544__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequence_mg26544__6);
        int o_consumeLetterSequence_mg26544__7 = r.pos();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterSequence_mg26544__3);
        Assert.assertEquals("", o_consumeLetterSequence_mg26544__4);
        Assert.assertEquals("", o_consumeLetterSequence_mg26544__5);
        Assert.assertEquals("", o_consumeLetterSequence_mg26544__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString26526litString26732() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequencelitString26526__3 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__3);
        String o_consumeLetterSequencelitString26526__4 = r.consumeTo("\n");
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__4);
        String o_consumeLetterSequencelitString26526__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__5);
        String o_consumeLetterSequencelitString26526__6 = r.consumeToEnd();
        Assert.assertEquals("\n", o_consumeLetterSequencelitString26526__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__3);
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__4);
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString26526litString26728() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequencelitString26526__3 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__3);
        String o_consumeLetterSequencelitString26526__4 = r.consumeTo("b&ar;");
        Assert.assertEquals("\n", o_consumeLetterSequencelitString26526__4);
        String o_consumeLetterSequencelitString26526__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__5);
        String o_consumeLetterSequencelitString26526__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__3);
        Assert.assertEquals("\n", o_consumeLetterSequencelitString26526__4);
        Assert.assertEquals("", o_consumeLetterSequencelitString26526__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString26524litString26757() throws Exception {
        CharacterReader r = new CharacterReader("#(eL|i;Gcgn+W");
        Assert.assertEquals("#(eL|i;Gcgn+W", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequencelitString26524__3 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26524__3);
        String o_consumeLetterSequencelitString26524__4 = r.consumeTo(":");
        Assert.assertEquals("#(eL|i;Gcgn+W", o_consumeLetterSequencelitString26524__4);
        String o_consumeLetterSequencelitString26524__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26524__5);
        String o_consumeLetterSequencelitString26524__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequencelitString26524__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterSequencelitString26524__3);
        Assert.assertEquals("#(eL|i;Gcgn+W", o_consumeLetterSequencelitString26524__4);
        Assert.assertEquals("", o_consumeLetterSequencelitString26524__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequence_mg26543litString26794() throws Exception {
        CharacterReader r = new CharacterReader("jnAM{vpovoB/#");
        Assert.assertEquals("jnAM{vpovoB/#", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequence_mg26543__3 = r.consumeLetterSequence();
        Assert.assertEquals("jnAM", o_consumeLetterSequence_mg26543__3);
        String o_consumeLetterSequence_mg26543__4 = r.consumeTo("bar;");
        Assert.assertEquals("{vpovoB/#", o_consumeLetterSequence_mg26543__4);
        String o_consumeLetterSequence_mg26543__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26543__5);
        String o_consumeLetterSequence_mg26543__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequence_mg26543__6);
        char o_consumeLetterSequence_mg26543__7 = r.current();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("jnAM", o_consumeLetterSequence_mg26543__3);
        Assert.assertEquals("{vpovoB/#", o_consumeLetterSequence_mg26543__4);
        Assert.assertEquals("", o_consumeLetterSequence_mg26543__5);
        Assert.assertEquals("", o_consumeLetterSequence_mg26543__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequence_mg26541_add27022litString28411() throws Exception {
        char __DSPOT_c_1392 = '7';
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequence_mg26541__4 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__4);
        String o_consumeLetterSequence_mg26541__5 = r.consumeTo("bar;");
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__5);
        String o_consumeLetterSequence_mg26541_add27022__10 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541_add27022__10);
        String o_consumeLetterSequence_mg26541__6 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__6);
        String o_consumeLetterSequence_mg26541__7 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__7);
        String o_consumeLetterSequence_mg26541__8 = r.consumeTo(__DSPOT_c_1392);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__8);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__4);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__5);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541_add27022__10);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__6);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__7);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequence_mg26541litString26765_mg31611() throws Exception {
        char __DSPOT_c_1392 = '7';
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequence_mg26541__4 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__4);
        String o_consumeLetterSequence_mg26541__5 = r.consumeTo("bar;");
        Assert.assertEquals(":", o_consumeLetterSequence_mg26541__5);
        String o_consumeLetterSequence_mg26541__6 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__6);
        String o_consumeLetterSequence_mg26541__7 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__7);
        String o_consumeLetterSequence_mg26541__8 = r.consumeTo(__DSPOT_c_1392);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__8);
        r.advance();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__4);
        Assert.assertEquals(":", o_consumeLetterSequence_mg26541__5);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__6);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__7);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__8);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequence_mg26541litString26765_mg31615() throws Exception {
        char __DSPOT_c_1392 = '7';
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequence_mg26541__4 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__4);
        String o_consumeLetterSequence_mg26541__5 = r.consumeTo("bar;");
        Assert.assertEquals(":", o_consumeLetterSequence_mg26541__5);
        String o_consumeLetterSequence_mg26541__6 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__6);
        String o_consumeLetterSequence_mg26541__7 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__7);
        String o_consumeLetterSequence_mg26541__8 = r.consumeTo(__DSPOT_c_1392);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__8);
        int o_consumeLetterSequence_mg26541litString26765_mg31615__19 = r.pos();
        Assert.assertEquals(1, ((int) (o_consumeLetterSequence_mg26541litString26765_mg31615__19)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__4);
        Assert.assertEquals(":", o_consumeLetterSequence_mg26541__5);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__6);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__7);
        Assert.assertEquals("", o_consumeLetterSequence_mg26541__8);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString26535_mg27121_add30818() throws Exception {
        CharacterReader r = new CharacterReader("One &bar; qux");
        Assert.assertEquals("One &bar; qux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequencelitString26535_mg27121_add30818__3 = r.consumeLetterSequence();
        Assert.assertEquals("One", o_consumeLetterSequencelitString26535_mg27121_add30818__3);
        String o_consumeLetterSequencelitString26535__3 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26535__3);
        String o_consumeLetterSequencelitString26535__4 = r.consumeTo(":");
        Assert.assertEquals(" &bar; qux", o_consumeLetterSequencelitString26535__4);
        String o_consumeLetterSequencelitString26535__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26535__5);
        String o_consumeLetterSequencelitString26535__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterSequencelitString26535__6);
        char o_consumeLetterSequencelitString26535_mg27121__15 = r.current();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One", o_consumeLetterSequencelitString26535_mg27121_add30818__3);
        Assert.assertEquals("", o_consumeLetterSequencelitString26535__3);
        Assert.assertEquals(" &bar; qux", o_consumeLetterSequencelitString26535__4);
        Assert.assertEquals("", o_consumeLetterSequencelitString26535__5);
        Assert.assertEquals("", o_consumeLetterSequencelitString26535__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencenull26545_failAssert21_mg27193_add30095() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One &bar; qux");
            Assert.assertEquals("One &bar; qux", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            String o_consumeLetterSequencenull26545_failAssert21_mg27193__5 = r.consumeLetterSequence();
            Assert.assertEquals("One", o_consumeLetterSequencenull26545_failAssert21_mg27193__5);
            r.consumeTo(null);
            r.consumeTo(null);
            r.consumeLetterSequence();
            r.consumeToEnd();
            org.junit.Assert.fail("consumeLetterSequencenull26545 should have thrown NullPointerException");
            r.advance();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void consumeLetterSequencelitString26523_add26902_add29899() throws Exception {
        CharacterReader r = new CharacterReader("One &bar; ux");
        Assert.assertEquals("One &bar; ux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterSequencelitString26523__3 = r.consumeLetterSequence();
        Assert.assertEquals("One", o_consumeLetterSequencelitString26523__3);
        String o_consumeLetterSequencelitString26523_add26902_add29899__6 = r.consumeTo("bar;");
        Assert.assertEquals(" &", o_consumeLetterSequencelitString26523_add26902_add29899__6);
        String o_consumeLetterSequencelitString26523__4 = r.consumeTo("bar;");
        Assert.assertEquals("", o_consumeLetterSequencelitString26523__4);
        String o_consumeLetterSequencelitString26523_add26902__9 = r.consumeLetterSequence();
        Assert.assertEquals("bar", o_consumeLetterSequencelitString26523_add26902__9);
        String o_consumeLetterSequencelitString26523__5 = r.consumeLetterSequence();
        Assert.assertEquals("", o_consumeLetterSequencelitString26523__5);
        String o_consumeLetterSequencelitString26523__6 = r.consumeToEnd();
        Assert.assertEquals("; ux", o_consumeLetterSequencelitString26523__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One", o_consumeLetterSequencelitString26523__3);
        Assert.assertEquals(" &", o_consumeLetterSequencelitString26523_add26902_add29899__6);
        Assert.assertEquals("", o_consumeLetterSequencelitString26523__4);
        Assert.assertEquals("bar", o_consumeLetterSequencelitString26523_add26902__9);
        Assert.assertEquals("", o_consumeLetterSequencelitString26523__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterSequence_mg26544litString26779null32567_failAssert64() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            String o_consumeLetterSequence_mg26544__3 = r.consumeLetterSequence();
            String o_consumeLetterSequence_mg26544__4 = r.consumeTo(null);
            String o_consumeLetterSequence_mg26544__5 = r.consumeLetterSequence();
            String o_consumeLetterSequence_mg26544__6 = r.consumeToEnd();
            int o_consumeLetterSequence_mg26544__7 = r.pos();
            org.junit.Assert.fail("consumeLetterSequence_mg26544litString26779null32567 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33143() throws Exception {
        CharacterReader r = new CharacterReader("{uj5b&)qJ&|U8I|)X<;");
        Assert.assertEquals("{uj5b&)qJ&|U8I|)X<;", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequencelitString33143__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33143__3);
        char o_consumeLetterThenDigitSequencelitString33143__4 = r.consume();
        Assert.assertEquals('{', ((char) (o_consumeLetterThenDigitSequencelitString33143__4)));
        String o_consumeLetterThenDigitSequencelitString33143__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("uj5", o_consumeLetterThenDigitSequencelitString33143__5);
        String o_consumeLetterThenDigitSequencelitString33143__6 = r.consumeToEnd();
        Assert.assertEquals("b&)qJ&|U8I|)X<;", o_consumeLetterThenDigitSequencelitString33143__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33143__3);
        Assert.assertEquals('{', ((char) (o_consumeLetterThenDigitSequencelitString33143__4)));
        Assert.assertEquals("uj5", o_consumeLetterThenDigitSequencelitString33143__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33146() throws Exception {
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequencelitString33146__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__3);
        char o_consumeLetterThenDigitSequencelitString33146__4 = r.consume();
        Assert.assertEquals(':', ((char) (o_consumeLetterThenDigitSequencelitString33146__4)));
        String o_consumeLetterThenDigitSequencelitString33146__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__5);
        String o_consumeLetterThenDigitSequencelitString33146__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__3);
        Assert.assertEquals(':', ((char) (o_consumeLetterThenDigitSequencelitString33146__4)));
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33139() throws Exception {
        CharacterReader r = new CharacterReader("hree");
        Assert.assertEquals("hree", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequencelitString33139__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("hree", o_consumeLetterThenDigitSequencelitString33139__3);
        char o_consumeLetterThenDigitSequencelitString33139__4 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_consumeLetterThenDigitSequencelitString33139__4)));
        String o_consumeLetterThenDigitSequencelitString33139__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33139__5);
        String o_consumeLetterThenDigitSequencelitString33139__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33139__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("hree", o_consumeLetterThenDigitSequencelitString33139__3);
        Assert.assertEquals('\uffff', ((char) (o_consumeLetterThenDigitSequencelitString33139__4)));
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33139__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33146_add33377() throws Exception {
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequencelitString33146_add33377__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146_add33377__3);
        String o_consumeLetterThenDigitSequencelitString33146__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__3);
        char o_consumeLetterThenDigitSequencelitString33146__4 = r.consume();
        String o_consumeLetterThenDigitSequencelitString33146__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__5);
        String o_consumeLetterThenDigitSequencelitString33146__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146_add33377__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33146__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_add33150litString33223() throws Exception {
        CharacterReader r = new CharacterReader("CHOKE");
        Assert.assertEquals("CHOKE", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequence_add33150__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("CHOKE", o_consumeLetterThenDigitSequence_add33150__3);
        char o_consumeLetterThenDigitSequence_add33150__4 = r.consume();
        String o_consumeLetterThenDigitSequence_add33150__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150__5);
        String o_consumeLetterThenDigitSequence_add33150__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150__6);
        String o_consumeLetterThenDigitSequence_add33150__7 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150__7);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("CHOKE", o_consumeLetterThenDigitSequence_add33150__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150__5);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33143_add33341() throws Exception {
        CharacterReader r = new CharacterReader("{uj5b&)qJ&|U8I|)X<;");
        Assert.assertEquals("{uj5b&)qJ&|U8I|)X<;", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequencelitString33143__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33143__3);
        char o_consumeLetterThenDigitSequencelitString33143__4 = r.consume();
        String o_consumeLetterThenDigitSequencelitString33143__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("uj5", o_consumeLetterThenDigitSequencelitString33143__5);
        String o_consumeLetterThenDigitSequencelitString33143__6 = r.consumeToEnd();
        Assert.assertEquals("b&)qJ&|U8I|)X<;", o_consumeLetterThenDigitSequencelitString33143__6);
        ((CharacterReader) (r)).toString();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33143__3);
        Assert.assertEquals("uj5", o_consumeLetterThenDigitSequencelitString33143__5);
        Assert.assertEquals("b&)qJ&|U8I|)X<;", o_consumeLetterThenDigitSequencelitString33143__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33140_add33361() throws Exception {
        CharacterReader r = new CharacterReader("One1[ Two &bar; qux");
        Assert.assertEquals("One1[ Two &bar; qux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequencelitString33140_add33361__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("One1", o_consumeLetterThenDigitSequencelitString33140_add33361__3);
        String o_consumeLetterThenDigitSequencelitString33140__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33140__3);
        char o_consumeLetterThenDigitSequencelitString33140__4 = r.consume();
        String o_consumeLetterThenDigitSequencelitString33140__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33140__5);
        String o_consumeLetterThenDigitSequencelitString33140__6 = r.consumeToEnd();
        Assert.assertEquals(" Two &bar; qux", o_consumeLetterThenDigitSequencelitString33140__6);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One1", o_consumeLetterThenDigitSequencelitString33140_add33361__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33140__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33140__5);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_mg33155litString33245() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequence_mg33155__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__3);
        char o_consumeLetterThenDigitSequence_mg33155__4 = r.consume();
        String o_consumeLetterThenDigitSequence_mg33155__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__5);
        String o_consumeLetterThenDigitSequence_mg33155__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__6);
        int o_consumeLetterThenDigitSequence_mg33155__7 = r.pos();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__5);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_mg33155litString33244() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequence_mg33155__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__3);
        char o_consumeLetterThenDigitSequence_mg33155__4 = r.consume();
        String o_consumeLetterThenDigitSequence_mg33155__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__5);
        String o_consumeLetterThenDigitSequence_mg33155__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__6);
        int o_consumeLetterThenDigitSequence_mg33155__7 = r.pos();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__5);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_mg33155litString33243() throws Exception {
        CharacterReader r = new CharacterReader("?U`8fVJ3B^KETq8HHB;");
        Assert.assertEquals("?U`8fVJ3B^KETq8HHB;", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequence_mg33155__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__3);
        char o_consumeLetterThenDigitSequence_mg33155__4 = r.consume();
        String o_consumeLetterThenDigitSequence_mg33155__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("U", o_consumeLetterThenDigitSequence_mg33155__5);
        String o_consumeLetterThenDigitSequence_mg33155__6 = r.consumeToEnd();
        Assert.assertEquals("`8fVJ3B^KETq8HHB;", o_consumeLetterThenDigitSequence_mg33155__6);
        int o_consumeLetterThenDigitSequence_mg33155__7 = r.pos();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33155__3);
        Assert.assertEquals("U", o_consumeLetterThenDigitSequence_mg33155__5);
        Assert.assertEquals("`8fVJ3B^KETq8HHB;", o_consumeLetterThenDigitSequence_mg33155__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33144_mg33475() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequencelitString33144__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__3);
        char o_consumeLetterThenDigitSequencelitString33144__4 = r.consume();
        String o_consumeLetterThenDigitSequencelitString33144__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__5);
        String o_consumeLetterThenDigitSequencelitString33144__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__6);
        int o_consumeLetterThenDigitSequencelitString33144_mg33475__15 = r.pos();
        Assert.assertEquals(1, ((int) (o_consumeLetterThenDigitSequencelitString33144_mg33475__15)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__5);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_add33148litString33222_add35830() throws Exception {
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequence_add33148__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33148__3);
        char o_consumeLetterThenDigitSequence_add33148__4 = r.consume();
        char o_consumeLetterThenDigitSequence_add33148__5 = r.consume();
        String o_consumeLetterThenDigitSequence_add33148litString33222_add35830__12 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33148litString33222_add35830__12);
        String o_consumeLetterThenDigitSequence_add33148__6 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33148__6);
        String o_consumeLetterThenDigitSequence_add33148__7 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33148__7);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33148__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33148litString33222_add35830__12);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33148__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_add33150_mg33448litString34592() throws Exception {
        char[] __DSPOT_chars_1853 = new char[]{ 'h', 'D', 'P', 'B' };
        CharacterReader r = new CharacterReader("4||/H$#w(W,^+8,u?E#");
        Assert.assertEquals("4||/H$#w(W,^+8,u?E#", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequence_add33150__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("4", o_consumeLetterThenDigitSequence_add33150__3);
        char o_consumeLetterThenDigitSequence_add33150__4 = r.consume();
        String o_consumeLetterThenDigitSequence_add33150__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150__5);
        String o_consumeLetterThenDigitSequence_add33150__6 = r.consumeToEnd();
        Assert.assertEquals("|/H$#w(W,^+8,u?E#", o_consumeLetterThenDigitSequence_add33150__6);
        String o_consumeLetterThenDigitSequence_add33150__7 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150__7);
        String o_consumeLetterThenDigitSequence_add33150_mg33448__19 = r.consumeToAny(__DSPOT_chars_1853);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150_mg33448__19);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("4", o_consumeLetterThenDigitSequence_add33150__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150__5);
        Assert.assertEquals("|/H$#w(W,^+8,u?E#", o_consumeLetterThenDigitSequence_add33150__6);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33150__7);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_mg33153litString33251litNum34748() throws Exception {
        char[] __DSPOT_chars_1847 = new char[0];
        CharacterReader r = new CharacterReader("[YU#(PJ`Fx,+N8!rM5U");
        Assert.assertEquals("[YU#(PJ`Fx,+N8!rM5U", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequence_mg33153__4 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33153__4);
        char o_consumeLetterThenDigitSequence_mg33153__5 = r.consume();
        String o_consumeLetterThenDigitSequence_mg33153__6 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("YU", o_consumeLetterThenDigitSequence_mg33153__6);
        String o_consumeLetterThenDigitSequence_mg33153__7 = r.consumeToEnd();
        Assert.assertEquals("#(PJ`Fx,+N8!rM5U", o_consumeLetterThenDigitSequence_mg33153__7);
        String o_consumeLetterThenDigitSequence_mg33153__8 = r.consumeToAny(__DSPOT_chars_1847);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33153__8);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_mg33153__4);
        Assert.assertEquals("YU", o_consumeLetterThenDigitSequence_mg33153__6);
        Assert.assertEquals("#(PJ`Fx,+N8!rM5U", o_consumeLetterThenDigitSequence_mg33153__7);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33144_add33357_add36759() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequencelitString33144__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__3);
        char o_consumeLetterThenDigitSequencelitString33144__4 = r.consume();
        String o_consumeLetterThenDigitSequencelitString33144__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__5);
        String o_consumeLetterThenDigitSequencelitString33144__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__6);
        ((CharacterReader) (r)).toString();
        ((CharacterReader) (r)).isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__5);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33144_add33351_mg37240() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).toString();
        String o_consumeLetterThenDigitSequencelitString33144__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__3);
        char o_consumeLetterThenDigitSequencelitString33144__4 = r.consume();
        String o_consumeLetterThenDigitSequencelitString33144__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__5);
        String o_consumeLetterThenDigitSequencelitString33144__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__6);
        int o_consumeLetterThenDigitSequencelitString33144_add33351_mg37240__16 = r.pos();
        Assert.assertEquals(1, ((int) (o_consumeLetterThenDigitSequencelitString33144_add33351_mg37240__16)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__5);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33144__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_add33149_mg33451litString34556() throws Exception {
        CharacterReader r = new CharacterReader(" &");
        Assert.assertEquals(" &", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequence_add33149__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33149__3);
        char o_consumeLetterThenDigitSequence_add33149__4 = r.consume();
        String o_consumeLetterThenDigitSequence_add33149__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33149__5);
        String o_consumeLetterThenDigitSequence_add33149__6 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33149__6);
        String o_consumeLetterThenDigitSequence_add33149__7 = r.consumeToEnd();
        Assert.assertEquals("&", o_consumeLetterThenDigitSequence_add33149__7);
        r.advance();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33149__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33149__5);
        Assert.assertEquals("", o_consumeLetterThenDigitSequence_add33149__6);
        Assert.assertEquals("&", o_consumeLetterThenDigitSequence_add33149__7);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequencelitString33139_mg33485_add35459() throws Exception {
        CharacterReader r = new CharacterReader("hree");
        Assert.assertEquals("hree", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequencelitString33139__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("hree", o_consumeLetterThenDigitSequencelitString33139__3);
        char o_consumeLetterThenDigitSequencelitString33139__4 = r.consume();
        String o_consumeLetterThenDigitSequencelitString33139_mg33485_add35459__9 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33139_mg33485_add35459__9);
        String o_consumeLetterThenDigitSequencelitString33139__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33139__5);
        String o_consumeLetterThenDigitSequencelitString33139__6 = r.consumeToEnd();
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33139__6);
        int o_consumeLetterThenDigitSequencelitString33139_mg33485__15 = r.pos();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("hree", o_consumeLetterThenDigitSequencelitString33139__3);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33139_mg33485_add35459__9);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33139__5);
        Assert.assertEquals("", o_consumeLetterThenDigitSequencelitString33139__6);
    }

    @Test(timeout = 10000)
    public void consumeLetterThenDigitSequence_mg33151_add33425litString34328() throws Exception {
        CharacterReader r = new CharacterReader("One12 Tw{o &bar; qux");
        Assert.assertEquals("One12 Tw{o &bar; qux", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String o_consumeLetterThenDigitSequence_mg33151__3 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_mg33151__3);
        char o_consumeLetterThenDigitSequence_mg33151__4 = r.consume();
        String o_consumeLetterThenDigitSequence_mg33151__5 = r.consumeLetterThenDigitSequence();
        Assert.assertEquals("Tw", o_consumeLetterThenDigitSequence_mg33151__5);
        String o_consumeLetterThenDigitSequence_mg33151__6 = r.consumeToEnd();
        Assert.assertEquals("{o &bar; qux", o_consumeLetterThenDigitSequence_mg33151__6);
        r.advance();
        ((CharacterReader) (r)).isEmpty();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One12", o_consumeLetterThenDigitSequence_mg33151__3);
        Assert.assertEquals("Tw", o_consumeLetterThenDigitSequence_mg33151__5);
        Assert.assertEquals("{o &bar; qux", o_consumeLetterThenDigitSequence_mg33151__6);
    }

    @Test(timeout = 10000)
    public void matcheslitString110526() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110526__3 = r.matches('O');
        Assert.assertFalse(o_matcheslitString110526__3);
        boolean o_matcheslitString110526__4 = r.matches("One Two Three");
        Assert.assertFalse(o_matcheslitString110526__4);
        boolean o_matcheslitString110526__5 = r.matches("One");
        Assert.assertFalse(o_matcheslitString110526__5);
        boolean o_matcheslitString110526__6 = r.matches("one");
        Assert.assertFalse(o_matcheslitString110526__6);
        char o_matcheslitString110526__7 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_matcheslitString110526__7)));
        boolean o_matcheslitString110526__8 = r.matches("One");
        Assert.assertFalse(o_matcheslitString110526__8);
        boolean o_matcheslitString110526__9 = r.matches("ne Two Three");
        Assert.assertFalse(o_matcheslitString110526__9);
        boolean o_matcheslitString110526__10 = r.matches("ne Two Three Four");
        Assert.assertFalse(o_matcheslitString110526__10);
        String o_matcheslitString110526__11 = r.consumeToEnd();
        Assert.assertEquals("", o_matcheslitString110526__11);
        boolean o_matcheslitString110526__12 = r.matches("ne");
        Assert.assertFalse(o_matcheslitString110526__12);
        r.isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_matcheslitString110526__3);
        Assert.assertFalse(o_matcheslitString110526__4);
        Assert.assertFalse(o_matcheslitString110526__5);
        Assert.assertFalse(o_matcheslitString110526__6);
        Assert.assertEquals('\uffff', ((char) (o_matcheslitString110526__7)));
        Assert.assertFalse(o_matcheslitString110526__8);
        Assert.assertFalse(o_matcheslitString110526__9);
        Assert.assertFalse(o_matcheslitString110526__10);
        Assert.assertEquals("", o_matcheslitString110526__11);
        Assert.assertFalse(o_matcheslitString110526__12);
    }

    @Test(timeout = 10000)
    public void matcheslitString110521() throws Exception {
        CharacterReader r = new CharacterReader("<!");
        Assert.assertEquals("<!", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110521__3 = r.matches('O');
        Assert.assertFalse(o_matcheslitString110521__3);
        boolean o_matcheslitString110521__4 = r.matches("One Two Three");
        Assert.assertFalse(o_matcheslitString110521__4);
        boolean o_matcheslitString110521__5 = r.matches("One");
        Assert.assertFalse(o_matcheslitString110521__5);
        boolean o_matcheslitString110521__6 = r.matches("one");
        Assert.assertFalse(o_matcheslitString110521__6);
        char o_matcheslitString110521__7 = r.consume();
        Assert.assertEquals('<', ((char) (o_matcheslitString110521__7)));
        boolean o_matcheslitString110521__8 = r.matches("One");
        Assert.assertFalse(o_matcheslitString110521__8);
        boolean o_matcheslitString110521__9 = r.matches("ne Two Three");
        Assert.assertFalse(o_matcheslitString110521__9);
        boolean o_matcheslitString110521__10 = r.matches("ne Two Three Four");
        Assert.assertFalse(o_matcheslitString110521__10);
        String o_matcheslitString110521__11 = r.consumeToEnd();
        Assert.assertEquals("!", o_matcheslitString110521__11);
        boolean o_matcheslitString110521__12 = r.matches("ne");
        Assert.assertFalse(o_matcheslitString110521__12);
        r.isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_matcheslitString110521__3);
        Assert.assertFalse(o_matcheslitString110521__4);
        Assert.assertFalse(o_matcheslitString110521__5);
        Assert.assertFalse(o_matcheslitString110521__6);
        Assert.assertEquals('<', ((char) (o_matcheslitString110521__7)));
        Assert.assertFalse(o_matcheslitString110521__8);
        Assert.assertFalse(o_matcheslitString110521__9);
        Assert.assertFalse(o_matcheslitString110521__10);
        Assert.assertEquals("!", o_matcheslitString110521__11);
        Assert.assertFalse(o_matcheslitString110521__12);
    }

    @Test(timeout = 10000)
    public void matcheslitString110527() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110527__3 = r.matches('O');
        Assert.assertFalse(o_matcheslitString110527__3);
        boolean o_matcheslitString110527__4 = r.matches("One Two Three");
        Assert.assertFalse(o_matcheslitString110527__4);
        boolean o_matcheslitString110527__5 = r.matches("One");
        Assert.assertFalse(o_matcheslitString110527__5);
        boolean o_matcheslitString110527__6 = r.matches("one");
        Assert.assertFalse(o_matcheslitString110527__6);
        char o_matcheslitString110527__7 = r.consume();
        Assert.assertEquals('\n', ((char) (o_matcheslitString110527__7)));
        boolean o_matcheslitString110527__8 = r.matches("One");
        Assert.assertFalse(o_matcheslitString110527__8);
        boolean o_matcheslitString110527__9 = r.matches("ne Two Three");
        Assert.assertFalse(o_matcheslitString110527__9);
        boolean o_matcheslitString110527__10 = r.matches("ne Two Three Four");
        Assert.assertFalse(o_matcheslitString110527__10);
        String o_matcheslitString110527__11 = r.consumeToEnd();
        Assert.assertEquals("", o_matcheslitString110527__11);
        boolean o_matcheslitString110527__12 = r.matches("ne");
        Assert.assertFalse(o_matcheslitString110527__12);
        r.isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_matcheslitString110527__3);
        Assert.assertFalse(o_matcheslitString110527__4);
        Assert.assertFalse(o_matcheslitString110527__5);
        Assert.assertFalse(o_matcheslitString110527__6);
        Assert.assertEquals('\n', ((char) (o_matcheslitString110527__7)));
        Assert.assertFalse(o_matcheslitString110527__8);
        Assert.assertFalse(o_matcheslitString110527__9);
        Assert.assertFalse(o_matcheslitString110527__10);
        Assert.assertEquals("", o_matcheslitString110527__11);
        Assert.assertFalse(o_matcheslitString110527__12);
    }

    @Test(timeout = 10000)
    public void matcheslitString110521litString113569() throws Exception {
        CharacterReader r = new CharacterReader("<!");
        Assert.assertEquals("<!", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110521__3 = r.matches('O');
        boolean o_matcheslitString110521__4 = r.matches("One Two Three");
        boolean o_matcheslitString110521__5 = r.matches("One");
        boolean o_matcheslitString110521__6 = r.matches("one");
        char o_matcheslitString110521__7 = r.consume();
        boolean o_matcheslitString110521__8 = r.matches("One");
        boolean o_matcheslitString110521__9 = r.matches("ne Two Thee");
        boolean o_matcheslitString110521__10 = r.matches("ne Two Three Four");
        String o_matcheslitString110521__11 = r.consumeToEnd();
        Assert.assertEquals("!", o_matcheslitString110521__11);
        boolean o_matcheslitString110521__12 = r.matches("ne");
        r.isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("!", o_matcheslitString110521__11);
    }

    @Test(timeout = 10000)
    public void matcheslitString110526_mg116282() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110526__3 = r.matches('O');
        boolean o_matcheslitString110526__4 = r.matches("One Two Three");
        boolean o_matcheslitString110526__5 = r.matches("One");
        boolean o_matcheslitString110526__6 = r.matches("one");
        char o_matcheslitString110526__7 = r.consume();
        boolean o_matcheslitString110526__8 = r.matches("One");
        boolean o_matcheslitString110526__9 = r.matches("ne Two Three");
        boolean o_matcheslitString110526__10 = r.matches("ne Two Three Four");
        String o_matcheslitString110526__11 = r.consumeToEnd();
        Assert.assertEquals("", o_matcheslitString110526__11);
        boolean o_matcheslitString110526__12 = r.matches("ne");
        r.isEmpty();
        r.advance();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_matcheslitString110526__11);
    }

    @Test(timeout = 10000)
    public void matcheslitString110523_mg116277litString121153() throws Exception {
        CharacterReader r = new CharacterReader("On+e Two Three");
        Assert.assertEquals("On+e Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110523__3 = r.matches('O');
        boolean o_matcheslitString110523__4 = r.matches("One Two Three");
        boolean o_matcheslitString110523__5 = r.matches("One");
        boolean o_matcheslitString110523__6 = r.matches("one");
        char o_matcheslitString110523__7 = r.consume();
        boolean o_matcheslitString110523__8 = r.matches("One");
        boolean o_matcheslitString110523__9 = r.matches("ne Two Three");
        boolean o_matcheslitString110523__10 = r.matches("\n");
        String o_matcheslitString110523__11 = r.consumeToEnd();
        Assert.assertEquals("n+e Two Three", o_matcheslitString110523__11);
        boolean o_matcheslitString110523__12 = r.matches("ne");
        r.isEmpty();
        r.advance();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("n+e Two Three", o_matcheslitString110523__11);
    }

    @Test(timeout = 10000)
    public void matcheslitString110526litString113104_add127916() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110526__3 = r.matches('O');
        boolean o_matcheslitString110526__4 = r.matches("two");
        boolean o_matcheslitString110526__5 = r.matches("One");
        boolean o_matcheslitString110526__6 = r.matches("one");
        char o_matcheslitString110526__7 = r.consume();
        boolean o_matcheslitString110526__8 = r.matches("One");
        boolean o_matcheslitString110526__9 = r.matches("ne Two Three");
        boolean o_matcheslitString110526__10 = r.matches("ne Two Three Four");
        String o_matcheslitString110526__11 = r.consumeToEnd();
        Assert.assertEquals("", o_matcheslitString110526__11);
        boolean o_matcheslitString110526__12 = r.matches("ne");
        r.isEmpty();
        ((CharacterReader) (r)).isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_matcheslitString110526__11);
    }

    @Test(timeout = 10000)
    public void matches_add110592_mg116087_mg129357() throws Exception {
        char __DSPOT_c_6464 = 't';
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matches_add110592__3 = r.matches('O');
        boolean o_matches_add110592__4 = r.matches("One Two Three");
        boolean o_matches_add110592__5 = r.matches("One");
        boolean o_matches_add110592__6 = r.matches("one");
        char o_matches_add110592__7 = r.consume();
        char o_matches_add110592__8 = r.consume();
        boolean o_matches_add110592__9 = r.matches("One");
        boolean o_matches_add110592__10 = r.matches("ne Two Three");
        boolean o_matches_add110592__11 = r.matches("ne Two Three Four");
        String o_matches_add110592__12 = r.consumeToEnd();
        Assert.assertEquals("e Two Three", o_matches_add110592__12);
        boolean o_matches_add110592__13 = r.matches("ne");
        r.isEmpty();
        r.advance();
        String o_matches_add110592_mg116087_mg129357__39 = r.consumeTo(__DSPOT_c_6464);
        Assert.assertEquals("", o_matches_add110592_mg116087_mg129357__39);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("e Two Three", o_matches_add110592__12);
    }

    @Test(timeout = 10000)
    public void matcheslitString110521_mg116376_remove128786() throws Exception {
        CharacterReader r = new CharacterReader("<!");
        Assert.assertEquals("<!", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110521__3 = r.matches('O');
        boolean o_matcheslitString110521__4 = r.matches("One Two Three");
        boolean o_matcheslitString110521__5 = r.matches("One");
        boolean o_matcheslitString110521__6 = r.matches("one");
        char o_matcheslitString110521__7 = r.consume();
        boolean o_matcheslitString110521__8 = r.matches("One");
        boolean o_matcheslitString110521__9 = r.matches("ne Two Three");
        boolean o_matcheslitString110521__10 = r.matches("ne Two Three Four");
        String o_matcheslitString110521__11 = r.consumeToEnd();
        Assert.assertEquals("!", o_matcheslitString110521__11);
        boolean o_matcheslitString110521__12 = r.matches("ne");
        int o_matcheslitString110521_mg116376__34 = r.pos();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("!", o_matcheslitString110521__11);
    }

    @Test(timeout = 10000)
    public void matcheslitString110523litChar114357_remove128733() throws Exception {
        CharacterReader r = new CharacterReader("On+e Two Three");
        Assert.assertEquals("On+e Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110523__3 = r.matches(')');
        boolean o_matcheslitString110523__4 = r.matches("One Two Three");
        boolean o_matcheslitString110523__5 = r.matches("One");
        boolean o_matcheslitString110523__6 = r.matches("one");
        char o_matcheslitString110523__7 = r.consume();
        boolean o_matcheslitString110523__8 = r.matches("One");
        boolean o_matcheslitString110523__9 = r.matches("ne Two Three");
        boolean o_matcheslitString110523__10 = r.matches("ne Two Three Four");
        String o_matcheslitString110523__11 = r.consumeToEnd();
        Assert.assertEquals("n+e Two Three", o_matcheslitString110523__11);
        boolean o_matcheslitString110523__12 = r.matches("ne");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("n+e Two Three", o_matcheslitString110523__11);
    }

    @Test(timeout = 10000)
    public void matcheslitString110526litString113106_mg129200() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matcheslitString110526__3 = r.matches('O');
        boolean o_matcheslitString110526__4 = r.matches("One Two Tehree");
        boolean o_matcheslitString110526__5 = r.matches("One");
        boolean o_matcheslitString110526__6 = r.matches("one");
        char o_matcheslitString110526__7 = r.consume();
        boolean o_matcheslitString110526__8 = r.matches("One");
        boolean o_matcheslitString110526__9 = r.matches("ne Two Three");
        boolean o_matcheslitString110526__10 = r.matches("ne Two Three Four");
        String o_matcheslitString110526__11 = r.consumeToEnd();
        Assert.assertEquals("", o_matcheslitString110526__11);
        boolean o_matcheslitString110526__12 = r.matches("ne");
        r.isEmpty();
        int o_matcheslitString110526litString113106_mg129200__34 = r.pos();
        Assert.assertEquals(1, ((int) (o_matcheslitString110526litString113106_mg129200__34)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_matcheslitString110526__11);
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString140198() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matchesIgnoreCaselitString140198__3 = r.matchesIgnoreCase("O");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__3);
        boolean o_matchesIgnoreCaselitString140198__4 = r.matchesIgnoreCase("o");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__4);
        boolean o_matchesIgnoreCaselitString140198__5 = r.matches('O');
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__5);
        boolean o_matchesIgnoreCaselitString140198__6 = r.matches('o');
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__6);
        boolean o_matchesIgnoreCaselitString140198__7 = r.matchesIgnoreCase("One Two Three");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__7);
        boolean o_matchesIgnoreCaselitString140198__8 = r.matchesIgnoreCase("ONE two THREE");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__8);
        boolean o_matchesIgnoreCaselitString140198__9 = r.matchesIgnoreCase("One");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__9);
        boolean o_matchesIgnoreCaselitString140198__10 = r.matchesIgnoreCase("one");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__10);
        char o_matchesIgnoreCaselitString140198__11 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_matchesIgnoreCaselitString140198__11)));
        boolean o_matchesIgnoreCaselitString140198__12 = r.matchesIgnoreCase("One");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__12);
        boolean o_matchesIgnoreCaselitString140198__13 = r.matchesIgnoreCase("NE Two Three");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__13);
        boolean o_matchesIgnoreCaselitString140198__14 = r.matchesIgnoreCase("ne Two Three Four");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__14);
        String o_matchesIgnoreCaselitString140198__15 = r.consumeToEnd();
        Assert.assertEquals("", o_matchesIgnoreCaselitString140198__15);
        boolean o_matchesIgnoreCaselitString140198__16 = r.matchesIgnoreCase("ne");
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__16);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__3);
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__4);
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__5);
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__6);
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__7);
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__8);
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__9);
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__10);
        Assert.assertEquals('\uffff', ((char) (o_matchesIgnoreCaselitString140198__11)));
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__12);
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__13);
        Assert.assertFalse(o_matchesIgnoreCaselitString140198__14);
        Assert.assertEquals("", o_matchesIgnoreCaselitString140198__15);
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString140199() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesIgnoreCaselitString140199__3 = r.matchesIgnoreCase("O");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__3);
        boolean o_matchesIgnoreCaselitString140199__4 = r.matchesIgnoreCase("o");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__4);
        boolean o_matchesIgnoreCaselitString140199__5 = r.matches('O');
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__5);
        boolean o_matchesIgnoreCaselitString140199__6 = r.matches('o');
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__6);
        boolean o_matchesIgnoreCaselitString140199__7 = r.matchesIgnoreCase("One Two Three");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__7);
        boolean o_matchesIgnoreCaselitString140199__8 = r.matchesIgnoreCase("ONE two THREE");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__8);
        boolean o_matchesIgnoreCaselitString140199__9 = r.matchesIgnoreCase("One");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__9);
        boolean o_matchesIgnoreCaselitString140199__10 = r.matchesIgnoreCase("one");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__10);
        char o_matchesIgnoreCaselitString140199__11 = r.consume();
        Assert.assertEquals('\n', ((char) (o_matchesIgnoreCaselitString140199__11)));
        boolean o_matchesIgnoreCaselitString140199__12 = r.matchesIgnoreCase("One");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__12);
        boolean o_matchesIgnoreCaselitString140199__13 = r.matchesIgnoreCase("NE Two Three");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__13);
        boolean o_matchesIgnoreCaselitString140199__14 = r.matchesIgnoreCase("ne Two Three Four");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__14);
        String o_matchesIgnoreCaselitString140199__15 = r.consumeToEnd();
        Assert.assertEquals("", o_matchesIgnoreCaselitString140199__15);
        boolean o_matchesIgnoreCaselitString140199__16 = r.matchesIgnoreCase("ne");
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__16);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__3);
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__4);
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__5);
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__6);
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__7);
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__8);
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__9);
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__10);
        Assert.assertEquals('\n', ((char) (o_matchesIgnoreCaselitString140199__11)));
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__12);
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__13);
        Assert.assertFalse(o_matchesIgnoreCaselitString140199__14);
        Assert.assertEquals("", o_matchesIgnoreCaselitString140199__15);
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString140197_add148856() throws Exception {
        CharacterReader r = new CharacterReader("(*N&{gQQ#DX&D");
        Assert.assertEquals("(*N&{gQQ#DX&D", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesIgnoreCaselitString140197_add148856__3 = r.matchesIgnoreCase("O");
        Assert.assertFalse(o_matchesIgnoreCaselitString140197_add148856__3);
        boolean o_matchesIgnoreCaselitString140197__3 = r.matchesIgnoreCase("O");
        boolean o_matchesIgnoreCaselitString140197__4 = r.matchesIgnoreCase("o");
        boolean o_matchesIgnoreCaselitString140197__5 = r.matches('O');
        boolean o_matchesIgnoreCaselitString140197__6 = r.matches('o');
        boolean o_matchesIgnoreCaselitString140197__7 = r.matchesIgnoreCase("One Two Three");
        boolean o_matchesIgnoreCaselitString140197__8 = r.matchesIgnoreCase("ONE two THREE");
        boolean o_matchesIgnoreCaselitString140197__9 = r.matchesIgnoreCase("One");
        boolean o_matchesIgnoreCaselitString140197__10 = r.matchesIgnoreCase("one");
        char o_matchesIgnoreCaselitString140197__11 = r.consume();
        boolean o_matchesIgnoreCaselitString140197__12 = r.matchesIgnoreCase("One");
        boolean o_matchesIgnoreCaselitString140197__13 = r.matchesIgnoreCase("NE Two Three");
        boolean o_matchesIgnoreCaselitString140197__14 = r.matchesIgnoreCase("ne Two Three Four");
        String o_matchesIgnoreCaselitString140197__15 = r.consumeToEnd();
        Assert.assertEquals("*N&{gQQ#DX&D", o_matchesIgnoreCaselitString140197__15);
        boolean o_matchesIgnoreCaselitString140197__16 = r.matchesIgnoreCase("ne");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_matchesIgnoreCaselitString140197_add148856__3);
        Assert.assertEquals("*N&{gQQ#DX&D", o_matchesIgnoreCaselitString140197__15);
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString140198litChar147374() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matchesIgnoreCaselitString140198__3 = r.matchesIgnoreCase("O");
        boolean o_matchesIgnoreCaselitString140198__4 = r.matchesIgnoreCase("o");
        boolean o_matchesIgnoreCaselitString140198__5 = r.matches('\u0000');
        boolean o_matchesIgnoreCaselitString140198__6 = r.matches('o');
        boolean o_matchesIgnoreCaselitString140198__7 = r.matchesIgnoreCase("One Two Three");
        boolean o_matchesIgnoreCaselitString140198__8 = r.matchesIgnoreCase("ONE two THREE");
        boolean o_matchesIgnoreCaselitString140198__9 = r.matchesIgnoreCase("One");
        boolean o_matchesIgnoreCaselitString140198__10 = r.matchesIgnoreCase("one");
        char o_matchesIgnoreCaselitString140198__11 = r.consume();
        boolean o_matchesIgnoreCaselitString140198__12 = r.matchesIgnoreCase("One");
        boolean o_matchesIgnoreCaselitString140198__13 = r.matchesIgnoreCase("NE Two Three");
        boolean o_matchesIgnoreCaselitString140198__14 = r.matchesIgnoreCase("ne Two Three Four");
        String o_matchesIgnoreCaselitString140198__15 = r.consumeToEnd();
        Assert.assertEquals("", o_matchesIgnoreCaselitString140198__15);
        boolean o_matchesIgnoreCaselitString140198__16 = r.matchesIgnoreCase("ne");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_matchesIgnoreCaselitString140198__15);
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCasenull140303_failAssert553_mg150465() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Three");
            Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            r.matchesIgnoreCase(null);
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
            org.junit.Assert.fail("matchesIgnoreCasenull140303 should have thrown NullPointerException");
            r.advance();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString140198_add149362null168073_failAssert604() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            boolean o_matchesIgnoreCaselitString140198__3 = r.matchesIgnoreCase("O");
            boolean o_matchesIgnoreCaselitString140198__4 = r.matchesIgnoreCase("o");
            boolean o_matchesIgnoreCaselitString140198_add149362__9 = r.matches('O');
            boolean o_matchesIgnoreCaselitString140198__5 = r.matches('O');
            boolean o_matchesIgnoreCaselitString140198__6 = r.matches('o');
            boolean o_matchesIgnoreCaselitString140198__7 = r.matchesIgnoreCase(null);
            boolean o_matchesIgnoreCaselitString140198__8 = r.matchesIgnoreCase("ONE two THREE");
            boolean o_matchesIgnoreCaselitString140198__9 = r.matchesIgnoreCase("One");
            boolean o_matchesIgnoreCaselitString140198__10 = r.matchesIgnoreCase("one");
            char o_matchesIgnoreCaselitString140198__11 = r.consume();
            boolean o_matchesIgnoreCaselitString140198__12 = r.matchesIgnoreCase("One");
            boolean o_matchesIgnoreCaselitString140198__13 = r.matchesIgnoreCase("NE Two Three");
            boolean o_matchesIgnoreCaselitString140198__14 = r.matchesIgnoreCase("ne Two Three Four");
            String o_matchesIgnoreCaselitString140198__15 = r.consumeToEnd();
            boolean o_matchesIgnoreCaselitString140198__16 = r.matchesIgnoreCase("ne");
            org.junit.Assert.fail("matchesIgnoreCaselitString140198_add149362null168073 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString140198null151108_failAssert586null169399() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            Assert.assertEquals("", ((CharacterReader) (r)).toString());
            Assert.assertTrue(((CharacterReader) (r)).isEmpty());
            boolean o_matchesIgnoreCaselitString140198__3 = r.matchesIgnoreCase("O");
            boolean o_matchesIgnoreCaselitString140198__4 = r.matchesIgnoreCase("o");
            boolean o_matchesIgnoreCaselitString140198__5 = r.matches('O');
            boolean o_matchesIgnoreCaselitString140198__6 = r.matches('o');
            boolean o_matchesIgnoreCaselitString140198__7 = r.matchesIgnoreCase(null);
            boolean o_matchesIgnoreCaselitString140198__8 = r.matchesIgnoreCase("ONE two THREE");
            boolean o_matchesIgnoreCaselitString140198__9 = r.matchesIgnoreCase("One");
            boolean o_matchesIgnoreCaselitString140198__10 = r.matchesIgnoreCase("one");
            char o_matchesIgnoreCaselitString140198__11 = r.consume();
            boolean o_matchesIgnoreCaselitString140198__12 = r.matchesIgnoreCase("One");
            boolean o_matchesIgnoreCaselitString140198__13 = r.matchesIgnoreCase("NE Two Three");
            boolean o_matchesIgnoreCaselitString140198__14 = r.matchesIgnoreCase(null);
            String o_matchesIgnoreCaselitString140198__15 = r.consumeToEnd();
            boolean o_matchesIgnoreCaselitString140198__16 = r.matchesIgnoreCase("ne");
            org.junit.Assert.fail("matchesIgnoreCaselitString140198null151108 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitChar140275litChar146595_add164224() throws Exception {
        CharacterReader r = new CharacterReader("One Two Three");
        Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesIgnoreCaselitChar140275__3 = r.matchesIgnoreCase("O");
        boolean o_matchesIgnoreCaselitChar140275__4 = r.matchesIgnoreCase("o");
        boolean o_matchesIgnoreCaselitChar140275__5 = r.matches('P');
        boolean o_matchesIgnoreCaselitChar140275__6 = r.matches('p');
        boolean o_matchesIgnoreCaselitChar140275litChar146595_add164224__15 = r.matchesIgnoreCase("One Two Three");
        Assert.assertTrue(o_matchesIgnoreCaselitChar140275litChar146595_add164224__15);
        boolean o_matchesIgnoreCaselitChar140275__7 = r.matchesIgnoreCase("One Two Three");
        boolean o_matchesIgnoreCaselitChar140275__8 = r.matchesIgnoreCase("ONE two THREE");
        boolean o_matchesIgnoreCaselitChar140275__9 = r.matchesIgnoreCase("One");
        boolean o_matchesIgnoreCaselitChar140275__10 = r.matchesIgnoreCase("one");
        char o_matchesIgnoreCaselitChar140275__11 = r.consume();
        boolean o_matchesIgnoreCaselitChar140275__12 = r.matchesIgnoreCase("One");
        boolean o_matchesIgnoreCaselitChar140275__13 = r.matchesIgnoreCase("NE Two Three");
        boolean o_matchesIgnoreCaselitChar140275__14 = r.matchesIgnoreCase("ne Two Three Four");
        String o_matchesIgnoreCaselitChar140275__15 = r.consumeToEnd();
        Assert.assertEquals("ne Two Three", o_matchesIgnoreCaselitChar140275__15);
        boolean o_matchesIgnoreCaselitChar140275__16 = r.matchesIgnoreCase("ne");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertTrue(o_matchesIgnoreCaselitChar140275litChar146595_add164224__15);
        Assert.assertEquals("ne Two Three", o_matchesIgnoreCaselitChar140275__15);
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCase_add140297null150529_failAssert595null169474() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One Two Three");
            Assert.assertEquals("One Two Three", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            boolean o_matchesIgnoreCase_add140297__3 = r.matchesIgnoreCase("O");
            boolean o_matchesIgnoreCase_add140297__4 = r.matchesIgnoreCase("o");
            boolean o_matchesIgnoreCase_add140297__5 = r.matches('O');
            boolean o_matchesIgnoreCase_add140297__6 = r.matches('o');
            boolean o_matchesIgnoreCase_add140297__7 = r.matchesIgnoreCase(null);
            boolean o_matchesIgnoreCase_add140297__8 = r.matchesIgnoreCase("ONE two THREE");
            boolean o_matchesIgnoreCase_add140297__9 = r.matchesIgnoreCase("One");
            boolean o_matchesIgnoreCase_add140297__10 = r.matchesIgnoreCase(null);
            char o_matchesIgnoreCase_add140297__11 = r.consume();
            boolean o_matchesIgnoreCase_add140297__12 = r.matchesIgnoreCase("One");
            boolean o_matchesIgnoreCase_add140297__13 = r.matchesIgnoreCase("NE Two Three");
            boolean o_matchesIgnoreCase_add140297__14 = r.matchesIgnoreCase("ne Two Three Four");
            String o_matchesIgnoreCase_add140297__15 = r.consumeToEnd();
            boolean o_matchesIgnoreCase_add140297__16 = r.matchesIgnoreCase("ne");
            boolean o_matchesIgnoreCase_add140297__17 = r.matchesIgnoreCase("ne");
            org.junit.Assert.fail("matchesIgnoreCase_add140297null150529 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void matchesIgnoreCaselitString140193null151163_failAssert572_add166622() throws Exception {
        try {
            CharacterReader r = new CharacterReader("Check");
            Assert.assertEquals("Check", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            boolean o_matchesIgnoreCaselitString140193__3 = r.matchesIgnoreCase("O");
            boolean o_matchesIgnoreCaselitString140193null151163_failAssert572_add166622__8 = r.matchesIgnoreCase("o");
            Assert.assertFalse(o_matchesIgnoreCaselitString140193null151163_failAssert572_add166622__8);
            boolean o_matchesIgnoreCaselitString140193__4 = r.matchesIgnoreCase("o");
            boolean o_matchesIgnoreCaselitString140193__5 = r.matches('O');
            boolean o_matchesIgnoreCaselitString140193__6 = r.matches('o');
            boolean o_matchesIgnoreCaselitString140193__7 = r.matchesIgnoreCase("One Two Three");
            boolean o_matchesIgnoreCaselitString140193__8 = r.matchesIgnoreCase("ONE two THREE");
            boolean o_matchesIgnoreCaselitString140193__9 = r.matchesIgnoreCase("One");
            boolean o_matchesIgnoreCaselitString140193__10 = r.matchesIgnoreCase("one");
            char o_matchesIgnoreCaselitString140193__11 = r.consume();
            boolean o_matchesIgnoreCaselitString140193__12 = r.matchesIgnoreCase("One");
            boolean o_matchesIgnoreCaselitString140193__13 = r.matchesIgnoreCase("NE Two Three");
            boolean o_matchesIgnoreCaselitString140193__14 = r.matchesIgnoreCase(null);
            String o_matchesIgnoreCaselitString140193__15 = r.consumeToEnd();
            boolean o_matchesIgnoreCaselitString140193__16 = r.matchesIgnoreCase("ne");
            org.junit.Assert.fail("matchesIgnoreCaselitString140193null151163 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void containsIgnoreCaselitString86420() throws Exception {
        CharacterReader r = new CharacterReader("One TWO three");
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCaselitString86420__3 = r.containsIgnoreCase("One ");
        Assert.assertFalse(o_containsIgnoreCaselitString86420__3);
        boolean o_containsIgnoreCaselitString86420__4 = r.containsIgnoreCase("three");
        Assert.assertTrue(o_containsIgnoreCaselitString86420__4);
        boolean o_containsIgnoreCaselitString86420__5 = r.containsIgnoreCase("one");
        Assert.assertFalse(o_containsIgnoreCaselitString86420__5);
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_containsIgnoreCaselitString86420__3);
        Assert.assertTrue(o_containsIgnoreCaselitString86420__4);
    }

    @Test(timeout = 10000)
    public void containsIgnoreCase_mg86449() throws Exception {
        char[] __DSPOT_chars_4681 = new char[]{ '.', '7' };
        CharacterReader r = new CharacterReader("One TWO three");
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCase_mg86449__4 = r.containsIgnoreCase("two");
        Assert.assertTrue(o_containsIgnoreCase_mg86449__4);
        boolean o_containsIgnoreCase_mg86449__5 = r.containsIgnoreCase("three");
        Assert.assertTrue(o_containsIgnoreCase_mg86449__5);
        boolean o_containsIgnoreCase_mg86449__6 = r.containsIgnoreCase("one");
        Assert.assertFalse(o_containsIgnoreCase_mg86449__6);
        String o_containsIgnoreCase_mg86449__7 = r.consumeToAny(__DSPOT_chars_4681);
        Assert.assertEquals("One TWO three", o_containsIgnoreCase_mg86449__7);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertTrue(o_containsIgnoreCase_mg86449__4);
        Assert.assertTrue(o_containsIgnoreCase_mg86449__5);
        Assert.assertFalse(o_containsIgnoreCase_mg86449__6);
    }

    @Test(timeout = 10000)
    public void containsIgnoreCaselitString86417() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCaselitString86417__3 = r.containsIgnoreCase("two");
        Assert.assertFalse(o_containsIgnoreCaselitString86417__3);
        boolean o_containsIgnoreCaselitString86417__4 = r.containsIgnoreCase("three");
        Assert.assertFalse(o_containsIgnoreCaselitString86417__4);
        boolean o_containsIgnoreCaselitString86417__5 = r.containsIgnoreCase("one");
        Assert.assertFalse(o_containsIgnoreCaselitString86417__5);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_containsIgnoreCaselitString86417__3);
        Assert.assertFalse(o_containsIgnoreCaselitString86417__4);
    }

    @Test(timeout = 10000)
    public void containsIgnoreCaselitString86415() throws Exception {
        CharacterReader r = new CharacterReader("One TWO hree");
        Assert.assertEquals("One TWO hree", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCaselitString86415__3 = r.containsIgnoreCase("two");
        Assert.assertTrue(o_containsIgnoreCaselitString86415__3);
        boolean o_containsIgnoreCaselitString86415__4 = r.containsIgnoreCase("three");
        Assert.assertFalse(o_containsIgnoreCaselitString86415__4);
        boolean o_containsIgnoreCaselitString86415__5 = r.containsIgnoreCase("one");
        Assert.assertFalse(o_containsIgnoreCaselitString86415__5);
        Assert.assertEquals("One TWO hree", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertTrue(o_containsIgnoreCaselitString86415__3);
        Assert.assertFalse(o_containsIgnoreCaselitString86415__4);
    }

    @Test(timeout = 10000)
    public void containsIgnoreCaselitString86412() throws Exception {
        CharacterReader r = new CharacterReader("one two three");
        Assert.assertEquals("one two three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCaselitString86412__3 = r.containsIgnoreCase("two");
        Assert.assertTrue(o_containsIgnoreCaselitString86412__3);
        boolean o_containsIgnoreCaselitString86412__4 = r.containsIgnoreCase("three");
        Assert.assertTrue(o_containsIgnoreCaselitString86412__4);
        boolean o_containsIgnoreCaselitString86412__5 = r.containsIgnoreCase("one");
        Assert.assertTrue(o_containsIgnoreCaselitString86412__5);
        Assert.assertEquals("one two three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertTrue(o_containsIgnoreCaselitString86412__3);
        Assert.assertTrue(o_containsIgnoreCaselitString86412__4);
    }

    @Test(timeout = 10000)
    public void containsIgnoreCase_mg86449litChar87278() throws Exception {
        char[] __DSPOT_chars_4681 = new char[]{ '.', 'n' };
        CharacterReader r = new CharacterReader("One TWO three");
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCase_mg86449__4 = r.containsIgnoreCase("two");
        boolean o_containsIgnoreCase_mg86449__5 = r.containsIgnoreCase("three");
        boolean o_containsIgnoreCase_mg86449__6 = r.containsIgnoreCase("one");
        String o_containsIgnoreCase_mg86449__7 = r.consumeToAny(__DSPOT_chars_4681);
        Assert.assertEquals("O", o_containsIgnoreCase_mg86449__7);
        Assert.assertEquals("ne TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void containsIgnoreCaselitString86443_add87366() throws Exception {
        CharacterReader r = new CharacterReader("One TWO three");
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCaselitString86443__3 = r.containsIgnoreCase("two");
        boolean o_containsIgnoreCaselitString86443__4 = r.containsIgnoreCase("three");
        boolean o_containsIgnoreCaselitString86443_add87366__9 = r.containsIgnoreCase(":");
        Assert.assertFalse(o_containsIgnoreCaselitString86443_add87366__9);
        boolean o_containsIgnoreCaselitString86443__5 = r.containsIgnoreCase(":");
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_containsIgnoreCaselitString86443_add87366__9);
    }

    @Test(timeout = 10000)
    public void containsIgnoreCaselitString86425_failAssert326_mg87774() throws Exception {
        try {
            CharacterReader r = new CharacterReader("One TWO three");
            Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            r.containsIgnoreCase("");
            r.containsIgnoreCase("three");
            r.containsIgnoreCase("one");
            org.junit.Assert.fail("containsIgnoreCaselitString86425 should have thrown StringIndexOutOfBoundsException");
            r.advance();
        } catch (StringIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void containsIgnoreCase_mg86447litString87034() throws Exception {
        CharacterReader r = new CharacterReader("One TWO three");
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCase_mg86447__3 = r.containsIgnoreCase("two");
        boolean o_containsIgnoreCase_mg86447__4 = r.containsIgnoreCase("three");
        boolean o_containsIgnoreCase_mg86447__5 = r.containsIgnoreCase("hne");
        r.advance();
        Assert.assertEquals("ne TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void containsIgnoreCase_mg86451litString86982() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCase_mg86451__3 = r.containsIgnoreCase("two");
        boolean o_containsIgnoreCase_mg86451__4 = r.containsIgnoreCase("three");
        boolean o_containsIgnoreCase_mg86451__5 = r.containsIgnoreCase("one");
        int o_containsIgnoreCase_mg86451__6 = r.pos();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void containsIgnoreCase_mg86448litChar87264litString88936() throws Exception {
        char __DSPOT_c_4680 = '\u0000';
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCase_mg86448__4 = r.containsIgnoreCase("two");
        boolean o_containsIgnoreCase_mg86448__5 = r.containsIgnoreCase("three");
        boolean o_containsIgnoreCase_mg86448__6 = r.containsIgnoreCase("one");
        String o_containsIgnoreCase_mg86448__7 = r.consumeTo(__DSPOT_c_4680);
        Assert.assertEquals("\n", o_containsIgnoreCase_mg86448__7);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void containsIgnoreCase_mg86449null87844_failAssert350litChar91693() throws Exception {
        try {
            char[] __DSPOT_chars_4681 = new char[]{ '.', '\n' };
            CharacterReader r = new CharacterReader("One TWO three");
            Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            boolean o_containsIgnoreCase_mg86449__4 = r.containsIgnoreCase(null);
            boolean o_containsIgnoreCase_mg86449__5 = r.containsIgnoreCase("three");
            boolean o_containsIgnoreCase_mg86449__6 = r.containsIgnoreCase("one");
            String o_containsIgnoreCase_mg86449__7 = r.consumeToAny(__DSPOT_chars_4681);
            org.junit.Assert.fail("containsIgnoreCase_mg86449null87844 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void containsIgnoreCase_add86446litString86632_mg93316() throws Exception {
        CharacterReader r = new CharacterReader("One TWO three");
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCase_add86446__3 = r.containsIgnoreCase("\n");
        boolean o_containsIgnoreCase_add86446__4 = r.containsIgnoreCase("three");
        boolean o_containsIgnoreCase_add86446__5 = r.containsIgnoreCase("one");
        boolean o_containsIgnoreCase_add86446__6 = r.containsIgnoreCase("one");
        r.advance();
        Assert.assertEquals("ne TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void containsIgnoreCaselitString86420_add87482_add91736() throws Exception {
        CharacterReader r = new CharacterReader("One TWO three");
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_containsIgnoreCaselitString86420_add87482_add91736__3 = r.containsIgnoreCase("One ");
        Assert.assertFalse(o_containsIgnoreCaselitString86420_add87482_add91736__3);
        boolean o_containsIgnoreCaselitString86420__3 = r.containsIgnoreCase("One ");
        boolean o_containsIgnoreCaselitString86420_add87482__6 = r.containsIgnoreCase("three");
        boolean o_containsIgnoreCaselitString86420__4 = r.containsIgnoreCase("three");
        boolean o_containsIgnoreCaselitString86420__5 = r.containsIgnoreCase("one");
        Assert.assertEquals("One TWO three", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_containsIgnoreCaselitString86420_add87482_add91736__3);
    }

    @Test(timeout = 10000)
    public void matchesAnylitString131438() throws Exception {
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAnylitString131438__4 = r.matchesAny(scan);
        Assert.assertTrue(o_matchesAnylitString131438__4);
        String o_matchesAnylitString131438__5 = r.consumeToAny(scan);
        Assert.assertEquals("", o_matchesAnylitString131438__5);
        boolean o_matchesAnylitString131438__6 = r.matchesAny(scan);
        Assert.assertTrue(o_matchesAnylitString131438__6);
        char o_matchesAnylitString131438__7 = r.consume();
        Assert.assertEquals('\n', ((char) (o_matchesAnylitString131438__7)));
        boolean o_matchesAnylitString131438__8 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131438__8);
        boolean o_matchesAnylitString131438__9 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131438__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertTrue(o_matchesAnylitString131438__4);
        Assert.assertEquals("", o_matchesAnylitString131438__5);
        Assert.assertTrue(o_matchesAnylitString131438__6);
        Assert.assertEquals('\n', ((char) (o_matchesAnylitString131438__7)));
        Assert.assertFalse(o_matchesAnylitString131438__8);
    }

    @Test(timeout = 10000)
    public void matchesAnylitString131439() throws Exception {
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAnylitString131439__4 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131439__4);
        String o_matchesAnylitString131439__5 = r.consumeToAny(scan);
        Assert.assertEquals(":", o_matchesAnylitString131439__5);
        boolean o_matchesAnylitString131439__6 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131439__6);
        char o_matchesAnylitString131439__7 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_matchesAnylitString131439__7)));
        boolean o_matchesAnylitString131439__8 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131439__8);
        boolean o_matchesAnylitString131439__9 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131439__9);
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_matchesAnylitString131439__4);
        Assert.assertEquals(":", o_matchesAnylitString131439__5);
        Assert.assertFalse(o_matchesAnylitString131439__6);
        Assert.assertEquals('\uffff', ((char) (o_matchesAnylitString131439__7)));
        Assert.assertFalse(o_matchesAnylitString131439__8);
    }

    @Test(timeout = 10000)
    public void matchesAnylitString131437() throws Exception {
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAnylitString131437__4 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131437__4);
        String o_matchesAnylitString131437__5 = r.consumeToAny(scan);
        Assert.assertEquals("", o_matchesAnylitString131437__5);
        boolean o_matchesAnylitString131437__6 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131437__6);
        char o_matchesAnylitString131437__7 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_matchesAnylitString131437__7)));
        boolean o_matchesAnylitString131437__8 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131437__8);
        boolean o_matchesAnylitString131437__9 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAnylitString131437__9);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_matchesAnylitString131437__4);
        Assert.assertEquals("", o_matchesAnylitString131437__5);
        Assert.assertFalse(o_matchesAnylitString131437__6);
        Assert.assertEquals('\uffff', ((char) (o_matchesAnylitString131437__7)));
        Assert.assertFalse(o_matchesAnylitString131437__8);
    }

    @Test(timeout = 10000)
    public void matchesAnylitChar131450litString131641() throws Exception {
        char[] scan = new char[]{ ' ', '\t', '\t' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAnylitChar131450__4 = r.matchesAny(scan);
        String o_matchesAnylitChar131450__5 = r.consumeToAny(scan);
        Assert.assertEquals("", o_matchesAnylitChar131450__5);
        boolean o_matchesAnylitChar131450__6 = r.matchesAny(scan);
        char o_matchesAnylitChar131450__7 = r.consume();
        boolean o_matchesAnylitChar131450__8 = r.matchesAny(scan);
        boolean o_matchesAnylitChar131450__9 = r.matchesAny(scan);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_matchesAnylitChar131450__5);
    }

    @Test(timeout = 10000)
    public void matchesAnylitChar131451litString131666() throws Exception {
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAnylitChar131451__4 = r.matchesAny(scan);
        String o_matchesAnylitChar131451__5 = r.consumeToAny(scan);
        Assert.assertEquals("", o_matchesAnylitChar131451__5);
        boolean o_matchesAnylitChar131451__6 = r.matchesAny(scan);
        char o_matchesAnylitChar131451__7 = r.consume();
        boolean o_matchesAnylitChar131451__8 = r.matchesAny(scan);
        boolean o_matchesAnylitChar131451__9 = r.matchesAny(scan);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_matchesAnylitChar131451__5);
    }

    @Test(timeout = 10000)
    public void matchesAny_mg131464litString131777() throws Exception {
        char[] __DSPOT_chars_6663 = new char[]{ 'D' };
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAny_mg131464__5 = r.matchesAny(scan);
        String o_matchesAny_mg131464__6 = r.consumeToAny(scan);
        Assert.assertEquals("", o_matchesAny_mg131464__6);
        boolean o_matchesAny_mg131464__7 = r.matchesAny(scan);
        char o_matchesAny_mg131464__8 = r.consume();
        boolean o_matchesAny_mg131464__9 = r.matchesAny(scan);
        boolean o_matchesAny_mg131464__10 = r.matchesAny(scan);
        String o_matchesAny_mg131464__11 = r.consumeToAny(__DSPOT_chars_6663);
        Assert.assertEquals("", o_matchesAny_mg131464__11);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_matchesAny_mg131464__6);
    }

    @Test(timeout = 10000)
    public void matchesAny_mg131463litString131763() throws Exception {
        char __DSPOT_c_6662 = 'C';
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAny_mg131463__5 = r.matchesAny(scan);
        String o_matchesAny_mg131463__6 = r.consumeToAny(scan);
        Assert.assertEquals(":", o_matchesAny_mg131463__6);
        boolean o_matchesAny_mg131463__7 = r.matchesAny(scan);
        char o_matchesAny_mg131463__8 = r.consume();
        boolean o_matchesAny_mg131463__9 = r.matchesAny(scan);
        boolean o_matchesAny_mg131463__10 = r.matchesAny(scan);
        String o_matchesAny_mg131463__11 = r.consumeTo(__DSPOT_c_6662);
        Assert.assertEquals("", o_matchesAny_mg131463__11);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(":", o_matchesAny_mg131463__6);
    }

    @Test(timeout = 10000)
    public void matchesAnylitChar131452_mg132617() throws Exception {
        char[] __DSPOT_chars_6685 = new char[]{ 'b', '[', 'w', 'I' };
        char[] scan = new char[]{ ' ', '\n', '\u0000' };
        CharacterReader r = new CharacterReader("One\nTwo\tThree");
        Assert.assertEquals("One\nTwo\tThree", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAnylitChar131452__4 = r.matchesAny(scan);
        String o_matchesAnylitChar131452__5 = r.consumeToAny(scan);
        Assert.assertEquals("One", o_matchesAnylitChar131452__5);
        boolean o_matchesAnylitChar131452__6 = r.matchesAny(scan);
        char o_matchesAnylitChar131452__7 = r.consume();
        boolean o_matchesAnylitChar131452__8 = r.matchesAny(scan);
        boolean o_matchesAnylitChar131452__9 = r.matchesAny(scan);
        String o_matchesAnylitChar131452_mg132617__23 = r.consumeToAny(__DSPOT_chars_6685);
        Assert.assertEquals("T", o_matchesAnylitChar131452_mg132617__23);
        Assert.assertEquals("wo\tThree", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One", o_matchesAnylitChar131452__5);
    }

    @Test(timeout = 10000)
    public void matchesAny_add131458litString131594_add137945() throws Exception {
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAny_add131458__4 = r.matchesAny(scan);
        String o_matchesAny_add131458__5 = r.consumeToAny(scan);
        Assert.assertEquals("", o_matchesAny_add131458__5);
        String o_matchesAny_add131458__6 = r.consumeToAny(scan);
        Assert.assertEquals("", o_matchesAny_add131458__6);
        boolean o_matchesAny_add131458__7 = r.matchesAny(scan);
        char o_matchesAny_add131458__8 = r.consume();
        boolean o_matchesAny_add131458litString131594_add137945__19 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAny_add131458litString131594_add137945__19);
        boolean o_matchesAny_add131458__9 = r.matchesAny(scan);
        boolean o_matchesAny_add131458__10 = r.matchesAny(scan);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_matchesAny_add131458__5);
        Assert.assertEquals("", o_matchesAny_add131458__6);
        Assert.assertFalse(o_matchesAny_add131458litString131594_add137945__19);
    }

    @Test(timeout = 10000)
    public void matchesAny_mg131464_mg132731litString134271() throws Exception {
        char __DSPOT_c_6730 = 'B';
        char[] __DSPOT_chars_6663 = new char[]{ 'D' };
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAny_mg131464__5 = r.matchesAny(scan);
        String o_matchesAny_mg131464__6 = r.consumeToAny(scan);
        Assert.assertEquals(":", o_matchesAny_mg131464__6);
        boolean o_matchesAny_mg131464__7 = r.matchesAny(scan);
        char o_matchesAny_mg131464__8 = r.consume();
        boolean o_matchesAny_mg131464__9 = r.matchesAny(scan);
        boolean o_matchesAny_mg131464__10 = r.matchesAny(scan);
        String o_matchesAny_mg131464__11 = r.consumeToAny(__DSPOT_chars_6663);
        Assert.assertEquals("", o_matchesAny_mg131464__11);
        String o_matchesAny_mg131464_mg132731__27 = r.consumeTo(__DSPOT_c_6730);
        Assert.assertEquals("", o_matchesAny_mg131464_mg132731__27);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(":", o_matchesAny_mg131464__6);
        Assert.assertEquals("", o_matchesAny_mg131464__11);
    }

    @Test(timeout = 10000)
    public void matchesAny_mg131465litChar132201litString134054() throws Exception {
        char[] scan = new char[]{ ' ', '\n', '\u0000' };
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAny_mg131465__4 = r.matchesAny(scan);
        String o_matchesAny_mg131465__5 = r.consumeToAny(scan);
        Assert.assertEquals("", o_matchesAny_mg131465__5);
        boolean o_matchesAny_mg131465__6 = r.matchesAny(scan);
        char o_matchesAny_mg131465__7 = r.consume();
        boolean o_matchesAny_mg131465__8 = r.matchesAny(scan);
        boolean o_matchesAny_mg131465__9 = r.matchesAny(scan);
        char o_matchesAny_mg131465__10 = r.current();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", o_matchesAny_mg131465__5);
    }

    @Test(timeout = 10000)
    public void matchesAny_mg131464_mg132734_add137185() throws Exception {
        char[] __DSPOT_chars_6663 = new char[]{ 'D' };
        char[] scan = new char[]{ ' ', '\n', '\t' };
        CharacterReader r = new CharacterReader("One\nTwo\tThree");
        Assert.assertEquals("One\nTwo\tThree", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAny_mg131464__5 = r.matchesAny(scan);
        String o_matchesAny_mg131464__6 = r.consumeToAny(scan);
        Assert.assertEquals("One", o_matchesAny_mg131464__6);
        boolean o_matchesAny_mg131464__7 = r.matchesAny(scan);
        char o_matchesAny_mg131464__8 = r.consume();
        boolean o_matchesAny_mg131464__9 = r.matchesAny(scan);
        boolean o_matchesAny_mg131464_mg132734_add137185__20 = r.matchesAny(scan);
        Assert.assertFalse(o_matchesAny_mg131464_mg132734_add137185__20);
        boolean o_matchesAny_mg131464__10 = r.matchesAny(scan);
        String o_matchesAny_mg131464__11 = r.consumeToAny(__DSPOT_chars_6663);
        Assert.assertEquals("Two\tThree", o_matchesAny_mg131464__11);
        int o_matchesAny_mg131464_mg132734__26 = r.pos();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("One", o_matchesAny_mg131464__6);
        Assert.assertFalse(o_matchesAny_mg131464_mg132734_add137185__20);
        Assert.assertEquals("Two\tThree", o_matchesAny_mg131464__11);
    }

    @Test(timeout = 10000)
    public void matchesAnylitChar131447_add132277litString133961() throws Exception {
        char[] scan = new char[]{ ' ', ' ', '\t' };
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_matchesAnylitChar131447__4 = r.matchesAny(scan);
        String o_matchesAnylitChar131447__5 = r.consumeToAny(scan);
        Assert.assertEquals(":", o_matchesAnylitChar131447__5);
        boolean o_matchesAnylitChar131447__6 = r.matchesAny(scan);
        char o_matchesAnylitChar131447__7 = r.consume();
        boolean o_matchesAnylitChar131447_add132277__16 = r.matchesAny(scan);
        boolean o_matchesAnylitChar131447__8 = r.matchesAny(scan);
        boolean o_matchesAnylitChar131447__9 = r.matchesAny(scan);
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(":", o_matchesAnylitChar131447__5);
    }

    @Test(timeout = 10000)
    public void cachesStringslitChar6001() throws Exception {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        Assert.assertEquals("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String one = r.consumeTo('\t');
        Assert.assertEquals("Check", one);
        char o_cachesStringslitChar6001__5 = r.consume();
        Assert.assertEquals('\t', ((char) (o_cachesStringslitChar6001__5)));
        String two = r.consumeTo('\t');
        Assert.assertEquals("Check", two);
        char o_cachesStringslitChar6001__8 = r.consume();
        Assert.assertEquals('\t', ((char) (o_cachesStringslitChar6001__8)));
        String three = r.consumeTo('\t');
        Assert.assertEquals("Check", three);
        char o_cachesStringslitChar6001__11 = r.consume();
        Assert.assertEquals('\t', ((char) (o_cachesStringslitChar6001__11)));
        String four = r.consumeTo('\t');
        Assert.assertEquals("CHOKE", four);
        char o_cachesStringslitChar6001__14 = r.consume();
        Assert.assertEquals('\t', ((char) (o_cachesStringslitChar6001__14)));
        String five = r.consumeTo(' ');
        Assert.assertEquals("A", five);
        boolean boolean_136 = one == two;
        boolean boolean_137 = two == three;
        boolean boolean_138 = three != four;
        boolean boolean_139 = four != five;
        Assert.assertEquals(" string that is longer than 16 chars", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("Check", one);
        Assert.assertEquals('\t', ((char) (o_cachesStringslitChar6001__5)));
        Assert.assertEquals("Check", two);
        Assert.assertEquals('\t', ((char) (o_cachesStringslitChar6001__8)));
        Assert.assertEquals("Check", three);
        Assert.assertEquals('\t', ((char) (o_cachesStringslitChar6001__11)));
        Assert.assertEquals("CHOKE", four);
        Assert.assertEquals('\t', ((char) (o_cachesStringslitChar6001__14)));
        Assert.assertEquals("A", five);
    }

    @Test(timeout = 10000)
    public void cachesStringslitChar5980() throws Exception {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars");
        Assert.assertEquals("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String one = r.consumeTo('\u0000');
        Assert.assertEquals("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars", one);
        char o_cachesStringslitChar5980__5 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitChar5980__5)));
        String two = r.consumeTo('\t');
        Assert.assertEquals("", two);
        char o_cachesStringslitChar5980__8 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitChar5980__8)));
        String three = r.consumeTo('\t');
        Assert.assertEquals("", three);
        char o_cachesStringslitChar5980__11 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitChar5980__11)));
        String four = r.consumeTo('\t');
        Assert.assertEquals("", four);
        char o_cachesStringslitChar5980__14 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitChar5980__14)));
        String five = r.consumeTo('\t');
        Assert.assertEquals("", five);
        boolean boolean_200 = one == two;
        boolean boolean_201 = two == three;
        boolean boolean_202 = three != four;
        boolean boolean_203 = four != five;
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("Check\tCheck\tCheck\tCHOKE\tA string that is longer than 16 chars", one);
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitChar5980__5)));
        Assert.assertEquals("", two);
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitChar5980__8)));
        Assert.assertEquals("", three);
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitChar5980__11)));
        Assert.assertEquals("", four);
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitChar5980__14)));
        Assert.assertEquals("", five);
    }

    @Test(timeout = 10000)
    public void cachesStringslitString5977_mg8342() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String one = r.consumeTo('\t');
        Assert.assertEquals("", one);
        char o_cachesStringslitString5977__5 = r.consume();
        String two = r.consumeTo('\t');
        Assert.assertEquals("", two);
        char o_cachesStringslitString5977__8 = r.consume();
        String three = r.consumeTo('\t');
        Assert.assertEquals("", three);
        char o_cachesStringslitString5977__11 = r.consume();
        String four = r.consumeTo('\t');
        Assert.assertEquals("", four);
        char o_cachesStringslitString5977__14 = r.consume();
        String five = r.consumeTo('\t');
        Assert.assertEquals("", five);
        boolean boolean_172 = one == two;
        boolean boolean_173 = two == three;
        boolean boolean_174 = three != four;
        boolean boolean_175 = four != five;
        int o_cachesStringslitString5977_mg8342__29 = r.pos();
        Assert.assertEquals(4, ((int) (o_cachesStringslitString5977_mg8342__29)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", one);
        Assert.assertEquals("", two);
        Assert.assertEquals("", three);
        Assert.assertEquals("", four);
        Assert.assertEquals("", five);
    }

    @Test(timeout = 10000)
    public void cachesStringslitString5978_add7986() throws Exception {
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String one = r.consumeTo('\t');
        Assert.assertEquals("\n", one);
        char o_cachesStringslitString5978__5 = r.consume();
        String two = r.consumeTo('\t');
        Assert.assertEquals("", two);
        char o_cachesStringslitString5978__8 = r.consume();
        String three = r.consumeTo('\t');
        Assert.assertEquals("", three);
        char o_cachesStringslitString5978_add7986__15 = r.consume();
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitString5978_add7986__15)));
        char o_cachesStringslitString5978__11 = r.consume();
        String four = r.consumeTo('\t');
        Assert.assertEquals("", four);
        char o_cachesStringslitString5978__14 = r.consume();
        String five = r.consumeTo('\t');
        Assert.assertEquals("", five);
        boolean boolean_164 = one == two;
        boolean boolean_165 = two == three;
        boolean boolean_166 = three != four;
        boolean boolean_167 = four != five;
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", one);
        Assert.assertEquals("", two);
        Assert.assertEquals("", three);
        Assert.assertEquals('\uffff', ((char) (o_cachesStringslitString5978_add7986__15)));
        Assert.assertEquals("", four);
        Assert.assertEquals("", five);
    }

    @Test(timeout = 10000)
    public void cachesStrings_remove6014litString6260() throws Exception {
        CharacterReader r = new CharacterReader("BM$%p*X^4IVe*cfhr{x]XSLitVio<J7U|{vW25-wM;F2/}k=(vtO^^O;y6H[`");
        Assert.assertEquals("BM$%p*X^4IVe*cfhr{x]XSLitVio<J7U|{vW25-wM;F2/}k=(vtO^^O;y6H[`", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String one = r.consumeTo('\t');
        Assert.assertEquals("BM$%p*X^4IVe*cfhr{x]XSLitVio<J7U|{vW25-wM;F2/}k=(vtO^^O;y6H[`", one);
        String two = r.consumeTo('\t');
        Assert.assertEquals("", two);
        char o_cachesStrings_remove6014__7 = r.consume();
        String three = r.consumeTo('\t');
        Assert.assertEquals("", three);
        char o_cachesStrings_remove6014__10 = r.consume();
        String four = r.consumeTo('\t');
        Assert.assertEquals("", four);
        char o_cachesStrings_remove6014__13 = r.consume();
        String five = r.consumeTo('\t');
        Assert.assertEquals("", five);
        boolean boolean_44 = one == two;
        boolean boolean_45 = two == three;
        boolean boolean_46 = three != four;
        boolean boolean_47 = four != five;
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("BM$%p*X^4IVe*cfhr{x]XSLitVio<J7U|{vW25-wM;F2/}k=(vtO^^O;y6H[`", one);
        Assert.assertEquals("", two);
        Assert.assertEquals("", three);
        Assert.assertEquals("", four);
        Assert.assertEquals("", five);
    }

    @Test(timeout = 10000)
    public void cachesStrings_add6008litString6214_mg16845() throws Exception {
        char __DSPOT_c_808 = '2';
        CharacterReader r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String one = r.consumeTo('\t');
        Assert.assertEquals("\n", one);
        char o_cachesStrings_add6008__5 = r.consume();
        String two = r.consumeTo('\t');
        Assert.assertEquals("", two);
        char o_cachesStrings_add6008__8 = r.consume();
        char o_cachesStrings_add6008__9 = r.consume();
        String three = r.consumeTo('\t');
        Assert.assertEquals("", three);
        char o_cachesStrings_add6008__12 = r.consume();
        String four = r.consumeTo('\t');
        Assert.assertEquals("", four);
        char o_cachesStrings_add6008__15 = r.consume();
        String five = r.consumeTo('\t');
        Assert.assertEquals("", five);
        boolean boolean_20 = one == two;
        boolean boolean_21 = two == three;
        boolean boolean_22 = three != four;
        boolean boolean_23 = four != five;
        String o_cachesStrings_add6008litString6214_mg16845__33 = r.consumeTo(__DSPOT_c_808);
        Assert.assertEquals("", o_cachesStrings_add6008litString6214_mg16845__33);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("\n", one);
        Assert.assertEquals("", two);
        Assert.assertEquals("", three);
        Assert.assertEquals("", four);
        Assert.assertEquals("", five);
    }

    @Test(timeout = 10000)
    public void cachesStringslitString5977litChar7267_mg16298() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String one = r.consumeTo('\t');
        Assert.assertEquals("", one);
        char o_cachesStringslitString5977__5 = r.consume();
        String two = r.consumeTo('\t');
        Assert.assertEquals("", two);
        char o_cachesStringslitString5977__8 = r.consume();
        String three = r.consumeTo('\u0000');
        Assert.assertEquals("", three);
        char o_cachesStringslitString5977__11 = r.consume();
        String four = r.consumeTo('\t');
        Assert.assertEquals("", four);
        char o_cachesStringslitString5977__14 = r.consume();
        String five = r.consumeTo('\t');
        Assert.assertEquals("", five);
        boolean boolean_172 = one == two;
        boolean boolean_173 = two == three;
        boolean boolean_174 = three != four;
        boolean boolean_175 = four != five;
        int o_cachesStringslitString5977litChar7267_mg16298__29 = r.pos();
        Assert.assertEquals(4, ((int) (o_cachesStringslitString5977litChar7267_mg16298__29)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", one);
        Assert.assertEquals("", two);
        Assert.assertEquals("", three);
        Assert.assertEquals("", four);
        Assert.assertEquals("", five);
    }

    @Test(timeout = 10000)
    public void cachesStringslitString5976litChar7110litChar11999() throws Exception {
        CharacterReader r = new CharacterReader("wTIPF26|q@>4ljZdn<b2-sF[T/_=$d_#KqEB_>y.MP=$5mUIn$!c8KzPHQ_&)");
        Assert.assertEquals("wTIPF26|q@>4ljZdn<b2-sF[T/_=$d_#KqEB_>y.MP=$5mUIn$!c8KzPHQ_&)", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String one = r.consumeTo('\n');
        Assert.assertEquals("wTIPF26|q@>4ljZdn<b2-sF[T/_=$d_#KqEB_>y.MP=$5mUIn$!c8KzPHQ_&)", one);
        char o_cachesStringslitString5976__5 = r.consume();
        String two = r.consumeTo('7');
        Assert.assertEquals("", two);
        char o_cachesStringslitString5976__8 = r.consume();
        String three = r.consumeTo('\t');
        Assert.assertEquals("", three);
        char o_cachesStringslitString5976__11 = r.consume();
        String four = r.consumeTo('\t');
        Assert.assertEquals("", four);
        char o_cachesStringslitString5976__14 = r.consume();
        String five = r.consumeTo('\t');
        Assert.assertEquals("", five);
        boolean boolean_148 = one == two;
        boolean boolean_149 = two == three;
        boolean boolean_150 = three != four;
        boolean boolean_151 = four != five;
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("wTIPF26|q@>4ljZdn<b2-sF[T/_=$d_#KqEB_>y.MP=$5mUIn$!c8KzPHQ_&)", one);
        Assert.assertEquals("", two);
        Assert.assertEquals("", three);
        Assert.assertEquals("", four);
        Assert.assertEquals("", five);
    }

    @Test(timeout = 10000)
    public void rangeEqualslitString202500() throws Exception {
        CharacterReader r = new CharacterReader("kZRn4vtbz7/e4_*198G7J,F");
        Assert.assertEquals("kZRn4vtbz7/e4_*198G7J,F", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_rangeEqualslitString202500__3 = r.rangeEquals(0, 5, "Check");
        Assert.assertFalse(o_rangeEqualslitString202500__3);
        boolean o_rangeEqualslitString202500__4 = r.rangeEquals(0, 5, "CHOKE");
        Assert.assertFalse(o_rangeEqualslitString202500__4);
        boolean o_rangeEqualslitString202500__5 = r.rangeEquals(0, 5, "Chec");
        Assert.assertFalse(o_rangeEqualslitString202500__5);
        boolean o_rangeEqualslitString202500__6 = r.rangeEquals(6, 5, "Check");
        Assert.assertFalse(o_rangeEqualslitString202500__6);
        boolean o_rangeEqualslitString202500__7 = r.rangeEquals(6, 5, "Chuck");
        Assert.assertFalse(o_rangeEqualslitString202500__7);
        boolean o_rangeEqualslitString202500__8 = r.rangeEquals(12, 5, "Check");
        Assert.assertFalse(o_rangeEqualslitString202500__8);
        boolean o_rangeEqualslitString202500__9 = r.rangeEquals(12, 5, "Cheeky");
        Assert.assertFalse(o_rangeEqualslitString202500__9);
        boolean o_rangeEqualslitString202500__10 = r.rangeEquals(18, 5, "CHOKE");
        Assert.assertFalse(o_rangeEqualslitString202500__10);
        boolean o_rangeEqualslitString202500__11 = r.rangeEquals(18, 5, "CHIKE");
        Assert.assertFalse(o_rangeEqualslitString202500__11);
        Assert.assertEquals("kZRn4vtbz7/e4_*198G7J,F", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_rangeEqualslitString202500__3);
        Assert.assertFalse(o_rangeEqualslitString202500__4);
        Assert.assertFalse(o_rangeEqualslitString202500__5);
        Assert.assertFalse(o_rangeEqualslitString202500__6);
        Assert.assertFalse(o_rangeEqualslitString202500__7);
        Assert.assertFalse(o_rangeEqualslitString202500__8);
        Assert.assertFalse(o_rangeEqualslitString202500__9);
        Assert.assertFalse(o_rangeEqualslitString202500__10);
    }

    @Test(timeout = 10000)
    public void rangeEquals_mg202693() throws Exception {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE");
        Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_rangeEquals_mg202693__3 = r.rangeEquals(0, 5, "Check");
        Assert.assertTrue(o_rangeEquals_mg202693__3);
        boolean o_rangeEquals_mg202693__4 = r.rangeEquals(0, 5, "CHOKE");
        Assert.assertFalse(o_rangeEquals_mg202693__4);
        boolean o_rangeEquals_mg202693__5 = r.rangeEquals(0, 5, "Chec");
        Assert.assertFalse(o_rangeEquals_mg202693__5);
        boolean o_rangeEquals_mg202693__6 = r.rangeEquals(6, 5, "Check");
        Assert.assertTrue(o_rangeEquals_mg202693__6);
        boolean o_rangeEquals_mg202693__7 = r.rangeEquals(6, 5, "Chuck");
        Assert.assertFalse(o_rangeEquals_mg202693__7);
        boolean o_rangeEquals_mg202693__8 = r.rangeEquals(12, 5, "Check");
        Assert.assertTrue(o_rangeEquals_mg202693__8);
        boolean o_rangeEquals_mg202693__9 = r.rangeEquals(12, 5, "Cheeky");
        Assert.assertFalse(o_rangeEquals_mg202693__9);
        boolean o_rangeEquals_mg202693__10 = r.rangeEquals(18, 5, "CHOKE");
        Assert.assertTrue(o_rangeEquals_mg202693__10);
        boolean o_rangeEquals_mg202693__11 = r.rangeEquals(18, 5, "CHIKE");
        Assert.assertFalse(o_rangeEquals_mg202693__11);
        r.advance();
        Assert.assertEquals("heck\tCheck\tCheck\tCHOKE", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertTrue(o_rangeEquals_mg202693__3);
        Assert.assertFalse(o_rangeEquals_mg202693__4);
        Assert.assertFalse(o_rangeEquals_mg202693__5);
        Assert.assertTrue(o_rangeEquals_mg202693__6);
        Assert.assertFalse(o_rangeEquals_mg202693__7);
        Assert.assertTrue(o_rangeEquals_mg202693__8);
        Assert.assertFalse(o_rangeEquals_mg202693__9);
        Assert.assertTrue(o_rangeEquals_mg202693__10);
        Assert.assertFalse(o_rangeEquals_mg202693__11);
    }

    @Test(timeout = 10000)
    public void rangeEqualslitString202496_failAssert882() throws Exception {
        try {
            CharacterReader r = new CharacterReader("one");
            r.rangeEquals(0, 5, "Check");
            r.rangeEquals(0, 5, "CHOKE");
            r.rangeEquals(0, 5, "Chec");
            r.rangeEquals(6, 5, "Check");
            r.rangeEquals(6, 5, "Chuck");
            r.rangeEquals(12, 5, "Check");
            r.rangeEquals(12, 5, "Cheeky");
            r.rangeEquals(18, 5, "CHOKE");
            r.rangeEquals(18, 5, "CHIKE");
            org.junit.Assert.fail("rangeEqualslitString202496 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("6", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void rangeEqualslitNum202578_failAssert865_mg237634() throws Exception {
        try {
            char __DSPOT_c_9644 = 'X';
            CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE");
            Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            r.rangeEquals(Integer.MAX_VALUE, 5, "Check");
            r.rangeEquals(0, 5, "CHOKE");
            r.rangeEquals(0, 5, "Chec");
            r.rangeEquals(6, 5, "Check");
            r.rangeEquals(6, 5, "Chuck");
            r.rangeEquals(12, 5, "Check");
            r.rangeEquals(12, 5, "Cheeky");
            r.rangeEquals(18, 5, "CHOKE");
            r.rangeEquals(18, 5, "CHIKE");
            org.junit.Assert.fail("rangeEqualslitNum202578 should have thrown ArrayIndexOutOfBoundsException");
            r.consumeTo(__DSPOT_c_9644);
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void rangeEqualslitNum202613_mg237198() throws Exception {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE");
        Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_rangeEqualslitNum202613__3 = r.rangeEquals(0, 5, "Check");
        boolean o_rangeEqualslitNum202613__4 = r.rangeEquals(0, 5, "CHOKE");
        boolean o_rangeEqualslitNum202613__5 = r.rangeEquals(0, 5, "Chec");
        boolean o_rangeEqualslitNum202613__6 = r.rangeEquals(5, 5, "Check");
        boolean o_rangeEqualslitNum202613__7 = r.rangeEquals(6, 5, "Chuck");
        boolean o_rangeEqualslitNum202613__8 = r.rangeEquals(12, 5, "Check");
        boolean o_rangeEqualslitNum202613__9 = r.rangeEquals(12, 5, "Cheeky");
        boolean o_rangeEqualslitNum202613__10 = r.rangeEquals(18, 5, "CHOKE");
        boolean o_rangeEqualslitNum202613__11 = r.rangeEquals(18, 5, "CHIKE");
        r.advance();
        Assert.assertEquals("heck\tCheck\tCheck\tCHOKE", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void rangeEqualslitNum202616litString204226_failAssert925() throws Exception {
        try {
            CharacterReader r = new CharacterReader(":");
            boolean o_rangeEqualslitNum202616__3 = r.rangeEquals(0, 5, "Check");
            boolean o_rangeEqualslitNum202616__4 = r.rangeEquals(0, 5, "CHOKE");
            boolean o_rangeEqualslitNum202616__5 = r.rangeEquals(0, 5, "Chec");
            boolean o_rangeEqualslitNum202616__6 = r.rangeEquals(0, 5, "Check");
            boolean o_rangeEqualslitNum202616__7 = r.rangeEquals(0, 5, "Check");
            boolean o_rangeEqualslitNum202616__8 = r.rangeEquals(6, 5, "Chuck");
            boolean o_rangeEqualslitNum202616__9 = r.rangeEquals(12, 5, "Check");
            boolean o_rangeEqualslitNum202616__10 = r.rangeEquals(12, 5, "Cheeky");
            boolean o_rangeEqualslitNum202616__11 = r.rangeEquals(18, 5, "CHOKE");
            boolean o_rangeEqualslitNum202616__12 = r.rangeEquals(18, 5, "CHIKE");
            org.junit.Assert.fail("rangeEqualslitNum202616litString204226 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("6", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void rangeEquals_add202691litString203383_mg273798() throws Exception {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE");
        Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_rangeEquals_add202691__3 = r.rangeEquals(0, 5, "Check");
        boolean o_rangeEquals_add202691__4 = r.rangeEquals(0, 5, ":");
        boolean o_rangeEquals_add202691__5 = r.rangeEquals(0, 5, "Chec");
        boolean o_rangeEquals_add202691__6 = r.rangeEquals(6, 5, "Check");
        boolean o_rangeEquals_add202691__7 = r.rangeEquals(6, 5, "Chuck");
        boolean o_rangeEquals_add202691__8 = r.rangeEquals(12, 5, "Check");
        boolean o_rangeEquals_add202691__9 = r.rangeEquals(12, 5, "Cheeky");
        boolean o_rangeEquals_add202691__10 = r.rangeEquals(18, 5, "CHOKE");
        boolean o_rangeEquals_add202691__11 = r.rangeEquals(18, 5, "CHOKE");
        boolean o_rangeEquals_add202691__12 = r.rangeEquals(18, 5, "CHIKE");
        int o_rangeEquals_add202691litString203383_mg273798__33 = r.pos();
        Assert.assertEquals(0, ((int) (o_rangeEquals_add202691litString203383_mg273798__33)));
        Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void rangeEqualslitNum202613_add235446litString245650_failAssert965() throws Exception {
        try {
            CharacterReader r = new CharacterReader(":");
            boolean o_rangeEqualslitNum202613__3 = r.rangeEquals(0, 5, "Check");
            boolean o_rangeEqualslitNum202613_add235446__6 = r.rangeEquals(0, 5, "CHOKE");
            boolean o_rangeEqualslitNum202613__4 = r.rangeEquals(0, 5, "CHOKE");
            boolean o_rangeEqualslitNum202613__5 = r.rangeEquals(0, 5, "Chec");
            boolean o_rangeEqualslitNum202613__6 = r.rangeEquals(5, 5, "Check");
            boolean o_rangeEqualslitNum202613__7 = r.rangeEquals(6, 5, "Chuck");
            boolean o_rangeEqualslitNum202613__8 = r.rangeEquals(12, 5, "Check");
            boolean o_rangeEqualslitNum202613__9 = r.rangeEquals(12, 5, "Cheeky");
            boolean o_rangeEqualslitNum202613__10 = r.rangeEquals(18, 5, "CHOKE");
            boolean o_rangeEqualslitNum202613__11 = r.rangeEquals(18, 5, "CHIKE");
            org.junit.Assert.fail("rangeEqualslitNum202613_add235446litString245650 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("5", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void rangeEquals_add202692_mg236763_add272722() throws Exception {
        CharacterReader r = new CharacterReader("Check\tCheck\tCheck\tCHOKE");
        Assert.assertEquals("Check\tCheck\tCheck\tCHOKE", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_rangeEquals_add202692__3 = r.rangeEquals(0, 5, "Check");
        boolean o_rangeEquals_add202692__4 = r.rangeEquals(0, 5, "CHOKE");
        boolean o_rangeEquals_add202692__5 = r.rangeEquals(0, 5, "Chec");
        boolean o_rangeEquals_add202692__6 = r.rangeEquals(6, 5, "Check");
        boolean o_rangeEquals_add202692__7 = r.rangeEquals(6, 5, "Chuck");
        boolean o_rangeEquals_add202692__8 = r.rangeEquals(12, 5, "Check");
        boolean o_rangeEquals_add202692__9 = r.rangeEquals(12, 5, "Cheeky");
        boolean o_rangeEquals_add202692_mg236763_add272722__24 = r.rangeEquals(18, 5, "CHOKE");
        Assert.assertTrue(o_rangeEquals_add202692_mg236763_add272722__24);
        boolean o_rangeEquals_add202692__10 = r.rangeEquals(18, 5, "CHOKE");
        boolean o_rangeEquals_add202692__11 = r.rangeEquals(18, 5, "CHIKE");
        boolean o_rangeEquals_add202692__12 = r.rangeEquals(18, 5, "CHIKE");
        r.advance();
        Assert.assertEquals("heck\tCheck\tCheck\tCHOKE", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        Assert.assertTrue(o_rangeEquals_add202692_mg236763_add272722__24);
    }

    @Test(timeout = 10000)
    public void emptylitString95072() throws Exception {
        CharacterReader r = new CharacterReader("Check");
        Assert.assertEquals("Check", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_emptylitString95072__3 = r.matchConsume("One");
        Assert.assertFalse(o_emptylitString95072__3);
        r.isEmpty();
        r = new CharacterReader("Two");
        Assert.assertEquals("Two", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String two = r.consumeToEnd();
        Assert.assertEquals("Two", two);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_emptylitString95072__3);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void emptylitString95095() throws Exception {
        CharacterReader r = new CharacterReader("One");
        Assert.assertEquals("One", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_emptylitString95095__3 = r.matchConsume("One");
        Assert.assertTrue(o_emptylitString95095__3);
        r.isEmpty();
        r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String two = r.consumeToEnd();
        Assert.assertEquals(":", two);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertTrue(o_emptylitString95095__3);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void emptylitString95077litString95450() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_emptylitString95077__3 = r.matchConsume("One");
        r.isEmpty();
        r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String two = r.consumeToEnd();
        Assert.assertEquals(":", two);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void emptylitString95075litString95421() throws Exception {
        CharacterReader r = new CharacterReader("Oe");
        Assert.assertEquals("Oe", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_emptylitString95075__3 = r.matchConsume("Osne");
        r.isEmpty();
        r = new CharacterReader("Two");
        Assert.assertEquals("Two", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String two = r.consumeToEnd();
        Assert.assertEquals("Two", two);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void emptylitString95072_add95740litString96830() throws Exception {
        CharacterReader r = new CharacterReader("Check");
        Assert.assertEquals("Check", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).isEmpty();
        boolean o_emptylitString95072__3 = r.matchConsume("OAne");
        r.isEmpty();
        r = new CharacterReader("Two");
        Assert.assertEquals("Two", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String two = r.consumeToEnd();
        Assert.assertEquals("Two", two);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void emptylitString95072_add95740_add99288() throws Exception {
        CharacterReader r = new CharacterReader("Check");
        Assert.assertEquals("Check", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).isEmpty();
        boolean o_emptylitString95072_add95740_add99288__4 = r.matchConsume("One");
        Assert.assertFalse(o_emptylitString95072_add95740_add99288__4);
        boolean o_emptylitString95072__3 = r.matchConsume("One");
        r.isEmpty();
        r = new CharacterReader("Two");
        Assert.assertEquals("Two", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String two = r.consumeToEnd();
        Assert.assertEquals("Two", two);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_emptylitString95072_add95740_add99288__4);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void empty_mg95099_remove96003litString98541() throws Exception {
        CharacterReader r = new CharacterReader("One");
        Assert.assertEquals("One", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_empty_mg95099__3 = r.matchConsume("One");
        r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        String two = r.consumeToEnd();
        Assert.assertEquals("", two);
        r.advance();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", two);
    }

    @Test(timeout = 10000)
    public void emptylitString95079litString95308litString98278() throws Exception {
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_emptylitString95079__3 = r.matchConsume("{ne");
        r.isEmpty();
        r = new CharacterReader("\n");
        Assert.assertEquals("\n", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        String two = r.consumeToEnd();
        Assert.assertEquals("\n", two);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
    }

    @Test(timeout = 10000)
    public void empty_mg95100null96198_failAssert418litString98829() throws Exception {
        try {
            char __DSPOT_c_5168 = 't';
            CharacterReader r = new CharacterReader("One");
            Assert.assertEquals("One", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            boolean o_empty_mg95100__4 = r.matchConsume(null);
            r.isEmpty();
            r = new CharacterReader("\n");
            String two = r.consumeToEnd();
            String o_empty_mg95100__10 = r.consumeTo(__DSPOT_c_5168);
            org.junit.Assert.fail("empty_mg95100null96198 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitString69616() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_consumeToNonexistentEndWhenAtAndlitString69616__3 = r.matchConsume("<!");
        Assert.assertFalse(o_consumeToNonexistentEndWhenAtAndlitString69616__3);
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        String after = r.consumeTo('>');
        Assert.assertEquals("", after);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_consumeToNonexistentEndWhenAtAndlitString69616__3);
        Assert.assertEquals("", after);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitString69615() throws Exception {
        CharacterReader r = new CharacterReader("u");
        Assert.assertEquals("u", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_consumeToNonexistentEndWhenAtAndlitString69615__3 = r.matchConsume("<!");
        Assert.assertFalse(o_consumeToNonexistentEndWhenAtAndlitString69615__3);
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        String after = r.consumeTo('>');
        Assert.assertEquals("u", after);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_consumeToNonexistentEndWhenAtAndlitString69615__3);
        Assert.assertEquals("u", after);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAnd_mg69636() throws Exception {
        char[] __DSPOT_chars_3735 = new char[]{ '#', '#', '@' };
        CharacterReader r = new CharacterReader("<!");
        Assert.assertEquals("<!", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_consumeToNonexistentEndWhenAtAnd_mg69636__4 = r.matchConsume("<!");
        Assert.assertTrue(o_consumeToNonexistentEndWhenAtAnd_mg69636__4);
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        String after = r.consumeTo('>');
        Assert.assertEquals("", after);
        String o_consumeToNonexistentEndWhenAtAnd_mg69636__10 = r.consumeToAny(__DSPOT_chars_3735);
        Assert.assertEquals("", o_consumeToNonexistentEndWhenAtAnd_mg69636__10);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertTrue(o_consumeToNonexistentEndWhenAtAnd_mg69636__4);
        Assert.assertEquals("", after);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitString69616_mg70439() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_consumeToNonexistentEndWhenAtAndlitString69616__3 = r.matchConsume("<!");
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        String after = r.consumeTo('>');
        Assert.assertEquals("", after);
        int o_consumeToNonexistentEndWhenAtAndlitString69616_mg70439__11 = r.pos();
        Assert.assertEquals(0, ((int) (o_consumeToNonexistentEndWhenAtAndlitString69616_mg70439__11)));
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", after);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndnull69639_failAssert203litChar70040() throws Exception {
        try {
            CharacterReader r = new CharacterReader("<!");
            Assert.assertEquals("<!", ((CharacterReader) (r)).toString());
            Assert.assertFalse(((CharacterReader) (r)).isEmpty());
            r.matchConsume(null);
            r.isEmpty();
            String after = r.consumeTo('?');
            r.isEmpty();
            r.isEmpty();
            org.junit.Assert.fail("consumeToNonexistentEndWhenAtAndnull69639 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitChar69628litString69796() throws Exception {
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_consumeToNonexistentEndWhenAtAndlitChar69628__3 = r.matchConsume("<!");
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        String after = r.consumeTo('=');
        Assert.assertEquals(":", after);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(":", after);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAnd_mg69635litChar69986() throws Exception {
        char __DSPOT_c_3734 = 'v';
        CharacterReader r = new CharacterReader("<!");
        Assert.assertEquals("<!", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_consumeToNonexistentEndWhenAtAnd_mg69635__4 = r.matchConsume("<!");
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        String after = r.consumeTo('>');
        Assert.assertEquals("", after);
        String o_consumeToNonexistentEndWhenAtAnd_mg69635__10 = r.consumeTo(__DSPOT_c_3734);
        Assert.assertEquals("", o_consumeToNonexistentEndWhenAtAnd_mg69635__10);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", after);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitChar69628_mg70395_add73953() throws Exception {
        CharacterReader r = new CharacterReader("<!");
        Assert.assertEquals("<!", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).isEmpty();
        boolean o_consumeToNonexistentEndWhenAtAndlitChar69628__3 = r.matchConsume("<!");
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        String after = r.consumeTo('=');
        Assert.assertEquals("", after);
        r.advance();
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", after);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitChar69628_mg70396_add74854() throws Exception {
        char __DSPOT_c_3750 = ')';
        CharacterReader r = new CharacterReader("<!");
        Assert.assertEquals("<!", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_consumeToNonexistentEndWhenAtAndlitChar69628__3 = r.matchConsume("<!");
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        String after = r.consumeTo('=');
        Assert.assertEquals("", after);
        String o_consumeToNonexistentEndWhenAtAndlitChar69628_mg70396__12 = r.consumeTo(__DSPOT_c_3750);
        Assert.assertEquals("", o_consumeToNonexistentEndWhenAtAndlitChar69628_mg70396__12);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("", after);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitString69616null70504_failAssert207_add74918() throws Exception {
        try {
            CharacterReader r = new CharacterReader("");
            Assert.assertEquals("", ((CharacterReader) (r)).toString());
            Assert.assertTrue(((CharacterReader) (r)).isEmpty());
            r.matchConsume(null);
            boolean o_consumeToNonexistentEndWhenAtAndlitString69616__3 = r.matchConsume(null);
            r.isEmpty();
            r.isEmpty();
            r.isEmpty();
            String after = r.consumeTo('>');
            org.junit.Assert.fail("consumeToNonexistentEndWhenAtAndlitString69616null70504 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitString69614litString69825_add73833() throws Exception {
        CharacterReader r = new CharacterReader("something Two Three Four");
        Assert.assertEquals("something Two Three Four", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        boolean o_consumeToNonexistentEndWhenAtAndlitString69614__3 = r.matchConsume("ne Two Three");
        r.isEmpty();
        String after = r.consumeTo('>');
        Assert.assertEquals("something Two Three Four", after);
        r.isEmpty();
        r.isEmpty();
        r.isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals("something Two Three Four", after);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitString69616_remove70331_add74328() throws Exception {
        CharacterReader r = new CharacterReader("");
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        boolean o_consumeToNonexistentEndWhenAtAndlitString69616_remove70331_add74328__3 = r.matchConsume("<!");
        Assert.assertFalse(o_consumeToNonexistentEndWhenAtAndlitString69616_remove70331_add74328__3);
        boolean o_consumeToNonexistentEndWhenAtAndlitString69616__3 = r.matchConsume("<!");
        r.isEmpty();
        r.isEmpty();
        String after = r.consumeTo('>');
        Assert.assertEquals("", after);
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertFalse(o_consumeToNonexistentEndWhenAtAndlitString69616_remove70331_add74328__3);
    }

    @Test(timeout = 10000)
    public void consumeToNonexistentEndWhenAtAndlitChar69627_add70084litString71138() throws Exception {
        CharacterReader r = new CharacterReader(":");
        Assert.assertEquals(":", ((CharacterReader) (r)).toString());
        Assert.assertFalse(((CharacterReader) (r)).isEmpty());
        ((CharacterReader) (r)).isEmpty();
        boolean o_consumeToNonexistentEndWhenAtAndlitChar69627__3 = r.matchConsume("<!");
        r.isEmpty();
        String after = r.consumeTo('?');
        Assert.assertEquals(":", after);
        r.isEmpty();
        r.isEmpty();
        Assert.assertEquals("", ((CharacterReader) (r)).toString());
        Assert.assertTrue(((CharacterReader) (r)).isEmpty());
        Assert.assertEquals(":", after);
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

