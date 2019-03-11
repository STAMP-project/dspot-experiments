/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.notification;


import PayloadFormat.NONE;
import com.google.cloud.notification.NotificationInfo.PayloadFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.pubsub.v1.ProjectTopicName;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class NotificationInfoTest {
    private static final String ETAG = "0xFF00";

    private static final String GENERATED_ID = "B/N:1";

    private static final String SELF_LINK = "http://storage/b/n";

    private static final List<String> EVENT_TYPES = ImmutableList.of("OBJECT_FINALIZE", "OBJECT_METADATA_UPDATE");

    private static final String OBJECT_NAME_PREFIX = "index.html";

    private static final PayloadFormat PAYLOAD_FORMAT = PayloadFormat.JSON_API_V1;

    private static final ProjectTopicName TOPIC = ProjectTopicName.of("myProject", "topic1");

    private static final Map<String, String> CUSTOM_ATTRIBUTES = ImmutableMap.of("label1", "value1");

    private static final NotificationInfo NOTIFICATION_INFO = NotificationInfo.newBuilder(NotificationInfoTest.TOPIC).setEtag(NotificationInfoTest.ETAG).setCustomAttributes(NotificationInfoTest.CUSTOM_ATTRIBUTES).setSelfLink(NotificationInfoTest.SELF_LINK).setEventTypes(NotificationInfoTest.EVENT_TYPES).setObjectNamePrefix(NotificationInfoTest.OBJECT_NAME_PREFIX).setPayloadFormat(NotificationInfoTest.PAYLOAD_FORMAT).setGeneratedId(NotificationInfoTest.GENERATED_ID).build();

    @Test
    public void testToBuilder() {
        compareBuckets(NotificationInfoTest.NOTIFICATION_INFO, NotificationInfoTest.NOTIFICATION_INFO.toBuilder().build());
        NotificationInfo bucketInfo = NotificationInfoTest.NOTIFICATION_INFO.toBuilder().setGeneratedId("id").build();
        Assert.assertEquals("id", bucketInfo.getGeneratedId());
        bucketInfo = bucketInfo.toBuilder().setGeneratedId(NotificationInfoTest.GENERATED_ID).build();
        compareBuckets(NotificationInfoTest.NOTIFICATION_INFO, bucketInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        NotificationInfo incompleteBucketInfo = NotificationInfo.newBuilder(ProjectTopicName.of("myProject", "topic1")).build();
        compareBuckets(incompleteBucketInfo, incompleteBucketInfo.toBuilder().build());
    }

    @Test
    public void testOf() {
        NotificationInfo bucketInfo = NotificationInfo.of(ProjectTopicName.of("myProject", "topic1"));
        Assert.assertEquals(ProjectTopicName.of("myProject", "topic1"), bucketInfo.getTopic());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(NotificationInfoTest.ETAG, NotificationInfoTest.NOTIFICATION_INFO.getEtag());
        Assert.assertEquals(NotificationInfoTest.GENERATED_ID, NotificationInfoTest.NOTIFICATION_INFO.getGeneratedId());
        Assert.assertEquals(NotificationInfoTest.SELF_LINK, NotificationInfoTest.NOTIFICATION_INFO.getSelfLink());
        Assert.assertEquals(NotificationInfoTest.EVENT_TYPES, NotificationInfoTest.NOTIFICATION_INFO.getEventTypes());
        Assert.assertEquals(NotificationInfoTest.OBJECT_NAME_PREFIX, NotificationInfoTest.NOTIFICATION_INFO.getObjectNamePrefix());
        Assert.assertEquals(NotificationInfoTest.PAYLOAD_FORMAT, NotificationInfoTest.NOTIFICATION_INFO.getPayloadFormat());
        Assert.assertEquals(NotificationInfoTest.TOPIC, NotificationInfoTest.NOTIFICATION_INFO.getTopic());
        Assert.assertEquals(NotificationInfoTest.CUSTOM_ATTRIBUTES, NotificationInfoTest.NOTIFICATION_INFO.getCustomAttributes());
    }

    @Test
    public void testToPbAndFromPb() {
        compareBuckets(NotificationInfoTest.NOTIFICATION_INFO, NotificationInfo.fromPb(NotificationInfoTest.NOTIFICATION_INFO.toPb()));
        NotificationInfo bucketInfo = NotificationInfo.of(ProjectTopicName.of("myProject", "topic1")).toBuilder().setPayloadFormat(NONE).build();
        compareBuckets(bucketInfo, NotificationInfo.fromPb(bucketInfo.toPb()));
    }
}

