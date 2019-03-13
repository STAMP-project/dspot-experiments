/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.beanvalidation;


import AvailableSettings.VALIDATION_FACTORY;
import java.net.URL;
import java.util.Collections;
import javax.persistence.EntityManagerFactory;
import javax.validation.ValidatorFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.test.jpa.xml.versions.JpaXsdVersionsTest;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test injection of ValidatorFactory using WF/Hibernate 2-phase boot process
 *
 * @author Steve Ebersole
 */
public class ValidatorFactory2PhaseInjectionTest extends BaseUnitTestCase {
    private ValidatorFactory vf;

    @Test
    public void testInjectionAvailabilityFromEmf() {
        EntityManagerFactoryBuilder emfb = Bootstrap.getEntityManagerFactoryBuilder(new JpaXsdVersionsTest.PersistenceUnitInfoImpl("my-test") {
            @Override
            public URL getPersistenceUnitRootUrl() {
                // just get any known url...
                return HibernatePersistenceProvider.class.getResource("/org/hibernate/jpa/persistence_1_0.xsd");
            }
        }, Collections.emptyMap());
        emfb.withValidatorFactory(vf);
        EntityManagerFactory emf = emfb.build();
        try {
            Assert.assertSame(vf, emf.getProperties().get(VALIDATION_FACTORY));
        } finally {
            emf.close();
        }
    }
}

