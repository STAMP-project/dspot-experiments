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
import io.helidon.config.ConfigSources;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Test for {@link io.helidon.security.providers.common.OutboundTarget}.
 */
public class OutboundTargetTest {
    @Test
    public void testAnyMatchNulls() {
        OutboundTarget instance = OutboundTarget.builder("name").build();
        MatcherAssert.assertThat(instance.matches("http", "localhost", null), CoreMatchers.is(true));
    }

    @Test
    public void testAnyMatchHosts() {
        OutboundTarget instance = OutboundTarget.builder("name").addTransport("https").build();
        MatcherAssert.assertThat(instance.matches("http", "localhost", null), CoreMatchers.is(false));
        MatcherAssert.assertThat(instance.matches("https", "localhost", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("https", "192.168.1.1", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("https", "www.google.com", null), CoreMatchers.is(true));
    }

    @Test
    public void testAnyMatchProtocol() {
        OutboundTarget instance = OutboundTarget.builder("name").addHost("localhost").build();
        MatcherAssert.assertThat(instance.matches("https", "192.168.1.1", null), CoreMatchers.is(false));
        MatcherAssert.assertThat(instance.matches("http", "localhost", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("https", "localhost", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("jms", "localhost", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("t3", "localhost", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("iiop", "localhost", null), CoreMatchers.is(true));
    }

    @Test
    public void testExactValues() {
        OutboundTarget instance = OutboundTarget.builder("name").addTransport("http").addTransport("https").addHost("localhost").addHost("192.168.1.14").addHost("10.17.17.1").build();
        MatcherAssert.assertThat(instance.matches("http", "localhost", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("http", "192.168.1.14", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("http", "10.17.17.1", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("https", "localhost", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("https", "192.168.1.14", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("https", "10.17.17.1", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("http", "192.168.1.13", null), CoreMatchers.is(false));
        MatcherAssert.assertThat(instance.matches("iiop", "localhost", null), CoreMatchers.is(false));
    }

    @Test
    public void testConfig() {
        Config config = Config.builder().sources(ConfigSources.classpath("targets_with_default.conf")).build();
        config = config.get("security-provider.outbound").asList(Config.class).get().get(0);
        OutboundTarget instance = OutboundTarget.builder("name").addTransport("http").addTransport("https").addHost("localhost").addHost("192.168.1.14").addHost("10.17.17.1").config(config).build();
        MatcherAssert.assertThat(instance.getConfig().isPresent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.getConfig().get(), CoreMatchers.sameInstance(config));
    }

    @Test
    public void testMatchingGlob() {
        OutboundTarget instance = OutboundTarget.builder("name").addTransport("http").addTransport("https").addHost("192.*.1.14").addHost("*.google.com").build();
        MatcherAssert.assertThat(instance.matches("http", "192.168.1.14", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("http", "192.12.1.14", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("http", "192.168.1.15", null), CoreMatchers.is(false));
        MatcherAssert.assertThat(instance.matches("http", "calendar.google.com", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("http", "my.calendar.google.com", null), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.matches("http", "calendar.google.org", null), CoreMatchers.is(false));
    }
}

