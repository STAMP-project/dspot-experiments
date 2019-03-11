/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.hql;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(PostgreSQL82Dialect.class)
public class PostgreSQLFunctionWhereClauseTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testHibernatePassThroughFunction() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::hql-user-defined-function-postgresql-where-clause-example[]
            List<org.hibernate.userguide.hql.Book> books = entityManager.createQuery(("select b " + ("from Book b " + "where apply_vat(b.priceCents) = :price ")), .class).setParameter("price", 5400).getResultList();
            assertTrue(books.stream().filter(( book) -> "High-Performance Java Persistence".equals(book.getTitle())).findAny().isPresent());
            // end::hql-user-defined-function-postgresql-where-clause-example[]
        });
    }

    @Test
    public void testCustomFunction() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::hql-user-defined-function-postgresql-jpql-example[]
            List<org.hibernate.userguide.hql.Book> books = entityManager.createQuery(("select b " + ("from Book b " + "where function('apply_vat', b.priceCents) = :price ")), .class).setParameter("price", 5400).getResultList();
            assertTrue(books.stream().filter(( book) -> "High-Performance Java Persistence".equals(book.getTitle())).findAny().isPresent());
            // end::hql-user-defined-function-postgresql-jpql-example[]
        });
    }

    @Entity(name = "Book")
    public static class Book {
        @Id
        private String isbn;

        private String title;

        private String author;

        private Integer priceCents;

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public Integer getPriceCents() {
            return priceCents;
        }

        public void setPriceCents(Integer priceCents) {
            this.priceCents = priceCents;
        }
    }
}

