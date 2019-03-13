/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package tests.api.java.math;


import java.math.BigDecimal;
import java.math.RoundingMode;
import junit.framework.TestCase;


public class RoundingModeTest extends TestCase {
    /**
     * java.math.RoundingMode#valueOf(int)
     */
    public void test_valueOfI() {
        TestCase.assertEquals("valueOf failed for ROUND_CEILING", RoundingMode.valueOf(BigDecimal.ROUND_CEILING), RoundingMode.CEILING);
        TestCase.assertEquals("valueOf failed for ROUND_DOWN", RoundingMode.valueOf(BigDecimal.ROUND_DOWN), RoundingMode.DOWN);
        TestCase.assertEquals("valueOf failed for ROUND_FLOOR", RoundingMode.valueOf(BigDecimal.ROUND_FLOOR), RoundingMode.FLOOR);
        TestCase.assertEquals("valueOf failed for ROUND_HALF_DOWN", RoundingMode.valueOf(BigDecimal.ROUND_HALF_DOWN), RoundingMode.HALF_DOWN);
        TestCase.assertEquals("valueOf failed for ROUND_HALF_EVEN", RoundingMode.valueOf(BigDecimal.ROUND_HALF_EVEN), RoundingMode.HALF_EVEN);
        TestCase.assertEquals("valueOf failed for ROUND_HALF_UP", RoundingMode.valueOf(BigDecimal.ROUND_HALF_UP), RoundingMode.HALF_UP);
        TestCase.assertEquals("valueOf failed for ROUND_UNNECESSARY", RoundingMode.valueOf(BigDecimal.ROUND_UNNECESSARY), RoundingMode.UNNECESSARY);
        TestCase.assertEquals("valueOf failed for ROUND_UP", RoundingMode.valueOf(BigDecimal.ROUND_UP), RoundingMode.UP);
        try {
            RoundingMode.valueOf(13);
            TestCase.fail("IllegalArgumentException expected for RoundingMode(13)");
        } catch (IllegalArgumentException e) {
        }
        try {
            RoundingMode.valueOf((-1));
            TestCase.fail("IllegalArgumentException expected for RoundingMode(-1)");
        } catch (IllegalArgumentException e) {
        }
    }
}

