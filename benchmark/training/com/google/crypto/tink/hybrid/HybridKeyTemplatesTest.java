/**
 * Copyright 2017 Google Inc.
 */
/**
 *
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
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
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
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink.hybrid;


import AeadKeyTemplates.AES128_CTR_HMAC_SHA256;
import AeadKeyTemplates.AES128_GCM;
import AeadKeyTemplates.AES256_EAX;
import EcPointFormat.UNCOMPRESSED;
import EciesAeadHkdfPrivateKeyManager.TYPE_URL;
import EllipticCurveType.NIST_P256;
import HashType.SHA256;
import OutputPrefixType.TINK;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.proto.EcPointFormat;
import com.google.crypto.tink.proto.EciesAeadHkdfKeyFormat;
import com.google.crypto.tink.proto.EciesHkdfKemParams;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyTemplate;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static HybridKeyTemplates.ECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256;
import static HybridKeyTemplates.ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM;


/**
 * Tests for HybridKeyTemplates.
 */
@RunWith(JUnit4.class)
public class HybridKeyTemplatesTest {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Test
    public void testECIES_P256_HKDF_HMAC_SHA256_AES128_GCM() throws Exception {
        KeyTemplate template = ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM;
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        EciesAeadHkdfKeyFormat format = EciesAeadHkdfKeyFormat.parseFrom(template.getValue());
        Assert.assertTrue(format.hasParams());
        Assert.assertTrue(format.getParams().hasKemParams());
        Assert.assertTrue(format.getParams().hasDemParams());
        Assert.assertTrue(format.getParams().getDemParams().hasAeadDem());
        Assert.assertEquals(UNCOMPRESSED, format.getParams().getEcPointFormat());
        EciesHkdfKemParams kemParams = format.getParams().getKemParams();
        Assert.assertEquals(NIST_P256, kemParams.getCurveType());
        Assert.assertEquals(SHA256, kemParams.getHkdfHashType());
        Assert.assertTrue(kemParams.getHkdfSalt().isEmpty());
        Assert.assertEquals(AES128_GCM.toString(), format.getParams().getDemParams().getAeadDem().toString());
    }

    @Test
    public void testECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256() throws Exception {
        KeyTemplate template = ECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256;
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        EciesAeadHkdfKeyFormat format = EciesAeadHkdfKeyFormat.parseFrom(template.getValue());
        Assert.assertTrue(format.hasParams());
        Assert.assertTrue(format.getParams().hasKemParams());
        Assert.assertTrue(format.getParams().hasDemParams());
        Assert.assertTrue(format.getParams().getDemParams().hasAeadDem());
        Assert.assertEquals(UNCOMPRESSED, format.getParams().getEcPointFormat());
        EciesHkdfKemParams kemParams = format.getParams().getKemParams();
        Assert.assertEquals(NIST_P256, kemParams.getCurveType());
        Assert.assertEquals(SHA256, kemParams.getHkdfHashType());
        Assert.assertTrue(kemParams.getHkdfSalt().isEmpty());
        Assert.assertEquals(AES128_CTR_HMAC_SHA256.toString(), format.getParams().getDemParams().getAeadDem().toString());
    }

    @Test
    public void testCreateEciesAeadHkdfKeyTemplate() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        EllipticCurveType curveType = EllipticCurveType.NIST_P384;
        HashType hashType = HashType.SHA512;
        EcPointFormat ecPointFormat = EcPointFormat.COMPRESSED;
        KeyTemplate demKeyTemplate = AeadKeyTemplates.AES256_EAX;
        String salt = "some salt";
        KeyTemplate template = HybridKeyTemplates.createEciesAeadHkdfKeyTemplate(curveType, hashType, ecPointFormat, demKeyTemplate, salt.getBytes(HybridKeyTemplatesTest.UTF_8));
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        EciesAeadHkdfKeyFormat format = EciesAeadHkdfKeyFormat.parseFrom(template.getValue());
        Assert.assertTrue(format.hasParams());
        Assert.assertTrue(format.getParams().hasKemParams());
        Assert.assertTrue(format.getParams().hasDemParams());
        Assert.assertTrue(format.getParams().getDemParams().hasAeadDem());
        Assert.assertEquals(ecPointFormat, format.getParams().getEcPointFormat());
        EciesHkdfKemParams kemParams = format.getParams().getKemParams();
        Assert.assertEquals(curveType, kemParams.getCurveType());
        Assert.assertEquals(hashType, kemParams.getHkdfHashType());
        Assert.assertEquals(salt, kemParams.getHkdfSalt().toStringUtf8());
        Assert.assertEquals(AES256_EAX.toString(), format.getParams().getDemParams().getAeadDem().toString());
    }
}

