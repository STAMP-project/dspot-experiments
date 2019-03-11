/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class TreatJoinTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8488")
    public void testTreatJoin() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Bid> query = cb.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Bid> bid = query.from(.class);
            Join<org.hibernate.jpa.test.criteria.Bid, org.hibernate.jpa.test.criteria.Book> book = cb.treat(bid.join("item"), .class);
            query.select(book.get("title"));
            final List<org.hibernate.jpa.test.criteria.Bid> resultList = entityManager.createQuery(query).getResultList();
            assertThat(resultList.size(), is(2));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8488")
    public void testTreatJoin2() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Bid> query = cb.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Bid> bid = query.from(.class);
            cb.treat(bid.join("item"), .class);
            cb.treat(bid.join("item"), .class);
            query.select(bid);
            final List<org.hibernate.jpa.test.criteria.Bid> resultList = entityManager.createQuery(query).getResultList();
            assertThat(resultList.size(), is(2));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8488")
    public void testJoinMethodOnATreatedJoin() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Bid> query = cb.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Bid> bid = query.from(.class);
            final Join<org.hibernate.jpa.test.criteria.Bid, org.hibernate.jpa.test.criteria.Book> item = bid.join("item");
            final Join<Object, Object> price = item.join("price");
            Join<org.hibernate.jpa.test.criteria.Bid, org.hibernate.jpa.test.criteria.Book> book = cb.treat(item, .class);
            Join<org.hibernate.jpa.test.criteria.Book, org.hibernate.jpa.test.criteria.Author> owner = book.join("author");
            query.select(owner.get("name"));
            query.where(cb.equal(price.get("amount"), 10));
            final List<org.hibernate.jpa.test.criteria.Bid> resultList = entityManager.createQuery(query).getResultList();
            assertThat(resultList.size(), is(2));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11081")
    public void testTreatedJoinInWhereClause() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Bid> query = cb.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Bid> bid = query.from(.class);
            final Join<org.hibernate.jpa.test.criteria.Bid, org.hibernate.jpa.test.criteria.Book> item = bid.join("item");
            Join<org.hibernate.jpa.test.criteria.Bid, org.hibernate.jpa.test.criteria.Book> book = cb.treat(item, .class);
            query.where(cb.equal(book.get("title"), "La moneta di Akragas"));
            final List<org.hibernate.jpa.test.criteria.Bid> resultList = entityManager.createQuery(query).getResultList();
            assertThat(resultList.size(), is(1));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10561")
    public void testJoinOnTreatedRoot() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Item> criteria = cb.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Item> root = criteria.from(.class);
            Root<org.hibernate.jpa.test.criteria.Book> treatedRoot = cb.treat(root, .class);
            criteria.where(cb.equal(treatedRoot.<org.hibernate.jpa.test.criteria.Book, org.hibernate.jpa.test.criteria.Author>join("author").<String>get("name"), "Andrea Camilleri"));
            entityManager.createQuery(criteria.select(treatedRoot)).getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10561")
    public void testJoinOnTreatedRootWithJoin() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Item> criteria = cb.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Item> root = criteria.from(.class);
            root.join("price");
            Root<org.hibernate.jpa.test.criteria.Book> treatedRoot = cb.treat(root, .class);
            criteria.where(cb.equal(treatedRoot.<org.hibernate.jpa.test.criteria.Book, org.hibernate.jpa.test.criteria.Author>join("author").<String>get("name"), "Andrea Camilleri"));
            entityManager.createQuery(criteria.select(treatedRoot)).getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10767")
    public void testJoinOnTreatedJoin() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Bid> criteria = cb.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Bid> root = criteria.from(.class);
            Join<org.hibernate.jpa.test.criteria.Book, org.hibernate.jpa.test.criteria.Author> join = cb.treat(root.<org.hibernate.jpa.test.criteria.Bid, org.hibernate.jpa.test.criteria.Item>join("item"), .class).join("author");
            criteria.where(cb.equal(join.<String>get("name"), "Andrea Camilleri"));
            entityManager.createQuery(criteria.select(root)).getResultList();
        });
    }

    @Entity(name = "Item")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class Item {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
        private TreatJoinTest.Price price;

        public Item() {
        }

        public Item(TreatJoinTest.Price price) {
            this.price = price;
        }
    }

    @Entity(name = "Price")
    public static class Price {
        @Id
        long id;

        public Price() {
        }

        public Price(int amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        int amount;

        String currency;
    }

    @Entity(name = "Book")
    @Table(name = "BOOK")
    public static class Book extends TreatJoinTest.Item {
        private String title;

        @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
        private TreatJoinTest.Author author;

        public Book() {
        }

        public Book(TreatJoinTest.Author author, String title, TreatJoinTest.Price price) {
            super(price);
            this.author = author;
            this.title = title;
        }
    }

    @Entity(name = "Car")
    @Table(name = "CAR")
    public static class Car extends TreatJoinTest.Item {
        @OneToMany
        private java.util.List<TreatJoinTest.Person> owners;

        String color;
    }

    @Entity(name = "Bid")
    @Table(name = "BID")
    public static class Bid {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
        private TreatJoinTest.Item item;

        public Bid() {
        }

        public Bid(TreatJoinTest.Item item) {
            this.item = item;
        }
    }

    @Entity(name = "Author")
    @Table(name = "AUTHOR")
    public static class Author {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        public Author() {
        }

        public Author(String name) {
            this.name = name;
        }
    }

    @Entity(name = "Person")
    @Table(name = "PERSON")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        private String name;
    }
}

