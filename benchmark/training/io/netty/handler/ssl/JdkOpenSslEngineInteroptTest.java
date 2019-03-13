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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JdkOpenSslEngineInteroptTest extends SSLEngineTest {
    public JdkOpenSslEngineInteroptTest(SSLEngineTest.BufferType type, SSLEngineTest.ProtocolCipherCombo protocolCipherCombo, boolean delegate) {
        super(type, protocolCipherCombo, delegate);
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
    public void testMutualAuthValidClientCertChainTooLongFailOptionalClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthValidClientCertChainTooLongFailOptionalClientAuth();
    }

    @Override
    @Test
    public void testMutualAuthValidClientCertChainTooLongFailRequireClientAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testMutualAuthValidClientCertChainTooLongFailRequireClientAuth();
    }

    @Override
    @Test
    public void testSessionAfterHandshakeKeyManagerFactoryMutualAuth() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testSessionAfterHandshakeKeyManagerFactoryMutualAuth();
    }

    @Override
    @Test
    public void testSessionAfterHandshakeKeyManagerFactory() throws Exception {
        OpenSslTestUtils.checkShouldUseKeyManagerFactory();
        super.testSessionAfterHandshakeKeyManagerFactory();
    }
}

