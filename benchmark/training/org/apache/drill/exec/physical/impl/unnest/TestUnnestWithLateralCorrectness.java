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
package org.apache.drill.exec.physical.impl.unnest;


import ExecConstants.OUTPUT_BATCH_SIZE;
import ExecConstants.OUTPUT_BATCH_SIZE_VALIDATOR;
import RecordBatch.IterOutcome;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.VARCHAR;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.exec.ops.OperatorContext;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.physical.config.LateralJoinPOP;
import org.apache.drill.exec.planner.common.DrillLateralJoinRelBase;
import org.apache.drill.exec.record.RecordBatch;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(OperatorTest.class)
public class TestUnnestWithLateralCorrectness extends SubOperatorTest {
    // Operator Context for mock batch
    public static OperatorContext operatorContext;

    public static PhysicalOperator mockPopConfig;

    public static LateralJoinPOP ljPopConfig;

    @Test
    public void testUnnestFixedWidthColumn() {
        Object[][] data = new Object[][]{ new Object[]{ ((Object) (new int[]{ 1, 2 })), ((Object) (new int[]{ 3, 4, 5 })) }, new Object[]{ ((Object) (new int[]{ 6, 7, 8, 9 })), ((Object) (new int[]{ 10, 11, 12, 13, 14 })) } };
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("rowNumber", INT).addArray("unnestColumn", INT).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema, incomingSchema };
        Integer[][][] baseline = new Integer[][][]{ new Integer[][]{ new Integer[]{ 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4 }// rowNum
        // rowNum
        // rowNum
        , new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 }// unnestColumn_flat
        // unnestColumn_flat
        // unnestColumn_flat
         } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline, false);
        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testUnnestVarWidthColumn() {
        Object[][] data = new Object[][]{ new Object[]{ ((Object) (new String[]{ "", "zero" })), ((Object) (new String[]{ "one", "two", "three" })) }, new Object[]{ ((Object) (new String[]{ "four", "five", "six", "seven" })), ((Object) (new String[]{ "eight", "nine", "ten", "eleven", "twelve" })) } };
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("someColumn", INT).addArray("unnestColumn", VARCHAR).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema, incomingSchema };
        Object[][][] baseline = new Object[][][]{ new Object[][]{ new Object[]{ 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4 }// rowNum
        // rowNum
        // rowNum
        , new Object[]{ "", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve" }// unnestColumn_flat
        // unnestColumn_flat
        // unnestColumn_flat
         } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline, false);
        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testUnnestMapColumn() {
        Object[][] data = getMapData();
        // Create input schema
        TupleMetadata incomingSchema = getRepeatedMapSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema, incomingSchema };
        Object[][][] baseline = getMapBaseline();
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline, false);
        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testUnnestEmptyList() {
        Object[][] data = new Object[][]{ new Object[]{ ((Object) (new String[]{  })), ((Object) (new String[]{  })) }, new Object[]{ ((Object) (new String[]{  })), ((Object) (new String[]{  })) } };
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("someColumn", INT).addArray("unnestColumn", VARCHAR).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema, incomingSchema };
        // All batches are empty
        String[][][] baseline = new String[][][]{ new String[][]{ new String[]{  } } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline, false);
        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testUnnestMultipleNewSchemaIncoming() {
        // Schema changes in incoming have no effect on unnest unless the type of the
        // unnest column itself has changed
        Object[][] data = new Object[][]{ new Object[]{ ((Object) (new String[]{ "0", "1" })), ((Object) (new String[]{ "2", "3", "4" })) }, new Object[]{ ((Object) (new String[]{ "5", "6" })) }, new Object[]{ ((Object) (new String[]{ "9" })) } };
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("someColumn", INT).addArray("unnestColumn", VARCHAR).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema, incomingSchema, incomingSchema };
        Object[][][] baseline = new Object[][][]{ new Object[][]{ new Object[]{ 1, 1, 2, 2, 2, 3, 3 }, new Object[]{ "0", "1", "2", "3", "4", "5", "6" } }, new Object[][]{ new Object[]{ 4 }, new Object[]{ "9" } } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK, IterOutcome.OK_NEW_SCHEMA };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline, false);
        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testUnnestSchemaChange() {
        Object[][] data = new Object[][]{ new Object[]{ ((Object) (new String[]{ "0", "1" })), ((Object) (new String[]{ "2", "3", "4" })) }, new Object[]{ ((Object) (new String[]{ "5", "6" })) }, new Object[]{ ((Object) (new int[]{ 9 })) } };
        // Create input schema
        TupleMetadata incomingSchema1 = new SchemaBuilder().add("someColumn", INT).addArray("unnestColumn", VARCHAR).buildSchema();
        TupleMetadata incomingSchema2 = new SchemaBuilder().add("someColumn", INT).addArray("unnestColumn", INT).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema1, incomingSchema1, incomingSchema2 };
        Object[][][] baseline = new Object[][][]{ new Object[][]{ new Object[]{ 1, 1, 2, 2, 2, 3, 3 }, new Object[]{ "0", "1", "2", "3", "4", "5", "6" } }, new Object[][]{ new Object[]{ 4 }, new Object[]{ 9 } } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK, IterOutcome.OK_NEW_SCHEMA };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline, false);
        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testUnnestLimitBatchSize_WithExcludedCols() {
        LateralJoinPOP previoudPop = TestUnnestWithLateralCorrectness.ljPopConfig;
        List<SchemaPath> excludedCols = new ArrayList<>();
        excludedCols.add(SchemaPath.getSimplePath("unnestColumn"));
        TestUnnestWithLateralCorrectness.ljPopConfig = new LateralJoinPOP(null, null, JoinRelType.INNER, DrillLateralJoinRelBase.IMPLICIT_COLUMN, excludedCols);
        final int limitedOutputBatchSize = 127;
        final int inputBatchSize = limitedOutputBatchSize + 1;
        // Since we want 127 row count and because of nearest power of 2 adjustment output row count will be reduced to
        // 64. So we should configure batch size for (N+1) rows if we want to output N rows where N is not power of 2
        // size of lateral output batch = (N+1)*8 bytes, where N = output batch row count
        // Lateral output batch size = (N+1) * (input row size without unnest field) + (N+1) * size of single unnest column
        // = (N+1) * (size of row id) + (N+1) * (size of single array entry)
        // = (N+1)*4 + (N+1) * 4
        // = (N+1) * 8
        // configure the output batch size to be one more record than that so that the batch sizer can round down
        final int limitedOutputBatchSizeBytes = 8 * (limitedOutputBatchSize + 1);
        testUnnestBatchSizing(inputBatchSize, limitedOutputBatchSize, limitedOutputBatchSizeBytes, true);
        TestUnnestWithLateralCorrectness.ljPopConfig = previoudPop;
    }

    @Test
    public void testUnnestLimitBatchSize() {
        final int limitedOutputBatchSize = 127;
        final int inputBatchSize = limitedOutputBatchSize + 1;
        // size of lateral output batch = 4N * (N + 5) bytes, where N = output batch row count
        // Lateral output batch size =  N * input row size + N * size of single unnest column
        // =  N * (size of row id + size of array offset vector + (N + 1 )*size of single array entry))
        // + N * 4
        // = N * (4 + 2*4 + (N+1)*4 )  + N * 4
        // = N * (16 + 4N) + N * 4
        // = 4N * (N + 5)
        // configure the output batch size to be one more record than that so that the batch sizer can round down
        final int limitedOutputBatchSizeBytes = (4 * limitedOutputBatchSize) * (limitedOutputBatchSize + 6);
        testUnnestBatchSizing(inputBatchSize, limitedOutputBatchSize, limitedOutputBatchSizeBytes, false);
    }

    // Limit sends a kill. Unnest has more than one record batch for a record when
    // the kill is sent.
    @Test
    public void testUnnestKillFromLimitSubquery1() {
        // similar to previous test; we split a record across more than one batch.
        // but we also set a limit less than the size of the batch so only one batch gets output.
        final int limitedOutputBatchSize = 127;
        final int inputBatchSize = limitedOutputBatchSize + 1;
        final int limitedOutputBatchSizeBytes = (4 * limitedOutputBatchSize) * (limitedOutputBatchSize + 6);
        // single record batch with single row. The unnest column has one
        // more record than the batch size we want in the output
        Object[][] data = new Object[1][1];
        for (int i = 0; i < (data.length); i++) {
            for (int j = 0; j < (data[i].length); j++) {
                data[i][j] = new int[inputBatchSize];
                for (int k = 0; k < inputBatchSize; k++) {
                    ((int[]) (data[i][j]))[k] = k;
                }
            }
        }
        // because of kill we only get one batch back
        Integer[][][] baseline = new Integer[1][2][];
        baseline[0][0] = new Integer[limitedOutputBatchSize];
        baseline[0][1] = new Integer[limitedOutputBatchSize];
        for (int i = 0; i < limitedOutputBatchSize; i++) {
            baseline[0][0][i] = 1;
            baseline[0][1][i] = i;
        }
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("rowNumber", INT).addArray("unnestColumn", INT).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK };
        final long outputBatchSize = SubOperatorTest.fixture.getFragmentContext().getOptions().getOption(OUTPUT_BATCH_SIZE_VALIDATOR);
        SubOperatorTest.fixture.getFragmentContext().getOptions().setLocalOption(OUTPUT_BATCH_SIZE, limitedOutputBatchSizeBytes);
        try {
            testUnnest(incomingSchemas, iterOutcomes, (-1), 1, data, baseline, false);// Limit of 100 values for unnest.

        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        } finally {
            SubOperatorTest.fixture.getFragmentContext().getOptions().setLocalOption(OUTPUT_BATCH_SIZE, outputBatchSize);
        }
    }

    // Limit sends a kill. Unnest has exactly one record batch for a record when
    // the kill is sent. This test is actually useless since it tests the behaviour of
    // lateral which doesn't send kill at all if it gets an EMIT. We expect limit
    // to do so, so let's keep the test to demonstrate the expected behaviour.
    @Test
    public void testUnnestKillFromLimitSubquery2() {
        // similar to previous test but the size of the array fits exactly into the record batch;
        final int limitedOutputBatchSize = 127;
        final int inputBatchSize = limitedOutputBatchSize + 1;
        final int limitedOutputBatchSizeBytes = (4 * limitedOutputBatchSize) * (limitedOutputBatchSize + 6);
        // single record batch with single row. The unnest column has one
        // more record than the batch size we want in the output
        Object[][] data = new Object[1][1];
        for (int i = 0; i < (data.length); i++) {
            for (int j = 0; j < (data[i].length); j++) {
                data[i][j] = new int[inputBatchSize];
                for (int k = 0; k < inputBatchSize; k++) {
                    ((int[]) (data[i][j]))[k] = k;
                }
            }
        }
        // because of kill we only get one batch back
        Integer[][][] baseline = new Integer[1][2][];
        baseline[0][0] = new Integer[limitedOutputBatchSize];
        baseline[0][1] = new Integer[limitedOutputBatchSize];
        for (int i = 0; i < limitedOutputBatchSize; i++) {
            baseline[0][0][i] = 1;
            baseline[0][1][i] = i;
        }
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("rowNumber", INT).addArray("unnestColumn", INT).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK };
        final long outputBatchSize = SubOperatorTest.fixture.getFragmentContext().getOptions().getOption(OUTPUT_BATCH_SIZE_VALIDATOR);
        SubOperatorTest.fixture.getFragmentContext().getOptions().setLocalOption(OUTPUT_BATCH_SIZE, limitedOutputBatchSizeBytes);
        try {
            testUnnest(incomingSchemas, iterOutcomes, (-1), 1, data, baseline, false);// Limit of 100 values for unnest.

        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        } finally {
            SubOperatorTest.fixture.getFragmentContext().getOptions().setLocalOption(OUTPUT_BATCH_SIZE, outputBatchSize);
        }
    }

    @Test
    public void testUnnestNonArrayColumn() {
        Object[][] data = new Object[][]{ new Object[]{ ((Object) (new Integer(1))), ((Object) (new Integer(3))) }, new Object[]{ ((Object) (new Integer(6))), ((Object) (new Integer(10))) } };
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("rowNumber", INT).add("unnestColumn", INT).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema, incomingSchema };
        // We expect an Exception
        Integer[][][] baseline = new Integer[][][]{  };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline, false);
        } catch (UserException | UnsupportedOperationException e) {
            return;// succeeded

        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testNestedUnnestMapColumn() {
        Object[][] data = getMapData();
        // Create input schema
        TupleMetadata incomingSchema = getRepeatedMapSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema, incomingSchema };
        Object[][][] baseline = getNestedMapBaseline();
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testNestedUnnest(incomingSchemas, iterOutcomes, 0, data, baseline);
        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }
}

