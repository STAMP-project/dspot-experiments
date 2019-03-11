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
package io.confluent.ksql.serde.delimited;


import io.confluent.ksql.GenericRow;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import io.confluent.ksql.logging.processing.ProcessingLogger;
import io.confluent.ksql.serde.SerdeTestUtils;
import io.confluent.ksql.serde.util.SerdeProcessingLogMessageFactory;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.connect.data.Schema;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class KsqlDelimitedDeserializerTest {
    private Schema orderSchema;

    private final ProcessingLogConfig processingLogConfig = new ProcessingLogConfig(Collections.emptyMap());

    private KsqlDelimitedDeserializer delimitedDeserializer;

    @Mock
    private ProcessingLogger recordLogger;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void shouldDeserializeDelimitedCorrectly() {
        final String rowString = "1511897796092,1,item_1,10.0\r\n";
        final GenericRow genericRow = delimitedDeserializer.deserialize("", rowString.getBytes(StandardCharsets.UTF_8));
        Assert.assertThat(genericRow.getColumns().size(), CoreMatchers.equalTo(4));
        Assert.assertThat(genericRow.getColumns().get(0), CoreMatchers.equalTo(1511897796092L));
        Assert.assertThat(genericRow.getColumns().get(1), CoreMatchers.equalTo(1L));
        Assert.assertThat(genericRow.getColumns().get(2), CoreMatchers.equalTo("item_1"));
        Assert.assertThat(genericRow.getColumns().get(3), CoreMatchers.equalTo(10.0));
    }

    @Test
    public void shouldLogErrors() {
        Throwable cause = null;
        final byte[] record = "badnumfields".getBytes(StandardCharsets.UTF_8);
        try {
            delimitedDeserializer.deserialize("topic", record);
            Assert.fail("deserialize should have thrown");
        } catch (final SerializationException e) {
            cause = e.getCause();
        }
        SerdeTestUtils.shouldLogError(recordLogger, SerdeProcessingLogMessageFactory.deserializationErrorMsg(cause, Optional.ofNullable(record)).apply(processingLogConfig), processingLogConfig);
    }

    @Test
    public void shouldDeserializeJsonCorrectlyWithRedundantFields() {
        final String rowString = "1511897796092,1,item_1,\r\n";
        final GenericRow genericRow = delimitedDeserializer.deserialize("", rowString.getBytes(StandardCharsets.UTF_8));
        Assert.assertThat(genericRow.getColumns().size(), CoreMatchers.equalTo(4));
        Assert.assertThat(genericRow.getColumns().get(0), CoreMatchers.equalTo(1511897796092L));
        Assert.assertThat(genericRow.getColumns().get(1), CoreMatchers.equalTo(1L));
        Assert.assertThat(genericRow.getColumns().get(2), CoreMatchers.equalTo("item_1"));
        Assert.assertNull(genericRow.getColumns().get(3));
    }
}

