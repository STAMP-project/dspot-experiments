/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.arrow.recordreader;


import Schema.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.datavec.arrow.ArrowConverter;
import org.junit.Assert;
import org.junit.Test;


public class ArrowWritableRecordTimeSeriesBatchTests {
    private static BufferAllocator bufferAllocator = new RootAllocator(Long.MAX_VALUE);

    @Test
    public void testBasicIndexing() {
        Schema.Builder schema = new Schema.Builder();
        for (int i = 0; i < 3; i++) {
            schema.addColumnInteger(String.valueOf(i));
        }
        List<List<Writable>> timeStep = Arrays.asList(Arrays.<Writable>asList(new IntWritable(0), new IntWritable(1), new IntWritable(2)), Arrays.<Writable>asList(new IntWritable(1), new IntWritable(2), new IntWritable(3)), Arrays.<Writable>asList(new IntWritable(4), new IntWritable(5), new IntWritable(6)));
        int numTimeSteps = 5;
        List<List<List<Writable>>> timeSteps = new ArrayList<>(numTimeSteps);
        for (int i = 0; i < numTimeSteps; i++) {
            timeSteps.add(timeStep);
        }
        List<FieldVector> fieldVectors = ArrowConverter.toArrowColumnsTimeSeries(ArrowWritableRecordTimeSeriesBatchTests.bufferAllocator, schema.build(), timeSteps);
        Assert.assertEquals(3, fieldVectors.size());
        for (FieldVector fieldVector : fieldVectors) {
            for (int i = 0; i < (fieldVector.getValueCount()); i++) {
                Assert.assertFalse(((("Index " + i) + " was null for field vector ") + fieldVector), fieldVector.isNull(i));
            }
        }
        ArrowWritableRecordTimeSeriesBatch arrowWritableRecordTimeSeriesBatch = new ArrowWritableRecordTimeSeriesBatch(fieldVectors, schema.build(), ((timeStep.size()) * (timeStep.get(0).size())));
        Assert.assertEquals(timeSteps, arrowWritableRecordTimeSeriesBatch.toArrayList());
    }
}

