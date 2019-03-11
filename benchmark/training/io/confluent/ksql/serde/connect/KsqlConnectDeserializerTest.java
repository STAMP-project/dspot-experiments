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
package io.confluent.ksql.serde.connect;


import io.confluent.ksql.GenericRow;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import io.confluent.ksql.logging.processing.ProcessingLogger;
import io.confluent.ksql.serde.SerdeTestUtils;
import io.confluent.ksql.serde.util.SerdeProcessingLogMessageFactory;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.storage.Converter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class KsqlConnectDeserializerTest {
    private static final String TOPIC = "topic";

    private static final byte[] BYTES = "bizbazboz".getBytes(StandardCharsets.UTF_8);

    @Mock
    private Converter converter;

    @Mock
    private DataTranslator dataTranslator;

    @Mock
    private ProcessingLogger recordLogger;

    @Mock
    private Schema schema;

    @Mock
    private Object value;

    @Mock
    private GenericRow genericRow;

    private final ProcessingLogConfig processingLogConfig = new ProcessingLogConfig(Collections.emptyMap());

    private KsqlConnectDeserializer connectDeserializer;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void shouldDeserializeRecordsCorrectly() {
        // When:
        final GenericRow deserialized = connectDeserializer.deserialize(KsqlConnectDeserializerTest.TOPIC, KsqlConnectDeserializerTest.BYTES);
        // Then:
        Mockito.verify(converter, Mockito.times(1)).toConnectData(KsqlConnectDeserializerTest.TOPIC, KsqlConnectDeserializerTest.BYTES);
        Mockito.verify(dataTranslator, Mockito.times(1)).toKsqlRow(schema, value);
        Assert.assertThat(deserialized, Matchers.sameInstance(genericRow));
    }

    @Test
    public void shouldLogOnError() {
        // Given:
        final RuntimeException error = new RuntimeException("bad");
        Mockito.reset(converter);
        Mockito.when(converter.toConnectData(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(error);
        // When:
        try {
            connectDeserializer.deserialize(KsqlConnectDeserializerTest.TOPIC, KsqlConnectDeserializerTest.BYTES);
            Assert.fail("deserialize should have thrown");
        } catch (final RuntimeException caught) {
            Assert.assertThat(caught, Matchers.sameInstance(error));
        }
        SerdeTestUtils.shouldLogError(recordLogger, SerdeProcessingLogMessageFactory.deserializationErrorMsg(error, Optional.ofNullable(KsqlConnectDeserializerTest.BYTES)).apply(processingLogConfig), processingLogConfig);
    }
}

