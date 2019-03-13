/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.hashcode;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11063")
public class SetHashcodeChangeTest extends BaseEnversJPAFunctionalTestCase {
    private Integer authorId;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            final SetHashcodeChangeTest.Author author = new SetHashcodeChangeTest.Author();
            author.setFirstName("TestFirstName");
            author.setLastName("lastName");
            author.addBook(createBook1());
            author.addBook(createBook2());
            entityManager.persist(author);
            authorId = author.getId();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            entityManager.close();
        }
        // Revision 2
        // Removes all books and re-adds original 2 plus one new book
        entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            final SetHashcodeChangeTest.Author author = entityManager.find(SetHashcodeChangeTest.Author.class, authorId);
            author.removeAllBooks();
            author.addBook(createBook1());
            author.addBook(createBook2());
            author.addBook(createBook3());
            entityManager.merge(author);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            entityManager.close();
        }
    }

    // tests that Author has 3 books.
    @Test
    public void testAuthorState() {
        EntityManager entityManager = getEntityManager();
        try {
            final SetHashcodeChangeTest.Author author = entityManager.find(SetHashcodeChangeTest.Author.class, authorId);
            Assert.assertNotNull(author);
            Assert.assertEquals(3, author.getBooks().size());
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void testAuthorLastRevision() {
        // tests that Author has 3 books, Book1, Book2, and Book3.
        // where Book1 and Book2 were removed and re-added with the addition of Book3.
        EntityManager entityManager = getEntityManager();
        try {
            final AuditReader reader = getAuditReader();
            final List<Number> revisions = reader.getRevisions(SetHashcodeChangeTest.Author.class, authorId);
            final Number lastRevision = revisions.get(((revisions.size()) - 1));
            final SetHashcodeChangeTest.Author author = ((SetHashcodeChangeTest.Author) (reader.createQuery().forEntitiesAtRevision(SetHashcodeChangeTest.Author.class, lastRevision).getSingleResult()));
            Assert.assertNotNull(author);
            Assert.assertEquals(3, author.getBooks().size());
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            entityManager.close();
        }
    }

    @Entity(name = "Author")
    @Audited
    public static class Author {
        @Id
        @GeneratedValue
        private Integer id;

        private String firstName;

        private String lastName;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "author")
        private Set<SetHashcodeChangeTest.Book> books;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
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

        public Set<SetHashcodeChangeTest.Book> getBooks() {
            return books;
        }

        public void setBooks(Set<SetHashcodeChangeTest.Book> books) {
            this.books = books;
        }

        public void addBook(SetHashcodeChangeTest.Book book) {
            if ((this.books) == null) {
                this.books = new HashSet<SetHashcodeChangeTest.Book>();
            }
            book.setAuthor(this);
            this.books.add(book);
        }

        public void removeAllBooks() {
            if ((this.books) != null) {
                this.books.clear();
            }
        }

        public SetHashcodeChangeTest.Book getBook(String title) {
            return books.stream().filter(( b) -> title.equals(b.getTitle())).findFirst().orElse(null);
        }

        public void removeBook(String title) {
            for (Iterator<SetHashcodeChangeTest.Book> it = books.iterator(); it.hasNext();) {
                SetHashcodeChangeTest.Book book = it.next();
                if (title.equals(title)) {
                    it.remove();
                }
            }
        }

        @Override
        public String toString() {
            return (((((((("Author{" + "id=") + (id)) + ", firstName='") + (firstName)) + '\'') + ", lastName='") + (lastName)) + '\'') + '}';
        }
    }

    @Entity(name = "Book")
    @Audited
    public static class Book {
        @Id
        @GeneratedValue
        private Integer id;

        private String title;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinTable(name = "author_book", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id", nullable = false))
        @NotAudited
        private SetHashcodeChangeTest.Author author;

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

        public SetHashcodeChangeTest.Author getAuthor() {
            return author;
        }

        public void setAuthor(SetHashcodeChangeTest.Author author) {
            this.author = author;
        }

        @Override
        public int hashCode() {
            return Objects.hash(title);
        }

        @Override
        public boolean equals(Object object) {
            if ((this) == object) {
                return true;
            }
            if ((object == null) || ((getClass()) != (object.getClass()))) {
                return false;
            }
            SetHashcodeChangeTest.Book book = ((SetHashcodeChangeTest.Book) (object));
            return Objects.equals(title, book.title);
        }

        @Override
        public String toString() {
            return ((((((("Book{" + "id=") + (id)) + ", title='") + (title)) + '\'') + ", author=") + (author)) + '}';
        }
    }
}

