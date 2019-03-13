package com.jsoniter;


import com.jsoniter.spi.JsonException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import junit.framework.TestCase;
import org.junit.experimental.categories.Category;


@Category(StreamingCategory.class)
public class TestIO extends TestCase {
    public void test_read_byte() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("1".getBytes()), 4096);
        TestCase.assertEquals('1', IterImpl.readByte(iter));
        try {
            IterImpl.readByte(iter);
            TestCase.fail();
        } catch (JsonException e) {
        }
    }

    public void test_read_bytes() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("12".getBytes()), 4096);
        TestCase.assertEquals('1', IterImpl.readByte(iter));
        TestCase.assertEquals('2', IterImpl.readByte(iter));
        try {
            IterImpl.readByte(iter);
            TestCase.fail();
        } catch (JsonException e) {
        }
    }

    public void test_unread_byte() throws IOException {
        JsonIterator iter = JsonIterator.parse(new ByteArrayInputStream("12".getBytes()), 4096);
        TestCase.assertEquals('1', IterImpl.readByte(iter));
        TestCase.assertEquals('2', IterImpl.readByte(iter));
        iter.unreadByte();
        TestCase.assertEquals('2', IterImpl.readByte(iter));
        iter.unreadByte();
        iter.unreadByte();
        TestCase.assertEquals('1', IterImpl.readByte(iter));
    }
}

