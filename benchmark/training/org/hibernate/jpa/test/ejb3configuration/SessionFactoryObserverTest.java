/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ejb3configuration;


import AvailableSettings.SESSION_FACTORY_OBSERVER;
import java.util.Collections;
import javax.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.test.PersistenceUnitInfoAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard <emmanuel@hibernate.org>
 */
public class SessionFactoryObserverTest {
    @Test
    public void testSessionFactoryObserverProperty() {
        EntityManagerFactoryBuilder builder = Bootstrap.getEntityManagerFactoryBuilder(new PersistenceUnitInfoAdapter(), Collections.singletonMap(SESSION_FACTORY_OBSERVER, SessionFactoryObserverTest.GoofySessionFactoryObserver.class.getName()));
        try {
            final EntityManagerFactory entityManagerFactory = builder.build();
            entityManagerFactory.close();
            Assert.fail("GoofyException should have been thrown");
        } catch (SessionFactoryObserverTest.GoofyException e) {
            // success
        }
    }

    public static class GoofySessionFactoryObserver implements SessionFactoryObserver {
        public void sessionFactoryCreated(SessionFactory factory) {
        }

        public void sessionFactoryClosed(SessionFactory factory) {
            throw new SessionFactoryObserverTest.GoofyException();
        }
    }

    public static class GoofyException extends RuntimeException {}
}

