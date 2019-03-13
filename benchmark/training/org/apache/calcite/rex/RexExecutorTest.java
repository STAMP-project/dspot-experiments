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
package org.apache.calcite.rex;


import SqlStdOperatorTable.LESS_THAN;
import SqlStdOperatorTable.LESS_THAN_OR_EQUAL;
import SqlStdOperatorTable.PLUS;
import SqlStdOperatorTable.SUBSTRING;
import SqlTypeName.INTEGER;
import SqlTypeName.VARCHAR;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.avatica.util.ByteString;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlBinaryOperator;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.fun.SqlMonotonicBinaryOperator;
import org.apache.calcite.sql.type.InferTypes;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.util.DateString;
import org.apache.calcite.util.NlsString;
import org.apache.calcite.util.Util;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link org.apache.calcite.rex.RexExecutorImpl}.
 */
public class RexExecutorTest {
    public RexExecutorTest() {
    }

    /**
     * Tests an executor that uses variables stored in a {@link DataContext}.
     * Can change the value of the variable and execute again.
     */
    @Test
    public void testVariableExecution() throws Exception {
        check(( rexBuilder, executor) -> {
            Object[] values = new Object[1];
            final DataContext testContext = new RexExecutorTest.TestDataContext(values);
            final RelDataTypeFactory typeFactory = rexBuilder.getTypeFactory();
            final RelDataType varchar = typeFactory.createSqlType(VARCHAR);
            final RelDataType integer = typeFactory.createSqlType(INTEGER);
            // Calcite is internally creating the input ref via a RexRangeRef
            // which eventually leads to a RexInputRef. So we are good.
            final RexInputRef input = rexBuilder.makeInputRef(varchar, 0);
            final RexNode lengthArg = rexBuilder.makeLiteral(3, integer, true);
            final RexNode substr = rexBuilder.makeCall(SUBSTRING, input, lengthArg);
            ImmutableList<RexNode> constExps = ImmutableList.of(substr);
            final RelDataType rowType = typeFactory.builder().add("someStr", varchar).build();
            final RexExecutable exec = executor.getExecutable(rexBuilder, constExps, rowType);
            exec.setDataContext(testContext);
            values[0] = "Hello World";
            Object[] result = exec.execute();
            Assert.assertTrue(((result[0]) instanceof String));
            Assert.assertThat(((String) (result[0])), CoreMatchers.equalTo("llo World"));
            values[0] = "Calcite";
            result = exec.execute();
            Assert.assertTrue(((result[0]) instanceof String));
            Assert.assertThat(((String) (result[0])), CoreMatchers.equalTo("lcite"));
        });
    }

    @Test
    public void testConstant() throws Exception {
        check(( rexBuilder, executor) -> {
            final List<RexNode> reducedValues = new ArrayList<>();
            final RexLiteral ten = rexBuilder.makeExactLiteral(BigDecimal.TEN);
            executor.reduce(rexBuilder, ImmutableList.of(ten), reducedValues);
            Assert.assertThat(reducedValues.size(), CoreMatchers.equalTo(1));
            Assert.assertThat(reducedValues.get(0), CoreMatchers.instanceOf(RexLiteral.class));
            Assert.assertThat(getValue2(), CoreMatchers.equalTo(((Object) (10L))));
        });
    }

    /**
     * Reduces several expressions to constants.
     */
    @Test
    public void testConstant2() throws Exception {
        // Same as testConstant; 10 -> 10
        checkConstant(10L, ( rexBuilder) -> rexBuilder.makeExactLiteral(BigDecimal.TEN));
        // 10 + 1 -> 11
        checkConstant(11L, ( rexBuilder) -> rexBuilder.makeCall(PLUS, rexBuilder.makeExactLiteral(BigDecimal.TEN), rexBuilder.makeExactLiteral(BigDecimal.ONE)));
        // date 'today' <= date 'today' -> true
        checkConstant(true, ( rexBuilder) -> {
            final DateString d = DateString.fromCalendarFields(Util.calendar());
            return rexBuilder.makeCall(LESS_THAN_OR_EQUAL, rexBuilder.makeDateLiteral(d), rexBuilder.makeDateLiteral(d));
        });
        // date 'today' < date 'today' -> false
        checkConstant(false, ( rexBuilder) -> {
            final DateString d = DateString.fromCalendarFields(Util.calendar());
            return rexBuilder.makeCall(LESS_THAN, rexBuilder.makeDateLiteral(d), rexBuilder.makeDateLiteral(d));
        });
    }

    @Test
    public void testSubstring() throws Exception {
        check(( rexBuilder, executor) -> {
            final List<RexNode> reducedValues = new ArrayList<>();
            final RexLiteral hello = rexBuilder.makeCharLiteral(new NlsString("Hello world!", null, null));
            final RexNode plus = rexBuilder.makeCall(PLUS, rexBuilder.makeExactLiteral(BigDecimal.ONE), rexBuilder.makeExactLiteral(BigDecimal.ONE));
            RexLiteral four = rexBuilder.makeExactLiteral(BigDecimal.valueOf(4));
            final RexNode substring = rexBuilder.makeCall(SUBSTRING, hello, plus, four);
            executor.reduce(rexBuilder, ImmutableList.of(substring, plus), reducedValues);
            Assert.assertThat(reducedValues.size(), CoreMatchers.equalTo(2));
            Assert.assertThat(reducedValues.get(0), CoreMatchers.instanceOf(RexLiteral.class));
            Assert.assertThat(getValue2(), CoreMatchers.equalTo(((Object) ("ello"))));// substring('Hello world!, 2, 4)

            Assert.assertThat(reducedValues.get(1), CoreMatchers.instanceOf(RexLiteral.class));
            Assert.assertThat(getValue2(), CoreMatchers.equalTo(((Object) (2L))));
        });
    }

    @Test
    public void testBinarySubstring() throws Exception {
        check(( rexBuilder, executor) -> {
            final List<RexNode> reducedValues = new ArrayList<>();
            // hello world! -> 48656c6c6f20776f726c6421
            final RexLiteral binaryHello = rexBuilder.makeBinaryLiteral(new ByteString("Hello world!".getBytes(StandardCharsets.UTF_8)));
            final RexNode plus = rexBuilder.makeCall(PLUS, rexBuilder.makeExactLiteral(BigDecimal.ONE), rexBuilder.makeExactLiteral(BigDecimal.ONE));
            RexLiteral four = rexBuilder.makeExactLiteral(BigDecimal.valueOf(4));
            final RexNode substring = rexBuilder.makeCall(SUBSTRING, binaryHello, plus, four);
            executor.reduce(rexBuilder, ImmutableList.of(substring, plus), reducedValues);
            Assert.assertThat(reducedValues.size(), CoreMatchers.equalTo(2));
            Assert.assertThat(reducedValues.get(0), CoreMatchers.instanceOf(RexLiteral.class));
            Assert.assertThat(getValue2().toString(), CoreMatchers.equalTo(((Object) ("656c6c6f"))));// substring('Hello world!, 2, 4)

            Assert.assertThat(reducedValues.get(1), CoreMatchers.instanceOf(RexLiteral.class));
            Assert.assertThat(getValue2(), CoreMatchers.equalTo(((Object) (2L))));
        });
    }

    @Test
    public void testDeterministic1() throws Exception {
        check(( rexBuilder, executor) -> {
            final RexNode plus = rexBuilder.makeCall(PLUS, rexBuilder.makeExactLiteral(BigDecimal.ONE), rexBuilder.makeExactLiteral(BigDecimal.ONE));
            Assert.assertThat(RexUtil.isDeterministic(plus), CoreMatchers.equalTo(true));
        });
    }

    @Test
    public void testDeterministic2() throws Exception {
        check(( rexBuilder, executor) -> {
            final RexNode plus = rexBuilder.makeCall(RexExecutorTest.PLUS_RANDOM, rexBuilder.makeExactLiteral(BigDecimal.ONE), rexBuilder.makeExactLiteral(BigDecimal.ONE));
            Assert.assertThat(RexUtil.isDeterministic(plus), CoreMatchers.equalTo(false));
        });
    }

    @Test
    public void testDeterministic3() throws Exception {
        check(( rexBuilder, executor) -> {
            final RexNode plus = rexBuilder.makeCall(PLUS, rexBuilder.makeCall(RexExecutorTest.PLUS_RANDOM, rexBuilder.makeExactLiteral(BigDecimal.ONE), rexBuilder.makeExactLiteral(BigDecimal.ONE)), rexBuilder.makeExactLiteral(BigDecimal.ONE));
            Assert.assertThat(RexUtil.isDeterministic(plus), CoreMatchers.equalTo(false));
        });
    }

    private static final SqlBinaryOperator PLUS_RANDOM = new SqlMonotonicBinaryOperator("+", SqlKind.PLUS, 40, true, ReturnTypes.NULLABLE_SUM, InferTypes.FIRST_KNOWN, OperandTypes.PLUS_OPERATOR) {
        @Override
        public boolean isDeterministic() {
            return false;
        }
    };

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1009">[CALCITE-1009]
     * SelfPopulatingList is not thread-safe</a>.
     */
    @Test
    public void testSelfPopulatingList() {
        final List<Thread> threads = new ArrayList<>();
        // noinspection MismatchedQueryAndUpdateOfCollection
        final List<String> list = new RexSlot.SelfPopulatingList("$", 1);
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread() {
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        // Random numbers between 0 and ~1m, smaller values more common
                        final int index = ((random.nextInt(1234567)) >> (random.nextInt(16))) >> (random.nextInt(16));
                        list.get(index);
                    }
                }
            });
        }
        for (Thread runnable : threads) {
            runnable.start();
        }
        for (Thread runnable : threads) {
            try {
                runnable.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            Assert.assertThat(list.get(i), CoreMatchers.is(("$" + i)));
        }
    }

    @Test
    public void testSelfPopulatingList30() {
        // noinspection MismatchedQueryAndUpdateOfCollection
        final List<String> list = new RexSlot.SelfPopulatingList("$", 30);
        final String s = list.get(30);
        Assert.assertThat(s, CoreMatchers.is("$30"));
    }

    /**
     * Callback for {@link #check}. Test code will typically use {@code builder}
     * to create some expressions, call
     * {@link org.apache.calcite.rex.RexExecutorImpl#reduce} to evaluate them into
     * a list, then check that the results are as expected.
     */
    interface Action {
        void check(RexBuilder rexBuilder, RexExecutorImpl executor);
    }

    /**
     * ArrayList-based DataContext to check Rex execution.
     */
    public static class TestDataContext implements DataContext {
        private final Object[] values;

        public TestDataContext(Object[] values) {
            this.values = values;
        }

        public SchemaPlus getRootSchema() {
            throw new RuntimeException("Unsupported");
        }

        public JavaTypeFactory getTypeFactory() {
            throw new RuntimeException("Unsupported");
        }

        public QueryProvider getQueryProvider() {
            throw new RuntimeException("Unsupported");
        }

        public Object get(String name) {
            if (name.equals("inputRecord")) {
                return values;
            } else {
                Assert.fail("Wrong DataContext access");
                return null;
            }
        }
    }
}

/**
 * End RexExecutorTest.java
 */
