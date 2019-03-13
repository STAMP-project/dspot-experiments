/**
 * ========================================================================
 */
/**
 * Copyright (C) 2016 Alex Shvid
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff;


import MessagePack.UTF8;
import io.protostuff.runtime.RuntimeSchema;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple test for msgpack objects
 *
 * @author Alex Shvid
 */
public class MsgpackSimpleTest {
    public static final Schema<MsgpackSimpleTest.ExampleMessage> SCHEMA = RuntimeSchema.getSchema(MsgpackSimpleTest.ExampleMessage.class);

    public static final Schema<MsgpackSimpleTest.FooMessage> FOO_SCHEMA = RuntimeSchema.getSchema(MsgpackSimpleTest.FooMessage.class);

    @Test
    public void testXIO() throws IOException {
        MsgpackSimpleTest.InnerMessage inner = new MsgpackSimpleTest.InnerMessage();
        inner.inner1 = "inner".getBytes();
        inner.inner2 = 12.34;
        MsgpackSimpleTest.ExampleMessage message = new MsgpackSimpleTest.ExampleMessage();
        message.field1 = 42;
        message.field2 = "hello";
        message.field3 = Arrays.asList(true, false, true);
        message.field4 = inner;
        byte[] xdata = MsgpackXIOUtil.toByteArray(message, MsgpackSimpleTest.SCHEMA, false, LinkedBuffer.allocate());
        byte[] data = MsgpackIOUtil.toByteArray(message, MsgpackSimpleTest.SCHEMA, false);
        // System.out.println(Arrays.toString(xdata));
        // System.out.println(Arrays.toString(data));
        Assert.assertTrue(Arrays.equals(data, xdata));
    }

    @Test
    public void testFooArray() throws IOException {
        MsgpackSimpleTest.FooMessage foo = new MsgpackSimpleTest.FooMessage();
        foo.field = Arrays.asList("a", "b");
        byte[] xdata = MsgpackXIOUtil.toByteArray(foo, MsgpackSimpleTest.FOO_SCHEMA, false, LinkedBuffer.allocate());
        byte[] data = MsgpackIOUtil.toByteArray(foo, MsgpackSimpleTest.FOO_SCHEMA, false);
        Assert.assertTrue(Arrays.equals(data, xdata));
    }

    @Test
    public void testXIOCharset() throws IOException {
        MsgpackSimpleTest.ExampleMessage message = new MsgpackSimpleTest.ExampleMessage();
        message.field2 = "?";
        byte[] xdata = MsgpackXIOUtil.toByteArray(message, MsgpackSimpleTest.SCHEMA, false, LinkedBuffer.allocate());
        byte[] data = MsgpackIOUtil.toByteArray(message, MsgpackSimpleTest.SCHEMA, false);
        Assert.assertTrue(Arrays.equals(data, xdata));
    }

    @Test
    public void testString() throws IOException {
        String string = "?";
        byte[] data = string.getBytes(UTF8);
        // System.out.println(Arrays.toString(data));
        LinkedBuffer lb = LinkedBuffer.allocate();
        WriteSession session = new WriteSession(lb);
        StringSerializer.writeUTF8(((CharSequence) (string)), session, session.tail);
        byte[] xdata = session.toByteArray();
        // System.out.println(Arrays.toString(xdata));
        Assert.assertTrue(Arrays.equals(data, xdata));
    }

    @Test
    public void testFooRepeated() throws Exception {
        ArrayList<MsgpackSimpleTest.FooMessage> foos = new ArrayList<MsgpackSimpleTest.FooMessage>();
        foos.add(new MsgpackSimpleTest.FooMessage("1", "2"));
        foos.add(new MsgpackSimpleTest.FooMessage("a", "b", "c"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MsgpackXIOUtil.writeListTo(out, foos, MsgpackSimpleTest.FOO_SCHEMA, false, LinkedBuffer.allocate());
        byte[] xdata = out.toByteArray();
        out = new ByteArrayOutputStream();
        MsgpackIOUtil.writeListTo(out, foos, MsgpackSimpleTest.FOO_SCHEMA, false);
        byte[] data = out.toByteArray();
        Assert.assertTrue(Arrays.equals(data, xdata));
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        List<MsgpackSimpleTest.FooMessage> parsedFoos = MsgpackIOUtil.parseListFrom(in, MsgpackSimpleTest.FOO_SCHEMA, false);
        Assert.assertEquals(foos.size(), parsedFoos.size());
        int i = 0;
        for (MsgpackSimpleTest.FooMessage f : parsedFoos) {
            Assert.assertEquals(foos.get((i++)).field, f.field);
        }
    }

    static class FooMessage {
        List<String> field;

        public FooMessage() {
        }

        public FooMessage(String... values) {
            this.field = Arrays.asList(values);
        }
    }

    static class ExampleMessage {
        public int field1;

        public String field2;

        public List<Boolean> field3;

        public MsgpackSimpleTest.InnerMessage field4;
    }

    static class InnerMessage {
        public byte[] inner1;

        public double inner2;
    }
}

