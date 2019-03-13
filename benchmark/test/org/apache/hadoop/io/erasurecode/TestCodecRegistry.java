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
package org.apache.hadoop.io.erasurecode;


import ErasureCodeConstants.RS_CODEC_NAME;
import ErasureCodeConstants.RS_LEGACY_CODEC_NAME;
import ErasureCodeConstants.XOR_CODEC_NAME;
import NativeRSRawErasureCoderFactory.CODER_NAME;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.io.erasurecode.rawcoder.NativeRSRawErasureCoderFactory;
import org.apache.hadoop.io.erasurecode.rawcoder.NativeXORRawErasureCoderFactory;
import org.apache.hadoop.io.erasurecode.rawcoder.RSLegacyRawErasureCoderFactory;
import org.apache.hadoop.io.erasurecode.rawcoder.RSRawErasureCoderFactory;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureCoderFactory;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureDecoder;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureEncoder;
import org.apache.hadoop.io.erasurecode.rawcoder.XORRawErasureCoderFactory;
import org.junit.Assert;
import org.junit.Test;

import static ErasureCodeConstants.RS_CODEC_NAME;


/**
 * Test CodecRegistry.
 */
public class TestCodecRegistry {
    @Test
    public void testGetCodecs() {
        Set<String> codecs = CodecRegistry.getInstance().getCodecNames();
        Assert.assertEquals(3, codecs.size());
        Assert.assertTrue(codecs.contains(RS_CODEC_NAME));
        Assert.assertTrue(codecs.contains(RS_LEGACY_CODEC_NAME));
        Assert.assertTrue(codecs.contains(XOR_CODEC_NAME));
    }

    @Test
    public void testGetCoders() {
        List<RawErasureCoderFactory> coders = CodecRegistry.getInstance().getCoders(RS_CODEC_NAME);
        Assert.assertEquals(2, coders.size());
        Assert.assertTrue(((coders.get(0)) instanceof NativeRSRawErasureCoderFactory));
        Assert.assertTrue(((coders.get(1)) instanceof RSRawErasureCoderFactory));
        coders = CodecRegistry.getInstance().getCoders(RS_LEGACY_CODEC_NAME);
        Assert.assertEquals(1, coders.size());
        Assert.assertTrue(((coders.get(0)) instanceof RSLegacyRawErasureCoderFactory));
        coders = CodecRegistry.getInstance().getCoders(XOR_CODEC_NAME);
        Assert.assertEquals(2, coders.size());
        Assert.assertTrue(((coders.get(0)) instanceof NativeXORRawErasureCoderFactory));
        Assert.assertTrue(((coders.get(1)) instanceof XORRawErasureCoderFactory));
    }

    @Test
    public void testGetCodersWrong() {
        List<RawErasureCoderFactory> coders = CodecRegistry.getInstance().getCoders("WRONG_CODEC");
        Assert.assertNull(coders);
    }

    @Test
    public void testGetCoderNames() {
        String[] coderNames = CodecRegistry.getInstance().getCoderNames(RS_CODEC_NAME);
        Assert.assertEquals(2, coderNames.length);
        Assert.assertEquals(CODER_NAME, coderNames[0]);
        Assert.assertEquals(RSRawErasureCoderFactory.CODER_NAME, coderNames[1]);
        coderNames = CodecRegistry.getInstance().getCoderNames(RS_LEGACY_CODEC_NAME);
        Assert.assertEquals(1, coderNames.length);
        Assert.assertEquals(RSLegacyRawErasureCoderFactory.CODER_NAME, coderNames[0]);
        coderNames = CodecRegistry.getInstance().getCoderNames(XOR_CODEC_NAME);
        Assert.assertEquals(2, coderNames.length);
        Assert.assertEquals(NativeXORRawErasureCoderFactory.CODER_NAME, coderNames[0]);
        Assert.assertEquals(XORRawErasureCoderFactory.CODER_NAME, coderNames[1]);
    }

    @Test
    public void testGetCoderByName() {
        RawErasureCoderFactory coder = CodecRegistry.getInstance().getCoderByName(RS_CODEC_NAME, RSRawErasureCoderFactory.CODER_NAME);
        Assert.assertTrue((coder instanceof RSRawErasureCoderFactory));
        coder = CodecRegistry.getInstance().getCoderByName(RS_CODEC_NAME, CODER_NAME);
        Assert.assertTrue((coder instanceof NativeRSRawErasureCoderFactory));
        coder = CodecRegistry.getInstance().getCoderByName(RS_LEGACY_CODEC_NAME, RSLegacyRawErasureCoderFactory.CODER_NAME);
        Assert.assertTrue((coder instanceof RSLegacyRawErasureCoderFactory));
        coder = CodecRegistry.getInstance().getCoderByName(XOR_CODEC_NAME, XORRawErasureCoderFactory.CODER_NAME);
        Assert.assertTrue((coder instanceof XORRawErasureCoderFactory));
        coder = CodecRegistry.getInstance().getCoderByName(XOR_CODEC_NAME, NativeXORRawErasureCoderFactory.CODER_NAME);
        Assert.assertTrue((coder instanceof NativeXORRawErasureCoderFactory));
    }

    @Test
    public void testGetCoderByNameWrong() {
        RawErasureCoderFactory coder = CodecRegistry.getInstance().getCoderByName(RS_CODEC_NAME, "WRONG_RS");
        Assert.assertNull(coder);
    }

    @Test
    public void testUpdateCoders() {
        class RSUserDefinedIncorrectFactory implements RawErasureCoderFactory {
            public RawErasureEncoder createEncoder(ErasureCoderOptions coderOptions) {
                return null;
            }

            public RawErasureDecoder createDecoder(ErasureCoderOptions coderOptions) {
                return null;
            }

            public String getCoderName() {
                return "rs_java";
            }

            public String getCodecName() {
                return RS_CODEC_NAME;
            }
        }
        List<RawErasureCoderFactory> userDefinedFactories = new ArrayList<>();
        userDefinedFactories.add(new RSUserDefinedIncorrectFactory());
        CodecRegistry.getInstance().updateCoders(userDefinedFactories);
        // check RS coders
        List<RawErasureCoderFactory> rsCoders = CodecRegistry.getInstance().getCoders(RS_CODEC_NAME);
        Assert.assertEquals(2, rsCoders.size());
        Assert.assertTrue(((rsCoders.get(0)) instanceof NativeRSRawErasureCoderFactory));
        Assert.assertTrue(((rsCoders.get(1)) instanceof RSRawErasureCoderFactory));
        // check RS coder names
        String[] rsCoderNames = CodecRegistry.getInstance().getCoderNames(RS_CODEC_NAME);
        Assert.assertEquals(2, rsCoderNames.length);
        Assert.assertEquals(CODER_NAME, rsCoderNames[0]);
        Assert.assertEquals(RSRawErasureCoderFactory.CODER_NAME, rsCoderNames[1]);
    }
}

