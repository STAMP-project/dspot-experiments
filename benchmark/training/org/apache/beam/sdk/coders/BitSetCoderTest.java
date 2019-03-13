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
import java.util.BitSet;
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
 * Tests for {@link BitSetCoder}.
 */
@RunWith(JUnit4.class)
public class BitSetCoderTest {
    private static final Coder<BitSet> TEST_CODER = BitSetCoder.of();

    private static final List<BitSet> TEST_VALUES = Arrays.asList(BitSet.valueOf(new byte[]{ 10, 11, 12 }), BitSet.valueOf(new byte[]{ 13, 3 }), BitSet.valueOf(new byte[]{ 13, 14 }), BitSet.valueOf(new byte[]{  }));

    @Test
    public void testDecodeEncodeEquals() throws Exception {
        for (BitSet value : BitSetCoderTest.TEST_VALUES) {
            CoderProperties.coderDecodeEncodeEqual(BitSetCoderTest.TEST_CODER, value);
        }
    }

    @Test
    public void testRegisterByteSizeObserver() throws Exception {
        CoderProperties.testByteCount(ByteArrayCoder.of(), OUTER, new byte[][]{ new byte[]{ 10, 11, 12 } });
        CoderProperties.testByteCount(ByteArrayCoder.of(), NESTED, new byte[][]{ new byte[]{ 10, 11, 12 }, new byte[]{  }, new byte[]{  }, new byte[]{ 13, 14 }, new byte[]{  } });
    }

    @Test
    public void testStructuralValueConsistentWithEquals() throws Exception {
        // We know that bi array coders are NOT compatible with equals
        // (aka injective w.r.t. Object.equals)
        for (BitSet value1 : BitSetCoderTest.TEST_VALUES) {
            for (BitSet value2 : BitSetCoderTest.TEST_VALUES) {
                CoderProperties.structuralValueConsistentWithEquals(BitSetCoderTest.TEST_CODER, value1, value2);
            }
        }
    }

    /**
     * Generated data to check that the wire format has not changed. To regenerate, see {@link org.apache.beam.sdk.coders.PrintBase64Encodings}.
     */
    private static final List<String> TEST_ENCODINGS = Arrays.asList("CgsM", "DQM", "DQ4", "");

    @Test
    public void testWireFormatEncode() throws Exception {
        CoderProperties.coderEncodesBase64(BitSetCoderTest.TEST_CODER, BitSetCoderTest.TEST_VALUES, BitSetCoderTest.TEST_ENCODINGS);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void encodeNullThrowsCoderException() throws Exception {
        thrown.expect(CoderException.class);
        thrown.expectMessage("cannot encode a null BitSet");
        CoderUtils.encodeToBase64(BitSetCoderTest.TEST_CODER, null);
    }

    @Test
    public void testEncodedTypeDescriptor() throws Exception {
        Assert.assertThat(BitSetCoderTest.TEST_CODER.getEncodedTypeDescriptor(), Matchers.equalTo(TypeDescriptor.of(BitSet.class)));
    }
}

