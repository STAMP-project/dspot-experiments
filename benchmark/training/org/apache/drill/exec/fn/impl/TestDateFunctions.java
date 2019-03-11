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
package org.apache.drill.exec.fn.impl;


import org.apache.drill.categories.SqlFunctionTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ UnlikelyTest.class, SqlFunctionTest.class })
public class TestDateFunctions extends PopUnitTestBase {
    @Test
    public void testDateDifferenceArithmetic() throws Exception {
        String[] expectedResults = new String[]{ "P365D", "P-366DT-60S", "PT39600S" };
        testCommon(expectedResults, "/functions/date/date_difference_arithmetic.json", "/test_simple_date.json");
    }

    @Test
    public void testAge() throws Exception {
        String[] expectedResults = new String[]{ "P109M16DT82800S", "P172M27D", "P-172M-27D", "P-39M-18DT-63573S" };
        testCommon(expectedResults, "/functions/date/age.json", "/test_simple_date.json");
    }

    @Test
    public void testIntervalArithmetic() throws Exception {
        String[] expectedResults = new String[]{ "P2Y2M", "P2DT3723S", "P2M", "PT3723S", "P28M", "PT7206S", "P7M", "PT1801.500S", "P33M18D", "PT8647.200S", "P6M19DT86399.999S", "PT1715.714S" };
        testCommon(expectedResults, "/functions/date/interval_arithmetic.json", "/test_simple_date.json");
    }

    @Test
    public void testToChar() throws Exception {
        ExecTest.mockUsDateFormatSymbols();
        String[] expectedResults = new String[]{ new LocalDate(2008, 2, 23).toString("yyyy-MMM-dd"), new LocalTime(12, 20, 30).toString("HH mm ss"), new LocalDateTime(2008, 2, 23, 12, 0, 0).toString("yyyy MMM dd HH:mm:ss") };
        testCommon(expectedResults, "/functions/date/to_char.json", "/test_simple_date.json");
    }
}

