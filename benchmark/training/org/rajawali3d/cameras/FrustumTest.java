package org.rajawali3d.cameras;


import org.junit.Assert;
import org.junit.Test;
import org.rajawali3d.bounds.BoundingBox;
import org.rajawali3d.math.vector.Vector3;


/**
 *
 *
 * @author Jared Woolston (jwoolston@keywcorp.com)
 */
public class FrustumTest {
    Frustum frustum;

    @Test
    public void testSphereInFrustum() {
        final Vector3 center = new Vector3();
        final double radius = 0;
        boolean result = frustum.sphereInFrustum(center, radius);
        Assert.assertTrue(result);
    }

    @Test
    public void testBoundsInFrustum() {
        final BoundingBox bounds = new BoundingBox();
        boolean result = frustum.boundsInFrustum(bounds);
        Assert.assertTrue(result);
    }

    @Test
    public void testPointInFrustum() {
        final Vector3 point = new Vector3();
        boolean result = frustum.pointInFrustum(point);
        Assert.assertTrue(result);
    }
}

