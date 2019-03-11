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


import KsqlConfig.KSQL_EXT_DIR;
import KsqlConfig.KSQL_SERVICE_ID_CONFIG;
import KsqlConfig.KSQL_UDF_SECURITY_MANAGER_ENABLED;
import KsqlFunction.INTERNAL_PATH;
import Schema.INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.common.Configurable;
import io.confluent.ksql.function.udf.Kudf;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * This uses ksql-engine/src/test/resource/udf-example.jar to load the custom jars.
 * You can find the classes it is loading in the same directory
 */
public class UdfLoaderTest {
    private final MutableFunctionRegistry functionRegistry = new InternalFunctionRegistry();

    private final UdfCompiler compiler = new UdfCompiler(Optional.empty());

    private final ClassLoader parentClassLoader = UdfLoaderTest.class.getClassLoader();

    private final Metrics metrics = new Metrics();

    private final UdfLoader pluginLoader = createUdfLoader(functionRegistry, true, false);

    private final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());

    @Test
    public void shouldLoadFunctionsInKsqlEngine() {
        final UdfFactory function = functionRegistry.getUdfFactory("substring");
        MatcherAssert.assertThat(function, CoreMatchers.not(CoreMatchers.nullValue()));
        final Kudf substring1 = function.getFunction(Arrays.asList(STRING_SCHEMA, INT32_SCHEMA)).newInstance(ksqlConfig);
        MatcherAssert.assertThat(substring1.evaluate("foo", 2), CoreMatchers.equalTo("oo"));
        final Kudf substring2 = function.getFunction(Arrays.asList(STRING_SCHEMA, INT32_SCHEMA, INT32_SCHEMA)).newInstance(ksqlConfig);
        MatcherAssert.assertThat(substring2.evaluate("foo", 2, 1), CoreMatchers.equalTo("o"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldLoadUdafs() {
        final KsqlAggregateFunction aggregate = functionRegistry.getAggregate("test_udaf", OPTIONAL_INT64_SCHEMA);
        final KsqlAggregateFunction<Long, Long> instance = aggregate.getInstance(new AggregateFunctionArguments(0, Collections.singletonList("udfIndex")));
        MatcherAssert.assertThat(instance.getInitialValueSupplier().get(), CoreMatchers.equalTo(0L));
        MatcherAssert.assertThat(instance.aggregate(1L, 1L), CoreMatchers.equalTo(2L));
        MatcherAssert.assertThat(instance.getMerger().apply("k", 2L, 3L), CoreMatchers.equalTo(5L));
    }

    @Test
    public void shouldLoadFunctionsFromJarsInPluginDir() {
        final UdfFactory toString = functionRegistry.getUdfFactory("tostring");
        final UdfFactory multi = functionRegistry.getUdfFactory("multiply");
        MatcherAssert.assertThat(toString, CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(multi, CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldLoadFunctionWithListReturnType() {
        // When:
        final UdfFactory toList = functionRegistry.getUdfFactory("tolist");
        // Then:
        MatcherAssert.assertThat(toList, CoreMatchers.not(CoreMatchers.nullValue()));
        final KsqlFunction function = toList.getFunction(Collections.singletonList(OPTIONAL_STRING_SCHEMA));
        MatcherAssert.assertThat(function.getReturnType(), CoreMatchers.equalTo(SchemaBuilder.array(OPTIONAL_STRING_SCHEMA).build()));
    }

    @Test
    public void shouldLoadFunctionWithMapReturnType() {
        // When:
        final UdfFactory toMap = functionRegistry.getUdfFactory("tomap");
        // Then:
        MatcherAssert.assertThat(toMap, CoreMatchers.not(CoreMatchers.nullValue()));
        final KsqlFunction function = toMap.getFunction(Collections.singletonList(OPTIONAL_STRING_SCHEMA));
        MatcherAssert.assertThat(function.getReturnType(), CoreMatchers.equalTo(SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA).build()));
    }

    @Test
    public void shouldPutJarUdfsInClassLoaderForJar() throws IllegalAccessException, NoSuchFieldException {
        final UdfFactory toString = functionRegistry.getUdfFactory("tostring");
        final UdfFactory multiply = functionRegistry.getUdfFactory("multiply");
        final Kudf toStringUdf = toString.getFunction(Collections.singletonList(STRING_SCHEMA)).newInstance(ksqlConfig);
        final Kudf multiplyUdf = multiply.getFunction(Arrays.asList(INT32_SCHEMA, INT32_SCHEMA)).newInstance(ksqlConfig);
        final ClassLoader multiplyLoader = UdfLoaderTest.getActualUdfClassLoader(multiplyUdf);
        MatcherAssert.assertThat(multiplyLoader, CoreMatchers.equalTo(UdfLoaderTest.getActualUdfClassLoader(toStringUdf)));
        MatcherAssert.assertThat(multiplyLoader, CoreMatchers.not(CoreMatchers.equalTo(parentClassLoader)));
    }

    @Test
    public void shouldCreateUdfFactoryWithJarPathWhenExternal() {
        final UdfFactory tostring = functionRegistry.getUdfFactory("tostring");
        MatcherAssert.assertThat(tostring.getPath(), CoreMatchers.equalTo("src/test/resources/udf-example.jar"));
    }

    @Test
    public void shouldCreateUdfFactoryWithInternalPathWhenInternal() {
        final UdfFactory substring = functionRegistry.getUdfFactory("substring");
        MatcherAssert.assertThat(substring.getPath(), CoreMatchers.equalTo(INTERNAL_PATH));
    }

    @Test
    public void shouldSupportUdfParameterAnnotation() {
        final UdfFactory substring = functionRegistry.getUdfFactory("somefunction");
        final KsqlFunction function = substring.getFunction(ImmutableList.of(OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA));
        final List<Schema> arguments = function.getArguments();
        MatcherAssert.assertThat(arguments.get(0).name(), Matchers.is("justValue"));
        MatcherAssert.assertThat(arguments.get(0).doc(), Matchers.is(""));
        MatcherAssert.assertThat(arguments.get(1).name(), Matchers.is("valueAndDescription"));
        MatcherAssert.assertThat(arguments.get(1).doc(), Matchers.is("Some description"));
        // NB: Is the below failing?
        // Then you need to add `-parameter` to your IDE's java compiler settings.
        MatcherAssert.assertThat(arguments.get(2).name(), Matchers.is("noValue"));
        MatcherAssert.assertThat(arguments.get(2).doc(), Matchers.is(""));
    }

    @Test
    public void shouldPutKsqlFunctionsInParentClassLoader() throws IllegalAccessException, NoSuchFieldException {
        final UdfFactory substring = functionRegistry.getUdfFactory("substring");
        final Kudf kudf = substring.getFunction(Arrays.asList(STRING_SCHEMA, INT32_SCHEMA)).newInstance(ksqlConfig);
        MatcherAssert.assertThat(UdfLoaderTest.getActualUdfClassLoader(kudf), CoreMatchers.equalTo(parentClassLoader));
    }

    @Test
    public void shouldLoadUdfsInKSQLIfLoadCustomerUdfsFalse() {
        // udf in ksql-engine will throw if not found
        loadKsqlUdfsOnly().getUdfFactory("substring");
    }

    @Test
    public void shouldNotLoadCustomUDfsIfLoadCustomUdfsFalse() {
        // udf in udf-example.jar
        try {
            loadKsqlUdfsOnly().getUdfFactory("tostring");
            Assert.fail("Should have thrown as function doesn't exist");
        } catch (final KsqlException e) {
            // pass
        }
    }

    @Test
    public void shouldCollectMetricsWhenMetricCollectionEnabled() {
        final InternalFunctionRegistry functionRegistry = new InternalFunctionRegistry();
        final UdfLoader pluginLoader = createUdfLoader(functionRegistry, true, true);
        pluginLoader.load();
        final UdfFactory substring = functionRegistry.getUdfFactory("substring");
        final KsqlFunction function = substring.getFunction(Arrays.asList(STRING_SCHEMA, INT32_SCHEMA));
        final Kudf kudf = function.newInstance(ksqlConfig);
        MatcherAssert.assertThat(kudf, CoreMatchers.instanceOf(UdfMetricProducer.class));
        final Sensor sensor = metrics.getSensor("ksql-udf-substring");
        MatcherAssert.assertThat(sensor, CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(metrics.metric(metrics.metricName("ksql-udf-substring-count", "ksql-udf")), CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(metrics.metric(metrics.metricName("ksql-udf-substring-max", "ksql-udf")), CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(metrics.metric(metrics.metricName("ksql-udf-substring-avg", "ksql-udf")), CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(metrics.metric(metrics.metricName("ksql-udf-substring-rate", "ksql-udf")), CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldUseConfigForExtDir() {
        final InternalFunctionRegistry functionRegistry = new InternalFunctionRegistry();
        // The tostring function is in the udf-example.jar that is found in src/test/resources
        final ImmutableMap<Object, Object> configMap = ImmutableMap.builder().put(KSQL_EXT_DIR, "src/test/resources").put(KSQL_UDF_SECURITY_MANAGER_ENABLED, false).build();
        final KsqlConfig config = new KsqlConfig(configMap);
        UdfLoader.newInstance(config, functionRegistry, "").load();
        // will throw if it doesn't exist
        functionRegistry.getUdfFactory("tostring");
    }

    @Test
    public void shouldNotThrowWhenExtDirDoesntExist() {
        final ImmutableMap<Object, Object> configMap = ImmutableMap.builder().put(KSQL_EXT_DIR, "foo/bar").put(KSQL_UDF_SECURITY_MANAGER_ENABLED, false).build();
        final KsqlConfig config = new KsqlConfig(configMap);
        UdfLoader.newInstance(config, new InternalFunctionRegistry(), "").load();
    }

    @Test
    public void shouldConfigureConfigurableUdfsOnInstantiation() {
        // Given:
        final KsqlConfig ksqlConfig = new KsqlConfig(ImmutableMap.of(KSQL_SERVICE_ID_CONFIG, "should not be passed", ((KsqlConfig.KSQL_FUNCTIONS_PROPERTY_PREFIX) + "configurableudf.some.setting"), "foo-bar", ((KsqlConfig.KSQL_FUNCTIONS_PROPERTY_PREFIX) + "_global_.expected-param"), "expected-value"));
        final KsqlFunction udf = functionRegistry.getUdfFactory("ConfigurableUdf").getFunction(ImmutableList.of(INT32_SCHEMA));
        // When:
        udf.newInstance(ksqlConfig);
        // Then:
        MatcherAssert.assertThat(UdfLoaderTest.PASSED_CONFIG, Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(UdfLoaderTest.PASSED_CONFIG.keySet(), CoreMatchers.not(Matchers.hasItem(KSQL_SERVICE_ID_CONFIG)));
        MatcherAssert.assertThat(UdfLoaderTest.PASSED_CONFIG.get(((KsqlConfig.KSQL_FUNCTIONS_PROPERTY_PREFIX) + "configurableudf.some.setting")), Matchers.is("foo-bar"));
        MatcherAssert.assertThat(UdfLoaderTest.PASSED_CONFIG.get(((KsqlConfig.KSQL_FUNCTIONS_PROPERTY_PREFIX) + "_global_.expected-param")), Matchers.is("expected-value"));
    }

    // Invoked via reflection in test.
    @SuppressWarnings({ "unused", "MethodMayBeStatic" })
    public static class UdfWithMissingDescriptionAnnotation {
        @Udf(description = "This invalid UDF is here to test that the loader does not blow up if badly" + " formed UDFs are in the class path.")
        public String something(final String value) {
            return null;
        }
    }

    private static Map<String, ?> PASSED_CONFIG = null;

    // Invoked via reflection in test.
    @SuppressWarnings({ "unused", "MethodMayBeStatic" })
    @UdfDescription(name = "ConfigurableUdf", description = "A test-only UDF for testing configure() is called")
    public static class ConfigurableUdf implements Configurable {
        @Override
        public void configure(final Map<String, ?> map) {
            UdfLoaderTest.PASSED_CONFIG = map;
        }

        @Udf
        public int foo(final int bar) {
            return bar;
        }
    }

    // Invoked via reflection in test.
    @SuppressWarnings({ "unused", "MethodMayBeStatic" })
    @UdfDescription(name = "SomeFunction", description = "A test-only UDF for testing configure() is called")
    public static class SomeFunctionUdf {
        @Udf
        public int foo(@UdfParameter("justValue")
        final String v0, @UdfParameter(value = "valueAndDescription", description = "Some description")
        final String v1, @UdfParameter
        final String noValue) {
            return 0;
        }
    }
}

