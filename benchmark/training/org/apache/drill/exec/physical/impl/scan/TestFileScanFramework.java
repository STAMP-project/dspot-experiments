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


import DataMode.REQUIRED;
import MinorType.INT;
import MinorType.VARCHAR;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.exceptions.ExecutionSetupException;
import org.apache.drill.exec.physical.impl.scan.file.BaseFileScanFramework;
import org.apache.drill.exec.physical.impl.scan.file.BaseFileScanFramework.FileSchemaNegotiator;
import org.apache.drill.exec.physical.impl.scan.file.FileScanFramework.FileReaderFactory;
import org.apache.drill.exec.physical.impl.scan.framework.ManagedReader;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.store.dfs.DrillFileSystem;
import org.apache.drill.exec.store.dfs.easy.FileWork;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileSplit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.apache.drill.common.types.TypeProtos.MinorType.VARCHAR;


/**
 * Tests the file metadata extensions to the file operator framework.
 * Focuses on the file metadata itself, assumes that other tests have
 * verified the underlying mechanisms.
 */
@Category(RowSetTests.class)
public class TestFileScanFramework extends SubOperatorTest {
    private static final String MOCK_FILE_NAME = "foo.csv";

    private static final String MOCK_FILE_PATH = "/w/x/y";

    private static final String MOCK_FILE_FQN = ((TestFileScanFramework.MOCK_FILE_PATH) + "/") + (TestFileScanFramework.MOCK_FILE_NAME);

    private static final String MOCK_FILE_SYSTEM_NAME = "file:" + (TestFileScanFramework.MOCK_FILE_FQN);

    private static final Path MOCK_ROOT_PATH = new Path("file:/w");

    private static final String MOCK_SUFFIX = "csv";

    private static final String MOCK_DIR0 = "x";

    private static final String MOCK_DIR1 = "y";

    /**
     * For schema-based testing, we only need the file path
     * from the file work
     */
    public static class DummyFileWork implements FileWork {
        private final Path path;

        public DummyFileWork(Path path) {
            this.path = path;
        }

        @Override
        public Path getPath() {
            return path;
        }

        @Override
        public long getStart() {
            return 0;
        }

        @Override
        public long getLength() {
            return 0;
        }
    }

    /**
     * Fixture class to assemble all the knick-knacks that make up a
     * file scan framework. The parts have a form unique to testing
     * since we are not actually doing real scans.
     */
    public abstract static class BaseFileScanOpFixture extends TestScanOperatorExec.AbstractScanOpFixture {
        protected Path selectionRoot = TestFileScanFramework.MOCK_ROOT_PATH;

        protected int partitionDepth = 3;

        protected List<FileWork> files = new ArrayList<>();

        protected Configuration fsConfig = new Configuration();

        public ScanOperatorExec build() {
            BaseFileScanFramework<?> framework = buildFramework();
            configure(framework);
            configureFileScan(framework);
            return buildScanOp(framework);
        }

        protected abstract BaseFileScanFramework<?> buildFramework();

        private void configureFileScan(BaseFileScanFramework<?> framework) {
            framework.setSelectionRoot(selectionRoot, partitionDepth);
        }
    }

    public static class FileScanOpFixture extends TestFileScanFramework.BaseFileScanOpFixture implements FileReaderFactory {
        protected final List<TestFileScanFramework.MockFileReader> readers = new ArrayList<>();

        protected Iterator<TestFileScanFramework.MockFileReader> readerIter;

        public void addReader(TestFileScanFramework.MockFileReader reader) {
            readers.add(reader);
            files.add(new TestFileScanFramework.DummyFileWork(reader.filePath()));
        }

        @Override
        protected BaseFileScanFramework<?> buildFramework() {
            readerIter = readers.iterator();
            return new org.apache.drill.exec.physical.impl.scan.file.FileScanFramework(projection, files, fsConfig, this);
        }

        @Override
        public ManagedReader<FileSchemaNegotiator> makeBatchReader(DrillFileSystem dfs, FileSplit split) throws ExecutionSetupException {
            if (!(readerIter.hasNext())) {
                return null;
            }
            return readerIter.next();
        }
    }

    private interface MockFileReader extends ManagedReader<FileSchemaNegotiator> {
        Path filePath();
    }

    /**
     * Base class for the "mock" readers used in this test. The mock readers
     * follow the normal (enhanced) reader API, but instead of actually reading
     * from a data source, they just generate data with a known schema.
     * They also expose internal state such as identifying which methods
     * were actually called.
     */
    private abstract static class BaseMockBatchReader implements TestFileScanFramework.MockFileReader {
        public boolean openCalled;

        public boolean closeCalled;

        public int startIndex;

        public int batchCount;

        public int batchLimit;

        protected ResultSetLoader tableLoader;

        protected Path filePath = new Path(TestFileScanFramework.MOCK_FILE_SYSTEM_NAME);

        @Override
        public Path filePath() {
            return filePath;
        }

        protected void makeBatch() {
            RowSetLoader writer = tableLoader.writer();
            int offset = (((batchCount) - 1) * 20) + (startIndex);
            writeRow(writer, (offset + 10), "fred");
            writeRow(writer, (offset + 20), "wilma");
        }

        protected void writeRow(RowSetLoader writer, int col1, String col2) {
            writer.start();
            if ((writer.column(0)) != null) {
                writer.scalar(0).setInt(col1);
            }
            if ((writer.column(1)) != null) {
                writer.scalar(1).setString(col2);
            }
            writer.save();
        }

        @Override
        public void close() {
            closeCalled = true;
        }
    }

    /**
     * "Late schema" reader, meaning that the reader does not know the schema on
     * open, but must "discover" it when reading data.
     */
    private static class MockLateSchemaReader extends TestFileScanFramework.BaseMockBatchReader {
        public boolean returnDataOnFirst;

        @Override
        public boolean open(FileSchemaNegotiator schemaNegotiator) {
            // No schema or file, just build the table loader.
            tableLoader = schemaNegotiator.build();
            openCalled = true;
            return true;
        }

        @Override
        public boolean next() {
            (batchCount)++;
            if ((batchCount) > (batchLimit)) {
                return false;
            } else
                if ((batchCount) == 1) {
                    // On first batch, pretend to discover the schema.
                    RowSetLoader rowSet = tableLoader.writer();
                    MaterializedField a = SchemaBuilder.columnSchema("a", INT, REQUIRED);
                    rowSet.addColumn(a);
                    MaterializedField b = new org.apache.drill.exec.record.metadata.ColumnBuilder("b", VARCHAR).setMode(DataMode.OPTIONAL).setWidth(10).build();
                    rowSet.addColumn(b);
                    if (!(returnDataOnFirst)) {
                        return true;
                    }
                }

            makeBatch();
            return true;
        }
    }

    /**
     * Mock reader with an early schema: the schema is known before the first
     * record. Think Parquet or JDBC.
     */
    private static class MockEarlySchemaReader extends TestFileScanFramework.BaseMockBatchReader {
        @Override
        public boolean open(FileSchemaNegotiator schemaNegotiator) {
            openCalled = true;
            TupleMetadata schema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR, 10).buildSchema();
            schemaNegotiator.setTableSchema(schema, true);
            tableLoader = schemaNegotiator.build();
            return true;
        }

        @Override
        public boolean next() {
            (batchCount)++;
            if ((batchCount) > (batchLimit)) {
                return false;
            }
            makeBatch();
            return true;
        }
    }

    @Test
    public void testLateSchemaFileWildcards() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestFileScanFramework.MockLateSchemaReader reader = new TestFileScanFramework.MockLateSchemaReader();
        reader.batchLimit = 2;
        reader.returnDataOnFirst = false;
        // Create the scan operator
        TestFileScanFramework.FileScanOpFixture scanFixture = new TestFileScanFramework.FileScanOpFixture();
        scanFixture.projectAllWithMetadata(2);
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Standard startup
        Assert.assertFalse(reader.openCalled);
        // First batch: build schema. The reader helps: it returns an
        // empty first batch.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertTrue(reader.openCalled);
        Assert.assertEquals(1, reader.batchCount);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        // Create the expected result.
        TupleMetadata expectedSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR, 10).add(ScanTestUtils.FULLY_QUALIFIED_NAME_COL, VARCHAR).add(ScanTestUtils.FILE_PATH_COL, VARCHAR).add(ScanTestUtils.FILE_NAME_COL, VARCHAR).add(ScanTestUtils.SUFFIX_COL, VARCHAR).addNullable(ScanTestUtils.partitionColName(0), VARCHAR).addNullable(ScanTestUtils.partitionColName(1), VARCHAR).addNullable(ScanTestUtils.partitionColName(2), VARCHAR).buildSchema();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(30, "fred", TestFileScanFramework.MOCK_FILE_FQN, TestFileScanFramework.MOCK_FILE_PATH, TestFileScanFramework.MOCK_FILE_NAME, TestFileScanFramework.MOCK_SUFFIX, TestFileScanFramework.MOCK_DIR0, TestFileScanFramework.MOCK_DIR1, null).addRow(40, "wilma", TestFileScanFramework.MOCK_FILE_FQN, TestFileScanFramework.MOCK_FILE_PATH, TestFileScanFramework.MOCK_FILE_NAME, TestFileScanFramework.MOCK_SUFFIX, TestFileScanFramework.MOCK_DIR0, TestFileScanFramework.MOCK_DIR1, null).build();
        RowSetComparison verifier = new RowSetComparison(expected);
        Assert.assertEquals(expected.batchSchema(), scan.batchAccessor().getSchema());
        // Next call, return with data.
        Assert.assertTrue(scan.next());
        verifier.verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertTrue(reader.closeCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    /**
     * Basic sanity test of a couple of implicit columns, along
     * with all table columns in table order. Full testing of implicit
     * columns is done on lower-level components.
     */
    @Test
    public void testMetadataColumns() {
        TestFileScanFramework.MockEarlySchemaReader reader = new TestFileScanFramework.MockEarlySchemaReader();
        reader.batchLimit = 1;
        // Select table and implicit columns.
        TestFileScanFramework.FileScanOpFixture scanFixture = new TestFileScanFramework.FileScanOpFixture();
        scanFixture.setProjection(new String[]{ "a", "b", "filename", "suffix" });
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Expect data and implicit columns
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).addNullable("b", VARCHAR, 10).add("filename", VARCHAR).add("suffix", VARCHAR).build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(10, "fred", TestFileScanFramework.MOCK_FILE_NAME, TestFileScanFramework.MOCK_SUFFIX).addRow(20, "wilma", TestFileScanFramework.MOCK_FILE_NAME, TestFileScanFramework.MOCK_SUFFIX).build();
        RowSetComparison verifier = new RowSetComparison(expected);
        // Schema should include implicit columns.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(expectedSchema, scan.batchAccessor().getSchema());
        scan.batchAccessor().release();
        // Read one batch, should contain implicit columns
        Assert.assertTrue(scan.next());
        verifier.verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    /**
     * Exercise the major project operations: subset of table
     * columns, implicit, partition, missing columns, and output
     * order (and positions) different than table. These cases
     * are more fully test on lower level components; here we verify
     * that the components are wired up correctly.
     */
    @Test
    public void testFullProject() {
        TestFileScanFramework.MockEarlySchemaReader reader = new TestFileScanFramework.MockEarlySchemaReader();
        reader.batchLimit = 1;
        // Select table and implicit columns.
        TestFileScanFramework.FileScanOpFixture scanFixture = new TestFileScanFramework.FileScanOpFixture();
        scanFixture.setProjection(new String[]{ "dir0", "b", "filename", "c", "suffix" });
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Expect data and implicit columns
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("dir0", VARCHAR).addNullable("b", VARCHAR, 10).add("filename", VARCHAR).addNullable("c", INT).add("suffix", VARCHAR).build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(TestFileScanFramework.MOCK_DIR0, "fred", TestFileScanFramework.MOCK_FILE_NAME, null, TestFileScanFramework.MOCK_SUFFIX).addRow(TestFileScanFramework.MOCK_DIR0, "wilma", TestFileScanFramework.MOCK_FILE_NAME, null, TestFileScanFramework.MOCK_SUFFIX).build();
        RowSetComparison verifier = new RowSetComparison(expected);
        // Schema should include implicit columns.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(expectedSchema, scan.batchAccessor().getSchema());
        scan.batchAccessor().release();
        // Read one batch, should contain implicit columns
        Assert.assertTrue(scan.next());
        verifier.verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    @Test
    public void testEmptyProject() {
        TestFileScanFramework.MockEarlySchemaReader reader = new TestFileScanFramework.MockEarlySchemaReader();
        reader.batchLimit = 1;
        // Select no columns
        TestFileScanFramework.FileScanOpFixture scanFixture = new TestFileScanFramework.FileScanOpFixture();
        scanFixture.setProjection(new String[]{  });
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Expect data and implicit columns
        BatchSchema expectedSchema = new SchemaBuilder().build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow().addRow().build();
        RowSetComparison verifier = new RowSetComparison(expected);
        // Schema should include implicit columns.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(expectedSchema, scan.batchAccessor().getSchema());
        scan.batchAccessor().release();
        // Read one batch, should contain implicit columns
        Assert.assertTrue(scan.next());
        verifier.verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    private static class MockMapReader extends TestFileScanFramework.BaseMockBatchReader {
        @Override
        public boolean open(FileSchemaNegotiator schemaNegotiator) {
            TupleMetadata schema = new SchemaBuilder().addMap("m1").add("a", INT).add("b", INT).resumeSchema().buildSchema();
            schemaNegotiator.setTableSchema(schema, true);
            tableLoader = schemaNegotiator.build();
            return true;
        }

        @Override
        public boolean next() {
            (batchCount)++;
            if ((batchCount) > (batchLimit)) {
                return false;
            }
            tableLoader.writer().addRow(new Object[]{ new Object[]{ 10, 11 } }).addRow(new Object[]{ new Object[]{ 20, 21 } });
            return true;
        }
    }

    @Test
    public void testMapProject() {
        TestFileScanFramework.MockMapReader reader = new TestFileScanFramework.MockMapReader();
        reader.batchLimit = 1;
        // Select one of the two map columns
        TestFileScanFramework.FileScanOpFixture scanFixture = new TestFileScanFramework.FileScanOpFixture();
        scanFixture.setProjection(new String[]{ "m1.a" });
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Expect data and implicit columns
        BatchSchema expectedSchema = new SchemaBuilder().addMap("m1").add("a", INT).resumeSchema().build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addSingleCol(new Object[]{ 10 }).addSingleCol(new Object[]{ 20 }).build();
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(expectedSchema, scan.batchAccessor().getSchema());
        scan.batchAccessor().release();
        Assert.assertTrue(scan.next());
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }
}

