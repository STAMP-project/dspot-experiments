/**
 * Copyright (c) 2018,2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.encryption;


import io.helidon.config.Config;
import io.helidon.config.ConfigValue;
import io.helidon.config.ConfigValues;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Test encryption and configuration.
 */
abstract class AbstractSecureConfigTest {
    static final String TEST_STRING = "?berstring";

    @Test
    public void testWrongSymmetric() {
        testPassword(getConfig(), "pwd9", "${AES=not really encrypted}");
        testPassword(getConfig(), "pwd10", "${RSA=not really encrypted}");
    }

    @Test
    public void testAsymmetric() {
        testPassword(getConfig(), "pwd3", AbstractSecureConfigTest.TEST_STRING);
        testPassword(getConfig(), "pwd5", "");
        testPassword(getConfigRequiresEncryption(), "pwd3", AbstractSecureConfigTest.TEST_STRING);
        testPassword(getConfigRequiresEncryption(), "pwd5", "");
    }

    @Test
    public void testPasswordArray() {
        ConfigValue<List<String>> passwordsOpt = getConfig().get("passwords").asList(String.class);
        MatcherAssert.assertThat("Passwords must be present", passwordsOpt.isPresent());
        List<String> passwords = passwordsOpt.get();
        MatcherAssert.assertThat(passwords, Matchers.hasSize(3));
        MatcherAssert.assertThat(passwords, Matchers.contains(AbstractSecureConfigTest.TEST_STRING, AbstractSecureConfigTest.TEST_STRING, ""));
    }

    @Test
    public void testConfigList() {
        ConfigValue<List<Config>> objects = getConfig().get("objects").asNodeList();
        MatcherAssert.assertThat("Objects should be present in config", objects.isPresent());
        List<? extends Config> configSources = objects.get();
        MatcherAssert.assertThat("there should be two objects", configSources.size(), CoreMatchers.is(2));
        Config config = configSources.get(0);
        MatcherAssert.assertThat(config.get("pwd").asString(), CoreMatchers.is(ConfigValues.simpleValue(AbstractSecureConfigTest.TEST_STRING)));
        config = configSources.get(1);
        MatcherAssert.assertThat(config.get("pwd").asString(), CoreMatchers.is(ConfigValues.simpleValue("")));
    }

    @Test
    public void testConfigListMissing() {
        ConfigValue<List<Config>> objects = getConfig().get("notThereAtAll").asList(Config.class);
        MatcherAssert.assertThat(objects, CoreMatchers.is(ConfigValues.empty()));
    }

    @Test
    public void testPasswordArrayMissing() {
        ConfigValue<List<String>> passwordsOpt = getConfig().get("notThereAtAll").asList(String.class);
        MatcherAssert.assertThat(passwordsOpt, CoreMatchers.is(ConfigValues.empty()));
    }

    @Test
    public void testCustomEnc() {
        MatcherAssert.assertThat(getConfigRequiresEncryption().get("customEnc").asString(), CoreMatchers.is(ConfigValues.simpleValue("${URGH=argh}")));
    }

    @Test
    public void testMissing() {
        MatcherAssert.assertThat(getConfigRequiresEncryption().get("thisDoesNotExist").asString(), CoreMatchers.is(ConfigValues.empty()));
    }
}

