package org.rajawali3d.bounds;


import org.junit.Assert;
import org.junit.Test;
import org.rajawali3d.math.vector.Vector3;


/**
 *
 *
 * @author Jared Woolston (jwoolston@keywcorp.com)
 */
public class BoundingSphereTest {
    BoundingSphere bounds;

    @Test
    public void testConstructor() {
        Assert.assertNotNull(bounds);
    }

    @Test
    public void testGetPosition() {
        Vector3 position = bounds.getPosition();
        Assert.assertEquals(0, position.x, 1.0E-14);
        Assert.assertEquals(0, position.y, 1.0E-14);
        Assert.assertEquals(0, position.z, 1.0E-14);
    }

    @Test
    public void testGetRadius() {
        double radius = bounds.getRadius();
        Assert.assertEquals(0, radius, 1.0E-14);
    }

    @Test
    public void testGetScale() {
        double scale = bounds.getScale();
        Assert.assertEquals(0, scale, 1.0E-14);
    }

    @Test
    public void testGetScaledRadius() {
        double scaledRadius = bounds.getScaledRadius();
        Assert.assertEquals(0, scaledRadius, 1.0E-14);
    }
}

