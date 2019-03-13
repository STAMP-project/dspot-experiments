/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.math;


import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Test;


public final class MatrixWritableTest extends MahoutTestCase {
    @Test
    public void testSparseMatrixWritable() throws Exception {
        Matrix m = new SparseMatrix(5, 5);
        m.set(1, 2, 3.0);
        m.set(3, 4, 5.0);
        Map<String, Integer> bindings = Maps.newHashMap();
        bindings.put("A", 0);
        bindings.put("B", 1);
        bindings.put("C", 2);
        bindings.put("D", 3);
        bindings.put("default", 4);
        m.setRowLabelBindings(bindings);
        m.setColumnLabelBindings(bindings);
        MatrixWritableTest.doTestMatrixWritableEquals(m);
    }

    @Test
    public void testSparseRowMatrixWritable() throws Exception {
        Matrix m = new SparseRowMatrix(5, 5);
        m.set(1, 2, 3.0);
        m.set(3, 4, 5.0);
        Map<String, Integer> bindings = Maps.newHashMap();
        bindings.put("A", 0);
        bindings.put("B", 1);
        bindings.put("C", 2);
        bindings.put("D", 3);
        bindings.put("default", 4);
        m.setRowLabelBindings(bindings);
        m.setColumnLabelBindings(bindings);
        MatrixWritableTest.doTestMatrixWritableEquals(m);
    }

    @Test
    public void testDenseMatrixWritable() throws Exception {
        Matrix m = new DenseMatrix(5, 5);
        m.set(1, 2, 3.0);
        m.set(3, 4, 5.0);
        Map<String, Integer> bindings = Maps.newHashMap();
        bindings.put("A", 0);
        bindings.put("B", 1);
        bindings.put("C", 2);
        bindings.put("D", 3);
        bindings.put("default", 4);
        m.setRowLabelBindings(bindings);
        m.setColumnLabelBindings(bindings);
        MatrixWritableTest.doTestMatrixWritableEquals(m);
    }
}

