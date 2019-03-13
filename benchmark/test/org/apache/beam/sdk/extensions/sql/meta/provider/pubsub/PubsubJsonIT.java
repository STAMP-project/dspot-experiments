/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.extensions.sql.meta.provider.pubsub;


import Schema.FieldType.INT32;
import Schema.FieldType.STRING;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.extensions.sql.impl.BeamSqlEnv;
import org.apache.beam.sdk.extensions.sql.impl.JdbcDriver;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.io.gcp.pubsub.TestPubsub;
import org.apache.beam.sdk.io.gcp.pubsub.TestPubsubSignal;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.SchemaCoder;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.SerializableFunctions;
import org.apache.beam.sdk.util.common.ReflectHelpers;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Supplier;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.calcite.jdbc.CalciteConnection;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration tests for querying Pubsub JSON messages with SQL.
 */
@RunWith(JUnit4.class)
public class PubsubJsonIT implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PubsubJsonIT.class);

    private static final Schema PAYLOAD_SCHEMA = Schema.builder().addNullableField("id", INT32).addNullableField("name", STRING).build();

    private static final String CONNECT_STRING_PREFIX = "jdbc:beam:";

    private static final String BEAM_CALCITE_SCHEMA = "beamCalciteSchema";

    private static final JdbcDriver INSTANCE = new JdbcDriver();

    private static volatile Boolean checked = false;

    @Rule
    public transient TestPubsub eventsTopic = TestPubsub.create();

    @Rule
    public transient TestPubsub dlqTopic = TestPubsub.create();

    @Rule
    public transient TestPubsubSignal resultSignal = TestPubsubSignal.create();

    @Rule
    public transient TestPubsubSignal dlqSignal = TestPubsubSignal.create();

    @Rule
    public transient TestPipeline pipeline = TestPipeline.create();

    /**
     * HACK: we need an objectmapper to turn pipelineoptions back into a map. We need to use
     * ReflectHelpers to get the extra PipelineOptions.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModules(ObjectMapper.findModules(ReflectHelpers.findClassLoader()));

    @Test
    public void testSelectsPayloadContent() throws Exception {
        String createTableString = ((("CREATE EXTERNAL TABLE message (\n" + (((((((("event_timestamp TIMESTAMP, \n" + "attributes MAP<VARCHAR, VARCHAR>, \n") + "payload ROW< \n") + "             id INTEGER, \n") + "             name VARCHAR \n") + "           > \n") + ") \n") + "TYPE \'pubsub\' \n") + "LOCATION '")) + (eventsTopic.topicPath())) + "\' \n") + "TBLPROPERTIES \'{ \"timestampAttributeKey\" : \"ts\" }\'";
        String queryString = "SELECT message.payload.id, message.payload.name from message";
        // Prepare messages to send later
        List<PubsubMessage> messages = ImmutableList.of(message(ts(1), 3, "foo"), message(ts(2), 5, "bar"), message(ts(3), 7, "baz"));
        // Initialize SQL environment and create the pubsub table
        BeamSqlEnv sqlEnv = BeamSqlEnv.inMemory(new PubsubJsonTableProvider());
        sqlEnv.executeDdl(createTableString);
        // Apply the PTransform to query the pubsub topic
        PCollection<Row> queryOutput = query(sqlEnv, pipeline, queryString);
        // Observe the query results and send success signal after seeing the expected messages
        queryOutput.apply("waitForSuccess", resultSignal.signalSuccessWhen(SchemaCoder.of(PubsubJsonIT.PAYLOAD_SCHEMA, SerializableFunctions.identity(), SerializableFunctions.identity()), ( observedRows) -> observedRows.equals(ImmutableSet.of(row(PAYLOAD_SCHEMA, 3, "foo"), row(PAYLOAD_SCHEMA, 5, "bar"), row(PAYLOAD_SCHEMA, 7, "baz")))));
        // Send the start signal to make sure the signaling topic is initialized
        Supplier<Void> start = resultSignal.waitForStart(Duration.standardMinutes(5));
        pipeline.begin().apply(resultSignal.signalStart());
        // Start the pipeline
        pipeline.run();
        // Wait until got the start response from the signalling topic
        start.get();
        // Start publishing the messages when main pipeline is started and signaling topic is ready
        eventsTopic.publish(messages);
        // Poll the signaling topic for success message
        resultSignal.waitForSuccess(Duration.standardSeconds(60));
    }

    @Test
    public void testSQLLimit() throws Exception {
        String createTableString = ((((((((("CREATE EXTERNAL TABLE message (\n" + (((((((("event_timestamp TIMESTAMP, \n" + "attributes MAP<VARCHAR, VARCHAR>, \n") + "payload ROW< \n") + "             id INTEGER, \n") + "             name VARCHAR \n") + "           > \n") + ") \n") + "TYPE \'pubsub\' \n") + "LOCATION '")) + (eventsTopic.topicPath())) + "\' \n") + "TBLPROPERTIES ") + "    '{ ") + "       \"timestampAttributeKey\" : \"ts\", ") + "       \"deadLetterQueue\" : \"") + (dlqTopic.topicPath())) + "\"") + "     }'";
        List<PubsubMessage> messages = ImmutableList.of(message(ts(1), 3, "foo"), message(ts(2), 5, "bar"), message(ts(3), 7, "baz"), message(ts(4), 9, "ba2"), message(ts(5), 10, "ba3"), message(ts(6), 13, "ba4"), message(ts(7), 15, "ba5"));
        // We need the default options on the schema to include the project passed in for the
        // integration test
        CalciteConnection connection = connect(pipeline.getOptions(), new PubsubJsonTableProvider());
        Statement statement = connection.createStatement();
        statement.execute(createTableString);
        // Because Pubsub only allow new subscription receives message after the subscription is
        // created, eventsTopic.publish(messages) can only be called after statement.executeQuery.
        // However, because statement.executeQuery is a blocking call, it has to be put into a
        // seperate thread to execute.
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future<List<String>> queryResult = pool.submit(((Callable) (() -> {
            ResultSet resultSet = statement.executeQuery("SELECT message.payload.id FROM message LIMIT 3");
            ImmutableList.Builder<String> result = ImmutableList.builder();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            } 
            return result.build();
        })));
        eventsTopic.checkIfAnySubscriptionExists(pipeline.getOptions().as(GcpOptions.class).getProject(), Duration.standardMinutes(1));
        eventsTopic.publish(messages);
        Assert.assertThat(queryResult.get(2, TimeUnit.MINUTES).size(), Matchers.equalTo(3));
        pool.shutdown();
    }
}

