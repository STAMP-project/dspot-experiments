/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ejb3configuration.id;


import AvailableSettings.IDENTIFIER_GENERATOR_STRATEGY_PROVIDER;
import AvailableSettings.LOADED_CLASSES;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.test.PersistenceUnitInfoAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard <emmanuel@hibernate.org>
 */
public class IdentifierGeneratorStrategyProviderTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testIdentifierGeneratorStrategyProvider() {
        Map settings = new HashMap();
        settings.put(IDENTIFIER_GENERATOR_STRATEGY_PROVIDER, FunkyIdentifierGeneratorProvider.class.getName());
        settings.put(LOADED_CLASSES, Collections.singletonList(Cable.class));
        final EntityManagerFactory entityManagerFactory = Bootstrap.getEntityManagerFactoryBuilder(new PersistenceUnitInfoAdapter(), settings).build();
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.persist(new Cable());
            entityManager.flush();
            Assert.fail("FunkyException should have been thrown when the id is generated");
        } catch (FunkyException e) {
            entityManager.close();
            entityManagerFactory.close();
        }
    }
}

