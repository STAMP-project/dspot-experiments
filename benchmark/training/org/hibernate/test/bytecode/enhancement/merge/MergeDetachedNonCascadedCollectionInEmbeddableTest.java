/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.merge;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-12637")
@RunWith(BytecodeEnhancerRunner.class)
public class MergeDetachedNonCascadedCollectionInEmbeddableTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testMergeDetached() {
        final MergeDetachedNonCascadedCollectionInEmbeddableTest.Heading heading = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.bytecode.enhancement.merge.Heading entity = new org.hibernate.test.bytecode.enhancement.merge.Heading();
            entity.name = "new";
            entity.setGrouping(new org.hibernate.test.bytecode.enhancement.merge.Grouping());
            org.hibernate.test.bytecode.enhancement.merge.Thing thing = new org.hibernate.test.bytecode.enhancement.merge.Thing();
            entity.getGrouping().getThings().add(thing);
            session.save(thing);
            session.save(entity);
            return entity;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            heading.name = "updated";
            org.hibernate.test.bytecode.enhancement.merge.Heading headingMerged = ((org.hibernate.test.bytecode.enhancement.merge.Heading) (session.merge(heading)));
            assertNotSame(heading, headingMerged);
            assertNotSame(heading.grouping, headingMerged.grouping);
            assertNotSame(heading.grouping.things, headingMerged.grouping.things);
        });
    }

    @Entity(name = "Heading")
    public static class Heading {
        private long id;

        private String name;

        private MergeDetachedNonCascadedCollectionInEmbeddableTest.Grouping grouping;

        @Id
        @GeneratedValue
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public MergeDetachedNonCascadedCollectionInEmbeddableTest.Grouping getGrouping() {
            return grouping;
        }

        public void setGrouping(MergeDetachedNonCascadedCollectionInEmbeddableTest.Grouping grouping) {
            this.grouping = grouping;
        }
    }

    @Embeddable
    public static class Grouping {
        private Set<MergeDetachedNonCascadedCollectionInEmbeddableTest.Thing> things = new HashSet<>();

        @OneToMany(fetch = FetchType.LAZY)
        @JoinColumn
        public Set<MergeDetachedNonCascadedCollectionInEmbeddableTest.Thing> getThings() {
            return things;
        }

        public void setThings(Set<MergeDetachedNonCascadedCollectionInEmbeddableTest.Thing> things) {
            this.things = things;
        }
    }

    @Entity(name = "Thing")
    public static class Thing {
        private long id;

        @Id
        @GeneratedValue
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}

