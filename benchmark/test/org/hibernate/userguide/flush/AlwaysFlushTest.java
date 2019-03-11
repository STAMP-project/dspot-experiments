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
import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class AlwaysFlushTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testFlushSQL() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createNativeQuery("delete from Person").executeUpdate();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("testFlushSQL");
            // tag::flushing-always-flush-sql-example[]
            org.hibernate.userguide.flush.Person person = new org.hibernate.userguide.flush.Person("John Doe");
            entityManager.persist(person);
            Session session = entityManager.unwrap(.class);
            assertTrue(((((Number) (session.createNativeQuery("select count(*) from Person").setFlushMode(FlushMode.ALWAYS).uniqueResult())).intValue()) == 1));
            // end::flushing-always-flush-sql-example[]
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
        private List<AlwaysFlushTest.Phone> phones = new ArrayList<>();

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

        public List<AlwaysFlushTest.Phone> getPhones() {
            return phones;
        }

        public void addPhone(AlwaysFlushTest.Phone phone) {
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
        private AlwaysFlushTest.Person person;

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

        public AlwaysFlushTest.Person getPerson() {
            return person;
        }

        public void setPerson(AlwaysFlushTest.Person person) {
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

