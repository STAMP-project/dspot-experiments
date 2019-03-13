/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.hql;


import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class SelectDistinctTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testDistinctProjection() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::hql-distinct-projection-query-example[]
            List<String> lastNames = entityManager.createQuery(("select distinct p.lastName " + "from Person p"), .class).getResultList();
            // end::hql-distinct-projection-query-example[]
            assertTrue(((((lastNames.size()) == 2) && (lastNames.contains("King"))) && (lastNames.contains("Mihalcea"))));
        });
    }

    @Test
    public void testAllAuthors() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.userguide.hql.Person> authors = entityManager.createQuery(("select p " + ("from Person p " + "left join fetch p.books")), .class).getResultList();
            authors.forEach(( author) -> {
                log.infof("Author %s wrote %d books", (((author.getFirstName()) + " ") + (author.getLastName())), author.getBooks().size());
            });
        });
    }

    @Test
    public void testDistinctAuthors() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::hql-distinct-entity-query-example[]
            List<org.hibernate.userguide.hql.Person> authors = entityManager.createQuery(("select distinct p " + ("from Person p " + "left join fetch p.books")), .class).getResultList();
            // end::hql-distinct-entity-query-example[]
            authors.forEach(( author) -> {
                log.infof("Author %s wrote %d books", (((author.getFirstName()) + " ") + (author.getLastName())), author.getBooks().size());
            });
        });
    }

    @Test
    public void testDistinctAuthorsWithoutPassThrough() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::hql-distinct-entity-query-hint-example[]
            List<org.hibernate.userguide.hql.Person> authors = entityManager.createQuery(("select distinct p " + ("from Person p " + "left join fetch p.books")), .class).setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false).getResultList();
            // end::hql-distinct-entity-query-hint-example[]
            authors.forEach(( author) -> {
                log.infof("Author %s wrote %d books", (((author.getFirstName()) + " ") + (author.getLastName())), author.getBooks().size());
            });
        });
    }

    @Entity(name = "Person")
    @Table(name = "person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "first_name")
        private String firstName;

        @Column(name = "last_name")
        private String lastName;

        @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
        private java.util.List<SelectDistinctTest.Book> books = new ArrayList<>();

        public Person() {
        }

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public java.util.List<SelectDistinctTest.Book> getBooks() {
            return books;
        }

        public void addBook(SelectDistinctTest.Book book) {
            books.add(book);
            book.setAuthor(this);
        }
    }

    @Entity(name = "Book")
    @Table(name = "book")
    public static class Book {
        @Id
        @GeneratedValue
        private Long id;

        private String title;

        @ManyToOne
        private SelectDistinctTest.Person author;

        public Book() {
        }

        public Book(String title) {
            this.title = title;
        }

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

        public SelectDistinctTest.Person getAuthor() {
            return author;
        }

        public void setAuthor(SelectDistinctTest.Person author) {
            this.author = author;
        }
    }
}

