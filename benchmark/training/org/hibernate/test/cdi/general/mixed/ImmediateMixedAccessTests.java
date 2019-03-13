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
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.resource.beans.container.internal.CdiBeanContainerBuilder;
import org.hibernate.resource.beans.container.internal.CdiBeanContainerImmediateAccessImpl;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.junit.Test;


/**
 * Tests for "mixed access" to both hosted and non-hosted beans.
 *
 * @author Steve Ebersole
 */
public class ImmediateMixedAccessTests implements BeanContainer.LifecycleOptions {
    @Test
    public void testImmediateMixedAccess() {
        try (final SeContainer cdiContainer = Helper.createSeContainer()) {
            BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
            final StandardServiceRegistry ssr = new org.hibernate.boot.registry.StandardServiceRegistryBuilder(bsr).applySetting(HBM2DDL_AUTO, CREATE_DROP).applySetting(CDI_BEAN_MANAGER, cdiContainer.getBeanManager()).build();
            final BeanContainer beanContainer = CdiBeanContainerBuilder.fromBeanManagerReference(cdiContainer.getBeanManager(), ssr);
            MatcherAssert.assertThat(beanContainer, CoreMatchers.instanceOf(CdiBeanContainerImmediateAccessImpl.class));
            final ContainedBean<HostedBean> hostedBean = beanContainer.getBean(HostedBean.class, this, INSTANCE);
            MatcherAssert.assertThat(hostedBean, CoreMatchers.notNullValue());
            MatcherAssert.assertThat(hostedBean.getBeanInstance(), CoreMatchers.notNullValue());
            MatcherAssert.assertThat(hostedBean.getBeanInstance().getInjectedHostedBean(), CoreMatchers.notNullValue());
            final ContainedBean<NonHostedBean> nonHostedBean = beanContainer.getBean(NonHostedBean.class, this, INSTANCE);
            MatcherAssert.assertThat(nonHostedBean, CoreMatchers.notNullValue());
            MatcherAssert.assertThat(nonHostedBean.getBeanInstance(), CoreMatchers.notNullValue());
        }
    }
}

