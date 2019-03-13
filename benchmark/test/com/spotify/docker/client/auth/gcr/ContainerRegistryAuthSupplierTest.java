/**
 * -
 * -\-\-
 * docker-client
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
package com.spotify.docker.client.auth.gcr;


import ContainerRegistryAuthSupplier.CredentialRefresher;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.RegistryConfigs;
import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class ContainerRegistryAuthSupplierTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final DateTime expiration = new DateTime(2017, 5, 23, 16, 25);

    private final String tokenValue = "abc123.foobar";

    // we can't really mock GoogleCredentials since getAccessToken() is a final method (which can't be
    // mocked). We can construct an instance of GoogleCredentials for a made-up accessToken though.
    private final AccessToken accessToken = new AccessToken(tokenValue, expiration.toDate());

    private final GoogleCredentials credentials = new GoogleCredentials(accessToken);

    private final Clock clock = Mockito.mock(Clock.class);

    private final int minimumExpirationSecs = 30;

    // we wrap the call to GoogleCredentials.refresh() in this interface because the actual
    // implementation in the GoogleCredentials class will throw an exception that it isn't
    // implemented - only subclasses (constructed from real InputStreams containing real credentials)
    // implement that method.
    private final CredentialRefresher refresher = Mockito.mock(CredentialRefresher.class);

    private final ContainerRegistryAuthSupplier supplier = new ContainerRegistryAuthSupplier(credentials, clock, TimeUnit.SECONDS.toMillis(minimumExpirationSecs), refresher);

    @Test
    public void testAuthForImage_NoRefresh() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) + 1)).getMillis());
        Assert.assertThat(supplier.authFor("gcr.io/foobar/barfoo:latest"), ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken));
        Mockito.verify(refresher, Mockito.never()).refresh(credentials);
    }

    @Test
    public void testAuthForImage_RefreshNeeded() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) - 1)).getMillis());
        Assert.assertThat(supplier.authFor("gcr.io/foobar/barfoo:latest"), ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken));
        Mockito.verify(refresher).refresh(credentials);
    }

    @Test
    public void testAuthForImage_TokenExpired() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.plusMinutes(1).getMillis());
        Assert.assertThat(supplier.authFor("gcr.io/foobar/barfoo:latest"), ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken));
        Mockito.verify(refresher).refresh(credentials);
    }

    @Test
    public void testAuthForImage_NonGcrImage() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) + 1)).getMillis());
        Assert.assertThat(supplier.authFor("foobar"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testAuthForImage_ExceptionOnRefresh() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) - 1)).getMillis());
        final IOException ex = new IOException("failure!!");
        Mockito.doThrow(ex).when(refresher).refresh(credentials);
        // the exception should propagate up
        exception.expect(DockerException.class);
        exception.expectCause(Matchers.is(ex));
        supplier.authFor("gcr.io/example/foobar:1.2.3");
    }

    @Test
    public void testAuthForImage_TokenWithoutExpirationDoesNotCauseRefresh() throws Exception {
        final AccessToken accessToken = new AccessToken(tokenValue, null);
        final GoogleCredentials credentials = new GoogleCredentials(accessToken);
        final ContainerRegistryAuthSupplier supplier = new ContainerRegistryAuthSupplier(credentials, clock, TimeUnit.SECONDS.toMillis(minimumExpirationSecs), refresher);
        Assert.assertThat(supplier.authFor("gcr.io/foobar/barfoo:latest"), ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken));
        Mockito.verify(refresher, Mockito.never()).refresh(credentials);
    }

    @Test
    public void testAuthForSwarm_NoRefresh() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) + 1)).getMillis());
        Assert.assertThat(supplier.authForSwarm(), ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken));
        Mockito.verify(refresher, Mockito.never()).refresh(credentials);
    }

    @Test
    public void testAuthForSwarm_RefreshNeeded() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) - 1)).getMillis());
        Assert.assertThat(supplier.authForSwarm(), ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken));
        Mockito.verify(refresher).refresh(credentials);
    }

    @Test
    public void testAuthForSwarm_ExceptionOnRefresh() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) - 1)).getMillis());
        Mockito.doThrow(new IOException("failure!!")).when(refresher).refresh(credentials);
        Assert.assertThat(supplier.authForSwarm(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testAuthForSwarm_TokenWithoutExpirationDoesNotCauseRefresh() throws Exception {
        final AccessToken accessToken = new AccessToken(tokenValue, null);
        final GoogleCredentials credentials = new GoogleCredentials(accessToken);
        final ContainerRegistryAuthSupplier supplier = new ContainerRegistryAuthSupplier(credentials, clock, TimeUnit.SECONDS.toMillis(minimumExpirationSecs), refresher);
        Assert.assertThat(supplier.authForSwarm(), ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken));
        Mockito.verify(refresher, Mockito.never()).refresh(credentials);
    }

    @Test
    public void testAuthForBuild_NoRefresh() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) + 1)).getMillis());
        final RegistryConfigs configs = supplier.authForBuild();
        Assert.assertThat(configs.configs().values(), Matchers.is(Matchers.not(Matchers.empty())));
        Assert.assertThat(configs.configs().values(), Matchers.everyItem(ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken)));
        Mockito.verify(refresher, Mockito.never()).refresh(credentials);
    }

    @Test
    public void testAuthForBuild_RefreshNeeded() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) - 1)).getMillis());
        final RegistryConfigs configs = supplier.authForBuild();
        Assert.assertThat(configs.configs().values(), Matchers.is(Matchers.not(Matchers.empty())));
        Assert.assertThat(configs.configs().values(), Matchers.everyItem(ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken)));
        Mockito.verify(refresher).refresh(credentials);
    }

    @Test
    public void testAuthForBuild_ExceptionOnRefresh() throws Exception {
        Mockito.when(clock.millis()).thenReturn(expiration.minusSeconds(((minimumExpirationSecs) - 1)).getMillis());
        Mockito.doThrow(new IOException("failure!!")).when(refresher).refresh(credentials);
        final RegistryConfigs configs = supplier.authForBuild();
        Assert.assertThat(configs.configs().values(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void testAuthForBuild_TokenWithoutExpirationDoesNotCauseRefresh() throws Exception {
        final AccessToken accessToken = new AccessToken(tokenValue, null);
        final GoogleCredentials credentials = new GoogleCredentials(accessToken);
        final ContainerRegistryAuthSupplier supplier = new ContainerRegistryAuthSupplier(credentials, clock, TimeUnit.SECONDS.toMillis(minimumExpirationSecs), refresher);
        final RegistryConfigs configs = supplier.authForBuild();
        Assert.assertThat(configs.configs().values(), Matchers.is(Matchers.not(Matchers.empty())));
        Assert.assertThat(configs.configs().values(), Matchers.everyItem(ContainerRegistryAuthSupplierTest.matchesAccessToken(accessToken)));
        Mockito.verify(refresher, Mockito.never()).refresh(credentials);
    }
}

