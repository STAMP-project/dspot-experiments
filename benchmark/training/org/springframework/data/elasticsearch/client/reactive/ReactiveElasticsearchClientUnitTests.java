/**
 * Copyright 2018-2019 the original author or authors.
 *
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
 */
package org.springframework.data.elasticsearch.client.reactive;


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.POST;
import HttpMethod.PUT;
import VersionType.EXTERNAL;
import XContentType.JSON;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Mono;

import static org.springframework.data.elasticsearch.client.reactive.ReactiveMockClientTestsUtils.MockWebClientProvider.Receive.error;
import static org.springframework.data.elasticsearch.client.reactive.ReactiveMockClientTestsUtils.MockWebClientProvider.Receive.fromPath;
import static org.springframework.data.elasticsearch.client.reactive.ReactiveMockClientTestsUtils.MockWebClientProvider.Receive.json;
import static org.springframework.data.elasticsearch.client.reactive.ReactiveMockClientTestsUtils.MockWebClientProvider.Receive.ok;


/**
 *
 *
 * @author Christoph Strobl
 * @unknown Golden Fool - Robin Hobb
 */
public class ReactiveElasticsearchClientUnitTests {
    static final String HOST = ":9200";

    ReactiveMockClientTestsUtils.MockDelegatingElasticsearchHostProvider<HostProvider> hostProvider;

    ReactiveElasticsearchClient client;

    // DATAES-512
    @Test
    public void sendRequestShouldCarryOnRequestParameters() {
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveDeleteOk();
        DeleteRequest request = new DeleteRequest("index", "type", "id");
        request.version(1000);
        request.versionType(EXTERNAL);
        request.timeout(TimeValue.timeValueMinutes(10));
        // 
        // 
        // 
        client.delete(request).then().as(StepVerifier::create).verifyComplete();
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        // 
        // 
        // 
        assertThat(uri.getQuery()).contains("version=1000").contains("version_type=external").contains("timeout=10m");
    }

    // --> PING
    @Test
    public void pingShouldHitMainEndpoint() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::ok);
        // 
        // 
        // 
        client.ping().then().as(StepVerifier::create).verifyComplete();
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/");
    }

    // DATAES-488
    @Test
    public void pingShouldReturnTrueOnHttp200() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::ok);
        // 
        // 
        // 
        client.ping().as(StepVerifier::create).expectNext(true).verifyComplete();
    }

    // DATAES-488
    @Test
    public void pingShouldReturnFalseOnNonHttp200() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::error);
        // 
        // 
        // 
        client.ping().as(StepVerifier::create).expectNext(false).verifyComplete();
    }

    // --> INFO
    @Test
    public void infoShouldHitMainEndpoint() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveInfo();
        // 
        // 
        // 
        client.info().then().as(StepVerifier::create).verifyComplete();
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/");
    }

    // DATAES-488
    @Test
    public void infoShouldReturnResponseCorrectly() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveInfo();
        // 
        // 
        // 
        client.info().as(StepVerifier::create).consumeNextWith(( mainResponse) -> {
        }).verifyComplete();
    }

    // --> GET
    // DATAES-488
    @Test
    public void getShouldHitGetEndpoint() {
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(( clientResponse) -> {
            when(clientResponse.statusCode()).thenReturn(HttpStatus.ACCEPTED, HttpStatus.NOT_FOUND);
        });
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveGetByIdNotFound();
        // 
        // 
        // 
        client.get(new GetRequest("twitter").id("1")).then().as(StepVerifier::create).verifyComplete();
        Mockito.verify(hostProvider.client(ReactiveElasticsearchClientUnitTests.HOST)).method(GET);
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/twitter/_all/1");
    }

    // DATAES-488
    @Test
    public void getShouldReturnExistingDocument() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveGetById();
        // 
        // 
        // 
        client.get(new GetRequest("twitter").id("1")).as(StepVerifier::create).consumeNextWith(( result) -> {
            assertThat(result.isExists()).isTrue();
            assertThat(result.getIndex()).isEqualTo("twitter");
            assertThat(result.getId()).isEqualTo("1");
            // 
            // 
            // 
            assertThat(result.getSource()).containsEntry("user", "kimchy").containsEntry("message", "Trying out Elasticsearch, so far so good?").containsKey("post_date");
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void getShouldReturnEmptyForNonExisting() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveGetByIdNotFound();
        // 
        // 
        client.get(new GetRequest("twitter").id("1")).as(StepVerifier::create).verifyComplete();
    }

    // --> MGET
    // DATAES-488
    @Test
    public void multiGetShouldHitMGetEndpoint() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveJsonFromFile("multi-get-ok-2-hits");
        // 
        // 
        // 
        client.multiGet(new MultiGetRequest().add("twitter", "_doc", "1").add("twitter", "_doc", "2")).then().as(StepVerifier::create).verifyComplete();
        Mockito.verify(hostProvider.client(ReactiveElasticsearchClientUnitTests.HOST)).method(POST);
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).exchange(( requestBodyUriSpec) -> {
            verify(requestBodyUriSpec).body(any(.class), any(.class));
        });
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/_mget");
    }

    // DATAES-488
    @Test
    public void multiGetShouldReturnExistingDocuments() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveJsonFromFile("multi-get-ok-2-hits");
        // 
        // 
        // 
        // 
        client.multiGet(new MultiGetRequest().add("twitter", "_doc", "1").add("twitter", "_doc", "2")).as(StepVerifier::create).consumeNextWith(( result) -> {
            assertThat(result.isExists()).isTrue();
            assertThat(result.getIndex()).isEqualTo("twitter");
            assertThat(result.getId()).isEqualTo("1");
            // 
            // 
            // 
            assertThat(result.getSource()).containsEntry("user", "kimchy").containsEntry("message", "Trying out Elasticsearch, so far so good?").containsKey("post_date");
        }).consumeNextWith(( result) -> {
            assertThat(result.isExists()).isTrue();
            assertThat(result.getIndex()).isEqualTo("twitter");
            assertThat(result.getId()).isEqualTo("2");
            // 
            // 
            // 
            assertThat(result.getSource()).containsEntry("user", "kimchy").containsEntry("message", "Another tweet, will it be indexed?").containsKey("post_date");
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void multiGetShouldWorkForNonExistingDocuments() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveJsonFromFile("multi-get-ok-2-hits-1-unavailable");
        // 
        // 
        // 
        // 
        client.multiGet(new MultiGetRequest().add("twitter", "_doc", "1").add("twitter", "_doc", "2")).as(StepVerifier::create).consumeNextWith(( result) -> {
            assertThat(result.isExists()).isTrue();
            assertThat(result.getIndex()).isEqualTo("twitter");
            assertThat(result.getId()).isEqualTo("1");
            // 
            // 
            // 
            assertThat(result.getSource()).containsEntry("user", "kimchy").containsEntry("message", "Trying out Elasticsearch, so far so good?").containsKey("post_date");
        }).consumeNextWith(( result) -> {
            assertThat(result.isExists()).isTrue();
            assertThat(result.getIndex()).isEqualTo("twitter");
            assertThat(result.getId()).isEqualTo("3");
            // 
            // 
            // 
            assertThat(result.getSource()).containsEntry("user", "elastic").containsEntry("message", "Building the site, should be kewl").containsKey("post_date");
        }).verifyComplete();
    }

    // --> EXISTS
    // DATAES-488
    @Test
    public void existsShouldHitGetEndpoint() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveGetById();
        // 
        // 
        // 
        client.exists(new GetRequest("twitter").id("1")).then().as(StepVerifier::create).verifyComplete();
        Mockito.verify(hostProvider.client(ReactiveElasticsearchClientUnitTests.HOST)).method(HEAD);
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/twitter/_all/1");
    }

    // DATAES-488
    @Test
    public void existsShouldReturnTrueIfExists() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveGetById();
        // 
        // 
        client.exists(new GetRequest("twitter").id("1")).as(StepVerifier::create).expectNext(true).verifyComplete();
    }

    // DATAES-488
    @Test
    public void existsShouldReturnFalseIfNotExists() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveGetByIdNotFound();
        // 
        // 
        client.exists(new GetRequest("twitter").id("1")).as(StepVerifier::create).expectNext(false).verifyComplete();
    }

    // --> INDEX
    // DATAES-488
    @Test
    public void indexNewShouldHitCreateEndpoint() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveIndexCreated();
        // 
        // 
        client.index(new IndexRequest("twitter").id("10").create(true).source(" { foo : \"bar\" }", JSON)).then().as(StepVerifier::create).verifyComplete();
        Mockito.verify(hostProvider.client(ReactiveElasticsearchClientUnitTests.HOST)).method(PUT);
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).exchange(( requestBodyUriSpec) -> {
            verify(requestBodyUriSpec).contentType(MediaType.APPLICATION_JSON);
        });
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/twitter/10/_create");
    }

    // DATAES-488
    @Test
    public void indexExistingShouldHitEndpointCorrectly() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveIndexUpdated();
        // 
        // 
        client.index(new IndexRequest("twitter").id("10").source(" { foo : \"bar\" }", JSON)).then().as(StepVerifier::create).verifyComplete();
        Mockito.verify(hostProvider.client(ReactiveElasticsearchClientUnitTests.HOST)).method(PUT);
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).exchange(( requestBodyUriSpec) -> {
            verify(requestBodyUriSpec).contentType(MediaType.APPLICATION_JSON);
        });
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/twitter/10");
    }

    // DATAES-488
    @Test
    public void indexShouldReturnCreatedWhenNewDocumentIndexed() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveIndexCreated();
        // 
        // 
        client.index(new IndexRequest("twitter").id("10").create(true).source(" { foo : \"bar\" }", JSON)).as(StepVerifier::create).consumeNextWith(( response) -> {
            assertThat(response.getId()).isEqualTo("10");
            assertThat(response.getIndex()).isEqualTo("twitter");
            assertThat(response.getResult()).isEqualTo(Result.CREATED);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void indexShouldReturnUpdatedWhenExistingDocumentIndexed() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveIndexUpdated();
        // 
        // 
        client.index(new IndexRequest("twitter").id("1").source(" { foo : \"bar\" }", JSON)).as(StepVerifier::create).consumeNextWith(( response) -> {
            assertThat(response.getId()).isEqualTo("1");
            assertThat(response.getIndex()).isEqualTo("twitter");
            assertThat(response.getResult()).isEqualTo(Result.UPDATED);
        }).verifyComplete();
    }

    // --> UPDATE
    // DATAES-488
    @Test
    public void updateShouldHitEndpointCorrectly() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveUpdateOk();
        // 
        // 
        client.update(new UpdateRequest("twitter", "doc", "1").doc(Collections.singletonMap("user", "cstrobl"))).then().as(StepVerifier::create).verifyComplete();
        Mockito.verify(hostProvider.client(ReactiveElasticsearchClientUnitTests.HOST)).method(POST);
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).exchange(( requestBodyUriSpec) -> {
            verify(requestBodyUriSpec).contentType(MediaType.APPLICATION_JSON);
        });
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/twitter/doc/1/_update");
    }

    // DATAES-488
    @Test
    public void updateShouldEmitResponseCorrectly() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveUpdateOk();
        // 
        // 
        client.update(new UpdateRequest("twitter", "doc", "1").doc(Collections.singletonMap("user", "cstrobl"))).as(StepVerifier::create).consumeNextWith(( updateResponse) -> {
            assertThat(updateResponse.getResult()).isEqualTo(Result.UPDATED);
            assertThat(updateResponse.getVersion()).isEqualTo(2);
            assertThat(updateResponse.getId()).isEqualTo("1");
            assertThat(updateResponse.getIndex()).isEqualTo("twitter");
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void updateShouldEmitErrorWhenNotFound() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).updateFail();
        // 
        // 
        client.update(new UpdateRequest("twitter", "doc", "1").doc(Collections.singletonMap("user", "cstrobl"))).as(StepVerifier::create).expectError(ElasticsearchStatusException.class).verify();
    }

    // --> DELETE
    // DATAES-488
    @Test
    public void deleteShouldHitEndpointCorrectly() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveDeleteOk();
        // 
        // 
        client.delete(new DeleteRequest("twitter", "doc", "1")).then().as(StepVerifier::create).verifyComplete();
        Mockito.verify(hostProvider.client(ReactiveElasticsearchClientUnitTests.HOST)).method(DELETE);
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/twitter/doc/1");
    }

    // DATAES-488
    @Test
    public void deleteShouldEmitResponseCorrectly() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveDeleteOk();
        // 
        // 
        // 
        client.delete(new DeleteRequest("twitter", "doc", "1")).as(StepVerifier::create).consumeNextWith(( deleteResponse) -> {
            assertThat(deleteResponse.getResult()).isEqualTo(Result.DELETED);
            assertThat(deleteResponse.getVersion()).isEqualTo(1);
            assertThat(deleteResponse.getId()).isEqualTo("1");
            assertThat(deleteResponse.getIndex()).isEqualTo("twitter");
        }).verifyComplete();
    }

    // --> SEARCH
    // DATAES-488
    @Test
    public void searchShouldHitSearchEndpoint() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveSearchOk();
        client.search(new SearchRequest("twitter")).as(StepVerifier::create).verifyComplete();
        Mockito.verify(hostProvider.client(ReactiveElasticsearchClientUnitTests.HOST)).method(POST);
        URI uri = hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).captureUri();
        assertThat(uri.getRawPath()).isEqualTo("/twitter/_search");
    }

    // DATAES-488
    @Test
    public void searchShouldReturnSingleResultCorrectly() {
        // 
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::json).body(fromPath("search-ok-single-hit"));
        // 
        // 
        client.search(new SearchRequest("twitter")).as(StepVerifier::create).consumeNextWith(( hit) -> {
            assertThat(hit.getId()).isEqualTo("2");
            assertThat(hit.getIndex()).isEqualTo("twitter");
            // 
            // 
            // 
            assertThat(hit.getSourceAsMap()).containsEntry("user", "kimchy").containsEntry("message", "Another tweet, will it be indexed?").containsKey("post_date");
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void searchShouldReturnMultipleResultsCorrectly() {
        // 
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::json).body(fromPath("search-ok-multiple-hits"));
        // 
        // 
        // 
        client.search(new SearchRequest("twitter")).as(StepVerifier::create).consumeNextWith(( hit) -> {
            assertThat(hit.getId()).isEqualTo("2");
            assertThat(hit.getIndex()).isEqualTo("twitter");
            // 
            // 
            // 
            assertThat(hit.getSourceAsMap()).containsEntry("user", "kimchy").containsEntry("message", "Another tweet, will it be indexed?").containsKey("post_date");
        }).consumeNextWith(( hit) -> {
            assertThat(hit.getId()).isEqualTo("1");
            assertThat(hit.getIndex()).isEqualTo("twitter");
            // 
            // 
            // 
            assertThat(hit.getSourceAsMap()).containsEntry("user", "kimchy").containsEntry("message", "Trying out Elasticsearch, so far so good?").containsKey("post_date");
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void searchShouldReturnEmptyFluxIfNothingFound() {
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receiveSearchOk();
        // 
        // 
        client.search(new SearchRequest("twitter")).as(StepVerifier::create).verifyComplete();
    }

    // --> SCROLL
    // DATAES-510
    @Test
    public void scrollShouldReadAll() throws IOException {
        byte[] start = StreamUtils.copyToByteArray(fromPath("search-ok-scroll").getInputStream());
        byte[] next = StreamUtils.copyToByteArray(fromPath("scroll_ok").getInputStream());
        byte[] end = StreamUtils.copyToByteArray(fromPath("scroll_no_more_results").getInputStream());
        byte[] cleanup = StreamUtils.copyToByteArray(fromPath("scroll_clean").getInputStream());
        // 
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::json).receive(( response) -> Mockito.Mockito.when(response.body(any())).thenReturn(Mono.just(start), Mono.just(next), Mono.just(end), Mono.just(cleanup)));
        // 
        // 
        // 
        client.scroll(new SearchRequest("twitter")).as(StepVerifier::create).expectNextCount(4).verifyComplete();
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(( response) -> {
            verify(response, times(4)).body(any());
        });
    }

    // DATAES-510
    @Test
    public void scrollShouldCleanUpResourcesOnError() throws IOException {
        byte[] start = StreamUtils.copyToByteArray(fromPath("search-ok-scroll").getInputStream());
        byte[] error = StreamUtils.copyToByteArray(fromPath("scroll_error").getInputStream());
        byte[] cleanup = StreamUtils.copyToByteArray(fromPath("scroll_clean").getInputStream());
        // 
        // 
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::json).receive(( response) -> Mockito.Mockito.when(response.body(any())).thenReturn(Mono.just(start), Mono.just(error), Mono.just(cleanup)));
        // 
        // 
        // 
        client.scroll(new SearchRequest("twitter")).as(StepVerifier::create).expectNextCount(2).verifyError();
        hostProvider.when(ReactiveElasticsearchClientUnitTests.HOST).receive(( response) -> {
            verify(response, times(3)).body(any());
        });
    }
}

