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
package io.confluent.ksql.structured;


import MessageType.RECORD_PROCESSING_ERROR;
import ProcessingLogMessageSchema.PROCESSING_LOG_SCHEMA;
import ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR_FIELD_MESSAGE;
import ProcessingLogMessageSchema.TYPE;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import io.confluent.ksql.logging.processing.ProcessingLogger;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.MetaStoreFixture;
import java.util.Collections;
import java.util.function.Function;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.Struct;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class SelectValueMapperTest {
    private final MetaStore metaStore = MetaStoreFixture.getNewMetaStore(new InternalFunctionRegistry());

    private final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());

    @Mock
    private ProcessingLogger processingLogger;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void shouldSelectChosenColumns() {
        // Given:
        final SelectValueMapper selectMapper = givenSelectMapperFor("SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;");
        // When:
        final GenericRow transformed = selectMapper.apply(SelectValueMapperTest.genericRow(1521834663L, "key1", 1L, "hi", "bye", 2.0F, "blah"));
        // Then:
        Assert.assertThat(transformed, Matchers.is(SelectValueMapperTest.genericRow(1L, "bye", 2.0F)));
    }

    @Test
    public void shouldApplyUdfsToColumns() {
        // Given:
        final SelectValueMapper selectMapper = givenSelectMapperFor("SELECT col0, col1, col2, CEIL(col3) FROM test1 WHERE col0 > 100;");
        // When:
        final GenericRow row = selectMapper.apply(SelectValueMapperTest.genericRow(1521834663L, "key1", 2L, "foo", "whatever", 6.9F, "boo", "hoo"));
        // Then:
        Assert.assertThat(row, Matchers.is(SelectValueMapperTest.genericRow(2L, "foo", "whatever", 7.0F)));
    }

    @Test
    public void shouldHandleNullRows() {
        // Given:
        final SelectValueMapper selectMapper = givenSelectMapperFor("SELECT col0, col1, col2, CEIL(col3) FROM test1 WHERE col0 > 100;");
        // When:
        final GenericRow row = selectMapper.apply(null);
        // Then:
        Assert.assertThat(row, Matchers.is(Matchers.nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldWriteProcessingLogOnError() {
        // Given:
        final SelectValueMapper selectMapper = givenSelectMapperFor("SELECT col0, col1, col2, CEIL(col3) FROM test1 WHERE col0 > 100;");
        // When:
        selectMapper.apply(new GenericRow(0L, "key", 2L, "foo", "whatever", null, "boo", "hoo"));
        // Then:
        final ArgumentCaptor<Function<ProcessingLogConfig, SchemaAndValue>> captor = ArgumentCaptor.forClass(Function.class);
        Mockito.verify(processingLogger).error(captor.capture());
        final SchemaAndValue schemaAndValue = captor.getValue().apply(new ProcessingLogConfig(Collections.emptyMap()));
        Assert.assertThat(schemaAndValue.schema(), Matchers.equalTo(PROCESSING_LOG_SCHEMA));
        final Struct struct = ((Struct) (schemaAndValue.value()));
        Assert.assertThat(struct.get(TYPE), Matchers.equalTo(RECORD_PROCESSING_ERROR.ordinal()));
        final Struct errorStruct = struct.getStruct(ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR);
        Assert.assertThat(errorStruct.get(RECORD_PROCESSING_ERROR_FIELD_MESSAGE), Matchers.equalTo(("Error computing expression CEIL(TEST1.COL3) " + "for column KSQL_COL_3 with index 3: null")));
    }
}

