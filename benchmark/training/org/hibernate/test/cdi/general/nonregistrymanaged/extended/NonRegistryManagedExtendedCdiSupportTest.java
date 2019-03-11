/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.general.nonregistrymanaged.extended;


import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.BeanManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.event.spi.jpa.ExtendedBeanManager;
import org.hibernate.test.cdi.general.nonregistrymanaged.Monitor;
import org.hibernate.test.cdi.general.nonregistrymanaged.NonRegistryManagedBeanConsumingIntegrator;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheAlternativeNamedApplicationScopedBeanImpl;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheAlternativeNamedDependentBeanImpl;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheApplicationScopedBean;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheDependentBean;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheFallbackBeanInstanceProducer;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheMainNamedApplicationScopedBeanImpl;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheMainNamedDependentBeanImpl;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheNamedApplicationScopedBean;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheNamedDependentBean;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheNestedDependentBean;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheNonHibernateBeanConsumer;
import org.hibernate.test.cdi.general.nonregistrymanaged.TheSharedApplicationScopedBean;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests support for requesting CDI beans from the {@link ManagedBeanRegistry}
 * when the CDI BeanManager access is "lazy" (beans are instantiated when instances are first requested),
 * and when the registry should not manage the lifecycle of beans, but leave it up to CDI.
 *
 * @author Steve Ebersole
 * @author Yoann Rodiere
 */
public class NonRegistryManagedExtendedCdiSupportTest extends BaseUnitTestCase {
    @Test
    public void testIt() {
        Monitor.reset();
        final NonRegistryManagedExtendedCdiSupportTest.ExtendedBeanManagerImpl standIn = new NonRegistryManagedExtendedCdiSupportTest.ExtendedBeanManagerImpl();
        final TheFallbackBeanInstanceProducer fallbackBeanInstanceProducer = new TheFallbackBeanInstanceProducer();
        final NonRegistryManagedBeanConsumingIntegrator beanConsumingIntegrator = new NonRegistryManagedBeanConsumingIntegrator(fallbackBeanInstanceProducer);
        try (SessionFactoryImplementor sessionFactory = buildSessionFactory(standIn, beanConsumingIntegrator)) {
            final SeContainerInitializer cdiInitializer = SeContainerInitializer.newInstance().disableDiscovery().addBeanClasses(TheApplicationScopedBean.class).addBeanClasses(TheNamedApplicationScopedBean.class, TheMainNamedApplicationScopedBeanImpl.class, TheAlternativeNamedApplicationScopedBeanImpl.class).addBeanClasses(TheSharedApplicationScopedBean.class).addBeanClasses(TheDependentBean.class).addBeanClasses(TheNamedDependentBean.class, TheMainNamedDependentBeanImpl.class, TheAlternativeNamedDependentBeanImpl.class).addBeanClasses(TheNestedDependentBean.class).addBeanClasses(TheNonHibernateBeanConsumer.class);
            try (final SeContainer cdiContainer = cdiInitializer.initialize()) {
                // Simulate CDI bean consumers outside of Hibernate ORM
                Instance<TheNonHibernateBeanConsumer> nonHibernateBeanConsumerInstance = cdiContainer.getBeanManager().createInstance().select(TheNonHibernateBeanConsumer.class);
                nonHibernateBeanConsumerInstance.get();
                // Here, the NonRegistryManagedBeanConsumingIntegrator has just been integrated and has requested beans
                // BUT it has not fetched instances of beans yet, so non-shared beans should not have been instantiated yet.
                Assert.assertEquals(0, Monitor.theApplicationScopedBean().currentInstantiationCount());
                Assert.assertEquals(0, Monitor.theMainNamedApplicationScopedBean().currentInstantiationCount());
                Assert.assertEquals(0, Monitor.theAlternativeNamedApplicationScopedBean().currentInstantiationCount());
                Assert.assertEquals(1, Monitor.theSharedApplicationScopedBean().currentInstantiationCount());
                Assert.assertEquals(0, Monitor.theDependentBean().currentInstantiationCount());
                Assert.assertEquals(0, Monitor.theMainNamedDependentBean().currentInstantiationCount());
                Assert.assertEquals(0, Monitor.theAlternativeNamedDependentBean().currentInstantiationCount());
                Assert.assertEquals(0, fallbackBeanInstanceProducer.currentInstantiationCount());
                Assert.assertEquals(0, fallbackBeanInstanceProducer.currentNamedInstantiationCount());
                // Nested dependent bean: 1 instance per bean that depends on it
                Assert.assertEquals(1, Monitor.theNestedDependentBean().currentInstantiationCount());
                standIn.beanManagerReady(cdiContainer.getBeanManager());
                beanConsumingIntegrator.ensureInstancesInitialized();
                // Here the NonRegistryManagedBeanConsumingIntegrator *did* fetch an instance of each bean,
                // so all beans should have been instantiated.
                // See NonRegistryManagedBeanConsumingIntegrator for a detailed list of requested beans
                // Application scope: maximum 1 instance as soon as at least one was requested
                Assert.assertEquals(1, Monitor.theApplicationScopedBean().currentInstantiationCount());
                Assert.assertEquals(1, Monitor.theMainNamedApplicationScopedBean().currentInstantiationCount());
                Assert.assertEquals(0, Monitor.theAlternativeNamedApplicationScopedBean().currentInstantiationCount());
                Assert.assertEquals(1, Monitor.theSharedApplicationScopedBean().currentInstantiationCount());
                // Dependent scope: 1 instance per bean we requested explicitly
                Assert.assertEquals(2, Monitor.theDependentBean().currentInstantiationCount());
                Assert.assertEquals(2, Monitor.theMainNamedDependentBean().currentInstantiationCount());
                Assert.assertEquals(0, Monitor.theAlternativeNamedDependentBean().currentInstantiationCount());
                // Reflection-instantiated: 1 instance per bean we requested explicitly
                Assert.assertEquals(2, fallbackBeanInstanceProducer.currentInstantiationCount());
                Assert.assertEquals(2, fallbackBeanInstanceProducer.currentNamedInstantiationCount());
                // Nested dependent bean: 1 instance per bean that depends on it
                Assert.assertEquals(7, Monitor.theNestedDependentBean().currentInstantiationCount());
                // Expect one PostConstruct call per CDI bean instance
                Assert.assertEquals(1, Monitor.theApplicationScopedBean().currentPostConstructCount());
                Assert.assertEquals(1, Monitor.theMainNamedApplicationScopedBean().currentPostConstructCount());
                Assert.assertEquals(0, Monitor.theAlternativeNamedApplicationScopedBean().currentPostConstructCount());
                Assert.assertEquals(1, Monitor.theSharedApplicationScopedBean().currentPostConstructCount());
                Assert.assertEquals(2, Monitor.theDependentBean().currentPostConstructCount());
                Assert.assertEquals(2, Monitor.theMainNamedDependentBean().currentPostConstructCount());
                Assert.assertEquals(0, Monitor.theAlternativeNamedDependentBean().currentPostConstructCount());
                Assert.assertEquals(7, Monitor.theNestedDependentBean().currentPostConstructCount());
                // Expect no PreDestroy call yet
                Assert.assertEquals(0, Monitor.theApplicationScopedBean().currentPreDestroyCount());
                Assert.assertEquals(0, Monitor.theMainNamedApplicationScopedBean().currentPreDestroyCount());
                Assert.assertEquals(0, Monitor.theAlternativeNamedApplicationScopedBean().currentPreDestroyCount());
                Assert.assertEquals(0, Monitor.theSharedApplicationScopedBean().currentPreDestroyCount());
                Assert.assertEquals(0, Monitor.theDependentBean().currentPreDestroyCount());
                Assert.assertEquals(0, Monitor.theMainNamedDependentBean().currentPreDestroyCount());
                Assert.assertEquals(0, Monitor.theAlternativeNamedDependentBean().currentPreDestroyCount());
                Assert.assertEquals(0, Monitor.theNestedDependentBean().currentPreDestroyCount());
            }
            // After the CDI context has ended, PreDestroy should have been called on every "normal-scoped" CDI bean
            // (i.e. all CDI beans excepting the dependent ones we requested explicitly and haven't released yet)
            Assert.assertEquals(1, Monitor.theApplicationScopedBean().currentPreDestroyCount());
            Assert.assertEquals(1, Monitor.theMainNamedApplicationScopedBean().currentPreDestroyCount());
            Assert.assertEquals(0, Monitor.theAlternativeNamedApplicationScopedBean().currentPreDestroyCount());
            Assert.assertEquals(1, Monitor.theSharedApplicationScopedBean().currentPreDestroyCount());
            Assert.assertEquals(0, Monitor.theDependentBean().currentPreDestroyCount());
            Assert.assertEquals(0, Monitor.theMainNamedDependentBean().currentPreDestroyCount());
            Assert.assertEquals(0, Monitor.theAlternativeNamedDependentBean().currentPreDestroyCount());
            Assert.assertEquals(3, Monitor.theNestedDependentBean().currentPreDestroyCount());
        }
        // Here, the NonRegistryManagedBeanConsumingIntegrator has just been disintegrated and has released beans
        // The dependent beans should now have been released as well.
        Assert.assertEquals(1, Monitor.theApplicationScopedBean().currentPreDestroyCount());
        Assert.assertEquals(1, Monitor.theMainNamedApplicationScopedBean().currentPreDestroyCount());
        Assert.assertEquals(0, Monitor.theAlternativeNamedApplicationScopedBean().currentPreDestroyCount());
        Assert.assertEquals(1, Monitor.theSharedApplicationScopedBean().currentPreDestroyCount());
        Assert.assertEquals(2, Monitor.theDependentBean().currentPreDestroyCount());
        Assert.assertEquals(2, Monitor.theMainNamedDependentBean().currentPreDestroyCount());
        Assert.assertEquals(0, Monitor.theAlternativeNamedDependentBean().currentPreDestroyCount());
        Assert.assertEquals(7, Monitor.theNestedDependentBean().currentPreDestroyCount());
    }

    public static class ExtendedBeanManagerImpl implements ExtendedBeanManager {
        private LifecycleListener callback;

        @Override
        public void registerLifecycleListener(LifecycleListener lifecycleListener) {
            this.callback = lifecycleListener;
        }

        public void beanManagerReady(BeanManager beanManager) {
            callback.beanManagerInitialized(beanManager);
        }
    }
}

