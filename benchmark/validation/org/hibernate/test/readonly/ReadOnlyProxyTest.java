/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.readonly;


import CacheMode.IGNORE;
import java.math.BigDecimal;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransientObjectException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.testing.FailureExpected;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests making initialized and uninitialized proxies read-only/modifiable
 *
 * @author Gail Badner
 */
public class ReadOnlyProxyTest extends AbstractReadOnlyTest {
    @Test
    public void testReadOnlyViaSessionDoesNotInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.setReadOnly(dp, false);
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.flush();
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.getTransaction().commit();
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyViaLazyInitializerDoesNotInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        LazyInitializer dpLI = getHibernateLazyInitializer();
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        dpLI.setReadOnly(true);
        checkReadOnly(s, dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        dpLI.setReadOnly(false);
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.flush();
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.getTransaction().commit();
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyViaSessionNoChangeAfterInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Hibernate.initialize(dp);
        Assert.assertTrue(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Hibernate.initialize(dp);
        Assert.assertTrue(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, true);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.setReadOnly(dp, false);
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Hibernate.initialize(dp);
        Assert.assertTrue(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyViaLazyInitializerNoChangeAfterInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        LazyInitializer dpLI = getHibernateLazyInitializer();
        checkReadOnly(s, dp, false);
        Assert.assertTrue(dpLI.isUninitialized());
        Hibernate.initialize(dp);
        Assert.assertFalse(dpLI.isUninitialized());
        checkReadOnly(s, dp, false);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        dpLI = ((HibernateProxy) (dp)).getHibernateLazyInitializer();
        dpLI.setReadOnly(true);
        checkReadOnly(s, dp, true);
        Assert.assertTrue(dpLI.isUninitialized());
        Hibernate.initialize(dp);
        Assert.assertFalse(dpLI.isUninitialized());
        checkReadOnly(s, dp, true);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        dpLI = ((HibernateProxy) (dp)).getHibernateLazyInitializer();
        dpLI.setReadOnly(true);
        checkReadOnly(s, dp, true);
        Assert.assertTrue(dpLI.isUninitialized());
        dpLI.setReadOnly(false);
        checkReadOnly(s, dp, false);
        Assert.assertTrue(dpLI.isUninitialized());
        Hibernate.initialize(dp);
        Assert.assertFalse(dpLI.isUninitialized());
        checkReadOnly(s, dp, false);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyViaSessionBeforeInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        s.setReadOnly(dp, true);
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        checkReadOnly(s, dp, true);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testModifiableViaSessionBeforeInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        checkReadOnly(s, dp, false);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyViaSessionBeforeInitByModifiableQuery() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        DataPoint dpFromQuery = ((DataPoint) (s.createQuery(("from DataPoint where id=" + (dpOrig.getId()))).setReadOnly(false).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(dpFromQuery));
        Assert.assertSame(dp, dpFromQuery);
        checkReadOnly(s, dp, true);
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyViaSessionBeforeInitByReadOnlyQuery() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        DataPoint dpFromQuery = ((DataPoint) (s.createQuery(("from DataPoint where id=" + (dpOrig.getId()))).setReadOnly(true).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(dpFromQuery));
        Assert.assertSame(dp, dpFromQuery);
        checkReadOnly(s, dp, true);
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testModifiableViaSessionBeforeInitByModifiableQuery() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        DataPoint dpFromQuery = ((DataPoint) (s.createQuery(("from DataPoint where id=" + (dpOrig.getId()))).setReadOnly(false).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(dpFromQuery));
        Assert.assertSame(dp, dpFromQuery);
        checkReadOnly(s, dp, false);
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testModifiableViaSessionBeforeInitByReadOnlyQuery() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        DataPoint dpFromQuery = ((DataPoint) (s.createQuery(("from DataPoint where id=" + (dpOrig.getId()))).setReadOnly(true).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(dpFromQuery));
        Assert.assertSame(dp, dpFromQuery);
        checkReadOnly(s, dp, false);
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyViaLazyInitializerBeforeInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        LazyInitializer dpLI = getHibernateLazyInitializer();
        Assert.assertTrue(dpLI.isUninitialized());
        checkReadOnly(s, dp, false);
        dpLI.setReadOnly(true);
        checkReadOnly(s, dp, true);
        dp.setDescription("changed");
        Assert.assertFalse(dpLI.isUninitialized());
        Assert.assertEquals("changed", dp.getDescription());
        checkReadOnly(s, dp, true);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testModifiableViaLazyInitializerBeforeInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        LazyInitializer dpLI = getHibernateLazyInitializer();
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertTrue(dpLI.isUninitialized());
        checkReadOnly(s, dp, false);
        dp.setDescription("changed");
        Assert.assertFalse(dpLI.isUninitialized());
        Assert.assertEquals("changed", dp.getDescription());
        checkReadOnly(s, dp, false);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyViaLazyInitializerAfterInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        LazyInitializer dpLI = getHibernateLazyInitializer();
        Assert.assertTrue(dpLI.isUninitialized());
        checkReadOnly(s, dp, false);
        dp.setDescription("changed");
        Assert.assertFalse(dpLI.isUninitialized());
        Assert.assertEquals("changed", dp.getDescription());
        checkReadOnly(s, dp, false);
        dpLI.setReadOnly(true);
        checkReadOnly(s, dp, true);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testModifiableViaLazyInitializerAfterInit() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        LazyInitializer dpLI = getHibernateLazyInitializer();
        Assert.assertTrue(dpLI.isUninitialized());
        checkReadOnly(s, dp, false);
        dp.setDescription("changed");
        Assert.assertFalse(dpLI.isUninitialized());
        Assert.assertEquals("changed", dp.getDescription());
        checkReadOnly(s, dp, false);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @FailureExpected(jiraKey = "HHH-4642")
    public void testModifyToReadOnlyToModifiableIsUpdated() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        s.setReadOnly(dp, false);
        checkReadOnly(s, dp, false);
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        try {
            Assert.assertEquals("changed", dp.getDescription());
            // should fail due to HHH-4642
        } finally {
            s.getTransaction().rollback();
            s.close();
            s = openSession();
            s.beginTransaction();
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    @FailureExpected(jiraKey = "HHH-4642")
    public void testReadOnlyModifiedToModifiableIsUpdated() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        s.setReadOnly(dp, false);
        checkReadOnly(s, dp, false);
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        try {
            Assert.assertEquals("changed", dp.getDescription());
            // should fail due to HHH-4642
        } finally {
            s.getTransaction().rollback();
            s.close();
            s = openSession();
            s.beginTransaction();
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    public void testReadOnlyChangedEvictedUpdate() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        s.evict(dp);
        Assert.assertFalse(s.contains(dp));
        s.update(dp);
        checkReadOnly(s, dp, false);
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyToModifiableInitWhenModifiedIsUpdated() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        s.setReadOnly(dp, false);
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyInitToModifiableModifiedIsUpdated() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Hibernate.initialize(dp);
        Assert.assertTrue(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, true);
        s.setReadOnly(dp, false);
        checkReadOnly(s, dp, false);
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyModifiedUpdate() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        checkReadOnly(s, dp, true);
        s.update(dp);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyDelete() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.delete(dp);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertNull(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyRefresh() {
        Session s = openSession();
        s.setCacheMode(IGNORE);
        Transaction t = s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setDescription("original");
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        s.save(dp);
        t.commit();
        s.close();
        s = openSession();
        s.setCacheMode(IGNORE);
        t = s.beginTransaction();
        dp = ((DataPoint) (s.load(DataPoint.class, dp.getId())));
        s.setReadOnly(dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        s.refresh(dp);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Assert.assertEquals("original", dp.getDescription());
        Assert.assertTrue(Hibernate.isInitialized(dp));
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertTrue(s.isReadOnly(dp));
        Assert.assertTrue(s.isReadOnly(((HibernateProxy) (dp)).getHibernateLazyInitializer().getImplementation()));
        s.refresh(dp);
        Assert.assertEquals("original", dp.getDescription());
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertTrue(s.isReadOnly(dp));
        Assert.assertTrue(s.isReadOnly(((HibernateProxy) (dp)).getHibernateLazyInitializer().getImplementation()));
        t.commit();
        s.clear();
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        Assert.assertEquals("original", dp.getDescription());
        s.delete(dp);
        t.commit();
        s.close();
    }

    @Test
    public void testReadOnlyRefreshDeleted() {
        Session s = openSession();
        s.setCacheMode(IGNORE);
        Transaction t = s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setDescription("original");
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        s.save(dp);
        t.commit();
        s.close();
        s = openSession();
        s.setCacheMode(IGNORE);
        t = s.beginTransaction();
        HibernateProxy dpProxy = ((HibernateProxy) (s.load(DataPoint.class, dp.getId())));
        Assert.assertFalse(Hibernate.isInitialized(dpProxy));
        t.commit();
        s.close();
        s = openSession();
        s.setCacheMode(IGNORE);
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        s.delete(dp);
        s.flush();
        try {
            s.refresh(dp);
            Assert.fail("should have thrown UnresolvableObjectException");
        } catch (UnresolvableObjectException ex) {
            // expected
        } finally {
            t.rollback();
            s.close();
        }
        s = openSession();
        t = s.beginTransaction();
        s.setCacheMode(IGNORE);
        DataPoint dpProxyInit = ((DataPoint) (s.load(DataPoint.class, dp.getId())));
        Assert.assertEquals("original", dp.getDescription());
        s.delete(dpProxyInit);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Assert.assertTrue((dpProxyInit instanceof HibernateProxy));
        Assert.assertTrue(Hibernate.isInitialized(dpProxyInit));
        try {
            s.refresh(dpProxyInit);
            Assert.fail("should have thrown UnresolvableObjectException");
        } catch (UnresolvableObjectException ex) {
            // expected
        } finally {
            t.rollback();
            s.close();
        }
        s = openSession();
        t = s.beginTransaction();
        Assert.assertTrue((dpProxy instanceof HibernateProxy));
        try {
            s.refresh(dpProxy);
            Assert.assertFalse(Hibernate.isInitialized(dpProxy));
            Hibernate.initialize(dpProxy);
            Assert.fail("should have thrown UnresolvableObjectException");
        } catch (UnresolvableObjectException ex) {
            // expected
        } finally {
            t.rollback();
            s.close();
        }
    }

    @Test
    public void testReadOnlyRefreshDetached() {
        Session s = openSession();
        s.setCacheMode(IGNORE);
        Transaction t = s.beginTransaction();
        DataPoint dp = new DataPoint();
        dp.setDescription("original");
        dp.setX(new BigDecimal(0.1).setScale(19, BigDecimal.ROUND_DOWN));
        dp.setY(new BigDecimal(Math.cos(dp.getX().doubleValue())).setScale(19, BigDecimal.ROUND_DOWN));
        s.save(dp);
        t.commit();
        s.close();
        s = openSession();
        s.setCacheMode(IGNORE);
        t = s.beginTransaction();
        dp = ((DataPoint) (s.load(DataPoint.class, dp.getId())));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Assert.assertFalse(s.isReadOnly(dp));
        s.setReadOnly(dp, true);
        Assert.assertTrue(s.isReadOnly(dp));
        s.evict(dp);
        s.refresh(dp);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Assert.assertFalse(s.isReadOnly(dp));
        dp.setDescription("changed");
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertTrue(Hibernate.isInitialized(dp));
        s.setReadOnly(dp, true);
        s.evict(dp);
        s.refresh(dp);
        Assert.assertEquals("original", dp.getDescription());
        Assert.assertFalse(s.isReadOnly(dp));
        t.commit();
        s.clear();
        t = s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dp.getId())));
        Assert.assertEquals("original", dp.getDescription());
        s.delete(dp);
        t.commit();
        s.close();
    }

    @Test
    public void testReadOnlyProxyMergeDetachedProxyWithChange() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Hibernate.initialize(dp);
        Assert.assertTrue(Hibernate.isInitialized(dp));
        s.getTransaction().commit();
        s.close();
        // modify detached proxy
        dp.setDescription("changed");
        s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dpLoaded = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dpLoaded instanceof HibernateProxy));
        checkReadOnly(s, dpLoaded, false);
        s.setReadOnly(dpLoaded, true);
        checkReadOnly(s, dpLoaded, true);
        Assert.assertFalse(Hibernate.isInitialized(dpLoaded));
        DataPoint dpMerged = ((DataPoint) (s.merge(dp)));
        Assert.assertSame(dpLoaded, dpMerged);
        Assert.assertTrue(Hibernate.isInitialized(dpLoaded));
        Assert.assertEquals("changed", dpLoaded.getDescription());
        checkReadOnly(s, dpLoaded, true);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyProxyInitMergeDetachedProxyWithChange() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Hibernate.initialize(dp);
        Assert.assertTrue(Hibernate.isInitialized(dp));
        s.getTransaction().commit();
        s.close();
        // modify detached proxy
        dp.setDescription("changed");
        s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dpLoaded = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dpLoaded instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dpLoaded));
        Hibernate.initialize(dpLoaded);
        Assert.assertTrue(Hibernate.isInitialized(dpLoaded));
        checkReadOnly(s, dpLoaded, false);
        s.setReadOnly(dpLoaded, true);
        checkReadOnly(s, dpLoaded, true);
        DataPoint dpMerged = ((DataPoint) (s.merge(dp)));
        Assert.assertSame(dpLoaded, dpMerged);
        Assert.assertEquals("changed", dpLoaded.getDescription());
        checkReadOnly(s, dpLoaded, true);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyProxyMergeDetachedEntityWithChange() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Hibernate.initialize(dp);
        Assert.assertTrue(Hibernate.isInitialized(dp));
        s.getTransaction().commit();
        s.close();
        // modify detached proxy target
        DataPoint dpEntity = ((DataPoint) (((HibernateProxy) (dp)).getHibernateLazyInitializer().getImplementation()));
        dpEntity.setDescription("changed");
        s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dpLoaded = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dpLoaded instanceof HibernateProxy));
        checkReadOnly(s, dpLoaded, false);
        s.setReadOnly(dpLoaded, true);
        checkReadOnly(s, dpLoaded, true);
        Assert.assertFalse(Hibernate.isInitialized(dpLoaded));
        DataPoint dpMerged = ((DataPoint) (s.merge(dpEntity)));
        Assert.assertSame(dpLoaded, dpMerged);
        Assert.assertTrue(Hibernate.isInitialized(dpLoaded));
        Assert.assertEquals("changed", dpLoaded.getDescription());
        checkReadOnly(s, dpLoaded, true);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyProxyInitMergeDetachedEntityWithChange() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Hibernate.initialize(dp);
        Assert.assertTrue(Hibernate.isInitialized(dp));
        s.getTransaction().commit();
        s.close();
        // modify detached proxy target
        DataPoint dpEntity = ((DataPoint) (((HibernateProxy) (dp)).getHibernateLazyInitializer().getImplementation()));
        dpEntity.setDescription("changed");
        s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dpLoaded = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dpLoaded instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dpLoaded));
        Hibernate.initialize(dpLoaded);
        Assert.assertTrue(Hibernate.isInitialized(dpLoaded));
        checkReadOnly(s, dpLoaded, false);
        s.setReadOnly(dpLoaded, true);
        checkReadOnly(s, dpLoaded, true);
        DataPoint dpMerged = ((DataPoint) (s.merge(dpEntity)));
        Assert.assertSame(dpLoaded, dpMerged);
        Assert.assertEquals("changed", dpLoaded.getDescription());
        checkReadOnly(s, dpLoaded, true);
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReadOnlyEntityMergeDetachedProxyWithChange() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        checkReadOnly(s, dp, false);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        Hibernate.initialize(dp);
        Assert.assertTrue(Hibernate.isInitialized(dp));
        s.getTransaction().commit();
        s.close();
        // modify detached proxy
        dp.setDescription("changed");
        s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dpEntity = ((DataPoint) (s.get(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertFalse((dpEntity instanceof HibernateProxy));
        Assert.assertFalse(s.isReadOnly(dpEntity));
        s.setReadOnly(dpEntity, true);
        Assert.assertTrue(s.isReadOnly(dpEntity));
        DataPoint dpMerged = ((DataPoint) (s.merge(dp)));
        Assert.assertSame(dpEntity, dpMerged);
        Assert.assertEquals("changed", dpEntity.getDescription());
        Assert.assertTrue(s.isReadOnly(dpEntity));
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSetReadOnlyInTwoTransactionsSameSession() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        Assert.assertFalse(Hibernate.isInitialized(dp));
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        checkReadOnly(s, dp, true);
        s.beginTransaction();
        checkReadOnly(s, dp, true);
        dp.setDescription("changed again");
        Assert.assertEquals("changed again", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSetReadOnlyBetweenTwoTransactionsSameSession() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        checkReadOnly(s, dp, false);
        s.flush();
        s.getTransaction().commit();
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        s.beginTransaction();
        checkReadOnly(s, dp, true);
        dp.setDescription("changed again");
        Assert.assertEquals("changed again", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals("changed", dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSetModifiableBetweenTwoTransactionsSameSession() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.setReadOnly(dp, true);
        checkReadOnly(s, dp, true);
        dp.setDescription("changed");
        Assert.assertTrue(Hibernate.isInitialized(dp));
        Assert.assertEquals("changed", dp.getDescription());
        checkReadOnly(s, dp, true);
        s.flush();
        s.getTransaction().commit();
        checkReadOnly(s, dp, true);
        s.setReadOnly(dp, false);
        checkReadOnly(s, dp, false);
        s.beginTransaction();
        checkReadOnly(s, dp, false);
        Assert.assertEquals("changed", dp.getDescription());
        s.refresh(dp);
        Assert.assertEquals(dpOrig.getDescription(), dp.getDescription());
        checkReadOnly(s, dp, false);
        dp.setDescription("changed again");
        Assert.assertEquals("changed again", dp.getDescription());
        s.flush();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        dp = ((DataPoint) (s.get(DataPoint.class, dpOrig.getId())));
        Assert.assertEquals(dpOrig.getId(), dp.getId());
        Assert.assertEquals("changed again", dp.getDescription());
        Assert.assertEquals(dpOrig.getX(), dp.getX());
        Assert.assertEquals(dpOrig.getY(), dp.getY());
        s.delete(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testIsReadOnlyAfterSessionClosed() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.getTransaction().commit();
        s.close();
        try {
            s.isReadOnly(dp);
            Assert.fail("should have failed because session was closed");
        } catch (IllegalStateException ex) {
            // expected
            Assert.assertFalse(((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnlySettingAvailable());
        } finally {
            s = openSession();
            s.beginTransaction();
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    public void testIsReadOnlyAfterSessionClosedViaLazyInitializer() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.getTransaction().commit();
        Assert.assertTrue(s.contains(dp));
        s.close();
        Assert.assertNull(((HibernateProxy) (dp)).getHibernateLazyInitializer().getSession());
        try {
            ((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnly();
            Assert.fail("should have failed because session was detached");
        } catch (TransientObjectException ex) {
            // expected
            Assert.assertFalse(((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnlySettingAvailable());
        } finally {
            s = openSession();
            s.beginTransaction();
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    public void testDetachedIsReadOnlyAfterEvictViaSession() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        Assert.assertTrue(s.contains(dp));
        s.evict(dp);
        Assert.assertFalse(s.contains(dp));
        Assert.assertNull(((HibernateProxy) (dp)).getHibernateLazyInitializer().getSession());
        try {
            s.isReadOnly(dp);
            Assert.fail("should have failed because proxy was detached");
        } catch (TransientObjectException ex) {
            // expected
            Assert.assertFalse(((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnlySettingAvailable());
        } finally {
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    public void testDetachedIsReadOnlyAfterEvictViaLazyInitializer() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.evict(dp);
        Assert.assertFalse(s.contains(dp));
        Assert.assertNull(((HibernateProxy) (dp)).getHibernateLazyInitializer().getSession());
        try {
            ((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnly();
            Assert.fail("should have failed because proxy was detached");
        } catch (TransientObjectException ex) {
            // expected
            Assert.assertFalse(((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnlySettingAvailable());
        } finally {
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    public void testSetReadOnlyAfterSessionClosed() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.getTransaction().commit();
        s.close();
        try {
            s.setReadOnly(dp, true);
            Assert.fail("should have failed because session was closed");
        } catch (IllegalStateException ex) {
            // expected
            Assert.assertFalse(((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnlySettingAvailable());
        } finally {
            s = openSession();
            s.beginTransaction();
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    public void testSetReadOnlyAfterSessionClosedViaLazyInitializer() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.getTransaction().commit();
        Assert.assertTrue(s.contains(dp));
        s.close();
        Assert.assertNull(((HibernateProxy) (dp)).getHibernateLazyInitializer().getSession());
        try {
            ((HibernateProxy) (dp)).getHibernateLazyInitializer().setReadOnly(true);
            Assert.fail("should have failed because session was detached");
        } catch (TransientObjectException ex) {
            // expected
            Assert.assertFalse(((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnlySettingAvailable());
        } finally {
            s = openSession();
            s.beginTransaction();
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    public void testSetClosedSessionInLazyInitializer() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.getTransaction().commit();
        Assert.assertTrue(s.contains(dp));
        s.close();
        Assert.assertNull(((HibernateProxy) (dp)).getHibernateLazyInitializer().getSession());
        Assert.assertTrue(isClosed());
        try {
            ((HibernateProxy) (dp)).getHibernateLazyInitializer().setSession(((SessionImplementor) (s)));
            Assert.fail("should have failed because session was closed");
        } catch (IllegalStateException ex) {
            // expected
            Assert.assertFalse(((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnlySettingAvailable());
        } finally {
            s = openSession();
            s.beginTransaction();
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    public void testDetachedSetReadOnlyAfterEvictViaSession() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        Assert.assertTrue(s.contains(dp));
        s.evict(dp);
        Assert.assertFalse(s.contains(dp));
        Assert.assertNull(((HibernateProxy) (dp)).getHibernateLazyInitializer().getSession());
        try {
            s.setReadOnly(dp, true);
            Assert.fail("should have failed because proxy was detached");
        } catch (TransientObjectException ex) {
            // expected
            Assert.assertFalse(((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnlySettingAvailable());
        } finally {
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }

    @Test
    public void testDetachedSetReadOnlyAfterEvictViaLazyInitializer() {
        DataPoint dpOrig = createDataPoint(IGNORE);
        Session s = openSession();
        s.setCacheMode(IGNORE);
        s.beginTransaction();
        DataPoint dp = ((DataPoint) (s.load(DataPoint.class, new Long(dpOrig.getId()))));
        Assert.assertTrue((dp instanceof HibernateProxy));
        Assert.assertFalse(Hibernate.isInitialized(dp));
        checkReadOnly(s, dp, false);
        s.evict(dp);
        Assert.assertFalse(s.contains(dp));
        Assert.assertNull(((HibernateProxy) (dp)).getHibernateLazyInitializer().getSession());
        try {
            ((HibernateProxy) (dp)).getHibernateLazyInitializer().setReadOnly(true);
            Assert.fail("should have failed because proxy was detached");
        } catch (TransientObjectException ex) {
            // expected
            Assert.assertFalse(((HibernateProxy) (dp)).getHibernateLazyInitializer().isReadOnlySettingAvailable());
        } finally {
            s.delete(dp);
            s.getTransaction().commit();
            s.close();
        }
    }
}

