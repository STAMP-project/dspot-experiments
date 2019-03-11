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


import HttpHeaders.EMPTY;
import RequestOptions.DEFAULT;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.elasticsearch.ElasticsearchVersion;
import org.springframework.data.elasticsearch.ElasticsearchVersionRule;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @unknown Fool's Fate - Robin Hobb
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:infrastructure.xml")
public class ReactiveElasticsearchClientTests {
    @Rule
    public ElasticsearchVersionRule elasticsearchVersion = ElasticsearchVersionRule.any();

    static final String INDEX_I = "idx-1-reactive-client-tests";

    static final String INDEX_II = "idx-2-reactive-client-tests";

    static final String TYPE_I = "doc-type-1";

    static final String TYPE_II = "doc-type-2";

    static final Map<String, String> DOC_SOURCE;

    RestHighLevelClient syncClient;

    ReactiveElasticsearchClient client;

    static {
        Map<String, String> source = new LinkedHashMap<>();
        source.put("firstname", "chade");
        source.put("lastname", "fallstar");
        DOC_SOURCE = Collections.unmodifiableMap(source);
    }

    // DATAES-488
    @Test
    public void pingForActiveHostShouldReturnTrue() {
        // 
        // 
        client.ping().as(StepVerifier::create).expectNext(true).verifyComplete();
    }

    // DATAES-488
    @Test
    public void pingForUnknownHostShouldReturnFalse() {
        // 
        // 
        // 
        DefaultReactiveElasticsearchClient.create(ClientConfiguration.builder().connectedTo("localhost:4711").withConnectTimeout(Duration.ofSeconds(2)).build()).ping().as(StepVerifier::create).expectNext(false).verifyComplete();
    }

    // DATAES-488
    @Test
    public void infoShouldReturnClusterInformation() {
        // 
        // 
        client.info().as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.isAvailable()).isTrue();
            assertThat(it.getVersion()).isGreaterThanOrEqualTo(Version.CURRENT);
        }).verifyComplete();
    }

    // DATAES-519
    @Test
    public void getOnNonExistingIndexShouldThrowException() {
        client.get(new GetRequest(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, "nonono")).as(StepVerifier::create).expectError(ElasticsearchStatusException.class).verify();
    }

    // DATAES-488
    @Test
    public void getShouldFetchDocumentById() {
        String id = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        // 
        // 
        // 
        client.get(new GetRequest(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id)).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.getId()).isEqualTo(id);
            assertThat(it.getSource()).containsAllEntriesOf(DOC_SOURCE);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void getShouldCompleteForNonExistingDocuments() {
        addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        String id = "this-one-does-not-exist";
        // 
        // 
        client.get(new GetRequest(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id)).as(StepVerifier::create).verifyComplete();
    }

    // DATAES-488
    @Test
    public void getShouldCompleteForNonExistingType() {
        String id = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        // 
        // 
        client.get(new GetRequest(ReactiveElasticsearchClientTests.INDEX_I, "fantasy-books", id)).as(StepVerifier::create).verifyComplete();
    }

    // DATAES-488
    @Test
    public void multiGetShouldReturnAllDocumentsFromSameCollection() {
        String id1 = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        String id2 = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        MultiGetRequest request = // 
        // 
        new MultiGetRequest().add(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id1).add(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id2);
        // 
        // 
        // 
        // 
        client.multiGet(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.getId()).isEqualTo(id1);
        }).consumeNextWith(( it) -> {
            assertThat(it.getId()).isEqualTo(id2);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void multiGetShouldReturnAllExistingDocumentsFromSameCollection() {
        String id1 = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        MultiGetRequest request = // 
        // 
        new MultiGetRequest().add(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id1).add(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, "this-one-does-not-exist");
        // 
        // 
        // 
        client.multiGet(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.getId()).isEqualTo(id1);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void multiGetShouldSkipNonExistingDocuments() {
        String id1 = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        String id2 = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        MultiGetRequest request = // 
        // 
        // 
        new MultiGetRequest().add(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id1).add(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, "this-one-does-not-exist").add(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id2);// 

        // 
        // 
        // 
        // 
        client.multiGet(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.getId()).isEqualTo(id1);
        }).consumeNextWith(( it) -> {
            assertThat(it.getId()).isEqualTo(id2);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void multiGetShouldCompleteIfNothingFound() {
        String id1 = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        String id2 = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        // 
        // 
        client.multiGet(new MultiGetRequest().add(ReactiveElasticsearchClientTests.INDEX_II, ReactiveElasticsearchClientTests.TYPE_I, id1).add(ReactiveElasticsearchClientTests.INDEX_II, ReactiveElasticsearchClientTests.TYPE_I, id2)).as(StepVerifier::create).verifyComplete();
    }

    // DATAES-488
    @Test
    public void multiGetShouldReturnAllExistingDocumentsFromDifferentCollection() {
        String id1 = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        String id2 = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_II).to(ReactiveElasticsearchClientTests.INDEX_II);
        MultiGetRequest request = // 
        // 
        new MultiGetRequest().add(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id1).add(ReactiveElasticsearchClientTests.INDEX_II, ReactiveElasticsearchClientTests.TYPE_II, id2);
        // 
        // 
        // 
        // 
        client.multiGet(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.getId()).isEqualTo(id1);
        }).consumeNextWith(( it) -> {
            assertThat(it.getId()).isEqualTo(id2);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void existsReturnsTrueForExistingDocuments() {
        String id = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        // 
        // 
        // 
        client.exists(new GetRequest(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id)).as(StepVerifier::create).expectNext(true).verifyComplete();
    }

    // DATAES-488
    @Test
    public void existsReturnsFalseForNonExistingDocuments() {
        String id = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        // 
        // 
        // 
        client.exists(new GetRequest(ReactiveElasticsearchClientTests.INDEX_II, ReactiveElasticsearchClientTests.TYPE_I, id)).as(StepVerifier::create).expectNext(false).verifyComplete();
    }

    // DATAES-488
    @Test
    public void indexShouldAddDocument() {
        IndexRequest request = indexRequest(ReactiveElasticsearchClientTests.DOC_SOURCE, ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I);
        // 
        // 
        // 
        client.index(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.status()).isEqualTo(RestStatus.CREATED);
            assertThat(it.getId()).isEqualTo(request.id());
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void indexShouldErrorForExistingDocuments() {
        String id = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        IndexRequest request = // 
        indexRequest(ReactiveElasticsearchClientTests.DOC_SOURCE, ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I).id(id);
        // 
        // 
        // 
        client.index(request).as(StepVerifier::create).consumeErrorWith(( error) -> {
            assertThat(error).isInstanceOf(.class);
        }).verify();
    }

    // DATAES-488
    @Test
    public void updateShouldUpsertNonExistingDocumentWhenUsedWithUpsert() {
        String id = UUID.randomUUID().toString();
        UpdateRequest request = // 
        // 
        new UpdateRequest(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id).doc(ReactiveElasticsearchClientTests.DOC_SOURCE).docAsUpsert(true);
        // 
        // 
        // 
        client.update(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.status()).isEqualTo(RestStatus.CREATED);
            assertThat(it.getId()).isEqualTo(id);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void updateShouldUpdateExistingDocument() {
        String id = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        UpdateRequest request = // 
        new UpdateRequest(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id).doc(Collections.singletonMap("dutiful", "farseer"));
        // 
        // 
        // 
        client.update(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.status()).isEqualTo(RestStatus.OK);
            assertThat(it.getId()).isEqualTo(id);
            assertThat(it.getVersion()).isEqualTo(2);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void updateShouldErrorNonExistingDocumentWhenNotUpserted() {
        String id = UUID.randomUUID().toString();
        UpdateRequest request = // 
        new UpdateRequest(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id).doc(ReactiveElasticsearchClientTests.DOC_SOURCE);
        // 
        // 
        // 
        client.update(request).as(StepVerifier::create).consumeErrorWith(( error) -> {
            assertThat(error).isInstanceOf(.class);
        }).verify();
    }

    // DATAES-488
    @Test
    public void deleteShouldRemoveExistingDocument() {
        String id = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        DeleteRequest request = new DeleteRequest(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, id);
        // 
        // 
        // 
        client.delete(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.status()).isEqualTo(RestStatus.OK);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void deleteShouldReturnNotFoundForNonExistingDocument() {
        addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        DeleteRequest request = new DeleteRequest(ReactiveElasticsearchClientTests.INDEX_I, ReactiveElasticsearchClientTests.TYPE_I, "this-one-does-not-exist");
        // 
        // 
        // 
        client.delete(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.status()).isEqualTo(RestStatus.NOT_FOUND);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    public void searchShouldFindExistingDocuments() {
        addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        SearchRequest request = // 
        new SearchRequest(ReactiveElasticsearchClientTests.INDEX_I).types(ReactiveElasticsearchClientTests.TYPE_I).source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        // 
        // 
        // 
        client.search(request).as(StepVerifier::create).expectNextCount(2).verifyComplete();
    }

    // DATAES-488
    @Test
    public void searchShouldCompleteIfNothingFound() throws IOException {
        syncClient.indices().create(new CreateIndexRequest(ReactiveElasticsearchClientTests.INDEX_I), DEFAULT);
        SearchRequest request = // 
        new SearchRequest(ReactiveElasticsearchClientTests.INDEX_I).types(ReactiveElasticsearchClientTests.TYPE_I).source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        // 
        // 
        client.search(request).as(StepVerifier::create).verifyComplete();
    }

    // DATAES-488
    @Test
    @ElasticsearchVersion(asOf = "6.5.0")
    public void deleteByShouldRemoveExistingDocument() {
        String id = addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        DeleteByQueryRequest request = // 
        // 
        new DeleteByQueryRequest(ReactiveElasticsearchClientTests.INDEX_I).setDocTypes(ReactiveElasticsearchClientTests.TYPE_I).setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("_id", id)));
        // 
        // 
        // 
        client.deleteBy(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.getDeleted()).isEqualTo(1);
        }).verifyComplete();
    }

    // DATAES-488
    @Test
    @ElasticsearchVersion(asOf = "6.5.0")
    public void deleteByEmitResultWhenNothingRemoved() {
        addSourceDocument().ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I);
        DeleteByQueryRequest request = // 
        // 
        new DeleteByQueryRequest(ReactiveElasticsearchClientTests.INDEX_I).setDocTypes(ReactiveElasticsearchClientTests.TYPE_I).setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("_id", "it-was-not-me")));
        // 
        // 
        // 
        client.deleteBy(request).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.getDeleted()).isEqualTo(0);
        }).verifyComplete();
    }

    // DATAES-510
    @Test
    public void scrollShouldReadWhileEndNotReached() {
        IntStream.range(0, 100).forEach(( it) -> add(Collections.singletonMap((it + "-foo"), "bar")).ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I));
        SearchRequest request = // 
        new SearchRequest(ReactiveElasticsearchClientTests.INDEX_I).types(ReactiveElasticsearchClientTests.TYPE_I).source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        request = request.scroll(TimeValue.timeValueMinutes(1));
        // 
        // 
        // 
        client.scroll(EMPTY, request).as(StepVerifier::create).expectNextCount(100).verifyComplete();
    }

    // DATAES-510
    @Test
    public void scrollShouldReadWhileTakeNotReached() {
        IntStream.range(0, 100).forEach(( it) -> add(Collections.singletonMap((it + "-foo"), "bar")).ofType(ReactiveElasticsearchClientTests.TYPE_I).to(ReactiveElasticsearchClientTests.INDEX_I));
        SearchRequest request = // 
        new SearchRequest(ReactiveElasticsearchClientTests.INDEX_I).types(ReactiveElasticsearchClientTests.TYPE_I).source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        request = request.scroll(TimeValue.timeValueMinutes(1));
        // 
        // 
        // 
        client.scroll(EMPTY, request).take(73).as(StepVerifier::create).expectNextCount(73).verifyComplete();
    }

    interface AddToIndexOfType extends ReactiveElasticsearchClientTests.AddToIndex {
        ReactiveElasticsearchClientTests.AddToIndex ofType(String type);
    }

    interface AddToIndex {
        String to(String index);
    }

    class AddDocument implements ReactiveElasticsearchClientTests.AddToIndexOfType {
        Map source;

        @Nullable
        String type;

        AddDocument(Map source) {
            this.source = source;
        }

        @Override
        public ReactiveElasticsearchClientTests.AddToIndex ofType(String type) {
            this.type = type;
            return this;
        }

        @Override
        public String to(String index) {
            return doIndex(new LinkedHashMap(source), index, type);
        }
    }
}

