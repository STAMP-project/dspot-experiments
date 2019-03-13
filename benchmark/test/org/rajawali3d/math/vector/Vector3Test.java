package org.rajawali3d.math.vector;


import Vector3.Axis;
import Vector3.Axis.X;
import Vector3.Axis.Y;
import Vector3.Axis.Z;
import Vector3.NEG_X;
import Vector3.X.x;
import Vector3.X.y;
import Vector3.X.z;
import org.junit.Assert;
import org.junit.Test;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;

import static Vector3.ONE;
import static Vector3.X;
import static Vector3.Y;
import static Vector3.Z;
import static Vector3.ZERO;


/**
 *
 *
 * @author Jared Woolston (jwoolston@keywcorp.com)
 */
public class Vector3Test {
    @Test
    public void testZero() {
        final Vector3 zero = ZERO;
        Assert.assertNotNull(zero);
        Assert.assertEquals(0, zero.x, 0);
        Assert.assertEquals(0, zero.y, 0);
        Assert.assertEquals(0, zero.z, 0);
    }

    @Test
    public void testOne() {
        final Vector3 one = ONE;
        Assert.assertNotNull(one);
        Assert.assertEquals(1.0, one.x, 0);
        Assert.assertEquals(1.0, one.y, 0);
        Assert.assertEquals(1.0, one.z, 0);
    }

    @Test
    public void testConstructorNoArgs() {
        final Vector3 v = new Vector3();
        Assert.assertNotNull(v);
        Assert.assertEquals(0, v.x, 0);
        Assert.assertEquals(0, v.y, 0);
        Assert.assertEquals(0, v.z, 0);
    }

    @Test
    public void testConstructorFromDouble() {
        final Vector3 v = new Vector3(1.0);
        Assert.assertNotNull(v);
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(1.0, v.y, 0);
        Assert.assertEquals(1.0, v.z, 0);
    }

    @Test
    public void testConstructorFromVector3() {
        final Vector3 v1 = new Vector3(2.0);
        final Vector3 v = new Vector3(v1);
        Assert.assertNotNull(v);
        Assert.assertEquals(2.0, v.x, 0);
        Assert.assertEquals(2.0, v.y, 0);
        Assert.assertEquals(2.0, v.z, 0);
    }

    @Test
    public void testConstructorFromStringArray() {
        final String[] values = new String[]{ "1", "2", "3" };
        final Vector3 v = new Vector3(values);
        Assert.assertNotNull(v);
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(2.0, v.y, 0);
        Assert.assertEquals(3.0, v.z, 0);
    }

    @Test
    public void testConstructorFromDoubleArray() {
        final double[] values = new double[]{ 1.0, 2.0, 3.0 };
        final Vector3 v = new Vector3(values);
        Assert.assertNotNull(v);
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(2.0, v.y, 0);
        Assert.assertEquals(3.0, v.z, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFromShortDoubleArray() {
        final double[] values = new double[]{ 1.0, 2.0 };
        new Vector3(values);
    }

    @Test
    public void testConstructorDoublesXyz() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        Assert.assertNotNull(v);
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(2.0, v.y, 0);
        Assert.assertEquals(3.0, v.z, 0);
    }

    @Test
    public void testSetAllFromDoublesXyz() {
        final Vector3 v = new Vector3();
        Assert.assertNotNull(v);
        final Vector3 out = v.setAll(1.0, 2.0, 3.0);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(2.0, v.y, 0);
        Assert.assertEquals(3.0, v.z, 0);
    }

    @Test
    public void testSetAllFromVector3() {
        final Vector3 v = new Vector3();
        Assert.assertNotNull(v);
        final Vector3 out = v.setAll(new Vector3(1.0, 2.0, 3.0));
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(2.0, v.y, 0);
        Assert.assertEquals(3.0, v.z, 0);
    }

    @Test
    public void testSetAllFromAxis() {
        final Vector3 v = new Vector3();
        Assert.assertNotNull(v);
        final Vector3 outX = v.setAll(X);
        Assert.assertNotNull(outX);
        Assert.assertTrue((outX == v));
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(0.0, v.y, 0);
        Assert.assertEquals(0.0, v.z, 0);
        final Vector3 outY = v.setAll(Y);
        Assert.assertNotNull(outY);
        Assert.assertTrue((outY == v));
        Assert.assertEquals(0.0, v.x, 0);
        Assert.assertEquals(1.0, v.y, 0);
        Assert.assertEquals(0.0, v.z, 0);
        final Vector3 outZ = v.setAll(Z);
        Assert.assertNotNull(outZ);
        Assert.assertTrue((outZ == v));
        Assert.assertEquals(0.0, v.x, 0);
        Assert.assertEquals(0.0, v.y, 0);
        Assert.assertEquals(1.0, v.z, 0);
    }

    @Test
    public void testAddVector3() {
        final Vector3 u = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = new Vector3(0.1, 0.2, 0.3);
        final Vector3 out = u.add(v);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == u));
        Assert.assertEquals(1.1, u.x, 0);
        Assert.assertEquals(2.2, u.y, 0);
        Assert.assertEquals(3.3, u.z, 0);
    }

    @Test
    public void testAddDoublesXyz() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final Vector3 out = v.add(0.1, 0.2, 0.3);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(1.1, v.x, 0);
        Assert.assertEquals(2.2, v.y, 0);
        Assert.assertEquals(3.3, v.z, 0);
    }

    @Test
    public void testAddDouble() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final Vector3 out = v.add(0.1);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(1.1, v.x, 0);
        Assert.assertEquals(2.1, v.y, 0);
        Assert.assertEquals(3.1, v.z, 0);
    }

    @Test
    public void testAddAndSet() {
        final Vector3 u = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = new Vector3(0.1, 0.2, 0.3);
        final Vector3 t = new Vector3();
        final Vector3 out = t.addAndSet(u, v);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == t));
        Assert.assertEquals(1.1, t.x, 0);
        Assert.assertEquals(2.2, t.y, 0);
        Assert.assertEquals(3.3, t.z, 0);
    }

    @Test
    public void testAddAndCreate() {
        final Vector3 u = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = new Vector3(0.1, 0.2, 0.3);
        final Vector3 t = Vector3.addAndCreate(u, v);
        Assert.assertNotNull(t);
        Assert.assertEquals(1.1, t.x, 0);
        Assert.assertEquals(2.2, t.y, 0);
        Assert.assertEquals(3.3, t.z, 0);
    }

    @Test
    public void testSubtractVector3() {
        final Vector3 u = new Vector3(1.1, 2.2, 3.3);
        final Vector3 v = new Vector3(0.1, 0.2, 0.3);
        final Vector3 out = u.subtract(v);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == u));
        Assert.assertEquals(1.0, u.x, 0);
        Assert.assertEquals(2.0, u.y, 0);
        Assert.assertEquals(3.0, u.z, 0);
    }

    @Test
    public void testSubtractDoublesXyz() {
        final Vector3 v = new Vector3(1.1, 2.2, 3.3);
        final Vector3 out = v.subtract(0.1, 0.2, 0.3);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(2.0, v.y, 0);
        Assert.assertEquals(3.0, v.z, 0);
    }

    @Test
    public void testSubtractDouble() {
        final Vector3 v = new Vector3(1.1, 2.1, 3.1);
        final Vector3 out = v.subtract(0.1);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(2.0, v.y, 0);
        Assert.assertEquals(3.0, v.z, 0);
    }

    @Test
    public void testSubtractAndSet() {
        final Vector3 u = new Vector3(1.1, 2.2, 3.3);
        final Vector3 v = new Vector3(0.1, 0.2, 0.3);
        final Vector3 t = new Vector3();
        final Vector3 out = t.subtractAndSet(u, v);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == t));
        Assert.assertEquals(1.0, t.x, 0);
        Assert.assertEquals(2.0, t.y, 0);
        Assert.assertEquals(3.0, t.z, 0);
    }

    @Test
    public void testSubtractAndCreate() {
        final Vector3 u = new Vector3(1.1, 2.2, 3.3);
        final Vector3 v = new Vector3(0.1, 0.2, 0.3);
        final Vector3 t = Vector3.subtractAndCreate(u, v);
        Assert.assertNotNull(t);
        Assert.assertEquals(1.0, t.x, 0);
        Assert.assertEquals(2.0, t.y, 0);
        Assert.assertEquals(3.0, t.z, 0);
    }

    @Test
    public void testMultiplyFromDouble() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final Vector3 out = v.multiply(2.0);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(2.0, v.x, 0);
        Assert.assertEquals(4.0, v.y, 0);
        Assert.assertEquals(6.0, v.z, 0);
    }

    @Test
    public void testMultiplyFromVector3() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v1 = new Vector3(2.0, 3.0, 4.0);
        final Vector3 out = v.multiply(v1);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(2.0, v.x, 0);
        Assert.assertEquals(6.0, v.y, 0);
        Assert.assertEquals(12.0, v.z, 0);
    }

    @Test
    public void testMultiplyFomDoubleMatrix() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final double[] matrix = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final Vector3 out = v.multiply(matrix);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(51.0, v.x, 0);
        Assert.assertEquals(58.0, v.y, 0);
        Assert.assertEquals(65.0, v.z, 0);
    }

    @Test
    public void testMultiplyFromMatrix4() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final double[] matrix = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final Matrix4 matrix4 = new Matrix4(matrix);
        final Vector3 out = v.multiply(matrix4);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(51.0, v.x, 0);
        Assert.assertEquals(58.0, v.y, 0);
        Assert.assertEquals(65.0, v.z, 0);
    }

    @Test
    public void testMultiplyAndSet() {
        final Vector3 v = new Vector3();
        final Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v2 = new Vector3(2.0, 3.0, 4.0);
        final Vector3 out = v.multiplyAndSet(v1, v2);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(2.0, v.x, 0);
        Assert.assertEquals(6.0, v.y, 0);
        Assert.assertEquals(12.0, v.z, 0);
    }

    @Test
    public void testMultiplyAndCreateFromTwoVector3() {
        final Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v2 = new Vector3(2.0, 3.0, 4.0);
        final Vector3 v = Vector3.multiplyAndCreate(v1, v2);
        Assert.assertNotNull(v);
        Assert.assertEquals(2.0, v.x, 0);
        Assert.assertEquals(6.0, v.y, 0);
        Assert.assertEquals(12.0, v.z, 0);
    }

    @Test
    public void testMultiplyAndCreateFromVector3Double() {
        final Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = Vector3.multiplyAndCreate(v1, 2.0);
        Assert.assertNotNull(v);
        Assert.assertEquals(2.0, v.x, 0);
        Assert.assertEquals(4.0, v.y, 0);
        Assert.assertEquals(6.0, v.z, 0);
    }

    @Test
    public void testDivideFromDouble() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final Vector3 out = v.divide(2.0);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(0.5, v.x, 0);
        Assert.assertEquals(1.0, v.y, 0);
        Assert.assertEquals(1.5, v.z, 0);
    }

    @Test
    public void testDivideFromVector3() {
        final Vector3 u = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = new Vector3(0.5, 0.25, 4.0);
        final Vector3 out = u.divide(v);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == u));
        Assert.assertEquals(2.0, u.x, 0);
        Assert.assertEquals(8.0, u.y, 0);
        Assert.assertEquals(0.75, u.z, 0);
    }

    @Test
    public void testDivideAndSet() {
        final Vector3 t = new Vector3();
        final Vector3 u = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = new Vector3(0.5, 0.25, 4.0);
        final Vector3 out = t.divideAndSet(u, v);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == t));
        Assert.assertEquals(t.x, 2.0, 0);
        Assert.assertEquals(t.y, 8.0, 0);
        Assert.assertEquals(t.z, 0.75, 0);
    }

    @Test
    public void testScaleAndSet() {
        final Vector3 t = new Vector3();
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final Vector3 out = t.scaleAndSet(v, 0.5);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == t));
        Assert.assertEquals(0.5, t.x, 0);
        Assert.assertEquals(1.0, t.y, 0);
        Assert.assertEquals(1.5, t.z, 0);
    }

    @Test
    public void testScaleAndCreate() {
        final Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = Vector3.scaleAndCreate(v1, 0.5);
        Assert.assertNotNull(v);
        Assert.assertEquals(0.5, v.x, 0);
        Assert.assertEquals(1.0, v.y, 0);
        Assert.assertEquals(1.5, v.z, 0);
    }

    @Test
    public void testRotateBy() {
        {
            final Quaternion q = Quaternion.getIdentity();
            Vector3 v = new Vector3(0, 0, 1);
            v.rotateBy(q);
            Assert.assertEquals("X", 0, v.x, 1.0E-14);
            Assert.assertEquals("Y", 0, v.y, 1.0E-14);
            Assert.assertEquals("Z", 1, v.z, 1.0E-14);
        }
        {
            final Quaternion q = new Quaternion(0.5, 0.5, 0.5, 0.5);
            Vector3 v = new Vector3(0, 0, 1);
            v.rotateBy(q);
            Assert.assertEquals("X", 1, v.x, 1.0E-14);
            Assert.assertEquals("Y", 0, v.y, 1.0E-14);
            Assert.assertEquals("Z", 0, v.z, 1.0E-14);
        }
    }

    @Test
    public void testRotateX() {
        final Vector3 v1 = new Vector3(X);
        final Vector3 v2 = new Vector3(Y);
        final Vector3 v3 = new Vector3(Z);
        v1.rotateX(Math.PI);
        v2.rotateX(Math.PI);
        v3.rotateX(((Math.PI) / 2.0));
        Assert.assertEquals(x, v1.x, 0);
        Assert.assertEquals(y, v1.y, 0);
        Assert.assertEquals(z, v1.z, 0);
        Assert.assertEquals(Vector3.NEG_Y.x, v2.x, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_Y.y, v2.y, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_Y.z, v2.z, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_Y.x, v3.x, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_Y.y, v3.y, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_Y.z, v3.z, 1.0E-14);
    }

    @Test
    public void testRotateY() {
        final Vector3 v1 = new Vector3(X);
        final Vector3 v2 = new Vector3(Y);
        final Vector3 v3 = new Vector3(Z);
        v1.rotateY(Math.PI);
        v2.rotateY(Math.PI);
        v3.rotateY(((Math.PI) / 2.0));
        Assert.assertEquals(Vector3.Y.x, v2.x, 0);
        Assert.assertEquals(Vector3.Y.y, v2.y, 0);
        Assert.assertEquals(Vector3.Y.z, v2.z, 0);
        Assert.assertEquals(Vector3.NEG_X.x, v1.x, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_X.y, v1.y, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_X.z, v1.z, 1.0E-14);
        Assert.assertEquals(x, v3.x, 1.0E-14);
        Assert.assertEquals(y, v3.y, 1.0E-14);
        Assert.assertEquals(z, v3.z, 1.0E-14);
    }

    @Test
    public void testRotateZ() {
        final Vector3 v1 = new Vector3(X);
        final Vector3 v2 = new Vector3(Y);
        final Vector3 v3 = new Vector3(Z);
        v3.rotateZ(Math.PI);
        v1.rotateZ(Math.PI);
        v2.rotateZ(((Math.PI) / 2.0));
        Assert.assertEquals(Vector3.Z.x, v3.x, 0);
        Assert.assertEquals(Vector3.Z.y, v3.y, 0);
        Assert.assertEquals(Vector3.Z.z, v3.z, 0);
        Assert.assertEquals(Vector3.NEG_X.x, v1.x, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_X.y, v1.y, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_X.z, v1.z, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_X.x, v2.x, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_X.y, v2.y, 1.0E-14);
        Assert.assertEquals(Vector3.NEG_X.z, v2.z, 1.0E-14);
    }

    @Test
    public void testNormalize() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final double mod = v.normalize();
        Assert.assertEquals(3.7416573867739413, mod, 1.0E-14);
        Assert.assertEquals(0.267261241912424, v.x, 1.0E-14);
        Assert.assertEquals(0.534522483824849, v.y, 1.0E-14);
        Assert.assertEquals(0.801783725737273, v.z, 1.0E-14);
        final Vector3 v1 = new Vector3(1.0, 0.0, 0.0);
        final double mod1 = v1.normalize();
        Assert.assertEquals(1.0, mod1, 0);
        Assert.assertEquals(1.0, v1.x, 0);
        Assert.assertEquals(0.0, v1.y, 0);
        Assert.assertEquals(0.0, v1.z, 0);
        final Vector3 v2 = new Vector3(0.0, 0.0, 0.0);
        final double mod2 = v2.normalize();
        Assert.assertEquals(0.0, mod2, 0);
        Assert.assertEquals(0.0, v2.x, 0);
        Assert.assertEquals(0.0, v2.y, 0);
        Assert.assertEquals(0.0, v2.z, 0);
    }

    @Test
    public void testOrthoNormalizeFromVector3Array() {
        final Vector3 v1 = new Vector3(X);
        final Vector3 v2 = new Vector3(Y);
        final Vector3 v3 = new Vector3(Z);
        v2.multiply(2.0);
        v3.multiply(3.0);
        Vector3.orthoNormalize(new Vector3[]{ v1, v2, v3 });
        Assert.assertEquals(1.0, v1.x, 0);
        Assert.assertEquals(0.0, v1.y, 0);
        Assert.assertEquals(0.0, v1.z, 0);
        Assert.assertEquals(0.0, v2.x, 0);
        Assert.assertEquals(1.0, v2.y, 0);
        Assert.assertEquals(0.0, v2.z, 0);
        Assert.assertEquals(0.0, v3.x, 0);
        Assert.assertEquals(0.0, v3.y, 0);
        Assert.assertEquals(1.0, v3.z, 0);
        v1.setAll(1, 1, 0);
        v2.setAll(0, 1, 1);
        v3.setAll(1, 0, 1);
        Vector3.orthoNormalize(new Vector3[]{ v1, v2, v3 });
        Assert.assertEquals(0.7071067811865475, v1.x, 1.0E-14);
        Assert.assertEquals(0.7071067811865475, v1.y, 1.0E-14);
        Assert.assertEquals(0.0, v1.z, 1.0E-14);
        Assert.assertEquals((-0.4082482904638631), v2.x, 1.0E-14);
        Assert.assertEquals(0.4082482904638631, v2.y, 1.0E-14);
        Assert.assertEquals(0.8164965809277261, v2.z, 1.0E-14);
        Assert.assertEquals(0.5773502691896256, v3.x, 1.0E-14);
        Assert.assertEquals((-0.5773502691896256), v3.y, 1.0E-14);
        Assert.assertEquals(0.5773502691896257, v3.z, 1.0E-14);
    }

    @Test
    public void testOrthoNormalizeFromTwoVector3() {
        final Vector3 v1 = new Vector3(X);
        final Vector3 v2 = new Vector3(Y);
        v2.multiply(2.0);
        Vector3.orthoNormalize(v1, v2);
        Assert.assertEquals(1.0, v1.x, 0);
        Assert.assertEquals(0.0, v1.y, 0);
        Assert.assertEquals(0.0, v1.z, 0);
        Assert.assertEquals(0.0, v2.x, 0);
        Assert.assertEquals(1.0, v2.y, 0);
        Assert.assertEquals(0.0, v2.z, 0);
        v1.setAll(1, 1, 0);
        v2.setAll(0, 1, 1);
        Vector3.orthoNormalize(v1, v2);
        Assert.assertEquals(((("v1: " + v1) + " v2: ") + v2), 0.7071067811865475, v1.x, 1.0E-14);
        Assert.assertEquals(0.7071067811865475, v1.y, 1.0E-14);
        Assert.assertEquals(0.0, v1.z, 1.0E-14);
        Assert.assertEquals((-0.4082482904638631), v2.x, 1.0E-14);
        Assert.assertEquals(0.4082482904638631, v2.y, 1.0E-14);
        Assert.assertEquals(0.8164965809277261, v2.z, 1.0E-14);
    }

    @Test
    public void testInverse() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final Vector3 out = v.inverse();
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals((-1.0), v.x, 0);
        Assert.assertEquals((-2.0), v.y, 0);
        Assert.assertEquals((-3.0), v.z, 0);
    }

    @Test
    public void testInvertAndCreate() {
        final Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = v1.invertAndCreate();
        Assert.assertNotNull(v);
        Assert.assertEquals((-1.0), v.x, 0);
        Assert.assertEquals((-2.0), v.y, 0);
        Assert.assertEquals((-3.0), v.z, 0);
    }

    @Test
    public void testLengthFromDoublesXyz() {
        final double l = Vector3.length(1.0, 2.0, 3.0);
        Assert.assertEquals(3.74165738677394, l, 1.0E-14);
    }

    @Test
    public void testLengthFromVector3() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final double l = Vector3.length(v);
        Assert.assertEquals(3.74165738677394, l, 1.0E-14);
    }

    @Test
    public void testLength2FromVector3() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final double l2 = Vector3.length2(v);
        Assert.assertEquals(14.0, l2, 1.0E-14);
    }

    @Test
    public void testLength2DoublesXyz() {
        final double l2 = Vector3.length2(1.0, 2.0, 3.0);
        Assert.assertEquals(14.0, l2, 1.0E-14);
    }

    @Test
    public void testLengthFromSelf() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final double l = v.length();
        Assert.assertEquals(3.74165738677394, l, 1.0E-14);
    }

    @Test
    public void testLength2FromSelf() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final double l2 = v.length2();
        Assert.assertEquals(14.0, l2, 1.0E-14);
    }

    @Test
    public void testDistanceToFromVector3() {
        final Vector3 v1 = new Vector3(0.0, 1.0, 2.0);
        final Vector3 v2 = new Vector3(3.0, 5.0, 7.0);
        final double distance1 = v1.distanceTo(v2);
        final double distance2 = v2.distanceTo(v1);
        Assert.assertEquals(7.07106781186548, distance1, 1.0E-14);
        Assert.assertEquals(7.07106781186548, distance2, 1.0E-14);
    }

    @Test
    public void testDistanceToFromDoublesXyz() {
        final Vector3 v1 = new Vector3(0.0, 1.0, 2.0);
        final Vector3 v2 = new Vector3(3.0, 5.0, 7.0);
        final double distance1 = v1.distanceTo(3.0, 5.0, 7.0);
        final double distance2 = v2.distanceTo(0.0, 1.0, 2.0);
        Assert.assertEquals(7.07106781186548, distance1, 1.0E-14);
        Assert.assertEquals(7.07106781186548, distance2, 1.0E-14);
    }

    @Test
    public void testDistanceToFromTwoVector3() {
        final Vector3 v1 = new Vector3(0.0, 1.0, 2.0);
        final Vector3 v2 = new Vector3(3.0, 5.0, 7.0);
        final double distance1 = Vector3.distanceTo(v1, v2);
        final double distance2 = Vector3.distanceTo(v2, v1);
        Assert.assertEquals(distance1, 7.07106781186548, 1.0E-14);
        Assert.assertEquals(distance2, 7.07106781186548, 1.0E-14);
    }

    @Test
    public void testDistanceToFromTwoPointsDoublesXyz() {
        final double distance1 = Vector3.distanceTo(0.0, 1.0, 2.0, 3.0, 5.0, 7.0);
        final double distance2 = Vector3.distanceTo(3.0, 5.0, 7.0, 0.0, 1.0, 2.0);
        Assert.assertEquals(7.07106781186548, distance1, 1.0E-14);
        Assert.assertEquals(7.07106781186548, distance2, 1.0E-14);
    }

    @Test
    public void testDistanceTo2FromVector3() {
        final Vector3 v1 = new Vector3(0.0, 1.0, 2.0);
        final Vector3 v2 = new Vector3(3.0, 5.0, 7.0);
        final double distance1 = v1.distanceTo2(v2);
        final double distance2 = v2.distanceTo2(v1);
        Assert.assertEquals(50.0, distance1, 0);
        Assert.assertEquals(50.0, distance2, 0);
    }

    @Test
    public void testDistanceTo2FromDoublesXyz() {
        final Vector3 v1 = new Vector3(0.0, 1.0, 2.0);
        final Vector3 v2 = new Vector3(3.0, 5.0, 7.0);
        final double distance1 = v1.distanceTo2(3.0, 5.0, 7.0);
        final double distance2 = v2.distanceTo2(0.0, 1.0, 2.0);
        Assert.assertEquals(50.0, distance1, 0);
        Assert.assertEquals(50.0, distance2, 0);
    }

    @Test
    public void testDistanceTo2FromTwoVector3() {
        final Vector3 v1 = new Vector3(0.0, 1.0, 2.0);
        final Vector3 v2 = new Vector3(3.0, 5.0, 7.0);
        final double distance1 = Vector3.distanceTo2(v1, v2);
        final double distance2 = Vector3.distanceTo2(v2, v1);
        Assert.assertEquals(50.0, distance1, 0);
        Assert.assertEquals(50.0, distance2, 0);
    }

    @Test
    public void testDistanceTo2FromTwoPointsDoublesXyz() {
        final double distance1 = Vector3.distanceTo2(0.0, 1.0, 2.0, 3.0, 5.0, 7.0);
        final double distance2 = Vector3.distanceTo2(3.0, 5.0, 7.0, 0.0, 1.0, 2.0);
        Assert.assertEquals(50.0, distance1, 0);
        Assert.assertEquals(50.0, distance2, 0);
    }

    @Test
    public void testAbsoluteValue() {
        final Vector3 v = new Vector3((-0.0), (-1.0), (-2.0));
        final Vector3 out = v.absoluteValue();
        Assert.assertNotNull(out);
        Assert.assertTrue((out == v));
        Assert.assertEquals(0.0, v.x, 0);
        Assert.assertEquals(1.0, v.y, 0);
        Assert.assertEquals(2.0, v.z, 0);
    }

    @Test
    public void testProjectFromVector3() {
        final Vector3 a = new Vector3(1.0, 1.0, 0.0);
        final Vector3 b = new Vector3(2.0, 0.0, 0.0);
        final Vector3 v = b.project(a);
        Assert.assertNotNull(v);
        Assert.assertTrue((v == b));
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(0.0, v.y, 0);
        Assert.assertEquals(0.0, v.z, 0);
    }

    @Test
    public void testProjectFromDoubleArrayMatrix() {
        final double[] m = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0 };
        Vector3 v = new Vector3(2.0, 3.0, 4.0);
        final Vector3 out = v.project(m);
        Assert.assertNotNull(out);
        Assert.assertSame(out, v);
        Assert.assertEquals(0.5, out.x, 1.0E-14);
        Assert.assertEquals(0.75, out.y, 1.0E-14);
        Assert.assertEquals(1.0, out.z, 1.0E-14);
    }

    @Test
    public void testProjectFromMatrix4() {
        final double[] m = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0 };
        Vector3 v = new Vector3(2.0, 3.0, 4.0);
        final Vector3 out = v.project(new Matrix4(m));
        Assert.assertNotNull(out);
        Assert.assertSame(out, v);
        Assert.assertEquals(0.5, out.x, 1.0E-14);
        Assert.assertEquals(0.75, out.y, 1.0E-14);
        Assert.assertEquals(1.0, out.z, 1.0E-14);
    }

    @Test
    public void testProjectAndCreate() {
        final Vector3 a = new Vector3(1.0, 1.0, 0.0);
        final Vector3 b = new Vector3(2.0, 0.0, 0.0);
        final Vector3 v = Vector3.projectAndCreate(a, b);
        Assert.assertNotNull(v);
        Assert.assertFalse((v == a));
        Assert.assertFalse((v == b));
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(0.0, v.y, 0);
        Assert.assertEquals(0.0, v.z, 0);
    }

    @Test
    public void testAngle() {
        final Vector3 v1 = new Vector3(X);
        final Vector3 v2 = new Vector3(Y);
        final Vector3 v = new Vector3(1.0, 1.0, 1.0);
        final double angle1 = v1.angle(v2);
        final double angle2 = v2.angle(v1);
        Assert.assertEquals(90.0, angle1, 0.0);
        Assert.assertEquals(90.0, angle2, 0.0);
        Assert.assertEquals(54.735610317245346, v.angle(Vector3.X), 1.0E-14);
        Assert.assertEquals(54.735610317245346, v.angle(Vector3.Y), 1.0E-14);
        Assert.assertEquals(54.735610317245346, v.angle(Vector3.Z), 1.0E-14);
    }

    @Test
    public void testDotFromTwoVector3() {
        final Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v2 = new Vector3(4.0, 5.0, 6.0);
        final double dot = Vector3.dot(v1, v2);
        Assert.assertEquals(32.0, dot, 0);
    }

    @Test
    public void testDotFromVector3() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v1 = new Vector3(4.0, 5.0, 6.0);
        final double dot = v.dot(v1);
        Assert.assertEquals(32.0, dot, 0);
    }

    @Test
    public void testDotFromDoublesXyz() {
        final Vector3 v = new Vector3(1.0, 2.0, 3.0);
        final double dot = v.dot(4.0, 5.0, 6.0);
        Assert.assertEquals(32.0, dot, 0);
    }

    @Test
    public void testDotFromTwoDoublesXyz() {
        final double dot = Vector3.dot(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        Assert.assertEquals(32.0, dot, 0);
    }

    @Test
    public void testCrossFromVector3() {
        final Vector3 u = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = new Vector3(4.0, 5.0, 6.0);
        final Vector3 out = u.cross(v);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == u));
        Assert.assertEquals((-3.0), u.x, 0);
        Assert.assertEquals(6.0, u.y, 0);
        Assert.assertEquals((-3.0), u.z, 0);
    }

    @Test
    public void testCrossFromDoublesXyz() {
        final Vector3 u = new Vector3(1.0, 2.0, 3.0);
        final Vector3 out = u.cross(4.0, 5.0, 6.0);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == u));
        Assert.assertEquals((-3.0), u.x, 0);
        Assert.assertEquals(6.0, u.y, 0);
        Assert.assertEquals((-3.0), u.z, 0);
    }

    @Test
    public void testCrossAndSet() {
        final Vector3 t = new Vector3();
        final Vector3 u = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = new Vector3(4.0, 5.0, 6.0);
        final Vector3 out = t.crossAndSet(u, v);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == t));
        Assert.assertEquals((-3.0), t.x, 0);
        Assert.assertEquals(6.0, t.y, 0);
        Assert.assertEquals((-3.0), t.z, 0);
    }

    @Test
    public void testCrossAndCreate() {
        final Vector3 u = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = new Vector3(4.0, 5.0, 6.0);
        final Vector3 t = Vector3.crossAndCreate(u, v);
        Assert.assertNotNull(t);
        Assert.assertEquals((-3.0), t.x, 0);
        Assert.assertEquals(6.0, t.y, 0);
        Assert.assertEquals((-3.0), t.z, 0);
    }

    @Test
    public void testGetRotationTo() {
        final Quaternion out = Vector3.X.getRotationTo(Vector3.Y);
        Assert.assertNotNull(out);
        Assert.assertEquals(0.7071067811865475, out.w, 1.0E-14);
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out.x));
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out.y));
        Assert.assertEquals(0.7071067811865475, out.z, 1.0E-14);
        final Quaternion out1 = Vector3.Y.getRotationTo(Vector3.Z);
        Assert.assertNotNull(out1);
        Assert.assertEquals(0.7071067811865475, out1.w, 1.0E-14);
        Assert.assertEquals(0.7071067811865475, out1.x, 1.0E-14);
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out1.y));
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out1.z));
        final Quaternion out2 = Vector3.X.getRotationTo(Vector3.Z);
        Assert.assertNotNull(out2);
        Assert.assertEquals(0.7071067811865475, out2.w, 1.0E-14);
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out2.x));
        Assert.assertEquals((-0.7071067811865475), out2.y, 1.0E-14);
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out2.z));
        final Quaternion out3 = Vector3.X.getRotationTo(Vector3.X);
        Assert.assertNotNull(out3);
        Assert.assertEquals(Double.doubleToRawLongBits(1.0), Double.doubleToRawLongBits(out3.w));
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out3.x));
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out3.y));
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out3.z));
        final Quaternion out4 = Vector3.X.getRotationTo(NEG_X);
        Assert.assertNotNull(out4);
        Assert.assertEquals(0.0, out4.w, 1.0E-14);
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out4.x));
        Assert.assertEquals(Double.doubleToRawLongBits(0.0), Double.doubleToRawLongBits(out4.y));
        Assert.assertEquals(Double.doubleToRawLongBits((-1.0)), Double.doubleToRawLongBits(out4.z));
    }

    @Test
    public void testLerp() {
        final Vector3 v = new Vector3(1.0, 0.0, 0.0);
        final Vector3 vp = new Vector3(0.0, 1.0, 0.0);
        final Vector3 v1 = v.lerp(vp, 0);
        Assert.assertNotNull(v1);
        Assert.assertTrue((v1 == v));
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(0.0, v.y, 0);
        Assert.assertEquals(0.0, v.z, 0);
        v.setAll(1.0, 0.0, 0.0);
        final Vector3 v2 = v.lerp(vp, 1.0);
        Assert.assertNotNull(v2);
        Assert.assertTrue((v2 == v));
        Assert.assertEquals(0.0, v.x, 0);
        Assert.assertEquals(1.0, v.y, 0);
        Assert.assertEquals(0.0, v.z, 0);
        v.setAll(1.0, 0.0, 0.0);
        final Vector3 v3 = v.lerp(vp, 0.5);
        Assert.assertNotNull(v3);
        Assert.assertTrue((v3 == v));
        Assert.assertEquals(0.5, v.x, 0);
        Assert.assertEquals(0.5, v.y, 0);
        Assert.assertEquals(0.0, v.z, 0);
    }

    @Test
    public void testLerpAndSet() {
        final Vector3 v = new Vector3(1.0, 0.0, 0.0);
        final Vector3 vp = new Vector3(0.0, 1.0, 0.0);
        final Vector3 out = new Vector3();
        final Vector3 v1 = out.lerpAndSet(v, vp, 0.0);
        Assert.assertNotNull(v1);
        Assert.assertTrue((v1 == out));
        Assert.assertEquals(1.0, v1.x, 0);
        Assert.assertEquals(0.0, v1.y, 0);
        Assert.assertEquals(0.0, v1.z, 0);
        final Vector3 v2 = out.lerpAndSet(v, vp, 1.0);
        Assert.assertNotNull(v2);
        Assert.assertTrue((v2 == out));
        Assert.assertEquals(0.0, v2.x, 0);
        Assert.assertEquals(1.0, v2.y, 0);
        Assert.assertEquals(0.0, v2.z, 0);
        final Vector3 v3 = out.lerpAndSet(v, vp, 0.5);
        Assert.assertNotNull(v3);
        Assert.assertTrue((v3 == out));
        Assert.assertEquals(0.5, v3.x, 0);
        Assert.assertEquals(0.5, v3.y, 0);
        Assert.assertEquals(0.0, v3.z, 0);
    }

    @Test
    public void testLerpAndCreate() {
        final Vector3 v = new Vector3(1.0, 0.0, 0.0);
        final Vector3 vp = new Vector3(0.0, 1.0, 0.0);
        final Vector3 v1 = Vector3.lerpAndCreate(v, vp, 0.0);
        Assert.assertNotNull(v1);
        Assert.assertEquals(1.0, v1.x, 0);
        Assert.assertEquals(0.0, v1.y, 0);
        Assert.assertEquals(0.0, v1.z, 0);
        final Vector3 v2 = Vector3.lerpAndCreate(v, vp, 1.0);
        Assert.assertNotNull(v2);
        Assert.assertEquals(0.0, v2.x, 0);
        Assert.assertEquals(1.0, v2.y, 0);
        Assert.assertEquals(0.0, v2.z, 0);
        final Vector3 v3 = Vector3.lerpAndCreate(v, vp, 0.5);
        Assert.assertNotNull(v3);
        Assert.assertEquals(0.5, v3.x, 0);
        Assert.assertEquals(0.5, v3.y, 0);
        Assert.assertEquals(0.0, v3.z, 0);
    }

    @Test
    public void testClone() {
        final Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v = v1.clone();
        Assert.assertNotNull(v);
        Assert.assertFalse((v == v1));
        Assert.assertEquals(1.0, v.x, 0);
        Assert.assertEquals(2.0, v.y, 0);
        Assert.assertEquals(3.0, v.z, 0);
    }

    @Test
    public void testIsUnit() {
        Assert.assertTrue(Vector3.X.isUnit());
        Assert.assertTrue(Vector3.Y.isUnit());
        Assert.assertTrue(Vector3.Z.isUnit());
        Assert.assertFalse(new Vector3(1).isUnit());
        Assert.assertFalse(new Vector3(0).isUnit());
    }

    @Test
    public void testIsUnitWithMargin() {
        Assert.assertTrue(Vector3.X.isUnit(0.1));
        Assert.assertTrue(Vector3.Y.isUnit(0.1));
        Assert.assertTrue(Vector3.Z.isUnit(0.1));
        Assert.assertFalse(new Vector3(1.0).isUnit(0.1));
        Assert.assertFalse(new Vector3(0.0).isUnit(0.1));
        Assert.assertTrue(new Vector3(0.95, 0.0, 0.0).isUnit(0.316227766016838));
        Assert.assertFalse(new Vector3(0.95, 0.0, 0.0).isUnit(0.05));
    }

    @Test
    public void testIsZero() {
        Assert.assertFalse(Vector3.X.isZero());
        Assert.assertFalse(Vector3.Y.isZero());
        Assert.assertFalse(Vector3.Z.isZero());
        Assert.assertFalse(new Vector3(1).isZero());
        Assert.assertTrue(new Vector3(0).isZero());
    }

    @Test
    public void testIsZeroWithMargin() {
        Assert.assertFalse(Vector3.X.isZero(0.1));
        Assert.assertFalse(Vector3.Y.isZero(0.1));
        Assert.assertFalse(Vector3.Z.isZero(0.1));
        Assert.assertFalse(new Vector3(1).isZero(0.1));
        Assert.assertTrue(new Vector3(0).isZero(0.1));
        Assert.assertTrue(new Vector3(0.1, 0.0, 0.0).isZero(0.1));
    }

    @Test
    public void testGetAxisVector() {
        Assert.assertEquals(Vector3.X, Vector3.getAxisVector(X));
        Assert.assertEquals(Vector3.Y, Vector3.getAxisVector(Y));
        Assert.assertEquals(Vector3.Z, Vector3.getAxisVector(Z));
    }

    @Test(expected = NullPointerException.class)
    public void testGetAxisVectorWithNull() {
        Vector3.getAxisVector(null);
    }

    @Test
    public void testAxisValueOf() {
        Assert.assertEquals(X, Axis.valueOf("X"));
        Assert.assertEquals(Y, Axis.valueOf("Y"));
        Assert.assertEquals(Z, Axis.valueOf("Z"));
    }

    @SuppressWarnings({ "EqualsBetweenInconvertibleTypes", "ObjectEqualsNull" })
    @Test
    public void testEquals() {
        final Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v2 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v3 = new Vector3(4.0, 5.0, 6.0);
        final Vector3 v4 = new Vector3(1.0, 5.0, 6.0);
        final Vector3 v5 = new Vector3(1.0, 2.0, 6.0);
        Assert.assertTrue(v1.equals(v2));
        Assert.assertFalse(v1.equals(v3));
        Assert.assertFalse(v1.equals(v4));
        Assert.assertFalse(v1.equals(v5));
        Assert.assertFalse(v1.equals("WRONG"));
        Assert.assertFalse(v1.equals(null));
    }

    @Test
    public void testEqualsWithError() {
        final Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v2 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v3 = new Vector3(4.0, 5.0, 6.0);
        final Vector3 v4 = new Vector3(1.0, 5.0, 6.0);
        final Vector3 v5 = new Vector3(1.0, 2.0, 6.0);
        final Vector3 v6 = new Vector3(1.1, 2.0, 3.0);
        final Vector3 v7 = new Vector3(1.1, 2.1, 3.0);
        final Vector3 v8 = new Vector3(1.1, 2.1, 3.1);
        Assert.assertTrue(v1.equals(v2, 0.0));
        Assert.assertFalse(v1.equals(v3, 1.0));
        Assert.assertFalse(v1.equals(v4, 1.0));
        Assert.assertFalse(v1.equals(v5, 1.0));
        Assert.assertTrue(v1.equals(v6, 0.2));
        Assert.assertTrue(v1.equals(v7, 0.2));
        Assert.assertTrue(v1.equals(v8, 0.2));
    }

    @Test
    public void testToString() {
        final Vector3 v = new Vector3();
        Assert.assertNotNull(v.toString());
    }
}

