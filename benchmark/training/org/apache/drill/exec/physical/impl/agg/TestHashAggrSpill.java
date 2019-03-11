/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.impl.agg;


import UserBitShared.DrillPBError.ErrorType;
import junit.framework.TestCase;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.DrillTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test spilling for the Hash Aggr operator (using the mock reader)
 */
@Category({ SlowTest.class, OperatorTest.class })
public class TestHashAggrSpill extends DrillTest {
    @Rule
    public final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    /**
     * Test "normal" spilling: Only 2 (or 3) partitions (out of 4) would require spilling
     * ("normal spill" means spill-cycle = 1 )
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSimpleHashAggrSpill() throws Exception {
        testSpill(68000000, 16, 2, 2, false, true, null, 1200000, 1, 2, 3);
    }

    /**
     * Test with "needed memory" prediction turned off
     * (i.e., do exercise code paths that catch OOMs from the Hash Table and recover)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoPredictHashAggrSpill() throws Exception {
        /* no prediction */
        testSpill(58000000, 16, 2, 2, false, false, null, 1200000, 1, 1, 1);
    }

    /**
     * Test Secondary and Tertiary spill cycles - Happens when some of the spilled partitions cause more spilling as they are read back
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHashAggrSecondaryTertiarySpill() throws Exception {
        testSpill(58000000, 16, 3, 1, false, true, "SELECT empid_s44, dept_i, branch_i, AVG(salary_i) FROM `mock`.`employee_1100K` GROUP BY empid_s44, dept_i, branch_i", 1100000, 3, 2, 2);
    }

    /**
     * Test with the "fallback" option disabled: When not enough memory available to allow spilling, then fail (Resource error) !!
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHashAggrFailWithFallbackDisabed() throws Exception {
        try {
            /* no fallback */
            /* no spill due to fallback to pre-1.11 */
            testSpill(34000000, 4, 5, 2, false, true, null, 1200000, 0, 0, 0);
            TestCase.fail();// in case the above test did not throw

        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof UserRemoteException));
            Assert.assertTrue(((getErrorType()) == (ErrorType.RESOURCE)));
            // must get here for the test to succeed ...
        }
    }

    /**
     * Test with the "fallback" option ON: When not enough memory is available to allow spilling (internally need enough memory to
     * create multiple partitions), then behave like the pre-1.11 Hash Aggregate: Allocate unlimited memory, no spill.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHashAggrSuccessWithFallbackEnabled() throws Exception {
        /* do fallback */
        /* no spill due to fallback to pre-1.11 */
        testSpill(34000000, 4, 5, 2, true, true, null, 1200000, 0, 0, 0);
    }
}

