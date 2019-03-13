/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.readonly;


import ScrollMode.FORWARD_ONLY;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 * @author Gail Badner
 */
public class ReadOnlyTest extends AbstractReadOnlyTest {
    @Test
    public void testReadOnlyOnProxies() {
        clearCounts();
        Session s = openSession();
        s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setDescription("original");
        s.save(dp);
        long dpId = dp.getId();
        s.getTransaction().commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpId))));
        Assert.assertFalse("was initialized", Hibernate.isInitialized(dp));
        s.setReadOnly(dp, true);
        Assert.assertFalse("was initialized during setReadOnly", Hibernate.isInitialized(dp));
        dp.setDescription("changed");
        Assert.assertTrue("was not initialized during mod", Hibernate.isInitialized(dp));
        Assert.assertEquals("desc not changed in memory", "changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        s = openSession();
        s.beginTransaction();
        List list = s.createQuery("from DataPoint where description = 'changed'").list();
        Assert.assertEquals("change written to database", 0, list.size());
        Assert.assertEquals(1, s.createQuery("delete from DataPoint").executeUpdate());
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        // deletes from Query.executeUpdate() are not tracked
        // assertDeleteCount( 1 );
    }

    @Test
    public void testReadOnlyMode() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        for (int i = 0; i < 100; i++) {
            DataPoint dp = new DataPoint();
            dp.setX(new BigDecimal((i * 0.1)).setScale(19, BigDecimal.ROUND_DOWN));
            dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
            s.save(dp);
        }
        t.commit();
        s.close();
        assertInsertCount(100);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        int i = 0;
        ScrollableResults sr = s.createQuery("from DataPoint dp order by dp.x asc").setReadOnly(true).scroll(FORWARD_ONLY);
        while (sr.next()) {
            DataPoint dp = ((DataPoint) (sr.get(0)));
            if ((++i) == 50) {
                s.setReadOnly(dp, false);
            }
            dp.setDescription("done!");
        } 
        t.commit();
        assertUpdateCount(1);
        clearCounts();
        s.clear();
        t = s.beginTransaction();
        List single = s.createQuery("from DataPoint where description='done!'").list();
        Assert.assertEquals(single.size(), 1);
        Assert.assertEquals(100, s.createQuery("delete from DataPoint").executeUpdate());
        t.commit();
        s.close();
        assertUpdateCount(0);
        // deletes from Query.executeUpdate() are not tracked
        // assertDeleteCount( 100 );
    }

    @Test
    public void testReadOnlyModeAutoFlushOnQuery() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        DataPoint dpFirst = null;
        for (int i = 0; i < 100; i++) {
            DataPoint dp = new DataPoint();
            dp.setX(new BigDecimal((i * 0.1)).setScale(19, BigDecimal.ROUND_DOWN));
            dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
            s.save(dp);
        }
        assertInsertCount(0);
        assertUpdateCount(0);
        ScrollableResults sr = s.createQuery("from DataPoint dp order by dp.x asc").setReadOnly(true).scroll(FORWARD_ONLY);
        assertInsertCount(100);
        assertUpdateCount(0);
        clearCounts();
        while (sr.next()) {
            DataPoint dp = ((DataPoint) (sr.get(0)));
            Assert.assertFalse(s.isReadOnly(dp));
            s.delete(dp);
        } 
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(100);
    }

    @Test
    public void testSaveReadOnlyModifyInSaveTransaction() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setDescription("original");
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        s.save(dp);
        s.setReadOnly(dp, true);
        dp.setDescription("different");
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        s.setReadOnly(dp, true);
        Assert.assertEquals("original", dp.getDescription());
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        s.refresh(dp);
        Assert.assertEquals("original", dp.getDescription());
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        t.commit();
        assertInsertCount(0);
        assertUpdateCount(0);
        s.clear();
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        Assert.assertEquals("original", dp.getDescription());
        s.delete(dp);
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
        clearCounts();
    }

    @Test
    public void testReadOnlyRefresh() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setDescription("original");
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        s.save(dp);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        s.setReadOnly(dp, true);
        Assert.assertEquals("original", dp.getDescription());
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        s.refresh(dp);
        Assert.assertEquals("original", dp.getDescription());
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        t.commit();
        assertInsertCount(0);
        assertUpdateCount(0);
        s.clear();
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        Assert.assertEquals("original", dp.getDescription());
        s.delete(dp);
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
        clearCounts();
    }

    @Test
    public void testReadOnlyRefreshDetached() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setDescription("original");
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        s.save(dp);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        s.refresh(dp);
        Assert.assertEquals("original", dp.getDescription());
        Assert.assertFalse(s.isReadOnly(dp));
        s.setReadOnly(dp, true);
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        s.evict(dp);
        s.refresh(dp);
        Assert.assertEquals("original", dp.getDescription());
        Assert.assertFalse(s.isReadOnly(dp));
        t.commit();
        assertInsertCount(0);
        assertUpdateCount(0);
        s.clear();
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        Assert.assertEquals("original", dp.getDescription());
        s.delete(dp);
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testReadOnlyDelete() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        s.save(dp);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        s.setReadOnly(dp, true);
        s.delete(dp);
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
        s = openSession();
        t = s.beginTransaction();
        List list = s.createQuery("from DataPoint where description='done!'").list();
        Assert.assertTrue(list.isEmpty());
        t.commit();
        s.close();
    }

    @Test
    public void testReadOnlyGetModifyAndDelete() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        s.save(dp);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        s.setReadOnly(dp, true);
        dp.setDescription("a DataPoint");
        s.delete(dp);
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        List list = s.createQuery("from DataPoint where description='done!'").list();
        Assert.assertTrue(list.isEmpty());
        t.commit();
        s.close();
    }

    @Test
    public void testReadOnlyModeWithExistingModifiableEntity() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        DataPoint dp = null;
        for (int i = 0; i < 100; i++) {
            dp = new DataPoint();
            dp.setX(new BigDecimal((i * 0.1)).setScale(19, BigDecimal.ROUND_DOWN));
            dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
            s.save(dp);
        }
        t.commit();
        s.close();
        assertInsertCount(100);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        DataPoint dpLast = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        Assert.assertFalse(s.isReadOnly(dpLast));
        int i = 0;
        ScrollableResults sr = s.createQuery("from DataPoint dp order by dp.x asc").setReadOnly(true).scroll(FORWARD_ONLY);
        int nExpectedChanges = 0;
        while (sr.next()) {
            dp = ((DataPoint) (sr.get(0)));
            if ((dp.getId()) == (dpLast.getId())) {
                // dpLast existed in the session before executing the read-only query
                Assert.assertFalse(s.isReadOnly(dp));
            } else {
                Assert.assertTrue(s.isReadOnly(dp));
            }
            if ((++i) == 50) {
                s.setReadOnly(dp, false);
                nExpectedChanges = (dp == dpLast) ? 1 : 2;
            }
            dp.setDescription("done!");
        } 
        t.commit();
        s.clear();
        assertInsertCount(0);
        assertUpdateCount(nExpectedChanges);
        clearCounts();
        t = s.beginTransaction();
        List list = s.createQuery("from DataPoint where description='done!'").list();
        Assert.assertEquals(list.size(), nExpectedChanges);
        Assert.assertEquals(100, s.createQuery("delete from DataPoint").executeUpdate());
        t.commit();
        s.close();
        assertUpdateCount(0);
    }

    @Test
    public void testModifiableModeWithExistingReadOnlyEntity() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        DataPoint dp = null;
        for (int i = 0; i < 100; i++) {
            dp = new DataPoint();
            dp.setX(new BigDecimal((i * 0.1)).setScale(19, BigDecimal.ROUND_DOWN));
            dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
            s.save(dp);
        }
        t.commit();
        s.close();
        assertInsertCount(100);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        t = s.beginTransaction();
        DataPoint dpLast = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        Assert.assertFalse(s.isReadOnly(dpLast));
        s.setReadOnly(dpLast, true);
        Assert.assertTrue(s.isReadOnly(dpLast));
        dpLast.setDescription("oy");
        int i = 0;
        assertUpdateCount(0);
        ScrollableResults sr = s.createQuery("from DataPoint dp order by dp.x asc").setReadOnly(false).scroll(FORWARD_ONLY);
        int nExpectedChanges = 0;
        while (sr.next()) {
            dp = ((DataPoint) (sr.get(0)));
            if ((dp.getId()) == (dpLast.getId())) {
                // dpLast existed in the session before executing the read-only query
                Assert.assertTrue(s.isReadOnly(dp));
            } else {
                Assert.assertFalse(s.isReadOnly(dp));
            }
            if ((++i) == 50) {
                s.setReadOnly(dp, true);
                nExpectedChanges = (dp == dpLast) ? 99 : 98;
            }
            dp.setDescription("done!");
        } 
        t.commit();
        s.clear();
        assertUpdateCount(nExpectedChanges);
        clearCounts();
        t = s.beginTransaction();
        List list = s.createQuery("from DataPoint where description='done!'").list();
        Assert.assertEquals(list.size(), nExpectedChanges);
        Assert.assertEquals(100, s.createQuery("delete from DataPoint").executeUpdate());
        t.commit();
        s.close();
        assertUpdateCount(0);
    }

    @Test
    public void testReadOnlyOnTextType() {
        final String origText = "some huge text string";
        final String newText = "some even bigger text string";
        clearCounts();
        Session s = openSession();
        s.beginTransaction();
        TextHolder holder = new TextHolder(origText);
        s.save(holder);
        Long id = holder.getId();
        s.getTransaction().commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        s = openSession();
        s.beginTransaction();
        holder = ((TextHolder) (s.get(TextHolder.class, id)));
        s.setReadOnly(holder, true);
        holder.setTheText(newText);
        s.flush();
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        s = openSession();
        s.beginTransaction();
        holder = ((TextHolder) (s.get(TextHolder.class, id)));
        Assert.assertEquals("change written to database", origText, holder.getTheText());
        s.delete(holder);
        s.getTransaction().commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    @Test
    public void testMergeWithReadOnlyEntity() {
        clearCounts();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        s.save(dp);
        t.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        clearCounts();
        dp.setDescription("description");
        s = openSession();
        t = s.beginTransaction();
        DataPoint dpManaged = ((DataPoint) (s.get(DataPoint.class, new Long(dp.getId()))));
        s.setReadOnly(dpManaged, true);
        DataPoint dpMerged = ((DataPoint) (s.merge(dp)));
        Assert.assertSame(dpManaged, dpMerged);
        t.commit();
        s.close();
        assertUpdateCount(0);
        s = openSession();
        t = s.beginTransaction();
        dpManaged = ((DataPoint) (s.get(DataPoint.class, new Long(dp.getId()))));
        Assert.assertNull(dpManaged.getDescription());
        s.delete(dpManaged);
        t.commit();
        s.close();
        assertUpdateCount(0);
        assertDeleteCount(1);
    }
}

