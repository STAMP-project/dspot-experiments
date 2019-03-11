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


import org.apache.ignite.ml.math.ExternalizeTest;
import org.apache.ignite.ml.math.primitives.MathTestConstants;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract class with base tests for each vector storage.
 */
public abstract class VectorBaseStorageTest<T extends VectorStorage> extends ExternalizeTest<T> {
    /**
     *
     */
    protected T storage;

    /**
     *
     */
    @Test
    public void getSet() throws Exception {
        for (int i = 0; i < (MathTestConstants.STORAGE_SIZE); i++) {
            double random = Math.random();
            set(i, random);
            Assert.assertEquals(MathTestConstants.WRONG_DATA_ELEMENT, get(i), random, MathTestConstants.NIL_DELTA);
        }
    }

    /**
     *
     */
    @Test
    public void size() {
        Assert.assertTrue(MathTestConstants.UNEXPECTED_VAL, ((storage.size()) == (MathTestConstants.STORAGE_SIZE)));
    }
}

