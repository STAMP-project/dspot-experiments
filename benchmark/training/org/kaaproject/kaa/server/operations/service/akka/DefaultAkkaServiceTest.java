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
package org.kaaproject.kaa.server.operations.service.akka;


import ChannelType.SYNC;
import ChannelType.SYNC_WITH_TIMEOUT;
import EndpointEventDeliveryMessage.EventDeliveryStatus.FAILURE;
import SyncResponseResultType.REDIRECT;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.Constants;
import org.kaaproject.kaa.common.avro.AvroByteArrayConverter;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.EndpointProfileSchemaDto;
import org.kaaproject.kaa.common.dto.EventClassFamilyVersionStateDto;
import org.kaaproject.kaa.common.dto.NotificationDto;
import org.kaaproject.kaa.common.dto.ServerProfileSchemaDto;
import org.kaaproject.kaa.common.dto.credentials.CredentialsStatus;
import org.kaaproject.kaa.common.dto.credentials.EndpointRegistrationDto;
import org.kaaproject.kaa.common.dto.ctl.CTLSchemaDto;
import org.kaaproject.kaa.common.dto.logs.LogSchemaDto;
import org.kaaproject.kaa.common.endpoint.gen.ConfigurationSyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.EndpointAttachRequest;
import org.kaaproject.kaa.common.endpoint.gen.EndpointDetachRequest;
import org.kaaproject.kaa.common.endpoint.gen.Event;
import org.kaaproject.kaa.common.endpoint.gen.EventSequenceNumberRequest;
import org.kaaproject.kaa.common.endpoint.gen.EventSequenceNumberResponse;
import org.kaaproject.kaa.common.endpoint.gen.EventSyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.EventSyncResponse;
import org.kaaproject.kaa.common.endpoint.gen.LogEntry;
import org.kaaproject.kaa.common.endpoint.gen.LogSyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.NotificationSyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.ProfileSyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.RedirectSyncResponse;
import org.kaaproject.kaa.common.endpoint.gen.SyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.SyncRequestMetaData;
import org.kaaproject.kaa.common.endpoint.gen.SyncResponse;
import org.kaaproject.kaa.common.endpoint.gen.UserSyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.UserSyncResponse;
import org.kaaproject.kaa.common.endpoint.security.MessageEncoderDecoder;
import org.kaaproject.kaa.common.endpoint.security.MessageEncoderDecoder.CipherPair;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;
import org.kaaproject.kaa.server.common.Base64Util;
import org.kaaproject.kaa.server.common.dao.ApplicationService;
import org.kaaproject.kaa.server.common.dao.CtlService;
import org.kaaproject.kaa.server.common.log.shared.appender.LogAppender;
import org.kaaproject.kaa.server.common.log.shared.appender.LogDeliveryCallback;
import org.kaaproject.kaa.server.common.log.shared.appender.data.BaseLogEventPack;
import org.kaaproject.kaa.server.common.thrift.gen.operations.Notification;
import org.kaaproject.kaa.server.common.thrift.gen.operations.RedirectionRule;
import org.kaaproject.kaa.server.common.thrift.gen.operations.ThriftEndpointConfigurationRefreshMessage;
import org.kaaproject.kaa.server.common.thrift.gen.operations.ThriftEndpointDeregistrationMessage;
import org.kaaproject.kaa.server.common.thrift.gen.operations.ThriftUnicastNotificationMessage;
import org.kaaproject.kaa.server.node.service.credentials.CredentialsService;
import org.kaaproject.kaa.server.node.service.credentials.CredentialsServiceLocator;
import org.kaaproject.kaa.server.node.service.registration.RegistrationService;
import org.kaaproject.kaa.server.operations.pojo.SyncContext;
import org.kaaproject.kaa.server.operations.service.OperationsService;
import org.kaaproject.kaa.server.operations.service.akka.actors.core.endpoint.local.LocalEndpointActorMessageProcessor;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.route.ActorClassifier;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.route.EndpointAddress;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.route.RouteOperation;
import org.kaaproject.kaa.server.operations.service.akka.messages.core.user.EndpointEventReceiveMessage;
import org.kaaproject.kaa.server.operations.service.cache.AppVersionKey;
import org.kaaproject.kaa.server.operations.service.cache.CacheService;
import org.kaaproject.kaa.server.operations.service.cache.EventClassFqnKey;
import org.kaaproject.kaa.server.operations.service.cluster.ClusterService;
import org.kaaproject.kaa.server.operations.service.event.EndpointEvent;
import org.kaaproject.kaa.server.operations.service.event.EventClassFamilyVersion;
import org.kaaproject.kaa.server.operations.service.event.EventClassFqnVersion;
import org.kaaproject.kaa.server.operations.service.event.EventService;
import org.kaaproject.kaa.server.operations.service.event.RemoteEndpointEvent;
import org.kaaproject.kaa.server.operations.service.event.RouteInfo;
import org.kaaproject.kaa.server.operations.service.event.RouteTableAddress;
import org.kaaproject.kaa.server.operations.service.event.RouteTableKey;
import org.kaaproject.kaa.server.operations.service.event.UserRouteInfo;
import org.kaaproject.kaa.server.operations.service.logs.LogAppenderService;
import org.kaaproject.kaa.server.operations.service.metrics.MetricsService;
import org.kaaproject.kaa.server.operations.service.notification.NotificationDeltaService;
import org.kaaproject.kaa.server.operations.service.security.KeyStoreService;
import org.kaaproject.kaa.server.operations.service.user.EndpointUserService;
import org.kaaproject.kaa.server.sync.EventServerSync;
import org.kaaproject.kaa.server.sync.ProfileClientSync;
import org.kaaproject.kaa.server.sync.ServerSync;
import org.kaaproject.kaa.server.sync.SyncStatus;
import org.kaaproject.kaa.server.sync.SyncStatus.SUCCESS;
import org.kaaproject.kaa.server.sync.UserServerSync;
import org.kaaproject.kaa.server.sync.platform.AvroEncDec;
import org.kaaproject.kaa.server.transport.EndpointRevocationException;
import org.kaaproject.kaa.server.transport.EndpointVerificationException;
import org.kaaproject.kaa.server.transport.InvalidSdkTokenException;
import org.kaaproject.kaa.server.transport.channel.ChannelContext;
import org.kaaproject.kaa.server.transport.channel.ChannelType;
import org.kaaproject.kaa.server.transport.message.ErrorBuilder;
import org.kaaproject.kaa.server.transport.message.MessageBuilder;
import org.kaaproject.kaa.server.transport.message.SessionAwareMessage;
import org.kaaproject.kaa.server.transport.message.SessionInitMessage;
import org.kaaproject.kaa.server.transport.session.SessionInfo;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultAkkaServiceTest {
    private static final String LOCAL_NODE_ID = "LOCAL_NODE_ID";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAkkaServiceTest.class);

    private static final String SERVER2 = "SERVER2";

    private static final String FQN1 = "fqn1";

    private static final int ECF1_VERSION = 43;

    private static final String ECF1_ID = "EF1_ID";

    private static final String TOPIC_ID = "TopicId";

    private static final String UNICAST_NOTIFICATION_ID = "UnicastNotificationId";

    private static final int TIMEOUT = 10000;

    private static final String TENANT_ID = "TENANT_ID";

    private static final String USER_ID = "USER_ID";

    private static final String APP_TOKEN = "APP_TOKEN";

    private static final String SDK_TOKEN = "SDK_TOKEN";

    private static final String INVALID_SDK_TOKEN = "INVALID_SDK_TOKEN";

    private static final String APP_ID = "APP_ID";

    private static final String PROFILE_BODY = "ProfileBody";

    private static final int REQUEST_ID = 42;

    private DefaultAkkaService akkaService;

    private AkkaClusterServiceListener clusterServiceListener;

    // mocks
    private ClusterService clusterService;

    private CacheService cacheService;

    private MetricsService metricsService;

    private KeyStoreService operationsKeyStoreService;

    private OperationsService operationsService;

    private NotificationDeltaService notificationDeltaService;

    private ApplicationService applicationService;

    private EventService eventService;

    private ApplicationDto applicationDto;

    private SyncContext simpleResponse;

    private SyncContext noDeltaResponse;

    private SyncContext deltaResponse;

    private SyncContext deltaResponseWithProfile;

    private SyncContext noDeltaResponseWithTopicState;

    private NotificationDto topicNotification;

    private LogAppenderService logAppenderService;

    private EndpointUserService endpointUserService;

    private CtlService ctlService;

    private CredentialsServiceLocator credentialsServiceLocator;

    private CredentialsService credentialsService;

    private RegistrationService registrationService;

    private KeyPair clientPair;

    private KeyPair targetPair;

    private KeyPair serverPair;

    private ByteBuffer clientPublicKey;

    private ByteBuffer clientPublicKeyHash;

    private ByteBuffer targetPublicKeyHash;

    private EndpointProfileDto mockProfile;

    @Test
    public void testAkkaInitialization() {
        Assert.assertNotNull(akkaService.getActorSystem());
    }

    @Test
    public void testDecodeSighnedException() throws Exception {
        SessionInitMessage message = Mockito.mock(SessionInitMessage.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        Mockito.when(message.getChannelContext()).thenReturn(Mockito.mock(ChannelContext.class));
        Mockito.when(message.getErrorBuilder()).thenReturn(errorBuilder);
        Mockito.when(message.getEncodedMessageData()).thenReturn("dummy".getBytes());
        Mockito.when(message.getEncodedSessionKey()).thenReturn("dummy".getBytes());
        Mockito.when(message.getSessionKeySignature()).thenReturn("dummy".getBytes());
        Mockito.when(message.isEncrypted()).thenReturn(true);
        akkaService.process(message);
        Mockito.verify(errorBuilder, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) * 10)).atLeastOnce()).build(Mockito.any(Exception.class));
    }

    @Test
    public void testDecodeSessionException() throws Exception {
        SessionAwareMessage message = Mockito.mock(SessionAwareMessage.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInfo sessionInfo = new SessionInfo(UUID.randomUUID(), Constants.KAA_PLATFORM_PROTOCOL_AVRO_ID, Mockito.mock(ChannelContext.class), ChannelType.ASYNC, Mockito.mock(CipherPair.class), EndpointObjectHash.fromSha1("test"), "applicationToken", "sdkToken", 100, true);
        Mockito.when(message.getChannelContext()).thenReturn(Mockito.mock(ChannelContext.class));
        Mockito.when(message.getErrorBuilder()).thenReturn(errorBuilder);
        Mockito.when(message.getSessionInfo()).thenReturn(sessionInfo);
        Mockito.when(message.getEncodedMessageData()).thenReturn("dummy".getBytes());
        Mockito.when(message.isEncrypted()).thenReturn(true);
        akkaService.process(message);
        Mockito.verify(errorBuilder, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) * 10)).atLeastOnce()).build(Mockito.any(Exception.class));
    }

    @Test
    public void testInvalidSDKTokenException() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.INVALID_SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        request.setSyncRequestMetaData(md);
        ProfileSyncRequest profileSync = new ProfileSyncRequest();
        profileSync.setEndpointPublicKey(clientPublicKey);
        profileSync.setProfileBody(ByteBuffer.wrap(DefaultAkkaServiceTest.PROFILE_BODY.getBytes()));
        request.setProfileSyncRequest(profileSync);
        whenSync(simpleResponse);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(errorBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.isA(InvalidSdkTokenException.class));
    }

    @Test
    public void testEndpointRegistrationRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        request.setSyncRequestMetaData(md);
        ProfileSyncRequest profileSync = new ProfileSyncRequest();
        profileSync.setEndpointPublicKey(clientPublicKey);
        profileSync.setProfileBody(ByteBuffer.wrap(DefaultAkkaServiceTest.PROFILE_BODY.getBytes()));
        request.setProfileSyncRequest(profileSync);
        whenSync(simpleResponse);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) * 10)).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testEndpointUpdateRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        request.setSyncRequestMetaData(md);
        ProfileSyncRequest profileSync = new ProfileSyncRequest();
        profileSync.setProfileBody(ByteBuffer.wrap(DefaultAkkaServiceTest.PROFILE_BODY.getBytes()));
        request.setProfileSyncRequest(profileSync);
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(simpleResponse);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) * 10)).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testSyncRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        request.setSyncRequestMetaData(md);
        SyncContext holder = simpleResponse;
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(holder);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testMultipleSyncRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        request.setSyncRequestMetaData(md);
        SyncContext holder = simpleResponse;
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(holder);
        Assert.assertNotNull(akkaService.getActorSystem());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message1 = toSignedRequest(UUID.randomUUID(), SYNC, channelContextMock, request, responseBuilder, errorBuilder);
        SessionInitMessage message2 = toSignedRequest(UUID.randomUUID(), SYNC, channelContextMock, request, responseBuilder, errorBuilder);
        akkaService.process(message1);
        akkaService.process(message2);
        Mockito.verify(operationsService, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) * 10)).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeast(2)).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testLongSyncRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        md.setTimeout(1000L);
        request.setSyncRequestMetaData(md);
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(noDeltaResponse);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testLongSyncNotification() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = buildSyncRequestMetaData();
        request.setSyncRequestMetaData(md);
        ConfigurationSyncRequest csRequest = new ConfigurationSyncRequest();
        csRequest.setConfigurationHash(ByteBuffer.wrap(new byte[]{  }));
        csRequest.setResyncOnly(true);
        request.setConfigurationSyncRequest(csRequest);
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(noDeltaResponse);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.when(applicationService.findAppById(DefaultAkkaServiceTest.APP_ID)).thenReturn(applicationDto);
        whenSync(deltaResponse);
        Notification thriftNotification = new Notification();
        thriftNotification.setAppId(DefaultAkkaServiceTest.APP_ID);
        akkaService.onNotification(thriftNotification);
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testLongSyncUnicastNotification() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        request.setSyncRequestMetaData(buildSyncRequestMetaData());
        NotificationSyncRequest nfRequest = new NotificationSyncRequest();
        request.setNotificationSyncRequest(nfRequest);
        whenSync(noDeltaResponse);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) / 2)).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.when(operationsService.updateSyncResponse(noDeltaResponse.getResponse(), new ArrayList<NotificationDto>(), DefaultAkkaServiceTest.UNICAST_NOTIFICATION_ID)).thenReturn(noDeltaResponse.getResponse());
        Mockito.when(applicationService.findAppById(DefaultAkkaServiceTest.APP_ID)).thenReturn(applicationDto);
        EndpointAddress address = new EndpointAddress(applicationDto.getTenantId(), applicationDto.getApplicationToken(), EndpointObjectHash.fromBytes(clientPublicKeyHash.array()));
        ActorClassifier classifier = ActorClassifier.GLOBAL;
        // TODO: replace nulls with values
        ThriftUnicastNotificationMessage msg = new ThriftUnicastNotificationMessage(null, null, DefaultAkkaServiceTest.UNICAST_NOTIFICATION_ID);
        clusterServiceListener.onEndpointActorMsg(new org.kaaproject.kaa.server.operations.service.akka.messages.core.route.ThriftEndpointActorMsg<ThriftUnicastNotificationMessage>(address, classifier, msg));
        Mockito.verify(operationsService, Mockito.timeout(((10 * (DefaultAkkaServiceTest.TIMEOUT)) / 2)).atLeastOnce()).updateSyncResponse(noDeltaResponse.getResponse(), new ArrayList<NotificationDto>(), DefaultAkkaServiceTest.UNICAST_NOTIFICATION_ID);
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testLongSyncTopicNotificationOnStart() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        Mockito.when(applicationService.findAppById(DefaultAkkaServiceTest.APP_ID)).thenReturn(applicationDto);
        Notification thriftNotification = new Notification();
        thriftNotification.setAppId(DefaultAkkaServiceTest.APP_ID);
        thriftNotification.setTopicId(DefaultAkkaServiceTest.TOPIC_ID);
        thriftNotification.setNotificationId(DefaultAkkaServiceTest.UNICAST_NOTIFICATION_ID);
        akkaService.onNotification(thriftNotification);
        Mockito.when(notificationDeltaService.findNotificationById(DefaultAkkaServiceTest.UNICAST_NOTIFICATION_ID)).thenReturn(topicNotification);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        request.setSyncRequestMetaData(buildSyncRequestMetaData());
        NotificationSyncRequest nfRequest = new NotificationSyncRequest();
        request.setNotificationSyncRequest(nfRequest);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        whenSync(noDeltaResponseWithTopicState);
        Mockito.when(operationsService.updateSyncResponse(noDeltaResponseWithTopicState.getResponse(), Collections.singletonList(topicNotification), null)).thenReturn(noDeltaResponseWithTopicState.getResponse());
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) / 2)).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(operationsService, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) / 2)).atLeastOnce()).updateSyncResponse(noDeltaResponseWithTopicState.getResponse(), Collections.singletonList(topicNotification), null);
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testLongSyncTopicNotification() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        request.setSyncRequestMetaData(buildSyncRequestMetaData());
        NotificationSyncRequest nfRequest = new NotificationSyncRequest();
        request.setNotificationSyncRequest(nfRequest);
        Mockito.when(applicationService.findAppById(DefaultAkkaServiceTest.APP_ID)).thenReturn(applicationDto);
        Mockito.when(notificationDeltaService.findNotificationById(DefaultAkkaServiceTest.UNICAST_NOTIFICATION_ID)).thenReturn(topicNotification);
        whenSync(noDeltaResponseWithTopicState);
        Mockito.when(operationsService.updateSyncResponse(noDeltaResponseWithTopicState.getResponse(), Collections.singletonList(topicNotification), null)).thenReturn(noDeltaResponseWithTopicState.getResponse());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Thread.sleep(3000);
        Notification thriftNotification = new Notification();
        thriftNotification.setAppId(DefaultAkkaServiceTest.APP_ID);
        thriftNotification.setTopicId(DefaultAkkaServiceTest.TOPIC_ID);
        thriftNotification.setNotificationId(DefaultAkkaServiceTest.UNICAST_NOTIFICATION_ID);
        akkaService.onNotification(thriftNotification);
        Mockito.verify(operationsService, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) / 2)).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(operationsService, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) / 2)).atLeastOnce()).updateSyncResponse(noDeltaResponseWithTopicState.getResponse(), Collections.singletonList(topicNotification), null);
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testRedirect() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        MessageEncoderDecoder crypt = new MessageEncoderDecoder(clientPair.getPrivate(), clientPair.getPublic(), serverPair.getPublic());
        akkaService.onRedirectionRule(new RedirectionRule("testDNS".hashCode(), 123, 1.0, 0.0, 60000));
        Thread.sleep(1000);
        SyncRequest request = new SyncRequest();
        request.setRequestId(32);
        request.setSyncRequestMetaData(buildSyncRequestMetaData());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder, crypt);
        akkaService.process(message);
        SyncResponse response = new SyncResponse();
        response.setRequestId(request.getRequestId());
        response.setStatus(REDIRECT);
        response.setRedirectSyncResponse(new RedirectSyncResponse("testDNS".hashCode()));
        Thread.sleep(((DefaultAkkaServiceTest.TIMEOUT) / 2));
        Mockito.verify(operationsService, Mockito.never()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        AvroByteArrayConverter<SyncResponse> responseConverter = new AvroByteArrayConverter(SyncResponse.class);
        byte[] encodedData = crypt.encodeData(responseConverter.toByteArray(response));
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(encodedData, true);
    }

    @Test
    public void testRedirectSessionRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        MessageEncoderDecoder crypt = new MessageEncoderDecoder(clientPair.getPrivate(), clientPair.getPublic(), serverPair.getPublic());
        akkaService.onRedirectionRule(new RedirectionRule("testDNS".hashCode(), 123, 0.0, 1.0, 60000));
        Thread.sleep(1000);
        SyncRequest request = new SyncRequest();
        request.setRequestId(32);
        request.setSyncRequestMetaData(buildSyncRequestMetaData());
        final MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        final ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        AvroByteArrayConverter<SyncRequest> requestConverter = new AvroByteArrayConverter(SyncRequest.class);
        final org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.SyncRequest kaaSync = new org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.SyncRequest(crypt.encodeData(requestConverter.toByteArray(request)), false, true);
        final SessionInfo session = new SessionInfo(UUID.randomUUID(), Constants.KAA_PLATFORM_PROTOCOL_AVRO_ID, channelContextMock, ChannelType.ASYNC, crypt.getSessionCipherPair(), EndpointObjectHash.fromBytes(clientPublicKey.array()), DefaultAkkaServiceTest.APP_TOKEN, DefaultAkkaServiceTest.SDK_TOKEN, 100, true);
        SessionAwareMessage message = new SessionAwareMessage() {
            @Override
            public SessionInfo getSessionInfo() {
                return session;
            }

            @Override
            public int getPlatformId() {
                return session.getPlatformId();
            }

            @Override
            public UUID getChannelUuid() {
                return session.getUuid();
            }

            @Override
            public ChannelType getChannelType() {
                return session.getChannelType();
            }

            @Override
            public ChannelContext getChannelContext() {
                return session.getCtx();
            }

            @Override
            public boolean isEncrypted() {
                return session.isEncrypted();
            }

            @Override
            public MessageBuilder getMessageBuilder() {
                return responseBuilder;
            }

            @Override
            public ErrorBuilder getErrorBuilder() {
                return errorBuilder;
            }

            @Override
            public byte[] getEncodedMessageData() {
                return kaaSync.getAvroObject();
            }
        };
        akkaService.process(message);
        SyncResponse response = new SyncResponse();
        response.setRequestId(request.getRequestId());
        response.setStatus(REDIRECT);
        response.setRedirectSyncResponse(new RedirectSyncResponse("testDNS".hashCode()));
        Thread.sleep(((DefaultAkkaServiceTest.TIMEOUT) / 2));
        Mockito.verify(operationsService, Mockito.never()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        AvroByteArrayConverter<SyncResponse> responseConverter = new AvroByteArrayConverter(SyncResponse.class);
        byte[] encodedData = crypt.encodeData(responseConverter.toByteArray(response));
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(encodedData, true);
    }

    @Test
    public void testRedirectExpire() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        MessageEncoderDecoder crypt = new MessageEncoderDecoder(clientPair.getPrivate(), clientPair.getPublic(), serverPair.getPublic());
        akkaService.onRedirectionRule(new RedirectionRule("testDNS".hashCode(), 123, 1.0, 1.0, 1000));
        Thread.sleep(2000);
        SyncRequest request = new SyncRequest();
        request.setRequestId(32);
        request.setSyncRequestMetaData(buildSyncRequestMetaData());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder, crypt);
        whenSync(noDeltaResponseWithTopicState);
        akkaService.process(message);
        SyncResponse response = new SyncResponse();
        response.setRequestId(request.getRequestId());
        response.setStatus(REDIRECT);
        response.setRedirectSyncResponse(new RedirectSyncResponse("testDNS".hashCode()));
        Mockito.verify(operationsService, Mockito.timeout(((DefaultAkkaServiceTest.TIMEOUT) / 2)).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
    }

    @Test
    public void testEndpointEventBasic() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        EndpointProfileDto sourceProfileMock = Mockito.mock(EndpointProfileDto.class);
        EndpointProfileDto targetProfileMock = Mockito.mock(EndpointProfileDto.class);
        EventClassFamilyVersionStateDto ecfVdto = new EventClassFamilyVersionStateDto();
        ecfVdto.setEcfId(DefaultAkkaServiceTest.ECF1_ID);
        ecfVdto.setVersion(DefaultAkkaServiceTest.ECF1_VERSION);
        Event event = new Event(0, DefaultAkkaServiceTest.FQN1, ByteBuffer.wrap(new byte[0]), Base64Util.encode(clientPublicKeyHash.array()), null);
        SyncRequest sourceRequest = new SyncRequest();
        sourceRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        md.setTimeout(((DefaultAkkaServiceTest.TIMEOUT) * 1L));
        sourceRequest.setSyncRequestMetaData(md);
        EventSyncRequest eventRequest = new EventSyncRequest();
        eventRequest.setEvents(Arrays.asList(event));
        sourceRequest.setEventSyncRequest(eventRequest);
        ServerSync sourceResponse = new ServerSync();
        sourceResponse.setStatus(SUCCESS);
        SyncContext sourceResponseHolder = new SyncContext(sourceResponse);
        sourceResponseHolder.setNotificationVersion(sourceProfileMock);
        SyncRequest targetRequest = new SyncRequest();
        targetRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(targetPublicKeyHash);
        md.setProfileHash(targetPublicKeyHash);
        md.setTimeout(((DefaultAkkaServiceTest.TIMEOUT) * 1L));
        targetRequest.setSyncRequestMetaData(md);
        targetRequest.setEventSyncRequest(new EventSyncRequest());
        ServerSync targetResponse = new ServerSync();
        targetResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetResponse.setStatus(SUCCESS);
        SyncContext targetResponseHolder = new SyncContext(targetResponse);
        targetResponseHolder.setNotificationVersion(targetProfileMock);
        whenSync(AvroEncDec.convert(sourceRequest), sourceResponseHolder);
        whenSync(AvroEncDec.convert(targetRequest), targetResponseHolder);
        Mockito.when(sourceProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(sourceProfileMock.getEndpointKeyHash()).thenReturn(clientPublicKeyHash.array());
        Mockito.when(sourceProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        Mockito.when(targetProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(targetProfileMock.getEndpointKeyHash()).thenReturn(targetPublicKeyHash.array());
        Mockito.when(targetProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        Mockito.when(cacheService.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1))).thenReturn(DefaultAkkaServiceTest.ECF1_ID);
        RouteTableKey routeKey = new RouteTableKey(DefaultAkkaServiceTest.APP_TOKEN, new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION));
        Mockito.when(cacheService.getRouteKeys(new EventClassFqnVersion(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1, DefaultAkkaServiceTest.ECF1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Assert.assertNotNull(akkaService.getActorSystem());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        MessageEncoderDecoder sourceCrypt = new MessageEncoderDecoder(clientPair.getPrivate(), clientPair.getPublic(), serverPair.getPublic());
        SessionInitMessage sourceMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, sourceRequest, responseBuilder, errorBuilder, sourceCrypt);
        akkaService.process(sourceMessage);
        // sourceRequest
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        MessageEncoderDecoder targetCrypt = new MessageEncoderDecoder(targetPair.getPrivate(), targetPair.getPublic(), serverPair.getPublic());
        SessionInitMessage targetMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, targetRequest, responseBuilder, errorBuilder, targetCrypt);
        akkaService.process(targetMessage);
        // targetRequest
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        SyncResponse eventResponse = new SyncResponse();
        eventResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        eventResponse.setStatus(SyncResponseResultType.SUCCESS);
        eventResponse.setEventSyncResponse(new EventSyncResponse());
        eventResponse.getEventSyncResponse().setEvents(Arrays.asList(event));
        AvroByteArrayConverter<SyncResponse> responseConverter = new AvroByteArrayConverter(SyncResponse.class);
        byte[] response = responseConverter.toByteArray(eventResponse);
        byte[] encodedData = targetCrypt.encodeData(response);
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(encodedData, true);
    }

    @Test
    public void testEndpointNotAttachedEvent() throws Exception {
        AkkaContext context = Mockito.mock(AkkaContext.class);
        ActorContext actorCtxMock = Mockito.mock(ActorContext.class);
        ActorRef actorMock = Mockito.spy(ActorRef.class);
        ActorSystem system = ActorSystem.create();
        try {
            final Props props = Props.create(DefaultAkkaServiceTest.TestActor.class);
            final TestActorRef<DefaultAkkaServiceTest.TestActor> parentMock = TestActorRef.create(system, props, "testA");
            Mockito.when(actorCtxMock.self()).thenReturn(actorMock);
            Mockito.when(actorCtxMock.parent()).thenReturn(parentMock);
            EndpointEventReceiveMessage msg = Mockito.mock(EndpointEventReceiveMessage.class);
            LocalEndpointActorMessageProcessor processor = Mockito.spy(new LocalEndpointActorMessageProcessor(context, DefaultAkkaServiceTest.APP_TOKEN, EndpointObjectHash.fromSha1(clientPublicKeyHash.array()), "ACTOR_TOKEN"));
            processor.processEndpointEventReceiveMessage(actorCtxMock, msg);
            Assert.assertEquals(FAILURE, getStatus());
        } finally {
            JavaTestKit.shutdownActorSystem(system);
        }
    }

    @Test
    public void testEndpointEventSeqNumberBasic() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        EndpointProfileDto sourceProfileMock = Mockito.mock(EndpointProfileDto.class);
        EventClassFamilyVersionStateDto ecfVdto = new EventClassFamilyVersionStateDto();
        ecfVdto.setEcfId(DefaultAkkaServiceTest.ECF1_ID);
        ecfVdto.setVersion(DefaultAkkaServiceTest.ECF1_VERSION);
        SyncRequest sourceRequest = new SyncRequest();
        sourceRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        sourceRequest.setSyncRequestMetaData(buildSyncRequestMetaData());
        EventSyncRequest eventRequest = new EventSyncRequest();
        eventRequest.setEventSequenceNumberRequest(new EventSequenceNumberRequest());
        sourceRequest.setEventSyncRequest(eventRequest);
        ServerSync sourceResponse = new ServerSync();
        sourceResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        sourceResponse.setStatus(SUCCESS);
        SyncContext sourceResponseHolder = new SyncContext(sourceResponse);
        sourceResponseHolder.setNotificationVersion(sourceProfileMock);
        whenSync(AvroEncDec.convert(sourceRequest), sourceResponseHolder);
        Mockito.when(sourceProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(sourceProfileMock.getEndpointKeyHash()).thenReturn(clientPublicKeyHash.array());
        Mockito.when(sourceProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        Mockito.when(cacheService.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1))).thenReturn(DefaultAkkaServiceTest.ECF1_ID);
        RouteTableKey routeKey = new RouteTableKey(DefaultAkkaServiceTest.APP_TOKEN, new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION));
        Mockito.when(cacheService.getRouteKeys(new EventClassFqnVersion(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1, DefaultAkkaServiceTest.ECF1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Assert.assertNotNull(akkaService.getActorSystem());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        MessageEncoderDecoder sourceCrypt = new MessageEncoderDecoder(clientPair.getPrivate(), clientPair.getPublic(), serverPair.getPublic());
        SessionInitMessage sourceMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, sourceRequest, responseBuilder, errorBuilder, sourceCrypt);
        akkaService.process(sourceMessage);
        // sourceRequest
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        SyncResponse eventResponse = new SyncResponse();
        eventResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        eventResponse.setStatus(SyncResponseResultType.SUCCESS);
        eventResponse.setEventSyncResponse(new EventSyncResponse());
        eventResponse.getEventSyncResponse().setEventSequenceNumberResponse(new EventSequenceNumberResponse(0));
        AvroByteArrayConverter<SyncResponse> responseConverter = new AvroByteArrayConverter(SyncResponse.class);
        byte[] response = responseConverter.toByteArray(eventResponse);
        DefaultAkkaServiceTest.LOG.trace("Response to compare {}", Arrays.toString(response));
        byte[] encodedData = sourceCrypt.encodeData(response);
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(encodedData, true);
    }

    @Test
    public void testRemoteIncomingEndpointEventBasic() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        EndpointProfileDto targetProfileMock = Mockito.mock(EndpointProfileDto.class);
        EventClassFamilyVersionStateDto ecfVdto = new EventClassFamilyVersionStateDto();
        ecfVdto.setEcfId(DefaultAkkaServiceTest.ECF1_ID);
        ecfVdto.setVersion(DefaultAkkaServiceTest.ECF1_VERSION);
        SyncRequest targetRequest = new SyncRequest();
        targetRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetRequest.setSyncRequestMetaData(buildSyncRequestMetaData(targetPublicKeyHash));
        targetRequest.setEventSyncRequest(new EventSyncRequest());
        ServerSync targetResponse = new ServerSync();
        targetResponse.setStatus(SUCCESS);
        SyncContext targetResponseHolder = new SyncContext(targetResponse);
        targetResponseHolder.setNotificationVersion(targetProfileMock);
        whenSync(AvroEncDec.convert(targetRequest), targetResponseHolder);
        Mockito.when(targetProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(targetProfileMock.getEndpointKeyHash()).thenReturn(targetPublicKeyHash.array());
        Mockito.when(targetProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        Mockito.when(cacheService.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1))).thenReturn(DefaultAkkaServiceTest.ECF1_ID);
        RouteTableKey routeKey = new RouteTableKey(DefaultAkkaServiceTest.APP_TOKEN, new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION));
        Mockito.when(cacheService.getRouteKeys(new EventClassFqnVersion(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1, DefaultAkkaServiceTest.ECF1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Assert.assertNotNull(akkaService.getActorSystem());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        MessageEncoderDecoder targetCrypt = new MessageEncoderDecoder(targetPair.getPrivate(), targetPair.getPublic(), serverPair.getPublic());
        SessionInitMessage targetMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, targetRequest, responseBuilder, errorBuilder, targetCrypt);
        akkaService.process(targetMessage);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        org.kaaproject.kaa.server.sync.Event event = new org.kaaproject.kaa.server.sync.Event(0, DefaultAkkaServiceTest.FQN1, ByteBuffer.wrap(new byte[0]), null, null);
        EndpointEvent endpointEvent = new EndpointEvent(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()), event, UUID.randomUUID(), System.currentTimeMillis(), DefaultAkkaServiceTest.ECF1_VERSION);
        RemoteEndpointEvent remoteEvent = new RemoteEndpointEvent(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID, endpointEvent, new RouteTableAddress(EndpointObjectHash.fromBytes(targetPublicKeyHash.array()), DefaultAkkaServiceTest.APP_TOKEN, "SERVER1"));
        akkaService.getListener().onEvent(remoteEvent);
        event = new org.kaaproject.kaa.server.sync.Event(0, DefaultAkkaServiceTest.FQN1, ByteBuffer.wrap(new byte[0]), null, Base64Util.encode(targetPublicKeyHash.array()));
        ServerSync eventResponse = new ServerSync();
        eventResponse.setStatus(SUCCESS);
        eventResponse.setEventSync(new EventServerSync());
        eventResponse.getEventSync().setEvents(Arrays.asList(event));
        AvroByteArrayConverter<SyncResponse> responseConverter = new AvroByteArrayConverter(SyncResponse.class);
        byte[] response = responseConverter.toByteArray(AvroEncDec.convert(eventResponse));
        byte[] encodedData = targetCrypt.encodeData(response);
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(encodedData, true);
    }

    @Test
    public void testRemoteOutcomingEndpointEventBasic() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        EndpointProfileDto sourceProfileMock = Mockito.mock(EndpointProfileDto.class);
        EventClassFamilyVersionStateDto ecfVdto = new EventClassFamilyVersionStateDto();
        ecfVdto.setEcfId(DefaultAkkaServiceTest.ECF1_ID);
        ecfVdto.setVersion(DefaultAkkaServiceTest.ECF1_VERSION);
        Event event = new Event(0, DefaultAkkaServiceTest.FQN1, ByteBuffer.wrap(new byte[0]), Base64Util.encode(clientPublicKeyHash.array()), null);
        SyncRequest sourceRequest = new SyncRequest();
        sourceRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        sourceRequest.setSyncRequestMetaData(buildSyncRequestMetaData(clientPublicKeyHash));
        EventSyncRequest eventRequest = new EventSyncRequest();
        eventRequest.setEvents(Arrays.asList(event));
        sourceRequest.setEventSyncRequest(eventRequest);
        ServerSync sourceResponse = new ServerSync();
        sourceResponse.setStatus(SUCCESS);
        SyncContext sourceResponseHolder = new SyncContext(sourceResponse);
        sourceResponseHolder.setNotificationVersion(sourceProfileMock);
        whenSync(AvroEncDec.convert(sourceRequest), sourceResponseHolder);
        Mockito.when(sourceProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(sourceProfileMock.getEndpointKeyHash()).thenReturn(clientPublicKeyHash.array());
        Mockito.when(sourceProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        Mockito.when(cacheService.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1))).thenReturn(DefaultAkkaServiceTest.ECF1_ID);
        RouteTableKey routeKey = new RouteTableKey(DefaultAkkaServiceTest.APP_TOKEN, new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION));
        Mockito.when(cacheService.getRouteKeys(new EventClassFqnVersion(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1, DefaultAkkaServiceTest.ECF1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Assert.assertNotNull(akkaService.getActorSystem());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        MessageEncoderDecoder sourceCrypt = new MessageEncoderDecoder(clientPair.getPrivate(), clientPair.getPublic(), serverPair.getPublic());
        SessionInitMessage sourceMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, sourceRequest, responseBuilder, errorBuilder, sourceCrypt);
        akkaService.process(sourceMessage);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        UserRouteInfo userRouteInfo = new UserRouteInfo(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID, DefaultAkkaServiceTest.SERVER2, RouteOperation.ADD);
        akkaService.getListener().onUserRouteInfo(userRouteInfo);
        RouteTableAddress remoteAddress = new RouteTableAddress(EndpointObjectHash.fromBytes(targetPublicKeyHash.array()), DefaultAkkaServiceTest.APP_TOKEN, DefaultAkkaServiceTest.SERVER2);
        RouteInfo routeInfo = new RouteInfo(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID, remoteAddress, Arrays.asList(new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION)));
        TimeUnit.SECONDS.sleep(2);
        akkaService.getListener().onRouteInfo(routeInfo);
        Mockito.verify(eventService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).sendEvent(Mockito.any(RemoteEndpointEvent.class));
    }

    @Test
    public void testLogSyncRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        md.setTimeout(1000L);
        request.setSyncRequestMetaData(md);
        LogSyncRequest logRequest = new LogSyncRequest(DefaultAkkaServiceTest.REQUEST_ID, Collections.singletonList(new LogEntry(ByteBuffer.wrap("String".getBytes()))));
        request.setLogSyncRequest(logRequest);
        whenSync(noDeltaResponseWithTopicState);
        LogAppender mockAppender = Mockito.mock(LogAppender.class);
        Mockito.when(logAppenderService.getApplicationAppenders(DefaultAkkaServiceTest.APP_ID)).thenReturn(Collections.singletonList(mockAppender));
        Mockito.when(logAppenderService.getLogSchema(Mockito.anyString(), Mockito.anyInt())).thenReturn(new org.kaaproject.kaa.server.common.log.shared.appender.LogSchema(new LogSchemaDto(), ""));
        EndpointProfileSchemaDto profileSchemaDto = new EndpointProfileSchemaDto();
        profileSchemaDto.setId("1");
        profileSchemaDto.setCtlSchemaId("22");
        CTLSchemaDto ctlSchema = new CTLSchemaDto();
        ctlSchema.setId("22");
        Mockito.when(cacheService.getProfileSchemaByAppAndVersion(new AppVersionKey(DefaultAkkaServiceTest.APP_TOKEN, 0))).thenReturn(profileSchemaDto);
        Mockito.when(cacheService.getCtlSchemaById("22")).thenReturn(ctlSchema);
        Mockito.when(ctlService.flatExportAsString(ctlSchema)).thenReturn("ClientProfileSchema");
        ServerProfileSchemaDto serverProfileSchemaDto = new ServerProfileSchemaDto();
        serverProfileSchemaDto.setId("1");
        serverProfileSchemaDto.setCtlSchemaId("23");
        CTLSchemaDto serverCtlSchema = new CTLSchemaDto();
        serverCtlSchema.setId("23");
        Mockito.when(cacheService.getServerProfileSchemaByAppAndVersion(new AppVersionKey(DefaultAkkaServiceTest.APP_TOKEN, 0))).thenReturn(serverProfileSchemaDto);
        Mockito.when(cacheService.getCtlSchemaById("23")).thenReturn(serverCtlSchema);
        Mockito.when(ctlService.flatExportAsString(serverCtlSchema)).thenReturn("ServerProfileSchema");
        Mockito.when(mockAppender.isSchemaVersionSupported(Mockito.anyInt())).thenReturn(true);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(logAppenderService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).getLogSchema(DefaultAkkaServiceTest.APP_ID, 44);
        Mockito.verify(mockAppender, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).doAppend(Mockito.any(BaseLogEventPack.class), Mockito.any(LogDeliveryCallback.class));
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(byte[].class), Mockito.any(boolean.class));
    }

    @Test
    public void testUserChange() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        MessageEncoderDecoder targetCrypt = new MessageEncoderDecoder(targetPair.getPrivate(), targetPair.getPublic(), serverPair.getPublic());
        EndpointProfileDto targetProfileMock = Mockito.mock(EndpointProfileDto.class);
        EventClassFamilyVersionStateDto ecfVdto = new EventClassFamilyVersionStateDto();
        ecfVdto.setEcfId(DefaultAkkaServiceTest.ECF1_ID);
        ecfVdto.setVersion(DefaultAkkaServiceTest.ECF1_VERSION);
        SyncRequest targetRequest = new SyncRequest();
        targetRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetRequest.setSyncRequestMetaData(buildSyncRequestMetaData(targetPublicKeyHash));
        targetRequest.setEventSyncRequest(new EventSyncRequest());
        ServerSync targetResponse = new ServerSync();
        targetResponse.setStatus(SUCCESS);
        SyncContext targetResponseHolder = new SyncContext(targetResponse);
        targetResponseHolder.setNotificationVersion(targetProfileMock);
        whenSync(AvroEncDec.convert(targetRequest), targetResponseHolder);
        Mockito.when(targetProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(targetProfileMock.getEndpointKeyHash()).thenReturn(targetPublicKeyHash.array());
        Mockito.when(targetProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        Mockito.when(cacheService.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1))).thenReturn(DefaultAkkaServiceTest.ECF1_ID);
        RouteTableKey routeKey = new RouteTableKey(DefaultAkkaServiceTest.APP_TOKEN, new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION));
        Mockito.when(cacheService.getRouteKeys(new EventClassFqnVersion(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1, DefaultAkkaServiceTest.ECF1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Assert.assertNotNull(akkaService.getActorSystem());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, targetRequest, responseBuilder, errorBuilder, targetCrypt);
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(eventService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).sendUserRouteInfo(new UserRouteInfo(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID));
        UserRouteInfo userRouteInfo = new UserRouteInfo(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID, DefaultAkkaServiceTest.SERVER2, RouteOperation.ADD);
        akkaService.getListener().onUserRouteInfo(userRouteInfo);
        TimeUnit.SECONDS.sleep(2);
        RouteTableAddress remoteAddress = new RouteTableAddress(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()), DefaultAkkaServiceTest.APP_TOKEN, DefaultAkkaServiceTest.SERVER2);
        RouteInfo remoteRouteInfo = new RouteInfo(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID, remoteAddress, Arrays.asList(new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION)));
        TimeUnit.SECONDS.sleep(2);
        akkaService.getListener().onRouteInfo(remoteRouteInfo);
        RouteTableAddress localAddress = new RouteTableAddress(EndpointObjectHash.fromBytes(targetPublicKeyHash.array()), DefaultAkkaServiceTest.APP_TOKEN, null);
        RouteInfo localRouteInfo = new RouteInfo(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID, localAddress, Arrays.asList(new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION)));
        Mockito.verify(eventService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).sendRouteInfo(Collections.singletonList(localRouteInfo), DefaultAkkaServiceTest.SERVER2);
        targetRequest = new SyncRequest();
        targetRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetRequest.setSyncRequestMetaData(buildSyncRequestMetaData(targetPublicKeyHash));
        targetResponse = new ServerSync();
        targetResponse.setStatus(SUCCESS);
        targetResponseHolder = new SyncContext(targetResponse);
        targetResponseHolder.setNotificationVersion(targetProfileMock);
        Mockito.when(targetProfileMock.getEndpointUserId()).thenReturn(((DefaultAkkaServiceTest.USER_ID) + "2"));
        whenSync(AvroEncDec.convert(targetRequest), targetResponseHolder);
        SessionInitMessage targetMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, targetRequest, responseBuilder, errorBuilder, targetCrypt);
        akkaService.process(targetMessage);
        Mockito.verify(eventService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).sendUserRouteInfo(new UserRouteInfo(DefaultAkkaServiceTest.TENANT_ID, ((DefaultAkkaServiceTest.USER_ID) + "2")));
        Mockito.verify(eventService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).sendRouteInfo(RouteInfo.deleteRouteFromAddress(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID, localAddress), DefaultAkkaServiceTest.SERVER2);
    }

    @Test
    public void testEndpointAttach() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        MessageEncoderDecoder targetCrypt = new MessageEncoderDecoder(targetPair.getPrivate(), targetPair.getPublic(), serverPair.getPublic());
        EndpointProfileDto targetProfileMock = Mockito.mock(EndpointProfileDto.class);
        EventClassFamilyVersionStateDto ecfVdto = new EventClassFamilyVersionStateDto();
        ecfVdto.setEcfId(DefaultAkkaServiceTest.ECF1_ID);
        ecfVdto.setVersion(DefaultAkkaServiceTest.ECF1_VERSION);
        SyncRequest targetRequest = new SyncRequest();
        targetRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetRequest.setSyncRequestMetaData(buildSyncRequestMetaData(targetPublicKeyHash));
        targetRequest.setEventSyncRequest(new EventSyncRequest());
        ServerSync targetResponse = new ServerSync();
        targetResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetResponse.setStatus(SUCCESS);
        targetResponse.setUserSync(new UserServerSync());
        SyncContext targetResponseHolder = new SyncContext(targetResponse);
        targetResponseHolder.setNotificationVersion(targetProfileMock);
        whenSync(AvroEncDec.convert(targetRequest), targetResponseHolder);
        Mockito.when(targetProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(targetProfileMock.getEndpointKeyHash()).thenReturn(targetPublicKeyHash.array());
        Mockito.when(targetProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        Mockito.when(cacheService.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1))).thenReturn(DefaultAkkaServiceTest.ECF1_ID);
        RouteTableKey routeKey = new RouteTableKey(DefaultAkkaServiceTest.APP_TOKEN, new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION));
        Mockito.when(cacheService.getRouteKeys(new EventClassFqnVersion(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1, DefaultAkkaServiceTest.ECF1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Assert.assertNotNull(akkaService.getActorSystem());
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage targetMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, targetRequest, responseBuilder, errorBuilder, targetCrypt);
        akkaService.process(targetMessage);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(eventService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).sendUserRouteInfo(new UserRouteInfo(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID));
        EndpointProfileDto sourceProfileMock = Mockito.mock(EndpointProfileDto.class);
        SyncRequest sourceRequest = new SyncRequest();
        sourceRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        sourceRequest.setSyncRequestMetaData(buildSyncRequestMetaData(clientPublicKeyHash));
        sourceRequest.setEventSyncRequest(new EventSyncRequest());
        UserSyncRequest userSyncRequest = new UserSyncRequest();
        EndpointAttachRequest eaRequest = new EndpointAttachRequest(DefaultAkkaServiceTest.REQUEST_ID, "token");
        userSyncRequest.setEndpointAttachRequests(Collections.singletonList(eaRequest));
        sourceRequest.setUserSyncRequest(userSyncRequest);
        ServerSync sourceResponse = new ServerSync();
        sourceResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        sourceResponse.setStatus(SUCCESS);
        UserServerSync userSyncResponse = new UserServerSync();
        userSyncResponse.setEndpointAttachResponses(Collections.singletonList(new org.kaaproject.kaa.server.sync.EndpointAttachResponse(DefaultAkkaServiceTest.REQUEST_ID, Base64Util.encode(targetPublicKeyHash.array()), SyncStatus.SUCCESS)));
        sourceResponse.setUserSync(userSyncResponse);
        SyncContext sourceResponseHolder = new SyncContext(sourceResponse);
        sourceResponseHolder.setNotificationVersion(sourceProfileMock);
        whenSync(AvroEncDec.convert(sourceRequest), sourceResponseHolder);
        Mockito.when(sourceProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(sourceProfileMock.getEndpointKeyHash()).thenReturn(clientPublicKeyHash.array());
        Mockito.when(sourceProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        MessageBuilder sourceResponseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder sourceErrorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage sourceMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, sourceRequest, sourceResponseBuilder, sourceErrorBuilder);
        akkaService.process(sourceMessage);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        SyncResponse targetSyncResponse = new SyncResponse();
        targetSyncResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetSyncResponse.setStatus(SyncResponseResultType.SUCCESS);
        targetSyncResponse.setUserSyncResponse(new UserSyncResponse());
        targetSyncResponse.getUserSyncResponse().setUserAttachNotification(new org.kaaproject.kaa.common.endpoint.gen.UserAttachNotification(DefaultAkkaServiceTest.USER_ID, Base64Util.encode(clientPublicKeyHash.array())));
        AvroByteArrayConverter<SyncResponse> responseConverter = new AvroByteArrayConverter(SyncResponse.class);
        byte[] response = responseConverter.toByteArray(targetSyncResponse);
        DefaultAkkaServiceTest.LOG.trace("Expected response {}", Arrays.toString(response));
        byte[] encodedData = targetCrypt.encodeData(response);
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(encodedData, true);
    }

    @Test
    public void testEndpointDetach() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        MessageEncoderDecoder targetCrypt = new MessageEncoderDecoder(targetPair.getPrivate(), targetPair.getPublic(), serverPair.getPublic());
        EndpointProfileDto targetProfileMock = Mockito.mock(EndpointProfileDto.class);
        EventClassFamilyVersionStateDto ecfVdto = new EventClassFamilyVersionStateDto();
        ecfVdto.setEcfId(DefaultAkkaServiceTest.ECF1_ID);
        ecfVdto.setVersion(DefaultAkkaServiceTest.ECF1_VERSION);
        SyncRequest targetRequest = new SyncRequest();
        targetRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetRequest.setSyncRequestMetaData(buildSyncRequestMetaData(targetPublicKeyHash));
        targetRequest.setEventSyncRequest(new EventSyncRequest());
        ServerSync targetResponse = new ServerSync();
        targetResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetResponse.setStatus(SUCCESS);
        targetResponse.setUserSync(new UserServerSync());
        SyncContext targetResponseHolder = new SyncContext(targetResponse);
        targetResponseHolder.setNotificationVersion(targetProfileMock);
        whenSync(AvroEncDec.convert(targetRequest), targetResponseHolder);
        Mockito.when(targetProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(targetProfileMock.getEndpointKeyHash()).thenReturn(targetPublicKeyHash.array());
        Mockito.when(targetProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        Mockito.when(cacheService.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1))).thenReturn(DefaultAkkaServiceTest.ECF1_ID);
        RouteTableKey routeKey = new RouteTableKey(DefaultAkkaServiceTest.APP_TOKEN, new EventClassFamilyVersion(DefaultAkkaServiceTest.ECF1_ID, DefaultAkkaServiceTest.ECF1_VERSION));
        Mockito.when(cacheService.getRouteKeys(new EventClassFqnVersion(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.FQN1, DefaultAkkaServiceTest.ECF1_VERSION))).thenReturn(Collections.singleton(routeKey));
        Assert.assertNotNull(akkaService.getActorSystem());
        MessageBuilder targetResponseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder targetErrorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage targetMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, targetRequest, targetResponseBuilder, targetErrorBuilder, targetCrypt);
        akkaService.process(targetMessage);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.verify(eventService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).sendUserRouteInfo(new UserRouteInfo(DefaultAkkaServiceTest.TENANT_ID, DefaultAkkaServiceTest.USER_ID));
        EndpointProfileDto sourceProfileMock = Mockito.mock(EndpointProfileDto.class);
        SyncRequest sourceRequest = new SyncRequest();
        sourceRequest.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        sourceRequest.setSyncRequestMetaData(buildSyncRequestMetaData(clientPublicKeyHash));
        sourceRequest.setEventSyncRequest(new EventSyncRequest());
        UserSyncRequest userSyncRequest = new UserSyncRequest();
        EndpointDetachRequest eaRequest = new EndpointDetachRequest(DefaultAkkaServiceTest.REQUEST_ID, Base64Util.encode(targetPublicKeyHash.array()));
        userSyncRequest.setEndpointDetachRequests(Collections.singletonList(eaRequest));
        sourceRequest.setUserSyncRequest(userSyncRequest);
        ServerSync sourceResponse = new ServerSync();
        sourceResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        sourceResponse.setStatus(SUCCESS);
        UserServerSync userSyncResponse = new UserServerSync();
        userSyncResponse.setEndpointDetachResponses(Collections.singletonList(new org.kaaproject.kaa.server.sync.EndpointDetachResponse(DefaultAkkaServiceTest.REQUEST_ID, SyncStatus.SUCCESS)));
        sourceResponse.setUserSync(userSyncResponse);
        SyncContext sourceResponseHolder = new SyncContext(sourceResponse);
        sourceResponseHolder.setNotificationVersion(sourceProfileMock);
        whenSync(AvroEncDec.convert(sourceRequest), sourceResponseHolder);
        Mockito.when(sourceProfileMock.getEndpointUserId()).thenReturn(DefaultAkkaServiceTest.USER_ID);
        Mockito.when(sourceProfileMock.getEndpointKeyHash()).thenReturn(clientPublicKeyHash.array());
        Mockito.when(sourceProfileMock.getEcfVersionStates()).thenReturn(Arrays.asList(ecfVdto));
        MessageBuilder sourceResponseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder sourceErrorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage sourceMessage = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, sourceRequest, sourceResponseBuilder, sourceErrorBuilder);
        akkaService.process(sourceMessage);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        SyncResponse targetSyncResponse = new SyncResponse();
        targetSyncResponse.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        targetSyncResponse.setStatus(SyncResponseResultType.SUCCESS);
        targetSyncResponse.setUserSyncResponse(new UserSyncResponse());
        targetSyncResponse.getUserSyncResponse().setUserDetachNotification(new org.kaaproject.kaa.common.endpoint.gen.UserDetachNotification(Base64Util.encode(clientPublicKeyHash.array())));
        AvroByteArrayConverter<SyncResponse> responseConverter = new AvroByteArrayConverter(SyncResponse.class);
        byte[] response = responseConverter.toByteArray(targetSyncResponse);
        byte[] encodedData = targetCrypt.encodeData(response);
        Mockito.verify(targetResponseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(encodedData, true);
    }

    @Test
    public void testNoEndpointCredentialsSyncRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        request.setSyncRequestMetaData(md);
        SyncContext holder = simpleResponse;
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(holder);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        Mockito.when(registrationService.findEndpointRegistrationByCredentialsId(Mockito.anyString())).thenReturn(Optional.ofNullable(((EndpointRegistrationDto) (null))));
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(errorBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(EndpointVerificationException.class));
    }

    // TODO: fix when server profile feature will be ready
    // @Test
    // public void testServerProfileUpdate() throws Exception {
    // ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
    // 
    // SyncRequest request = new SyncRequest();
    // request.setRequestId(REQUEST_ID);
    // SyncRequestMetaData md = buildSyncRequestMetaData();
    // request.setSyncRequestMetaData(md);
    // 
    // ConfigurationSyncRequest csRequest = new ConfigurationSyncRequest();
    // request.setConfigurationSyncRequest(csRequest);
    // 
    // Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(
    // clientPair.getPublic());
    // whenSync(deltaResponseWithProfile);
    // 
    // MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
    // ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
    // 
    // SessionInitMessage message = toSignedRequest(UUID.randomUUID(),
    // ChannelType.SYNC_WITH_TIMEOUT, channelContextMock, request,
    // responseBuilder, errorBuilder);
    // Assert.assertNotNull(akkaService.getActorSystem());
    // akkaService.process(message);
    // 
    // Mockito.verify(operationsService,
    // Mockito.timeout(TIMEOUT).atLeastOnce()).syncProfile(Mockito.any(SyncContext.class),
    // Mockito.any(ProfileClientSync.class));
    // 
    // Notification thriftNotification = new Notification();
    // thriftNotification.setAppId(APP_ID);
    // thriftNotification.setOp(Operation.UPDATE_SERVER_PROFILE);
    // thriftNotification.setKeyHash(clientPublicKeyHash);
    // akkaService.onNotification(thriftNotification);
    // 
    // Mockito.verify(operationsService,
    // Mockito.timeout(TIMEOUT*100).atLeastOnce())
    // .refreshServerEndpointProfile(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()));
    // }
    // TODO: Implement tests that cover endpoint verification logic.
    @Test
    public void testRevokedEndpointCredentialsSyncRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        request.setSyncRequestMetaData(md);
        SyncContext holder = simpleResponse;
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(holder);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        Mockito.when(registrationService.findEndpointRegistrationByCredentialsId(Mockito.anyString())).thenReturn(Optional.ofNullable(((EndpointRegistrationDto) (null))));
        Mockito.when(credentialsService.lookupCredentials(Mockito.anyString())).thenReturn(Optional.of(new org.kaaproject.kaa.common.dto.credentials.CredentialsDto(new byte[]{  }, CredentialsStatus.REVOKED)));
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(errorBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(EndpointVerificationException.class));
    }

    @Test
    public void testInUseEndpointCredentialsSyncRequest() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = new SyncRequestMetaData();
        md.setSdkToken(DefaultAkkaServiceTest.SDK_TOKEN);
        md.setEndpointPublicKeyHash(clientPublicKeyHash);
        md.setProfileHash(clientPublicKeyHash);
        request.setSyncRequestMetaData(md);
        SyncContext holder = simpleResponse;
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(holder);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        Mockito.when(registrationService.findEndpointRegistrationByCredentialsId(Mockito.anyString())).thenReturn(Optional.ofNullable(((EndpointRegistrationDto) (null))));
        Mockito.when(credentialsService.lookupCredentials(Mockito.anyString())).thenReturn(Optional.of(new org.kaaproject.kaa.common.dto.credentials.CredentialsDto(new byte[]{  }, CredentialsStatus.IN_USE)));
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(errorBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(EndpointVerificationException.class));
    }

    @Test
    public void testLongSyncRevocation() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = buildSyncRequestMetaData();
        request.setSyncRequestMetaData(md);
        ConfigurationSyncRequest csRequest = new ConfigurationSyncRequest();
        csRequest.setConfigurationHash(ByteBuffer.wrap(new byte[]{  }));
        csRequest.setResyncOnly(true);
        request.setConfigurationSyncRequest(csRequest);
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(noDeltaResponse);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(Mockito.any(SyncContext.class), Mockito.any(ProfileClientSync.class));
        Mockito.when(applicationService.findAppById(DefaultAkkaServiceTest.APP_ID)).thenReturn(applicationDto);
        whenSync(deltaResponse);
        EndpointAddress address = new EndpointAddress(applicationDto.getTenantId(), applicationDto.getApplicationToken(), EndpointObjectHash.fromBytes(clientPublicKeyHash.array()));
        ActorClassifier classifier = ActorClassifier.APPLICATION;
        clusterServiceListener.onEndpointActorMsg(new org.kaaproject.kaa.server.operations.service.akka.messages.core.route.ThriftEndpointActorMsg<ThriftEndpointDeregistrationMessage>(address, classifier, new ThriftEndpointDeregistrationMessage()));
        Mockito.verify(errorBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(Mockito.any(EndpointRevocationException.class));
    }

    @Test
    public void testEndpointConfigurationRefresh() throws Exception {
        ChannelContext channelContextMock = Mockito.mock(ChannelContext.class);
        EndpointProfileDto profileDto = new EndpointProfileDto();
        SyncRequest request = new SyncRequest();
        request.setRequestId(DefaultAkkaServiceTest.REQUEST_ID);
        SyncRequestMetaData md = buildSyncRequestMetaData();
        request.setSyncRequestMetaData(md);
        ConfigurationSyncRequest csRequest = new ConfigurationSyncRequest();
        csRequest.setConfigurationHash(ByteBuffer.wrap("hash".getBytes()));
        request.setConfigurationSyncRequest(csRequest);
        Mockito.when(cacheService.getEndpointKey(EndpointObjectHash.fromBytes(clientPublicKeyHash.array()))).thenReturn(clientPair.getPublic());
        whenSync(noDeltaResponse);
        MessageBuilder responseBuilder = Mockito.mock(MessageBuilder.class);
        ErrorBuilder errorBuilder = Mockito.mock(ErrorBuilder.class);
        SessionInitMessage message = toSignedRequest(UUID.randomUUID(), SYNC_WITH_TIMEOUT, channelContextMock, request, responseBuilder, errorBuilder);
        Assert.assertNotNull(akkaService.getActorSystem());
        akkaService.process(message);
        Mockito.verify(operationsService, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).syncClientProfile(ArgumentMatchers.any(SyncContext.class), ArgumentMatchers.any(ProfileClientSync.class));
        byte[] epsConfHash = "hash2".getBytes();
        whenSync(deltaResponse);
        Mockito.when(applicationService.findAppById(DefaultAkkaServiceTest.APP_ID)).thenReturn(applicationDto);
        Mockito.when(operationsService.fetchEndpointSpecificConfigurationHash(ArgumentMatchers.any(EndpointProfileDto.class))).thenReturn(epsConfHash);
        Mockito.when(operationsService.refreshServerEndpointProfile(ArgumentMatchers.any(EndpointObjectHash.class))).thenReturn(profileDto);
        EndpointAddress address = new EndpointAddress(applicationDto.getTenantId(), applicationDto.getApplicationToken(), EndpointObjectHash.fromBytes(clientPublicKeyHash.array()));
        ActorClassifier classifier = ActorClassifier.GLOBAL;
        ThriftEndpointConfigurationRefreshMessage msg = new ThriftEndpointConfigurationRefreshMessage(null, null);
        clusterServiceListener.onEndpointActorMsg(new org.kaaproject.kaa.server.operations.service.akka.messages.core.route.ThriftEndpointActorMsg(address, classifier, msg));
        Mockito.verify(responseBuilder, Mockito.timeout(DefaultAkkaServiceTest.TIMEOUT).atLeastOnce()).build(ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(boolean.class));
        Mockito.verify(operationsService).syncConfigurationHashes(ArgumentMatchers.any(SyncContext.class), ArgumentMatchers.isNull(byte[].class), ArgumentMatchers.eq(epsConfHash));
    }

    private static class TestActor extends UntypedActor {
        private Object msg;

        @Override
        public void onReceive(Object msg) throws Exception {
            this.msg = ((Object) (msg));
        }

        public Object getMsg() {
            return msg;
        }
    }
}

