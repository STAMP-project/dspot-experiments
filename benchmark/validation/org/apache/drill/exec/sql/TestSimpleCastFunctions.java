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
package org.apache.drill.exec.sql;


import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.drill.categories.SqlTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.shaded.guava.com.google.common.base.Function;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SqlTest.class)
public class TestSimpleCastFunctions extends BaseTestQuery {
    private static final List<Function<String, String>> inputFunctions = Lists.newArrayList();

    static {
        TestSimpleCastFunctions.inputFunctions.add(new Function<String, String>() {
            @Nullable
            @Override
            public String apply(String input) {
                return input.toLowerCase();
            }
        });
        TestSimpleCastFunctions.inputFunctions.add(new Function<String, String>() {
            @Nullable
            @Override
            public String apply(String input) {
                return input.toUpperCase();
            }
        });
        TestSimpleCastFunctions.inputFunctions.add(new Function<String, String>() {
            @Nullable
            @Override
            public String apply(String input) {
                return ("   " + input) + "   ";
            }
        });
    }

    @Test
    public void testCastFromBooleanToString() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(("select" + ((((" cast(false as varchar(5)) c1," + " cast(true as varchar(4)) c2,") + " cast((1 < 5) as varchar(4)) c3,") + " cast((1 > 5) as varchar(5)) c4") + " from (values(1))"))).unOrdered().baselineColumns("c1", "c2", "c3", "c4").baselineValues("false", "true", "true", "false").go();
    }

    @Test
    public void testCastStringToBooleanTrueValue() throws Exception {
        List<String> literals = Arrays.asList("t", "true", "y", "yes", "on", "1");
        String query = "select cast('%s' as boolean) b_val from (values(1))";
        for (String literal : literals) {
            for (Function<String, String> function : TestSimpleCastFunctions.inputFunctions) {
                BaseTestQuery.testBuilder().sqlQuery(query, function.apply(literal)).unOrdered().baselineColumns("b_val").baselineValues(true).go();
            }
        }
    }

    @Test
    public void testCastStringToBooleanFalseValue() throws Exception {
        List<String> literals = Arrays.asList("f", "false", "n", "no", "off", "0");
        String query = "select cast('%s' as boolean) b_val from (values(1))";
        for (String literal : literals) {
            for (Function<String, String> function : TestSimpleCastFunctions.inputFunctions) {
                BaseTestQuery.testBuilder().sqlQuery(query, function.apply(literal)).unOrdered().baselineColumns("b_val").baselineValues(false).go();
            }
        }
    }

    @Test
    public void testCastNumericToBooleanTrueValue() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select cast(1 as boolean) b_val from (values(1))").unOrdered().baselineColumns("b_val").baselineValues(true).go();
    }

    @Test
    public void testCastNumericToBooleanFalseValue() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select cast(0 as boolean) b_val from (values(1))").unOrdered().baselineColumns("b_val").baselineValues(false).go();
    }

    @Test
    public void testCastNullToBoolean() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select cast(null as boolean) b_val from (values(1))").unOrdered().baselineColumns("b_val").baselineValues(((String) (null))).go();
    }

    @Test(expected = UserRemoteException.class)
    public void testIncorrectStringBoolean() throws Exception {
        try {
            BaseTestQuery.test("select cast('A' as boolean) b_val from (values(1))");
        } catch (UserRemoteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Invalid value for boolean: A"));
            throw e;
        }
    }

    @Test(expected = UserRemoteException.class)
    public void testIncorrectNumericBoolean() throws Exception {
        try {
            BaseTestQuery.test("select cast(123 as boolean) b_val from (values(1))");
        } catch (UserRemoteException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Invalid value for boolean: 123"));
            throw e;
        }
    }
}

