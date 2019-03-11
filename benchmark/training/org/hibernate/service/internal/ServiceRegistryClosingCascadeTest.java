/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.service.internal;


import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ServiceRegistryClosingCascadeTest extends BaseUnitTestCase {
    @Test
    public void testSessionFactoryClosing() {
        BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        StandardServiceRegistry sr = build();
        Assert.assertTrue(isActive());
        Configuration config = new Configuration();
        SessionFactory sf = config.buildSessionFactory(sr);
        sf.close();
        Assert.assertFalse(isActive());
    }
}

