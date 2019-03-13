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
package org.apache.beam.sdk.coders;


import Coder.Context.OUTER;
import Context.NESTED;
import GlobalWindow.Coder.INSTANCE;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.beam.sdk.coders.Coder.Context;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link NullableCoder}.
 */
@RunWith(JUnit4.class)
public class NullableCoderTest {
    private static final Coder<String> TEST_CODER = NullableCoder.of(StringUtf8Coder.of());

    private static final List<String> TEST_VALUES = Arrays.asList("", "a", "13", "hello", null, "a longer string with spaces and all that", "a string with a \n newline", "?????");

    @Test
    public void testDecodeEncodeContentsInSameOrder() throws Exception {
        for (String value : NullableCoderTest.TEST_VALUES) {
            CoderProperties.coderDecodeEncodeEqual(NullableCoderTest.TEST_CODER, value);
        }
    }

    @Test
    public void testCoderSerializable() throws Exception {
        CoderProperties.coderSerializable(NullableCoderTest.TEST_CODER);
    }

    @Test
    public void testCoderIsSerializableWithWellKnownCoderType() throws Exception {
        CoderProperties.coderSerializable(NullableCoder.of(INSTANCE));
    }

    /**
     * Generated data to check that the wire format has not changed. To regenerate, see {@code PrintBase64Encodings}.
     *
     * @see org.apache.beam.sdk.coders.PrintBase64Encodings
     */
    private static final List<String> TEST_ENCODINGS = Arrays.asList("AQ", "AWE", "ATEz", "AWhlbGxv", "AA", "AWEgbG9uZ2VyIHN0cmluZyB3aXRoIHNwYWNlcyBhbmQgYWxsIHRoYXQ", "AWEgc3RyaW5nIHdpdGggYSAKIG5ld2xpbmU", "AeOCueOCv-ODquODs-OCsA");

    @Test
    public void testWireFormatEncode() throws Exception {
        CoderProperties.coderEncodesBase64(NullableCoderTest.TEST_CODER, NullableCoderTest.TEST_VALUES, NullableCoderTest.TEST_ENCODINGS);
    }

    @Test
    public void testEncodedSize() throws Exception {
        NullableCoder<Double> coder = NullableCoder.of(DoubleCoder.of());
        Assert.assertEquals(1, coder.getEncodedElementByteSize(null));
        Assert.assertEquals(9, coder.getEncodedElementByteSize(5.0));
    }

    @Test
    public void testEncodedSizeNested() throws Exception {
        NullableCoder<String> varLenCoder = NullableCoder.of(StringUtf8Coder.of());
        Assert.assertEquals(1, varLenCoder.getEncodedElementByteSize(null));
        Assert.assertEquals(6, varLenCoder.getEncodedElementByteSize("spam"));
    }

    @Test
    public void testObserverIsCheap() throws Exception {
        NullableCoder<Double> coder = NullableCoder.of(DoubleCoder.of());
        Assert.assertTrue(coder.isRegisterByteSizeObserverCheap(5.0));
    }

    @Test
    public void testObserverIsNotCheap() throws Exception {
        NullableCoder<List<String>> coder = NullableCoder.of(ListCoder.of(StringUtf8Coder.of()));
        Assert.assertFalse(coder.isRegisterByteSizeObserverCheap(ImmutableList.of("hi", "test")));
    }

    @Test
    public void testObserverIsAlwaysCheapForNullValues() throws Exception {
        NullableCoder<List<String>> coder = NullableCoder.of(ListCoder.of(StringUtf8Coder.of()));
        Assert.assertTrue(coder.isRegisterByteSizeObserverCheap(null));
    }

    @Test
    public void testStructuralValueConsistentWithEquals() throws Exception {
        CoderProperties.structuralValueConsistentWithEquals(NullableCoderTest.TEST_CODER, null, null);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDecodingError() throws Exception {
        thrown.expect(CoderException.class);
        thrown.expectMessage(Matchers.equalTo(("NullableCoder expects either a byte valued 0 (null) " + "or 1 (present), got 5")));
        InputStream input = new ByteArrayInputStream(new byte[]{ 5 });
        NullableCoderTest.TEST_CODER.decode(input, OUTER);
    }

    @Test
    public void testSubcoderRecievesEntireStream() throws Exception {
        NullableCoder<String> coder = NullableCoder.of(new NullableCoderTest.EntireStreamExpectingCoder());
        CoderProperties.coderDecodeEncodeEqualInContext(coder, Context.OUTER, null);
        CoderProperties.coderDecodeEncodeEqualInContext(coder, Context.OUTER, "foo");
    }

    @Test
    public void testNestedNullableCoder() {
        NullableCoder<Double> coder = NullableCoder.of(DoubleCoder.of());
        Assert.assertThat(NullableCoder.of(coder), Matchers.theInstance(coder));
    }

    @Test
    public void testEncodedTypeDescriptor() throws Exception {
        Assert.assertThat(NullableCoderTest.TEST_CODER.getEncodedTypeDescriptor(), Matchers.equalTo(TypeDescriptor.of(String.class)));
    }

    private static class EntireStreamExpectingCoder extends AtomicCoder<String> {
        @Override
        public void encode(String value, OutputStream outStream) throws IOException {
            encode(value, outStream, NESTED);
        }

        @Override
        public void encode(String value, OutputStream outStream, Context context) throws IOException {
            checkArgument(context.isWholeStream, "Expected to get entire stream");
            StringUtf8Coder.of().encode(value, outStream, context);
        }

        @Override
        public String decode(InputStream inStream) throws IOException, CoderException {
            return decode(inStream, NESTED);
        }

        @Override
        public String decode(InputStream inStream, Context context) throws IOException, CoderException {
            checkArgument(context.isWholeStream, "Expected to get entire stream");
            return StringUtf8Coder.of().decode(inStream, context);
        }

        @Override
        public List<? extends Coder<?>> getCoderArguments() {
            return Collections.emptyList();
        }

        @Override
        public void verifyDeterministic() throws NonDeterministicException {
        }
    }
}

