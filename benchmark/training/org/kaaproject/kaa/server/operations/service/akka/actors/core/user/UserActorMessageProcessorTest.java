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
package org.kaaproject.kaa.server.operations.service.akka.actors.core.user;


import akka.actor.ActorContext;
import akka.actor.ActorRef;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;
import org.kaaproject.kaa.server.common.Base64Util;
import org.kaaproject.kaa.server.operations.service.akka.AkkaContext;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.route.RouteOperation;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.session.EndpointEventTimeoutMessage;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.user.EndpointEventReceiveMessage;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.user.EndpointEventSendMessage;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.user.EndpointUserConnectMessage;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.user.RemoteEndpointEventMessage;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.user.RouteInfoMessage;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.user.UserRouteInfoMessage;
import org.kaaproject.kaa.server.operations.service.cache.CacheService;
import org.kaaproject.kaa.server.operations.service.cache.EventClassFqnKey;
import org.kaaproject.kaa.server.operations.service.event.EndpointEvent;
import org.kaaproject.kaa.server.operations.service.event.EventClassFamilyVersion;
import org.kaaproject.kaa.server.operations.service.event.EventClassFqnVersion;
import org.kaaproject.kaa.server.operations.service.event.EventService;
import org.kaaproject.kaa.server.operations.service.event.EventStorage;
import org.kaaproject.kaa.server.operations.service.event.RemoteEndpointEvent;
import org.kaaproject.kaa.server.operations.service.event.RouteInfo;
import org.kaaproject.kaa.server.operations.service.event.RouteTableAddress;
import org.kaaproject.kaa.server.operations.service.event.RouteTableKey;
import org.kaaproject.kaa.server.operations.service.event.UserRouteInfo;
import org.kaaproject.kaa.server.sync.Event;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.util.ReflectionUtils;


public class UserActorMessageProcessorTest {
    private static final int ECF_ID2_VERSION = 3;

    private static final int ECF_ID1_VERSION = 1;

    private static final String ECF_ID2 = "ECF_ID2";

    private static final String ECF_ID1 = "ECF_ID1";

    private static final String SERVER2 = "SERVER2";

    private static final String SERVER3 = "SERVER3";

    private static final String APP_TOKEN = "APP_TOKEN";

    private static final String TENANT_ID = "TENANT_ID";

    private static final String USER_ID = "USER_ID";

    private final EndpointObjectHash endpoint1Key = EndpointObjectHash.fromSha1("endpoint1");

    private final EndpointObjectHash endpoint2Key = EndpointObjectHash.fromSha1("endpoint2");

    private final EndpointObjectHash endpoint3Key = EndpointObjectHash.fromSha1("endpoint3");

    private LocalUserActorMessageProcessor messageProcessor;

    private List<EventClassFamilyVersion> ecfVersions;

    private EventClassFamilyVersion ecfVersion1;

    private EventClassFamilyVersion ecfVersion2;

    private RouteTableAddress address1;

    private RouteTableAddress address2;

    private RouteTableAddress address3;

    private AkkaContext akkaContextMock;

    private CacheService cacheServiceMock;

    private EventService eventServiceMock;

    private ActorContext actorContextMock;

    private ActorRef originatorRefMock;

    @Test
    public void testEndpointConnectFlow() {
        EndpointUserConnectMessage message1 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint1Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message1);
        Mockito.verify(eventServiceMock).sendUserRouteInfo(new UserRouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID));
        Mockito.verify(eventServiceMock, Mockito.times(0)).sendRouteInfo(ArgumentMatchers.any(RouteInfo.class), ArgumentMatchers.any(String.class));
        RouteInfo routeInfo = new RouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, address2, ecfVersions);
        RouteInfoMessage message2 = new RouteInfoMessage(routeInfo);
        messageProcessor.processRouteInfoMessage(actorContextMock, message2);
        RouteInfo localRouteInfo = new RouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, address1, ecfVersions);
        Mockito.verify(eventServiceMock).sendRouteInfo(Collections.singletonList(localRouteInfo), UserActorMessageProcessorTest.SERVER2);
        EndpointUserConnectMessage message3 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint3Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message3);
        Mockito.verify(eventServiceMock).sendRouteInfo(new RouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, address3, ecfVersions), UserActorMessageProcessorTest.SERVER2);
    }

    @Test
    public void testEndpointLocalEvent() {
        EndpointUserConnectMessage message1 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint1Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message1);
        EndpointUserConnectMessage message2 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint2Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message2);
        Mockito.verify(eventServiceMock).sendUserRouteInfo(new UserRouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID));
        Mockito.verify(eventServiceMock, Mockito.times(0)).sendRouteInfo(ArgumentMatchers.any(RouteInfo.class), ArgumentMatchers.any(String.class));
        Mockito.when(cacheServiceMock.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(UserActorMessageProcessorTest.TENANT_ID, "testClassFqn"))).thenReturn(UserActorMessageProcessorTest.ECF_ID1);
        RouteTableKey routeKey = new RouteTableKey(UserActorMessageProcessorTest.APP_TOKEN, ecfVersion1);
        Mockito.when(cacheServiceMock.getRouteKeys(new EventClassFqnVersion(UserActorMessageProcessorTest.TENANT_ID, "testClassFqn", UserActorMessageProcessorTest.ECF_ID1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Event event = new Event(0, "testClassFqn", ByteBuffer.wrap(new byte[0]), null, Base64Util.encode(endpoint1Key.getData()));
        EndpointEventSendMessage eventMessage = new EndpointEventSendMessage(UserActorMessageProcessorTest.USER_ID, Collections.singletonList(event), endpoint2Key, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointEventSendMessage(actorContextMock, eventMessage);
        Mockito.verify(messageProcessor).sendEventToLocal(Mockito.any(ActorContext.class), Mockito.any(EndpointEventReceiveMessage.class));
    }

    @Test
    public void testEndpointRemoteReceiveEvent() {
        EndpointUserConnectMessage message1 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint1Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message1);
        RouteTableKey routeKey = new RouteTableKey(UserActorMessageProcessorTest.APP_TOKEN, ecfVersion1);
        Mockito.when(cacheServiceMock.getRouteKeys(new EventClassFqnVersion(UserActorMessageProcessorTest.TENANT_ID, "testClassFqn", UserActorMessageProcessorTest.ECF_ID1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Event event = new Event(0, "testClassFqn", ByteBuffer.wrap(new byte[0]), null, Base64Util.encode(endpoint1Key.getData()));
        EndpointEvent endpointEvent = new EndpointEvent(endpoint2Key, event, UUID.randomUUID(), System.currentTimeMillis(), UserActorMessageProcessorTest.ECF_ID1_VERSION);
        RemoteEndpointEvent remoteEvent = new RemoteEndpointEvent(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, endpointEvent, new RouteTableAddress(endpoint1Key, UserActorMessageProcessorTest.APP_TOKEN, "SERVER1"));
        RemoteEndpointEventMessage message2 = new RemoteEndpointEventMessage(remoteEvent);
        messageProcessor.processRemoteEndpointEventMessage(actorContextMock, message2);
        Mockito.verify(cacheServiceMock, Mockito.never()).getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(UserActorMessageProcessorTest.TENANT_ID, "testClassFqn"));
        Mockito.verify(messageProcessor).sendEventToLocal(Mockito.any(ActorContext.class), Mockito.any(EndpointEventReceiveMessage.class));
    }

    @Test
    public void testEndpointRemoteSendEvent() {
        EndpointUserConnectMessage message1 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint1Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message1);
        Mockito.verify(eventServiceMock).sendUserRouteInfo(new UserRouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID));
        Mockito.verify(eventServiceMock, Mockito.times(0)).sendRouteInfo(ArgumentMatchers.any(RouteInfo.class), ArgumentMatchers.any(String.class));
        RouteInfo routeInfo = new RouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, address2, ecfVersions);
        RouteInfoMessage message2 = new RouteInfoMessage(routeInfo);
        messageProcessor.processRouteInfoMessage(actorContextMock, message2);
        Mockito.when(cacheServiceMock.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(UserActorMessageProcessorTest.TENANT_ID, "testClassFqn"))).thenReturn(UserActorMessageProcessorTest.ECF_ID1);
        RouteTableKey routeKey = new RouteTableKey(UserActorMessageProcessorTest.APP_TOKEN, ecfVersion1);
        Mockito.when(cacheServiceMock.getRouteKeys(new EventClassFqnVersion(UserActorMessageProcessorTest.TENANT_ID, "testClassFqn", UserActorMessageProcessorTest.ECF_ID1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Event event = new Event(0, "testClassFqn", ByteBuffer.wrap(new byte[0]), null, Base64Util.encode(endpoint2Key.getData()));
        EndpointEventSendMessage eventMessage = new EndpointEventSendMessage(UserActorMessageProcessorTest.USER_ID, Collections.singletonList(event), endpoint1Key, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointEventSendMessage(actorContextMock, eventMessage);
        Mockito.verify(messageProcessor, Mockito.never()).sendEventToLocal(Mockito.any(ActorContext.class), Mockito.any(EndpointEventReceiveMessage.class));
        Mockito.verify(eventServiceMock).sendEvent(ArgumentMatchers.any(RemoteEndpointEvent.class));
    }

    @Test
    public void testEndpointTimeoutMessage() throws NoSuchFieldException, SecurityException {
        EndpointUserConnectMessage message1 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint1Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message1);
        Mockito.when(cacheServiceMock.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(UserActorMessageProcessorTest.TENANT_ID, "testClassFqn"))).thenReturn(UserActorMessageProcessorTest.ECF_ID1);
        RouteTableKey routeKey = new RouteTableKey(UserActorMessageProcessorTest.APP_TOKEN, ecfVersion1);
        Mockito.when(cacheServiceMock.getRouteKeys(new EventClassFqnVersion(UserActorMessageProcessorTest.TENANT_ID, "testClassFqn", UserActorMessageProcessorTest.ECF_ID1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Event event = new Event(0, "testClassFqn", ByteBuffer.wrap(new byte[0]), null, null);
        EndpointEventSendMessage eventMessage = new EndpointEventSendMessage(UserActorMessageProcessorTest.USER_ID, Collections.singletonList(event), endpoint1Key, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointEventSendMessage(actorContextMock, eventMessage);
        EndpointUserConnectMessage message2 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint2Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message2);
        // 1 - means it is called once
        Mockito.verify(messageProcessor, Mockito.times(1)).sendEventToLocal(Mockito.any(ActorContext.class), Mockito.any(EndpointEventReceiveMessage.class));
        Field field = LocalUserActorMessageProcessor.class.getDeclaredField("eventStorage");
        field.setAccessible(true);
        EventStorage storage = ((EventStorage) (ReflectionUtils.getField(field, messageProcessor)));
        EndpointEventTimeoutMessage message = new EndpointEventTimeoutMessage(storage.getEvents(routeKey).iterator().next());
        messageProcessor.processEndpointEventTimeoutMessage(actorContextMock, message);
        EndpointUserConnectMessage message3 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint3Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message3);
        // still 1 - means it was not called after first call
        Mockito.verify(messageProcessor, Mockito.times(1)).sendEventToLocal(Mockito.any(ActorContext.class), Mockito.any(EndpointEventReceiveMessage.class));
    }

    @Test
    public void testUserRouteInfoAddFlow() {
        EndpointUserConnectMessage message1 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint1Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message1);
        Mockito.verify(eventServiceMock).sendUserRouteInfo(new UserRouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID));
        Mockito.verify(eventServiceMock, Mockito.times(0)).sendRouteInfo(ArgumentMatchers.any(RouteInfo.class), ArgumentMatchers.any(String.class));
        UserRouteInfoMessage message2 = new UserRouteInfoMessage(new UserRouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, UserActorMessageProcessorTest.SERVER2, RouteOperation.ADD));
        messageProcessor.processUserRouteInfoMessage(actorContextMock, message2);
        RouteInfo localRouteInfo = new RouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, address1, ecfVersions);
        Mockito.verify(eventServiceMock, Mockito.times(1)).sendRouteInfo(Collections.singletonList(localRouteInfo), UserActorMessageProcessorTest.SERVER2);
        UserRouteInfoMessage message3 = new UserRouteInfoMessage(new UserRouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, UserActorMessageProcessorTest.SERVER3, RouteOperation.ADD));
        messageProcessor.processUserRouteInfoMessage(actorContextMock, message3);
        Mockito.verify(eventServiceMock, Mockito.times(1)).sendRouteInfo(Collections.singletonList(localRouteInfo), UserActorMessageProcessorTest.SERVER3);
        Mockito.verify(eventServiceMock, Mockito.times(1)).sendRouteInfo(Collections.singletonList(localRouteInfo), UserActorMessageProcessorTest.SERVER2);
    }

    @Test
    public void testUserRouteInfoRemoveFlow() {
        EndpointUserConnectMessage message1 = new EndpointUserConnectMessage(UserActorMessageProcessorTest.USER_ID, endpoint1Key, ecfVersions, 1, null, UserActorMessageProcessorTest.APP_TOKEN, originatorRefMock);
        messageProcessor.processEndpointConnectMessage(actorContextMock, message1);
        Mockito.verify(eventServiceMock).sendUserRouteInfo(new UserRouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID));
        Mockito.verify(eventServiceMock, Mockito.times(0)).sendRouteInfo(ArgumentMatchers.any(RouteInfo.class), ArgumentMatchers.any(String.class));
        UserRouteInfoMessage message2 = new UserRouteInfoMessage(new UserRouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, UserActorMessageProcessorTest.SERVER2, RouteOperation.DELETE));
        messageProcessor.processUserRouteInfoMessage(actorContextMock, message2);
        RouteInfo localRouteInfo = new RouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, address1, ecfVersions);
        Mockito.verify(eventServiceMock, Mockito.times(0)).sendRouteInfo(Collections.singletonList(localRouteInfo), UserActorMessageProcessorTest.SERVER2);
        UserRouteInfoMessage message3 = new UserRouteInfoMessage(new UserRouteInfo(UserActorMessageProcessorTest.TENANT_ID, UserActorMessageProcessorTest.USER_ID, UserActorMessageProcessorTest.SERVER3, RouteOperation.DELETE));
        messageProcessor.processUserRouteInfoMessage(actorContextMock, message3);
        Mockito.verify(eventServiceMock, Mockito.times(0)).sendRouteInfo(Collections.singletonList(localRouteInfo), UserActorMessageProcessorTest.SERVER3);
        Mockito.verify(eventServiceMock, Mockito.times(0)).sendRouteInfo(Collections.singletonList(localRouteInfo), UserActorMessageProcessorTest.SERVER2);
    }
}

