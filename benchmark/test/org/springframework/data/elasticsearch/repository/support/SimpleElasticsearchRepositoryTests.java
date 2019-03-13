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
package org.springframework.data.elasticsearch.repository.support;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.common.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.entities.SampleEntity;
import org.springframework.data.elasticsearch.repositories.sample.SampleElasticsearchRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Mark Paluch
 * @author Christoph Strobl
 * @author Michael Wirth
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/simple-repository-test.xml")
public class SimpleElasticsearchRepositoryTests {
    @Autowired
    private SampleElasticsearchRepository repository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldDoBulkIndexDocument() {
        // given
        String documentId1 = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId1);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        // when
        repository.saveAll(Arrays.asList(sampleEntity1, sampleEntity2));
        // then
        Optional<SampleEntity> entity1FromElasticSearch = findById(documentId1);
        Assert.assertThat(entity1FromElasticSearch.isPresent(), is(true));
        Optional<SampleEntity> entity2FromElasticSearch = findById(documentId2);
        Assert.assertThat(entity2FromElasticSearch.isPresent(), is(true));
    }

    @Test
    public void shouldSaveDocument() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        // when
        save(sampleEntity);
        // then
        Optional<SampleEntity> entityFromElasticSearch = findById(documentId);
        Assert.assertThat(entityFromElasticSearch.isPresent(), is(true));
    }

    @Test(expected = ActionRequestValidationException.class)
    public void throwExceptionWhenTryingToInsertWithVersionButWithoutId() {
        // given
        SampleEntity sampleEntity = new SampleEntity();
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        // when
        save(sampleEntity);
        // then
        Assert.assertThat(getId(), is(notNullValue()));
    }

    @Test
    public void shouldFindDocumentById() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        // when
        Optional<SampleEntity> entityFromElasticSearch = findById(documentId);
        // then
        Assert.assertThat(entityFromElasticSearch.isPresent(), is(true));
        Assert.assertThat(sampleEntity, is(equalTo(sampleEntity)));
    }

    @Test
    public void shouldReturnCountOfDocuments() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        // when
        Long count = count();
        // then
        Assert.assertThat(count, is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldFindAllDocuments() {
        // when
        Iterable<SampleEntity> results = findAll();
        // then
        Assert.assertThat(results, is(notNullValue()));
    }

    @Test
    public void shouldDeleteDocument() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        // when
        deleteById(documentId);
        // then
        Optional<SampleEntity> entityFromElasticSearch = findById(documentId);
        Assert.assertThat(entityFromElasticSearch.isPresent(), is(false));
    }

    @Test
    public void shouldSearchDocumentsGivenSearchQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("some test message");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(termQuery("message", "test")).build();
        // when
        Page<SampleEntity> page = repository.search(query);
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getNumberOfElements(), is(greaterThanOrEqualTo(1)));
    }

    @Test
    public void shouldSearchDocumentsGivenElasticsearchQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.search(termQuery("message", "world"), new PageRequest(0, 50));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getNumberOfElements(), is(greaterThanOrEqualTo(1)));
    }

    /* DATAES-82 */
    @Test
    public void shouldFindAllByIdQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity2);
        // when
        Iterable<SampleEntity> sampleEntities = findAllById(Arrays.asList(documentId, documentId2));
        // then
        Assert.assertNotNull("sample entities cant be null..", sampleEntities);
        List<SampleEntity> entities = CollectionUtils.iterableAsArrayList(sampleEntities);
        Assert.assertThat(entities.size(), is(2));
    }

    @Test
    public void shouldSaveIterableEntities() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        Iterable<SampleEntity> sampleEntities = Arrays.asList(sampleEntity1, sampleEntity2);
        // when
        repository.saveAll(sampleEntities);
        // then
        Page<SampleEntity> entities = repository.search(termQuery("id", documentId), new PageRequest(0, 50));
        Assert.assertNotNull(entities);
    }

    @Test
    public void shouldReturnTrueGivenDocumentWithIdExists() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        // when
        boolean exist = existsById(documentId);
        // then
        Assert.assertEquals(exist, true);
    }

    // DATAES-363
    @Test
    public void shouldReturnFalseGivenDocumentWithIdDoesNotExist() {
        // given
        String documentId = randomNumeric(5);
        // when
        boolean exist = existsById(documentId);
        // then
        Assert.assertThat(exist, is(false));
    }

    @Test
    public void shouldReturnResultsForGivenSearchQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        // when
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", documentId)).build();
        Page<SampleEntity> sampleEntities = repository.search(searchQuery);
        // then
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(1L));
    }

    @Test
    public void shouldDeleteAll() {
        // when
        repository.deleteAll();
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntity> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
    }

    @Test
    public void shouldDeleteById() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        // when
        long result = repository.deleteSampleEntityById(documentId);
        refresh();
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", documentId)).build();
        Page<SampleEntity> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
        Assert.assertThat(result, equalTo(1L));
    }

    @Test
    public void shouldDeleteByMessageAndReturnList() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("hello world 1");
        setAvailable(true);
        setVersion(System.currentTimeMillis());
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setMessage("hello world 2");
        setAvailable(true);
        setVersion(System.currentTimeMillis());
        documentId = randomNumeric(5);
        SampleEntity sampleEntity3 = new SampleEntity();
        setId(documentId);
        setMessage("hello world 3");
        setAvailable(false);
        setVersion(System.currentTimeMillis());
        repository.saveAll(Arrays.asList(sampleEntity1, sampleEntity2, sampleEntity3));
        // when
        List<SampleEntity> result = repository.deleteByAvailable(true);
        refresh();
        // then
        Assert.assertThat(result.size(), equalTo(2));
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntity> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(1L));
    }

    @Test
    public void shouldDeleteByListForMessage() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("hello world 1");
        setVersion(System.currentTimeMillis());
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setMessage("hello world 2");
        setVersion(System.currentTimeMillis());
        documentId = randomNumeric(5);
        SampleEntity sampleEntity3 = new SampleEntity();
        setId(documentId);
        setMessage("hello world 3");
        setVersion(System.currentTimeMillis());
        repository.saveAll(Arrays.asList(sampleEntity1, sampleEntity2, sampleEntity3));
        // when
        List<SampleEntity> result = repository.deleteByMessage("hello world 3");
        refresh();
        // then
        Assert.assertThat(result.size(), equalTo(1));
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntity> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(2L));
    }

    @Test
    public void shouldDeleteByType() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setType("book");
        setVersion(System.currentTimeMillis());
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("article");
        setVersion(System.currentTimeMillis());
        documentId = randomNumeric(5);
        SampleEntity sampleEntity3 = new SampleEntity();
        setId(documentId);
        setType("image");
        setVersion(System.currentTimeMillis());
        repository.saveAll(Arrays.asList(sampleEntity1, sampleEntity2, sampleEntity3));
        // when
        repository.deleteByType("article");
        refresh();
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntity> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(2L));
    }

    @Test
    public void shouldDeleteEntity() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        // when
        delete(sampleEntity);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", documentId)).build();
        Page<SampleEntity> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
    }

    @Test
    public void shouldReturnIterableEntities() {
        // given
        String documentId1 = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId1);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity1);
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity2);
        // when
        Iterable<SampleEntity> sampleEntities = repository.search(termQuery("id", documentId1));
        // then
        Assert.assertNotNull("sample entities cant be null..", sampleEntities);
    }

    @Test
    public void shouldDeleteIterableEntities() {
        // given
        String documentId1 = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId1);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntity2);
        Iterable<SampleEntity> sampleEntities = Arrays.asList(sampleEntity2, sampleEntity2);
        // when
        deleteAll(sampleEntities);
        // then
        Assert.assertThat(repository.findById(documentId1).isPresent(), is(false));
        Assert.assertThat(repository.findById(documentId2).isPresent(), is(false));
    }

    @Test
    public void shouldIndexEntity() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setVersion(System.currentTimeMillis());
        setMessage("some message");
        // when
        index(sampleEntity);
        // then
        Page<SampleEntity> entities = repository.search(termQuery("id", documentId), new PageRequest(0, 50));
        Assert.assertThat(entities.getTotalElements(), equalTo(1L));
    }

    @Test
    public void shouldSortByGivenField() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("world");
        save(sampleEntity);
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("hello");
        save(sampleEntity2);
        // when
        Iterable<SampleEntity> sampleEntities = repository.findAll(new Sort(new org.springframework.data.domain.Sort.Order(ASC, "message")));
        // then
        Assert.assertThat(sampleEntities, is(notNullValue()));
    }

    @Test
    public void shouldReturnSimilarEntities() {
        // given
        String sampleMessage = "So we build a web site or an application and want to add search to it, " + ((("and then it hits us: getting search working is hard. We want our search solution to be fast," + " we want a painless setup and a completely free search schema, we want to be able to index data simply using JSON over HTTP, ") + "we want our search server to be always available, we want to be able to start with one machine and scale to hundreds, ") + "we want real-time search, we want simple multi-tenancy, and we want a solution that is built for the cloud.");
        List<SampleEntity> sampleEntities = SimpleElasticsearchRepositoryTests.createSampleEntitiesWithMessage(sampleMessage, 30);
        saveAll(sampleEntities);
        // when
        Page<SampleEntity> results = repository.searchSimilar(sampleEntities.get(0), new String[]{ "message" }, new PageRequest(0, 5));
        // then
        Assert.assertThat(results.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }
}

