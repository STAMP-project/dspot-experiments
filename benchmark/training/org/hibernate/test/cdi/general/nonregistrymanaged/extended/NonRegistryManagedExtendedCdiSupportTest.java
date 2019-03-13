/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.general.nonregistrymanaged.extended;


import org.hibernate.test.cdi.testsupport.TestingExtendedBeanManager;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 * Tests support for requesting CDI beans from the {@link ManagedBeanRegistry}
 * when the CDI BeanManager access is "lazy" (beans are instantiated when instances are first requested),
 * and when the registry should not manage the lifecycle of beans, but leave it up to CDI.
 *
 * @author Steve Ebersole
 * @author Yoann Rodiere
 */
public class NonRegistryManagedExtendedCdiSupportTest extends BaseUnitTestCase {
    @Test
    public void test() {
        doTest(TestingExtendedBeanManager.create());
    }

    /**
     * NOTE : we use the deprecated one here to make sure this continues to work.
     * Scott still uses this in WildFly and we need it to continue to work there
     */
    @Test
    public void testLegacy() {
        doTest(TestingExtendedBeanManager.createLegacy());
    }
}

