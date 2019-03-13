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


import PutInfluxDB.INFLUX_DB_ERROR_MESSAGE;
import PutInfluxDB.INFLUX_DB_URL;
import PutInfluxDB.PASSWORD;
import PutInfluxDB.REL_FAILURE;
import PutInfluxDB.REL_SUCCESS;
import PutInfluxDB.USERNAME;
import java.util.List;
import org.apache.nifi.util.MockFlowFile;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration test for InfluxDB. Please ensure that the InfluxDB is running
 * on local host with default port and has database test with table test. Please set user
 * and password if applicable before running the integration tests.
 */
public class ITPutInfluxDB extends AbstractITInfluxDB {
    @Test
    public void testValidSinglePoint() {
        String message = "water,country=US,city=newark rain=1,humidity=0.6 ";
        byte[] bytes = message.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertEquals("Value should be equal", null, flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        QueryResult result = influxDB.query(new Query("select * from water", dbName));
        Assert.assertEquals("size should be same", 1, result.getResults().iterator().next().getSeries().size());
        List<List<Object>> values = result.getResults().iterator().next().getSeries().iterator().next().getValues();
        Assert.assertEquals("size should be same", 1, values.size());
    }

    @Test
    public void testValidSinglePointWithTime() {
        QueryResult result = influxDB.query(new Query("select * from water where time = 1501002274856668652", dbName));
        Assert.assertEquals("Should have no results", null, result.getResults().iterator().next().getSeries());
        String message = "water,country=US,city=sf rain=1,humidity=0.6 1501002274856668652";
        byte[] bytes = message.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertEquals("Value should be equal", null, flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        result = influxDB.query(new Query("select * from water where time = 1501002274856668652", dbName));
        Assert.assertEquals("size should be same", 1, result.getResults().iterator().next().getSeries().size());
        List<List<Object>> values = result.getResults().iterator().next().getSeries().iterator().next().getValues();
        Assert.assertEquals("size should be same", 1, values.size());
    }

    @Test
    public void testValidSinglePointWithTimeAndUrlExpression() {
        runner.setVariable("influxDBUrl", "http://localhost:8086");
        runner.setProperty(INFLUX_DB_URL, "${influxDBUrl}");
        QueryResult result = influxDB.query(new Query("select * from water where time = 1501002274856668652", dbName));
        Assert.assertEquals("Should have no results", null, result.getResults().iterator().next().getSeries());
        String message = "water,country=US,city=sf rain=1,humidity=0.6 1501002274856668652";
        byte[] bytes = message.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertEquals("Value should be equal", null, flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        result = influxDB.query(new Query("select * from water where time = 1501002274856668652", dbName));
        Assert.assertEquals("size should be same", 1, result.getResults().iterator().next().getSeries().size());
        List<List<Object>> values = result.getResults().iterator().next().getSeries().iterator().next().getValues();
        Assert.assertEquals("size should be same", 1, values.size());
    }

    @Test
    public void testValidSinglePointWithUsernameEL() {
        runner.setVariable("influxdb.username", "admin");
        runner.setProperty(USERNAME, "${influxdb.username}");
        QueryResult result = influxDB.query(new Query("select * from water where time = 1501002274856668652", dbName));
        Assert.assertEquals("Should have no results", null, result.getResults().iterator().next().getSeries());
        String message = "water,country=US,city=sf rain=1,humidity=0.6 1501002274856668652";
        byte[] bytes = message.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testValidSinglePointWithPasswordEL() {
        runner.setVariable("influxdb.password", "admin");
        runner.setProperty(PASSWORD, "${influxdb.password}");
        QueryResult result = influxDB.query(new Query("select * from water where time = 1501002274856668652", dbName));
        Assert.assertEquals("Should have no results", null, result.getResults().iterator().next().getSeries());
        String message = "water,country=US,city=sf rain=1,humidity=0.6 1501002274856668652";
        byte[] bytes = message.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testValidTwoPointWithSameMeasurement() {
        String message = (("water,country=US,city=newark rain=1,humidity=0.6" + (System.lineSeparator())) + "water,country=US,city=nyc rain=2,humidity=0.7") + (System.lineSeparator());
        byte[] bytes = message.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertEquals("Value should be equal", null, flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        QueryResult result = influxDB.query(new Query("select * from water", dbName));
        Assert.assertEquals("size should be same", 1, result.getResults().iterator().next().getSeries().size());
        List<List<Object>> values = result.getResults().iterator().next().getSeries().iterator().next().getValues();
        Assert.assertEquals("size should be same", 2, values.size());
    }

    @Test
    public void testValidTwoPointWithSameMeasurementBadFormat() {
        String message = (("water,country=US,city=newark rain=1,humidity=0.6" + (System.lineSeparator())) + "water,country=US,city=nyc,rain=2,humidity=0.7") + (System.lineSeparator());
        byte[] bytes = message.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertEquals("Value should be equal", "{\"error\":\"partial write: unable to parse \'water,country=US,city=nyc,rain=2,humidity=0.7\': missing fields dropped=0\"}\n", flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        QueryResult result = influxDB.query(new Query("select * from water", dbName));
        Assert.assertEquals("size should be same", 1, result.getResults().iterator().next().getSeries().size());
        List<List<Object>> values = result.getResults().iterator().next().getSeries().iterator().next().getValues();
        Assert.assertEquals("size should be same", 1, values.size());
    }

    @Test
    public void testValidTwoPointWithDifferentMeasurement() {
        String message = (("water,country=US,city=newark rain=1,humidity=0.6" + (System.lineSeparator())) + "testm,country=US,city=chicago rain=10,humidity=0.9") + (System.lineSeparator());
        byte[] bytes = message.getBytes();
        runner.enqueue(bytes);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals("Value should be equal", 1, flowFiles.size());
        Assert.assertEquals("Value should be equal", null, flowFiles.get(0).getAttribute(INFLUX_DB_ERROR_MESSAGE));
        QueryResult result = influxDB.query(new Query("select * from water, testm", dbName));
        Assert.assertEquals("size should be same", 2, result.getResults().iterator().next().getSeries().size());
        List<List<Object>> values = result.getResults().iterator().next().getSeries().iterator().next().getValues();
        Assert.assertEquals("size should be same", 1, values.size());
    }
}

