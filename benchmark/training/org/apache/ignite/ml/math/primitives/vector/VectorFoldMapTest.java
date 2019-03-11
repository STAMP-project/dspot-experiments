/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.math.primitives.vector;


import Functions.IDENTITY;
import Functions.PLUS;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import org.junit.Test;


/**
 * See also: {@link AbstractVectorTest} and {@link VectorToMatrixTest}.
 */
public class VectorFoldMapTest {
    /**
     *
     */
    @Test
    public void mapVectorTest() {
        operationVectorTest(( operand1, operand2) -> operand1 + operand2, (Vector v1,Vector v2) -> v1.map(v2, PLUS));
    }

    /**
     *
     */
    @Test
    public void mapDoubleFunctionTest() {
        consumeSampleVectors(( v, desc) -> operatorTest(v, desc, ( vec) -> vec.map(Functions.INV), ( val) -> 1.0 / val));
    }

    /**
     *
     */
    @Test
    public void mapBiFunctionTest() {
        consumeSampleVectors(( v, desc) -> operatorTest(v, desc, ( vec) -> vec.map(Functions.PLUS, 1.0), ( val) -> 1.0 + val));
    }

    /**
     *
     */
    @Test
    public void foldMapTest() {
        toDoubleTest(( ref) -> Arrays.stream(ref).map(DoubleUnaryOperator.identity()).sum(), ( v) -> v.foldMap(PLUS, IDENTITY, 0.0));
    }

    /**
     *
     */
    @Test
    public void foldMapVectorTest() {
        toDoubleTest(( ref) -> 2.0 * (Arrays.stream(ref).sum()), ( v) -> v.foldMap(v, PLUS, PLUS, 0.0));
    }
}

