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
package org.apache.beam.sdk.extensions.protobuf;


import Context.NESTED;
import com.google.protobuf.ByteString;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.ListCoder;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.util.CoderUtils;
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
 * Test case for {@link ByteStringCoder}.
 */
@RunWith(JUnit4.class)
public class ByteStringCoderTest {
    private static final ByteStringCoder TEST_CODER = ByteStringCoder.of();

    private static final List<String> TEST_STRING_VALUES = Arrays.asList("", "a", "13", "hello", "a longer string with spaces and all that", "a string with a \n newline", "???????????????");

    private static final ImmutableList<ByteString> TEST_VALUES;

    static {
        ImmutableList.Builder<ByteString> builder = ImmutableList.builder();
        for (String s : ByteStringCoderTest.TEST_STRING_VALUES) {
            builder.add(ByteString.copyFromUtf8(s));
        }
        TEST_VALUES = builder.build();
    }

    /**
     * Generated data to check that the wire format has not changed. To regenerate, see {@link org.apache.beam.sdk.coders.PrintBase64Encodings}.
     */
    private static final List<String> TEST_ENCODINGS = Arrays.asList("", "YQ", "MTM", "aGVsbG8", "YSBsb25nZXIgc3RyaW5nIHdpdGggc3BhY2VzIGFuZCBhbGwgdGhhdA", "YSBzdHJpbmcgd2l0aCBhIAogbmV3bGluZQ", "Pz8_Pz8_Pz8_Pz8_Pz8_");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDecodeEncodeEqualInAllContexts() throws Exception {
        for (ByteString value : ByteStringCoderTest.TEST_VALUES) {
            CoderProperties.coderDecodeEncodeEqual(ByteStringCoderTest.TEST_CODER, value);
        }
    }

    @Test
    public void testWireFormatEncode() throws Exception {
        CoderProperties.coderEncodesBase64(ByteStringCoderTest.TEST_CODER, ByteStringCoderTest.TEST_VALUES, ByteStringCoderTest.TEST_ENCODINGS);
    }

    @Test
    public void testCoderDeterministic() throws Throwable {
        ByteStringCoderTest.TEST_CODER.verifyDeterministic();
    }

    @Test
    public void testConsistentWithEquals() {
        Assert.assertTrue(ByteStringCoderTest.TEST_CODER.consistentWithEquals());
    }

    @Test
    public void testEncodeNullThrowsCoderException() throws Exception {
        thrown.expect(CoderException.class);
        thrown.expectMessage("cannot encode a null ByteString");
        CoderUtils.encodeToBase64(ByteStringCoderTest.TEST_CODER, null);
    }

    @Test
    public void testNestedCoding() throws Throwable {
        Coder<List<ByteString>> listCoder = ListCoder.of(ByteStringCoderTest.TEST_CODER);
        CoderProperties.coderDecodeEncodeContentsEqual(listCoder, ByteStringCoderTest.TEST_VALUES);
        CoderProperties.coderDecodeEncodeContentsInSameOrder(listCoder, ByteStringCoderTest.TEST_VALUES);
    }

    @Test
    public void testEncodedElementByteSize() throws Throwable {
        for (ByteString value : ByteStringCoderTest.TEST_VALUES) {
            byte[] encoded = CoderUtils.encodeToByteArray(ByteStringCoderTest.TEST_CODER, value, NESTED);
            Assert.assertEquals(encoded.length, ByteStringCoderTest.TEST_CODER.getEncodedElementByteSize(value));
        }
    }

    @Test
    public void testEncodedTypeDescriptor() throws Exception {
        Assert.assertThat(ByteStringCoderTest.TEST_CODER.getEncodedTypeDescriptor(), Matchers.equalTo(TypeDescriptor.of(ByteString.class)));
    }
}

