/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.tests;


import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;


public class SystemTestUtilTest {
    private final Map<String, String> expectedParsedMap = new TreeMap<>();

    @Test
    public void shouldParseCorrectMap() {
        final String formattedConfigs = "foo=foo1,bar=bar1,baz=baz1";
        final Map<String, String> parsedMap = SystemTestUtil.parseConfigs(formattedConfigs);
        final TreeMap<String, String> sortedParsedMap = new TreeMap<>(parsedMap);
        Assert.assertEquals(sortedParsedMap, expectedParsedMap);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionOnNull() {
        SystemTestUtil.parseConfigs(null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfNotCorrectKeyValueSeparator() {
        final String badString = "foo:bar,baz:boo";
        SystemTestUtil.parseConfigs(badString);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfNotCorrectKeyValuePairSeparator() {
        final String badString = "foo=bar;baz=boo";
        SystemTestUtil.parseConfigs(badString);
    }

    @Test
    public void shouldParseSingleKeyValuePairString() {
        final Map<String, String> expectedSinglePairMap = new HashMap<>();
        expectedSinglePairMap.put("foo", "bar");
        final String singleValueString = "foo=bar";
        final Map<String, String> parsedMap = SystemTestUtil.parseConfigs(singleValueString);
        Assert.assertEquals(expectedSinglePairMap, parsedMap);
    }
}

