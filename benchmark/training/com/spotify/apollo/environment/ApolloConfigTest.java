/**
 * -\-\-
 * Spotify Apollo API Environment
 * --
 * Copyright (C) 2013 - 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.environment;


import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ApolloConfigTest {
    @Test
    public void testBackendApolloBackend() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of("apollo.backend", "foo"));
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.backend(), Matchers.is("foo"));
    }

    @Test
    public void testBackendApolloDomain() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of("apollo.domain", "foo"));
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.backend(), Matchers.is("foo"));
    }

    @Test
    public void testBackendDomain() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of("domain", "foo"));
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.backend(), Matchers.is("foo"));
    }

    @Test
    public void testBackendDefault() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of());
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.backend(), Matchers.is(""));
    }

    @Test
    public void testEnableIncomingRequestLogging() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of("apollo.logIncomingRequests", false));
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.enableIncomingRequestLogging(), Matchers.is(false));
    }

    @Test
    public void testEnableIncomingRequestLoggingDefault() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of());
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.enableIncomingRequestLogging(), Matchers.is(true));
    }

    @Test
    public void testEnableOutgoingRequestLogging() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of("apollo.logOutgoingRequests", false));
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.enableOutgoingRequestLogging(), Matchers.is(false));
    }

    @Test
    public void testEnableOutgoingRequestLoggingDefault() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of());
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.enableOutgoingRequestLogging(), Matchers.is(true));
    }

    @Test
    public void testEnableMetaApi() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of("apollo.metaApi", true));
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.enableMetaApi(), Matchers.is(true));
    }

    @Test
    public void testEnableMetaApiDefault() throws Exception {
        final Config config = ConfigFactory.parseMap(ImmutableMap.of());
        final ApolloConfig sut = new ApolloConfig(config);
        Assert.assertThat(sut.enableMetaApi(), Matchers.is(true));
    }
}

