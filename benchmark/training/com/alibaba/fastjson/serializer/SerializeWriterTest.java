package com.alibaba.fastjson.serializer;


import com.alibaba.fastjson.util.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import org.junit.Test;


public class SerializeWriterTest {
    private final Logger logger = Logger.getLogger(SerializeWriterTest.class.getSimpleName());

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final SerializeWriter writer = new SerializeWriter(new OutputStreamWriter(baos));

    @Test
    public void testWriteLiteBasicStr() throws UnsupportedEncodingException {
        String targetStr = new String(IOUtils.DIGITS);
        this.doTestWrite(targetStr);
    }

    @Test
    public void testWriteLiteSpecilaStr() throws UnsupportedEncodingException {
        this.doTestWrite(this.makeSpecialChars());
    }

    @Test
    public void testWriteLargeBasicStr() throws UnsupportedEncodingException {
        String tmp = new String(IOUtils.DIGITS);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            builder.append(tmp);
        }
        this.doTestWrite(builder.toString());
    }

    @Test
    public void testWriteLargeSpecialStr() throws UnsupportedEncodingException {
        String tmp = this.makeSpecialChars();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            builder.append(tmp);
        }
        this.doTestWrite(builder.toString());
    }

    @Test
    public void test_large() throws Exception {
        SerializeWriter writer = new SerializeWriter();
        for (int i = 0; i < (1024 * 1024); ++i) {
            writer.write(i);
        }
        writer.close();
    }
}

