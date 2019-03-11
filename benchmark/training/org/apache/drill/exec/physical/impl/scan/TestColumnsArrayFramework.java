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


import ColumnsArrayManager.COLUMNS_COL;
import MinorType.VARCHAR;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.exceptions.ExecutionSetupException;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.exec.physical.impl.scan.columns.ColumnsArrayManager;
import org.apache.drill.exec.physical.impl.scan.columns.ColumnsScanFramework.ColumnsSchemaNegotiator;
import org.apache.drill.exec.physical.impl.scan.columns.ColumnsScanFramework.FileReaderCreator;
import org.apache.drill.exec.physical.impl.scan.file.BaseFileScanFramework;
import org.apache.drill.exec.physical.impl.scan.framework.ManagedReader;
import org.apache.drill.exec.physical.rowSet.impl.RowSetTestUtils;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.store.dfs.DrillFileSystem;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.SubOperatorTest;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileSplit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the columns-array specific behavior in the columns scan framework.
 */
@Category(RowSetTests.class)
public class TestColumnsArrayFramework extends SubOperatorTest {
    private static final Path MOCK_FILE_PATH = new Path("file:/w/x/y/z.csv");

    public static class ColumnsScanOpFixture extends TestFileScanFramework.BaseFileScanOpFixture implements FileReaderCreator {
        protected final List<TestColumnsArrayFramework.DummyColumnsReader> readers = new ArrayList<>();

        protected Iterator<TestColumnsArrayFramework.DummyColumnsReader> readerIter;

        public void addReader(TestColumnsArrayFramework.DummyColumnsReader reader) {
            readers.add(reader);
            files.add(new TestFileScanFramework.DummyFileWork(reader.filePath()));
        }

        @Override
        protected BaseFileScanFramework<?> buildFramework() {
            readerIter = readers.iterator();
            return new org.apache.drill.exec.physical.impl.scan.columns.ColumnsScanFramework(projection, files, fsConfig, this);
        }

        @Override
        public ManagedReader<ColumnsSchemaNegotiator> makeBatchReader(DrillFileSystem dfs, FileSplit split) throws ExecutionSetupException {
            if (!(readerIter.hasNext())) {
                return null;
            }
            return readerIter.next();
        }
    }

    public static class DummyColumnsReader implements ManagedReader<ColumnsSchemaNegotiator> {
        public TupleMetadata schema;

        public ColumnsSchemaNegotiator negotiator;

        int nextCount;

        protected Path filePath = TestColumnsArrayFramework.MOCK_FILE_PATH;

        public DummyColumnsReader(TupleMetadata schema) {
            this.schema = schema;
        }

        public Path filePath() {
            return filePath;
        }

        @Override
        public boolean open(ColumnsSchemaNegotiator negotiator) {
            this.negotiator = negotiator;
            negotiator.setTableSchema(schema, true);
            negotiator.build();
            return true;
        }

        @Override
        public boolean next() {
            return ((nextCount)++) == 0;
        }

        @Override
        public void close() {
        }
    }

    /**
     * Test including a column other than "columns". Occurs when
     * using implicit columns.
     */
    @Test
    public void testNonColumnsProjection() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestColumnsArrayFramework.DummyColumnsReader reader = new TestColumnsArrayFramework.DummyColumnsReader(new SchemaBuilder().add("a", VARCHAR).buildSchema());
        // Create the scan operator
        TestColumnsArrayFramework.ColumnsScanOpFixture scanFixture = new TestColumnsArrayFramework.ColumnsScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Start the one and only reader, and check the columns
        // schema info.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertNotNull(reader.negotiator);
        Assert.assertFalse(reader.negotiator.columnsArrayProjected());
        Assert.assertNull(reader.negotiator.projectedIndexes());
        scanFixture.close();
    }

    /**
     * Test projecting just the `columns` column.
     */
    @Test
    public void testColumnsProjection() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestColumnsArrayFramework.DummyColumnsReader reader = new TestColumnsArrayFramework.DummyColumnsReader(new SchemaBuilder().addArray(COLUMNS_COL, VARCHAR).buildSchema());
        // Create the scan operator
        TestColumnsArrayFramework.ColumnsScanOpFixture scanFixture = new TestColumnsArrayFramework.ColumnsScanOpFixture();
        scanFixture.setProjection(RowSetTestUtils.projectList(COLUMNS_COL));
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Start the one and only reader, and check the columns
        // schema info.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertNotNull(reader.negotiator);
        Assert.assertTrue(reader.negotiator.columnsArrayProjected());
        Assert.assertNull(reader.negotiator.projectedIndexes());
        scanFixture.close();
    }

    /**
     * Test including a specific index of `columns` such as
     * `columns`[1].
     */
    @Test
    public void testColumnsIndexProjection() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestColumnsArrayFramework.DummyColumnsReader reader = new TestColumnsArrayFramework.DummyColumnsReader(new SchemaBuilder().addArray(COLUMNS_COL, VARCHAR).buildSchema());
        // Create the scan operator
        TestColumnsArrayFramework.ColumnsScanOpFixture scanFixture = new TestColumnsArrayFramework.ColumnsScanOpFixture();
        scanFixture.setProjection(Lists.newArrayList(SchemaPath.parseFromString(((ColumnsArrayManager.COLUMNS_COL) + "[1]")), SchemaPath.parseFromString(((ColumnsArrayManager.COLUMNS_COL) + "[3]"))));
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Start the one and only reader, and check the columns
        // schema info.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertNotNull(reader.negotiator);
        Assert.assertTrue(reader.negotiator.columnsArrayProjected());
        boolean[] projIndexes = reader.negotiator.projectedIndexes();
        Assert.assertNotNull(projIndexes);
        Assert.assertEquals(4, projIndexes.length);
        Assert.assertFalse(projIndexes[0]);
        Assert.assertTrue(projIndexes[1]);
        Assert.assertFalse(projIndexes[2]);
        Assert.assertTrue(projIndexes[3]);
        scanFixture.close();
    }
}

