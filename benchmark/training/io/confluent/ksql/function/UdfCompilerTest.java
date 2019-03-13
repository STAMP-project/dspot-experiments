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
package io.confluent.ksql.function;


import io.confluent.ksql.function.udaf.TestUdaf;
import io.confluent.ksql.util.KsqlException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.Metrics;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class UdfCompilerTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final ClassLoader classLoader = UdfCompilerTest.class.getClassLoader();

    private final UdfCompiler udfCompiler = new UdfCompiler(Optional.empty());

    @Test
    public void shouldCompileFunctionWithMapArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udf", Map.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, Collections.emptyMap()), CoreMatchers.equalTo("{}"));
    }

    @Test
    public void shouldCompileFunctionWithListArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udf", List.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, Collections.emptyList()), CoreMatchers.equalTo("[]"));
    }

    @Test
    public void shouldCompileFunctionWithDoubleArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udf", Double.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, 1), CoreMatchers.equalTo(1.0));
    }

    @Test
    public void shouldCompileFunctionWithIntegerArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udf", Integer.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, 1), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldCompileFunctionWithLongArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udf", Long.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, 1), CoreMatchers.equalTo(1L));
    }

    @Test
    public void shouldCompileFunctionWithBooleanArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udf", Boolean.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, true), CoreMatchers.equalTo(true));
    }

    @Test
    public void shouldCompileFunctionWithIntArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udfPrimitive", int.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, 1), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldCompileFunctionWithPrimitiveLongArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udfPrimitive", long.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, 1), CoreMatchers.equalTo(1L));
    }

    @Test
    public void shouldCompileFunctionWithPrimitiveDoubleArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udfPrimitive", double.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, 1), CoreMatchers.equalTo(1.0));
    }

    @Test
    public void shouldCompileFunctionWithPrimitiveBooleanArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udfPrimitive", boolean.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, true), CoreMatchers.equalTo(true));
    }

    @Test
    public void shouldCompileFunctionWithStringArgument() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udf", String.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, "foo"), CoreMatchers.equalTo("foo"));
    }

    @Test
    public void shouldHandleMethodsWithMultipleArguments() throws NoSuchMethodException {
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("multi", int.class, long.class, double.class), classLoader);
        MatcherAssert.assertThat(udf.eval(this, 1, 2, 3), CoreMatchers.equalTo(6.0));
    }

    @Test
    public void shouldCompileUdafWithMethodWithNoArgs() throws NoSuchMethodException {
        final KsqlAggregateFunction function = udfCompiler.compileAggregate(TestUdaf.class.getMethod("createSumLong"), classLoader, "test-udf", "desc");
        MatcherAssert.assertThat(function.getInstance(new AggregateFunctionArguments(0, Collections.singletonList("udfIndex"))), CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldImplementTableAggregateFunctionWhenTableUdafClass() throws NoSuchMethodException {
        final KsqlAggregateFunction function = udfCompiler.compileAggregate(TestUdaf.class.getMethod("createSumLong"), classLoader, "test-udf", "desc");
        MatcherAssert.assertThat(function, CoreMatchers.instanceOf(TableAggregationFunction.class));
    }

    @Test
    public void shouldCompileUdafWhenMethodHasArgs() throws NoSuchMethodException {
        final KsqlAggregateFunction function = udfCompiler.compileAggregate(TestUdaf.class.getMethod("createSumLengthString", String.class), classLoader, "test-udf", "desc");
        final KsqlAggregateFunction instance = function.getInstance(new AggregateFunctionArguments(0, Arrays.asList("udfIndex", "some string")));
        MatcherAssert.assertThat(instance, CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(instance, CoreMatchers.not(CoreMatchers.instanceOf(TableAggregationFunction.class)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCollectMetricsForUdafsWhenEnabled() throws NoSuchMethodException {
        final Metrics metrics = new Metrics();
        final UdfCompiler udfCompiler = new UdfCompiler(Optional.of(metrics));
        final KsqlAggregateFunction function = udfCompiler.compileAggregate(TestUdaf.class.getMethod("createSumLong"), classLoader, "test-udf", "desc");
        final KsqlAggregateFunction<Long, Long> executable = function.getInstance(new AggregateFunctionArguments(0, Collections.singletonList("udfIndex")));
        executable.aggregate(1L, 1L);
        executable.aggregate(1L, 1L);
        final KafkaMetric metric = metrics.metric(metrics.metricName("aggregate-test-udf-createSumLong-count", "ksql-udaf-test-udf-createSumLong"));
        MatcherAssert.assertThat(metric.metricValue(), CoreMatchers.equalTo(2.0));
    }

    @Test(expected = KsqlException.class)
    public void shouldThrowIfUnsupportedArgumentType() throws NoSuchMethodException {
        udfCompiler.compile(getClass().getMethod("udf", Set.class), classLoader);
    }

    @Test
    public void shouldThrowKsqlFunctionExceptionIfNullPassedWhenExpectingPrimitiveType() throws NoSuchMethodException {
        expectedException.expect(KsqlFunctionException.class);
        expectedException.expectMessage("Can't coerce argument at index 0 from null to a primitive type");
        final UdfInvoker udf = udfCompiler.compile(getClass().getMethod("udfPrimitive", double.class), classLoader);
        udf.eval(this, new Object[]{ null });
    }

    @Test
    public void shouldThrowWhenUdafReturnTypeIsntAUdaf() throws NoSuchMethodException {
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage(("UDAFs must implement io.confluent.ksql.function.udaf.Udaf " + ("or io.confluent.ksql.function.udaf.TableUdaf .method='createBlah', functionName='test'" + " UDFClass='class io.confluent.ksql.function.UdfCompilerTest")));
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createBlah"), classLoader, "test", "desc");
    }

    @Test
    public void shouldHandleUdafsWithLongValTypeDoubleAggType() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createLongDouble"), classLoader, "test", "desc");
    }

    @Test
    public void shouldHandleUdafsWithDoubleValTypeLongAggType() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createDoubleLong"), classLoader, "test", "desc");
    }

    @Test
    public void shouldHandleUdafsWithIntegerValTypeStringAggType() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createIntegerString"), classLoader, "test", "desc");
    }

    @Test
    public void shouldHandleUdafsWithStringValTypeIntegerAggType() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createStringInteger"), classLoader, "test", "desc");
    }

    @Test
    public void shouldHandleUdafsWithBooleanValTypeListAggType() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createBooleanList"), classLoader, "test", "desc");
    }

    @Test
    public void shouldHandleUdafsWithListValTypeBooleamAggType() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createListBoolean"), classLoader, "test", "desc");
    }

    @Test
    public void shouldHandleUdafsWithMapValMapAggTypes() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createMapMap"), classLoader, "test", "desc");
    }

    @Test
    public void shouldHandleUdafsWithMapValMapAggTypesAndFactoryArg() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createMapMap", int.class), classLoader, "test", "desc");
    }

    @Test(expected = KsqlException.class)
    public void shouldThrowWhenTryingToGenerateUdafThatHasIncorrectTypes() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createBad"), classLoader, "test", "desc");
    }

    @Test(expected = KsqlException.class)
    public void shouldThrowWhenUdafFactoryMethodIsntStatic() throws NoSuchMethodException {
        udfCompiler.compileAggregate(UdfCompilerTest.class.getMethod("createNonStatic"), classLoader, "test", "desc");
    }
}

