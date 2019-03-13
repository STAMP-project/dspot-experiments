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


import Functions.PLUS;
import java.util.Iterator;
import org.junit.Test;


public final class TestSparseMatrix extends MatrixTest {
    /**
     * test optimized addition of sparse matrices
     */
    @Test
    public void add() {
        Matrix a = new SparseMatrix(3, 3);
        a.set(0, 0, 1);
        a.set(0, 2, 3);
        a.set(2, 0, 1);
        a.set(2, 1, 2);
        Matrix b = new SparseMatrix(3, 3);
        b.set(0, 0, 3);
        b.set(0, 2, 1);
        b.set(1, 1, 5);
        b.set(2, 2, 2);
        a.assign(b, PLUS);
        assertEquals(4, a.getQuick(0, 0), 0.0);
        assertEquals(0, a.getQuick(0, 1), 0.0);
        assertEquals(4, a.getQuick(0, 2), 0.0);
        assertEquals(0, a.getQuick(1, 0), 0.0);
        assertEquals(5, a.getQuick(1, 1), 0.0);
        assertEquals(0, a.getQuick(1, 2), 0.0);
        assertEquals(1, a.getQuick(2, 0), 0.0);
        assertEquals(2, a.getQuick(2, 1), 0.0);
        assertEquals(2, a.getQuick(2, 2), 0.0);
    }

    /**
     * Test copy method of sparse matrices which have empty non-initialized rows
     */
    @Test
    public void testSparseCopy() {
        SparseMatrix matrix = createSparseMatrixWithEmptyRow();
        Matrix copy = matrix.clone();
        assertSame("wrong class", copy.getClass(), matrix.getClass());
        SparseMatrix castedCopy = ((SparseMatrix) (copy));
        Iterator<MatrixSlice> originalSlices = matrix.iterator();
        Iterator<MatrixSlice> copySlices = castedCopy.iterator();
        while ((originalSlices.hasNext()) && (copySlices.hasNext())) {
            MatrixSlice originalSlice = originalSlices.next();
            MatrixSlice copySlice = copySlices.next();
            assertEquals("Wrong row indices.", originalSlice.index(), copySlice.index());
            assertEquals("Slices are not equal.", originalSlice, copySlice);
        } 
        assertSame("Number of rows of original and copy are not equal.", originalSlices.hasNext(), copySlices.hasNext());
    }
}

