/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.immutable.entitywithmutablecollection;


import java.util.Iterator;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public abstract class AbstractEntityWithManyToManyTest extends BaseCoreFunctionalTestCase {
    private boolean isPlanContractsInverse;

    private boolean isPlanContractsBidirectional;

    private boolean isPlanVersioned;

    private boolean isContractVersioned;

    @Test
    public void testUpdateProperty() {
        clearCounts();
        Plan p = new Plan("plan");
        p.addContract(new Contract(null, "gail", "phone"));
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        p.setDescription("new plan");
        Assert.assertEquals(1, p.getContracts().size());
        Contract c = ((Contract) (p.getContracts().iterator().next()));
        c.setCustomerName("yogi");
        t.commit();
        s.close();
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(1, p.getContracts().size());
        c = ((Contract) (p.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(1, c.getPlans().size());
            Assert.assertSame(p, c.getPlans().iterator().next());
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithNonEmptyManyToManyCollectionOfNew() {
        clearCounts();
        Plan p = new Plan("plan");
        p.addContract(new Contract(null, "gail", "phone"));
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(1, p.getContracts().size());
        Contract c = ((Contract) (p.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(1, c.getPlans().size());
            Assert.assertSame(p, c.getPlans().iterator().next());
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithNonEmptyManyToManyCollectionOfExisting() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        Plan p = new Plan("plan");
        p.addContract(c);
        s = openSession();
        t = s.beginTransaction();
        s.save(p);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(1, p.getContracts().size());
        c = ((Contract) (p.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(1, c.getPlans().size());
            Assert.assertSame(p, c.getPlans().iterator().next());
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testAddNewManyToManyElementToPersistentEntity() {
        clearCounts();
        Plan p = new Plan("plan");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.get(Plan.class, p.getId())));
        Assert.assertEquals(0, p.getContracts().size());
        p.addContract(new Contract(null, "gail", "phone"));
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(1, p.getContracts().size());
        Contract c = ((Contract) (p.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(1, c.getPlans().size());
            Assert.assertSame(p, c.getPlans().iterator().next());
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testAddExistingManyToManyElementToPersistentEntity() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.get(Plan.class, p.getId())));
        Assert.assertEquals(0, p.getContracts().size());
        c = ((Contract) (s.get(Contract.class, c.getId())));
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(0, c.getPlans().size());
        }
        p.addContract(c);
        t.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount(((isContractVersioned) && (isPlanVersioned) ? 2 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(1, p.getContracts().size());
        c = ((Contract) (p.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertSame(p, c.getPlans().iterator().next());
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithEmptyManyToManyCollectionUpdateWithExistingElement() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        p.addContract(c);
        s = openSession();
        t = s.beginTransaction();
        s.update(p);
        t.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount(((isContractVersioned) && (isPlanVersioned) ? 2 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(1, p.getContracts().size());
        c = ((Contract) (p.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertSame(p, c.getPlans().iterator().next());
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithNonEmptyManyToManyCollectionUpdateWithNewElement() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        p.addContract(c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        Contract newC = new Contract(null, "sherman", "telepathy");
        p.addContract(newC);
        s = openSession();
        t = s.beginTransaction();
        s.update(p);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(2, p.getContracts().size());
        for (Iterator it = p.getContracts().iterator(); it.hasNext();) {
            Contract aContract = ((Contract) (it.next()));
            if ((aContract.getId()) == (c.getId())) {
                Assert.assertEquals("gail", aContract.getCustomerName());
            } else
                if ((aContract.getId()) == (newC.getId())) {
                    Assert.assertEquals("sherman", aContract.getCustomerName());
                } else {
                    Assert.fail("unknown contract");
                }

            if (isPlanContractsBidirectional) {
                Assert.assertSame(p, aContract.getPlans().iterator().next());
            }
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    public void testCreateWithEmptyManyToManyCollectionMergeWithExistingElement() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        p.addContract(c);
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.merge(p)));
        t.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount(((isContractVersioned) && (isPlanVersioned) ? 2 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(1, p.getContracts().size());
        c = ((Contract) (p.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertSame(p, c.getPlans().iterator().next());
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithNonEmptyManyToManyCollectionMergeWithNewElement() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        p.addContract(c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        Contract newC = new Contract(null, "yogi", "mail");
        p.addContract(newC);
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.merge(p)));
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(((isContractVersioned) && (isPlanVersioned) ? 2 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(2, p.getContracts().size());
        for (Iterator it = p.getContracts().iterator(); it.hasNext();) {
            Contract aContract = ((Contract) (it.next()));
            if ((aContract.getId()) == (c.getId())) {
                Assert.assertEquals("gail", aContract.getCustomerName());
            } else
                if (!(aContract.getCustomerName().equals(newC.getCustomerName()))) {
                    Assert.fail(("unknown contract:" + (aContract.getCustomerName())));
                }

            if (isPlanContractsBidirectional) {
                Assert.assertSame(p, aContract.getPlans().iterator().next());
            }
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    public void testRemoveManyToManyElementUsingUpdate() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        p.addContract(c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        p.removeContract(c);
        Assert.assertEquals(0, p.getContracts().size());
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(0, c.getPlans().size());
        }
        s = openSession();
        t = s.beginTransaction();
        s.update(p);
        t.commit();
        s.close();
        assertUpdateCount((isContractVersioned ? 1 : 0));
        assertDeleteCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        if (isPlanContractsInverse) {
            Assert.assertEquals(1, p.getContracts().size());
            c = ((Contract) (p.getContracts().iterator().next()));
            Assert.assertEquals("gail", c.getCustomerName());
            Assert.assertSame(p, c.getPlans().iterator().next());
        } else {
            Assert.assertEquals(0, p.getContracts().size());
            c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
            if (isPlanContractsBidirectional) {
                Assert.assertEquals(0, c.getPlans().size());
            }
            s.delete(c);
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testRemoveManyToManyElementUsingUpdateBothSides() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        p.addContract(c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        p.removeContract(c);
        Assert.assertEquals(0, p.getContracts().size());
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(0, c.getPlans().size());
        }
        s = openSession();
        t = s.beginTransaction();
        s.update(p);
        s.update(c);
        t.commit();
        s.close();
        assertUpdateCount(((isContractVersioned) && (isPlanVersioned) ? 2 : 0));
        assertDeleteCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(0, p.getContracts().size());
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(0, c.getPlans().size());
        }
        s.delete(c);
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testRemoveManyToManyElementUsingMerge() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        p.addContract(c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        p.removeContract(c);
        Assert.assertEquals(0, p.getContracts().size());
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(0, c.getPlans().size());
        }
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.merge(p)));
        t.commit();
        s.close();
        assertUpdateCount((isContractVersioned ? 1 : 0));
        assertDeleteCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        if (isPlanContractsInverse) {
            Assert.assertEquals(1, p.getContracts().size());
            c = ((Contract) (p.getContracts().iterator().next()));
            Assert.assertEquals("gail", c.getCustomerName());
            Assert.assertSame(p, c.getPlans().iterator().next());
        } else {
            Assert.assertEquals(0, p.getContracts().size());
            c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
            if (isPlanContractsBidirectional) {
                Assert.assertEquals(0, c.getPlans().size());
            }
            s.delete(c);
        }
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testRemoveManyToManyElementUsingMergeBothSides() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        p.addContract(c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        p.removeContract(c);
        Assert.assertEquals(0, p.getContracts().size());
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(0, c.getPlans().size());
        }
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.merge(p)));
        c = ((Contract) (s.merge(c)));
        t.commit();
        s.close();
        assertUpdateCount(((isContractVersioned) && (isPlanVersioned) ? 2 : 0));
        assertDeleteCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(0, p.getContracts().size());
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(0, c.getPlans().size());
        }
        s.delete(c);
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testDeleteManyToManyElement() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        p.addContract(c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        s.update(p);
        p.removeContract(c);
        s.delete(c);
        t.commit();
        s.close();
        assertUpdateCount((isContractVersioned ? 1 : 0));
        assertDeleteCount(1);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(0, p.getContracts().size());
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertNull(c);
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testRemoveManyToManyElementByDelete() {
        clearCounts();
        Plan p = new Plan("plan");
        Contract c = new Contract(null, "gail", "phone");
        p.addContract(c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        p.removeContract(c);
        Assert.assertEquals(0, p.getContracts().size());
        if (isPlanContractsBidirectional) {
            Assert.assertEquals(0, c.getPlans().size());
        }
        s = openSession();
        t = s.beginTransaction();
        s.update(p);
        s.delete(c);
        t.commit();
        s.close();
        assertUpdateCount((isPlanVersioned ? 1 : 0));
        assertDeleteCount(1);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(0, p.getContracts().size());
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testManyToManyCollectionOptimisticLockingWithMerge() {
        clearCounts();
        Plan pOrig = new Plan("plan");
        Contract cOrig = new Contract(null, "gail", "phone");
        pOrig.addContract(cOrig);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(pOrig);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        Plan p = ((Plan) (s.get(Plan.class, pOrig.getId())));
        Contract newC = new Contract(null, "sherman", "note");
        p.addContract(newC);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        pOrig.removeContract(cOrig);
        try {
            s.merge(pOrig);
            Assert.assertFalse(isContractVersioned);
        } catch (PersistenceException ex) {
            ExtraAssertions.assertTyping(StaleObjectStateException.class, ex.getCause());
            Assert.assertTrue(isContractVersioned);
        } finally {
            t.rollback();
        }
        s.close();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        s.delete(p);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    public void testManyToManyCollectionOptimisticLockingWithUpdate() {
        clearCounts();
        Plan pOrig = new Plan("plan");
        Contract cOrig = new Contract(null, "gail", "phone");
        pOrig.addContract(cOrig);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(pOrig);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        Plan p = ((Plan) (s.get(Plan.class, pOrig.getId())));
        Contract newC = new Contract(null, "yogi", "pawprint");
        p.addContract(newC);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        pOrig.removeContract(cOrig);
        s.update(pOrig);
        try {
            t.commit();
            Assert.assertFalse(isContractVersioned);
        } catch (PersistenceException ex) {
            t.rollback();
            Assert.assertTrue(isContractVersioned);
            if (!(sessionFactory().getSessionFactoryOptions().isJdbcBatchVersionedData())) {
                ExtraAssertions.assertTyping(StaleObjectStateException.class, ex.getCause());
            } else {
                ExtraAssertions.assertTyping(StaleStateException.class, ex.getCause());
            }
        }
        s.close();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        s.delete(p);
        s.createQuery("delete from Contract").executeUpdate();
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
    }

    @Test
    public void testMoveManyToManyElementToNewEntityCollection() {
        clearCounts();
        Plan p = new Plan("plan");
        p.addContract(new Contract(null, "gail", "phone"));
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).uniqueResult()));
        Assert.assertEquals(1, p.getContracts().size());
        Contract c = ((Contract) (p.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertSame(p, c.getPlans().iterator().next());
        }
        p.removeContract(c);
        Plan p2 = new Plan("new plan");
        p2.addContract(c);
        s.save(p2);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(((isPlanVersioned) && (isContractVersioned) ? 2 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).add(Restrictions.idEq(new Long(p.getId()))).uniqueResult()));
        p2 = ((Plan) (s.createCriteria(Plan.class).add(Restrictions.idEq(new Long(p2.getId()))).uniqueResult()));
        /* if ( isPlanContractsInverse ) {
        assertEquals( 1, p.getContracts().size() );
        c = ( Contract ) p.getContracts().iterator().next();
        assertEquals( "gail", c.getCustomerName() );
        if ( isPlanContractsBidirectional ) {
        assertSame( p, c.getPlans().iterator().next() );
        }
        assertEquals( 0, p2.getContracts().size() );
        }
        else {
         */
        Assert.assertEquals(0, p.getContracts().size());
        Assert.assertEquals(1, p2.getContracts().size());
        c = ((Contract) (p2.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertSame(p2, c.getPlans().iterator().next());
        }
        // }
        s.delete(p);
        s.delete(p2);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    public void testMoveManyToManyElementToExistingEntityCollection() {
        clearCounts();
        Plan p = new Plan("plan");
        p.addContract(new Contract(null, "gail", "phone"));
        Plan p2 = new Plan("plan2");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(p);
        s.persist(p2);
        t.commit();
        s.close();
        assertInsertCount(3);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).add(Restrictions.idEq(new Long(p.getId()))).uniqueResult()));
        Assert.assertEquals(1, p.getContracts().size());
        Contract c = ((Contract) (p.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertSame(p, c.getPlans().iterator().next());
        }
        p.removeContract(c);
        t.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount(((isPlanVersioned) && (isContractVersioned) ? 2 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p2 = ((Plan) (s.createCriteria(Plan.class).add(Restrictions.idEq(new Long(p2.getId()))).uniqueResult()));
        c = ((Contract) (s.createCriteria(Contract.class).add(Restrictions.idEq(new Long(c.getId()))).uniqueResult()));
        p2.addContract(c);
        t.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount(((isPlanVersioned) && (isContractVersioned) ? 2 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        p = ((Plan) (s.createCriteria(Plan.class).add(Restrictions.idEq(new Long(p.getId()))).uniqueResult()));
        p2 = ((Plan) (s.createCriteria(Plan.class).add(Restrictions.idEq(new Long(p2.getId()))).uniqueResult()));
        /* if ( isPlanContractsInverse ) {
        assertEquals( 1, p.getContracts().size() );
        c = ( Contract ) p.getContracts().iterator().next();
        assertEquals( "gail", c.getCustomerName() );
        if ( isPlanContractsBidirectional ) {
        assertSame( p, c.getPlans().iterator().next() );
        }
        assertEquals( 0, p2.getContracts().size() );
        }
        else {
         */
        Assert.assertEquals(0, p.getContracts().size());
        Assert.assertEquals(1, p2.getContracts().size());
        c = ((Contract) (p2.getContracts().iterator().next()));
        Assert.assertEquals("gail", c.getCustomerName());
        if (isPlanContractsBidirectional) {
            Assert.assertSame(p2, c.getPlans().iterator().next());
        }
        // }
        s.delete(p);
        s.delete(p2);
        Assert.assertEquals(new Long(0), s.createCriteria(Plan.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }
}

