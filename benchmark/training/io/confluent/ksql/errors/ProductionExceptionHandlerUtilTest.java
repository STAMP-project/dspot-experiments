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
package io.confluent.ksql.errors;


import MessageType.PRODUCTION_ERROR;
import ProcessingLogMessageSchema.PROCESSING_LOG_SCHEMA;
import ProcessingLogMessageSchema.PRODUCTION_ERROR_FIELD_MESSAGE;
import ProcessingLogMessageSchema.TYPE;
import ProductionExceptionHandlerResponse.CONTINUE;
import ProductionExceptionHandlerResponse.FAIL;
import io.confluent.ksql.errors.ProductionExceptionHandlerUtil.LogAndContinueProductionExceptionHandler;
import io.confluent.ksql.errors.ProductionExceptionHandlerUtil.LogAndFailProductionExceptionHandler;
import io.confluent.ksql.errors.ProductionExceptionHandlerUtil.LogAndXProductionExceptionHandler;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import io.confluent.ksql.logging.processing.ProcessingLogger;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.streams.errors.ProductionExceptionHandler.ProductionExceptionHandlerResponse;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ProductionExceptionHandlerUtilTest {
    private Map<String, ?> CONFIGS;

    @Mock
    private ProcessingLogger logger;

    @Captor
    private ArgumentCaptor<Function<ProcessingLogConfig, SchemaAndValue>> msgCaptor;

    @Mock
    private ProducerRecord<byte[], byte[]> record;

    @Mock
    private ProductionExceptionHandlerResponse mockResponse;

    private final ProcessingLogConfig processingLogConfig = new ProcessingLogConfig(Collections.emptyMap());

    private LogAndXProductionExceptionHandler exceptionHandler;

    @Test
    public void shouldReturnLogAndFailHandler() {
        MatcherAssert.assertThat(ProductionExceptionHandlerUtil.getHandler(true), Matchers.equalTo(LogAndFailProductionExceptionHandler.class));
    }

    @Test
    public void shouldReturnLogAndContinueHandler() {
        MatcherAssert.assertThat(ProductionExceptionHandlerUtil.getHandler(false), Matchers.equalTo(LogAndContinueProductionExceptionHandler.class));
    }

    @Test
    public void shouldLogErrorCorrectly() {
        // When:
        exceptionHandler.handle(record, new Exception("foo"));
        // Then:
        Mockito.verify(logger).error(msgCaptor.capture());
        final SchemaAndValue schemaAndValue = msgCaptor.getValue().apply(processingLogConfig);
        MatcherAssert.assertThat(schemaAndValue.schema(), CoreMatchers.is(PROCESSING_LOG_SCHEMA));
        final Struct msg = ((Struct) (schemaAndValue.value()));
        MatcherAssert.assertThat(msg.get(TYPE), CoreMatchers.is(PRODUCTION_ERROR.getTypeId()));
        MatcherAssert.assertThat(msg.get(ProcessingLogMessageSchema.PRODUCTION_ERROR), CoreMatchers.notNullValue());
        final Struct productionError = msg.getStruct(ProcessingLogMessageSchema.PRODUCTION_ERROR);
        MatcherAssert.assertThat(productionError.schema(), CoreMatchers.is(PRODUCTION_ERROR.getSchema()));
        MatcherAssert.assertThat(productionError.get(PRODUCTION_ERROR_FIELD_MESSAGE), CoreMatchers.is("foo"));
    }

    @Test
    public void shouldReturnCorrectResponse() {
        assertResponseIs(mockResponse);
    }

    @Test
    public void shouldReturnFailFromLogAndFailHandler() {
        // Given:
        exceptionHandler = new LogAndFailProductionExceptionHandler();
        exceptionHandler.configure(CONFIGS);
        // Then:
        assertResponseIs(FAIL);
    }

    @Test
    public void shouldReturnContinueFromLogAndContinueHandler() {
        // Given:
        exceptionHandler = new LogAndContinueProductionExceptionHandler();
        exceptionHandler.configure(CONFIGS);
        // Then:
        assertResponseIs(CONTINUE);
    }

    private static class TestLogAndXProductionExceptionHandler extends LogAndXProductionExceptionHandler {
        private final ProductionExceptionHandlerResponse response;

        private TestLogAndXProductionExceptionHandler(ProductionExceptionHandlerResponse response) {
            this.response = response;
        }

        @Override
        ProductionExceptionHandlerResponse getResponse() {
            return response;
        }
    }
}

