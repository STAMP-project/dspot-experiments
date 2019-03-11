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


import ExitCode.ERROR_PARSING_COMMAND_LINE;
import ExitCode.ERROR_PARSING_INT_ARG;
import ExitCode.ERROR_READING_NIFI_PROPERTIES;
import ExitCode.HELP;
import TlsConfig.DEFAULT_HOSTNAME;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.nifi.toolkit.tls.commandLine.CommandLineParseException;
import org.apache.nifi.toolkit.tls.configuration.InstanceDefinition;
import org.apache.nifi.toolkit.tls.configuration.InstanceIdentifier;
import org.apache.nifi.toolkit.tls.configuration.StandaloneConfig;
import org.junit.Assert;
import org.junit.Test;


public class TlsToolkitStandaloneCommandLineTest {
    private SecureRandom secureRandom;

    private TlsToolkitStandaloneCommandLine tlsToolkitStandaloneCommandLine;

    @Test
    public void testHelp() {
        try {
            tlsToolkitStandaloneCommandLine.parse("-h");
            Assert.fail("Expected usage and help exit");
        } catch (CommandLineParseException e) {
            Assert.assertEquals(HELP, e.getExitCode());
        }
    }

    @Test
    public void testUnknownArg() {
        try {
            tlsToolkitStandaloneCommandLine.parse("--unknownArg");
            Assert.fail("Expected error parsing command line");
        } catch (CommandLineParseException e) {
            Assert.assertEquals(ERROR_PARSING_COMMAND_LINE, e.getExitCode());
        }
    }

    @Test
    public void testKeyAlgorithm() throws CommandLineParseException {
        String testKeyAlgorithm = "testKeyAlgorithm";
        tlsToolkitStandaloneCommandLine.parse("-a", testKeyAlgorithm);
        Assert.assertEquals(testKeyAlgorithm, tlsToolkitStandaloneCommandLine.createConfig().getKeyPairAlgorithm());
    }

    @Test
    public void testKeySizeArgNotInteger() {
        try {
            tlsToolkitStandaloneCommandLine.parse("-k", "badVal");
            Assert.fail("Expected bad keysize exit code");
        } catch (CommandLineParseException e) {
            Assert.assertEquals(ERROR_PARSING_INT_ARG, e.getExitCode());
        }
    }

    @Test
    public void testKeySize() throws IOException, CommandLineParseException {
        int testKeySize = 4096;
        tlsToolkitStandaloneCommandLine.parse("-k", Integer.toString(testKeySize));
        Assert.assertEquals(testKeySize, tlsToolkitStandaloneCommandLine.createConfig().getKeySize());
    }

    @Test
    public void testSigningAlgorithm() throws IOException, CommandLineParseException {
        String testSigningAlgorithm = "testSigningAlgorithm";
        tlsToolkitStandaloneCommandLine.parse("-s", testSigningAlgorithm);
        Assert.assertEquals(testSigningAlgorithm, tlsToolkitStandaloneCommandLine.createConfig().getSigningAlgorithm());
    }

    @Test
    public void testSAN() throws IOException, CommandLineParseException {
        String dnsSAN = "nifi.apache.org";
        tlsToolkitStandaloneCommandLine.parse("--subjectAlternativeNames", dnsSAN);
        Assert.assertEquals(dnsSAN, tlsToolkitStandaloneCommandLine.createConfig().getDomainAlternativeNames());
    }

    @Test
    public void testDaysNotInteger() {
        try {
            tlsToolkitStandaloneCommandLine.parse("-d", "badVal");
        } catch (CommandLineParseException e) {
            Assert.assertEquals(ERROR_PARSING_INT_ARG, e.getExitCode());
        }
    }

    @Test
    public void testDays() throws CommandLineParseException {
        int testDays = 29;
        tlsToolkitStandaloneCommandLine.parse("-d", Integer.toString(testDays));
        Assert.assertEquals(testDays, tlsToolkitStandaloneCommandLine.createConfig().getDays());
    }

    @Test
    public void testKeyStoreType() throws CommandLineParseException {
        String testKeyStoreType = "testKeyStoreType";
        tlsToolkitStandaloneCommandLine.parse("-T", testKeyStoreType);
        Assert.assertEquals(testKeyStoreType, tlsToolkitStandaloneCommandLine.getKeyStoreType());
    }

    @Test
    public void testOutputDirectory() throws CommandLineParseException {
        String testPath = (((((((File.separator) + "fake") + (File.separator)) + "path") + (File.separator)) + "doesnt") + (File.separator)) + "exist";
        tlsToolkitStandaloneCommandLine.parse("-o", testPath);
        Assert.assertEquals(testPath, tlsToolkitStandaloneCommandLine.createConfig().getBaseDir().getPath());
    }

    @Test
    public void testHostnames() throws CommandLineParseException {
        String nifi1 = "nifi1";
        String nifi2 = "nifi2";
        tlsToolkitStandaloneCommandLine.parse("-n", ((nifi1 + " , ") + nifi2));
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(2, instanceDefinitions.size());
        Assert.assertEquals(nifi1, instanceDefinitions.get(0).getHostname());
        Assert.assertEquals(nifi2, instanceDefinitions.get(1).getHostname());
    }

    @Test
    public void testNifiPropertiesFile() throws IOException, CommandLineParseException {
        tlsToolkitStandaloneCommandLine.parse("-f", TlsToolkitStandaloneTest.TEST_NIFI_PROPERTIES);
        Assert.assertEquals(TlsToolkitStandaloneTest.FAKE_VALUE, getProperties().get(TlsToolkitStandaloneTest.NIFI_FAKE_PROPERTY));
    }

    @Test
    public void testNifiPropertiesFileDefault() throws IOException, CommandLineParseException {
        tlsToolkitStandaloneCommandLine.parse();
        Assert.assertNull(getProperties().get(TlsToolkitStandaloneTest.NIFI_FAKE_PROPERTY));
    }

    @Test
    public void testBadNifiPropertiesFile() {
        try {
            tlsToolkitStandaloneCommandLine.parse("-f", "/this/file/should/not/exist.txt");
            Assert.fail("Expected error when unable to read file");
        } catch (CommandLineParseException e) {
            Assert.assertEquals(ERROR_READING_NIFI_PROPERTIES, e.getExitCode());
        }
    }

    @Test
    public void testNotSameKeyAndKeystorePassword() throws CommandLineParseException {
        tlsToolkitStandaloneCommandLine.parse("-g", "-n", DEFAULT_HOSTNAME);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(1, instanceDefinitions.size());
        Assert.assertNotEquals(instanceDefinitions.get(0).getKeyStorePassword(), instanceDefinitions.get(0).getKeyPassword());
    }

    @Test
    public void testSameKeyAndKeystorePassword() throws CommandLineParseException {
        tlsToolkitStandaloneCommandLine.parse("-n", DEFAULT_HOSTNAME);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(1, instanceDefinitions.size());
        Assert.assertEquals(instanceDefinitions.get(0).getKeyStorePassword(), instanceDefinitions.get(0).getKeyPassword());
    }

    @Test
    public void testSameKeyAndKeystorePasswordWithKeystorePasswordSpecified() throws CommandLineParseException {
        String testPassword = "testPassword";
        tlsToolkitStandaloneCommandLine.parse("-S", testPassword, "-n", DEFAULT_HOSTNAME);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(1, instanceDefinitions.size());
        Assert.assertEquals(testPassword, instanceDefinitions.get(0).getKeyStorePassword());
        Assert.assertEquals(testPassword, instanceDefinitions.get(0).getKeyPassword());
    }

    @Test
    public void testSameKeyAndKeystorePasswordWithKeyPasswordSpecified() throws CommandLineParseException {
        String testPassword = "testPassword";
        tlsToolkitStandaloneCommandLine.parse("-K", testPassword, "-n", DEFAULT_HOSTNAME);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertNotEquals(instanceDefinitions.get(0).getKeyStorePassword(), instanceDefinitions.get(0).getKeyPassword());
        Assert.assertEquals(1, instanceDefinitions.size());
        Assert.assertEquals(testPassword, instanceDefinitions.get(0).getKeyPassword());
    }

    @Test
    public void testKeyStorePasswordArg() throws CommandLineParseException {
        String testPassword = "testPassword";
        tlsToolkitStandaloneCommandLine.parse("-S", testPassword, "-n", DEFAULT_HOSTNAME);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(1, instanceDefinitions.size());
        Assert.assertEquals(testPassword, instanceDefinitions.get(0).getKeyStorePassword());
    }

    @Test
    public void testMultipleKeystorePasswordArgs() throws CommandLineParseException {
        String testPassword1 = "testPassword1";
        String testPassword2 = "testPassword2";
        tlsToolkitStandaloneCommandLine.parse("-n", "nifi1,nifi2", "-S", testPassword1, "-S", testPassword2);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(2, instanceDefinitions.size());
        Assert.assertEquals(testPassword1, instanceDefinitions.get(0).getKeyStorePassword());
        Assert.assertEquals(testPassword2, instanceDefinitions.get(1).getKeyStorePassword());
    }

    @Test
    public void testKeyPasswordArg() throws CommandLineParseException {
        String testPassword = "testPassword";
        tlsToolkitStandaloneCommandLine.parse("-K", testPassword, "-n", DEFAULT_HOSTNAME);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(1, instanceDefinitions.size());
        Assert.assertEquals(testPassword, instanceDefinitions.get(0).getKeyPassword());
    }

    @Test
    public void testMultipleKeyPasswordArgs() throws CommandLineParseException {
        String testPassword1 = "testPassword1";
        String testPassword2 = "testPassword2";
        tlsToolkitStandaloneCommandLine.parse("-n", "nifi1,nifi2", "-K", testPassword1, "-K", testPassword2);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(2, instanceDefinitions.size());
        Assert.assertEquals(testPassword1, instanceDefinitions.get(0).getKeyPassword());
        Assert.assertEquals(testPassword2, instanceDefinitions.get(1).getKeyPassword());
    }

    @Test
    public void testTruststorePasswordArg() throws CommandLineParseException {
        String testPassword = "testPassword";
        tlsToolkitStandaloneCommandLine.parse("-P", testPassword, "-n", DEFAULT_HOSTNAME);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(1, instanceDefinitions.size());
        Assert.assertEquals(testPassword, instanceDefinitions.get(0).getTrustStorePassword());
    }

    @Test
    public void testMultipleTruststorePasswordArgs() throws CommandLineParseException {
        String testPassword1 = "testPassword1";
        String testPassword2 = "testPassword2";
        tlsToolkitStandaloneCommandLine.parse("-n", "nifi1,nifi2", "-P", testPassword1, "-P", testPassword2);
        List<InstanceDefinition> instanceDefinitions = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions();
        Assert.assertEquals(2, instanceDefinitions.size());
        Assert.assertEquals(testPassword1, instanceDefinitions.get(0).getTrustStorePassword());
        Assert.assertEquals(testPassword2, instanceDefinitions.get(1).getTrustStorePassword());
    }

    @Test
    public void testNifiDnPrefix() throws CommandLineParseException {
        String testPrefix = "O=apache, CN=";
        tlsToolkitStandaloneCommandLine.parse("-n", "nifi", "--nifiDnPrefix", testPrefix);
        StandaloneConfig config = tlsToolkitStandaloneCommandLine.createConfig();
        Assert.assertEquals(testPrefix, config.getDnPrefix());
    }

    @Test
    public void testNifiDnSuffix() throws CommandLineParseException {
        String testSuffix = ", O=apache, OU=nifi";
        tlsToolkitStandaloneCommandLine.parse("-n", "nifi", "--nifiDnSuffix", testSuffix);
        StandaloneConfig config = tlsToolkitStandaloneCommandLine.createConfig();
        Assert.assertEquals(testSuffix, config.getDnSuffix());
    }

    @Test
    public void testClientDnDefault() throws CommandLineParseException {
        tlsToolkitStandaloneCommandLine.parse();
        Assert.assertEquals(Collections.emptyList(), tlsToolkitStandaloneCommandLine.createConfig().getClientDns());
    }

    @Test
    public void testClientDnSingle() throws CommandLineParseException {
        String testCn = "OU=NIFI,CN=testuser";
        tlsToolkitStandaloneCommandLine.parse("-C", testCn);
        List<String> clientDns = tlsToolkitStandaloneCommandLine.createConfig().getClientDns();
        Assert.assertEquals(1, clientDns.size());
        Assert.assertEquals(testCn, clientDns.get(0));
    }

    @Test
    public void testClientDnMulti() throws CommandLineParseException {
        String testCn = "OU=NIFI,CN=testuser";
        String testCn2 = "OU=NIFI,CN=testuser2";
        tlsToolkitStandaloneCommandLine.parse("-C", testCn, "-C", testCn2);
        StandaloneConfig standaloneConfig = tlsToolkitStandaloneCommandLine.createConfig();
        List<String> clientDns = standaloneConfig.getClientDns();
        Assert.assertEquals(2, clientDns.size());
        Assert.assertEquals(testCn, clientDns.get(0));
        Assert.assertEquals(testCn2, clientDns.get(1));
        Assert.assertEquals(2, standaloneConfig.getClientPasswords().size());
    }

    @Test
    public void testClientPasswordMulti() throws CommandLineParseException {
        String testCn = "OU=NIFI,CN=testuser";
        String testCn2 = "OU=NIFI,CN=testuser2";
        String testPass1 = "testPass1";
        String testPass2 = "testPass2";
        tlsToolkitStandaloneCommandLine.parse("-C", testCn, "-C", testCn2, "-B", testPass1, "-B", testPass2);
        StandaloneConfig standaloneConfig = tlsToolkitStandaloneCommandLine.createConfig();
        List<String> clientDns = standaloneConfig.getClientDns();
        Assert.assertEquals(2, clientDns.size());
        Assert.assertEquals(testCn, clientDns.get(0));
        Assert.assertEquals(testCn2, clientDns.get(1));
        List<String> clientPasswords = standaloneConfig.getClientPasswords();
        Assert.assertEquals(2, clientPasswords.size());
        Assert.assertEquals(testPass1, clientPasswords.get(0));
        Assert.assertEquals(testPass2, clientPasswords.get(1));
    }

    @Test
    public void testNoGlobalOrder() throws CommandLineParseException {
        String hostname1 = "other0[4-6]";
        String hostname2 = "nifi3(2)";
        tlsToolkitStandaloneCommandLine.parse("-n", hostname1, "-n", hostname2);
        Map<InstanceIdentifier, InstanceDefinition> definitionMap = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions().stream().collect(Collectors.toMap(InstanceDefinition::getInstanceIdentifier, Function.identity()));
        Assert.assertEquals(5, definitionMap.size());
        InstanceDefinition nifi3_1 = definitionMap.get(new InstanceIdentifier("nifi3", 1));
        Assert.assertNotNull(nifi3_1);
        Assert.assertEquals(1, nifi3_1.getInstanceIdentifier().getNumber());
        Assert.assertEquals(1, nifi3_1.getNumber());
        InstanceDefinition nifi3_2 = definitionMap.get(new InstanceIdentifier("nifi3", 2));
        Assert.assertNotNull(nifi3_2);
        Assert.assertEquals(2, nifi3_2.getInstanceIdentifier().getNumber());
        Assert.assertEquals(2, nifi3_2.getNumber());
        InstanceDefinition other04 = definitionMap.get(new InstanceIdentifier("other04", 1));
        Assert.assertNotNull(other04);
        Assert.assertEquals(1, other04.getInstanceIdentifier().getNumber());
        Assert.assertEquals(1, other04.getNumber());
        InstanceDefinition other05 = definitionMap.get(new InstanceIdentifier("other05", 1));
        Assert.assertNotNull(other05);
        Assert.assertEquals(1, other05.getInstanceIdentifier().getNumber());
        Assert.assertEquals(1, other05.getNumber());
        InstanceDefinition other06 = definitionMap.get(new InstanceIdentifier("other06", 1));
        Assert.assertNotNull(other06);
        Assert.assertEquals(1, other06.getInstanceIdentifier().getNumber());
        Assert.assertEquals(1, other06.getNumber());
    }

    @Test
    public void testGlobalOrder() throws CommandLineParseException {
        String hostname1 = "other0[4-6]";
        String hostname2 = "nifi3(2)";
        String globalOrder1 = "nifi[1-5](2),other[01-4]";
        String globalOrder2 = "other[05-10]";
        tlsToolkitStandaloneCommandLine.parse("-n", hostname1, "-n", hostname2, "-G", globalOrder1, "-G", globalOrder2);
        Map<InstanceIdentifier, InstanceDefinition> definitionMap = tlsToolkitStandaloneCommandLine.createConfig().getInstanceDefinitions().stream().collect(Collectors.toMap(InstanceDefinition::getInstanceIdentifier, Function.identity()));
        Assert.assertEquals(5, definitionMap.size());
        InstanceDefinition nifi3_1 = definitionMap.get(new InstanceIdentifier("nifi3", 1));
        Assert.assertNotNull(nifi3_1);
        Assert.assertEquals(1, nifi3_1.getInstanceIdentifier().getNumber());
        Assert.assertEquals(5, nifi3_1.getNumber());
        InstanceDefinition nifi3_2 = definitionMap.get(new InstanceIdentifier("nifi3", 2));
        Assert.assertNotNull(nifi3_2);
        Assert.assertEquals(2, nifi3_2.getInstanceIdentifier().getNumber());
        Assert.assertEquals(6, nifi3_2.getNumber());
        InstanceDefinition other04 = definitionMap.get(new InstanceIdentifier("other04", 1));
        Assert.assertNotNull(other04);
        Assert.assertEquals(1, other04.getInstanceIdentifier().getNumber());
        Assert.assertEquals(14, other04.getNumber());
        InstanceDefinition other05 = definitionMap.get(new InstanceIdentifier("other05", 1));
        Assert.assertNotNull(other05);
        Assert.assertEquals(1, other05.getInstanceIdentifier().getNumber());
        Assert.assertEquals(15, other05.getNumber());
        InstanceDefinition other06 = definitionMap.get(new InstanceIdentifier("other06", 1));
        Assert.assertNotNull(other06);
        Assert.assertEquals(1, other06.getInstanceIdentifier().getNumber());
        Assert.assertEquals(16, other06.getNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadGlobalOrder() throws CommandLineParseException {
        tlsToolkitStandaloneCommandLine.parse("-n", "notInGlobalOrder", "-G", "nifi[1-3]");
    }

    @Test
    public void testDefaultOutputPathRoot() {
        Path root = Paths.get(".").toAbsolutePath().getRoot().resolve(".");
        String calculateDefaultOutputDirectory = TlsToolkitStandaloneCommandLine.calculateDefaultOutputDirectory(root);
        Assert.assertEquals(root.toAbsolutePath().getRoot().toString(), calculateDefaultOutputDirectory);
    }

    @Test
    public void testDefaultOutputPath() {
        Path path = Paths.get(".");
        String calculateDefaultOutputDirectory = TlsToolkitStandaloneCommandLine.calculateDefaultOutputDirectory(path);
        Assert.assertEquals(("../" + (path.toAbsolutePath().normalize().getFileName().toString())), calculateDefaultOutputDirectory);
    }
}

