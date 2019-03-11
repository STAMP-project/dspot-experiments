/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.optlock;


import DialectChecks.DoesRepeatableReadNotCauseReadersToBlockWritersCheck;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 * Tests relating to the optimistic-lock mapping option.
 *
 * @author Gavin King
 * @author Steve Ebersole
 */
@RequiresDialectFeature(value = DoesRepeatableReadNotCauseReadersToBlockWritersCheck.class, comment = "potential deadlock")
public class OptimisticLockTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOptimisticLockDirty() {
        testUpdateOptimisticLockFailure("LockDirty");
    }

    @Test
    public void testOptimisticLockAll() {
        testUpdateOptimisticLockFailure("LockAll");
    }

    @Test
    public void testOptimisticLockDirtyDelete() {
        testDeleteOptimisticLockFailure("LockDirty");
    }

    @Test
    public void testOptimisticLockAllDelete() {
        testDeleteOptimisticLockFailure("LockAll");
    }
}

