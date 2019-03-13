/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.hql;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Tuple;
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
public class PostgreSQLFunctionSelectClauseTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testHibernateSelectClauseFunction() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::hql-user-defined-function-postgresql-select-clause-example[]
            List<Tuple> books = entityManager.createQuery(("select b.title as title, apply_vat(b.priceCents) as price " + ("from Book b " + "where b.author = :author ")), .class).setParameter("author", "Vlad Mihalcea").getResultList();
            assertEquals(1, books.size());
            Tuple book = books.get(0);
            assertEquals("High-Performance Java Persistence", book.get("title"));
            assertEquals(5400, ((Number) (book.get("price"))).intValue());
            // end::hql-user-defined-function-postgresql-select-clause-example[]
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

