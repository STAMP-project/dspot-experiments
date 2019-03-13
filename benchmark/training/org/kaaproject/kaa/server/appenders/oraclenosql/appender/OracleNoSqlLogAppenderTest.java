/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.appenders.oraclenosql.appender;


import BasicEndpointProfile.SCHEMA;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oracle.kv.DurabilityException;
import oracle.kv.FaultException;
import oracle.kv.OperationExecutionException;
import oracle.kv.util.kvlite.KVLite;
import org.apache.avro.generic.GenericRecord;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.avro.GenericAvroConverter;
import org.kaaproject.kaa.common.dto.EndpointProfileDataDto;
import org.kaaproject.kaa.common.dto.logs.LogSchemaDto;
import org.kaaproject.kaa.common.endpoint.gen.BasicEndpointProfile;
import org.kaaproject.kaa.server.common.log.shared.appender.LogAppender;
import org.kaaproject.kaa.server.common.log.shared.appender.LogDeliveryCallback;
import org.kaaproject.kaa.server.common.log.shared.appender.LogEvent;
import org.kaaproject.kaa.server.common.log.shared.appender.LogSchema;
import org.kaaproject.kaa.server.common.log.shared.appender.data.BaseLogEventPack;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;


public class OracleNoSqlLogAppenderTest {
    private static final Logger LOG = LoggerFactory.getLogger(OracleNoSqlLogAppenderTest.class);

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String APPLICATION_ID = "application_id";

    private static final String APPLICATION_TOKEN = "application_token";

    private static final String TENANT_ID = "tenant_id";

    private static final String ENDPOINT_KEY = "endpoint key";

    private static final String EMPTY_SCHEMA = "{" + (((("\"type\": \"record\"," + "\"name\": \"Log\",") + "\"namespace\": \"org.kaaproject.kaa.schema.base\",") + "\"fields\": []") + "}");

    private static final String LOG_DATA = "null";

    private static final long DATE_CREATED = System.currentTimeMillis();

    private static final String STORE_NAME = "kvstore";

    private static final String STORE_HOST = "127.0.0.1";

    private static final int STORE_PORT = 10555;

    private static KVLite kvLite;

    private static File storeRootDir;

    private LogAppender logAppender;

    @Test
    public void closeAppenderTest() {
        Assert.assertFalse(((boolean) (ReflectionTestUtils.getField(logAppender, "closed"))));
        logAppender.close();
        Assert.assertTrue(((boolean) (ReflectionTestUtils.getField(logAppender, "closed"))));
    }

    @Test
    public void doAppendClosedTest() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Logger testLogger = Mockito.mock(Logger.class);
        Field field = logAppender.getClass().getDeclaredField("LOG");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, ((field.getModifiers()) & (~(Modifier.FINAL))));
        field.set(null, testLogger);
        logAppender.close();
        OracleNoSqlLogAppenderTest.TestLogDeliveryCallback callback = new OracleNoSqlLogAppenderTest.TestLogDeliveryCallback();
        LogSchemaDto schemaDto = new LogSchemaDto();
        LogSchema schema = new LogSchema(schemaDto, SCHEMA..toString());
        EndpointProfileDataDto profileDto = new EndpointProfileDataDto("1", OracleNoSqlLogAppenderTest.ENDPOINT_KEY, 1, "test", 0, null);
        BaseLogEventPack logEventPack = new BaseLogEventPack(profileDto, OracleNoSqlLogAppenderTest.DATE_CREATED, schema.getVersion(), null);
        logEventPack.setLogSchema(schema);
        logAppender.doAppend(logEventPack, callback);
        Assert.assertTrue(callback.internallError);
    }

    @Test
    public void doAppendWithCatchIOExceptionTest() throws IOException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException, DurabilityException, FaultException, OperationExecutionException {
        GenericAvroConverter<BasicEndpointProfile> converter = new GenericAvroConverter<BasicEndpointProfile>(BasicEndpointProfile.SCHEMA$);
        BasicEndpointProfile theLog = new BasicEndpointProfile("test");
        List<LogEvent> events = new ArrayList<>();
        LogEvent event1 = new LogEvent();
        event1.setLogData(new byte[0]);
        LogEvent event2 = new LogEvent();
        event2.setLogData(converter.encode(theLog));
        LogEvent event3 = new LogEvent();
        event3.setLogData(converter.encode(theLog));
        events.add(event1);
        events.add(event2);
        events.add(event3);
        LogSchemaDto schemaDto = new LogSchemaDto();
        LogSchema schema = new LogSchema(schemaDto, SCHEMA..toString());
        EndpointProfileDataDto profileDto = new EndpointProfileDataDto("1", OracleNoSqlLogAppenderTest.ENDPOINT_KEY, 1, "test", 0, null);
        BaseLogEventPack logEventPack = new BaseLogEventPack(profileDto, OracleNoSqlLogAppenderTest.DATE_CREATED, schema.getVersion(), events);
        logEventPack.setLogSchema(schema);
        OracleNoSqlLogAppenderTest.TestLogDeliveryCallback callback = new OracleNoSqlLogAppenderTest.TestLogDeliveryCallback();
        logAppender.doAppend(logEventPack, callback);
        Assert.assertTrue(callback.internallError);
    }

    @Test
    public void doAppendTest() throws Exception {
        List<LogEvent> events = new ArrayList<>();
        LogEvent event1 = new LogEvent();
        event1.setLogData(OracleNoSqlLogAppenderTest.LOG_DATA.getBytes(OracleNoSqlLogAppenderTest.UTF_8));
        LogEvent event2 = new LogEvent();
        event2.setLogData(OracleNoSqlLogAppenderTest.LOG_DATA.getBytes(OracleNoSqlLogAppenderTest.UTF_8));
        LogEvent event3 = new LogEvent();
        event3.setLogData(OracleNoSqlLogAppenderTest.LOG_DATA.getBytes(OracleNoSqlLogAppenderTest.UTF_8));
        events.add(event1);
        events.add(event2);
        events.add(event3);
        LogSchemaDto dto = new LogSchemaDto();
        dto.setVersion(1);
        LogSchema schema = new LogSchema(dto, OracleNoSqlLogAppenderTest.EMPTY_SCHEMA);
        int version = dto.getVersion();
        EndpointProfileDataDto profileDto = new EndpointProfileDataDto("1", OracleNoSqlLogAppenderTest.ENDPOINT_KEY, 1, "test", 0, null);
        BaseLogEventPack logEventPack = new BaseLogEventPack(profileDto, OracleNoSqlLogAppenderTest.DATE_CREATED, version, events);
        logEventPack.setLogSchema(schema);
        Map<String, GenericAvroConverter<GenericRecord>> converters = new HashMap<>();
        GenericAvroConverter<GenericRecord> converter = new GenericAvroConverter<GenericRecord>(schema.getSchema()) {
            @Override
            public GenericRecord decodeBinary(byte[] bytes) {
                return null;
            }

            @Override
            public String encodeToJson(GenericRecord record) {
                return OracleNoSqlLogAppenderTest.LOG_DATA;
            }
        };
        converters.put(schema.getSchema(), converter);
        ReflectionTestUtils.setField(logAppender, "converters", converters);
        Assert.assertEquals(0, getKeyValuesCount());
        OracleNoSqlLogAppenderTest.TestLogDeliveryCallback callback = new OracleNoSqlLogAppenderTest.TestLogDeliveryCallback();
        logAppender.doAppend(logEventPack, callback);
        Assert.assertTrue(callback.success);
        Assert.assertEquals(3, getKeyValuesCount());
    }

    private static class TestLogDeliveryCallback implements LogDeliveryCallback {
        private volatile boolean success;

        private volatile boolean internallError;

        private volatile boolean connectionError;

        private volatile boolean remoteError;

        @Override
        public void onSuccess() {
            success = true;
        }

        @Override
        public void onInternalError() {
            internallError = true;
        }

        @Override
        public void onConnectionError() {
            connectionError = true;
        }

        @Override
        public void onRemoteError() {
            remoteError = true;
        }
    }
}

