/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.NaturalId;
import org.hibernate.query.Query;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Jan-Willem Gmelig Meyling
 * @author Christian Beikov
 */
public class NaturalIdDereferenceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void naturalIdDereferenceTest() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r.normalBook.isbn FROM BookRef r");
            List resultList = query.getResultList();
            assertFalse(resultList.isEmpty());
            assertEquals(1, getSQLJoinCount(query));
        });
    }

    @Test
    public void normalIdDereferenceFromAlias() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r.normalBook.id FROM BookRef r");
            List resultList = query.getResultList();
            assertFalse(resultList.isEmpty());
            assertEquals(0, getSQLJoinCount(query));
        });
    }

    @Test
    public void naturalIdDereferenceFromAlias() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r.naturalBook.isbn FROM BookRef r");
            List resultList = query.getResultList();
            assertFalse(resultList.isEmpty());
            assertEquals(0, getSQLJoinCount(query));
        });
    }

    @Test
    public void normalIdDereferenceFromImplicitJoin() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r2.normalBookRef.normalBook.id FROM BookRefRef r2");
            query.getResultList();
            assertEquals(1, getSQLJoinCount(query));
        });
    }

    @Test
    public void naturalIdDereferenceFromImplicitJoin() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r2.normalBookRef.naturalBook.isbn FROM BookRefRef r2");
            query.getResultList();
            assertEquals(1, getSQLJoinCount(query));
        });
    }

    /**
     * Due to the construction of the mapping for {@link BookRefRef#naturalBookRef}, the {@code isbn} column maps
     * to both the referenced {@link BookRef} and {@link Book}. As such, {@code r2.naturalBookRef.naturalBook.isbn}
     * can be dereferenced without a single join.
     */
    @Test
    public void nestedNaturalIdDereferenceFromImplicitJoin() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r2.naturalBookRef.naturalBook.isbn FROM BookRefRef r2");
            query.getResultList();
            assertEquals(0, getSQLJoinCount(query));
        });
    }

    /**
     * Adjustment of {@link #nestedNaturalIdDereferenceFromImplicitJoin()}, that instead selects the {@code id} property,
     * which requires a single join to {@code Book}.
     */
    @Test
    public void nestedNaturalIdDereferenceFromImplicitJoin2() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r2.naturalBookRef.naturalBook.id FROM BookRefRef r2");
            query.getResultList();
            assertEquals(1, getSQLJoinCount(query));
        });
    }

    @Test
    public void doNotDereferenceNaturalIdIfIsReferenceToPrimaryKey() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r2.normalBookRef.normalBook.isbn FROM BookRefRef r2");
            query.getResultList();
            assertEquals(2, getSQLJoinCount(query));
        });
    }

    @Test
    public void selectedEntityIsNotDereferencedForPrimaryKey() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r2.normalBookRef.normalBook FROM BookRefRef r2");
            query.getResultList();
            assertEquals(2, getSQLJoinCount(query));
        });
    }

    /**
     * BookRefRef can be joined directly with Book due to the construction of the isbn key.
     * <p>
     * I.e.
     * <p>
     * BookRefRef{isbn=abcd} enforces BookRef{isbn=abc} (FK) enforces BookRef{isbn=abc} (FK),
     * so bookRefRef.naturalBookRef.naturalBook = Book{isbn=abc}.
     * <p>
     * BookRefRef{isbn=null}, i.e. no BookRef for this BookRefRef, and as such no book,
     * so bookRefRef.naturalBookRef.naturalBook yields null which is expected.
     */
    @Test
    public void selectedEntityIsNotDereferencedForNaturalId() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r2.naturalBookRef.naturalBook FROM BookRefRef r2");
            query.getResultList();
            assertEquals(1, getSQLJoinCount(query));
        });
    }

    /**
     * {@code r2.normalBookRef.normalBook.id} requires 1 join as seen in {@link #normalIdDereferenceFromImplicitJoin}.
     * {@code r3.naturalBookRef.naturalBook.isbn} requires 1 join as seen in {@link #selectedEntityIsNotDereferencedForNaturalId()}.
     * An additional join is added to join BookRef once more on {@code r2.normalBookRef.normalBook.isbn = r3.naturalBookRef.naturalBook.isbn}.
     * This results in three joins in total.
     */
    @Test
    public void dereferenceNaturalIdInJoin() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery(("SELECT r2.normalBookRef.normalBook.id, r3.naturalBookRef.naturalBook.isbn " + "FROM BookRefRef r2 JOIN BookRefRef r3 ON r2.normalBookRef.normalBook.isbn = r3.naturalBookRef.naturalBook.isbn"));
            query.getResultList();
            // r2.normalBookRef.normalBook.id requires
            assertEquals(3, getSQLJoinCount(query));
        });
    }

    /**
     * {@code BookRefRef} is joined with {@code BookRef} on {@code b.naturalBook.isbn = a.naturalBookRef.naturalBook.isbn}.
     * {@code b.naturalBook.isbn} can be dereferenced without any join ({@link #naturalIdDereferenceFromAlias()}).
     * {@code a.naturalBookRef.naturalBook.isbn} can be dereferenced without any join ({@link #nestedNaturalIdDereferenceFromImplicitJoin()}).
     * We finally select all properties of {@code b.normalBook}, which requires {@code Book} to be joined.
     * This results in two joins in total.
     */
    @Test
    public void dereferenceNaturalIdInJoin2() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery(("SELECT b.normalBook FROM BookRefRef a " + "JOIN BookRef b ON b.naturalBook.isbn = a.naturalBookRef.naturalBook.isbn"));
            query.getResultList();
            assertEquals(2, getSQLJoinCount(query));
        });
    }

    /**
     * The {@link BookRef#normalBook} is joined with {@code BookRef} on {@code join.isbn = from.normalBook.isbn}.
     * {@code join.isbn = from.normalBook.isbn} both dereference to {@code join.isbn}.
     * {@code r.normalBook.isbn} dereferences to {@code join.isbn}.
     * As a result, only a single join is required.
     */
    @Test
    public void dereferenceNaturalIdInJoin3() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r.normalBook.isbn FROM BookRef r JOIN r.normalBook b ON b.isbn = r.normalBook.isbn");
            query.getResultList();
            assertEquals(1, getSQLJoinCount(query));
        });
    }

    /**
     * The {@link Book} is joined with {@link BookRef} on {@code book.isbn = ref.normalBook.isbn}.
     * {@code book.isbn} can be dereferenced from the {@code Book} table.
     * {@code ref.normalBook.isbn} requires an implicit join with book.
     * {@code ref.normalBook.isbn} in the final selection is available due to the aforementioned join.
     * As a result, 2 joins are required.
     */
    @Test
    public void dereferenceNaturalIdInJoin4() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery("SELECT r.normalBook.isbn FROM BookRef r JOIN Book b ON b.isbn = r.normalBook.isbn");
            query.getResultList();
            assertEquals(2, getSQLJoinCount(query));
        });
    }

    @Entity(name = "Book")
    @Table(name = "book")
    public static class Book {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @NaturalId
        @Column(name = "isbn", unique = true)
        private String isbn;
    }

    @Entity(name = "BookRef")
    @Table(name = "bookref")
    public static class BookRef {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @ManyToOne(optional = true)
        @JoinColumn(nullable = true, columnDefinition = "id_ref")
        private NaturalIdDereferenceTest.Book normalBook;

        @ManyToOne
        @JoinColumn(name = "isbn_ref", referencedColumnName = "isbn")
        private NaturalIdDereferenceTest.Book naturalBook;
    }

    @Entity(name = "BookRefRef")
    @Table(name = "bookrefref")
    public static class BookRefRef {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @ManyToOne
        @JoinColumn(nullable = true, columnDefinition = "id_ref_ref")
        private NaturalIdDereferenceTest.BookRef normalBookRef;

        @ManyToOne
        @JoinColumn(name = "isbn_ref_Ref", referencedColumnName = "isbn_ref")
        private NaturalIdDereferenceTest.BookRef naturalBookRef;
    }
}

