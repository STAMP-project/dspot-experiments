/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.packaging;


import AvailableSettings.JTA_DATASOURCE;
import EventType.PRE_INSERT;
import EventType.PRE_UPDATE;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.internal.util.ConfigHelper;
import org.hibernate.jpa.test.Distributor;
import org.hibernate.jpa.test.Item;
import org.hibernate.jpa.test.pack.cfgxmlpar.Morito;
import org.hibernate.jpa.test.pack.defaultpar.ApplicationServer;
import org.hibernate.jpa.test.pack.defaultpar.IncrementListener;
import org.hibernate.jpa.test.pack.defaultpar.Lighter;
import org.hibernate.jpa.test.pack.defaultpar.Money;
import org.hibernate.jpa.test.pack.defaultpar.Mouse;
import org.hibernate.jpa.test.pack.defaultpar.OtherIncrementListener;
import org.hibernate.jpa.test.pack.defaultpar.Version;
import org.hibernate.jpa.test.pack.defaultpar_1_0.ApplicationServer1;
import org.hibernate.jpa.test.pack.defaultpar_1_0.Lighter1;
import org.hibernate.jpa.test.pack.defaultpar_1_0.Mouse1;
import org.hibernate.jpa.test.pack.defaultpar_1_0.Version1;
import org.hibernate.jpa.test.pack.excludehbmpar.Caipirinha;
import org.hibernate.jpa.test.pack.explodedpar.Carpet;
import org.hibernate.jpa.test.pack.explodedpar.Elephant;
import org.hibernate.jpa.test.pack.externaljar.Scooter;
import org.hibernate.jpa.test.pack.spacepar.Bug;
import org.hibernate.jpa.test.pack.various.Airplane;
import org.hibernate.jpa.test.pack.various.Seat;
import org.hibernate.stat.Statistics;
import org.junit.Assert;
import org.junit.Test;


/**
 * In this test we verify that  it is possible to bootstrap Hibernate/JPA from
 * various bundles (war, par, ...) using {@code Persistence.createEntityManagerFactory()}
 * <p/>
 * Each test will before its run build the required bundle and place them into the classpath.
 *
 * @author Gavin King
 * @author Hardy Ferentschik
 */
@SuppressWarnings("unchecked")
public class PackagedEntityManagerTest extends PackagingTestCase {
    private EntityManagerFactory emf;

    @Test
    public void testDefaultPar() throws Exception {
        File testPackage = buildDefaultPar();
        addPackageToClasspath(testPackage);
        // run the test
        emf = Persistence.createEntityManagerFactory("defaultpar", new HashMap());
        EntityManager em = emf.createEntityManager();
        ApplicationServer as = new ApplicationServer();
        as.setName("JBoss AS");
        Version v = new Version();
        v.setMajor(4);
        v.setMinor(0);
        v.setMicro(3);
        as.setVersion(v);
        Mouse mouse = new Mouse();
        mouse.setName("mickey");
        em.getTransaction().begin();
        em.persist(as);
        em.persist(mouse);
        Assert.assertEquals(1, em.createNamedQuery("allMouse").getResultList().size());
        Lighter lighter = new Lighter();
        lighter.name = "main";
        lighter.power = " 250 W";
        em.persist(lighter);
        em.flush();
        em.remove(lighter);
        em.remove(mouse);
        Assert.assertNotNull(as.getId());
        em.remove(as);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Test
    public void testDefaultParForPersistence_1_0() throws Exception {
        File testPackage = buildDefaultPar_1_0();
        addPackageToClasspath(testPackage);
        emf = Persistence.createEntityManagerFactory("defaultpar_1_0", new HashMap());
        EntityManager em = emf.createEntityManager();
        ApplicationServer1 as = new ApplicationServer1();
        as.setName("JBoss AS");
        Version1 v = new Version1();
        v.setMajor(4);
        v.setMinor(0);
        v.setMicro(3);
        as.setVersion(v);
        Mouse1 mouse = new Mouse1();
        mouse.setName("mickey");
        em.getTransaction().begin();
        em.persist(as);
        em.persist(mouse);
        Assert.assertEquals(1, em.createNamedQuery("allMouse_1_0").getResultList().size());
        Lighter1 lighter = new Lighter1();
        lighter.name = "main";
        lighter.power = " 250 W";
        em.persist(lighter);
        em.flush();
        em.remove(lighter);
        em.remove(mouse);
        Assert.assertNotNull(as.getId());
        em.remove(as);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Test
    public void testListenersDefaultPar() throws Exception {
        File testPackage = buildDefaultPar();
        addPackageToClasspath(testPackage);
        IncrementListener.reset();
        OtherIncrementListener.reset();
        emf = Persistence.createEntityManagerFactory("defaultpar", new HashMap());
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        ApplicationServer as = new ApplicationServer();
        as.setName("JBoss AS");
        Version v = new Version();
        v.setMajor(4);
        v.setMinor(0);
        v.setMicro(3);
        as.setVersion(v);
        em.persist(as);
        em.flush();
        Assert.assertEquals("Failure in default listeners", 1, IncrementListener.getIncrement());
        Assert.assertEquals("Failure in XML overriden listeners", 1, OtherIncrementListener.getIncrement());
        Mouse mouse = new Mouse();
        mouse.setName("mickey");
        em.persist(mouse);
        em.flush();
        Assert.assertEquals("Failure in @ExcludeDefaultListeners", 1, IncrementListener.getIncrement());
        Assert.assertEquals(1, OtherIncrementListener.getIncrement());
        Money money = new Money();
        em.persist(money);
        em.flush();
        Assert.assertEquals("Failure in @ExcludeDefaultListeners", 2, IncrementListener.getIncrement());
        Assert.assertEquals(1, OtherIncrementListener.getIncrement());
        em.getTransaction().rollback();
        em.close();
        emf.close();
    }

    @Test
    public void testExplodedPar() throws Exception {
        File testPackage = buildExplodedPar();
        addPackageToClasspath(testPackage);
        emf = Persistence.createEntityManagerFactory("explodedpar", new HashMap());
        EntityManager em = emf.createEntityManager();
        Carpet carpet = new Carpet();
        Elephant el = new Elephant();
        el.setName("Dumbo");
        carpet.setCountry("Turkey");
        em.getTransaction().begin();
        em.persist(carpet);
        em.persist(el);
        Assert.assertEquals(1, em.createNamedQuery("allCarpet").getResultList().size());
        Assert.assertNotNull(carpet.getId());
        em.remove(carpet);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Test
    public void testExcludeHbmPar() throws Exception {
        File testPackage = buildExcludeHbmPar();
        addPackageToClasspath(testPackage);
        try {
            emf = Persistence.createEntityManagerFactory("excludehbmpar", new HashMap());
        } catch (PersistenceException e) {
            emf.close();
            Throwable nested = e.getCause();
            if (nested == null) {
                throw e;
            }
            nested = nested.getCause();
            if (nested == null) {
                throw e;
            }
            if (!(nested instanceof ClassNotFoundException)) {
                throw e;
            }
            Assert.fail(("Try to process hbm file: " + (e.getMessage())));
        }
        EntityManager em = emf.createEntityManager();
        Caipirinha s = new Caipirinha("Strong");
        em.getTransaction().begin();
        em.persist(s);
        em.getTransaction().commit();
        em.getTransaction().begin();
        s = em.find(Caipirinha.class, s.getId());
        em.remove(s);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Test
    public void testCfgXmlPar() throws Exception {
        File testPackage = buildCfgXmlPar();
        addPackageToClasspath(testPackage);
        emf = Persistence.createEntityManagerFactory("cfgxmlpar", new HashMap());
        Assert.assertTrue(emf.getProperties().containsKey("hibernate.test-assertable-setting"));
        EntityManager em = emf.createEntityManager();
        Item i = new Item();
        i.setDescr("Blah");
        i.setName("factory");
        Morito m = new Morito();
        m.setPower("SuperStrong");
        em.getTransaction().begin();
        em.persist(i);
        em.persist(m);
        em.getTransaction().commit();
        em.getTransaction().begin();
        i = em.find(Item.class, i.getName());
        em.remove(i);
        em.remove(em.find(Morito.class, m.getId()));
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Test
    public void testSpacePar() throws Exception {
        File testPackage = buildSpacePar();
        addPackageToClasspath(testPackage);
        emf = Persistence.createEntityManagerFactory("space par", new HashMap());
        EntityManager em = emf.createEntityManager();
        Bug bug = new Bug();
        bug.setSubject("Spaces in directory name don't play well on Windows");
        em.getTransaction().begin();
        em.persist(bug);
        em.flush();
        em.remove(bug);
        Assert.assertNotNull(bug.getId());
        em.getTransaction().rollback();
        em.close();
        emf.close();
    }

    @Test
    public void testOverriddenPar() throws Exception {
        File testPackage = buildOverridenPar();
        addPackageToClasspath(testPackage);
        HashMap properties = new HashMap();
        properties.put(JTA_DATASOURCE, null);
        Properties p = new Properties();
        p.load(ConfigHelper.getResourceAsStream("/overridenpar.properties"));
        properties.putAll(p);
        emf = Persistence.createEntityManagerFactory("overridenpar", properties);
        EntityManager em = emf.createEntityManager();
        org.hibernate.jpa.test.pack.overridenpar.Bug bug = new org.hibernate.jpa.test.pack.overridenpar.Bug();
        bug.setSubject("Allow DS overriding");
        em.getTransaction().begin();
        em.persist(bug);
        em.flush();
        em.remove(bug);
        Assert.assertNotNull(bug.getId());
        em.getTransaction().rollback();
        em.close();
        emf.close();
    }

    @Test
    public void testListeners() throws Exception {
        File testPackage = buildExplicitPar();
        addPackageToClasspath(testPackage);
        emf = Persistence.createEntityManagerFactory("manager1", new HashMap());
        EntityManager em = emf.createEntityManager();
        EventListenerRegistry listenerRegistry = em.unwrap(SharedSessionContractImplementor.class).getFactory().getServiceRegistry().getService(EventListenerRegistry.class);
        Assert.assertEquals("Explicit pre-insert event through hibernate.ejb.event.pre-insert does not work", listenerRegistry.getEventListenerGroup(PRE_INSERT).count(), ((listenerRegistry.getEventListenerGroup(PRE_UPDATE).count()) + 1));
        em.close();
        emf.close();
    }

    @Test
    public void testExtendedEntityManager() throws Exception {
        File testPackage = buildExplicitPar();
        addPackageToClasspath(testPackage);
        emf = Persistence.createEntityManagerFactory("manager1", new HashMap());
        EntityManager em = emf.createEntityManager();
        Item item = new Item("Mouse", "Micro$oft mouse");
        em.getTransaction().begin();
        em.persist(item);
        Assert.assertTrue(em.contains(item));
        em.getTransaction().commit();
        Assert.assertTrue(em.contains(item));
        em.getTransaction().begin();
        Item item1 = ((Item) (em.createQuery("select i from Item i where descr like 'M%'").getSingleResult()));
        Assert.assertNotNull(item1);
        Assert.assertSame(item, item1);
        item.setDescr("Micro$oft wireless mouse");
        Assert.assertTrue(em.contains(item));
        em.getTransaction().commit();
        Assert.assertTrue(em.contains(item));
        em.getTransaction().begin();
        item1 = em.find(Item.class, "Mouse");
        Assert.assertSame(item, item1);
        em.getTransaction().commit();
        Assert.assertTrue(em.contains(item));
        item1 = em.find(Item.class, "Mouse");
        Assert.assertSame(item, item1);
        Assert.assertTrue(em.contains(item));
        item1 = ((Item) (em.createQuery("select i from Item i where descr like 'M%'").getSingleResult()));
        Assert.assertNotNull(item1);
        Assert.assertSame(item, item1);
        Assert.assertTrue(em.contains(item));
        em.getTransaction().begin();
        Assert.assertTrue(em.contains(item));
        em.remove(item);
        em.remove(item);// second remove should be a no-op

        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Test
    public void testConfiguration() throws Exception {
        File testPackage = buildExplicitPar();
        addPackageToClasspath(testPackage);
        emf = Persistence.createEntityManagerFactory("manager1", new HashMap());
        Item item = new Item("Mouse", "Micro$oft mouse");
        Distributor res = new Distributor();
        res.setName("Bruce");
        item.setDistributors(new HashSet<Distributor>());
        item.getDistributors().add(res);
        Statistics stats = getSessionFactory().getStatistics();
        stats.clear();
        stats.setStatisticsEnabled(true);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(res);
        em.persist(item);
        Assert.assertTrue(em.contains(item));
        em.getTransaction().commit();
        em.close();
        Assert.assertEquals(1, stats.getSecondLevelCachePutCount());
        Assert.assertEquals(0, stats.getSecondLevelCacheHitCount());
        em = emf.createEntityManager();
        em.getTransaction().begin();
        Item second = em.find(Item.class, item.getName());
        Assert.assertEquals(1, second.getDistributors().size());
        Assert.assertEquals(1, stats.getSecondLevelCacheHitCount());
        em.getTransaction().commit();
        em.close();
        em = emf.createEntityManager();
        em.getTransaction().begin();
        second = em.find(Item.class, item.getName());
        Assert.assertEquals(1, second.getDistributors().size());
        Assert.assertEquals(3, stats.getSecondLevelCacheHitCount());
        for (Distributor distro : second.getDistributors()) {
            em.remove(distro);
        }
        em.remove(second);
        em.getTransaction().commit();
        em.close();
        stats.clear();
        stats.setStatisticsEnabled(false);
        emf.close();
    }

    @Test
    public void testExternalJar() throws Exception {
        File externalJar = buildExternalJar();
        File testPackage = buildExplicitPar();
        addPackageToClasspath(testPackage, externalJar);
        emf = Persistence.createEntityManagerFactory("manager1", new HashMap());
        EntityManager em = emf.createEntityManager();
        Scooter s = new Scooter();
        s.setModel("Abadah");
        s.setSpeed(85L);
        em.getTransaction().begin();
        em.persist(s);
        em.getTransaction().commit();
        em.close();
        em = emf.createEntityManager();
        em.getTransaction().begin();
        s = em.find(Scooter.class, s.getModel());
        Assert.assertEquals(Long.valueOf(85), s.getSpeed());
        em.remove(s);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Test
    public void testRelativeJarReferences() throws Exception {
        File externalJar = buildExternalJar2();
        File testPackage = buildExplicitPar2();
        addPackageToClasspath(testPackage, externalJar);
        // if the jar cannot be resolved, this call should fail
        emf = Persistence.createEntityManagerFactory("manager1", new HashMap());
        // but to make sure, also verify that the entity defined in the external jar was found
        emf.getMetamodel().entity(Airplane.class);
        emf.getMetamodel().entity(Scooter.class);
        // additionally, try to use them
        EntityManager em = emf.createEntityManager();
        Scooter s = new Scooter();
        s.setModel("Abadah");
        s.setSpeed(85L);
        em.getTransaction().begin();
        em.persist(s);
        em.getTransaction().commit();
        em.close();
        em = emf.createEntityManager();
        em.getTransaction().begin();
        s = em.find(Scooter.class, s.getModel());
        Assert.assertEquals(Long.valueOf(85), s.getSpeed());
        em.remove(s);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Test
    public void testORMFileOnMainAndExplicitJars() throws Exception {
        File testPackage = buildExplicitPar();
        addPackageToClasspath(testPackage);
        emf = Persistence.createEntityManagerFactory("manager1", new HashMap());
        EntityManager em = emf.createEntityManager();
        Seat seat = new Seat();
        seat.setNumber("3B");
        Airplane plane = new Airplane();
        plane.setSerialNumber("75924418409052355");
        em.getTransaction().begin();
        em.persist(seat);
        em.persist(plane);
        em.flush();
        em.getTransaction().rollback();
        em.close();
        emf.close();
    }
}

