/**
 * Copyright 2019 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.streaming;


import HttpStatus.OK;
import MediaType.JSON_SEQ;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.stream.Stream;
import org.junit.ClassRule;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


public class JsonTextSequencesTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @ClassRule
    public static ServerRule rule = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.service("/seq/publisher", ( ctx, req) -> JsonTextSequences.fromPublisher(Flux.just("foo", "bar", "baz", "qux"))).service("/seq/stream", ( ctx, req) -> JsonTextSequences.fromStream(Stream.of("foo", "bar", "baz", "qux"), MoreExecutors.directExecutor())).service("/seq/custom-mapper", ( ctx, req) -> JsonTextSequences.fromPublisher(Flux.just("foo", "bar", "baz", "qux"), new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT))).service("/seq/single", ( ctx, req) -> JsonTextSequences.fromObject("foo"));
        }
    };

    @Test
    public void fromPublisherOrStream() {
        final HttpClient client = HttpClient.of(JsonTextSequencesTest.rule.uri("/seq"));
        for (final String path : ImmutableList.of("/publisher", "/stream", "/custom-mapper")) {
            final HttpResponse response = client.get(path);
            StepVerifier.create(response).expectNext(HttpHeaders.of(OK).contentType(JSON_SEQ)).assertNext(( o) -> ensureExpectedHttpData(o, "foo")).assertNext(( o) -> ensureExpectedHttpData(o, "bar")).assertNext(( o) -> ensureExpectedHttpData(o, "baz")).assertNext(( o) -> ensureExpectedHttpData(o, "qux")).assertNext(this::assertThatLastContent).expectComplete().verify();
        }
    }

    @Test
    public void singleSequence() {
        final AggregatedHttpMessage response = HttpClient.of(JsonTextSequencesTest.rule.uri("/seq")).get("/single").aggregate().join();
        assertThat(response.status()).isEqualTo(OK);
        assertThat(response.headers().contentType()).isEqualTo(JSON_SEQ);
        // Check whether the content is serialized as a JSON Text Sequence format.
        assertThat(response.content().array()).containsExactly(30, '"', 'f', 'o', 'o', '"', 10);
    }
}

