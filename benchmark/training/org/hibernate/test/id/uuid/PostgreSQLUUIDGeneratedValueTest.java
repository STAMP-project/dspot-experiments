/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.uuid;


import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(PostgreSQL82Dialect.class)
public class PostgreSQLUUIDGeneratedValueTest extends BaseEntityManagerFunctionalTestCase {
    private PostgreSQLUUIDGeneratedValueTest.Book book;

    @Test
    public void testJPQL() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<UUID> books = entityManager.createQuery(("select b.id " + ("from Book b " + "where b.id = :id"))).setParameter("id", book.id).getResultList();
            assertEquals(1, books.size());
        });
    }

    @Test
    public void testNativeSQL() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<UUID> books = entityManager.createNativeQuery(("select b.id as id " + ("from Book b " + "where b.id = :id"))).setParameter("id", book.id).unwrap(.class).addScalar("id", PostgresUUIDType.INSTANCE).getResultList();
            assertEquals(1, books.size());
        });
    }

    @Entity(name = "Book")
    public static class Book {
        @Id
        @GeneratedValue
        private UUID id;

        private String title;

        private String author;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
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
    }
}

