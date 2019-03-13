/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.caching;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::caching-entity-mapping-example[]
public class NonStrictReadWriteCacheTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testCache() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.caching.Person person = new org.hibernate.userguide.caching.Person();
            entityManager.persist(person);
            org.hibernate.userguide.caching.Phone home = new org.hibernate.userguide.caching.Phone("123-456-7890");
            org.hibernate.userguide.caching.Phone office = new org.hibernate.userguide.caching.Phone("098-765-4321");
            person.addPhone(home);
            person.addPhone(office);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.caching.Person person = entityManager.find(.class, 1L);
            person.getPhones().size();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("Log collection from cache");
            // tag::caching-collection-example[]
            org.hibernate.userguide.caching.Person person = entityManager.find(.class, 1L);
            person.getPhones().size();
            // end::caching-collection-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("Load from cache");
            entityManager.find(.class, 1L).getPhones().size();
        });
    }

    @Entity(name = "Person")
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public static class Person {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private String name;

        // tag::caching-collection-mapping-example[]
        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
        @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
        private List<NonStrictReadWriteCacheTest.Phone> phones = new ArrayList<>();

        // end::caching-collection-mapping-example[]
        @Version
        private int version;

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

        public void setName(String name) {
            this.name = name;
        }

        public List<NonStrictReadWriteCacheTest.Phone> getPhones() {
            return phones;
        }

        public void addPhone(NonStrictReadWriteCacheTest.Phone phone) {
            phones.add(phone);
            phone.setPerson(this);
        }
    }

    // tag::caching-entity-mapping-example[]
    // tag::caching-entity-mapping-example[]
    @Entity(name = "Phone")
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        private String mobile;

        @ManyToOne
        private NonStrictReadWriteCacheTest.Person person;

        @Version
        private int version;

        // Getters and setters are omitted for brevity
        // end::caching-entity-mapping-example[]
        public Phone() {
        }

        public Phone(String mobile) {
            this.mobile = mobile;
        }

        public Long getId() {
            return id;
        }

        public String getMobile() {
            return mobile;
        }

        public NonStrictReadWriteCacheTest.Person getPerson() {
            return person;
        }

        public void setPerson(NonStrictReadWriteCacheTest.Person person) {
            this.person = person;
        }
    }
}

