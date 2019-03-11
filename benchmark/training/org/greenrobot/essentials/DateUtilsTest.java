/**
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.greenrobot.essentials;


import org.junit.Test;


public class DateUtilsTest {
    @Test
    public void testGetDayDifferenceOfReadableIntsPlusMinusNDays() {
        testGetDayDifferenceOfReadableIntsPlusMinusNDays(1);
        testGetDayDifferenceOfReadableIntsPlusMinusNDays((-1));
    }

    @Test
    public void testGetDayDifferenceOfReadableInts() {
        checkDayDifference(20110101, 20110101, 0);
        checkDayDifference(20110101, 20110102, 1);
        checkDayDifference(20110101, 20110103, 2);
        checkDayDifference(20110101, 20110201, 31);
        checkDayDifference(20110101, 20110301, 59);
        checkDayDifference(20110102, 20110101, (-1));
        checkDayDifference(20110103, 20110101, (-2));
        checkDayDifference(20110201, 20110101, (-31));
        checkDayDifference(20110301, 20110101, (-59));
        checkDayDifference(20111231, 20120101, 1);
    }
}

