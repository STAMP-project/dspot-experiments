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
package org.kaaproject.kaa.client.event;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.channel.EventTransport;
import org.kaaproject.kaa.client.context.ExecutorContext;
import org.kaaproject.kaa.client.persistance.KaaClientPropertiesStateTest;
import org.kaaproject.kaa.client.persistence.FilePersistentStorage;
import org.kaaproject.kaa.client.persistence.KaaClientPropertiesState;
import org.kaaproject.kaa.client.transact.TransactionId;
import org.kaaproject.kaa.client.util.CommonsBase64;
import org.kaaproject.kaa.common.endpoint.gen.EventListenersResponse;
import org.kaaproject.kaa.common.endpoint.gen.EventSyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.SyncResponseResultType;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EventManagerTest {
    @Test
    public void testNoHandler() throws IOException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        EventTransport transport = Mockito.mock(EventTransport.class);
        EventFamily eventFamily = Mockito.mock(EventFamily.class);
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        EventManager eventManager = new DefaultEventManager(state, executorContext, transport);
        eventManager.registerEventFamily(eventFamily);
        eventManager.produceEvent("kaa.test.event.PlayEvent", new byte[0], null);
        Mockito.verify(transport, Mockito.times(1)).sync();
        Mockito.verify(eventFamily, Mockito.times(0)).getSupportedEventFqns();
        Mockito.verify(eventFamily, Mockito.times(0)).onGenericEvent(ArgumentMatchers.anyString(), ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());
    }

    @Test
    public void testEngageRelease() throws IOException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        EventTransport transport = Mockito.mock(EventTransport.class);
        EventFamily eventFamily = Mockito.mock(EventFamily.class);
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        EventManager eventManager = new DefaultEventManager(state, executorContext, transport);
        eventManager.registerEventFamily(eventFamily);
        eventManager.produceEvent("kaa.test.event.PlayEvent", new byte[0], null);
        Mockito.verify(transport, Mockito.times(1)).sync();
        eventManager.engageDataChannel();
        eventManager.produceEvent("kaa.test.event.PlayEvent", new byte[0], null);
        Mockito.verify(transport, Mockito.times(1)).sync();
        Assert.assertTrue(eventManager.releaseDataChannel());
    }

    @Test
    public void testTransaction() throws IOException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        EventTransport transport = Mockito.mock(EventTransport.class);
        EventFamily eventFamily = Mockito.mock(EventFamily.class);
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        EventManager eventManager = new DefaultEventManager(state, executorContext, transport);
        eventManager.registerEventFamily(eventFamily);
        TransactionId trxId = eventManager.beginTransaction();
        Assert.assertNotNull("Null transaction id", trxId);
        eventManager.produceEvent("kaa.test.event.PlayEvent", new byte[0], null, trxId);
        eventManager.produceEvent("kaa.test.event.PlayEvent", new byte[0], null, trxId);
        Mockito.verify(transport, Mockito.times(0)).sync();
        eventManager.rollback(trxId);
        Mockito.verify(transport, Mockito.times(0)).sync();
        trxId = eventManager.beginTransaction();
        eventManager.produceEvent("kaa.test.event.PlayEvent", new byte[0], null, trxId);
        Mockito.verify(transport, Mockito.times(0)).sync();
        eventManager.commit(trxId);
        Mockito.verify(transport, Mockito.times(1)).sync();
    }

    @Test
    public void testOneEventForTwoDifferentFamilies() throws Exception {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        EventTransport transport = Mockito.mock(EventTransport.class);
        EventManagerTest.ConcreteEventFamily eventFamily = new EventManagerTest.ConcreteEventFamily("kaa.test.event.PlayEvent");
        EventManagerTest.ConcreteEventFamily eventFamily2 = new EventManagerTest.ConcreteEventFamily("kaa.test.event.StopEvent");
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Mockito.when(executorContext.getCallbackExecutor()).thenReturn(executor);
        EventManager eventManager = new DefaultEventManager(state, executorContext, transport);
        eventManager.registerEventFamily(eventFamily);
        eventManager.registerEventFamily(eventFamily2);
        Assert.assertEquals("Events count doesn't match", new Integer(0), eventFamily.getEventsCount());
        Assert.assertEquals("Events count doesn't match", new Integer(0), eventFamily2.getEventsCount());
        eventManager.onGenericEvent("kaa.test.event.PlayEvent", new byte[0], null);
        Thread.sleep(500);
        Assert.assertEquals("Events count doesn't match", new Integer(1), eventFamily.getEventsCount());
        Assert.assertEquals("Events count doesn't match", new Integer(0), eventFamily2.getEventsCount());
        eventManager.onGenericEvent("kaa.test.event.StopEvent", new byte[0], null);
        Thread.sleep(500);
        Assert.assertEquals("Events count doesn't match", new Integer(1), eventFamily.getEventsCount());
        Assert.assertEquals("Events count doesn't match", new Integer(1), eventFamily2.getEventsCount());
        eventManager.onGenericEvent("kaa.test.event.NoSuchEvent", new byte[0], null);
        Thread.sleep(500);
        Assert.assertEquals("Events count doesn't match", new Integer(1), eventFamily.getEventsCount());
        Assert.assertEquals("Events count doesn't match", new Integer(1), eventFamily2.getEventsCount());
    }

    @Test
    public void checkFillRequest() throws IOException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        EventTransport transport = Mockito.mock(EventTransport.class);
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        EventManager eventManager = new DefaultEventManager(state, executorContext, transport);
        EventSyncRequest request = new EventSyncRequest();
        eventManager.produceEvent("kaa.test.event.SomeEvent", new byte[0], "theTarget");
        eventManager.fillEventListenersSyncRequest(request);
        request.setEvents(eventManager.pollPendingEvents());
        Assert.assertNotNull(request.getEvents());
        Assert.assertEquals(1, request.getEvents().size());
        Assert.assertEquals("kaa.test.event.SomeEvent", request.getEvents().get(0).getEventClassFQN());
        Assert.assertEquals("theTarget", request.getEvents().get(0).getTarget());
        Assert.assertArrayEquals(new byte[0], request.getEvents().get(0).getEventData().array());
        request = new EventSyncRequest();
        List<String> eventFQNs = new ArrayList<String>();
        eventFQNs.add("eventFQN1");
        eventManager.findEventListeners(eventFQNs, new FindEventListenersCallback() {
            @Override
            public void onRequestFailed() {
            }

            @Override
            public void onEventListenersReceived(List<String> eventListeners) {
            }
        });
        eventManager.findEventListeners(eventFQNs, new FindEventListenersCallback() {
            @Override
            public void onRequestFailed() {
            }

            @Override
            public void onEventListenersReceived(List<String> eventListeners) {
            }
        });
        eventManager.fillEventListenersSyncRequest(request);
        Assert.assertNotNull(request.getEventListenersRequests());
        Assert.assertEquals(2, request.getEventListenersRequests().size());
        Assert.assertArrayEquals(eventFQNs.toArray(), request.getEventListenersRequests().get(0).getEventClassFQNs().toArray());
    }

    @Test
    public void testEventListenersRequestResponse() throws IOException {
        KaaClientPropertiesState state = new KaaClientPropertiesState(new FilePersistentStorage(), CommonsBase64.getInstance(), KaaClientPropertiesStateTest.getProperties());
        EventTransport transport = Mockito.mock(EventTransport.class);
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        Mockito.when(executorContext.getCallbackExecutor()).thenReturn(Executors.newFixedThreadPool(1));
        EventManager eventManager = new DefaultEventManager(state, executorContext, transport);
        List<String> eventFQNs = new ArrayList<String>();
        eventFQNs.add("eventFQN1");
        FindEventListenersCallback fetchListener = Mockito.mock(FindEventListenersCallback.class);
        int requestIdOk = eventManager.findEventListeners(eventFQNs, fetchListener);
        int requestIdBad = eventManager.findEventListeners(eventFQNs, fetchListener);
        Mockito.verify(transport, Mockito.atLeast(1)).sync();
        List<EventListenersResponse> response = new ArrayList<EventListenersResponse>();
        response.add(new EventListenersResponse(requestIdOk, new ArrayList<String>(), SyncResponseResultType.SUCCESS));
        response.add(new EventListenersResponse(requestIdBad, new ArrayList<String>(), SyncResponseResultType.FAILURE));
        eventManager.eventListenersResponseReceived(response);
        Mockito.verify(fetchListener, Mockito.timeout(1000).times(1)).onEventListenersReceived(ArgumentMatchers.anyListOf(String.class));
        Mockito.verify(fetchListener, Mockito.times(1)).onRequestFailed();
    }

    private class ConcreteEventFamily implements EventFamily {
        Set<String> supportedEventFQNs = new HashSet<String>();

        private Integer eventsCount;

        public ConcreteEventFamily(String supportedFQN) {
            eventsCount = 0;
            supportedEventFQNs.add(supportedFQN);
        }

        @Override
        public Set<String> getSupportedEventFqns() {
            return supportedEventFQNs;
        }

        @Override
        public void onGenericEvent(String eventFqn, byte[] data, String source) {
            (eventsCount)++;
        }

        public Integer getEventsCount() {
            return eventsCount;
        }
    }
}

