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
package org.kaaproject.kaa.server.sync.platform;


import BinaryEncDec.CONFIGURATION_EXTENSION_ID;
import BinaryEncDec.EVENT_EXTENSION_ID;
import BinaryEncDec.FAILURE;
import BinaryEncDec.LOGGING_EXTENSION_ID;
import BinaryEncDec.META_DATA_EXTENSION_ID;
import BinaryEncDec.NOTIFICATION_EXTENSION_ID;
import BinaryEncDec.PROFILE_EXTENSION_ID;
import BinaryEncDec.USER_EXTENSION_ID;
import Constants.KAA_PLATFORM_PROTOCOL_BINARY_ID;
import SubscriptionCommandType.ADD;
import SubscriptionCommandType.REMOVE;
import SyncStatus.PROFILE_RESYNC;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.Base64Util;
import org.kaaproject.kaa.server.sync.ClientSync;
import org.kaaproject.kaa.server.sync.ConfigurationClientSync;
import org.kaaproject.kaa.server.sync.Event;
import org.kaaproject.kaa.server.sync.EventClientSync;
import org.kaaproject.kaa.server.sync.EventSequenceNumberResponse;
import org.kaaproject.kaa.server.sync.EventServerSync;
import org.kaaproject.kaa.server.sync.LogClientSync;
import org.kaaproject.kaa.server.sync.LogServerSync;
import org.kaaproject.kaa.server.sync.NotificationClientSync;
import org.kaaproject.kaa.server.sync.ProfileClientSync;
import org.kaaproject.kaa.server.sync.ProfileServerSync;
import org.kaaproject.kaa.server.sync.ServerSync;
import org.kaaproject.kaa.server.sync.SyncResponseStatus;
import org.kaaproject.kaa.server.sync.SyncStatus;
import org.kaaproject.kaa.server.sync.UserAttachNotification;
import org.kaaproject.kaa.server.sync.UserClientSync;
import org.kaaproject.kaa.server.sync.UserServerSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BinaryEncDec.MAX_SUPPORTED_VERSION;
import static BinaryEncDec.MIN_SUPPORTED_VERSION;


public class BinaryEncDecTest {
    private static final Logger LOG = LoggerFactory.getLogger(BinaryEncDecTest.class);

    private static final int SHA_1_LENGTH = 20;

    private static final int MAGIC_NUMBER = 42;

    private static final int MAGIC_INDEX = 3;

    private static final short BIG_MAGIC_NUMBER = ((short) ((BinaryEncDecTest.MAGIC_NUMBER) * (BinaryEncDecTest.MAGIC_NUMBER)));

    private BinaryEncDec encDec;

    @Test(expected = PlatformEncDecException.class)
    public void testSmall() throws PlatformEncDecException {
        encDec.decode("small".getBytes());
    }

    @Test(expected = PlatformEncDecException.class)
    public void testWrongProtocol() throws PlatformEncDecException {
        encDec.decode(buildHeader(Integer.MAX_VALUE, 0, 0));
    }

    @Test(expected = PlatformEncDecException.class)
    public void testToOldVersion() throws PlatformEncDecException {
        encDec.decode(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, ((MIN_SUPPORTED_VERSION) - 1), 0));
    }

    @Test(expected = PlatformEncDecException.class)
    public void testVeryNewVersion() throws PlatformEncDecException {
        encDec.decode(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, ((MAX_SUPPORTED_VERSION) + 1), 0));
    }

    @Test(expected = PlatformEncDecException.class)
    public void testNoMetaData() throws PlatformEncDecException {
        encDec.decode(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 0));
    }

    @Test(expected = PlatformEncDecException.class)
    public void testSmallExtensionHeaderData() throws PlatformEncDecException {
        encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 1), "trash".getBytes()));
    }

    @Test(expected = PlatformEncDecException.class)
    public void testWrongPayloadLengthData() throws PlatformEncDecException {
        encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 1), buildExtensionHeader(META_DATA_EXTENSION_ID, 0, 0, 200)));
    }

    @Test
    public void testParseMetaDataWithNoOptions() throws PlatformEncDecException {
        byte[] md = new byte[4];
        md[0] = 1;
        md[1] = 2;
        md[2] = 3;
        md[3] = 4;
        ClientSync sync = encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 1), buildExtensionHeader(META_DATA_EXTENSION_ID, 0, 0, md.length), md));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
        Assert.assertEquals(((((((1 * 256) * 256) * 256) + ((2 * 256) * 256)) + (3 * 256)) + 4), sync.getRequestId());
    }

    @Test
    public void testParseMetaDataWithOptions() throws PlatformEncDecException {
        ClientSync sync = encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 1), getValidMetaData()));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
        Assert.assertEquals(1, sync.getRequestId());
        Assert.assertEquals(60L, sync.getClientSyncMetaData().getTimeout());
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, sync.getClientSyncMetaData().getEndpointPublicKeyHash().get(BinaryEncDecTest.MAGIC_INDEX));
        Assert.assertEquals(((BinaryEncDecTest.MAGIC_NUMBER) + 1), sync.getClientSyncMetaData().getProfileHash().get(BinaryEncDecTest.MAGIC_INDEX));
    }

    @Test
    public void testEncodeBasicServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(BinaryEncDecTest.MAGIC_NUMBER);
        sync.setStatus(PROFILE_RESYNC);
        ByteBuffer buf = ByteBuffer.wrap(encDec.encode(sync));
        // metadata
        int size = ((8// header
         + 8) + 4) + 4;
        Assert.assertEquals(size, buf.array().length);
        buf.position(((buf.capacity()) - 8));
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, buf.getInt());
        BinaryEncDecTest.LOG.trace(Arrays.toString(buf.array()));
    }

    @Test
    public void testEncodeProfileServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(BinaryEncDecTest.MAGIC_NUMBER);
        ProfileServerSync pSync = new ProfileServerSync(SyncResponseStatus.RESYNC);
        sync.setProfileSync(pSync);
        sync.setStatus(PROFILE_RESYNC);
        ByteBuffer buf = ByteBuffer.wrap(encDec.encode(sync));
        // profile sync
        int size = (((8// header
         + 8) + 4) + 4)// metadata
         + 8;
        Assert.assertEquals(size, buf.array().length);
        buf.position(((buf.capacity()) - 16));
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, buf.getInt());
        BinaryEncDecTest.LOG.trace(Arrays.toString(buf.array()));
    }

    @Test
    public void testEncodeLogServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(BinaryEncDecTest.MAGIC_NUMBER);
        LogServerSync lSync = new LogServerSync(Collections.singletonList(new org.kaaproject.kaa.server.sync.LogDeliveryStatus(BinaryEncDecTest.MAGIC_NUMBER, SyncStatus.FAILURE, null)));
        sync.setLogSync(lSync);
        sync.setStatus(PROFILE_RESYNC);
        ByteBuffer buf = ByteBuffer.wrap(encDec.encode(sync));
        // log sync
        int size = (((((8// header
         + 8) + 4) + 4)// metadata
         + 4) + 8) + 4;
        Assert.assertEquals(size, buf.array().length);
        buf.position(((buf.capacity()) - 4));
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, buf.getShort());
        buf.position(((buf.capacity()) - 2));
        Assert.assertEquals(FAILURE, buf.get());
        BinaryEncDecTest.LOG.trace(Arrays.toString(buf.array()));
    }

    @Test
    public void testEncodeUserServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(BinaryEncDecTest.MAGIC_NUMBER);
        UserServerSync uSync = new UserServerSync();
        uSync.setUserAttachNotification(new UserAttachNotification("id", "token"));
        sync.setUserSync(uSync);
        sync.setStatus(PROFILE_RESYNC);
        ByteBuffer buf = ByteBuffer.wrap(encDec.encode(sync));
        // user sync
        int size = ((((((8// header
         + 8) + 4) + 4)// metadata
         + 8) + 4) + 4) + 8;
        Assert.assertEquals(size, buf.array().length);
        BinaryEncDecTest.LOG.trace(Arrays.toString(buf.array()));
    }

    @Test
    public void testEncodeEventServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(BinaryEncDecTest.MAGIC_NUMBER);
        EventServerSync eSync = new EventServerSync();
        eSync.setEventSequenceNumberResponse(new EventSequenceNumberResponse(BinaryEncDecTest.MAGIC_NUMBER));
        Event event = new Event();
        event.setEventClassFqn("fqn");
        event.setSource(Base64Util.encode(new byte[BinaryEncDecTest.SHA_1_LENGTH]));
        eSync.setEvents(Collections.singletonList(event));
        sync.setEventSync(eSync);
        sync.setStatus(PROFILE_RESYNC);
        ByteBuffer buf = ByteBuffer.wrap(encDec.encode(sync));
        // event sync
        int size = ((((((((8// header
         + 8) + 4) + 4)// metadata
         + 8) + 4)// event header + seq number
         + 4) + 4) + (BinaryEncDecTest.SHA_1_LENGTH)) + 4;
        System.out.println(Arrays.toString(buf.array()));
        Assert.assertEquals(size, buf.array().length);
    }

    @Test
    public void testProfileClientSync() throws PlatformEncDecException {
        ByteBuffer buf = ByteBuffer.wrap(new byte[(((((4 + 100) + 4) + 4) + 128) + 4) + 8]);
        // profile length and data
        buf.putInt(100);
        byte[] profileBody = new byte[100];
        profileBody[BinaryEncDecTest.MAGIC_INDEX] = BinaryEncDecTest.MAGIC_NUMBER;
        buf.put(profileBody);
        // public key
        buf.put(((byte) (6)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (128)));
        byte[] keyBody = new byte[128];
        keyBody[BinaryEncDecTest.MAGIC_INDEX] = BinaryEncDecTest.MAGIC_NUMBER;
        buf.put(keyBody);
        // access token
        buf.put(((byte) (7)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (5)));
        buf.put("token".getBytes(Charset.forName("UTF-8")));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        ClientSync sync = encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 2), getValidMetaData(), buildExtensionHeader(PROFILE_EXTENSION_ID, 0, 0, buf.array().length), buf.array()));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
        Assert.assertNotNull(sync.getProfileSync());
        ProfileClientSync pSync = sync.getProfileSync();
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, pSync.getProfileBody().array()[BinaryEncDecTest.MAGIC_INDEX]);
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, pSync.getEndpointPublicKey().array()[BinaryEncDecTest.MAGIC_INDEX]);
        Assert.assertEquals("token", pSync.getEndpointAccessToken());
    }

    @Test
    public void testUserClientSync() throws PlatformEncDecException {
        ByteBuffer buf = ByteBuffer.wrap(new byte[(((((((((4 + 4) + 4) + 8) + 8) + 4) + 4) + 8) + 4) + 4) + (BinaryEncDecTest.SHA_1_LENGTH)]);
        // user assign request
        buf.put(((byte) (0)));
        buf.put(((byte) (4)));
        buf.put(((byte) (0)));
        buf.put(((byte) (5)));
        buf.put(((byte) (0)));
        buf.put(((byte) (8)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put("user".getBytes(Charset.forName("UTF-8")));
        buf.put("token".getBytes(Charset.forName("UTF-8")));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put("verifier".getBytes(Charset.forName("UTF-8")));
        // attach requests
        buf.put(((byte) (1)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (1)));
        buf.putShort(BinaryEncDecTest.BIG_MAGIC_NUMBER);
        buf.put(((byte) (0)));
        buf.put(((byte) (6)));
        buf.put("token2".getBytes(Charset.forName("UTF-8")));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        // detach requests
        buf.put(((byte) (2)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (1)));
        buf.putShort(((short) ((BinaryEncDecTest.BIG_MAGIC_NUMBER) + 1)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        byte[] keyHash = new byte[BinaryEncDecTest.SHA_1_LENGTH];
        keyHash[BinaryEncDecTest.MAGIC_INDEX] = BinaryEncDecTest.MAGIC_NUMBER;
        buf.put(keyHash);
        ClientSync sync = encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 2), getValidMetaData(), buildExtensionHeader(USER_EXTENSION_ID, 0, 0, buf.array().length), buf.array()));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
        Assert.assertNotNull(sync.getUserSync());
        UserClientSync uSync = sync.getUserSync();
        Assert.assertNotNull(uSync.getUserAttachRequest());
        Assert.assertEquals("user", uSync.getUserAttachRequest().getUserExternalId());
        Assert.assertEquals("token", uSync.getUserAttachRequest().getUserAccessToken());
        Assert.assertNotNull(uSync.getEndpointAttachRequests());
        Assert.assertEquals(1, uSync.getEndpointAttachRequests().size());
        Assert.assertEquals(BinaryEncDecTest.BIG_MAGIC_NUMBER, uSync.getEndpointAttachRequests().get(0).getRequestId());
        Assert.assertEquals("token2", uSync.getEndpointAttachRequests().get(0).getEndpointAccessToken());
        Assert.assertNotNull(uSync.getEndpointDetachRequests());
        Assert.assertEquals(1, uSync.getEndpointDetachRequests().size());
        Assert.assertEquals(((BinaryEncDecTest.BIG_MAGIC_NUMBER) + 1), uSync.getEndpointDetachRequests().get(0).getRequestId());
        Assert.assertEquals(Base64Util.encode(keyHash), uSync.getEndpointDetachRequests().get(0).getEndpointKeyHash());
    }

    @Test
    public void testLogClientSync() throws PlatformEncDecException {
        ByteBuffer buf = ByteBuffer.wrap(new byte[(4 + 4) + 128]);
        // user assign request
        buf.putShort(BinaryEncDecTest.BIG_MAGIC_NUMBER);
        buf.put(((byte) (0)));
        buf.put(((byte) (1)));
        buf.putInt(127);
        byte[] logData = new byte[127];
        logData[BinaryEncDecTest.MAGIC_NUMBER] = BinaryEncDecTest.MAGIC_NUMBER;
        buf.put(logData);
        buf.put(((byte) (0)));
        ClientSync sync = encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 2), getValidMetaData(), buildExtensionHeader(LOGGING_EXTENSION_ID, 0, 0, buf.array().length), buf.array()));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
        Assert.assertNotNull(sync.getLogSync());
        LogClientSync logSync = sync.getLogSync();
        Assert.assertEquals(BinaryEncDecTest.BIG_MAGIC_NUMBER, logSync.getRequestId());
        Assert.assertNotNull(logSync.getLogEntries());
        Assert.assertEquals(1, logSync.getLogEntries().size());
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, logSync.getLogEntries().get(0).getData().array()[BinaryEncDecTest.MAGIC_NUMBER]);
    }

    @Test
    public void testConfigurationClientSyncWithEmptyHash() throws PlatformEncDecException {
        ByteBuffer buf = ByteBuffer.wrap(new byte[4]);
        // user assign request
        buf.putInt(BinaryEncDecTest.MAGIC_NUMBER);
        ClientSync sync = encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 2), getValidMetaData(), buildExtensionHeader(CONFIGURATION_EXTENSION_ID, 0, 0, buf.array().length), buf.array()));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
        Assert.assertNotNull(sync.getConfigurationSync());
        ConfigurationClientSync cSync = sync.getConfigurationSync();
        Assert.assertNull(cSync.getConfigurationHash());
    }

    @Test
    public void testConfigurationClientSync() throws PlatformEncDecException {
        ByteBuffer buf = ByteBuffer.wrap(new byte[4 + (BinaryEncDecTest.SHA_1_LENGTH)]);
        // user assign request
        buf.putInt(BinaryEncDecTest.MAGIC_NUMBER);
        byte[] hash = new byte[BinaryEncDecTest.SHA_1_LENGTH];
        hash[BinaryEncDecTest.MAGIC_INDEX] = BinaryEncDecTest.MAGIC_NUMBER;
        buf.put(hash);
        ClientSync sync = encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 2), getValidMetaData(), buildExtensionHeader(CONFIGURATION_EXTENSION_ID, 0, 2, buf.array().length), buf.array()));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
        Assert.assertNotNull(sync.getConfigurationSync());
        ConfigurationClientSync cSync = sync.getConfigurationSync();
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, cSync.getConfigurationHash().array()[BinaryEncDecTest.MAGIC_INDEX]);
    }

    @Test
    public void testNotificationClientSync() throws PlatformEncDecException {
        ByteBuffer buf = ByteBuffer.wrap(// remove topic command
        new byte[((((((((((4// topic hash
         + 4) + 8) + 4)// topic list
         + 4) + 4) + 3) + 1)// unicast notifications
         + 4) + 8)// add topic command
         + 4) + 8]);
        // topic hash
        buf.putInt(BinaryEncDecTest.MAGIC_NUMBER);
        // topic list
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.putShort(((short) (1)));
        buf.putLong(303L);
        buf.putInt(BinaryEncDecTest.MAGIC_NUMBER);
        // unicast notifications
        buf.put(((byte) (1)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (1)));
        buf.putInt(3);
        buf.put("uid".getBytes(Charset.forName("UTF-8")));
        buf.put(((byte) (0)));
        // add topic command
        buf.put(((byte) (2)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (1)));
        buf.putLong(101);
        // remove topic command
        buf.put(((byte) (3)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (1)));
        buf.putLong(202);
        ClientSync sync = encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 2), getValidMetaData(), buildExtensionHeader(NOTIFICATION_EXTENSION_ID, 0, 2, buf.array().length), buf.array()));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
        Assert.assertNotNull(sync.getNotificationSync());
        NotificationClientSync nSync = sync.getNotificationSync();
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, nSync.getTopicListHash());
        Assert.assertNotNull(nSync.getAcceptedUnicastNotifications());
        Assert.assertEquals(1, nSync.getAcceptedUnicastNotifications().size());
        Assert.assertEquals("uid", nSync.getAcceptedUnicastNotifications().get(0));
        Assert.assertNotNull(nSync.getSubscriptionCommands());
        Assert.assertEquals(2, nSync.getSubscriptionCommands().size());
        Assert.assertEquals(ADD, nSync.getSubscriptionCommands().get(0).getCommand());
        Assert.assertEquals("101", nSync.getSubscriptionCommands().get(0).getTopicId());
        Assert.assertEquals(REMOVE, nSync.getSubscriptionCommands().get(1).getCommand());
        Assert.assertEquals("202", nSync.getSubscriptionCommands().get(1).getTopicId());
        Assert.assertNotNull(nSync.getTopicStates());
        Assert.assertEquals(1, nSync.getTopicStates().size());
        Assert.assertEquals("303", nSync.getTopicStates().get(0).getTopicId());
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, nSync.getTopicStates().get(0).getSeqNumber());
    }

    @Test
    public void testEventClientSync() throws PlatformEncDecException {
        ByteBuffer buf = ByteBuffer.wrap(new byte[((((((((((((4// listeners
         + 2) + 2) + 2) + 2) + 4)// listener
         + 4)// events
         + 4) + 2) + 2) + 4) + 4) + (BinaryEncDecTest.SHA_1_LENGTH)) + 100]);// event

        // listeners
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (1)));
        // listener
        buf.putShort(((short) (BinaryEncDecTest.MAGIC_NUMBER)));
        buf.putShort(((short) (1)));
        buf.putShort(((short) (4)));
        buf.putShort(((short) (0)));
        buf.put("name".getBytes(Charset.forName("UTF-8")));
        // events
        buf.put(((byte) (1)));
        buf.put(((byte) (0)));
        buf.put(((byte) (0)));
        buf.put(((byte) (1)));
        // event
        buf.putInt(BinaryEncDecTest.MAGIC_NUMBER);
        buf.putShort(((short) (3)));
        buf.putShort(((short) (4)));
        buf.putInt(100);
        byte[] hash = new byte[BinaryEncDecTest.SHA_1_LENGTH];
        hash[BinaryEncDecTest.MAGIC_INDEX] = BinaryEncDecTest.MAGIC_NUMBER;
        buf.put(hash);
        buf.put("name".getBytes(Charset.forName("UTF-8")));
        byte[] data = new byte[100];
        data[BinaryEncDecTest.MAGIC_INDEX] = BinaryEncDecTest.MAGIC_NUMBER;
        buf.put(data);
        ClientSync sync = encDec.decode(concat(buildHeader(KAA_PLATFORM_PROTOCOL_BINARY_ID, 1, 2), getValidMetaData(), buildExtensionHeader(EVENT_EXTENSION_ID, 0, 2, buf.array().length), buf.array()));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
        Assert.assertNotNull(sync.getEventSync());
        EventClientSync eSync = sync.getEventSync();
        Assert.assertEquals(true, eSync.isSeqNumberRequest());
        Assert.assertNotNull(eSync.getEventListenersRequests());
        Assert.assertEquals(1, eSync.getEventListenersRequests().size());
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, eSync.getEventListenersRequests().get(0).getRequestId());
        Assert.assertNotNull(eSync.getEventListenersRequests().get(0).getEventClassFqns());
        Assert.assertEquals("name", eSync.getEventListenersRequests().get(0).getEventClassFqns().get(0));
        Assert.assertNotNull(eSync.getEvents());
        Assert.assertEquals(1, eSync.getEvents().size());
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, eSync.getEvents().get(0).getSeqNum());
        Assert.assertEquals("name", eSync.getEvents().get(0).getEventClassFqn());
        Assert.assertEquals(Base64Util.encode(hash), eSync.getEvents().get(0).getTarget());
        Assert.assertEquals(BinaryEncDecTest.MAGIC_NUMBER, eSync.getEvents().get(0).getEventData().array()[BinaryEncDecTest.MAGIC_INDEX]);
    }
}

