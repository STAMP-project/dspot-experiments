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
package org.kaaproject.kaa.server.appenders.cassandra.appender;


import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.kaaproject.kaa.common.avro.AvroByteArrayConverter;
import org.kaaproject.kaa.common.dto.EndpointProfileDataDto;
import org.kaaproject.kaa.common.dto.logs.LogAppenderDto;
import org.kaaproject.kaa.server.appenders.cassandra.appender.gen.LogData;
import org.kaaproject.kaa.server.appenders.cassandra.config.gen.CassandraConfig;
import org.kaaproject.kaa.server.common.CustomCassandraCQLUnit;
import org.kaaproject.kaa.server.common.log.shared.appender.LogAppender;
import org.kaaproject.kaa.server.common.log.shared.appender.LogDeliveryCallback;
import org.kaaproject.kaa.server.common.log.shared.avro.gen.RecordHeader;
import org.springframework.test.util.ReflectionTestUtils;


public class CassandraLogAppenderTest {
    public static final String KEY_SPACE_NAME = "kaa_test";

    private static final String SERVER_PROFILE_SCHEMA_FILE = "server_profile_schema.avsc";

    private static final String SERVER_PROFILE_CONTENT_FILE = "server_profile_content.json";

    private static final Random RANDOM = new Random();

    @ClassRule
    public static CustomCassandraCQLUnit cassandraUnit = new CustomCassandraCQLUnit(new ClassPathCQLDataSet("appender_test.cql", false, false), "cassandra-test.yaml", (4 * 60000L));

    private LogAppender logAppender;

    private LogAppenderDto appenderDto;

    private CassandraConfig configuration;

    private RecordHeader header;

    private String appToken;

    private String endpointKeyHash;

    private EndpointProfileDataDto profileDto;

    private AvroByteArrayConverter<LogData> logDataConverter = new AvroByteArrayConverter(LogData.class);

    @Test
    public void doAppendTest() throws IOException, InterruptedException {
        CassandraLogAppenderTest.DeliveryCallback callback = new CassandraLogAppenderTest.DeliveryCallback();
        logAppender.doAppend(generateLogEventPack(20, true), callback);
        Thread.sleep(3000);
        CassandraLogEventDao logEventDao = ((CassandraLogEventDao) (ReflectionTestUtils.getField(logAppender, "logEventDao")));
        Session session = ((Session) (ReflectionTestUtils.getField(logEventDao, "session")));
        ResultSet resultSet = session.execute(QueryBuilder.select().countAll().from(CassandraLogAppenderTest.KEY_SPACE_NAME, ((("logs_" + (appToken)) + "_") + (Math.abs(configuration.hashCode())))));
        Row row = resultSet.one();
        Assert.assertEquals(20L, row.getLong(0));
        Assert.assertEquals(1, callback.getSuccessCount());
    }

    /**
     * Tests that log records cannot be appended if they lack a server field
     * mapped by the appender.
     */
    @Test
    public void doAppendNegativeTest() throws Exception {
        this.initLogAppender(true);
        this.logAppender.doAppend(this.generateLogEventPack(5, false), new CassandraLogAppenderTest.DeliveryCallback());
        /* Check that a server error has occured because no server profile is
        specified for this log pack.
         */
        Thread.sleep(3000);
        CassandraLogEventDao logEventDao = ((CassandraLogEventDao) (ReflectionTestUtils.getField(this.logAppender, "logEventDao")));
        Session session = ((Session) (ReflectionTestUtils.getField(logEventDao, "session")));
        String table = (("logs_" + (this.appToken)) + "_") + (Math.abs(this.configuration.hashCode()));
        /* Nothing has been saved because of the error. */
        Row count = session.execute(QueryBuilder.select().countAll().from(CassandraLogAppenderTest.KEY_SPACE_NAME, table)).one();
        Assert.assertEquals(0L, count.getLong(0));
    }

    class DeliveryCallback implements LogDeliveryCallback {
        private AtomicInteger successCount = new AtomicInteger();

        @Override
        public void onSuccess() {
            successCount.incrementAndGet();
        }

        @Override
        public void onInternalError() {
        }

        @Override
        public void onConnectionError() {
        }

        @Override
        public void onRemoteError() {
        }

        public int getSuccessCount() {
            return successCount.get();
        }
    }
}

