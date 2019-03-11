/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.security;


import JaasContext.Type.SERVER;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.apache.kafka.common.network.ListenerName;
import org.junit.Assert;
import org.junit.Test;

import static javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
import static javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
import static javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
import static javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;


/**
 * Tests parsing of {@link SaslConfigs#SASL_JAAS_CONFIG} property and verifies that the format
 * and parsing are consistent with JAAS configuration files loaded by the JRE.
 */
public class JaasContextTest {
    private File jaasConfigFile;

    @Test
    public void testConfigNoOptions() throws Exception {
        checkConfiguration("test.testConfigNoOptions", REQUIRED, new HashMap<String, Object>());
    }

    @Test
    public void testControlFlag() throws Exception {
        AppConfigurationEntry.LoginModuleControlFlag[] controlFlags = new AppConfigurationEntry.LoginModuleControlFlag[]{ REQUIRED, REQUISITE, SUFFICIENT, OPTIONAL };
        Map<String, Object> options = new HashMap<>();
        options.put("propName", "propValue");
        for (AppConfigurationEntry.LoginModuleControlFlag controlFlag : controlFlags) {
            checkConfiguration("test.testControlFlag", controlFlag, options);
        }
    }

    @Test
    public void testSingleOption() throws Exception {
        Map<String, Object> options = new HashMap<>();
        options.put("propName", "propValue");
        checkConfiguration("test.testSingleOption", REQUISITE, options);
    }

    @Test
    public void testMultipleOptions() throws Exception {
        Map<String, Object> options = new HashMap<>();
        for (int i = 0; i < 10; i++)
            options.put(("propName" + i), ("propValue" + i));

        checkConfiguration("test.testMultipleOptions", SUFFICIENT, options);
    }

    @Test
    public void testQuotedOptionValue() throws Exception {
        Map<String, Object> options = new HashMap<>();
        options.put("propName", "prop value");
        options.put("propName2", "value1 = 1, value2 = 2");
        String config = String.format("test.testQuotedOptionValue required propName=\"%s\" propName2=\"%s\";", options.get("propName"), options.get("propName2"));
        checkConfiguration(config, "test.testQuotedOptionValue", REQUIRED, options);
    }

    @Test
    public void testQuotedOptionName() throws Exception {
        Map<String, Object> options = new HashMap<>();
        options.put("prop name", "propValue");
        String config = "test.testQuotedOptionName required \"prop name\"=propValue;";
        checkConfiguration(config, "test.testQuotedOptionName", REQUIRED, options);
    }

    @Test
    public void testMultipleLoginModules() throws Exception {
        StringBuilder builder = new StringBuilder();
        int moduleCount = 3;
        Map<Integer, Map<String, Object>> moduleOptions = new HashMap<>();
        for (int i = 0; i < moduleCount; i++) {
            Map<String, Object> options = new HashMap<>();
            options.put("index", ("Index" + i));
            options.put("module", ("Module" + i));
            moduleOptions.put(i, options);
            String module = jaasConfigProp(("test.Module" + i), REQUIRED, options);
            builder.append(' ');
            builder.append(module);
        }
        String jaasConfigProp = builder.toString();
        String clientContextName = "CLIENT";
        Configuration configuration = new JaasConfig(clientContextName, jaasConfigProp);
        AppConfigurationEntry[] dynamicEntries = configuration.getAppConfigurationEntry(clientContextName);
        Assert.assertEquals(moduleCount, dynamicEntries.length);
        for (int i = 0; i < moduleCount; i++) {
            AppConfigurationEntry entry = dynamicEntries[i];
            checkEntry(entry, ("test.Module" + i), REQUIRED, moduleOptions.get(i));
        }
        String serverContextName = "SERVER";
        writeConfiguration(serverContextName, jaasConfigProp);
        AppConfigurationEntry[] staticEntries = Configuration.getConfiguration().getAppConfigurationEntry(serverContextName);
        for (int i = 0; i < moduleCount; i++) {
            AppConfigurationEntry staticEntry = staticEntries[i];
            checkEntry(staticEntry, dynamicEntries[i].getLoginModuleName(), REQUIRED, dynamicEntries[i].getOptions());
        }
    }

    @Test
    public void testMissingLoginModule() throws Exception {
        checkInvalidConfiguration("  required option1=value1;");
    }

    @Test
    public void testMissingControlFlag() throws Exception {
        checkInvalidConfiguration("test.loginModule option1=value1;");
    }

    @Test
    public void testMissingOptionValue() throws Exception {
        checkInvalidConfiguration("loginModule required option1;");
    }

    @Test
    public void testMissingSemicolon() throws Exception {
        checkInvalidConfiguration("test.testMissingSemicolon required option1=value1");
    }

    @Test
    public void testNumericOptionWithoutQuotes() throws Exception {
        checkInvalidConfiguration("test.testNumericOptionWithoutQuotes required option1=3;");
    }

    @Test
    public void testInvalidControlFlag() throws Exception {
        checkInvalidConfiguration("test.testInvalidControlFlag { option1=3;");
    }

    @Test
    public void testNumericOptionWithQuotes() throws Exception {
        Map<String, Object> options = new HashMap<>();
        options.put("option1", "3");
        String config = "test.testNumericOptionWithQuotes required option1=\"3\";";
        checkConfiguration(config, "test.testNumericOptionWithQuotes", REQUIRED, options);
    }

    @Test
    public void testLoadForServerWithListenerNameOverride() throws IOException {
        writeConfiguration(Arrays.asList("KafkaServer { test.LoginModuleDefault required; };", "plaintext.KafkaServer { test.LoginModuleOverride requisite; };"));
        JaasContext context = JaasContext.loadServerContext(new ListenerName("plaintext"), "SOME-MECHANISM", Collections.emptyMap());
        Assert.assertEquals("plaintext.KafkaServer", context.name());
        Assert.assertEquals(SERVER, context.type());
        Assert.assertEquals(1, context.configurationEntries().size());
        checkEntry(context.configurationEntries().get(0), "test.LoginModuleOverride", REQUISITE, Collections.emptyMap());
    }

    @Test
    public void testLoadForServerWithListenerNameAndFallback() throws IOException {
        writeConfiguration(Arrays.asList("KafkaServer { test.LoginModule required; };", "other.KafkaServer { test.LoginModuleOther requisite; };"));
        JaasContext context = JaasContext.loadServerContext(new ListenerName("plaintext"), "SOME-MECHANISM", Collections.emptyMap());
        Assert.assertEquals("KafkaServer", context.name());
        Assert.assertEquals(SERVER, context.type());
        Assert.assertEquals(1, context.configurationEntries().size());
        checkEntry(context.configurationEntries().get(0), "test.LoginModule", REQUIRED, Collections.emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadForServerWithWrongListenerName() throws IOException {
        writeConfiguration("Server", "test.LoginModule required;");
        JaasContext.loadServerContext(new ListenerName("plaintext"), "SOME-MECHANISM", Collections.emptyMap());
    }
}

