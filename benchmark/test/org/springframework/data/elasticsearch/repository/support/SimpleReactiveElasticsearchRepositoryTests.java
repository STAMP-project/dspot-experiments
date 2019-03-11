/**
 * Copyright 2019 the original author or authors.
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
package org.springframework.data.elasticsearch.repository.support;


import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.TestUtils;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration;
import org.springframework.data.elasticsearch.entities.SampleEntity;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Christoph Strobl
 * @unknown Fool's Fate - Robin Hobb
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleReactiveElasticsearchRepositoryTests {
    @Configuration
    @EnableReactiveElasticsearchRepositories(considerNestedRepositories = true)
    static class Config extends AbstractReactiveElasticsearchConfiguration {
        @Override
        public ReactiveElasticsearchClient reactiveElasticsearchClient() {
            return TestUtils.reactiveClient();
        }
    }

    static final String INDEX = "test-index-sample";

    static final String TYPE = "test-type";

    @Autowired
    SimpleReactiveElasticsearchRepositoryTests.ReactiveSampleEntityRepository repository;

    // DATAES-519
    @Test
    public void saveShouldSaveSingleEntity() {
        // 
        // 
        // 
        repository.save(builder().build()).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(TestUtils.documentWithId(it.getId()).ofType(TYPE).existsIn(INDEX)).isTrue();
        }).verifyComplete();
    }

    // DATAES-519
    @Test
    public void saveShouldComputeMultipleEntities() {
        // 
        // 
        // 
        // 
        // 
        repository.saveAll(Arrays.asList(builder().build(), builder().build(), builder().build())).as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(TestUtils.documentWithId(it.getId()).ofType(TYPE).existsIn(INDEX)).isTrue();
        }).consumeNextWith(( it) -> {
            assertThat(TestUtils.documentWithId(it.getId()).ofType(TYPE).existsIn(INDEX)).isTrue();
        }).consumeNextWith(( it) -> {
            assertThat(TestUtils.documentWithId(it.getId()).ofType(TYPE).existsIn(INDEX)).isTrue();
        }).verifyComplete();
    }

    // DATAES-519
    @Test
    public void findByIdShouldCompleteIfIndexDoesNotExist() {
        findById("id-two").as(StepVerifier::create).verifyComplete();
    }

    // DATAES-519
    @Test
    public void findShouldRetrieveSingleEntityById() {
        // 
        // 
        bulkIndex(builder().id("id-one").build(), builder().id("id-two").build(), builder().id("id-three").build());
        // 
        // 
        findById("id-two").as(StepVerifier::create).consumeNextWith(( it) -> {
            assertThat(it.getId()).isEqualTo("id-two");
        }).verifyComplete();
    }

    // DATAES-519
    @Test
    public void findByIdShouldCompleteIfNothingFound() {
        // 
        // 
        bulkIndex(builder().id("id-one").build(), builder().id("id-two").build(), builder().id("id-three").build());
        // 
        findById("does-not-exist").as(StepVerifier::create).verifyComplete();
    }

    // DATAES-519
    @Test
    public void findAllByIdByIdShouldCompleteIfIndexDoesNotExist() {
        findAllById(Arrays.asList("id-two", "id-two")).as(StepVerifier::create).verifyComplete();
    }

    // DATAES-519
    @Test
    public void findAllByIdShouldRetrieveMatchingDocuments() {
        // 
        // 
        bulkIndex(builder().id("id-one").build(), builder().id("id-two").build(), builder().id("id-three").build());
        // 
        // 
        // 
        findAllById(Arrays.asList("id-one", "id-two")).as(StepVerifier::create).expectNextCount(2).verifyComplete();
    }

    // DATAES-519
    @Test
    public void findAllByIdShouldCompleteWhenNothingFound() {
        // 
        // 
        bulkIndex(builder().id("id-one").build(), builder().id("id-two").build(), builder().id("id-three").build());
        // 
        // 
        findAllById(Arrays.asList("can't", "touch", "this")).as(StepVerifier::create).verifyComplete();
    }

    // DATAES-519
    @Test
    public void countShouldReturnZeroWhenIndexDoesNotExist() {
        count().as(StepVerifier::create).expectNext(0L).verifyComplete();
    }

    // DATAES-519
    @Test
    public void countShouldCountDocuments() {
        // 
        bulkIndex(builder().id("id-one").build(), builder().id("id-two").build());
        count().as(StepVerifier::create).expectNext(2L).verifyComplete();
    }

    // DATAES-519
    @Test
    public void existsByIdShouldReturnTrueIfExists() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        existsById("id-two").as(StepVerifier::create).expectNext(true).verifyComplete();
    }

    // DATAES-519
    @Test
    public void existsByIdShouldReturnFalseIfNotExists() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        existsById("wrecking ball").as(StepVerifier::create).expectNext(false).verifyComplete();
    }

    // DATAES-519
    @Test
    public void countShouldCountMatchingDocuments() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        repository.countAllByMessage("test").as(StepVerifier::create).expectNext(2L).verifyComplete();
    }

    // DATAES-519
    @Test
    public void existsShouldReturnTrueIfExists() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        repository.existsAllByMessage("message").as(StepVerifier::create).expectNext(true).verifyComplete();
    }

    // DATAES-519
    @Test
    public void existsShouldReturnFalseIfNotExists() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        repository.existsAllByMessage("these days").as(StepVerifier::create).expectNext(false).verifyComplete();
    }

    // DATAES-519
    @Test
    public void deleteByIdShouldCompleteIfNothingDeleted() {
        // 
        bulkIndex(builder().id("id-one").build(), builder().id("id-two").build());
        deleteById("does-not-exist").as(StepVerifier::create).verifyComplete();
    }

    // DATAES-519
    @Test
    public void deleteByIdShouldCompleteWhenIndexDoesNotExist() {
        deleteById("does-not-exist").as(StepVerifier::create).verifyComplete();
    }

    // DATAES-519
    @Test
    public void deleteByIdShouldDeleteEntry() {
        SampleEntity toBeDeleted = builder().id("id-two").build();
        bulkIndex(builder().id("id-one").build(), toBeDeleted);
        repository.deleteById(getId()).as(StepVerifier::create).verifyComplete();
        assertThat(TestUtils.documentWithId(getId()).ofType(SimpleReactiveElasticsearchRepositoryTests.TYPE).existsIn(SimpleReactiveElasticsearchRepositoryTests.INDEX)).isFalse();
    }

    // DATAES-519
    @Test
    public void deleteShouldDeleteEntry() {
        SampleEntity toBeDeleted = builder().id("id-two").build();
        bulkIndex(builder().id("id-one").build(), toBeDeleted);
        delete(toBeDeleted).as(StepVerifier::create).verifyComplete();
        assertThat(TestUtils.documentWithId(getId()).ofType(SimpleReactiveElasticsearchRepositoryTests.TYPE).existsIn(SimpleReactiveElasticsearchRepositoryTests.INDEX)).isFalse();
    }

    // DATAES-519
    @Test
    public void deleteAllShouldDeleteGivenEntries() {
        SampleEntity toBeDeleted = builder().id("id-one").build();
        SampleEntity hangInThere = builder().id("id-two").build();
        SampleEntity toBeDeleted2 = builder().id("id-three").build();
        bulkIndex(toBeDeleted, hangInThere, toBeDeleted2);
        repository.deleteAll(Arrays.asList(toBeDeleted, toBeDeleted2)).as(StepVerifier::create).verifyComplete();
        assertThat(TestUtils.documentWithId(getId()).ofType(SimpleReactiveElasticsearchRepositoryTests.TYPE).existsIn(SimpleReactiveElasticsearchRepositoryTests.INDEX)).isFalse();
        assertThat(TestUtils.documentWithId(getId()).ofType(SimpleReactiveElasticsearchRepositoryTests.TYPE).existsIn(SimpleReactiveElasticsearchRepositoryTests.INDEX)).isFalse();
        assertThat(TestUtils.documentWithId(getId()).ofType(SimpleReactiveElasticsearchRepositoryTests.TYPE).existsIn(SimpleReactiveElasticsearchRepositoryTests.INDEX)).isTrue();
    }

    // DATAES-519
    @Test
    public void deleteAllShouldDeleteAllEntries() {
        // 
        // 
        bulkIndex(builder().id("id-one").build(), builder().id("id-two").build(), builder().id("id-three").build());
        deleteAll().as(StepVerifier::create).verifyComplete();
        assertThat(TestUtils.isEmptyIndex(SimpleReactiveElasticsearchRepositoryTests.INDEX)).isTrue();
    }

    // DATAES-519
    @Test
    public void derivedFinderMethodShouldBeExecutedCorrectly() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        repository.findAllByMessageLike("test").as(StepVerifier::create).expectNextCount(2).verifyComplete();
    }

    // DATAES-519
    @Test
    public void derivedFinderMethodShouldBeExecutedCorrectlyWhenGivenPublisher() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        repository.findAllByMessage(Mono.just("test")).as(StepVerifier::create).expectNextCount(2).verifyComplete();
    }

    // DATAES-519
    @Test
    public void derivedFinderWithDerivedSortMethodShouldBeExecutedCorrectly() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("test").rate(3).build(), builder().id("id-two").message("test test").rate(1).build(), builder().id("id-three").message("test test").rate(2).build());
        // 
        // 
        // 
        // 
        // 
        repository.findAllByMessageLikeOrderByRate("test").as(StepVerifier::create).consumeNextWith(( it) -> assertThat(it.getId()).isEqualTo("id-two")).consumeNextWith(( it) -> assertThat(it.getId()).isEqualTo("id-three")).consumeNextWith(( it) -> assertThat(it.getId()).isEqualTo("id-one")).verifyComplete();
    }

    // DATAES-519
    @Test
    public void derivedFinderMethodWithSortParameterShouldBeExecutedCorrectly() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("test").rate(3).build(), builder().id("id-two").message("test test").rate(1).build(), builder().id("id-three").message("test test").rate(2).build());
        // 
        // 
        // 
        // 
        // 
        repository.findAllByMessage("test", Sort.by(Order.asc("rate"))).as(StepVerifier::create).consumeNextWith(( it) -> assertThat(it.getId()).isEqualTo("id-two")).consumeNextWith(( it) -> assertThat(it.getId()).isEqualTo("id-three")).consumeNextWith(( it) -> assertThat(it.getId()).isEqualTo("id-one")).verifyComplete();
    }

    // DATAES-519
    @Test
    public void derivedFinderMethodWithPageableParameterShouldBeExecutedCorrectly() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("test").rate(3).build(), builder().id("id-two").message("test test").rate(1).build(), builder().id("id-three").message("test test").rate(2).build());
        // 
        // 
        // 
        // 
        repository.findAllByMessage("test", PageRequest.of(0, 2, Sort.by(Order.asc("rate")))).as(StepVerifier::create).consumeNextWith(( it) -> assertThat(it.getId()).isEqualTo("id-two")).consumeNextWith(( it) -> assertThat(it.getId()).isEqualTo("id-three")).verifyComplete();
    }

    // DATAES-519
    @Test
    public void derivedFinderMethodReturningMonoShouldBeExecutedCorrectly() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        repository.findFirstByMessageLike("test").as(StepVerifier::create).expectNextCount(1).verifyComplete();
    }

    // DATAES-519
    @Test
    public void annotatedFinderMethodShouldBeExecutedCorrectly() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        repository.findAllViaAnnotatedQueryByMessageLike("test").as(StepVerifier::create).expectNextCount(2).verifyComplete();
    }

    // DATAES-519
    @Test
    public void derivedDeleteMethodShouldBeExecutedCorrectly() {
        // 
        // 
        bulkIndex(builder().id("id-one").message("message").build(), builder().id("id-two").message("test message").build(), builder().id("id-three").message("test test").build());
        // 
        // 
        // 
        repository.deleteAllByMessage("message").as(StepVerifier::create).expectNext(2L).verifyComplete();
        assertThat(TestUtils.documentWithId("id-one").ofType(SimpleReactiveElasticsearchRepositoryTests.TYPE).existsIn(SimpleReactiveElasticsearchRepositoryTests.INDEX)).isFalse();
        assertThat(TestUtils.documentWithId("id-two").ofType(SimpleReactiveElasticsearchRepositoryTests.TYPE).existsIn(SimpleReactiveElasticsearchRepositoryTests.INDEX)).isFalse();
        assertThat(TestUtils.documentWithId("id-three").ofType(SimpleReactiveElasticsearchRepositoryTests.TYPE).existsIn(SimpleReactiveElasticsearchRepositoryTests.INDEX)).isTrue();
    }

    interface ReactiveSampleEntityRepository extends ReactiveCrudRepository<SampleEntity, String> {
        Flux<SampleEntity> findAllByMessageLike(String message);

        Flux<SampleEntity> findAllByMessageLikeOrderByRate(String message);

        Flux<SampleEntity> findAllByMessage(String message, Sort sort);

        Flux<SampleEntity> findAllByMessage(String message, Pageable pageable);

        Flux<SampleEntity> findAllByMessage(Publisher<String> message);

        @Query("{ \"bool\" : { \"must\" : { \"term\" : { \"message\" : \"?0\" } } } }")
        Flux<SampleEntity> findAllViaAnnotatedQueryByMessageLike(String message);

        Mono<SampleEntity> findFirstByMessageLike(String message);

        Mono<Long> countAllByMessage(String message);

        Mono<Boolean> existsAllByMessage(String message);

        Mono<Long> deleteAllByMessage(String message);
    }
}

