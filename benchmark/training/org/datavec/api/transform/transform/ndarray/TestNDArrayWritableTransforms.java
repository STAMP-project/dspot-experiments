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
package org.datavec.api.transform.transform.ndarray;


import Distance.COSINE;
import MathFunction.SIN;
import MathFunction.SQRT;
import MathOp.Add;
import java.util.Arrays;
import java.util.List;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 * Created by Alex on 02/06/2017.
 */
public class TestNDArrayWritableTransforms {
    @Test
    public void testNDArrayWritableBasic() {
        Schema s = new Schema.Builder().addColumnDouble("col0").addColumnNDArray("col1", new long[]{ 1, 10 }).addColumnString("col2").build();
        TransformProcess tp = new TransformProcess.Builder(s).ndArrayScalarOpTransform("col1", Add, 100).build();
        List<Writable> in = Arrays.<Writable>asList(new DoubleWritable(0), new org.datavec.api.writable.NDArrayWritable(Nd4j.linspace(0, 9, 10)), new Text("str0"));
        List<Writable> out = tp.execute(in);
        List<Writable> exp = Arrays.<Writable>asList(new DoubleWritable(0), new org.datavec.api.writable.NDArrayWritable(Nd4j.linspace(0, 9, 10).addi(100)), new Text("str0"));
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testNDArrayColumnsMathOpTransform() {
        Schema s = new Schema.Builder().addColumnDouble("col0").addColumnNDArray("col1", new long[]{ 1, 10 }).addColumnNDArray("col2", new long[]{ 1, 10 }).build();
        TransformProcess tp = new TransformProcess.Builder(s).ndArrayColumnsMathOpTransform("myCol", Add, "col1", "col2").build();
        List<String> expColNames = Arrays.asList("col0", "col1", "col2", "myCol");
        Assert.assertEquals(expColNames, tp.getFinalSchema().getColumnNames());
        List<Writable> in = Arrays.<Writable>asList(new DoubleWritable(0), new org.datavec.api.writable.NDArrayWritable(Nd4j.linspace(0, 9, 10)), new org.datavec.api.writable.NDArrayWritable(Nd4j.valueArrayOf(1, 10, 2.0)));
        List<Writable> out = tp.execute(in);
        List<Writable> exp = Arrays.<Writable>asList(new DoubleWritable(0), new org.datavec.api.writable.NDArrayWritable(Nd4j.linspace(0, 9, 10)), new org.datavec.api.writable.NDArrayWritable(Nd4j.valueArrayOf(1, 10, 2.0)), new org.datavec.api.writable.NDArrayWritable(Nd4j.linspace(0, 9, 10).addi(2.0)));
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testNDArrayMathFunctionTransform() {
        Schema s = new Schema.Builder().addColumnDouble("col0").addColumnNDArray("col1", new long[]{ 1, 10 }).addColumnNDArray("col2", new long[]{ 1, 10 }).build();
        TransformProcess tp = new TransformProcess.Builder(s).ndArrayMathFunctionTransform("col1", SIN).ndArrayMathFunctionTransform("col2", SQRT).build();
        List<String> expColNames = Arrays.asList("col0", "col1", "col2");
        Assert.assertEquals(expColNames, tp.getFinalSchema().getColumnNames());
        List<Writable> in = Arrays.<Writable>asList(new DoubleWritable(0), new org.datavec.api.writable.NDArrayWritable(Nd4j.linspace(0, 9, 10)), new org.datavec.api.writable.NDArrayWritable(Nd4j.valueArrayOf(1, 10, 2.0)));
        List<Writable> out = tp.execute(in);
        List<Writable> exp = Arrays.<Writable>asList(new DoubleWritable(0), new org.datavec.api.writable.NDArrayWritable(Transforms.sin(Nd4j.linspace(0, 9, 10))), new org.datavec.api.writable.NDArrayWritable(Transforms.sqrt(Nd4j.valueArrayOf(1, 10, 2.0))));
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testNDArrayDistanceTransform() {
        Schema s = new Schema.Builder().addColumnDouble("col0").addColumnNDArray("col1", new long[]{ 1, 10 }).addColumnNDArray("col2", new long[]{ 1, 10 }).build();
        TransformProcess tp = new TransformProcess.Builder(s).ndArrayDistanceTransform("dist", COSINE, "col1", "col2").build();
        List<String> expColNames = Arrays.asList("col0", "col1", "col2", "dist");
        Assert.assertEquals(expColNames, tp.getFinalSchema().getColumnNames());
        Nd4j.getRandom().setSeed(12345);
        INDArray arr1 = Nd4j.rand(1, 10);
        INDArray arr2 = Nd4j.rand(1, 10);
        double cosine = Transforms.cosineSim(arr1, arr2);
        List<Writable> in = Arrays.<Writable>asList(new DoubleWritable(0), new org.datavec.api.writable.NDArrayWritable(arr1.dup()), new org.datavec.api.writable.NDArrayWritable(arr2.dup()));
        List<Writable> out = tp.execute(in);
        List<Writable> exp = Arrays.<Writable>asList(new DoubleWritable(0), new org.datavec.api.writable.NDArrayWritable(arr1), new org.datavec.api.writable.NDArrayWritable(arr2), new DoubleWritable(cosine));
        Assert.assertEquals(exp, out);
    }
}

