/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.immutable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.HibernateException;
import org.hibernate.annotations.Immutable;
import org.hibernate.query.Query;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12387")
public class ImmutableEntityUpdateQueryHandlingModeExceptionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testBulkUpdate() {
        Country _country = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Country country = new Country();
            country.setName("Germany");
            session.persist(country);
            return country;
        });
        try {
            TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
                session.createQuery(("update Country " + "set name = :name")).setParameter("name", "N/A").executeUpdate();
            });
            Assert.fail("Should throw HibernateException");
        } catch (HibernateException e) {
            Assert.assertEquals("The query: [update Country set name = :name] attempts to update an immutable entity: [Country]", e.getMessage());
        }
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Country country = session.find(.class, _country.getId());
            assertEquals("Germany", country.getName());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12927")
    public void testUpdateMutableWithImmutableSubSelect() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String selector = "foo";
            org.hibernate.test.annotations.immutable.ImmutableEntity trouble = new org.hibernate.test.annotations.immutable.ImmutableEntity(selector);
            session.persist(trouble);
            org.hibernate.test.annotations.immutable.MutableEntity entity = new org.hibernate.test.annotations.immutable.MutableEntity(trouble, "start");
            session.persist(entity);
            // Change a mutable value via selection based on an immutable property
            String statement = "Update MutableEntity e set e.changeable = :changeable where e.trouble.id in " + "(select i.id from ImmutableEntity i where i.selector = :selector)";
            Query query = session.createQuery(statement);
            query.setParameter("changeable", "end");
            query.setParameter("selector", "foo");
            query.executeUpdate();
            session.refresh(entity);
            // Assert that the value was changed. If HHH-12927 has not been fixed an exception will be thrown
            // before we get here.
            assertEquals("end", entity.getChangeable());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12927")
    public void testUpdateImmutableWithMutableSubSelect() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String selector = "foo";
            org.hibernate.test.annotations.immutable.ImmutableEntity trouble = new org.hibernate.test.annotations.immutable.ImmutableEntity(selector);
            session.persist(trouble);
            org.hibernate.test.annotations.immutable.MutableEntity entity = new org.hibernate.test.annotations.immutable.MutableEntity(trouble, "start");
            session.persist(entity);
            // Change a mutable value via selection based on an immutable property
            String statement = "Update ImmutableEntity e set e.selector = :changeable where e.id in " + "(select i.id from MutableEntity i where i.changeable = :selector)";
            Query query = session.createQuery(statement);
            query.setParameter("changeable", "end");
            query.setParameter("selector", "foo");
            try {
                query.executeUpdate();
                fail("Should have throw exception");
            } catch ( expected) {
            }
        });
    }

    @Entity(name = "MutableEntity")
    public static class MutableEntity {
        @Id
        @GeneratedValue
        private Long id;

        private String changeable;

        @OneToOne
        private ImmutableEntityUpdateQueryHandlingModeExceptionTest.ImmutableEntity trouble;

        public MutableEntity() {
        }

        public MutableEntity(ImmutableEntityUpdateQueryHandlingModeExceptionTest.ImmutableEntity trouble, String changeable) {
            this.trouble = trouble;
            this.changeable = changeable;
        }

        public Long getId() {
            return id;
        }

        public ImmutableEntityUpdateQueryHandlingModeExceptionTest.ImmutableEntity getTrouble() {
            return trouble;
        }

        public String getChangeable() {
            return changeable;
        }

        public void setChangeable(String changeable) {
            this.changeable = changeable;
        }
    }

    @Entity(name = "ImmutableEntity")
    @Immutable
    public static class ImmutableEntity {
        @Id
        @GeneratedValue
        private Long id;

        private String selector;

        public ImmutableEntity() {
        }

        public ImmutableEntity(String selector) {
            this.selector = selector;
        }

        public Long getId() {
            return id;
        }

        public String getSelector() {
            return selector;
        }
    }
}

