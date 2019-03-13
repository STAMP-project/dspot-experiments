/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.kryo;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.ListCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.KV;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test targeted at {@link KryoCoder}.
 */
public class KryoCoderTest {
    private static final PipelineOptions OPTIONS = PipelineOptionsFactory.create();

    @Test
    public void testBasicCoding() throws IOException {
        final KryoCoder<KryoCoderTest.ClassToBeEncoded> coder = KryoCoder.of(KryoCoderTest.OPTIONS, ( k) -> k.register(.class));
        assertEncoding(coder);
    }

    @Test(expected = CoderException.class)
    public void testWrongRegistrarCoding() throws IOException {
        KryoCoderTest.OPTIONS.as(KryoOptions.class).setKryoRegistrationRequired(true);
        final KryoCoder<KryoCoderTest.ClassToBeEncoded> coder = KryoCoder.of(KryoCoderTest.OPTIONS);
        assertEncoding(coder);
    }

    @Test(expected = CoderException.class)
    public void testWrongRegistrarDecoding() throws IOException {
        final KryoRegistrar registrarCoding = ( k) -> k.register(.class);
        final KryoRegistrar registrarDecoding = ( k) -> {
            // No-op
        };
        final KryoCoder<KryoCoderTest.ClassToBeEncoded> coderToEncode = KryoCoder.of(KryoCoderTest.OPTIONS, registrarCoding);
        final KryoCoder<KryoCoderTest.ClassToBeEncoded> coderToDecode = KryoCoder.of(KryoCoderTest.OPTIONS, registrarDecoding);
        assertEncoding(coderToEncode, coderToDecode);
    }

    @Test
    public void testCodingOfTwoClassesInSerial() throws IOException {
        final KryoRegistrar registrar = ( k) -> {
            k.register(.class);
            k.register(.class);
        };
        final KryoCoder<KryoCoderTest.ClassToBeEncoded> coder = KryoCoder.of(KryoCoderTest.OPTIONS, registrar);
        final KryoCoder<KryoCoderTest.TestClass> secondCoder = KryoCoder.of(KryoCoderTest.OPTIONS, registrar);
        final KryoCoderTest.ClassToBeEncoded originalValue = new KryoCoderTest.ClassToBeEncoded("XyZ", 42, Double.NaN);
        final KryoCoderTest.TestClass secondOriginalValue = new KryoCoderTest.TestClass("just a parameter");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        coder.encode(originalValue, outputStream);
        secondCoder.encode(secondOriginalValue, outputStream);
        final byte[] buf = outputStream.toByteArray();
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
        final KryoCoderTest.ClassToBeEncoded decodedValue = coder.decode(inputStream);
        final KryoCoderTest.TestClass secondDecodedValue = secondCoder.decode(inputStream);
        Assert.assertNotNull(decodedValue);
        Assert.assertEquals(originalValue, decodedValue);
        Assert.assertNotNull(secondDecodedValue);
        Assert.assertNotNull(secondDecodedValue.param);
        Assert.assertEquals("just a parameter", secondDecodedValue.param);
    }

    /**
     * Test whenever the {@link KryoCoder} is serializable.
     */
    @Test
    public void testCoderSerialization() throws IOException, ClassNotFoundException {
        final KryoRegistrar registrar = ( k) -> k.register(.class);
        final KryoCoder<KryoCoderTest.ClassToBeEncoded> coder = KryoCoder.of(KryoCoderTest.OPTIONS, registrar);
        final ByteArrayOutputStream outStr = new ByteArrayOutputStream();
        final ObjectOutputStream oss = new ObjectOutputStream(outStr);
        oss.writeObject(coder);
        oss.flush();
        oss.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(outStr.toByteArray()));
        @SuppressWarnings("unchecked")
        KryoCoder<KryoCoderTest.ClassToBeEncoded> coderDeserialized = ((KryoCoder<KryoCoderTest.ClassToBeEncoded>) (ois.readObject()));
        assertEncoding(coder, coderDeserialized);
    }

    @Test
    public void testCodingWithKvCoderKeyIsKryoCoder() throws IOException {
        final KryoRegistrar registrar = ( k) -> k.register(.class);
        final ListCoder<Void> listCoder = ListCoder.of(VoidCoder.of());
        final KvCoder<KryoCoderTest.TestClass, List<Void>> kvCoder = KvCoder.of(KryoCoder.of(KryoCoderTest.OPTIONS, registrar), listCoder);
        final List<Void> inputValue = new ArrayList<>();
        inputValue.add(null);
        inputValue.add(null);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final KryoCoderTest.TestClass inputKey = new KryoCoderTest.TestClass("something");
        kvCoder.encode(KV.of(inputKey, inputValue), byteArrayOutputStream);
        final KV<KryoCoderTest.TestClass, List<Void>> decoded = kvCoder.decode(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        Assert.assertNotNull(decoded);
        Assert.assertNotNull(decoded.getKey());
        Assert.assertEquals(inputKey, decoded.getKey());
        Assert.assertNotNull(decoded.getValue());
        Assert.assertEquals(inputValue, decoded.getValue());
    }

    @Test
    public void testCodingWithKvCoderValueIsKryoCoder() throws IOException {
        final KryoRegistrar registrar = ( k) -> k.register(.class);
        final KvCoder<String, KryoCoderTest.TestClass> kvCoder = KvCoder.of(StringUtf8Coder.of(), KryoCoder.of(KryoCoderTest.OPTIONS, registrar));
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final String inputKey = "key";
        final KryoCoderTest.TestClass inputValue = new KryoCoderTest.TestClass("something");
        kvCoder.encode(KV.of(inputKey, inputValue), byteArrayOutputStream);
        final KV<String, KryoCoderTest.TestClass> decoded = kvCoder.decode(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        Assert.assertNotNull(decoded);
        Assert.assertNotNull(decoded.getKey());
        Assert.assertEquals(inputKey, decoded.getKey());
        Assert.assertNotNull(decoded.getValue());
        Assert.assertEquals(inputValue, decoded.getValue());
    }

    @Test
    public void testCodingWithKvCoderClassToBeEncoded() throws IOException {
        final KryoRegistrar registrar = ( k) -> {
            k.register(.class);
            k.register(.class);
        };
        final ListCoder<Void> listCoder = ListCoder.of(VoidCoder.of());
        final KvCoder<KryoCoderTest.ClassToBeEncoded, List<Void>> kvCoder = KvCoder.of(KryoCoder.of(KryoCoderTest.OPTIONS, registrar), listCoder);
        final List<Void> inputValue = new ArrayList<>();
        inputValue.add(null);
        inputValue.add(null);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final KryoCoderTest.ClassToBeEncoded inputKey = new KryoCoderTest.ClassToBeEncoded("something", 1, 0.2);
        kvCoder.encode(KV.of(inputKey, inputValue), byteArrayOutputStream);
        final KV<KryoCoderTest.ClassToBeEncoded, List<Void>> decoded = kvCoder.decode(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        Assert.assertNotNull(decoded);
        Assert.assertNotNull(decoded.getKey());
        Assert.assertEquals(inputKey, decoded.getKey());
        Assert.assertNotNull(decoded.getValue());
        Assert.assertEquals(inputValue, decoded.getValue());
    }

    private static class ClassToBeEncoded {
        private String firstField;

        private Integer secondField;

        private Double thirdField;

        ClassToBeEncoded(String firstField, Integer secondField, Double thirdField) {
            this.firstField = firstField;
            this.secondField = secondField;
            this.thirdField = thirdField;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            KryoCoderTest.ClassToBeEncoded that = ((KryoCoderTest.ClassToBeEncoded) (o));
            return ((Objects.equals(firstField, that.firstField)) && (Objects.equals(secondField, that.secondField))) && (Objects.equals(thirdField, that.thirdField));
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstField, secondField, thirdField);
        }
    }

    static class TestClass {
        String param;

        TestClass(String param) {
            this.param = param;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            KryoCoderTest.TestClass testClass = ((KryoCoderTest.TestClass) (o));
            return Objects.equals(param, testClass.param);
        }

        @Override
        public int hashCode() {
            return Objects.hash(param);
        }
    }
}

