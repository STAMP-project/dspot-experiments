/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.events.extended;


import Action.CREATE_DROP;
import AvailableSettings.CDI_BEAN_MANAGER;
import AvailableSettings.HBM2DDL_AUTO;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.BeanManager;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.event.spi.jpa.ExtendedBeanManager;
import org.hibernate.test.cdi.events.Monitor;
import org.hibernate.test.cdi.events.TheEntity;
import org.hibernate.test.cdi.events.TheListener;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.transaction.TransactionUtil2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests support for CDI delaying access to the CDI container until
 * first needed
 *
 * @author Steve Ebersole
 */
public class ValidExtendedCdiSupportTest extends BaseUnitTestCase {
    @Test
    public void testIt() {
        Monitor.reset();
        final ValidExtendedCdiSupportTest.ExtendedBeanManagerImpl standIn = new ValidExtendedCdiSupportTest.ExtendedBeanManagerImpl();
        BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder(bsr).applySetting(HBM2DDL_AUTO, CREATE_DROP).applySetting(CDI_BEAN_MANAGER, standIn).build();
        final SessionFactoryImplementor sessionFactory;
        try {
            sessionFactory = ((SessionFactoryImplementor) (addAnnotatedClass(TheEntity.class).buildMetadata().getSessionFactoryBuilder().build()));
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(ssr);
            throw e;
        }
        try {
            // The CDI bean should not be built immediately...
            Assert.assertFalse(Monitor.wasInstantiated());
            Assert.assertEquals(0, Monitor.currentCount());
            // But now lets initialize CDI and do the callback
            final SeContainerInitializer cdiInitializer = SeContainerInitializer.newInstance().disableDiscovery().addBeanClasses(Monitor.class, TheListener.class);
            try (final SeContainer cdiContainer = cdiInitializer.initialize()) {
                standIn.beanManagerReady(cdiContainer.getBeanManager());
                // at this point the bean should have been accessed
                Assert.assertTrue(Monitor.wasInstantiated());
                Assert.assertEquals(0, Monitor.currentCount());
                try {
                    TransactionUtil2.inTransaction(sessionFactory, ( session) -> session.persist(new TheEntity(1)));
                    TransactionUtil2.inTransaction(sessionFactory, ( session) -> {
                        TheEntity it = session.find(.class, 1);
                        assertNotNull(it);
                    });
                } finally {
                    TransactionUtil2.inTransaction(sessionFactory, ( session) -> {
                        session.createQuery("delete TheEntity").executeUpdate();
                    });
                }
            }
        } finally {
            sessionFactory.close();
        }
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

