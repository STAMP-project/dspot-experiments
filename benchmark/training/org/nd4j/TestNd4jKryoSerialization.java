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
package org.nd4j;


import DataType.FLOAT;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.serializer.SerializerInstance;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import scala.Tuple2;


/**
 * Created by Alex on 04/07/2016.
 */
public class TestNd4jKryoSerialization {
    private JavaSparkContext sc;

    @Test
    public void testSerialization() {
        Tuple2<INDArray, INDArray> t2 = new Tuple2(Nd4j.linspace(1, 10, 10, FLOAT), Nd4j.linspace(10, 20, 10, FLOAT));
        Broadcast<Tuple2<INDArray, INDArray>> b = sc.broadcast(t2);
        List<INDArray> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(Nd4j.ones(5));
        }
        JavaRDD<INDArray> rdd = sc.parallelize(list);
        rdd.foreach(new TestNd4jKryoSerialization.AssertFn(b));
    }

    @Test
    public void testSerializationPrimitives() {
        Counter<Integer> c = new Counter();
        c.incrementCount(5, 3.0);
        CounterMap<Integer, Double> cm = new CounterMap();
        cm.setCount(7, 3.0, 4.5);
        Object[] objs = new Object[]{ new AtomicBoolean(true), new AtomicBoolean(false), new AtomicDouble(5.0), c, cm, new ImmutablePair(5, 3.0), new ImmutableQuad(1, 2.0, 3.0F, 4L), new ImmutableTriple(1, 2.0, 3.0F), new Pair(5, 3.0), new Quad(1, 2.0, 3.0F, 4L), new Triple(1, 2.0, 3.0F) };
        SerializerInstance si = sc.env().serializer().newInstance();
        for (Object o : objs) {
            System.out.println(o.getClass());
            // System.out.println(ie.getClass());
            testSerialization(o, si);
        }
    }

    @AllArgsConstructor
    public static class AssertFn implements VoidFunction<INDArray> {
        private Broadcast<Tuple2<INDArray, INDArray>> b;

        @Override
        public void call(INDArray arr) throws Exception {
            Tuple2<INDArray, INDArray> t2 = b.getValue();
            Assert.assertEquals(Nd4j.linspace(1, 10, 10, FLOAT), t2._1());
            Assert.assertEquals(Nd4j.linspace(10, 20, 10, FLOAT), t2._2());
            Assert.assertEquals(Nd4j.ones(5), arr);
        }
    }
}

