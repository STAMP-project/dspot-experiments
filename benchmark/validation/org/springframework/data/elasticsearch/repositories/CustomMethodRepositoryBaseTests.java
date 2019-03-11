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
package org.springframework.data.elasticsearch.repositories;


import Sort.Direction;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.entities.SampleEntity;
import org.springframework.data.elasticsearch.repositories.custom.SampleCustomMethodRepository;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;


/**
 *
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Franck Marchand
 * @author Kevin Leturc
 * @author Christoph Strobl
 * @author Don Wellington
 */
public abstract class CustomMethodRepositoryBaseTests {
    @Autowired
    private SampleCustomMethodRepository repository;

    @Test
    public void shouldExecuteCustomMethod() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("some message");
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByType("test", new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodForNot() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("some");
        setMessage("some message");
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByTypeNot("test", new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithQuery() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        String searchTerm = "customQuery";
        setMessage(searchTerm);
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByMessage(searchTerm.toLowerCase(), new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(greaterThanOrEqualTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithLessThan() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(9);
        setMessage("some message");
        save(sampleEntity);
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setRate(20);
        setMessage("some message");
        save(sampleEntity2);
        // when
        Page<SampleEntity> page = repository.findByRateLessThan(10, new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithBefore() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("some message");
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByRateBefore(10, new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithAfter() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("some message");
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByRateAfter(10, new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithLike() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByMessageLike("fo", new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodForStartingWith() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByMessageStartingWith("fo", new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodForEndingWith() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByMessageEndingWith("o", new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodForContains() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByMessageContaining("fo", new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodForIn() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        save(sampleEntity2);
        List<String> ids = Arrays.asList(documentId, documentId2);
        // when
        Page<SampleEntity> page = repository.findByIdIn(ids, new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(2L)));
    }

    @Test
    public void shouldExecuteCustomMethodForNotIn() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        save(sampleEntity2);
        List<String> ids = Arrays.asList(documentId);
        // when
        Page<SampleEntity> page = repository.findByIdNotIn(ids, new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
        Assert.assertThat(page.getContent().get(0).getId(), is(documentId2));
    }

    @Test
    public void shouldExecuteCustomMethodForTrue() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        setAvailable(true);
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        setAvailable(false);
        save(sampleEntity2);
        // when
        Page<SampleEntity> page = repository.findByAvailableTrue(new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodForFalse() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        setAvailable(true);
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        setAvailable(false);
        save(sampleEntity2);
        // when
        Page<SampleEntity> page = repository.findByAvailableFalse(new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodForOrderBy() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("abc");
        setMessage("test");
        setAvailable(true);
        save(sampleEntity);
        // document 2
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("xyz");
        setMessage("bar");
        setAvailable(false);
        save(sampleEntity2);
        // document 3
        String documentId3 = randomNumeric(5);
        SampleEntity sampleEntity3 = new SampleEntity();
        setId(documentId3);
        setType("def");
        setMessage("foo");
        setAvailable(false);
        save(sampleEntity3);
        // when
        Page<SampleEntity> page = repository.findByMessageOrderByTypeAsc("foo", new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithBooleanParameter() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        setAvailable(true);
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        setAvailable(false);
        save(sampleEntity2);
        // when
        Page<SampleEntity> page = repository.findByAvailable(false, new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldReturnPageableResultsWithQueryAnnotationExpectedPageSize() {
        // given
        for (int i = 0; i < 30; i++) {
            String documentId = String.valueOf(i);
            SampleEntity sampleEntity = new SampleEntity();
            setId(documentId);
            setMessage("message");
            setVersion(System.currentTimeMillis());
            save(sampleEntity);
        }
        // when
        Page<SampleEntity> pageResult = repository.findByMessage("message", new PageRequest(0, 23));
        // then
        Assert.assertThat(pageResult.getTotalElements(), is(equalTo(30L)));
        Assert.assertThat(pageResult.getContent().size(), is(equalTo(23)));
    }

    @Test
    public void shouldReturnPageableResultsWithGivenSortingOrder() {
        // given
        String documentId = random(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("abc");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("abd");
        setVersion(System.currentTimeMillis());
        save(sampleEntity2);
        String documentId3 = randomNumeric(5);
        SampleEntity sampleEntity3 = new SampleEntity();
        setId(documentId3);
        setMessage("abe");
        setVersion(System.currentTimeMillis());
        save(sampleEntity3);
        // when
        Page<SampleEntity> pageResult = repository.findByMessageContaining("a", new PageRequest(0, 23, new Sort(new Sort.Order(Direction.DESC, "message"))));
        // then
        Assert.assertThat(pageResult.getContent().isEmpty(), is(false));
        Assert.assertThat(getMessage(), is(getMessage()));
    }

    @Test
    public void shouldReturnListForMessage() {
        // given
        String documentId = random(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setMessage("abc");
        setVersion(System.currentTimeMillis());
        save(sampleEntity);
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setMessage("abd");
        setVersion(System.currentTimeMillis());
        save(sampleEntity2);
        String documentId3 = randomNumeric(5);
        SampleEntity sampleEntity3 = new SampleEntity();
        setId(documentId3);
        setMessage("abe");
        setVersion(System.currentTimeMillis());
        save(sampleEntity3);
        // when
        List<SampleEntity> sampleEntities = repository.findByMessage("abc");
        // then
        Assert.assertThat(sampleEntities.isEmpty(), is(false));
        Assert.assertThat(sampleEntities.size(), is(1));
    }

    @Test
    public void shouldExecuteCustomMethodWithGeoPoint() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByLocation(new GeoPoint(45.7806, 3.0875), new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithGeoPointAndString() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        documentId = randomNumeric(5);
        sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(48.7806, 3.0875));
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByLocationAndMessage(new GeoPoint(45.7806, 3.0875), "foo", new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithWithinGeoPoint() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByLocationWithin(new GeoPoint(45.7806, 3.0875), "2km", new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithWithinPoint() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByLocationWithin(new Point(45.7806, 3.0875), new org.springframework.data.geo.Distance(2, Metrics.KILOMETERS), new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithNearBox() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test2");
        setRate(10);
        setMessage("foo");
        sampleEntity2.setLocation(new GeoPoint(30.7806, 0.0875));
        save(sampleEntity2);
        // when
        Page<SampleEntity> pageAll = repository.findAll(new PageRequest(0, 10));
        // then
        Assert.assertThat(pageAll, is(notNullValue()));
        Assert.assertThat(pageAll.getTotalElements(), is(equalTo(2L)));
        // when
        Page<SampleEntity> page = repository.findByLocationNear(new org.springframework.data.geo.Box(new Point(46.0, 3.0), new Point(45.0, 4.0)), new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    @Test
    public void shouldExecuteCustomMethodWithNearPointAndDistance() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        // when
        Page<SampleEntity> page = repository.findByLocationNear(new Point(45.7806, 3.0875), new org.springframework.data.geo.Distance(2, Metrics.KILOMETERS), new PageRequest(0, 10));
        // then
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(equalTo(1L)));
    }

    /* DATAES-165 */
    @Test
    public void shouldAllowReturningJava8StreamInCustomQuery() {
        // given
        List<SampleEntity> entities = createSampleEntities("abc", 30);
        saveAll(entities);
        // when
        Stream<SampleEntity> stream = repository.findByType("abc");
        // then
        Assert.assertThat(stream, is(notNullValue()));
        Assert.assertThat(stream.count(), is(equalTo(30L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethod() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("some message");
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test2");
        setMessage("some message");
        save(sampleEntity2);
        // when
        long count = repository.countByType("test");
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodForNot() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("some");
        setMessage("some message");
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("some message");
        save(sampleEntity2);
        // when
        long count = repository.countByTypeNot("test");
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodWithBooleanParameter() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        setAvailable(true);
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        setAvailable(false);
        save(sampleEntity2);
        // when
        long count = repository.countByAvailable(false);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodWithLessThan() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(9);
        setMessage("some message");
        save(sampleEntity);
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setRate(20);
        setMessage("some message");
        save(sampleEntity2);
        // when
        long count = repository.countByRateLessThan(10);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodWithBefore() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("some message");
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(20);
        setMessage("some message");
        save(sampleEntity2);
        // when
        long count = repository.countByRateBefore(10);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodWithAfter() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("some message");
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(0);
        setMessage("some message");
        save(sampleEntity2);
        // when
        long count = repository.countByRateAfter(10);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodWithLike() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("some message");
        save(sampleEntity2);
        // when
        long count = repository.countByMessageLike("fo");
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodForStartingWith() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("some message");
        save(sampleEntity2);
        // when
        long count = repository.countByMessageStartingWith("fo");
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodForEndingWith() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("some message");
        save(sampleEntity2);
        // when
        long count = repository.countByMessageEndingWith("o");
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodForContains() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("some message");
        save(sampleEntity2);
        // when
        long count = repository.countByMessageContaining("fo");
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodForIn() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        save(sampleEntity2);
        List<String> ids = Arrays.asList(documentId, documentId2);
        // when
        long count = repository.countByIdIn(ids);
        // then
        Assert.assertThat(count, is(equalTo(2L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodForNotIn() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        save(sampleEntity2);
        List<String> ids = Arrays.asList(documentId);
        // when
        long count = repository.countByIdNotIn(ids);
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodForTrue() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        setAvailable(true);
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        setAvailable(false);
        save(sampleEntity2);
        // when
        long count = repository.countByAvailableTrue();
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodForFalse() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setMessage("foo");
        setAvailable(true);
        save(sampleEntity);
        // given
        String documentId2 = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId2);
        setType("test");
        setMessage("bar");
        setAvailable(false);
        save(sampleEntity2);
        // when
        long count = repository.countByAvailableFalse();
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodWithWithinGeoPoint() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity2.setLocation(new GeoPoint(30.7806, 0.0875));
        save(sampleEntity2);
        // when
        long count = repository.countByLocationWithin(new GeoPoint(45.7806, 3.0875), "2km");
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodWithWithinPoint() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity2.setLocation(new GeoPoint(30.7806, 0.0875));
        save(sampleEntity2);
        // when
        long count = repository.countByLocationWithin(new Point(45.7806, 3.0875), new org.springframework.data.geo.Distance(2, Metrics.KILOMETERS));
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodWithNearBox() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test2");
        setRate(10);
        setMessage("foo");
        sampleEntity2.setLocation(new GeoPoint(30.7806, 0.0875));
        save(sampleEntity2);
        // when
        long count = repository.countByLocationNear(new org.springframework.data.geo.Box(new Point(46.0, 3.0), new Point(45.0, 4.0)));
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }

    /* DATAES-106 */
    @Test
    public void shouldCountCustomMethodWithNearPointAndDistance() {
        // given
        String documentId = randomNumeric(5);
        SampleEntity sampleEntity = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity.setLocation(new GeoPoint(45.7806, 3.0875));
        save(sampleEntity);
        documentId = randomNumeric(5);
        SampleEntity sampleEntity2 = new SampleEntity();
        setId(documentId);
        setType("test");
        setRate(10);
        setMessage("foo");
        sampleEntity2.setLocation(new GeoPoint(30.7806, 0.0875));
        save(sampleEntity2);
        // when
        long count = repository.countByLocationNear(new Point(45.7806, 3.0875), new org.springframework.data.geo.Distance(2, Metrics.KILOMETERS));
        // then
        Assert.assertThat(count, is(equalTo(1L)));
    }
}

