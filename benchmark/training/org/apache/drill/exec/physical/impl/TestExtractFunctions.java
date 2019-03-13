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
package org.apache.drill.exec.physical.impl;


import org.apache.drill.categories.SlowTest;
import org.apache.drill.exec.pop.PopUnitTestBase;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/* This class tests the existing date types. Simply using date types
by casting from VarChar, performing basic functions and converting
back to VarChar.
 */
@Category({ SlowTest.class })
public class TestExtractFunctions extends PopUnitTestBase {
    @Test
    public void testFromDate() throws Exception {
        long[][] expectedValues = new long[][]{ new long[]{ 0, 0, 2, 1, 1970 }, new long[]{ 0, 0, 28, 12, 2008 }, new long[]{ 0, 0, 27, 2, 2000 } };
        testFrom("date", "/test_simple_date.json", "stringdate", expectedValues);
    }

    @Test
    public void testFromTimeStamp() throws Exception {
        long[][] expectedValues = new long[][]{ new long[]{ 20, 10, 2, 1, 1970 }, new long[]{ 34, 11, 28, 12, 2008 }, new long[]{ 24, 14, 27, 2, 2000 } };
        testFrom("timestamp", "/test_simple_date.json", "stringdate", expectedValues);
    }

    @Test
    public void testFromInterval() throws Exception {
        long[][] expectedValues = new long[][]{ new long[]{ 20, 1, 1, 2, 2 }, new long[]{ 0, 0, 0, 2, 2 }, new long[]{ 20, 1, 0, 0, 0 }, new long[]{ 20, 1, 1, 2, 2 }, new long[]{ 0, 0, 0, 0, 0 }, new long[]{ -39, 0, 1, 10, 1 } };
        testFrom("interval", "/test_simple_interval.json", "stringinterval", expectedValues);
    }

    @Test
    public void testFromIntervalDay() throws Exception {
        long[][] expectedValues = new long[][]{ new long[]{ 20, 1, 1, 0, 0 }, new long[]{ 0, 0, 0, 0, 0 }, new long[]{ 20, 1, 0, 0, 0 }, new long[]{ 20, 1, 1, 0, 0 }, new long[]{ 0, 0, 0, 0, 0 }, new long[]{ -39, 0, 1, 0, 0 } };
        testFrom("intervalday", "/test_simple_interval.json", "stringinterval", expectedValues);
    }

    @Test
    public void testFromIntervalYear() throws Exception {
        long[][] expectedValues = new long[][]{ new long[]{ 0, 0, 0, 2, 2 }, new long[]{ 0, 0, 0, 2, 2 }, new long[]{ 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 2, 2 }, new long[]{ 0, 0, 0, 0, 0 }, new long[]{ 0, 0, 0, 10, 1 } };
        testFrom("intervalyear", "/test_simple_interval.json", "stringinterval", expectedValues);
    }
}

