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
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.event.spi.jpa.ExtendedBeanManager;
import org.hibernate.test.cdi.events.Monitor;
import org.hibernate.test.cdi.events.TheEntity;
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
public class InvalidExtendedCdiSupportTest extends BaseUnitTestCase {
    @Test
    public void testIt() {
        Monitor.reset();
        final InvalidExtendedCdiSupportTest.ExtendedBeanManagerImpl standIn = new InvalidExtendedCdiSupportTest.ExtendedBeanManagerImpl();
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
            // this time (compared to the valid test) lets not build the CDI
            // container and just let Hibernate try to use the uninitialized
            // ExtendedBeanManager reference
            try {
                TransactionUtil2.inTransaction(sessionFactory, ( session) -> session.persist(new TheEntity(1)));
                TransactionUtil2.inTransaction(sessionFactory, ( session) -> {
                    session.createQuery("delete TheEntity").executeUpdate();
                });
                Assert.fail("Expecting failure");
            } catch (IllegalStateException expected) {
            }
        } finally {
            sessionFactory.close();
        }
    }

    public static class ExtendedBeanManagerImpl implements ExtendedBeanManager {
        @Override
        public void registerLifecycleListener(LifecycleListener lifecycleListener) {
        }
    }
}

