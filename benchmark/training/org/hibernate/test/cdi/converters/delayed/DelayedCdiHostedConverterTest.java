/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.converters.delayed;


import Action.CREATE_DROP;
import AvailableSettings.CDI_BEAN_MANAGER;
import AvailableSettings.DELAY_CDI_ACCESS;
import AvailableSettings.HBM2DDL_AUTO;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.test.cdi.converters.ConverterBean;
import org.hibernate.test.cdi.converters.MonitorBean;
import org.hibernate.test.cdi.converters.TheEntity;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.transaction.TransactionUtil2;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class DelayedCdiHostedConverterTest extends BaseUnitTestCase {
    @Test
    public void testIt() {
        MonitorBean.reset();
        final SeContainerInitializer cdiInitializer = SeContainerInitializer.newInstance().disableDiscovery().addBeanClasses(MonitorBean.class, ConverterBean.class);
        try (final SeContainer cdiContainer = cdiInitializer.initialize()) {
            BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
            final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder(bsr).applySetting(HBM2DDL_AUTO, CREATE_DROP).applySetting(CDI_BEAN_MANAGER, cdiContainer.getBeanManager()).applySetting(DELAY_CDI_ACCESS, "true").build();
            final SessionFactoryImplementor sessionFactory;
            try {
                sessionFactory = ((SessionFactoryImplementor) (addAnnotatedClass(TheEntity.class).buildMetadata().getSessionFactoryBuilder().build()));
            } catch (Exception e) {
                StandardServiceRegistryBuilder.destroy(ssr);
                throw e;
            }
            // The CDI bean should not have been built immediately...
            Assert.assertFalse(MonitorBean.wasInstantiated());
            Assert.assertEquals(0, MonitorBean.currentFromDbCount());
            Assert.assertEquals(0, MonitorBean.currentToDbCount());
            try {
                TransactionUtil2.inTransaction(sessionFactory, ( session) -> session.persist(new TheEntity(1, "me", 5)));
                // The CDI bean should have been built on first use
                Assert.assertTrue(MonitorBean.wasInstantiated());
                Assert.assertEquals(0, MonitorBean.currentFromDbCount());
                Assert.assertEquals(1, MonitorBean.currentToDbCount());
                TransactionUtil2.inTransaction(sessionFactory, ( session) -> {
                    TheEntity it = session.find(.class, 1);
                    assertNotNull(it);
                });
                Assert.assertEquals(1, MonitorBean.currentFromDbCount());
                Assert.assertEquals(1, MonitorBean.currentToDbCount());
            } finally {
                TransactionUtil2.inTransaction(sessionFactory, ( session) -> {
                    session.createQuery("delete TheEntity").executeUpdate();
                });
                sessionFactory.close();
            }
        }
    }
}

