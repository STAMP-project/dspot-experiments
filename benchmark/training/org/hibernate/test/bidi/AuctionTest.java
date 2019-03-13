/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bidi;


import java.math.BigDecimal;
import java.util.Date;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class AuctionTest extends BaseCoreFunctionalTestCase {
    @Test
    @SuppressWarnings({ "unchecked" })
    @SkipForDialect(value = { PostgreSQL81Dialect.class, PostgreSQLDialect.class }, comment = "doesn't like boolean=1")
    public void testLazy() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Auction a = new Auction();
        a.setDescription("an auction for something");
        a.setEnd(new Date());
        Bid b = new Bid();
        b.setAmount(new BigDecimal(123.34).setScale(19, BigDecimal.ROUND_DOWN));
        b.setSuccessful(true);
        b.setDatetime(new Date());
        b.setItem(a);
        a.getBids().add(b);
        a.setSuccessfulBid(b);
        s.persist(b);
        t.commit();
        s.close();
        Long aid = a.getId();
        Long bid = b.getId();
        s = openSession();
        t = s.beginTransaction();
        b = ((Bid) (s.load(Bid.class, bid)));
        Assert.assertFalse(Hibernate.isInitialized(b));
        a = ((Auction) (s.get(Auction.class, aid)));
        Assert.assertFalse(Hibernate.isInitialized(a.getBids()));
        Assert.assertTrue(Hibernate.isInitialized(a.getSuccessfulBid()));
        Assert.assertSame(a.getBids().iterator().next(), b);
        Assert.assertSame(b, a.getSuccessfulBid());
        Assert.assertTrue(Hibernate.isInitialized(b));
        Assert.assertTrue(b.isSuccessful());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        b = ((Bid) (s.load(Bid.class, bid)));
        Assert.assertFalse(Hibernate.isInitialized(b));
        a = ((Auction) (s.createQuery("from Auction a left join fetch a.bids").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(b));
        Assert.assertTrue(Hibernate.isInitialized(a.getBids()));
        Assert.assertSame(b, a.getSuccessfulBid());
        Assert.assertSame(a.getBids().iterator().next(), b);
        Assert.assertTrue(b.isSuccessful());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        b = ((Bid) (s.load(Bid.class, bid)));
        a = ((Auction) (s.load(Auction.class, aid)));
        Assert.assertFalse(Hibernate.isInitialized(b));
        Assert.assertFalse(Hibernate.isInitialized(a));
        s.createQuery("from Auction a left join fetch a.successfulBid").list();
        Assert.assertTrue(Hibernate.isInitialized(b));
        Assert.assertTrue(Hibernate.isInitialized(a));
        Assert.assertSame(b, a.getSuccessfulBid());
        Assert.assertFalse(Hibernate.isInitialized(a.getBids()));
        Assert.assertSame(a.getBids().iterator().next(), b);
        Assert.assertTrue(b.isSuccessful());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        b = ((Bid) (s.load(Bid.class, bid)));
        a = ((Auction) (s.load(Auction.class, aid)));
        Assert.assertFalse(Hibernate.isInitialized(b));
        Assert.assertFalse(Hibernate.isInitialized(a));
        Assert.assertSame(s.get(Bid.class, bid), b);
        Assert.assertTrue(Hibernate.isInitialized(b));
        Assert.assertSame(s.get(Auction.class, aid), a);
        Assert.assertTrue(Hibernate.isInitialized(a));
        Assert.assertSame(b, a.getSuccessfulBid());
        Assert.assertFalse(Hibernate.isInitialized(a.getBids()));
        Assert.assertSame(a.getBids().iterator().next(), b);
        Assert.assertTrue(b.isSuccessful());
        t.commit();
        s.close();
    }
}

