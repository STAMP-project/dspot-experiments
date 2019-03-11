/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cascade.circle;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 * The test case uses the following model:
 *
 *                          <-    ->
 *                      -- (N : 0,1) -- Tour
 *                      |    <-   ->
 *                      | -- (1 : N) -- (pickup) ----
 *               ->     | |                          |
 * Route -- (1 : N) -- Node                      Transport
 *                      |  <-   ->                |
 *                      -- (1 : N) -- (delivery) --
 *
 *  Arrows indicate the direction of cascade-merge, cascade-save, and cascade-save-or-update
 *
 * It reproduced the following issues:
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-3046
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-3810
 * <p/>
 * This tests that cascades are done properly from each entity.
 *
 * @author Pavol Zibrita, Gail Badner
 */
public class MultiPathCircleCascadeTest extends BaseCoreFunctionalTestCase {
    private static interface EntityOperation {
        boolean isLegacy();

        Object doEntityOperation(Object entity, Session s);
    }

    private static MultiPathCircleCascadeTest.EntityOperation MERGE_OPERATION = new MultiPathCircleCascadeTest.EntityOperation() {
        @Override
        public boolean isLegacy() {
            return false;
        }

        @Override
        public Object doEntityOperation(Object entity, Session s) {
            return s.merge(entity);
        }
    };

    private static MultiPathCircleCascadeTest.EntityOperation SAVE_OPERATION = new MultiPathCircleCascadeTest.EntityOperation() {
        @Override
        public boolean isLegacy() {
            return true;
        }

        @Override
        public Object doEntityOperation(Object entity, Session s) {
            s.save(entity);
            return entity;
        }
    };

    private static MultiPathCircleCascadeTest.EntityOperation SAVE_UPDATE_OPERATION = new MultiPathCircleCascadeTest.EntityOperation() {
        @Override
        public boolean isLegacy() {
            return true;
        }

        @Override
        public Object doEntityOperation(Object entity, Session s) {
            s.saveOrUpdate(entity);
            return entity;
        }
    };

    @Test
    public void testMergeEntityWithNonNullableTransientEntity() {
        testEntityWithNonNullableTransientEntity(MultiPathCircleCascadeTest.MERGE_OPERATION);
    }

    @Test
    public void testSaveEntityWithNonNullableTransientEntity() {
        testEntityWithNonNullableTransientEntity(MultiPathCircleCascadeTest.SAVE_OPERATION);
    }

    @Test
    public void testSaveUpdateEntityWithNonNullableTransientEntity() {
        testEntityWithNonNullableTransientEntity(MultiPathCircleCascadeTest.SAVE_UPDATE_OPERATION);
    }

    @Test
    public void testMergeEntityWithNonNullableEntityNull() {
        testEntityWithNonNullableEntityNull(MultiPathCircleCascadeTest.MERGE_OPERATION);
    }

    @Test
    public void testSaveEntityWithNonNullableEntityNull() {
        testEntityWithNonNullableEntityNull(MultiPathCircleCascadeTest.SAVE_OPERATION);
    }

    @Test
    public void testSaveUpdateEntityWithNonNullableEntityNull() {
        testEntityWithNonNullableEntityNull(MultiPathCircleCascadeTest.SAVE_UPDATE_OPERATION);
    }

    @Test
    public void testMergeEntityWithNonNullablePropSetToNull() {
        testEntityWithNonNullablePropSetToNull(MultiPathCircleCascadeTest.MERGE_OPERATION);
    }

    @Test
    public void testSaveEntityWithNonNullablePropSetToNull() {
        testEntityWithNonNullablePropSetToNull(MultiPathCircleCascadeTest.SAVE_OPERATION);
    }

    @Test
    public void testSaveUpdateEntityWithNonNullablePropSetToNull() {
        testEntityWithNonNullablePropSetToNull(MultiPathCircleCascadeTest.SAVE_UPDATE_OPERATION);
    }

    @Test
    public void testMergeRoute() {
        testRoute(MultiPathCircleCascadeTest.MERGE_OPERATION);
    }

    // skip SAVE_OPERATION since Route is not transient
    @Test
    public void testSaveUpdateRoute() {
        testRoute(MultiPathCircleCascadeTest.SAVE_UPDATE_OPERATION);
    }

    @Test
    public void testMergePickupNode() {
        testPickupNode(MultiPathCircleCascadeTest.MERGE_OPERATION);
    }

    @Test
    public void testSavePickupNode() {
        testPickupNode(MultiPathCircleCascadeTest.SAVE_OPERATION);
    }

    @Test
    public void testSaveUpdatePickupNode() {
        testPickupNode(MultiPathCircleCascadeTest.SAVE_UPDATE_OPERATION);
    }

    @Test
    public void testMergeDeliveryNode() {
        testDeliveryNode(MultiPathCircleCascadeTest.MERGE_OPERATION);
    }

    @Test
    public void testSaveDeliveryNode() {
        testDeliveryNode(MultiPathCircleCascadeTest.SAVE_OPERATION);
    }

    @Test
    public void testSaveUpdateDeliveryNode() {
        testDeliveryNode(MultiPathCircleCascadeTest.SAVE_UPDATE_OPERATION);
    }

    @Test
    public void testMergeTour() {
        testTour(MultiPathCircleCascadeTest.MERGE_OPERATION);
    }

    @Test
    public void testSaveTour() {
        testTour(MultiPathCircleCascadeTest.SAVE_OPERATION);
    }

    @Test
    public void testSaveUpdateTour() {
        testTour(MultiPathCircleCascadeTest.SAVE_UPDATE_OPERATION);
    }

    @Test
    public void testMergeTransport() {
        testTransport(MultiPathCircleCascadeTest.MERGE_OPERATION);
    }

    @Test
    public void testSaveTransport() {
        testTransport(MultiPathCircleCascadeTest.SAVE_OPERATION);
    }

    @Test
    public void testSaveUpdateTransport() {
        testTransport(MultiPathCircleCascadeTest.SAVE_UPDATE_OPERATION);
    }

    @Test
    public void testMergeData3Nodes() {
        testData3Nodes(MultiPathCircleCascadeTest.MERGE_OPERATION);
    }

    @Test
    public void testSaveData3Nodes() {
        testData3Nodes(MultiPathCircleCascadeTest.SAVE_OPERATION);
    }

    @Test
    public void testSaveUpdateData3Nodes() {
        testData3Nodes(MultiPathCircleCascadeTest.SAVE_UPDATE_OPERATION);
    }
}

