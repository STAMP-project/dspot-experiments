/**
 * -\-\-
 * Spotify Apollo Extra
 * --
 * Copyright (C) 2013 - 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.apollo.logging.extra;


import com.spotify.apollo.Request;
import com.spotify.apollo.RequestMetadata;
import com.spotify.apollo.Response;
import com.spotify.apollo.dispatch.Endpoint;
import com.spotify.apollo.request.OngoingRequest;
import com.spotify.apollo.request.RequestMetadataImpl;
import com.spotify.apollo.request.RequestRunnableFactory;
import com.spotify.apollo.route.RuleMatch;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;


public class RequestLoggingDecoratorTest {
    private static final String MAGIC_TIMESTAMP = "TIMESTAMP";

    private static final BiConsumer<OngoingRequest, RuleMatch<Endpoint>> EMPTY_CONTINUATION = ( ongoingRequest1, endpointRuleMatch) -> {
    };

    public static final String REMOTE_IP = "1.2.3.4";

    private RequestLoggingDecorator decorator;

    private RequestRunnableFactory delegateFactory;

    private final Request request = Request.forUri("http://tessting");

    private OngoingRequest ongoingRequest = new RequestLoggingDecoratorTest.FakeRequest(request);

    private TestLogger testLogger = TestLoggerFactory.getTestLogger(RequestLoggingDecorator.class);

    @Rule
    public TestLoggerFactoryResetRule resetRule = new TestLoggerFactoryResetRule();

    @Test
    public void shouldLogRequestAndResponseByDefault() throws Exception {
        List<LoggingEvent> events = collectLoggingEventsForRequest(ongoingRequest);
        Assert.assertThat(events, CoreMatchers.is(singleEventMatching("{} - - {} \"{}\" {} {} \"{}\" \"{}\"", RequestLoggingDecoratorTest.REMOTE_IP, RequestLoggingDecoratorTest.MAGIC_TIMESTAMP, "GET http://tessting", "200", "-", "-", "-")));
    }

    @Test
    public void shouldLogUserAgentIfPresent() throws Exception {
        ongoingRequest = new RequestLoggingDecoratorTest.FakeRequest(Request.forUri("http://hi").withHeader("User-Agent", "007"));
        List<LoggingEvent> events = collectLoggingEventsForRequest(ongoingRequest);
        Assert.assertThat(events.size(), CoreMatchers.is(1));
        Assert.assertThat(events.get(0).getArguments().get(6), CoreMatchers.is("007"));
    }

    @Test
    public void shouldLogRefererIfPresent() throws Exception {
        ongoingRequest = new RequestLoggingDecoratorTest.FakeRequest(Request.forUri("http://hi").withHeader("Referer", "www.spotify.com"));
        List<LoggingEvent> events = collectLoggingEventsForRequest(ongoingRequest);
        Assert.assertThat(events.size(), CoreMatchers.is(1));
        Assert.assertThat(events.get(0).getArguments().get(5), CoreMatchers.is("www.spotify.com"));
    }

    @Test
    public void shouldSendRequestAndResponseToConsumerIfConfigured() throws Exception {
        AtomicReference<Request> reference = new AtomicReference<>();
        decorator.setLogger(( request, response) -> reference.set(request.request()));
        decorator.apply(delegateFactory).create(ongoingRequest).run(RequestLoggingDecoratorTest.EMPTY_CONTINUATION);
        Assert.assertThat(reference.get(), CoreMatchers.is(request));
    }

    @Test
    public void shouldLogSizeInBytesIfPresent() throws Exception {
        delegateFactory = ( ongoingRequest) -> ( matchContinuation) -> ongoingRequest.reply(Response.forPayload(encodeUtf8("7 bytes")));
        List<LoggingEvent> events = collectLoggingEventsForRequest(ongoingRequest);
        Assert.assertThat(events, CoreMatchers.is(singleEventMatching("{} - - {} \"{}\" {} {} \"{}\" \"{}\"", RequestLoggingDecoratorTest.REMOTE_IP, RequestLoggingDecoratorTest.MAGIC_TIMESTAMP, "GET http://tessting", "200", "7", "-", "-")));
    }

    @Test
    public void shouldLogDashIfNoReply() throws Exception {
        delegateFactory = ( ongoingRequest) -> ( matchContinuation) -> ongoingRequest.drop();
        List<LoggingEvent> events = collectLoggingEventsForRequest(ongoingRequest);
        Assert.assertThat(events, CoreMatchers.is(singleEventMatching("{} - - {} \"{}\" {} {} \"{}\" \"{}\"", RequestLoggingDecoratorTest.REMOTE_IP, RequestLoggingDecoratorTest.MAGIC_TIMESTAMP, "GET http://tessting", "-", "-", "-", "-")));
    }

    private static class FakeRequest implements OngoingRequest {
        private final Request request;

        private FakeRequest(Request request) {
            this.request = request;
        }

        @Override
        public Request request() {
            return request;
        }

        @Override
        public void reply(Response<ByteString> response) {
        }

        @Override
        public void drop() {
        }

        @Override
        public boolean isExpired() {
            return false;
        }

        @Override
        public RequestMetadata metadata() {
            return new RequestMetadata() {
                @Override
                public Instant arrivalTime() {
                    return Instant.now();
                }

                @Override
                public Optional<HostAndPort> localAddress() {
                    return null;
                }

                @Override
                public Optional<HostAndPort> remoteAddress() {
                    return Optional.of(new RequestMetadataImpl.HostAndPortImpl() {
                        @Override
                        public String host() {
                            return RequestLoggingDecoratorTest.REMOTE_IP;
                        }

                        @Override
                        public int port() {
                            return 0;
                        }
                    });
                }
            };
        }
    }
}

