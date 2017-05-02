/**
 * ========================================================================
 */
/**
 * Copyright 2007-2009 David Yu dyuproject@gmail.com
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
 * Benchmark to compare the serialization speed of 3 types. CodedOutput, BufferedOutput and DeferredOutput.
 *
 * @author David Yu
 * @created Nov 13, 2009
 */
public class AmplCompareOutputsTest extends io.protostuff.AbstractTest {
    static final io.protostuff.Baz negativeBaz = new io.protostuff.Baz((-567), "negativeBaz", (-202020202));

    static final io.protostuff.Bar negativeBar = new io.protostuff.Bar((-12), "negativeBar", io.protostuff.AmplCompareOutputsTest.negativeBaz, io.protostuff.Bar.Status.STARTED, io.protostuff.ByteString.copyFromUtf8("a1"), true, (-130.031F), (-1000.0001), (-101010101));

    static final io.protostuff.Baz baz = new io.protostuff.Baz(567, "baz", 202020202);

    static final io.protostuff.Bar bar = new io.protostuff.Bar(890, "bar", io.protostuff.AmplCompareOutputsTest.baz, io.protostuff.Bar.Status.STARTED, io.protostuff.ByteString.copyFromUtf8("b2"), true, 150.051F, 2000.0002, 303030303);

    // a total of 1000 bytes
    public static final io.protostuff.Foo foo = io.protostuff.SerializableObjects.newFoo(new java.lang.Integer[]{ 90210 , -90210 , 0 }, new java.lang.String[]{ "foo" , "123456789012345678901234567890123456789012" }, new io.protostuff.Bar[]{ io.protostuff.AmplCompareOutputsTest.bar , io.protostuff.AmplCompareOutputsTest.negativeBar }, new io.protostuff.Foo.EnumSample[]{ io.protostuff.Foo.EnumSample.TYPE0 , io.protostuff.Foo.EnumSample.TYPE2 }, new io.protostuff.ByteString[]{ io.protostuff.ByteString.copyFromUtf8("ef") , io.protostuff.ByteString.copyFromUtf8("gh") , // two 350-byte bytestring.
    io.protostuff.ByteString.copyFromUtf8("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890") , io.protostuff.ByteString.copyFromUtf8("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890") }, new java.lang.Boolean[]{ true , false }, new java.lang.Float[]{ 1234.4321F , -1234.4321F , 0.0F }, new java.lang.Double[]{ 1.234567887654321E7 , -1.234567887654321E7 , 0.0 }, new java.lang.Long[]{ 7060504030201L , -7060504030201L , 0L });

    public void testFooProtobuf() throws java.lang.Exception {
        io.protostuff.Foo fooCompare = io.protostuff.AmplCompareOutputsTest.foo;
        byte[] computed = io.protostuff.AmplCompareOutputsTest.toByteArrayComputedProtobuf(fooCompare, fooCompare.cachedSchema());
        byte[] buffered = io.protostuff.AmplCompareOutputsTest.toByteArrayBufferedProtobuf(fooCompare, fooCompare.cachedSchema());
        byte[] streamed = io.protostuff.AmplCompareOutputsTest.toByteArrayStreamedProtobuf(fooCompare, fooCompare.cachedSchema());
        junit.framework.TestCase.assertTrue(((computed.length) == (buffered.length)));
        junit.framework.TestCase.assertTrue(((computed.length) == (streamed.length)));
        java.lang.String strComputed = new java.lang.String(computed, "UTF-8");
        junit.framework.TestCase.assertEquals(strComputed, new java.lang.String(buffered, "UTF-8"));
        junit.framework.TestCase.assertEquals(strComputed, new java.lang.String(streamed, "UTF-8"));
    }

    public void testFooProtostuff() throws java.lang.Exception {
        io.protostuff.Foo fooCompare = io.protostuff.AmplCompareOutputsTest.foo;
        byte[] computed = io.protostuff.AmplCompareOutputsTest.toByteArrayComputedProtostuff(fooCompare, fooCompare.cachedSchema());
        byte[] buffered = io.protostuff.AmplCompareOutputsTest.toByteArrayBufferedProtostuff(fooCompare, fooCompare.cachedSchema());
        byte[] streamed = io.protostuff.AmplCompareOutputsTest.toByteArrayStreamedProtostuff(fooCompare, fooCompare.cachedSchema());
        junit.framework.TestCase.assertTrue(((computed.length) == (buffered.length)));
        junit.framework.TestCase.assertTrue(((computed.length) == (streamed.length)));
        java.lang.String strComputed = new java.lang.String(computed, "UTF-8");
        junit.framework.TestCase.assertEquals(strComputed, new java.lang.String(buffered, "UTF-8"));
        junit.framework.TestCase.assertEquals(strComputed, new java.lang.String(streamed, "UTF-8"));
    }

    public void testBenchmark() throws java.lang.Exception {
        if (!("false".equals(java.lang.System.getProperty("benchmark.skip"))))
            return ;
        
        java.lang.String dir = java.lang.System.getProperty("benchmark.output_dir");
        java.io.PrintStream out = (dir == null) ? java.lang.System.out : new java.io.PrintStream(new java.io.FileOutputStream(new java.io.File(new java.io.File(dir), (("protostuff-core-" + (java.lang.System.currentTimeMillis())) + ".txt")), true));
        int warmups = java.lang.Integer.getInteger("benchmark.warmups", 200000);
        int loops = java.lang.Integer.getInteger("benchmark.loops", 2000000);
        java.lang.String title = ("protostuff-core serialization benchmark for " + loops) + " runs";
        out.println(title);
        out.println();
        io.protostuff.AmplCompareOutputsTest.start(io.protostuff.AmplCompareOutputsTest.foo, io.protostuff.AmplCompareOutputsTest.SERIALIZERS, out, warmups, loops);
        if ((java.lang.System.out) != out)
            out.close();
        
    }

    public static void main(java.lang.String[] args) throws java.lang.Exception {
        java.lang.String dir = java.lang.System.getProperty("benchmark.output_dir");
        java.io.PrintStream out = (dir == null) ? java.lang.System.out : new java.io.PrintStream(new java.io.FileOutputStream(new java.io.File(new java.io.File(dir), (("protostuff-core-" + (java.lang.System.currentTimeMillis())) + ".txt")), true));
        int warmups = java.lang.Integer.getInteger("benchmark.warmups", 200000);
        int loops = java.lang.Integer.getInteger("benchmark.loops", 2000000);
        java.lang.String title = ("protostuff-core serialization benchmark for " + loops) + " runs";
        out.println(title);
        out.println();
        io.protostuff.AmplCompareOutputsTest.start(io.protostuff.AmplCompareOutputsTest.foo, io.protostuff.AmplCompareOutputsTest.SERIALIZERS, out, warmups, loops);
        if ((java.lang.System.out) != out)
            out.close();
        
    }

    public static <T extends io.protostuff.Message<T>> void start(T message, io.protostuff.AmplCompareOutputsTest.Serializer[] serializers, java.io.PrintStream out, int warmups, int loops) throws java.lang.Exception {
        for (io.protostuff.AmplCompareOutputsTest.Serializer s : serializers)
            io.protostuff.AmplCompareOutputsTest.ser(message, s, out, s.getName(), warmups, loops);
        
    }

    static <T extends io.protostuff.Message<T>> void ser(T message, io.protostuff.AmplCompareOutputsTest.Serializer serializer, java.io.PrintStream out, java.lang.String name, int warmups, int loops) throws java.lang.Exception {
        int len = serializer.serialize(message).length;
        for (int i = 0; i < warmups; i++)
            serializer.serialize(message);
        
        long start = java.lang.System.currentTimeMillis();
        for (int i = 0; i < loops; i++)
            serializer.serialize(message);
        
        long finish = java.lang.System.currentTimeMillis();
        long elapsed = finish - start;
        out.println(((((elapsed + " ms elapsed with ") + len) + " bytes for ") + name));
    }

    static <T> byte[] toByteArrayComputedProtobuf(T message, io.protostuff.Schema<T> schema) {
        return io.protostuff.CodedOutput.toByteArray(message, schema, false);
    }

    static <T> byte[] toByteArrayBufferedProtobuf(T message, io.protostuff.Schema<T> schema) {
        final io.protostuff.ProtobufOutput output = new io.protostuff.ProtobufOutput(new io.protostuff.LinkedBuffer(io.protostuff.AmplCompareOutputsTest.BUF_SIZE));
        try {
            schema.writeTo(output, message);
        } catch (java.io.IOException e) {
            throw new java.lang.RuntimeException(("Serializing to a byte array threw an IOException " + "(should never happen)."), e);
        }
        return output.toByteArray();
    }

    static <T> byte[] toByteArrayStreamedProtobuf(T message, io.protostuff.Schema<T> schema) {
        final java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        final io.protostuff.CodedOutput output = new io.protostuff.CodedOutput(out, new byte[io.protostuff.AmplCompareOutputsTest.BUF_SIZE], false);
        try {
            schema.writeTo(output, message);
            output.flush();
        } catch (java.io.IOException e) {
            throw new java.lang.RuntimeException(("Serializing to a byte array threw an IOException " + "(should never happen)."), e);
        }
        return out.toByteArray();
    }

    static <T> byte[] toByteArrayComputedProtostuff(T message, io.protostuff.Schema<T> schema) {
        return io.protostuff.CodedOutput.toByteArray(message, schema, true);
    }

    static <T> byte[] toByteArrayBufferedProtostuff(T message, io.protostuff.Schema<T> schema) {
        final io.protostuff.ProtostuffOutput output = new io.protostuff.ProtostuffOutput(new io.protostuff.LinkedBuffer(io.protostuff.AmplCompareOutputsTest.BUF_SIZE));
        try {
            schema.writeTo(output, message);
        } catch (java.io.IOException e) {
            throw new java.lang.RuntimeException(("Serializing to a byte array threw an IOException " + "(should never happen)."), e);
        }
        return output.toByteArray();
    }

    static <T> byte[] toByteArrayStreamedProtostuff(T message, io.protostuff.Schema<T> schema) {
        /* final ByteArrayOutputStream out = new ByteArrayOutputStream(); try { ProtostuffIOUtil.writeTo(out, message,
        schema, new LinkedBuffer(BUF_SIZE)); } catch (IOException e) { throw new
        RuntimeException("Serializing to a byte array threw an IOException " + "(should never happen).", e); } return
        out.toByteArray();
         */
        final java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        final io.protostuff.LinkedBuffer buffer = new io.protostuff.LinkedBuffer(io.protostuff.AmplCompareOutputsTest.BUF_SIZE);
        final io.protostuff.ProtostuffOutput output = new io.protostuff.ProtostuffOutput(buffer, out);
        try {
            schema.writeTo(output, message);
            io.protostuff.LinkedBuffer.writeTo(out, buffer);
        } catch (java.io.IOException e) {
            throw new java.lang.RuntimeException(("Serializing to a byte array threw an IOException " + "(should never happen)."), e);
        }
        return out.toByteArray();
    }

    static final int BUF_SIZE = 256;

    public interface Serializer {
        public <T extends io.protostuff.Message<T>> byte[] serialize(T message);

        public java.lang.String getName();
    }

    static final io.protostuff.AmplCompareOutputsTest.Serializer PROTOBUF_COMPUTED_OUTPUT = new io.protostuff.AmplCompareOutputsTest.Serializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> byte[] serialize(T message) {
            return io.protostuff.AmplCompareOutputsTest.toByteArrayComputedProtobuf(message, message.cachedSchema());
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protobuf-computed-output";
        }
    };

    static final io.protostuff.AmplCompareOutputsTest.Serializer PROTOBUF_BUFFERED_OUTPUT = new io.protostuff.AmplCompareOutputsTest.Serializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> byte[] serialize(T message) {
            return io.protostuff.AmplCompareOutputsTest.toByteArrayBufferedProtobuf(message, message.cachedSchema());
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protobuf-buffered-output";
        }
    };

    static final io.protostuff.AmplCompareOutputsTest.Serializer PROTOBUF_STREAMED_OUTPUT = new io.protostuff.AmplCompareOutputsTest.Serializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> byte[] serialize(T message) {
            return io.protostuff.AmplCompareOutputsTest.toByteArrayStreamedProtobuf(message, message.cachedSchema());
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protobuf-streamed-output";
        }
    };

    static final io.protostuff.AmplCompareOutputsTest.Serializer PROTOSTUFF_COMPUTED_OUTPUT = new io.protostuff.AmplCompareOutputsTest.Serializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> byte[] serialize(T message) {
            return io.protostuff.AmplCompareOutputsTest.toByteArrayComputedProtostuff(message, message.cachedSchema());
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protostuff-computed-output";
        }
    };

    static final io.protostuff.AmplCompareOutputsTest.Serializer PROTOSTUFF_BUFFERED_OUTPUT = new io.protostuff.AmplCompareOutputsTest.Serializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> byte[] serialize(T message) {
            return io.protostuff.AmplCompareOutputsTest.toByteArrayBufferedProtostuff(message, message.cachedSchema());
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protostuff-buffered-output";
        }
    };

    static final io.protostuff.AmplCompareOutputsTest.Serializer PROTOSTUFF_STREAMED_OUTPUT = new io.protostuff.AmplCompareOutputsTest.Serializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> byte[] serialize(T message) {
            return io.protostuff.AmplCompareOutputsTest.toByteArrayStreamedProtostuff(message, message.cachedSchema());
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protostuff-streamed-output";
        }
    };

    static final io.protostuff.AmplCompareOutputsTest.Serializer[] SERIALIZERS = new io.protostuff.AmplCompareOutputsTest.Serializer[]{ io.protostuff.AmplCompareOutputsTest.PROTOBUF_COMPUTED_OUTPUT , io.protostuff.AmplCompareOutputsTest.PROTOBUF_BUFFERED_OUTPUT , io.protostuff.AmplCompareOutputsTest.PROTOBUF_STREAMED_OUTPUT , io.protostuff.AmplCompareOutputsTest.PROTOSTUFF_COMPUTED_OUTPUT , io.protostuff.AmplCompareOutputsTest.PROTOSTUFF_BUFFERED_OUTPUT , io.protostuff.AmplCompareOutputsTest.PROTOSTUFF_STREAMED_OUTPUT };
}

