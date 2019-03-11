/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.pc;


import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.PersistenceUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.NaturalId;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::pc-find-by-natural-id-entity-example[]
public class PersistenceContextTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-unwrap-example[]
            Session session = entityManager.unwrap(.class);
            SessionImplementor sessionImplementor = entityManager.unwrap(.class);
            SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(.class);
            // end::pc-unwrap-example[]
        });
        Long _personId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createQuery("delete from Book").executeUpdate();
            entityManager.createQuery("delete from Person").executeUpdate();
            // tag::pc-persist-jpa-example[]
            org.hibernate.userguide.pc.Person person = new org.hibernate.userguide.pc.Person();
            person.setId(1L);
            person.setName("John Doe");
            entityManager.persist(person);
            // end::pc-persist-jpa-example[]
            // tag::pc-remove-jpa-example[]
            entityManager.remove(person);
            // end::pc-remove-jpa-example[]
            entityManager.persist(person);
            Long personId = person.getId();
            // tag::pc-get-reference-jpa-example[]
            org.hibernate.userguide.pc.Book book = new org.hibernate.userguide.pc.Book();
            book.setAuthor(entityManager.getReference(.class, personId));
            // end::pc-get-reference-jpa-example[]
            return personId;
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long personId = _personId;
            // tag::pc-find-jpa-example[]
            org.hibernate.userguide.pc.Person person = entityManager.find(.class, personId);
            // end::pc-find-jpa-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            entityManager.createQuery("delete from Book").executeUpdate();
            entityManager.createQuery("delete from Person").executeUpdate();
            // tag::pc-persist-native-example[]
            org.hibernate.userguide.pc.Person person = new org.hibernate.userguide.pc.Person();
            person.setId(1L);
            person.setName("John Doe");
            session.save(person);
            // end::pc-persist-native-example[]
            // tag::pc-remove-native-example[]
            session.delete(person);
            // end::pc-remove-native-example[]
            session.save(person);
            Long personId = person.getId();
            // tag::pc-get-reference-native-example[]
            org.hibernate.userguide.pc.Book book = new org.hibernate.userguide.pc.Book();
            book.setId(1L);
            book.setIsbn("123-456-7890");
            entityManager.persist(book);
            book.setAuthor(session.load(.class, personId));
            // end::pc-get-reference-native-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            Long personId = _personId;
            // tag::pc-find-native-example[]
            org.hibernate.userguide.pc.Person person = session.get(.class, personId);
            // end::pc-find-native-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            Long personId = _personId;
            // tag::pc-find-by-id-native-example[]
            org.hibernate.userguide.pc.Person person = session.byId(.class).load(personId);
            // end::pc-find-by-id-native-example[]
            // tag::pc-find-optional-by-id-native-example[]
            Optional<org.hibernate.userguide.pc.Person> optionalPerson = session.byId(.class).loadOptional(personId);
            // end::pc-find-optional-by-id-native-example[]
            String isbn = "123-456-7890";
            // tag::pc-find-by-simple-natural-id-example[]
            org.hibernate.userguide.pc.Book book = session.bySimpleNaturalId(.class).getReference(isbn);
            // end::pc-find-by-simple-natural-id-example[]
            assertNotNull(book);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            String isbn = "123-456-7890";
            // tag::pc-find-by-natural-id-example[]
            org.hibernate.userguide.pc.Book book = session.byNaturalId(.class).using("isbn", isbn).load();
            // end::pc-find-by-natural-id-example[]
            assertNotNull(book);
            // tag::pc-find-optional-by-simple-natural-id-example[]
            Optional<org.hibernate.userguide.pc.Book> optionalBook = session.byNaturalId(.class).using("isbn", isbn).loadOptional();
            // end::pc-find-optional-by-simple-natural-id-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long personId = _personId;
            // tag::pc-managed-state-jpa-example[]
            org.hibernate.userguide.pc.Person person = entityManager.find(.class, personId);
            person.setName("John Doe");
            entityManager.flush();
            // end::pc-managed-state-jpa-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long personId = _personId;
            // tag::pc-refresh-jpa-example[]
            org.hibernate.userguide.pc.Person person = entityManager.find(.class, personId);
            entityManager.createQuery("update Person set name = UPPER(name)").executeUpdate();
            entityManager.refresh(person);
            assertEquals("JOHN DOE", person.getName());
            // end::pc-refresh-jpa-example[]
        });
        try {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Long personId = _personId;
                // tag::pc-refresh-child-entity-jpa-example[]
                try {
                    org.hibernate.userguide.pc.Person person = entityManager.find(.class, personId);
                    org.hibernate.userguide.pc.Book book = new org.hibernate.userguide.pc.Book();
                    book.setId(100L);
                    book.setTitle("Hibernate User Guide");
                    book.setAuthor(person);
                    person.getBooks().add(book);
                    entityManager.refresh(person);
                } catch ( expected) {
                    log.info("Beware when cascading the refresh associations to transient entities!");
                }
                // end::pc-refresh-child-entity-jpa-example[]
            });
        } catch (Exception expected) {
        }
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            Long personId = _personId;
            // tag::pc-managed-state-native-example[]
            org.hibernate.userguide.pc.Person person = session.byId(.class).load(personId);
            person.setName("John Doe");
            session.flush();
            // end::pc-managed-state-native-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            Long personId = _personId;
            // tag::pc-refresh-native-example[]
            org.hibernate.userguide.pc.Person person = session.byId(.class).load(personId);
            session.doWork(( connection) -> {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("UPDATE Person SET name = UPPER(name)");
                }
            });
            session.refresh(person);
            assertEquals("JOHN DOE", person.getName());
            // end::pc-refresh-native-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            Long personId = _personId;
            // tag::pc-detach-reattach-lock-example[]
            org.hibernate.userguide.pc.Person person = session.byId(.class).load(personId);
            // Clear the Session so the person entity becomes detached
            session.clear();
            person.setName("Mr. John Doe");
            session.lock(person, LockMode.NONE);
            // end::pc-detach-reattach-lock-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            Long personId = _personId;
            // tag::pc-detach-reattach-saveOrUpdate-example[]
            org.hibernate.userguide.pc.Person person = session.byId(.class).load(personId);
            // Clear the Session so the person entity becomes detached
            session.clear();
            person.setName("Mr. John Doe");
            session.saveOrUpdate(person);
            // end::pc-detach-reattach-saveOrUpdate-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            Long personId = _personId;
            org.hibernate.userguide.pc.Person personDetachedReference = session.byId(.class).load(personId);
            // Clear the Session so the person entity becomes detached
            session.clear();
            new org.hibernate.userguide.pc.MergeVisualizer(session).merge(personDetachedReference);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long personId = _personId;
            // tag::pc-merge-jpa-example[]
            org.hibernate.userguide.pc.Person person = entityManager.find(.class, personId);
            // Clear the EntityManager so the person entity becomes detached
            entityManager.clear();
            person.setName("Mr. John Doe");
            person = entityManager.merge(person);
            // end::pc-merge-jpa-example[]
            // tag::pc-contains-jpa-example[]
            boolean contained = entityManager.contains(person);
            // end::pc-contains-jpa-example[]
            assertTrue(contained);
            // tag::pc-verify-lazy-jpa-example[]
            PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
            boolean personInitialized = persistenceUnitUtil.isLoaded(person);
            boolean personBooksInitialized = persistenceUnitUtil.isLoaded(person.getBooks());
            boolean personNameInitialized = persistenceUnitUtil.isLoaded(person, "name");
            // end::pc-verify-lazy-jpa-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long personId = _personId;
            org.hibernate.userguide.pc.Person person = entityManager.find(.class, personId);
            // tag::pc-verify-lazy-jpa-alternative-example[]
            PersistenceUtil persistenceUnitUtil = Persistence.getPersistenceUtil();
            boolean personInitialized = persistenceUnitUtil.isLoaded(person);
            boolean personBooksInitialized = persistenceUnitUtil.isLoaded(person.getBooks());
            boolean personNameInitialized = persistenceUnitUtil.isLoaded(person, "name");
            // end::pc-verify-lazy-jpa-alternative-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            Long personId = _personId;
            // tag::pc-merge-native-example[]
            org.hibernate.userguide.pc.Person person = session.byId(.class).load(personId);
            // Clear the Session so the person entity becomes detached
            session.clear();
            person.setName("Mr. John Doe");
            person = ((org.hibernate.userguide.pc.Person) (session.merge(person)));
            // end::pc-merge-native-example[]
            // tag::pc-contains-native-example[]
            boolean contained = session.contains(person);
            // end::pc-contains-native-example[]
            assertTrue(contained);
            // tag::pc-verify-lazy-native-example[]
            boolean personInitialized = Hibernate.isInitialized(person);
            boolean personBooksInitialized = Hibernate.isInitialized(person.getBooks());
            boolean personNameInitialized = Hibernate.isPropertyInitialized(person, "name");
            // end::pc-verify-lazy-native-example[]
        });
    }

    // end::pc-merge-visualize-example[]
    public static class MergeVisualizer {
        private final Session session;

        public MergeVisualizer(Session session) {
            this.session = session;
        }

        // tag::pc-merge-visualize-example[]
        public PersistenceContextTest.Person merge(PersistenceContextTest.Person detached) {
            PersistenceContextTest.Person newReference = session.byId(PersistenceContextTest.Person.class).load(detached.getId());
            newReference.setName(detached.getName());
            return newReference;
        }
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
        private List<PersistenceContextTest.Book> books = new ArrayList<>();

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

        public List<PersistenceContextTest.Book> getBooks() {
            return books;
        }
    }

    // tag::pc-find-by-natural-id-entity-example[]
    // tag::pc-find-by-natural-id-entity-example[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        private Long id;

        private String title;

        @NaturalId
        private String isbn;

        @ManyToOne
        private PersistenceContextTest.Person author;

        // Getters and setters are omitted for brevity
        // end::pc-find-by-natural-id-entity-example[]
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

        public PersistenceContextTest.Person getAuthor() {
            return author;
        }

        public void setAuthor(PersistenceContextTest.Person author) {
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

