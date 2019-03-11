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
package org.apache.drill.exec.physical.impl.protocol;


import DataMode.OPTIONAL;
import IterOutcome.NONE;
import IterOutcome.OK;
import IterOutcome.OK_NEW_SCHEMA;
import MinorType.INT;
import MinorType.VARCHAR;
import java.util.Iterator;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.exec.expr.TypeHelper;
import org.apache.drill.exec.ops.OperatorContext;
import org.apache.drill.exec.physical.impl.protocol.VectorContainerAccessor.ContainerAndSv2Accessor;
import org.apache.drill.exec.proto.UserBitShared.NamePart;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.TypedFieldId;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.record.VectorWrapper;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.vector.IntVector;
import org.apache.drill.exec.vector.VarCharVector;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the implementation of the Drill Volcano iterator protocol that
 * wraps the modular operator implementation.
 */
public class TestOperatorRecordBatch extends SubOperatorTest {
    private static final Logger logger = LoggerFactory.getLogger(SubOperatorTest.class);

    /**
     * Mock operator executor that simply tracks each method call
     * and provides a light-weight vector container. Returns a
     * defined number of (batches) with an optional schema change.
     */
    private class MockOperatorExec implements OperatorExec {
        public boolean bindCalled;

        public boolean buildSchemaCalled;

        public int nextCalls = 1;

        public int nextCount;

        public int schemaChangeAt = -1;

        public boolean cancelCalled;

        public boolean closeCalled;

        public boolean schemaEOF;

        private final VectorContainerAccessor batchAccessor;

        public MockOperatorExec() {
            this(TestOperatorRecordBatch.mockBatch());
        }

        public MockOperatorExec(VectorContainer container) {
            batchAccessor = new VectorContainerAccessor();
            batchAccessor.setContainer(container);
        }

        public MockOperatorExec(VectorContainerAccessor accessor) {
            batchAccessor = accessor;
        }

        @Override
        public void bind(OperatorContext context) {
            bindCalled = true;
        }

        @Override
        public BatchAccessor batchAccessor() {
            return batchAccessor;
        }

        @Override
        public boolean buildSchema() {
            buildSchemaCalled = true;
            return !(schemaEOF);
        }

        @Override
        public boolean next() {
            (nextCount)++;
            if ((nextCount) > (nextCalls)) {
                return false;
            }
            if ((nextCount) == (schemaChangeAt)) {
                BatchSchema newSchema = new SchemaBuilder(batchAccessor.getSchema()).add("b", VARCHAR).build();
                VectorContainer newContainer = new VectorContainer(SubOperatorTest.fixture.allocator(), newSchema);
                batchAccessor.setContainer(newContainer);
            }
            return true;
        }

        @Override
        public void cancel() {
            cancelCalled = true;
        }

        @Override
        public void close() {
            batchAccessor().getOutgoingContainer().clear();
            closeCalled = true;
        }
    }

    /**
     * Simulate a normal run: return some batches, encounter a schema change.
     */
    @Test
    public void testNormalLifeCycle() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec();
        opExec.nextCalls = 2;
        opExec.schemaChangeAt = 2;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertSame(SubOperatorTest.fixture.getFragmentContext(), opBatch.fragmentContext());
            Assert.assertNotNull(opBatch.getContext());
            // First call to next() builds schema
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            Assert.assertTrue(opExec.bindCalled);
            Assert.assertTrue(opExec.buildSchemaCalled);
            Assert.assertEquals(0, opExec.nextCount);
            // Second call returns the first batch
            Assert.assertEquals(OK, opBatch.next());
            Assert.assertEquals(1, opExec.nextCount);
            // Third call causes a schema change
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            Assert.assertEquals(2, opExec.nextCount);
            // Fourth call reaches EOF
            Assert.assertEquals(NONE, opBatch.next());
            Assert.assertEquals(3, opExec.nextCount);
            // Close
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.closeCalled);
        Assert.assertFalse(opExec.cancelCalled);
    }

    /**
     * Simulate a truncated life cycle: next() is never called. Not a valid part
     * of the protocol; but should be ready anyway.
     */
    @Test
    public void testTruncatedLifeCycle() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec();
        opExec.schemaEOF = true;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.bindCalled);
        Assert.assertTrue(opExec.closeCalled);
    }

    /**
     * Simulate reaching EOF when trying to create the schema.
     */
    @Test
    public void testSchemaEOF() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec();
        opExec.schemaEOF = true;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(NONE, opBatch.next());
            Assert.assertTrue(opExec.buildSchemaCalled);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.closeCalled);
    }

    /**
     * Simulate reaching EOF on the first batch. This simulated data source
     * discovered a schema, but had no data.
     */
    @Test
    public void testFirstBatchEOF() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec();
        opExec.nextCalls = 0;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            Assert.assertTrue(opExec.buildSchemaCalled);
            Assert.assertEquals(NONE, opBatch.next());
            Assert.assertEquals(1, opExec.nextCount);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.closeCalled);
    }

    /**
     * Simulate the caller failing the operator before getting the schema.
     */
    @Test
    public void testFailEarly() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec();
        opExec.nextCalls = 2;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            opBatch.kill(false);
            Assert.assertFalse(opExec.buildSchemaCalled);
            Assert.assertEquals(0, opExec.nextCount);
            Assert.assertFalse(opExec.cancelCalled);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.closeCalled);
    }

    /**
     * Simulate the caller failing the operator before EOF.
     */
    @Test
    public void testFailWhileReading() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec();
        opExec.nextCalls = 2;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            Assert.assertEquals(OK, opBatch.next());
            opBatch.kill(false);
            Assert.assertTrue(opExec.cancelCalled);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.closeCalled);
    }

    /**
     * Simulate the caller failing the operator after EOF but before close.
     * This is a silly time to fail, but have to handle it anyway.
     */
    @Test
    public void testFailBeforeClose() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec();
        opExec.nextCalls = 2;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            Assert.assertEquals(OK, opBatch.next());
            Assert.assertEquals(OK, opBatch.next());
            Assert.assertEquals(NONE, opBatch.next());
            opBatch.kill(false);
            // Already hit EOF, so fail won't be passed along.
            Assert.assertFalse(opExec.cancelCalled);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.closeCalled);
    }

    /**
     * Simulate the caller failing the operator after close.
     * This is violates the operator protocol, but have to handle it anyway.
     */
    @Test
    public void testFailAfterClose() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec();
        opExec.nextCalls = 2;
        OperatorRecordBatch opBatch = makeOpBatch(opExec);
        Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
        Assert.assertEquals(OK, opBatch.next());
        Assert.assertEquals(OK, opBatch.next());
        Assert.assertEquals(NONE, opBatch.next());
        try {
            opBatch.close();
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.closeCalled);
        opBatch.kill(false);
        Assert.assertFalse(opExec.cancelCalled);
    }

    /**
     * The record batch abstraction has a bunch of methods to work with a vector container.
     * Rather than simply exposing the container itself, the batch instead exposes various
     * container operations. Probably an artifact of its history. In any event, make
     * sure those methods are passed through to the container accessor.
     */
    @Test
    public void testBatchAccessor() {
        BatchSchema schema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, "fred").addRow(20, "wilma").build();
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec(rs.container());
        opExec.nextCalls = 1;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            Assert.assertEquals(schema, opBatch.getSchema());
            Assert.assertEquals(2, opBatch.getRecordCount());
            Assert.assertSame(rs.container(), opBatch.getOutgoingContainer());
            Iterator<VectorWrapper<?>> iter = opBatch.iterator();
            Assert.assertEquals("a", iter.next().getValueVector().getField().getName());
            Assert.assertEquals("b", iter.next().getValueVector().getField().getName());
            // Not a full test of the schema path; just make sure that the
            // pass-through to the Vector Container works.
            SchemaPath path = SchemaPath.create(NamePart.newBuilder().setName("a").build());
            TypedFieldId id = opBatch.getValueVectorId(path);
            Assert.assertEquals(INT, id.getFinalType().getMinorType());
            Assert.assertEquals(1, id.getFieldIds().length);
            Assert.assertEquals(0, id.getFieldIds()[0]);
            path = SchemaPath.create(NamePart.newBuilder().setName("b").build());
            id = opBatch.getValueVectorId(path);
            Assert.assertEquals(VARCHAR, id.getFinalType().getMinorType());
            Assert.assertEquals(1, id.getFieldIds().length);
            Assert.assertEquals(1, id.getFieldIds()[0]);
            // Sanity check of getValueAccessorById()
            VectorWrapper<?> w = opBatch.getValueAccessorById(IntVector.class, 0);
            Assert.assertNotNull(w);
            Assert.assertEquals("a", w.getValueVector().getField().getName());
            w = opBatch.getValueAccessorById(VarCharVector.class, 1);
            Assert.assertNotNull(w);
            Assert.assertEquals("b", w.getValueVector().getField().getName());
            // getWritableBatch() ?
            // No selection vectors
            try {
                opBatch.getSelectionVector2();
                Assert.fail();
            } catch (UnsupportedOperationException e) {
                // Expected
            }
            try {
                opBatch.getSelectionVector4();
                Assert.fail();
            } catch (UnsupportedOperationException e) {
                // Expected
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(opExec.closeCalled);
    }

    @Test
    public void testSchemaChange() {
        BatchSchema schema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, "fred").addRow(20, "wilma").build();
        VectorContainer container = rs.container();
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec(container);
        int schemaVersion = opExec.batchAccessor().schemaVersion();
        // Be tidy: start at 1.
        Assert.assertEquals(1, schemaVersion);
        // Changing data does not trigger schema change
        container.zeroVectors();
        opExec.batchAccessor.setContainer(container);
        Assert.assertEquals(schemaVersion, opExec.batchAccessor().schemaVersion());
        // Different container, same vectors, does not trigger a change
        VectorContainer c2 = new VectorContainer(SubOperatorTest.fixture.allocator());
        for (VectorWrapper<?> vw : container) {
            c2.add(vw.getValueVector());
        }
        c2.buildSchema(SelectionVectorMode.NONE);
        opExec.batchAccessor.setContainer(c2);
        Assert.assertEquals(schemaVersion, opExec.batchAccessor().schemaVersion());
        opExec.batchAccessor.setContainer(container);
        Assert.assertEquals(schemaVersion, opExec.batchAccessor().schemaVersion());
        // Replacing a vector with another of the same type does trigger
        // a change.
        VectorContainer c3 = new VectorContainer(SubOperatorTest.fixture.allocator());
        c3.add(container.getValueVector(0).getValueVector());
        c3.add(TypeHelper.getNewVector(container.getValueVector(1).getValueVector().getField(), SubOperatorTest.fixture.allocator(), null));
        c3.buildSchema(SelectionVectorMode.NONE);
        opExec.batchAccessor.setContainer(c3);
        Assert.assertEquals((schemaVersion + 1), opExec.batchAccessor().schemaVersion());
        schemaVersion = opExec.batchAccessor().schemaVersion();
        // No change if same schema again
        opExec.batchAccessor.setContainer(c3);
        Assert.assertEquals(schemaVersion, opExec.batchAccessor().schemaVersion());
        // Adding a vector triggers a change
        MaterializedField c = SchemaBuilder.columnSchema("c", INT, OPTIONAL);
        c3.add(TypeHelper.getNewVector(c, SubOperatorTest.fixture.allocator(), null));
        c3.buildSchema(SelectionVectorMode.NONE);
        opExec.batchAccessor.setContainer(c3);
        Assert.assertEquals((schemaVersion + 1), opExec.batchAccessor().schemaVersion());
        schemaVersion = opExec.batchAccessor().schemaVersion();
        // No change if same schema again
        opExec.batchAccessor.setContainer(c3);
        Assert.assertEquals(schemaVersion, opExec.batchAccessor().schemaVersion());
        // Removing a vector triggers a change
        c3.remove(c3.getValueVector(2).getValueVector());
        c3.buildSchema(SelectionVectorMode.NONE);
        Assert.assertEquals(2, c3.getNumberOfColumns());
        opExec.batchAccessor.setContainer(c3);
        Assert.assertEquals((schemaVersion + 1), opExec.batchAccessor().schemaVersion());
        schemaVersion = opExec.batchAccessor().schemaVersion();
        // Clean up
        opExec.close();
        c2.clear();
        c3.clear();
    }

    /**
     * Test that an SV2 is properly handled by the proper container accessor.
     */
    @Test
    public void testSv2() {
        BatchSchema schema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).build();
        RowSet.SingleRowSet rs = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, "fred").addRow(20, "wilma").withSv2().build();
        ContainerAndSv2Accessor accessor = new ContainerAndSv2Accessor();
        accessor.setContainer(rs.container());
        accessor.setSelectionVector(rs.getSv2());
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec(accessor);
        opExec.nextCalls = 1;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            Assert.assertSame(rs.getSv2(), opBatch.getSelectionVector2());
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.closeCalled);
        // Must release SV2
        rs.clear();
    }

    // -----------------------------------------------------------------------
    // Exception error cases
    // 
    // Assumes that any of the operator executor methods could throw an
    // exception. A wise implementation will throw a user exception that the
    // operator just passes along. A lazy implementation will throw any old
    // unchecked exception. Validate both cases.
    public static final String ERROR_MSG = "My Bad!";

    /**
     * Failure on the bind method.
     */
    @Test
    public void testWrappedExceptionOnBind() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec() {
            @Override
            public void bind(OperatorContext context) {
                throw new IllegalStateException(TestOperatorRecordBatch.ERROR_MSG);
            }
        };
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(TestOperatorRecordBatch.ERROR_MSG));
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        } catch (Throwable t) {
            Assert.fail();
        }
        Assert.assertFalse(opExec.cancelCalled);// Cancel not called: too early in life

        Assert.assertFalse(opExec.closeCalled);// Same with close

    }

    @Test
    public void testUserExceptionOnBind() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec() {
            @Override
            public void bind(OperatorContext context) {
                throw UserException.connectionError().message(TestOperatorRecordBatch.ERROR_MSG).build(TestOperatorRecordBatch.logger);
            }
        };
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            opBatch.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(TestOperatorRecordBatch.ERROR_MSG));
            Assert.assertNull(e.getCause());
        } catch (Throwable t) {
            Assert.fail();
        }
        Assert.assertFalse(opExec.cancelCalled);// Cancel not called: too early in life

        Assert.assertFalse(opExec.closeCalled);// Same with close

    }

    /**
     * Failure when building the schema (first call to next()).
     */
    @Test
    public void testWrappedExceptionOnBuildSchema() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec() {
            @Override
            public boolean buildSchema() {
                throw new IllegalStateException(TestOperatorRecordBatch.ERROR_MSG);
            }
        };
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            opBatch.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(TestOperatorRecordBatch.ERROR_MSG));
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        } catch (Throwable t) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.cancelCalled);
        Assert.assertTrue(opExec.closeCalled);
    }

    @Test
    public void testUserExceptionOnBuildSchema() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec() {
            @Override
            public boolean buildSchema() {
                throw UserException.dataReadError().message(TestOperatorRecordBatch.ERROR_MSG).build(TestOperatorRecordBatch.logger);
            }
        };
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            opBatch.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(TestOperatorRecordBatch.ERROR_MSG));
            Assert.assertNull(e.getCause());
        } catch (Throwable t) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.cancelCalled);
        Assert.assertTrue(opExec.closeCalled);
    }

    /**
     * Failure on the second or subsequent calls to next(), when actually
     * fetching a record batch.
     */
    @Test
    public void testWrappedExceptionOnNext() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec() {
            @Override
            public boolean next() {
                throw new IllegalStateException(TestOperatorRecordBatch.ERROR_MSG);
            }
        };
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            opBatch.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(TestOperatorRecordBatch.ERROR_MSG));
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        } catch (Throwable t) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.cancelCalled);
        Assert.assertTrue(opExec.closeCalled);
    }

    @Test
    public void testUserExceptionOnNext() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec() {
            @Override
            public boolean next() {
                throw UserException.dataReadError().message(TestOperatorRecordBatch.ERROR_MSG).build(TestOperatorRecordBatch.logger);
            }
        };
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            opBatch.next();
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(TestOperatorRecordBatch.ERROR_MSG));
            Assert.assertNull(e.getCause());
        } catch (Throwable t) {
            Assert.fail();
        }
        Assert.assertTrue(opExec.cancelCalled);
        Assert.assertTrue(opExec.closeCalled);
    }

    /**
     * Failure when closing the operator implementation.
     */
    @Test
    public void testWrappedExceptionOnClose() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec() {
            @Override
            public void close() {
                // Release memory
                super.close();
                // Then fail
                throw new IllegalStateException(TestOperatorRecordBatch.ERROR_MSG);
            }
        };
        opExec.nextCalls = 1;
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            Assert.assertEquals(OK, opBatch.next());
            Assert.assertEquals(NONE, opBatch.next());
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(TestOperatorRecordBatch.ERROR_MSG));
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        } catch (Throwable t) {
            Assert.fail();
        }
        Assert.assertFalse(opExec.cancelCalled);
        Assert.assertTrue(opExec.closeCalled);
    }

    @Test
    public void testUserExceptionOnClose() {
        TestOperatorRecordBatch.MockOperatorExec opExec = new TestOperatorRecordBatch.MockOperatorExec() {
            @Override
            public void close() {
                // Release memory
                super.close();
                // Then fail
                throw UserException.dataReadError().message(TestOperatorRecordBatch.ERROR_MSG).build(TestOperatorRecordBatch.logger);
            }
        };
        try (OperatorRecordBatch opBatch = makeOpBatch(opExec)) {
            Assert.assertEquals(OK_NEW_SCHEMA, opBatch.next());
            Assert.assertEquals(OK, opBatch.next());
            Assert.assertEquals(NONE, opBatch.next());
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains(TestOperatorRecordBatch.ERROR_MSG));
            Assert.assertNull(e.getCause());
        } catch (Throwable t) {
            Assert.fail();
        }
        Assert.assertFalse(opExec.cancelCalled);
        Assert.assertTrue(opExec.closeCalled);
    }
}

