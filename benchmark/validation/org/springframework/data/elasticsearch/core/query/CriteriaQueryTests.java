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
package org.springframework.data.elasticsearch.core.query;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.entities.SampleEntity;
import org.springframework.data.elasticsearch.utils.IndexBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:elasticsearch-template-test.xml")
public class CriteriaQueryTests {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldPerformAndOperation() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("some test message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(documentId);
        indexQuery.setObject(sampleEntity);
        elasticsearchTemplate.index(indexQuery);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").contains("test").and("message").contains("some"));
        // when
        SampleEntity sampleEntity1 = elasticsearchTemplate.queryForObject(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntity1, is(notNullValue()));
    }

    @Test
    public void shouldPerformAndOperationWithinCriteria() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(documentId);
        indexQuery.setObject(sampleEntity);
        indexQueries.add(indexQuery);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria().and(new Criteria("message").contains("some")));
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldPerformIsOperation() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(documentId);
        indexQuery.setObject(sampleEntity);
        indexQueries.add(indexQuery);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").is("some message"));
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat("message", is(criteriaQuery.getCriteria().getField().getName()));
        Assert.assertThat(page.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldPerformMultipleIsOperations() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("test message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").is("some message"));
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat("message", is(criteriaQuery.getCriteria().getField().getName()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldPerformEndsWithOperation() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("test message end");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        Criteria criteria = new Criteria("message").endsWith("end");
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        // when
        SampleEntity sampleEntity = elasticsearchTemplate.queryForObject(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat("message", is(criteriaQuery.getCriteria().getField().getName()));
        Assert.assertThat(sampleEntity, is(notNullValue()));
    }

    @Test
    public void shouldPerformStartsWithOperation() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("start some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("test message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        Criteria criteria = new Criteria("message").startsWith("start");
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        // when
        SampleEntity sampleEntity = elasticsearchTemplate.queryForObject(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat("message", is(criteriaQuery.getCriteria().getField().getName()));
        Assert.assertThat(sampleEntity, is(notNullValue()));
    }

    @Test
    public void shouldPerformContainsOperation() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("contains some message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("test message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").contains("contains"));
        // when
        SampleEntity sampleEntity = elasticsearchTemplate.queryForObject(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat("message", is(criteriaQuery.getCriteria().getField().getName()));
        Assert.assertThat(sampleEntity, is(notNullValue()));
    }

    @Test
    public void shouldExecuteExpression() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("elasticsearch search");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("test message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").expression("+elasticsearch || test"));
        // when
        SampleEntity sampleEntity = elasticsearchTemplate.queryForObject(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat("message", is(criteriaQuery.getCriteria().getField().getName()));
        Assert.assertThat(sampleEntity, is(notNullValue()));
    }

    @Test
    public void shouldExecuteCriteriaChain() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("some message search");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("test test message");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(is("some message search"));
        // when
        SampleEntity sampleEntity = elasticsearchTemplate.queryForObject(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat("message", is(criteriaQuery.getCriteria().getField().getName()));
        Assert.assertThat(sampleEntity, is(notNullValue()));
    }

    @Test
    public void shouldPerformIsNotOperation() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setMessage("bar");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("foo");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").is("foo").not());
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertTrue(criteriaQuery.getCriteria().isNegating());
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertFalse(page.iterator().next().getMessage().contains("foo"));
    }

    @Test
    public void shouldPerformBetweenOperation() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setRate(100);
        setMessage("bar");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setRate(200);
        setMessage("foo");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("rate").between(100, 150));
        // when
        SampleEntity sampleEntity = elasticsearchTemplate.queryForObject(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(sampleEntity, is(notNullValue()));
    }

    @Test
    public void shouldPerformBetweenOperationWithoutUpperBound() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setRate(300);
        setMessage("bar");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setRate(400);
        setMessage("foo");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("rate").between(350, null));
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldPerformBetweenOperationWithoutLowerBound() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setRate(500);
        setMessage("bar");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setRate(600);
        setMessage("foo");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("rate").between(null, 550));
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldPerformLessThanEqualOperation() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setRate(700);
        setMessage("bar");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setRate(800);
        setMessage("foo");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("rate").lessThanEqual(750));
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldPerformGreaterThanEquals() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setRate(900);
        setMessage("bar");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setRate(1000);
        setMessage("foo");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("rate").greaterThanEqual(950));
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldPerformBoostOperation() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        // first document
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity1 = new SampleEntity();
        setId(documentId);
        setRate(700);
        setMessage("bar foo");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(documentId);
        indexQuery1.setObject(sampleEntity1);
        indexQueries.add(indexQuery1);
        // second document
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setRate(800);
        setMessage("foo");
        setVersion(System.currentTimeMillis());
        IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(documentId2);
        indexQuery2.setObject(sampleEntity2);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").contains("foo").boost(1));
        // when
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(page.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldReturnDocumentAboveMinimalScoreGivenCriteria() {
        // given
        List<IndexQuery> indexQueries = new ArrayList<>();
        indexQueries.add(IndexBuilder.buildIndex(builder().id("1").message("ab").build()));
        indexQueries.add(IndexBuilder.buildIndex(builder().id("2").message("bc").build()));
        indexQueries.add(IndexBuilder.buildIndex(builder().id("3").message("ac").build()));
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(SampleEntity.class);
        // when
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("message").contains("a").or(new Criteria("message").contains("b")));
        criteriaQuery.setMinScore(2.0F);
        Page<SampleEntity> page = elasticsearchTemplate.queryForPage(criteriaQuery, SampleEntity.class);
        // then
        Assert.assertThat(page.getTotalElements(), is(1L));
        Assert.assertThat(page.getContent().get(0).getMessage(), is("ab"));
    }
}

