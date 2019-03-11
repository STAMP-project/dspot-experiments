package com.jsoniter.output;


import com.jsoniter.spi.Config;
import com.jsoniter.spi.JsoniterSpi;
import java.io.IOException;
import junit.framework.TestCase;


public class TestStreamBuffer extends TestCase {
    public void test_write_string() throws IOException {
        JsonStream jsonStream = new JsonStream(null, 32);
        jsonStream.writeVal("01234567");
        jsonStream.writeVal("01234567");
        jsonStream.writeVal("012345678");
        jsonStream.writeVal("");
        TestCase.assertEquals(33, jsonStream.buffer().len());
    }

    public void test_write_raw() throws IOException {
        JsonStream jsonStream = new JsonStream(null, 32);
        jsonStream.writeRaw("0123456789");
        jsonStream.writeRaw("0123456789");
        jsonStream.writeRaw("0123456789");
        jsonStream.writeRaw("0123456789");
        TestCase.assertEquals(40, jsonStream.buffer().len());
    }

    public void test_write_bytes() throws IOException {
        JsonStream jsonStream = new JsonStream(null, 32);
        jsonStream.write("0123456789".getBytes());
        jsonStream.write("0123456789".getBytes());
        jsonStream.write("0123456789".getBytes());
        jsonStream.write("0123456789".getBytes());
        TestCase.assertEquals(40, jsonStream.buffer().len());
    }

    public void test_write_indention() throws IOException {
        Config oldConfig = JsoniterSpi.getCurrentConfig();
        try {
            JsoniterSpi.setCurrentConfig(new Config.Builder().indentionStep(32).build());
            JsonStream jsonStream = new JsonStream(null, 32);
            jsonStream.writeArrayStart();
            jsonStream.writeIndention();
            TestCase.assertEquals(34, jsonStream.buffer().len());
        } finally {
            JsoniterSpi.setCurrentConfig(oldConfig);
        }
    }

    public void test_write_int() throws IOException {
        JsonStream jsonStream = new JsonStream(null, 32);
        jsonStream.writeVal(123456789);
        jsonStream.writeVal(123456789);
        jsonStream.writeVal(123456789);
        jsonStream.writeVal(123456789);
        TestCase.assertEquals(36, jsonStream.buffer().len());
    }

    public void test_write_long() throws IOException {
        JsonStream jsonStream = new JsonStream(null, 32);
        jsonStream.writeVal(123456789L);
        jsonStream.writeVal(123456789L);
        jsonStream.writeVal(123456789L);
        jsonStream.writeVal(123456789L);
        TestCase.assertEquals(36, jsonStream.buffer().len());
    }
}

