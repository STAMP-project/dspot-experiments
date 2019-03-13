/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.impl.scan;


import DataMode.OPTIONAL;
import MinorType.INT;
import MinorType.VARCHAR;
import SchemaPath.DYNAMIC_STAR;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.exec.physical.impl.protocol.SchemaTracker;
import org.apache.drill.exec.physical.impl.scan.file.FileMetadataManager;
import org.apache.drill.exec.physical.impl.scan.project.ReaderSchemaOrchestrator;
import org.apache.drill.exec.physical.impl.scan.project.ScanSchemaOrchestrator;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.impl.RowSetTestUtils;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests the scan orchestrator's ability to merge table schemas
 * with implicit file columns provided by the file metadata manager.
 */
@Category(RowSetTests.class)
public class TestScanOrchestratorMetadata extends SubOperatorTest {
    /**
     * Resolve a selection list using SELECT *.
     */
    @Test
    public void testWildcardWithMetadata() {
        Path filePath = new Path("hdfs:///w/x/y/z.csv");
        FileMetadataManager metadataManager = // Don't expand partition columns for wildcard
        // N/A
        new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), false, false, new Path("hdfs:///w"), FileMetadataManager.AUTO_PARTITION_DEPTH, Lists.newArrayList(filePath));
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        scanner.withMetadata(metadataManager);
        // SELECT *, filename, suffix ...
        scanner.build(RowSetTestUtils.projectList(DYNAMIC_STAR, ScanTestUtils.FULLY_QUALIFIED_NAME_COL, ScanTestUtils.FILE_PATH_COL, ScanTestUtils.FILE_NAME_COL, ScanTestUtils.SUFFIX_COL, ScanTestUtils.partitionColName(0), ScanTestUtils.partitionColName(1)));
        // ... FROM file
        metadataManager.startFile(filePath);
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        TupleMetadata expectedSchema = ScanTestUtils.expandMetadata(tableSchema, metadataManager, 2);
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(1, "fred", "/w/x/y/z.csv", "/w/x/y", "z.csv", "csv", "x", "y").addRow(2, "wilma", "/w/x/y/z.csv", "/w/x/y", "z.csv", "csv", "x", "y").build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        scanner.close();
    }

    /**
     * Test SELECT c FROM table(a, b)
     * The result set will be one null column for each record, but
     * no file data.
     */
    @Test
    public void testSelectNone() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        Path filePath = new Path("hdfs:///w/x/y/z.csv");
        FileMetadataManager metadataManager = // Don't expand partition columns for wildcard
        // N/A
        new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), false, false, new Path("hdfs:///w"), FileMetadataManager.AUTO_PARTITION_DEPTH, Lists.newArrayList(filePath));
        scanner.withMetadata(metadataManager);
        // SELECT c ...
        scanner.build(RowSetTestUtils.projectList("c"));
        // ... FROM file
        metadataManager.startFile(filePath);
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("c", INT).build();
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addSingleCol(null).addSingleCol(null).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        scanner.close();
    }

    /**
     * Test SELECT a, b, dir0, suffix FROM table(a, b)
     * dir0, suffix are file metadata columns
     */
    @Test
    public void testEarlySchemaSelectAllAndMetadata() {
        // Null columns of type VARCHAR
        MajorType nullType = MajorType.newBuilder().setMinorType(VARCHAR).setMode(OPTIONAL).build();
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        scanner.setNullType(nullType);
        Path filePath = new Path("hdfs:///w/x/y/z.csv");
        FileMetadataManager metadataManager = // Don't expand partition columns for wildcard
        // N/A
        new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), false, false, new Path("hdfs:///w"), FileMetadataManager.AUTO_PARTITION_DEPTH, Lists.newArrayList(filePath));
        scanner.withMetadata(metadataManager);
        // SELECT a, b, dir0, suffix ...
        scanner.build(RowSetTestUtils.projectList("a", "b", "dir0", "suffix"));
        // ... FROM file
        metadataManager.startFile(filePath);
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        // Verify empty batch.
        reader.defineSchema();
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).addNullable("dir0", VARCHAR).add("suffix", VARCHAR).build();
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).build();
            Assert.assertNotNull(scanner.output());
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        }
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(1, "fred", "x", "csv").addRow(2, "wilma", "x", "csv").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        }
        scanner.close();
    }

    /**
     * Test SELECT dir0, b, suffix, c FROM table(a, b)
     * Full combination of metadata, table and null columns
     */
    @Test
    public void testMixture() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        Path filePath = new Path("hdfs:///w/x/y/z.csv");
        FileMetadataManager metadataManager = // Don't expand partition columns for wildcard
        // N/A
        new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), false, false, new Path("hdfs:///w"), FileMetadataManager.AUTO_PARTITION_DEPTH, Lists.newArrayList(filePath));
        scanner.withMetadata(metadataManager);
        // SELECT dir0, b, suffix, c ...
        scanner.build(RowSetTestUtils.projectList("dir0", "b", "suffix", "c"));
        // ... FROM file
        metadataManager.startFile(filePath);
        ReaderSchemaOrchestrator reader = scanner.startReader();
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        // Create the table loader
        ResultSetLoader loader = reader.makeTableLoader(tableSchema);
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("dir0", VARCHAR).add("b", VARCHAR).add("suffix", VARCHAR).addNullable("c", INT).build();
        // Create a batch of data.
        reader.startBatch();
        loader.writer().addRow(1, "fred").addRow(2, "wilma");
        reader.endBatch();
        // Verify
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow("x", "fred", "csv", null).addRow("x", "wilma", "csv", null).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
        scanner.close();
    }

    /**
     * Verify that metadata columns follow distinct files
     * <br>
     * SELECT dir0, filename, b FROM (a.csv, b.csv)
     */
    @Test
    public void testMetadataMulti() {
        ScanSchemaOrchestrator scanner = new ScanSchemaOrchestrator(SubOperatorTest.fixture.allocator());
        Path filePathA = new Path("hdfs:///w/x/y/a.csv");
        Path filePathB = new Path("hdfs:///w/x/b.csv");
        FileMetadataManager metadataManager = // Don't expand partition columns for wildcard
        // N/A
        new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), false, false, new Path("hdfs:///w"), FileMetadataManager.AUTO_PARTITION_DEPTH, Lists.newArrayList(filePathA, filePathB));
        scanner.withMetadata(metadataManager);
        // SELECT dir0, dir1, filename, b ...
        scanner.build(RowSetTestUtils.projectList(ScanTestUtils.partitionColName(0), ScanTestUtils.partitionColName(1), ScanTestUtils.FILE_NAME_COL, "b"));
        // file schema (a, b)
        TupleMetadata tableSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR, 10).buildSchema();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable(ScanTestUtils.partitionColName(0), VARCHAR).addNullable(ScanTestUtils.partitionColName(1), VARCHAR).add(ScanTestUtils.FILE_NAME_COL, VARCHAR).addNullable("b", VARCHAR, 10).build();
        SchemaTracker tracker = new SchemaTracker();
        int schemaVersion;
        {
            // ... FROM file a.csv
            metadataManager.startFile(filePathA);
            ReaderSchemaOrchestrator reader = scanner.startReader();
            ResultSetLoader loader = reader.makeTableLoader(tableSchema);
            reader.startBatch();
            loader.writer().addRow(10, "fred").addRow(20, "wilma");
            reader.endBatch();
            tracker.trackSchema(scanner.output());
            schemaVersion = tracker.schemaVersion();
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow("x", "y", "a.csv", "fred").addRow("x", "y", "a.csv", "wilma").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
            // Do explicit close (as in real code) to avoid an implicit
            // close which will blow away the current file info...
            scanner.closeReader();
        }
        {
            // ... FROM file b.csv
            metadataManager.startFile(filePathB);
            ReaderSchemaOrchestrator reader = scanner.startReader();
            ResultSetLoader loader = reader.makeTableLoader(tableSchema);
            reader.startBatch();
            loader.writer().addRow(30, "bambam").addRow(40, "betty");
            reader.endBatch();
            tracker.trackSchema(scanner.output());
            Assert.assertEquals(schemaVersion, tracker.schemaVersion());
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow("x", null, "b.csv", "bambam").addRow("x", null, "b.csv", "betty").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(scanner.output()));
            scanner.closeReader();
        }
        scanner.close();
    }
}

