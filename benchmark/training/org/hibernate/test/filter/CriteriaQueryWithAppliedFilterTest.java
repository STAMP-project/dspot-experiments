/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.filter;


import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.Criteria;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10991")
public class CriteriaQueryWithAppliedFilterTest extends BaseCoreFunctionalTestCase {
    private static final CriteriaQueryWithAppliedFilterTest.Identifier STUDENT_ID = new CriteriaQueryWithAppliedFilterTest.Identifier(2, new CriteriaQueryWithAppliedFilterTest.Identifier2(4, 5L));

    @Test
    public void testSubquery() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final Criteria query = session.createCriteria(.class);
            query.add(Restrictions.eq("name", "dre"));
            final DetachedCriteria inner = DetachedCriteria.forClass(.class);
            inner.setProjection(Projections.min("age"));
            query.add(Property.forName("age").eq(inner));
            query.add(Restrictions.eq("name", "dre"));
            final List list = query.list();
            assertThat(list.size(), is(1));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.enableFilter("statusFilter").setParameter("status", "deleted");
            final Criteria query = session.createCriteria(.class);
            query.add(Restrictions.eq("name", "dre"));
            final DetachedCriteria inner = DetachedCriteria.forClass(.class);
            inner.setProjection(Projections.min("age"));
            query.add(Property.forName("age").eq(inner));
            query.add(Restrictions.eq("name", "dre"));
            final List list = query.list();
            assertThat(list.size(), is(0));
        });
    }

    @Test
    public void testSubqueryWithRestrictionsOnComponentTypes() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.enableFilter("statusFilter").setParameter("status", "active");
            final Criteria query = session.createCriteria(.class);
            query.add(Restrictions.eq("id", STUDENT_ID));
            final DetachedCriteria subSelect = DetachedCriteria.forClass(.class);
            subSelect.setProjection(Projections.max("age"));
            subSelect.add(Restrictions.eq("id", STUDENT_ID));
            query.add(Property.forName("age").eq(subSelect));
            final List list = query.list();
            assertThat(list.size(), is(1));
        });
    }

    @Test
    public void testSubqueryWithRestrictionsOnComponentTypes2() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.enableFilter("statusFilter").setParameter("status", "active");
            final Criteria query = session.createCriteria(.class);
            query.add(Restrictions.eq("id", STUDENT_ID));
            final DetachedCriteria subSelect = DetachedCriteria.forClass(.class);
            subSelect.setProjection(Projections.max("age"));
            subSelect.add(Restrictions.eq("address", new org.hibernate.test.filter.Address("London", "Lollard St")));
            subSelect.add(Restrictions.eq("id", STUDENT_ID));
            query.add(Property.forName("age").eq(subSelect));
            final List list = query.list();
            assertThat(list.size(), is(1));
        });
    }

    @Test
    public void testRestrictionsOnComponentTypes() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.enableFilter("statusFilter").setParameter("status", "active");
            final Criteria query = session.createCriteria(.class);
            query.add(Restrictions.eq("id", STUDENT_ID));
            query.add(Restrictions.eq("address", new org.hibernate.test.filter.Address("London", "Lollard St")));
            query.add(Restrictions.eq("name", "dre"));
            final List list = query.list();
            assertThat(list.size(), is(1));
        });
    }

    @FilterDef(name = "statusFilter", parameters = { @ParamDef(name = "status", type = "string") })
    @Filter(name = "statusFilter", condition = "STATUS = :status ")
    @Entity(name = "Student")
    @Table(name = "STUDENT")
    public static class Student {
        @EmbeddedId
        private CriteriaQueryWithAppliedFilterTest.Identifier id;

        private String name;

        private int age;

        @Column(name = "STATUS")
        private String status;

        @Embedded
        private CriteriaQueryWithAppliedFilterTest.Address address;

        public void setId(CriteriaQueryWithAppliedFilterTest.Identifier id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setAddress(CriteriaQueryWithAppliedFilterTest.Address address) {
            this.address = address;
        }
    }

    @Embeddable
    public static class Identifier implements Serializable {
        private Integer id1;

        @Embedded
        private CriteriaQueryWithAppliedFilterTest.Identifier2 id2;

        public Identifier() {
        }

        public Identifier(Integer id1, CriteriaQueryWithAppliedFilterTest.Identifier2 id2) {
            this.id1 = id1;
            this.id2 = id2;
        }
    }

    @Embeddable
    public static class Identifier2 implements Serializable {
        private Integer id3;

        private Long id4;

        public Identifier2() {
        }

        public Identifier2(Integer id1, Long id2) {
            this.id3 = id1;
            this.id4 = id2;
        }
    }

    @Embeddable
    public static class Address implements Serializable {
        private String city;

        private String street;

        public Address() {
        }

        public Address(String city, String street) {
            this.city = city;
            this.street = street;
        }
    }
}

