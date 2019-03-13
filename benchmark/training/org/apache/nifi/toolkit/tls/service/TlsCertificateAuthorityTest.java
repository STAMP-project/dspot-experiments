/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.toolkit.tls.service;


import KeystoreType.PKCS12;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.nifi.toolkit.tls.configuration.TlsClientConfig;
import org.apache.nifi.toolkit.tls.configuration.TlsConfig;
import org.apache.nifi.toolkit.tls.service.client.TlsCertificateAuthorityClient;
import org.apache.nifi.toolkit.tls.service.server.TlsCertificateAuthorityService;
import org.apache.nifi.toolkit.tls.util.InputStreamFactory;
import org.apache.nifi.toolkit.tls.util.OutputStreamFactory;
import org.junit.Assert;
import org.junit.Test;


public class TlsCertificateAuthorityTest {
    private File serverConfigFile;

    private File clientConfigFile;

    private OutputStreamFactory outputStreamFactory;

    private InputStreamFactory inputStreamFactory;

    private TlsConfig serverConfig;

    private TlsClientConfig clientConfig;

    private ObjectMapper objectMapper;

    private ByteArrayOutputStream serverKeyStoreOutputStream;

    private ByteArrayOutputStream clientKeyStoreOutputStream;

    private ByteArrayOutputStream clientTrustStoreOutputStream;

    private ByteArrayOutputStream serverConfigFileOutputStream;

    private ByteArrayOutputStream clientConfigFileOutputStream;

    @Test
    public void testClientGetCertDifferentPasswordsForKeyAndKeyStore() throws Exception {
        TlsCertificateAuthorityService tlsCertificateAuthorityService = null;
        try {
            tlsCertificateAuthorityService = new TlsCertificateAuthorityService(outputStreamFactory);
            tlsCertificateAuthorityService.start(serverConfig, serverConfigFile.getAbsolutePath(), true);
            TlsCertificateAuthorityClient tlsCertificateAuthorityClient = new TlsCertificateAuthorityClient(outputStreamFactory);
            tlsCertificateAuthorityClient.generateCertificateAndGetItSigned(clientConfig, null, clientConfigFile.getAbsolutePath(), true);
            validate();
        } finally {
            if (tlsCertificateAuthorityService != null) {
                tlsCertificateAuthorityService.shutdown();
            }
        }
    }

    @Test
    public void testClientGetCertSamePasswordsForKeyAndKeyStore() throws Exception {
        TlsCertificateAuthorityService tlsCertificateAuthorityService = null;
        try {
            tlsCertificateAuthorityService = new TlsCertificateAuthorityService(outputStreamFactory);
            tlsCertificateAuthorityService.start(serverConfig, serverConfigFile.getAbsolutePath(), false);
            TlsCertificateAuthorityClient tlsCertificateAuthorityClient = new TlsCertificateAuthorityClient(outputStreamFactory);
            tlsCertificateAuthorityClient.generateCertificateAndGetItSigned(clientConfig, null, clientConfigFile.getAbsolutePath(), false);
            validate();
        } finally {
            if (tlsCertificateAuthorityService != null) {
                tlsCertificateAuthorityService.shutdown();
            }
        }
    }

    @Test
    public void testClientPkcs12() throws Exception {
        serverConfig.setKeyStoreType(PKCS12.toString());
        clientConfig.setKeyStoreType(PKCS12.toString());
        TlsCertificateAuthorityService tlsCertificateAuthorityService = null;
        try {
            tlsCertificateAuthorityService = new TlsCertificateAuthorityService(outputStreamFactory);
            tlsCertificateAuthorityService.start(serverConfig, serverConfigFile.getAbsolutePath(), false);
            TlsCertificateAuthorityClient tlsCertificateAuthorityClient = new TlsCertificateAuthorityClient(outputStreamFactory);
            new org.apache.nifi.toolkit.tls.service.client.TlsCertificateAuthorityClientCommandLine(inputStreamFactory);
            tlsCertificateAuthorityClient.generateCertificateAndGetItSigned(clientConfig, null, clientConfigFile.getAbsolutePath(), true);
            validate();
        } finally {
            if (tlsCertificateAuthorityService != null) {
                tlsCertificateAuthorityService.shutdown();
            }
        }
    }

    @Test
    public void testTokenMismatch() throws Exception {
        serverConfig.setToken("a different token...");
        try {
            testClientGetCertSamePasswordsForKeyAndKeyStore();
            Assert.fail("Expected error with mismatching token");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("forbidden"));
        }
    }
}

