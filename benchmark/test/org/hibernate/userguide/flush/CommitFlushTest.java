/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.flush;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class CommitFlushTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testFlushJPQL() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("testFlushJPQL");
            // tag::flushing-commit-flush-jpql-example[]
            org.hibernate.userguide.flush.Person person = new org.hibernate.userguide.flush.Person("John Doe");
            entityManager.persist(person);
            entityManager.createQuery("select p from Advertisement p").setFlushMode(FlushModeType.COMMIT).getResultList();
            entityManager.createQuery("select p from Person p").setFlushMode(FlushModeType.COMMIT).getResultList();
            // end::flushing-commit-flush-jpql-example[]
        });
    }

    @Test
    public void testFlushSQL() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createNativeQuery("delete from Person").executeUpdate();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("testFlushSQL");
            // tag::flushing-commit-flush-sql-example[]
            org.hibernate.userguide.flush.Person person = new org.hibernate.userguide.flush.Person("John Doe");
            entityManager.persist(person);
            assertTrue(((((Number) (entityManager.createNativeQuery("select count(*) from Person").getSingleResult())).intValue()) == 1));
            // end::flushing-commit-flush-sql-example[]
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
        private List<CommitFlushTest.Phone> phones = new ArrayList<>();

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

        public List<CommitFlushTest.Phone> getPhones() {
            return phones;
        }

        public void addPhone(CommitFlushTest.Phone phone) {
            phones.add(phone);
            phone.setPerson(this);
        }
    }

    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne
        private CommitFlushTest.Person person;

        @Column(name = "`number`")
        private String number;

        public Phone() {
        }

        public Phone(String number) {
            this.number = number;
        }

        public Long getId() {
            return id;
        }

        public String getNumber() {
            return number;
        }

        public CommitFlushTest.Person getPerson() {
            return person;
        }

        public void setPerson(CommitFlushTest.Person person) {
            this.person = person;
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

