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
package org.kaaproject.kaa.server.operations.service.notification;


import TopicTypeDto.MANDATORY;
import TopicTypeDto.OPTIONAL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.EndpointGroupDto;
import org.kaaproject.kaa.common.dto.EndpointGroupStateDto;
import org.kaaproject.kaa.common.dto.EndpointNotificationDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.NotificationDto;
import org.kaaproject.kaa.common.dto.TopicDto;
import org.kaaproject.kaa.common.hash.Sha1HashUtils;
import org.kaaproject.kaa.server.common.dao.EndpointService;
import org.kaaproject.kaa.server.common.dao.NotificationService;
import org.kaaproject.kaa.server.common.dao.TopicService;
import org.kaaproject.kaa.server.operations.pojo.GetNotificationRequest;
import org.kaaproject.kaa.server.operations.pojo.GetNotificationResponse;
import org.kaaproject.kaa.server.operations.service.cache.CacheService;
import org.kaaproject.kaa.server.operations.service.cache.TopicListCacheEntry;
import org.kaaproject.kaa.server.sync.SubscriptionCommand;
import org.kaaproject.kaa.server.sync.SubscriptionCommandType;
import org.kaaproject.kaa.server.sync.TopicState;
import org.mockito.Mockito;


public class DefaultNotificationDeltaServiceTest {
    private static final byte[] ENDPOINT_KEY_HASH_BYTES = "ENDPOINT_KEY_HASH".getBytes();

    private static final String CF1 = "CF1";

    private static final String PF1 = "PF1";

    private static final String PF2 = "PF2";

    private static final String EG1 = "EG1";

    private static final String EG2 = "EG2";

    private static final String EG3 = "EG3";

    private static final String T1 = "101";

    private static final String T1NF43 = (DefaultNotificationDeltaServiceTest.T1) + "NF43";

    private static final String T2 = "102";

    private static final String T3 = "103";

    private static final String T4 = "104";

    private static final String T5 = "105";

    private static final String NF_ID_1 = "NF_ID_1";

    private static final String PNF_ID_1 = "PNF_ID_1";

    private static final String PNF_ID_2 = "PNF_ID_2";

    private NotificationDeltaService notificationDeltaService;

    // mocks
    private NotificationService notificationService;

    private TopicService topicService;

    private EndpointService endpointService;

    private CacheService cacheService;

    @Test
    public void testFindNotificationById() {
        NotificationDto expected = new NotificationDto();
        expected.setId(DefaultNotificationDeltaServiceTest.NF_ID_1);
        Mockito.when(notificationService.findNotificationById(DefaultNotificationDeltaServiceTest.NF_ID_1)).thenReturn(expected);
        NotificationDto actual = notificationDeltaService.findNotificationById(DefaultNotificationDeltaServiceTest.NF_ID_1);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFindUnicastNotificationById() {
        NotificationDto expected = new NotificationDto();
        expected.setId(DefaultNotificationDeltaServiceTest.PNF_ID_1);
        EndpointNotificationDto pnf = new EndpointNotificationDto();
        pnf.setId(DefaultNotificationDeltaServiceTest.PNF_ID_1);
        pnf.setNotificationDto(new NotificationDto());
        Mockito.when(notificationService.findUnicastNotificationById(DefaultNotificationDeltaServiceTest.PNF_ID_1)).thenReturn(pnf);
        NotificationDto actual = notificationDeltaService.findUnicastNotificationById(DefaultNotificationDeltaServiceTest.PNF_ID_1);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getNotificationDeltaSimple() {
        GetNotificationRequest request = new GetNotificationRequest(0, new EndpointProfileDto(), null, null, null);
        GetNotificationResponse response = notificationDeltaService.getNotificationDelta(request);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getNotifications());
        Assert.assertEquals(0, response.getNotifications().size());
        Assert.assertNotNull(response.getSubscriptionStates());
        Assert.assertEquals(0, response.getSubscriptionStates().size());
        // no changes in topic list means null
        Assert.assertNull(response.getTopicList());
    }

    @Test
    public void getTopicListHash() {
        EndpointProfileDto profile = new EndpointProfileDto();
        profile.setEndpointKeyHash(DefaultNotificationDeltaServiceTest.ENDPOINT_KEY_HASH_BYTES);
        List<String> subscriptions = new ArrayList<>();
        subscriptions.add(DefaultNotificationDeltaServiceTest.T1);
        subscriptions.add(DefaultNotificationDeltaServiceTest.T3);
        profile.setSubscriptions(subscriptions);
        List<EndpointGroupStateDto> groupStates = new ArrayList<>();
        groupStates.add(new EndpointGroupStateDto(DefaultNotificationDeltaServiceTest.EG1, DefaultNotificationDeltaServiceTest.PF1, DefaultNotificationDeltaServiceTest.CF1));
        groupStates.add(new EndpointGroupStateDto(DefaultNotificationDeltaServiceTest.EG2, DefaultNotificationDeltaServiceTest.PF2, null));
        groupStates.add(new EndpointGroupStateDto(DefaultNotificationDeltaServiceTest.EG3, DefaultNotificationDeltaServiceTest.PF2, null));
        profile.setGroupState(groupStates);
        EndpointGroupDto eg1 = new EndpointGroupDto();
        eg1.setId(DefaultNotificationDeltaServiceTest.EG1);
        eg1.setTopics(Collections.singletonList(DefaultNotificationDeltaServiceTest.T1));
        EndpointGroupDto eg2 = new EndpointGroupDto();
        eg2.setId(DefaultNotificationDeltaServiceTest.EG2);
        List<String> topicIds = new ArrayList<>();
        topicIds.add(DefaultNotificationDeltaServiceTest.T3);
        topicIds.add(DefaultNotificationDeltaServiceTest.T5);
        eg2.setTopics(topicIds);
        EndpointGroupDto eg3 = new EndpointGroupDto();
        eg3.setId(DefaultNotificationDeltaServiceTest.EG3);
        TopicDto t1 = new TopicDto();
        t1.setId(DefaultNotificationDeltaServiceTest.T1);
        t1.setName(DefaultNotificationDeltaServiceTest.T1);
        t1.setType(MANDATORY);
        TopicDto t3 = new TopicDto();
        t3.setId(DefaultNotificationDeltaServiceTest.T3);
        t3.setName(DefaultNotificationDeltaServiceTest.T3);
        t3.setType(OPTIONAL);
        TopicDto t5 = new TopicDto();
        t5.setId(DefaultNotificationDeltaServiceTest.T5);
        t5.setName(DefaultNotificationDeltaServiceTest.T5);
        t5.setType(MANDATORY);
        Mockito.when(cacheService.getEndpointGroupById(DefaultNotificationDeltaServiceTest.EG1)).thenReturn(eg1);
        Mockito.when(cacheService.getEndpointGroupById(DefaultNotificationDeltaServiceTest.EG2)).thenReturn(eg2);
        Mockito.when(cacheService.getEndpointGroupById(DefaultNotificationDeltaServiceTest.EG3)).thenReturn(eg3);
        Mockito.when(cacheService.getTopicById(DefaultNotificationDeltaServiceTest.T1)).thenReturn(t1);
        Mockito.when(cacheService.getTopicById(DefaultNotificationDeltaServiceTest.T3)).thenReturn(t3);
        Mockito.when(cacheService.getTopicById(DefaultNotificationDeltaServiceTest.T5)).thenReturn(t5);
        TopicListCacheEntry cacheEntry = notificationDeltaService.getTopicListHash("APP_TOKEN", "ENDPOINT_ID", profile);
        Assert.assertNotNull(cacheEntry);
        Assert.assertNotNull(cacheEntry.getTopics());
        Assert.assertEquals(130150, cacheEntry.getSimpleHash());
        Assert.assertEquals(3, cacheEntry.getTopics().size());
    }

    @Test
    public void getNotificationDelta() {
        EndpointProfileDto profile = new EndpointProfileDto();
        profile.setEndpointKeyHash(DefaultNotificationDeltaServiceTest.ENDPOINT_KEY_HASH_BYTES);
        List<String> subscriptions = new ArrayList<>();
        subscriptions.add(DefaultNotificationDeltaServiceTest.T1);
        subscriptions.add(DefaultNotificationDeltaServiceTest.T3);
        profile.setSubscriptions(subscriptions);
        List<SubscriptionCommand> subscriptionCommands = new ArrayList<>();
        subscriptionCommands.add(new SubscriptionCommand(DefaultNotificationDeltaServiceTest.T1, SubscriptionCommandType.ADD));
        subscriptionCommands.add(new SubscriptionCommand(DefaultNotificationDeltaServiceTest.T2, SubscriptionCommandType.ADD));
        subscriptionCommands.add(new SubscriptionCommand(DefaultNotificationDeltaServiceTest.T3, SubscriptionCommandType.REMOVE));
        subscriptionCommands.add(new SubscriptionCommand(DefaultNotificationDeltaServiceTest.T4, SubscriptionCommandType.REMOVE));
        List<String> acceptedUnicastNotifications = new ArrayList<>();
        acceptedUnicastNotifications.add(DefaultNotificationDeltaServiceTest.PNF_ID_1);
        List<TopicState> topicStates = new ArrayList<>();
        topicStates.add(new TopicState(DefaultNotificationDeltaServiceTest.T1, 42));
        topicStates.add(new TopicState(DefaultNotificationDeltaServiceTest.T3, 1));
        List<EndpointGroupStateDto> history = new ArrayList<>();
        history.add(new EndpointGroupStateDto(DefaultNotificationDeltaServiceTest.EG1, DefaultNotificationDeltaServiceTest.PF1, DefaultNotificationDeltaServiceTest.CF1));
        history.add(new EndpointGroupStateDto(DefaultNotificationDeltaServiceTest.EG2, DefaultNotificationDeltaServiceTest.PF2, null));
        history.add(new EndpointGroupStateDto(DefaultNotificationDeltaServiceTest.EG3, DefaultNotificationDeltaServiceTest.PF2, null));
        profile.setGroupState(history);
        profile.setSimpleTopicHash(130150);
        profile.setTopicHash(Sha1HashUtils.hashToBytes((((((DefaultNotificationDeltaServiceTest.T1) + "|") + (DefaultNotificationDeltaServiceTest.T3)) + "|") + (DefaultNotificationDeltaServiceTest.T5))));
        // EndpointGroupDto eg1 = new EndpointGroupDto();
        // eg1.setId(EG1);
        // eg1.setTopics(Collections.singletonList(T1));
        // EndpointGroupDto eg2 = new EndpointGroupDto();
        // eg2.setId(EG2);
        // List<String> topicIds = new ArrayList<>();
        // topicIds.add(T3);
        // topicIds.add(T5);
        // eg2.setTopics(topicIds);
        // EndpointGroupDto eg3 = new EndpointGroupDto();
        // eg3.setId(EG3);
        // TopicDto t1 = new TopicDto();
        // t1.setId(T1);
        // t1.setName(T1);
        // t1.setType(TopicTypeDto.MANDATORY);
        // TopicDto t3 = new TopicDto();
        // t3.setId(T3);
        // t3.setName(T3);
        // t3.setType(TopicTypeDto.OPTIONAL);
        // TopicDto t5 = new TopicDto();
        // t5.setId(T5);
        // t5.setName(T5);
        // t5.setType(TopicTypeDto.MANDATORY);
        // 
        // Mockito.when(cacheService.getEndpointGroupById(EG1)).thenReturn(eg1);
        // Mockito.when(cacheService.getEndpointGroupById(EG2)).thenReturn(eg2);
        // Mockito.when(cacheService.getEndpointGroupById(EG3)).thenReturn(eg3);
        // Mockito.when(cacheService.getTopicById(T1)).thenReturn(t1);
        // Mockito.when(cacheService.getTopicById(T3)).thenReturn(t3);
        // Mockito.when(cacheService.getTopicById(T5)).thenReturn(t5);
        // 
        // Mockito.when(endpointService.findEndpointGroupById(EG1)).thenReturn(eg1);
        // Mockito.when(endpointService.findEndpointGroupById(EG2)).thenReturn(eg2);
        // Mockito.when(endpointService.findEndpointGroupById(EG3)).thenReturn(eg3);
        // Mockito.when(topicService.findTopicById(T1)).thenReturn(t1);
        // Mockito.when(topicService.findTopicById(T3)).thenReturn(t3);
        // Mockito.when(topicService.findTopicById(T5)).thenReturn(t5);
        NotificationDto t1Nf43 = new NotificationDto();
        t1Nf43.setId(DefaultNotificationDeltaServiceTest.T1NF43);
        t1Nf43.setSecNum(43);
        t1Nf43.setExpiredAt(new Date(((System.currentTimeMillis()) + (TimeUnit.DAYS.toMillis(7)))));
        Mockito.when(notificationService.findNotificationsByTopicIdAndVersionAndStartSecNum(DefaultNotificationDeltaServiceTest.T1, 42, 0, 0)).thenReturn(Collections.singletonList(t1Nf43));
        EndpointNotificationDto pnf2 = new EndpointNotificationDto();
        NotificationDto nfDto = new NotificationDto();
        pnf2.setId(DefaultNotificationDeltaServiceTest.PNF_ID_2);
        pnf2.setNotificationDto(nfDto);
        nfDto.setExpiredAt(new Date(((System.currentTimeMillis()) + (TimeUnit.DAYS.toMillis(7)))));
        Mockito.when(notificationService.findUnicastNotificationsByKeyHash(DefaultNotificationDeltaServiceTest.ENDPOINT_KEY_HASH_BYTES)).thenReturn(Collections.singletonList(pnf2));
        GetNotificationRequest request = new GetNotificationRequest(130150, profile, subscriptionCommands, acceptedUnicastNotifications, topicStates);
        GetNotificationResponse response = notificationDeltaService.getNotificationDelta(request);
        // Mockito.verify(cacheService).getTopicById(T1);
        // Mockito.verify(cacheService).getTopicById(T3);
        // Mockito.verify(cacheService).getTopicById(T5);
        // 
        Mockito.verify(notificationService).findNotificationsByTopicIdAndVersionAndStartSecNum(DefaultNotificationDeltaServiceTest.T1, 42, 0, 0);
        Mockito.verify(notificationService).findNotificationsByTopicIdAndVersionAndStartSecNum(DefaultNotificationDeltaServiceTest.T2, 0, 0, 0);
        // Mockito.verify(notificationService).findNotificationsByTopicIdAndVersionAndStartSecNum(T5, 0, 0, 0);
        Mockito.verify(notificationService).removeUnicastNotificationById(DefaultNotificationDeltaServiceTest.PNF_ID_1);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getNotifications());
        Assert.assertEquals(2, response.getNotifications().size());
        Assert.assertEquals(DefaultNotificationDeltaServiceTest.T1NF43, response.getNotifications().get(0).getId());
        Assert.assertEquals(DefaultNotificationDeltaServiceTest.PNF_ID_2, response.getNotifications().get(1).getId());
        Assert.assertNotNull(response.getSubscriptionStates());
        Assert.assertEquals(2, response.getSubscriptionStates().size());
        Assert.assertEquals(new Integer(43), response.getSubscriptionStates().get(DefaultNotificationDeltaServiceTest.T1));
        Assert.assertEquals(new Integer(0), response.getSubscriptionStates().get(DefaultNotificationDeltaServiceTest.T2));
        // no changes in topic list means null
        Assert.assertNull(response.getTopicList());
    }
}

