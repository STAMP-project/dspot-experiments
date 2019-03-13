/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


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
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Christian Beikov
 */
public class JoinOnClauseTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11437")
    public void testOnClauseUsesSuperclassAttribute() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.test.hql.Book> result = entityManager.createQuery("SELECT DISTINCT b1 FROM Book b1 JOIN Book b2 ON b1.price = b2.price", .class).getResultList();
            assertEquals(2, result.size());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11435")
    @FailureExpected(jiraKey = "HHH-11435")
    public void testOnClauseUsesNonDrivingTableAlias() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            try {
                entityManager.createQuery("SELECT b1 FROM Book b1 JOIN Book b2 ON b1.author = author2 LEFT JOIN b2.author author2");
                fail("Referring to a join alias in the on clause that is joined later should be invalid!");
            } catch ( ex) {
                // TODO: Assert it fails due to the alias not being defined
            }
        });
    }

    @Entity(name = "Item")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class Item {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
        private JoinOnClauseTest.Price price;

        public Item() {
        }

        public Item(JoinOnClauseTest.Price price) {
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
    public static class Book extends JoinOnClauseTest.Item {
        private String title;

        @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
        private JoinOnClauseTest.Author author;

        public Book() {
        }

        public Book(JoinOnClauseTest.Author author, String title, JoinOnClauseTest.Price price) {
            super(price);
            this.author = author;
            this.title = title;
        }
    }

    @Entity(name = "Car")
    @Table(name = "CAR")
    public static class Car extends JoinOnClauseTest.Item {
        @OneToMany
        private java.util.List<JoinOnClauseTest.Person> owners;

        String color;
    }

    @Entity(name = "Bid")
    @Table(name = "BID")
    public static class Bid {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
        private JoinOnClauseTest.Item item;

        public Bid() {
        }

        public Bid(JoinOnClauseTest.Item item) {
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

