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
package org.kaaproject.kaa.server.operations.service.akka.messages.core.endpoint;


import TransportType.BOOTSTRAP;
import TransportType.CONFIGURATION;
import TransportType.EVENT;
import TransportType.LOGGING;
import TransportType.NOTIFICATION;
import TransportType.PROFILE;
import TransportType.USER;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.Constants;
import org.kaaproject.kaa.server.sync.ClientSync;
import org.kaaproject.kaa.server.sync.ConfigurationClientSync;
import org.kaaproject.kaa.server.sync.EventClientSync;
import org.kaaproject.kaa.server.sync.LogClientSync;
import org.kaaproject.kaa.server.sync.NotificationClientSync;
import org.kaaproject.kaa.server.sync.ProfileClientSync;
import org.kaaproject.kaa.server.sync.UserClientSync;
import org.kaaproject.kaa.server.transport.channel.ChannelType;
import org.kaaproject.kaa.server.transport.session.SessionInfo;


public class SyncRequestMessageTest {
    private static final SessionInfo SESSION = new SessionInfo(UUID.randomUUID(), Constants.KAA_PLATFORM_PROTOCOL_AVRO_ID, null, ChannelType.SYNC, null, null, "applicationToken", "sdkToken", 0, true);

    private static final NotificationClientSync NOTIFICATION_CLIENT_SYNC = new NotificationClientSync(2, null, null, null);

    private static final ConfigurationClientSync CONFIGURATION_CLIENT_SYNC = new ConfigurationClientSync(ByteBuffer.wrap("String".getBytes()), false);

    @Test
    public void testIsValid() {
        ClientSync request = new ClientSync();
        SyncRequestMessage message = createSyncRequestMessage(request);
        ClientSync other = new ClientSync();
        SyncRequestMessage otherMessage = createSyncRequestMessage(other);
        Assert.assertFalse(message.isValid(BOOTSTRAP));
        Assert.assertFalse(message.isValid(PROFILE));
        other.setProfileSync(new ProfileClientSync());
        message.merge(otherMessage);
        Assert.assertTrue(message.isValid(PROFILE));
        Assert.assertFalse(message.isValid(CONFIGURATION));
        other.setConfigurationSync(SyncRequestMessageTest.CONFIGURATION_CLIENT_SYNC);
        message.merge(otherMessage);
        Assert.assertTrue(message.isValid(CONFIGURATION));
        Assert.assertFalse(message.isValid(NOTIFICATION));
        other.setNotificationSync(SyncRequestMessageTest.NOTIFICATION_CLIENT_SYNC);
        message.merge(otherMessage);
        Assert.assertTrue(message.isValid(NOTIFICATION));
        Assert.assertFalse(message.isValid(USER));
        other.setUserSync(new UserClientSync());
        message.merge(otherMessage);
        Assert.assertTrue(message.isValid(USER));
        Assert.assertFalse(message.isValid(LOGGING));
        other.setLogSync(new LogClientSync());
        message.merge(otherMessage);
        Assert.assertTrue(message.isValid(LOGGING));
        Assert.assertFalse(message.isValid(EVENT));
        other.setEventSync(new EventClientSync());
        message.merge(otherMessage);
        Assert.assertTrue(message.isValid(EVENT));
    }

    @Test
    public void testMergeWithProfileSync() {
        ClientSync request = new ClientSync();
        SyncRequestMessage message = createSyncRequestMessage(request);
        ClientSync other = new ClientSync();
        SyncRequestMessage otherMessage = createSyncRequestMessage(other);
        request.setNotificationSync(SyncRequestMessageTest.NOTIFICATION_CLIENT_SYNC);
        other.setProfileSync(new ProfileClientSync());
        other.setNotificationSync(SyncRequestMessageTest.NOTIFICATION_CLIENT_SYNC);
        other.setConfigurationSync(SyncRequestMessageTest.CONFIGURATION_CLIENT_SYNC);
        Assert.assertNotNull(message.merge(otherMessage).getConfigurationSync());
        Assert.assertNotNull(message.merge(otherMessage).getNotificationSync());
        other.setConfigurationSync(null);
        other.setNotificationSync(null);
        Assert.assertNotNull(message.merge(otherMessage).getConfigurationSync());
        Assert.assertNotNull(message.merge(otherMessage).getNotificationSync());
    }

    @Test
    public void testMergeWithoutProfileSync() {
        ClientSync request = new ClientSync();
        SyncRequestMessage message = createSyncRequestMessage(request);
        ClientSync other = new ClientSync();
        SyncRequestMessage otherMessage = createSyncRequestMessage(other);
        request.setNotificationSync(SyncRequestMessageTest.NOTIFICATION_CLIENT_SYNC);
        other.setNotificationSync(SyncRequestMessageTest.NOTIFICATION_CLIENT_SYNC);
        Assert.assertNull(message.merge(otherMessage).getConfigurationSync());
        Assert.assertNull(message.merge(otherMessage).getNotificationSync());
    }
}

