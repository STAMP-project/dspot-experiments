/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::identifiers-generators-custom-uuid-mapping-example[]
public class UuidCustomGeneratedValueTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        UuidCustomGeneratedValueTest.Book book = new UuidCustomGeneratedValueTest.Book();
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor("Vlad Mihalcea");
            entityManager.persist(book);
        });
        Assert.assertNotNull(book.getId());
    }

    // tag::identifiers-generators-custom-uuid-mapping-example[]
    // tag::identifiers-generators-custom-uuid-mapping-example[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        @GeneratedValue(generator = "custom-uuid")
        @GenericGenerator(name = "custom-uuid", strategy = "org.hibernate.id.UUIDGenerator", parameters = { @Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
        private UUID id;

        private String title;

        private String author;

        // Getters and setters are omitted for brevity
        // end::identifiers-generators-custom-uuid-mapping-example[]
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

