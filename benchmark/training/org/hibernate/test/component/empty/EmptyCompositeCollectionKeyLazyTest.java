/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.component.empty;


import java.io.Serializable;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import org.hibernate.Hibernate;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class EmptyCompositeCollectionKeyLazyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testGetEntityWithEmptyCollection() {
        EmptyCompositeCollectionKeyLazyTest.AnEntity.PK id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity(new org.hibernate.test.component.empty.AnEntity.PK("first", "last"));
            session.persist(anEntity);
            return anEntity.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.find(.class, id);
            assertFalse(Hibernate.isInitialized(anEntity.names));
            assertTrue(anEntity.names.isEmpty());
        });
    }

    @Test
    public void testQueryEntityWithEmptyCollection() {
        EmptyCompositeCollectionKeyLazyTest.AnEntity.PK id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity(new org.hibernate.test.component.empty.AnEntity.PK("first", "last"));
            session.persist(anEntity);
            return anEntity.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery("from AnEntity where id = :id", .class).setParameter("id", id).uniqueResult();
            assertFalse(Hibernate.isInitialized(anEntity.names));
            assertTrue(anEntity.names.isEmpty());
        });
    }

    @Test
    public void testQueryEntityJoinFetchEmptyCollection() {
        EmptyCompositeCollectionKeyLazyTest.AnEntity.PK id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity(new org.hibernate.test.component.empty.AnEntity.PK("first", "last"));
            session.persist(anEntity);
            return anEntity.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery("from AnEntity e join fetch e.names where e.id = :id ", .class).setParameter("id", id).uniqueResult();
            assertNull(anEntity);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11928")
    public void testQueryEntityLeftJoinFetchEmptyCollection() {
        EmptyCompositeCollectionKeyLazyTest.AnEntity.PK id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity(new org.hibernate.test.component.empty.AnEntity.PK("first", "last"));
            session.persist(anEntity);
            return anEntity.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery("from AnEntity e left join fetch e.names where e.id = :id", .class).setParameter("id", id).uniqueResult();
            assertTrue(Hibernate.isInitialized(anEntity.names));
            assertTrue(anEntity.names.isEmpty());
        });
    }

    @Entity(name = "AnEntity")
    public static class AnEntity {
        @EmbeddedId
        private EmptyCompositeCollectionKeyLazyTest.AnEntity.PK id;

        @ElementCollection
        private Set<String> names;

        public AnEntity() {
        }

        public AnEntity(EmptyCompositeCollectionKeyLazyTest.AnEntity.PK id) {
            this.id = id;
        }

        @Embeddable
        public static class PK implements Serializable {
            private String firstName;

            private String lastName;

            public PK() {
            }

            public PK(String firstName, String lastName) {
                this.firstName = firstName;
                this.lastName = lastName;
            }
        }
    }
}

