/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.events.nocdi;


import Action.CREATE_DROP;
import AvailableSettings.HBM2DDL_AUTO;
import org.hamcrest.CoreMatchers;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.test.cdi.events.Monitor;
import org.hibernate.test.cdi.events.TheEntity;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Attempt to use CDI injection when no CDI is available
 *
 * @author Steve Ebersole
 */
public class InvalidNoCdiSupportTest extends BaseUnitTestCase {
    @Test
    public void testIt() {
        Monitor.reset();
        BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder(bsr).applySetting(HBM2DDL_AUTO, CREATE_DROP).build();
        final SessionFactoryImplementor sessionFactory;
        try {
            // because there is no CDI available, building the SF should immediately
            // try to build the ManagedBeans which should fail here
            sessionFactory = ((SessionFactoryImplementor) (addAnnotatedClass(TheEntity.class).buildMetadata().getSessionFactoryBuilder().build()));
            sessionFactory.close();
            Assert.fail("Expecting failure");
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(ssr);
            Assert.assertThat(e, CoreMatchers.instanceOf(InstantiationException.class));
        }
    }
}

