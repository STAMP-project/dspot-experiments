/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.ssl;


import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.junit.Assert;
import org.junit.Test;


public class CipherSuiteConverterTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CipherSuiteConverterTest.class);

    @Test
    public void testJ2OMappings() throws Exception {
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "ECDHE-ECDSA-AES128-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", "ECDHE-RSA-AES128-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_RSA_WITH_AES_128_CBC_SHA256", "AES128-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", "ECDH-ECDSA-AES128-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", "ECDH-RSA-AES128-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "DHE-RSA-AES128-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", "DHE-DSS-AES128-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", "ECDHE-ECDSA-AES128-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "ECDHE-RSA-AES128-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_RSA_WITH_AES_128_CBC_SHA", "AES128-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", "ECDH-ECDSA-AES128-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", "ECDH-RSA-AES128-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "DHE-RSA-AES128-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_DSS_WITH_AES_128_CBC_SHA", "DHE-DSS-AES128-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "ECDHE-ECDSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "ECDHE-RSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_RSA_WITH_AES_128_GCM_SHA256", "AES128-GCM-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", "ECDH-ECDSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", "ECDH-RSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", "DHE-RSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", "DHE-DSS-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", "ECDHE-ECDSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", "ECDHE-RSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_RSA_WITH_3DES_EDE_CBC_SHA", "DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", "ECDH-ECDSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA", "ECDH-RSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA", "DHE-RSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA", "DHE-DSS-DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", "ECDHE-ECDSA-RC4-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_RSA_WITH_RC4_128_SHA", "ECDHE-RSA-RC4-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_RSA_WITH_RC4_128_SHA", "RC4-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_ECDSA_WITH_RC4_128_SHA", "ECDH-ECDSA-RC4-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_RSA_WITH_RC4_128_SHA", "ECDH-RSA-RC4-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_RSA_WITH_RC4_128_MD5", "RC4-MD5");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DH_anon_WITH_AES_128_GCM_SHA256", "ADH-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DH_anon_WITH_AES_128_CBC_SHA256", "ADH-AES128-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_anon_WITH_AES_128_CBC_SHA", "AECDH-AES128-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DH_anon_WITH_AES_128_CBC_SHA", "ADH-AES128-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA", "AECDH-DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", "ADH-DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_anon_WITH_RC4_128_SHA", "AECDH-RC4-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DH_anon_WITH_RC4_128_MD5", "ADH-RC4-MD5");
        CipherSuiteConverterTest.testJ2OMapping("SSL_RSA_WITH_DES_CBC_SHA", "DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DHE_RSA_WITH_DES_CBC_SHA", "DHE-RSA-DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DHE_DSS_WITH_DES_CBC_SHA", "DHE-DSS-DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DH_anon_WITH_DES_CBC_SHA", "ADH-DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", "EXP-DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", "EXP-DHE-RSA-DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", "EXP-DHE-DSS-DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA", "EXP-ADH-DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_RSA_EXPORT_WITH_RC4_40_MD5", "EXP-RC4-MD5");
        CipherSuiteConverterTest.testJ2OMapping("SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", "EXP-ADH-RC4-MD5");
        CipherSuiteConverterTest.testJ2OMapping("TLS_RSA_WITH_NULL_SHA256", "NULL-SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_ECDSA_WITH_NULL_SHA", "ECDHE-ECDSA-NULL-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_RSA_WITH_NULL_SHA", "ECDHE-RSA-NULL-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_RSA_WITH_NULL_SHA", "NULL-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_ECDSA_WITH_NULL_SHA", "ECDH-ECDSA-NULL-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_RSA_WITH_NULL_SHA", "ECDH-RSA-NULL-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_anon_WITH_NULL_SHA", "AECDH-NULL-SHA");
        CipherSuiteConverterTest.testJ2OMapping("SSL_RSA_WITH_NULL_MD5", "NULL-MD5");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_WITH_3DES_EDE_CBC_SHA", "KRB5-DES-CBC3-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_WITH_3DES_EDE_CBC_MD5", "KRB5-DES-CBC3-MD5");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_WITH_RC4_128_SHA", "KRB5-RC4-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_WITH_RC4_128_MD5", "KRB5-RC4-MD5");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_WITH_DES_CBC_SHA", "KRB5-DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_WITH_DES_CBC_MD5", "KRB5-DES-CBC-MD5");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", "EXP-KRB5-DES-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", "EXP-KRB5-DES-CBC-MD5");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_EXPORT_WITH_RC4_40_SHA", "EXP-KRB5-RC4-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_EXPORT_WITH_RC4_40_MD5", "EXP-KRB5-RC4-MD5");
        CipherSuiteConverterTest.testJ2OMapping("SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5", "EXP-RC2-CBC-MD5");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_DSS_WITH_AES_256_CBC_SHA", "DHE-DSS-AES256-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_RSA_WITH_AES_256_CBC_SHA", "DHE-RSA-AES256-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DH_anon_WITH_AES_256_CBC_SHA", "ADH-AES256-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", "ECDHE-ECDSA-AES256-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "ECDHE-RSA-AES256-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", "ECDH-ECDSA-AES256-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", "ECDH-RSA-AES256-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDH_anon_WITH_AES_256_CBC_SHA", "AECDH-AES256-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_MD5", "EXP-KRB5-RC2-CBC-MD5");
        CipherSuiteConverterTest.testJ2OMapping("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_SHA", "EXP-KRB5-RC2-CBC-SHA");
        CipherSuiteConverterTest.testJ2OMapping("TLS_RSA_WITH_AES_256_CBC_SHA", "AES256-SHA");
        // For historical reasons the CHACHA20 ciphers do not follow OpenSSL's custom naming
        // convention and omits the HMAC algorithm portion of the name.
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256", "ECDHE-RSA-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256", "ECDHE-ECDSA-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA256", "DHE-RSA-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testJ2OMapping("TLS_PSK_WITH_CHACHA20_POLY1305_SHA256", "PSK-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testJ2OMapping("TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256", "ECDHE-PSK-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testJ2OMapping("TLS_DHE_PSK_WITH_CHACHA20_POLY1305_SHA256", "DHE-PSK-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testJ2OMapping("TLS_RSA_PSK_WITH_CHACHA20_POLY1305_SHA256", "RSA-PSK-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testJ2OMapping("TLS_AES_128_GCM_SHA256", "TLS_AES_128_GCM_SHA256");
        CipherSuiteConverterTest.testJ2OMapping("TLS_AES_256_GCM_SHA384", "TLS_AES_256_GCM_SHA384");
        CipherSuiteConverterTest.testJ2OMapping("TLS_CHACHA20_POLY1305_SHA256", "TLS_CHACHA20_POLY1305_SHA256");
    }

    @Test
    public void testO2JMappings() throws Exception {
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "ECDHE-ECDSA-AES128-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_AES_128_CBC_SHA256", "ECDHE-RSA-AES128-SHA256");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_AES_128_CBC_SHA256", "AES128-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDH_ECDSA_WITH_AES_128_CBC_SHA256", "ECDH-ECDSA-AES128-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDH_RSA_WITH_AES_128_CBC_SHA256", "ECDH-RSA-AES128-SHA256");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_AES_128_CBC_SHA256", "DHE-RSA-AES128-SHA256");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_AES_128_CBC_SHA256", "DHE-DSS-AES128-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_AES_128_CBC_SHA", "ECDHE-ECDSA-AES128-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_AES_128_CBC_SHA", "ECDHE-RSA-AES128-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_AES_128_CBC_SHA", "AES128-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_ECDSA_WITH_AES_128_CBC_SHA", "ECDH-ECDSA-AES128-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_RSA_WITH_AES_128_CBC_SHA", "ECDH-RSA-AES128-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_AES_128_CBC_SHA", "DHE-RSA-AES128-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_AES_128_CBC_SHA", "DHE-DSS-AES128-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "ECDHE-ECDSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_AES_128_GCM_SHA256", "ECDHE-RSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_AES_128_GCM_SHA256", "AES128-GCM-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDH_ECDSA_WITH_AES_128_GCM_SHA256", "ECDH-ECDSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDH_RSA_WITH_AES_128_GCM_SHA256", "ECDH-RSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_AES_128_GCM_SHA256", "DHE-RSA-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_AES_128_GCM_SHA256", "DHE-DSS-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", "ECDHE-ECDSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", "ECDHE-RSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_3DES_EDE_CBC_SHA", "DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", "ECDH-ECDSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_RSA_WITH_3DES_EDE_CBC_SHA", "ECDH-RSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_3DES_EDE_CBC_SHA", "DHE-RSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_3DES_EDE_CBC_SHA", "DHE-DSS-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_RC4_128_SHA", "ECDHE-ECDSA-RC4-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_RC4_128_SHA", "ECDHE-RSA-RC4-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_RC4_128_SHA", "RC4-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_ECDSA_WITH_RC4_128_SHA", "ECDH-ECDSA-RC4-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_RSA_WITH_RC4_128_SHA", "ECDH-RSA-RC4-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_RC4_128_MD5", "RC4-MD5");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_AES_128_GCM_SHA256", "ADH-AES128-GCM-SHA256");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_AES_128_CBC_SHA256", "ADH-AES128-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDH_anon_WITH_AES_128_CBC_SHA", "AECDH-AES128-SHA");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_AES_128_CBC_SHA", "ADH-AES128-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_anon_WITH_3DES_EDE_CBC_SHA", "AECDH-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_3DES_EDE_CBC_SHA", "ADH-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_anon_WITH_RC4_128_SHA", "AECDH-RC4-SHA");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_RC4_128_MD5", "ADH-RC4-MD5");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_DES_CBC_SHA", "DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_DES_CBC_SHA", "DHE-RSA-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_DES_CBC_SHA", "DHE-DSS-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_DES_CBC_SHA", "ADH-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_EXPORT_WITH_DES_CBC_40_SHA", "EXP-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_EXPORT_WITH_DES_CBC_40_SHA", "EXP-DHE-RSA-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_EXPORT_WITH_DES_CBC_40_SHA", "EXP-DHE-DSS-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_EXPORT_WITH_DES_CBC_40_SHA", "EXP-ADH-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_EXPORT_WITH_RC4_40_MD5", "EXP-RC4-MD5");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_EXPORT_WITH_RC4_40_MD5", "EXP-ADH-RC4-MD5");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_NULL_SHA256", "NULL-SHA256");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_NULL_SHA", "ECDHE-ECDSA-NULL-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_NULL_SHA", "ECDHE-RSA-NULL-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_NULL_SHA", "NULL-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_ECDSA_WITH_NULL_SHA", "ECDH-ECDSA-NULL-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_RSA_WITH_NULL_SHA", "ECDH-RSA-NULL-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_anon_WITH_NULL_SHA", "AECDH-NULL-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_NULL_MD5", "NULL-MD5");
        CipherSuiteConverterTest.testO2JMapping("KRB5_WITH_3DES_EDE_CBC_SHA", "KRB5-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("KRB5_WITH_3DES_EDE_CBC_MD5", "KRB5-DES-CBC3-MD5");
        CipherSuiteConverterTest.testO2JMapping("KRB5_WITH_RC4_128_SHA", "KRB5-RC4-SHA");
        CipherSuiteConverterTest.testO2JMapping("KRB5_WITH_RC4_128_MD5", "KRB5-RC4-MD5");
        CipherSuiteConverterTest.testO2JMapping("KRB5_WITH_DES_CBC_SHA", "KRB5-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("KRB5_WITH_DES_CBC_MD5", "KRB5-DES-CBC-MD5");
        CipherSuiteConverterTest.testO2JMapping("KRB5_EXPORT_WITH_DES_CBC_40_SHA", "EXP-KRB5-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("KRB5_EXPORT_WITH_DES_CBC_40_MD5", "EXP-KRB5-DES-CBC-MD5");
        CipherSuiteConverterTest.testO2JMapping("KRB5_EXPORT_WITH_RC4_40_SHA", "EXP-KRB5-RC4-SHA");
        CipherSuiteConverterTest.testO2JMapping("KRB5_EXPORT_WITH_RC4_40_MD5", "EXP-KRB5-RC4-MD5");
        CipherSuiteConverterTest.testO2JMapping("RSA_EXPORT_WITH_RC2_CBC_40_MD5", "EXP-RC2-CBC-MD5");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_AES_256_CBC_SHA", "DHE-DSS-AES256-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_AES_256_CBC_SHA", "DHE-RSA-AES256-SHA");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_AES_256_CBC_SHA", "ADH-AES256-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_AES_256_CBC_SHA", "ECDHE-ECDSA-AES256-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_AES_256_CBC_SHA", "ECDHE-RSA-AES256-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_ECDSA_WITH_AES_256_CBC_SHA", "ECDH-ECDSA-AES256-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_RSA_WITH_AES_256_CBC_SHA", "ECDH-RSA-AES256-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_anon_WITH_AES_256_CBC_SHA", "AECDH-AES256-SHA");
        CipherSuiteConverterTest.testO2JMapping("KRB5_EXPORT_WITH_RC2_CBC_40_MD5", "EXP-KRB5-RC2-CBC-MD5");
        CipherSuiteConverterTest.testO2JMapping("KRB5_EXPORT_WITH_RC2_CBC_40_SHA", "EXP-KRB5-RC2-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_AES_256_CBC_SHA", "AES256-SHA");
        // Test the known mappings that actually do not exist in Java
        CipherSuiteConverterTest.testO2JMapping("EDH_DSS_WITH_3DES_EDE_CBC_SHA", "EDH-DSS-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_SEED_SHA", "SEED-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_CAMELLIA128_SHA", "CAMELLIA128-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_IDEA_CBC_SHA", "IDEA-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("PSK_WITH_AES_128_CBC_SHA", "PSK-AES128-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("PSK_WITH_3DES_EDE_CBC_SHA", "PSK-3DES-EDE-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("KRB5_WITH_IDEA_CBC_SHA", "KRB5-IDEA-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("KRB5_WITH_IDEA_CBC_MD5", "KRB5-IDEA-CBC-MD5");
        CipherSuiteConverterTest.testO2JMapping("PSK_WITH_RC4_128_SHA", "PSK-RC4-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_AES_256_GCM_SHA384", "ECDHE-RSA-AES256-GCM-SHA384");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "ECDHE-ECDSA-AES256-GCM-SHA384");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_AES_256_CBC_SHA384", "ECDHE-RSA-AES256-SHA384");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "ECDHE-ECDSA-AES256-SHA384");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_AES_256_GCM_SHA384", "DHE-DSS-AES256-GCM-SHA384");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_AES_256_GCM_SHA384", "DHE-RSA-AES256-GCM-SHA384");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_AES_256_CBC_SHA256", "DHE-RSA-AES256-SHA256");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_AES_256_CBC_SHA256", "DHE-DSS-AES256-SHA256");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_CAMELLIA256_SHA", "DHE-RSA-CAMELLIA256-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_CAMELLIA256_SHA", "DHE-DSS-CAMELLIA256-SHA");
        CipherSuiteConverterTest.testO2JMapping("ECDH_RSA_WITH_AES_256_GCM_SHA384", "ECDH-RSA-AES256-GCM-SHA384");
        CipherSuiteConverterTest.testO2JMapping("ECDH_ECDSA_WITH_AES_256_GCM_SHA384", "ECDH-ECDSA-AES256-GCM-SHA384");
        CipherSuiteConverterTest.testO2JMapping("ECDH_RSA_WITH_AES_256_CBC_SHA384", "ECDH-RSA-AES256-SHA384");
        CipherSuiteConverterTest.testO2JMapping("ECDH_ECDSA_WITH_AES_256_CBC_SHA384", "ECDH-ECDSA-AES256-SHA384");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_AES_256_GCM_SHA384", "AES256-GCM-SHA384");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_AES_256_CBC_SHA256", "AES256-SHA256");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_CAMELLIA256_SHA", "CAMELLIA256-SHA");
        CipherSuiteConverterTest.testO2JMapping("PSK_WITH_AES_256_CBC_SHA", "PSK-AES256-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_SEED_SHA", "DHE-RSA-SEED-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_SEED_SHA", "DHE-DSS-SEED-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_CAMELLIA128_SHA", "DHE-RSA-CAMELLIA128-SHA");
        CipherSuiteConverterTest.testO2JMapping("DHE_DSS_WITH_CAMELLIA128_SHA", "DHE-DSS-CAMELLIA128-SHA");
        CipherSuiteConverterTest.testO2JMapping("EDH_RSA_WITH_3DES_EDE_CBC_SHA", "EDH-RSA-DES-CBC3-SHA");
        CipherSuiteConverterTest.testO2JMapping("SRP_DSS_WITH_AES_256_CBC_SHA", "SRP-DSS-AES-256-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("SRP_RSA_WITH_AES_256_CBC_SHA", "SRP-RSA-AES-256-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("SRP_WITH_AES_256_CBC_SHA", "SRP-AES-256-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_AES_256_GCM_SHA384", "ADH-AES256-GCM-SHA384");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_AES_256_CBC_SHA256", "ADH-AES256-SHA256");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_CAMELLIA256_SHA", "ADH-CAMELLIA256-SHA");
        CipherSuiteConverterTest.testO2JMapping("SRP_DSS_WITH_AES_128_CBC_SHA", "SRP-DSS-AES-128-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("SRP_RSA_WITH_AES_128_CBC_SHA", "SRP-RSA-AES-128-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("SRP_WITH_AES_128_CBC_SHA", "SRP-AES-128-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_SEED_SHA", "ADH-SEED-SHA");
        CipherSuiteConverterTest.testO2JMapping("DH_anon_WITH_CAMELLIA128_SHA", "ADH-CAMELLIA128-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_RC2_CBC_MD5", "RC2-CBC-MD5");
        CipherSuiteConverterTest.testO2JMapping("SRP_DSS_WITH_3DES_EDE_CBC_SHA", "SRP-DSS-3DES-EDE-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("SRP_RSA_WITH_3DES_EDE_CBC_SHA", "SRP-RSA-3DES-EDE-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("SRP_WITH_3DES_EDE_CBC_SHA", "SRP-3DES-EDE-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_3DES_EDE_CBC_MD5", "DES-CBC3-MD5");
        CipherSuiteConverterTest.testO2JMapping("EDH_RSA_WITH_DES_CBC_SHA", "EDH-RSA-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("EDH_DSS_WITH_DES_CBC_SHA", "EDH-DSS-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("RSA_WITH_DES_CBC_MD5", "DES-CBC-MD5");
        CipherSuiteConverterTest.testO2JMapping("EDH_RSA_EXPORT_WITH_DES_CBC_40_SHA", "EXP-EDH-RSA-DES-CBC-SHA");
        CipherSuiteConverterTest.testO2JMapping("EDH_DSS_EXPORT_WITH_DES_CBC_40_SHA", "EXP-EDH-DSS-DES-CBC-SHA");
        // For historical reasons the CHACHA20 ciphers do not follow OpenSSL's custom naming
        // convention and omits the HMAC algorithm portion of the name.
        CipherSuiteConverterTest.testO2JMapping("ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256", "ECDHE-RSA-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256", "ECDHE-ECDSA-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testO2JMapping("DHE_RSA_WITH_CHACHA20_POLY1305_SHA256", "DHE-RSA-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testO2JMapping("PSK_WITH_CHACHA20_POLY1305_SHA256", "PSK-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testO2JMapping("ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256", "ECDHE-PSK-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testO2JMapping("DHE_PSK_WITH_CHACHA20_POLY1305_SHA256", "DHE-PSK-CHACHA20-POLY1305");
        CipherSuiteConverterTest.testO2JMapping("RSA_PSK_WITH_CHACHA20_POLY1305_SHA256", "RSA-PSK-CHACHA20-POLY1305");
    }

    @Test
    public void testCachedJ2OMappings() {
        CipherSuiteConverterTest.testCachedJ2OMapping("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "ECDHE-ECDSA-AES128-SHA256");
    }

    @Test
    public void testUnknownOpenSSLCiphersToJava() {
        CipherSuiteConverterTest.testUnknownOpenSSLCiphersToJava("(NONE)");
        CipherSuiteConverterTest.testUnknownOpenSSLCiphersToJava("unknown");
        CipherSuiteConverterTest.testUnknownOpenSSLCiphersToJava("");
    }

    @Test
    public void testUnknownJavaCiphersToOpenSSL() {
        CipherSuiteConverterTest.testUnknownJavaCiphersToOpenSSL("(NONE)");
        CipherSuiteConverterTest.testUnknownJavaCiphersToOpenSSL("unknown");
        CipherSuiteConverterTest.testUnknownJavaCiphersToOpenSSL("");
    }

    @Test
    public void testCachedO2JMappings() {
        CipherSuiteConverterTest.testCachedO2JMapping("ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "ECDHE-ECDSA-AES128-SHA256");
    }

    @Test
    public void testTlsv13Mappings() {
        CipherSuiteConverter.clearCache();
        Assert.assertEquals("TLS_AES_128_GCM_SHA256", CipherSuiteConverter.toJava("TLS_AES_128_GCM_SHA256", "TLS"));
        Assert.assertNull(CipherSuiteConverter.toJava("TLS_AES_128_GCM_SHA256", "SSL"));
        Assert.assertEquals("TLS_AES_256_GCM_SHA384", CipherSuiteConverter.toJava("TLS_AES_256_GCM_SHA384", "TLS"));
        Assert.assertNull(CipherSuiteConverter.toJava("TLS_AES_256_GCM_SHA384", "SSL"));
        Assert.assertEquals("TLS_CHACHA20_POLY1305_SHA256", CipherSuiteConverter.toJava("TLS_CHACHA20_POLY1305_SHA256", "TLS"));
        Assert.assertNull(CipherSuiteConverter.toJava("TLS_CHACHA20_POLY1305_SHA256", "SSL"));
        // BoringSSL use different cipher naming then OpenSSL so we need to test for both
        Assert.assertEquals("TLS_AES_128_GCM_SHA256", CipherSuiteConverter.toOpenSsl("TLS_AES_128_GCM_SHA256", false));
        Assert.assertEquals("TLS_AES_256_GCM_SHA384", CipherSuiteConverter.toOpenSsl("TLS_AES_256_GCM_SHA384", false));
        Assert.assertEquals("TLS_CHACHA20_POLY1305_SHA256", CipherSuiteConverter.toOpenSsl("TLS_CHACHA20_POLY1305_SHA256", false));
        Assert.assertEquals("AEAD-AES128-GCM-SHA256", CipherSuiteConverter.toOpenSsl("TLS_AES_128_GCM_SHA256", true));
        Assert.assertEquals("AEAD-AES256-GCM-SHA384", CipherSuiteConverter.toOpenSsl("TLS_AES_256_GCM_SHA384", true));
        Assert.assertEquals("AEAD-CHACHA20-POLY1305-SHA256", CipherSuiteConverter.toOpenSsl("TLS_CHACHA20_POLY1305_SHA256", true));
    }
}

