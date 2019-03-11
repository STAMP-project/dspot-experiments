/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.kvp;


import java.awt.image.IndexColorModel;
import org.geotools.image.palette.InverseColorMapOp;
import org.junit.Assert;
import org.junit.Test;

import static PaletteManager.safePalette;


public class PaletteManagerTest {
    @Test
    public void testSameIndexColorModel() {
        IndexColorModel safePalette = safePalette;
        // using the same palette we get back the same inverter (it's cached, not rebuilt)
        InverseColorMapOp op1 = PaletteManager.getInverseColorMapOp(safePalette);
        InverseColorMapOp op2 = PaletteManager.getInverseColorMapOp(safePalette);
        Assert.assertEquals(op1, op2);
    }

    @Test
    public void testDifferentColorModels() {
        IndexColorModel safePalette = safePalette;
        IndexColorModel grayPalette = PaletteManagerTest.buildGrayPalette();
        InverseColorMapOp op1 = PaletteManager.getInverseColorMapOp(safePalette);
        InverseColorMapOp op2 = PaletteManager.getInverseColorMapOp(grayPalette);
        // the hashcode bug in IndexedColorModel would have made it return the same inverter
        Assert.assertNotEquals(op1, op2);
    }
}

