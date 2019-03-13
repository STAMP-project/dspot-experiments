/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.batch;


import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 * This is how to do batch processing in Hibernate. Remember to enable JDBC batch updates, or this test will take a
 * VeryLongTime!
 *
 * @author Gavin King
 */
public class BatchTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testBatchInsertUpdate() {
        long start = System.currentTimeMillis();
        final int N = 5000;// 26 secs with batch flush, 26 without

        // final int N = 100000; //53 secs with batch flush, OOME without
        // final int N = 250000; //137 secs with batch flush, OOME without
        int batchSize = sessionFactory().getSettings().getJdbcBatchSize();
        doBatchInsertUpdate(N, batchSize);
        System.out.println(((System.currentTimeMillis()) - start));
    }

    @Test
    public void testBatchInsertUpdateSizeEqJdbcBatchSize() {
        int batchSize = sessionFactory().getSettings().getJdbcBatchSize();
        doBatchInsertUpdate(50, batchSize);
    }

    @Test
    public void testBatchInsertUpdateSizeLtJdbcBatchSize() {
        int batchSize = sessionFactory().getSettings().getJdbcBatchSize();
        doBatchInsertUpdate(50, (batchSize - 1));
    }

    @Test
    public void testBatchInsertUpdateSizeGtJdbcBatchSize() {
        int batchSize = sessionFactory().getSettings().getJdbcBatchSize();
        doBatchInsertUpdate(50, (batchSize + 1));
    }
}

