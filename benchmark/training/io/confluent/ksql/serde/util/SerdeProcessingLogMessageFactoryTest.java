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
package io.confluent.ksql.serde.util;


import ProcessingLogConfig.INCLUDE_ROWS;
import ProcessingLogMessageSchema.TYPE;
import com.google.common.collect.ImmutableList;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.Struct;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SerdeProcessingLogMessageFactoryTest {
    private final byte[] record = new byte[256];

    private final Exception error = new Exception("error message");

    private final ProcessingLogConfig config = new ProcessingLogConfig(Collections.singletonMap(INCLUDE_ROWS, true));

    @Test
    public void shouldSetNullRecordToNull() {
        // When:
        final SchemaAndValue msg = SerdeProcessingLogMessageFactory.deserializationErrorMsg(error, Optional.empty()).apply(config);
        // Then:
        final Struct struct = ((Struct) (msg.value()));
        final Struct deserializationError = struct.getStruct(DESERIALIZATION_ERROR);
        Assert.assertThat(deserializationError.get(DESERIALIZATION_ERROR_FIELD_RECORD_B64), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldBuildDeserializationError() {
        // When:
        final SchemaAndValue msg = SerdeProcessingLogMessageFactory.deserializationErrorMsg(error, Optional.of(record)).apply(config);
        // Then:
        final Schema schema = msg.schema();
        Assert.assertThat(schema, Matchers.equalTo(PROCESSING_LOG_SCHEMA));
        final Struct struct = ((Struct) (msg.value()));
        Assert.assertThat(struct.get(TYPE), Matchers.equalTo(MessageType.DESERIALIZATION_ERROR.getTypeId()));
        final Struct deserializationError = struct.getStruct(DESERIALIZATION_ERROR);
        Assert.assertThat(deserializationError.get(DESERIALIZATION_ERROR_FIELD_MESSAGE), Matchers.equalTo(error.getMessage()));
        Assert.assertThat(deserializationError.get(DESERIALIZATION_ERROR_FIELD_RECORD_B64), Matchers.equalTo(Base64.getEncoder().encodeToString(record)));
        schema.fields().forEach(( f) -> {
            if (!(ImmutableList.of(TYPE, DESERIALIZATION_ERROR).contains(f.name()))) {
                assertThat(struct.get(f), is(nullValue()));
            }
        });
    }

    @Test
    public void shouldBuildDeserializationErrorWithNullRecordIfIncludeRowFalse() {
        // Given:
        final ProcessingLogConfig config = new ProcessingLogConfig(Collections.singletonMap(INCLUDE_ROWS, false));
        // When:
        final SchemaAndValue msg = SerdeProcessingLogMessageFactory.deserializationErrorMsg(error, Optional.of(record)).apply(config);
        // Then:
        final Struct struct = ((Struct) (msg.value()));
        Assert.assertThat(struct.get(TYPE), Matchers.equalTo(MessageType.DESERIALIZATION_ERROR.getTypeId()));
        final Struct deserializationError = struct.getStruct(DESERIALIZATION_ERROR);
        Assert.assertThat(deserializationError.get(DESERIALIZATION_ERROR_FIELD_RECORD_B64), Matchers.nullValue());
    }
}

