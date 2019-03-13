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
package org.apache.ignite.ml.nn;


import LossFunctions.HINGE;
import LossFunctions.L1;
import LossFunctions.L2;
import LossFunctions.LOG;
import LossFunctions.MSE;
import org.apache.ignite.ml.math.functions.IgniteDifferentiableVectorToDoubleFunction;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link LossFunctions}.
 */
public class LossFunctionsTest {
    /**
     *
     */
    @Test
    public void testMSE() {
        IgniteDifferentiableVectorToDoubleFunction f = MSE.apply(new DenseVector(new double[]{ 2.0, 1.0 }));
        Assert.assertNotNull(f);
        test(new double[]{ 1.0, 3.0 }, f);
    }

    /**
     *
     */
    @Test
    public void testLOG() {
        IgniteDifferentiableVectorToDoubleFunction f = LOG.apply(new DenseVector(new double[]{ 2.0, 1.0 }));
        Assert.assertNotNull(f);
        test(new double[]{ 1.0, 3.0 }, f);
    }

    /**
     *
     */
    @Test
    public void testL2() {
        IgniteDifferentiableVectorToDoubleFunction f = L2.apply(new DenseVector(new double[]{ 2.0, 1.0 }));
        Assert.assertNotNull(f);
        test(new double[]{ 1.0, 3.0 }, f);
    }

    /**
     *
     */
    @Test
    public void testL1() {
        IgniteDifferentiableVectorToDoubleFunction f = L1.apply(new DenseVector(new double[]{ 2.0, 1.0 }));
        Assert.assertNotNull(f);
        test(new double[]{ 1.0, 3.0 }, f);
    }

    /**
     *
     */
    @Test
    public void testHINGE() {
        IgniteDifferentiableVectorToDoubleFunction f = HINGE.apply(new DenseVector(new double[]{ 2.0, 1.0 }));
        Assert.assertNotNull(f);
        test(new double[]{ 1.0, 3.0 }, f);
    }
}

