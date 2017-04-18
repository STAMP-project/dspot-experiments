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
 * Test case for protostuff pipes and protobuf pipes.
 *
 * @author David Yu
 * @created Oct 8, 2010
 */
public class AmplProtostuffPipeTest extends io.protostuff.AbstractTest {
    public static <T> void roundTrip(T message, io.protostuff.Schema<T> schema, io.protostuff.Pipe.Schema<T> pipeSchema) throws java.lang.Exception {
        byte[] protobuf = io.protostuff.ProtobufIOUtil.toByteArray(message, schema, io.protostuff.AbstractTest.buf());
        java.io.ByteArrayInputStream protobufStream = new java.io.ByteArrayInputStream(protobuf);
        byte[] protostuff = io.protostuff.ProtostuffIOUtil.toByteArray(io.protostuff.ProtobufIOUtil.newPipe(protobuf, 0, protobuf.length), pipeSchema, io.protostuff.AbstractTest.buf());
        byte[] protostuffFromStream = io.protostuff.ProtostuffIOUtil.toByteArray(io.protostuff.ProtobufIOUtil.newPipe(protobufStream), pipeSchema, io.protostuff.AbstractTest.buf());
        junit.framework.TestCase.assertTrue(((protostuff.length) == (protostuffFromStream.length)));
        junit.framework.TestCase.assertTrue(java.util.Arrays.equals(protostuff, protostuffFromStream));
        T parsedMessage = schema.newMessage();
        io.protostuff.ProtostuffIOUtil.mergeFrom(protostuff, parsedMessage, schema);
        io.protostuff.SerializableObjects.assertEquals(message, parsedMessage);
        java.io.ByteArrayInputStream protostuffStream = new java.io.ByteArrayInputStream(protostuff);
        byte[] protobufRoundTrip = io.protostuff.ProtobufIOUtil.toByteArray(io.protostuff.ProtostuffIOUtil.newPipe(protostuff, 0, protostuff.length), pipeSchema, io.protostuff.AbstractTest.buf());
        byte[] protobufRoundTripFromStream = io.protostuff.ProtobufIOUtil.toByteArray(io.protostuff.ProtostuffIOUtil.newPipe(protostuffStream), pipeSchema, io.protostuff.AbstractTest.buf());
        junit.framework.TestCase.assertTrue(((protobufRoundTrip.length) == (protobufRoundTripFromStream.length)));
        junit.framework.TestCase.assertTrue(java.util.Arrays.equals(protobufRoundTrip, protobufRoundTripFromStream));
        junit.framework.TestCase.assertTrue(((protobufRoundTrip.length) == (protobuf.length)));
        junit.framework.TestCase.assertTrue(java.util.Arrays.equals(protobufRoundTrip, protobuf));
    }

    public void testFoo() throws java.lang.Exception {
        io.protostuff.Foo foo = io.protostuff.SerializableObjects.foo;
        io.protostuff.AmplProtostuffPipeTest.roundTrip(foo, io.protostuff.Foo.getSchema(), io.protostuff.Foo.getPipeSchema());
    }

    public void testBar() throws java.lang.Exception {
        io.protostuff.Bar bar = io.protostuff.SerializableObjects.bar;
        io.protostuff.AmplProtostuffPipeTest.roundTrip(bar, io.protostuff.Bar.getSchema(), io.protostuff.Bar.getPipeSchema());
    }

    public void testBaz() throws java.lang.Exception {
        io.protostuff.Baz baz = io.protostuff.SerializableObjects.baz;
        io.protostuff.AmplProtostuffPipeTest.roundTrip(baz, io.protostuff.Baz.getSchema(), io.protostuff.Baz.getPipeSchema());
    }
}

