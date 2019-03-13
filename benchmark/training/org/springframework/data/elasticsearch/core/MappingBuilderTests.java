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


import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.builder.SampleInheritedEntityBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.entities.Book;
import org.springframework.data.elasticsearch.entities.CopyToEntity;
import org.springframework.data.elasticsearch.entities.GeoEntity;
import org.springframework.data.elasticsearch.entities.Group;
import org.springframework.data.elasticsearch.entities.MinimalEntity;
import org.springframework.data.elasticsearch.entities.NormalizerEntity;
import org.springframework.data.elasticsearch.entities.SampleInheritedEntity;
import org.springframework.data.elasticsearch.entities.SampleTransientEntity;
import org.springframework.data.elasticsearch.entities.SimpleRecursiveEntity;
import org.springframework.data.elasticsearch.entities.StockPrice;
import org.springframework.data.elasticsearch.entities.User;
import org.springframework.data.elasticsearch.utils.IndexBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.math.BigDecimal.valueOf;


/**
 *
 *
 * @author Stuart Stevenson
 * @author Jakub Vavrik
 * @author Mohsin Husen
 * @author Keivn Leturc
 * @author Nordine Bittich
 * @author Don Wellington
 * @author Sascha Woo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:elasticsearch-template-test.xml")
public class MappingBuilderTests {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldNotFailOnCircularReference() {
        elasticsearchTemplate.deleteIndex(SimpleRecursiveEntity.class);
        elasticsearchTemplate.createIndex(SimpleRecursiveEntity.class);
        elasticsearchTemplate.putMapping(SimpleRecursiveEntity.class);
        elasticsearchTemplate.refresh(SimpleRecursiveEntity.class);
    }

    @Test
    public void testInfiniteLoopAvoidance() throws IOException {
        final String expected = "{\"mapping\":{\"properties\":{\"message\":{\"store\":true,\"" + ("type\":\"text\",\"index\":false," + "\"analyzer\":\"standard\"}}}}");
        XContentBuilder xContentBuilder = MappingBuilder.buildMapping(SampleTransientEntity.class, "mapping", "id", null);
        Assert.assertThat(xContentBuilderToString(xContentBuilder), CoreMatchers.is(expected));
    }

    @Test
    public void shouldUseValueFromAnnotationType() throws IOException {
        // Given
        final String expected = "{\"mapping\":{\"properties\":{\"price\":{\"store\":false,\"type\":\"double\"}}}}";
        // When
        XContentBuilder xContentBuilder = MappingBuilder.buildMapping(StockPrice.class, "mapping", "id", null);
        // Then
        Assert.assertThat(xContentBuilderToString(xContentBuilder), CoreMatchers.is(expected));
    }

    // DATAES-530
    @Test
    public void shouldAddStockPriceDocumentToIndex() throws IOException {
        // Given
        // When
        elasticsearchTemplate.deleteIndex(StockPrice.class);
        elasticsearchTemplate.createIndex(StockPrice.class);
        elasticsearchTemplate.putMapping(StockPrice.class);
        String symbol = "AU";
        double price = 2.34;
        String id = "abc";
        elasticsearchTemplate.index(IndexBuilder.buildIndex(builder().id(id).symbol(symbol).price(valueOf(price)).build()));
        elasticsearchTemplate.refresh(StockPrice.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        List<StockPrice> result = elasticsearchTemplate.queryForList(searchQuery, StockPrice.class);
        // Then
        Assert.assertThat(result.size(), CoreMatchers.is(1));
        StockPrice entry = result.get(0);
        Assert.assertThat(getSymbol(), CoreMatchers.is(symbol));
        Assert.assertThat(getPrice(), CoreMatchers.is(valueOf(price)));
    }

    @Test
    public void shouldCreateMappingForSpecifiedParentType() throws IOException {
        final String expected = "{\"mapping\":{\"_parent\":{\"type\":\"parentType\"},\"properties\":{}}}";
        XContentBuilder xContentBuilder = MappingBuilder.buildMapping(MinimalEntity.class, "mapping", "id", "parentType");
        Assert.assertThat(xContentBuilderToString(xContentBuilder), CoreMatchers.is(expected));
    }

    /* DATAES-76 */
    @Test
    public void shouldBuildMappingWithSuperclass() throws IOException {
        final String expected = "{\"mapping\":{\"properties\":{\"message\":{\"store\":true,\"" + (("type\":\"text\",\"index\":false,\"analyzer\":\"standard\"}" + ",\"createdDate\":{\"store\":false,") + "\"type\":\"date\",\"index\":false}}}}");
        XContentBuilder xContentBuilder = MappingBuilder.buildMapping(SampleInheritedEntity.class, "mapping", "id", null);
        Assert.assertThat(xContentBuilderToString(xContentBuilder), CoreMatchers.is(expected));
    }

    /* DATAES-76 */
    @Test
    public void shouldAddSampleInheritedEntityDocumentToIndex() throws IOException {
        // Given
        // When
        elasticsearchTemplate.deleteIndex(SampleInheritedEntity.class);
        elasticsearchTemplate.createIndex(SampleInheritedEntity.class);
        elasticsearchTemplate.putMapping(SampleInheritedEntity.class);
        Date createdDate = new Date();
        String message = "msg";
        String id = "abc";
        elasticsearchTemplate.index(new SampleInheritedEntityBuilder(id).createdDate(createdDate).message(message).buildIndex());
        elasticsearchTemplate.refresh(SampleInheritedEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        List<SampleInheritedEntity> result = elasticsearchTemplate.queryForList(searchQuery, SampleInheritedEntity.class);
        // Then
        Assert.assertThat(result.size(), CoreMatchers.is(1));
        SampleInheritedEntity entry = result.get(0);
        Assert.assertThat(entry.getCreatedDate(), CoreMatchers.is(createdDate));
        Assert.assertThat(entry.getMessage(), CoreMatchers.is(message));
    }

    @Test
    public void shouldBuildMappingsForGeoPoint() throws IOException {
        // given
        // when
        XContentBuilder xContentBuilder = MappingBuilder.buildMapping(GeoEntity.class, "mapping", "id", null);
        // then
        final String result = xContentBuilderToString(xContentBuilder);
        Assert.assertThat(result, CoreMatchers.containsString("\"pointA\":{\"type\":\"geo_point\""));
        Assert.assertThat(result, CoreMatchers.containsString("\"pointB\":{\"type\":\"geo_point\""));
        Assert.assertThat(result, CoreMatchers.containsString("\"pointC\":{\"type\":\"geo_point\""));
        Assert.assertThat(result, CoreMatchers.containsString("\"pointD\":{\"type\":\"geo_point\""));
    }

    /**
     * DATAES-260 - StacOverflow when two reverse relationship.
     */
    @Test
    public void shouldHandleReverseRelationship() {
        // given
        elasticsearchTemplate.createIndex(User.class);
        elasticsearchTemplate.putMapping(User.class);
        elasticsearchTemplate.createIndex(Group.class);
        elasticsearchTemplate.putMapping(Group.class);
        // when
        // then
    }

    @Test
    public void shouldMapBooks() {
        // given
        elasticsearchTemplate.createIndex(Book.class);
        elasticsearchTemplate.putMapping(Book.class);
        // when
        // then
    }

    // DATAES-420
    @Test
    public void shouldUseBothAnalyzer() {
        // given
        elasticsearchTemplate.deleteIndex(Book.class);
        elasticsearchTemplate.createIndex(Book.class);
        elasticsearchTemplate.putMapping(Book.class);
        // when
        Map mapping = elasticsearchTemplate.getMapping(Book.class);
        Map descriptionMapping = ((Map) (((Map) (mapping.get("properties"))).get("description")));
        Map prefixDescription = ((Map) (((Map) (descriptionMapping.get("fields"))).get("prefix")));
        // then
        Assert.assertThat(prefixDescription.size(), CoreMatchers.is(3));
        Assert.assertThat(prefixDescription.get("type"), CoreMatchers.equalTo("text"));
        Assert.assertThat(prefixDescription.get("analyzer"), CoreMatchers.equalTo("stop"));
        Assert.assertThat(prefixDescription.get("search_analyzer"), CoreMatchers.equalTo("standard"));
        Assert.assertThat(descriptionMapping.get("type"), CoreMatchers.equalTo("text"));
        Assert.assertThat(descriptionMapping.get("analyzer"), CoreMatchers.equalTo("whitespace"));
    }

    // DATAES-492
    @Test
    public void shouldUseKeywordNormalizer() throws IOException {
        // given
        elasticsearchTemplate.deleteIndex(NormalizerEntity.class);
        elasticsearchTemplate.createIndex(NormalizerEntity.class);
        elasticsearchTemplate.putMapping(NormalizerEntity.class);
        // when
        Map mapping = elasticsearchTemplate.getMapping(NormalizerEntity.class);
        Map properties = ((Map) (mapping.get("properties")));
        Map fieldName = ((Map) (properties.get("name")));
        Map fieldDescriptionLowerCase = ((Map) (((Map) (((Map) (properties.get("description"))).get("fields"))).get("lower_case")));
        // then
        Assert.assertThat(fieldName.get("type"), CoreMatchers.equalTo("keyword"));
        Assert.assertThat(fieldName.get("normalizer"), CoreMatchers.equalTo("lower_case_normalizer"));
        Assert.assertThat(fieldDescriptionLowerCase.get("type"), CoreMatchers.equalTo("keyword"));
        Assert.assertThat(fieldDescriptionLowerCase.get("normalizer"), CoreMatchers.equalTo("lower_case_normalizer"));
    }

    // DATAES-503
    @Test
    public void shouldUseCopyTo() throws IOException {
        // given
        elasticsearchTemplate.deleteIndex(CopyToEntity.class);
        elasticsearchTemplate.createIndex(CopyToEntity.class);
        elasticsearchTemplate.putMapping(CopyToEntity.class);
        // when
        Map mapping = elasticsearchTemplate.getMapping(CopyToEntity.class);
        Map properties = ((Map) (mapping.get("properties")));
        Map fieldFirstName = ((Map) (properties.get("firstName")));
        Map fieldLastName = ((Map) (properties.get("lastName")));
        // then
        List<String> copyToValue = Arrays.asList("name");
        Assert.assertThat(fieldFirstName.get("copy_to"), CoreMatchers.equalTo(copyToValue));
        Assert.assertThat(fieldLastName.get("copy_to"), CoreMatchers.equalTo(copyToValue));
    }
}

