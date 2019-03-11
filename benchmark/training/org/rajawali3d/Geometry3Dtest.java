package org.rajawali3d;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jared Woolston (jwoolston@keywcorp.com)
 */
public class Geometry3Dtest {
    private Geometry3D geometry;

    @Test
    public void testConstructor() {
        Assert.assertNotNull(geometry);
    }

    @Test
    public void testGetNumIndices() {
        Assert.assertEquals(0, geometry.getNumIndices());
    }

    @Test
    public void testGetNumTriangles() {
        Assert.assertEquals(0, geometry.getNumTriangles());
    }

    @Test
    public void testGetNumVertices() {
        Assert.assertEquals(0, geometry.getNumVertices());
    }
}

