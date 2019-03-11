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


import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;


public class MyArraysTest {
    @Test
    public void test() {
        float[] w = new float[]{ 1, 2, 3, 4, 0, 0, 0 };
        int[] idx = MyArrays.getTop(w, 0.95F, false);
        MyArrays.set(w, idx, 0);
        MyArrays.normalize(w);
        for (int i = 0; i < (w.length); i++) {
            System.out.print(((w[i]) + " "));
        }
    }

    @Test
    public void testentropy() {
        int[] w;
        float[] ww;
        float e;
        w = new int[]{ 1, 2, 3, 4, 0, 0, 0 };
        e = MyArrays.entropy(w);
        System.out.print((e + " "));
        ww = new float[]{ 1, 0, 0, 0, 0, 0, 0 };
        e = MyArrays.entropy(ww);
        System.out.print((e + " "));
        System.out.println();
        TIntArrayList www = new TIntArrayList();
        for (int i = 0; i < 100; i++) {
            www.add(1);
            w = www.toArray();
            e = MyArrays.entropy(w);
            System.out.print((e + " "));
            System.out.println((e / (w.length)));
        }
        System.out.println();
        ww = new float[]{ 0.5F, 0.5F };
        e = MyArrays.entropy(ww);
        System.out.print((e + " "));
    }
}

