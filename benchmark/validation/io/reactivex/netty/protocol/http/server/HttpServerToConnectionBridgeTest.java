package io.reactivex.netty.protocol.http.server;


import io.reactivex.netty.protocol.http.internal.AbstractHttpConnectionBridge;
import io.reactivex.netty.protocol.http.internal.AbstractHttpConnectionBridgeTest;
import io.reactivex.netty.protocol.tcp.server.events.TcpServerEventPublisher;
import java.nio.channels.ClosedChannelException;
import org.junit.Rule;
import org.junit.Test;
import rx.observers.TestSubscriber;


public class HttpServerToConnectionBridgeTest {
    @Rule
    public final AbstractHttpConnectionBridgeTest.HandlerRule handlerRule = new AbstractHttpConnectionBridgeTest.HandlerRule() {
        @Override
        protected AbstractHttpConnectionBridge<String> newAbstractHttpConnectionBridgeMock() {
            return new HttpServerToConnectionBridge(new io.reactivex.netty.protocol.http.server.events.HttpServerEventPublisher(new TcpServerEventPublisher()));
        }
    };

    @Test(timeout = 60000)
    public void testPendingContentSubscriber() throws Exception {
        handlerRule.setupAndAssertConnectionInputSub();
        handlerRule.simulateHeaderReceive();/* Simulate header receive, required for content sub. */

        TestSubscriber<String> subscriber = new TestSubscriber();
        handlerRule.getChannel().pipeline().fireUserEventTriggered(new io.reactivex.netty.protocol.http.internal.HttpContentSubscriberEvent(subscriber));
        TestSubscriber<String> subscriber1 = new TestSubscriber();
        handlerRule.getChannel().pipeline().fireUserEventTriggered(new io.reactivex.netty.protocol.http.internal.HttpContentSubscriberEvent(subscriber1));
        subscriber.assertNoErrors();
        subscriber1.assertNoErrors();
        subscriber.unsubscribe();
        subscriber.assertUnsubscribed();
        handlerRule.getChannel().close().await();
        subscriber.assertNoErrors();
        subscriber1.assertError(ClosedChannelException.class);
    }
}

