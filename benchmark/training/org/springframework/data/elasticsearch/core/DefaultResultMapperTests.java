/**
 * Copyright 2013-2019 the original author or authors.
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


import com.fasterxml.jackson.databind.util.ArrayIterator;
import java.util.Arrays;
import java.util.LinkedList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.entities.Car;
import org.springframework.data.elasticsearch.entities.SampleEntity;


/**
 *
 *
 * @author Artur Konczak
 * @author Mohsin Husen
 * @author Chris White
 * @author Mark Paluch
 * @author Ilkang Na
 * @author Christoph Strobl
 */
@RunWith(Parameterized.class)
public class DefaultResultMapperTests {
    private DefaultResultMapper resultMapper;

    private SimpleElasticsearchMappingContext context;

    private EntityMapper entityMapper;

    @Mock
    private SearchResponse response;

    public DefaultResultMapperTests(SimpleElasticsearchMappingContext context, EntityMapper entityMapper) {
        this.context = context;
        this.entityMapper = entityMapper;
    }

    @Test
    public void shouldMapAggregationsToPage() {
        // Given
        SearchHit[] hits = new SearchHit[]{ createCarHit("Ford", "Grat"), createCarHit("BMW", "Arrow") };
        SearchHits searchHits = Mockito.mock(SearchHits.class);
        Mockito.when(searchHits.getTotalHits()).thenReturn(2L);
        Mockito.when(searchHits.iterator()).thenReturn(new ArrayIterator(hits));
        Mockito.when(response.getHits()).thenReturn(searchHits);
        Aggregations aggregations = new Aggregations(Arrays.asList(createCarAggregation()));
        Mockito.when(response.getAggregations()).thenReturn(aggregations);
        // When
        AggregatedPage<Car> page = ((AggregatedPage<Car>) (resultMapper.mapResults(response, Car.class, Pageable.unpaged())));
        // Then
        page.hasFacets();
        Assert.assertThat(page.hasAggregations(), is(true));
        Assert.assertThat(page.getAggregation("Diesel").getName(), is("Diesel"));
    }

    @Test
    public void shouldMapSearchRequestToPage() {
        // Given
        SearchHit[] hits = new SearchHit[]{ createCarHit("Ford", "Grat"), createCarHit("BMW", "Arrow") };
        SearchHits searchHits = Mockito.mock(SearchHits.class);
        Mockito.when(searchHits.getTotalHits()).thenReturn(2L);
        Mockito.when(searchHits.iterator()).thenReturn(new ArrayIterator(hits));
        Mockito.when(response.getHits()).thenReturn(searchHits);
        // When
        Page<Car> page = resultMapper.mapResults(response, Car.class, Pageable.unpaged());
        // Then
        Assert.assertThat(page.hasContent(), is(true));
        Assert.assertThat(page.getTotalElements(), is(2L));
        Assert.assertThat(page.getContent().get(0).getName(), is("Ford"));
    }

    @Test
    public void shouldMapPartialSearchRequestToObject() {
        // Given
        SearchHit[] hits = new SearchHit[]{ createCarPartialHit("Ford", "Grat"), createCarPartialHit("BMW", "Arrow") };
        SearchHits searchHits = Mockito.mock(SearchHits.class);
        Mockito.when(searchHits.getTotalHits()).thenReturn(2L);
        Mockito.when(searchHits.iterator()).thenReturn(new ArrayIterator(hits));
        Mockito.when(response.getHits()).thenReturn(searchHits);
        // When
        Page<Car> page = resultMapper.mapResults(response, Car.class, Pageable.unpaged());
        // Then
        Assert.assertThat(page.hasContent(), is(true));
        Assert.assertThat(page.getTotalElements(), is(2L));
        Assert.assertThat(page.getContent().get(0).getName(), is("Ford"));
    }

    @Test
    public void shouldMapGetRequestToObject() {
        // Given
        GetResponse response = Mockito.mock(GetResponse.class);
        Mockito.when(response.getSourceAsString()).thenReturn(createJsonCar("Ford", "Grat"));
        // When
        Car result = resultMapper.mapResult(response, Car.class);
        // Then
        Assert.assertThat(result, notNullValue());
        Assert.assertThat(result.getModel(), is("Grat"));
        Assert.assertThat(result.getName(), is("Ford"));
    }

    // DATAES-198
    @Test
    public void setsVersionFromGetResponse() {
        GetResponse response = Mockito.mock(GetResponse.class);
        Mockito.when(response.getSourceAsString()).thenReturn("{}");
        Mockito.when(response.getVersion()).thenReturn(1234L);
        SampleEntity result = resultMapper.mapResult(response, SampleEntity.class);
        Assert.assertThat(result, is(notNullValue()));
        Assert.assertThat(getVersion(), is(1234L));
    }

    // DATAES-198
    @Test
    public void setsVersionFromMultiGetResponse() {
        GetResponse response1 = Mockito.mock(GetResponse.class);
        Mockito.when(response1.getSourceAsString()).thenReturn("{}");
        Mockito.when(response1.isExists()).thenReturn(true);
        Mockito.when(response1.getVersion()).thenReturn(1234L);
        GetResponse response2 = Mockito.mock(GetResponse.class);
        Mockito.when(response2.getSourceAsString()).thenReturn("{}");
        Mockito.when(response2.isExists()).thenReturn(true);
        Mockito.when(response2.getVersion()).thenReturn(5678L);
        MultiGetResponse multiResponse = Mockito.mock(MultiGetResponse.class);
        Mockito.when(multiResponse.getResponses()).thenReturn(new MultiGetItemResponse[]{ new MultiGetItemResponse(response1, null), new MultiGetItemResponse(response2, null) });
        LinkedList<SampleEntity> results = resultMapper.mapResults(multiResponse, SampleEntity.class);
        Assert.assertThat(results, is(notNullValue()));
        Assert.assertThat(results, hasSize(2));
        Assert.assertThat(getVersion(), is(1234L));
        Assert.assertThat(getVersion(), is(5678L));
    }

    // DATAES-198
    @Test
    public void setsVersionFromSearchResponse() {
        SearchHit hit1 = Mockito.mock(SearchHit.class);
        Mockito.when(hit1.getSourceAsString()).thenReturn("{}");
        Mockito.when(hit1.getVersion()).thenReturn(1234L);
        SearchHit hit2 = Mockito.mock(SearchHit.class);
        Mockito.when(hit2.getSourceAsString()).thenReturn("{}");
        Mockito.when(hit2.getVersion()).thenReturn(5678L);
        SearchHits searchHits = Mockito.mock(SearchHits.class);
        Mockito.when(searchHits.getTotalHits()).thenReturn(2L);
        Mockito.when(searchHits.iterator()).thenReturn(Arrays.asList(hit1, hit2).iterator());
        SearchResponse searchResponse = Mockito.mock(SearchResponse.class);
        Mockito.when(searchResponse.getHits()).thenReturn(searchHits);
        AggregatedPage<SampleEntity> results = resultMapper.mapResults(searchResponse, SampleEntity.class, Mockito.mock(Pageable.class));
        Assert.assertThat(results, is(notNullValue()));
        Assert.assertThat(getVersion(), is(1234L));
        Assert.assertThat(getVersion(), is(5678L));
    }

    @Document(indexName = "test-index-immutable-internal")
    @NoArgsConstructor(force = true)
    @Getter
    static class ImmutableEntity {
        private final String id;

        private final String name;
    }
}

