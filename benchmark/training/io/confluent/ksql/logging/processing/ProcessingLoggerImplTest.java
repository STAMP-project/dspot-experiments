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
package io.confluent.ksql.logging.processing;


import Schema.OPTIONAL_STRING_SCHEMA;
import io.confluent.common.logging.StructuredLogger;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ProcessingLoggerImplTest {
    @Mock
    private StructuredLogger innerLogger;

    @Mock
    private ProcessingLogConfig processingLogConfig;

    @Mock
    private Function<ProcessingLogConfig, SchemaAndValue> msgFactory;

    @Mock
    private SchemaAndValue msg;

    @SuppressWarnings("unchecked")
    private final ArgumentCaptor<Supplier<SchemaAndValue>> msgCaptor = ArgumentCaptor.forClass(Supplier.class);

    private ProcessingLogger processingLogger;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldLogError() {
        // When:
        processingLogger.error(msgFactory);
        // Then:
        final SchemaAndValue msg = verifyErrorMessage();
        Assert.assertThat(msg, Matchers.is(this.msg));
    }

    @Test
    public void shouldBuildMessageUsingConfig() {
        // When:
        processingLogger.error(msgFactory);
        // Then:
        verifyErrorMessage();
        Mockito.verify(msgFactory).apply(processingLogConfig);
    }

    @Test
    public void shouldThrowOnBadSchema() {
        // Given:
        Mockito.when(msg.schema()).thenReturn(OPTIONAL_STRING_SCHEMA);
        // Then:
        expectedException.expect(RuntimeException.class);
        // When:
        processingLogger.error(msgFactory);
        verifyErrorMessage();
    }
}

