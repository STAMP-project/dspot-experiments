/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.exception;


import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
@SuppressWarnings("unchecked")
public class ExceptionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testOptimisticLockingException() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        EntityManager em2 = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Music music = new Music();
        music.setName("Old Country");
        em.persist(music);
        em.getTransaction().commit();
        try {
            em2.getTransaction().begin();
            Music music2 = em2.find(Music.class, music.getId());
            music2.setName("HouseMusic");
            em2.getTransaction().commit();
        } catch (Exception e) {
            em2.getTransaction().rollback();
            throw e;
        } finally {
            em2.close();
        }
        em.getTransaction().begin();
        music.setName("Rock");
        try {
            em.flush();
            Assert.fail("Should raise an optimistic lock exception");
        } catch (OptimisticLockException e) {
            // success
            Assert.assertEquals(music, e.getEntity());
        } catch (Exception e) {
            Assert.fail("Should raise an optimistic lock exception");
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    public void testEntityNotFoundException() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        Music music = em.getReference(Music.class, (-1));
        try {
            music.getName();
            Assert.fail("Non existent entity should raise an exception when state is accessed");
        } catch (EntityNotFoundException e) {
            log.debug("success");
        } finally {
            em.close();
        }
    }

    @Test
    public void testConstraintViolationException() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Music music = new Music();
        music.setName("Jazz");
        em.persist(music);
        Musician lui = new Musician();
        lui.setName("Lui Armstrong");
        lui.setFavouriteMusic(music);
        em.persist(lui);
        em.getTransaction().commit();
        try {
            em.getTransaction().begin();
            String hqlDelete = "delete Music where name = :name";
            em.createQuery(hqlDelete).setParameter("name", "Jazz").executeUpdate();
            em.getTransaction().commit();
            Assert.fail();
        } catch (PersistenceException e) {
            Throwable t = e.getCause();
            Assert.assertTrue("Should be a constraint violation", (t instanceof ConstraintViolationException));
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-4676")
    public void testInterceptor() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Instrument instrument = new Instrument();
        instrument.setName("Guitar");
        try {
            em.persist(instrument);
            Assert.fail("Commit should have failed.");
        } catch (RuntimeException e) {
            Assert.assertTrue(em.getTransaction().getRollbackOnly());
        }
        em.close();
    }
}

