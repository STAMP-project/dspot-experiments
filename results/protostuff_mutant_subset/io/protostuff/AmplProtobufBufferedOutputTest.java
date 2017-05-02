/**
 * ========================================================================
 */
/**
 * Copyright 2007-2010 David Yu dyuproject@gmail.com
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


/**
 * Test the buffering output capability of {@link ProtobufOutput}.
 *
 * @author David Yu
 * @created Oct 7, 2010
 */
public class AmplProtobufBufferedOutputTest extends io.protostuff.SerDeserTest {
    @java.lang.Override
    protected <T> void mergeDelimitedFrom(java.io.InputStream in, T message, io.protostuff.Schema<T> schema) throws java.io.IOException {
        io.protostuff.ProtobufIOUtil.mergeDelimitedFrom(in, message, schema);
    }

    @java.lang.Override
    protected <T> void writeDelimitedTo(java.io.OutputStream out, T message, io.protostuff.Schema<T> schema) throws java.io.IOException {
        io.protostuff.ProtobufIOUtil.writeDelimitedTo(out, message, schema, io.protostuff.AbstractTest.buf());
    }

    @java.lang.Override
    protected <T> void mergeFrom(byte[] data, int offset, int length, T message, io.protostuff.Schema<T> schema) throws java.io.IOException {
        io.protostuff.ProtobufIOUtil.mergeFrom(data, offset, length, message, schema);
    }

    @java.lang.Override
    protected <T> byte[] toByteArray(T message, io.protostuff.Schema<T> schema) {
        return io.protostuff.ProtobufIOUtil.toByteArray(message, schema, io.protostuff.AbstractTest.buf());
    }

    public void testNestedOverflow() {
        final int bufSize = 256;
        final io.protostuff.Bar bar = new io.protostuff.Bar();
        // reserve 3 bytes:
        // 1st: tag
        // 2nd and 3rd: delimited length (greater than 127 takes to more than one byte)
        int repeat = bufSize - 3;
        bar.setSomeString(io.protostuff.SerDeserTest.repeatChar('a', repeat));
        byte[] data = io.protostuff.ProtobufIOUtil.toByteArray(bar, bar.cachedSchema(), io.protostuff.AbstractTest.buf(bufSize));
        junit.framework.TestCase.assertEquals(bufSize, data.length);
        // additional size will be:
        // 1 (tag)
        // 1 (delimiter)
        // 1 + 1 = tag + value (10)
        // 1 + 1 + 3 = tag + delim + value ("baz")
        // 1 + 1 = tag + value (15)
        // =====
        // 11
        bar.setSomeBaz(new io.protostuff.Baz(10, "baz", 15L));
        data = io.protostuff.ProtobufIOUtil.toByteArray(bar, bar.cachedSchema(), io.protostuff.AbstractTest.buf(bufSize));
        junit.framework.TestCase.assertEquals((bufSize + 11), data.length);
        final io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        io.protostuff.ProtobufIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        junit.framework.TestCase.assertEquals(bar, parsedBar);
    }

    public void testNestedLarge() {
        final int bufSize = 256;
        final io.protostuff.Bar bar = new io.protostuff.Bar();
        // nested message size will be:
        // 1 + 1 = tag + value (10)
        // 1 + 1 + 125 = tag + delim + value ("baz")
        // 1 + 1 = tag + value (15)
        // =====
        // 131
        bar.setSomeBaz(new io.protostuff.Baz(10, io.protostuff.SerDeserTest.repeatChar('b', 125), 15L));
        // size will be:
        // 1 (tag)
        // 2 (delimited length) (nested message size is greater than 127)
        // 131 (nested message size)
        // =====
        // 134
        byte[] data = io.protostuff.ProtobufIOUtil.toByteArray(bar, bar.cachedSchema(), io.protostuff.AbstractTest.buf(bufSize));
        junit.framework.TestCase.assertEquals(134, data.length);
        final io.protostuff.Bar parsedBar = new io.protostuff.Bar();
        io.protostuff.ProtobufIOUtil.mergeFrom(data, parsedBar, parsedBar.cachedSchema());
        junit.framework.TestCase.assertEquals(bar, parsedBar);
    }
}

