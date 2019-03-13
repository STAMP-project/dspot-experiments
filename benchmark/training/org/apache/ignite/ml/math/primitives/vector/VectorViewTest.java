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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.BiConsumer;
import org.apache.ignite.ml.math.primitives.MathTestConstants;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.apache.ignite.ml.math.primitives.vector.impl.VectorView;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link VectorView}.
 */
public class VectorViewTest {
    /**
     *
     */
    private static final int OFFSET = 10;

    /**
     *
     */
    private static final int VIEW_LENGTH = 80;

    /**
     *
     */
    private static final String EXTERNALIZE_TEST_FILE_NAME = "externalizeTest";

    /**
     *
     */
    private VectorView testVector;

    /**
     *
     */
    private DenseVector parentVector;

    /**
     *
     */
    private double[] parentData;

    /**
     *
     */
    @Test
    public void testCopy() throws Exception {
        Vector cp = testVector.copy();
        Assert.assertTrue(MathTestConstants.VAL_NOT_EQUALS, cp.equals(testVector));
    }

    /**
     *
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLike() throws Exception {
        for (int card : new int[]{ 1, 2, 4, 8, 16, 32, 64, 128 })
            consumeSampleVectors(( v, desc) -> {
                Vector vLike = like(card);
                Class<? extends Vector> expType = v.getClass();
                Assert.assertNotNull(((("Expect non-null like vector for " + (expType.getSimpleName())) + " in ") + desc), vLike);
                Assert.assertEquals(("Expect size equal to cardinality at " + desc), card, vLike.size());
                Class<? extends Vector> actualType = vLike.getClass();
                Assert.assertTrue(((((("Expected matrix type " + (expType.getSimpleName())) + " should be assignable from actual type ") + (actualType.getSimpleName())) + " in ") + desc), expType.isAssignableFrom(actualType));
            });

    }

    /**
     * See also {@link VectorToMatrixTest#testLikeMatrix()}.
     */
    @Test
    public void testLikeMatrix() {
        consumeSampleVectors(( v, desc) -> {
            boolean expECaught = false;
            try {
                Assert.assertNull(("Null view instead of exception in " + desc), likeMatrix(1, 1));
            } catch (UnsupportedOperationException uoe) {
                expECaught = true;
            }
            Assert.assertTrue(("Expected exception was not caught in " + desc), expECaught);
        });
    }

    /**
     *
     */
    @Test
    public void testWriteReadExternal() throws Exception {
        Assert.assertNotNull("Unexpected null parent data", parentData);
        File f = new File(VectorViewTest.EXTERNALIZE_TEST_FILE_NAME);
        try {
            ObjectOutputStream objOutputStream = new ObjectOutputStream(new FileOutputStream(f));
            objOutputStream.writeObject(testVector);
            objOutputStream.close();
            ObjectInputStream objInputStream = new ObjectInputStream(new FileInputStream(f));
            VectorView readVector = ((VectorView) (objInputStream.readObject()));
            objInputStream.close();
            Assert.assertTrue(MathTestConstants.VAL_NOT_EQUALS, testVector.equals(readVector));
        } catch (ClassNotFoundException | IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}

