package org.rajawali3d;


import Vector3.Axis.X;
import Vector3.Axis.Y;
import Vector3.Axis.Z;
import org.junit.Assert;
import org.junit.Test;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;


/**
 *
 *
 * @author Jared Woolston (jwoolston@keywcorp.com)
 */
public class ATransformable3DTest {
    private ATransformable3DTest.transformable3D transformable;

    class transformable3D extends ATransformable3D {}

    @Test
    public void testGetScale() {
        double expected = 1;
        Vector3 scale = getScale();
        Assert.assertEquals(expected, scale.x, 1.0E-10);
        Assert.assertEquals(expected, scale.y, 1.0E-10);
        Assert.assertEquals(expected, scale.z, 1.0E-10);
    }

    @Test
    public void testGetLookAt() {
        double expected = 0;
        Vector3 rotation = getLookAt();
        Assert.assertEquals(expected, rotation.x, 1.0E-10);
        Assert.assertEquals(expected, rotation.y, 1.0E-10);
        Assert.assertEquals(expected, rotation.z, 1.0E-10);
    }

    @Test
    public void testGetRotX() {
        double expected = 0;
        Assert.assertEquals(expected, getRotX(), 1.0E-10);
    }

    @Test
    public void testGetRotY() {
        double expected = 0;
        Assert.assertEquals(expected, getRotY(), 1.0E-10);
    }

    @Test
    public void testGetRotZ() {
        double expected = 0;
        Assert.assertEquals(expected, getRotZ(), 1.0E-10);
    }

    @Test
    public void testGetOrientation() {
        Quaternion result = getOrientation();
        Assert.assertEquals(1, result.w, 1.0E-10);
        Assert.assertEquals(0, result.x, 1.0E-10);
        Assert.assertEquals(0, result.y, 1.0E-10);
        Assert.assertEquals(0, result.z, 1.0E-10);
    }

    @Test
    public void testSetScale() {
        double expectedX = 60;
        double expectedY = 120;
        double expectedZ = 180;
        setScale(expectedX, expectedY, expectedZ);
        Vector3 scale = getScale();
        Assert.assertEquals(expectedX, scale.x, 1.0E-10);
        Assert.assertEquals(expectedY, scale.y, 1.0E-10);
        Assert.assertEquals(expectedZ, scale.z, 1.0E-10);
    }

    @Test
    public void testSetLookAt() {
        double expectedX = 60;
        double expectedY = 120;
        double expectedZ = 180;
        setLookAt(expectedX, expectedY, expectedZ);
        Vector3 rotation = getLookAt();
        Assert.assertEquals(expectedX, rotation.x, 1.0E-10);
        Assert.assertEquals(expectedY, rotation.y, 1.0E-10);
        Assert.assertEquals(expectedZ, rotation.z, 1.0E-10);
    }

    @Test
    public void testSetRotX() {
        double expected = 42;
        setRotX(expected);
        Assert.assertEquals(expected, getRotZ(), 1.0E-10);
    }

    @Test
    public void testSetRotY() {
        double expected = 42;
        setRotY(expected);
        Assert.assertEquals(expected, getRotY(), 1.0E-10);
    }

    @Test
    public void testSetRotZ() {
        double expected = 42;
        setRotZ(expected);
        Assert.assertEquals(expected, getRotX(), 1.0E-10);
    }

    @Test
    public void testSetOrientation() {
        double w = 1 / 2.0;
        double x = (Math.sqrt(3)) / 4.0;
        double y = 1 / 2.0;
        double z = (Math.sqrt(5)) / 4.0;
        Quaternion expected = new Quaternion(w, x, y, z);
        transformable.setOrientation(expected);
        Quaternion result = getOrientation();
        Assert.assertEquals(expected.w, result.w, 1.0E-10);
        Assert.assertEquals(expected.x, result.x, 1.0E-10);
        Assert.assertEquals(expected.y, result.y, 1.0E-10);
        Assert.assertEquals(expected.z, result.z, 1.0E-10);
    }

    @Test
    public void testRotateX() {
        double expected = 42;
        transformable.rotate(X, expected);
        Assert.assertEquals(expected, getRotX(), 1.0E-10);
    }

    @Test
    public void testRotateY() {
        double expected = 42;
        transformable.rotate(Y, expected);
        Assert.assertEquals(expected, getRotY(), 1.0E-10);
    }

    @Test
    public void testRotateZ() {
        double expected = 42;
        transformable.rotate(Z, expected);
        Assert.assertEquals(expected, getRotZ(), 1.0E-10);
    }

    @Test
    public void testRotateAroundX() {
        double expected = 42;
        transformable.rotate(Vector3.X, expected);
        Assert.assertEquals(expected, getRotX(), 1.0E-10);
    }

    @Test
    public void testRotateAroundY() {
        double expected = 42;
        transformable.rotate(Vector3.Y, expected);
        Assert.assertEquals(expected, getRotY(), 1.0E-10);
    }

    @Test
    public void testRotateAroundZ() {
        double expected = 42;
        transformable.rotate(Vector3.Z, expected);
        Assert.assertEquals(expected, getRotZ(), 1.0E-10);
    }
}

