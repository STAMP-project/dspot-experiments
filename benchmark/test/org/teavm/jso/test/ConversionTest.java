/**
 * Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.jso.test;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSString;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@SkipJVM
public class ConversionTest {
    @Test
    public void convertsPrimitivesToJavaScript() {
        Assert.assertEquals("true:2:3:64:4:5.5:6.5:foo", ConversionTest.combinePrimitives(true, ((byte) (2)), ((short) (3)), '@', 4, 5.5F, 6.5, "foo"));
    }

    @Test
    public void convertsPrimitivesToJava() {
        ConversionTest.Primitives map = ConversionTest.getPrimitives();
        Assert.assertTrue(map.getA());
        Assert.assertEquals(2, map.getB());
        Assert.assertEquals(3, map.getC());
        Assert.assertEquals('@', map.getD());
        Assert.assertEquals(4, map.getE());
        Assert.assertEquals(5.5, map.getF(), 0.01);
        Assert.assertEquals(6.5, map.getG(), 0.01);
        Assert.assertEquals("foo", map.getH());
    }

    @Test
    public void convertsPrimitiveArraysToJavaScript() {
        Assert.assertEquals("true:2:3:64:4:5.5:6.5:foo", ConversionTest.combinePrimitiveArrays(new boolean[]{ true }, new byte[]{ 2 }, new short[]{ 3 }, new char[]{ '@' }, new int[]{ 4 }, new float[]{ 5.5F }, new double[]{ 6.5 }, new String[]{ "foo" }));
    }

    @Test
    public void convertsPrimitiveArraysToJava() {
        ConversionTest.PrimitiveArrays arrays = ConversionTest.getPrimitiveArrays();
        boolean[] booleanArray = arrays.getA();
        Assert.assertEquals(1, booleanArray.length);
        Assert.assertTrue(booleanArray[0]);
        Assert.assertArrayEquals(new byte[]{ 2 }, arrays.getB());
        Assert.assertArrayEquals(new short[]{ 3 }, arrays.getC());
        Assert.assertArrayEquals(new char[]{ '@' }, arrays.getD());
        Assert.assertArrayEquals(new int[]{ 4 }, arrays.getE());
        Assert.assertArrayEquals(new float[]{ 5.5F }, arrays.getF(), 0.01F);
        Assert.assertArrayEquals(new double[]{ 6.5 }, arrays.getG(), 0.01);
        Assert.assertArrayEquals(new String[]{ "foo" }, arrays.getH());
    }

    @Test
    public void convertsPrimitiveArrays2ToJavaScript() {
        Assert.assertEquals("true:2:3:64:4:5.5:6.5:foo", ConversionTest.combinePrimitiveArrays2(new boolean[][]{ new boolean[]{ true } }, new byte[][]{ new byte[]{ 2 } }, new short[][]{ new short[]{ 3 } }, new char[][]{ new char[]{ '@' } }, new int[][]{ new int[]{ 4 } }, new float[][]{ new float[]{ 5.5F } }, new double[][]{ new double[]{ 6.5 } }, new String[][]{ new String[]{ "foo" } }));
    }

    @Test
    public void convertsPrimitiveArrays2ToJava() {
        ConversionTest.PrimitiveArrays2 arrays = ConversionTest.getPrimitiveArrays2();
        boolean[][] booleanArray = arrays.getA();
        Assert.assertEquals(1, booleanArray.length);
        Assert.assertEquals(1, booleanArray[0].length);
        Assert.assertTrue(booleanArray[0][0]);
        Assert.assertArrayEquals(new byte[]{ 2 }, arrays.getB()[0]);
        Assert.assertArrayEquals(new short[]{ 3 }, arrays.getC()[0]);
        Assert.assertArrayEquals(new char[]{ '@' }, arrays.getD()[0]);
        Assert.assertArrayEquals(new int[]{ 4 }, arrays.getE()[0]);
        Assert.assertArrayEquals(new float[]{ 5.5F }, arrays.getF()[0], 0.01F);
        Assert.assertArrayEquals(new double[]{ 6.5 }, arrays.getG()[0], 0.01);
        Assert.assertArrayEquals(new String[]{ "foo" }, arrays.getH()[0]);
    }

    @Test
    public void convertsPrimitiveArrays4ToJavaScript() {
        Assert.assertEquals("true:2:3:64:4:5.5:6.5:foo", ConversionTest.combinePrimitiveArrays4(new boolean[][][][]{ new boolean[][][]{ new boolean[][]{ new boolean[]{ true } } } }, new byte[][][][]{ new byte[][][]{ new byte[][]{ new byte[]{ 2 } } } }, new short[][][][]{ new short[][][]{ new short[][]{ new short[]{ 3 } } } }, new char[][][][]{ new char[][][]{ new char[][]{ new char[]{ '@' } } } }, new int[][][][]{ new int[][][]{ new int[][]{ new int[]{ 4 } } } }, new float[][][][]{ new float[][][]{ new float[][]{ new float[]{ 5.5F } } } }, new double[][][][]{ new double[][][]{ new double[][]{ new double[]{ 6.5 } } } }, new String[][][][]{ new String[][][]{ new String[][]{ new String[]{ "foo" } } } }));
    }

    @Test
    public void convertsPrimitiveArrays4ToJava() {
        ConversionTest.PrimitiveArrays4 arrays = ConversionTest.getPrimitiveArrays4();
        boolean[][][][] booleanArray = arrays.getA();
        Assert.assertEquals(1, booleanArray.length);
        Assert.assertEquals(1, booleanArray[0][0][0].length);
        Assert.assertTrue(booleanArray[0][0][0][0]);
        Assert.assertArrayEquals(new byte[]{ 2 }, arrays.getB()[0][0][0]);
        Assert.assertArrayEquals(new short[]{ 3 }, arrays.getC()[0][0][0]);
        Assert.assertArrayEquals(new char[]{ '@' }, arrays.getD()[0][0][0]);
        Assert.assertArrayEquals(new int[]{ 4 }, arrays.getE()[0][0][0]);
        Assert.assertArrayEquals(new float[]{ 5.5F }, arrays.getF()[0][0][0], 0.01F);
        Assert.assertArrayEquals(new double[]{ 6.5 }, arrays.getG()[0][0][0], 0.01);
        Assert.assertArrayEquals(new String[]{ "foo" }, arrays.getH()[0][0][0]);
    }

    @Test
    public void passesJSObject() {
        Assert.assertEquals("(foo)", ConversionTest.surround(JSString.valueOf("foo")).stringValue());
    }

    @Test
    public void convertsArrayOfJSObject() {
        Assert.assertEquals("(foo)", ConversionTest.surround(new JSString[]{ JSString.valueOf("foo") })[0].stringValue());
        Assert.assertEquals("(foo)", ConversionTest.surround(new JSString[][]{ new JSString[]{ JSString.valueOf("foo") } })[0][0].stringValue());
        Assert.assertEquals("(foo)", ConversionTest.surround(new JSString[][][][]{ new JSString[][][]{ new JSString[][]{ new JSString[]{ JSString.valueOf("foo") } } } })[0][0][0][0].stringValue());
    }

    @Test
    public void copiesArray() {
        int[] array = new int[]{ 23 };
        Assert.assertEquals(24, ConversionTest.mutate(array));
        Assert.assertEquals(23, array[0]);
    }

    @Test
    public void passesArrayByRef() {
        int[] array = new int[]{ 23, 42 };
        ConversionTest.mutateByRef(array);
        Assert.assertEquals(24, array[0]);
        Assert.assertEquals(43, array[1]);
        ConversionTest.createByRefMutator().mutate(array);
        Assert.assertEquals(25, array[0]);
        Assert.assertEquals(44, array[1]);
    }

    interface Primitives extends JSObject {
        @JSProperty
        boolean getA();

        @JSProperty
        byte getB();

        @JSProperty
        short getC();

        @JSProperty
        char getD();

        @JSProperty
        int getE();

        @JSProperty
        float getF();

        @JSProperty
        double getG();

        @JSProperty
        String getH();
    }

    interface PrimitiveArrays extends JSObject {
        @JSProperty
        boolean[] getA();

        @JSProperty
        byte[] getB();

        @JSProperty
        short[] getC();

        @JSProperty
        char[] getD();

        @JSProperty
        int[] getE();

        @JSProperty
        float[] getF();

        @JSProperty
        double[] getG();

        @JSProperty
        String[] getH();
    }

    interface PrimitiveArrays2 extends JSObject {
        @JSProperty
        boolean[][] getA();

        @JSProperty
        byte[][] getB();

        @JSProperty
        short[][] getC();

        @JSProperty
        char[][] getD();

        @JSProperty
        int[][] getE();

        @JSProperty
        float[][] getF();

        @JSProperty
        double[][] getG();

        @JSProperty
        String[][] getH();
    }

    interface PrimitiveArrays4 extends JSObject {
        @JSProperty
        boolean[][][][] getA();

        @JSProperty
        byte[][][][] getB();

        @JSProperty
        short[][][][] getC();

        @JSProperty
        char[][][][] getD();

        @JSProperty
        int[][][][] getE();

        @JSProperty
        float[][][][] getF();

        @JSProperty
        double[][][][] getG();

        @JSProperty
        String[][][][] getH();
    }

    private interface ByRefMutator extends JSObject {
        void mutate(@JSByRef
        int[] array);
    }
}

