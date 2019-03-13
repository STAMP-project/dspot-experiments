/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security.providers.common;


import io.helidon.config.Config;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Test for {@link io.helidon.security.providers.common.OutboundConfig}.
 */
public class OutboundConfigTest {
    private Config configWithDefaults;

    private Config configNoDefaults;

    @Test
    public void testParsing() {
        OutboundConfig targets = OutboundConfig.create(configWithDefaults, new OutboundTarget[0]);
        List<OutboundTarget> targetList = targets.targets();
        MatcherAssert.assertThat(targetList, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(targetList.size(), CoreMatchers.is(2));
        OutboundTarget target = targetList.get(0);
        MatcherAssert.assertThat(target.name(), CoreMatchers.is("default"));
        MatcherAssert.assertThat(target.hosts(), CoreMatchers.hasItems("a.b.com"));
        MatcherAssert.assertThat(target.transports(), CoreMatchers.hasItems("https"));
        Optional<Config> targetsConfigOpt = target.getConfig();
        Config targetsConfig = targetsConfigOpt.orElseGet(() -> {
            fail("Config was expected to be non-null");
            return null;
        });
        MatcherAssert.assertThat(targetsConfig.get("type").asString(), CoreMatchers.is(simpleValue("S2S")));
        target = targetList.get(1);
        MatcherAssert.assertThat(target.name(), CoreMatchers.is("obo"));
        MatcherAssert.assertThat(target.hosts(), CoreMatchers.hasItems("b.c.com", "d.e.org"));
        MatcherAssert.assertThat(target.transports(), CoreMatchers.hasItems("https"));
        targetsConfigOpt = target.getConfig();
        targetsConfig = targetsConfigOpt.orElseGet(() -> {
            fail("Config was expected to be non-null");
            return null;
        });
        MatcherAssert.assertThat(targetsConfig.get("type").asString(), CoreMatchers.is(simpleValue("S2S_OBO")));
    }

    @Test
    public void testWithDefaults() {
        // default value must be overriden by config, so the test is the same as above...
        OutboundTarget defaultValue = OutboundTarget.builder("default").build();
        OutboundConfig targets = OutboundConfig.create(configWithDefaults, new OutboundTarget[]{ defaultValue });
        List<OutboundTarget> targetList = targets.targets();
        MatcherAssert.assertThat(targetList, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(targetList.size(), CoreMatchers.is(2));
        OutboundTarget target = targetList.get(0);
        MatcherAssert.assertThat(target.name(), CoreMatchers.is("default"));
        MatcherAssert.assertThat(target.transports(), CoreMatchers.hasItems("https"));
        MatcherAssert.assertThat(target.hosts(), CoreMatchers.hasItems("a.b.com"));
        Optional<Config> targetsConfigOpt = target.getConfig();
        Config targetsConfig = targetsConfigOpt.orElseGet(() -> {
            fail("Config was expected to be non-null");
            return null;
        });
        MatcherAssert.assertThat(targetsConfig.get("type").asString(), CoreMatchers.is(simpleValue("S2S")));
        target = targetList.get(1);
        MatcherAssert.assertThat(target.name(), CoreMatchers.is("obo"));
        MatcherAssert.assertThat(target.transports(), CoreMatchers.hasItems("https"));
        MatcherAssert.assertThat(target.hosts(), CoreMatchers.hasItems("b.c.com", "d.e.org"));
        targetsConfigOpt = target.getConfig();
        targetsConfig = targetsConfigOpt.orElseGet(() -> {
            fail("Config was expected to be non-null");
            return null;
        });
        MatcherAssert.assertThat(targetsConfig.get("type").asString(), CoreMatchers.is(simpleValue("S2S_OBO")));
    }

    @Test
    public void testWithDefaultsNoDefaultConfig() {
        // default value must be overridden by config, so the test is the same as above...
        OutboundTarget[] defaultValue = new OutboundTarget[]{ OutboundTarget.builder("default").addTransport("https").addHost("www.google.com").build(), OutboundTarget.builder("default2").addTransport("http").addHost("localhost").addHost("127.0.0.1").build() };
        OutboundConfig targets = OutboundConfig.create(configNoDefaults, defaultValue);
        List<OutboundTarget> targetList = targets.targets();
        MatcherAssert.assertThat(targetList, CoreMatchers.notNullValue());
        // 2 defaults + one from config file
        MatcherAssert.assertThat(targetList.size(), CoreMatchers.is(3));
        OutboundTarget target = targetList.get(0);
        MatcherAssert.assertThat(target.name(), CoreMatchers.is("default"));
        MatcherAssert.assertThat(target.transports(), CoreMatchers.hasItems("https"));
        MatcherAssert.assertThat(target.hosts(), CoreMatchers.hasItems("www.google.com"));
        Optional<Config> targetsConfig = target.getConfig();
        MatcherAssert.assertThat(targetsConfig.isPresent(), CoreMatchers.is(false));
        target = targetList.get(1);
        MatcherAssert.assertThat(target.name(), CoreMatchers.is("default2"));
        MatcherAssert.assertThat(target.transports(), CoreMatchers.hasItems("http"));
        MatcherAssert.assertThat(target.hosts(), CoreMatchers.hasItems("localhost", "127.0.0.1"));
        targetsConfig = target.getConfig();
        MatcherAssert.assertThat(targetsConfig.isPresent(), CoreMatchers.is(false));
        target = targetList.get(2);
        MatcherAssert.assertThat(target.name(), CoreMatchers.is("obo"));
        MatcherAssert.assertThat(target.transports(), CoreMatchers.hasItems("https"));
        MatcherAssert.assertThat(target.hosts(), CoreMatchers.hasItems("b.c.com", "d.e.org"));
        targetsConfig = target.getConfig();
        Config config = targetsConfig.orElseGet(() -> {
            fail("Config was expected to be non-null");
            return null;
        });
        MatcherAssert.assertThat(config.get("type").asString(), CoreMatchers.is(simpleValue("S2S_OBO")));
    }

    @Test
    public void testUserScenario() {
        OutboundConfig targets = OutboundConfig.create(configWithDefaults);
        Optional<OutboundTarget> optional = targets.findTarget(buildEnv("https", "a.b.com"));
        Map<String, String> expectedProps = new HashMap<>();
        expectedProps.put("type", "S2S");
        MatcherAssert.assertThat(optional.isPresent(), CoreMatchers.is(true));
        optional.ifPresent(( t) -> validateTarget(t, "default", expectedProps));
        optional = targets.findTarget(buildEnv("iiop", "192.168.1.1"));
        MatcherAssert.assertThat(optional.isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void testUserScenarioWithDefaults() {
        OutboundTarget[] defaultValue = new OutboundTarget[]{ OutboundTarget.builder("default").addTransport("https").addHost("www.google.com").build(), OutboundTarget.builder("default2").addTransport("http").addHost("localhost").addHost("127.0.0.1").build(), // intentionally the same config, to make sure we do this in order
        OutboundTarget.builder("default3").addTransport("http").addHost("localhost").addHost("127.0.0.1").build() };
        OutboundConfig targets = OutboundConfig.create(configNoDefaults, defaultValue);
        Optional<OutboundTarget> optional = targets.findTarget(buildEnv("https", "d.e.org"));
        MatcherAssert.assertThat(optional.isPresent(), CoreMatchers.is(true));
        Map<String, String> expectedProps = new HashMap<>();
        expectedProps.put("type", "S2S_OBO");
        expectedProps.put("s2s-private-key", "~/.ssh/second_id_rsa");
        expectedProps.put("s2s-certificate", "~/.ssh/second_id_rsa.crt");
        optional.ifPresent(( t) -> validateTarget(t, "obo", expectedProps));
        expectedProps.clear();
        optional = targets.findTarget(buildEnv("https", "www.google.com"));
        MatcherAssert.assertThat(optional.isPresent(), CoreMatchers.is(true));
        optional.ifPresent(( t) -> validateTarget(t, "default", expectedProps));
        optional = targets.findTarget(buildEnv("http", "localhost"));
        MatcherAssert.assertThat(optional.isPresent(), CoreMatchers.is(true));
        optional.ifPresent(( t) -> validateTarget(t, "default2", expectedProps));
    }
}

