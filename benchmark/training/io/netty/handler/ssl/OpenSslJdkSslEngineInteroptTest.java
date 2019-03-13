/**
 * Copyright 2016 The Netty Project
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


import javax.net.ssl.SSLException;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class OpenSslJdkSslEngineInteroptTest extends SSLEngineTest {
    public OpenSslJdkSslEngineInteroptTest(SSLEngineTest.BufferType type, SSLEngineTest.ProtocolCipherCombo combo, boolean delegate) {
        super(type, combo, delegate);
    }

    @Override
    @Test
    public void testMutualAuthInvalidIntermediateCASucceedWithOptionalClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthInvalidIntermediateCASucceedWithOptionalClientAuth();
    }

    @Override
    @Test
    public void testMutualAuthInvalidIntermediateCAFailWithOptionalClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthInvalidIntermediateCAFailWithOptionalClientAuth();
    }

    @Override
    @Test
    public void testMutualAuthInvalidIntermediateCAFailWithRequiredClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthInvalidIntermediateCAFailWithRequiredClientAuth();
    }

    @Override
    @Test
    public void testClientHostnameValidationSuccess() throws InterruptedException, SSLException {
        Assume.assumeTrue(OpenSsl.supportsHostnameValidation());
        super.testClientHostnameValidationSuccess();
    }

    @Override
    @Test
    public void testClientHostnameValidationFail() throws InterruptedException, SSLException {
        Assume.assumeTrue(OpenSsl.supportsHostnameValidation());
        super.testClientHostnameValidationFail();
    }

    @Override
    @Test
    public void testSessionAfterHandshakeKeyManagerFactoryMutualAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testSessionAfterHandshakeKeyManagerFactoryMutualAuth();
    }
}

