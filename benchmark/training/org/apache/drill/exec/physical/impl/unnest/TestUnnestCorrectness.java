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
import junit.framework.TestCase;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.exec.ops.OperatorContext;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.record.RecordBatch;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(OperatorTest.class)
public class TestUnnestCorrectness extends SubOperatorTest {
    // Operator Context for mock batch
    public static OperatorContext operatorContext;

    // use MockLateralJoinPop for MockRecordBatch ??
    public static PhysicalOperator mockPopConfig;

    @Test
    public void testUnnestFixedWidthColumn() {
        Object[][] data = new Object[][]{ new Object[]{ ((Object) (new int[]{ 1, 2 })), ((Object) (new int[]{ 3, 4, 5 })) }, new Object[]{ ((Object) (new int[]{ 6, 7, 8, 9 })), ((Object) (new int[]{ 10, 11, 12, 13, 14 })) } };
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("otherColumn", INT).addArray("unnestColumn", INT).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema, incomingSchema };
        // First batch in baseline is an empty batch corresponding to OK_NEW_SCHEMA
        Integer[][] baseline = new Integer[][]{ new Integer[]{  }, new Integer[]{  }, new Integer[]{ 1, 1, 2, 2, 2 }, new Integer[]{ 1, 2, 3, 4, 5 }, new Integer[]{ 1, 1, 1, 1, 2, 2, 2, 2, 2 }, new Integer[]{ 6, 7, 8, 9, 10, 11, 12, 13, 14 } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline);
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
        // First batch in baseline is an empty batch corresponding to OK_NEW_SCHEMA
        Object[][] baseline = new Object[][]{ new Object[]{  }, new Object[]{  }, new Object[]{ 1, 1, 2, 2, 2 }, new Object[]{ "", "zero", "one", "two", "three" }, new Object[]{ 1, 1, 1, 1, 2, 2, 2, 2, 2 }, new Object[]{ "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve" } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline);
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
        // First batch in baseline is an empty batch corresponding to OK_NEW_SCHEMA
        Object[][] baseline = getMapBaseline();
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline);
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
        // First batch in baseline is an empty batch corresponding to OK_NEW_SCHEMA
        // All subsequent batches are also empty
        String[][] baseline = new String[][]{ new String[]{  }, new String[]{  }, new String[]{  }, new String[]{  }, new String[]{  }, new String[]{  } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline);
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
        // First batch in baseline is an empty batch corresponding to OK_NEW_SCHEMA
        Object[][] baseline = new Object[][]{ new Object[]{  }, new Object[]{  }, new Object[]{ 1, 1, 2, 2, 2 }, new Object[]{ "0", "1", "2", "3", "4" }, new Object[]{ 1, 1 }, new Object[]{ "5", "6" }, new Object[]{ 1 }, new Object[]{ "9" } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK, IterOutcome.OK_NEW_SCHEMA };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline);
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
        // First batch in baseline is an empty batch corresponding to OK_NEW_SCHEMA
        // Another empty batch introduced by the schema change in the last batch
        Object[][] baseline = new Object[][]{ new Object[]{  }, new Object[]{  }, new Object[]{ 1, 1, 2, 2, 2 }, new Object[]{ "0", "1", "2", "3", "4" }, new Object[]{ 1, 1 }, new Object[]{ "5", "6" }, new Object[]{  }, new Object[]{  }, new Object[]{ 1 }, new Object[]{ 9 } };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK, IterOutcome.OK_NEW_SCHEMA };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline);
        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testUnnestLimitBatchSize() {
        final int limitedOutputBatchSize = 1023;// one less than the power of two. See RecordBatchMemoryManager

        // .adjustOutputRowCount
        final int limitedOutputBatchSizeBytes = (1024 * 4) * 2;// (num rows+1) * size of int * num of columns (rowId, unnest_col)

        final int inputBatchSize = 1023 + 1;
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
        Integer[][] baseline = new Integer[6][];
        baseline[0] = new Integer[]{  };
        baseline[1] = new Integer[]{  };
        baseline[2] = new Integer[limitedOutputBatchSize];
        baseline[3] = new Integer[limitedOutputBatchSize];
        baseline[4] = new Integer[1];
        baseline[5] = new Integer[1];
        for (int i = 0; i < limitedOutputBatchSize; i++) {
            baseline[2][i] = 1;
            baseline[3][i] = i;
        }
        baseline[4][0] = 1;
        baseline[5][0] = limitedOutputBatchSize;
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("otherColumn", INT).addArray("unnestColumn", INT).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK };
        final long outputBatchSize = SubOperatorTest.fixture.getFragmentContext().getOptions().getOption(OUTPUT_BATCH_SIZE_VALIDATOR);
        SubOperatorTest.fixture.getFragmentContext().getOptions().setLocalOption(OUTPUT_BATCH_SIZE, limitedOutputBatchSizeBytes);
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline);
        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        } finally {
            SubOperatorTest.fixture.getFragmentContext().getOptions().setLocalOption(OUTPUT_BATCH_SIZE, outputBatchSize);
        }
    }

    // Limit sends a kill. Unnest has more than one record batch for a record when
    // the kill is sent.
    @Test
    public void testUnnestKillFromLimitSubquery1() {
        // similar to previous test; we split a record across more than one batch.
        // but we also set a limit less than the size of the batch so only one batch gets output.
        final int limitedOutputBatchSize = 1023;// one less than the power of two. See RecordBatchMemoryManager

        // .adjustOutputRowCount
        final int limitedOutputBatchSizeBytes = (1024 * 4) * 2;// (num rows+1) * size of int * num of columns (rowId, unnest_col)

        final int inputBatchSize = 1023 + 1;
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
        Integer[][] baseline = new Integer[6][];
        baseline[0] = new Integer[]{  };
        baseline[1] = new Integer[]{  };
        baseline[2] = new Integer[limitedOutputBatchSize];
        baseline[3] = new Integer[limitedOutputBatchSize];
        baseline[4] = new Integer[1];
        baseline[5] = new Integer[1];
        for (int i = 0; i < limitedOutputBatchSize; i++) {
            baseline[2][i] = 1;
            baseline[3][i] = i;
        }
        baseline[4] = new Integer[]{  };// because of kill the next batch is an empty batch

        baseline[5] = new Integer[]{  };// because of kill the next batch is an empty batch

        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("otherColumn", INT).addArray("unnestColumn", INT).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK };
        final long outputBatchSize = SubOperatorTest.fixture.getFragmentContext().getOptions().getOption(OUTPUT_BATCH_SIZE_VALIDATOR);
        SubOperatorTest.fixture.getFragmentContext().getOptions().setLocalOption(OUTPUT_BATCH_SIZE, limitedOutputBatchSizeBytes);
        try {
            testUnnest(incomingSchemas, iterOutcomes, 100, (-1), data, baseline);// Limit of 100 values for unnest.

        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        } finally {
            SubOperatorTest.fixture.getFragmentContext().getOptions().setLocalOption(OUTPUT_BATCH_SIZE, outputBatchSize);
        }
    }

    // Limit sends a kill. Unnest has exactly one record batch for a record when
    // the kill is sent. This test is actually useless since it tests the behaviour of
    // the mock lateral which doesn't send kill at all if it gets an EMIT. We expect limit
    // to do so, so let's keep the test to demonstrate the expected behaviour.
    @Test
    public void testUnnestKillFromLimitSubquery2() {
        // similar to previous test but the size of the array fits exactly into the record batch;
        final int limitedOutputBatchSize = 1023;// one less than the power of two. See RecordBatchMemoryManager

        // .adjustOutputRowCount
        final int limitedOutputBatchSizeBytes = (1024 * 4) * 2;// (num rows+1) * size of int * num of columns (rowId, unnest_col)

        final int inputBatchSize = 1023;
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
        Integer[][] baseline = new Integer[4][];
        baseline[0] = new Integer[]{  };
        baseline[1] = new Integer[]{  };
        baseline[2] = new Integer[limitedOutputBatchSize];
        baseline[3] = new Integer[limitedOutputBatchSize];
        for (int i = 0; i < limitedOutputBatchSize; i++) {
            baseline[2][i] = 1;
            baseline[3][i] = i;
        }
        // Create input schema
        TupleMetadata incomingSchema = new SchemaBuilder().add("rowNumber", INT).addArray("unnestColumn", INT).buildSchema();
        TupleMetadata[] incomingSchemas = new TupleMetadata[]{ incomingSchema };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK };
        final long outputBatchSize = SubOperatorTest.fixture.getFragmentContext().getOptions().getOption(OUTPUT_BATCH_SIZE_VALIDATOR);
        SubOperatorTest.fixture.getFragmentContext().getOptions().setLocalOption(OUTPUT_BATCH_SIZE, limitedOutputBatchSizeBytes);
        try {
            testUnnest(incomingSchemas, iterOutcomes, 100, (-1), data, baseline);// Limit of 100 values for unnest.

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
        Integer[][] baseline = new Integer[][]{  };
        RecordBatch[] iterOutcomes = new IterOutcome[]{ IterOutcome.OK_NEW_SCHEMA, IterOutcome.OK };
        try {
            testUnnest(incomingSchemas, iterOutcomes, data, baseline);
        } catch (UserException e) {
            return;// succeeded

        } catch (Exception e) {
            TestCase.fail(("Failed due to exception: " + (e.getMessage())));
        }
    }
}

