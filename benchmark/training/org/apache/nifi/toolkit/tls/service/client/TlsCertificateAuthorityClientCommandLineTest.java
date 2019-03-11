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
package org.apache.nifi.toolkit.tls.service.client;


import ExitCode.ERROR_TOKEN_ARG_EMPTY;
import ExitCode.HELP;
import KeystoreType.JKS;
import TlsCertificateAuthorityClientCommandLine.DEFAULT_CERTIFICATE_DIRECTORY;
import TlsCertificateAuthorityClientCommandLine.DEFAULT_CONFIG_JSON;
import TlsConfig.DEFAULT_HOSTNAME;
import TlsConfig.DEFAULT_KEY_PAIR_ALGORITHM;
import TlsConfig.DEFAULT_KEY_SIZE;
import TlsConfig.DEFAULT_KEY_STORE_TYPE;
import TlsConfig.DEFAULT_PORT;
import java.io.IOException;
import java.net.InetAddress;
import org.apache.nifi.toolkit.tls.commandLine.CommandLineParseException;
import org.apache.nifi.toolkit.tls.configuration.TlsClientConfig;
import org.apache.nifi.toolkit.tls.configuration.TlsConfig;
import org.apache.nifi.toolkit.tls.service.BaseCertificateAuthorityCommandLine;
import org.junit.Assert;
import org.junit.Test;

import static TlsCertificateAuthorityClientCommandLine.KEYSTORE;
import static TlsCertificateAuthorityClientCommandLine.TRUSTSTORE;


public class TlsCertificateAuthorityClientCommandLineTest {
    private TlsCertificateAuthorityClientCommandLine tlsCertificateAuthorityClientCommandLine;

    private String testToken;

    @Test
    public void testNoToken() {
        try {
            tlsCertificateAuthorityClientCommandLine.parse(new String[0]);
            Assert.fail("Expected failure with no token argument");
        } catch (CommandLineParseException e) {
            Assert.assertEquals(ERROR_TOKEN_ARG_EMPTY, e.getExitCode());
        }
    }

    @Test
    public void testDefaults() throws IOException, CommandLineParseException {
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken);
        TlsClientConfig clientConfig = tlsCertificateAuthorityClientCommandLine.createClientConfig();
        Assert.assertEquals(DEFAULT_HOSTNAME, clientConfig.getCaHostname());
        Assert.assertEquals(new TlsConfig().calcDefaultDn(InetAddress.getLocalHost().getHostName()), clientConfig.getDn());
        Assert.assertEquals(((KEYSTORE) + (DEFAULT_KEY_STORE_TYPE.toLowerCase())), clientConfig.getKeyStore());
        Assert.assertEquals(DEFAULT_KEY_STORE_TYPE, clientConfig.getKeyStoreType());
        Assert.assertNull(clientConfig.getKeyStorePassword());
        Assert.assertNull(clientConfig.getKeyPassword());
        Assert.assertEquals(((TRUSTSTORE) + (DEFAULT_KEY_STORE_TYPE.toLowerCase())), clientConfig.getTrustStore());
        Assert.assertEquals(DEFAULT_KEY_STORE_TYPE, clientConfig.getTrustStoreType());
        Assert.assertNull(clientConfig.getTrustStorePassword());
        Assert.assertEquals(DEFAULT_KEY_SIZE, clientConfig.getKeySize());
        Assert.assertEquals(DEFAULT_KEY_PAIR_ALGORITHM, clientConfig.getKeyPairAlgorithm());
        Assert.assertEquals(testToken, clientConfig.getToken());
        Assert.assertEquals(DEFAULT_PORT, clientConfig.getPort());
        Assert.assertEquals(DEFAULT_CONFIG_JSON, tlsCertificateAuthorityClientCommandLine.getConfigJsonOut());
        Assert.assertNull(tlsCertificateAuthorityClientCommandLine.getConfigJsonIn());
        Assert.assertEquals(DEFAULT_CERTIFICATE_DIRECTORY, tlsCertificateAuthorityClientCommandLine.getCertificateDirectory());
    }

    @Test
    public void testKeySize() throws CommandLineParseException {
        int keySize = 1234;
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-k", Integer.toString(keySize));
        Assert.assertEquals(keySize, tlsCertificateAuthorityClientCommandLine.getKeySize());
    }

    @Test
    public void testKeyPairAlgorithm() throws CommandLineParseException {
        String testAlgorithm = "testAlgorithm";
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-a", testAlgorithm);
        Assert.assertEquals(testAlgorithm, tlsCertificateAuthorityClientCommandLine.getKeyAlgorithm());
    }

    @Test
    public void testHelp() {
        try {
            tlsCertificateAuthorityClientCommandLine.parse("-h");
            Assert.fail("Expected exception");
        } catch (CommandLineParseException e) {
            Assert.assertEquals(HELP, e.getExitCode());
        }
    }

    @Test
    public void testCaHostname() throws IOException, CommandLineParseException {
        String testCaHostname = "testCaHostname";
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-c", testCaHostname);
        Assert.assertEquals(testCaHostname, tlsCertificateAuthorityClientCommandLine.createClientConfig().getCaHostname());
    }

    @Test
    public void testDn() throws IOException, CommandLineParseException {
        String testDn = "testDn";
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-D", testDn);
        Assert.assertEquals(testDn, tlsCertificateAuthorityClientCommandLine.createClientConfig().getDn());
    }

    @Test
    public void testPort() throws IOException, CommandLineParseException {
        int testPort = 2345;
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-p", Integer.toString(testPort));
        Assert.assertEquals(testPort, tlsCertificateAuthorityClientCommandLine.createClientConfig().getPort());
    }

    @Test
    public void testKeyStoreType() throws IOException, CommandLineParseException {
        String testType = "testType";
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-T", testType);
        TlsClientConfig clientConfig = tlsCertificateAuthorityClientCommandLine.createClientConfig();
        Assert.assertEquals(testType, clientConfig.getKeyStoreType());
        String trustStoreType = JKS.toString().toLowerCase();
        Assert.assertEquals(trustStoreType, clientConfig.getTrustStoreType());
        Assert.assertEquals(((KEYSTORE) + (testType.toLowerCase())), clientConfig.getKeyStore());
        Assert.assertEquals(((TRUSTSTORE) + trustStoreType), clientConfig.getTrustStore());
    }

    @Test
    public void testConfigJsonOut() throws CommandLineParseException {
        String testPath = "/1/2/3/4";
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-f", testPath);
        Assert.assertEquals(testPath, tlsCertificateAuthorityClientCommandLine.getConfigJsonOut());
        Assert.assertNull(tlsCertificateAuthorityClientCommandLine.getConfigJsonIn());
    }

    @Test
    public void testConfigJsonOutAndUseForBoth() throws CommandLineParseException {
        String testPath = "/1/2/3/4";
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-f", testPath, "-F");
        Assert.assertEquals(testPath, tlsCertificateAuthorityClientCommandLine.getConfigJsonOut());
        Assert.assertEquals(testPath, tlsCertificateAuthorityClientCommandLine.getConfigJsonIn());
    }

    @Test
    public void testConfigJsonIn() throws CommandLineParseException {
        String testPath = "/1/2/3/4";
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, ("--" + (BaseCertificateAuthorityCommandLine.READ_CONFIG_JSON_ARG)), testPath);
        Assert.assertEquals(BaseCertificateAuthorityCommandLine.DEFAULT_CONFIG_JSON, tlsCertificateAuthorityClientCommandLine.getConfigJsonOut());
        Assert.assertEquals(testPath, tlsCertificateAuthorityClientCommandLine.getConfigJsonIn());
    }

    @Test
    public void testConfigJsonInAndOut() throws CommandLineParseException {
        String testPath = "/1/2/3/4";
        String testIn = "/2/3/4/5";
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-f", testPath, ("--" + (BaseCertificateAuthorityCommandLine.READ_CONFIG_JSON_ARG)), testIn);
        Assert.assertEquals(testPath, tlsCertificateAuthorityClientCommandLine.getConfigJsonOut());
        Assert.assertEquals(testIn, tlsCertificateAuthorityClientCommandLine.getConfigJsonIn());
    }

    @Test
    public void testCertificateFile() throws CommandLineParseException {
        String testCertificateFile = "testCertificateFile";
        tlsCertificateAuthorityClientCommandLine.parse("-t", testToken, "-C", testCertificateFile);
        Assert.assertEquals(testCertificateFile, tlsCertificateAuthorityClientCommandLine.getCertificateDirectory());
    }
}

