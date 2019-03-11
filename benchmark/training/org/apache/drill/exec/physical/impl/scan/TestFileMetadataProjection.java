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
import DataMode.REQUIRED;
import FileMetadataColumn.ID;
import MinorType.VARCHAR;
import UnresolvedColumn.UNRESOLVED;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.physical.impl.scan.file.FileMetadata;
import org.apache.drill.exec.physical.impl.scan.file.FileMetadataColumn;
import org.apache.drill.exec.physical.impl.scan.file.FileMetadataManager;
import org.apache.drill.exec.physical.impl.scan.file.PartitionColumn;
import org.apache.drill.exec.physical.impl.scan.project.ColumnProjection;
import org.apache.drill.exec.physical.impl.scan.project.NullColumnBuilder;
import org.apache.drill.exec.physical.impl.scan.project.ResolvedColumn;
import org.apache.drill.exec.physical.impl.scan.project.ResolvedTuple.ResolvedRow;
import org.apache.drill.exec.physical.impl.scan.project.ScanLevelProjection;
import org.apache.drill.exec.physical.rowSet.impl.RowSetTestUtils;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.SubOperatorTest;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


// TODO: Test more partition cols in select than are available dirs
// TODO: Test customizing partition name, implicit col names.
// TODO: Test a single-file scan in which the root dir is the same as the one and only file.
@Category(RowSetTests.class)
public class TestFileMetadataProjection extends SubOperatorTest {
    @Test
    public void testMetadataBuilder() {
        {
            // Degenerate case: no file or root
            FileMetadata md = new FileMetadata(null, null);
            Assert.assertFalse(md.isSet());
            Assert.assertNull(md.filePath());
            Assert.assertEquals(0, md.dirPathLength());
            Assert.assertNull(md.partition(0));
        }
        {
            // Degenerate case: no file path, but with as selection root
            // Should never occur in practice.
            Path root = new Path("hdfs://a/b");
            FileMetadata md = new FileMetadata(null, root);
            Assert.assertFalse(md.isSet());
            Assert.assertNull(md.filePath());
            Assert.assertEquals(0, md.dirPathLength());
            Assert.assertNull(md.partition(0));
        }
        {
            // Simple file, no selection root.
            // Should never really occur, but let's test it anyway.
            Path input = new Path("hdfs://foo.csv");
            FileMetadata md = new FileMetadata(input, null);
            Assert.assertTrue(md.isSet());
            Assert.assertSame(input, md.filePath());
            Assert.assertEquals(0, md.dirPathLength());
            Assert.assertNull(md.partition(0));
        }
        {
            // Normal file, no selection root.
            Path input = new Path("hdfs://a/b/c/foo.csv");
            FileMetadata md = new FileMetadata(input, null);
            Assert.assertTrue(md.isSet());
            Assert.assertSame(input, md.filePath());
            Assert.assertEquals(0, md.dirPathLength());
            Assert.assertNull(md.partition(0));
        }
        {
            // Normal file, resides in selection root.
            Path root = new Path("hdfs://a/b");
            Path input = new Path("hdfs://a/b/foo.csv");
            FileMetadata md = new FileMetadata(input, root);
            Assert.assertTrue(md.isSet());
            Assert.assertSame(input, md.filePath());
            Assert.assertEquals(0, md.dirPathLength());
            Assert.assertNull(md.partition(0));
        }
        {
            // Normal file, below selection root.
            Path root = new Path("hdfs://a/b");
            Path input = new Path("hdfs://a/b/c/foo.csv");
            FileMetadata md = new FileMetadata(input, root);
            Assert.assertTrue(md.isSet());
            Assert.assertSame(input, md.filePath());
            Assert.assertEquals(1, md.dirPathLength());
            Assert.assertEquals("c", md.partition(0));
            Assert.assertNull(md.partition(1));
        }
        {
            // Normal file, above selection root.
            // This is an error condition.
            Path root = new Path("hdfs://a/b");
            Path input = new Path("hdfs://a/foo.csv");
            try {
                new FileMetadata(input, root);
                Assert.fail();
            } catch (IllegalArgumentException e) {
                // Expected
            }
        }
        {
            // Normal file, disjoint with selection root.
            // This is an error condition.
            Path root = new Path("hdfs://a/b");
            Path input = new Path("hdfs://d/foo.csv");
            try {
                new FileMetadata(input, root);
                Assert.fail();
            } catch (IllegalArgumentException e) {
                // Expected
            }
        }
    }

    /**
     * Test the file projection planner with metadata.
     */
    @Test
    public void testProjectList() {
        Path filePath = new Path("hdfs:///w/x/y/z.csv");
        FileMetadataManager metadataManager = new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), new Path("hdfs:///w"), Lists.newArrayList(filePath));
        ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList(ScanTestUtils.FILE_NAME_COL, "a", ScanTestUtils.partitionColName(0)), ScanTestUtils.parsers(metadataManager.projectionParser()));
        Assert.assertEquals(3, scanProj.columns().size());
        // Scan-level projection: defines the columns
        {
            Assert.assertTrue(((scanProj.columns().get(0)) instanceof FileMetadataColumn));
            FileMetadataColumn col0 = ((FileMetadataColumn) (scanProj.columns().get(0)));
            Assert.assertEquals(ID, col0.nodeType());
            Assert.assertEquals(ScanTestUtils.FILE_NAME_COL, col0.name());
            Assert.assertEquals(VARCHAR, col0.schema().getType().getMinorType());
            Assert.assertEquals(REQUIRED, col0.schema().getType().getMode());
            ColumnProjection col1 = scanProj.columns().get(1);
            Assert.assertEquals(UNRESOLVED, col1.nodeType());
            Assert.assertEquals("a", col1.name());
            Assert.assertTrue(((scanProj.columns().get(2)) instanceof PartitionColumn));
            PartitionColumn col2 = ((PartitionColumn) (scanProj.columns().get(2)));
            Assert.assertEquals(PartitionColumn.ID, col2.nodeType());
            Assert.assertEquals(ScanTestUtils.partitionColName(0), col2.name());
            Assert.assertEquals(VARCHAR, col2.schema().getType().getMinorType());
            Assert.assertEquals(OPTIONAL, col2.schema().getType().getMode());
        }
        // Schema-level projection, fills in values.
        TupleMetadata tableSchema = new SchemaBuilder().add("a", VARCHAR).buildSchema();
        metadataManager.startFile(filePath);
        NullColumnBuilder builder = new NullColumnBuilder(null, false);
        ResolvedRow rootTuple = new ResolvedRow(builder);
        new org.apache.drill.exec.physical.impl.scan.project.ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers(metadataManager));
        List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(3, columns.size());
        {
            Assert.assertTrue(((columns.get(0)) instanceof FileMetadataColumn));
            FileMetadataColumn col0 = ((FileMetadataColumn) (columns.get(0)));
            Assert.assertEquals(ID, col0.nodeType());
            Assert.assertEquals(ScanTestUtils.FILE_NAME_COL, col0.name());
            Assert.assertEquals("z.csv", col0.value());
            Assert.assertEquals(VARCHAR, col0.schema().getType().getMinorType());
            Assert.assertEquals(REQUIRED, col0.schema().getType().getMode());
            ResolvedColumn col1 = columns.get(1);
            Assert.assertEquals("a", col1.name());
            Assert.assertTrue(((columns.get(2)) instanceof PartitionColumn));
            PartitionColumn col2 = ((PartitionColumn) (columns.get(2)));
            Assert.assertEquals(PartitionColumn.ID, col2.nodeType());
            Assert.assertEquals(ScanTestUtils.partitionColName(0), col2.name());
            Assert.assertEquals("x", col2.value());
            Assert.assertEquals(VARCHAR, col2.schema().getType().getMinorType());
            Assert.assertEquals(OPTIONAL, col2.schema().getType().getMode());
        }
        // Verify that the file metadata columns were picked out
        Assert.assertEquals(2, metadataManager.metadataColumns().size());
        Assert.assertSame(columns.get(0), metadataManager.metadataColumns().get(0));
        Assert.assertSame(columns.get(2), metadataManager.metadataColumns().get(1));
    }

    /**
     * Test a query with explicit mention of file metadata columns.
     */
    @Test
    public void testFileMetadata() {
        Path filePath = new Path("hdfs:///w/x/y/z.csv");
        FileMetadataManager metadataManager = new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), new Path("hdfs:///w"), Lists.newArrayList(filePath));
        ScanLevelProjection scanProj = new ScanLevelProjection(// Sic, to test case sensitivity
        RowSetTestUtils.projectList("a", ScanTestUtils.FULLY_QUALIFIED_NAME_COL, "filEPath", ScanTestUtils.FILE_NAME_COL, ScanTestUtils.SUFFIX_COL), ScanTestUtils.parsers(metadataManager.projectionParser()));
        Assert.assertEquals(5, scanProj.columns().size());
        Assert.assertEquals(ScanTestUtils.FULLY_QUALIFIED_NAME_COL, scanProj.columns().get(1).name());
        Assert.assertEquals("filEPath", scanProj.columns().get(2).name());
        Assert.assertEquals(ScanTestUtils.FILE_NAME_COL, scanProj.columns().get(3).name());
        Assert.assertEquals(ScanTestUtils.SUFFIX_COL, scanProj.columns().get(4).name());
        // Schema-level projection, fills in values.
        TupleMetadata tableSchema = new SchemaBuilder().add("a", VARCHAR).buildSchema();
        metadataManager.startFile(filePath);
        NullColumnBuilder builder = new NullColumnBuilder(null, false);
        ResolvedRow rootTuple = new ResolvedRow(builder);
        new org.apache.drill.exec.physical.impl.scan.project.ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers(metadataManager));
        List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(5, columns.size());
        Assert.assertEquals("/w/x/y/z.csv", value());
        Assert.assertEquals("/w/x/y", value());
        Assert.assertEquals("z.csv", value());
        Assert.assertEquals("csv", value());
    }

    /**
     * Test the obscure case that the partition column contains two digits:
     * dir11. Also tests the obscure case that the output only has partition
     * columns.
     */
    @Test
    public void testPartitionColumnTwoDigits() {
        Path filePath = new Path("hdfs:///x/0/1/2/3/4/5/6/7/8/9/10/d11/z.csv");
        FileMetadataManager metadataManager = new FileMetadataManager(SubOperatorTest.fixture.getOptionManager(), new Path("hdfs:///x"), Lists.newArrayList(filePath));
        ScanLevelProjection scanProj = new ScanLevelProjection(RowSetTestUtils.projectList("dir11"), ScanTestUtils.parsers(metadataManager.projectionParser()));
        TupleMetadata tableSchema = new SchemaBuilder().add("a", VARCHAR).buildSchema();
        metadataManager.startFile(filePath);
        NullColumnBuilder builder = new NullColumnBuilder(null, false);
        ResolvedRow rootTuple = new ResolvedRow(builder);
        new org.apache.drill.exec.physical.impl.scan.project.ExplicitSchemaProjection(scanProj, tableSchema, rootTuple, ScanTestUtils.resolvers(metadataManager));
        List<ResolvedColumn> columns = rootTuple.columns();
        Assert.assertEquals(1, columns.size());
        Assert.assertEquals("d11", value());
    }
}

