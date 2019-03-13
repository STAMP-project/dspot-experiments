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
package io.confluent.ksql.benchmark;


import com.google.common.collect.ImmutableList;
import io.confluent.ksql.benchmark.SerdeBenchmark.SerdeState;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SerdeBenchmarkTest {
    private static final List<String> SCHEMAS = ImmutableList.of("impressions", "metrics");

    private static final List<String> FORMATS = ImmutableList.of("JSON", "Avro");

    private static final String TOPIC_NAME = "serde_benchmark";

    private final String schemaName;

    private final String serializationFormat;

    private SerdeState serdeState;

    public SerdeBenchmarkTest(final String schemaName, final String serializationFormat) {
        this.schemaName = schemaName;
        this.serializationFormat = serializationFormat;
    }

    @Test
    public void shouldSerializeDeserialize() {
        MatcherAssert.assertThat(serdeState.serializer.serialize(SerdeBenchmarkTest.TOPIC_NAME, serdeState.row), CoreMatchers.is(serdeState.bytes));
        MatcherAssert.assertThat(serdeState.deserializer.deserialize(SerdeBenchmarkTest.TOPIC_NAME, serdeState.bytes), CoreMatchers.is(serdeState.row));
    }
}

