package com.orientechnologies.orient.client.remote.message;


import ODatabaseType.MEMORY;
import ORecordSerializerNetworkV37.INSTANCE;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 09/05/17.
 */
public class ORemotePushMessagesTest {
    @Test
    public void testDistributedConfig() throws IOException {
        MockChannel channel = new MockChannel();
        List<String> hosts = new ArrayList<>();
        hosts.add("one");
        hosts.add("two");
        OPushDistributedConfigurationRequest request = new OPushDistributedConfigurationRequest(hosts);
        request.write(channel);
        channel.close();
        OPushDistributedConfigurationRequest readRequest = new OPushDistributedConfigurationRequest();
        readRequest.read(channel);
        Assert.assertEquals(readRequest.getHosts().size(), 2);
        Assert.assertEquals(readRequest.getHosts().get(0), "one");
        Assert.assertEquals(readRequest.getHosts().get(1), "two");
    }

    @Test
    public void testSchema() throws IOException {
        OrientDB orientDB = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDB.create("test", MEMORY);
        ODatabaseSession session = orientDB.open("test", "admin", "admin");
        ODocument schema = getSharedContext().getSchema().toStream();
        session.close();
        orientDB.close();
        MockChannel channel = new MockChannel();
        OPushSchemaRequest request = new OPushSchemaRequest(schema);
        request.write(channel);
        channel.close();
        OPushSchemaRequest readRequest = new OPushSchemaRequest();
        readRequest.read(channel);
        Assert.assertNotNull(readRequest.getSchema());
    }

    @Test
    public void testIndexManager() throws IOException {
        OrientDB orientDB = new OrientDB("embedded:", OrientDBConfig.defaultConfig());
        orientDB.create("test", MEMORY);
        ODatabaseSession session = orientDB.open("test", "admin", "admin");
        ODocument schema = getSharedContext().getIndexManager().toStream();
        session.close();
        orientDB.close();
        MockChannel channel = new MockChannel();
        OPushIndexManagerRequest request = new OPushIndexManagerRequest(schema);
        request.write(channel);
        channel.close();
        OPushIndexManagerRequest readRequest = new OPushIndexManagerRequest();
        readRequest.read(channel);
        Assert.assertNotNull(readRequest.getIndexManager());
    }

    @Test
    public void testSubscribeRequest() throws IOException {
        MockChannel channel = new MockChannel();
        OSubscribeRequest request = new OSubscribeRequest(new OSubscribeLiveQueryRequest("10", new HashMap()));
        request.write(channel, null);
        channel.close();
        OSubscribeRequest requestRead = new OSubscribeRequest();
        requestRead.read(channel, 1, INSTANCE);
        Assert.assertEquals(request.getPushMessage(), requestRead.getPushMessage());
        Assert.assertTrue(((requestRead.getPushRequest()) instanceof OSubscribeLiveQueryRequest));
    }

    @Test
    public void testSubscribeResponse() throws IOException {
        MockChannel channel = new MockChannel();
        OSubscribeResponse response = new OSubscribeResponse(new OSubscribeLiveQueryResponse(10));
        response.write(channel, 1, INSTANCE);
        channel.close();
        OSubscribeResponse responseRead = new OSubscribeResponse(new OSubscribeLiveQueryResponse());
        responseRead.read(channel, null);
        Assert.assertTrue(((responseRead.getResponse()) instanceof OSubscribeLiveQueryResponse));
        Assert.assertEquals(getMonitorId(), 10);
    }

    @Test
    public void testUnsubscribeRequest() throws IOException {
        MockChannel channel = new MockChannel();
        OUnsubscribeRequest request = new OUnsubscribeRequest(new OUnsubscribeLiveQueryRequest(10));
        request.write(channel, null);
        channel.close();
        OUnsubscribeRequest readRequest = new OUnsubscribeRequest();
        readRequest.read(channel, 0, null);
        Assert.assertEquals(getMonitorId(), 10);
    }
}

