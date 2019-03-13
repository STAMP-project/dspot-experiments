/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
/**
 * Class comes from Apache Commons Lang, added some tiny changes
 */
package org.mockito.internal.matchers.apachecommons;


import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


/**
 *
 *
 * @author <a href="mailto:sdowney@panix.com">Steve Downey</a>
 * @author <a href="mailto:scolebourne@joda.org">Stephen Colebourne</a>
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @author Maarten Coene
 * @version $Id: EqualsBuilderTest.java 611543 2008-01-13 07:00:22Z bayard $
 */
public class EqualsBuilderTest extends TestBase {
    static class TestObject {
        private int a;

        public TestObject() {
        }

        public TestObject(int a) {
            this.a = a;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == (this)) {
                return true;
            }
            if ((o.getClass()) != (getClass())) {
                return false;
            }
            EqualsBuilderTest.TestObject rhs = ((EqualsBuilderTest.TestObject) (o));
            return (a) == (rhs.a);
        }

        public int hashCode() {
            return super.hashCode();
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getA() {
            return a;
        }
    }

    static class TestSubObject extends EqualsBuilderTest.TestObject {
        private int b;

        public TestSubObject() {
            super(0);
        }

        public TestSubObject(int a, int b) {
            super(a);
            this.b = b;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == (this)) {
                return true;
            }
            if ((o.getClass()) != (getClass())) {
                return false;
            }
            EqualsBuilderTest.TestSubObject rhs = ((EqualsBuilderTest.TestSubObject) (o));
            return (super.equals(o)) && ((b) == (rhs.b));
        }

        public int hashCode() {
            return 1;
        }

        public void setB(int b) {
            this.b = b;
        }

        public int getB() {
            return b;
        }
    }

    static class TestEmptySubObject extends EqualsBuilderTest.TestObject {
        public TestEmptySubObject(int a) {
            super(a);
        }
    }

    @SuppressWarnings("unused")
    static class TestTSubObject extends EqualsBuilderTest.TestObject {
        private transient int t;

        public TestTSubObject(int a, int t) {
            super(a);
            this.t = t;
        }
    }

    @SuppressWarnings("unused")
    static class TestTTSubObject extends EqualsBuilderTest.TestTSubObject {
        private transient int tt;

        public TestTTSubObject(int a, int t, int tt) {
            super(a, t);
            this.tt = tt;
        }
    }

    @SuppressWarnings("unused")
    static class TestTTLeafObject extends EqualsBuilderTest.TestTTSubObject {
        private int leafValue;

        public TestTTLeafObject(int a, int t, int tt, int leafValue) {
            super(a, t, tt);
            this.leafValue = leafValue;
        }
    }

    static class TestTSubObject2 extends EqualsBuilderTest.TestObject {
        private transient int t;

        public TestTSubObject2(int a, int t) {
            super(a);
        }

        public int getT() {
            return t;
        }

        public void setT(int t) {
            this.t = t;
        }
    }

    @Test
    public void testReflectionEquals() {
        EqualsBuilderTest.TestObject o1 = new EqualsBuilderTest.TestObject(4);
        EqualsBuilderTest.TestObject o2 = new EqualsBuilderTest.TestObject(5);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(o1, o1));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(o1, o2))));
        o2.setA(4);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(o1, o2));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(o1, this))));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(o1, null))));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(null, o2))));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(((Object) (null)), ((Object) (null))));
    }

    @Test
    public void testReflectionHierarchyEquals() {
        testReflectionHierarchyEquals(false);
        testReflectionHierarchyEquals(true);
        // Transients
        Assert.assertTrue(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), true));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), false));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(1, 0, 0, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), true))));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 0), true))));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(new EqualsBuilderTest.TestTTLeafObject(0, 2, 3, 4), new EqualsBuilderTest.TestTTLeafObject(1, 2, 3, 4), true))));
    }

    @Test
    public void testSuper() {
        EqualsBuilderTest.TestObject o1 = new EqualsBuilderTest.TestObject(4);
        EqualsBuilderTest.TestObject o2 = new EqualsBuilderTest.TestObject(5);
        Assert.assertEquals(true, new EqualsBuilder().appendSuper(true).append(o1, o1).isEquals());
        Assert.assertEquals(false, new EqualsBuilder().appendSuper(false).append(o1, o1).isEquals());
        Assert.assertEquals(false, new EqualsBuilder().appendSuper(true).append(o1, o2).isEquals());
        Assert.assertEquals(false, new EqualsBuilder().appendSuper(false).append(o1, o2).isEquals());
    }

    @Test
    public void testObject() {
        EqualsBuilderTest.TestObject o1 = new EqualsBuilderTest.TestObject(4);
        EqualsBuilderTest.TestObject o2 = new EqualsBuilderTest.TestObject(5);
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, o2).isEquals())));
        o2.setA(4);
        Assert.assertTrue(new EqualsBuilder().append(o1, o2).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, this).isEquals())));
        Assert.assertTrue((!(new EqualsBuilder().append(o1, null).isEquals())));
        Assert.assertTrue((!(new EqualsBuilder().append(null, o2).isEquals())));
        Assert.assertTrue(new EqualsBuilder().append(((Object) (null)), ((Object) (null))).isEquals());
    }

    @Test
    public void testLong() {
        long o1 = 1L;
        long o2 = 2L;
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, o2).isEquals())));
    }

    @Test
    public void testInt() {
        int o1 = 1;
        int o2 = 2;
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, o2).isEquals())));
    }

    @Test
    public void testShort() {
        short o1 = 1;
        short o2 = 2;
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, o2).isEquals())));
    }

    @Test
    public void testChar() {
        char o1 = 1;
        char o2 = 2;
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, o2).isEquals())));
    }

    @Test
    public void testByte() {
        byte o1 = 1;
        byte o2 = 2;
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, o2).isEquals())));
    }

    @Test
    public void testDouble() {
        double o1 = 1;
        double o2 = 2;
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, o2).isEquals())));
        Assert.assertTrue((!(new EqualsBuilder().append(o1, Double.NaN).isEquals())));
        Assert.assertTrue(new EqualsBuilder().append(Double.NaN, Double.NaN).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY).isEquals());
    }

    @Test
    public void testFloat() {
        float o1 = 1;
        float o2 = 2;
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, o2).isEquals())));
        Assert.assertTrue((!(new EqualsBuilder().append(o1, Float.NaN).isEquals())));
        Assert.assertTrue(new EqualsBuilder().append(Float.NaN, Float.NaN).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY).isEquals());
    }

    // https://issues.apache.org/jira/browse/LANG-393
    @Test
    public void testBigDecimal() {
        BigDecimal o1 = new BigDecimal("2.0");
        BigDecimal o2 = new BigDecimal("2.00");
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(o1, o2).isEquals());
    }

    @Test
    public void testAccessors() {
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        Assert.assertTrue(equalsBuilder.isEquals());
        equalsBuilder.setEquals(true);
        Assert.assertTrue(equalsBuilder.isEquals());
        equalsBuilder.setEquals(false);
        Assert.assertFalse(equalsBuilder.isEquals());
    }

    @Test
    public void testBoolean() {
        boolean o1 = true;
        boolean o2 = false;
        Assert.assertTrue(new EqualsBuilder().append(o1, o1).isEquals());
        Assert.assertTrue((!(new EqualsBuilder().append(o1, o2).isEquals())));
    }

    @Test
    public void testObjectArray() {
        EqualsBuilderTest.TestObject[] obj1 = new EqualsBuilderTest.TestObject[3];
        obj1[0] = new EqualsBuilderTest.TestObject(4);
        obj1[1] = new EqualsBuilderTest.TestObject(5);
        obj1[2] = null;
        EqualsBuilderTest.TestObject[] obj2 = new EqualsBuilderTest.TestObject[3];
        obj2[0] = new EqualsBuilderTest.TestObject(4);
        obj2[1] = new EqualsBuilderTest.TestObject(5);
        obj2[2] = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj2, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1].setA(6);
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1[1].setA(5);
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[2] = obj1[1];
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1[2] = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj2 = null;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1 = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testLongArray() {
        long[] obj1 = new long[2];
        obj1[0] = 5L;
        obj1[1] = 6L;
        long[] obj2 = new long[2];
        obj2[0] = 5L;
        obj2[1] = 6L;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj2 = null;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1 = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testIntArray() {
        int[] obj1 = new int[2];
        obj1[0] = 5;
        obj1[1] = 6;
        int[] obj2 = new int[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj2 = null;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1 = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testShortArray() {
        short[] obj1 = new short[2];
        obj1[0] = 5;
        obj1[1] = 6;
        short[] obj2 = new short[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj2 = null;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1 = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testCharArray() {
        char[] obj1 = new char[2];
        obj1[0] = 5;
        obj1[1] = 6;
        char[] obj2 = new char[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj2 = null;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1 = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testByteArray() {
        byte[] obj1 = new byte[2];
        obj1[0] = 5;
        obj1[1] = 6;
        byte[] obj2 = new byte[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj2 = null;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1 = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testDoubleArray() {
        double[] obj1 = new double[2];
        obj1[0] = 5;
        obj1[1] = 6;
        double[] obj2 = new double[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj2 = null;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1 = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testFloatArray() {
        float[] obj1 = new float[2];
        obj1[0] = 5;
        obj1[1] = 6;
        float[] obj2 = new float[2];
        obj2[0] = 5;
        obj2[1] = 6;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj2 = null;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1 = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testBooleanArray() {
        boolean[] obj1 = new boolean[2];
        obj1[0] = true;
        obj1[1] = false;
        boolean[] obj2 = new boolean[2];
        obj2[0] = true;
        obj2[1] = false;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        obj1[1] = true;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj2 = null;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
        obj1 = null;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
    }

    @Test
    public void testMultiLongArray() {
        long[][] array1 = new long[2][2];
        long[][] array2 = new long[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
    }

    @Test
    public void testMultiIntArray() {
        int[][] array1 = new int[2][2];
        int[][] array2 = new int[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
    }

    @Test
    public void testMultiShortArray() {
        short[][] array1 = new short[2][2];
        short[][] array2 = new short[2][2];
        for (short i = 0; i < (array1.length); ++i) {
            for (short j = 0; j < (array1[0].length); j++) {
                array1[i][j] = i;
                array2[i][j] = i;
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
    }

    @Test
    public void testMultiCharArray() {
        char[][] array1 = new char[2][2];
        char[][] array2 = new char[2][2];
        for (char i = 0; i < (array1.length); ++i) {
            for (char j = 0; j < (array1[0].length); j++) {
                array1[i][j] = i;
                array2[i][j] = i;
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
    }

    @Test
    public void testMultiByteArray() {
        byte[][] array1 = new byte[2][2];
        byte[][] array2 = new byte[2][2];
        for (byte i = 0; i < (array1.length); ++i) {
            for (byte j = 0; j < (array1[0].length); j++) {
                array1[i][j] = i;
                array2[i][j] = i;
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
    }

    @Test
    public void testMultiFloatArray() {
        float[][] array1 = new float[2][2];
        float[][] array2 = new float[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
    }

    @Test
    public void testMultiDoubleArray() {
        double[][] array1 = new double[2][2];
        double[][] array2 = new double[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
    }

    @Test
    public void testMultiBooleanArray() {
        boolean[][] array1 = new boolean[2][2];
        boolean[][] array2 = new boolean[2][2];
        for (int i = 0; i < (array1.length); ++i) {
            for (int j = 0; j < (array1[0].length); j++) {
                array1[i][j] = (i == 1) || (j == 1);
                array2[i][j] = (i == 1) || (j == 1);
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = false;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
        // compare 1 dim to 2.
        boolean[] array3 = new boolean[]{ true, true };
        Assert.assertFalse(new EqualsBuilder().append(array1, array3).isEquals());
        Assert.assertFalse(new EqualsBuilder().append(array3, array1).isEquals());
        Assert.assertFalse(new EqualsBuilder().append(array2, array3).isEquals());
        Assert.assertFalse(new EqualsBuilder().append(array3, array2).isEquals());
    }

    @Test
    public void testRaggedArray() {
        long[][] array1 = new long[2][];
        long[][] array2 = new long[2][];
        for (int i = 0; i < (array1.length); ++i) {
            array1[i] = new long[2];
            array2[i] = new long[2];
            for (int j = 0; j < (array1[i].length); ++j) {
                array1[i][j] = (i + 1) * (j + 1);
                array2[i][j] = (i + 1) * (j + 1);
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        array1[1][1] = 0;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
    }

    @Test
    public void testMixedArray() {
        Object[] array1 = new Object[2];
        Object[] array2 = new Object[2];
        for (int i = 0; i < (array1.length); ++i) {
            array1[i] = new long[2];
            array2[i] = new long[2];
            for (int j = 0; j < 2; ++j) {
                ((long[]) (array1[i]))[j] = (i + 1) * (j + 1);
                ((long[]) (array2[i]))[j] = (i + 1) * (j + 1);
            }
        }
        Assert.assertTrue(new EqualsBuilder().append(array1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(array1, array2).isEquals());
        ((long[]) (array1[1]))[1] = 0;
        Assert.assertTrue((!(new EqualsBuilder().append(array1, array2).isEquals())));
    }

    @Test
    public void testObjectArrayHiddenByObject() {
        EqualsBuilderTest.TestObject[] array1 = new EqualsBuilderTest.TestObject[2];
        array1[0] = new EqualsBuilderTest.TestObject(4);
        array1[1] = new EqualsBuilderTest.TestObject(5);
        EqualsBuilderTest.TestObject[] array2 = new EqualsBuilderTest.TestObject[2];
        array2[0] = new EqualsBuilderTest.TestObject(4);
        array2[1] = new EqualsBuilderTest.TestObject(5);
        Object obj1 = array1;
        Object obj2 = array2;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1].setA(6);
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
    }

    @Test
    public void testLongArrayHiddenByObject() {
        long[] array1 = new long[2];
        array1[0] = 5L;
        array1[1] = 6L;
        long[] array2 = new long[2];
        array2[0] = 5L;
        array2[1] = 6L;
        Object obj1 = array1;
        Object obj2 = array2;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
    }

    @Test
    public void testIntArrayHiddenByObject() {
        int[] array1 = new int[2];
        array1[0] = 5;
        array1[1] = 6;
        int[] array2 = new int[2];
        array2[0] = 5;
        array2[1] = 6;
        Object obj1 = array1;
        Object obj2 = array2;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
    }

    @Test
    public void testShortArrayHiddenByObject() {
        short[] array1 = new short[2];
        array1[0] = 5;
        array1[1] = 6;
        short[] array2 = new short[2];
        array2[0] = 5;
        array2[1] = 6;
        Object obj1 = array1;
        Object obj2 = array2;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
    }

    @Test
    public void testCharArrayHiddenByObject() {
        char[] array1 = new char[2];
        array1[0] = 5;
        array1[1] = 6;
        char[] array2 = new char[2];
        array2[0] = 5;
        array2[1] = 6;
        Object obj1 = array1;
        Object obj2 = array2;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
    }

    @Test
    public void testByteArrayHiddenByObject() {
        byte[] array1 = new byte[2];
        array1[0] = 5;
        array1[1] = 6;
        byte[] array2 = new byte[2];
        array2[0] = 5;
        array2[1] = 6;
        Object obj1 = array1;
        Object obj2 = array2;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
    }

    @Test
    public void testDoubleArrayHiddenByObject() {
        double[] array1 = new double[2];
        array1[0] = 5;
        array1[1] = 6;
        double[] array2 = new double[2];
        array2[0] = 5;
        array2[1] = 6;
        Object obj1 = array1;
        Object obj2 = array2;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
    }

    @Test
    public void testFloatArrayHiddenByObject() {
        float[] array1 = new float[2];
        array1[0] = 5;
        array1[1] = 6;
        float[] array2 = new float[2];
        array2[0] = 5;
        array2[1] = 6;
        Object obj1 = array1;
        Object obj2 = array2;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = 7;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
    }

    @Test
    public void testBooleanArrayHiddenByObject() {
        boolean[] array1 = new boolean[2];
        array1[0] = true;
        array1[1] = false;
        boolean[] array2 = new boolean[2];
        array2[0] = true;
        array2[1] = false;
        Object obj1 = array1;
        Object obj2 = array2;
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array1).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, obj2).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(obj1, array2).isEquals());
        array1[1] = true;
        Assert.assertTrue((!(new EqualsBuilder().append(obj1, obj2).isEquals())));
    }

    public static class TestACanEqualB {
        private int a;

        public TestACanEqualB(int a) {
            this.a = a;
        }

        public boolean equals(Object o) {
            if (o == (this)) {
                return true;
            }
            if (o instanceof EqualsBuilderTest.TestACanEqualB) {
                return (this.a) == (((EqualsBuilderTest.TestACanEqualB) (o)).getA());
            }
            if (o instanceof EqualsBuilderTest.TestBCanEqualA) {
                return (this.a) == (((EqualsBuilderTest.TestBCanEqualA) (o)).getB());
            }
            return false;
        }

        public int hashCode() {
            return 1;
        }

        public int getA() {
            return this.a;
        }
    }

    public static class TestBCanEqualA {
        private int b;

        public TestBCanEqualA(int b) {
            this.b = b;
        }

        public boolean equals(Object o) {
            if (o == (this)) {
                return true;
            }
            if (o instanceof EqualsBuilderTest.TestACanEqualB) {
                return (this.b) == (((EqualsBuilderTest.TestACanEqualB) (o)).getA());
            }
            if (o instanceof EqualsBuilderTest.TestBCanEqualA) {
                return (this.b) == (((EqualsBuilderTest.TestBCanEqualA) (o)).getB());
            }
            return false;
        }

        public int hashCode() {
            return 1;
        }

        public int getB() {
            return this.b;
        }
    }

    /**
     * Tests two instances of classes that can be equal and that are not "related". The two classes are not subclasses
     * of each other and do not share a parent aside from Object.
     * See http://issues.apache.org/bugzilla/show_bug.cgi?id=33069
     */
    @Test
    public void testUnrelatedClasses() {
        Object[] x = new Object[]{ new EqualsBuilderTest.TestACanEqualB(1) };
        Object[] y = new Object[]{ new EqualsBuilderTest.TestBCanEqualA(1) };
        // sanity checks:
        Assert.assertTrue(Arrays.equals(x, x));
        Assert.assertTrue(Arrays.equals(y, y));
        Assert.assertTrue(Arrays.equals(x, y));
        Assert.assertTrue(Arrays.equals(y, x));
        // real tests:
        Assert.assertTrue(x[0].equals(x[0]));
        Assert.assertTrue(y[0].equals(y[0]));
        Assert.assertTrue(x[0].equals(y[0]));
        Assert.assertTrue(y[0].equals(x[0]));
        Assert.assertTrue(new EqualsBuilder().append(x, x).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(y, y).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(x, y).isEquals());
        Assert.assertTrue(new EqualsBuilder().append(y, x).isEquals());
    }

    /**
     * Test from http://issues.apache.org/bugzilla/show_bug.cgi?id=33067
     */
    @Test
    public void testNpeForNullElement() {
        Object[] x1 = new Object[]{ new Integer(1), null, new Integer(3) };
        Object[] x2 = new Object[]{ new Integer(1), new Integer(2), new Integer(3) };
        // causes an NPE in 2.0 according to:
        // http://issues.apache.org/bugzilla/show_bug.cgi?id=33067
        new EqualsBuilder().append(x1, x2);
    }

    @Test
    public void testReflectionEqualsExcludeFields() throws Exception {
        EqualsBuilderTest.TestObjectWithMultipleFields x1 = new EqualsBuilderTest.TestObjectWithMultipleFields(1, 2, 3);
        EqualsBuilderTest.TestObjectWithMultipleFields x2 = new EqualsBuilderTest.TestObjectWithMultipleFields(1, 3, 4);
        // not equal when including all fields
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(x1, x2))));
        // doesn't barf on null, empty array, or non-existent field, but still tests as not equal
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(x1, x2, ((String[]) (null))))));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(x1, x2, new String[]{  }))));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(x1, x2, new String[]{ "xxx" }))));
        // not equal if only one of the differing fields excluded
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(x1, x2, new String[]{ "two" }))));
        Assert.assertTrue((!(EqualsBuilder.reflectionEquals(x1, x2, new String[]{ "three" }))));
        // equal if both differing fields excluded
        Assert.assertTrue(EqualsBuilder.reflectionEquals(x1, x2, new String[]{ "two", "three" }));
        // still equal as long as both differing fields are among excluded
        Assert.assertTrue(EqualsBuilder.reflectionEquals(x1, x2, new String[]{ "one", "two", "three" }));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(x1, x2, new String[]{ "one", "two", "three", "xxx" }));
    }

    @SuppressWarnings("unused")
    static class TestObjectWithMultipleFields {
        private EqualsBuilderTest.TestObject one;

        private EqualsBuilderTest.TestObject two;

        private EqualsBuilderTest.TestObject three;

        public TestObjectWithMultipleFields(int one, int two, int three) {
            this.one = new EqualsBuilderTest.TestObject(one);
            this.two = new EqualsBuilderTest.TestObject(two);
            this.three = new EqualsBuilderTest.TestObject(three);
        }
    }
}

