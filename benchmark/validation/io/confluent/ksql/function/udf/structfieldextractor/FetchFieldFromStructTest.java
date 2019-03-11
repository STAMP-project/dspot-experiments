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
package io.confluent.ksql.function.udf.structfieldextractor;


import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import io.confluent.ksql.function.KsqlFunctionException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class FetchFieldFromStructTest {
    private final FetchFieldFromStruct fetchFieldFromStruct = new FetchFieldFromStruct();

    private final Schema addressSchema = SchemaBuilder.struct().field("NUMBER", OPTIONAL_INT64_SCHEMA).field("STREET", OPTIONAL_STRING_SCHEMA).field("CITY", OPTIONAL_STRING_SCHEMA).field("STATE", OPTIONAL_STRING_SCHEMA).field("ZIPCODE", OPTIONAL_INT64_SCHEMA).optional().build();

    @Test
    public void shouldReturnCorrectField() {
        MatcherAssert.assertThat(fetchFieldFromStruct.evaluate(getStruct(), "NUMBER"), CoreMatchers.equalTo(101L));
    }

    @Test(expected = KsqlFunctionException.class)
    public void shouldFailIfFirstArgIsNotStruct() {
        fetchFieldFromStruct.evaluate(getStruct().get("STATE"), "STATE");
    }

    @Test
    public void shouldReturnNullIfFirstArgIsNull() {
        MatcherAssert.assertThat(fetchFieldFromStruct.evaluate(null, "NUMBER"), CoreMatchers.nullValue());
    }

    @Test(expected = KsqlFunctionException.class)
    public void shouldThrowIfArgSizeIsNot2() {
        fetchFieldFromStruct.evaluate();
    }
}

