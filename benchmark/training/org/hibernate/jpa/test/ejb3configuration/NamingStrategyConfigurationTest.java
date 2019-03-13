/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ejb3configuration;


import AvailableSettings.PHYSICAL_NAMING_STRATEGY;
import java.util.Collections;
import javax.persistence.EntityManagerFactory;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.test.MyNamingStrategy;
import org.hibernate.jpa.test.PersistenceUnitInfoAdapter;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class NamingStrategyConfigurationTest extends BaseUnitTestCase {
    @Test
    public void testNamingStrategyFromProperty() {
        // configure NamingStrategy
        {
            PersistenceUnitInfoAdapter adapter = new PersistenceUnitInfoAdapter();
            EntityManagerFactoryBuilderImpl builder = ((EntityManagerFactoryBuilderImpl) (Bootstrap.getEntityManagerFactoryBuilder(adapter, Collections.singletonMap(PHYSICAL_NAMING_STRATEGY, MyNamingStrategy.class.getName()))));
            final EntityManagerFactory emf = builder.build();
            try {
                Assert.assertEquals(MyNamingStrategy.class.getName(), builder.getConfigurationValues().get(PHYSICAL_NAMING_STRATEGY));
                ExtraAssertions.assertTyping(MyNamingStrategy.class, builder.getMetadata().getMetadataBuildingOptions().getPhysicalNamingStrategy());
            } finally {
                if (emf != null) {
                    emf.close();
                }
            }
        }
    }
}

