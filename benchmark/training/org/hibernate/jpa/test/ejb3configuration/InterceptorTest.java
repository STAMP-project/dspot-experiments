/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ejb3configuration;


import AvailableSettings.INTERCEPTOR;
import AvailableSettings.SESSION_INTERCEPTOR;
import java.util.Map;
import java.util.function.Supplier;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.AvailableSettings.SESSION_SCOPED_INTERCEPTOR;
import org.hibernate.jpa.test.Item;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class InterceptorTest {
    private EntityManagerFactory entityManagerFactory;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // test deprecated Interceptor settings
    @Test
    public void testDeprecatedConfiguredInterceptor() {
        Map settings = basicSettings();
        settings.put(INTERCEPTOR, ExceptionInterceptor.class.getName());
        buildEntityManagerFactory(settings);
        Item i = new Item();
        i.setName("Laptop");
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                entityManager.persist(i);
                fail("No interceptor");
                return null;
            });
        } catch (IllegalStateException e) {
            Assert.assertEquals(ExceptionInterceptor.EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testDeprecatedConfiguredSessionInterceptor() {
        Map settings = basicSettings();
        settings.put(SESSION_INTERCEPTOR, LocalExceptionInterceptor.class.getName());
        buildEntityManagerFactory(settings);
        Item i = new Item();
        i.setName("Laptop");
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                entityManager.persist(i);
                fail("No interceptor");
                return null;
            });
        } catch (IllegalStateException e) {
            Assert.assertEquals(LocalExceptionInterceptor.LOCAL_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // test Interceptor settings
    @Test
    public void testConfiguredInterceptor() {
        Map settings = basicSettings();
        settings.put(org.hibernate.cfg.AvailableSettings.INTERCEPTOR, ExceptionInterceptor.class.getName());
        buildEntityManagerFactory(settings);
        Item i = new Item();
        i.setName("Laptop");
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                entityManager.persist(i);
                fail("No interceptor");
                return null;
            });
        } catch (IllegalStateException e) {
            Assert.assertEquals(ExceptionInterceptor.EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testConfiguredSessionInterceptor() {
        Map settings = basicSettings();
        settings.put(SESSION_SCOPED_INTERCEPTOR, LocalExceptionInterceptor.class.getName());
        buildEntityManagerFactory(settings);
        Item i = new Item();
        i.setName("Laptop");
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                entityManager.persist(i);
                fail("No interceptor");
                return null;
            });
        } catch (IllegalStateException e) {
            Assert.assertEquals(LocalExceptionInterceptor.LOCAL_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testConfiguredSessionInterceptorWithSessionFactory() {
        StandardServiceRegistryImpl standardRegistry = ((StandardServiceRegistryImpl) (new StandardServiceRegistryBuilder().build()));
        SessionFactory sessionFactory = null;
        try {
            MetadataSources metadataSources = new MetadataSources(standardRegistry);
            for (Class annotatedClass : getAnnotatedClasses()) {
                metadataSources.addAnnotatedClass(annotatedClass);
            }
            Metadata metadata = metadataSources.getMetadataBuilder().build();
            SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();
            sessionFactoryBuilder.applyStatelessInterceptor(LocalExceptionInterceptor.class);
            sessionFactory = sessionFactoryBuilder.build();
            final SessionFactory sessionFactoryInstance = sessionFactory;
            Supplier<SessionFactory> sessionFactorySupplier = () -> sessionFactoryInstance;
            Item i = new Item();
            i.setName("Laptop");
            try {
                TransactionUtil.doInHibernate(sessionFactorySupplier, ( session) -> {
                    session.persist(i);
                    fail("No interceptor");
                    return null;
                });
            } catch (IllegalStateException e) {
                Assert.assertEquals(LocalExceptionInterceptor.LOCAL_EXCEPTION_MESSAGE, e.getMessage());
            }
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
            standardRegistry.destroy();
        }
    }

    @Test
    public void testConfiguredSessionInterceptorSupplier() {
        Map settings = basicSettings();
        settings.put(SESSION_SCOPED_INTERCEPTOR, ((Supplier<Interceptor>) (LocalExceptionInterceptor::new)));
        buildEntityManagerFactory(settings);
        Item i = new Item();
        i.setName("Laptop");
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                entityManager.persist(i);
                fail("No interceptor");
                return null;
            });
        } catch (IllegalStateException e) {
            Assert.assertEquals(LocalExceptionInterceptor.LOCAL_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testEmptyCreateEntityManagerFactoryAndPropertyUse() {
        Map settings = basicSettings();
        settings.put(INTERCEPTOR, ExceptionInterceptor.class.getName());
        buildEntityManagerFactory(settings);
        Item i = new Item();
        i.setName("Laptop");
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                entityManager.persist(i);
                fail("No interceptor");
                return null;
            });
        } catch (IllegalStateException e) {
            Assert.assertEquals(ExceptionInterceptor.EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testOnLoadCallInInterceptor() {
        Map settings = basicSettings();
        settings.put(INTERCEPTOR, new ExceptionInterceptor(true));
        buildEntityManagerFactory(settings);
        Item i = new Item();
        i.setName("Laptop");
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                entityManager.persist(i);
                entityManager.persist(i);
                entityManager.flush();
                entityManager.clear();
                try {
                    entityManager.find(.class, i.getName());
                    fail("No interceptor");
                } catch ( e) {
                    assertEquals(ExceptionInterceptor.EXCEPTION_MESSAGE, e.getMessage());
                }
            });
        } catch (IllegalStateException e) {
            Assert.assertEquals(LocalExceptionInterceptor.LOCAL_EXCEPTION_MESSAGE, e.getMessage());
        }
    }
}

