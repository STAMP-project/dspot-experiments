/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.immutable.entitywithmutablecollection;


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
@SuppressWarnings({ "UnusedDeclaration" })
public abstract class AbstractEntityWithOneToManyTest extends BaseCoreFunctionalTestCase {
    private boolean isContractPartiesInverse;

    private boolean isContractPartiesBidirectional;

    private boolean isContractVariationsBidirectional;

    private boolean isContractVersioned;

    @Test
    public void testUpdateProperty() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        c.addParty(new Party("party"));
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        c.setCustomerName("yogi");
        Assert.assertEquals(1, c.getParties().size());
        Party party = ((Party) (c.getParties().iterator().next()));
        party.setName("new party");
        t.commit();
        s.close();
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(1, c.getParties().size());
        party = ((Party) (c.getParties().iterator().next()));
        Assert.assertEquals("party", party.getName());
        if (isContractPartiesBidirectional) {
            Assert.assertSame(c, party.getContract());
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithNonEmptyOneToManyCollectionOfNew() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        c.addParty(new Party("party"));
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(1, c.getParties().size());
        Party party = ((Party) (c.getParties().iterator().next()));
        Assert.assertEquals("party", party.getName());
        if (isContractPartiesBidirectional) {
            Assert.assertSame(c, party.getContract());
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithNonEmptyOneToManyCollectionOfExisting() {
        clearCounts();
        Party party = new Party("party");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(party);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        c.addParty(party);
        s = openSession();
        t = s.beginTransaction();
        s.save(c);
        t.commit();
        s.close();
        assertInsertCount(1);
        // BUG, should be assertUpdateCount( ! isContractPartiesInverse && isPartyVersioned ? 1 : 0 );
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        if (isContractPartiesInverse) {
            Assert.assertEquals(0, c.getParties().size());
            party = ((Party) (s.createCriteria(Party.class).uniqueResult()));
            Assert.assertNull(party.getContract());
            s.delete(party);
        } else {
            Assert.assertEquals(1, c.getParties().size());
            party = ((Party) (c.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            if (isContractPartiesBidirectional) {
                Assert.assertSame(c, party.getContract());
            }
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testAddNewOneToManyElementToPersistentEntity() {
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
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.get(Contract.class, c.getId())));
        Assert.assertEquals(0, c.getParties().size());
        c.addParty(new Party("party"));
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(1, c.getParties().size());
        Party party = ((Party) (c.getParties().iterator().next()));
        Assert.assertEquals("party", party.getName());
        if (isContractPartiesBidirectional) {
            Assert.assertSame(c, party.getContract());
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testAddExistingOneToManyElementToPersistentEntity() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Party party = new Party("party");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        s.persist(party);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.get(Contract.class, c.getId())));
        Assert.assertEquals(0, c.getParties().size());
        party = ((Party) (s.get(Party.class, party.getId())));
        if (isContractPartiesBidirectional) {
            Assert.assertNull(party.getContract());
        }
        c.addParty(party);
        t.commit();
        s.close();
        assertInsertCount(0);
        if (checkUpdateCountsAfterAddingExistingElement()) {
            assertUpdateCount(((isContractVersioned) && (!(isContractPartiesInverse)) ? 1 : 0));
        }
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        if (isContractPartiesInverse) {
            Assert.assertEquals(0, c.getParties().size());
            s.delete(party);
        } else {
            Assert.assertEquals(1, c.getParties().size());
            party = ((Party) (c.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            if (isContractPartiesBidirectional) {
                Assert.assertSame(c, party.getContract());
            }
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithEmptyOneToManyCollectionUpdateWithExistingElement() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Party party = new Party("party");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        s.persist(party);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        c.addParty(party);
        s = openSession();
        t = s.beginTransaction();
        s.update(c);
        t.commit();
        s.close();
        assertInsertCount(0);
        if (checkUpdateCountsAfterAddingExistingElement()) {
            assertUpdateCount(((isContractVersioned) && (!(isContractPartiesInverse)) ? 1 : 0));
        }
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        if (isContractPartiesInverse) {
            Assert.assertEquals(0, c.getParties().size());
            s.delete(party);
        } else {
            Assert.assertEquals(1, c.getParties().size());
            party = ((Party) (c.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            if (isContractPartiesBidirectional) {
                Assert.assertSame(c, party.getContract());
            }
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithNonEmptyOneToManyCollectionUpdateWithNewElement() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Party party = new Party("party");
        c.addParty(party);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        Party newParty = new Party("new party");
        c.addParty(newParty);
        s = openSession();
        t = s.beginTransaction();
        s.update(c);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(2, c.getParties().size());
        for (Object o : c.getParties()) {
            Party aParty = ((Party) (o));
            if ((aParty.getId()) == (party.getId())) {
                Assert.assertEquals("party", aParty.getName());
            } else
                if ((aParty.getId()) == (newParty.getId())) {
                    Assert.assertEquals("new party", aParty.getName());
                } else {
                    Assert.fail("unknown party");
                }

            if (isContractPartiesBidirectional) {
                Assert.assertSame(c, aParty.getContract());
            }
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    public void testCreateWithEmptyOneToManyCollectionMergeWithExistingElement() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Party party = new Party("party");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        s.persist(party);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        c.addParty(party);
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.merge(c)));
        t.commit();
        s.close();
        assertInsertCount(0);
        if (checkUpdateCountsAfterAddingExistingElement()) {
            assertUpdateCount(((isContractVersioned) && (!(isContractPartiesInverse)) ? 1 : 0));
        }
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        if (isContractPartiesInverse) {
            Assert.assertEquals(0, c.getParties().size());
            s.delete(party);
        } else {
            Assert.assertEquals(1, c.getParties().size());
            party = ((Party) (c.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            if (isContractPartiesBidirectional) {
                Assert.assertSame(c, party.getContract());
            }
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testCreateWithNonEmptyOneToManyCollectionMergeWithNewElement() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Party party = new Party("party");
        c.addParty(party);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        Party newParty = new Party("new party");
        c.addParty(newParty);
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.merge(c)));
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(2, c.getParties().size());
        for (Object o : c.getParties()) {
            Party aParty = ((Party) (o));
            if ((aParty.getId()) == (party.getId())) {
                Assert.assertEquals("party", aParty.getName());
            } else
                if (!(aParty.getName().equals(newParty.getName()))) {
                    Assert.fail(("unknown party:" + (aParty.getName())));
                }

            if (isContractPartiesBidirectional) {
                Assert.assertSame(c, aParty.getContract());
            }
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    public void testMoveOneToManyElementToNewEntityCollection() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        c.addParty(new Party("party"));
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(1, c.getParties().size());
        Party party = ((Party) (c.getParties().iterator().next()));
        Assert.assertEquals("party", party.getName());
        if (isContractPartiesBidirectional) {
            Assert.assertSame(c, party.getContract());
        }
        c.removeParty(party);
        Contract c2 = new Contract(null, "david", "phone");
        c2.addParty(party);
        s.save(c2);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).add(Restrictions.idEq(Long.valueOf(c.getId()))).uniqueResult()));
        c2 = ((Contract) (s.createCriteria(Contract.class).add(Restrictions.idEq(Long.valueOf(c2.getId()))).uniqueResult()));
        if (isContractPartiesInverse) {
            Assert.assertEquals(1, c.getParties().size());
            party = ((Party) (c.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            if (isContractPartiesBidirectional) {
                Assert.assertSame(c, party.getContract());
            }
            Assert.assertEquals(0, c2.getParties().size());
        } else {
            Assert.assertEquals(0, c.getParties().size());
            Assert.assertEquals(1, c2.getParties().size());
            party = ((Party) (c2.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            if (isContractPartiesBidirectional) {
                Assert.assertSame(c2, party.getContract());
            }
        }
        s.delete(c);
        s.delete(c2);
        Assert.assertEquals(new Long(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(new Long(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    public void testMoveOneToManyElementToExistingEntityCollection() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        c.addParty(new Party("party"));
        Contract c2 = new Contract(null, "david", "phone");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        s.persist(c2);
        t.commit();
        s.close();
        assertInsertCount(3);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).add(Restrictions.idEq(Long.valueOf(c.getId()))).uniqueResult()));
        Assert.assertEquals(1, c.getParties().size());
        Party party = ((Party) (c.getParties().iterator().next()));
        Assert.assertEquals("party", party.getName());
        if (isContractPartiesBidirectional) {
            Assert.assertSame(c, party.getContract());
        }
        c.removeParty(party);
        c2 = ((Contract) (s.createCriteria(Contract.class).add(Restrictions.idEq(Long.valueOf(c2.getId()))).uniqueResult()));
        c2.addParty(party);
        t.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount((isContractVersioned ? 2 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).add(Restrictions.idEq(Long.valueOf(c.getId()))).uniqueResult()));
        c2 = ((Contract) (s.createCriteria(Contract.class).add(Restrictions.idEq(Long.valueOf(c2.getId()))).uniqueResult()));
        if (isContractPartiesInverse) {
            Assert.assertEquals(1, c.getParties().size());
            party = ((Party) (c.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            if (isContractPartiesBidirectional) {
                Assert.assertSame(c, party.getContract());
            }
            Assert.assertEquals(0, c2.getParties().size());
        } else {
            Assert.assertEquals(0, c.getParties().size());
            Assert.assertEquals(1, c2.getParties().size());
            party = ((Party) (c2.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            if (isContractPartiesBidirectional) {
                Assert.assertSame(c2, party.getContract());
            }
        }
        s.delete(c);
        s.delete(c2);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    public void testRemoveOneToManyElementUsingUpdate() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Party party = new Party("party");
        c.addParty(party);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        c.removeParty(party);
        Assert.assertEquals(0, c.getParties().size());
        if (isContractPartiesBidirectional) {
            Assert.assertNull(party.getContract());
        }
        s = openSession();
        t = s.beginTransaction();
        s.update(c);
        s.update(party);
        t.commit();
        s.close();
        if (checkUpdateCountsAfterRemovingElementWithoutDelete()) {
            assertUpdateCount(((isContractVersioned) && (!(isContractPartiesInverse)) ? 1 : 0));
        }
        assertDeleteCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        if (isContractPartiesInverse) {
            Assert.assertEquals(1, c.getParties().size());
            party = ((Party) (c.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            Assert.assertSame(c, party.getContract());
        } else {
            Assert.assertEquals(0, c.getParties().size());
            party = ((Party) (s.createCriteria(Party.class).uniqueResult()));
            if (isContractPartiesBidirectional) {
                Assert.assertNull(party.getContract());
            }
            s.delete(party);
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testRemoveOneToManyElementUsingMerge() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Party party = new Party("party");
        c.addParty(party);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        c.removeParty(party);
        Assert.assertEquals(0, c.getParties().size());
        if (isContractPartiesBidirectional) {
            Assert.assertNull(party.getContract());
        }
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.merge(c)));
        party = ((Party) (s.merge(party)));
        t.commit();
        s.close();
        if (checkUpdateCountsAfterRemovingElementWithoutDelete()) {
            assertUpdateCount(((isContractVersioned) && (!(isContractPartiesInverse)) ? 1 : 0));
        }
        assertDeleteCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        if (isContractPartiesInverse) {
            Assert.assertEquals(1, c.getParties().size());
            party = ((Party) (c.getParties().iterator().next()));
            Assert.assertEquals("party", party.getName());
            Assert.assertSame(c, party.getContract());
        } else {
            Assert.assertEquals(0, c.getParties().size());
            party = ((Party) (s.createCriteria(Party.class).uniqueResult()));
            if (isContractPartiesBidirectional) {
                Assert.assertNull(party.getContract());
            }
            s.delete(party);
        }
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    public void testDeleteOneToManyElement() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Party party = new Party("party");
        c.addParty(party);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        s.update(c);
        c.removeParty(party);
        s.delete(party);
        t.commit();
        s.close();
        assertUpdateCount((isContractVersioned ? 1 : 0));
        assertDeleteCount(1);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(0, c.getParties().size());
        party = ((Party) (s.createCriteria(Party.class).uniqueResult()));
        Assert.assertNull(party);
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testRemoveOneToManyElementByDelete() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        Party party = new Party("party");
        c.addParty(party);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        c.removeParty(party);
        Assert.assertEquals(0, c.getParties().size());
        if (isContractPartiesBidirectional) {
            Assert.assertNull(party.getContract());
        }
        s = openSession();
        t = s.beginTransaction();
        s.update(c);
        s.delete(party);
        t.commit();
        s.close();
        assertUpdateCount((isContractVersioned ? 1 : 0));
        assertDeleteCount(1);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(0, c.getParties().size());
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testRemoveOneToManyOrphanUsingUpdate() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        ContractVariation cv = new ContractVariation(1, c);
        cv.setText("cv1");
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        c.getVariations().remove(cv);
        cv.setContract(null);
        Assert.assertEquals(0, c.getVariations().size());
        if (isContractVariationsBidirectional) {
            Assert.assertNull(cv.getContract());
        }
        s = openSession();
        t = s.beginTransaction();
        s.update(c);
        t.commit();
        s.close();
        assertUpdateCount((isContractVersioned ? 1 : 0));
        assertDeleteCount(1);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(0, c.getVariations().size());
        cv = ((ContractVariation) (s.createCriteria(ContractVariation.class).uniqueResult()));
        Assert.assertNull(cv);
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(ContractVariation.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testRemoveOneToManyOrphanUsingMerge() {
        Contract c = new Contract(null, "gail", "phone");
        ContractVariation cv = new ContractVariation(1, c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        c.getVariations().remove(cv);
        cv.setContract(null);
        Assert.assertEquals(0, c.getVariations().size());
        if (isContractVariationsBidirectional) {
            Assert.assertNull(cv.getContract());
        }
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.merge(c)));
        cv = ((ContractVariation) (s.merge(cv)));
        t.commit();
        s.close();
        assertUpdateCount((isContractVersioned ? 1 : 0));
        assertDeleteCount(1);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(0, c.getVariations().size());
        cv = ((ContractVariation) (s.createCriteria(ContractVariation.class).uniqueResult()));
        Assert.assertNull(cv);
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(ContractVariation.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testDeleteOneToManyOrphan() {
        clearCounts();
        Contract c = new Contract(null, "gail", "phone");
        ContractVariation cv = new ContractVariation(1, c);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(c);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        s.update(c);
        c.getVariations().remove(cv);
        cv.setContract(null);
        Assert.assertEquals(0, c.getVariations().size());
        s.delete(cv);
        t.commit();
        s.close();
        assertUpdateCount((isContractVersioned ? 1 : 0));
        assertDeleteCount(1);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        Assert.assertEquals(0, c.getVariations().size());
        cv = ((ContractVariation) (s.createCriteria(ContractVariation.class).uniqueResult()));
        Assert.assertNull(cv);
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(ContractVariation.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testOneToManyCollectionOptimisticLockingWithMerge() {
        clearCounts();
        Contract cOrig = new Contract(null, "gail", "phone");
        Party partyOrig = new Party("party");
        cOrig.addParty(partyOrig);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(cOrig);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        Contract c = ((Contract) (s.get(Contract.class, cOrig.getId())));
        Party newParty = new Party("new party");
        c.addParty(newParty);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        cOrig.removeParty(partyOrig);
        try {
            s.merge(cOrig);
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
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    public void testOneToManyCollectionOptimisticLockingWithUpdate() {
        clearCounts();
        Contract cOrig = new Contract(null, "gail", "phone");
        Party partyOrig = new Party("party");
        cOrig.addParty(partyOrig);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.persist(cOrig);
        t.commit();
        s.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        Contract c = ((Contract) (s.get(Contract.class, cOrig.getId())));
        Party newParty = new Party("new party");
        c.addParty(newParty);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount((isContractVersioned ? 1 : 0));
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        cOrig.removeParty(partyOrig);
        s.update(cOrig);
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
        c = ((Contract) (s.createCriteria(Contract.class).uniqueResult()));
        s.createQuery("delete from Party").executeUpdate();
        s.delete(c);
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Contract.class).setProjection(Projections.rowCount()).uniqueResult());
        Assert.assertEquals(Long.valueOf(0), s.createCriteria(Party.class).setProjection(Projections.rowCount()).uniqueResult());
        t.commit();
        s.close();
    }
}

