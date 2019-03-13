/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.boot;


import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.hibernate.internal.util.ConfigHelper;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.service.spi.ServiceException;
import org.junit.Test;


/**
 * Test to verify that a dump configuration error results in an exception being
 * thrown even when booting via the standard JPA boostrap API.
 *
 * @author Andrea Boriero
 * @author Sanne Grinovero
 */
public class BootFailureTest extends BaseEntityManagerFunctionalTestCase {
    @Test(expected = ServiceException.class)
    public void exceptionOnIllegalPUTest() {
        bootstrapPersistenceUnit("IntentionallyBroken");
    }

    @Test(expected = ServiceException.class)
    public void exceptionOnIllegalPUWithoutProviderTest() {
        bootstrapPersistenceUnit("IntentionallyBrokenWihoutExplicitProvider");
    }

    private static class TestClassLoader extends ClassLoader {
        static final List<URL> urls = Arrays.asList(ConfigHelper.findAsResource("org/hibernate/jpa/test/bootstrap/META-INF/persistence.xml"));

        @Override
        protected Enumeration<URL> findResources(String name) {
            return name.equals("META-INF/persistence.xml") ? Collections.enumeration(BootFailureTest.TestClassLoader.urls) : Collections.emptyEnumeration();
        }
    }
}

