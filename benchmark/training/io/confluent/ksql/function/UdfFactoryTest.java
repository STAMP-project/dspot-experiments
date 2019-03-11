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


import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import com.google.common.collect.ImmutableList;
import io.confluent.ksql.function.udf.Kudf;
import io.confluent.ksql.function.udf.UdfMetadata;
import io.confluent.ksql.util.KsqlException;
import java.util.Arrays;
import java.util.Collections;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.connect.data.Schema;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class UdfFactoryTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final String functionName = "TestFunc";

    private final UdfFactory factory = new UdfFactory(UdfFactoryTest.TestFunc.class, new UdfMetadata(UdfFactoryTest.functionName, "", "", "", "internal", false));

    @Test
    public void shouldThrowIfNoVariantFoundThatAcceptsSuppliedParamTypes() {
        expectedException.expect(KafkaException.class);
        expectedException.expectMessage("Function 'TestFunc' does not accept parameters of types:[VARCHAR(STRING), BIGINT]");
        factory.getFunction(ImmutableList.of(STRING_SCHEMA, INT64_SCHEMA));
    }

    @Test
    public void shouldFindFirstMatchingFunctionWhenNullTypeInArgs() {
        final KsqlFunction expected = KsqlFunction.createLegacyBuiltIn(STRING_SCHEMA, Collections.singletonList(OPTIONAL_STRING_SCHEMA), UdfFactoryTest.functionName, UdfFactoryTest.TestFunc.class);
        factory.addFunction(expected);
        factory.addFunction(KsqlFunction.createLegacyBuiltIn(STRING_SCHEMA, Collections.singletonList(OPTIONAL_INT64_SCHEMA), UdfFactoryTest.functionName, UdfFactoryTest.TestFunc.class));
        final KsqlFunction function = factory.getFunction(Collections.singletonList(null));
        MatcherAssert.assertThat(function, CoreMatchers.equalTo(expected));
    }

    @Test
    public void shouldNotMatchingFunctionWhenNullTypeInArgsIfParamLengthsDiffer() {
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("VARCHAR(STRING), null");
        final KsqlFunction function = KsqlFunction.createLegacyBuiltIn(STRING_SCHEMA, Collections.singletonList(OPTIONAL_STRING_SCHEMA), UdfFactoryTest.functionName, UdfFactoryTest.TestFunc.class);
        factory.addFunction(function);
        factory.getFunction(Arrays.asList(STRING_SCHEMA, null));
    }

    @Test
    public void shouldThrowExceptionWhenAtLeastOneArgumentOtherThanNullDoesntMatch() {
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("BIGINT, null");
        final KsqlFunction function = KsqlFunction.createLegacyBuiltIn(STRING_SCHEMA, Arrays.asList(OPTIONAL_STRING_SCHEMA, OPTIONAL_STRING_SCHEMA), UdfFactoryTest.functionName, UdfFactoryTest.TestFunc.class);
        factory.addFunction(function);
        factory.getFunction(Arrays.asList(OPTIONAL_INT64_SCHEMA, null));
    }

    @Test
    public void shouldThrowWhenNullAndPrimitiveTypeArg() {
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("VARCHAR(STRING), null");
        final KsqlFunction function = KsqlFunction.createLegacyBuiltIn(STRING_SCHEMA, Arrays.asList(OPTIONAL_STRING_SCHEMA, INT32_SCHEMA), UdfFactoryTest.functionName, UdfFactoryTest.TestFunc.class);
        factory.addFunction(function);
        factory.getFunction(Arrays.asList(STRING_SCHEMA, null));
    }

    @Test
    public void shouldMatchNullWithStringSchema() {
        final KsqlFunction function = KsqlFunction.createLegacyBuiltIn(STRING_SCHEMA, Arrays.asList(INT64_SCHEMA, OPTIONAL_STRING_SCHEMA), UdfFactoryTest.functionName, UdfFactoryTest.TestFunc.class);
        factory.addFunction(function);
        factory.getFunction(Arrays.asList(OPTIONAL_INT64_SCHEMA, null));
    }

    @Test
    public void shouldThrowExceptionIfAddingFunctionWithDifferentPath() {
        expectedException.expect(KafkaException.class);
        expectedException.expectMessage("as a function with the same name has been loaded from a different jar");
        factory.addFunction(KsqlFunction.create(STRING_SCHEMA, Collections.<Schema>emptyList(), "TestFunc", UdfFactoryTest.TestFunc.class, ( ksqlConfig) -> null, "", "not the same path"));
    }

    private abstract class TestFunc implements Kudf {}
}

