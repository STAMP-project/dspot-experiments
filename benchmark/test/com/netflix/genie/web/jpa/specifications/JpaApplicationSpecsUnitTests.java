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


import ApplicationEntity_.name;
import ApplicationEntity_.status;
import ApplicationEntity_.tags;
import ApplicationEntity_.type;
import ApplicationEntity_.user;
import ApplicationStatus.ACTIVE;
import ApplicationStatus.DEPRECATED;
import com.google.common.collect.Sets;
import com.netflix.genie.common.dto.ApplicationStatus;
import com.netflix.genie.test.categories.UnitTest;
import com.netflix.genie.web.jpa.entities.ApplicationEntity;
import com.netflix.genie.web.jpa.entities.TagEntity;
import java.util.Set;
import java.util.UUID;
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
public class JpaApplicationSpecsUnitTests {
    private static final String NAME = "tez";

    private static final String USER_NAME = "tgianos";

    private static final TagEntity TAG_1 = new TagEntity("tez");

    private static final TagEntity TAG_2 = new TagEntity("yarn");

    private static final TagEntity TAG_3 = new TagEntity("hadoop");

    private static final Set<TagEntity> TAGS = Sets.newHashSet(JpaApplicationSpecsUnitTests.TAG_1, JpaApplicationSpecsUnitTests.TAG_2, JpaApplicationSpecsUnitTests.TAG_3);

    private static final Set<ApplicationStatus> STATUSES = Sets.newHashSet(ACTIVE, DEPRECATED);

    private static final String TYPE = UUID.randomUUID().toString();

    private Root<ApplicationEntity> root;

    private CriteriaQuery<?> cq;

    private CriteriaBuilder cb;

    private SetJoin<ApplicationEntity, TagEntity> tagEntityJoin;

    /**
     * Test the find specification.
     */
    @Test
    public void testFindAll() {
        final Specification<ApplicationEntity> spec = JpaApplicationSpecs.find(JpaApplicationSpecsUnitTests.NAME, JpaApplicationSpecsUnitTests.USER_NAME, JpaApplicationSpecsUnitTests.STATUSES, JpaApplicationSpecsUnitTests.TAGS, JpaApplicationSpecsUnitTests.TYPE);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaApplicationSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaApplicationSpecsUnitTests.USER_NAME);
        for (final ApplicationStatus status : JpaApplicationSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        // Tags
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaApplicationSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(type), JpaApplicationSpecsUnitTests.TYPE);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindAllLike() {
        final String newName = (JpaApplicationSpecsUnitTests.NAME) + "%";
        final String newUser = (JpaApplicationSpecsUnitTests.USER_NAME) + "%";
        final String newType = (JpaApplicationSpecsUnitTests.TYPE) + "%";
        final Specification<ApplicationEntity> spec = JpaApplicationSpecs.find(newName, newUser, JpaApplicationSpecsUnitTests.STATUSES, JpaApplicationSpecsUnitTests.TAGS, newType);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).like(this.root.get(name), newName);
        Mockito.verify(this.cb, Mockito.times(1)).like(this.root.get(user), newUser);
        for (final ApplicationStatus status : JpaApplicationSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaApplicationSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        Mockito.verify(this.cb, Mockito.times(1)).like(this.root.get(type), newType);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoName() {
        final Specification<ApplicationEntity> spec = JpaApplicationSpecs.find(null, JpaApplicationSpecsUnitTests.USER_NAME, JpaApplicationSpecsUnitTests.STATUSES, JpaApplicationSpecsUnitTests.TAGS, JpaApplicationSpecsUnitTests.TYPE);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(name), JpaApplicationSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaApplicationSpecsUnitTests.USER_NAME);
        for (final ApplicationStatus status : JpaApplicationSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaApplicationSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(type), JpaApplicationSpecsUnitTests.TYPE);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoUserName() {
        final Specification<ApplicationEntity> spec = JpaApplicationSpecs.find(JpaApplicationSpecsUnitTests.NAME, null, JpaApplicationSpecsUnitTests.STATUSES, JpaApplicationSpecsUnitTests.TAGS, JpaApplicationSpecsUnitTests.TYPE);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaApplicationSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(user), JpaApplicationSpecsUnitTests.USER_NAME);
        for (final ApplicationStatus status : JpaApplicationSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaApplicationSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(type), JpaApplicationSpecsUnitTests.TYPE);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoStatuses() {
        final Specification<ApplicationEntity> spec = JpaApplicationSpecs.find(JpaApplicationSpecsUnitTests.NAME, JpaApplicationSpecsUnitTests.USER_NAME, null, JpaApplicationSpecsUnitTests.TAGS, JpaApplicationSpecsUnitTests.TYPE);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaApplicationSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaApplicationSpecsUnitTests.USER_NAME);
        for (final ApplicationStatus status : JpaApplicationSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaApplicationSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(type), JpaApplicationSpecsUnitTests.TYPE);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindEmptyStatuses() {
        final Specification<ApplicationEntity> spec = JpaApplicationSpecs.find(JpaApplicationSpecsUnitTests.NAME, JpaApplicationSpecsUnitTests.USER_NAME, Sets.newHashSet(), JpaApplicationSpecsUnitTests.TAGS, JpaApplicationSpecsUnitTests.TYPE);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaApplicationSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaApplicationSpecsUnitTests.USER_NAME);
        for (final ApplicationStatus status : JpaApplicationSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaApplicationSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(type), JpaApplicationSpecsUnitTests.TYPE);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoTags() {
        final Specification<ApplicationEntity> spec = JpaApplicationSpecs.find(JpaApplicationSpecsUnitTests.NAME, JpaApplicationSpecsUnitTests.USER_NAME, JpaApplicationSpecsUnitTests.STATUSES, null, JpaApplicationSpecsUnitTests.TYPE);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaApplicationSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaApplicationSpecsUnitTests.USER_NAME);
        for (final ApplicationStatus status : JpaApplicationSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.never()).join(tags);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(type), JpaApplicationSpecsUnitTests.TYPE);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoType() {
        final Specification<ApplicationEntity> spec = JpaApplicationSpecs.find(JpaApplicationSpecsUnitTests.NAME, JpaApplicationSpecsUnitTests.USER_NAME, JpaApplicationSpecsUnitTests.STATUSES, JpaApplicationSpecsUnitTests.TAGS, null);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaApplicationSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaApplicationSpecsUnitTests.USER_NAME);
        for (final ApplicationStatus status : JpaApplicationSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaApplicationSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
        Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(type), JpaApplicationSpecsUnitTests.TYPE);
    }
}

