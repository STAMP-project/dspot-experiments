/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.querycache;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12430")
public class QueryCacheJoinFetchTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-12430")
    public void testLifecycle() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.querycache.Person person = new org.hibernate.test.querycache.Person();
            org.hibernate.test.querycache.Phone phone1 = new org.hibernate.test.querycache.Phone("123-456-7890");
            org.hibernate.test.querycache.Phone phone2 = new org.hibernate.test.querycache.Phone("321-654-0987");
            person.addPhone(phone1);
            person.addPhone(phone2);
            entityManager.persist(person);
        });
        entityManagerFactory().getCache().evictAll();
        entityManagerFactory().unwrap(SessionFactory.class).getStatistics().clear();
        QueryCacheJoinFetchTest.Person person = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.createQuery(("select distinct p " + ("from Person p " + "join fetch p.phones ph")), .class).setHint(QueryHints.CACHEABLE, Boolean.TRUE).getSingleResult();
        });
        Assert.assertEquals(2, person.getPhones().size());
        Assert.assertEquals(0, entityManagerFactory().unwrap(SessionFactory.class).getStatistics().getQueryCacheHitCount());
        person = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.getEntityManagerFactory().getCache().evictAll();
            return entityManager.createQuery(("select distinct p " + ("from Person p " + "join fetch p.phones ph")), .class).setHint(QueryHints.CACHEABLE, Boolean.TRUE).getSingleResult();
        });
        Assert.assertEquals(1, entityManagerFactory().unwrap(SessionFactory.class).getStatistics().getQueryCacheHitCount());
        Assert.assertEquals(2, person.getPhones().size());
    }

    @Entity(name = "Person")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
        @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        private List<QueryCacheJoinFetchTest.Phone> phones = new ArrayList<>();

        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public List<QueryCacheJoinFetchTest.Phone> getPhones() {
            return phones;
        }

        public void addPhone(QueryCacheJoinFetchTest.Phone phone) {
            phones.add(phone);
            phone.setPerson(this);
        }

        public void removePhone(QueryCacheJoinFetchTest.Phone phone) {
            phones.remove(phone);
            phone.setPerson(null);
        }
    }

    @Entity(name = "Phone")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        @Column(name = "`number`", unique = true)
        private String number;

        @ManyToOne
        private QueryCacheJoinFetchTest.Person person;

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

        public QueryCacheJoinFetchTest.Person getPerson() {
            return person;
        }

        public void setPerson(QueryCacheJoinFetchTest.Person person) {
            this.person = person;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            QueryCacheJoinFetchTest.Phone phone = ((QueryCacheJoinFetchTest.Phone) (o));
            return Objects.equals(number, phone.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
    }
}

