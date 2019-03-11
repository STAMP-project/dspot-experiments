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


import MathFunction.SQRT;
import MathOp.Add;
import MathOp.Multiply;
import java.util.Arrays;
import java.util.List;
import org.datavec.api.transform.MathFunction;
import org.datavec.api.transform.MathOp;
import org.datavec.api.transform.Transform;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.serde.JsonSerializer;
import org.datavec.api.transform.serde.YamlSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 20/07/2016.
 */
public class TestYamlJsonSerde {
    public static YamlSerializer y = new YamlSerializer();

    public static JsonSerializer j = new JsonSerializer();

    @Test
    public void testTransforms() {
        Transform[] transforms = new Transform[]{ new org.datavec.api.transform.ndarray.NDArrayColumnsMathOpTransform("newCol", MathOp.Divide, "in1", "in2"), new org.datavec.api.transform.ndarray.NDArrayMathFunctionTransform("inCol", MathFunction.SQRT), new org.datavec.api.transform.ndarray.NDArrayScalarOpTransform("inCol", MathOp.ScalarMax, 3.0) };
        for (Transform t : transforms) {
            String yaml = TestYamlJsonSerde.y.serialize(t);
            String json = TestYamlJsonSerde.j.serialize(t);
            // System.out.println(yaml);
            // System.out.println(json);
            // System.out.println();
            Transform t2 = TestYamlJsonSerde.y.deserializeTransform(yaml);
            Transform t3 = TestYamlJsonSerde.j.deserializeTransform(json);
            Assert.assertEquals(t, t2);
            Assert.assertEquals(t, t3);
        }
        String tArrAsYaml = TestYamlJsonSerde.y.serialize(transforms);
        String tArrAsJson = TestYamlJsonSerde.j.serialize(transforms);
        String tListAsYaml = TestYamlJsonSerde.y.serializeTransformList(Arrays.asList(transforms));
        String tListAsJson = TestYamlJsonSerde.j.serializeTransformList(Arrays.asList(transforms));
        // System.out.println("\n\n\n\n");
        // System.out.println(tListAsYaml);
        List<Transform> lFromYaml = TestYamlJsonSerde.y.deserializeTransformList(tListAsYaml);
        List<Transform> lFromJson = TestYamlJsonSerde.j.deserializeTransformList(tListAsJson);
        Assert.assertEquals(Arrays.asList(transforms), TestYamlJsonSerde.y.deserializeTransformList(tArrAsYaml));
        Assert.assertEquals(Arrays.asList(transforms), TestYamlJsonSerde.j.deserializeTransformList(tArrAsJson));
        Assert.assertEquals(Arrays.asList(transforms), lFromYaml);
        Assert.assertEquals(Arrays.asList(transforms), lFromJson);
    }

    @Test
    public void testTransformProcessAndSchema() {
        Schema schema = new Schema.Builder().addColumnInteger("firstCol").addColumnNDArray("nd1a", new long[]{ 1, 10 }).addColumnNDArray("nd1b", new long[]{ 1, 10 }).addColumnNDArray("nd2", new long[]{ 1, 100 }).addColumnNDArray("nd3", new long[]{ -1, -1 }).build();
        TransformProcess tp = new TransformProcess.Builder(schema).integerMathOp("firstCol", Add, 1).ndArrayColumnsMathOpTransform("added", Add, "nd1a", "nd1b").ndArrayMathFunctionTransform("nd2", SQRT).ndArrayScalarOpTransform("nd3", Multiply, 2.0).build();
        String asJson = tp.toJson();
        String asYaml = tp.toYaml();
        TransformProcess fromJson = TransformProcess.fromJson(asJson);
        TransformProcess fromYaml = TransformProcess.fromYaml(asYaml);
        Assert.assertEquals(tp, fromJson);
        Assert.assertEquals(tp, fromYaml);
    }
}

