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
package org.springframework.data.elasticsearch.repository.query;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.annotations.org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.data.elasticsearch.entities.Person;
import org.springframework.data.repository.Repository;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Christoph Strobl
 * @unknown Fool's Fate - Robin Hobb
 */
@RunWith(MockitoJUnitRunner.class)
public class ReactiveElasticsearchStringQueryUnitTests {
    SpelExpressionParser PARSER = new SpelExpressionParser();

    ElasticsearchConverter converter;

    @Mock
    ReactiveElasticsearchOperations operations;

    // DATAES-519
    @Test
    public void bindsSimplePropertyCorrectly() throws Exception {
        ReactiveElasticsearchStringQuery elasticsearchStringQuery = createQueryForMethod("findByName", String.class);
        StubParameterAccessor accesor = new StubParameterAccessor("Luke");
        org.springframework.data.elasticsearch.core.query.Query query = elasticsearchStringQuery.createQuery(accesor);
        StringQuery reference = new StringQuery("{ 'bool' : { 'must' : { 'term' : { 'name' : 'Luke' } } } }");
        assertThat(query).isInstanceOf(StringQuery.class);
        assertThat(getSource()).isEqualTo(reference.getSource());
    }

    private interface SampleRepository extends Repository<Person, String> {
        @Query("{ 'bool' : { 'must' : { 'term' : { 'name' : '?0' } } } }")
        Mono<Person> findByName(String name);

        @Query("{ 'bool' : { 'must' : { 'term' : { 'name' : '?#{[0]}' } } } }")
        Flux<Person> findByNameWithExpression(String param0);
    }
}

