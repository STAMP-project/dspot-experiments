/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.function.udf.json;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class ArrayContainsKudfTest {
    private ArrayContainsKudf jsonUdf = new ArrayContainsKudf();

    @Test
    public void shouldReturnFalseOnEmptyArray() {
        Assert.assertEquals(false, jsonUdf.evaluate("[]", true));
        Assert.assertEquals(false, jsonUdf.evaluate("[]", false));
        Assert.assertEquals(false, jsonUdf.evaluate("[]", null));
        Assert.assertEquals(false, jsonUdf.evaluate("[]", 1.0));
        Assert.assertEquals(false, jsonUdf.evaluate("[]", 100));
        Assert.assertEquals(false, jsonUdf.evaluate("[]", "abc"));
        Assert.assertEquals(false, jsonUdf.evaluate("[]", ""));
    }

    @Test
    public void shouldNotFindValuesInNullArray() {
        Assert.assertEquals(true, jsonUdf.evaluate("[null]", null));
        Assert.assertEquals(false, jsonUdf.evaluate("[null]", "null"));
        Assert.assertEquals(false, jsonUdf.evaluate("[null]", true));
        Assert.assertEquals(false, jsonUdf.evaluate("[null]", false));
        Assert.assertEquals(false, jsonUdf.evaluate("[null]", 1.0));
        Assert.assertEquals(false, jsonUdf.evaluate("[null]", 100));
        Assert.assertEquals(false, jsonUdf.evaluate("[null]", "abc"));
        Assert.assertEquals(false, jsonUdf.evaluate("[null]", ""));
    }

    @Test
    public void shouldFindIntegersInJsonArray() {
        final String json = "[2147483647, {\"ab\":null }, -2147483648, 1, 2, 3, null, [4], 4]";
        Assert.assertEquals(true, jsonUdf.evaluate(json, 2147483647));
        Assert.assertEquals(true, jsonUdf.evaluate(json, -2147483648));
        Assert.assertEquals(true, jsonUdf.evaluate(json, 1));
        Assert.assertEquals(true, jsonUdf.evaluate(json, 2));
        Assert.assertEquals(true, jsonUdf.evaluate(json, 3));
        Assert.assertEquals(false, jsonUdf.evaluate("5", 5));
        Assert.assertEquals(false, jsonUdf.evaluate(json, 5));
    }

    @Test
    public void shouldFindLongsInJsonArray() {
        Assert.assertEquals(true, jsonUdf.evaluate("[1]", 1L));
        Assert.assertEquals(true, jsonUdf.evaluate("[1111111111111111]", 1111111111111111L));
        Assert.assertEquals(true, jsonUdf.evaluate("[[222222222222222], 33333]", 33333L));
        Assert.assertEquals(true, jsonUdf.evaluate("[{}, \"abc\", null, 1]", 1L));
        Assert.assertEquals(false, jsonUdf.evaluate("[[222222222222222], 33333]", 222222222222222L));
        Assert.assertEquals(false, jsonUdf.evaluate("[{}, \"abc\", null, [1]]", 1L));
        Assert.assertEquals(false, jsonUdf.evaluate("[{}, \"abc\", null, {\"1\":1}]", 1L));
    }

    @Test
    public void shouldFindDoublesInJsonArray() {
        Assert.assertEquals(true, jsonUdf.evaluate("[-1.0, 2.0, 3.0]", 2.0));
        Assert.assertEquals(true, jsonUdf.evaluate("[1.0, -2.0, 3.0]", (-2.0)));
        Assert.assertEquals(true, jsonUdf.evaluate("[1.0, 2.0, 1.6E3]", 1600.0));
        Assert.assertEquals(true, jsonUdf.evaluate("[1.0, 2.0, -1.6E3]", (-1600.0)));
        Assert.assertEquals(true, jsonUdf.evaluate("[{}, \"abc\", null, -2.0]", (-2.0)));
        Assert.assertEquals(false, jsonUdf.evaluate("[[2.0], 3.0]", 2.0));
    }

    @Test
    public void shouldFindStringsInJsonArray() {
        Assert.assertEquals(true, jsonUdf.evaluate("[\"abc\"]", "abc"));
        Assert.assertEquals(true, jsonUdf.evaluate("[\"cbda\", \"abc\"]", "abc"));
        Assert.assertEquals(true, jsonUdf.evaluate("[{}, \"abc\", null, 1]", "abc"));
        Assert.assertEquals(true, jsonUdf.evaluate("[\"\"]", ""));
        Assert.assertEquals(false, jsonUdf.evaluate("[\"\"]", null));
        Assert.assertEquals(false, jsonUdf.evaluate("[1,2,3]", "1"));
        Assert.assertEquals(false, jsonUdf.evaluate("[null]", ""));
        Assert.assertEquals(false, jsonUdf.evaluate("[\"abc\", \"dba\"]", "abd"));
    }

    @Test
    public void shouldFindBooleansInJsonArray() {
        Assert.assertEquals(true, jsonUdf.evaluate("[false, false, true, false]", true));
        Assert.assertEquals(true, jsonUdf.evaluate("[true, true, false]", false));
        Assert.assertEquals(false, jsonUdf.evaluate("[true, true]", false));
        Assert.assertEquals(false, jsonUdf.evaluate("[false, false]", true));
    }

    @Test
    public void shouldReturnFalseOnEmptyList() {
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.emptyList(), true));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.emptyList(), false));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.emptyList(), null));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.emptyList(), 1.0));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.emptyList(), 100));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.emptyList(), "abc"));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.emptyList(), ""));
    }

    @Test
    public void shouldNotFindValuesInNullListElements() {
        Assert.assertEquals(true, jsonUdf.evaluate(Collections.singletonList(null), null));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.singletonList(null), "null"));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.singletonList(null), true));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.singletonList(null), false));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.singletonList(null), 1.0));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.singletonList(null), 100));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.singletonList(null), "abc"));
        Assert.assertEquals(false, jsonUdf.evaluate(Collections.singletonList(null), ""));
    }

    @Test
    public void shouldFindStringInList() {
        Assert.assertEquals(true, jsonUdf.evaluate(Arrays.asList("abc", "bd", "DC"), "DC"));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList("abc", "bd", "DC"), "dc"));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList("abc", "bd", "1"), 1));
    }

    @Test
    public void shouldFindIntegersInList() {
        Assert.assertEquals(true, jsonUdf.evaluate(Arrays.asList(1, 2, 3), 2));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList(1, 2, 3), 0));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList(1, 2, 3), "1"));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList(1, 2, 3), "aa"));
    }

    @Test
    public void shouldFindLongInList() {
        Assert.assertEquals(true, jsonUdf.evaluate(Arrays.asList(1L, 2L, 3L), 2L));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList(1L, 2L, 3L), 0L));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList(1L, 2L, 3L), "1"));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList(1L, 2L, 3L), "aaa"));
    }

    @Test
    public void shouldFindDoublesInList() {
        Assert.assertEquals(true, jsonUdf.evaluate(Arrays.asList(1.0, 2.0, 3.0), 2.0));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList(1.0, 2.0, 3.0), 4.0));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList(1.0, 2.0, 3.0), "1"));
        Assert.assertEquals(false, jsonUdf.evaluate(Arrays.asList(1.0, 2.0, 3.0), "aaa"));
    }
}

