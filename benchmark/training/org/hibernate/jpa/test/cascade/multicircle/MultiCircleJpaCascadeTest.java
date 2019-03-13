/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.cascade.multicircle;


import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import org.hibernate.TransientPropertyValueException;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test uses a complicated model that requires Hibernate to delay
 * inserts until non-nullable transient entity dependencies are resolved.
 *
 * All IDs are generated from a sequence.
 *
 * JPA cascade types are used (javax.persistence.CascadeType)..
 *
 * This test uses the following model:
 *
 * <code>
 *     ------------------------------ N G
 *     |
 *     |                                1
 *     |                                |
 *     |                                |
 *     |                                N
 *     |
 *     |         E N--------------0,1 * F
 *     |
 *     |         1                      N
 *     |         |                      |
 *     |         |                      |
 *     1         N                      |
 *     *                                |
 *     B * N---1 D * 1------------------
 *     *
 *     N         N
 *     |         |
 *     |         |
 *     1         |
 *               |
 *     C * 1-----
 * </code>
 *
 * In the diagram, all associations are bidirectional;
 * assocations marked with '*' cascade persist, save, merge operations to the
 * associated entities (e.g., B cascades persist to D, but D does not cascade
 * persist to B);
 *
 * b, c, d, e, f, and g are all transient unsaved that are associated with each other.
 *
 * When saving b, the entities are added to the ActionQueue in the following order:
 * c, d (depends on e), f (depends on d, g), e, b, g.
 *
 * Entities are inserted in the following order:
 * c, e, d, b, g, f.
 */
public class MultiCircleJpaCascadeTest extends BaseEntityManagerFunctionalTestCase {
    private B b;

    private C c;

    private D d;

    private E e;

    private F f;

    private G g;

    private boolean skipCleanup;

    @Test
    public void testPersist() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(b);
        em.getTransaction().commit();
        em.close();
        check();
    }

    @Test
    public void testPersistNoCascadeToTransient() {
        skipCleanup = true;
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(c);
            Assert.fail("should have failed.");
        } catch (IllegalStateException ex) {
            Assert.assertTrue(TransientPropertyValueException.class.isInstance(ex.getCause()));
            TransientPropertyValueException pve = ((TransientPropertyValueException) (ex.getCause()));
            Assert.assertEquals(G.class.getName(), pve.getTransientEntityName());
            Assert.assertEquals(F.class.getName(), pve.getPropertyOwnerEntityName());
            Assert.assertEquals("g", pve.getPropertyName());
        }
        em.getTransaction().rollback();
        em.close();
    }

    // fails on d.e; should pass
    @Test
    @FailureExpected(jiraKey = "HHH-6999")
    public void testPersistThenUpdate() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(b);
        // remove old e from associations
        e.getDCollection().remove(d);
        d.setE(null);
        f.getECollection().remove(e);
        e.setF(null);
        // add new e to associations
        e = new E();
        e.getDCollection().add(d);
        f.getECollection().add(e);
        d.setE(e);
        e.setF(f);
        em.getTransaction().commit();
        em.close();
        check();
    }

    @Test
    public void testPersistThenUpdateNoCascadeToTransient() {
        // expected to fail, so nothing will be persisted.
        skipCleanup = true;
        // remove elements from collections and persist
        c.getBCollection().clear();
        c.getDCollection().clear();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(c);
        // now add the elements back
        c.getBCollection().add(b);
        c.getDCollection().add(d);
        try {
            em.getTransaction().commit();
            Assert.fail("should have thrown IllegalStateException");
        } catch (RollbackException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
            IllegalStateException ise = ((IllegalStateException) (ex.getCause()));
            // should fail on entity g (due to no cascade to f.g);
            // instead it fails on entity e ( due to no cascade to d.e)
            // because e is not in the process of being saved yet.
            // when HHH-6999 is fixed, this test should be changed to
            // check for g and f.g
            // noinspection ThrowableResultOfMethodCallIgnored
            TransientPropertyValueException tpve = ExtraAssertions.assertTyping(TransientPropertyValueException.class, ise.getCause());
            Assert.assertEquals(E.class.getName(), tpve.getTransientEntityName());
            Assert.assertEquals(D.class.getName(), tpve.getPropertyOwnerEntityName());
            Assert.assertEquals("e", tpve.getPropertyName());
        }
        em.close();
    }

    @Test
    public void testMerge() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        b = em.merge(b);
        c = b.getC();
        d = b.getD();
        e = d.getE();
        f = e.getF();
        g = f.getG();
        em.getTransaction().commit();
        em.close();
        check();
    }
}

