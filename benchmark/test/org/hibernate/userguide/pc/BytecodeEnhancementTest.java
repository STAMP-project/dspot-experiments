/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.pc;


import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.LazyGroup;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::BytecodeEnhancement-dirty-tracking-bidirectional-example[]
public class BytecodeEnhancementTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::BytecodeEnhancement-dirty-tracking-bidirectional-incorrect-usage-example[]
            org.hibernate.userguide.pc.Person person = new org.hibernate.userguide.pc.Person();
            person.setName("John Doe");
            org.hibernate.userguide.pc.Book book = new org.hibernate.userguide.pc.Book();
            person.getBooks().add(book);
            try {
                book.getAuthor().getName();
            } catch ( expected) {
                // This blows up ( NPE ) in normal Java usage
            }
            // end::BytecodeEnhancement-dirty-tracking-bidirectional-incorrect-usage-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::BytecodeEnhancement-dirty-tracking-bidirectional-correct-usage-example[]
            org.hibernate.userguide.pc.Person person = new org.hibernate.userguide.pc.Person();
            person.setName("John Doe");
            org.hibernate.userguide.pc.Book book = new org.hibernate.userguide.pc.Book();
            person.getBooks().add(book);
            book.setAuthor(person);
            book.getAuthor().getName();
            // end::BytecodeEnhancement-dirty-tracking-bidirectional-correct-usage-example[]
        });
    }

    // tag::BytecodeEnhancement-lazy-loading-example[]
    // tag::BytecodeEnhancement-lazy-loading-example[]
    @Entity
    public class Customer {
        @Id
        private Integer id;

        private String name;

        @Basic(fetch = FetchType.LAZY)
        private UUID accountsPayableXrefId;

        @Lob
        @Basic(fetch = FetchType.LAZY)
        @LazyGroup("lobs")
        private Blob image;

        // Getters and setters are omitted for brevity
        // end::BytecodeEnhancement-lazy-loading-example[]
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UUID getAccountsPayableXrefId() {
            return accountsPayableXrefId;
        }

        public void setAccountsPayableXrefId(UUID accountsPayableXrefId) {
            this.accountsPayableXrefId = accountsPayableXrefId;
        }

        public Blob getImage() {
            return image;
        }

        public void setImage(Blob image) {
            this.image = image;
        }
    }

    // end::BytecodeEnhancement-lazy-loading-example[]
    // tag::BytecodeEnhancement-dirty-tracking-bidirectional-example[]
    // tag::BytecodeEnhancement-dirty-tracking-bidirectional-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        @OneToMany(mappedBy = "author")
        private List<BytecodeEnhancementTest.Book> books = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::BytecodeEnhancement-dirty-tracking-bidirectional-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<BytecodeEnhancementTest.Book> getBooks() {
            return books;
        }
    }

    // tag::BytecodeEnhancement-dirty-tracking-bidirectional-example[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        private Long id;

        private String title;

        @NaturalId
        private String isbn;

        @ManyToOne
        private BytecodeEnhancementTest.Person author;

        // Getters and setters are omitted for brevity
        // end::BytecodeEnhancement-dirty-tracking-bidirectional-example[]
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

        public BytecodeEnhancementTest.Person getAuthor() {
            return author;
        }

        public void setAuthor(BytecodeEnhancementTest.Person author) {
            this.author = author;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }
    }
}

