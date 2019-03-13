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


import CommandEntity_.name;
import CommandEntity_.status;
import CommandEntity_.tags;
import CommandEntity_.user;
import CommandStatus.ACTIVE;
import CommandStatus.INACTIVE;
import com.google.common.collect.Sets;
import com.netflix.genie.common.dto.CommandStatus;
import com.netflix.genie.test.categories.UnitTest;
import com.netflix.genie.web.jpa.entities.CommandEntity;
import com.netflix.genie.web.jpa.entities.TagEntity;
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
 * Tests for the command specifications.
 *
 * @author tgianos
 */
@Category(UnitTest.class)
public class JpaCommandSpecsUnitTests {
    private static final String NAME = "hive";

    private static final String USER_NAME = "tgianos";

    private static final TagEntity TAG_1 = new TagEntity("prod");

    private static final TagEntity TAG_2 = new TagEntity("hive");

    private static final TagEntity TAG_3 = new TagEntity("11");

    private static final Set<TagEntity> TAGS = Sets.newHashSet(JpaCommandSpecsUnitTests.TAG_1, JpaCommandSpecsUnitTests.TAG_2, JpaCommandSpecsUnitTests.TAG_3);

    private static final Set<CommandStatus> STATUSES = Sets.newHashSet(ACTIVE, INACTIVE);

    private Root<CommandEntity> root;

    private CriteriaQuery<?> cq;

    private CriteriaBuilder cb;

    private SetJoin<CommandEntity, TagEntity> tagEntityJoin;

    /**
     * Test the find specification.
     */
    @Test
    public void testFindAll() {
        final Specification<CommandEntity> spec = JpaCommandSpecs.find(JpaCommandSpecsUnitTests.NAME, JpaCommandSpecsUnitTests.USER_NAME, JpaCommandSpecsUnitTests.STATUSES, JpaCommandSpecsUnitTests.TAGS);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaCommandSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaCommandSpecsUnitTests.USER_NAME);
        for (final CommandStatus status : JpaCommandSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaCommandSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindAllLike() {
        final String newName = (JpaCommandSpecsUnitTests.NAME) + "%";
        final String newUser = (JpaCommandSpecsUnitTests.USER_NAME) + "%";
        final Specification<CommandEntity> spec = JpaCommandSpecs.find(newName, newUser, JpaCommandSpecsUnitTests.STATUSES, JpaCommandSpecsUnitTests.TAGS);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).like(this.root.get(name), newName);
        Mockito.verify(this.cb, Mockito.times(1)).like(this.root.get(user), newUser);
        for (final CommandStatus status : JpaCommandSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaCommandSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoName() {
        final Specification<CommandEntity> spec = JpaCommandSpecs.find(null, JpaCommandSpecsUnitTests.USER_NAME, JpaCommandSpecsUnitTests.STATUSES, JpaCommandSpecsUnitTests.TAGS);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(name), JpaCommandSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaCommandSpecsUnitTests.USER_NAME);
        for (final CommandStatus status : JpaCommandSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaCommandSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoUserName() {
        final Specification<CommandEntity> spec = JpaCommandSpecs.find(JpaCommandSpecsUnitTests.NAME, null, JpaCommandSpecsUnitTests.STATUSES, JpaCommandSpecsUnitTests.TAGS);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaCommandSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(user), JpaCommandSpecsUnitTests.USER_NAME);
        for (final CommandStatus status : JpaCommandSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaCommandSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoTags() {
        final Specification<CommandEntity> spec = JpaCommandSpecs.find(JpaCommandSpecsUnitTests.NAME, JpaCommandSpecsUnitTests.USER_NAME, JpaCommandSpecsUnitTests.STATUSES, null);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaCommandSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaCommandSpecsUnitTests.USER_NAME);
        for (final CommandStatus status : JpaCommandSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.never()).join(tags);
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindNoStatuses() {
        final Specification<CommandEntity> spec = JpaCommandSpecs.find(JpaCommandSpecsUnitTests.NAME, JpaCommandSpecsUnitTests.USER_NAME, null, JpaCommandSpecsUnitTests.TAGS);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaCommandSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaCommandSpecsUnitTests.USER_NAME);
        for (final CommandStatus status : JpaCommandSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaCommandSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
    }

    /**
     * Test the find specification.
     */
    @Test
    public void testFindEmptyStatuses() {
        final Specification<CommandEntity> spec = JpaCommandSpecs.find(JpaCommandSpecsUnitTests.NAME, JpaCommandSpecsUnitTests.USER_NAME, Sets.newHashSet(), JpaCommandSpecsUnitTests.TAGS);
        spec.toPredicate(this.root, this.cq, this.cb);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(name), JpaCommandSpecsUnitTests.NAME);
        Mockito.verify(this.cb, Mockito.times(1)).equal(this.root.get(user), JpaCommandSpecsUnitTests.USER_NAME);
        for (final CommandStatus status : JpaCommandSpecsUnitTests.STATUSES) {
            Mockito.verify(this.cb, Mockito.never()).equal(this.root.get(status), status);
        }
        Mockito.verify(this.root, Mockito.times(1)).join(tags);
        Mockito.verify(this.tagEntityJoin, Mockito.times(1)).in(JpaCommandSpecsUnitTests.TAGS);
        Mockito.verify(this.cq, Mockito.times(1)).groupBy(Mockito.any(Path.class));
        Mockito.verify(this.cq, Mockito.times(1)).having(Mockito.any(Predicate.class));
    }
}

