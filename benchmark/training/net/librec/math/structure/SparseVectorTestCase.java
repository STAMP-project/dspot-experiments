/**
 * Copyright (C) 2016 LibRec
 *
 * This file is part of LibRec.
 * LibRec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibRec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibRec. If not, see <http://www.gnu.org/licenses/>.
 */
package net.librec.math.structure;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases about the VectorBasedSequentialSparseVector class
 * {@link net.librec.math.structure.VectorBasedSequentialSparseVector}
 *
 * @author Ma Chen
 */
public class SparseVectorTestCase {
    @Test
    public void testDotWithSparseVectorPerformance() {
        int dimension = 5000;
        double sparsity = 0.8;
        double[] data1 = new double[dimension];
        double[] data2 = new double[dimension];
        Random rand = new Random();
        for (int i = 0; i < dimension; i++) {
            if ((rand.nextFloat()) >= sparsity) {
                // sparsity
                data1[i] = i;
            }
        }
        for (int i = 0; i < dimension; i++) {
            if ((rand.nextFloat()) >= sparsity) {
                // sparsity
                data2[i] = i;
            }
        }
        DenseVector tmp1 = new VectorBasedDenseVector(data1);
        VectorBasedSequentialSparseVector sparseVector1 = new VectorBasedSequentialSparseVector(tmp1);
        DenseVector tmp2 = new VectorBasedDenseVector(data2);
        VectorBasedSequentialSparseVector sparseVector2 = new VectorBasedSequentialSparseVector(tmp2);
        long startTime = System.currentTimeMillis();
        double result = sparseVector1.dot(sparseVector2);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
    }

    @Test
    public void testFromDenseVector() {
        double[] values = new double[]{ 1, 2, 0 };
        Vector v2 = new VectorBasedDenseVector(values);
        VectorBasedSequentialSparseVector sv = new VectorBasedSequentialSparseVector(v2);
        System.out.println(("??????" + (sv.getNumEntries())));
        Assert.assertEquals(2, sv.getNumEntries());
    }
}

