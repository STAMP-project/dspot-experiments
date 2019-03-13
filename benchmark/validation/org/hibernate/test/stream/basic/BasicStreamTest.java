/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.stream.basic;


import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicStreamTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void basicStreamTest() {
        Session session = openSession();
        session.getTransaction().begin();
        // mainly we want to make sure that closing the Stream releases the ScrollableResults too
        MatcherAssert.assertThat(getJdbcCoordinator().getLogicalConnection().getResourceRegistry().hasRegisteredResources(), Is.is(false));
        final Stream<BasicStreamTest.MyEntity> stream = session.createQuery("from MyEntity", BasicStreamTest.MyEntity.class).stream();
        MatcherAssert.assertThat(getJdbcCoordinator().getLogicalConnection().getResourceRegistry().hasRegisteredResources(), Is.is(true));
        stream.forEach(System.out::println);
        MatcherAssert.assertThat(getJdbcCoordinator().getLogicalConnection().getResourceRegistry().hasRegisteredResources(), Is.is(true));
        stream.close();
        MatcherAssert.assertThat(getJdbcCoordinator().getLogicalConnection().getResourceRegistry().hasRegisteredResources(), Is.is(false));
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10824")
    public void testQueryStream() {
        Session session = openSession();
        try {
            session.getTransaction().begin();
            BasicStreamTest.MyEntity e = new BasicStreamTest.MyEntity();
            e.id = 1;
            e.name = "Test";
            session.persist(e);
            session.getTransaction().commit();
            session.clear();
            // Test stream query without type.
            Object result = session.createQuery("From MyEntity").stream().findFirst().orElse(null);
            ExtraAssertions.assertTyping(BasicStreamTest.MyEntity.class, result);
            // Test stream query with type.
            result = session.createQuery("From MyEntity", BasicStreamTest.MyEntity.class).stream().findFirst().orElse(null);
            ExtraAssertions.assertTyping(BasicStreamTest.MyEntity.class, result);
            // Test stream query using forEach
            session.createQuery("From MyEntity", BasicStreamTest.MyEntity.class).stream().forEach(( i) -> {
                assertTyping(.class, i);
            });
            Stream<Object[]> data = session.createQuery("SELECT me.id, me.name FROM MyEntity me").stream();
            data.forEach(( i) -> {
                ExtraAssertions.assertTyping(Integer.class, i[0]);
                ExtraAssertions.assertTyping(String.class, i[1]);
            });
        } finally {
            session.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11743")
    public void testTupleStream() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.stream.basic.MyEntity entity = new org.hibernate.test.stream.basic.MyEntity();
            entity.id = 2;
            entity.name = "an entity";
            session.persist(entity);
        });
        // test tuple stream using criteria
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
            Root<org.hibernate.test.stream.basic.MyEntity> me = criteria.from(.class);
            criteria.multiselect(me.get("id"), me.get("name"));
            Stream<Tuple> data = session.createQuery(criteria).stream();
            data.forEach(( tuple) -> assertTyping(.class, tuple));
        });
        // test tuple stream using JPQL
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Stream<Tuple> data = session.createQuery("SELECT me.id, me.name FROM MyEntity me", .class).stream();
            data.forEach(( tuple) -> assertTyping(.class, tuple));
        });
    }

    @Entity(name = "MyEntity")
    @Table(name = "MyEntity")
    public static class MyEntity {
        @Id
        public Integer id;

        public String name;
    }
}

