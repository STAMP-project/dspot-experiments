/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.server.config;


import SystemEnvironment.GO_SSL_EXCLUDE_CIPHERS;
import SystemEnvironment.GO_SSL_EXCLUDE_PROTOCOLS;
import SystemEnvironment.GO_SSL_INCLUDE_CIPHERS;
import SystemEnvironment.GO_SSL_INCLUDE_PROTOCOLS;
import SystemEnvironment.GO_SSL_RENEGOTIATION_ALLOWED;
import com.thoughtworks.go.util.SystemEnvironment;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class GoSSLConfigTest {
    private SystemEnvironment systemEnvironment;

    @Test
    public void shouldGetIncludedCiphers() {
        String[] ciphers = new String[]{ "CIPHER1", "CIPHER2" };
        Mockito.when(systemEnvironment.get(GO_SSL_INCLUDE_CIPHERS)).thenReturn(ciphers);
        GoSSLConfig config = new GoSSLConfig(systemEnvironment);
        Assert.assertThat(config.getCipherSuitesToBeIncluded(), Matchers.is(ciphers));
    }

    @Test
    public void shouldGetExcludedCiphers() {
        String[] ciphers = new String[]{ "CIPHER1", "CIPHER2" };
        Mockito.when(systemEnvironment.get(GO_SSL_EXCLUDE_CIPHERS)).thenReturn(ciphers);
        GoSSLConfig config = new GoSSLConfig(systemEnvironment);
        Assert.assertThat(config.getCipherSuitesToBeExcluded(), Matchers.is(ciphers));
    }

    @Test
    public void shouldGetIncludedProtocols() {
        String[] protocols = new String[]{ "PROTO1", "PROTO2" };
        Mockito.when(systemEnvironment.get(GO_SSL_INCLUDE_PROTOCOLS)).thenReturn(protocols);
        GoSSLConfig config = new GoSSLConfig(systemEnvironment);
        Assert.assertThat(config.getProtocolsToBeIncluded(), Matchers.is(protocols));
    }

    @Test
    public void shouldGetExcludedProtocols() {
        String[] protocols = new String[]{ "PROTO1", "PROTO2" };
        Mockito.when(systemEnvironment.get(GO_SSL_EXCLUDE_PROTOCOLS)).thenReturn(protocols);
        GoSSLConfig config = new GoSSLConfig(systemEnvironment);
        Assert.assertThat(config.getProtocolsToBeExcluded(), Matchers.is(protocols));
    }

    @Test
    public void shouldGetRenegotiationAllowedFlag() {
        Mockito.when(systemEnvironment.get(GO_SSL_RENEGOTIATION_ALLOWED)).thenReturn(true);
        GoSSLConfig config = new GoSSLConfig(systemEnvironment);
        Assert.assertThat(config.isRenegotiationAllowed(), Matchers.is(true));
    }
}

