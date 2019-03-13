/**
 * Copyright 2014-2019 the original author or authors.
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


import HighlightBuilder.Field;
import Sort.Direction;
import Sort.Order;
import SortOrder.ASC;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.entities.Book;
import org.springframework.data.elasticsearch.entities.GTEVersionEntity;
import org.springframework.data.elasticsearch.entities.HetroEntity1;
import org.springframework.data.elasticsearch.entities.HetroEntity2;
import org.springframework.data.elasticsearch.entities.SampleEntity;
import org.springframework.data.elasticsearch.entities.SampleMappingEntity;
import org.springframework.data.elasticsearch.entities.UseServerConfigurationEntity;
import org.springframework.data.elasticsearch.utils.IndexBuilder;
import org.springframework.data.util.CloseableIterator;


/**
 * Base for testing rest/transport templates
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Franck Marchand
 * @author Abdul Mohammed
 * @author Kevin Leturc
 * @author Mason Chan
 * @author Chris White
 * @author Ilkang Na
 * @author Alen Turkovic
 * @author Sascha Woo
 * @author Jean-Baptiste Nizet
 * @author Zetang Zeng
 * @author Peter Nowak
 * @author Ivan Greene
 */
@Ignore
public class ElasticsearchTemplateTests {
    private static final String INDEX_NAME = "test-index-sample";

    private static final String INDEX_1_NAME = "test-index-1";

    private static final String INDEX_2_NAME = "test-index-2";

    private static final String TYPE_NAME = "test-type";

    @Autowired
    protected ElasticsearchOperations elasticsearchTemplate;

    /* DATAES-106 */
    @Test
    public void shouldReturnCountForGivenCriteriaQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        // when
        long count = elasticsearchTemplate.count(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    @Test
    public void shouldReturnCountForGivenSearchQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        // when
        long count = elasticsearchTemplate.count(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    @Test
    public void shouldReturnObjectForGivenId() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        // when
        GetQuery getQuery = new GetQuery();
        getQuery.setId(documentId);
        SampleEntity sampleEntity1 = elasticsearchTemplate.queryForObject(getQuery, SampleEntity.class);
        // then
        Assert.assertNotNull("entity can't be null....", sampleEntity1);
        Assert.assertEquals(sampleEntity, sampleEntity1);
    }

    @Test
    public void shouldReturnObjectsForGivenIdsUsingMultiGet() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("some message").version(System.currentTimeMillis()).build();
        indexQueries = getIndexQueries(Arrays.asList(sampleEntity1, sampleEntity2));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        SearchQuery query = new NativeSearchQueryBuilder().withIds(Arrays.asList(documentId, documentId2)).build();
        LinkedList<SampleEntity> sampleEntities = elasticsearchTemplate.multiGet(query, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.size(), is(equalTo(2)));
        Assert.assertEquals(sampleEntities.get(0), sampleEntity1);
        Assert.assertEquals(sampleEntities.get(1), sampleEntity2);
    }

    @Test
    public void shouldReturnObjectsForGivenIdsUsingMultiGetWithFields() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId).message("some message").type("type1").version(System.currentTimeMillis()).build();
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("some message").type("type2").version(System.currentTimeMillis()).build();
        indexQueries = getIndexQueries(Arrays.asList(sampleEntity1, sampleEntity2));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        SearchQuery query = new NativeSearchQueryBuilder().withIds(Arrays.asList(documentId, documentId2)).withFields("message", "type").build();
        LinkedList<SampleEntity> sampleEntities = elasticsearchTemplate.multiGet(query, SampleEntity.class, new MultiGetResultMapper() {
            @Override
            public <T> LinkedList<T> mapResults(MultiGetResponse responses, Class<T> clazz) {
                LinkedList<T> list = new LinkedList<>();
                for (MultiGetItemResponse response : responses.getResponses()) {
                    SampleEntity entity = new SampleEntity();
                    entity.setId(getId());
                    setMessage(((String) (response.getResponse().getSource().get("message"))));
                    setType(((String) (response.getResponse().getSource().get("type"))));
                    list.add(((T) (entity)));
                }
                return list;
            }
        });
        // then
        Assert.assertThat(sampleEntities.size(), is(equalTo(2)));
    }

    @Test
    public void shouldReturnPageForGivenSearchQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities, is(notNullValue()));
        Assert.assertThat(sampleEntities.getTotalElements(), greaterThanOrEqualTo(1L));
    }

    // DATAES-422 - Add support for IndicesOptions in search queries
    @Test
    public void shouldPassIndicesOptionsForGivenSearchQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery idxQuery = new IndexQueryBuilder().withIndexName(ElasticsearchTemplateTests.INDEX_1_NAME).withId(getId()).withObject(sampleEntity).build();
        elasticsearchTemplate.index(idxQuery);
        elasticsearchTemplate.refresh(ElasticsearchTemplateTests.INDEX_1_NAME);
        // when
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(ElasticsearchTemplateTests.INDEX_1_NAME, ElasticsearchTemplateTests.INDEX_2_NAME).withIndicesOptions(IndicesOptions.lenientExpandOpen()).build();
        Page<SampleEntity> entities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(entities, is(notNullValue()));
        Assert.assertThat(entities.getTotalElements(), greaterThanOrEqualTo(1L));
    }

    @Test
    public void shouldDoBulkIndex() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("some message").version(System.currentTimeMillis()).build();
        indexQueries = getIndexQueries(Arrays.asList(sampleEntity1, sampleEntity2));
        // when
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.getTotalElements(), is(equalTo(2L)));
    }

    @Test
    public void shouldDoBulkUpdate() {
        // given
        String documentId = randomNumeric(5);
        String messageBeforeUpdate = "some test message";
        String messageAfterUpdate = "test message";
        SampleEntity sampleEntity = builder().id(documentId).message(messageBeforeUpdate).version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source("message", messageAfterUpdate);
        UpdateQuery updateQuery = new UpdateQueryBuilder().withId(documentId).withClass(SampleEntity.class).withIndexRequest(indexRequest).build();
        List<UpdateQuery> queries = new ArrayList<>();
        queries.add(updateQuery);
        // when
        elasticsearchTemplate.bulkUpdate(queries);
        // then
        GetQuery getQuery = new GetQuery();
        getQuery.setId(documentId);
        SampleEntity indexedEntity = elasticsearchTemplate.queryForObject(getQuery, SampleEntity.class);
        Assert.assertThat(getMessage(), is(messageAfterUpdate));
    }

    @Test
    public void shouldDeleteDocumentForGivenId() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        // when
        elasticsearchTemplate.delete(ElasticsearchTemplateTests.INDEX_NAME, ElasticsearchTemplateTests.TYPE_NAME, documentId);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", documentId)).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
    }

    @Test
    public void shouldDeleteEntityForGivenId() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        // when
        elasticsearchTemplate.delete(SampleEntity.class, documentId);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", documentId)).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
    }

    @Test
    public void shouldDeleteDocumentForGivenQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(termQuery("id", documentId));
        elasticsearchTemplate.delete(deleteQuery, SampleEntity.class);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", documentId)).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
    }

    @Test
    public void shouldFilterSearchResultsForGivenFilter() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFilter(boolQuery().filter(termQuery("id", documentId))).build();
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(1L));
    }

    @Test
    public void shouldSortResultsGivenSortCriteria() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId).message("abc").rate(10).version(System.currentTimeMillis()).build();
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("xyz").rate(5).version(System.currentTimeMillis()).build();
        // third document
        String documentId3 = randomNumeric(5);
        SampleEntity sampleEntity3 = builder().id(documentId3).message("xyz").rate(15).version(System.currentTimeMillis()).build();
        indexQueries = getIndexQueries(Arrays.asList(sampleEntity1, sampleEntity2, sampleEntity3));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withSort(new FieldSortBuilder("rate").order(ASC)).build();
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(3L));
        Assert.assertThat(getRate(), is(getRate()));
    }

    @Test
    public void shouldSortResultsGivenMultipleSortCriteria() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId).message("abc").rate(10).version(System.currentTimeMillis()).build();
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("xyz").rate(5).version(System.currentTimeMillis()).build();
        // third document
        String documentId3 = randomNumeric(5);
        SampleEntity sampleEntity3 = builder().id(documentId3).message("xyz").rate(15).version(System.currentTimeMillis()).build();
        indexQueries = getIndexQueries(Arrays.asList(sampleEntity1, sampleEntity2, sampleEntity3));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withSort(new FieldSortBuilder("rate").order(ASC)).withSort(new FieldSortBuilder("message").order(ASC)).build();
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(3L));
        Assert.assertThat(getRate(), is(getRate()));
        Assert.assertThat(getMessage(), is(getMessage()));
    }

    // DATAES-312
    @Test
    public void shouldSortResultsGivenNullFirstSortCriteria() {
        // given
        List<IndexQuery> indexQueries;
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId).message("abc").rate(15).version(System.currentTimeMillis()).build();
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("xyz").rate(5).version(System.currentTimeMillis()).build();
        // third document
        String documentId3 = randomNumeric(5);
        SampleEntity sampleEntity3 = builder().id(documentId3).rate(10).version(System.currentTimeMillis()).build();
        indexQueries = getIndexQueries(Arrays.asList(sampleEntity1, sampleEntity2, sampleEntity3));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withPageable(PageRequest.of(0, 10, Sort.by(Order.asc("message").nullsFirst()))).build();
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(3L));
        Assert.assertThat(getRate(), is(getRate()));
        Assert.assertThat(getMessage(), is(getMessage()));
    }

    // DATAES-312
    @Test
    public void shouldSortResultsGivenNullLastSortCriteria() {
        // given
        List<IndexQuery> indexQueries;
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId).message("abc").rate(15).version(System.currentTimeMillis()).build();
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("xyz").rate(5).version(System.currentTimeMillis()).build();
        // third document
        String documentId3 = randomNumeric(5);
        SampleEntity sampleEntity3 = builder().id(documentId3).rate(10).version(System.currentTimeMillis()).build();
        indexQueries = getIndexQueries(Arrays.asList(sampleEntity1, sampleEntity2, sampleEntity3));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withPageable(PageRequest.of(0, 10, Sort.by(Order.asc("message").nullsLast()))).build();
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(3L));
        Assert.assertThat(getRate(), is(getRate()));
        Assert.assertThat(getMessage(), is(getMessage()));
    }

    // DATAES-467
    @Test
    public void shouldSortResultsByScore() {
        // given
        List<SampleEntity> entities = // 
        // 
        // 
        Arrays.asList(builder().id("1").message("abc").build(), builder().id("2").message("def").build(), builder().id("3").message("ghi").build());
        elasticsearchTemplate.bulkIndex(getIndexQueries(entities));
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withPageable(PageRequest.of(0, 10, Sort.by(Order.asc("_score")))).build();
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(3L));
    }

    @Test
    public void shouldExecuteStringQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        StringQuery stringQuery = new StringQuery(matchAllQuery().toString());
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(stringQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(1L));
    }

    @Test
    public void shouldUseScriptedFields() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setRate(2);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(documentId);
        indexQuery.setObject(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        Map<String, Object> params = new HashMap<>();
        params.put("factor", 2);
        // when
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withScriptField(new ScriptField("scriptedRate", new org.elasticsearch.script.Script(ScriptType.INLINE, "expression", "doc['rate'] * factor", params))).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(1L));
        Assert.assertThat(sampleEntities.getContent().get(0).getScriptedRate(), equalTo(4.0));
    }

    @Test
    public void shouldReturnPageableResultsGivenStringQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        StringQuery stringQuery = new StringQuery(matchAllQuery().toString(), new PageRequest(0, 10));
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(stringQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldReturnSortedPageableResultsGivenStringQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(documentId);
        indexQuery.setObject(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        StringQuery stringQuery = new StringQuery(matchAllQuery().toString(), new PageRequest(0, 10), new Sort(new Sort.Order(Direction.ASC, "message")));
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(stringQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldReturnObjectMatchingGivenStringQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        StringQuery stringQuery = new StringQuery(termQuery("id", documentId).toString());
        // when
        SampleEntity sampleEntity1 = elasticsearchTemplate.queryForObject(stringQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntity1, is(notNullValue()));
        Assert.assertThat(getId(), is(equalTo(documentId)));
    }

    @Test
    public void shouldCreateIndexGivenEntityClass() {
        // when
        boolean created = elasticsearchTemplate.createIndex(SampleEntity.class);
        elasticsearchTemplate.putMapping(SampleEntity.class);
        final Map setting = elasticsearchTemplate.getSetting(SampleEntity.class);
        // then
        Assert.assertThat(created, is(true));
        Assert.assertThat(setting.get("index.number_of_shards"), Matchers.Matchers.<Object>is("1"));
        Assert.assertThat(setting.get("index.number_of_replicas"), Matchers.Matchers.<Object>is("0"));
    }

    @Test
    public void shouldExecuteGivenCriteriaQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("test message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").contains("test"));
        // when
        SampleEntity sampleEntity1 = elasticsearchTemplate.queryForObject(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntity1, is(notNullValue()));
    }

    @Test
    public void shouldDeleteGivenCriteriaQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("test message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").contains("test"));
        // when
        elasticsearchTemplate.delete(criteriaQuery, SampleEntity.class);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        StringQuery stringQuery = new StringQuery(matchAllQuery().toString());
        List<SampleEntity> sampleEntities = elasticsearchTemplate.queryForList(stringQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.size(), is(0));
    }

    @Test
    public void shouldReturnSpecifiedFields() {
        // given
        String documentId = randomNumeric(5);
        String message = "some test message";
        SampleEntity sampleEntity = builder().id(documentId).message(message).version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withFields("message").build();
        // when
        Page<String> page = elasticsearchTemplate.queryForPage(searchQuery, String.class, new SearchResultMapperAdapter() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<String> values = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    values.add(((String) (searchHit.getSourceAsMap().get("message"))));
                }
                return new org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl(((List<T>) (values)));
            }
        });
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
        Assert.assertThat(page.getContent().get(0), is(message));
    }

    @Test
    public void shouldReturnFieldsBasedOnSourceFilter() {
        // given
        String documentId = randomNumeric(5);
        String message = "some test message";
        SampleEntity sampleEntity = builder().id(documentId).message(message).version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        FetchSourceFilterBuilder sourceFilter = new FetchSourceFilterBuilder();
        sourceFilter.withIncludes("message");
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withSourceFilter(sourceFilter.build()).build();
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
        Assert.assertThat(getMessage(), is(message));
    }

    @Test
    public void shouldReturnSimilarResultsGivenMoreLikeThisQuery() {
        // given
        String sampleMessage = "So we build a web site or an application and want to add search to it, " + ((("and then it hits us: getting search working is hard. We want our search solution to be fast," + " we want a painless setup and a completely free search schema, we want to be able to index data simply using JSON over HTTP, ") + "we want our search server to be always available, we want to be able to start with one machine and scale to hundreds, ") + "we want real-time search, we want simple multi-tenancy, and we want a solution that is built for the cloud.");
        String documentId1 = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId1).message(sampleMessage).version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        String documentId2 = randomNumeric(5);
        elasticsearchTemplate.index(getIndexQuery(builder().id(documentId2).message(sampleMessage).version(System.currentTimeMillis()).build()));
        elasticsearchTemplate.refresh(SampleEntity.class);
        MoreLikeThisQuery moreLikeThisQuery = new MoreLikeThisQuery();
        moreLikeThisQuery.setId(documentId2);
        moreLikeThisQuery.addFields("message");
        moreLikeThisQuery.setMinDocFreq(1);
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.moreLikeThis(moreLikeThisQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), is(equalTo(1L)));
        Assert.assertThat(sampleEntities.getContent(), hasItem(sampleEntity));
    }

    /* DATAES-167 */
    @Test
    public void shouldReturnResultsWithScanAndScrollForGivenCriteriaQuery() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        criteriaQuery.addIndices(ElasticsearchTemplateTests.INDEX_NAME);
        criteriaQuery.addTypes(ElasticsearchTemplateTests.TYPE_NAME);
        criteriaQuery.setPageable(new PageRequest(0, 10));
        ScrolledPage<SampleEntity> scroll = ((ScrolledPage<SampleEntity>) (elasticsearchTemplate.startScroll(1000, criteriaQuery, SampleEntity.class)));
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scroll = ((ScrolledPage<SampleEntity>) (elasticsearchTemplate.continueScroll(scroll.getScrollId(), 1000, SampleEntity.class)));
        } 
        elasticsearchTemplate.clearScroll(scroll.getScrollId());
        Assert.assertThat(sampleEntities.size(), is(equalTo(30)));
    }

    @Test
    public void shouldReturnResultsWithScanAndScrollForGivenSearchQuery() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withPageable(new PageRequest(0, 10)).build();
        ScrolledPage<SampleEntity> scroll = ((ScrolledPage<SampleEntity>) (elasticsearchTemplate.startScroll(1000, searchQuery, SampleEntity.class)));
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scroll = ((ScrolledPage<SampleEntity>) (elasticsearchTemplate.continueScroll(scroll.getScrollId(), 1000, SampleEntity.class)));
        } 
        elasticsearchTemplate.clearScroll(scroll.getScrollId());
        Assert.assertThat(sampleEntities.size(), is(equalTo(30)));
    }

    final SearchResultMapper searchResultMapper = new SearchResultMapperAdapter() {
        @Override
        public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
            List<SampleEntity> result = new ArrayList<>();
            for (SearchHit searchHit : response.getHits()) {
                if ((response.getHits().getHits().length) <= 0) {
                    return new org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl<T>(Collections.EMPTY_LIST, response.getScrollId());
                }
                String message = ((String) (searchHit.getSourceAsMap().get("message")));
                SampleEntity sampleEntity = new SampleEntity();
                sampleEntity.setId(searchHit.getId());
                setMessage(message);
                result.add(sampleEntity);
            }
            if ((result.size()) > 0) {
                return new org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl<T>(((List<T>) (result)), response.getScrollId());
            }
            return new org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl<T>(Collections.emptyList(), response.getScrollId());
        }
    };

    /* DATAES-167 */
    @Test
    public void shouldReturnResultsWithScanAndScrollForSpecifiedFieldsForCriteriaQuery() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        criteriaQuery.addIndices(ElasticsearchTemplateTests.INDEX_NAME);
        criteriaQuery.addTypes(ElasticsearchTemplateTests.TYPE_NAME);
        criteriaQuery.addFields("message");
        criteriaQuery.setPageable(new PageRequest(0, 10));
        Page<SampleEntity> scroll = elasticsearchTemplate.startScroll(1000, criteriaQuery, SampleEntity.class, searchResultMapper);
        String scrollId = ((ScrolledPage<?>) (scroll)).getScrollId();
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scrollId = ((ScrolledPage<?>) (scroll)).getScrollId();
            scroll = elasticsearchTemplate.continueScroll(scrollId, 1000, SampleEntity.class, searchResultMapper);
        } 
        elasticsearchTemplate.clearScroll(scrollId);
        Assert.assertThat(sampleEntities.size(), is(equalTo(30)));
    }

    /* DATAES-84 */
    @Test
    public void shouldReturnResultsWithScanAndScrollForSpecifiedFieldsForSearchCriteria() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withFields("message").withQuery(matchAllQuery()).withPageable(new PageRequest(0, 10)).build();
        Page<SampleEntity> scroll = elasticsearchTemplate.startScroll(1000, searchQuery, SampleEntity.class, searchResultMapper);
        String scrollId = getScrollId();
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scrollId = getScrollId();
            scroll = elasticsearchTemplate.continueScroll(scrollId, 1000, SampleEntity.class, searchResultMapper);
        } 
        elasticsearchTemplate.clearScroll(scrollId);
        Assert.assertThat(sampleEntities.size(), is(equalTo(30)));
    }

    /* DATAES-167 */
    @Test
    public void shouldReturnResultsForScanAndScrollWithCustomResultMapperForGivenCriteriaQuery() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        criteriaQuery.addIndices(ElasticsearchTemplateTests.INDEX_NAME);
        criteriaQuery.addTypes(ElasticsearchTemplateTests.TYPE_NAME);
        criteriaQuery.setPageable(new PageRequest(0, 10));
        Page<SampleEntity> scroll = elasticsearchTemplate.startScroll(1000, criteriaQuery, SampleEntity.class, searchResultMapper);
        String scrollId = getScrollId();
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scrollId = getScrollId();
            scroll = elasticsearchTemplate.continueScroll(scrollId, 1000, SampleEntity.class, searchResultMapper);
        } 
        elasticsearchTemplate.clearScroll(scrollId);
        Assert.assertThat(sampleEntities.size(), is(equalTo(30)));
    }

    @Test
    public void shouldReturnResultsForScanAndScrollWithCustomResultMapperForGivenSearchQuery() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withPageable(new PageRequest(0, 10)).build();
        Page<SampleEntity> scroll = elasticsearchTemplate.startScroll(1000, searchQuery, SampleEntity.class, searchResultMapper);
        String scrollId = getScrollId();
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scrollId = getScrollId();
            scroll = elasticsearchTemplate.continueScroll(scrollId, 1000, SampleEntity.class, searchResultMapper);
        } 
        elasticsearchTemplate.clearScroll(scrollId);
        Assert.assertThat(sampleEntities.size(), is(equalTo(30)));
    }

    /* DATAES-217 */
    @Test
    public void shouldReturnResultsWithScanAndScrollForGivenCriteriaQueryAndClass() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        criteriaQuery.setPageable(new PageRequest(0, 10));
        Page<SampleEntity> scroll = elasticsearchTemplate.startScroll(1000, criteriaQuery, SampleEntity.class);
        String scrollId = getScrollId();
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scrollId = getScrollId();
            scroll = elasticsearchTemplate.continueScroll(scrollId, 1000, SampleEntity.class);
        } 
        elasticsearchTemplate.clearScroll(scrollId);
        Assert.assertThat(sampleEntities.size(), is(equalTo(30)));
    }

    /* DATAES-217 */
    @Test
    public void shouldReturnResultsWithScanAndScrollForGivenSearchQueryAndClass() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withPageable(new PageRequest(0, 10)).build();
        Page<SampleEntity> scroll = elasticsearchTemplate.startScroll(1000, searchQuery, SampleEntity.class);
        String scrollId = getScrollId();
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scrollId = getScrollId();
            scroll = elasticsearchTemplate.continueScroll(scrollId, 1000, SampleEntity.class);
        } 
        elasticsearchTemplate.clearScroll(scrollId);
        Assert.assertThat(sampleEntities.size(), is(equalTo(30)));
    }

    /* DATAES-167 */
    @Test
    public void shouldReturnResultsWithStreamForGivenCriteriaQuery() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        criteriaQuery.addIndices(ElasticsearchTemplateTests.INDEX_NAME);
        criteriaQuery.addTypes(ElasticsearchTemplateTests.TYPE_NAME);
        criteriaQuery.setPageable(new PageRequest(0, 10));
        CloseableIterator<SampleEntity> stream = elasticsearchTemplate.stream(criteriaQuery, SampleEntity.class);
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (stream.hasNext()) {
            sampleEntities.add(stream.next());
        } 
        Assert.assertThat(sampleEntities.size(), is(equalTo(30)));
    }

    @Test
    public void shouldReturnListForGivenCriteria() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId).message("test message").version(System.currentTimeMillis()).build();
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("test test").rate(5).version(System.currentTimeMillis()).build();
        // third document
        String documentId3 = randomNumeric(5);
        SampleEntity sampleEntity3 = builder().id(documentId3).message("some message").rate(15).version(System.currentTimeMillis()).build();
        indexQueries = getIndexQueries(Arrays.asList(sampleEntity1, sampleEntity2, sampleEntity3));
        // when
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        CriteriaQuery singleCriteriaQuery = new CriteriaQuery(new Criteria("message").contains("test"));
        CriteriaQuery multipleCriteriaQuery = new CriteriaQuery(contains("message"));
        List<SampleEntity> sampleEntitiesForSingleCriteria = elasticsearchTemplate.queryForList(singleCriteriaQuery, SampleEntity.class);
        List<SampleEntity> sampleEntitiesForAndCriteria = elasticsearchTemplate.queryForList(multipleCriteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntitiesForSingleCriteria.size(), is(2));
        Assert.assertThat(sampleEntitiesForAndCriteria.size(), is(1));
    }

    @Test
    public void shouldReturnListForGivenStringQuery() {
        // given
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId).message("test message").version(System.currentTimeMillis()).build();
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("test test").rate(5).version(System.currentTimeMillis()).build();
        // third document
        String documentId3 = randomNumeric(5);
        SampleEntity sampleEntity3 = builder().id(documentId3).message("some message").rate(15).version(System.currentTimeMillis()).build();
        List<IndexQuery> indexQueries = getIndexQueries(Arrays.asList(sampleEntity1, sampleEntity2, sampleEntity3));
        // when
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        StringQuery stringQuery = new StringQuery(matchAllQuery().toString());
        List<SampleEntity> sampleEntities = elasticsearchTemplate.queryForList(stringQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.size(), is(3));
    }

    @Test
    public void shouldPutMappingForGivenEntity() throws Exception {
        // given
        Class entity = SampleMappingEntity.class;
        elasticsearchTemplate.deleteIndex(entity);
        elasticsearchTemplate.createIndex(entity);
        // when
        Assert.assertThat(elasticsearchTemplate.putMapping(entity), is(true));
    }

    @Test
    public void shouldDeleteIndexForGivenEntity() {
        // given
        Class clazz = SampleEntity.class;
        // when
        elasticsearchTemplate.deleteIndex(clazz);
        // then
        Assert.assertThat(elasticsearchTemplate.indexExists(clazz), is(false));
    }

    @Test
    public void shouldDoPartialUpdateForExistingDocument() {
        // given
        String documentId = randomNumeric(5);
        String messageBeforeUpdate = "some test message";
        String messageAfterUpdate = "test message";
        SampleEntity sampleEntity = builder().id(documentId).message(messageBeforeUpdate).version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source("message", messageAfterUpdate);
        UpdateQuery updateQuery = new UpdateQueryBuilder().withId(documentId).withClass(SampleEntity.class).withIndexRequest(indexRequest).build();
        // when
        elasticsearchTemplate.update(updateQuery);
        // then
        GetQuery getQuery = new GetQuery();
        getQuery.setId(documentId);
        SampleEntity indexedEntity = elasticsearchTemplate.queryForObject(getQuery, SampleEntity.class);
        Assert.assertThat(getMessage(), is(messageAfterUpdate));
    }

    @Test
    public void shouldDoUpsertIfDocumentDoesNotExist() {
        // given
        String documentId = randomNumeric(5);
        String message = "test message";
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source("message", message);
        UpdateQuery updateQuery = new UpdateQueryBuilder().withId(documentId).withDoUpsert(true).withClass(SampleEntity.class).withIndexRequest(indexRequest).build();
        // when
        elasticsearchTemplate.update(updateQuery);
        // then
        GetQuery getQuery = new GetQuery();
        getQuery.setId(documentId);
        SampleEntity indexedEntity = elasticsearchTemplate.queryForObject(getQuery, SampleEntity.class);
        Assert.assertThat(getMessage(), is(message));
    }

    @Test
    public void shouldReturnHighlightedFieldsForGivenQueryAndFields() {
        // given
        String documentId = randomNumeric(5);
        String actualMessage = "some test message";
        String highlightedMessage = "some <em>test</em> message";
        SampleEntity sampleEntity = builder().id(documentId).message(actualMessage).version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        final List<HighlightBuilder.Field> message = new HighlightBuilder().field("message").fields();
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("message", "test")).withHighlightFields(message.toArray(new HighlightBuilder.Field[message.size()])).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class, new SearchResultMapperAdapter() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<SampleEntity> chunk = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    if ((response.getHits().getHits().length) <= 0) {
                        return null;
                    }
                    SampleEntity user = new SampleEntity();
                    user.setId(searchHit.getId());
                    setMessage(((String) (searchHit.getSourceAsMap().get("message"))));
                    user.setHighlightedMessage(searchHit.getHighlightFields().get("message").fragments()[0].toString());
                    chunk.add(user);
                }
                if ((chunk.size()) > 0) {
                    return new org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl(((List<T>) (chunk)));
                }
                return null;
            }
        });
        Assert.assertThat(sampleEntities.getContent().get(0).getHighlightedMessage(), is(highlightedMessage));
    }

    // DATAES-412
    @Test
    public void shouldReturnMultipleHighlightFields() {
        // given
        String documentId = randomNumeric(5);
        String actualType = "some test type";
        String actualMessage = "some test message";
        String highlightedType = "some <em>test</em> type";
        String highlightedMessage = "some <em>test</em> message";
        SampleEntity sampleEntity = builder().id(documentId).type(actualType).message(actualMessage).version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery().must(termQuery("type", "test")).must(termQuery("message", "test"))).withHighlightFields(new HighlightBuilder.Field("type"), new HighlightBuilder.Field("message")).build();
        // when
        elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class, new SearchResultMapperAdapter() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                for (SearchHit searchHit : response.getHits()) {
                    Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                    HighlightField highlightFieldType = highlightFields.get("type");
                    HighlightField highlightFieldMessage = highlightFields.get("message");
                    // then
                    Assert.assertNotNull(highlightFieldType);
                    Assert.assertNotNull(highlightFieldMessage);
                    Assert.assertThat(highlightFieldType.fragments()[0].toString(), is(highlightedType));
                    Assert.assertThat(highlightFieldMessage.fragments()[0].toString(), is(highlightedMessage));
                }
                return null;
            }
        });
    }

    // DATAES-479
    @Test
    public void shouldHonorTheHighlightBuilderOptions() {
        // given
        String documentId = randomNumeric(5);
        String actualMessage = "some test message with <html> unsafe <script> text";
        String highlightedMessage = "some <em>test</em> message with &lt;html&gt; unsafe &lt;script&gt; text";
        SampleEntity sampleEntity = builder().id(documentId).message(actualMessage).version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("message", "test")).withHighlightBuilder(new HighlightBuilder().encoder("html")).withHighlightFields(new HighlightBuilder.Field("message")).build();
        // when
        elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class, new SearchResultMapperAdapter() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                for (SearchHit searchHit : response.getHits()) {
                    Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                    HighlightField highlightFieldMessage = highlightFields.get("message");
                    // then
                    Assert.assertNotNull(highlightFieldMessage);
                    Assert.assertThat(highlightFieldMessage.fragments()[0].toString(), is(highlightedMessage));
                }
                return null;
            }
        });
    }

    // DATAES-479
    @Test
    public void shouldHighlightIfBuilderSetEvenIfFieldsNotSet() {
        // given
        String documentId = randomNumeric(5);
        String actualMessage = "some test message text";
        String highlightedMessage = "some <em>test</em> message text";
        SampleEntity sampleEntity = builder().id(documentId).message(actualMessage).version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("message", "test")).withHighlightBuilder(new HighlightBuilder().field("message")).build();
        // when
        elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                for (SearchHit searchHit : response.getHits()) {
                    Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                    HighlightField highlightFieldMessage = highlightFields.get("message");
                    // then
                    Assert.assertNotNull(highlightFieldMessage);
                    Assert.assertThat(highlightFieldMessage.fragments()[0].toString(), is(highlightedMessage));
                }
                return null;
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> type) {
                return null;
            }
        });
    }

    // DATAES-487
    @Test
    public void shouldReturnSameEntityForMultiSearch() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        indexQueries.add(IndexBuilder.buildIndex(builder().id("1").message("ab").build()));
        indexQueries.add(IndexBuilder.buildIndex(builder().id("2").message("bc").build()));
        indexQueries.add(IndexBuilder.buildIndex(builder().id("3").message("ac").build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        List<SearchQuery> queries = new ArrayList<>();
        queries.add(new NativeSearchQueryBuilder().withQuery(termQuery("message", "ab")).build());
        queries.add(new NativeSearchQueryBuilder().withQuery(termQuery("message", "bc")).build());
        queries.add(new NativeSearchQueryBuilder().withQuery(termQuery("message", "ac")).build());
        // then
        List<Page<SampleEntity>> sampleEntities = elasticsearchTemplate.queryForPage(queries, SampleEntity.class);
        for (Page<SampleEntity> sampleEntity : sampleEntities) {
            Assert.assertThat(sampleEntity.getTotalElements(), equalTo(1L));
        }
    }

    // DATAES-487
    @Test
    public void shouldReturnDifferentEntityForMultiSearch() {
        // given
        Class<Book> clazz = Book.class;
        elasticsearchTemplate.deleteIndex(clazz);
        elasticsearchTemplate.createIndex(clazz);
        elasticsearchTemplate.putMapping(clazz);
        elasticsearchTemplate.refresh(clazz);
        List<IndexQuery> indexQueries = new ArrayList<>();
        indexQueries.add(IndexBuilder.buildIndex(builder().id("1").message("ab").build()));
        indexQueries.add(IndexBuilder.buildIndex(builder().id("2").description("bc").build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        elasticsearchTemplate.refresh(clazz);
        // when
        List<SearchQuery> queries = new ArrayList<>();
        queries.add(new NativeSearchQueryBuilder().withQuery(termQuery("message", "ab")).build());
        queries.add(new NativeSearchQueryBuilder().withQuery(termQuery("description", "bc")).build());
        // then
        List<Page<?>> pages = elasticsearchTemplate.queryForPage(queries, Lists.newArrayList(SampleEntity.class, clazz));
        Assert.assertThat(pages.get(0).getTotalElements(), equalTo(1L));
        Assert.assertThat(pages.get(0).getContent().get(0).getClass(), equalTo(SampleEntity.class));
        Assert.assertThat(pages.get(1).getTotalElements(), equalTo(1L));
        Assert.assertThat(pages.get(1).getContent().get(0).getClass(), equalTo(clazz));
    }

    @Test
    public void shouldDeleteDocumentBySpecifiedTypeUsingDeleteQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(termQuery("id", documentId));
        deleteQuery.setIndex(ElasticsearchTemplateTests.INDEX_NAME);
        deleteQuery.setType(ElasticsearchTemplateTests.TYPE_NAME);
        elasticsearchTemplate.delete(deleteQuery);
        elasticsearchTemplate.refresh(ElasticsearchTemplateTests.INDEX_NAME);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", documentId)).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
    }

    @Test
    public void shouldIndexDocumentForSpecifiedSource() {
        // given
        String documentSource = "{\"id\":\"2333343434\",\"type\":null,\"message\":\"some message\",\"rate\":0,\"available\":false,\"highlightedMessage\":null,\"version\":1385208779482}";
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId("2333343434");
        indexQuery.setSource(documentSource);
        indexQuery.setIndexName(ElasticsearchTemplateTests.INDEX_NAME);
        indexQuery.setType(ElasticsearchTemplateTests.TYPE_NAME);
        // when
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", indexQuery.getId())).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).build();
        // then
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class, new SearchResultMapperAdapter() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<SampleEntity> values = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    SampleEntity sampleEntity = new SampleEntity();
                    sampleEntity.setId(searchHit.getId());
                    setMessage(((String) (searchHit.getSourceAsMap().get("message"))));
                    values.add(sampleEntity);
                }
                return new org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl(((List<T>) (values)));
            }
        });
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getContent().size(), is(1));
        Assert.assertThat(getId(), is(indexQuery.getId()));
    }

    @Test(expected = ElasticsearchException.class)
    public void shouldThrowElasticsearchExceptionWhenNoDocumentSpecified() {
        // given
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId("2333343434");
        indexQuery.setIndexName(ElasticsearchTemplateTests.INDEX_NAME);
        indexQuery.setType(ElasticsearchTemplateTests.TYPE_NAME);
        // when
        elasticsearchTemplate.index(indexQuery);
    }

    @Test
    public void shouldReturnIds() {
        // given
        List<IndexQuery> entities = ElasticsearchTemplateTests.createSampleEntitiesWithMessage("Test message", 30);
        // when
        elasticsearchTemplate.bulkIndex(entities);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("message", "message")).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withPageable(new PageRequest(0, 100)).build();
        // then
        List<String> ids = elasticsearchTemplate.queryForIds(searchQuery);
        Assert.assertThat(ids, is(notNullValue()));
        Assert.assertThat(ids.size(), is(30));
    }

    @Test
    public void shouldReturnDocumentAboveMinimalScoreGivenQuery() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        indexQueries.add(IndexBuilder.buildIndex(builder().id("1").message("ab").build()));
        indexQueries.add(IndexBuilder.buildIndex(builder().id("2").message("bc").build()));
        indexQueries.add(IndexBuilder.buildIndex(builder().id("3").message("ac").build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery().must(wildcardQuery("message", "*a*")).should(wildcardQuery("message", "*b*"))).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withMinScore(2.0F).build();
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(page.getTotalElements(), is(1L));
        Assert.assertThat(getMessage(), is("ab"));
    }

    // DATAES-462
    @Test
    public void shouldReturnScores() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        indexQueries.add(IndexBuilder.buildIndex(builder().id("1").message("ab xz").build()));
        indexQueries.add(IndexBuilder.buildIndex(builder().id("2").message("bc").build()));
        indexQueries.add(IndexBuilder.buildIndex(builder().id("3").message("ac xz hi").build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("message", "xz")).withSort(SortBuilders.fieldSort("message")).withTrackScores(true).build();
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(page, instanceOf(AggregatedPage.class));
        Assert.assertThat(getMaxScore(), greaterThan(0.0F));
        Assert.assertThat(page.getContent().get(0).getScore(), greaterThan(0.0F));
    }

    @Test
    public void shouldDoIndexWithoutId() {
        // given
        // document
        SampleEntity sampleEntity = new SampleEntity();
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setObject(sampleEntity);
        // when
        String documentId = elasticsearchTemplate.index(indexQuery);
        // then
        Assert.assertThat(getId(), is(equalTo(documentId)));
        GetQuery getQuery = new GetQuery();
        getQuery.setId(documentId);
        SampleEntity result = elasticsearchTemplate.queryForObject(getQuery, SampleEntity.class);
        Assert.assertThat(getId(), is(equalTo(documentId)));
    }

    @Test
    public void shouldDoBulkIndexWithoutId() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        SampleEntity sampleEntity1 = new SampleEntity();
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        SampleEntity sampleEntity2 = new SampleEntity();
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        // when
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.getTotalElements(), is(equalTo(2L)));
        Assert.assertThat(getId(), is(notNullValue()));
        Assert.assertThat(getId(), is(notNullValue()));
    }

    @Test
    public void shouldIndexMapWithIndexNameAndTypeAtRuntime() {
        // given
        Map<String, Object> person1 = new HashMap<>();
        person1.put("userId", "1");
        person1.put("email", "smhdiu@gmail.com");
        person1.put("title", "Mr");
        person1.put("firstName", "Mohsin");
        person1.put("lastName", "Husen");
        Map<String, Object> person2 = new HashMap<>();
        person2.put("userId", "2");
        person2.put("email", "akonczak@gmail.com");
        person2.put("title", "Mr");
        person2.put("firstName", "Artur");
        person2.put("lastName", "Konczak");
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId("1");
        indexQuery1.setObject(person1);
        indexQuery1.setIndexName(ElasticsearchTemplateTests.INDEX_NAME);
        indexQuery1.setType(ElasticsearchTemplateTests.TYPE_NAME);
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId("2");
        indexQuery2.setObject(person2);
        indexQuery2.setIndexName(ElasticsearchTemplateTests.INDEX_NAME);
        indexQuery2.setType(ElasticsearchTemplateTests.TYPE_NAME);
        List<IndexQuery> indexQueries = new ArrayList<>();
        indexQueries.add(indexQuery1);
        indexQueries.add(indexQuery2);
        // when
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(ElasticsearchTemplateTests.INDEX_NAME);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withQuery(matchAllQuery()).build();
        Page<Map> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, Map.class, new SearchResultMapperAdapter() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<Map> chunk = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    if ((response.getHits().getHits().length) <= 0) {
                        return null;
                    }
                    Map<String, Object> person = new HashMap<>();
                    person.put("userId", searchHit.getSourceAsMap().get("userId"));
                    person.put("email", searchHit.getSourceAsMap().get("email"));
                    person.put("title", searchHit.getSourceAsMap().get("title"));
                    person.put("firstName", searchHit.getSourceAsMap().get("firstName"));
                    person.put("lastName", searchHit.getSourceAsMap().get("lastName"));
                    chunk.add(person);
                }
                if ((chunk.size()) > 0) {
                    return new org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl(((List<T>) (chunk)));
                }
                return null;
            }
        });
        Assert.assertThat(sampleEntities.getTotalElements(), is(equalTo(2L)));
        Assert.assertThat(sampleEntities.getContent().get(0).get("userId"), is(person1.get("userId")));
        Assert.assertThat(sampleEntities.getContent().get(1).get("userId"), is(person2.get("userId")));
    }

    /* DATAES-523 */
    @Test
    public void shouldIndexGteEntityWithVersionType() {
        // given
        String documentId = randomNumeric(5);
        GTEVersionEntity entity = builder().id(documentId).name("FooBar").version(System.currentTimeMillis()).build();
        IndexQueryBuilder indexQueryBuilder = new IndexQueryBuilder().withId(documentId).withIndexName(ElasticsearchTemplateTests.INDEX_NAME).withType(ElasticsearchTemplateTests.TYPE_NAME).withVersion(getVersion()).withObject(entity);
        Exception ex = null;
        try {
            elasticsearchTemplate.index(indexQueryBuilder.build());
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertNull(ex);
        elasticsearchTemplate.refresh(ElasticsearchTemplateTests.INDEX_NAME);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withQuery(matchAllQuery()).build();
        // when
        Page<GTEVersionEntity> entities = elasticsearchTemplate.queryForPage(searchQuery, GTEVersionEntity.class);
        // then
        Assert.assertThat(entities, is(notNullValue()));
        Assert.assertThat(entities.getTotalElements(), greaterThanOrEqualTo(1L));
        // reindex with same version
        try {
            elasticsearchTemplate.index(indexQueryBuilder.build());
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertNull(ex);
        elasticsearchTemplate.refresh(ElasticsearchTemplateTests.INDEX_NAME);
        // reindex with version one below
        try {
            elasticsearchTemplate.index(indexQueryBuilder.withVersion(((getVersion()) - 1)).build());
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
        String message = ex.getMessage().toLowerCase();
        Assert.assertTrue("Exception is version conflict", ((message.contains("version")) && (message.contains("conflict"))));
    }

    @Test
    public void shouldIndexSampleEntityWithIndexAndTypeAtRuntime() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = new IndexQueryBuilder().withId(documentId).withIndexName(ElasticsearchTemplateTests.INDEX_NAME).withType(ElasticsearchTemplateTests.TYPE_NAME).withObject(sampleEntity).build();
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(ElasticsearchTemplateTests.INDEX_NAME);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withQuery(matchAllQuery()).build();
        // when
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities, is(notNullValue()));
        Assert.assertThat(sampleEntities.getTotalElements(), greaterThanOrEqualTo(1L));
    }

    /* DATAES-106 */
    @Test
    public void shouldReturnCountForGivenCriteriaQueryWithGivenIndexUsingCriteriaQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        criteriaQuery.addIndices(ElasticsearchTemplateTests.INDEX_NAME);
        // when
        long count = elasticsearchTemplate.count(criteriaQuery);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-67 */
    @Test
    public void shouldReturnCountForGivenSearchQueryWithGivenIndexUsingSearchQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(ElasticsearchTemplateTests.INDEX_NAME).build();
        // when
        long count = elasticsearchTemplate.count(searchQuery);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldReturnCountForGivenCriteriaQueryWithGivenIndexAndTypeUsingCriteriaQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        criteriaQuery.addIndices(ElasticsearchTemplateTests.INDEX_NAME);
        criteriaQuery.addTypes("test-type");
        // when
        long count = elasticsearchTemplate.count(criteriaQuery);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-67 */
    @Test
    public void shouldReturnCountForGivenSearchQueryWithGivenIndexAndTypeUsingSearchQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes("test-type").build();
        // when
        long count = elasticsearchTemplate.count(searchQuery);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldReturnCountForGivenCriteriaQueryWithGivenMultiIndices() {
        // given
        cleanUpIndices();
        String documentId1 = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId1).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery1 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-1").withObject(sampleEntity1).build();
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("some test message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery2 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-2").withObject(sampleEntity2).build();
        elasticsearchTemplate.bulkIndex(Arrays.asList(indexQuery1, indexQuery2));
        elasticsearchTemplate.refresh("test-index-1");
        elasticsearchTemplate.refresh("test-index-2");
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        criteriaQuery.addIndices("test-index-1", "test-index-2");
        // when
        long count = elasticsearchTemplate.count(criteriaQuery);
        // then
        Assert.assertThat(count, is(equalTo(2L)));
    }

    /* DATAES-67 */
    @Test
    public void shouldReturnCountForGivenSearchQueryWithGivenMultiIndices() {
        // given
        cleanUpIndices();
        String documentId1 = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId1).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery1 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-1").withObject(sampleEntity1).build();
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("some test message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery2 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-2").withObject(sampleEntity2).build();
        elasticsearchTemplate.bulkIndex(Arrays.asList(indexQuery1, indexQuery2));
        elasticsearchTemplate.refresh("test-index-1");
        elasticsearchTemplate.refresh("test-index-2");
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices("test-index-1", "test-index-2").build();
        // when
        long count = elasticsearchTemplate.count(searchQuery);
        // then
        Assert.assertThat(count, is(equalTo(2L)));
    }

    /* DATAES-71 */
    @Test
    public void shouldCreatedIndexWithSpecifiedIndexName() {
        // given
        elasticsearchTemplate.deleteIndex("test-index");
        // when
        elasticsearchTemplate.createIndex("test-index");
        // then
        Assert.assertThat(elasticsearchTemplate.indexExists("test-index"), is(true));
    }

    /* DATAES-72 */
    @Test
    public void shouldDeleteIndexForSpecifiedIndexName() {
        // given
        elasticsearchTemplate.createIndex(SampleEntity.class);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        elasticsearchTemplate.deleteIndex("test-index");
        // then
        Assert.assertThat(elasticsearchTemplate.indexExists("test-index"), is(false));
    }

    /* DATAES-106 */
    @Test
    public void shouldReturnCountForGivenCriteriaQueryWithGivenIndexNameForSpecificIndex() {
        // given
        cleanUpIndices();
        String documentId1 = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId1).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery1 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-1").withObject(sampleEntity1).build();
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("some test message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery2 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-2").withObject(sampleEntity2).build();
        elasticsearchTemplate.bulkIndex(Arrays.asList(indexQuery1, indexQuery2));
        elasticsearchTemplate.refresh("test-index-1");
        elasticsearchTemplate.refresh("test-index-2");
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        criteriaQuery.addIndices("test-index-1");
        // when
        long count = elasticsearchTemplate.count(criteriaQuery);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-67 */
    @Test
    public void shouldReturnCountForGivenSearchQueryWithGivenIndexNameForSpecificIndex() {
        // given
        cleanUpIndices();
        String documentId1 = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId1).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery1 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-1").withObject(sampleEntity1).build();
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("some test message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery2 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-2").withObject(sampleEntity2).build();
        elasticsearchTemplate.bulkIndex(Arrays.asList(indexQuery1, indexQuery2));
        elasticsearchTemplate.refresh("test-index-1");
        elasticsearchTemplate.refresh("test-index-2");
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices("test-index-1").build();
        // when
        long count = elasticsearchTemplate.count(searchQuery);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionForGivenCriteriaQueryWhenNoIndexSpecifiedForCountQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria());
        // when
        long count = elasticsearchTemplate.count(criteriaQuery);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-67 */
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionForGivenSearchQueryWhenNoIndexSpecifiedForCountQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = builder().id(documentId).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery = getIndexQuery(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        // when
        long count = elasticsearchTemplate.count(searchQuery);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-71 */
    @Test
    public void shouldCreateIndexWithGivenSettings() {
        // given
        String settings = "{\n" + (((((((((((("        \"index\": {\n" + "            \"number_of_shards\": \"1\",\n") + "            \"number_of_replicas\": \"0\",\n") + "            \"analysis\": {\n") + "                \"analyzer\": {\n") + "                    \"emailAnalyzer\": {\n") + "                        \"type\": \"custom\",\n") + "                        \"tokenizer\": \"uax_url_email\"\n") + "                    }\n") + "                }\n") + "            }\n") + "        }\n") + "}");
        elasticsearchTemplate.deleteIndex("test-index");
        // when
        elasticsearchTemplate.createIndex("test-index", settings);
        // then
        Map map = elasticsearchTemplate.getSetting("test-index");
        boolean hasAnalyzer = map.containsKey("index.analysis.analyzer.emailAnalyzer.tokenizer");
        String emailAnalyzer = ((String) (map.get("index.analysis.analyzer.emailAnalyzer.tokenizer")));
        Assert.assertThat(elasticsearchTemplate.indexExists("test-index"), is(true));
        Assert.assertThat(hasAnalyzer, is(true));
        Assert.assertThat(emailAnalyzer, is("uax_url_email"));
    }

    /* DATAES-71 */
    @Test
    public void shouldCreateGivenSettingsForGivenIndex() {
        // given
        // delete , create and apply mapping in before method
        // then
        Map map = elasticsearchTemplate.getSetting(SampleEntity.class);
        Assert.assertThat(elasticsearchTemplate.indexExists("test-index"), is(true));
        Assert.assertThat(map.containsKey("index.refresh_interval"), is(true));
        Assert.assertThat(map.containsKey("index.number_of_replicas"), is(true));
        Assert.assertThat(map.containsKey("index.number_of_shards"), is(true));
        Assert.assertThat(map.containsKey("index.store.type"), is(true));
        Assert.assertThat(((String) (map.get("index.refresh_interval"))), is("-1"));
        Assert.assertThat(((String) (map.get("index.number_of_replicas"))), is("0"));
        Assert.assertThat(((String) (map.get("index.number_of_shards"))), is("1"));
        Assert.assertThat(((String) (map.get("index.store.type"))), is("fs"));
    }

    /* DATAES-88 */
    @Test
    public void shouldCreateIndexWithGivenClassAndSettings() {
        // given
        String settings = "{\n" + (((((((((((("        \"index\": {\n" + "            \"number_of_shards\": \"1\",\n") + "            \"number_of_replicas\": \"0\",\n") + "            \"analysis\": {\n") + "                \"analyzer\": {\n") + "                    \"emailAnalyzer\": {\n") + "                        \"type\": \"custom\",\n") + "                        \"tokenizer\": \"uax_url_email\"\n") + "                    }\n") + "                }\n") + "            }\n") + "        }\n") + "}");
        elasticsearchTemplate.deleteIndex(SampleEntity.class);
        elasticsearchTemplate.createIndex(SampleEntity.class, settings);
        elasticsearchTemplate.putMapping(SampleEntity.class);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        Map map = elasticsearchTemplate.getSetting(SampleEntity.class);
        Assert.assertThat(elasticsearchTemplate.indexExists(ElasticsearchTemplateTests.INDEX_NAME), is(true));
        Assert.assertThat(map.containsKey("index.number_of_replicas"), is(true));
        Assert.assertThat(map.containsKey("index.number_of_shards"), is(true));
        Assert.assertThat(((String) (map.get("index.number_of_replicas"))), is("0"));
        Assert.assertThat(((String) (map.get("index.number_of_shards"))), is("1"));
    }

    @Test
    public void shouldTestResultsAcrossMultipleIndices() {
        // given
        String documentId1 = randomNumeric(5);
        SampleEntity sampleEntity1 = builder().id(documentId1).message("some message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery1 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-1").withObject(sampleEntity1).build();
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = builder().id(documentId2).message("some test message").version(System.currentTimeMillis()).build();
        IndexQuery indexQuery2 = new IndexQueryBuilder().withId(getId()).withIndexName("test-index-2").withObject(sampleEntity2).build();
        elasticsearchTemplate.bulkIndex(Arrays.asList(indexQuery1, indexQuery2));
        elasticsearchTemplate.refresh("test-index-1");
        elasticsearchTemplate.refresh("test-index-2");
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withIndices("test-index-1", "test-index-2").build();
        // when
        List<SampleEntity> sampleEntities = elasticsearchTemplate.queryForList(searchQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntities.size(), is(equalTo(2)));
    }

    /**
     * This is basically a demonstration to show composing entities out of heterogeneous indexes.
     */
    @Test
    public void shouldComposeObjectsReturnedFromHeterogeneousIndexes() {
        // Given
        HetroEntity1 entity1 = new HetroEntity1(randomNumeric(3), "aFirstName");
        HetroEntity2 entity2 = new HetroEntity2(randomNumeric(4), "aLastName");
        IndexQuery idxQuery1 = new IndexQueryBuilder().withIndexName(ElasticsearchTemplateTests.INDEX_1_NAME).withId(entity1.getId()).withObject(entity1).build();
        IndexQuery idxQuery2 = new IndexQueryBuilder().withIndexName(ElasticsearchTemplateTests.INDEX_2_NAME).withId(entity2.getId()).withObject(entity2).build();
        elasticsearchTemplate.bulkIndex(Arrays.asList(idxQuery1, idxQuery2));
        elasticsearchTemplate.refresh(ElasticsearchTemplateTests.INDEX_1_NAME);
        elasticsearchTemplate.refresh(ElasticsearchTemplateTests.INDEX_2_NAME);
        // When
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withTypes("hetro").withIndices(ElasticsearchTemplateTests.INDEX_1_NAME, ElasticsearchTemplateTests.INDEX_2_NAME).build();
        Page<ElasticsearchTemplateTests.ResultAggregator> page = elasticsearchTemplate.queryForPage(searchQuery, ElasticsearchTemplateTests.ResultAggregator.class, new SearchResultMapperAdapter() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<ElasticsearchTemplateTests.ResultAggregator> values = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    String id = String.valueOf(searchHit.getSourceAsMap().get("id"));
                    String firstName = (StringUtils.isNotEmpty(((String) (searchHit.getSourceAsMap().get("firstName"))))) ? ((String) (searchHit.getSourceAsMap().get("firstName"))) : "";
                    String lastName = (StringUtils.isNotEmpty(((String) (searchHit.getSourceAsMap().get("lastName"))))) ? ((String) (searchHit.getSourceAsMap().get("lastName"))) : "";
                    values.add(new ElasticsearchTemplateTests.ResultAggregator(id, firstName, lastName));
                }
                return new org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl(((List<T>) (values)));
            }
        });
        Assert.assertThat(page.getTotalElements(), is(2L));
    }

    @Test
    public void shouldCreateIndexUsingServerDefaultConfiguration() {
        // given
        // when
        boolean created = elasticsearchTemplate.createIndex(UseServerConfigurationEntity.class);
        // then
        Assert.assertThat(created, is(true));
        final Map setting = elasticsearchTemplate.getSetting(UseServerConfigurationEntity.class);
        Assert.assertThat(setting.get("index.number_of_shards"), Matchers.Matchers.<Object>is("5"));
        Assert.assertThat(setting.get("index.number_of_replicas"), Matchers.Matchers.<Object>is("1"));
    }

    @Test
    public void shouldReadFileFromClasspathRetainingNewlines() {
        // given
        String settingsFile = "/settings/test-settings.yml";
        // when
        String content = ElasticsearchTemplate.readFileFromClasspath(settingsFile);
        // then
        Assert.assertThat(content, is(("index:\n" + (((((("  number_of_shards: 1\n" + "  number_of_replicas: 0\n") + "  analysis:\n") + "    analyzer:\n") + "      emailAnalyzer:\n") + "        type: custom\n") + "        tokenizer: uax_url_email\n"))));
    }

    // DATAES-531
    @Test
    public void shouldReturnMappingForGivenEntityClass() {
        // when
        boolean created = elasticsearchTemplate.createIndex(SampleEntity.class);
        elasticsearchTemplate.putMapping(SampleEntity.class);
        final Map mapping = elasticsearchTemplate.getMapping(SampleEntity.class);
        // then
        Assert.assertThat(created, is(true));
        Assert.assertThat(mapping, notNullValue());
        Assert.assertThat(((Map) (((Map) (mapping.get("properties"))).get("message"))).get("type"), Matchers.Matchers.<Object>is("text"));
    }

    // DATAES-525
    @Test
    public void shouldDeleteOnlyDocumentsMatchedByDeleteQuery() {
        List<IndexQuery> indexQueries = new ArrayList<>();
        // given
        // document to be deleted
        String documentIdToDelete = UUID.randomUUID().toString();
        indexQueries.add(getIndexQuery(builder().id(documentIdToDelete).message("some message").version(System.currentTimeMillis()).build()));
        // remaining document
        String remainingDocumentId = UUID.randomUUID().toString();
        indexQueries.add(getIndexQuery(builder().id(remainingDocumentId).message("some other message").version(System.currentTimeMillis()).build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(idsQuery().addIds(documentIdToDelete));
        elasticsearchTemplate.delete(deleteQuery, SampleEntity.class);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        // document with id "remainingDocumentId" should still be indexed
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(1L));
        Assert.assertThat(getId(), is(remainingDocumentId));
    }

    // DATAES-525
    @Test
    public void shouldDeleteOnlyDocumentsMatchedByCriteriaQuery() {
        List<IndexQuery> indexQueries = new ArrayList<>();
        // given
        // document to be deleted
        String documentIdToDelete = UUID.randomUUID().toString();
        indexQueries.add(getIndexQuery(builder().id(documentIdToDelete).message("some message").version(System.currentTimeMillis()).build()));
        // remaining document
        String remainingDocumentId = UUID.randomUUID().toString();
        indexQueries.add(getIndexQuery(builder().id(remainingDocumentId).message("some other message").version(System.currentTimeMillis()).build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("id").is(documentIdToDelete));
        elasticsearchTemplate.delete(criteriaQuery, SampleEntity.class);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        // document with id "remainingDocumentId" should still be indexed
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(1L));
        Assert.assertThat(getId(), is(remainingDocumentId));
    }

    // DATAES-525
    @Test
    public void shouldDeleteDocumentForGivenIdOnly() {
        List<IndexQuery> indexQueries = new ArrayList<>();
        // given
        // document to be deleted
        String documentIdToDelete = UUID.randomUUID().toString();
        indexQueries.add(getIndexQuery(builder().id(documentIdToDelete).message("some message").version(System.currentTimeMillis()).build()));
        // remaining document
        String remainingDocumentId = UUID.randomUUID().toString();
        indexQueries.add(getIndexQuery(builder().id(remainingDocumentId).message("some other message").version(System.currentTimeMillis()).build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        elasticsearchTemplate.delete(SampleEntity.class, documentIdToDelete);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // then
        // document with id "remainingDocumentId" should still be indexed
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(1L));
        Assert.assertThat(getId(), is(remainingDocumentId));
    }

    // DATAES-525
    @Test
    public void shouldApplyCriteriaQueryToScanAndScrollForGivenCriteriaQuery() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        indexQueries.add(getIndexQuery(builder().id(UUID.randomUUID().toString()).message("some message that should be found by the scroll query").version(System.currentTimeMillis()).build()));
        indexQueries.add(getIndexQuery(builder().id(UUID.randomUUID().toString()).message("some other message that should be found by the scroll query").version(System.currentTimeMillis()).build()));
        String notFindableMessage = "this entity must not be found by the scroll query";
        indexQueries.add(getIndexQuery(builder().id(UUID.randomUUID().toString()).message(notFindableMessage).version(System.currentTimeMillis()).build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").contains("message"));
        criteriaQuery.addIndices(ElasticsearchTemplateTests.INDEX_NAME);
        criteriaQuery.addTypes(ElasticsearchTemplateTests.TYPE_NAME);
        criteriaQuery.setPageable(PageRequest.of(0, 10));
        ScrolledPage<SampleEntity> scroll = ((ScrolledPage<SampleEntity>) (elasticsearchTemplate.startScroll(1000, criteriaQuery, SampleEntity.class)));
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scroll = ((ScrolledPage<SampleEntity>) (elasticsearchTemplate.continueScroll(scroll.getScrollId(), 1000, SampleEntity.class)));
        } 
        elasticsearchTemplate.clearScroll(scroll.getScrollId());
        // then
        Assert.assertThat(sampleEntities.size(), is(equalTo(2)));
        Assert.assertThat(sampleEntities.stream().map(SampleEntity::getMessage).collect(Collectors.toList()), not(contains(notFindableMessage)));
    }

    // DATAES-525
    @Test
    public void shouldApplySearchQueryToScanAndScrollForGivenSearchQuery() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        indexQueries.add(getIndexQuery(builder().id(UUID.randomUUID().toString()).message("some message that should be found by the scroll query").version(System.currentTimeMillis()).build()));
        indexQueries.add(getIndexQuery(builder().id(UUID.randomUUID().toString()).message("some other message that should be found by the scroll query").version(System.currentTimeMillis()).build()));
        String notFindableMessage = "this entity must not be found by the scroll query";
        indexQueries.add(getIndexQuery(builder().id(UUID.randomUUID().toString()).message(notFindableMessage).version(System.currentTimeMillis()).build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("message", "message")).withIndices(ElasticsearchTemplateTests.INDEX_NAME).withTypes(ElasticsearchTemplateTests.TYPE_NAME).withPageable(PageRequest.of(0, 10)).build();
        ScrolledPage<SampleEntity> scroll = ((ScrolledPage<SampleEntity>) (elasticsearchTemplate.startScroll(1000, searchQuery, SampleEntity.class)));
        List<SampleEntity> sampleEntities = new ArrayList<>();
        while (scroll.hasContent()) {
            sampleEntities.addAll(scroll.getContent());
            scroll = ((ScrolledPage<SampleEntity>) (elasticsearchTemplate.continueScroll(scroll.getScrollId(), 1000, SampleEntity.class)));
        } 
        elasticsearchTemplate.clearScroll(scroll.getScrollId());
        // then
        Assert.assertThat(sampleEntities.size(), is(equalTo(2)));
        Assert.assertThat(sampleEntities.stream().map(SampleEntity::getMessage).collect(Collectors.toList()), not(contains(notFindableMessage)));
    }

    @Document(indexName = ElasticsearchTemplateTests.INDEX_2_NAME, replicas = 0, shards = 1)
    class ResultAggregator {
        private String id;

        private String firstName;

        private String lastName;

        ResultAggregator(String id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}

