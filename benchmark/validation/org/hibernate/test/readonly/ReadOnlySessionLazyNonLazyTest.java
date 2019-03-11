/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.readonly;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;


/**
 * Model:
 *
 * Container
 * |-- N : 1 -- noProxyOwner (property-ref="name" lazy="no-proxy" cascade="all")
 * |-- N : 1 -- proxyOwner (property-ref="name" lazy="proxy" cascade="all")
 * |-- N : 1 -- nonLazyOwner (property-ref="name" lazy="false" cascade="all")
 * |-- N : 1 -- noProxyInfo" (lazy="no-proxy" cascade="all")
 * |-- N : 1 -- proxyInfo (lazy="proxy" cascade="all"
 * |-- N : 1 -- nonLazyInfo" (lazy="false" cascade="all")
 * |
 * |-- 1 : N -- lazyDataPoints" (lazy="true" inverse="false" cascade="all")
 * |-- 1 : N -- nonLazySelectDataPoints" (lazy="false" inverse="false" cascade="all" fetch="select")
 * |-- 1 : N -- nonLazyJoinDataPoints" (lazy="false" inverse="false" cascade="all" fetch="join")
 *
 * Note: the following many-to-one properties use a property-ref so they are
 * initialized, regardless of how the lazy attribute is mapped:
 * noProxyOwner, proxyOwner, nonLazyOwner
 *
 * @author Gail Badner
 */
public class ReadOnlySessionLazyNonLazyTest extends AbstractReadOnlyTest {
    @Test
    @SuppressWarnings({ "unchecked" })
    public void testExistingModifiableAfterSetSessionReadOnly() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        t = s.beginTransaction();
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Container c = ((Container) (s.load(Container.class, cOrig.getId())));
        Assert.assertSame(cOrig, c);
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertSame(cOrig, c);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.refresh(cOrig);
        Assert.assertSame(cOrig, c);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.evict(cOrig);
        c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        expectedReadOnlyObjects.add(c.getLazyDataPoints().iterator().next());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testExistingReadOnlyAfterSetSessionModifiable() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.setDefaultReadOnly(true);
        Container c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(false);
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        // expectedReadOnlyObjects.add(c.getLazyDataPoints().iterator().next() );
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testExistingReadOnlyAfterSetSessionModifiableExisting() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        DataPoint lazyDataPointOrig = ((DataPoint) (cOrig.getLazyDataPoints().iterator().next()));
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.setDefaultReadOnly(true);
        Container c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(false);
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        DataPoint lazyDataPoint = ((DataPoint) (s.get(DataPoint.class, lazyDataPointOrig.getId())));
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        Assert.assertSame(lazyDataPoint, c.getLazyDataPoints().iterator().next());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testExistingReadOnlyAfterSetSessionModifiableExistingEntityReadOnly() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        DataPoint lazyDataPointOrig = ((DataPoint) (cOrig.getLazyDataPoints().iterator().next()));
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.setDefaultReadOnly(true);
        Container c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(false);
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        DataPoint lazyDataPoint = ((DataPoint) (s.get(DataPoint.class, lazyDataPointOrig.getId())));
        s.setDefaultReadOnly(false);
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        Assert.assertSame(lazyDataPoint, c.getLazyDataPoints().iterator().next());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        expectedReadOnlyObjects.add(lazyDataPoint);
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testExistingReadOnlyAfterSetSessionModifiableProxyExisting() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        DataPoint lazyDataPointOrig = ((DataPoint) (cOrig.getLazyDataPoints().iterator().next()));
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.setDefaultReadOnly(true);
        Container c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(false);
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        DataPoint lazyDataPoint = ((DataPoint) (s.load(DataPoint.class, lazyDataPointOrig.getId())));
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        Assert.assertSame(lazyDataPoint, c.getLazyDataPoints().iterator().next());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testExistingReadOnlyAfterSetSessionModifiableExistingProxyReadOnly() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        DataPoint lazyDataPointOrig = ((DataPoint) (cOrig.getLazyDataPoints().iterator().next()));
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.setDefaultReadOnly(true);
        Container c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(false);
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        DataPoint lazyDataPoint = ((DataPoint) (s.load(DataPoint.class, lazyDataPointOrig.getId())));
        s.setDefaultReadOnly(false);
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        Assert.assertSame(lazyDataPoint, c.getLazyDataPoints().iterator().next());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        expectedReadOnlyObjects.add(lazyDataPoint);
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testDefaultModifiableWithReadOnlyQueryForEntity() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Assert.assertFalse(s.isDefaultReadOnly());
        Container c = ((Container) (s.createQuery(("from Container where id=" + (cOrig.getId()))).setReadOnly(true).uniqueResult()));
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        // expectedReadOnlyObjects.add(c.getLazyDataPoints().iterator().next() );
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testDefaultReadOnlyWithModifiableQueryForEntity() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        Container c = ((Container) (s.createQuery(("from Container where id=" + (cOrig.getId()))).setReadOnly(false).uniqueResult()));
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet();
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        expectedReadOnlyObjects.add(c.getLazyDataPoints().iterator().next());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testDefaultReadOnlyWithQueryForEntity() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        Container c = ((Container) (s.createQuery(("from Container where id=" + (cOrig.getId()))).uniqueResult()));
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        expectedReadOnlyObjects.add(c.getLazyDataPoints().iterator().next());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testDefaultModifiableWithQueryForEntity() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Assert.assertFalse(s.isDefaultReadOnly());
        Container c = ((Container) (s.createQuery(("from Container where id=" + (cOrig.getId()))).uniqueResult()));
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet();
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getNoProxyInfo()));
        Hibernate.initialize(c.getNoProxyInfo());
        expectedInitializedObjects.add(c.getNoProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getProxyInfo()));
        Hibernate.initialize(c.getProxyInfo());
        expectedInitializedObjects.add(c.getProxyInfo());
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        Assert.assertFalse(Hibernate.isInitialized(c.getLazyDataPoints()));
        Hibernate.initialize(c.getLazyDataPoints());
        expectedInitializedObjects.add(c.getLazyDataPoints().iterator().next());
        // expectedReadOnlyObjects.add(c.getLazyDataPoints().iterator().next() );
        checkContainer(c, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testDefaultModifiableWithReadOnlyQueryForCollectionEntities() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Assert.assertFalse(s.isDefaultReadOnly());
        DataPoint dp = ((DataPoint) (s.createQuery(("select c.lazyDataPoints from Container c join c.lazyDataPoints where c.id=" + (cOrig.getId()))).setReadOnly(true).uniqueResult()));
        Assert.assertTrue(s.isReadOnly(dp));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testDefaultReadOnlyWithModifiableFilterCollectionEntities() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        Container c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        List list = s.createFilter(c.getLazyDataPoints(), "").setMaxResults(1).setReadOnly(false).list();
        Assert.assertEquals(1, list.size());
        Assert.assertFalse(s.isReadOnly(list.get(0)));
        list = s.createFilter(c.getNonLazyJoinDataPoints(), "").setMaxResults(1).setReadOnly(false).list();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(s.isReadOnly(list.get(0)));
        list = s.createFilter(c.getNonLazySelectDataPoints(), "").setMaxResults(1).setReadOnly(false).list();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(s.isReadOnly(list.get(0)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testDefaultModifiableWithReadOnlyFilterCollectionEntities() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Assert.assertFalse(s.isDefaultReadOnly());
        Container c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet();
        List list = s.createFilter(c.getLazyDataPoints(), "").setMaxResults(1).setReadOnly(true).list();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(s.isReadOnly(list.get(0)));
        list = s.createFilter(c.getNonLazyJoinDataPoints(), "").setMaxResults(1).setReadOnly(true).list();
        Assert.assertEquals(1, list.size());
        Assert.assertFalse(s.isReadOnly(list.get(0)));
        list = s.createFilter(c.getNonLazySelectDataPoints(), "").setMaxResults(1).setReadOnly(true).list();
        Assert.assertEquals(1, list.size());
        Assert.assertFalse(s.isReadOnly(list.get(0)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testDefaultReadOnlyWithFilterCollectionEntities() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        Container c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet(// c.getLazyDataPoints(),
        Arrays.asList(c, c.getNoProxyInfo(), c.getProxyInfo(), c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        List list = s.createFilter(c.getLazyDataPoints(), "").setMaxResults(1).list();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(s.isReadOnly(list.get(0)));
        list = s.createFilter(c.getNonLazyJoinDataPoints(), "").setMaxResults(1).list();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(s.isReadOnly(list.get(0)));
        list = s.createFilter(c.getNonLazySelectDataPoints(), "").setMaxResults(1).list();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(s.isReadOnly(list.get(0)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testDefaultModifiableWithFilterCollectionEntities() {
        Container cOrig = createContainer();
        Set expectedInitializedObjects = new HashSet(Arrays.asList(cOrig, cOrig.getNoProxyInfo(), cOrig.getProxyInfo(), cOrig.getNonLazyInfo(), cOrig.getNoProxyOwner(), cOrig.getProxyOwner(), cOrig.getNonLazyOwner(), cOrig.getLazyDataPoints().iterator().next(), cOrig.getNonLazyJoinDataPoints().iterator().next(), cOrig.getNonLazySelectDataPoints().iterator().next()));
        Set expectedReadOnlyObjects = new HashSet();
        Session s = openSession();
        Assert.assertFalse(s.isDefaultReadOnly());
        Transaction t = s.beginTransaction();
        s.save(cOrig);
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        s.setDefaultReadOnly(true);
        Assert.assertTrue(s.isDefaultReadOnly());
        checkContainer(cOrig, expectedInitializedObjects, expectedReadOnlyObjects, s);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Assert.assertFalse(s.isDefaultReadOnly());
        Container c = ((Container) (s.get(Container.class, cOrig.getId())));
        Assert.assertNotSame(cOrig, c);
        expectedInitializedObjects = new HashSet(Arrays.asList(c, c.getNonLazyInfo(), c.getNoProxyOwner(), c.getProxyOwner(), c.getNonLazyOwner(), c.getNonLazyJoinDataPoints().iterator().next(), c.getNonLazySelectDataPoints().iterator().next()));
        expectedReadOnlyObjects = new HashSet();
        List list = s.createFilter(c.getLazyDataPoints(), "").setMaxResults(1).list();
        Assert.assertEquals(1, list.size());
        Assert.assertFalse(s.isReadOnly(list.get(0)));
        list = s.createFilter(c.getNonLazyJoinDataPoints(), "").setMaxResults(1).list();
        Assert.assertEquals(1, list.size());
        Assert.assertFalse(s.isReadOnly(list.get(0)));
        list = s.createFilter(c.getNonLazySelectDataPoints(), "").setMaxResults(1).list();
        Assert.assertEquals(1, list.size());
        Assert.assertFalse(s.isReadOnly(list.get(0)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.createQuery("delete from Container").executeUpdate();
        s.createQuery("delete from Info").executeUpdate();
        s.createQuery("delete from Owner").executeUpdate();
        t.commit();
        s.close();
    }
}

