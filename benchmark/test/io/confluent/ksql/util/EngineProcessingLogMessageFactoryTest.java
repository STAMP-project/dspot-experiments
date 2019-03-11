package io.confluent.ksql.util;


import MessageType.RECORD_PROCESSING_ERROR;
import ProcessingLogConfig.INCLUDE_ROWS;
import ProcessingLogMessageSchema.PROCESSING_LOG_SCHEMA;
import ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR_FIELD_MESSAGE;
import ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR_FIELD_RECORD;
import ProcessingLogMessageSchema.TYPE;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.Struct;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EngineProcessingLogMessageFactoryTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String errorMsg = "error msg";

    private final ProcessingLogConfig config = new ProcessingLogConfig(Collections.singletonMap(INCLUDE_ROWS, true));

    @Test
    @SuppressWarnings("unchecked")
    public void shouldBuildRecordProcessingErrorCorrectly() throws IOException {
        // When:
        final SchemaAndValue msgAndSchema = EngineProcessingLogMessageFactory.recordProcessingError(EngineProcessingLogMessageFactoryTest.errorMsg, new GenericRow(123, "data")).apply(config);
        // Then:
        Assert.assertThat(msgAndSchema.schema(), Matchers.equalTo(PROCESSING_LOG_SCHEMA));
        final Struct msg = ((Struct) (msgAndSchema.value()));
        Assert.assertThat(msg.get(TYPE), Matchers.equalTo(RECORD_PROCESSING_ERROR.getTypeId()));
        Assert.assertThat(msg.get(ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR), Matchers.notNullValue());
        final Struct recordProcessingError = msg.getStruct(ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR);
        Assert.assertThat(recordProcessingError.get(RECORD_PROCESSING_ERROR_FIELD_MESSAGE), Matchers.equalTo(EngineProcessingLogMessageFactoryTest.errorMsg));
        final List<Object> rowAsList = EngineProcessingLogMessageFactoryTest.OBJECT_MAPPER.readValue(recordProcessingError.getString(RECORD_PROCESSING_ERROR_FIELD_RECORD), List.class);
        Assert.assertThat(rowAsList, Matchers.contains(123, "data"));
    }

    @Test
    public void shouldBuildRecordProcessingErrorCorrectlyIfRowNull() {
        // When:
        final SchemaAndValue msgAndSchema = EngineProcessingLogMessageFactory.recordProcessingError(EngineProcessingLogMessageFactoryTest.errorMsg, null).apply(config);
        // Then:
        final Struct msg = ((Struct) (msgAndSchema.value()));
        final Struct recordProcessingError = msg.getStruct(ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR);
        Assert.assertThat(recordProcessingError.get(RECORD_PROCESSING_ERROR_FIELD_RECORD), Matchers.nullValue());
    }

    @Test
    public void shouldBuildRecordProcessingErrorWithNullRowIfIncludeRowsFalse() {
        // Given:
        final ProcessingLogConfig config = new ProcessingLogConfig(Collections.singletonMap(INCLUDE_ROWS, false));
        // When:
        final SchemaAndValue msgAndSchema = EngineProcessingLogMessageFactory.recordProcessingError(EngineProcessingLogMessageFactoryTest.errorMsg, new GenericRow(123, "data")).apply(config);
        // Then:
        final Struct msg = ((Struct) (msgAndSchema.value()));
        final Struct recordProcessingError = msg.getStruct(ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR);
        Assert.assertThat(recordProcessingError.get(RECORD_PROCESSING_ERROR_FIELD_RECORD), Matchers.nullValue());
    }
}

