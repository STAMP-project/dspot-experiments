/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.ordering;


import StringComparators.ALPHANUMERIC;
import StringComparators.LEXICOGRAPHIC;
import StringComparators.NUMERIC;
import StringComparators.STRLEN;
import StringComparators.VERSION;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class StringComparatorsTest {
    @Test
    public void testLexicographicComparator() {
        commonTest(LEXICOGRAPHIC);
        Assert.assertTrue(((LEXICOGRAPHIC.compare("apple", "banana")) < 0));
        Assert.assertTrue(((LEXICOGRAPHIC.compare("banana", "banana")) == 0));
    }

    @Test
    public void testAlphanumericComparator() {
        commonTest(ALPHANUMERIC);
        // numbers < non numeric
        Assert.assertTrue(((ALPHANUMERIC.compare("123", "abc")) < 0));
        Assert.assertTrue(((ALPHANUMERIC.compare("abc", "123")) > 0));
        // numbers ordered numerically
        Assert.assertTrue(((ALPHANUMERIC.compare("2", "11")) < 0));
        Assert.assertTrue(((ALPHANUMERIC.compare("a2", "a11")) < 0));
        // leading zeros
        Assert.assertTrue(((ALPHANUMERIC.compare("02", "11")) < 0));
        Assert.assertTrue(((ALPHANUMERIC.compare("02", "002")) < 0));
        // decimal points ...
        Assert.assertTrue(((ALPHANUMERIC.compare("1.3", "1.5")) < 0));
        // ... don't work too well
        Assert.assertTrue(((ALPHANUMERIC.compare("1.3", "1.15")) < 0));
        // but you can sort ranges
        List<String> sorted = Lists.newArrayList("1-5", "11-15", "16-20", "21-25", "26-30", "6-10", "Other");
        Collections.sort(sorted, ALPHANUMERIC);
        Assert.assertEquals(ImmutableList.of("1-5", "6-10", "11-15", "16-20", "21-25", "26-30", "Other"), sorted);
        List<String> sortedFixedDecimal = Lists.newArrayList("Other", "[0.00-0.05)", "[0.05-0.10)", "[0.10-0.50)", "[0.50-1.00)", "[1.00-5.00)", "[5.00-10.00)", "[10.00-20.00)");
        Collections.sort(sortedFixedDecimal, ALPHANUMERIC);
        Assert.assertEquals(ImmutableList.of("[0.00-0.05)", "[0.05-0.10)", "[0.10-0.50)", "[0.50-1.00)", "[1.00-5.00)", "[5.00-10.00)", "[10.00-20.00)", "Other"), sortedFixedDecimal);
    }

    @Test
    public void testStrlenComparator() {
        commonTest(STRLEN);
        Assert.assertTrue(((STRLEN.compare("a", "apple")) < 0));
        Assert.assertTrue(((STRLEN.compare("a", "elppa")) < 0));
        Assert.assertTrue(((STRLEN.compare("apple", "elppa")) < 0));
    }

    @Test
    public void testNumericComparator() {
        commonTest(NUMERIC);
        Assert.assertTrue(((NUMERIC.compare("-1230.452487532", "6893")) < 0));
        List<String> values = Arrays.asList("-1", "-1.10", "-1.2", "-100", "-2", "0", "1", "1.10", "1.2", "2", "100");
        Collections.sort(values, NUMERIC);
        Assert.assertEquals(Arrays.asList("-100", "-2", "-1.2", "-1.10", "-1", "0", "1", "1.10", "1.2", "2", "100"), values);
        Assert.assertTrue(((NUMERIC.compare(null, null)) == 0));
        Assert.assertTrue(((NUMERIC.compare(null, "1001")) < 0));
        Assert.assertTrue(((NUMERIC.compare("1001", null)) > 0));
        Assert.assertTrue(((NUMERIC.compare("-500000000.14124", "CAN'T TOUCH THIS")) > 0));
        Assert.assertTrue(((NUMERIC.compare("CAN'T PARSE THIS", "-500000000.14124")) < 0));
        Assert.assertTrue(((NUMERIC.compare("CAN'T PARSE THIS", "CAN'T TOUCH THIS")) < 0));
    }

    @Test
    public void testVersionComparator() {
        commonTest(VERSION);
        Assert.assertTrue(((VERSION.compare("02", "002")) == 0));
        Assert.assertTrue(((VERSION.compare("1.0", "2.0")) < 0));
        Assert.assertTrue(((VERSION.compare("9.1", "10.0")) < 0));
        Assert.assertTrue(((VERSION.compare("1.1.1", "2.0")) < 0));
        Assert.assertTrue(((VERSION.compare("1.0-SNAPSHOT", "1.0")) < 0));
        Assert.assertTrue(((VERSION.compare("2.0.1-xyz-1", "2.0.1-1-xyz")) < 0));
        Assert.assertTrue(((VERSION.compare("1.0-SNAPSHOT", "1.0-Final")) < 0));
    }

    @Test
    public void testLexicographicComparatorSerdeTest() throws IOException {
        ObjectMapper jsonMapper = new DefaultObjectMapper();
        String expectJsonSpec = "{\"type\":\"lexicographic\"}";
        String jsonSpec = jsonMapper.writeValueAsString(LEXICOGRAPHIC);
        Assert.assertEquals(expectJsonSpec, jsonSpec);
        Assert.assertEquals(LEXICOGRAPHIC, jsonMapper.readValue(expectJsonSpec, StringComparator.class));
        String makeFromJsonSpec = "\"lexicographic\"";
        Assert.assertEquals(LEXICOGRAPHIC, jsonMapper.readValue(makeFromJsonSpec, StringComparator.class));
    }

    @Test
    public void testAlphanumericComparatorSerdeTest() throws IOException {
        ObjectMapper jsonMapper = new DefaultObjectMapper();
        String expectJsonSpec = "{\"type\":\"alphanumeric\"}";
        String jsonSpec = jsonMapper.writeValueAsString(ALPHANUMERIC);
        Assert.assertEquals(expectJsonSpec, jsonSpec);
        Assert.assertEquals(ALPHANUMERIC, jsonMapper.readValue(expectJsonSpec, StringComparator.class));
        String makeFromJsonSpec = "\"alphanumeric\"";
        Assert.assertEquals(ALPHANUMERIC, jsonMapper.readValue(makeFromJsonSpec, StringComparator.class));
    }

    @Test
    public void testStrlenComparatorSerdeTest() throws IOException {
        ObjectMapper jsonMapper = new DefaultObjectMapper();
        String expectJsonSpec = "{\"type\":\"strlen\"}";
        String jsonSpec = jsonMapper.writeValueAsString(STRLEN);
        Assert.assertEquals(expectJsonSpec, jsonSpec);
        Assert.assertEquals(STRLEN, jsonMapper.readValue(expectJsonSpec, StringComparator.class));
        String makeFromJsonSpec = "\"strlen\"";
        Assert.assertEquals(STRLEN, jsonMapper.readValue(makeFromJsonSpec, StringComparator.class));
    }

    @Test
    public void testNumericComparatorSerdeTest() throws IOException {
        ObjectMapper jsonMapper = new DefaultObjectMapper();
        String expectJsonSpec = "{\"type\":\"numeric\"}";
        String jsonSpec = jsonMapper.writeValueAsString(NUMERIC);
        Assert.assertEquals(expectJsonSpec, jsonSpec);
        Assert.assertEquals(NUMERIC, jsonMapper.readValue(expectJsonSpec, StringComparator.class));
        String makeFromJsonSpec = "\"numeric\"";
        Assert.assertEquals(NUMERIC, jsonMapper.readValue(makeFromJsonSpec, StringComparator.class));
        makeFromJsonSpec = "\"NuMeRiC\"";
        Assert.assertEquals(NUMERIC, jsonMapper.readValue(makeFromJsonSpec, StringComparator.class));
    }
}

