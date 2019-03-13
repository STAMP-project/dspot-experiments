/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.kml.utils;


import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;


public class KmlCentroidBuilderTest {
    Geometry cShapeGeom;

    @Test
    public void testSampleForPoint() throws Exception {
        Geometry g = cShapeGeom;
        KmlCentroidOptions opts1 = KmlCentroidOptions.create(ImmutableMap.of(KmlCentroidOptions.CONTAIN, "true", KmlCentroidOptions.SAMPLE, "2"));
        KmlCentroidOptions opts2 = KmlCentroidOptions.create(ImmutableMap.of(KmlCentroidOptions.CONTAIN, "true", KmlCentroidOptions.SAMPLE, "10"));
        KmlCentroidBuilder builder = new KmlCentroidBuilder();
        Coordinate c = builder.geometryCentroid(g, null, opts1);
        Assert.assertFalse(g.contains(g.getFactory().createPoint(c)));
        c = builder.geometryCentroid(g, null, opts2);
        Assert.assertTrue(g.contains(g.getFactory().createPoint(c)));
    }

    @Test
    public void testClip() {
        Geometry g = cShapeGeom;
        KmlCentroidOptions opts1 = KmlCentroidOptions.create(ImmutableMap.of());
        KmlCentroidOptions opts2 = KmlCentroidOptions.create(ImmutableMap.of(KmlCentroidOptions.CLIP, "true"));
        opts2.isClip();
        KmlCentroidBuilder builder = new KmlCentroidBuilder();
        Coordinate c = builder.geometryCentroid(g, null, opts1);
        Assert.assertFalse(g.contains(g.getFactory().createPoint(c)));
        Envelope bbox = new Envelope((-106.603059724489), (-103.655010760585), 34.6334331742943, 36.9918723454173);
        c = builder.geometryCentroid(g, bbox, opts2);
        Assert.assertTrue(g.contains(g.getFactory().createPoint(c)));
    }

    @Test
    public void testCaseInsensitivity() {
        KmlCentroidOptions opts = KmlCentroidOptions.create(ImmutableMap.of(KmlCentroidOptions.CONTAIN.toUpperCase(), "true", KmlCentroidOptions.CLIP.toUpperCase(), "true", KmlCentroidOptions.SAMPLE.toUpperCase(), "12"));
        Assert.assertTrue(opts.isContain());
        Assert.assertTrue(opts.isClip());
        Assert.assertEquals(12, opts.getSamples());
    }
}

