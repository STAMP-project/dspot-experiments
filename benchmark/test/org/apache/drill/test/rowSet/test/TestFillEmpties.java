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
package org.apache.drill.test.rowSet.test;


import DataMode.OPTIONAL;
import DataMode.REPEATED;
import DataMode.REQUIRED;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Test;


/**
 * Test the "fill empties" logic for all types for all modes.
 * This test exploits the dynamic typing ability of the
 * accessors. Creating an object per value is too slow for
 * production code, but very handy for tests such as this.
 * <p>
 * Note that this test also has the handy side-effect of testing
 * null handling in the accessor classes.
 */
public class TestFillEmpties extends SubOperatorTest {
    public static final int ROW_COUNT = 1000;

    /**
     * Test "fill empties" for required types. Here, the fill value
     * is more of a convention: 0 (fixed-width) or an empty
     * entry (variable width.) Some fill value is required to avoid
     * the alternatives which are either 1) leave the value as
     * garbage, or 2) raise an exception about the missing value.
     */
    @Test
    public void testFillEmptiesRequired() {
        testFillEmpties(REQUIRED);
    }

    /**
     * Test "fill empties" for nullable types which are the most
     * "natural" type for omitted values.
     * Nullable vectors fill empties with nulls.
     */
    @Test
    public void testFillEmptiesNullable() {
        testFillEmpties(OPTIONAL);
    }

    /**
     * Test "fill empties" for repeated types.
     * Drill defines a null (omitted) array as the same thing as
     * a zero-length array.
     */
    @Test
    public void testFillEmptiesRepeated() {
        testFillEmpties(REPEATED);
    }
}

