/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.general.mixed;


import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.test.cdi.testsupport.TestingExtendedBeanManager;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ExtendedMixedAccessTest implements BeanContainer.LifecycleOptions {
    @Test
    public void testExtendedMixedAccess() {
        doTest(TestingExtendedBeanManager.create());
    }

    /**
     * NOTE : we use the deprecated one here to make sure this continues to work.
     * Scott still uses this in WildFly and we need it to continue to work there
     */
    @Test
    public void testLegacyExtendedMixedAccess() {
        doTest(TestingExtendedBeanManager.createLegacy());
    }
}

