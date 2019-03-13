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
package org.apache.camel.component.xmlsecurity;


import java.lang.reflect.Constructor;
import java.security.Provider;
import java.security.Security;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;


/**
 * Test for the ECDSA algorithms
 */
public class ECDSASignatureTest extends CamelTestSupport {
    private static String payload;

    private boolean canTest = true;

    static {
        boolean includeNewLine = true;
        if ((TestSupport.getJavaMajorVersion()) >= 9) {
            includeNewLine = false;
        }
        ECDSASignatureTest.payload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (includeNewLine ? "\n" : "")) + "<root xmlns=\"http://test/test\"><test>Test Message</test></root>";
    }

    public ECDSASignatureTest() throws Exception {
        try {
            // BouncyCastle is required for some algorithms
            if ((Security.getProvider("BC")) == null) {
                Constructor<?> cons;
                Class<?> c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
                cons = c.getConstructor(new Class[]{  });
                Provider provider = ((Provider) (cons.newInstance()));
                Security.insertProviderAt(provider, 2);
            }
            // This test fails with the IBM JDK
            if (isJavaVendor("IBM")) {
                canTest = false;
            }
        } catch (Exception e) {
            System.err.println(("Cannot test due " + (e.getMessage())));
            log.warn(("Cannot test due " + (e.getMessage())), e);
            canTest = false;
        }
    }

    @Test
    public void testECDSASHA1() throws Exception {
        if (!(canTest)) {
            return;
        }
        setupMock();
        sendBody("direct:ecdsa_sha1", ECDSASignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testECDSASHA224() throws Exception {
        if (!(canTest)) {
            return;
        }
        setupMock();
        sendBody("direct:ecdsa_sha224", ECDSASignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testECDSASHA256() throws Exception {
        if (!(canTest)) {
            return;
        }
        setupMock();
        sendBody("direct:ecdsa_sha256", ECDSASignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testECDSASHA384() throws Exception {
        if (!(canTest)) {
            return;
        }
        setupMock();
        sendBody("direct:ecdsa_sha384", ECDSASignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testECDSASHA512() throws Exception {
        if (!(canTest)) {
            return;
        }
        setupMock();
        sendBody("direct:ecdsa_sha512", ECDSASignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testECDSARIPEMD160() throws Exception {
        if (!(canTest)) {
            return;
        }
        setupMock();
        sendBody("direct:ecdsa_ripemd160", ECDSASignatureTest.payload);
        assertMockEndpointsSatisfied();
    }
}

