/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.entity;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class Java5FeaturesTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testInterface() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Race r = new Race();
        r.setId(new Integer(1));
        r.setLength(new Long(3));
        s.persist(r);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        r = ((Race) (s.get(Race.class, r.getId())));
        Assert.assertEquals(new Long(3), r.getLength());
        tx.commit();
        s.close();
    }

    @Test
    public void testEnums() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        CommunityBid communityBid = new CommunityBid();
        communityBid.setId(new Integer(2));
        communityBid.setCommunityNote(Starred.OK);
        Bid bid = new Bid();
        bid.setId(new Integer(1));
        bid.setDescription("My best one");
        bid.setNote(Starred.OK);
        bid.setEditorsNote(Starred.GOOD);
        s.persist(bid);
        s.persist(communityBid);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        // bid = (Bid) s.get( Bid.class, bid.getId() );
        bid = ((Bid) (s.createQuery((((("select b from Bid b where b.note = " + (Starred.class.getName())) + ".OK and b.editorsNote = ") + (Starred.class.getName())) + ".GOOD and b.id = :id")).setParameter("id", bid.getId()).uniqueResult()));
        // testing constant value
        Assert.assertEquals(Starred.OK, bid.getNote());
        Assert.assertEquals(Starred.GOOD, bid.getEditorsNote());
        bid = ((Bid) (s.createQuery(("select b from Bid b where b.note = :note" + (" and b.editorsNote = :editorNote " + " and b.id = :id"))).setParameter("id", bid.getId()).setParameter("note", Starred.OK).setParameter("editorNote", Starred.GOOD).uniqueResult()));
        // testing constant value
        Assert.assertEquals(Starred.OK, bid.getNote());
        Assert.assertEquals(Starred.GOOD, bid.getEditorsNote());
        bid.setNote(null);
        tx.commit();
        s.clear();
        tx = s.beginTransaction();
        bid = ((Bid) (s.get(Bid.class, bid.getId())));
        communityBid = ((CommunityBid) (s.get(CommunityBid.class, communityBid.getId())));
        Assert.assertNull(bid.getNote());
        Assert.assertEquals(Starred.OK, communityBid.getCommunityNote());
        s.delete(bid);
        s.clear();
        communityBid = ((CommunityBid) (s.createSQLQuery("select {b.*} from Bid b where b.id = ?").addEntity("b", CommunityBid.class).setInteger(1, communityBid.getId()).uniqueResult()));
        Assert.assertEquals(Starred.OK, communityBid.getCommunityNote());
        s.delete(communityBid);
        tx.commit();
        s.close();
    }
}

