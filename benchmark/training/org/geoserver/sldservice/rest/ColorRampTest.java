/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * Copyright (C) 2007-2008-2009 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.sldservice.rest;


import java.awt.Color;
import java.util.List;
import org.geoserver.sldservice.utils.classifier.impl.BlueColorRamp;
import org.geoserver.sldservice.utils.classifier.impl.GrayColorRamp;
import org.geoserver.sldservice.utils.classifier.impl.JetColorRamp;
import org.geoserver.sldservice.utils.classifier.impl.RedColorRamp;
import org.junit.Assert;
import org.junit.Test;


public class ColorRampTest {
    protected static final int MIN_COLOR_INT = 52;

    protected static final int MAX_COLOR_INT = 255;

    @Test
    public void blueColorRampTest() throws Exception {
        BlueColorRamp blueRamp = new BlueColorRamp();
        blueRamp.setNumClasses(10);
        Assert.assertEquals(10, blueRamp.getNumClasses());
        List<Color> colors = blueRamp.getRamp();
        Assert.assertEquals("Incorrect size for color ramp", 10, colors.size());
        Assert.assertEquals("Incorrect value for 1st color", new Color(0, 0, ColorRampTest.MIN_COLOR_INT), colors.get(0));
        Assert.assertEquals("Incorrect value for last color", new Color(0, 0, ColorRampTest.MAX_COLOR_INT), colors.get(9));
        blueRamp.revert();
        List<Color> reverseColors = blueRamp.getRamp();
        Assert.assertEquals("Incorrect value for last reverse color", new Color(0, 0, ColorRampTest.MIN_COLOR_INT), reverseColors.get(9));
        Assert.assertEquals("Incorrect value for 1st reverse color", new Color(0, 0, ColorRampTest.MAX_COLOR_INT), reverseColors.get(0));
    }

    @Test
    public void redColorRampTest() throws Exception {
        RedColorRamp redRamp = new RedColorRamp();
        redRamp.setNumClasses(10);
        Assert.assertEquals(10, redRamp.getNumClasses());
        List<Color> colors = redRamp.getRamp();
        Assert.assertEquals("Incorrect size for color ramp", 10, colors.size());
        Assert.assertEquals("Incorrect value for 1st color", new Color(ColorRampTest.MIN_COLOR_INT, 0, 0), colors.get(0));
        Assert.assertEquals("Incorrect value for last color", new Color(ColorRampTest.MAX_COLOR_INT, 0, 0), colors.get(9));
        redRamp.revert();
        List<Color> reverseColors = redRamp.getRamp();
        Assert.assertEquals("Incorrect value for last reverse color", new Color(ColorRampTest.MAX_COLOR_INT, 0, 0), reverseColors.get(0));
        Assert.assertEquals("Incorrect value for 1st reverse color", new Color(ColorRampTest.MIN_COLOR_INT, 0, 0), reverseColors.get(9));
    }

    @Test
    public void grayColorRampTest() throws Exception {
        GrayColorRamp grayRamp = new GrayColorRamp();
        grayRamp.setNumClasses(10);
        Assert.assertEquals(10, grayRamp.getNumClasses());
        List<Color> colors = grayRamp.getRamp();
        Assert.assertEquals("Incorrect size for color ramp", 10, colors.size());
        Assert.assertEquals("Incorrect value for 1st color", new Color(ColorRampTest.MIN_COLOR_INT, ColorRampTest.MIN_COLOR_INT, ColorRampTest.MIN_COLOR_INT), colors.get(0));
        Assert.assertEquals("Incorrect value for last color", new Color(ColorRampTest.MAX_COLOR_INT, ColorRampTest.MAX_COLOR_INT, ColorRampTest.MAX_COLOR_INT), colors.get(9));
        grayRamp.revert();
        List<Color> reverseColors = grayRamp.getRamp();
        Assert.assertEquals("Incorrect value for last reverse color", new Color(ColorRampTest.MIN_COLOR_INT, ColorRampTest.MIN_COLOR_INT, ColorRampTest.MIN_COLOR_INT), reverseColors.get(9));
        Assert.assertEquals("Incorrect value for 1st reverse color", new Color(ColorRampTest.MAX_COLOR_INT, ColorRampTest.MAX_COLOR_INT, ColorRampTest.MAX_COLOR_INT), reverseColors.get(0));
    }

    @Test
    public void jetColorRampTest() throws Exception {
        JetColorRamp jetRamp = new JetColorRamp();
        jetRamp.setNumClasses(10);
        Assert.assertEquals(11, jetRamp.getNumClasses());
        List<Color> colors = jetRamp.getRamp();
        Assert.assertEquals("Incorrect size for color ramp", 10, colors.size());
        Assert.assertEquals("Incorrect value for 1st color", new Color(0, 0, 255), colors.get(0));
        Assert.assertEquals("Incorrect value for last color", new Color(255, 0, 0), colors.get(9));
        jetRamp.revert();
        List<Color> reverseColors = jetRamp.getRamp();
        Assert.assertEquals("Incorrect value for last reverse color", new Color(0, 0, 255), reverseColors.get(9));
        Assert.assertEquals("Incorrect value for 1st reverse color", new Color(255, 0, 0), reverseColors.get(0));
    }
}

