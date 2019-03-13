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
package org.springframework.data.elasticsearch.core;


import IndicesOptions.LENIENT_EXPAND_OPEN;
import RefreshPolicy.IMMEDIATE;
import RefreshPolicy.NONE;
import RefreshPolicy.WAIT_UNTIL;
import java.util.Collections;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.entities.SampleEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Christoph Strobl
 * @unknown Fool's Fate - Robin Hobb
 */
public class ReactiveElasticsearchTemplateUnitTests {
    // 
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    ReactiveElasticsearchClient client;

    ReactiveElasticsearchTemplate template;

    // DATAES-504
    @Test
    public void insertShouldUseDefaultRefreshPolicy() {
        ArgumentCaptor<IndexRequest> captor = ArgumentCaptor.forClass(IndexRequest.class);
        Mockito.when(client.index(captor.capture())).thenReturn(Mono.empty());
        // 
        // 
        template.save(Collections.singletonMap("key", "value"), "index", "type").as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().getRefreshPolicy()).isEqualTo(IMMEDIATE);
    }

    // DATAES-504
    @Test
    public void insertShouldApplyRefreshPolicy() {
        ArgumentCaptor<IndexRequest> captor = ArgumentCaptor.forClass(IndexRequest.class);
        Mockito.when(client.index(captor.capture())).thenReturn(Mono.empty());
        template.setRefreshPolicy(WAIT_UNTIL);
        // 
        // 
        template.save(Collections.singletonMap("key", "value"), "index", "type").as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().getRefreshPolicy()).isEqualTo(WAIT_UNTIL);
    }

    // DATAES-504, DATAES-518
    @Test
    public void findShouldFallBackToDefaultIndexOptionsIfNotSet() {
        ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
        Mockito.when(client.search(captor.capture())).thenReturn(Flux.empty());
        // 
        // 
        template.find(new org.springframework.data.elasticsearch.core.query.CriteriaQuery(new Criteria("*")).setPageable(PageRequest.of(0, 10)), SampleEntity.class).as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().indicesOptions()).isEqualTo(DEFAULT_INDICES_OPTIONS);
    }

    // DATAES-504, DATAES-518
    @Test
    public void findShouldApplyIndexOptionsIfSet() {
        ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
        Mockito.when(client.search(captor.capture())).thenReturn(Flux.empty());
        template.setIndicesOptions(LENIENT_EXPAND_OPEN);
        // 
        // 
        template.find(new org.springframework.data.elasticsearch.core.query.CriteriaQuery(new Criteria("*")).setPageable(PageRequest.of(0, 10)), SampleEntity.class).as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().indicesOptions()).isEqualTo(LENIENT_EXPAND_OPEN);
    }

    // DATAES-504
    @Test
    public void findShouldApplyPaginationIfSet() {
        ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
        Mockito.when(client.search(captor.capture())).thenReturn(Flux.empty());
        // 
        // 
        template.find(new org.springframework.data.elasticsearch.core.query.CriteriaQuery(new Criteria("*")).setPageable(PageRequest.of(2, 50)), SampleEntity.class).as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().source().from()).isEqualTo(100);
        assertThat(captor.getValue().source().size()).isEqualTo(50);
    }

    // DATAES-504, DATAES-518
    @Test
    public void findShouldUseScrollIfPaginationNotSet() {
        ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
        Mockito.when(client.scroll(captor.capture())).thenReturn(Flux.empty());
        // 
        // 
        template.find(new org.springframework.data.elasticsearch.core.query.CriteriaQuery(new Criteria("*")).setPageable(Pageable.unpaged()), SampleEntity.class).as(StepVerifier::create).verifyComplete();
        Mockito.verify(client).scroll(ArgumentMatchers.any());
    }

    // DATAES-504
    @Test
    public void deleteShouldUseDefaultRefreshPolicy() {
        ArgumentCaptor<DeleteRequest> captor = ArgumentCaptor.forClass(DeleteRequest.class);
        Mockito.when(client.delete(captor.capture())).thenReturn(Mono.empty());
        // 
        // 
        template.deleteById("id", "index", "type").as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().getRefreshPolicy()).isEqualTo(IMMEDIATE);
    }

    // DATAES-504
    @Test
    public void deleteShouldApplyRefreshPolicy() {
        ArgumentCaptor<DeleteRequest> captor = ArgumentCaptor.forClass(DeleteRequest.class);
        Mockito.when(client.delete(captor.capture())).thenReturn(Mono.empty());
        template.setRefreshPolicy(WAIT_UNTIL);
        // 
        // 
        template.deleteById("id", "index", "type").as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().getRefreshPolicy()).isEqualTo(WAIT_UNTIL);
    }

    // DATAES-504
    @Test
    public void deleteByShouldUseDefaultRefreshPolicy() {
        ArgumentCaptor<DeleteByQueryRequest> captor = ArgumentCaptor.forClass(DeleteByQueryRequest.class);
        Mockito.when(client.deleteBy(captor.capture())).thenReturn(Mono.empty());
        // 
        // 
        template.deleteBy(new org.springframework.data.elasticsearch.core.query.StringQuery(QueryBuilders.matchAllQuery().toString()), Object.class, "index", "type").as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().isRefresh()).isTrue();
    }

    // DATAES-504
    @Test
    public void deleteByShouldApplyRefreshPolicy() {
        ArgumentCaptor<DeleteByQueryRequest> captor = ArgumentCaptor.forClass(DeleteByQueryRequest.class);
        Mockito.when(client.deleteBy(captor.capture())).thenReturn(Mono.empty());
        template.setRefreshPolicy(NONE);
        // 
        // 
        template.deleteBy(new org.springframework.data.elasticsearch.core.query.StringQuery(QueryBuilders.matchAllQuery().toString()), Object.class, "index", "type").as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().isRefresh()).isFalse();
    }

    // DATAES-504
    @Test
    public void deleteByShouldApplyIndicesOptions() {
        ArgumentCaptor<DeleteByQueryRequest> captor = ArgumentCaptor.forClass(DeleteByQueryRequest.class);
        Mockito.when(client.deleteBy(captor.capture())).thenReturn(Mono.empty());
        // 
        // 
        template.deleteBy(new org.springframework.data.elasticsearch.core.query.StringQuery(QueryBuilders.matchAllQuery().toString()), Object.class, "index", "type").as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().indicesOptions()).isEqualTo(DEFAULT_INDICES_OPTIONS);
    }

    // DATAES-504
    @Test
    public void deleteByShouldApplyIndicesOptionsIfSet() {
        ArgumentCaptor<DeleteByQueryRequest> captor = ArgumentCaptor.forClass(DeleteByQueryRequest.class);
        Mockito.when(client.deleteBy(captor.capture())).thenReturn(Mono.empty());
        template.setIndicesOptions(LENIENT_EXPAND_OPEN);
        // 
        // 
        template.deleteBy(new org.springframework.data.elasticsearch.core.query.StringQuery(QueryBuilders.matchAllQuery().toString()), Object.class, "index", "type").as(StepVerifier::create).verifyComplete();
        assertThat(captor.getValue().indicesOptions()).isEqualTo(LENIENT_EXPAND_OPEN);
    }
}

