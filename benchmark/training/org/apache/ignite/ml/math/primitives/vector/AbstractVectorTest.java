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


import Functions.PLUS;
import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoublePredicate;
import java.util.stream.StreamSupport;
import org.apache.ignite.ml.math.exceptions.IndexException;
import org.apache.ignite.ml.math.primitives.MathTestConstants;
import org.apache.ignite.ml.math.primitives.vector.storage.DenseVectorStorage;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link AbstractVector}.
 */
public class AbstractVectorTest {
    /**
     *
     */
    private AbstractVector testVector;

    /**
     *
     */
    @Test
    public void setStorage() {
        testVector.setStorage(createStorage());
        Assert.assertTrue(((testVector.size()) == (MathTestConstants.STORAGE_SIZE)));
    }

    /**
     *
     */
    @Test
    public void size() {
        testVector.setStorage(createStorage());
        Assert.assertTrue(((testVector.size()) == (MathTestConstants.STORAGE_SIZE)));
        testVector.setStorage(new DenseVectorStorage(((MathTestConstants.STORAGE_SIZE) + (MathTestConstants.STORAGE_SIZE))));
        Assert.assertTrue(((testVector.size()) == ((MathTestConstants.STORAGE_SIZE) + (MathTestConstants.STORAGE_SIZE))));
        testVector = getAbstractVector(createStorage());
        Assert.assertTrue(((testVector.size()) == (MathTestConstants.STORAGE_SIZE)));
    }

    /**
     *
     */
    @Test
    public void getPositive() {
        testVector = getAbstractVector(createStorage());
        for (int i = 0; i < (MathTestConstants.STORAGE_SIZE); i++)
            Assert.assertNotNull(MathTestConstants.NULL_VALUES, testVector.get(i));

    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void getNegative0() {
        testVector.get(0);
    }

    /**
     *
     */
    @Test(expected = IndexException.class)
    public void getNegative1() {
        testVector.setStorage(createStorage());
        testVector.get((-1));
    }

    /**
     *
     */
    @Test(expected = IndexException.class)
    public void getNegative2() {
        testVector.setStorage(createStorage());
        testVector.get(((testVector.size()) + 1));
    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void getXNegative0() {
        testVector.getX(0);
    }

    /**
     *
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void getXNegative1() {
        testVector.setStorage(createStorage());
        testVector.getX((-1));
    }

    /**
     *
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void getXNegative2() {
        testVector.setStorage(createStorage());
        testVector.getX(((MathTestConstants.STORAGE_SIZE) + 1));
    }

    /**
     *
     */
    @Test
    public void set() {
        double[] data = initVector();
        for (int i = 0; i < (MathTestConstants.STORAGE_SIZE); i++)
            testVector.set(i, Math.exp(data[i]));

        for (int i = 0; i < (MathTestConstants.STORAGE_SIZE); i++)
            Assert.assertEquals(MathTestConstants.VAL_NOT_EQUALS, testVector.get(i), Math.exp(data[i]), MathTestConstants.NIL_DELTA);

    }

    /**
     *
     */
    @Test(expected = IndexException.class)
    public void setNegative0() {
        testVector.set((-1), (-1));
    }

    /**
     *
     */
    @Test(expected = IndexException.class)
    public void setNegative1() {
        initVector();
        testVector.set((-1), (-1));
    }

    /**
     *
     */
    @Test(expected = IndexException.class)
    public void setNegative2() {
        initVector();
        testVector.set(((MathTestConstants.STORAGE_SIZE) + 1), (-1));
    }

    /**
     *
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void setXNegative0() {
        initVector();
        testVector.setX((-1), (-1));
    }

    /**
     *
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void setXNegative1() {
        initVector();
        testVector.setX(((MathTestConstants.STORAGE_SIZE) + 1), (-1));
    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void setXNegative2() {
        testVector.setX((-1), (-1));
    }

    /**
     *
     */
    @Test
    public void isZero() {
        Assert.assertTrue(MathTestConstants.UNEXPECTED_VAL, testVector.isZero(0.0));
        Assert.assertFalse(MathTestConstants.UNEXPECTED_VAL, testVector.isZero(1.0));
    }

    /**
     *
     */
    @Test
    public void guid() {
        Assert.assertNotNull(MathTestConstants.NULL_GUID, testVector.guid());
        Assert.assertEquals(MathTestConstants.UNEXPECTED_GUID_VAL, testVector.guid(), testVector.guid());
        Assert.assertFalse(MathTestConstants.EMPTY_GUID, testVector.guid().toString().isEmpty());
        testVector = getAbstractVector(createStorage());
        Assert.assertNotNull(MathTestConstants.NULL_GUID, testVector.guid());
        Assert.assertEquals(MathTestConstants.UNEXPECTED_GUID_VAL, testVector.guid(), testVector.guid());
        Assert.assertFalse(MathTestConstants.EMPTY_GUID, testVector.guid().toString().isEmpty());
    }

    /**
     *
     */
    @Test
    public void equalsTest() {
        VectorStorage storage = createStorage();
        AbstractVector testVector1 = getAbstractVector();
        testVector1.setStorage(storage);
        AbstractVector testVector2 = getAbstractVector();
        Assert.assertEquals(MathTestConstants.VAL_NOT_EQUALS, testVector, testVector);
        testVector2.setStorage(storage);
        Assert.assertTrue(MathTestConstants.VAL_NOT_EQUALS, testVector1.equals(testVector2));
        Assert.assertFalse(MathTestConstants.VALUES_SHOULD_BE_NOT_EQUALS, testVector1.equals(testVector));
    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void all() {
        Assert.assertNotNull(MathTestConstants.NULL_VAL, testVector.all());
        Assert.assertNotNull(MathTestConstants.NULL_VAL, getAbstractVector(createStorage()).all());
        getAbstractVector().all().iterator().next();
    }

    /**
     *
     */
    @Test
    public void nonZeroElements() {
        VectorStorage storage = createStorage();
        double[] data = storage.data();
        testVector = getAbstractVector(storage);
        Assert.assertEquals(MathTestConstants.VAL_NOT_EQUALS, testVector.nonZeroElements(), Arrays.stream(data).filter(( x) -> x != 0.0).count());
        addNilValues(data);
        Assert.assertEquals(MathTestConstants.VAL_NOT_EQUALS, testVector.nonZeroElements(), Arrays.stream(data).filter(( x) -> x != 0.0).count());
    }

    /**
     *
     */
    @Test
    public void foldMapWithSecondVector() {
        double[] data0 = initVector();
        VectorStorage storage1 = createStorage();
        double[] data1 = storage1.data().clone();
        AbstractVector testVector1 = getAbstractVector(storage1);
        StringBuilder testVal = new StringBuilder();
        for (int i = 0; i < (data0.length); i++)
            testVal.append(((data0[i]) + (data1[i])));

        Assert.assertEquals(MathTestConstants.VAL_NOT_EQUALS, testVector.foldMap(testVector1, ( string, xi) -> string.concat(xi.toString()), PLUS, ""), testVal.toString());
    }

    /**
     *
     */
    @Test
    public void nonZeroes() {
        Assert.assertNotNull(MathTestConstants.NULL_VAL, testVector.nonZeroes());
        double[] data = initVector();
        Assert.assertNotNull(MathTestConstants.NULL_VAL, testVector.nonZeroes());
        Assert.assertEquals(MathTestConstants.VAL_NOT_EQUALS, StreamSupport.stream(testVector.nonZeroes().spliterator(), false).count(), Arrays.stream(data).filter(( x) -> x != 0.0).count());
        addNilValues(data);
        Assert.assertEquals(MathTestConstants.VAL_NOT_EQUALS, StreamSupport.stream(testVector.nonZeroes().spliterator(), false).count(), Arrays.stream(data).filter(( x) -> x != 0.0).count());
    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void nonZeroesEmpty() {
        testVector.nonZeroes().iterator().next();
    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void assign() {
        testVector.assign(MathTestConstants.TEST_VAL);
    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void assignArr() {
        testVector.assign(new double[1]);
    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void assignArrEmpty() {
        testVector.assign(new double[0]);
    }

    /**
     *
     */
    @Test(expected = NullPointerException.class)
    public void dotNegative() {
        testVector.dot(getAbstractVector(createEmptyStorage()));
    }

    /**
     *
     */
    @Test
    public void dotSelf() {
        double[] data = initVector();
        Assert.assertEquals(MathTestConstants.VAL_NOT_EQUALS, testVector.dotSelf(), Arrays.stream(data).reduce(0, ( x, y) -> x + (y * y)), MathTestConstants.NIL_DELTA);
    }

    /**
     *
     */
    @Test
    public void getStorage() {
        Assert.assertNotNull(MathTestConstants.NULL_VAL, getAbstractVector(createEmptyStorage()));
        Assert.assertNotNull(MathTestConstants.NULL_VAL, getAbstractVector(createStorage()));
        testVector.setStorage(createStorage());
        Assert.assertNotNull(MathTestConstants.NULL_VAL, testVector.getStorage());
    }

    /**
     *
     */
    @Test
    public void getElement() {
        double[] data = initVector();
        for (int i = 0; i < (data.length); i++) {
            Assert.assertNotNull(MathTestConstants.NULL_VAL, testVector.getElement(i));
            Assert.assertEquals(MathTestConstants.UNEXPECTED_VAL, testVector.getElement(i).get(), data[i], MathTestConstants.NIL_DELTA);
            testVector.getElement(i).set((++(data[i])));
            Assert.assertEquals(MathTestConstants.UNEXPECTED_VAL, testVector.getElement(i).get(), data[i], MathTestConstants.NIL_DELTA);
        }
    }
}

