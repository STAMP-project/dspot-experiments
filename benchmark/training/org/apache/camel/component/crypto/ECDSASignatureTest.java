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
package org.apache.camel.component.crypto;


import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class ECDSASignatureTest extends CamelTestSupport {
    private String payload = "Dear Alice, Rest assured it's me, signed Bob";

    private boolean ibmJDK;

    private PrivateKey privateKey;

    private X509Certificate x509;

    private boolean canRun = true;

    public ECDSASignatureTest() throws Exception {
        // BouncyCastle is required for ECDSA support for JDK 1.6
        if ((isJava16()) && ((Security.getProvider("BC")) == null)) {
            Constructor<?> cons;
            Class<?> c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            cons = c.getConstructor(new Class[]{  });
            Provider provider = ((Provider) (cons.newInstance()));
            Security.insertProviderAt(provider, 2);
        }
        // This test fails with the IBM JDK
        if (isJavaVendor("IBM")) {
            ibmJDK = true;
        }
        // see if we can load the keystore et all
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = ECDSASignatureTest.class.getResourceAsStream("/org/apache/camel/component/crypto/ecdsa.jks");
            keyStore.load(in, "security".toCharArray());
            privateKey = ((PrivateKey) (keyStore.getKey("ECDSA", "security".toCharArray())));
            x509 = ((X509Certificate) (keyStore.getCertificate("ECDSA")));
        } catch (Throwable e) {
            log.warn((("Cannot setup keystore for running this test due " + (e.getMessage())) + ". This test is skipped."), e);
            canRun = false;
        }
    }

    @Test
    public void testECDSASHA1() throws Exception {
        if ((ibmJDK) || (!(canRun))) {
            return;
        }
        setupMock();
        sendBody("direct:ecdsa-sha1", payload);
        assertMockEndpointsSatisfied();
    }
}

