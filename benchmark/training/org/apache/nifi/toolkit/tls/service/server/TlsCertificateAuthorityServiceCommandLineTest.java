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
package org.apache.nifi.toolkit.tls.service.server;


import BaseCertificateAuthorityCommandLine.DEFAULT_CONFIG_JSON;
import TlsConfig.DEFAULT_DAYS;
import TlsConfig.DEFAULT_HOSTNAME;
import TlsConfig.DEFAULT_KEY_PAIR_ALGORITHM;
import TlsConfig.DEFAULT_KEY_SIZE;
import TlsConfig.DEFAULT_KEY_STORE_TYPE;
import TlsConfig.DEFAULT_PORT;
import TlsConfig.DEFAULT_SIGNING_ALGORITHM;
import java.io.IOException;
import org.apache.nifi.toolkit.tls.commandLine.CommandLineParseException;
import org.apache.nifi.toolkit.tls.configuration.TlsConfig;
import org.apache.nifi.toolkit.tls.service.BaseCertificateAuthorityCommandLine;
import org.apache.nifi.toolkit.tls.util.InputStreamFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static TlsCertificateAuthorityServiceCommandLine.NIFI_CA_KEYSTORE;


@RunWith(MockitoJUnitRunner.class)
public class TlsCertificateAuthorityServiceCommandLineTest {
    @Mock
    InputStreamFactory inputStreamFactory;

    TlsCertificateAuthorityServiceCommandLine tlsCertificateAuthorityServiceCommandLine;

    String testToken;

    @Test
    public void testDefaults() throws IOException, CommandLineParseException {
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken);
        Assert.assertEquals(DEFAULT_CONFIG_JSON, tlsCertificateAuthorityServiceCommandLine.getConfigJsonOut());
        Assert.assertNull(tlsCertificateAuthorityServiceCommandLine.getConfigJsonIn());
        TlsConfig tlsConfig = tlsCertificateAuthorityServiceCommandLine.createConfig();
        Assert.assertEquals(DEFAULT_HOSTNAME, tlsConfig.getCaHostname());
        Assert.assertEquals(testToken, tlsConfig.getToken());
        Assert.assertEquals(DEFAULT_PORT, tlsConfig.getPort());
        Assert.assertEquals(DEFAULT_KEY_STORE_TYPE, tlsConfig.getKeyStoreType());
        Assert.assertEquals(((NIFI_CA_KEYSTORE) + (tlsConfig.getKeyStoreType().toLowerCase())), tlsConfig.getKeyStore());
        Assert.assertNull(tlsConfig.getKeyStorePassword());
        Assert.assertNull(tlsConfig.getKeyPassword());
        Assert.assertEquals(DEFAULT_KEY_SIZE, tlsConfig.getKeySize());
        Assert.assertEquals(DEFAULT_KEY_PAIR_ALGORITHM, tlsConfig.getKeyPairAlgorithm());
        Assert.assertEquals(DEFAULT_SIGNING_ALGORITHM, tlsConfig.getSigningAlgorithm());
        Assert.assertEquals(DEFAULT_DAYS, tlsConfig.getDays());
    }

    @Test
    public void testCaHostname() throws IOException, CommandLineParseException {
        String testCaHostname = "testCaHostname";
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-c", testCaHostname);
        Assert.assertEquals(testCaHostname, tlsCertificateAuthorityServiceCommandLine.createConfig().getCaHostname());
    }

    @Test
    public void testPort() throws IOException, CommandLineParseException {
        int testPort = 4321;
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-p", Integer.toString(testPort));
        Assert.assertEquals(testPort, tlsCertificateAuthorityServiceCommandLine.createConfig().getPort());
    }

    @Test
    public void testKeyStoreType() throws IOException, CommandLineParseException {
        String testKeyStoreType = "testKeyStoreType";
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-T", testKeyStoreType);
        TlsConfig tlsConfig = tlsCertificateAuthorityServiceCommandLine.createConfig();
        Assert.assertEquals(testKeyStoreType, tlsConfig.getKeyStoreType());
        Assert.assertEquals(((NIFI_CA_KEYSTORE) + (tlsConfig.getKeyStoreType().toLowerCase())), tlsConfig.getKeyStore());
    }

    @Test
    public void testKeySize() throws IOException, CommandLineParseException {
        int testKeySize = 8192;
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-k", Integer.toString(testKeySize));
        Assert.assertEquals(testKeySize, tlsCertificateAuthorityServiceCommandLine.createConfig().getKeySize());
    }

    @Test
    public void testKeyPairAlgorithm() throws IOException, CommandLineParseException {
        String testAlgorithm = "testAlgorithm";
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-a", testAlgorithm);
        Assert.assertEquals(testAlgorithm, tlsCertificateAuthorityServiceCommandLine.createConfig().getKeyPairAlgorithm());
    }

    @Test
    public void testSigningAlgorithm() throws IOException, CommandLineParseException {
        String testSigningAlgorithm = "testSigningAlgorithm";
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-s", testSigningAlgorithm);
        Assert.assertEquals(testSigningAlgorithm, tlsCertificateAuthorityServiceCommandLine.createConfig().getSigningAlgorithm());
    }

    @Test
    public void testDays() throws IOException, CommandLineParseException {
        int days = 1234;
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-d", Integer.toString(days));
        Assert.assertEquals(days, tlsCertificateAuthorityServiceCommandLine.createConfig().getDays());
    }

    @Test
    public void testConfigJsonOut() throws CommandLineParseException {
        String out = "testJson.out";
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-f", out);
        Assert.assertEquals(out, tlsCertificateAuthorityServiceCommandLine.getConfigJsonOut());
        Assert.assertNull(tlsCertificateAuthorityServiceCommandLine.getConfigJsonIn());
    }

    @Test
    public void testConfigJsonOutAndUseForBoth() throws CommandLineParseException {
        String out = "testJson.out";
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-f", out, "-F");
        Assert.assertEquals(out, tlsCertificateAuthorityServiceCommandLine.getConfigJsonOut());
        Assert.assertEquals(out, tlsCertificateAuthorityServiceCommandLine.getConfigJsonIn());
    }

    @Test
    public void testConfigJsonIn() throws CommandLineParseException {
        String in = "testJson.in";
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, ("--" + (BaseCertificateAuthorityCommandLine.READ_CONFIG_JSON_ARG)), in);
        Assert.assertEquals(DEFAULT_CONFIG_JSON, tlsCertificateAuthorityServiceCommandLine.getConfigJsonOut());
        Assert.assertEquals(in, tlsCertificateAuthorityServiceCommandLine.getConfigJsonIn());
    }

    @Test
    public void testConfigJsonInAndOut() throws CommandLineParseException {
        String out = "testJson.out";
        String in = "testJson.in";
        tlsCertificateAuthorityServiceCommandLine.parse("-t", testToken, "-f", out, ("--" + (BaseCertificateAuthorityCommandLine.READ_CONFIG_JSON_ARG)), in);
        Assert.assertEquals(out, tlsCertificateAuthorityServiceCommandLine.getConfigJsonOut());
        Assert.assertEquals(in, tlsCertificateAuthorityServiceCommandLine.getConfigJsonIn());
    }
}

