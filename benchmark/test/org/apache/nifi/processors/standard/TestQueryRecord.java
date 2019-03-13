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
package org.apache.nifi.processors.standard;


import QueryRecord.INCLUDE_ZERO_RECORD_FLOWFILES;
import QueryRecord.RECORD_READER_FACTORY;
import QueryRecord.RECORD_WRITER_FACTORY;
import QueryRecord.REL_FAILURE;
import RecordFieldType.FLOAT;
import RecordFieldType.INT;
import RecordFieldType.STRING;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.RecordSetWriter;
import org.apache.nifi.serialization.RecordSetWriterFactory;
import org.apache.nifi.serialization.WriteResult;
import org.apache.nifi.serialization.record.ArrayListRecordReader;
import org.apache.nifi.serialization.record.ArrayListRecordWriter;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.MockRecordWriter;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.RecordSet;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestQueryRecord {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.log.nifi.io.nio", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.nifi.processors.standard.SQLTransform", "debug");
    }

    private static final String REL_NAME = "success";

    @Test
    public void testRecordPathFunctions() throws InitializationException {
        final Record record = createHierarchicalRecord();
        final ArrayListRecordReader recordReader = new ArrayListRecordReader(record.getSchema());
        recordReader.addRecord(record);
        final ArrayListRecordWriter writer = new ArrayListRecordWriter(record.getSchema());
        TestRunner runner = getRunner();
        runner.addControllerService("reader", recordReader);
        runner.enableControllerService(recordReader);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(RECORD_READER_FACTORY, "reader");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.setProperty(TestQueryRecord.REL_NAME, ("SELECT RPATH_STRING(person, '/name') AS name," + (((((((((" RPATH_INT(person, '/age') AS age," + " RPATH(person, '/name') AS nameObj,") + " RPATH(person, '/age') AS ageObj,") + " RPATH(person, '/favoriteColors') AS colors,") + " RPATH(person, '//name') AS names,") + " RPATH_DATE(person, '/dob') AS dob,") + " RPATH_LONG(person, '/dobTimestamp') AS dobTimestamp,") + " RPATH_DATE(person, \'toDate(/joinTimestamp, \"yyyy-MM-dd\")\') AS joinTime, ") + " RPATH_DOUBLE(person, '/weight') AS weight") + " FROM FLOWFILE")));
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final List<Record> written = writer.getRecordsWritten();
        Assert.assertEquals(1, written.size());
        final Record output = written.get(0);
        Assert.assertEquals("John Doe", output.getValue("name"));
        Assert.assertEquals("John Doe", output.getValue("nameObj"));
        Assert.assertEquals(30, output.getValue("age"));
        Assert.assertEquals(30, output.getValue("ageObj"));
        Assert.assertArrayEquals(new String[]{ "red", "green" }, ((Object[]) (output.getValue("colors"))));
        Assert.assertArrayEquals(new String[]{ "John Doe", "Jane Doe" }, ((Object[]) (output.getValue("names"))));
        Assert.assertEquals("1517702400000", output.getAsString("joinTime"));
        Assert.assertEquals(Double.valueOf(180.8), output.getAsDouble("weight"));
    }

    @Test
    public void testRecordPathInAggregate() throws InitializationException {
        final Record record = createHierarchicalRecord();
        final ArrayListRecordReader recordReader = new ArrayListRecordReader(record.getSchema());
        for (int i = 0; i < 100; i++) {
            final Record toAdd = createHierarchicalRecord();
            final Record person = ((Record) (toAdd.getValue("person")));
            person.setValue("name", ("Person " + i));
            person.setValue("age", i);
            recordReader.addRecord(toAdd);
        }
        final ArrayListRecordWriter writer = new ArrayListRecordWriter(record.getSchema());
        TestRunner runner = getRunner();
        runner.addControllerService("reader", recordReader);
        runner.enableControllerService(recordReader);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(RECORD_READER_FACTORY, "reader");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.setProperty(TestQueryRecord.REL_NAME, ("SELECT RPATH_STRING(person, '/name') AS name FROM FLOWFILE" + ((" WHERE RPATH_INT(person, '/age') > (" + "   SELECT AVG( RPATH_INT(person, '/age') ) FROM FLOWFILE") + ")")));
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final List<Record> written = writer.getRecordsWritten();
        Assert.assertEquals(50, written.size());
        int i = 50;
        for (final Record writtenRecord : written) {
            final String name = writtenRecord.getAsString("name");
            Assert.assertEquals(("Person " + i), name);
            i++;
        }
    }

    @Test
    public void testRecordPathWithArray() throws InitializationException {
        final Record record = createHierarchicalArrayRecord();
        final ArrayListRecordReader recordReader = new ArrayListRecordReader(record.getSchema());
        recordReader.addRecord(record);
        final ArrayListRecordWriter writer = new ArrayListRecordWriter(record.getSchema());
        TestRunner runner = getRunner();
        runner.addControllerService("reader", recordReader);
        runner.enableControllerService(recordReader);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(RECORD_READER_FACTORY, "reader");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.setProperty(TestQueryRecord.REL_NAME, ("SELECT title, name" + (("    FROM FLOWFILE" + "    WHERE RPATH(addresses, '/state[/label = ''home'']') <>") + "          RPATH(addresses, '/state[/label = ''work'']')")));
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final List<Record> written = writer.getRecordsWritten();
        Assert.assertEquals(1, written.size());
        final Record output = written.get(0);
        Assert.assertEquals("John Doe", output.getValue("name"));
        Assert.assertEquals("Software Engineer", output.getValue("title"));
    }

    @Test
    public void testCompareResultsOfTwoRecordPathsAgainstArray() throws InitializationException {
        final Record record = createHierarchicalArrayRecord();
        // Change the value of the 'state' field of both addresses to NY.
        // This allows us to use an equals operator to ensure that we do get back the same values,
        // whereas the unit test above tests <> and that may result in 'false confidence' if the software
        // were to provide the wrong values but values that were not equal.
        Record[] addresses = ((Record[]) (record.getValue("addresses")));
        for (final Record address : addresses) {
            address.setValue("state", "NY");
        }
        final ArrayListRecordReader recordReader = new ArrayListRecordReader(record.getSchema());
        recordReader.addRecord(record);
        final ArrayListRecordWriter writer = new ArrayListRecordWriter(record.getSchema());
        TestRunner runner = getRunner();
        runner.addControllerService("reader", recordReader);
        runner.enableControllerService(recordReader);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(RECORD_READER_FACTORY, "reader");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.setProperty(TestQueryRecord.REL_NAME, ("SELECT title, name" + (("    FROM FLOWFILE" + "    WHERE RPATH(addresses, '/state[/label = ''home'']') =") + "          RPATH(addresses, '/state[/label = ''work'']')")));
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final List<Record> written = writer.getRecordsWritten();
        Assert.assertEquals(1, written.size());
        final Record output = written.get(0);
        Assert.assertEquals("John Doe", output.getValue("name"));
        Assert.assertEquals("Software Engineer", output.getValue("title"));
    }

    @Test
    public void testRecordPathWithArrayAndOnlyOneElementMatchingRPath() throws InitializationException {
        final Record record = createHierarchicalArrayRecord();
        final ArrayListRecordReader recordReader = new ArrayListRecordReader(record.getSchema());
        recordReader.addRecord(record);
        final ArrayListRecordWriter writer = new ArrayListRecordWriter(record.getSchema());
        TestRunner runner = getRunner();
        runner.addControllerService("reader", recordReader);
        runner.enableControllerService(recordReader);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(RECORD_READER_FACTORY, "reader");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.setProperty(TestQueryRecord.REL_NAME, ("SELECT title, name" + ("    FROM FLOWFILE" + "    WHERE RPATH(addresses, '/state[. = ''NY'']') = 'NY'")));
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final List<Record> written = writer.getRecordsWritten();
        Assert.assertEquals(1, written.size());
        final Record output = written.get(0);
        Assert.assertEquals("John Doe", output.getValue("name"));
        Assert.assertEquals("Software Engineer", output.getValue("title"));
    }

    @Test
    public void testLikeWithRecordPath() throws InitializationException {
        final Record record = createHierarchicalArrayRecord();
        final ArrayListRecordReader recordReader = new ArrayListRecordReader(record.getSchema());
        recordReader.addRecord(record);
        final ArrayListRecordWriter writer = new ArrayListRecordWriter(record.getSchema());
        TestRunner runner = getRunner();
        runner.addControllerService("reader", recordReader);
        runner.enableControllerService(recordReader);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(RECORD_READER_FACTORY, "reader");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.setProperty(TestQueryRecord.REL_NAME, ("SELECT title, name" + ("    FROM FLOWFILE" + "    WHERE RPATH_STRING(addresses, '/state[. = ''NY'']') LIKE 'N%'")));
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final List<Record> written = writer.getRecordsWritten();
        Assert.assertEquals(1, written.size());
        final Record output = written.get(0);
        Assert.assertEquals("John Doe", output.getValue("name"));
        Assert.assertEquals("Software Engineer", output.getValue("title"));
    }

    @Test
    public void testRecordPathWithMap() throws InitializationException {
        final Record record = createHierarchicalRecord();
        final ArrayListRecordReader recordReader = new ArrayListRecordReader(record.getSchema());
        recordReader.addRecord(record);
        final ArrayListRecordWriter writer = new ArrayListRecordWriter(record.getSchema());
        TestRunner runner = getRunner();
        runner.addControllerService("reader", recordReader);
        runner.enableControllerService(recordReader);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(RECORD_READER_FACTORY, "reader");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.setProperty(TestQueryRecord.REL_NAME, ("SELECT RPATH(favoriteThings, '.[''sport'']') AS sport," + ((" RPATH_STRING(person, '/name') AS nameObj" + " FROM FLOWFILE") + " WHERE RPATH(favoriteThings, '.[''color'']') = 'green'")));
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final List<Record> written = writer.getRecordsWritten();
        Assert.assertEquals(1, written.size());
        final Record output = written.get(0);
        Assert.assertEquals("basketball", output.getValue("sport"));
        Assert.assertEquals("John Doe", output.getValue("nameObj"));
    }

    @Test
    public void testStreamClosedWhenBadData() throws InitializationException {
        final MockRecordParser parser = new MockRecordParser();
        parser.failAfter(0);
        parser.addSchemaField("name", STRING);
        parser.addSchemaField("age", INT);
        parser.addRecord("Tom", 49);
        final MockRecordWriter writer = new MockRecordWriter("\"name\",\"points\"");
        TestRunner runner = getRunner();
        runner.addControllerService("parser", parser);
        runner.enableControllerService(parser);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(TestQueryRecord.REL_NAME, "select name, age from FLOWFILE WHERE name <> ''");
        runner.setProperty(RECORD_READER_FACTORY, "parser");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void testSimple() throws IOException, SQLException, InitializationException {
        final MockRecordParser parser = new MockRecordParser();
        parser.addSchemaField("name", STRING);
        parser.addSchemaField("age", INT);
        parser.addRecord("Tom", 49);
        final MockRecordWriter writer = new MockRecordWriter("\"name\",\"points\"");
        TestRunner runner = getRunner();
        runner.addControllerService("parser", parser);
        runner.enableControllerService(parser);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(TestQueryRecord.REL_NAME, "select name, age from FLOWFILE WHERE name <> ''");
        runner.setProperty(RECORD_READER_FACTORY, "parser");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        final int numIterations = 1;
        for (int i = 0; i < numIterations; i++) {
            runner.enqueue(new byte[0]);
        }
        runner.setThreadCount(4);
        runner.run((2 * numIterations));
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(TestQueryRecord.REL_NAME).get(0);
        System.out.println(new String(out.toByteArray()));
        out.assertContentEquals("\"name\",\"points\"\n\"Tom\",\"49\"\n");
    }

    @Test
    public void testNullable() throws IOException, SQLException, InitializationException {
        final MockRecordParser parser = new MockRecordParser();
        parser.addSchemaField("name", STRING, true);
        parser.addSchemaField("age", INT, true);
        parser.addRecord("Tom", 49);
        parser.addRecord("Alice", null);
        parser.addRecord(null, 36);
        final MockRecordWriter writer = new MockRecordWriter("\"name\",\"points\"");
        TestRunner runner = getRunner();
        runner.addControllerService("parser", parser);
        runner.enableControllerService(parser);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(TestQueryRecord.REL_NAME, "select name, age from FLOWFILE");
        runner.setProperty(RECORD_READER_FACTORY, "parser");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        final int numIterations = 1;
        for (int i = 0; i < numIterations; i++) {
            runner.enqueue(new byte[0]);
        }
        runner.setThreadCount(4);
        runner.run((2 * numIterations));
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(TestQueryRecord.REL_NAME).get(0);
        System.out.println(new String(out.toByteArray()));
        out.assertContentEquals("\"name\",\"points\"\n\"Tom\",\"49\"\n\"Alice\",\n,\"36\"\n");
    }

    @Test
    public void testParseFailure() throws IOException, SQLException, InitializationException {
        final MockRecordParser parser = new MockRecordParser();
        parser.addSchemaField("name", STRING);
        parser.addSchemaField("age", INT);
        parser.addRecord("Tom", 49);
        final MockRecordWriter writer = new MockRecordWriter("\"name\",\"points\"");
        TestRunner runner = getRunner();
        runner.addControllerService("parser", parser);
        runner.enableControllerService(parser);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(TestQueryRecord.REL_NAME, "select name, age from FLOWFILE WHERE name <> ''");
        runner.setProperty(RECORD_READER_FACTORY, "parser");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        final int numIterations = 1;
        for (int i = 0; i < numIterations; i++) {
            runner.enqueue(new byte[0]);
        }
        runner.setThreadCount(4);
        runner.run((2 * numIterations));
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(TestQueryRecord.REL_NAME).get(0);
        System.out.println(new String(out.toByteArray()));
        out.assertContentEquals("\"name\",\"points\"\n\"Tom\",\"49\"\n");
    }

    @Test
    public void testTransformCalc() throws IOException, SQLException, InitializationException {
        final MockRecordParser parser = new MockRecordParser();
        parser.addSchemaField("ID", INT);
        parser.addSchemaField("AMOUNT1", FLOAT);
        parser.addSchemaField("AMOUNT2", FLOAT);
        parser.addSchemaField("AMOUNT3", FLOAT);
        parser.addRecord(8, 10.05F, 15.45F, 89.99F);
        parser.addRecord(100, 20.25F, 25.25F, 45.25F);
        parser.addRecord(105, 20.05F, 25.05F, 45.05F);
        parser.addRecord(200, 34.05F, 25.05F, 75.05F);
        final MockRecordWriter writer = new MockRecordWriter("\"NAME\",\"POINTS\"");
        TestRunner runner = getRunner();
        runner.addControllerService("parser", parser);
        runner.enableControllerService(parser);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(TestQueryRecord.REL_NAME, "select ID, AMOUNT1+AMOUNT2+AMOUNT3 as TOTAL from FLOWFILE where ID=100");
        runner.setProperty(RECORD_READER_FACTORY, "parser");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(TestQueryRecord.REL_NAME).get(0);
        out.assertContentEquals("\"NAME\",\"POINTS\"\n\"100\",\"90.75\"\n");
    }

    @Test
    public void testHandlingWithInvalidSchema() throws InitializationException {
        final MockRecordParser parser = new MockRecordParser();
        parser.addSchemaField("name", STRING);
        parser.addSchemaField("favorite_color", STRING);
        parser.addSchemaField("address", STRING);
        parser.addRecord("Tom", "blue", null);
        parser.addRecord("Jerry", "red", null);
        final MockRecordWriter writer = new MockRecordWriter("\"name\",\"points\"");
        TestRunner runner = getRunner();
        runner.enforceReadStreamsClosed(false);
        runner.addControllerService("parser", parser);
        runner.enableControllerService(parser);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(INCLUDE_ZERO_RECORD_FLOWFILES, "false");
        runner.setProperty("rel1", "select * from FLOWFILE where address IS NOT NULL");
        runner.setProperty("rel2", "select name, CAST(favorite_color AS DOUBLE) AS num from FLOWFILE");
        runner.setProperty("rel3", "select * from FLOWFILE where address IS NOT NULL");
        runner.setProperty(RECORD_READER_FACTORY, "parser");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enqueue("");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testAggregateFunction() throws IOException, InitializationException {
        final MockRecordParser parser = new MockRecordParser();
        parser.addSchemaField("name", STRING);
        parser.addSchemaField("points", INT);
        parser.addRecord("Tom", 1);
        parser.addRecord("Jerry", 2);
        parser.addRecord("Tom", 99);
        final MockRecordWriter writer = new MockRecordWriter("\"name\",\"points\"");
        TestRunner runner = getRunner();
        runner.addControllerService("parser", parser);
        runner.enableControllerService(parser);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(TestQueryRecord.REL_NAME, "select name, sum(points) as points from FLOWFILE GROUP BY name");
        runner.setProperty(RECORD_READER_FACTORY, "parser");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enqueue("");
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        final MockFlowFile flowFileOut = runner.getFlowFilesForRelationship(TestQueryRecord.REL_NAME).get(0);
        flowFileOut.assertContentEquals("\"name\",\"points\"\n\"Tom\",\"100\"\n\"Jerry\",\"2\"\n");
    }

    @Test
    public void testNullValueInSingleField() throws IOException, InitializationException {
        final MockRecordParser parser = new MockRecordParser();
        parser.addSchemaField("name", STRING);
        parser.addSchemaField("points", INT);
        parser.addRecord("Tom", 1);
        parser.addRecord("Jerry", null);
        parser.addRecord("Tom", null);
        parser.addRecord("Jerry", 3);
        final MockRecordWriter writer = new MockRecordWriter(null, false);
        TestRunner runner = getRunner();
        runner.addControllerService("parser", parser);
        runner.enableControllerService(parser);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(TestQueryRecord.REL_NAME, "select points from FLOWFILE");
        runner.setProperty("count", "select count(*) as c from flowfile where points is null");
        runner.setProperty(RECORD_READER_FACTORY, "parser");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enqueue("");
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
        runner.assertTransferCount("count", 1);
        final MockFlowFile flowFileOut = runner.getFlowFilesForRelationship(TestQueryRecord.REL_NAME).get(0);
        flowFileOut.assertContentEquals("1\n\n\n3\n");
        final MockFlowFile countFlowFile = runner.getFlowFilesForRelationship("count").get(0);
        countFlowFile.assertContentEquals("2\n");
    }

    @Test
    public void testColumnNames() throws InitializationException {
        final MockRecordParser parser = new MockRecordParser();
        parser.addSchemaField("name", STRING);
        parser.addSchemaField("points", INT);
        parser.addSchemaField("greeting", STRING);
        parser.addRecord("Tom", 1, "Hello");
        parser.addRecord("Jerry", 2, "Hi");
        parser.addRecord("Tom", 99, "Howdy");
        final List<String> colNames = new ArrayList<>();
        colNames.add("name");
        colNames.add("points");
        colNames.add("greeting");
        colNames.add("FAV_GREETING");
        final TestQueryRecord.ResultSetValidatingRecordWriter writer = new TestQueryRecord.ResultSetValidatingRecordWriter(colNames);
        TestRunner runner = getRunner();
        runner.addControllerService("parser", parser);
        runner.enableControllerService(parser);
        runner.addControllerService("writer", writer);
        runner.enableControllerService(writer);
        runner.setProperty(TestQueryRecord.REL_NAME, "select *, greeting AS FAV_GREETING from FLOWFILE");
        runner.setProperty(RECORD_READER_FACTORY, "parser");
        runner.setProperty(RECORD_WRITER_FACTORY, "writer");
        runner.enqueue("");
        runner.run();
        runner.assertTransferCount(TestQueryRecord.REL_NAME, 1);
    }

    private static class ResultSetValidatingRecordWriter extends AbstractControllerService implements RecordSetWriterFactory {
        private final List<String> columnNames;

        public ResultSetValidatingRecordWriter(final List<String> colNames) {
            this.columnNames = new ArrayList<>(colNames);
        }

        @Override
        public RecordSchema getSchema(Map<String, String> variables, RecordSchema readSchema) throws IOException, SchemaNotFoundException {
            final List<RecordField> recordFields = columnNames.stream().map(( name) -> new RecordField(name, STRING.getDataType())).collect(Collectors.toList());
            return new org.apache.nifi.serialization.SimpleRecordSchema(recordFields);
        }

        @Override
        public RecordSetWriter createWriter(final ComponentLog logger, final RecordSchema schema, final OutputStream out) {
            return new RecordSetWriter() {
                @Override
                public void flush() throws IOException {
                    out.flush();
                }

                @Override
                public WriteResult write(final RecordSet rs) throws IOException {
                    final int colCount = rs.getSchema().getFieldCount();
                    Assert.assertEquals(columnNames.size(), colCount);
                    final List<String> colNames = new ArrayList<>(colCount);
                    for (int i = 0; i < colCount; i++) {
                        colNames.add(rs.getSchema().getField(i).getFieldName());
                    }
                    Assert.assertEquals(columnNames, colNames);
                    // Iterate over the rest of the records to ensure that we read the entire stream. If we don't
                    // do this, we won't consume all of the data and as a result we will not close the stream properly
                    Record record;
                    while ((record = rs.next()) != null) {
                        System.out.println(record);
                    } 
                    return WriteResult.of(0, Collections.emptyMap());
                }

                @Override
                public String getMimeType() {
                    return "text/plain";
                }

                @Override
                public WriteResult write(Record record) throws IOException {
                    return null;
                }

                @Override
                public void close() throws IOException {
                    out.close();
                }

                @Override
                public void beginRecordSet() throws IOException {
                }

                @Override
                public WriteResult finishRecordSet() throws IOException {
                    return WriteResult.EMPTY;
                }
            };
        }
    }
}

