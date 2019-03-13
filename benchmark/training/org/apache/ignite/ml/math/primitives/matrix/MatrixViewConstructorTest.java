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


import java.util.function.Supplier;
import org.apache.ignite.ml.math.primitives.matrix.impl.DenseMatrix;
import org.apache.ignite.ml.math.primitives.matrix.impl.ViewMatrix;
import org.apache.ignite.ml.math.primitives.matrix.storage.ViewMatrixStorage;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class MatrixViewConstructorTest {
    /**
     *
     */
    @Test
    public void invalidArgsTest() {
        Matrix m = new DenseMatrix(1, 1);
        DenseMatrixConstructorTest.verifyAssertionError(() -> new ViewMatrix(((Matrix) (null)), 0, 0, 1, 1), "Null parent matrix.");
        DenseMatrixConstructorTest.verifyAssertionError(() -> new ViewMatrix(m, (-1), 0, 1, 1), "Invalid row offset.");
        DenseMatrixConstructorTest.verifyAssertionError(() -> new ViewMatrix(m, 0, (-1), 1, 1), "Invalid col offset.");
        DenseMatrixConstructorTest.verifyAssertionError(() -> new ViewMatrix(m, 0, 0, 0, 1), "Invalid rows.");
        DenseMatrixConstructorTest.verifyAssertionError(() -> new ViewMatrix(m, 0, 0, 1, 0), "Invalid cols.");
    }

    /**
     *
     */
    @Test
    public void basicTest() {
        for (Matrix m : new Matrix[]{ new DenseMatrix(3, 3), new DenseMatrix(3, 4), new DenseMatrix(4, 3) })
            for (int rowOff : new int[]{ 0, 1 })
                for (int colOff : new int[]{ 0, 1 })
                    for (int rows : new int[]{ 1, 2 })
                        for (int cols : new int[]{ 1, 2 })
                            basicTest(m, rowOff, colOff, rows, cols);





    }

    /**
     *
     */
    @Test
    public void attributeTest() {
        for (Matrix m : new Matrix[]{ new DenseMatrix(3, 3), new DenseMatrix(3, 4), new DenseMatrix(4, 3) }) {
            ViewMatrix matrixView = new ViewMatrix(m, 0, 0, m.rowSize(), m.columnSize());
            ViewMatrixStorage delegateStorage = ((ViewMatrixStorage) (matrixView.getStorage()));
            Assert.assertEquals(m.rowSize(), matrixView.rowSize());
            Assert.assertEquals(m.columnSize(), matrixView.columnSize());
            Assert.assertEquals(m.rowSize(), delegateStorage.rowsLength());
            Assert.assertEquals(m.columnSize(), delegateStorage.columnsLength());
            Assert.assertEquals(m.isSequentialAccess(), delegateStorage.isSequentialAccess());
            Assert.assertEquals(m.isRandomAccess(), delegateStorage.isRandomAccess());
            Assert.assertEquals(m.isDistributed(), delegateStorage.isDistributed());
            Assert.assertEquals(m.isDense(), delegateStorage.isDense());
            Assert.assertEquals(m.isArrayBased(), delegateStorage.isArrayBased());
            Assert.assertArrayEquals(m.getStorage().data(), delegateStorage.data(), 0.0);
        }
    }
}

