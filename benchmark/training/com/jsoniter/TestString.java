package com.jsoniter;


import com.jsoniter.spi.JsonException;
import java.io.IOException;
import junit.framework.TestCase;


public class TestString extends TestCase {
    static {
        // JsonIterator.enableStreamingSupport();
    }

    public void test_ascii_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("'hello''world'".replace('\'', '"'));
        TestCase.assertEquals("hello", iter.readString());
        TestCase.assertEquals("world", iter.readString());
        iter = JsonIterator.parse("'hello''world'".replace('\'', '"'));
        TestCase.assertEquals("hello", iter.readStringAsSlice().toString());
        TestCase.assertEquals("world", iter.readStringAsSlice().toString());
    }

    public void test_ascii_string_with_escape() throws IOException {
        JsonIterator iter = JsonIterator.parse("\'he\\tllo\'".replace('\'', '"'));
        TestCase.assertEquals("he\tllo", iter.readString());
    }

    public void test_utf8_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("'??'".replace('\'', '"'));
        TestCase.assertEquals("??", iter.readString());
    }

    public void test_incomplete_escape() throws IOException {
        JsonIterator iter = JsonIterator.parse("\"\\");
        try {
            iter.readString();
            TestCase.fail();
        } catch (JsonException e) {
        }
    }

    public void test_surrogate() throws IOException {
        JsonIterator iter = JsonIterator.parse("\"\ud83d\udc4a\"");
        TestCase.assertEquals("\ud83d\udc4a", iter.readString());
    }

    public void test_larger_than_buffer() throws IOException {
        JsonIterator iter = JsonIterator.parse("'0123456789012345678901234567890123'".replace('\'', '"'));
        TestCase.assertEquals("0123456789012345678901234567890123", iter.readString());
    }

    public void test_null_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("null".replace('\'', '"'));
        TestCase.assertEquals(null, iter.readString());
    }

    public void test_incomplete_string() throws IOException {
        try {
            JsonIterator.deserialize("\"abc", String.class);
            TestCase.fail();
        } catch (JsonException e) {
        }
    }

    public void test_invalid_string() throws IOException {
        for (String str : new String[]{ "\"\\x0008\"", "\"\\u000Z\"", "\"\\u000\"", "\"\\u00\"", "\"\\u0\"", "\"\\\"", "\"\\udd1e\"", "\"\\ud834\"", "\"\\ud834\\x\"", "\"\\ud834\\ud834\"" }) {
            try {
                JsonIterator.deserialize(str, String.class);
            } catch (JsonException e) {
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }

    public void test_long_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("\"[\\\"LL\\\",\\\"MM\\\\\\/LW\\\",\\\"JY\\\",\\\"S\\\",\\\"C\\\",\\\"IN\\\",\\\"ME \\\\\\/ LE\\\"]\"");
        TestCase.assertEquals("[\"LL\",\"MM\\/LW\",\"JY\",\"S\",\"C\",\"IN\",\"ME \\/ LE\"]", iter.readString());
    }
}

