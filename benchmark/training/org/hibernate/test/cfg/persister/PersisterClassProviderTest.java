/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cfg.persister;


import SessionFactoryRegistry.INSTANCE;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.persister.spi.PersisterClassResolver;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard <emmanuel@hibernate.org>
 */
public class PersisterClassProviderTest extends BaseUnitTestCase {
    @Test
    public void testPersisterClassProvider() throws Exception {
        Configuration cfg = new Configuration();
        cfg.addAnnotatedClass(Gate.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
        // no exception as the GoofyPersisterClassProvider is not set
        SessionFactory sessionFactory;
        try {
            sessionFactory = cfg.buildSessionFactory(serviceRegistry);
            sessionFactory.close();
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).addService(PersisterClassResolver.class, new GoofyPersisterClassProvider()).build();
        cfg = new Configuration();
        cfg.addAnnotatedClass(Gate.class);
        try {
            sessionFactory = cfg.buildSessionFactory(serviceRegistry);
            sessionFactory.close();
            Assert.fail("The entity persister should be overridden");
        } catch (MappingException e) {
            Assert.assertEquals("The entity persister should be overridden", GoofyPersisterClassProvider.NoopEntityPersister.class, ((GoofyException) (e.getCause())).getValue());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
        Assert.assertFalse(INSTANCE.hasRegistrations());
        cfg = new Configuration();
        cfg.addAnnotatedClass(Portal.class);
        cfg.addAnnotatedClass(Window.class);
        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).addService(PersisterClassResolver.class, new GoofyPersisterClassProvider()).build();
        try {
            sessionFactory = cfg.buildSessionFactory(serviceRegistry);
            sessionFactory.close();
            Assert.fail("The collection persister should be overridden but not the entity persister");
        } catch (MappingException e) {
            Assert.assertEquals("The collection persister should be overridden but not the entity persister", GoofyPersisterClassProvider.NoopCollectionPersister.class, ((GoofyException) (e.getCause())).getValue());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
        cfg = new Configuration();
        cfg.addAnnotatedClass(Tree.class);
        cfg.addAnnotatedClass(Palmtree.class);
        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).addService(PersisterClassResolver.class, new GoofyPersisterClassProvider()).build();
        try {
            sessionFactory = cfg.buildSessionFactory(serviceRegistry);
            sessionFactory.close();
            Assert.fail("The entity persisters should be overridden in a class hierarchy");
        } catch (MappingException e) {
            Assert.assertEquals("The entity persisters should be overridden in a class hierarchy", GoofyPersisterClassProvider.NoopEntityPersister.class, ((GoofyException) (e.getCause())).getValue());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
        Assert.assertFalse(INSTANCE.hasRegistrations());
    }
}

