/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.jose;


import JWEConstants.A128CBC_HS256;
import JWEKeyStorage.KeyUse.ENCRYPTION;
import JWEKeyStorage.KeyUse.SIGNATURE;
import java.io.UnsupportedEncodingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.common.util.Base64;
import org.keycloak.common.util.Base64Url;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JWETest {
    private static final String PAYLOAD = "Hello world! How are you man? I hope you are fine. This is some quite a long text, which is much longer than just simple 'Hello World'";

    private static final byte[] HMAC_SHA256_KEY = new byte[]{ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16 };

    private static final byte[] AES_128_KEY = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

    private static final byte[] HMAC_SHA512_KEY = new byte[]{ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

    private static final byte[] AES_256_KEY = new byte[]{ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

    @Test
    public void testDirect_Aes128CbcHmacSha256() throws Exception {
        SecretKey aesKey = new SecretKeySpec(JWETest.AES_128_KEY, "AES");
        SecretKey hmacKey = new SecretKeySpec(JWETest.HMAC_SHA256_KEY, "HMACSHA2");
        testDirectEncryptAndDecrypt(aesKey, hmacKey, A128CBC_HS256, JWETest.PAYLOAD, true);
    }

    @Test
    public void testPassword() throws Exception {
        byte[] salt = JWEUtils.generateSecret(8);
        String encodedSalt = Base64.encodeBytes(salt);
        String jwe = JWE.encryptUTF8("geheim", encodedSalt, JWETest.PAYLOAD);
        String decodedContent = JWE.decryptUTF8("geheim", encodedSalt, jwe);
        Assert.assertEquals(JWETest.PAYLOAD, decodedContent);
    }

    @Test
    public void testAesKW_Aes128CbcHmacSha256() throws Exception {
        SecretKey aesKey = new SecretKeySpec(JWETest.AES_128_KEY, "AES");
        testAesKW_Aes128CbcHmacSha256(aesKey);
    }

    @Test
    public void testSalt() {
        byte[] random = JWEUtils.generateSecret(8);
        System.out.print("new byte[] = {");
        for (byte b : random) {
            System.out.print((("" + (Byte.toString(b))) + ","));
        }
    }

    @Test
    public void externalJweAes128CbcHmacSha256Test() throws UnsupportedEncodingException, JWEException {
        String externalJwe = "eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..qysUrI1iVtiG4Z4jyr7XXg.apdNSQhR7WDMg6IHf5aLVI0gGp6JuOHYmIUtflns4WHmyxOOnh_GShLI6DWaK_SiywTV5gZvZYtl8H8Iv5fTfLkc4tiDDjbdtmsOP7tqyRxVh069gU5UvEAgmCXbIKALutgYXcYe2WM4E6BIHPTSt8jXdkktFcm7XHiD7mpakZyjXsG8p3XVkQJ72WbJI_t6.Ks6gHeko7BRTZ4CFs5ijRA";
        System.out.println(("External encoded content length: " + (externalJwe.length())));
        final SecretKey aesKey = new SecretKeySpec(JWETest.AES_128_KEY, "AES");
        final SecretKey hmacKey = new SecretKeySpec(JWETest.HMAC_SHA256_KEY, "HMACSHA2");
        JWE jwe = new JWE();
        jwe.getKeyStorage().setCEKKey(aesKey, ENCRYPTION).setCEKKey(hmacKey, SIGNATURE);
        jwe.verifyAndDecodeJwe(externalJwe);
        String decodedContent = new String(jwe.getContent(), "UTF-8");
        Assert.assertEquals(JWETest.PAYLOAD, decodedContent);
    }

    @Test
    public void externalJweAes128KeyWrapTest() throws Exception {
        // See example "A.3" from JWE specification - https://tools.ietf.org/html/rfc7516#page-41
        String externalJwe = "eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.6KB707dM9YTIgHtLvtgWQ8mKwboJW3of9locizkDTHzBC2IlrT1oOQ.AxY8DCtDaGlsbGljb3RoZQ.KDlTtXchhZTGufMYmOYGS4HffxPSUrfmqCHXaI9wOGY.U0m_YmjN04DJvceFICbCVQ";
        byte[] aesKey = Base64Url.decode("GawgguFyGrWKav7AX4VKUg");
        SecretKeySpec aesKeySpec = new SecretKeySpec(aesKey, "AES");
        JWE jwe = new JWE();
        jwe.getKeyStorage().setEncryptionKey(aesKeySpec);
        jwe.verifyAndDecodeJwe(externalJwe);
        String decodedContent = new String(jwe.getContent(), "UTF-8");
        Assert.assertEquals("Live long and prosper.", decodedContent);
    }
}

