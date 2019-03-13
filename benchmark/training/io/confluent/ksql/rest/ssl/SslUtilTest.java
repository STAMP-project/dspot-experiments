/**
 * Copyright 2019 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.ssl;


import NoopHostnameVerifier.INSTANCE;
import RestConfig.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG;
import RestConfig.SSL_KEYSTORE_LOCATION_CONFIG;
import RestConfig.SSL_KEYSTORE_PASSWORD_CONFIG;
import RestConfig.SSL_KEY_PASSWORD_CONFIG;
import RestConfig.SSL_TRUSTSTORE_LOCATION_CONFIG;
import RestConfig.SSL_TRUSTSTORE_PASSWORD_CONFIG;
import com.google.common.collect.ImmutableMap;
import io.confluent.common.config.ConfigException;
import io.confluent.ksql.util.KsqlException;
import java.security.KeyStore;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;


public class SslUtilTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldNotLoadKeyStoreByDefault() {
        // When:
        final Optional<KeyStore> result = SslUtil.loadKeyStore(Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Optional.empty()));
    }

    @Test
    public void shouldNotLoadTrustStoreByDefault() {
        // When:
        final Optional<KeyStore> result = SslUtil.loadTrustStore(Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Optional.empty()));
    }

    @Test
    public void shouldLoadKeyStore() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_KEYSTORE_LOCATION_CONFIG, SslUtilTest.keyStoreProp(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG), SSL_KEYSTORE_PASSWORD_CONFIG, SslUtilTest.keyStoreProp(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));
        // When:
        final Optional<KeyStore> result = SslUtil.loadKeyStore(props);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Matchers.not(Optional.empty())));
    }

    @Test
    public void shouldLoadTrustStore() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_TRUSTSTORE_LOCATION_CONFIG, SslUtilTest.trustStoreProp(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG), SSL_TRUSTSTORE_PASSWORD_CONFIG, SslUtilTest.trustStoreProp(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
        // When:
        final Optional<KeyStore> result = SslUtil.loadTrustStore(props);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Matchers.not(Optional.empty())));
    }

    @Test
    public void shouldThrowIfKeyStoreNotFound() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_KEYSTORE_LOCATION_CONFIG, "/will/not/find/me");
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Failed to load keyStore: /will/not/find/me");
        // When:
        SslUtil.loadKeyStore(props);
    }

    @Test
    public void shouldThrowIfTrustStoreNotFound() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_TRUSTSTORE_LOCATION_CONFIG, "/will/not/find/me");
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Failed to load keyStore: /will/not/find/me");
        // When:
        SslUtil.loadTrustStore(props);
    }

    @Test
    public void shouldThrowIfKeyStorePasswordWrong() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_KEYSTORE_LOCATION_CONFIG, SslUtilTest.keyStoreProp(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG), SSL_KEYSTORE_PASSWORD_CONFIG, "wrong!");
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Failed to load keyStore:");
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.is("Keystore was tampered with, or password was incorrect")));
        // When:
        SslUtil.loadKeyStore(props);
    }

    @Test
    public void shouldThrowIfTrustStorePasswordWrong() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_TRUSTSTORE_LOCATION_CONFIG, SslUtilTest.trustStoreProp(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG), SSL_TRUSTSTORE_PASSWORD_CONFIG, "wrong!");
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Failed to load keyStore:");
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.is("Keystore was tampered with, or password was incorrect")));
        // When:
        SslUtil.loadTrustStore(props);
    }

    @Test
    public void shouldDefaultToNoKeyPassword() {
        MatcherAssert.assertThat(SslUtil.getKeyPassword(Collections.emptyMap()), Matchers.is(""));
    }

    @Test
    public void shouldExtractKeyPassword() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_KEY_PASSWORD_CONFIG, "let me in");
        // Then:
        MatcherAssert.assertThat(SslUtil.getKeyPassword(props), Matchers.is("let me in"));
    }

    @Test
    public void shouldDefaultToNoopHostNameVerification() {
        MatcherAssert.assertThat(SslUtil.getHostNameVerifier(Collections.emptyMap()), Matchers.is(Optional.of(INSTANCE)));
    }

    @Test
    public void shouldSupportNoOpHostNameVerifier() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        // Then:
        MatcherAssert.assertThat(SslUtil.getHostNameVerifier(props), Matchers.is(Optional.of(INSTANCE)));
    }

    @Test
    public void shouldSupportHttpsHostNameVerifier() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "httpS");
        // Then:
        MatcherAssert.assertThat(SslUtil.getHostNameVerifier(props), Matchers.is(Optional.empty()));
    }

    @Test
    public void shouldThrowOnUnsupportedHostNameVerifier() {
        // Given:
        final Map<String, String> props = ImmutableMap.of(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "what?");
        // Then:
        expectedException.expect(ConfigException.class);
        expectedException.expectMessage("Invalid value what? for configuration ssl.endpoint.identification.algorithm: Not supported");
        // When:
        SslUtil.getHostNameVerifier(props);
    }
}

