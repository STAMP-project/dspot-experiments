/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron;


import ControlProtocolEvents.ON_CLIENT_TIMEOUT;
import ControlProtocolEvents.ON_ERROR;
import ControlProtocolEvents.ON_OPERATION_SUCCESS;
import ControlProtocolEvents.ON_PUBLICATION_READY;
import ControlProtocolEvents.ON_SUBSCRIPTION_READY;
import io.aeron.exceptions.ConductorServiceTimeoutException;
import io.aeron.exceptions.DriverTimeoutException;
import io.aeron.exceptions.RegistrationException;
import io.aeron.exceptions.TimeoutException;
import io.aeron.logbuffer.LogBufferDescriptor;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.agrona.ErrorHandler;
import org.agrona.concurrent.broadcast.CopyBroadcastReceiver;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ClientConductorTest {
    private static final int TERM_BUFFER_LENGTH = LogBufferDescriptor.TERM_MIN_LENGTH;

    private static final int SESSION_ID_1 = 13;

    private static final int SESSION_ID_2 = 15;

    private static final String CHANNEL = "aeron:udp?endpoint=localhost:40124";

    private static final int STREAM_ID_1 = 2;

    private static final int STREAM_ID_2 = 4;

    private static final int SEND_BUFFER_CAPACITY = 1024;

    private static final int COUNTER_BUFFER_LENGTH = 1024;

    private static final long CORRELATION_ID = 2000;

    private static final long CORRELATION_ID_2 = 2002;

    private static final long CLOSE_CORRELATION_ID = 2001;

    private static final long UNKNOWN_CORRELATION_ID = 3000;

    private static final long KEEP_ALIVE_INTERVAL = TimeUnit.MILLISECONDS.toNanos(500);

    private static final long AWAIT_TIMEOUT = 100;

    private static final long INTER_SERVICE_TIMEOUT_MS = 1000;

    private static final int SUBSCRIPTION_POSITION_ID = 2;

    private static final int SUBSCRIPTION_POSITION_REGISTRATION_ID = 4001;

    private static final String SOURCE_INFO = "127.0.0.1:40789";

    private final PublicationBuffersReadyFlyweight publicationReady = new PublicationBuffersReadyFlyweight();

    private final SubscriptionReadyFlyweight subscriptionReady = new SubscriptionReadyFlyweight();

    private final OperationSucceededFlyweight operationSuccess = new OperationSucceededFlyweight();

    private final ErrorResponseFlyweight errorResponse = new ErrorResponseFlyweight();

    private final ClientTimeoutFlyweight clientTimeout = new ClientTimeoutFlyweight();

    private final UnsafeBuffer publicationReadyBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(ClientConductorTest.SEND_BUFFER_CAPACITY));

    private final UnsafeBuffer subscriptionReadyBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(ClientConductorTest.SEND_BUFFER_CAPACITY));

    private final UnsafeBuffer operationSuccessBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(ClientConductorTest.SEND_BUFFER_CAPACITY));

    private final UnsafeBuffer errorMessageBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(ClientConductorTest.SEND_BUFFER_CAPACITY));

    private final UnsafeBuffer clientTimeoutBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(ClientConductorTest.SEND_BUFFER_CAPACITY));

    private final CopyBroadcastReceiver mockToClientReceiver = Mockito.mock(CopyBroadcastReceiver.class);

    private final UnsafeBuffer counterValuesBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(ClientConductorTest.COUNTER_BUFFER_LENGTH));

    private long timeMs = 0;

    private final EpochClock epochClock = () -> timeMs += 10;

    private long timeNs = 0;

    private final NanoClock nanoClock = () -> timeNs += 10000000;

    private final ErrorHandler mockClientErrorHandler = Mockito.spy(new ClientConductorTest.PrintError());

    private ClientConductor conductor;

    private final DriverProxy driverProxy = Mockito.mock(DriverProxy.class);

    private final AvailableImageHandler mockAvailableImageHandler = Mockito.mock(AvailableImageHandler.class);

    private final UnavailableImageHandler mockUnavailableImageHandler = Mockito.mock(UnavailableImageHandler.class);

    private final LogBuffersFactory logBuffersFactory = Mockito.mock(LogBuffersFactory.class);

    private final Lock mockClientLock = Mockito.mock(Lock.class);

    private boolean suppressPrintError = false;

    // --------------------------------
    // Publication related interactions
    // --------------------------------
    @Test
    public void addPublicationShouldNotifyMediaDriver() {
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> publicationReady.length());
        conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        Mockito.verify(driverProxy).addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
    }

    @Test
    public void addPublicationShouldMapLogFile() {
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> publicationReady.length());
        conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        Mockito.verify(logBuffersFactory).map(((ClientConductorTest.SESSION_ID_1) + "-log"));
    }

    @Test(expected = DriverTimeoutException.class, timeout = 5000)
    public void addPublicationShouldTimeoutWithoutReadyMessage() {
        conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
    }

    @Test
    public void closingPublicationShouldNotifyMediaDriver() {
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> publicationReady.length());
        final Publication publication = conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        whenReceiveBroadcastOnMessage(ON_OPERATION_SUCCESS, operationSuccessBuffer, ( buffer) -> OperationSucceededFlyweight.LENGTH);
        publication.close();
        Mockito.verify(driverProxy).removePublication(ClientConductorTest.CORRELATION_ID);
    }

    @Test
    public void closingPublicationShouldPurgeCache() {
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> publicationReady.length());
        final Publication firstPublication = conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        whenReceiveBroadcastOnMessage(ON_OPERATION_SUCCESS, operationSuccessBuffer, ( buffer) -> OperationSucceededFlyweight.LENGTH);
        firstPublication.close();
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> publicationReady.length());
        final Publication secondPublication = conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        MatcherAssert.assertThat(firstPublication, Matchers.not(Matchers.sameInstance(secondPublication)));
    }

    @Test(expected = RegistrationException.class)
    public void shouldFailToClosePublicationOnMediaDriverError() {
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> publicationReady.length());
        final Publication publication = conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        whenReceiveBroadcastOnMessage(ON_ERROR, errorMessageBuffer, ( buffer) -> {
            errorResponse.errorCode(INVALID_CHANNEL);
            errorResponse.errorMessage("channel unknown");
            errorResponse.offendingCommandCorrelationId(CLOSE_CORRELATION_ID);
            return errorResponse.length();
        });
        publication.close();
    }

    @Test(expected = RegistrationException.class)
    public void shouldFailToAddPublicationOnMediaDriverError() {
        whenReceiveBroadcastOnMessage(ON_ERROR, errorMessageBuffer, ( buffer) -> {
            errorResponse.errorCode(INVALID_CHANNEL);
            errorResponse.errorMessage("invalid channel");
            errorResponse.offendingCommandCorrelationId(CORRELATION_ID);
            return errorResponse.length();
        });
        conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
    }

    @Test
    public void closingPublicationDoesNotRemoveOtherPublications() {
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> publicationReady.length());
        final Publication publication = conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> {
            publicationReady.streamId(STREAM_ID_2);
            publicationReady.sessionId(SESSION_ID_2);
            publicationReady.logFileName(((SESSION_ID_2) + "-log"));
            publicationReady.correlationId(CORRELATION_ID_2);
            publicationReady.registrationId(CORRELATION_ID_2);
            return publicationReady.length();
        });
        conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_2);
        whenReceiveBroadcastOnMessage(ON_OPERATION_SUCCESS, operationSuccessBuffer, ( buffer) -> OperationSucceededFlyweight.LENGTH);
        publication.close();
        Mockito.verify(driverProxy).removePublication(ClientConductorTest.CORRELATION_ID);
        Mockito.verify(driverProxy, Mockito.never()).removePublication(ClientConductorTest.CORRELATION_ID_2);
    }

    @Test
    public void shouldNotMapBuffersForUnknownCorrelationId() {
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> {
            publicationReady.correlationId(UNKNOWN_CORRELATION_ID);
            publicationReady.registrationId(UNKNOWN_CORRELATION_ID);
            return publicationReady.length();
        });
        whenReceiveBroadcastOnMessage(ON_PUBLICATION_READY, publicationReadyBuffer, ( buffer) -> {
            publicationReady.correlationId(CORRELATION_ID);
            return publicationReady.length();
        });
        final Publication publication = conductor.addPublication(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        conductor.doWork();
        Mockito.verify(logBuffersFactory, Mockito.times(1)).map(ArgumentMatchers.anyString());
        MatcherAssert.assertThat(publication.registrationId(), Is.is(ClientConductorTest.CORRELATION_ID));
    }

    // ---------------------------------
    // Subscription related interactions
    // ---------------------------------
    @Test
    public void addSubscriptionShouldNotifyMediaDriver() {
        whenReceiveBroadcastOnMessage(ON_SUBSCRIPTION_READY, subscriptionReadyBuffer, ( buffer) -> {
            subscriptionReady.correlationId(CORRELATION_ID);
            return SubscriptionReadyFlyweight.LENGTH;
        });
        conductor.addSubscription(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        Mockito.verify(driverProxy).addSubscription(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
    }

    @Test
    public void closingSubscriptionShouldNotifyMediaDriver() {
        whenReceiveBroadcastOnMessage(ON_SUBSCRIPTION_READY, subscriptionReadyBuffer, ( buffer) -> {
            subscriptionReady.correlationId(CORRELATION_ID);
            return SubscriptionReadyFlyweight.LENGTH;
        });
        final Subscription subscription = conductor.addSubscription(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        whenReceiveBroadcastOnMessage(ON_OPERATION_SUCCESS, operationSuccessBuffer, ( buffer) -> {
            operationSuccess.correlationId(CLOSE_CORRELATION_ID);
            return CorrelatedMessageFlyweight.LENGTH;
        });
        subscription.close();
        Mockito.verify(driverProxy).removeSubscription(ClientConductorTest.CORRELATION_ID);
    }

    @Test(expected = DriverTimeoutException.class, timeout = 5000)
    public void addSubscriptionShouldTimeoutWithoutOperationSuccessful() {
        conductor.addSubscription(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
    }

    @Test(expected = RegistrationException.class)
    public void shouldFailToAddSubscriptionOnMediaDriverError() {
        whenReceiveBroadcastOnMessage(ON_ERROR, errorMessageBuffer, ( buffer) -> {
            errorResponse.errorCode(INVALID_CHANNEL);
            errorResponse.errorMessage("invalid channel");
            errorResponse.offendingCommandCorrelationId(CORRELATION_ID);
            return errorResponse.length();
        });
        conductor.addSubscription(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
    }

    @Test
    public void clientNotifiedOfNewImageShouldMapLogFile() {
        whenReceiveBroadcastOnMessage(ON_SUBSCRIPTION_READY, subscriptionReadyBuffer, ( buffer) -> {
            subscriptionReady.correlationId(CORRELATION_ID);
            return SubscriptionReadyFlyweight.LENGTH;
        });
        final Subscription subscription = conductor.addSubscription(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        conductor.onAvailableImage(ClientConductorTest.CORRELATION_ID, ClientConductorTest.SESSION_ID_1, subscription.registrationId(), ClientConductorTest.SUBSCRIPTION_POSITION_ID, ((ClientConductorTest.SESSION_ID_1) + "-log"), ClientConductorTest.SOURCE_INFO);
        Mockito.verify(logBuffersFactory).map(ArgumentMatchers.eq(((ClientConductorTest.SESSION_ID_1) + "-log")));
    }

    @Test
    public void clientNotifiedOfNewAndInactiveImages() {
        whenReceiveBroadcastOnMessage(ON_SUBSCRIPTION_READY, subscriptionReadyBuffer, ( buffer) -> {
            subscriptionReady.correlationId(CORRELATION_ID);
            return SubscriptionReadyFlyweight.LENGTH;
        });
        final Subscription subscription = conductor.addSubscription(ClientConductorTest.CHANNEL, ClientConductorTest.STREAM_ID_1);
        conductor.onAvailableImage(ClientConductorTest.CORRELATION_ID, ClientConductorTest.SESSION_ID_1, subscription.registrationId(), ClientConductorTest.SUBSCRIPTION_POSITION_ID, ((ClientConductorTest.SESSION_ID_1) + "-log"), ClientConductorTest.SOURCE_INFO);
        Assert.assertFalse(subscription.hasNoImages());
        Assert.assertTrue(subscription.isConnected());
        Mockito.verify(mockAvailableImageHandler).onAvailableImage(ArgumentMatchers.any(Image.class));
        conductor.onUnavailableImage(ClientConductorTest.CORRELATION_ID, subscription.registrationId());
        Mockito.verify(mockUnavailableImageHandler).onUnavailableImage(ArgumentMatchers.any(Image.class));
        Assert.assertTrue(subscription.hasNoImages());
        Assert.assertFalse(subscription.isConnected());
    }

    @Test
    public void shouldIgnoreUnknownNewImage() {
        conductor.onAvailableImage(ClientConductorTest.CORRELATION_ID_2, ClientConductorTest.SESSION_ID_2, ClientConductorTest.SUBSCRIPTION_POSITION_REGISTRATION_ID, ClientConductorTest.SUBSCRIPTION_POSITION_ID, ((ClientConductorTest.SESSION_ID_2) + "-log"), ClientConductorTest.SOURCE_INFO);
        Mockito.verify(logBuffersFactory, Mockito.never()).map(ArgumentMatchers.anyString());
        Mockito.verify(mockAvailableImageHandler, Mockito.never()).onAvailableImage(ArgumentMatchers.any(Image.class));
    }

    @Test
    public void shouldIgnoreUnknownInactiveImage() {
        conductor.onUnavailableImage(ClientConductorTest.CORRELATION_ID_2, ClientConductorTest.SUBSCRIPTION_POSITION_REGISTRATION_ID);
        Mockito.verify(logBuffersFactory, Mockito.never()).map(ArgumentMatchers.anyString());
        Mockito.verify(mockUnavailableImageHandler, Mockito.never()).onUnavailableImage(ArgumentMatchers.any(Image.class));
    }

    @Test
    public void shouldTimeoutInterServiceIfTooLongBetweenDoWorkCalls() {
        suppressPrintError = true;
        conductor.doWork();
        timeNs += (TimeUnit.MILLISECONDS.toNanos(ClientConductorTest.INTER_SERVICE_TIMEOUT_MS)) + 1;
        conductor.doWork();
        Mockito.verify(mockClientErrorHandler).onError(ArgumentMatchers.any(ConductorServiceTimeoutException.class));
        Assert.assertTrue(conductor.isTerminating());
    }

    @Test
    public void shouldTerminateAndErrorOnClientTimeoutFromDriver() {
        suppressPrintError = true;
        conductor.onClientTimeout();
        Mockito.verify(mockClientErrorHandler).onError(ArgumentMatchers.any(TimeoutException.class));
        boolean threwException = false;
        try {
            conductor.doWork();
        } catch (final AgentTerminationException ex) {
            threwException = true;
        }
        Assert.assertTrue(threwException);
        Assert.assertTrue(conductor.isTerminating());
    }

    @Test
    public void shouldNotCloseAndErrorOnClientTimeoutForAnotherClientIdFromDriver() {
        whenReceiveBroadcastOnMessage(ON_CLIENT_TIMEOUT, clientTimeoutBuffer, ( buffer) -> {
            clientTimeout.clientId(((conductor.driverListenerAdapter().clientId()) + 1));
            return ClientTimeoutFlyweight.LENGTH;
        });
        conductor.doWork();
        Mockito.verify(mockClientErrorHandler, Mockito.never()).onError(ArgumentMatchers.any(TimeoutException.class));
        Assert.assertFalse(conductor.isClosed());
    }

    class PrintError implements ErrorHandler {
        public void onError(final Throwable throwable) {
            if (!(suppressPrintError)) {
                throwable.printStackTrace();
            }
        }
    }
}

