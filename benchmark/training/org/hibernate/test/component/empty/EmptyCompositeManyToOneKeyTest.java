/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.component.empty;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class EmptyCompositeManyToOneKeyTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11922")
    public void testGetEntityWithNullManyToOne() {
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.find(.class, id);
            assertNotNull(anEntity);
            assertNull(anEntity.otherEntity);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11922")
    public void testQueryEntityWithNullManyToOne() {
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery(("from AnEntity where id = " + id), .class).uniqueResult();
            assertNull(anEntity.otherEntity);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11922")
    public void testQueryEntityJoinFetchNullManyToOne() {
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery(("from AnEntity e join fetch e.otherEntity where e.id = " + id), .class).uniqueResult();
            assertNull(anEntity);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11922")
    public void testQueryEntityLeftJoinFetchNullManyToOne() {
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery(("from AnEntity e left join fetch e.otherEntity where e.id = " + id), .class).uniqueResult();
            assertNull(anEntity.otherEntity);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11922")
    public void testQueryEntityAndNullManyToOne() {
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final Object[] result = session.createQuery(("select e, e.otherEntity from AnEntity e left join e.otherEntity where e.id = " + id), .class).uniqueResult();
            assertEquals(2, result.length);
            assertTrue(.class.isInstance(result[0]));
            assertNull(result[1]);
        });
    }

    @Entity(name = "AnEntity")
    public static class AnEntity {
        @Id
        private int id;

        @ManyToOne
        private EmptyCompositeManyToOneKeyTest.OtherEntity otherEntity;
    }

    @Entity(name = "OtherEntity")
    public static class OtherEntity implements Serializable {
        @Id
        private String firstName;

        @Id
        private String lastName;

        private String description;

        @Override
        public String toString() {
            return ((((((((("OtherEntity{" + "firstName='") + (firstName)) + '\'') + ", lastName='") + (lastName)) + '\'') + ", description='") + (description)) + '\'') + '}';
        }
    }
}

