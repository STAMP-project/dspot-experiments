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
package org.apache.drill.hbase;


import org.apache.drill.categories.HbaseStorageTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SlowTest.class, HbaseStorageTest.class })
public class TestOrderedBytesConvertFunctions extends BaseTestQuery {
    private static final String CONVERSION_TEST_PHYSICAL_PLAN = "functions/conv/conversionTestWithPhysicalPlan.json";

    private static final float DELTA = ((float) (1.0E-4));

    String textFileContent;

    @Test
    public void testOrderedBytesDouble() throws Throwable {
        verifyPhysicalPlan("convert_to(4.9e-324, 'DOUBLE_OB')", new byte[]{ 49, ((byte) (128)), 0, 0, 0, 0, 0, 0, 1 });
    }

    @Test
    public void testOrderedBytesDoubleConvertFrom() throws Throwable {
        verifyPhysicalPlan("convert_from(binary_string(\'\\x31\\x80\\x00\\x00\\x00\\x00\\x00\\x00\\x01\'), \'DOUBLE_OB\')", Double.valueOf(4.9E-324));
    }
}

