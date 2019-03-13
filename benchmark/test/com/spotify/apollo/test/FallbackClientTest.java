/**
 * -\-\-
 * Spotify Apollo Testing Helpers
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
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
package com.spotify.apollo.test;


import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.environment.IncomingRequestAwareClient;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import okio.ByteString;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FallbackClientTest {
    FallbackClient fallbackClient;

    StubClient stubClient;

    @Test
    public void shouldFallBackIfNoResponseFound() throws Exception {
        stubClient.respond(Response.forPayload(ByteString.encodeUtf8("Hello World"))).to("http://ping");
        final String mockReply = call("http://ping");
        final String fixedReply1 = call("http://pong");
        final String fixedReply2 = call("http://bang");
        Assert.assertThat(mockReply, Matchers.is("Hello World"));
        Assert.assertThat(fixedReply1, Matchers.is("I am pretty rigid"));
        Assert.assertThat(fixedReply2, Matchers.is("I am pretty rigid"));
    }

    private static final class FixedReplyClient implements IncomingRequestAwareClient {
        private final String reply;

        public FixedReplyClient(String reply) {
            this.reply = reply;
        }

        private Response<ByteString> reply() {
            return Response.forPayload(ByteString.encodeUtf8(reply));
        }

        @Override
        public CompletionStage<Response<ByteString>> send(Request request, Optional<Request> incoming) {
            return CompletableFuture.completedFuture(reply());
        }
    }
}

