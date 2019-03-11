/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.generatedkeys.identity;


import DialectChecks.SupportsIdentityColumns;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import org.hibernate.Session;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class IdentityGeneratedKeysTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIdentityColumnGeneratedIds() {
        Session s = openSession();
        s.beginTransaction();
        MyEntity myEntity = new MyEntity("test");
        Long id = ((Long) (s.save(myEntity)));
        Assert.assertNotNull("identity column did not force immediate insert", id);
        Assert.assertEquals(id, myEntity.getId());
        s.delete(myEntity);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testPersistOutsideTransaction() {
        Session s = openSession();
        try {
            // first test save() which should force an immediate insert...
            MyEntity myEntity1 = new MyEntity("test-save");
            Long id = ((Long) (s.save(myEntity1)));
            Assert.assertNotNull("identity column did not force immediate insert", id);
            Assert.assertEquals(id, myEntity1.getId());
            // next test persist() which should cause a delayed insert...
            long initialInsertCount = sessionFactory().getStatistics().getEntityInsertCount();
            MyEntity myEntity2 = new MyEntity("test-persist");
            s.persist(myEntity2);
            Assert.assertEquals("persist on identity column not delayed", initialInsertCount, sessionFactory().getStatistics().getEntityInsertCount());
            Assert.assertNull(myEntity2.getId());
            // an explicit flush should cause execution of the delayed insertion
            s.flush();
            Assert.fail("TransactionRequiredException required upon flush");
        } catch (PersistenceException ex) {
            // expected
            ExtraAssertions.assertTyping(TransactionRequiredException.class, ex);
        } finally {
            s.close();
        }
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testPersistOutsideTransactionCascadedToNonInverseCollection() {
        long initialInsertCount = sessionFactory().getStatistics().getEntityInsertCount();
        Session s = openSession();
        try {
            MyEntity myEntity = new MyEntity("test-persist");
            myEntity.getNonInverseChildren().add(new MyChild("test-child-persist-non-inverse"));
            s.persist(myEntity);
            Assert.assertEquals("persist on identity column not delayed", initialInsertCount, sessionFactory().getStatistics().getEntityInsertCount());
            Assert.assertNull(myEntity.getId());
            s.flush();
            Assert.fail("TransactionRequiredException required upon flush");
        } catch (PersistenceException ex) {
            // expected
            ExtraAssertions.assertTyping(TransactionRequiredException.class, ex);
        } finally {
            s.close();
        }
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testPersistOutsideTransactionCascadedToInverseCollection() {
        long initialInsertCount = sessionFactory().getStatistics().getEntityInsertCount();
        Session s = openSession();
        try {
            MyEntity myEntity2 = new MyEntity("test-persist-2");
            MyChild child = new MyChild("test-child-persist-inverse");
            myEntity2.getInverseChildren().add(child);
            child.setInverseParent(myEntity2);
            s.persist(myEntity2);
            Assert.assertEquals("persist on identity column not delayed", initialInsertCount, sessionFactory().getStatistics().getEntityInsertCount());
            Assert.assertNull(myEntity2.getId());
            s.flush();
            Assert.fail("TransactionRequiredException expected upon flush.");
        } catch (PersistenceException ex) {
            // expected
            ExtraAssertions.assertTyping(TransactionRequiredException.class, ex);
        } finally {
            s.close();
        }
    }

    @Test
    public void testPersistOutsideTransactionCascadedToManyToOne() {
        long initialInsertCount = sessionFactory().getStatistics().getEntityInsertCount();
        Session s = openSession();
        try {
            MyEntity myEntity = new MyEntity("test-persist");
            myEntity.setSibling(new MySibling("test-persist-sibling-out"));
            s.persist(myEntity);
            Assert.assertEquals("persist on identity column not delayed", initialInsertCount, sessionFactory().getStatistics().getEntityInsertCount());
            Assert.assertNull(myEntity.getId());
            s.flush();
            Assert.fail("TransactionRequiredException expected upon flush.");
        } catch (PersistenceException ex) {
            // expected
            ExtraAssertions.assertTyping(TransactionRequiredException.class, ex);
        } finally {
            s.close();
        }
    }

    @Test
    public void testPersistOutsideTransactionCascadedFromManyToOne() {
        long initialInsertCount = sessionFactory().getStatistics().getEntityInsertCount();
        Session s = openSession();
        try {
            MyEntity myEntity2 = new MyEntity("test-persist-2");
            MySibling sibling = new MySibling("test-persist-sibling-in");
            sibling.setEntity(myEntity2);
            s.persist(sibling);
            Assert.assertEquals("persist on identity column not delayed", initialInsertCount, sessionFactory().getStatistics().getEntityInsertCount());
            Assert.assertNull(myEntity2.getId());
            s.flush();
            Assert.fail("TransactionRequiredException expected upon flush.");
        } catch (PersistenceException ex) {
            // expected
            ExtraAssertions.assertTyping(TransactionRequiredException.class, ex);
        } finally {
            s.close();
        }
    }
}

