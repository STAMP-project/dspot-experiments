/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.uniquekey;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import org.hamcrest.CoreMatchers;
import org.hibernate.annotations.NaturalId;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


public class NaturalIdCachingTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.uniquekey.Property property = new org.hibernate.test.uniquekey.Property(1, 1, 1);
            session.persist(property);
            session.persist(new org.hibernate.test.uniquekey.PropertyHolder(1, property));
            session.persist(new org.hibernate.test.uniquekey.PropertyHolder(2, property));
        });
        Assert.assertThat(sessionFactory().getStatistics().getEntityInsertCount(), CoreMatchers.is(3L));
        sessionFactory().getStatistics().clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.byId(.class).load(1);
            session.byId(.class).load(2);
        });
        Assert.assertThat(sessionFactory().getStatistics().getEntityLoadCount(), CoreMatchers.is(3L));
        Assert.assertThat(sessionFactory().getStatistics().getPrepareStatementCount(), CoreMatchers.is(3L));
    }

    @Entity(name = "PropertyHolder")
    public static class PropertyHolder implements Serializable {
        @Id
        private Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({ @JoinColumn(name = "PROP_CODE", referencedColumnName = "CODE"), @JoinColumn(name = "PROP_ITEM", referencedColumnName = "ITEM") })
        private NaturalIdCachingTest.Property property;

        private String severalOtherFields = "Several other fields ...";

        protected PropertyHolder() {
        }

        public PropertyHolder(Integer id, NaturalIdCachingTest.Property property) {
            this.id = id;
            this.property = property;
        }
    }

    @Entity(name = "PropertyEntity")
    public static class Property implements Serializable {
        @Id
        private Integer id;

        @NaturalId
        private Integer code;

        @NaturalId
        private Integer item;

        private String description = "A description ...";

        protected Property() {
        }

        public Property(Integer id, Integer code, Integer item) {
            this.id = id;
            this.code = code;
            this.item = item;
        }
    }
}

