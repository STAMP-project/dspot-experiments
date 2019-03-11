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
package org.apache.ignite.ml.math.primitives.matrix;


import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import org.apache.ignite.ml.math.ExternalizeTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link MatrixStorage} implementations.
 *
 * TODO: IGNITE-5723, add attribute tests.
 */
public class MatrixStorageImplementationTest extends ExternalizeTest<MatrixStorage> {
    /**
     * The columnSize() and the rowSize() test.
     */
    @Test
    public void sizeTest() {
        final AtomicReference<Integer> expRowSize = new AtomicReference<>(0);
        final AtomicReference<Integer> expColSize = new AtomicReference<>(0);
        consumeSampleStorages(( x, y) -> {
            expRowSize.set(x);
            expColSize.set(y);
        }, ( ms, desc) -> Assert.assertTrue(("Expected size for " + desc), ((expColSize.get().equals(ms.columnSize())) && (expRowSize.get().equals(ms.rowSize())))));
    }

    /**
     *
     */
    @Test
    public void getSetTest() {
        consumeSampleStorages(null, ( ms, desc) -> {
            for (int i = 0; i < (ms.rowSize()); i++) {
                for (int j = 0; j < (ms.columnSize()); j++) {
                    double random = Math.random();
                    ms.set(i, j, random);
                    Assert.assertTrue(((((("Unexpected value for " + desc) + " x:") + i) + ", y:") + j), ((Double.compare(random, ms.get(i, j))) == 0));
                }
            }
        });
    }
}

