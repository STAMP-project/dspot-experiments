/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.flush;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::flushing-auto-flush-jpql-entity-example[]
public class HibernateAutoFlushTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testFlushAutoSQLNativeSession() {
        doInHibernate(this::sessionFactory, ( session) -> {
            session.createNativeQuery("delete from Person").executeUpdate();
        });
        doInHibernate(this::sessionFactory, ( session) -> {
            log.info("testFlushAutoSQLNativeSession");
            // tag::flushing-auto-flush-sql-native-example[]
            assertTrue(((((Number) (session.createNativeQuery("select count(*) from Person").getSingleResult())).intValue()) == 0));
            org.hibernate.userguide.flush.Person person = new org.hibernate.userguide.flush.Person("John Doe");
            session.persist(person);
            assertTrue(((((Number) (session.createNativeQuery("select count(*) from Person").uniqueResult())).intValue()) == 0));
            // end::flushing-auto-flush-sql-native-example[]
        });
    }

    // tag::flushing-auto-flush-jpql-entity-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    @Entity(name = "Advertisement")
    public static class Advertisement {
        @Id
        @GeneratedValue
        private Long id;

        private String title;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}

