/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import DialectChecks.SupportsResultSetPositioningOnForwardOnlyCursorCheck;
import ScrollMode.FORWARD_ONLY;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.CUBRIDDialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the new functionality of allowing scrolling of results which
 * contain collection fetches.
 *
 * @author Steve Ebersole
 */
public class ScrollableCollectionFetchingTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testTupleReturnFails() {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        try {
            s.createQuery("select a, a.weight from Animal a inner join fetch a.offspring").scroll();
            Assert.fail("scroll allowed with collection fetch and reurning tuples");
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QueryException.class, e.getCause());
        } catch (HibernateException e) {
            // expected result...
        }
        txn.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = SybaseASE15Dialect.class, jiraKey = "HHH-5229")
    @SkipForDialect(value = { AbstractHANADialect.class }, comment = "HANA only supports forward-only cursors.")
    public void testScrollingJoinFetchesEmptyResultSet() {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        final String query = "from Animal a left join fetch a.offspring where a.description like :desc order by a.id";
        // first, as a control, make sure there are no results
        int size = s.createQuery(query).setString("desc", "root%").list().size();
        Assert.assertEquals(0, size);
        // now get the scrollable results
        ScrollableResults results = s.createQuery(query).setString("desc", "root%").scroll();
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertFalse(results.next());
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertFalse(results.previous());
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        results.beforeFirst();
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertFalse(results.next());
        Assert.assertFalse(results.first());
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertFalse(results.next());
        results.afterLast();
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertFalse(results.next());
        Assert.assertFalse(results.last());
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertFalse(results.next());
        for (int i = 1; i < 3; i++) {
            Assert.assertFalse(results.scroll(i));
            Assert.assertFalse(results.isFirst());
            Assert.assertFalse(results.isLast());
            Assert.assertFalse(results.scroll((-i)));
            Assert.assertFalse(results.isFirst());
            Assert.assertFalse(results.isLast());
            Assert.assertFalse(results.setRowNumber(i));
            Assert.assertFalse(results.isFirst());
            Assert.assertFalse(results.isLast());
            Assert.assertFalse(results.setRowNumber((-i)));
            Assert.assertFalse(results.isFirst());
            Assert.assertFalse(results.isLast());
        }
        txn.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = CUBRIDDialect.class, comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" + "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables")
    @SkipForDialect(value = AbstractHANADialect.class, comment = "HANA only supports forward-only cursors")
    public void testScrollingJoinFetchesSingleRowResultSet() {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Animal mother = new Animal();
        mother.setDescription("root-1");
        Animal daughter = new Animal();
        daughter.setDescription("daughter");
        daughter.setMother(mother);
        mother.addOffspring(daughter);
        s.save(mother);
        s.save(daughter);
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        Assert.assertNotNull(s.createQuery("from Animal a left join fetch a.offspring where a.description like :desc order by a.id").setString("desc", "root%").uniqueResult());
        ScrollableResults results = s.createQuery("from Animal a left join fetch a.offspring where a.description like :desc order by a.id").setString("desc", "root%").scroll();
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertFalse(results.previous());
        Assert.assertTrue(results.next());
        Assert.assertTrue(results.isFirst());
        Assert.assertTrue(results.isLast());
        Assert.assertFalse(results.next());
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertTrue(results.previous());
        Assert.assertTrue(results.isFirst());
        Assert.assertTrue(results.isLast());
        Assert.assertFalse(results.previous());
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertTrue(results.next());
        Assert.assertTrue(results.isFirst());
        Assert.assertTrue(results.isLast());
        results.beforeFirst();
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertFalse(results.previous());
        Assert.assertTrue(results.first());
        Assert.assertTrue(results.isFirst());
        Assert.assertTrue(results.isLast());
        Assert.assertFalse(results.next());
        results.afterLast();
        Assert.assertFalse(results.isFirst());
        Assert.assertFalse(results.isLast());
        Assert.assertFalse(results.next());
        Assert.assertTrue(results.last());
        Assert.assertTrue(results.isFirst());
        Assert.assertTrue(results.isLast());
        Assert.assertFalse(results.next());
        Assert.assertTrue(results.first());
        Assert.assertTrue(results.isFirst());
        Assert.assertTrue(results.isLast());
        for (int i = 1; i < 3; i++) {
            Assert.assertTrue(results.setRowNumber(1));
            Assert.assertTrue(results.isFirst());
            Assert.assertTrue(results.isLast());
            Assert.assertFalse(results.scroll(i));
            Assert.assertFalse(results.isFirst());
            Assert.assertFalse(results.isLast());
            Assert.assertTrue(results.setRowNumber(1));
            Assert.assertTrue(results.isFirst());
            Assert.assertTrue(results.isLast());
            Assert.assertFalse(results.scroll((-i)));
            Assert.assertFalse(results.isFirst());
            Assert.assertFalse(results.isLast());
            if (i != 1) {
                Assert.assertFalse(results.setRowNumber(i));
                Assert.assertFalse(results.isFirst());
                Assert.assertFalse(results.isLast());
                Assert.assertFalse(results.setRowNumber((-i)));
                Assert.assertFalse(results.isFirst());
                Assert.assertFalse(results.isLast());
            }
        }
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        s.createQuery("delete Animal where not description like 'root%'").executeUpdate();
        s.createQuery("delete Animal").executeUpdate();
        txn.commit();
        s.close();
    }

    @Test
    @RequiresDialectFeature(value = SupportsResultSetPositioningOnForwardOnlyCursorCheck.class, comment = "Driver does not support result set positioning  methods on forward-only cursors")
    public void testScrollingJoinFetchesForward() {
        ScrollableCollectionFetchingTest.TestData data = new ScrollableCollectionFetchingTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        ScrollableResults results = s.createQuery("from Animal a left join fetch a.offspring where a.description like :desc order by a.id").setString("desc", "root%").scroll(FORWARD_ONLY);
        int counter = 0;
        while (results.next()) {
            counter++;
            Animal animal = ((Animal) (results.get(0)));
            checkResult(animal);
        } 
        Assert.assertEquals("unexpected result count", 2, counter);
        txn.commit();
        s.close();
        data.cleanup();
    }

    @Test
    @SkipForDialect(value = CUBRIDDialect.class, comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" + "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables")
    @SkipForDialect(value = AbstractHANADialect.class, comment = "HANA only supports forward-only cursors.")
    public void testScrollingJoinFetchesReverse() {
        ScrollableCollectionFetchingTest.TestData data = new ScrollableCollectionFetchingTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        ScrollableResults results = s.createQuery("from Animal a left join fetch a.offspring where a.description like :desc order by a.id").setString("desc", "root%").scroll();
        results.afterLast();
        int counter = 0;
        while (results.previous()) {
            counter++;
            Animal animal = ((Animal) (results.get(0)));
            checkResult(animal);
        } 
        Assert.assertEquals("unexpected result count", 2, counter);
        txn.commit();
        s.close();
        data.cleanup();
    }

    @Test
    @SkipForDialect(value = CUBRIDDialect.class, comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" + "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables")
    @SkipForDialect(value = AbstractHANADialect.class, comment = "HANA only supports forward-only cursors.")
    public void testScrollingJoinFetchesPositioning() {
        ScrollableCollectionFetchingTest.TestData data = new ScrollableCollectionFetchingTest.TestData();
        data.prepare();
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        ScrollableResults results = s.createQuery("from Animal a left join fetch a.offspring where a.description like :desc order by a.id").setString("desc", "root%").scroll();
        results.first();
        Animal animal = ((Animal) (results.get(0)));
        Assert.assertEquals("first() did not return expected row", data.root1Id, animal.getId());
        results.scroll(1);
        animal = ((Animal) (results.get(0)));
        Assert.assertEquals("scroll(1) did not return expected row", data.root2Id, animal.getId());
        results.scroll((-1));
        animal = ((Animal) (results.get(0)));
        Assert.assertEquals("scroll(-1) did not return expected row", data.root1Id, animal.getId());
        results.setRowNumber(1);
        animal = ((Animal) (results.get(0)));
        Assert.assertEquals("setRowNumber(1) did not return expected row", data.root1Id, animal.getId());
        results.setRowNumber(2);
        animal = ((Animal) (results.get(0)));
        Assert.assertEquals("setRowNumber(2) did not return expected row", data.root2Id, animal.getId());
        txn.commit();
        s.close();
        data.cleanup();
    }

    private class TestData {
        private Long root1Id;

        private Long root2Id;

        private void prepare() {
            Session s = openSession();
            Transaction txn = s.beginTransaction();
            Animal mother = new Animal();
            mother.setDescription("root-1");
            Animal another = new Animal();
            another.setDescription("root-2");
            Animal son = new Animal();
            son.setDescription("son");
            Animal daughter = new Animal();
            daughter.setDescription("daughter");
            Animal grandson = new Animal();
            grandson.setDescription("grandson");
            Animal grandDaughter = new Animal();
            grandDaughter.setDescription("granddaughter");
            son.setMother(mother);
            mother.addOffspring(son);
            daughter.setMother(mother);
            mother.addOffspring(daughter);
            grandson.setMother(daughter);
            daughter.addOffspring(grandson);
            grandDaughter.setMother(daughter);
            daughter.addOffspring(grandDaughter);
            s.save(mother);
            s.save(another);
            s.save(son);
            s.save(daughter);
            s.save(grandson);
            s.save(grandDaughter);
            txn.commit();
            s.close();
            root1Id = mother.getId();
            root2Id = another.getId();
        }

        private void cleanup() {
            Session s = openSession();
            Transaction txn = s.beginTransaction();
            s.createQuery("delete Animal where description like 'grand%'").executeUpdate();
            s.createQuery("delete Animal where not description like 'root%'").executeUpdate();
            s.createQuery("delete Animal").executeUpdate();
            txn.commit();
            s.close();
        }
    }
}

