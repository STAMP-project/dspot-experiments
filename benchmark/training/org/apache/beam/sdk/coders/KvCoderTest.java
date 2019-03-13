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


import GlobalWindow.Coder.INSTANCE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test case for {@link KvCoder}.
 */
@RunWith(JUnit4.class)
public class KvCoderTest {
    private static class CoderAndData<T> {
        Coder<T> coder;

        List<T> data;
    }

    private static class AnyCoderAndData {
        private KvCoderTest.CoderAndData<?> coderAndData;
    }

    private static final List<KvCoderTest.AnyCoderAndData> TEST_DATA = Arrays.asList(KvCoderTest.coderAndData(VarIntCoder.of(), Arrays.asList((-1), 0, 1, 13, Integer.MAX_VALUE, Integer.MIN_VALUE)), KvCoderTest.coderAndData(BigEndianLongCoder.of(), Arrays.asList((-1L), 0L, 1L, 13L, Long.MAX_VALUE, Long.MIN_VALUE)), KvCoderTest.coderAndData(StringUtf8Coder.of(), Arrays.asList("", "hello", "goodbye", "1")), KvCoderTest.coderAndData(KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of()), Arrays.asList(KV.of("", (-1)), KV.of("hello", 0), KV.of("goodbye", Integer.MAX_VALUE))), KvCoderTest.coderAndData(ListCoder.of(VarLongCoder.of()), Arrays.asList(Arrays.asList(1L, 2L, 3L), Collections.emptyList())));

    @Test
    @SuppressWarnings("rawtypes")
    public void testDecodeEncodeEqual() throws Exception {
        for (KvCoderTest.AnyCoderAndData keyCoderAndData : KvCoderTest.TEST_DATA) {
            Coder keyCoder = keyCoderAndData.coderAndData.coder;
            for (Object key : keyCoderAndData.coderAndData.data) {
                for (KvCoderTest.AnyCoderAndData valueCoderAndData : KvCoderTest.TEST_DATA) {
                    Coder valueCoder = valueCoderAndData.coderAndData.coder;
                    for (Object value : valueCoderAndData.coderAndData.data) {
                        CoderProperties.coderDecodeEncodeEqual(KvCoder.of(keyCoder, valueCoder), KV.of(key, value));
                    }
                }
            }
        }
    }

    @Test
    public void testCoderIsSerializableWithWellKnownCoderType() throws Exception {
        CoderProperties.coderSerializable(KvCoder.of(INSTANCE, INSTANCE));
    }

    /**
     * Homogeneously typed test value for ease of use with the wire format test utility.
     */
    private static final Coder<KV<String, Integer>> TEST_CODER = KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of());

    private static final List<KV<String, Integer>> TEST_VALUES = Arrays.asList(KV.of("", (-1)), KV.of("hello", 0), KV.of("goodbye", Integer.MAX_VALUE));

    /**
     * Generated data to check that the wire format has not changed. To regenerate, see {@link org.apache.beam.sdk.coders.PrintBase64Encodings}.
     */
    private static final List<String> TEST_ENCODINGS = Arrays.asList("AP____8P", "BWhlbGxvAA", "B2dvb2RieWX_____Bw");

    @Test
    public void testWireFormatEncode() throws Exception {
        CoderProperties.coderEncodesBase64(KvCoderTest.TEST_CODER, KvCoderTest.TEST_VALUES, KvCoderTest.TEST_ENCODINGS);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void encodeNullThrowsCoderException() throws Exception {
        thrown.expect(CoderException.class);
        thrown.expectMessage("cannot encode a null KV");
        CoderUtils.encodeToBase64(KvCoderTest.TEST_CODER, null);
    }

    @Test
    public void testEncodedTypeDescriptor() throws Exception {
        TypeDescriptor<KV<String, Integer>> typeDescriptor = new TypeDescriptor<KV<String, Integer>>() {};
        Assert.assertThat(KvCoderTest.TEST_CODER.getEncodedTypeDescriptor(), Matchers.equalTo(typeDescriptor));
    }
}

