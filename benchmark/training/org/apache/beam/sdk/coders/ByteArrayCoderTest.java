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


import Coder.Context.NESTED;
import Coder.Context.OUTER;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ByteArrayCoder}.
 */
@RunWith(JUnit4.class)
public class ByteArrayCoderTest {
    private static final ByteArrayCoder TEST_CODER = ByteArrayCoder.of();

    private static final List<byte[]> TEST_VALUES = Arrays.asList(new byte[]{ 10, 11, 12 }, new byte[]{ 13, 3 }, new byte[]{ 13, 14 }, new byte[]{  });

    @Test
    public void testDecodeEncodeEquals() throws Exception {
        for (byte[] value : ByteArrayCoderTest.TEST_VALUES) {
            CoderProperties.coderDecodeEncodeEqual(ByteArrayCoderTest.TEST_CODER, value);
        }
    }

    @Test
    public void testRegisterByteSizeObserver() throws Exception {
        CoderProperties.testByteCount(ByteArrayCoder.of(), OUTER, new byte[][]{ new byte[]{ 10, 11, 12 } });
        CoderProperties.testByteCount(ByteArrayCoder.of(), NESTED, new byte[][]{ new byte[]{ 10, 11, 12 }, new byte[]{  }, new byte[]{  }, new byte[]{ 13, 14 }, new byte[]{  } });
    }

    @Test
    public void testStructuralValueConsistentWithEquals() throws Exception {
        // We know that byte array coders are NOT compatible with equals
        // (aka injective w.r.t. Object.equals)
        for (byte[] value1 : ByteArrayCoderTest.TEST_VALUES) {
            for (byte[] value2 : ByteArrayCoderTest.TEST_VALUES) {
                CoderProperties.structuralValueConsistentWithEquals(ByteArrayCoderTest.TEST_CODER, value1, value2);
            }
        }
    }

    @Test
    public void testEncodeThenMutate() throws Exception {
        byte[] input = new byte[]{ 7, 3, 10, 15 };
        byte[] encoded = CoderUtils.encodeToByteArray(ByteArrayCoderTest.TEST_CODER, input);
        input[1] = 9;
        byte[] decoded = CoderUtils.decodeFromByteArray(ByteArrayCoderTest.TEST_CODER, encoded);
        // now that I have mutated the input, the output should NOT match
        Assert.assertThat(input, Matchers.not(Matchers.equalTo(decoded)));
    }

    @Test
    public void testEncodeAndOwn() throws Exception {
        for (byte[] value : ByteArrayCoderTest.TEST_VALUES) {
            byte[] encodedSlow = CoderUtils.encodeToByteArray(ByteArrayCoderTest.TEST_CODER, value);
            byte[] encodedFast = ByteArrayCoderTest.encodeToByteArrayAndOwn(ByteArrayCoderTest.TEST_CODER, value);
            Assert.assertThat(encodedSlow, Matchers.equalTo(encodedFast));
        }
    }

    /**
     * Generated data to check that the wire format has not changed. To regenerate, see {@link org.apache.beam.sdk.coders.PrintBase64Encodings}.
     */
    private static final List<String> TEST_ENCODINGS = Arrays.asList("CgsM", "DQM", "DQ4", "");

    @Test
    public void testWireFormatEncode() throws Exception {
        CoderProperties.coderEncodesBase64(ByteArrayCoderTest.TEST_CODER, ByteArrayCoderTest.TEST_VALUES, ByteArrayCoderTest.TEST_ENCODINGS);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void encodeNullThrowsCoderException() throws Exception {
        thrown.expect(CoderException.class);
        thrown.expectMessage("cannot encode a null byte[]");
        CoderUtils.encodeToBase64(ByteArrayCoderTest.TEST_CODER, null);
    }

    @Test
    public void testEncodedTypeDescriptor() throws Exception {
        Assert.assertThat(ByteArrayCoderTest.TEST_CODER.getEncodedTypeDescriptor(), Matchers.equalTo(TypeDescriptor.of(byte[].class)));
    }
}

