/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
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
 */
package org.kaaproject.kaa.client.event.registration;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.channel.UserTransport;
import org.kaaproject.kaa.client.context.ExecutorContext;
import org.kaaproject.kaa.client.event.EndpointAccessToken;
import org.kaaproject.kaa.client.event.EndpointKeyHash;
import org.kaaproject.kaa.client.persistence.KaaClientState;
import org.kaaproject.kaa.common.endpoint.gen.SyncResponseResultType;
import org.kaaproject.kaa.common.endpoint.gen.UserAttachNotification;
import org.kaaproject.kaa.common.endpoint.gen.UserAttachResponse;
import org.kaaproject.kaa.common.endpoint.gen.UserDetachNotification;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


public class DefaultEndpointRegistrationManagerTest {
    private static final int REQUEST_ID = 42;

    private static ExecutorContext executorContext;

    private static ExecutorService executor;

    @Test(expected = IllegalStateException.class)
    public void checkUserAttachWithNoDefaultVerifier() throws Exception {
        KaaClientState state = Mockito.mock(KaaClientState.class);
        Mockito.when(state.getEndpointAccessToken()).thenReturn("");
        UserTransport transport = Mockito.mock(UserTransport.class);
        DefaultEndpointRegistrationManager manager = Mockito.spy(new DefaultEndpointRegistrationManager(state, DefaultEndpointRegistrationManagerTest.executorContext, transport, null));
        manager.attachUser("externalId", "token", null);
    }

    @Test
    public void checkUserAttachWithCustomVerifier() throws Exception {
        KaaClientState state = Mockito.mock(KaaClientState.class);
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        Mockito.when(state.getEndpointAccessToken()).thenReturn("");
        UserTransport transport = Mockito.mock(UserTransport.class);
        DefaultEndpointRegistrationManager manager = Mockito.spy(new DefaultEndpointRegistrationManager(state, executorContext, transport, null));
        manager.attachUser("verifierToken", "externalId", "token", null);
        Mockito.verify(transport, Mockito.times(1)).sync();
    }

    @Test
    public void checkAttachEndpoint() throws Exception {
        KaaClientState state = Mockito.mock(KaaClientState.class);
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        Mockito.when(state.getEndpointAccessToken()).thenReturn("");
        UserTransport transport = Mockito.mock(UserTransport.class);
        DefaultEndpointRegistrationManager manager = Mockito.spy(new DefaultEndpointRegistrationManager(state, executorContext, transport, null));
        OnAttachEndpointOperationCallback listener = Mockito.mock(OnAttachEndpointOperationCallback.class);
        manager.attachEndpoint(new EndpointAccessToken("accessToken1"), listener);
        manager.attachEndpoint(new EndpointAccessToken("accessToken2"), listener);
        ReflectionTestUtils.setField(manager, "userTransport", null);
        manager.attachEndpoint(new EndpointAccessToken("accessToken3"), null);
        Mockito.verify(transport, Mockito.times(2)).sync();
    }

    @Test
    public void checkDetachEndpoint() throws Exception {
        KaaClientState state = Mockito.mock(KaaClientState.class);
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        Mockito.when(state.getEndpointAccessToken()).thenReturn("");
        OnDetachEndpointOperationCallback listener = Mockito.mock(OnDetachEndpointOperationCallback.class);
        UserTransport transport = Mockito.mock(UserTransport.class);
        DefaultEndpointRegistrationManager manager = Mockito.spy(new DefaultEndpointRegistrationManager(state, executorContext, transport, null));
        manager.detachEndpoint(new EndpointKeyHash("keyHash1"), listener);
        manager.detachEndpoint(new EndpointKeyHash("keyHash2"), listener);
        ReflectionTestUtils.setField(manager, "userTransport", null);
        manager.detachEndpoint(new EndpointKeyHash("keyHash3"), null);
        Mockito.verify(transport, Mockito.times(2)).sync();
    }

    @Test
    public void checkAttachUser() {
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        KaaClientState state = Mockito.mock(KaaClientState.class);
        Mockito.when(state.getEndpointAccessToken()).thenReturn("");
        UserTransport transport = Mockito.mock(UserTransport.class);
        EndpointRegistrationManager manager = new DefaultEndpointRegistrationManager(state, executorContext, transport, null);
        manager.attachUser("externalId", "userExternalId", "userAccessToken", new UserAttachCallback() {
            @Override
            public void onAttachResult(UserAttachResponse response) {
            }
        });
        Mockito.verify(transport, Mockito.times(1)).sync();
    }

    @Test
    public void checkWrappers() {
        String token1 = "token1";
        String token2 = "token2";
        EndpointAccessToken at1 = new EndpointAccessToken(token1);
        EndpointAccessToken at1_2 = new EndpointAccessToken(token1);
        EndpointAccessToken at2 = new EndpointAccessToken(token2);
        Assert.assertEquals("EnndpointAccessToken != EndpointAccessToken", at1, at1);
        Assert.assertNotEquals("EndpointAccessToken should be not equal to String object", at1, token1);
        Assert.assertEquals("toString() returned different value from getToken()", at1.getToken(), at1.toString());
        Assert.assertEquals("Objects with equal tokens are not equal", at1, at1_2);
        Assert.assertNotEquals("Objects with different tokens are equal", at1, at2);
        Assert.assertEquals("Objects' hash codes with equal tokens are not equal", at1.hashCode(), at1_2.hashCode());
        Assert.assertNotEquals("Objects' hash codes with different tokens are equal", at1.hashCode(), at2.hashCode());
        at1_2.setToken(token2);
        Assert.assertEquals("Objects with equal tokens are not equal", at1_2, at2);
        Assert.assertNotEquals("Objects with different tokens are equal", at1, at1_2);
        EndpointAccessToken emptyToken1 = new EndpointAccessToken(null);
        EndpointAccessToken emptyToken2 = new EndpointAccessToken(null);
        Assert.assertEquals("Empty objects with are not equal", emptyToken1, emptyToken2);
        Assert.assertEquals("Objects' hash codes with empty tokens are not equal", emptyToken1.hashCode(), emptyToken1.hashCode());
        Assert.assertNotEquals("Different objects are equal", at1, emptyToken1);
        Assert.assertNotEquals("Null-equality of EndpointAccessToken", at1, null);
        String hash1 = "hash1";
        String hash2 = "hash2";
        EndpointKeyHash eoh1 = new EndpointKeyHash(hash1);
        EndpointKeyHash eoh1_2 = new EndpointKeyHash(hash1);
        EndpointKeyHash eoh2 = new EndpointKeyHash(hash2);
        Assert.assertEquals("EndpointKeyHash != EndpointKeyHash", eoh1, eoh1);
        Assert.assertNotEquals("EndpointKeyHash should be not equal to String object", eoh1, hash1);
        Assert.assertEquals("toString() returned different value from getKeyHash()", eoh1.getKeyHash(), eoh1.toString());
        Assert.assertEquals("Objects with equal keyHashes are not equal", eoh1, eoh1_2);
        Assert.assertNotEquals("Objects with different keyHashes are equal", eoh1, eoh2);
        Assert.assertEquals("Objects' hash codes with equal keyHashes are not equal", eoh1.hashCode(), eoh1_2.hashCode());
        Assert.assertNotEquals("Objects' hash codes with different keyHashes are equal", eoh1.hashCode(), eoh2.hashCode());
        eoh1_2.setKeyHash(hash2);
        Assert.assertEquals("Objects with equal keyHashes are not equal", eoh1_2, eoh2);
        Assert.assertNotEquals("Objects with different keyHashes are equal", eoh1, eoh1_2);
        EndpointKeyHash emptyHash1 = new EndpointKeyHash(null);
        EndpointKeyHash emptyHash2 = new EndpointKeyHash(null);
        Assert.assertEquals("Empty objects with are not equal", emptyHash1, emptyHash2);
        Assert.assertEquals("Objects' hash codes with empty hashes are not equal", emptyHash1.hashCode(), emptyHash1.hashCode());
        Assert.assertNotEquals("Different objects are equal", eoh1, emptyHash1);
        Assert.assertNotEquals("Null-equality of EndpointKeyHash", eoh1, null);
    }

    @Test
    public void checkOnAttachedCallback() throws IOException {
        KaaClientState state = Mockito.mock(KaaClientState.class);
        Mockito.when(state.getEndpointAccessToken()).thenReturn("");
        AttachEndpointToUserCallback listener = Mockito.mock(AttachEndpointToUserCallback.class);
        DefaultEndpointRegistrationManager manager = new DefaultEndpointRegistrationManager(state, DefaultEndpointRegistrationManagerTest.executorContext, null, null);
        manager.setAttachedCallback(null);
        manager.onUpdate(null, null, null, new UserAttachNotification("foo", "bar"), null);
        manager.setAttachedCallback(listener);
        manager.onUpdate(null, null, null, new UserAttachNotification("foo", "bar"), null);
        Mockito.verify(listener, Mockito.timeout(1000).times(1)).onAttachedToUser("foo", "bar");
        Mockito.verify(state, Mockito.timeout(1000).times(2)).setAttachedToUser(true);
        manager.setAttachedCallback(null);
        manager.attachUser("externalId", "foo", "bar", null);
        manager.onUpdate(null, null, new UserAttachResponse(SyncResponseResultType.SUCCESS, null, null), null, null);
        manager.setAttachedCallback(listener);
        manager.attachUser("externalId", "foo", "bar", null);
        manager.onUpdate(null, null, new UserAttachResponse(SyncResponseResultType.SUCCESS, null, null), null, null);
        Mockito.verify(listener, Mockito.timeout(1000).times(1)).onAttachedToUser(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(state, Mockito.timeout(1000).times(4)).setAttachedToUser(true);
    }

    @Test
    public void checkOnDetachedCallback() throws IOException {
        KaaClientState state = Mockito.mock(KaaClientState.class);
        Mockito.when(state.getEndpointAccessToken()).thenReturn("");
        DetachEndpointFromUserCallback listener = Mockito.mock(DetachEndpointFromUserCallback.class);
        DefaultEndpointRegistrationManager manager = new DefaultEndpointRegistrationManager(state, DefaultEndpointRegistrationManagerTest.executorContext, null, null);
        manager.setDetachedCallback(null);
        manager.onUpdate(null, null, null, null, new UserDetachNotification("foo"));
        manager.setDetachedCallback(listener);
        manager.onUpdate(null, null, null, null, new UserDetachNotification("foo"));
        Mockito.verify(listener, Mockito.timeout(1000).times(1)).onDetachedFromUser("foo");
        Mockito.verify(state, Mockito.timeout(1000).times(2)).setAttachedToUser(false);
    }
}

