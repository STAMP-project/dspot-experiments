/**
 * -
 * -\-\-
 * Helios Client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
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
package com.spotify.helios.client;


import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Optional;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class GoogleCredentialsAccessTokenSupplierTest {
    @Test
    public void testGetWhenDisabled() {
        final GoogleCredentialsAccessTokenSupplier supplier = new GoogleCredentialsAccessTokenSupplier(false, null, null);
        MatcherAssert.assertThat(supplier.get(), Matchers.equalTo(Optional.<AccessToken>absent()));
    }

    @Test
    public void testGetWithStaticToken() {
        final AccessToken token = new AccessToken("token", null);
        final GoogleCredentialsAccessTokenSupplier supplier = new GoogleCredentialsAccessTokenSupplier(true, token, null);
        MatcherAssert.assertThat(supplier.get(), Matchers.equalTo(Optional.of(token)));
    }

    @Test
    public void testGetWithCredentials() throws IOException {
        final GoogleCredentials credentials = Mockito.mock(GoogleCredentials.class);
        final GoogleCredentialsAccessTokenSupplier supplier = new GoogleCredentialsAccessTokenSupplier(true, null, null, credentials);
        supplier.get();
        Mockito.verify(credentials).refreshIfExpired();
    }
}

