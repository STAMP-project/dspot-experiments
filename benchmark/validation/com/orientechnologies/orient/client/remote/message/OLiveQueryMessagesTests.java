package com.orientechnologies.orient.client.remote.message;


import OErrorCode.GENERIC_ERROR;
import OLiveQueryPushRequest.END;
import OLiveQueryPushRequest.ERROR;
import com.orientechnologies.common.exception.OErrorCode;
import com.orientechnologies.orient.client.remote.message.live.OLiveQueryResult;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.ORecordSerializerNetworkV37;
import com.orientechnologies.orient.core.sql.executor.OResultInternal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static OLiveQueryPushRequest.END;


/**
 * Created by tglman on 17/05/17.
 */
public class OLiveQueryMessagesTests {
    @Test
    public void testRequestWriteRead() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("par", "value");
        OSubscribeLiveQueryRequest request = new OSubscribeLiveQueryRequest("select from Some", params);
        MockChannel channel = new MockChannel();
        request.write(channel, null);
        channel.close();
        OSubscribeLiveQueryRequest requestRead = new OSubscribeLiveQueryRequest();
        requestRead.read(channel, (-1), new ORecordSerializerNetworkV37());
        Assert.assertEquals(requestRead.getQuery(), "select from Some");
        Assert.assertEquals(requestRead.getParams(), params);
    }

    @Test
    public void testSubscribeResponseWriteRead() throws IOException {
        OSubscribeLiveQueryResponse response = new OSubscribeLiveQueryResponse(20);
        MockChannel channel = new MockChannel();
        response.write(channel, 0, null);
        channel.close();
        OSubscribeLiveQueryResponse responseRead = new OSubscribeLiveQueryResponse();
        responseRead.read(channel, null);
        Assert.assertEquals(responseRead.getMonitorId(), 20);
    }

    @Test
    public void testLiveQueryErrorPushRequest() throws IOException {
        OLiveQueryPushRequest pushRequest = new OLiveQueryPushRequest(10, 20, OErrorCode.GENERIC_ERROR, "the message");
        MockChannel channel = new MockChannel();
        pushRequest.write(channel);
        channel.close();
        OLiveQueryPushRequest pushRequestRead = new OLiveQueryPushRequest();
        pushRequestRead.read(channel);
        Assert.assertEquals(pushRequestRead.getMonitorId(), 10);
        Assert.assertEquals(pushRequestRead.getStatus(), ERROR);
        Assert.assertEquals(pushRequestRead.getErrorIdentifier(), 20);
        Assert.assertEquals(pushRequestRead.getErrorCode(), GENERIC_ERROR);
        Assert.assertEquals(pushRequestRead.getErrorMessage(), "the message");
    }

    @Test
    public void testLiveQueryPushRequest() throws IOException {
        List<OLiveQueryResult> events = new ArrayList<>();
        OResultInternal res = new OResultInternal();
        res.setProperty("one", "one");
        res.setProperty("two", 10);
        events.add(new OLiveQueryResult(OLiveQueryResult.CREATE_EVENT, res, null));
        events.add(new OLiveQueryResult(OLiveQueryResult.UPDATE_EVENT, new OResultInternal(), new OResultInternal()));
        events.add(new OLiveQueryResult(OLiveQueryResult.DELETE_EVENT, new OResultInternal(), null));
        OLiveQueryPushRequest pushRequest = new OLiveQueryPushRequest(10, END, events);
        MockChannel channel = new MockChannel();
        pushRequest.write(channel);
        channel.close();
        OLiveQueryPushRequest pushRequestRead = new OLiveQueryPushRequest();
        pushRequestRead.read(channel);
        Assert.assertEquals(pushRequestRead.getMonitorId(), 10);
        Assert.assertEquals(pushRequestRead.getStatus(), END);
        Assert.assertEquals(pushRequestRead.getEvents().size(), 3);
        Assert.assertEquals(pushRequestRead.getEvents().get(0).getCurrentValue().getProperty("one"), "one");
        Assert.assertEquals(((int) (pushRequestRead.getEvents().get(0).getCurrentValue().getProperty("two"))), 10);
    }
}

