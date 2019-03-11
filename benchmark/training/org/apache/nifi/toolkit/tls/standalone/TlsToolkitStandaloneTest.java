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
package org.apache.nifi.toolkit.tls.standalone;


import ExitCode.ERROR_GENERATING_CONFIG;
import ExitCode.ERROR_PARSING_COMMAND_LINE;
import ExitCode.HELP;
import ExitCode.SUCCESS;
import KeystoreType.PKCS12;
import NiFiProperties.SECURITY_KEYSTORE_PASSWD;
import NiFiProperties.SECURITY_KEY_PASSWD;
import NiFiProperties.SECURITY_TRUSTSTORE_PASSWD;
import TlsConfig.DEFAULT_HOSTNAME;
import TlsConfig.DEFAULT_KEY_PAIR_ALGORITHM;
import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Properties;
import org.apache.nifi.toolkit.tls.SystemExitCapturer;
import org.junit.Assert;
import org.junit.Test;

import static TlsToolkitStandaloneCommandLine.NIFI_DN_PREFIX_ARG;
import static TlsToolkitStandaloneCommandLine.NIFI_DN_SUFFIX_ARG;


public class TlsToolkitStandaloneTest {
    public static final String NIFI_FAKE_PROPERTY = "nifi.fake.property";

    public static final String FAKE_VALUE = "fake value";

    public static final String TEST_NIFI_PROPERTIES = "src/test/resources/localhost/nifi.properties";

    private SystemExitCapturer systemExitCapturer;

    private File tempDir;

    @Test
    public void testBadParse() {
        runAndAssertExitCode(ERROR_PARSING_COMMAND_LINE, "--unknownArgument");
    }

    @Test
    public void testHelp() {
        runAndAssertExitCode(HELP, "-h");
        runAndAssertExitCode(HELP, "--help");
    }

    @Test
    public void testDirOutput() throws Exception {
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-n", DEFAULT_HOSTNAME);
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        Properties nifiProperties = checkHostDirAndReturnNifiProperties(DEFAULT_HOSTNAME, x509Certificate);
        Assert.assertNull(nifiProperties.get("nifi.fake.property"));
        Assert.assertEquals(nifiProperties.getProperty(SECURITY_KEYSTORE_PASSWD), nifiProperties.getProperty(SECURITY_KEY_PASSWD));
    }

    @Test
    public void testDifferentArg() throws Exception {
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-g", "-n", DEFAULT_HOSTNAME);
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        Properties nifiProperties = checkHostDirAndReturnNifiProperties(DEFAULT_HOSTNAME, x509Certificate);
        Assert.assertNull(nifiProperties.get("nifi.fake.property"));
        Assert.assertNotEquals(nifiProperties.getProperty(SECURITY_KEYSTORE_PASSWD), nifiProperties.getProperty(SECURITY_KEY_PASSWD));
    }

    @Test
    public void testFileArg() throws Exception {
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-f", TlsToolkitStandaloneTest.TEST_NIFI_PROPERTIES, "-n", DEFAULT_HOSTNAME);
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        Properties nifiProperties = checkHostDirAndReturnNifiProperties(DEFAULT_HOSTNAME, x509Certificate);
        Assert.assertEquals(TlsToolkitStandaloneTest.FAKE_VALUE, nifiProperties.get(TlsToolkitStandaloneTest.NIFI_FAKE_PROPERTY));
    }

    @Test
    public void testHostnamesArgumentOverwrite() throws Exception {
        String nifi1 = "nifi1";
        String nifi2 = "nifi2";
        String nifi3 = "nifi3";
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-n", ((nifi1 + ",") + nifi2));
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-n", nifi3);
        checkHostDirAndReturnNifiProperties(nifi1, x509Certificate);
        checkHostDirAndReturnNifiProperties(nifi2, x509Certificate);
        checkHostDirAndReturnNifiProperties(nifi3, x509Certificate);
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-O", "-n", nifi3);
        checkHostDirAndReturnNifiProperties(nifi3, x509Certificate);
    }

    @Test
    public void testHostnamesArgumentNoOverwrite() throws Exception {
        String nifi = "nifi";
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-n", nifi);
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        checkHostDirAndReturnNifiProperties(nifi, x509Certificate);
        runAndAssertExitCode(ERROR_GENERATING_CONFIG, "-o", tempDir.getAbsolutePath(), "-n", nifi);
    }

    @Test
    public void testKeyPasswordArg() throws Exception {
        String testKey = "testKey";
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-K", testKey, "-n", DEFAULT_HOSTNAME);
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        Properties nifiProperties = checkHostDirAndReturnNifiProperties(DEFAULT_HOSTNAME, x509Certificate);
        Assert.assertEquals(testKey, nifiProperties.getProperty(SECURITY_KEY_PASSWD));
    }

    @Test
    public void testKeyStorePasswordArg() throws Exception {
        String testKeyStore = "testKeyStore";
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-S", testKeyStore, "-n", DEFAULT_HOSTNAME);
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        Properties nifiProperties = checkHostDirAndReturnNifiProperties(DEFAULT_HOSTNAME, x509Certificate);
        Assert.assertEquals(testKeyStore, nifiProperties.getProperty(SECURITY_KEYSTORE_PASSWD));
    }

    @Test
    public void testTrustStorePasswordArg() throws Exception {
        String testTrustStore = "testTrustStore";
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-P", testTrustStore, "-n", DEFAULT_HOSTNAME);
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        Properties nifiProperties = checkHostDirAndReturnNifiProperties(DEFAULT_HOSTNAME, x509Certificate);
        Assert.assertEquals(testTrustStore, nifiProperties.getProperty(SECURITY_TRUSTSTORE_PASSWD));
    }

    @Test
    public void testDnArgs() throws Exception {
        String nifiDnPrefix = "O=apache, CN=";
        String nifiDnSuffix = ", OU=nifi";
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-n", DEFAULT_HOSTNAME, ("--" + (NIFI_DN_PREFIX_ARG)), nifiDnPrefix, ("--" + (NIFI_DN_SUFFIX_ARG)), nifiDnSuffix);
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        checkHostDirAndReturnNifiProperties(DEFAULT_HOSTNAME, nifiDnPrefix, nifiDnSuffix, x509Certificate);
    }

    @Test
    public void testKeyStoreTypeArg() throws Exception {
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-n", DEFAULT_HOSTNAME, "-T", PKCS12.toString().toLowerCase(), "-K", "change", "-S", "change", "-P", "change");
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        checkHostDirAndReturnNifiProperties(DEFAULT_HOSTNAME, x509Certificate);
    }

    @Test
    public void testClientDnsArg() throws Exception {
        String clientDn = "OU=NIFI,CN=testuser";
        String clientDn2 = "OU=NIFI,CN=testuser2";
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-C", clientDn, "-C", clientDn2, "-B", "pass1", "-P", "pass2");
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        checkClientCert(clientDn, x509Certificate);
        checkClientCert(clientDn2, x509Certificate);
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-O", "-C", clientDn2, "-B", "pass3");
        checkClientCert(clientDn2, x509Certificate);
    }

    @Test
    public void testClientDnsArgNoOverwrite() throws Exception {
        String clientDn = "OU=NIFI,CN=testuser";
        runAndAssertExitCode(SUCCESS, "-o", tempDir.getAbsolutePath(), "-C", clientDn, "-B", "passwor");
        X509Certificate x509Certificate = checkLoadCertPrivateKey(DEFAULT_KEY_PAIR_ALGORITHM);
        checkClientCert(clientDn, x509Certificate);
        runAndAssertExitCode(ERROR_GENERATING_CONFIG, "-o", tempDir.getAbsolutePath(), "-C", clientDn);
    }
}

