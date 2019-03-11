/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.commands;


import Result.Status.OK;
import java.io.File;
import java.util.Properties;
import org.apache.geode.distributed.ConfigurationProperties;
import org.apache.geode.management.internal.cli.i18n.CliStrings;
import org.apache.geode.management.internal.cli.result.CommandResult;
import org.apache.geode.management.internal.cli.result.model.ResultModel;
import org.apache.geode.management.internal.cli.shell.Gfsh;
import org.apache.geode.management.internal.cli.shell.OperationInvoker;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConnectCommandTest {
    @ClassRule
    public static GfshParserRule gfshParserRule = new GfshParserRule();

    private ConnectCommand connectCommand;

    private Gfsh gfsh;

    private CommandResult result;

    private ResultModel resultModel;

    private OperationInvoker operationInvoker;

    private Properties properties;

    private ArgumentCaptor<File> fileCaptor;

    @Test
    public void whenGfshIsAlreadyConnected() throws Exception {
        Mockito.when(gfsh.isConnectedAndReady()).thenReturn(true);
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect");
        assertThat(result.nextLine()).contains("Already connected to");
    }

    @Test
    public void promptForPasswordIfUsernameIsGiven() throws Exception {
        Mockito.doReturn(properties).when(connectCommand).resolveSslProperties(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.any());
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect --user=user");
        Mockito.verify(gfsh).readPassword(((CliStrings.CONNECT__PASSWORD) + ": "));
        assertThat(properties.getProperty("security-username")).isEqualTo("user");
        assertThat(properties.getProperty("security-password")).isEqualTo("");
    }

    @Test
    public void notPromptForPasswordIfUsernameIsGiven() throws Exception {
        Mockito.doReturn(properties).when(connectCommand).resolveSslProperties(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.any());
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect --user=user --password=pass");
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(((CliStrings.CONNECT__PASSWORD) + ": "));
        assertThat(properties.getProperty("security-username")).isEqualTo("user");
        assertThat(properties.getProperty("security-password")).isEqualTo("pass");
    }

    @Test
    public void notPromptForPasswordIfuserNameIsGivenInFile() throws Exception {
        // username specified in property file won't prompt for password
        properties.setProperty("security-username", "user");
        Mockito.doReturn(properties).when(connectCommand).loadProperties(ArgumentMatchers.any(File.class));
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect");
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(((CliStrings.CONNECT__PASSWORD) + ": "));
        assertThat(properties).doesNotContainKey("security-password");
    }

    @Test
    public void plainConnectNotLoadFileNotPrompt() throws Exception {
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect");
        // will not try to load from any file
        Mockito.verify(connectCommand).loadProperties(null, null);
        // will not try to prompt
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
    }

    @Test
    public void connectUseSsl() throws Exception {
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect --use-ssl");
        // will not try to load from any file
        Mockito.verify(connectCommand).loadProperties(null, null);
        // gfsh will prompt for the all the ssl properties
        Mockito.verify(gfsh).readText("key-store: ");
        Mockito.verify(gfsh).readPassword("key-store-password: ");
        Mockito.verify(gfsh).readText("key-store-type(default: JKS): ");
        Mockito.verify(gfsh).readText("trust-store: ");
        Mockito.verify(gfsh).readPassword("trust-store-password: ");
        Mockito.verify(gfsh).readText("trust-store-type(default: JKS): ");
        Mockito.verify(gfsh).readText("ssl-ciphers(default: any): ");
        Mockito.verify(gfsh).readText("ssl-protocols(default: any): ");
        // verify the resulting properties has correct values
        assertThat(properties).hasSize(9);
        assertThat(properties.getProperty("ssl-keystore")).isEqualTo("");
        assertThat(properties.getProperty("ssl-keystore-password")).isEqualTo("");
        assertThat(properties.getProperty("ssl-keystore-type")).isEqualTo("JKS");
        assertThat(properties.getProperty("ssl-truststore")).isEqualTo("");
        assertThat(properties.getProperty("ssl-truststore-password")).isEqualTo("");
        assertThat(properties.getProperty("ssl-truststore-type")).isEqualTo("JKS");
        assertThat(properties.getProperty("ssl-ciphers")).isEqualTo("any");
        assertThat(properties.getProperty("ssl-protocols")).isEqualTo("any");
        assertThat(properties.getProperty("ssl-enabled-components")).isEqualTo("all");
    }

    @Test
    public void securityFileContainsSSLPropsAndNoUseSSL() throws Exception {
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "keystore");
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect --security-properties-file=test");
        // will try to load from this file
        Mockito.verify(connectCommand).loadProperties(ArgumentMatchers.any(), fileCaptor.capture());
        assertThat(fileCaptor.getValue()).hasName("test");
        // it will prompt for missing properties
        Mockito.verify(gfsh, Mockito.times(6)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(2)).readPassword(ArgumentMatchers.any());
    }

    @Test
    public void securityFileContainsNoSSLPropsAndNoUseSSL() throws Exception {
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect --security-properties-file=test");
        // will try to load from this file
        Mockito.verify(connectCommand).loadProperties(ArgumentMatchers.any(), fileCaptor.capture());
        assertThat(fileCaptor.getValue()).hasName("test");
        // it will prompt for missing properties
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
    }

    @Test
    public void connectUseLegacySecurityPropertiesFile() throws Exception {
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_KEYSTORE, "jmx-keystore");
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect --security-properties-file=test --key-store=keystore --key-store-password=password");
        // wil try to load from this file
        Mockito.verify(connectCommand).loadProperties(fileCaptor.capture());
        assertThat(fileCaptor.getValue()).hasName("test");
        // it will not prompt for missing properties
        Mockito.verify(gfsh, Mockito.times(0)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(0)).readPassword(ArgumentMatchers.any());
        // the command option will be ignored
        assertThat(properties).hasSize(1);
        assertThat(properties.get(ConfigurationProperties.JMX_MANAGER_SSL_KEYSTORE)).isEqualTo("jmx-keystore");
    }

    @Test
    public void connectUseSecurityPropertiesFile_promptForMissing() throws Exception {
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "keystore");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_PASSWORD, "password");
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect --security-properties-file=test");
        // since nothing is loaded, will prompt for all missing values
        Mockito.verify(gfsh, Mockito.times(6)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(1)).readPassword(ArgumentMatchers.any());
    }

    @Test
    public void connectUseSecurityPropertiesFileAndOption_promptForMissing() throws Exception {
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "keystore");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_PASSWORD, "password");
        result = ConnectCommandTest.gfshParserRule.executeCommandWithInstance(connectCommand, "connect --security-properties-file=test --key-store=keystore2 --trust-store=truststore2");
        // since nothing is loaded, will prompt for all missing values
        Mockito.verify(gfsh, Mockito.times(5)).readText(ArgumentMatchers.any());
        Mockito.verify(gfsh, Mockito.times(1)).readPassword(ArgumentMatchers.any());
        assertThat(properties).hasSize(9);
        assertThat(properties.getProperty("ssl-keystore")).isEqualTo("keystore2");
        assertThat(properties.getProperty("ssl-keystore-password")).isEqualTo("password");
        assertThat(properties.getProperty("ssl-keystore-type")).isEqualTo("JKS");
        assertThat(properties.getProperty("ssl-truststore")).isEqualTo("truststore2");
        assertThat(properties.getProperty("ssl-truststore-password")).isEqualTo("");
        assertThat(properties.getProperty("ssl-truststore-type")).isEqualTo("JKS");
        assertThat(properties.getProperty("ssl-ciphers")).isEqualTo("any");
        assertThat(properties.getProperty("ssl-protocols")).isEqualTo("any");
        assertThat(properties.getProperty("ssl-enabled-components")).isEqualTo("all");
    }

    @Test
    public void containsLegacySSLConfigTest_ssl() throws Exception {
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "keystore");
        assertThat(ConnectCommand.containsLegacySSLConfig(properties)).isFalse();
    }

    @Test
    public void containsLegacySSLConfigTest_cluster() throws Exception {
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_KEYSTORE, "cluster-keystore");
        assertThat(ConnectCommand.containsLegacySSLConfig(properties)).isTrue();
    }

    @Test
    public void containsLegacySSLConfigTest_jmx() throws Exception {
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_KEYSTORE, "jmx-keystore");
        assertThat(ConnectCommand.containsLegacySSLConfig(properties)).isTrue();
    }

    @Test
    public void containsLegacySSLConfigTest_http() throws Exception {
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_KEYSTORE, "http-keystore");
        assertThat(ConnectCommand.containsLegacySSLConfig(properties)).isTrue();
    }

    @Test
    public void loadPropertiesWithNull() throws Exception {
        Mockito.doCallRealMethod().when(connectCommand).loadProperties(ArgumentMatchers.any());
        assertThat(connectCommand.loadProperties(null, null)).isEmpty();
    }

    @Test
    public void isSslImpliedByOptions() throws Exception {
        assertThat(connectCommand.isSslImpliedBySslOptions(((String) (null)))).isFalse();
        assertThat(connectCommand.isSslImpliedBySslOptions(((String[]) (null)))).isFalse();
        assertThat(connectCommand.isSslImpliedBySslOptions(null, null, null)).isFalse();
        assertThat(connectCommand.isSslImpliedBySslOptions(null, "test")).isTrue();
    }

    @Test
    public void resolveSslProperties() throws Exception {
        // assume properties loaded from either file has an ssl property
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "keystore");
        properties = connectCommand.resolveSslProperties(gfsh, false, null, null);
        assertThat(properties).hasSize(9);
        properties.clear();
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "keystore");
        properties = connectCommand.resolveSslProperties(gfsh, false, null, null, "keystore2", "password");
        assertThat(properties).hasSize(9);
        assertThat(properties.getProperty(ConfigurationProperties.SSL_KEYSTORE)).isEqualTo("keystore2");
        assertThat(properties.getProperty(ConfigurationProperties.SSL_KEYSTORE_PASSWORD)).isEqualTo("password");
    }

    @Test
    public void connectToManagerWithDifferentMajorVersion() {
        Mockito.when(gfsh.getVersion()).thenReturn("2.2");
        Mockito.when(operationInvoker.getRemoteVersion()).thenReturn("1.2");
        Mockito.when(operationInvoker.isConnected()).thenReturn(true);
        ConnectCommandTest.gfshParserRule.executeAndAssertThat(connectCommand, "connect --locator=localhost:4040").statusIsError().containsOutput("Cannot use a 2.2 gfsh client to connect to a 1.2 cluster.");
    }

    @Test
    public void connectToManagerWithDifferentMinorVersion() {
        Mockito.when(gfsh.getVersion()).thenReturn("1.2");
        Mockito.when(operationInvoker.getRemoteVersion()).thenReturn("1.3");
        Mockito.when(operationInvoker.isConnected()).thenReturn(true);
        ConnectCommandTest.gfshParserRule.executeAndAssertThat(connectCommand, "connect --locator=localhost:4040").statusIsError().containsOutput("Cannot use a 1.2 gfsh client to connect to a 1.3 cluster.");
    }

    @Test
    public void connectToManagerWithGreaterPatchVersion() {
        Mockito.when(gfsh.getVersion()).thenReturn("1.5.1");
        Mockito.when(operationInvoker.getRemoteVersion()).thenReturn("1.5.2");
        Mockito.when(operationInvoker.isConnected()).thenReturn(true);
        Mockito.when(resultModel.getStatus()).thenReturn(OK);
        ConnectCommandTest.gfshParserRule.executeAndAssertThat(connectCommand, "connect --locator=localhost:4040").statusIsSuccess();
    }

    @Test
    public void connectToManagerWithNoPatchVersion() {
        Mockito.when(gfsh.getVersion()).thenReturn("1.5.1");
        Mockito.when(operationInvoker.getRemoteVersion()).thenReturn("1.5");
        Mockito.when(operationInvoker.isConnected()).thenReturn(true);
        Mockito.when(resultModel.getStatus()).thenReturn(OK);
        ConnectCommandTest.gfshParserRule.executeAndAssertThat(connectCommand, "connect --locator=localhost:4040").statusIsSuccess();
    }

    @Test
    public void connectToManagerWithLessorPatchVersion() {
        Mockito.when(gfsh.getVersion()).thenReturn("1.5.1");
        Mockito.when(operationInvoker.getRemoteVersion()).thenReturn("1.5.0");
        Mockito.when(operationInvoker.isConnected()).thenReturn(true);
        Mockito.when(resultModel.getStatus()).thenReturn(OK);
        ConnectCommandTest.gfshParserRule.executeAndAssertThat(connectCommand, "connect --locator=localhost:4040").statusIsSuccess();
    }

    @Test
    public void connectToOlderManagerWithNewerGfsh() {
        Mockito.when(gfsh.getVersion()).thenReturn("1.5");
        Mockito.when(operationInvoker.getRemoteVersion()).thenThrow(new RuntimeException("release version not available"));
        Mockito.when(operationInvoker.isConnected()).thenReturn(true);
        ConnectCommandTest.gfshParserRule.executeAndAssertThat(connectCommand, "connect --locator=localhost:4040").statusIsError().containsOutput("Cannot use a 1.5 gfsh client to connect to this cluster.");
    }

    @Test
    public void connectToAValidManager() {
        Mockito.when(gfsh.getVersion()).thenReturn("1.5");
        Mockito.when(operationInvoker.getRemoteVersion()).thenReturn("1.5");
        Mockito.when(operationInvoker.isConnected()).thenReturn(true);
        Mockito.when(resultModel.getStatus()).thenReturn(OK);
        ConnectCommandTest.gfshParserRule.executeAndAssertThat(connectCommand, "connect --locator=localhost:4040").statusIsSuccess();
    }
}

