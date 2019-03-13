/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.onetomany.inheritance.single;


import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-11005")
public class MappedSuperclassMapTest extends BaseNonConfigCoreFunctionalTestCase {
    private static final String SKU001 = "SKU001";

    private static final String SKU002 = "SKU002";

    private static final String WAR_AND_PEACE = "0140447938";

    private static final String ANNA_KARENINA = "0140449175";

    @Test
    public void lookupEntities() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( sess) -> {
            List<Library> libraries = sess.createQuery("FROM Library").list();
            assertEquals(1, libraries.size());
            Library library = libraries.get(0);
            assertNotNull(library);
            assertEquals(2, library.getBooksOnInventory().size());
            Book book = library.getBooksOnInventory().get(SKU001);
            assertNotNull(book);
            Library Library = library;
            Library.getBooksOnIsbn().get(WAR_AND_PEACE);
            assertEquals(WAR_AND_PEACE, book.getIsbn());
            book = library.getBooksOnInventory().get(SKU002);
            assertNotNull(book);
            assertEquals(ANNA_KARENINA, book.getIsbn());
        });
    }

    @Test
    public void lookupEntities_entrySet() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( sess) -> {
            List<Library> libraries = sess.createQuery("FROM Library").list();
            assertEquals(1, libraries.size());
            Library library = libraries.get(0);
            assertNotNull(library);
            assertEquals(2, library.getBooksOnInventory().size());
            for (Entry<String, Book> entry : library.getBooksOnInventory().entrySet()) {
                log.info(((("Found SKU " + (entry.getKey())) + " with ISBN ") + (entry.getValue().getIsbn())));
            }
        });
    }

    @Test
    public void breakReferences() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( sess) -> {
            List<Book> books = sess.createQuery("FROM Book").list();
            assertEquals(2, books.size());
            for (Book book : books) {
                assertNotNull(book.getLibrary());
                log.info(((("Found SKU " + (book.getInventoryCode())) + " with library ") + (book.getLibrary().getEntid())));
            }
            for (Book book : books) {
                book.getLibrary().removeBook(book);
            }
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( sess) -> {
            List<Book> books = sess.createQuery("FROM Book").list();
            assertEquals(2, books.size());
            for (Book book : books) {
                if ((book.getLibrary()) == null) {
                    log.info((("Found SKU " + (book.getInventoryCode())) + " with no library"));
                }
            }
            List<Library> libraries = sess.createQuery("FROM Library").list();
            assertEquals(1, libraries.size());
            Library library = libraries.get(0);
            assertNotNull(library);
            assertEquals(0, library.getBooksOnInventory().size());
            log.info((("Found Library " + (library.getEntid())) + " with no books"));
        });
    }
}

