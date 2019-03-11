/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.userguide.util;


import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class IsLoadedTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testIsLoadedOnPrivateSuperclassProperty() {
        EntityManager em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        try {
            Author a = new Author();
            Book book = new Book(a);
            em.persist(a);
            em.persist(book);
            em.flush();
            em.clear();
            book = em.find(Book.class, book.getId());
            Assert.assertTrue(em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(book));
            Assert.assertFalse(em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(book, "author"));
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }
}

