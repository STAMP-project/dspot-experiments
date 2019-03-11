/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.locking;


import java.util.ArrayList;
import java.util.Collections;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::locking-jpa-query-hints-scope-entity-example[]
public class ExplicitLockingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testJPALockTimeout() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.locking.Person person = new org.hibernate.userguide.locking.Person("John Doe");
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("testJPALockTimeout");
            Long id = 1L;
            // tag::locking-jpa-query-hints-timeout-example[]
            entityManager.find(.class, id, LockModeType.PESSIMISTIC_WRITE, Collections.singletonMap("javax.persistence.lock.timeout", 200));
            // end::locking-jpa-query-hints-timeout-example[]
        });
    }

    @Test
    public void testJPALockScope() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.locking.Person person = new org.hibernate.userguide.locking.Person("John Doe");
            entityManager.persist(person);
            org.hibernate.userguide.locking.Phone home = new org.hibernate.userguide.locking.Phone("123-456-7890");
            org.hibernate.userguide.locking.Phone office = new org.hibernate.userguide.locking.Phone("098-765-4321");
            person.getPhones().add(home);
            person.getPhones().add(office);
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("testJPALockScope");
            Long id = 1L;
            // tag::locking-jpa-query-hints-scope-example[]
            org.hibernate.userguide.locking.Person person = entityManager.find(.class, id, LockModeType.PESSIMISTIC_WRITE, Collections.singletonMap("javax.persistence.lock.scope", PessimisticLockScope.EXTENDED));
            // end::locking-jpa-query-hints-scope-example[]
            assertEquals(2, person.getPhones().size());
        });
    }

    @Test
    public void testBuildLockRequest() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("testBuildLockRequest");
            org.hibernate.userguide.locking.Person person = new org.hibernate.userguide.locking.Person("John Doe");
            org.hibernate.userguide.locking.Phone home = new org.hibernate.userguide.locking.Phone("123-456-7890");
            org.hibernate.userguide.locking.Phone office = new org.hibernate.userguide.locking.Phone("098-765-4321");
            person.getPhones().add(home);
            person.getPhones().add(office);
            entityManager.persist(person);
            entityManager.flush();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long id = 1L;
            // tag::locking-buildLockRequest-example[]
            org.hibernate.userguide.locking.Person person = entityManager.find(.class, id);
            Session session = entityManager.unwrap(.class);
            session.buildLockRequest(LockOptions.NONE).setLockMode(LockMode.PESSIMISTIC_READ).setTimeOut(LockOptions.NO_WAIT).lock(person);
            // end::locking-buildLockRequest-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long id = 1L;
            // tag::locking-buildLockRequest-scope-example[]
            org.hibernate.userguide.locking.Person person = entityManager.find(.class, id);
            Session session = entityManager.unwrap(.class);
            session.buildLockRequest(LockOptions.NONE).setLockMode(LockMode.PESSIMISTIC_READ).setTimeOut(LockOptions.NO_WAIT).setScope(true).lock(person);
            // end::locking-buildLockRequest-scope-example[]
        });
    }

    @Test
    @RequiresDialect(Oracle8iDialect.class)
    public void testFollowOnLocking() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("testBuildLockRequest");
            org.hibernate.userguide.locking.Person person1 = new org.hibernate.userguide.locking.Person("John Doe");
            org.hibernate.userguide.locking.Person person2 = new org.hibernate.userguide.locking.Person("Mrs. John Doe");
            entityManager.persist(person1);
            entityManager.persist(person2);
            entityManager.flush();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::locking-follow-on-example[]
            List<org.hibernate.userguide.locking.Person> persons = entityManager.createQuery("select DISTINCT p from Person p", .class).setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
            // end::locking-follow-on-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::locking-follow-on-secondary-query-example[]
            List<org.hibernate.userguide.locking.Person> persons = entityManager.createQuery("select DISTINCT p from Person p", .class).getResultList();
            entityManager.createQuery("select p.id from Person p where p in :persons").setLockMode(LockModeType.PESSIMISTIC_WRITE).setParameter("persons", persons).getResultList();
            // end::locking-follow-on-secondary-query-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::locking-follow-on-explicit-example[]
            List<org.hibernate.userguide.locking.Person> persons = entityManager.createQuery("select p from Person p", .class).setMaxResults(10).unwrap(.class).setLockOptions(new LockOptions(LockMode.PESSIMISTIC_WRITE).setFollowOnLocking(false)).getResultList();
            // end::locking-follow-on-explicit-example[]
        });
    }

    // tag::locking-jpa-query-hints-scope-entity-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`name`")
        private String name;

        @ElementCollection
        @JoinTable(name = "person_phone", joinColumns = @JoinColumn(name = "person_id"))
        private java.util.List<ExplicitLockingTest.Phone> phones = new ArrayList<>();

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

        public java.util.List<ExplicitLockingTest.Phone> getPhones() {
            return phones;
        }
    }

    @Embeddable
    public static class Phone {
        @Column
        private String mobile;

        public Phone() {
        }

        public Phone(String mobile) {
            this.mobile = mobile;
        }
    }
}

