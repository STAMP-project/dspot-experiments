/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.influxdb;


import ExecuteInfluxDBQuery.INFLUX_DB_ERROR_MESSAGE;
import ExecuteInfluxDBQuery.INFLUX_DB_EXECUTED_QUERY;
import ExecuteInfluxDBQuery.INFLUX_DB_QUERY;
import ExecuteInfluxDBQuery.INFLUX_DB_QUERY_CHUNK_SIZE;
import ExecuteInfluxDBQuery.REL_FAILURE;
import ExecuteInfluxDBQuery.REL_SUCCESS;
import InfluxDB.ConsistencyLevel.ONE;
import PutInfluxDB.INFLUX_DB_URL;
import com.google.gson.Gson;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunners;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Series;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration test for executing InfluxDB queries. Please ensure that the InfluxDB is running
 * on local host with default port and has database test with table test. Please set user
 * and password if applicable before running the integration tests.
 */
public class ITExecuteInfluxDBQuery extends AbstractITInfluxDB {
    protected Gson gson = new Gson();

    @Test
    public void testValidScheduleQueryWithNoIncoming() {
        String message = "water,country=US,city=newark rain=1,humidity=0.6 1501002274856668652";
        influxDB.write(dbName, AbstractITInfluxDB.DEFAULT_RETENTION_POLICY, ONE, message);
        String query = "select * from water";
        runner.setProperty(INFLUX_DB_QUERY, query);
        runner.setIncomingConnection(false);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertNull("Value should be null", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        Assert.assertEquals("Value should be equal", query, flowFiles.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
        QueryResult queryResult = gson.fromJson(new StringReader(new String(flowFiles.get(0).toByteArray())), QueryResult.class);
        Series series = queryResult.getResults().get(0).getSeries().get(0);
        validateSeries(series.getName(), series.getColumns(), series.getValues().get(0), "newark", 1.0);
    }

    @Test
    public void testValidSinglePoint() {
        String message = "water,country=US,city=newark rain=1,humidity=0.6 1501002274856668652";
        influxDB.write(dbName, AbstractITInfluxDB.DEFAULT_RETENTION_POLICY, ONE, message);
        String query = "select * from water";
        byte[] bytes = query.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertNull("Value should be null", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        Assert.assertEquals("Value should be equal", query, flowFiles.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
        QueryResult queryResult = gson.fromJson(new StringReader(new String(flowFiles.get(0).toByteArray())), QueryResult.class);
        Series series = queryResult.getResults().get(0).getSeries().get(0);
        validateSeries(series.getName(), series.getColumns(), series.getValues().get(0), "newark", 1.0);
    }

    @Test
    public void testShowDatabases() {
        String query = "show databases";
        byte[] bytes = query.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertNull("Value should be null", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        Assert.assertEquals("Value should be equal", query, flowFiles.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
        String result = new String(flowFiles.get(0).toByteArray());
        QueryResult queryResult = gson.fromJson(new StringReader(result), QueryResult.class);
        Series series = queryResult.getResults().get(0).getSeries().get(0);
        Assert.assertEquals("series name should be same", "databases", series.getName());
        Assert.assertEquals("series column should be same", "name", series.getColumns().get(0));
        boolean internal = series.getValues().get(0).stream().anyMatch(( o) -> o.equals("_internal"));
        Assert.assertTrue(("content should contain _internal " + queryResult), internal);
        boolean test = series.getValues().stream().flatMap(( i) -> ((List<Object>) (i)).stream()).anyMatch(( o) -> o.equals("test"));
        Assert.assertTrue(("content should contain test " + queryResult), test);
    }

    @Test
    public void testCreateDB() {
        String query = "create database test1";
        byte[] bytes = query.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertNull("Value should be null", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        Assert.assertEquals("Value should be equal", query, flowFiles.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
        QueryResult queryResult = gson.fromJson(new StringReader(new String(flowFiles.get(0).toByteArray())), QueryResult.class);
        Assert.assertNotNull("QueryResult should not be null", queryResult.getResults());
        Assert.assertEquals("results array should be same size", 1, queryResult.getResults().size());
        Assert.assertNull("No series", queryResult.getResults().get(0).getSeries());
    }

    @Test
    public void testEmptyFlowFileQueryWithScheduledQuery() {
        String message = "water,country=US,city=newark rain=1,humidity=0.6 1501002274856668652";
        influxDB.write(dbName, AbstractITInfluxDB.DEFAULT_RETENTION_POLICY, ONE, message);
        String query = "select * from water";
        runner.setProperty(INFLUX_DB_QUERY, query);
        byte[] bytes = new byte[]{  };
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertEquals("Value should be equal", null, flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        Assert.assertEquals("Value should be equal", query, flowFiles.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
        QueryResult queryResult = gson.fromJson(new StringReader(new String(flowFiles.get(0).toByteArray())), QueryResult.class);
        Assert.assertNotNull("QueryResult should not be null", queryResult.getResults());
        Assert.assertEquals("results array should be same size", 1, queryResult.getResults().size());
        Series series = queryResult.getResults().get(0).getSeries().get(0);
        validateSeries(series.getName(), series.getColumns(), series.getValues().get(0), "newark", 1.0);
    }

    @Test
    public void testEmptyFlowFileQueryWithScheduledQueryEL() {
        String message = "water,country=US,city=newark rain=1,humidity=0.6 1501002274856668652";
        influxDB.write(dbName, AbstractITInfluxDB.DEFAULT_RETENTION_POLICY, ONE, message);
        String query = "select * from ${measurement}";
        runner.setProperty(INFLUX_DB_QUERY, query);
        byte[] bytes = new byte[]{  };
        Map<String, String> properties = new HashMap<>();
        properties.put("measurement", "water");
        runner.enqueue(bytes, properties);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertNull("Value should be null", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        Assert.assertEquals("Value should be equal", query.replace("${measurement}", "water"), flowFiles.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
        QueryResult queryResult = gson.fromJson(new StringReader(new String(flowFiles.get(0).toByteArray())), QueryResult.class);
        Assert.assertNotNull("QueryResult should not be null", queryResult.getResults());
        Assert.assertEquals("results array should be same size", 1, queryResult.getResults().size());
        Series series = queryResult.getResults().get(0).getSeries().get(0);
        validateSeries(series.getName(), series.getColumns(), series.getValues().get(0), "newark", 1.0);
    }

    @Test
    public void testEmptyFlowFileQuery() {
        String query = "";
        byte[] bytes = query.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFilesSuccess = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 0, flowFilesSuccess.size());
        List<MockFlowFile> flowFilesFailure = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals("Value should be equal", 1, flowFilesFailure.size());
        Assert.assertEquals("Value should be equal", "FlowFile query is empty and no scheduled query is set", flowFilesFailure.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        Assert.assertNull("Value should be null", flowFilesFailure.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
    }

    @Test
    public void testNoFlowFileNoScheduledInfluxDBQuery() {
        try {
            runner.setIncomingConnection(false);
            runner.run(1, true, true);
            Assert.fail("Should throw assertion error");
        } catch (AssertionError error) {
            Assert.assertEquals("Message should be same", "Could not invoke methods annotated with @OnScheduled annotation due to: java.lang.reflect.InvocationTargetException", error.getLocalizedMessage());
        }
    }

    @Test
    public void testValidTwoPoints() {
        String message = ("water,country=US,city=newark rain=1,humidity=0.6 1501002274856668652" + (System.lineSeparator())) + "water,country=US,city=nyc rain=2,humidity=0.6 1501002274856668652";
        influxDB.write(dbName, AbstractITInfluxDB.DEFAULT_RETENTION_POLICY, ONE, message);
        String query = "select * from water";
        byte[] bytes = query.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertNull("Value should be null", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        Assert.assertEquals("Value should be equal", query, flowFiles.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
        QueryResult queryResult = gson.fromJson(new StringReader(new String(flowFiles.get(0).toByteArray())), QueryResult.class);
        Assert.assertNotNull("QueryResult should not be null", queryResult.getResults());
        Assert.assertEquals("results array should be same size", 1, queryResult.getResults().size());
        Assert.assertEquals("Series size should be same", 1, queryResult.getResults().get(0).getSeries().size());
        Series series1 = queryResult.getResults().get(0).getSeries().get(0);
        validateSeries(series1.getName(), series1.getColumns(), series1.getValues().get(0), "newark", 1.0);
        Series series2 = queryResult.getResults().get(0).getSeries().get(0);
        validateSeries(series2.getName(), series2.getColumns(), series2.getValues().get(1), "nyc", 2.0);
    }

    @Test
    public void testMalformedQuery() {
        String query = "select * from";
        byte[] bytes = query.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFilesSuccess = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 0, flowFilesSuccess.size());
        List<MockFlowFile> flowFilesFailure = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals("Value should be equal", 1, flowFilesFailure.size());
        Assert.assertEquals("Value should be equal", "{\"error\":\"error parsing query: found EOF, expected identifier at line 1, char 15\"}", flowFilesFailure.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE).trim());
        Assert.assertEquals("Value should be equal", query, flowFilesFailure.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
    }

    @Test
    public void testQueryResultHasError() throws Throwable {
        ExecuteInfluxDBQuery mockExecuteInfluxDBQuery = new ExecuteInfluxDBQuery() {
            @Override
            protected List<QueryResult> executeQuery(ProcessContext context, String database, String query, TimeUnit timeunit, int chunkSize) throws InterruptedException {
                List<QueryResult> result = super.executeQuery(context, database, query, timeunit, chunkSize);
                result.get(0).setError("Test Error");
                return result;
            }
        };
        runner = TestRunners.newTestRunner(mockExecuteInfluxDBQuery);
        initializeRunner();
        byte[] bytes = "select * from /.*/".getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals("Test Error", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
    }

    @Test
    public void testValidSameTwoPoints() {
        String message = ("water,country=US,city=nyc rain=1,humidity=0.6 1501002274856668652" + (System.lineSeparator())) + "water,country=US,city=nyc rain=1,humidity=0.6 1501002274856668652";
        influxDB.write(dbName, AbstractITInfluxDB.DEFAULT_RETENTION_POLICY, ONE, message);
        String query = "select * from water";
        byte[] bytes = query.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertNull("Value should be null", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        Assert.assertEquals("Value should be equal", query, flowFiles.get(0).getAttribute(INFLUX_DB_EXECUTED_QUERY));
        QueryResult queryResult = gson.fromJson(new StringReader(new String(flowFiles.get(0).toByteArray())), QueryResult.class);
        Assert.assertNotNull("QueryResult should not be null", queryResult.getResults());
        Assert.assertEquals("Result size should be same", 1, queryResult.getResults().size());
        Series series = queryResult.getResults().get(0).getSeries().get(0);
        validateSeries(series.getName(), series.getColumns(), series.getValues().get(0), "nyc", 1.0);
    }

    @Test
    public void testValidTwoPointsUrlEL() {
        runner.setVariable("influxDBUrl", "http://localhost:8086");
        runner.setProperty(INFLUX_DB_URL, "${influxDBUrl}");
        testValidTwoPoints();
    }

    @Test
    public void testChunkedQuery() {
        String message = ((("chunkedQueryTest,country=GB value=1 1524938495000000000" + (System.lineSeparator())) + "chunkedQueryTest,country=PL value=2 1524938505000000000") + (System.lineSeparator())) + "chunkedQueryTest,country=US value=3 1524938505800000000";
        influxDB.write(dbName, AbstractITInfluxDB.DEFAULT_RETENTION_POLICY, ONE, message);
        String query = "select * from chunkedQueryTest";
        byte[] bytes = query.getBytes();
        runner.enqueue(bytes);
        runner.setProperty(INFLUX_DB_QUERY_CHUNK_SIZE, "2");
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertNull("Value should be null", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        List<QueryResult> queryResult = gson.fromJson(new StringReader(new String(flowFiles.get(0).toByteArray())), QueryResultListType);
        Assert.assertNotNull("QueryResult array should not be null", queryResult);
        Assert.assertEquals("QueryResult array size should be equal 2", 2, queryResult.size());
        Assert.assertEquals("First chunk should have 2 elements", 2, chunkSize(queryResult.get(0)));
        Assert.assertEquals("Second chunk should have 1 elements", 1, chunkSize(queryResult.get(1)));
    }
}

