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


import NotificationTypeDto.SYSTEM;
import SyncResponseStatus.DELTA;
import SyncStatus.SUCCESS;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.EndpointGroupStateDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.NotificationDto;
import org.kaaproject.kaa.common.dto.TopicDto;
import org.kaaproject.kaa.server.operations.service.delta.DeltaServiceIT;
import org.kaaproject.kaa.server.operations.service.notification.NotificationDeltaService;
import org.kaaproject.kaa.server.sync.NotificationServerSync;
import org.kaaproject.kaa.server.sync.ServerSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: adjust to current logic
// @Test
// public void buildResponseEmptyTest() throws GetDeltaException{
// GetDeltaResponse deltaResponse = new GetDeltaResponse(GetDeltaResponseType.NO_DELTA);
// GetNotificationResponse notificationResponse = new GetNotificationResponse();
// SyncResponseHolder responseHolder = DefaultOperationsService.buildResponse(new SyncResponse(), 123, deltaResponse, notificationResponse);
// 
// assertNotNull(responseHolder);
// assertNotNull(responseHolder.getResponse());
// assertEquals(Integer.valueOf(123), responseHolder.getResponse().getAppStateSeqNumber());
// }
// @Test
// public void buildResponseDeltaTest() throws GetDeltaException{
// GetDeltaResponse deltaResponse = new GetDeltaResponse(GetDeltaResponseType.DELTA, 123, binaryDelta);
// deltaResponse.setConfSchema(deltaSchemaBody);
// GetNotificationResponse notificationResponse = new GetNotificationResponse();
// SyncResponseHolder responseHolder = DefaultOperationsService.buildResponse(new SyncResponse(),123, deltaResponse, notificationResponse);
// 
// assertNotNull(responseHolder);
// assertNotNull(responseHolder.getResponse());
// assertEquals(Integer.valueOf(123), responseHolder.getResponse().getAppStateSeqNumber());
// assertNotNull(responseHolder.getResponse().getConfSyncResponse().getConfDeltaBody());
// assertNotNull(responseHolder.getResponse().getConfSyncResponse().getConfSchemaBody());
// }
// @Test
// public void buildResponseNotificationTest() throws GetDeltaException{
// GetDeltaResponse deltaResponse = new GetDeltaResponse(GetDeltaResponseType.NO_DELTA);
// GetNotificationResponse notificationResponse = new GetNotificationResponse();
// List<NotificationDto> notifications = new ArrayList<>();
// notifications.add(systemTopicNfDto);
// notifications.add(userTopicNfDto);
// notifications.add(unicastNfDto);
// notificationResponse.setNotifications(notifications);
// List<TopicDto> topics = new ArrayList<>();
// topics.add(systemTopic);
// topics.add(userTopic);
// notificationResponse.setTopicList(topics);
// SyncResponseHolder responseHolder = DefaultOperationsService.buildResponse(new SyncResponse(), 123, deltaResponse, notificationResponse);
// 
// assertNotNull(responseHolder);
// assertNotNull(responseHolder.getResponse());
// assertEquals(Integer.valueOf(123), responseHolder.getResponse().getAppStateSeqNumber());
// assertNotNull(responseHolder.getResponse().getNotificationSyncResponse().getNotifications());
// assertNotNull(responseHolder.getResponse().getNotificationSyncResponse().getAvailableTopics());
// }
public class OperationsServiceTest {
    public static final String COMPLEX_PROTOCOL_SCHEMA = "operations/service/delta/complexFieldsDeltaProtocolSchema.json";

    protected static final Logger LOG = LoggerFactory.getLogger(DeltaServiceIT.class);

    private static final String UNICAST_NF_ID = "unicastNfId";

    private static final String SYSTEM_TOPIC_NF_ID = "systemTopicNfId";

    private static final String SYSTEM_TOPIC_ID = "systemTopicId";

    private static final String SYSTEM_TOPIC_NAME = "systemTopicName";

    private static final String USER_TOPIC_NF_ID = "userTopicNfId";

    private static final String USER_TOPIC_ID = "userTopicId";

    private static final String USER_TOPIC_NAME = "userTopicName";

    private OperationsService operationsService;

    private NotificationDeltaService notificationDeltaService;

    private TopicDto systemTopic;

    private TopicDto userTopic;

    private NotificationDto unicastNfDto;

    private NotificationDto systemTopicNfDto;

    private NotificationDto userTopicNfDto;

    @Test
    public void updateSyncResponseEmptyTest() {
        ServerSync response = new ServerSync();
        response.setStatus(SUCCESS);
        ServerSync result = operationsService.updateSyncResponse(response, new ArrayList<NotificationDto>(), null);
        Assert.assertNull(result);
    }

    @Test
    public void updateSyncResponseNotEmptyTest() {
        ServerSync response = new ServerSync();
        response.setStatus(SUCCESS);
        response.setNotificationSync(new NotificationServerSync());
        NotificationDto nfDto = new NotificationDto();
        nfDto.setId("nfId");
        nfDto.setBody("body".getBytes());
        nfDto.setType(SYSTEM);
        ServerSync result = operationsService.updateSyncResponse(response, Collections.singletonList(nfDto), null);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getNotificationSync());
        Assert.assertNotNull(result.getNotificationSync().getNotifications());
        Assert.assertEquals(DELTA, result.getNotificationSync().getResponseStatus());
        response = new ServerSync();
        response.setStatus(SUCCESS);
        NotificationServerSync nfResponse = new NotificationServerSync();
        nfResponse.setNotifications(new ArrayList<org.kaaproject.kaa.server.sync.Notification>());
        response.setNotificationSync(new NotificationServerSync());
        result = operationsService.updateSyncResponse(response, Collections.singletonList(nfDto), null);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getNotificationSync());
        Assert.assertNotNull(result.getNotificationSync().getNotifications());
        Assert.assertEquals(DELTA, result.getNotificationSync().getResponseStatus());
    }

    @Test
    public void updateSyncResponseUnicastTest() {
        ServerSync response = new ServerSync();
        response.setStatus(SUCCESS);
        response.setNotificationSync(new NotificationServerSync());
        ServerSync result = operationsService.updateSyncResponse(response, new ArrayList<NotificationDto>(), OperationsServiceTest.UNICAST_NF_ID);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getNotificationSync());
        Assert.assertNotNull(result.getNotificationSync().getNotifications());
        Assert.assertEquals(DELTA, result.getNotificationSync().getResponseStatus());
        Assert.assertEquals(1, result.getNotificationSync().getNotifications().size());
        Assert.assertEquals(OperationsServiceTest.UNICAST_NF_ID, result.getNotificationSync().getNotifications().get(0).getUid());
        Assert.assertNotNull(result.getNotificationSync().getNotifications().get(0).getUid());
        Assert.assertNull(result.getNotificationSync().getNotifications().get(0).getSeqNumber());
    }

    @Test
    public void updateSyncResponseTopicTest() {
        ServerSync response = new ServerSync();
        response.setStatus(SUCCESS);
        response.setNotificationSync(new NotificationServerSync());
        ServerSync result = operationsService.updateSyncResponse(response, Collections.singletonList(systemTopicNfDto), null);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getNotificationSync());
        Assert.assertNotNull(result.getNotificationSync().getNotifications());
        Assert.assertEquals(DELTA, result.getNotificationSync().getResponseStatus());
        Assert.assertEquals(1, result.getNotificationSync().getNotifications().size());
        Assert.assertEquals(OperationsServiceTest.SYSTEM_TOPIC_ID, result.getNotificationSync().getNotifications().get(0).getTopicId());
        Assert.assertEquals(NotificationType.SYSTEM, result.getNotificationSync().getNotifications().get(0).getType());
        Assert.assertNull(result.getNotificationSync().getNotifications().get(0).getUid());
        Assert.assertNotNull(result.getNotificationSync().getNotifications().get(0).getSeqNumber());
    }

    @Test
    public void isFirstRequestTest() {
        EndpointProfileDto profile = new EndpointProfileDto();
        Assert.assertTrue(DefaultOperationsService.isFirstRequest(profile));
        profile.setConfigurationHash(new byte[0]);
        Assert.assertTrue(DefaultOperationsService.isFirstRequest(profile));
        profile.setGroupState(Collections.singletonList(new EndpointGroupStateDto()));
        Assert.assertFalse(DefaultOperationsService.isFirstRequest(profile));
    }
}

