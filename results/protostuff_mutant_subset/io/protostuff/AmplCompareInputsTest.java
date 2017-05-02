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
 * Benchmark to compare the deserialization speed of 2 types. CodedInput and ByteArrayInput.
 *
 * @author David Yu
 * @created Jun 22, 2010
 */
public class AmplCompareInputsTest extends io.protostuff.AbstractTest {
    public void testBenchmark() throws java.lang.Exception {
        if (!("false".equals(java.lang.System.getProperty("benchmark.skip"))))
            return ;
        
        java.lang.String dir = java.lang.System.getProperty("benchmark.output_dir");
        java.io.PrintStream out = (dir == null) ? java.lang.System.out : new java.io.PrintStream(new java.io.FileOutputStream(new java.io.File(new java.io.File(dir), (("protostuff-core-" + (java.lang.System.currentTimeMillis())) + ".txt")), true));
        int warmups = java.lang.Integer.getInteger("benchmark.warmups", 200000);
        int loops = java.lang.Integer.getInteger("benchmark.loops", 2000000);
        java.lang.String title = ("protostuff-core deserialization benchmark for " + loops) + " runs";
        out.println(title);
        out.println();
        io.protostuff.AmplCompareInputsTest.start(out, warmups, loops);
        if ((java.lang.System.out) != out)
            out.close();
        
    }

    public static void start(java.io.PrintStream out, int warmups, int loops) throws java.lang.Exception {
        for (io.protostuff.AmplCompareInputsTest.Deserializer s : io.protostuff.AmplCompareInputsTest.DESERIALIZERS)
            io.protostuff.AmplCompareInputsTest.deser(out, s, s.getName(), warmups, loops);
        
    }

    static void deser(java.io.PrintStream out, io.protostuff.AmplCompareInputsTest.Deserializer deserializer, java.lang.String name, int warmups, int loops) throws java.lang.Exception {
        final byte[] data = deserializer.getSerializer().serialize(io.protostuff.SerializableObjects.foo);
        int len = data.length;
        io.protostuff.Foo f = new io.protostuff.Foo();
        deserializer.mergeFrom(data, f);
        io.protostuff.SerializableObjects.assertEquals(io.protostuff.SerializableObjects.foo, f);
        for (int i = 0; i < warmups; i++)
            deserializer.mergeFrom(data, new io.protostuff.Foo());
        
        long start = java.lang.System.currentTimeMillis();
        for (int i = 0; i < loops; i++)
            deserializer.mergeFrom(data, new io.protostuff.Foo());
        
        long finish = java.lang.System.currentTimeMillis();
        long elapsed = finish - start;
        out.println(((((elapsed + " ms elapsed with ") + len) + " bytes for ") + name));
    }

    public static void main(java.lang.String[] args) throws java.lang.Exception {
        java.lang.String dir = java.lang.System.getProperty("benchmark.output_dir");
        java.io.PrintStream out = (dir == null) ? java.lang.System.out : new java.io.PrintStream(new java.io.FileOutputStream(new java.io.File(new java.io.File(dir), (("protostuff-core-" + (java.lang.System.currentTimeMillis())) + ".txt")), true));
        int warmups = java.lang.Integer.getInteger("benchmark.warmups", 200000);
        int loops = java.lang.Integer.getInteger("benchmark.loops", 2000000);
        java.lang.String title = ("protostuff-core deserialization benchmark for " + loops) + " runs";
        out.println(title);
        out.println();
        io.protostuff.AmplCompareInputsTest.start(out, warmups, loops);
        if ((java.lang.System.out) != out)
            out.close();
        
    }

    public interface Deserializer {
        public <T extends io.protostuff.Message<T>> void mergeFrom(byte[] data, T message) throws java.io.IOException;

        public java.lang.String getName();

        public io.protostuff.CompareOutputsTest.Serializer getSerializer();
    }

    public static final io.protostuff.AmplCompareInputsTest.Deserializer PROTOBUF_CODED_INPUT = new io.protostuff.AmplCompareInputsTest.Deserializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> void mergeFrom(byte[] data, T message) throws java.io.IOException {
            final io.protostuff.CodedInput input = new io.protostuff.CodedInput(data, 0, data.length, false);
            message.cachedSchema().mergeFrom(input, message);
            input.checkLastTagWas(0);
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protobuf-coded-input";
        }

        @java.lang.Override
        public io.protostuff.CompareOutputsTest.Serializer getSerializer() {
            return io.protostuff.CompareOutputsTest.PROTOBUF_COMPUTED_OUTPUT;
        }
    };

    public static final io.protostuff.AmplCompareInputsTest.Deserializer PROTOSTUFF_CODED_INPUT = new io.protostuff.AmplCompareInputsTest.Deserializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> void mergeFrom(byte[] data, T message) throws java.io.IOException {
            final io.protostuff.CodedInput input = new io.protostuff.CodedInput(data, 0, data.length, true);
            message.cachedSchema().mergeFrom(input, message);
            input.checkLastTagWas(0);
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protostuff-coded-input";
        }

        @java.lang.Override
        public io.protostuff.CompareOutputsTest.Serializer getSerializer() {
            return io.protostuff.CompareOutputsTest.PROTOSTUFF_COMPUTED_OUTPUT;
        }
    };

    public static final io.protostuff.AmplCompareInputsTest.Deserializer PROTOBUF_BYTE_ARRAY_INPUT = new io.protostuff.AmplCompareInputsTest.Deserializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> void mergeFrom(byte[] data, T message) throws java.io.IOException {
            try {
                final io.protostuff.ByteArrayInput input = new io.protostuff.ByteArrayInput(data, 0, data.length, false);
                message.cachedSchema().mergeFrom(input, message);
                input.checkLastTagWas(0);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                throw io.protostuff.ProtobufException.truncatedMessage(e);
            }
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protobuf-bytearray-input";
        }

        @java.lang.Override
        public io.protostuff.CompareOutputsTest.Serializer getSerializer() {
            return io.protostuff.CompareOutputsTest.PROTOBUF_BUFFERED_OUTPUT;
        }
    };

    public static final io.protostuff.AmplCompareInputsTest.Deserializer PROTOSTUFF_BYTE_ARRAY_INPUT = new io.protostuff.AmplCompareInputsTest.Deserializer() {
        @java.lang.Override
        public <T extends io.protostuff.Message<T>> void mergeFrom(byte[] data, T message) throws java.io.IOException {
            try {
                final io.protostuff.ByteArrayInput input = new io.protostuff.ByteArrayInput(data, 0, data.length, true);
                message.cachedSchema().mergeFrom(input, message);
                input.checkLastTagWas(0);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                throw io.protostuff.ProtobufException.truncatedMessage(e);
            }
        }

        @java.lang.Override
        public java.lang.String getName() {
            return "protostuff-bytearray-input";
        }

        @java.lang.Override
        public io.protostuff.CompareOutputsTest.Serializer getSerializer() {
            return io.protostuff.CompareOutputsTest.PROTOSTUFF_BUFFERED_OUTPUT;
        }
    };

    static final io.protostuff.AmplCompareInputsTest.Deserializer[] DESERIALIZERS = new io.protostuff.AmplCompareInputsTest.Deserializer[]{ io.protostuff.AmplCompareInputsTest.PROTOBUF_CODED_INPUT , io.protostuff.AmplCompareInputsTest.PROTOSTUFF_CODED_INPUT , io.protostuff.AmplCompareInputsTest.PROTOBUF_BYTE_ARRAY_INPUT , io.protostuff.AmplCompareInputsTest.PROTOSTUFF_BYTE_ARRAY_INPUT };
}

