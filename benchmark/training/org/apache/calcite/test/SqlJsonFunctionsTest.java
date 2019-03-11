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
package org.apache.calcite.test;


import SqlFunctions.PathContext;
import SqlFunctions.PathMode.LAX;
import SqlFunctions.PathMode.STRICT;
import SqlJsonConstructorNullClause.ABSENT_ON_NULL;
import SqlJsonConstructorNullClause.NULL_ON_NULL;
import SqlJsonExistsErrorBehavior.ERROR;
import SqlJsonExistsErrorBehavior.FALSE;
import SqlJsonExistsErrorBehavior.TRUE;
import SqlJsonExistsErrorBehavior.UNKNOWN;
import SqlJsonQueryEmptyOrErrorBehavior.EMPTY_ARRAY;
import SqlJsonQueryEmptyOrErrorBehavior.EMPTY_OBJECT;
import SqlJsonQueryWrapperBehavior.WITHOUT_ARRAY;
import SqlJsonQueryWrapperBehavior.WITH_CONDITIONAL_ARRAY;
import SqlJsonQueryWrapperBehavior.WITH_UNCONDITIONAL_ARRAY;
import SqlJsonValueEmptyOrErrorBehavior.DEFAULT;
import SqlJsonValueEmptyOrErrorBehavior.NULL;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Longs;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.calcite.runtime.CalciteException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


/**
 * Unit test for the methods in {@link SqlFunctions} that implement JSON processing functions.
 */
public class SqlJsonFunctionsTest {
    @Test
    public void testJsonValueExpression() {
        assertJsonValueExpression("{}", CoreMatchers.is(Collections.emptyMap()));
    }

    @Test
    public void testJsonStructuredValueExpression() {
        assertJsonStructuredValueExpression("bar", CoreMatchers.is("bar"));
        assertJsonStructuredValueExpression(100, CoreMatchers.is(100));
    }

    @Test
    public void testJsonApiCommonSyntax() {
        assertJsonApiCommonSyntax(ImmutableMap.of("foo", "bar"), "lax $.foo", contextMatches(PathContext.withReturned(LAX, "bar")));
        assertJsonApiCommonSyntax(ImmutableMap.of("foo", "bar"), "strict $.foo", contextMatches(PathContext.withReturned(STRICT, "bar")));
        assertJsonApiCommonSyntax(ImmutableMap.of("foo", "bar"), "lax $.foo1", contextMatches(PathContext.withReturned(LAX, null)));
        assertJsonApiCommonSyntax(ImmutableMap.of("foo", "bar"), "strict $.foo1", contextMatches(PathContext.withStrictException(new PathNotFoundException("No results for path: $['foo1']"))));
        assertJsonApiCommonSyntax(ImmutableMap.of("foo", 100), "lax $.foo", contextMatches(PathContext.withReturned(LAX, 100)));
    }

    @Test
    public void testJsonExists() {
        assertJsonExists(PathContext.withReturned(STRICT, "bar"), FALSE, CoreMatchers.is(true));
        assertJsonExists(PathContext.withReturned(STRICT, "bar"), TRUE, CoreMatchers.is(true));
        assertJsonExists(PathContext.withReturned(STRICT, "bar"), UNKNOWN, CoreMatchers.is(true));
        assertJsonExists(PathContext.withReturned(STRICT, "bar"), ERROR, CoreMatchers.is(true));
        assertJsonExists(PathContext.withReturned(LAX, null), FALSE, CoreMatchers.is(false));
        assertJsonExists(PathContext.withReturned(LAX, null), TRUE, CoreMatchers.is(false));
        assertJsonExists(PathContext.withReturned(LAX, null), UNKNOWN, CoreMatchers.is(false));
        assertJsonExists(PathContext.withReturned(LAX, null), ERROR, CoreMatchers.is(false));
        assertJsonExists(PathContext.withStrictException(new Exception("test message")), FALSE, CoreMatchers.is(false));
        assertJsonExists(PathContext.withStrictException(new Exception("test message")), TRUE, CoreMatchers.is(true));
        assertJsonExists(PathContext.withStrictException(new Exception("test message")), UNKNOWN, CoreMatchers.nullValue());
        assertJsonExistsFailed(PathContext.withStrictException(new Exception("test message")), ERROR, errorMatches(new RuntimeException("java.lang.Exception: test message")));
    }

    @Test
    public void testJsonValueAny() {
        assertJsonValueAny(PathContext.withReturned(LAX, "bar"), NULL, null, NULL, null, CoreMatchers.is("bar"));
        assertJsonValueAny(PathContext.withReturned(LAX, null), NULL, null, NULL, null, CoreMatchers.nullValue());
        assertJsonValueAny(PathContext.withReturned(LAX, null), DEFAULT, "empty", NULL, null, CoreMatchers.is("empty"));
        assertJsonValueAnyFailed(PathContext.withReturned(LAX, null), SqlJsonValueEmptyOrErrorBehavior.ERROR, null, NULL, null, errorMatches(new CalciteException(("Empty result of JSON_VALUE function is not " + "allowed"), null)));
        assertJsonValueAny(PathContext.withReturned(LAX, Collections.emptyList()), NULL, null, NULL, null, CoreMatchers.nullValue());
        assertJsonValueAny(PathContext.withReturned(LAX, Collections.emptyList()), DEFAULT, "empty", NULL, null, CoreMatchers.is("empty"));
        assertJsonValueAnyFailed(PathContext.withReturned(LAX, Collections.emptyList()), SqlJsonValueEmptyOrErrorBehavior.ERROR, null, NULL, null, errorMatches(new CalciteException(("Empty result of JSON_VALUE function is not " + "allowed"), null)));
        assertJsonValueAny(PathContext.withStrictException(new Exception("test message")), NULL, null, NULL, null, CoreMatchers.nullValue());
        assertJsonValueAny(PathContext.withStrictException(new Exception("test message")), NULL, null, DEFAULT, "empty", CoreMatchers.is("empty"));
        assertJsonValueAnyFailed(PathContext.withStrictException(new Exception("test message")), NULL, null, SqlJsonValueEmptyOrErrorBehavior.ERROR, null, errorMatches(new RuntimeException("java.lang.Exception: test message")));
        assertJsonValueAny(PathContext.withReturned(STRICT, Collections.emptyList()), NULL, null, NULL, null, CoreMatchers.nullValue());
        assertJsonValueAny(PathContext.withReturned(STRICT, Collections.emptyList()), NULL, null, DEFAULT, "empty", CoreMatchers.is("empty"));
        assertJsonValueAnyFailed(PathContext.withReturned(STRICT, Collections.emptyList()), NULL, null, SqlJsonValueEmptyOrErrorBehavior.ERROR, null, errorMatches(new CalciteException(("Strict jsonpath mode requires scalar value, " + "and the actual value is: '[]'"), null)));
    }

    @Test
    public void testJsonQuery() {
        assertJsonQuery(PathContext.withReturned(LAX, Collections.singletonList("bar")), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.is("[\"bar\"]"));
        assertJsonQuery(PathContext.withReturned(LAX, null), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.nullValue());
        assertJsonQuery(PathContext.withReturned(LAX, null), WITHOUT_ARRAY, EMPTY_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.is("[]"));
        assertJsonQuery(PathContext.withReturned(LAX, null), WITHOUT_ARRAY, EMPTY_OBJECT, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.is("{}"));
        assertJsonQueryFailed(PathContext.withReturned(LAX, null), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.ERROR, SqlJsonQueryEmptyOrErrorBehavior.NULL, errorMatches(new CalciteException(("Empty result of JSON_QUERY function is not " + "allowed"), null)));
        assertJsonQuery(PathContext.withReturned(LAX, "bar"), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.nullValue());
        assertJsonQuery(PathContext.withReturned(LAX, "bar"), WITHOUT_ARRAY, EMPTY_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.is("[]"));
        assertJsonQuery(PathContext.withReturned(LAX, "bar"), WITHOUT_ARRAY, EMPTY_OBJECT, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.is("{}"));
        assertJsonQueryFailed(PathContext.withReturned(LAX, "bar"), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.ERROR, SqlJsonQueryEmptyOrErrorBehavior.NULL, errorMatches(new CalciteException(("Empty result of JSON_QUERY function is not " + "allowed"), null)));
        assertJsonQuery(PathContext.withStrictException(new Exception("test message")), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, EMPTY_ARRAY, CoreMatchers.is("[]"));
        assertJsonQuery(PathContext.withStrictException(new Exception("test message")), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, EMPTY_OBJECT, CoreMatchers.is("{}"));
        assertJsonQueryFailed(PathContext.withStrictException(new Exception("test message")), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.ERROR, errorMatches(new RuntimeException("java.lang.Exception: test message")));
        assertJsonQuery(PathContext.withReturned(STRICT, "bar"), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.nullValue());
        assertJsonQuery(PathContext.withReturned(STRICT, "bar"), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, EMPTY_ARRAY, CoreMatchers.is("[]"));
        assertJsonQueryFailed(PathContext.withReturned(STRICT, "bar"), WITHOUT_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.ERROR, errorMatches(new CalciteException(("Strict jsonpath mode requires array or " + "object value, and the actual value is: 'bar'"), null)));
        // wrapper behavior test
        assertJsonQuery(PathContext.withReturned(STRICT, "bar"), WITH_UNCONDITIONAL_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.is("[\"bar\"]"));
        assertJsonQuery(PathContext.withReturned(STRICT, "bar"), WITH_CONDITIONAL_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.is("[\"bar\"]"));
        assertJsonQuery(PathContext.withReturned(STRICT, Collections.singletonList("bar")), WITH_UNCONDITIONAL_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.is("[[\"bar\"]]"));
        assertJsonQuery(PathContext.withReturned(STRICT, Collections.singletonList("bar")), WITH_CONDITIONAL_ARRAY, SqlJsonQueryEmptyOrErrorBehavior.NULL, SqlJsonQueryEmptyOrErrorBehavior.NULL, CoreMatchers.is("[\"bar\"]"));
    }

    @Test
    public void testJsonize() {
        assertJsonize(new HashMap<>(), CoreMatchers.is("{}"));
    }

    @Test
    public void assertJsonPretty() {
        assertJsonPretty(new HashMap<>(), CoreMatchers.is("{ }"));
        assertJsonPretty(Longs.asList(1, 2), CoreMatchers.is("[ 1, 2 ]"));
        Object input = new Object() {
            private final Object self = this;
        };
        CalciteException expected = new CalciteException((("Cannot serialize object to JSON, and the object is: '" + input) + "'"), null);
        assertJsonPrettyFailed(input, errorMatches(expected));
    }

    @Test
    public void testDejsonize() {
        assertDejsonize("{}", CoreMatchers.is(Collections.emptyMap()));
        assertDejsonize("[]", CoreMatchers.is(Collections.emptyList()));
        // expect exception thrown
        final String message = "com.fasterxml.jackson.core.JsonParseException: " + (("Unexpected close marker '}': expected ']' (for Array starting at " + "[Source: (String)\"[}\"; line: 1, column: 1])\n at [Source: ") + "(String)\"[}\"; line: 1, column: 3]");
        assertDejsonizeFailed("[}", errorMatches(new InvalidJsonException(message)));
    }

    @Test
    public void testJsonObject() {
        assertJsonObject(CoreMatchers.is("{}"), NULL_ON_NULL);
        assertJsonObject(CoreMatchers.is("{\"foo\":\"bar\"}"), NULL_ON_NULL, "foo", "bar");
        assertJsonObject(CoreMatchers.is("{\"foo\":null}"), NULL_ON_NULL, "foo", null);
        assertJsonObject(CoreMatchers.is("{}"), ABSENT_ON_NULL, "foo", null);
    }

    @Test
    public void testJsonType() {
        assertJsonType(CoreMatchers.is("OBJECT"), "{}");
        assertJsonType(CoreMatchers.is("ARRAY"), "[\"foo\",null]");
        assertJsonType(CoreMatchers.is("NULL"), "null");
        assertJsonType(CoreMatchers.is("BOOLEAN"), "false");
        assertJsonType(CoreMatchers.is("INTEGER"), "12");
        assertJsonType(CoreMatchers.is("DOUBLE"), "11.22");
    }

    @Test
    public void testJsonDepth() {
        assertJsonDepth(CoreMatchers.is(1), "{}");
        assertJsonDepth(CoreMatchers.is(1), "false");
        assertJsonDepth(CoreMatchers.is(1), "12");
        assertJsonDepth(CoreMatchers.is(1), "11.22");
        assertJsonDepth(CoreMatchers.is(2), "[\"foo\",null]");
        assertJsonDepth(CoreMatchers.is(3), "{\"a\": [10, true]}");
        assertJsonDepth(CoreMatchers.nullValue(), "null");
    }

    @Test
    public void testJsonObjectAggAdd() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> expected = new HashMap<>();
        expected.put("foo", "bar");
        assertJsonObjectAggAdd(map, "foo", "bar", NULL_ON_NULL, CoreMatchers.is(expected));
        expected.put("foo1", null);
        assertJsonObjectAggAdd(map, "foo1", null, NULL_ON_NULL, CoreMatchers.is(expected));
        assertJsonObjectAggAdd(map, "foo2", null, ABSENT_ON_NULL, CoreMatchers.is(expected));
    }

    @Test
    public void testJsonArray() {
        assertJsonArray(CoreMatchers.is("[]"), NULL_ON_NULL);
        assertJsonArray(CoreMatchers.is("[\"foo\"]"), NULL_ON_NULL, "foo");
        assertJsonArray(CoreMatchers.is("[\"foo\",null]"), NULL_ON_NULL, "foo", null);
        assertJsonArray(CoreMatchers.is("[\"foo\"]"), ABSENT_ON_NULL, "foo", null);
    }

    @Test
    public void testJsonArrayAggAdd() {
        List<Object> list = new ArrayList<>();
        List<Object> expected = new ArrayList<>();
        expected.add("foo");
        assertJsonArrayAggAdd(list, "foo", NULL_ON_NULL, CoreMatchers.is(expected));
        expected.add(null);
        assertJsonArrayAggAdd(list, null, NULL_ON_NULL, CoreMatchers.is(expected));
        assertJsonArrayAggAdd(list, null, ABSENT_ON_NULL, CoreMatchers.is(expected));
    }

    @Test
    public void testJsonPredicate() {
        assertIsJsonValue("[]", CoreMatchers.is(true));
        assertIsJsonValue("{}", CoreMatchers.is(true));
        assertIsJsonValue("100", CoreMatchers.is(true));
        assertIsJsonValue("{]", CoreMatchers.is(false));
        assertIsJsonObject("[]", CoreMatchers.is(false));
        assertIsJsonObject("{}", CoreMatchers.is(true));
        assertIsJsonObject("100", CoreMatchers.is(false));
        assertIsJsonObject("{]", CoreMatchers.is(false));
        assertIsJsonArray("[]", CoreMatchers.is(true));
        assertIsJsonArray("{}", CoreMatchers.is(false));
        assertIsJsonArray("100", CoreMatchers.is(false));
        assertIsJsonArray("{]", CoreMatchers.is(false));
        assertIsJsonScalar("[]", CoreMatchers.is(false));
        assertIsJsonScalar("{}", CoreMatchers.is(false));
        assertIsJsonScalar("100", CoreMatchers.is(true));
        assertIsJsonScalar("{]", CoreMatchers.is(false));
    }
}

/**
 * End SqlJsonFunctionsTest.java
 */
