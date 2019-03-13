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
package org.kaaproject.kaa.server.operations.service.akka.actors.core;


import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.NotificationDto;


public class TopicActorTest {
    private NotificationDto systemNf;

    private NotificationDto userNf;

    private NotificationDto unicastNf;

    @Test
    public void testIsSchemaVersionMatch() {
        Assert.assertTrue(TopicActor.isSchemaVersionMatch(systemNf, 42, 73));
        Assert.assertTrue(TopicActor.isSchemaVersionMatch(userNf, 42, 73));
        Assert.assertFalse(TopicActor.isSchemaVersionMatch(systemNf, 73, 42));
        Assert.assertFalse(TopicActor.isSchemaVersionMatch(userNf, 73, 42));
        Assert.assertFalse(TopicActor.isSchemaVersionMatch(unicastNf, 73, 42));
    }

    @Test
    public void testFilterMap() {
        SortedMap<Integer, NotificationDto> pendingNotificationMap = new TreeMap<>();
        pendingNotificationMap.put(1, systemNf);
        pendingNotificationMap.put(2, userNf);
        List<NotificationDto> actual = TopicActor.filterMap(pendingNotificationMap, 42, 42, new GregorianCalendar(TimeZone.getTimeZone("UTC")));
        Assert.assertNotNull(actual);
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(systemNf, actual.get(0));
    }
}

