/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.general.mixed;


import Action.CREATE_DROP;
import AvailableSettings.CDI_BEAN_MANAGER;
import AvailableSettings.HBM2DDL_AUTO;
import FallbackBeanInstanceProducer.INSTANCE;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.BeanManager;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.resource.beans.container.internal.CdiBeanContainerExtendedAccessImpl;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ExtendedMixedAccessTest implements BeanContainer.LifecycleOptions {
    @Test
    public void testExtendedMixedAccess() {
        final Helper.TestingExtendedBeanManager extendedBeanManager = Helper.createExtendedBeanManager();
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(HBM2DDL_AUTO, CREATE_DROP).applySetting(CDI_BEAN_MANAGER, extendedBeanManager).build();
        final BeanContainer beanContainer = ssr.getService(ManagedBeanRegistry.class).getBeanContainer();
        MatcherAssert.assertThat(beanContainer, CoreMatchers.instanceOf(CdiBeanContainerExtendedAccessImpl.class));
        try (final SeContainer cdiContainer = Helper.createSeContainer()) {
            final BeanManager beanManager = cdiContainer.getBeanManager();
            extendedBeanManager.notifyListenerReady(beanManager);
            MatcherAssert.assertThat(beanManager, CoreMatchers.sameInstance(getUsableBeanManager()));
            final ContainedBean<HostedBean> hostedBean = beanContainer.getBean(HostedBean.class, this, INSTANCE);
            MatcherAssert.assertThat(hostedBean, CoreMatchers.notNullValue());
            MatcherAssert.assertThat(hostedBean.getBeanInstance(), CoreMatchers.notNullValue());
            MatcherAssert.assertThat(hostedBean.getBeanInstance().getInjectedHostedBean(), CoreMatchers.notNullValue());
            final ContainedBean<NonHostedBean> nonHostedBean = beanContainer.getBean(NonHostedBean.class, this, INSTANCE);
            MatcherAssert.assertThat(nonHostedBean, CoreMatchers.notNullValue());
            MatcherAssert.assertThat(nonHostedBean.getBeanInstance(), CoreMatchers.notNullValue());
            extendedBeanManager.notifyListenerShuttingDown(beanManager);
        }
    }
}

