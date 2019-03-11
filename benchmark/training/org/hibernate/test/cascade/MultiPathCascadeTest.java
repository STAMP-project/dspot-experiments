/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cascade;


import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:ovidiu@feodorov.com">Ovidiu Feodorov</a>
 * @author Gail Badner
 */
public class MultiPathCascadeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testMultiPathMergeModifiedDetached() throws Exception {
        // persist a simple A in the database
        Session s = openSession();
        s.beginTransaction();
        A a = new A();
        a.setData("Anna");
        s.save(a);
        s.getTransaction().commit();
        s.close();
        // modify detached entity
        modifyEntity(a);
        s = openSession();
        s.beginTransaction();
        a = ((A) (s.merge(a)));
        s.getTransaction().commit();
        s.close();
        verifyModifications(a.getId());
    }

    @Test
    public void testMultiPathMergeModifiedDetachedIntoProxy() throws Exception {
        // persist a simple A in the database
        Session s = openSession();
        s.beginTransaction();
        A a = new A();
        a.setData("Anna");
        s.save(a);
        s.getTransaction().commit();
        s.close();
        // modify detached entity
        modifyEntity(a);
        s = openSession();
        s.beginTransaction();
        A aLoaded = ((A) (s.load(A.class, new Long(a.getId()))));
        Assert.assertTrue((aLoaded instanceof HibernateProxy));
        Assert.assertSame(aLoaded, s.merge(a));
        s.getTransaction().commit();
        s.close();
        verifyModifications(a.getId());
    }

    @Test
    public void testMultiPathUpdateModifiedDetached() throws Exception {
        // persist a simple A in the database
        Session s = openSession();
        s.beginTransaction();
        A a = new A();
        a.setData("Anna");
        s.save(a);
        s.getTransaction().commit();
        s.close();
        // modify detached entity
        modifyEntity(a);
        s = openSession();
        s.beginTransaction();
        s.update(a);
        s.getTransaction().commit();
        s.close();
        verifyModifications(a.getId());
    }

    @Test
    public void testMultiPathGetAndModify() throws Exception {
        // persist a simple A in the database
        Session s = openSession();
        s.beginTransaction();
        A a = new A();
        a.setData("Anna");
        s.save(a);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        // retrieve the previously saved instance from the database, and update it
        a = ((A) (s.get(A.class, new Long(a.getId()))));
        modifyEntity(a);
        s.getTransaction().commit();
        s.close();
        verifyModifications(a.getId());
    }

    @Test
    public void testMultiPathMergeNonCascadedTransientEntityInCollection() throws Exception {
        // persist a simple A in the database
        Session s = openSession();
        s.beginTransaction();
        A a = new A();
        a.setData("Anna");
        s.save(a);
        s.getTransaction().commit();
        s.close();
        // modify detached entity
        modifyEntity(a);
        s = openSession();
        s.beginTransaction();
        a = ((A) (s.merge(a)));
        s.getTransaction().commit();
        s.close();
        verifyModifications(a.getId());
        // add a new (transient) G to collection in h
        // there is no cascade from H to the collection, so this should fail when merged
        Assert.assertEquals(1, a.getHs().size());
        H h = ((H) (a.getHs().iterator().next()));
        G gNew = new G();
        gNew.setData("Gail");
        gNew.getHs().add(h);
        h.getGs().add(gNew);
        s = openSession();
        s.beginTransaction();
        try {
            s.merge(a);
            s.merge(h);
            s.getTransaction().commit();
            Assert.fail("should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            // expected
        } finally {
            s.getTransaction().rollback();
        }
        s.close();
    }

    @Test
    public void testMultiPathMergeNonCascadedTransientEntityInOneToOne() throws Exception {
        // persist a simple A in the database
        Session s = openSession();
        s.beginTransaction();
        A a = new A();
        a.setData("Anna");
        s.save(a);
        s.getTransaction().commit();
        s.close();
        // modify detached entity
        modifyEntity(a);
        s = openSession();
        s.beginTransaction();
        a = ((A) (s.merge(a)));
        s.getTransaction().commit();
        s.close();
        verifyModifications(a.getId());
        // change the one-to-one association from g to be a new (transient) A
        // there is no cascade from G to A, so this should fail when merged
        G g = a.getG();
        a.setG(null);
        A aNew = new A();
        aNew.setData("Alice");
        g.setA(aNew);
        aNew.setG(g);
        s = openSession();
        s.beginTransaction();
        try {
            s.merge(a);
            s.merge(g);
            s.getTransaction().commit();
            Assert.fail("should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            // expected
        } finally {
            s.getTransaction().rollback();
        }
        s.close();
    }

    @Test
    public void testMultiPathMergeNonCascadedTransientEntityInManyToOne() throws Exception {
        // persist a simple A in the database
        Session s = openSession();
        s.beginTransaction();
        A a = new A();
        a.setData("Anna");
        s.save(a);
        s.getTransaction().commit();
        s.close();
        // modify detached entity
        modifyEntity(a);
        s = openSession();
        s.beginTransaction();
        a = ((A) (s.merge(a)));
        s.getTransaction().commit();
        s.close();
        verifyModifications(a.getId());
        // change the many-to-one association from h to be a new (transient) A
        // there is no cascade from H to A, so this should fail when merged
        Assert.assertEquals(1, a.getHs().size());
        H h = ((H) (a.getHs().iterator().next()));
        a.getHs().remove(h);
        A aNew = new A();
        aNew.setData("Alice");
        aNew.addH(h);
        s = openSession();
        s.beginTransaction();
        try {
            s.merge(a);
            s.merge(h);
            s.getTransaction().commit();
            Assert.fail("should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
            // expected
        } finally {
            s.getTransaction().rollback();
        }
        s.close();
    }
}

