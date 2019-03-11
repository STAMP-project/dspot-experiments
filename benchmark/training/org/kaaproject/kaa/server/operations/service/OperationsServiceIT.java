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
package org.kaaproject.kaa.server.operations.service;


import SyncResponseStatus.DELTA;
import SyncResponseStatus.NO_DELTA;
import SyncResponseStatus.RESYNC;
import SyncStatus.FAILURE;
import SyncStatus.PROFILE_RESYNC;
import SyncStatus.SUCCESS;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Collections;
import org.apache.avro.generic.GenericRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.avro.GenericAvroConverter;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.EndpointUserDto;
import org.kaaproject.kaa.common.dto.ProfileFilterDto;
import org.kaaproject.kaa.common.dto.TopicDto;
import org.kaaproject.kaa.common.dto.admin.SdkProfileDto;
import org.kaaproject.kaa.common.endpoint.gen.BasicEndpointProfile;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;
import org.kaaproject.kaa.server.common.Base64Util;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.common.dao.SdkProfileService;
import org.kaaproject.kaa.server.common.dao.impl.EndpointConfigurationDao;
import org.kaaproject.kaa.server.common.dao.impl.TenantDao;
import org.kaaproject.kaa.server.common.dao.model.EndpointConfiguration;
import org.kaaproject.kaa.server.common.dao.model.sql.Application;
import org.kaaproject.kaa.server.common.dao.model.sql.ConfigurationSchema;
import org.kaaproject.kaa.server.common.dao.model.sql.EndpointProfileSchema;
import org.kaaproject.kaa.server.common.dao.model.sql.Tenant;
import org.kaaproject.kaa.server.operations.pojo.SyncContext;
import org.kaaproject.kaa.server.operations.pojo.exceptions.GetDeltaException;
import org.kaaproject.kaa.server.sync.ClientSync;
import org.kaaproject.kaa.server.sync.ClientSyncMetaData;
import org.kaaproject.kaa.server.sync.ConfigurationClientSync;
import org.kaaproject.kaa.server.sync.EndpointAttachRequest;
import org.kaaproject.kaa.server.sync.EventClientSync;
import org.kaaproject.kaa.server.sync.EventListenersRequest;
import org.kaaproject.kaa.server.sync.NotificationClientSync;
import org.kaaproject.kaa.server.sync.ProfileClientSync;
import org.kaaproject.kaa.server.sync.ServerSync;
import org.kaaproject.kaa.server.sync.SubscriptionCommand;
import org.kaaproject.kaa.server.sync.SubscriptionCommandType;
import org.kaaproject.kaa.server.sync.UserAttachRequest;
import org.kaaproject.kaa.server.sync.UserClientSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/operations/common-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Transactional
public class OperationsServiceIT extends AbstractTest {
    public static final String DATA_SCHEMA_LOCATION = "operations/service/default_schema.json";

    public static final String BASE_SCHEMA_LOCATION = "operations/service/default_schema_converted_to_base.json";

    public static final String BASE_DATA_LOCATION = "operations/service/base_data.json";

    public static final String BASE_DATA_UPDATED_LOCATION = "operations/service/base_data_updated.json";

    public static final String NEW_COMPLEX_CONFIG = "operations/service/delta/complexFieldsDeltaNew.json";

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final Logger LOG = LoggerFactory.getLogger(OperationsServiceIT.class);

    private static final String ENDPOINT_ACCESS_TOKEN = "endpointAccessToken";

    private static final String INVALID_ENDPOINT_ACCESS_TOKEN = "InvalidEndpointAccessToken";

    private static final int REQUEST_ID1 = 42;

    private static final String USER_VERIFIER_ID = "user@test.com";

    private static final String USER_EXTERNAL_ID = "user@test.com";

    private static final String USER_ACCESS_TOKEN = "userAccessToken";

    private static final String INVALID_USER_ACCESS_TOKEN = "invalidUserAccessToken";

    private static final int CONF_SCHEMA_VERSION = 2;

    private static final int PROFILE_SCHEMA_VERSION = 1;

    private static final int APPLICATION_SEQ_NUMBER = 7;

    private static final String CUSTOMER_ID = "CustomerId";

    private static final String APPLICATION_NAME = "ApplicationName";

    private static final BasicEndpointProfile ENDPOINT_PROFILE = new BasicEndpointProfile("dummy profile 1");

    private static final BasicEndpointProfile NEW_ENDPOINT_PROFILE = new BasicEndpointProfile("dummy profile 2");

    private static final BasicEndpointProfile FAKE_ENDPOINT_PROFILE = new BasicEndpointProfile("dummy profile 3");

    private static final byte[] ENDPOINT_KEY = OperationsServiceIT.getRandEndpointKey();

    private static final byte[] ENDPOINT_KEY2 = OperationsServiceIT.getRandEndpointKey();

    private static final SdkProfileDto SDK_PROFILE = new SdkProfileDto(null, OperationsServiceIT.CONF_SCHEMA_VERSION, OperationsServiceIT.PROFILE_SCHEMA_VERSION, 1, 1, null, null, null, null, null, null);

    private final GenericAvroConverter<GenericRecord> avroConverter = new GenericAvroConverter<GenericRecord>(BasicEndpointProfile.SCHEMA$);

    @Autowired
    protected OperationsService operationsService;

    @Autowired
    protected TenantDao<Tenant> customerDao;

    @Autowired
    protected EndpointConfigurationDao<EndpointConfiguration> endpointConfigurationDao;

    @Autowired
    protected SdkProfileService sdkProfileService;

    private String SDK_TOKEN;

    private String deltaSchema;

    private String currentConfiguration;

    private byte[] currentConfigurationHash;

    private ConfigurationSchema confSchema;

    private Application application;

    private Tenant customer;

    private EndpointProfileSchema endpointProfileSchema;

    private ProfileFilterDto profileFilter;

    private TopicDto mandatoryTopicDto;

    private TopicDto optionalTopicDto;

    private EndpointUserDto userDto;

    private KeyPair keyPair;

    @Test
    public void basicRegistrationTest() throws IOException, GetDeltaException {
        registerEndpoint();
    }

    @Test
    public void basicDoubleRegistrationTest() throws IOException, GetDeltaException {
        byte[] profile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setSdkToken(SDK_TOKEN);
        request.setClientSyncMetaData(md);
        ProfileClientSync profileSync = new ProfileClientSync(ByteBuffer.wrap(OperationsServiceIT.ENDPOINT_KEY), ByteBuffer.wrap(profile), SDK_TOKEN, null);
        request.setProfileSync(profileSync);
        request.setConfigurationSync(new ConfigurationClientSync());
        SyncContext context = OperationsServiceIT.createContext(request);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.syncConfiguration(context, request.getConfigurationSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getConfigurationSync());
        Assert.assertEquals(RESYNC, response.getConfigurationSync().getResponseStatus());
        Assert.assertNotNull(response.getConfigurationSync().getConfDeltaBody());
        // Kaa #7786
        Assert.assertNull(response.getConfigurationSync().getConfSchemaBody());
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.syncConfiguration(context, request.getConfigurationSync());
        response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getConfigurationSync());
        Assert.assertEquals(RESYNC, response.getConfigurationSync().getResponseStatus());
        Assert.assertNotNull(response.getConfigurationSync().getConfDeltaBody());
        // Kaa #7786
        Assert.assertNull(response.getConfigurationSync().getConfSchemaBody());
    }

    @Test
    public void basicUpdateTest() throws IOException, GetDeltaException {
        basicRegistrationTest();
        byte[] oldProfile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        byte[] profile = avroConverter.encode(OperationsServiceIT.NEW_ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(oldProfile).getData()));
        md.setSdkToken(SDK_TOKEN);
        request.setClientSyncMetaData(md);
        ProfileClientSync profileSync = new ProfileClientSync(null, ByteBuffer.wrap(profile), SDK_TOKEN, null);
        request.setProfileSync(profileSync);
        ConfigurationClientSync confSyncRequest = new ConfigurationClientSync();
        confSyncRequest.setConfigurationHash(ByteBuffer.wrap(currentConfigurationHash));
        request.setConfigurationSync(confSyncRequest);
        SyncContext context = OperationsServiceIT.createContext(request);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.syncConfiguration(context, request.getConfigurationSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getConfigurationSync());
        Assert.assertEquals(NO_DELTA, response.getConfigurationSync().getResponseStatus());
        Assert.assertNull(response.getConfigurationSync().getConfDeltaBody());
        // Kaa #7786
        Assert.assertNull(response.getConfigurationSync().getConfSchemaBody());
    }

    @Test
    public void basicProfileResyncTest() throws IOException, GetDeltaException {
        basicRegistrationTest();
        byte[] profile = avroConverter.encode(OperationsServiceIT.FAKE_ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(profile).getData()));
        request.setClientSyncMetaData(md);
        ConfigurationClientSync confSyncRequest = new ConfigurationClientSync();
        confSyncRequest.setConfigurationHash(ByteBuffer.wrap(currentConfigurationHash));
        request.setConfigurationSync(confSyncRequest);
        SyncContext context = OperationsServiceIT.createContext(request);
        operationsService.syncClientProfile(context, request.getProfileSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(PROFILE_RESYNC, response.getStatus());
    }

    @Test
    public void basicDeltaTest() throws IOException, GetDeltaException {
        basicRegistrationTest();
        byte[] profile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(profile).getData()));
        request.setClientSyncMetaData(md);
        ConfigurationClientSync confSyncRequest = new ConfigurationClientSync();
        confSyncRequest.setConfigurationHash(ByteBuffer.wrap(currentConfigurationHash));
        request.setConfigurationSync(confSyncRequest);
        SyncContext context = OperationsServiceIT.createContext(request);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.syncConfiguration(context, request.getConfigurationSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getConfigurationSync());
        Assert.assertEquals(NO_DELTA, response.getConfigurationSync().getResponseStatus());
        Assert.assertNull(response.getConfigurationSync().getConfDeltaBody());
        // Kaa #7786
        Assert.assertNull(response.getConfigurationSync().getConfSchemaBody());
        Assert.assertNull(response.getNotificationSync());
    }

    @Test
    public void basicMandatoryNotificationsTest() throws IOException, GetDeltaException {
        basicRegistrationTest();
        byte[] profile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(profile).getData()));
        request.setClientSyncMetaData(md);
        NotificationClientSync nfSyncRequest = new NotificationClientSync();
        request.setNotificationSync(nfSyncRequest);
        SyncContext context = OperationsServiceIT.createContext(request);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.syncNotification(context, request.getNotificationSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getNotificationSync());
        Assert.assertEquals(DELTA, response.getNotificationSync().getResponseStatus());
        Assert.assertNotNull(response.getNotificationSync().getNotifications());
        // Only mandatory notification
        Assert.assertEquals(1, response.getNotificationSync().getNotifications().size());
    }

    @Test
    public void basicOptionalNotificationsTest() throws IOException, GetDeltaException {
        basicRegistrationTest();
        byte[] profile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(profile).getData()));
        request.setClientSyncMetaData(md);
        NotificationClientSync nfSyncRequest = new NotificationClientSync();
        SubscriptionCommand command = new SubscriptionCommand(optionalTopicDto.getId(), SubscriptionCommandType.ADD);
        nfSyncRequest.setSubscriptionCommands(Collections.singletonList(command));
        request.setNotificationSync(nfSyncRequest);
        SyncContext context = OperationsServiceIT.createContext(request);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.syncNotification(context, request.getNotificationSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNotNull(response.getNotificationSync());
        Assert.assertEquals(DELTA, response.getNotificationSync().getResponseStatus());
        Assert.assertNotNull(response.getNotificationSync().getNotifications());
        // Mandatory + Optional notification
        Assert.assertEquals(2, response.getNotificationSync().getNotifications().size());
    }

    @Test
    public void basicUserAttachTest() throws IOException, GetDeltaException {
        EndpointProfileDto profile = registerEndpoint();
        profile.setEndpointUserId(null);
        profile = operationsService.attachEndpointToUser(profile, application.getApplicationToken(), OperationsServiceIT.USER_EXTERNAL_ID);
        Assert.assertNotNull(profile.getEndpointUserId());
    }

    @Test
    public void basicEndpointAttachTest() throws IOException, GetDeltaException {
        // register main endpoint
        EndpointProfileDto profileDto = registerEndpoint();
        // register second endpoint
        createSecondEndpoint();
        byte[] profile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(profile).getData()));
        request.setClientSyncMetaData(md);
        UserClientSync userRequest = new UserClientSync();
        userRequest.setEndpointAttachRequests(Collections.singletonList(new EndpointAttachRequest(OperationsServiceIT.REQUEST_ID1, OperationsServiceIT.ENDPOINT_ACCESS_TOKEN)));
        request.setUserSync(userRequest);
        profileDto.setEndpointUserId(userDto.getId());
        SyncContext context = OperationsServiceIT.createContext(request);
        context.setNotificationVersion(profileDto);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.processEndpointAttachDetachRequests(context, request.getUserSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNull(response.getConfigurationSync());
        Assert.assertNull(response.getNotificationSync());
        Assert.assertNotNull(response.getUserSync().getEndpointAttachResponses());
        Assert.assertEquals(1, response.getUserSync().getEndpointAttachResponses().size());
        Assert.assertEquals(SUCCESS, response.getUserSync().getEndpointAttachResponses().get(0).getResult());
    }

    @Test
    public void basicEndpointAttachFailTest() throws IOException, GetDeltaException {
        // register main endpoint
        EndpointProfileDto profileDto = registerEndpoint();
        // register second endpoint
        createSecondEndpoint();
        operationsService.attachEndpointToUser(profileDto, application.getApplicationToken(), OperationsServiceIT.USER_EXTERNAL_ID);
        byte[] profile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(profile).getData()));
        request.setClientSyncMetaData(md);
        UserClientSync userRequest = new UserClientSync();
        userRequest.setEndpointAttachRequests(Collections.singletonList(new EndpointAttachRequest(OperationsServiceIT.REQUEST_ID1, OperationsServiceIT.INVALID_ENDPOINT_ACCESS_TOKEN)));
        request.setUserSync(userRequest);
        SyncContext context = OperationsServiceIT.createContext(request);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.processEndpointAttachDetachRequests(context, request.getUserSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNull(response.getConfigurationSync());
        Assert.assertNull(response.getNotificationSync());
        Assert.assertNotNull(response.getUserSync());
        Assert.assertNotNull(response.getUserSync().getEndpointAttachResponses());
        Assert.assertEquals(1, response.getUserSync().getEndpointAttachResponses().size());
        Assert.assertEquals(FAILURE, response.getUserSync().getEndpointAttachResponses().get(0).getResult());
    }

    @Test
    public void basicEndpointDetachTest() throws IOException, GetDeltaException {
        // register main endpoint
        EndpointProfileDto profileDto = registerEndpoint();
        byte[] profile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        // register second endpoint
        EndpointProfileDto secondDto = createSecondEndpoint();
        operationsService.attachEndpointToUser(profileDto, application.getApplicationToken(), OperationsServiceIT.USER_EXTERNAL_ID);
        operationsService.attachEndpointToUser(secondDto, application.getApplicationToken(), OperationsServiceIT.USER_EXTERNAL_ID);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(profile).getData()));
        request.setClientSyncMetaData(md);
        UserClientSync userRequest = new UserClientSync();
        userRequest.setEndpointDetachRequests(Collections.singletonList(new org.kaaproject.kaa.server.sync.EndpointDetachRequest(OperationsServiceIT.REQUEST_ID1, Base64Util.encode(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY2).getData()))));
        request.setUserSync(userRequest);
        SyncContext context = OperationsServiceIT.createContext(request);
        context.setNotificationVersion(profileDto);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.processEndpointAttachDetachRequests(context, request.getUserSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNull(response.getConfigurationSync());
        Assert.assertNull(response.getNotificationSync());
        Assert.assertNotNull(response.getUserSync());
        Assert.assertNotNull(response.getUserSync().getEndpointDetachResponses());
        Assert.assertEquals(1, response.getUserSync().getEndpointDetachResponses().size());
        Assert.assertEquals(SUCCESS, response.getUserSync().getEndpointDetachResponses().get(0).getResult());
    }

    @Test
    public void basicEndpointDetachFailTest() throws IOException, GetDeltaException {
        // register main endpoint
        EndpointProfileDto profileDto = registerEndpoint();
        // register second endpoint
        createSecondEndpoint();
        byte[] profile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(profile).getData()));
        request.setClientSyncMetaData(md);
        UserClientSync userRequest = new UserClientSync();
        userRequest.setUserAttachRequest(new UserAttachRequest(OperationsServiceIT.USER_VERIFIER_ID, OperationsServiceIT.USER_EXTERNAL_ID, OperationsServiceIT.USER_ACCESS_TOKEN));
        userRequest.setEndpointDetachRequests(Collections.singletonList(new org.kaaproject.kaa.server.sync.EndpointDetachRequest(OperationsServiceIT.REQUEST_ID1, Base64Util.encode(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY2).getData()))));
        request.setUserSync(userRequest);
        profileDto.setEndpointUserId(userDto.getId());
        SyncContext context = OperationsServiceIT.createContext(request);
        context.setNotificationVersion(profileDto);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.processEndpointAttachDetachRequests(context, request.getUserSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNull(response.getConfigurationSync());
        Assert.assertNull(response.getNotificationSync());
        Assert.assertNotNull(response.getUserSync());
        Assert.assertNotNull(response.getUserSync().getEndpointDetachResponses());
        Assert.assertEquals(1, response.getUserSync().getEndpointDetachResponses().size());
        Assert.assertEquals(FAILURE, response.getUserSync().getEndpointDetachResponses().get(0).getResult());
    }

    @Test
    public void basicEventListenerFailTest() throws IOException, GetDeltaException {
        // register main endpoint
        basicRegistrationTest();
        byte[] profile = avroConverter.encode(OperationsServiceIT.ENDPOINT_PROFILE);
        ClientSync request = new ClientSync();
        ClientSyncMetaData md = new ClientSyncMetaData();
        md.setApplicationToken(application.getApplicationToken());
        md.setEndpointPublicKeyHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(OperationsServiceIT.ENDPOINT_KEY).getData()));
        md.setProfileHash(ByteBuffer.wrap(EndpointObjectHash.fromSha1(profile).getData()));
        request.setClientSyncMetaData(md);
        EventClientSync eventRequest = new EventClientSync();
        eventRequest.setEventListenersRequests(Collections.singletonList(new EventListenersRequest(OperationsServiceIT.REQUEST_ID1, Arrays.asList("fqn"))));
        request.setEventSync(eventRequest);
        SyncContext context = OperationsServiceIT.createContext(request);
        operationsService.syncClientProfile(context, request.getProfileSync());
        operationsService.processEventListenerRequests(context, request.getEventSync());
        ServerSync response = context.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(SUCCESS, response.getStatus());
        Assert.assertNull(response.getConfigurationSync());
        Assert.assertNull(response.getNotificationSync());
        Assert.assertNotNull(response.getEventSync());
        Assert.assertNotNull(response.getEventSync().getEventListenersResponses());
        Assert.assertEquals(1, response.getEventSync().getEventListenersResponses().size());
        Assert.assertEquals(FAILURE, response.getEventSync().getEventListenersResponses().get(0).getResult());
    }
}

