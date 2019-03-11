package org.web3j.protocol.websocket;


import WebSocketService.REQUEST_TIMEOUT;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.reactivestreams.Subscription;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthSubscribe;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.websocket.events.NewHeadsNotification;


public class WebSocketServiceTest {
    private static final int REQUEST_ID = 1;

    private WebSocketClient webSocketClient = Mockito.mock(WebSocketClient.class);

    private ScheduledExecutorService executorService = Mockito.mock(ScheduledExecutorService.class);

    private WebSocketService service = new WebSocketService(webSocketClient, executorService, true);

    private Request<?, Web3ClientVersion> request = new Request("web3_clientVersion", Collections.<String>emptyList(), service, Web3ClientVersion.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Request<Object, EthSubscribe> subscribeRequest;

    @Test
    public void testThrowExceptionIfServerUrlIsInvalid() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Failed to parse URL: \'invalid\\url\'");
        new WebSocketService("invalid\\url", true);
    }

    @Test
    public void testConnectViaWebSocketClient() throws Exception {
        service.connect();
        Mockito.verify(webSocketClient).connectBlocking();
    }

    @Test
    public void testInterruptCurrentThreadIfConnectionIsInterrupted() throws Exception {
        Mockito.when(webSocketClient.connectBlocking()).thenThrow(new InterruptedException());
        service.connect();
        Assert.assertTrue("Interrupted flag was not set properly", Thread.currentThread().isInterrupted());
    }

    @Test
    public void testThrowExceptionIfConnectionFailed() throws Exception {
        thrown.expect(ConnectException.class);
        thrown.expectMessage("Failed to connect to WebSocket");
        Mockito.when(webSocketClient.connectBlocking()).thenReturn(false);
        service.connect();
    }

    @Test
    public void testNotWaitingForReplyWithUnknownId() {
        Assert.assertFalse(service.isWaitingForReply(123));
    }

    @Test
    public void testWaitingForReplyToSentRequest() throws Exception {
        service.sendAsync(request, Web3ClientVersion.class);
        Assert.assertTrue(service.isWaitingForReply(request.getId()));
    }

    @Test
    public void testNoLongerWaitingForResponseAfterReply() throws Exception {
        service.sendAsync(request, Web3ClientVersion.class);
        sendGethVersionReply();
        Assert.assertFalse(service.isWaitingForReply(1));
    }

    @Test
    public void testSendWebSocketRequest() throws Exception {
        service.sendAsync(request, Web3ClientVersion.class);
        Mockito.verify(webSocketClient).send("{\"jsonrpc\":\"2.0\",\"method\":\"web3_clientVersion\",\"params\":[],\"id\":1}");
    }

    @Test
    public void testIgnoreInvalidReplies() throws Exception {
        thrown.expect(IOException.class);
        thrown.expectMessage("Failed to parse incoming WebSocket message");
        service.sendAsync(request, Web3ClientVersion.class);
        service.onWebSocketMessage("{");
    }

    @Test
    public void testThrowExceptionIfIdHasInvalidType() throws Exception {
        thrown.expect(IOException.class);
        thrown.expectMessage("'id' expected to be long, but it is: 'true'");
        service.sendAsync(request, Web3ClientVersion.class);
        service.onWebSocketMessage("{\"id\":true}");
    }

    @Test
    public void testThrowExceptionIfIdIsMissing() throws Exception {
        thrown.expect(IOException.class);
        thrown.expectMessage("Unknown message type");
        service.sendAsync(request, Web3ClientVersion.class);
        service.onWebSocketMessage("{}");
    }

    @Test
    public void testThrowExceptionIfUnexpectedIdIsReceived() throws Exception {
        thrown.expect(IOException.class);
        thrown.expectMessage("Received reply for unexpected request id: 12345");
        service.sendAsync(request, Web3ClientVersion.class);
        service.onWebSocketMessage("{\"jsonrpc\":\"2.0\",\"id\":12345,\"result\":\"geth-version\"}");
    }

    @Test
    public void testReceiveReply() throws Exception {
        CompletableFuture<Web3ClientVersion> reply = service.sendAsync(request, Web3ClientVersion.class);
        sendGethVersionReply();
        Assert.assertTrue(reply.isDone());
        Assert.assertEquals("geth-version", reply.get().getWeb3ClientVersion());
    }

    @Test
    public void testReceiveError() throws Exception {
        CompletableFuture<Web3ClientVersion> reply = service.sendAsync(request, Web3ClientVersion.class);
        sendErrorReply();
        Assert.assertTrue(reply.isDone());
        Web3ClientVersion version = reply.get();
        Assert.assertTrue(version.hasError());
        Assert.assertEquals(new Response.Error((-1), "Error message"), version.getError());
    }

    @Test
    public void testCloseRequestWhenConnectionIsClosed() throws Exception {
        thrown.expect(ExecutionException.class);
        CompletableFuture<Web3ClientVersion> reply = service.sendAsync(request, Web3ClientVersion.class);
        service.onWebSocketClose();
        Assert.assertTrue(reply.isDone());
        reply.get();
    }

    @Test(expected = ExecutionException.class)
    public void testCancelRequestAfterTimeout() throws Exception {
        Mockito.when(executorService.schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(REQUEST_TIMEOUT), ArgumentMatchers.eq(TimeUnit.SECONDS))).then(( invocation) -> {
            Runnable runnable = invocation.getArgumentAt(0, .class);
            runnable.run();
            return null;
        });
        CompletableFuture<Web3ClientVersion> reply = service.sendAsync(request, Web3ClientVersion.class);
        Assert.assertTrue(reply.isDone());
        reply.get();
    }

    @Test
    public void testSyncRequest() throws Exception {
        CountDownLatch requestSent = new CountDownLatch(1);
        // Wait for a request to be sent
        Mockito.doAnswer(( invocation) -> {
            requestSent.countDown();
            return null;
        }).when(webSocketClient).send(ArgumentMatchers.anyString());
        // Send reply asynchronously
        runAsync(() -> {
            try {
                requestSent.await(2, TimeUnit.SECONDS);
                sendGethVersionReply();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Web3ClientVersion reply = service.send(request, Web3ClientVersion.class);
        Assert.assertEquals(reply.getWeb3ClientVersion(), "geth-version");
    }

    @Test
    public void testCloseWebSocketOnClose() throws Exception {
        service.close();
        Mockito.verify(webSocketClient).close();
        Mockito.verify(executorService).shutdown();
    }

    @Test
    public void testSendSubscriptionReply() throws Exception {
        subscribeToEvents();
        verifyStartedSubscriptionHadnshake();
    }

    @Test
    public void testPropagateSubscriptionEvent() throws Exception {
        CountDownLatch eventReceived = new CountDownLatch(1);
        CountDownLatch disposed = new CountDownLatch(1);
        AtomicReference<NewHeadsNotification> actualNotificationRef = new AtomicReference<>();
        runAsync(() -> {
            Disposable disposable = subscribeToEvents().subscribe(( newHeadsNotification) -> {
                actualNotificationRef.set(newHeadsNotification);
                eventReceived.countDown();
            });
            try {
                eventReceived.await(2, TimeUnit.SECONDS);
                disposable.dispose();
                disposed.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        sendSubscriptionConfirmation();
        sendWebSocketEvent();
        Assert.assertTrue(disposed.await(6, TimeUnit.SECONDS));
        Assert.assertEquals("0xd9263f42a87", actualNotificationRef.get().getParams().getResult().getDifficulty());
    }

    @Test
    public void testSendUnsubscribeRequest() throws Exception {
        CountDownLatch unsubscribed = new CountDownLatch(1);
        runAsync(() -> {
            Flowable<NewHeadsNotification> flowable = subscribeToEvents();
            flowable.subscribe().dispose();
            unsubscribed.countDown();
        });
        sendSubscriptionConfirmation();
        sendWebSocketEvent();
        Assert.assertTrue(unsubscribed.await(2, TimeUnit.SECONDS));
        verifyUnsubscribed();
    }

    @Test
    public void testStopWaitingForSubscriptionReplyAfterTimeout() throws Exception {
        CountDownLatch errorReceived = new CountDownLatch(1);
        AtomicReference<Throwable> actualThrowable = new AtomicReference<>();
        runAsync(() -> subscribeToEvents().subscribe(new org.reactivestreams.Subscriber<NewHeadsNotification>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                actualThrowable.set(e);
                errorReceived.countDown();
            }

            @Override
            public void onSubscribe(Subscription s) {
            }

            @Override
            public void onNext(NewHeadsNotification newHeadsNotification) {
            }
        }));
        waitForRequestSent();
        Exception e = new IOException("timeout");
        service.closeRequest(1, e);
        Assert.assertTrue(errorReceived.await(2, TimeUnit.SECONDS));
        Assert.assertEquals(e, actualThrowable.get());
    }

    @Test
    public void testOnErrorCalledIfConnectionClosed() throws Exception {
        CountDownLatch errorReceived = new CountDownLatch(1);
        AtomicReference<Throwable> actualThrowable = new AtomicReference<>();
        runAsync(() -> subscribeToEvents().subscribe(new org.reactivestreams.Subscriber<NewHeadsNotification>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                actualThrowable.set(e);
                errorReceived.countDown();
            }

            @Override
            public void onSubscribe(Subscription s) {
            }

            @Override
            public void onNext(NewHeadsNotification newHeadsNotification) {
            }
        }));
        waitForRequestSent();
        sendSubscriptionConfirmation();
        service.onWebSocketClose();
        Assert.assertTrue(errorReceived.await(2, TimeUnit.SECONDS));
        Assert.assertEquals(IOException.class, actualThrowable.get().getClass());
        Assert.assertEquals("Connection was closed", actualThrowable.get().getMessage());
    }

    @Test
    public void testIfCloseObserverIfSubscriptionRequestFailed() throws Exception {
        CountDownLatch errorReceived = new CountDownLatch(1);
        AtomicReference<Throwable> actualThrowable = new AtomicReference<>();
        runAsync(() -> subscribeToEvents().subscribe(new org.reactivestreams.Subscriber<NewHeadsNotification>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                actualThrowable.set(e);
                errorReceived.countDown();
            }

            @Override
            public void onSubscribe(Subscription s) {
            }

            @Override
            public void onNext(NewHeadsNotification newHeadsNotification) {
            }
        }));
        waitForRequestSent();
        sendErrorReply();
        Assert.assertTrue(errorReceived.await(2, TimeUnit.SECONDS));
        Throwable throwable = actualThrowable.get();
        Assert.assertEquals(IOException.class, throwable.getClass());
        Assert.assertEquals("Subscription request failed with error: Error message", throwable.getMessage());
    }
}

