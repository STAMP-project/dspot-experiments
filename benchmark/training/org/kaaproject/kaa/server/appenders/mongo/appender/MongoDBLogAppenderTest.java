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
package org.kaaproject.kaa.server.appenders.mongo.appender;


import BasicEndpointProfile.SCHEMA;
import com.mongodb.DBObject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import org.kaaproject.kaa.server.common.log.shared.appender.data.BaseSchemaInfo;
import org.kaaproject.kaa.server.common.log.shared.appender.data.ProfileInfo;
import org.kaaproject.kaa.server.common.nosql.mongo.dao.MongoDBTestRunner;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;


public class MongoDBLogAppenderTest {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDBLogAppenderTest.class);

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String APPLICATION_ID = "application_id";

    private static final String APPLICATION_TOKEN = "application_token";

    private static final String TENANT_ID = "tenant_id";

    private static final String NEW_APPENDER_NAME = "new name";

    private static final String ENDPOINT_KEY = "endpoint key";

    private static final String EMPTY_SCHEMA = "{" + (((("\"type\": \"record\"," + "\"name\": \"Log\",") + "\"namespace\": \"org.kaaproject.kaa.schema.base\",") + "\"fields\": []") + "}");

    private static final String LOG_DATA = "null";

    private static final long DATE_CREATED = System.currentTimeMillis();

    private static final String SERVER_PROFILE_SCHEMA_FILE = "server_profile_schema.avsc";

    private static final String SERVER_PROFILE_CONTENT_FILE = "server_profile_content.json";

    // According to the server profile schema file
    private static final String SERVER_FIELD_KEY = "country??";

    // According to the server profile content file
    private static final String SERVER_FIELD_VALUE = "1.0.$.";

    private static final String SERVER_PROFILE = "serverProfile";

    private LogAppender logAppender;

    @Test
    public void changeAppenderNameTest() {
        String oldName = logAppender.getName();
        logAppender.setName(MongoDBLogAppenderTest.NEW_APPENDER_NAME);
        Assert.assertNotEquals(oldName, logAppender.getName());
        Assert.assertEquals(MongoDBLogAppenderTest.NEW_APPENDER_NAME, logAppender.getName());
    }

    @Test
    public void closeAppenderTest() {
        Assert.assertFalse(((boolean) (ReflectionTestUtils.getField(logAppender, "closed"))));
        logAppender.close();
        Assert.assertTrue(((boolean) (ReflectionTestUtils.getField(logAppender, "closed"))));
    }

    @Test
    public void getConverterTest() {
        String schema = MongoDBLogAppenderTest.EMPTY_SCHEMA;
        Assert.assertTrue(((Map) (ReflectionTestUtils.getField(logAppender, "converters"))).isEmpty());
        GenericAvroConverter<GenericRecord> converter1 = ReflectionTestUtils.invokeMethod(logAppender, "getConverter", schema);
        Assert.assertEquals(1, ((Map) (ReflectionTestUtils.getField(logAppender, "converters"))).size());
        GenericAvroConverter<GenericRecord> converter2 = ReflectionTestUtils.invokeMethod(logAppender, "getConverter", schema);
        Assert.assertEquals(1, ((Map) (ReflectionTestUtils.getField(logAppender, "converters"))).size());
        Assert.assertEquals(converter1, converter2);
    }

    // Not throws NullPointerException
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
        MongoDBLogAppenderTest.TestLogDeliveryCallback callback = new MongoDBLogAppenderTest.TestLogDeliveryCallback();
        EndpointProfileDataDto profileDto = new EndpointProfileDataDto("1", MongoDBLogAppenderTest.ENDPOINT_KEY, 1, "", 0, null);
        BaseLogEventPack logEventPack = new BaseLogEventPack(profileDto, MongoDBLogAppenderTest.DATE_CREATED, 1, null);
        LogSchemaDto schemaDto = new LogSchemaDto();
        schemaDto.setVersion(1);
        LogSchema schema = new LogSchema(schemaDto, SCHEMA..toString());
        logEventPack.setLogSchema(schema);
        logAppender.doAppend(logEventPack, callback);
        Assert.assertTrue(callback.internallError);
    }

    @Test
    public void doAppendWithCatchIOExceptionTest() throws IOException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
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
        EndpointProfileDataDto profileDto = new EndpointProfileDataDto("1", MongoDBLogAppenderTest.ENDPOINT_KEY, 1, "", 0, null);
        BaseLogEventPack logEventPack = new BaseLogEventPack(profileDto, MongoDBLogAppenderTest.DATE_CREATED, schema.getVersion(), events);
        logEventPack.setLogSchema(schema);
        LogEventDao logEventDao = Mockito.mock(LogEventDao.class);
        LogEventDao eventDao = ((LogEventDao) (ReflectionTestUtils.getField(logAppender, "logEventDao")));
        ReflectionTestUtils.setField(logAppender, "logEventDao", logEventDao);
        MongoDBLogAppenderTest.TestLogDeliveryCallback callback = new MongoDBLogAppenderTest.TestLogDeliveryCallback();
        logAppender.doAppend(logEventPack, callback);
        Assert.assertTrue(callback.internallError);
        Mockito.verify(logEventDao, Mockito.never()).save(Mockito.anyList(), Mockito.<ProfileInfo>anyObject(), Mockito.<ProfileInfo>anyObject(), Mockito.anyString());
        ReflectionTestUtils.setField(logAppender, "logEventDao", eventDao);
    }

    @Test
    public void doAppendWithoutServerProfileTest() throws IOException {
        GenericAvroConverter<BasicEndpointProfile> converter = new GenericAvroConverter<BasicEndpointProfile>(BasicEndpointProfile.SCHEMA$);
        BasicEndpointProfile theLog = new BasicEndpointProfile("test");
        List<LogEvent> events = new ArrayList<>();
        LogEvent event1 = new LogEvent();
        event1.setLogData(converter.encode(theLog));
        LogEvent event2 = new LogEvent();
        event2.setLogData(converter.encode(theLog));
        LogEvent event3 = new LogEvent();
        event3.setLogData(converter.encode(theLog));
        events.add(event1);
        events.add(event2);
        events.add(event3);
        LogSchemaDto schemaDto = new LogSchemaDto();
        LogSchema schema = new LogSchema(schemaDto, SCHEMA..toString());
        EndpointProfileDataDto profileDto = new EndpointProfileDataDto("1", MongoDBLogAppenderTest.ENDPOINT_KEY, 1, "", 0, null);
        BaseLogEventPack logEventPack = new BaseLogEventPack(profileDto, MongoDBLogAppenderTest.DATE_CREATED, schema.getVersion(), events);
        logEventPack.setLogSchema(schema);
        String collectionName = ((String) (ReflectionTestUtils.getField(logAppender, "collectionName")));
        Assert.assertEquals(0, MongoDBTestRunner.getDB().getCollection(collectionName).count());
        MongoDBLogAppenderTest.TestLogDeliveryCallback callback = new MongoDBLogAppenderTest.TestLogDeliveryCallback();
        logAppender.doAppend(logEventPack, callback);
        Assert.assertTrue(callback.success);
        collectionName = ((String) (ReflectionTestUtils.getField(logAppender, "collectionName")));
        Assert.assertEquals(3, MongoDBTestRunner.getDB().getCollection(collectionName).count());
    }

    @Test
    public void doAppendWithServerProfileTest() throws Exception {
        // Reinitilize the log appender to include server profile data
        this.initLogAppender(false, true);
        GenericAvroConverter<BasicEndpointProfile> converter = new GenericAvroConverter<BasicEndpointProfile>(BasicEndpointProfile.SCHEMA$);
        BasicEndpointProfile log = new BasicEndpointProfile("body");
        List<LogEvent> logEvents = new ArrayList<>();
        LogEvent alpha = new LogEvent();
        alpha.setLogData(converter.encode(log));
        logEvents.add(alpha);
        LogEvent beta = new LogEvent();
        beta.setLogData(converter.encode(log));
        logEvents.add(alpha);
        LogEvent gamma = new LogEvent();
        gamma.setLogData(converter.encode(log));
        logEvents.add(alpha);
        LogSchemaDto logSchemaDto = new LogSchemaDto();
        LogSchema logSchema = new LogSchema(logSchemaDto, SCHEMA..toString());
        EndpointProfileDataDto profileDto = new EndpointProfileDataDto("1", MongoDBLogAppenderTest.ENDPOINT_KEY, 1, "", 0, null);
        BaseLogEventPack logEventPack = new BaseLogEventPack(profileDto, MongoDBLogAppenderTest.DATE_CREATED, logSchema.getVersion(), logEvents);
        logEventPack.setLogSchema(logSchema);
        // Add server profile data
        BaseSchemaInfo schemaInfo = new BaseSchemaInfo(Integer.toString(new Random().nextInt()), this.getResourceAsString(MongoDBLogAppenderTest.SERVER_PROFILE_SCHEMA_FILE));
        String body = this.getResourceAsString(MongoDBLogAppenderTest.SERVER_PROFILE_CONTENT_FILE);
        logEventPack.setServerProfile(new org.kaaproject.kaa.server.common.log.shared.appender.data.BaseProfileInfo(schemaInfo, body));
        this.logAppender.doAppend(logEventPack, new MongoDBLogAppenderTest.TestLogDeliveryCallback());
        String collectionName = ((String) (ReflectionTestUtils.getField(this.logAppender, "collectionName")));
        DBObject serverProfile = ((DBObject) (MongoDBTestRunner.getDB().getCollection(collectionName).findOne().get(MongoDBLogAppenderTest.SERVER_PROFILE)));
        DBObject profile = ((DBObject) (serverProfile.get("Profile")));
        DBObject profileNamespace = ((DBObject) (profile.get("org?kaaproject?kaa?schema?sample?profile")));
        Assert.assertEquals(MongoDBLogAppenderTest.SERVER_FIELD_VALUE, profileNamespace.get(MongoDBLogAppenderTest.SERVER_FIELD_KEY));
    }

    @Test
    public void doAppendWithEmptyServerProfileTest() throws Exception {
        // Reinitilize the log appender to include server profile data
        this.initLogAppender(false, true);
        GenericAvroConverter<BasicEndpointProfile> converter = new GenericAvroConverter<BasicEndpointProfile>(BasicEndpointProfile.SCHEMA$);
        BasicEndpointProfile log = new BasicEndpointProfile("body");
        List<LogEvent> logEvents = new ArrayList<>();
        LogEvent alpha = new LogEvent();
        alpha.setLogData(converter.encode(log));
        logEvents.add(alpha);
        LogEvent beta = new LogEvent();
        beta.setLogData(converter.encode(log));
        logEvents.add(alpha);
        LogEvent gamma = new LogEvent();
        gamma.setLogData(converter.encode(log));
        logEvents.add(alpha);
        LogSchemaDto logSchemaDto = new LogSchemaDto();
        LogSchema logSchema = new LogSchema(logSchemaDto, SCHEMA..toString());
        EndpointProfileDataDto profileDto = new EndpointProfileDataDto("1", MongoDBLogAppenderTest.ENDPOINT_KEY, 1, "", 0, null);
        BaseLogEventPack logEventPack = new BaseLogEventPack(profileDto, MongoDBLogAppenderTest.DATE_CREATED, logSchema.getVersion(), logEvents);
        logEventPack.setLogSchema(logSchema);
        this.logAppender.doAppend(logEventPack, new MongoDBLogAppenderTest.TestLogDeliveryCallback());
        String collectionName = ((String) (ReflectionTestUtils.getField(this.logAppender, "collectionName")));
        DBObject serverProfile = ((DBObject) (MongoDBTestRunner.getDB().getCollection(collectionName).findOne().get(MongoDBLogAppenderTest.SERVER_PROFILE)));
        Assert.assertEquals(null, serverProfile);
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

