/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.osgi.test;


import java.util.Calendar;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import org.apache.karaf.features.BootFinished;
import org.apache.karaf.features.FeaturesService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.osgi.test.client.AuditedDataPoint;
import org.hibernate.osgi.test.client.DataPoint;
import org.hibernate.osgi.test.client.SomeService;
import org.hibernate.osgi.test.client.TestIntegrator;
import org.hibernate.osgi.test.client.TestStrategyRegistrationProvider;
import org.hibernate.osgi.test.client.TestTypeContributor;
import org.hibernate.type.BasicType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


/**
 * Tests for hibernate-osgi running within a Karaf container via PaxExam.
 *
 * @author Steve Ebersole
 * @author Brett Meyer
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OsgiIntegrationTest {
    private static final boolean DEBUG = false;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // The tests
    @Inject
    protected FeaturesService featuresService;

    @Inject
    BootFinished bootFinished;

    @Inject
    @SuppressWarnings("UnusedDeclaration")
    private BundleContext bundleContext;

    @Test
    public void testActivation() throws Exception {
        Assert.assertTrue(featuresService.isInstalled(featuresService.getFeature("hibernate-orm")));
        Assert.assertTrue(featuresService.isInstalled(featuresService.getFeature("hibernate-envers")));
        assertActiveBundle("org.hibernate.orm.core");
        assertActiveBundle("org.hibernate.orm.envers");
    }

    @Test
    public void testJpa() throws Exception {
        final ServiceReference serviceReference = bundleContext.getServiceReference(PersistenceProvider.class.getName());
        final PersistenceProvider persistenceProvider = ((PersistenceProvider) (bundleContext.getService(serviceReference)));
        final EntityManagerFactory emf = persistenceProvider.createEntityManagerFactory("hibernate-osgi-test", null);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(new DataPoint("Brett"));
        em.getTransaction().commit();
        em.close();
        em = emf.createEntityManager();
        em.getTransaction().begin();
        DataPoint dp = em.find(DataPoint.class, 1);
        Assert.assertNotNull(dp);
        Assert.assertEquals("Brett", dp.getName());
        em.getTransaction().commit();
        em.close();
        em = emf.createEntityManager();
        em.getTransaction().begin();
        dp = em.find(DataPoint.class, 1);
        dp.setName("Brett2");
        em.getTransaction().commit();
        em.close();
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from DataPoint").executeUpdate();
        em.getTransaction().commit();
        em.close();
        em = emf.createEntityManager();
        em.getTransaction().begin();
        dp = em.find(DataPoint.class, 1);
        Assert.assertNull(dp);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testNative() throws Exception {
        final ServiceReference sr = bundleContext.getServiceReference(SessionFactory.class.getName());
        final SessionFactory sf = ((SessionFactory) (bundleContext.getService(sr)));
        Session s = sf.openSession();
        s.getTransaction().begin();
        s.persist(new DataPoint("Brett"));
        s.getTransaction().commit();
        s.close();
        s = sf.openSession();
        s.getTransaction().begin();
        DataPoint dp = ((DataPoint) (s.get(DataPoint.class, 1)));
        Assert.assertNotNull(dp);
        Assert.assertEquals("Brett", dp.getName());
        s.getTransaction().commit();
        s.close();
        dp.setName("Brett2");
        s = sf.openSession();
        s.getTransaction().begin();
        s.update(dp);
        s.getTransaction().commit();
        s.close();
        s = sf.openSession();
        s.getTransaction().begin();
        dp = ((DataPoint) (s.get(DataPoint.class, 1)));
        Assert.assertNotNull(dp);
        Assert.assertEquals("Brett2", dp.getName());
        s.getTransaction().commit();
        s.close();
        s = sf.openSession();
        s.getTransaction().begin();
        s.createQuery("delete from DataPoint").executeUpdate();
        s.getTransaction().commit();
        s.close();
        s = sf.openSession();
        s.getTransaction().begin();
        dp = ((DataPoint) (s.get(DataPoint.class, 1)));
        Assert.assertNull(dp);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testNativeEnvers() throws Exception {
        final ServiceReference sr = bundleContext.getServiceReference(SessionFactory.class.getName());
        final SessionFactory sf = ((SessionFactory) (bundleContext.getService(sr)));
        final Integer adpId;
        Session s = sf.openSession();
        s.getTransaction().begin();
        AuditedDataPoint adp = new AuditedDataPoint("Chris");
        s.persist(adp);
        s.getTransaction().commit();
        adpId = adp.getId();
        s.close();
        s = sf.openSession();
        s.getTransaction().begin();
        adp = s.get(AuditedDataPoint.class, adpId);
        adp.setName("Chris2");
        s.getTransaction().commit();
        s.close();
        s = sf.openSession();
        AuditReader ar = AuditReaderFactory.get(s);
        Assert.assertEquals(2, ar.getRevisions(AuditedDataPoint.class, adpId).size());
        AuditedDataPoint rev1 = ar.find(AuditedDataPoint.class, adpId, 1);
        AuditedDataPoint rev2 = ar.find(AuditedDataPoint.class, adpId, 2);
        Assert.assertEquals(new AuditedDataPoint(adpId, "Chris"), rev1);
        Assert.assertEquals(new AuditedDataPoint(adpId, "Chris2"), rev2);
        s.close();
    }

    @Test
    public void testJpaEnvers() throws Exception {
        final ServiceReference serviceReference = bundleContext.getServiceReference(PersistenceProvider.class.getName());
        final PersistenceProvider persistenceProvider = ((PersistenceProvider) (bundleContext.getService(serviceReference)));
        final EntityManagerFactory emf = persistenceProvider.createEntityManagerFactory("hibernate-osgi-test", null);
        final Integer adpId;
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        AuditedDataPoint adp = new AuditedDataPoint("Chris");
        em.persist(adp);
        em.getTransaction().commit();
        adpId = adp.getId();
        em.close();
        em = emf.createEntityManager();
        em.getTransaction().begin();
        adp = em.find(AuditedDataPoint.class, adpId);
        adp.setName("Chris2");
        em.getTransaction().commit();
        em.close();
        em = emf.createEntityManager();
        AuditReader ar = AuditReaderFactory.get(em);
        Assert.assertEquals(2, ar.getRevisions(AuditedDataPoint.class, adpId).size());
        AuditedDataPoint rev1 = ar.find(AuditedDataPoint.class, adpId, 1);
        AuditedDataPoint rev2 = ar.find(AuditedDataPoint.class, adpId, 2);
        Assert.assertEquals(new AuditedDataPoint(adpId, "Chris"), rev1);
        Assert.assertEquals(new AuditedDataPoint(adpId, "Chris2"), rev2);
        em.close();
    }

    @Test
    public void testExtensionPoints() throws Exception {
        final ServiceReference sr = bundleContext.getServiceReference(SessionFactory.class.getName());
        final SessionFactoryImplementor sfi = ((SessionFactoryImplementor) (bundleContext.getService(sr)));
        Assert.assertTrue(TestIntegrator.passed());
        Class impl = sfi.getServiceRegistry().getService(StrategySelector.class).selectStrategyImplementor(Calendar.class, TestStrategyRegistrationProvider.GREGORIAN);
        Assert.assertNotNull(impl);
        BasicType basicType = sfi.getTypeResolver().basic(TestTypeContributor.NAME);
        Assert.assertNotNull(basicType);
    }

    @Test
    public void testServiceContributorDiscovery() throws Exception {
        final ServiceReference sr = bundleContext.getServiceReference(SessionFactory.class.getName());
        final SessionFactoryImplementor sfi = ((SessionFactoryImplementor) (bundleContext.getService(sr)));
        Assert.assertNotNull(sfi.getServiceRegistry().getService(SomeService.class));
    }
}

