/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.server.resources.streaming;


import CaptureType.ALL;
import CloseCodes.PROTOCOL_ERROR;
import CloseCodes.UNEXPECTED_CONDITION;
import JsonMapper.INSTANCE;
import Schema.OPTIONAL_FLOAT32_SCHEMA;
import Schema.STRING_SCHEMA;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.rest.server.resources.streaming.Flow.Subscription;
import java.io.IOException;
import java.util.Map;
import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class WebSocketSubscriberTest {
    private static final ObjectMapper mapper = INSTANCE.mapper;

    private final Subscription subscription = EasyMock.mock(Subscription.class);

    private final Session session = EasyMock.mock(Session.class);

    private final Async async = EasyMock.mock(Async.class);

    private final Basic basic = EasyMock.mock(Basic.class);

    private final WebSocketSubscriber<Map<String, Object>> subscriber = new WebSocketSubscriber(session, WebSocketSubscriberTest.mapper);

    @Test
    public void testSanity() throws Exception {
        replayOnSubscribe();
        EasyMock.expect(session.getAsyncRemote()).andReturn(async).anyTimes();
        final Capture<String> json = EasyMock.newCapture(ALL);
        async.sendText(EasyMock.capture(json), EasyMock.anyObject());
        EasyMock.expectLastCall().times(3);
        subscription.request(1);
        EasyMock.expectLastCall().once();
        session.close(EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        subscription.cancel();
        EasyMock.replay(subscription, session, async);
        subscriber.onNext(ImmutableList.of(ImmutableMap.of("a", 1), ImmutableMap.of("b", 2), ImmutableMap.of("c", 3)));
        Assert.assertEquals(ImmutableList.of("{\"a\":1}", "{\"b\":2}", "{\"c\":3}"), json.getValues());
        subscriber.onComplete();
        subscriber.close();
        EasyMock.verify(subscription, session, async);
    }

    @Test
    public void testStopSendingAfterClose() {
        replayOnSubscribe();
        EasyMock.expect(session.getAsyncRemote()).andReturn(async).anyTimes();
        final Capture<String> json = EasyMock.newCapture(ALL);
        async.sendText(EasyMock.capture(json), EasyMock.anyObject());
        subscription.request(1);
        subscription.cancel();
        EasyMock.replay(subscription, session, async);
        subscriber.onNext(ImmutableList.of(ImmutableMap.of("a", 1)));
        subscriber.close();
        subscriber.onNext(ImmutableList.of(ImmutableMap.of("b", 2), ImmutableMap.of("c", 3)));
        Assert.assertEquals(ImmutableList.of("{\"a\":1}"), json.getValues());
        EasyMock.verify(subscription, session, async);
    }

    @Test
    public void testOnSchema() throws Exception {
        replayOnSubscribe();
        session.getBasicRemote();
        EasyMock.expectLastCall().andReturn(basic).once();
        final Capture<String> schema = EasyMock.newCapture();
        basic.sendText(EasyMock.capture(schema));
        EasyMock.expectLastCall().andThrow(new IOException("bad bad io")).once();
        final Capture<CloseReason> reason = EasyMock.newCapture();
        session.close(EasyMock.capture(reason));
        subscription.cancel();
        EasyMock.replay(subscription, session, basic);
        subscriber.onSchema(SchemaBuilder.struct().field("currency", STRING_SCHEMA).field("amount", OPTIONAL_FLOAT32_SCHEMA).build());
        subscriber.close();
        Assert.assertEquals(("[" + ((("{\"name\":\"currency\"," + "\"schema\":{\"type\":\"STRING\",\"fields\":null,\"memberSchema\":null}},") + "{\"name\":\"amount\",") + "\"schema\":{\"type\":\"DOUBLE\",\"fields\":null,\"memberSchema\":null}}]")), schema.getValue());
        Assert.assertEquals("Unable to send schema", reason.getValue().getReasonPhrase());
        Assert.assertEquals(PROTOCOL_ERROR, reason.getValue().getCloseCode());
        EasyMock.verify(subscription, session, basic);
    }

    @Test
    public void testOnError() throws Exception {
        replayOnSubscribe();
        final Capture<CloseReason> reason = EasyMock.newCapture();
        EasyMock.expect(session.getId()).andReturn("abc123").once();
        session.close(EasyMock.capture(reason));
        EasyMock.expectLastCall().once();
        subscription.cancel();
        EasyMock.expectLastCall().once();
        EasyMock.replay(subscription, session);
        subscriber.onError(new RuntimeException("streams died"));
        subscriber.close();
        Assert.assertEquals("streams exception", reason.getValue().getReasonPhrase());
        Assert.assertEquals(UNEXPECTED_CONDITION, reason.getValue().getCloseCode());
        EasyMock.verify(subscription, session);
    }
}

