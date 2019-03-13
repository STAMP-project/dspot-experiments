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
import MinorType.INT;
import MinorType.VARCHAR;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.exceptions.ExecutionSetupException;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.common.types.TypeProtos.MinorType;
import org.apache.drill.exec.ops.OperatorContext;
import org.apache.drill.exec.physical.base.AbstractSubScan;
import org.apache.drill.exec.physical.base.Scan;
import org.apache.drill.exec.physical.impl.scan.framework.AbstractScanFramework;
import org.apache.drill.exec.physical.impl.scan.framework.BasicScanFramework;
import org.apache.drill.exec.physical.impl.scan.framework.ManagedReader;
import org.apache.drill.exec.physical.impl.scan.framework.SchemaNegotiator;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.physical.rowSet.impl.RowSetTestUtils;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test of the scan operator framework. Here the focus is on the
 * implementation of the scan operator itself. This operator is
 * based on a number of lower-level abstractions, each of which has
 * its own unit tests. To make this more concrete: review the scan
 * operator code paths. Each path should be exercised by one or more
 * of the tests here. If, however, the code path depends on the
 * details of another, supporting class, then tests for that class
 * appear elsewhere.
 */
// TODO: Schema change in late reader
@Category(RowSetTests.class)
public class TestScanOperatorExec extends SubOperatorTest {
    private static final Logger logger = LoggerFactory.getLogger(TestScanOperatorExec.class);

    /**
     * Base class for the "mock" readers used in this test. The mock readers
     * follow the normal (enhanced) reader API, but instead of actually reading
     * from a data source, they just generate data with a known schema.
     * They also expose internal state such as identifying which methods
     * were actually called.
     */
    private abstract static class BaseMockBatchReader implements ManagedReader<SchemaNegotiator> {
        public boolean openCalled;

        public boolean closeCalled;

        public int startIndex;

        public int batchCount;

        public int batchLimit;

        protected ResultSetLoader tableLoader;

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
    private static class MockLateSchemaReader extends TestScanOperatorExec.BaseMockBatchReader {
        public boolean returnDataOnFirst;

        @Override
        public boolean open(SchemaNegotiator schemaNegotiator) {
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
                    MaterializedField b = new org.apache.drill.exec.record.metadata.ColumnBuilder("b", MinorType.VARCHAR).setMode(OPTIONAL).setWidth(10).build();
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
     * Mock reader that returns no schema and no records.
     */
    private static class MockNullEarlySchemaReader extends TestScanOperatorExec.BaseMockBatchReader {
        @Override
        public boolean open(SchemaNegotiator schemaNegotiator) {
            openCalled = true;
            return false;
        }

        @Override
        public boolean next() {
            return false;
        }
    }

    /**
     * Mock reader that pretends to have a schema at open time
     * like an HBase or JDBC reader.
     */
    private static class MockEarlySchemaReader extends TestScanOperatorExec.BaseMockBatchReader {
        @Override
        public boolean open(SchemaNegotiator schemaNegotiator) {
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

    private static class MockEarlySchemaReader2 extends TestScanOperatorExec.MockEarlySchemaReader {
        @Override
        public boolean open(SchemaNegotiator schemaNegotiator) {
            openCalled = true;
            TupleMetadata schema = new SchemaBuilder().add("a", VARCHAR).addNullable("b", VARCHAR, 10).buildSchema();
            schemaNegotiator.setTableSchema(schema, true);
            schemaNegotiator.build();
            tableLoader = schemaNegotiator.build();
            return true;
        }

        @Override
        protected void writeRow(RowSetLoader writer, int col1, String col2) {
            writer.start();
            if ((writer.column(0)) != null) {
                writer.scalar(0).setString(Integer.toString(col1));
            }
            if ((writer.column(1)) != null) {
                writer.scalar(1).setString(col2);
            }
            writer.save();
        }
    }

    public abstract static class AbstractScanOpFixture {
        private OperatorContext opContext;

        protected List<SchemaPath> projection;

        public ScanOperatorExec scanOp;

        private int batchByteCount;

        private int maxRowCount;

        private MajorType nullType;

        public void projectAll() {
            projection = RowSetTestUtils.projectAll();
        }

        public void projectAllWithMetadata(int dirs) {
            projection = ScanTestUtils.projectAllWithMetadata(dirs);
        }

        public void setProjection(String... projCols) {
            projection = RowSetTestUtils.projectList(projCols);
        }

        public void setProjection(List<SchemaPath> cols) {
            projection = cols;
        }

        public void setMaxBatchByteCount(int byteCount) {
            batchByteCount = byteCount;
        }

        public void setMaxRowCount(int rowCount) {
            maxRowCount = rowCount;
        }

        public void setNullType(MajorType type) {
            nullType = type;
        }

        protected void configure(AbstractScanFramework<?> framework) {
            framework.setMaxBatchByteCount(batchByteCount);
            framework.setMaxRowCount(maxRowCount);
            framework.setNullType(nullType);
        }

        protected ScanOperatorExec buildScanOp(ScanOperatorEvents framework) {
            scanOp = new ScanOperatorExec(framework);
            Scan scanConfig = new AbstractSubScan("bob") {
                @Override
                public int getOperatorType() {
                    return 0;
                }
            };
            opContext = SubOperatorTest.fixture.newOperatorContext(scanConfig);
            scanOp.bind(opContext);
            return scanOp;
        }

        public void close() {
            try {
                scanOp.close();
            } finally {
                opContext.close();
            }
        }
    }

    /**
     * Fixture to handle the boiler-plate needed to set up the components that make
     * up a scan. (In real code, this is all done via the scan batch creator.)
     */
    public static class BasicScanOpFixture extends TestScanOperatorExec.AbstractScanOpFixture {
        public final List<ManagedReader<SchemaNegotiator>> readers = new ArrayList<>();

        public BasicScanFramework framework;

        public void addReader(ManagedReader<SchemaNegotiator> reader) {
            readers.add(reader);
        }

        public ScanOperatorExec build() {
            framework = new BasicScanFramework(projection, readers.iterator());
            configure(framework);
            return buildScanOp(framework);
        }
    }

    /**
     * Most basic test of a reader that discovers its schema as it goes along.
     * The purpose is to validate the most basic life-cycle steps before trying
     * more complex variations.
     */
    @Test
    public void testLateSchemaLifecycle() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestScanOperatorExec.MockLateSchemaReader reader = new TestScanOperatorExec.MockLateSchemaReader();
        reader.batchLimit = 2;
        reader.returnDataOnFirst = false;
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Standard startup
        Assert.assertFalse(reader.openCalled);
        // First batch: build schema. The reader does not help: it returns an
        // empty first batch.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertTrue(reader.openCalled);
        Assert.assertEquals(1, reader.batchCount);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        // Create the expected result.
        RowSet.SingleRowSet expected = makeExpected(20);
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
     * Test the case that a late scan operator is closed before
     * the first reader is opened.
     */
    @Test
    public void testLateSchemaEarlyClose() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestScanOperatorExec.MockLateSchemaReader reader = new TestScanOperatorExec.MockLateSchemaReader();
        reader.batchLimit = 2;
        reader.returnDataOnFirst = false;
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        scanFixture.build();
        // Reader never opened.
        scanFixture.close();
        Assert.assertFalse(reader.openCalled);
        Assert.assertEquals(0, reader.batchCount);
        Assert.assertFalse(reader.closeCalled);
    }

    /**
     * Test the case that a late schema reader is closed after discovering
     * schema, before any calls to next().
     */
    @Test
    public void testLateSchemaEarlyReaderClose() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestScanOperatorExec.MockLateSchemaReader reader = new TestScanOperatorExec.MockLateSchemaReader();
        reader.batchLimit = 2;
        reader.returnDataOnFirst = false;
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Get the schema as above.
        Assert.assertTrue(scan.buildSchema());
        // No lookahead batch created.
        scanFixture.close();
        Assert.assertEquals(1, reader.batchCount);
        Assert.assertTrue(reader.closeCalled);
    }

    /**
     * Test the case that a late schema reader is closed before
     * consuming the look-ahead batch used to infer schema.
     */
    @Test
    public void testLateSchemaEarlyCloseWithData() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestScanOperatorExec.MockLateSchemaReader reader = new TestScanOperatorExec.MockLateSchemaReader();
        reader.batchLimit = 2;
        reader.returnDataOnFirst = true;
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Get the schema as above.
        Assert.assertTrue(scan.buildSchema());
        // Lookahead batch created.
        scanFixture.close();
        Assert.assertEquals(1, reader.batchCount);
        Assert.assertTrue(reader.closeCalled);
    }

    /**
     * Pathological case that a scan operator is provided no readers.
     * It will throw a user exception because the downstream operators
     * can't handle this case so we choose to stop the show early to
     * avoid getting into a strange state.
     */
    @Test
    public void testNoReader() {
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        ScanOperatorExec scan = scanFixture.build();
        try {
            scan.buildSchema();
        } catch (UserException e) {
            // Expected
            Assert.assertTrue(((e.getCause()) instanceof ExecutionSetupException));
        }
        // Must close the DAG (context and scan operator) even on failures
        scanFixture.close();
    }

    /**
     * Test a late-schema source that has no file information.
     * (Like a Hive or JDBC data source.)
     */
    @Test
    public void testLateSchemaLifecycleNoFile() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestScanOperatorExec.MockLateSchemaReader reader = new TestScanOperatorExec.MockLateSchemaReader();
        reader.batchLimit = 2;
        reader.returnDataOnFirst = false;
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
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
        RowSet.SingleRowSet expected = makeExpected(20);
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

    @Test
    public void testLateSchemaNoData() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestScanOperatorExec.MockLateSchemaReader reader = new TestScanOperatorExec.MockLateSchemaReader();
        reader.batchLimit = 0;
        reader.returnDataOnFirst = false;
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Standard startup
        Assert.assertFalse(reader.openCalled);
        // First batch: EOF.
        Assert.assertFalse(scan.buildSchema());
        Assert.assertTrue(reader.openCalled);
        Assert.assertTrue(reader.closeCalled);
        scanFixture.close();
    }

    @Test
    public void testLateSchemaDataOnFirst() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestScanOperatorExec.MockLateSchemaReader reader = new TestScanOperatorExec.MockLateSchemaReader();
        reader.batchLimit = 1;
        reader.returnDataOnFirst = true;
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
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
        RowSet.SingleRowSet expected = makeExpected();
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

    @Test
    public void testEarlySchemaLifecycle() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestScanOperatorExec.MockEarlySchemaReader reader = new TestScanOperatorExec.MockEarlySchemaReader();
        reader.batchLimit = 1;
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        RowSet.SingleRowSet expected = makeExpected();
        RowSetComparison verifier = new RowSetComparison(expected);
        // First batch: return schema.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(0, reader.batchCount);
        Assert.assertEquals(expected.batchSchema(), scan.batchAccessor().getSchema());
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        // Next call, return with data.
        Assert.assertTrue(scan.next());
        verifier.verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        // Next again: no-op
        Assert.assertFalse(scan.next());
        scanFixture.close();
        // Close again: no-op
        scan.close();
    }

    private static class MockEarlySchemaReader3 extends TestScanOperatorExec.MockEarlySchemaReader {
        @Override
        public boolean next() {
            if ((batchCount) >= (batchLimit)) {
                return false;
            }
            (batchCount)++;
            makeBatch();
            return (batchCount) < (batchLimit);
        }
    }

    @Test
    public void testEarlySchemaDataWithEof() {
        // Create a mock reader, return two batches: one schema-only, another with data.
        TestScanOperatorExec.MockEarlySchemaReader3 reader = new TestScanOperatorExec.MockEarlySchemaReader3();
        reader.batchLimit = 1;
        // Create the scan operator
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        RowSet.SingleRowSet expected = makeExpected();
        RowSetComparison verifier = new RowSetComparison(expected);
        // First batch: return schema.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        // Next call, return with data.
        Assert.assertTrue(scan.next());
        verifier.verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        // Next again: no-op
        Assert.assertFalse(scan.next());
        scanFixture.close();
        // Close again: no-op
        scan.close();
    }

    /**
     * Test the case where the reader does not play the "first batch contains
     * only schema" game, and instead returns data. The Scan operator will
     * split the first batch into two: one with schema only, another with
     * data.
     */
    @Test
    public void testNonEmptyFirstBatch() {
        RowSet.SingleRowSet expected = makeExpected();
        TestScanOperatorExec.MockLateSchemaReader reader = new TestScanOperatorExec.MockLateSchemaReader();
        reader.batchLimit = 2;
        reader.returnDataOnFirst = true;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // First batch. The reader returns a non-empty batch. The scan
        // operator strips off the schema and returns just that.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(1, reader.batchCount);
        Assert.assertEquals(expected.batchSchema(), scan.batchAccessor().getSchema());
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scan.batchAccessor().release();
        // Second batch. Returns the "look-ahead" batch returned by
        // the reader earlier.
        Assert.assertTrue(scan.next());
        Assert.assertEquals(1, reader.batchCount);
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // Third batch, normal case.
        Assert.assertTrue(scan.next());
        Assert.assertEquals(2, reader.batchCount);
        new RowSetComparison(makeExpected(20)).verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertTrue(reader.closeCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    /**
     * Test EOF on the first batch. Is allowed, but will result in the scan operator
     * passing a null batch to the parent.
     */
    @Test
    public void testEOFOnSchema() {
        TestScanOperatorExec.MockNullEarlySchemaReader reader = new TestScanOperatorExec.MockNullEarlySchemaReader();
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // EOF
        Assert.assertFalse(scan.buildSchema());
        Assert.assertTrue(reader.closeCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    @Test
    public void testEOFOnFirstBatch() {
        TestScanOperatorExec.MockEarlySchemaReader reader = new TestScanOperatorExec.MockEarlySchemaReader();
        reader.batchLimit = 0;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        Assert.assertTrue(scan.buildSchema());
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertTrue(reader.closeCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    /**
     * Test normal case with multiple readers. These return
     * the same schema, so no schema change.
     */
    @Test
    public void testMultipleReaders() {
        TestScanOperatorExec.MockNullEarlySchemaReader nullReader = new TestScanOperatorExec.MockNullEarlySchemaReader();
        TestScanOperatorExec.MockEarlySchemaReader reader1 = new TestScanOperatorExec.MockEarlySchemaReader();
        reader1.batchLimit = 2;
        TestScanOperatorExec.MockEarlySchemaReader reader2 = new TestScanOperatorExec.MockEarlySchemaReader();
        reader2.batchLimit = 2;
        reader2.startIndex = 100;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(nullReader);
        scanFixture.addReader(reader1);
        scanFixture.addReader(reader2);
        ScanOperatorExec scan = scanFixture.build();
        // First batch, schema only.
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        scan.batchAccessor().release();
        // Second batch.
        Assert.assertTrue(scan.next());
        Assert.assertEquals(1, reader1.batchCount);
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        verifyBatch(0, scan.batchAccessor().getOutgoingContainer());
        // Third batch.
        Assert.assertTrue(scan.next());
        Assert.assertEquals(2, reader1.batchCount);
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        verifyBatch(20, scan.batchAccessor().getOutgoingContainer());
        // Second reader. First batch includes data, no special first-batch
        // handling for the second reader.
        Assert.assertFalse(reader1.closeCalled);
        Assert.assertFalse(reader2.openCalled);
        Assert.assertTrue(scan.next());
        Assert.assertTrue(reader1.closeCalled);
        Assert.assertTrue(reader2.openCalled);
        Assert.assertEquals(1, reader2.batchCount);
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        verifyBatch(100, scan.batchAccessor().getOutgoingContainer());
        // Second batch from second reader.
        Assert.assertTrue(scan.next());
        Assert.assertEquals(2, reader2.batchCount);
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        verifyBatch(120, scan.batchAccessor().getOutgoingContainer());
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertTrue(reader2.closeCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    /**
     * Multiple readers with a schema change between them.
     */
    @Test
    public void testSchemaChange() {
        TestScanOperatorExec.MockEarlySchemaReader reader1 = new TestScanOperatorExec.MockEarlySchemaReader();
        reader1.batchLimit = 2;
        TestScanOperatorExec.MockEarlySchemaReader reader2 = new TestScanOperatorExec.MockEarlySchemaReader2();
        reader2.batchLimit = 2;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader1);
        scanFixture.addReader(reader2);
        ScanOperatorExec scan = scanFixture.build();
        // Build schema
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        scan.batchAccessor().release();
        // First batch
        Assert.assertTrue(scan.next());
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        scan.batchAccessor().release();
        // Second batch
        Assert.assertTrue(scan.next());
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        scan.batchAccessor().release();
        // Second reader.
        BatchSchema expectedSchema2 = new SchemaBuilder().add("a", VARCHAR).addNullable("b", VARCHAR, 10).build();
        Assert.assertTrue(scan.next());
        Assert.assertEquals(2, scan.batchAccessor().schemaVersion());
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema2).addRow("10", "fred").addRow("20", "wilma").build();
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // Second batch from second reader.
        Assert.assertTrue(scan.next());
        Assert.assertEquals(2, scan.batchAccessor().schemaVersion());
        expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema2).addRow("30", "fred").addRow("40", "wilma").build();
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // EOF
        Assert.assertFalse(scan.next());
        Assert.assertTrue(reader2.closeCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    /**
     * Test multiple readers, all EOF on first batch.
     */
    @Test
    public void testMultiEOFOnFirstBatch() {
        TestScanOperatorExec.MockEarlySchemaReader reader1 = new TestScanOperatorExec.MockEarlySchemaReader();
        reader1.batchLimit = 0;
        TestScanOperatorExec.MockEarlySchemaReader reader2 = new TestScanOperatorExec.MockEarlySchemaReader();
        reader2.batchLimit = 0;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader1);
        scanFixture.addReader(reader2);
        ScanOperatorExec scan = scanFixture.build();
        // EOF
        Assert.assertTrue(scan.buildSchema());
        Assert.assertFalse(scan.next());
        Assert.assertTrue(reader1.closeCalled);
        Assert.assertTrue(reader2.closeCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
    }

    public final String ERROR_MSG = "My Bad!";

    @Test
    public void testExceptionOnOpen() {
        // Reader which fails on open with a known error message
        // using an exception other than UserException.
        TestScanOperatorExec.MockEarlySchemaReader reader = new TestScanOperatorExec.MockEarlySchemaReader() {
            @Override
            public boolean open(SchemaNegotiator schemaNegotiator) {
                openCalled = true;
                throw new IllegalStateException(ERROR_MSG);
            }
        };
        reader.batchLimit = 0;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        try {
            scan.buildSchema();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(ERROR_MSG));
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        }
        Assert.assertTrue(reader.openCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
        Assert.assertTrue(reader.closeCalled);
    }

    @Test
    public void testUserExceptionOnOpen() {
        // Reader which fails on open with a known error message
        // using a UserException.
        TestScanOperatorExec.MockEarlySchemaReader reader = new TestScanOperatorExec.MockEarlySchemaReader() {
            @Override
            public boolean open(SchemaNegotiator schemaNegotiator) {
                openCalled = true;
                throw UserException.dataReadError().addContext(ERROR_MSG).build(TestScanOperatorExec.logger);
            }
        };
        reader.batchLimit = 2;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        try {
            scan.buildSchema();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(ERROR_MSG));
            Assert.assertNull(e.getCause());
        }
        Assert.assertTrue(reader.openCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
        Assert.assertTrue(reader.closeCalled);
    }

    @Test
    public void testExceptionOnFirstNext() {
        TestScanOperatorExec.MockEarlySchemaReader reader = new TestScanOperatorExec.MockEarlySchemaReader() {
            @Override
            public boolean next() {
                super.next();// Load some data

                throw new IllegalStateException(ERROR_MSG);
            }
        };
        reader.batchLimit = 2;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        Assert.assertTrue(scan.buildSchema());
        try {
            scan.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(ERROR_MSG));
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        }
        Assert.assertTrue(reader.openCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
        Assert.assertTrue(reader.closeCalled);
    }

    @Test
    public void testUserExceptionOnFirstNext() {
        TestScanOperatorExec.MockEarlySchemaReader reader = new TestScanOperatorExec.MockEarlySchemaReader() {
            @Override
            public boolean next() {
                super.next();// Load some data

                throw UserException.dataReadError().addContext(ERROR_MSG).build(TestScanOperatorExec.logger);
            }
        };
        reader.batchLimit = 2;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        Assert.assertTrue(scan.buildSchema());
        // EOF
        try {
            scan.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(ERROR_MSG));
            Assert.assertNull(e.getCause());
        }
        Assert.assertTrue(reader.openCalled);
        Assert.assertEquals(0, scan.batchAccessor().getRowCount());
        scanFixture.close();
        Assert.assertTrue(reader.closeCalled);
    }

    /**
     * Test throwing an exception after the first batch, but while
     * "reading" the second. Note that the first batch returns data
     * and is spread over two next() calls, so the error is on the
     * third call to the scan operator next().
     */
    @Test
    public void testExceptionOnSecondNext() {
        TestScanOperatorExec.MockEarlySchemaReader reader = new TestScanOperatorExec.MockEarlySchemaReader() {
            @Override
            public boolean next() {
                if ((batchCount) == 1) {
                    super.next();// Load some data

                    throw new IllegalStateException(ERROR_MSG);
                }
                return super.next();
            }
        };
        reader.batchLimit = 2;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Schema
        Assert.assertTrue(scan.buildSchema());
        // First batch
        Assert.assertTrue(scan.next());
        scan.batchAccessor().release();
        // Fail
        try {
            scan.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(ERROR_MSG));
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        }
        scanFixture.close();
        Assert.assertTrue(reader.closeCalled);
    }

    @Test
    public void testUserExceptionOnSecondNext() {
        TestScanOperatorExec.MockEarlySchemaReader reader = new TestScanOperatorExec.MockEarlySchemaReader() {
            @Override
            public boolean next() {
                if ((batchCount) == 1) {
                    super.next();// Load some data

                    throw UserException.dataReadError().addContext(ERROR_MSG).build(TestScanOperatorExec.logger);
                }
                return super.next();
            }
        };
        reader.batchLimit = 2;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader);
        ScanOperatorExec scan = scanFixture.build();
        // Schema
        Assert.assertTrue(scan.buildSchema());
        // First batch
        Assert.assertTrue(scan.next());
        scan.batchAccessor().release();
        // Fail
        try {
            scan.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(ERROR_MSG));
            Assert.assertNull(e.getCause());
        }
        scanFixture.close();
        Assert.assertTrue(reader.closeCalled);
    }

    @Test
    public void testExceptionOnClose() {
        TestScanOperatorExec.MockEarlySchemaReader reader1 = new TestScanOperatorExec.MockEarlySchemaReader() {
            @Override
            public void close() {
                super.close();
                throw new IllegalStateException(ERROR_MSG);
            }
        };
        reader1.batchLimit = 2;
        TestScanOperatorExec.MockEarlySchemaReader reader2 = new TestScanOperatorExec.MockEarlySchemaReader();
        reader2.batchLimit = 2;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader1);
        scanFixture.addReader(reader2);
        ScanOperatorExec scan = scanFixture.build();
        Assert.assertTrue(scan.buildSchema());
        Assert.assertTrue(scan.next());
        scan.batchAccessor().release();
        Assert.assertTrue(scan.next());
        scan.batchAccessor().release();
        // Fail on close of first reader
        try {
            scan.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(ERROR_MSG));
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        }
        Assert.assertTrue(reader1.closeCalled);
        Assert.assertFalse(reader2.openCalled);
        scanFixture.close();
    }

    @Test
    public void testUserExceptionOnClose() {
        TestScanOperatorExec.MockEarlySchemaReader reader1 = new TestScanOperatorExec.MockEarlySchemaReader() {
            @Override
            public void close() {
                super.close();
                throw UserException.dataReadError().addContext(ERROR_MSG).build(TestScanOperatorExec.logger);
            }
        };
        reader1.batchLimit = 2;
        TestScanOperatorExec.MockEarlySchemaReader reader2 = new TestScanOperatorExec.MockEarlySchemaReader();
        reader2.batchLimit = 2;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.projectAll();
        scanFixture.addReader(reader1);
        scanFixture.addReader(reader2);
        ScanOperatorExec scan = scanFixture.build();
        Assert.assertTrue(scan.buildSchema());
        Assert.assertTrue(scan.next());
        scan.batchAccessor().release();
        Assert.assertTrue(scan.next());
        scan.batchAccessor().release();
        // Fail on close of first reader
        try {
            scan.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(ERROR_MSG));
            Assert.assertNull(e.getCause());
        }
        Assert.assertTrue(reader1.closeCalled);
        Assert.assertFalse(reader2.openCalled);
        scanFixture.close();
    }

    /**
     * Mock reader that produces "jumbo" batches that cause a vector to
     * fill and a row to overflow from one batch to the next.
     */
    private static class OverflowReader extends TestScanOperatorExec.BaseMockBatchReader {
        private final String value;

        public int rowCount;

        /**
         * If true, the reader will report EOF after filling a batch
         * to overflow. This simulates the corner case in which a reader
         * has, say, 1000 rows, hits overflow on row 1000, then declares
         * it has nothing more to read.
         * <p>
         * If false, reports EOF on a call to next() without reading more
         * rows. The overlow row from the prior batch still exists in
         * the result set loader.
         */
        public boolean reportEofWithOverflow;

        public OverflowReader() {
            char[] buf = new char[512];
            Arrays.fill(buf, 'x');
            value = new String(buf);
        }

        @Override
        public boolean open(SchemaNegotiator schemaNegotiator) {
            openCalled = true;
            TupleMetadata schema = new SchemaBuilder().add("a", VARCHAR).buildSchema();
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
            RowSetLoader writer = tableLoader.writer();
            while (!(writer.isFull())) {
                writer.start();
                writer.scalar(0).setString(value);
                writer.save();
                (rowCount)++;
            } 
            // The vector overflowed on the last row. But, we still had to write the row.
            // The row is tucked away in the loader to appear as the first row in
            // the next batch.
            // 
            // Depending on the flag set by the test routine, either report the EOF
            // during this read, or report it next time around.
            return reportEofWithOverflow ? (batchCount) < (batchLimit) : true;
        }
    }

    /**
     * Test multiple readers, with one of them creating "jumbo" batches
     * that overflow. Specifically, test a corner case. A batch ends right
     * at file EOF, but that last batch overflowed.
     */
    @Test
    public void testMultipleReadersWithOverflow() {
        runOverflowTest(false);
        runOverflowTest(true);
    }

    private static class MockOneColEarlySchemaReader extends TestScanOperatorExec.BaseMockBatchReader {
        @Override
        public boolean open(SchemaNegotiator schemaNegotiator) {
            openCalled = true;
            TupleMetadata schema = new SchemaBuilder().add("a", INT).buildSchema();
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

        @Override
        protected void writeRow(RowSetLoader writer, int col1, String col2) {
            writer.start();
            if ((writer.column(0)) != null) {
                writer.scalar(0).setInt((col1 + 1));
            }
            writer.save();
        }
    }

    /**
     * Test the ability of the scan operator to "smooth" out schema changes
     * by reusing the type from a previous reader, if known. That is,
     * given three readers:<br>
     * (a, b)<br>
     * (b)<br>
     * (a, b)<br>
     * Then the type of column a should be preserved for the second reader that
     * does not include a. This works if a is nullable. If so, a's type will
     * be used for the empty column, rather than the usual nullable int.
     * <p>
     * Full testing of smoothing is done in
     * {#link TestScanProjector}. Here we just make sure that the
     * smoothing logic is available via the scan operator.
     */
    @Test
    public void testSchemaSmoothing() {
        // Reader returns (a, b)
        TestScanOperatorExec.MockEarlySchemaReader reader1 = new TestScanOperatorExec.MockEarlySchemaReader();
        reader1.batchLimit = 1;
        // Reader returns (a)
        TestScanOperatorExec.MockOneColEarlySchemaReader reader2 = new TestScanOperatorExec.MockOneColEarlySchemaReader();
        reader2.batchLimit = 1;
        reader2.startIndex = 100;
        // Reader returns (a, b)
        TestScanOperatorExec.MockEarlySchemaReader reader3 = new TestScanOperatorExec.MockEarlySchemaReader();
        reader3.batchLimit = 1;
        reader3.startIndex = 200;
        TestScanOperatorExec.BasicScanOpFixture scanFixture = new TestScanOperatorExec.BasicScanOpFixture();
        scanFixture.setProjection(new String[]{ "a", "b" });
        scanFixture.addReader(reader1);
        scanFixture.addReader(reader2);
        scanFixture.addReader(reader3);
        ScanOperatorExec scan = scanFixture.build();
        // Schema based on (a, b)
        Assert.assertTrue(scan.buildSchema());
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        scan.batchAccessor().release();
        // Batch from (a, b) reader 1
        Assert.assertTrue(scan.next());
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        verifyBatch(0, scan.batchAccessor().getOutgoingContainer());
        // Batch from (a) reader 2
        // Due to schema smoothing, b vector type is left unchanged,
        // but is null filled.
        Assert.assertTrue(scan.next());
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(scan.batchAccessor().getSchema()).addRow(111, null).addRow(121, null).build();
        new RowSetComparison(expected).verifyAndClearAll(SubOperatorTest.fixture.wrap(scan.batchAccessor().getOutgoingContainer()));
        // Batch from (a, b) reader 3
        // Recycles b again, back to being a table column.
        Assert.assertTrue(scan.next());
        Assert.assertEquals(1, scan.batchAccessor().schemaVersion());
        verifyBatch(200, scan.batchAccessor().getOutgoingContainer());
        Assert.assertFalse(scan.next());
        scanFixture.close();
    }
}

