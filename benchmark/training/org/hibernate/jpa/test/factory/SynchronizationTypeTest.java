/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.factory;


import SynchronizationType.SYNCHRONIZED;
import SynchronizationType.UNSYNCHRONIZED;
import java.util.Collections;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SynchronizationTypeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testPassingSynchronizationType() {
        try {
            entityManagerFactory().createEntityManager(SYNCHRONIZED);
            Assert.fail("Call should have throw ISE");
        } catch (IllegalStateException expected) {
        }
        try {
            entityManagerFactory().createEntityManager(UNSYNCHRONIZED);
            Assert.fail("Call should have throw ISE");
        } catch (IllegalStateException expected) {
        }
        try {
            entityManagerFactory().createEntityManager(SYNCHRONIZED, Collections.emptyMap());
            Assert.fail("Call should have throw ISE");
        } catch (IllegalStateException expected) {
        }
        try {
            entityManagerFactory().createEntityManager(UNSYNCHRONIZED, Collections.emptyMap());
            Assert.fail("Call should have throw ISE");
        } catch (IllegalStateException expected) {
        }
    }
}

