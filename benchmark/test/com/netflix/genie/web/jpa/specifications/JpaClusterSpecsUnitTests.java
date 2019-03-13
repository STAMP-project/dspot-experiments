/**
 * Copyright 2015 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package com.netflix.genie.web.jpa.specifications;


import ClusterEntity_.name;
import ClusterEntity_.status;
import ClusterEntity_.tags;
import ClusterEntity_.updated;
import com.google.common.collect.Sets;
import com.netflix.genie.common.dto.ClusterStatus;
import com.netflix.genie.test.categories.UnitTest;
import com.netflix.genie.web.jpa.entities.ClusterEntity;
import com.netflix.genie.web.jpa.entities.TagEntity;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;


/**
 * Tests for the application specifications.
 *
 * @author tgianos
 */
@Category(UnitTest.class)
public class JpaClusterSpecsUnitTests {
    private static final String NAME = "h2prod";

    private static final TagEntity TAG_1 = new TagEntity("prod");

    private static final TagEntity TAG_2 = new TagEntity("yarn");

    private static final TagEntity TAG_3 = new TagEntity("hadoop");

    private static final ClusterStatus STATUS_1 = ClusterStatus.UP;

    private static final ClusterStatus STATUS_2 = ClusterStatus.OUT_OF_SERVICE;

    private static final Set<TagEntity> TAGS = Sets.newHashSet(JpaClusterSpecsUnitTests.TAG_1, JpaClusterSpecsUnitTests.TAG_2, JpaClusterSpecsUnitTests.TAG_3);

    private static final Set<ClusterStatus> STATUSES = EnumSet.noneOf(ClusterStatus.class);

    private static final Instant MIN_UPDATE_TIME = Instant.ofEpochMilli(123467L);

    private static final Instant MAX_UPDATE_TIME = Instant.ofEpochMilli(1234643L);

    private Root<ClusterEntity> root;

    private CriteriaQuery<?> cq;

    private CriteriaBuilder cb;

    private SetJoin<ClusterEntity, TagEntity> tagEntityJoin;

    /**
     * Test the find specification.
     */
    @Test
    public void testFindAll() {
        final Specification<ClusterEntity> spec = JpaClusterSpecs.find(JpaClusterSpecsUnitTests.NAME, JpaClusterSpecsUnitTests.STATUSES, JpaClusterSpecsUnitTests.TAGS, JpaClusterSpecsUnitTests.MIN_UPDATE_TIME, JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaClusterSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).greaterThanOrEqualTo(this.root.get(updated), JpaClusterSpecsUnitTests.MIN_UPDATE_TIME);
        Mockito.verify(this.cb, Mockito.times(1)).lessThan(this.root.get(updated), JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaClusterSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        for (final ClusterStatus status : JpaClusterSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindAllLike() {
        final String newName = (JpaClusterSpecsUnitTests.NAME) + "%";
        final Specification<ClusterEntity> spec = JpaClusterSpecs.find(newName, JpaClusterSpecsUnitTests.STATUSES, JpaClusterSpecsUnitTests.TAGS, JpaClusterSpecsUnitTests.MIN_UPDATE_TIME, JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).like(this.root.get(name), newName);
        Mockito.verify(this.cb, Mockito.times(1)).greaterThanOrEqualTo(this.root.get(updated), JpaClusterSpecsUnitTests.MIN_UPDATE_TIME);
        Mockito.verify(this.cb, Mockito.times(1)).lessThan(this.root.get(updated), JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaClusterSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        for (final ClusterStatus status : JpaClusterSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoName() {
        final Specification<ClusterEntity> spec = JpaClusterSpecs.find(null, JpaClusterSpecsUnitTests.STATUSES, JpaClusterSpecsUnitTests.TAGS, JpaClusterSpecsUnitTests.MIN_UPDATE_TIME, JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.never()).like(this.root.get(name), JpaClusterSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(name), JpaClusterSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).greaterThanOrEqualTo(this.root.get(updated), JpaClusterSpecsUnitTests.MIN_UPDATE_TIME);
        Mockito.verify(this.cb, Mockito.times(1)).lessThan(this.root.get(updated), JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaClusterSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        for (final ClusterStatus status : JpaClusterSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoStatuses() {
        final Specification<ClusterEntity> spec = JpaClusterSpecs.find(JpaClusterSpecsUnitTests.NAME, null, JpaClusterSpecsUnitTests.TAGS, JpaClusterSpecsUnitTests.MIN_UPDATE_TIME, JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaClusterSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).greaterThanOrEqualTo(this.root.get(updated), JpaClusterSpecsUnitTests.MIN_UPDATE_TIME);
        Mockito.verify(this.cb, Mockito.times(1)).lessThan(this.root.get(updated), JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaClusterSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        for (final ClusterStatus status : JpaClusterSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(status), status);
        }
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindEmptyStatuses() {
        final Specification<ClusterEntity> spec = JpaClusterSpecs.find(JpaClusterSpecsUnitTests.NAME, EnumSet.noneOf(ClusterStatus.class), JpaClusterSpecsUnitTests.TAGS, JpaClusterSpecsUnitTests.MIN_UPDATE_TIME, JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaClusterSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).greaterThanOrEqualTo(this.root.get(updated), JpaClusterSpecsUnitTests.MIN_UPDATE_TIME);
        Mockito.verify(this.cb, Mockito.times(1)).lessThan(this.root.get(updated), JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaClusterSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        for (final ClusterStatus status : JpaClusterSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(status), status);
        }
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoTags() {
        final Specification<ClusterEntity> spec = JpaClusterSpecs.find(JpaClusterSpecsUnitTests.NAME, JpaClusterSpecsUnitTests.STATUSES, null, JpaClusterSpecsUnitTests.MIN_UPDATE_TIME, JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaClusterSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).greaterThanOrEqualTo(this.root.get(updated), JpaClusterSpecsUnitTests.MIN_UPDATE_TIME);
        Mockito.verify(this.cb, Mockito.times(1)).lessThan(this.root.get(updated), JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        Mockito.verify(this.root, Mockito.never()).join(tags);
        for (final ClusterStatus status : JpaClusterSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoMinTime() {
        final Specification<ClusterEntity> spec = JpaClusterSpecs.find(JpaClusterSpecsUnitTests.NAME, JpaClusterSpecsUnitTests.STATUSES, JpaClusterSpecsUnitTests.TAGS, null, JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaClusterSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.never()).greaterThanOrEqualTo(this.root.get(updated), JpaClusterSpecsUnitTests.MIN_UPDATE_TIME);
        Mockito.verify(this.cb, Mockito.times(1)).lessThan(this.root.get(updated), JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaClusterSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        for (final ClusterStatus status : JpaClusterSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoMax() {
        final Specification<ClusterEntity> spec = JpaClusterSpecs.find(JpaClusterSpecsUnitTests.NAME, JpaClusterSpecsUnitTests.STATUSES, JpaClusterSpecsUnitTests.TAGS, JpaClusterSpecsUnitTests.MIN_UPDATE_TIME, null);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaClusterSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).greaterThanOrEqualTo(this.root.get(updated), JpaClusterSpecsUnitTests.MIN_UPDATE_TIME);
        Mockito.verify(this.cb, Mockito.never()).lessThan(this.root.get(updated), JpaClusterSpecsUnitTests.MAX_UPDATE_TIME);
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaClusterSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        for (final ClusterStatus status : JpaClusterSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
    }
}

