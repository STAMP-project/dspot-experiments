package io.reactivex.netty.protocol.http.client.events;


import ClientEvent.AcquireStart;
import ClientEvent.AcquireSuccess;
import ClientEvent.ConnectStart;
import ClientEvent.ConnectSuccess;
import Event.BytesRead;
import Event.FlushStart;
import Event.FlushSuccess;
import Event.WriteStart;
import Event.WriteSuccess;
import io.reactivex.netty.protocol.http.server.HttpServerRule;
import org.junit.Rule;
import org.junit.Test;


public class HttpClientEventsTest {
    @Rule
    public final HttpServerRule serverRule = new HttpServerRule();

    @Test(timeout = 60000)
    public void testEventsPublished() throws Exception {
        HttpClientEventsListenerImpl listener = sendRequests(false);
        listener.getTcpDelegate().assertMethodCalled(ConnectStart);
        listener.getTcpDelegate().assertMethodCalled(ConnectSuccess);
        listener.getTcpDelegate().assertMethodCalled(WriteStart);
        listener.getTcpDelegate().assertMethodCalled(WriteSuccess);
        listener.getTcpDelegate().assertMethodCalled(FlushStart);
        listener.getTcpDelegate().assertMethodCalled(FlushSuccess);
        listener.getTcpDelegate().assertMethodCalled(BytesRead);
    }

    @Test(timeout = 60000)
    public void testPooledEventsPublished() throws Exception {
        HttpClientEventsListenerImpl listener = sendRequests(true);
        listener.getTcpDelegate().assertMethodCalled(AcquireStart);
        listener.getTcpDelegate().assertMethodCalled(AcquireSuccess);
        listener.getTcpDelegate().assertMethodCalled(ConnectStart);
        listener.getTcpDelegate().assertMethodCalled(ConnectSuccess);
        listener.getTcpDelegate().assertMethodCalled(WriteStart);
        listener.getTcpDelegate().assertMethodCalled(WriteSuccess);
        listener.getTcpDelegate().assertMethodCalled(FlushStart);
        listener.getTcpDelegate().assertMethodCalled(FlushSuccess);
        listener.getTcpDelegate().assertMethodCalled(BytesRead);
    }
}

