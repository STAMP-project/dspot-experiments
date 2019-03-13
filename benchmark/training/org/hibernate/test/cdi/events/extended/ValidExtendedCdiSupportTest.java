/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.events.extended;


import org.hibernate.test.cdi.testsupport.TestingExtendedBeanManager;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 * Tests support for CDI delaying access to the CDI container until
 * first needed
 *
 * @author Steve Ebersole
 */
public class ValidExtendedCdiSupportTest extends BaseUnitTestCase {
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

