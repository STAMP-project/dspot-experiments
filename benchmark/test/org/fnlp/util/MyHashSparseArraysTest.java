/**
 * This file is part of FNLP (formerly FudanNLP).
 *
 *  FNLP is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FNLP is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FudanNLP.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2009-2014 www.fnlp.org. All rights reserved.
 */
package org.fnlp.util;


import org.fnlp.ml.types.sv.HashSparseVector;
import org.junit.Test;


public class MyHashSparseArraysTest {
    @Test
    public void test() {
        float[] w = new float[]{ 0.3F, 1.2F, 1.09F, -0.45F, -1.2F, 0, 0, 0, 0 };
        HashSparseVector sv = new HashSparseVector(w);
        int[][] idx = MyHashSparseArrays.getTop(sv.data, 0.99F);
        MyHashSparseArrays.setZero(sv.data, idx[1]);
        System.out.println(sv);
    }
}

