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
package org.kaaproject.kaa.server.common.nosql.mongo.dao;


import DirtiesContext.ClassMode;
import NotificationTypeDto.SYSTEM;
import NotificationTypeDto.USER;
import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.dto.EndpointNotificationDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.NotificationDto;
import org.kaaproject.kaa.common.dto.NotificationSchemaDto;
import org.kaaproject.kaa.common.dto.TopicDto;
import org.kaaproject.kaa.common.dto.VersionDto;
import org.kaaproject.kaa.server.common.dao.exception.IncorrectParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/mongo-dao-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class NotificationServiceImplTest extends AbstractMongoTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImplTest.class);

    @Test
    public void testSaveNotificationSchema() {
        NotificationSchemaDto schema = generateNotificationSchemaDto(null, null);
        Assert.assertNotNull(schema);
        Assert.assertTrue(isNotBlank(schema.getId()));
        Assert.assertEquals(2, schema.getVersion());
        Assert.assertEquals(USER, schema.getType());
    }

    @Test
    public void testSaveNotification() {
        NotificationDto notification = generateNotificationsDto(null, null, 1, null).get(0);
        Assert.assertNotNull(notification);
        Assert.assertTrue(isNotBlank(notification.getId()));
        Assert.assertEquals(USER, notification.getType());
    }

    @Test
    public void testFindNotificationById() {
        NotificationDto notification = generateNotificationsDto(null, null, 1, null).get(0);
        Assert.assertNotNull(notification);
        Assert.assertTrue(isNotBlank(notification.getId()));
        NotificationDto found = notificationService.findNotificationById(notification.getId());
        Assert.assertEquals(notification, found);
    }

    @Test
    public void testFindNotificationsByTopicId() {
        NotificationDto notification = generateNotificationsDto(null, null, 1, null).get(0);
        Assert.assertNotNull(notification);
        NotificationDto found = notificationService.findNotificationsByTopicId(notification.getTopicId()).get(0);
        Assert.assertEquals(notification, found);
    }

    @Test
    public void testFindNotificationSchemaById() {
        NotificationSchemaDto schema = generateNotificationSchemaDto(null, null);
        Assert.assertNotNull(schema);
        NotificationSchemaDto found = notificationService.findNotificationSchemaById(schema.getId());
        Assert.assertEquals(schema, found);
    }

    @Test
    public void testFindNotificationSchemasByAppId() {
        NotificationSchemaDto schema = generateNotificationSchemaDto(null, null);
        Assert.assertNotNull(schema);
        NotificationSchemaDto found = notificationService.findNotificationSchemasByAppId(schema.getApplicationId()).get(1);
        Assert.assertEquals(schema, found);
    }

    @Test
    public void testFindUserNotificationSchemasByAppId() {
        NotificationDto dto = generateNotificationsDto(null, null, 1, null).get(0);
        List<VersionDto> schemas = notificationService.findUserNotificationSchemasByAppId(dto.getApplicationId());
        generateNotificationSchemaDto(dto.getApplicationId(), SYSTEM);
        Assert.assertEquals(2, schemas.size());
    }

    @Test
    public void testFindNotificationSchemaVersionsByAppId() {
        NotificationDto dto = generateNotificationsDto(null, null, 1, null).get(0);
        generateNotificationSchemaDto(dto.getApplicationId(), SYSTEM);
        List<VersionDto> schemas = notificationService.findNotificationSchemaVersionsByAppId(dto.getApplicationId());
        Assert.assertEquals(3, schemas.size());
    }

    @Test
    public void testRemoveNotificationSchemasByAppId() {
        NotificationDto dto = generateNotificationsDto(null, null, 3, null).get(0);
        String appId = dto.getApplicationId();
        notificationService.removeNotificationSchemasByAppId(appId);
        List<NotificationSchemaDto> schemas = notificationService.findNotificationSchemasByAppId(appId);
        Assert.assertTrue(schemas.isEmpty());
    }

    @Test
    public void testFindNotificationsByTopicIdAndVersionAndStartSecNum() {
        NotificationDto dto = generateNotificationsDto(null, null, 3, USER).get(0);
        String topicId = dto.getTopicId();
        List<NotificationDto> notifications = notificationService.findNotificationsByTopicIdAndVersionAndStartSecNum(topicId, 0, 1, dto.getNfVersion());
        Assert.assertFalse(notifications.isEmpty());
        Assert.assertEquals(3, notifications.size());
    }

    @Test
    public void testFindNotificationSchemasByAppIdAndType() {
        NotificationDto dto = generateNotificationsDto(null, null, 1, null).get(0);
        generateNotificationSchemaDto(dto.getApplicationId(), SYSTEM);
        List<NotificationSchemaDto> schemas = notificationService.findNotificationSchemasByAppIdAndType(dto.getApplicationId(), SYSTEM);
        Assert.assertEquals(1, schemas.size());
        schemas = notificationService.findNotificationSchemasByAppIdAndType(dto.getApplicationId(), USER);
        Assert.assertEquals(2, schemas.size());
    }

    @Test
    public void testFindNotificationSchemaByAppIdAndTypeAndVersion() {
        NotificationDto dto = generateNotificationsDto(null, null, 1, null).get(0);
        generateNotificationSchemaDto(dto.getApplicationId(), SYSTEM);
        NotificationSchemaDto schema = notificationService.findNotificationSchemaByAppIdAndTypeAndVersion(dto.getApplicationId(), SYSTEM, 1);
        Assert.assertNotNull(schema);
    }

    @Test
    public void testFindUnicastNotificationById() {
        TopicDto topicDto = generateTopicDto(null, null);
        EndpointProfileDto profile = generateEndpointProfileDto(topicDto.getApplicationId(), Arrays.asList(topicDto.getId()));
        byte[] keyHash = profile.getEndpointKeyHash();
        EndpointNotificationDto notification = generateUnicastNotificationDto(null, topicDto.getId(), keyHash);
        Assert.assertTrue(isNotBlank(notification.getId()));
        EndpointNotificationDto found = notificationService.findUnicastNotificationById(notification.getId());
        Assert.assertEquals(notification, found);
    }

    @Test
    public void testRemoveUnicastNotificationsByKeyHash() {
        TopicDto topicDto = generateTopicDto(null, null);
        EndpointProfileDto profile = generateEndpointProfileDto(topicDto.getApplicationId(), Arrays.asList(topicDto.getId()));
        byte[] keyHash = profile.getEndpointKeyHash();
        EndpointNotificationDto notification = generateUnicastNotificationDto(null, topicDto.getId(), keyHash);
        Assert.assertTrue(isNotBlank(notification.getId()));
        notificationService.removeUnicastNotificationsByKeyHash(keyHash);
        List<EndpointNotificationDto> notifications = notificationService.findUnicastNotificationsByKeyHash(keyHash);
        Assert.assertTrue(notifications.isEmpty());
    }

    @Test(expected = IncorrectParameterException.class)
    public void testSaveInvalidNotificationSchema() {
        notificationService.saveNotificationSchema(new NotificationSchemaDto());
    }

    @Test(expected = IncorrectParameterException.class)
    public void testSaveNotificationSchemaWithEmptyType() {
        NotificationSchemaDto schema = new NotificationSchemaDto();
        schema.setApplicationId(new ObjectId().toString());
        notificationService.saveNotificationSchema(schema);
    }

    @Test(expected = IncorrectParameterException.class)
    public void testSaveNotificationSchemaWithInvalidAppId() {
        NotificationSchemaDto schema = new NotificationSchemaDto();
        schema.setApplicationId("Invalid Application id.");
        notificationService.saveNotificationSchema(schema);
    }

    @Test
    public void testRemoveUnicastNotificationById() {
        TopicDto topicDto = generateTopicDto(null, null);
        EndpointProfileDto profile = generateEndpointProfileDto(topicDto.getApplicationId(), Arrays.asList(topicDto.getId()));
        byte[] keyHash = profile.getEndpointKeyHash();
        EndpointNotificationDto notification = generateUnicastNotificationDto(null, topicDto.getId(), keyHash);
        Assert.assertTrue(isNotBlank(notification.getId()));
        notificationService.removeUnicastNotificationById(notification.getId());
        EndpointNotificationDto notif = notificationService.findUnicastNotificationById(notification.getId());
        Assert.assertNull(notif);
    }

    @Test(expected = IncorrectParameterException.class)
    public void testSaveInvalidNotification() {
        NotificationDto notification = new NotificationDto();
        notificationService.saveNotification(notification);
    }

    @Test(expected = NumberFormatException.class)
    public void testSaveNotificationWithIncorrectIds() {
        NotificationDto notification = new NotificationDto();
        notification.setSchemaId(new ObjectId().toString());
        notification.setTopicId(new ObjectId().toString());
        notificationService.saveNotification(notification);
    }
}

