/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.adapter.geode.rel;


import org.junit.Test;


/**
 * Test with different types of data like boolean, time, timestamp
 */
public class GeodeAllDataTypesTest extends AbstractGeodeTest {
    @Test
    public void testSqlSingleBooleanWhereFilter() {
        calciteAssert().query(("SELECT booleanValue as booleanValue " + "FROM geode.allDataTypesRegion WHERE booleanValue = true")).returnsCount(2).queryContains(GeodeAssertions.query(("SELECT booleanValue AS booleanValue FROM /allDataTypesRegion " + "WHERE booleanValue = true")));
    }

    @Test
    public void testSqlBooleanColumnFilter() {
        calciteAssert().query(("SELECT booleanValue as booleanValue " + "FROM geode.allDataTypesRegion WHERE booleanValue")).returnsCount(2).queryContains(GeodeAssertions.query(("SELECT booleanValue AS booleanValue FROM /allDataTypesRegion " + "WHERE booleanValue = true")));
    }

    @Test
    public void testSqlBooleanColumnNotFilter() {
        calciteAssert().query(("SELECT booleanValue as booleanValue " + "FROM geode.allDataTypesRegion WHERE not booleanValue")).returnsCount(1).queryContains(GeodeAssertions.query(("SELECT booleanValue AS booleanValue FROM /allDataTypesRegion " + "WHERE booleanValue = false")));
    }

    @Test
    public void testSqlMultipleBooleanWhereFilter() {
        calciteAssert().query(("SELECT booleanValue as booleanValue " + "FROM geode.allDataTypesRegion WHERE booleanValue = true OR booleanValue = false")).returnsCount(3).queryContains(GeodeAssertions.query(("SELECT booleanValue AS booleanValue FROM /allDataTypesRegion " + "WHERE booleanValue = true OR booleanValue = false")));
    }

    @Test
    public void testSqlWhereWithMultipleOrForLiteralFields() {
        calciteAssert().query(("SELECT stringValue " + (("FROM geode.allDataTypesRegion WHERE (stringValue = 'abc' OR stringValue = 'def') OR " + "(floatValue = 1.5678 OR floatValue = null) OR ") + "(booleanValue = true OR booleanValue = false OR booleanValue = null)"))).returnsCount(3).queryContains(GeodeAssertions.query(("SELECT stringValue AS stringValue " + (("FROM /allDataTypesRegion WHERE " + "stringValue IN SET('abc', 'def') OR floatValue = 1.5678 ") + "OR booleanValue = true OR booleanValue = false"))));
    }

    @Test
    public void testSqlSingleDateWhereFilter() {
        calciteAssert().query(("SELECT dateValue\n" + ("FROM geode.allDataTypesRegion\n" + "WHERE dateValue = DATE '2018-02-03'"))).returnsCount(1).queryContains(GeodeAssertions.query(("SELECT dateValue AS dateValue " + ("FROM /allDataTypesRegion " + "WHERE dateValue = DATE '2018-02-03'"))));
        calciteAssert().query(("SELECT dateValue\n" + ("FROM geode.allDataTypesRegion\n" + "WHERE dateValue > DATE '2018-02-03'"))).returnsCount(2).queryContains(GeodeAssertions.query(("SELECT dateValue AS dateValue " + ("FROM /allDataTypesRegion " + "WHERE dateValue > DATE '2018-02-03'"))));
        calciteAssert().query(("SELECT dateValue\n" + ("FROM geode.allDataTypesRegion\n" + "WHERE dateValue < DATE '2018-02-03'"))).returnsCount(0).queryContains(GeodeAssertions.query(("SELECT dateValue AS dateValue " + ("FROM /allDataTypesRegion " + "WHERE dateValue < DATE '2018-02-03'"))));
    }

    @Test
    public void testSqlMultipleDateWhereFilter() {
        calciteAssert().query(("SELECT dateValue\n" + (("FROM geode.allDataTypesRegion\n" + "WHERE dateValue = DATE \'2018-02-03\'\n") + "  OR dateValue = DATE '2018-02-04'"))).returnsCount(2).queryContains(GeodeAssertions.query(("SELECT dateValue AS dateValue " + (("FROM /allDataTypesRegion " + "WHERE dateValue IN SET(DATE '2018-02-03',") + " DATE '2018-02-04')"))));
    }

    @Test
    public void testSqlSingleTimeWhereFilter() {
        calciteAssert().query(("SELECT timeValue\n" + ("FROM geode.allDataTypesRegion\n" + "WHERE timeValue = TIME '02:22:23'"))).returnsCount(1).queryContains(GeodeAssertions.query(("SELECT timeValue AS timeValue " + ("FROM /allDataTypesRegion " + "WHERE timeValue = TIME '02:22:23'"))));
        calciteAssert().query(("SELECT timeValue\n" + ("FROM geode.allDataTypesRegion\n" + "WHERE timeValue > TIME '02:22:23'"))).returnsCount(2).queryContains(GeodeAssertions.query(("SELECT timeValue AS timeValue " + ("FROM /allDataTypesRegion " + "WHERE timeValue > TIME '02:22:23'"))));
        calciteAssert().query(("SELECT timeValue\n" + ("FROM geode.allDataTypesRegion\n" + "WHERE timeValue < TIME '02:22:23'"))).returnsCount(0).queryContains(GeodeAssertions.query(("SELECT timeValue AS timeValue " + ("FROM /allDataTypesRegion " + "WHERE timeValue < TIME '02:22:23'"))));
    }

    @Test
    public void testSqlMultipleTimeWhereFilter() {
        calciteAssert().query(("SELECT timeValue\n" + (("FROM geode.allDataTypesRegion\n" + "WHERE timeValue = TIME \'02:22:23\'\n") + "  OR timeValue = TIME '03:22:23'"))).returnsCount(2).queryContains(GeodeAssertions.query(("SELECT timeValue AS timeValue " + ("FROM /allDataTypesRegion " + "WHERE timeValue IN SET(TIME '02:22:23', TIME '03:22:23')"))));
    }

    @Test
    public void testSqlSingleTimestampWhereFilter() {
        calciteAssert().query(("SELECT timestampValue\n" + ("FROM geode.allDataTypesRegion\n" + "WHERE timestampValue = TIMESTAMP '2018-02-03 02:22:33'"))).returnsCount(1).queryContains(GeodeAssertions.query(("SELECT timestampValue AS timestampValue " + ("FROM /allDataTypesRegion " + "WHERE timestampValue = TIMESTAMP '2018-02-03 02:22:33'"))));
        calciteAssert().query(("SELECT timestampValue\n" + ("FROM geode.allDataTypesRegion\n" + "WHERE timestampValue > TIMESTAMP '2018-02-03 02:22:33'"))).returnsCount(2).queryContains(GeodeAssertions.query(("SELECT timestampValue AS timestampValue " + ("FROM /allDataTypesRegion " + "WHERE timestampValue > TIMESTAMP '2018-02-03 02:22:33'"))));
        calciteAssert().query(("SELECT timestampValue\n" + ("FROM geode.allDataTypesRegion\n" + "WHERE timestampValue < TIMESTAMP '2018-02-03 02:22:33'"))).returnsCount(0).queryContains(GeodeAssertions.query(("SELECT timestampValue AS timestampValue " + ("FROM /allDataTypesRegion " + "WHERE timestampValue < TIMESTAMP '2018-02-03 02:22:33'"))));
    }

    @Test
    public void testSqlMultipleTimestampWhereFilter() {
        calciteAssert().query(("SELECT timestampValue\n" + (("FROM geode.allDataTypesRegion\n" + "WHERE timestampValue = TIMESTAMP \'2018-02-03 02:22:33\'\n") + "  OR timestampValue = TIMESTAMP '2018-02-05 04:22:33'"))).returnsCount(2).queryContains(GeodeAssertions.query(("SELECT timestampValue AS timestampValue " + ((("FROM /allDataTypesRegion " + "WHERE timestampValue IN SET(") + "TIMESTAMP '2018-02-03 02:22:33', ") + "TIMESTAMP '2018-02-05 04:22:33')"))));
    }

    @Test
    public void testSqlWhereWithMultipleOrForAllFields() {
        calciteAssert().query(("SELECT stringValue " + (((((("FROM geode.allDataTypesRegion WHERE (stringValue = 'abc' OR stringValue = 'def') OR " + "(floatValue = 1.5678 OR floatValue = null) OR ") + "(dateValue = DATE '2018-02-05' OR dateValue = DATE '2018-02-06' ) OR ") + "(timeValue = TIME '03:22:23' OR timeValue = TIME '07:22:23') OR ") + "(timestampValue = TIMESTAMP '2018-02-05 04:22:33' OR ") + "timestampValue = TIMESTAMP '2017-02-05 04:22:33') OR ") + "(booleanValue = true OR booleanValue = false OR booleanValue = null)"))).returnsCount(3).queryContains(GeodeAssertions.query(("SELECT stringValue AS stringValue " + ((((("FROM /allDataTypesRegion WHERE " + "stringValue IN SET('abc', 'def') OR floatValue IN SET(1.5678, null) OR dateValue ") + "IN SET(DATE '2018-02-05', DATE '2018-02-06') OR timeValue ") + "IN SET(TIME '03:22:23', TIME '07:22:23') OR timestampValue ") + "IN SET(TIMESTAMP '2018-02-05 04:22:33', TIMESTAMP '2017-02-05 04:22:33') ") + "OR booleanValue IN SET(true, false, null)"))));
    }
}

/**
 * End GeodeAllDataTypesTest.java
 */
