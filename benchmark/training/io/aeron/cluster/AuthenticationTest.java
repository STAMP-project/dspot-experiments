/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.cluster;


import io.aeron.cluster.client.AeronCluster;
import io.aeron.cluster.service.ClusteredServiceContainer;
import java.util.concurrent.atomic.AtomicLong;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.collections.MutableLong;
import org.agrona.collections.MutableReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AuthenticationTest {
    private static final long MAX_CATALOG_ENTRIES = 1024;

    private static final String CREDENTIALS_STRING = "username=\"admin\"|password=\"secret\"";

    private static final String CHALLENGE_STRING = "I challenge you!";

    private static final String PRINCIPAL_STRING = "I am THE Principal!";

    private ClusteredMediaDriver clusteredMediaDriver;

    private ClusteredServiceContainer container;

    private final ExpandableArrayBuffer msgBuffer = new ExpandableArrayBuffer();

    private AeronCluster aeronCluster;

    private final byte[] encodedCredentials = AuthenticationTest.CREDENTIALS_STRING.getBytes();

    private final byte[] encodedChallenge = AuthenticationTest.CHALLENGE_STRING.getBytes();

    @Test(timeout = 10000)
    public void shouldAuthenticateOnConnectRequestWithEmptyCredentials() {
        final AtomicLong serviceMsgCounter = new AtomicLong(0L);
        final MutableLong serviceSessionId = new MutableLong((-1L));
        final MutableLong authenticatorSessionId = new MutableLong((-1L));
        final MutableReference<byte[]> encodedPrincipal = new MutableReference();
        final CredentialsSupplier credentialsSupplier = Mockito.spy(new CredentialsSupplier() {
            public byte[] encodedCredentials() {
                return NULL_CREDENTIAL;
            }

            public byte[] onChallenge(final byte[] encodedChallenge) {
                Assert.fail();
                return null;
            }
        });
        final Authenticator authenticator = Mockito.spy(new Authenticator() {
            public void onConnectRequest(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                authenticatorSessionId.value = sessionId;
                Assert.assertThat(encodedCredentials.length, CoreMatchers.is(0));
            }

            public void onChallengeResponse(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                Assert.fail();
            }

            public void onConnectedSession(final SessionProxy sessionProxy, final long nowMs) {
                Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(sessionProxy.sessionId()));
                sessionProxy.authenticate(null);
            }

            public void onChallengedSession(final SessionProxy sessionProxy, final long nowMs) {
                Assert.fail();
            }
        });
        launchClusteredMediaDriver(() -> authenticator);
        launchService(serviceSessionId, encodedPrincipal, serviceMsgCounter);
        connectClient(credentialsSupplier);
        sendCountedMessageIntoCluster(0);
        while ((serviceMsgCounter.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(aeronCluster.clusterSessionId()));
        Assert.assertThat(serviceSessionId.value, CoreMatchers.is(aeronCluster.clusterSessionId()));
        Assert.assertThat(encodedPrincipal.get().length, CoreMatchers.is(0));
    }

    @Test(timeout = 10000)
    public void shouldAuthenticateOnConnectRequestWithCredentials() {
        final AtomicLong serviceMsgCounter = new AtomicLong(0L);
        final MutableLong serviceSessionId = new MutableLong((-1L));
        final MutableLong authenticatorSessionId = new MutableLong((-1L));
        final MutableReference<byte[]> encodedPrincipal = new MutableReference();
        final CredentialsSupplier credentialsSupplier = Mockito.spy(new CredentialsSupplier() {
            public byte[] encodedCredentials() {
                return encodedCredentials;
            }

            public byte[] onChallenge(final byte[] encodedChallenge) {
                Assert.fail();
                return null;
            }
        });
        final Authenticator authenticator = Mockito.spy(new Authenticator() {
            public void onConnectRequest(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                authenticatorSessionId.value = sessionId;
                Assert.assertThat(new String(encodedCredentials), CoreMatchers.is(AuthenticationTest.CREDENTIALS_STRING));
            }

            public void onChallengeResponse(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                Assert.fail();
            }

            public void onConnectedSession(final SessionProxy sessionProxy, final long nowMs) {
                Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(sessionProxy.sessionId()));
                sessionProxy.authenticate(AuthenticationTest.PRINCIPAL_STRING.getBytes());
            }

            public void onChallengedSession(final SessionProxy sessionProxy, final long nowMs) {
                Assert.fail();
            }
        });
        launchClusteredMediaDriver(() -> authenticator);
        launchService(serviceSessionId, encodedPrincipal, serviceMsgCounter);
        connectClient(credentialsSupplier);
        sendCountedMessageIntoCluster(0);
        while ((serviceMsgCounter.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(aeronCluster.clusterSessionId()));
        Assert.assertThat(serviceSessionId.value, CoreMatchers.is(aeronCluster.clusterSessionId()));
        Assert.assertThat(new String(encodedPrincipal.get()), CoreMatchers.is(AuthenticationTest.PRINCIPAL_STRING));
    }

    @Test(timeout = 10000)
    public void shouldAuthenticateOnChallengeResponse() {
        final AtomicLong serviceMsgCounter = new AtomicLong(0L);
        final MutableLong serviceSessionId = new MutableLong((-1L));
        final MutableLong authenticatorSessionId = new MutableLong((-1L));
        final MutableReference<byte[]> encodedPrincipal = new MutableReference();
        final CredentialsSupplier credentialsSupplier = Mockito.spy(new CredentialsSupplier() {
            public byte[] encodedCredentials() {
                return NULL_CREDENTIAL;
            }

            public byte[] onChallenge(final byte[] encodedChallenge) {
                Assert.assertThat(new String(encodedChallenge), CoreMatchers.is(AuthenticationTest.CHALLENGE_STRING));
                return encodedCredentials;
            }
        });
        final Authenticator authenticator = Mockito.spy(new Authenticator() {
            boolean challengeSuccessful = false;

            public void onConnectRequest(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                authenticatorSessionId.value = sessionId;
                Assert.assertThat(encodedCredentials.length, CoreMatchers.is(0));
            }

            public void onChallengeResponse(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(sessionId));
                Assert.assertThat(new String(encodedCredentials), CoreMatchers.is(AuthenticationTest.CREDENTIALS_STRING));
                challengeSuccessful = true;
            }

            public void onConnectedSession(final SessionProxy sessionProxy, final long nowMs) {
                Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(sessionProxy.sessionId()));
                sessionProxy.challenge(encodedChallenge);
            }

            public void onChallengedSession(final SessionProxy sessionProxy, final long nowMs) {
                if (challengeSuccessful) {
                    Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(sessionProxy.sessionId()));
                    sessionProxy.authenticate(AuthenticationTest.PRINCIPAL_STRING.getBytes());
                }
            }
        });
        launchClusteredMediaDriver(() -> authenticator);
        launchService(serviceSessionId, encodedPrincipal, serviceMsgCounter);
        connectClient(credentialsSupplier);
        sendCountedMessageIntoCluster(0);
        while ((serviceMsgCounter.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(aeronCluster.clusterSessionId()));
        Assert.assertThat(serviceSessionId.value, CoreMatchers.is(aeronCluster.clusterSessionId()));
        Assert.assertThat(new String(encodedPrincipal.get()), CoreMatchers.is(AuthenticationTest.PRINCIPAL_STRING));
    }

    @Test(timeout = 10000)
    public void shouldRejectOnConnectRequest() {
        final AtomicLong serviceMsgCounter = new AtomicLong(0L);
        final MutableLong serviceSessionId = new MutableLong((-1L));
        final MutableLong authenticatorSessionId = new MutableLong((-1L));
        final MutableReference<byte[]> encodedPrincipal = new MutableReference();
        final CredentialsSupplier credentialsSupplier = Mockito.spy(new CredentialsSupplier() {
            public byte[] encodedCredentials() {
                return NULL_CREDENTIAL;
            }

            public byte[] onChallenge(final byte[] encodedChallenge) {
                Assert.assertThat(new String(encodedChallenge), CoreMatchers.is(AuthenticationTest.CHALLENGE_STRING));
                return encodedCredentials;
            }
        });
        final Authenticator authenticator = Mockito.spy(new Authenticator() {
            public void onConnectRequest(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                authenticatorSessionId.value = sessionId;
                Assert.assertThat(encodedCredentials.length, CoreMatchers.is(0));
            }

            public void onChallengeResponse(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                Assert.fail();
            }

            public void onConnectedSession(final SessionProxy sessionProxy, final long nowMs) {
                Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(sessionProxy.sessionId()));
                sessionProxy.reject();
            }

            public void onChallengedSession(final SessionProxy sessionProxy, final long nowMs) {
                Assert.fail();
            }
        });
        launchClusteredMediaDriver(() -> authenticator);
        launchService(serviceSessionId, encodedPrincipal, serviceMsgCounter);
        try {
            connectClient(credentialsSupplier);
        } catch (final AuthenticationException ex) {
            Assert.assertThat(serviceSessionId.value, CoreMatchers.is((-1L)));
            return;
        }
        Assert.fail("should have seen exception");
    }

    @Test(timeout = 10000)
    public void shouldRejectOnChallengeResponse() {
        final AtomicLong serviceMsgCounter = new AtomicLong(0L);
        final MutableLong serviceSessionId = new MutableLong((-1L));
        final MutableLong authenticatorSessionId = new MutableLong((-1L));
        final MutableReference<byte[]> encodedPrincipal = new MutableReference();
        final CredentialsSupplier credentialsSupplier = Mockito.spy(new CredentialsSupplier() {
            public byte[] encodedCredentials() {
                return NULL_CREDENTIAL;
            }

            public byte[] onChallenge(final byte[] encodedChallenge) {
                Assert.assertThat(new String(encodedChallenge), CoreMatchers.is(AuthenticationTest.CHALLENGE_STRING));
                return encodedCredentials;
            }
        });
        final Authenticator authenticator = Mockito.spy(new Authenticator() {
            boolean challengeRespondedTo = false;

            public void onConnectRequest(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                authenticatorSessionId.value = sessionId;
                Assert.assertThat(encodedCredentials.length, CoreMatchers.is(0));
            }

            public void onChallengeResponse(final long sessionId, final byte[] encodedCredentials, final long nowMs) {
                Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(sessionId));
                Assert.assertThat(new String(encodedCredentials), CoreMatchers.is(AuthenticationTest.CREDENTIALS_STRING));
                challengeRespondedTo = true;
            }

            public void onConnectedSession(final SessionProxy sessionProxy, final long nowMs) {
                Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(sessionProxy.sessionId()));
                sessionProxy.challenge(encodedChallenge);
            }

            public void onChallengedSession(final SessionProxy sessionProxy, final long nowMs) {
                if (challengeRespondedTo) {
                    Assert.assertThat(authenticatorSessionId.value, CoreMatchers.is(sessionProxy.sessionId()));
                    sessionProxy.reject();
                }
            }
        });
        launchClusteredMediaDriver(() -> authenticator);
        launchService(serviceSessionId, encodedPrincipal, serviceMsgCounter);
        try {
            connectClient(credentialsSupplier);
        } catch (final AuthenticationException ex) {
            Assert.assertThat(serviceSessionId.value, CoreMatchers.is((-1L)));
            return;
        }
        Assert.fail("should have seen exception");
    }
}

