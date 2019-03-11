/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.dialect.MariaDB103Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12973")
@RequiresDialect(MariaDB103Dialect.class)
public class SequenceInformationMariaDBTest extends BaseEntityManagerFunctionalTestCase {
    private DriverManagerConnectionProviderImpl connectionProvider;

    @Test
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.dialect.functional.Book book = new org.hibernate.test.dialect.functional.Book();
            book.setTitle("My Book");
            entityManager.persist(book);
        });
    }

    @Entity
    @Table(name = "TBL_BOOK")
    public static class Book {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @SequenceGenerator(name = "book_sequence", allocationSize = 10)
        @Column(name = "ID")
        private Integer id;

        @Column(name = "TITLE")
        private String title;

        public Book() {
        }

        public Book(String title) {
            this.title = title;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return ((((("Book{" + "id=") + (id)) + ", title='") + (title)) + '\'') + '}';
        }
    }

    @Entity
    @Table(name = "TBL_AUTHOR")
    public static class Author {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @SequenceGenerator(name = "author_sequence", allocationSize = 10)
        @Column(name = "ID")
        private Integer id;

        @Column(name = "FIRST_NAME")
        private String firstName;

        @Column(name = "LAST_NAME")
        private String lastName;
    }
}

