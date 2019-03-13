package org.rajawali3d.math;


import Vector3.X;
import Vector3.Y;
import Vector3.Z;
import Vector3.ZERO;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.rajawali3d.math.vector.Vector3;


/**
 *
 *
 * @author Jared Woolston (jwoolston@keywcorp.com)
 */
public class Matrix4Test {
    @Test
    public void testConstructorNoArgs() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        Assert.assertNotNull(m);
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testConstructorFromMatrix4() {
        final double[] expected = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final Matrix4 from = new Matrix4(expected);
        Assert.assertNotNull(from);
        final Matrix4 m = new Matrix4(from);
        Assert.assertNotNull(m);
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        Assert.assertFalse((result == expected));
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testConstructorFromDoubleArray() {
        final double[] expected = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final Matrix4 m = new Matrix4(expected);
        Assert.assertNotNull(m);
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        Assert.assertFalse((result == expected));
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testConstructorFromFloatArray() {
        final float[] expected = new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F, 10.0F, 11.0F, 12.0F, 13.0F, 14.0F, 15.0F, 16.0F };
        final Matrix4 m = new Matrix4(expected);
        Assert.assertNotNull(m);
        final float[] result = m.getFloatValues();
        Assert.assertNotNull(result);
        Assert.assertFalse((result == expected));
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testConstructorFromQuaternion() {
        final double[] expected = new double[]{ 0.6603582554517136, 0.7019626168224298, -0.26724299065420565, 0.0, -0.55803738317757, 0.6966355140186917, 0.4511214953271028, 0.0, 0.5027570093457944, -0.1488785046728972, 0.8515732087227414, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Quaternion q = new Quaternion(0.8958236433584459, (-0.16744367165578425), (-0.2148860452915898), (-0.3516317104771469));
        final Matrix4 m = new Matrix4(q);
        Assert.assertNotNull(m);
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetAllFromMatrix4() {
        final double[] expected = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final Matrix4 from = new Matrix4(expected);
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setAll(from);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        Assert.assertFalse((result == expected));
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetAllFromDoubleArray() {
        final double[] expected = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setAll(expected);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        Assert.assertFalse((result == expected));
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetAllFromFloatArray() {
        final float[] from = new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F, 10.0F, 11.0F, 12.0F, 13.0F, 14.0F, 15.0F, 16.0F };
        final double[] expected = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setAll(from);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        Assert.assertFalse((result == expected));
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetAllFromQuaternion() {
        final float[] from = new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F, 10.0F, 11.0F, 12.0F, 13.0F, 14.0F, 15.0F, 16.0F };
        final double[] expected = new double[]{ 0.6603582554517136, 0.7019626168224298, -0.26724299065420565, 0.0, -0.55803738317757, 0.6966355140186917, 0.4511214953271028, 0.0, 0.5027570093457944, -0.1488785046728972, 0.8515732087227414, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4(from);
        final Quaternion q = new Quaternion(0.8958236433584459, (-0.16744367165578425), (-0.2148860452915898), (-0.3516317104771469));
        final Matrix4 out = m.setAll(q);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        Assert.assertFalse((result == expected));
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetAllFromQuaternionComponents() {
        final float[] from = new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F, 10.0F, 11.0F, 12.0F, 13.0F, 14.0F, 15.0F, 16.0F };
        final double[] expected = new double[]{ 0.6603582554517136, 0.7019626168224298, -0.26724299065420565, 0.0, -0.55803738317757, 0.6966355140186917, 0.4511214953271028, 0.0, 0.5027570093457944, -0.1488785046728972, 0.8515732087227414, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4(from);
        final Matrix4 out = m.setAll(0.8958236433584459, (-0.16744367165578425), (-0.2148860452915898), (-0.3516317104771469));
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        Assert.assertFalse((result == expected));
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetAllFromAxesAndPosition() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 2.0, 3.0, 4.0, 1.0 };
        final Vector3 position = new Vector3(2.0, 3.0, 4.0);
        final Vector3 forward = new Vector3(0.0, 1.0, 1.0);
        final Vector3 up = new Vector3(0.0, 1.0, (-1.0));
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setAll(X, up, forward, position);
        Assert.assertNotNull(out);
        Assert.assertSame(out, m);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetAllFromPositionScaleRotation() {
        final double[] expected = new double[]{ 0.6603582554517136, 1.4039252336448595, -0.26724299065420565, 0.0, -0.55803738317757, 1.3932710280373835, 0.4511214953271028, 0.0, 0.5027570093457944, -0.2977570093457944, 0.8515732087227414, 0.0, 2.0, 3.0, 4.0, 1.0 };
        final Quaternion rotation = new Quaternion(0.8958236433584459, (-0.16744367165578425), (-0.2148860452915898), (-0.3516317104771469));
        final Vector3 position = new Vector3(2.0, 3.0, 4.0);
        final Vector3 scale = new Vector3(1.0, 2.0, 1.0);
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setAll(position, scale, rotation);
        Assert.assertNotNull(out);
        Assert.assertSame(out, m);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testIdentity() {
        final double[] from = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4(from);
        final Matrix4 out = m.identity();
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testZero() {
        final double[] from = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final double[] expected = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        final Matrix4 m = new Matrix4(from);
        final Matrix4 out = m.zero();
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testDeterminant() {
        final double[] from = new double[]{ 16.0, 2.0, 3.0, 13.0, 5.0, 11.0, 10.0, 8.0, 9.0, 7.0, 6.0, 12.0, 4.0, 8.0, 12.0, 19.0 };
        final double expected = -3672.0;
        final Matrix4 m = new Matrix4(from);
        final double result = m.determinant();
        Assert.assertEquals(expected, result, 1.0E-14);
    }

    @Test
    public void testInverse() {
        final double[] from = new double[]{ 16.0, 5.0, 9.0, 4.0, 2.0, 11.0, 7.0, 8.0, 3.0, 10.0, 6.0, 12.0, 13.0, 8.0, 12.0, 19.0 };
        final double[] singular = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0 };
        final double[] expected = new double[]{ 0.1122004357298475, -0.19281045751633988, 0.22222222222222224, -0.08278867102396514, 0.08088235294117647, -0.0661764705882353, 0.25, -0.14705882352941177, -0.11683006535947714, 0.428921568627451, -0.5833333333333334, 0.2124183006535948, -0.03703703703703704, -0.11111111111111112, 0.11111111111111112, 0.03703703703703704 };
        final Matrix4 m = new Matrix4(from);
        final Matrix4 out = m.inverse();
        Assert.assertNotNull(out);
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
        final Matrix4 sing = new Matrix4(singular);
        boolean thrown = false;
        try {
            sing.inverse();
        } catch (IllegalStateException e) {
            thrown = true;
        } finally {
            Assert.assertTrue(thrown);
        }
    }

    @Test
    public void testInverseMultiplication() {
        /* given that a matrix is invertable, test that:
        - a matrix times it's inverse equals the identity
        - an inverse times it's matrix equals the identity
         */
        final double[] seed = new double[]{ 4, 1, 1, 1, 1, 4, 1, 1, 1, 1, 4, 1, 1, 1, 1, 4 };
        final double[] expected = new Matrix4().identity().getDoubleValues();
        final Matrix4 charm = new Matrix4(seed);
        final Matrix4 strange = new Matrix4(seed);
        strange.inverse();
        final double[] result1 = charm.clone().multiply(strange).getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals("matrix times inverse", expected[i], result1[i], 1.0E-14);
        }
        final double[] result2 = strange.clone().multiply(charm).getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals("inverse times matrix", expected[i], result2[i], 1.0E-14);
        }
    }

    @Test
    public void testTranspose() {
        final double[] from = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final double[] expected = new double[]{ 1.0, 5.0, 9.0, 13.0, 2.0, 6.0, 10.0, 14.0, 3.0, 7.0, 11.0, 15.0, 4.0, 8.0, 12.0, 16.0 };
        final Matrix4 m = new Matrix4(from);
        final Matrix4 out = m.transpose();
        Assert.assertNotNull(out);
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testAdd() {
        final double[] from = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final double[] add = new double[]{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
        final double[] expected = new double[]{ 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0 };
        final Matrix4 fromM = new Matrix4(from);
        final Matrix4 addM = new Matrix4(add);
        final Matrix4 out = fromM.add(addM);
        Assert.assertNotNull(out);
        Assert.assertEquals(fromM, out);
        final double[] result = fromM.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSubtract() {
        final double[] from = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final double[] subtract = new double[]{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
        final double[] expected = new double[]{ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0 };
        final Matrix4 fromM = new Matrix4(from);
        final Matrix4 subtractM = new Matrix4(subtract);
        final Matrix4 out = fromM.subtract(subtractM);
        Assert.assertNotNull(out);
        Assert.assertEquals(fromM, out);
        final double[] result = fromM.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testMultiply() {
        final double[] from = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final double[] multiply = new double[]{ 15.0, 14.0, 13.0, 12.0, 11.0, 10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0 };
        final double[] expected = new double[]{ 358.0, 412.0, 466.0, 520.0, 246.0, 284.0, 322.0, 360.0, 134.0, 156.0, 178.0, 200.0, 22.0, 28.0, 34.0, 40.0 };
        final Matrix4 fromM = new Matrix4(from);
        final Matrix4 multiplyM = new Matrix4(multiply);
        final Matrix4 out = fromM.multiply(multiplyM);
        Assert.assertNotNull(out);
        Assert.assertEquals(fromM, out);
        final double[] result = fromM.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Index " + i) + " Result: ") + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testLeftMultiply() {
        final double[] from = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final double[] multiply = new double[]{ 15.0, 14.0, 13.0, 12.0, 11.0, 10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0 };
        final double[] expected = new double[]{ 70.0, 60.0, 50.0, 40.0, 214.0, 188.0, 162.0, 136.0, 358.0, 316.0, 274.0, 232.0, 502.0, 444.0, 386.0, 328.0 };
        final Matrix4 fromM = new Matrix4(from);
        final Matrix4 multiplyM = new Matrix4(multiply);
        final Matrix4 out = fromM.leftMultiply(multiplyM);
        Assert.assertNotNull(out);
        Assert.assertEquals(fromM, out);
        final double[] result = fromM.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Index " + i) + " Result: ") + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testMultiplyDouble() {
        final double[] from = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final double factor = 2.0;
        final double[] expected = new double[]{ 2.0, 4.0, 6.0, 8.0, 10.0, 12.0, 14.0, 16.0, 18.0, 20.0, 22.0, 24.0, 26.0, 28.0, 30.0, 32.0 };
        final Matrix4 fromM = new Matrix4(from);
        final Matrix4 out = fromM.multiply(factor);
        Assert.assertNotNull(out);
        Assert.assertEquals(fromM, out);
        final double[] result = fromM.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testTranslateWithVector3() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0// Col 0
        , 0.0, 1.0, 0.0, 0.0// Col 1
        , 0.0, 0.0, 1.0, 0.0// Col 2
        , 1.0, 2.0, 3.0, 1.0// Col 3
         };
        final Matrix4 m = new Matrix4();
        m.identity();
        final Matrix4 out = m.translate(new Vector3(1, 2, 3));
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testTranslateFromDoubles() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0// Col 0
        , 0.0, 1.0, 0.0, 0.0// Col 1
        , 0.0, 0.0, 1.0, 0.0// Col 2
        , 1.0, 2.0, 3.0, 1.0// Col 3
         };
        final Matrix4 m = new Matrix4();
        m.identity();
        final Matrix4 out = m.translate(1, 2, 3);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testNegTranslate() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0// Col 0
        , 0.0, 1.0, 0.0, 0.0// Col 1
        , 0.0, 0.0, 1.0, 0.0// Col 2
        , -1.0, -2.0, -3.0, 1.0// Col 3
         };
        final Matrix4 m = new Matrix4();
        m.identity();
        final Matrix4 out = m.negTranslate(new Vector3(1, 2, 3));
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testScaleFromVector3() {
        final double[] expected = new double[]{ 2.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        m.identity();
        final Matrix4 out = m.scale(new Vector3(2, 3, 4));
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testScaleFromDoubles() {
        final double[] expected = new double[]{ 2.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        m.identity();
        final Matrix4 out = m.scale(2, 3, 4);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testScaleFromDouble() {
        final double[] expected = new double[]{ 2.0, 0.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        m.identity();
        final Matrix4 out = m.scale(2.0);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testRotateWithQuaternion() {
        final Matrix4 e = new Matrix4();
        double[] expected;
        final Matrix4 m = new Matrix4();
        double[] result;
        // Test X Axis
        m.rotate(new Quaternion(Vector3.X, 20.0));
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.X, 20.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("X - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
        // Test Y
        m.identity();
        m.rotate(new Quaternion(Vector3.Y, 30.0));
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.Y, 30.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Y - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
        // Test Z
        m.identity();
        m.rotate(new Quaternion(Vector3.Z, 40.0));
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.Z, 40.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Z - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testRotateWithVector3AxisAngle() {
        final Matrix4 e = new Matrix4();
        double[] expected;
        final Matrix4 m = new Matrix4();
        double[] result;
        // Test X Axis
        m.rotate(X, 20);
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.X, 20.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("X - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
        // Test Y
        m.identity();
        m.rotate(Y, 30);
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.Y, 30.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Y - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
        // Test Z
        m.identity();
        m.rotate(Z, 40);
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.Z, 40.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Z - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testRotateWithAxisAngle() {
        final Matrix4 e = new Matrix4();
        double[] expected;
        final Matrix4 m = new Matrix4();
        double[] result;
        // Test X Axis
        m.rotate(Axis.X, 20);
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.X, 20.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("X - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
        // Test Y
        m.identity();
        m.rotate(Axis.Y, 30);
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.Y, 30.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Y - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
        // Test Z
        m.identity();
        m.rotate(Axis.Z, 40);
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.Z, 40.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Z - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testRotateDoubleAxisAngle() {
        final Matrix4 e = new Matrix4();
        double[] expected;
        final Matrix4 m = new Matrix4();
        double[] result;
        // Test X Axis
        m.rotate(1.0, 0.0, 0.0, 20.0);
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.X, 20.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("X - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
        // Test Y
        m.identity();
        m.rotate(0.0, 1.0, 0.0, 30.0);
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.Y, 30.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Y - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
        // Test Z
        m.identity();
        m.rotate(0.0, 0.0, 1.0, 40.0);
        result = m.getDoubleValues();
        Assert.assertNotNull(result);
        e.setAll(new Quaternion(Vector3.Z, 40.0));
        expected = e.getDoubleValues();
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Z - Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testRotateBetweenTwoVectors() {
        final double[] expected = new double[]{ 0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Quaternion q = new Quaternion();
        q.fromRotationBetween(X, Y);
        final Matrix4 out = new Matrix4(q);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetTranslationFromVector3() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0// Col 0
        , 0.0, 1.0, 0.0, 0.0// Col 1
        , 0.0, 0.0, 1.0, 0.0// Col 2
        , 1.0, 2.0, 3.0, 1.0// Col 3
         };
        final Matrix4 m = new Matrix4();
        m.identity();
        final Matrix4 out = m.setTranslation(new Vector3(1, 2, 3));
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetTranslationFromDoubles() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0// Col 0
        , 0.0, 1.0, 0.0, 0.0// Col 1
        , 0.0, 0.0, 1.0, 0.0// Col 2
        , 1.0, 2.0, 3.0, 1.0// Col 3
         };
        final Matrix4 m = new Matrix4();
        m.identity();
        final Matrix4 out = m.setTranslation(1, 2, 3);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetCoordinateZoom() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 2.0 };
        final Matrix4 m = new Matrix4();
        m.identity();
        final Matrix4 out = m.setCoordinateZoom(2.0);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testRotateVector() {
        final Matrix4 m = new Matrix4(new Quaternion(Vector3.X, 45.0));
        final Vector3 v = new Vector3(0.0, 1.0, 0.0);
        m.rotateVector(v);
        Assert.assertEquals(0.0, v.x, 1.0E-14);
        Assert.assertEquals(0.7071067811865475, v.y, 1.0E-14);
        Assert.assertEquals((-0.7071067811865475), v.z, 1.0E-14);
    }

    @Test
    public void testProjectVector() {
        final double[] m = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0 };
        Vector3 v = new Vector3(2.0, 3.0, 4.0);
        final Matrix4 matrix = new Matrix4(m);
        final Vector3 out = matrix.projectVector(v);
        Assert.assertNotNull(out);
        Assert.assertSame(out, v);
        Assert.assertEquals(0.5, out.x, 1.0E-14);
        Assert.assertEquals(0.75, out.y, 1.0E-14);
        Assert.assertEquals(1.0, out.z, 1.0E-14);
    }

    @Test
    public void testProjectAndCreateVector() {
        final double[] m = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0 };
        Vector3 v = new Vector3(2.0, 3.0, 4.0);
        final Matrix4 matrix = new Matrix4(m);
        final Vector3 out = matrix.projectAndCreateVector(v);
        Assert.assertNotNull(out);
        Assert.assertTrue((out != v));
        Assert.assertEquals(0.5, out.x, 1.0E-14);
        Assert.assertEquals(0.75, out.y, 1.0E-14);
        Assert.assertEquals(1.0, out.z, 1.0E-14);
    }

    @Test
    public void testLerp() {
        final double[] expected = new double[]{ 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 };
        final Matrix4 zero = new Matrix4();
        zero.zero();
        final Matrix4 one = new Matrix4();
        one.setAll(new double[]{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 });
        final Matrix4 out = zero.lerp(one, 0.5);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToNormalMatrix() {
        final double[] from = new double[]{ 0.6603582554517136, 0.7019626168224298, -0.26724299065420565, 0.0, -0.55803738317757, 0.6966355140186917, 0.4511214953271028, 0.0, 0.5027570093457944, -0.1488785046728972, 0.8515732087227414, 0.0, 2.0, 3.0, 4.0, 1.0 };
        final double[] expected = new double[]{ 0.660211240510574, 0.7018151895155996, -0.2670828890740923, 0.0, -0.5578276574045106, 0.6965042017970164, 0.45110187226905063, 0.0, 0.5026988507104198, -0.14872805483576382, 0.851508961743878, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4(from);
        final Matrix4 out = m.setToNormalMatrix();
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToPerspective() {
        final double[] expected = new double[]{ 1.3323467750529825, 0.0, 0.0, 0.0, 0.0, 1.7320508075688774, 0.0, 0.0, 0.0, 0.0, -1.002002002002002, -1.0, 0.0, 0.0, -2.002002002002002, 0.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToPerspective(1, 1000, 60, 1.3);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToOrthographic2D() {
        final double[] expected = new double[]{ 0.001953125, 0.0, 0.0, 0.0, 0.0, 0.00390625, 0.0, 0.0, 0.0, 0.0, -2.0, 0.0, -1.0, -1.0, -1.0, 1.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToOrthographic2D(0.0, 0.0, 1024.0, 512.0);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToOrthographic2D1() {
        final double[] expected = new double[]{ 0.001953125, 0.0, 0.0, 0.0, 0.0, 0.00390625, 0.0, 0.0, 0.0, 0.0, -0.20202020202020202, 0.0, -1.0, -1.0, -1.02020202020202, 1.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToOrthographic2D(0.0, 0.0, 1024.0, 512.0, 0.1, 10.0);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToOrthographic() {
        final double[] expected = new double[]{ 2.0, 0.0, 0.0, 0.0, 0.0, 1.25, 0.0, 0.0, 0.0, 0.0, -2.5, 0.0, -0.0, -0.0, -1.25, 1.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToOrthographic((-0.5), 0.5, (-0.8), 0.8, 0.1, 0.9);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(((("Result: " + (Arrays.toString(result))) + " Expected: ") + (Arrays.toString(expected))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToTranslationFromVector3() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 2.0, 3.0, 1.0 };
        final Matrix4 m = new Matrix4();
        m.zero();
        final Matrix4 out = m.setToTranslation(new Vector3(1, 2, 3));
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToTranslationFromDoubles() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0// Col 0
        , 0.0, 1.0, 0.0, 0.0// Col 1
        , 0.0, 0.0, 1.0, 0.0// Col 2
        , 1.0, 2.0, 3.0, 1.0// Col 3
         };
        final Matrix4 m = new Matrix4();
        m.zero();
        final Matrix4 out = m.setToTranslation(1, 2, 3);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToScaleFromVector3() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        m.zero();
        final Matrix4 out = m.setToScale(new Vector3(1, 2, 3));
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToScaleFromDoubles() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        m.zero();
        final Matrix4 out = m.setToScale(1, 2, 3);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToTranslationAndScalingFromVector3s() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0// Col 0
        , 0.0, 2.0, 0.0, 0.0// Col 1
        , 0.0, 0.0, 3.0, 0.0// Col 2
        , 3.0, 2.0, 1.0, 1.0// Col 3
         };
        final Matrix4 m = new Matrix4();
        m.zero();
        final Matrix4 out = m.setToTranslationAndScaling(new Vector3(3, 2, 1), new Vector3(1, 2, 3));
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToTranslationAndScalingFromDoubles() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0// Col 0
        , 0.0, 2.0, 0.0, 0.0// Col 1
        , 0.0, 0.0, 3.0, 0.0// Col 2
        , 3.0, 2.0, 1.0, 1.0// Col 3
         };
        final Matrix4 m = new Matrix4();
        m.zero();
        final Matrix4 out = m.setToTranslationAndScaling(3, 2, 1, 1, 2, 3);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == m));
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToRotationVector3AxisAngle() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 0.7071067811865475, -0.7071067811865476, 0.0, 0.0, 0.7071067811865476, 0.7071067811865475, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToRotation(X, 45.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToRotationAxisAngle() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 0.7071067811865475, -0.7071067811865476, 0.0, 0.0, 0.7071067811865476, 0.7071067811865475, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToRotation(Axis.X, 45.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToRotationDoublesAxisAngle() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 0.7071067811865475, -0.7071067811865476, 0.0, 0.0, 0.7071067811865476, 0.7071067811865475, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToRotation(1.0, 0.0, 0.0, 45.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToRotationTwoVector3() {
        final double[] expected = new double[]{ 0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        final Vector3 v1 = new Vector3(1.0, 0.0, 0.0);
        final Vector3 v2 = new Vector3(0.0, 1.0, 0.0);
        final Matrix4 out = m.setToRotation(v1, v2);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToRotationTwoVectorsDoubles() {
        final double[] expected = new double[]{ 0.0, -1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToRotation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToRotationEulerAngles() {
        final double[] expected = new double[]{ 0.8825641192593856, -0.44096961052988237, 0.1631759111665348, 0.0, 0.4698463103929541, 0.8137976813493737, -0.34202014332566866, 0.0, 0.018028311236297265, 0.37852230636979245, 0.9254165783983234, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToRotation(10.0, 20.0, 30.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToLookAtDirectionUp() {
        final Quaternion q = new Quaternion(1.0, 2.0, 3.0, 4.0);
        final Vector3 lookAt = Vector3.subtractAndCreate(new Vector3(0, 10.0, 10.0), ZERO);
        q.lookAt(lookAt, Y);
        final double[] expected = q.toRotationMatrix().getDoubleValues();
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToLookAt(lookAt, Y);
        Assert.assertNotNull(out);
        Assert.assertSame(out, m);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToLookAtPositionTargetUp() {
        final Quaternion q = new Quaternion(1.0, 2.0, 3.0, 4.0);
        final Vector3 lookAt = Vector3.subtractAndCreate(new Vector3(0, 10.0, 10.0), ZERO);
        q.lookAt(lookAt, Y);
        final double[] expected = q.toRotationMatrix().getDoubleValues();
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToLookAt(ZERO, new Vector3(0, 10.0, 10.0), Y);
        Assert.assertNotNull(out);
        Assert.assertSame(out, m);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testSetToWorld() {
        final double[] expected = new double[]{ -1.0, 0.0, 0.0, 0.0, 0.0, 0.7071067811865476, -0.7071067811865476, 0.0, 0.0, -0.7071067811865475, -0.7071067811865475, 0.0, 2.0, 3.0, 4.0, 1.0 };
        final Vector3 position = new Vector3(2.0, 3.0, 4.0);
        final Vector3 forward = new Vector3(0.0, 1.0, 1.0);
        final Vector3 up = new Vector3(0.0, 1.0, (-1.0));
        final Matrix4 m = new Matrix4();
        final Matrix4 out = m.setToWorld(position, forward, up);
        Assert.assertNotNull(out);
        Assert.assertSame(out, m);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testGetTranslation() {
        final double[] from = new double[]{ 1.0, 0.0, 0.0, 0.0// Col 0
        , 0.0, 2.0, 0.0, 0.0// Col 1
        , 0.0, 0.0, 3.0, 0.0// Col 2
        , 1.0, 2.0, 3.0, 1.0// Col 3
         };
        final Vector3 expected = new Vector3(1, 2, 3);
        final Matrix4 m = new Matrix4(from);
        final Vector3 out = m.getTranslation();
        Assert.assertNotNull(out);
        Assert.assertTrue(expected.equals(out, 1.0E-14));
    }

    @Test
    public void testGetScalingNoArgs() {
        final double[] from = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Vector3 expected = new Vector3(1, 2, 3);
        final Matrix4 m = new Matrix4(from);
        final Vector3 out = m.getScaling();
        Assert.assertNotNull(out);
        Assert.assertTrue(expected.equals(out, 1.0E-14));
    }

    @Test
    public void testGetScalingInVector3() {
        final double[] from = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Vector3 expected = new Vector3(1, 2, 3);
        final Vector3 setIn = new Vector3(0, 0, 0);
        final Matrix4 m = new Matrix4(from);
        final Vector3 out = m.getScaling(setIn);
        Assert.assertNotNull(out);
        Assert.assertTrue((out == setIn));
        Assert.assertTrue(expected.equals(out, 1.0E-14));
    }

    @Test
    public void testCreateRotationMatrixFromQuaternion() {
        final double[] expected = new double[]{ 0.6603582554517136, 0.7019626168224298, -0.26724299065420565, 0.0, -0.55803738317757, 0.6966355140186917, 0.4511214953271028, 0.0, 0.5027570093457944, -0.1488785046728972, 0.8515732087227414, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Quaternion q = new Quaternion(0.8958236433584459, (-0.16744367165578425), (-0.2148860452915898), (-0.3516317104771469));
        final Matrix4 out = Matrix4.createRotationMatrix(q);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testCreateRotationMatrixVector3AxisAngle() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 0.7071067811865475, -0.7071067811865476, 0.0, 0.0, 0.7071067811865476, 0.7071067811865475, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 out = Matrix4.createRotationMatrix(X, 45.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testCreateRotationMatrixAxisAngle() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 0.7071067811865475, -0.7071067811865476, 0.0, 0.0, 0.7071067811865476, 0.7071067811865475, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 out = Matrix4.createRotationMatrix(Axis.X, 45.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testCreateRotationMatrixDoublesAxisAngle() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 0.7071067811865475, -0.7071067811865476, 0.0, 0.0, 0.7071067811865476, 0.7071067811865475, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 out = Matrix4.createRotationMatrix(1.0, 0.0, 0.0, 45.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testCreateRotationMatrixEulerAngles() {
        final double[] expected = new double[]{ 0.8825641192593856, -0.44096961052988237, 0.1631759111665348, 0.0, 0.4698463103929541, 0.8137976813493737, -0.34202014332566866, 0.0, 0.018028311236297265, 0.37852230636979245, 0.9254165783983234, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 out = Matrix4.createRotationMatrix(10.0, 20.0, 30.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(("Result: " + (Arrays.toString(result))), expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testCreateTranslationMatrixVector3() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 2.0, 3.0, 1.0 };
        final Matrix4 out = Matrix4.createTranslationMatrix(new Vector3(1.0, 2.0, 3.0));
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testCreateTranslationMatrixDoubles() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 2.0, 3.0, 1.0 };
        final Matrix4 out = Matrix4.createTranslationMatrix(1.0, 2.0, 3.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testCreateScaleMatrixVector3() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 out = Matrix4.createScaleMatrix(new Vector3(1.0, 2.0, 3.0));
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testCreateScaleMatrixDoubles() {
        final double[] expected = new double[]{ 2.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 out = Matrix4.createScaleMatrix(2.0, 3.0, 4.0);
        Assert.assertNotNull(out);
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testGetFloatValues() {
        final double[] expected = new double[]{ 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };
        final Matrix4 m = new Matrix4();
        final float[] result = m.getFloatValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testGetDoubleValues() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final Matrix4 m = new Matrix4();
        final double[] result = m.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testClone() {
        final double[] expected = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final Matrix4 from = new Matrix4(expected);
        final Matrix4 out = from.clone();
        Assert.assertNotNull(out);
        Assert.assertTrue((from != out));
        final double[] result = out.getDoubleValues();
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testToArray() {
        final double[] expected = new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        final double[] result = new double[16];
        final Matrix4 m = new Matrix4();
        m.toArray(result);
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testToFloatArray() {
        final double[] expected = new double[]{ 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };
        final float[] result = new float[16];
        final Matrix4 m = new Matrix4();
        m.toFloatArray(result);
        Assert.assertNotNull(result);
        for (int i = 0; i < (expected.length); ++i) {
            Assert.assertEquals(expected[i], result[i], 1.0E-14);
        }
    }

    @Test
    public void testEquals() {
        final double[] from = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
        final Matrix4 a = new Matrix4();
        final Matrix4 b = new Matrix4();
        final Matrix4 c = new Matrix4(from);
        Assert.assertEquals(a, b);
        Assert.assertNotEquals(a, c);
        Assert.assertNotEquals(b, c);
        Assert.assertNotEquals(null, a);
        Assert.assertNotEquals("not a matrix", a);
    }

    @Test
    public void testToString() {
        final Matrix4 m = new Matrix4();
        Assert.assertNotNull(m.toString());
    }
}

