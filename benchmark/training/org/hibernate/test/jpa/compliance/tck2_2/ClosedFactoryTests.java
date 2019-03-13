/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.jpa.compliance.tck2_2;


import AvailableSettings.JPA_CLOSED_COMPLIANCE;
import java.util.Collections;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "12097")
public class ClosedFactoryTests extends BaseUnitTestCase {
    @Test
    public void testClosedChecks() {
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(JPA_CLOSED_COMPLIANCE, "true").build();
        try {
            final SessionFactoryBuilderImplementor factoryBuilder = ((SessionFactoryBuilderImplementor) (buildMetadata().getSessionFactoryBuilder()));
            final SessionFactory sf = factoryBuilder.build();
            sf.close();
            Assert.assertTrue(sf.isClosed());
            // we now have a closed SF (EMF)... test the closed checks in various methods
            try {
                sf.getCache();
                Assert.fail("#getCache did not fail");
            } catch (IllegalStateException expected) {
                // this is the expected outcome
            } catch (Exception e) {
                Assert.fail(("#getCache failed, but not with the expected IllegalStateException : " + (e.toString())));
            }
            try {
                sf.getMetamodel();
                Assert.fail("#getMetamodel did not fail");
            } catch (IllegalStateException expected) {
                // this is the expected outcome
            } catch (Exception e) {
                Assert.fail(("#getMetamodel failed, but not with the expected IllegalStateException : " + (e.toString())));
            }
            try {
                sf.getCriteriaBuilder();
                Assert.fail("#getCriteriaBuilder did not fail");
            } catch (IllegalStateException expected) {
                // this is the expected outcome
            } catch (Exception e) {
                Assert.fail(("#getCriteriaBuilder failed, but not with the expected IllegalStateException : " + (e.toString())));
            }
            try {
                sf.getProperties();
                Assert.fail("#getProperties did not fail");
            } catch (IllegalStateException expected) {
                // this is the expected outcome
            } catch (Exception e) {
                Assert.fail(("#getProperties failed, but not with the expected IllegalStateException : " + (e.toString())));
            }
            try {
                sf.getPersistenceUnitUtil();
                Assert.fail("#getPersistenceUnitUtil did not fail");
            } catch (IllegalStateException expected) {
                // this is the expected outcome
            } catch (Exception e) {
                Assert.fail(("#getPersistenceUnitUtil failed, but not with the expected IllegalStateException : " + (e.toString())));
            }
            try {
                sf.close();
                Assert.fail("#close did not fail");
            } catch (IllegalStateException expected) {
                // this is the expected outcome
            } catch (Exception e) {
                Assert.fail(("#close failed, but not with the expected IllegalStateException : " + (e.toString())));
            }
            try {
                sf.createEntityManager();
                Assert.fail("#createEntityManager did not fail");
            } catch (IllegalStateException expected) {
                // this is the expected outcome
            } catch (Exception e) {
                Assert.fail(("#createEntityManager failed, but not with the expected IllegalStateException : " + (e.toString())));
            }
            try {
                sf.createEntityManager(Collections.emptyMap());
                Assert.fail("#createEntityManager(Map) did not fail");
            } catch (IllegalStateException expected) {
                // this is the expected outcome
            } catch (Exception e) {
                Assert.fail(("#createEntityManager(Map) failed, but not with the expected IllegalStateException : " + (e.toString())));
            }
        } catch (Exception e) {
            // if an exception is
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

