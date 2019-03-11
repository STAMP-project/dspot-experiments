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


import Sort.Direction;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import org.springframework.data.elasticsearch.entities.SampleEntityUUIDKeyed;
import org.springframework.data.elasticsearch.repositories.sample.SampleUUIDKeyedElasticsearchRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Gad Akuka
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Mark Paluch
 * @author Christoph Strobl
 * @author Michael Wirth
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/simple-repository-test.xml")
public class UUIDElasticsearchRepositoryTests {
    @Autowired
    private SampleUUIDKeyedElasticsearchRepository repository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldDoBulkIndexDocument() {
        // given
        UUID documentId1 = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed1 = new SampleEntityUUIDKeyed();
        setId(documentId1);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        UUID documentId2 = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed2 = new SampleEntityUUIDKeyed();
        setId(documentId2);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        // when
        repository.saveAll(Arrays.asList(sampleEntityUUIDKeyed1, sampleEntityUUIDKeyed2));
        // then
        Optional<SampleEntityUUIDKeyed> entity1FromElasticSearch = findById(documentId1);
        Assert.assertThat(entity1FromElasticSearch.isPresent(), is(true));
        Optional<SampleEntityUUIDKeyed> entity2FromElasticSearch = findById(documentId2);
        Assert.assertThat(entity2FromElasticSearch.isPresent(), is(true));
    }

    @Test
    public void shouldSaveDocument() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        // when
        save(sampleEntityUUIDKeyed);
        // then
        Optional<SampleEntityUUIDKeyed> entityFromElasticSearch = findById(documentId);
        Assert.assertThat(entityFromElasticSearch.isPresent(), is(true));
    }

    @Test
    public void shouldFindDocumentById() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed);
        // when
        Optional<SampleEntityUUIDKeyed> entityFromElasticSearch = findById(documentId);
        // then
        Assert.assertThat(entityFromElasticSearch.isPresent(), is(true));
        Assert.assertThat(sampleEntityUUIDKeyed, is(equalTo(sampleEntityUUIDKeyed)));
    }

    @Test
    public void shouldReturnCountOfDocuments() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed);
        // when
        Long count = count();
        // then
        Assert.assertThat(count, is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldFindAllDocuments() {
        // when
        Iterable<SampleEntityUUIDKeyed> results = findAll();
        // then
        Assert.assertThat(results, is(notNullValue()));
    }

    @Test
    public void shouldDeleteDocument() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed);
        // when
        deleteById(documentId);
        // then
        Optional<SampleEntityUUIDKeyed> entityFromElasticSearch = findById(documentId);
        Assert.assertThat(entityFromElasticSearch.isPresent(), is(false));
    }

    @Test
    public void shouldSearchDocumentsGivenSearchQuery() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("some test message");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed);
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(termQuery("message", "test")).build();
        // when
        Page<SampleEntityUUIDKeyed> page = repository.search(query);
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getNumberOfElements(), is(greaterThanOrEqualTo(1)));
    }

    @Test
    public void shouldSearchDocumentsGivenElasticsearchQuery() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed);
        // when
        Page<SampleEntityUUIDKeyed> page = repository.search(termQuery("message", "world"), new PageRequest(0, 50));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getNumberOfElements(), is(greaterThanOrEqualTo(1)));
    }

    /* DATAES-82 */
    @Test
    public void shouldFindAllByIdQuery() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed);
        UUID documentId2 = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed2 = new SampleEntityUUIDKeyed();
        setId(documentId2);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed2);
        // when
        LinkedList<SampleEntityUUIDKeyed> sampleEntities = ((LinkedList<SampleEntityUUIDKeyed>) (findAllById(Arrays.asList(documentId, documentId2))));
        // then
        Assert.assertNotNull("sample entities cant be null..", sampleEntities);
        Assert.assertThat(sampleEntities.size(), is(2));
        Assert.assertThat(getId(), isIn(Arrays.asList(documentId, documentId2)));
        Assert.assertThat(getId(), isIn(Arrays.asList(documentId, documentId2)));
    }

    @Test
    public void shouldSaveIterableEntities() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed1 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        UUID documentId2 = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed2 = new SampleEntityUUIDKeyed();
        setId(documentId2);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        Iterable<SampleEntityUUIDKeyed> sampleEntities = Arrays.asList(sampleEntityUUIDKeyed1, sampleEntityUUIDKeyed2);
        // when
        repository.saveAll(sampleEntities);
        // then
        Page<SampleEntityUUIDKeyed> entities = repository.search(termQuery("id", documentId.toString()), new PageRequest(0, 50));
        Assert.assertNotNull(entities);
    }

    @Test
    public void shouldReturnTrueGivenDocumentWithIdExists() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed);
        // when
        boolean exist = existsById(documentId);
        // then
        Assert.assertEquals(exist, true);
    }

    // DATAES-363
    @Test
    public void shouldReturnFalseGivenDocumentWithIdDoesNotExist() {
        // given
        UUID documentId = UUID.randomUUID();
        // when
        boolean exist = existsById(documentId);
        // then
        Assert.assertThat(exist, is(false));
    }

    @Test
    public void shouldDeleteAll() {
        // when
        repository.deleteAll();
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntityUUIDKeyed> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
    }

    @Test
    public void shouldDeleteById() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed);
        // when
        long result = repository.deleteSampleEntityUUIDKeyedById(documentId);
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", documentId.toString())).build();
        Page<SampleEntityUUIDKeyed> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
        Assert.assertThat(result, equalTo(1L));
    }

    @Test
    public void shouldDeleteByMessageAndReturnList() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed1 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world 1");
        setAvailable(true);
        setVersion(System.currentTimeMillis());
        documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed2 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world 2");
        setAvailable(true);
        setVersion(System.currentTimeMillis());
        documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed3 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world 3");
        setAvailable(false);
        setVersion(System.currentTimeMillis());
        repository.saveAll(Arrays.asList(sampleEntityUUIDKeyed1, sampleEntityUUIDKeyed2, sampleEntityUUIDKeyed3));
        // when
        List<SampleEntityUUIDKeyed> result = repository.deleteByAvailable(true);
        refresh();
        // then
        Assert.assertThat(result.size(), equalTo(2));
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntityUUIDKeyed> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(1L));
    }

    @Test
    public void shouldDeleteByListForMessage() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed1 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world 1");
        setVersion(System.currentTimeMillis());
        documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed2 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world 2");
        setVersion(System.currentTimeMillis());
        documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed3 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world 3");
        setVersion(System.currentTimeMillis());
        repository.saveAll(Arrays.asList(sampleEntityUUIDKeyed1, sampleEntityUUIDKeyed2, sampleEntityUUIDKeyed3));
        // when
        List<SampleEntityUUIDKeyed> result = repository.deleteByMessage("hello world 3");
        refresh();
        // then
        Assert.assertThat(result.size(), equalTo(1));
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntityUUIDKeyed> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(2L));
    }

    @Test
    public void shouldDeleteByType() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed1 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setType("book");
        setVersion(System.currentTimeMillis());
        documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed2 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setType("article");
        setVersion(System.currentTimeMillis());
        documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed3 = new SampleEntityUUIDKeyed();
        setId(documentId);
        setType("image");
        setVersion(System.currentTimeMillis());
        repository.saveAll(Arrays.asList(sampleEntityUUIDKeyed1, sampleEntityUUIDKeyed2, sampleEntityUUIDKeyed3));
        // when
        repository.deleteByType("article");
        refresh();
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        Page<SampleEntityUUIDKeyed> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(2L));
    }

    @Test
    public void shouldDeleteEntity() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed);
        // when
        delete(sampleEntityUUIDKeyed);
        refresh();
        // then
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(termQuery("id", documentId.toString())).build();
        Page<SampleEntityUUIDKeyed> sampleEntities = repository.search(searchQuery);
        Assert.assertThat(sampleEntities.getTotalElements(), equalTo(0L));
    }

    @Test
    public void shouldReturnIterableEntities() {
        // given
        UUID documentId1 = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed1 = new SampleEntityUUIDKeyed();
        setId(documentId1);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed1);
        UUID documentId2 = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed2 = new SampleEntityUUIDKeyed();
        setId(documentId2);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed2);
        // when
        Iterable<SampleEntityUUIDKeyed> sampleEntities = repository.search(termQuery("id", documentId1.toString()));
        // then
        Assert.assertNotNull("sample entities cant be null..", sampleEntities);
    }

    @Test
    public void shouldDeleteIterableEntities() {
        // given
        UUID documentId1 = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed1 = new SampleEntityUUIDKeyed();
        setId(documentId1);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        UUID documentId2 = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed2 = new SampleEntityUUIDKeyed();
        setId(documentId2);
        setMessage("hello world.");
        setVersion(System.currentTimeMillis());
        save(sampleEntityUUIDKeyed2);
        Iterable<SampleEntityUUIDKeyed> sampleEntities = Arrays.asList(sampleEntityUUIDKeyed2, sampleEntityUUIDKeyed2);
        // when
        deleteAll(sampleEntities);
        // then
        Assert.assertThat(repository.findById(documentId1).isPresent(), is(false));
        Assert.assertThat(repository.findById(documentId2).isPresent(), is(false));
    }

    @Test
    public void shouldSortByGivenField() {
        // given
        UUID documentId = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed = new SampleEntityUUIDKeyed();
        setId(documentId);
        setMessage("world");
        save(sampleEntityUUIDKeyed);
        UUID documentId2 = UUID.randomUUID();
        SampleEntityUUIDKeyed sampleEntityUUIDKeyed2 = new SampleEntityUUIDKeyed();
        setId(documentId2);
        setMessage("hello");
        save(sampleEntityUUIDKeyed2);
        // when
        Iterable<SampleEntityUUIDKeyed> sampleEntities = repository.findAll(new Sort(new Sort.Order(Direction.ASC, "message")));
        // then
        Assert.assertThat(sampleEntities, is(notNullValue()));
    }

    @Test
    public void shouldReturnSimilarEntities() {
        // given
        String sampleMessage = "So we build a web site or an application and want to add search to it, " + ((("and then it hits us: getting search working is hard. We want our search solution to be fast," + " we want a painless setup and a completely free search schema, we want to be able to index data simply using JSON over HTTP, ") + "we want our search server to be always available, we want to be able to start with one machine and scale to hundreds, ") + "we want real-time search, we want simple multi-tenancy, and we want a solution that is built for the cloud.");
        List<SampleEntityUUIDKeyed> sampleEntities = UUIDElasticsearchRepositoryTests.createSampleEntitiesWithMessage(sampleMessage, 30);
        saveAll(sampleEntities);
        // when
        Page<SampleEntityUUIDKeyed> results = repository.searchSimilar(sampleEntities.get(0), new String[]{ "message" }, new PageRequest(0, 5));
        // then
        Assert.assertThat(results.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }
}

