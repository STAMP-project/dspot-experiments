/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.crypto.cms;


import CryptoCmsConstants.CAMEL_CRYPTO_CMS_SIGNED_DATA;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.apache.camel.Exchange;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsException;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsFormatException;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsInvalidKeyException;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsNoCertificateForSignerInfoException;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsNoCertificateForSignerInfosException;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsNoKeyOrCertificateForAliasException;
import org.apache.camel.component.crypto.cms.exception.CryptoCmsSignatureInvalidContentHashException;
import org.apache.camel.component.crypto.cms.sig.DefaultSignedDataVerifierConfiguration;
import org.apache.camel.component.crypto.cms.sig.DefaultSignerInfo;
import org.apache.camel.component.crypto.cms.sig.SignedDataCreator;
import org.apache.camel.component.crypto.cms.sig.SignedDataCreatorConfiguration;
import org.apache.camel.component.crypto.cms.sig.SignedDataVerifier;
import org.apache.camel.component.crypto.cms.util.ExchangeUtil;
import org.apache.camel.component.crypto.cms.util.KeystoreUtil;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.util.IOHelper;
import org.junit.Assert;
import org.junit.Test;


public class SignedDataTest {
    @Test
    public void testWithCertificatesIncluded() throws Exception {
        signAndVerify("Test Message", "system.jks", "SHA1withRSA", "rsa", true, true);
    }

    @Test
    public void testWithCertificatesIncludedNoSignedAttributes() throws Exception {
        signAndVerify("Test Message", "system.jks", "SHA1withRSA", "rsa", true, true);
    }

    @Test
    public void testWithCertificatesIncludedTimestampSignedAttribute() throws Exception {
        signAndVerify("Test Message", "system.jks", "SHA1withRSA", "rsa", true, true);
    }

    @Test
    public void testWithCertificatesIncludedCertificateSignedAttribute() throws Exception {
        signAndVerify("Test Message", "system.jks", "SHA1withRSA", "rsa", true, true);
    }

    @Test
    public void testWithoutCertificatesIncludedAndDigestAlgorithmSHA1andSignatureAlgorithm() throws Exception {
        signAndVerify("Test Message", "system.jks", "SHA1withDSA", "dsa", true, false);
    }

    @Test
    public void signWithTwoAliases() throws Exception {
        sign("", "system.jks", "SHA1withRSA", true, false, "rsa", "rsa2");
    }

    @Test(expected = CryptoCmsNoKeyOrCertificateForAliasException.class)
    public void signWithTwoAliasesOneWithNoPrivateKeyInKeystore() throws Exception {
        sign("Test Message", "system.jks", "SHA1withDSA", true, false, "dsa", "noEntry");
    }

    @Test(expected = CryptoCmsNoKeyOrCertificateForAliasException.class)
    public void signWrongAlias() throws Exception {
        sign("Test Message", "system.jks", "SHA1withDSA", true, false, "wrong");
    }

    @Test
    public void signEmptyContent() throws Exception {
        sign("", "system.jks", "SHA1withDSA", true, false, "dsa");
    }

    @Test(expected = CryptoCmsInvalidKeyException.class)
    public void signSignatureAlgorithmNotCorrespondingToPrivateKey() throws Exception {
        sign("testMessage", "system.jks", "MD5withRSA", true, false, "dsa");
    }

    @Test(expected = IllegalArgumentException.class)
    public void signWrongSignatureAlgorithm() throws Exception {
        sign("testMessage", "system.jks", "wrongRSA", true, false, "rsa");
    }

    @Test
    public void verifySignedDataWithoutSignedContent() throws Exception {
        InputStream is = SignedDataTest.class.getClassLoader().getResourceAsStream("detached_signature.binary");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOHelper.copy(is, os);
        byte[] signed = os.toByteArray();
        try {
            verify("system.jks", "rsa", signed, false);
        } catch (CryptoCmsException e) {
            String message = e.getMessage();
            Assert.assertEquals(("PKCS7/CMS signature validation not possible: The content for which the hash-value must be calculated is missing in the PKCS7/CMS signed data instance. " + "Please check the configuration of the sender of the PKCS7/CMS signature."), message);
            return;
        }
        Assert.fail("Exception expected");
    }

    @Test(expected = CryptoCmsNoCertificateForSignerInfosException.class)
    public void verifyNoVerifierCerts() throws Exception {
        byte[] signed = sign("Test Message", "system.jks", "SHA1withRSA", true, true, "rsa");
        verify("system.jks", "wrongAlias", signed, false);// wrongAlias means

        // that no
        // certificates are
        // added to the
        // verifier keystore
    }

    @Test(expected = CryptoCmsFormatException.class)
    public void verifyWrongFormat() throws Exception {
        verify("system.jks", "rsa", "test".getBytes(), false);
    }

    @Test(expected = CryptoCmsFormatException.class)
    public void verifyWrongFormatInHeader() throws Exception {
        verifyContentWithSeparateSignature(new ByteArrayInputStream("ABCDEFG1ABCDEFG1ABCDEFG1".getBytes()), new ByteArrayInputStream("ABCDEFG1ABCDEFG1ABCDEFG1".getBytes()), "rsa");
    }

    @Test
    public void verifyContentWithSeparateSignature() throws Exception {
        InputStream message = new ByteArrayInputStream("Test Message".getBytes(StandardCharsets.UTF_8));
        InputStream signature = this.getClass().getClassLoader().getResourceAsStream("detached_signature.binary");
        Assert.assertNotNull(signature);
        verifyContentWithSeparateSignature(message, signature, "rsa");
    }

    @Test(expected = CryptoCmsSignatureInvalidContentHashException.class)
    public void verifyContentWithSeparateSignatureWrongContent() throws Exception {
        InputStream message = new ByteArrayInputStream("Wrong Message".getBytes());
        InputStream signature = this.getClass().getClassLoader().getResourceAsStream("detached_signature.binary");
        Assert.assertNotNull(signature);
        verifyContentWithSeparateSignature(message, signature, "rsa");
    }

    @Test
    public void verifyWithServeralAliases() throws Exception {
        verifyDetachedSignatureWithKeystore("system.jks", "rsa", "rsa2");
    }

    @Test
    public void verifyWithServeralAliasesOneWithNoEntryInKeystore() throws Exception {
        verifyDetachedSignatureWithKeystore("system.jks", "noEntry", "rsa");
    }

    @Test(expected = CryptoCmsException.class)
    public void verifyWithEmptyAlias() throws Exception {
        verifyDetachedSignatureWithKeystore("system.jks", "");
    }

    @Test(expected = CryptoCmsNoCertificateForSignerInfoException.class)
    public void verifyDetachedSignatureWithAliasNotFittingToSigner() throws Exception {
        verifyDetachedSignatureWithKeystore("system.jks", "rsa2");
    }

    @Test(expected = CryptoCmsNoCertificateForSignerInfosException.class)
    public void verifyDetachedSignatureWithAliasNotFittingToSignerWithVerifiyAllSignaturesFalse() throws Exception {
        verifyDetachedSignatureWithKeystore("system.jks", Boolean.FALSE, "rsa2");
    }

    @Test
    public void signatureAndContentSeparatedExplicitMode() throws Exception {
        String keystoreName = "system.jks";
        String alias = "rsa";
        KeyStoreParameters keystore = KeystoreUtil.getKeyStoreParameters(keystoreName);
        DefaultSignerInfo signerInfo = new DefaultSignerInfo();
        signerInfo.setIncludeCertificates(false);// without certificates,

        // optional default value is
        // true
        signerInfo.setSignatureAlgorithm("SHA1withRSA");// mandatory

        signerInfo.setPrivateKeyAlias(alias);
        signerInfo.setKeyStoreParameters(keystore);
        SignedDataCreatorConfiguration config = new SignedDataCreatorConfiguration(new DefaultCamelContext());
        config.setSigner(signerInfo);
        config.setIncludeContent(false);// optional default value is true

        config.setToBase64(Boolean.TRUE);
        config.init();
        SignedDataCreator signer = new SignedDataCreator(config);
        String message = "Test Message";
        Exchange exchange = ExchangeUtil.getExchange();
        exchange.getIn().setBody(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
        signer.process(exchange);
        byte[] signature = exchange.getOut().getHeader(CAMEL_CRYPTO_CMS_SIGNED_DATA, byte[].class);
        DefaultSignedDataVerifierConfiguration verifierConf = getCryptoCmsSignedDataVerifierConf(keystoreName, Collections.singleton(alias), Boolean.FALSE);
        verifierConf.setSignedDataHeaderBase64(Boolean.TRUE);
        SignedDataVerifier verifier = new org.apache.camel.component.crypto.cms.sig.SignedDataVerifierFromHeader(verifierConf);
        exchange = ExchangeUtil.getExchange();
        exchange.getIn().setBody(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
        exchange.getIn().setHeader(CAMEL_CRYPTO_CMS_SIGNED_DATA, new ByteArrayInputStream(signature));
        verifier.process(exchange);
    }

    @Test
    public void testSigAlgorithmSHADSA() throws Exception {
        signAndVerifyByDSASigAlgorithm("SHA1withDSA");
    }

    // SHA224withDSA
    @Test
    public void testSigAlgorithmSHA224withDSA() throws Exception {
        signAndVerifyByDSASigAlgorithm("SHA224withDSA");
    }

    // SHA256withDSA
    @Test
    public void testSigAlgorithmSHA256withDSA() throws Exception {
        signAndVerifyByDSASigAlgorithm("SHA256withDSA");
    }

    // SHA1withECDSA // ECSDSA keys not supported
    @Test(expected = CryptoCmsException.class)
    public void testSigAlgorithmSHA1withECDSA() throws Exception {
        signAndVerifyByDSASigAlgorithm("SHA1withECDSA");
    }

    // MD2withRSA
    @Test
    public void testSigAlgorithmMD2withRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("MD2withRSA");
    }

    // MD5/RSA
    // MD2withRSA
    @Test
    public void testSigAlgorithmMD5withRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("MD5withRSA");
    }

    // SHA/RSA
    @Test
    public void testSigAlgorithmSHAwithRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("SHA1withRSA");// SHA/RSA");

    }

    // SHA224/RSA
    @Test
    public void testSigAlgorithmSHA224withRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("SHA224withRSA");
    }

    // SHA256/RSA
    @Test
    public void testSigAlgorithmSHA256withRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("SHA256withRSA");
    }

    // SHA384/RSA
    @Test
    public void testSigAlgorithmSHA384withRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("SHA384withRSA");
    }

    // SHA512/RSA
    @Test
    public void testSigAlgorithmSHA512withRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("SHA512withRSA");
    }

    // RIPEMD160/RSA
    @Test
    public void testSigAlgorithmRIPEMD160withRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("RIPEMD160withRSA");
    }

    // RIPEMD128/RSA
    @Test
    public void testSigAlgorithmRIPEMD128withRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("RIPEMD128withRSA");
    }

    // RIPEMD256/RSA
    @Test
    public void testSigAlgorithmRIPEMD256withRSA() throws Exception {
        signAndVerifyByRSASigAlgorithm("RIPEMD256withRSA");
    }

    @Test(expected = CryptoCmsInvalidKeyException.class)
    public void testSigAlgorithmDoesnotFitToDSAPrivateKey() throws Exception {
        signAndVerifyByDSASigAlgorithm("RIPEMD128withRSA");
    }

    @Test(expected = CryptoCmsInvalidKeyException.class)
    public void testSigAlgorithmDoesnotFitToRSAPrivateKey() throws Exception {
        signAndVerifyByRSASigAlgorithm("SHA224withDSA");
    }
}

