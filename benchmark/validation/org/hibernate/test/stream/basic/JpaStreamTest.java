/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.stream.basic;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class JpaStreamTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11907")
    public void testQueryStream() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.stream.basic.MyEntity e = new org.hibernate.test.stream.basic.MyEntity();
            e.id = 1;
            e.name = "Test";
            session.persist(e);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // Test stream query without type.
            Object result = session.createQuery("From MyEntity").getResultStream().findFirst().orElse(null);
            assertTyping(.class, result);
            // Test stream query with type.
            result = session.createQuery("From MyEntity", .class).getResultStream().findFirst().orElse(null);
            assertTyping(.class, result);
            // Test stream query using forEach
            session.createQuery("From MyEntity", .class).getResultStream().forEach(( i) -> {
                assertTyping(.class, i);
            });
            Stream<Object[]> data = session.createQuery("SELECT me.id, me.name FROM MyEntity me").getResultStream();
            data.forEach(( i) -> {
                assertTyping(.class, i[0]);
                assertTyping(.class, i[1]);
            });
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

