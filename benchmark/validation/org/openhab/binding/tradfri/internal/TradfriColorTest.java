/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tradfri.internal;


import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TradfriColor}.
 *
 * @author Holger Reichert - Initial contribution
 */
public class TradfriColorTest {
    @Test
    public void testFromCieKnownGood1() {
        TradfriColor color = new TradfriColor(29577, 12294, 354);
        Assert.assertEquals(29577, ((int) (color.xyX)));
        Assert.assertEquals(12294, ((int) (color.xyY)));
        Assert.assertEquals(254, ((int) (color.brightness)));
        HSBType hsbType = color.getHSB();
        Assert.assertNotNull(hsbType);
        Assert.assertEquals(321, hsbType.getHue().intValue());
        Assert.assertEquals(100, hsbType.getSaturation().intValue());
        Assert.assertEquals(100, hsbType.getBrightness().intValue());
    }

    @Test
    public void testFromCieKnownGood2() {
        TradfriColor color = new TradfriColor(19983, 37417, 84);
        Assert.assertEquals(19983, ((int) (color.xyX)));
        Assert.assertEquals(37417, ((int) (color.xyY)));
        Assert.assertEquals(84, ((int) (color.brightness)));
        HSBType hsbType = color.getHSB();
        Assert.assertNotNull(hsbType);
        Assert.assertEquals(115, hsbType.getHue().intValue());
        Assert.assertEquals(77, hsbType.getSaturation().intValue());
        Assert.assertEquals(34, hsbType.getBrightness().intValue());
    }

    @Test
    public void testFromCieKnownGood3() {
        TradfriColor color = new TradfriColor(19983, 37417, 1);
        Assert.assertEquals(19983, ((int) (color.xyX)));
        Assert.assertEquals(37417, ((int) (color.xyY)));
        Assert.assertEquals(1, ((int) (color.brightness)));
        HSBType hsbType = color.getHSB();
        Assert.assertNotNull(hsbType);
        Assert.assertEquals(115, hsbType.getHue().intValue());
        Assert.assertEquals(77, hsbType.getSaturation().intValue());
        Assert.assertEquals(1, hsbType.getBrightness().intValue());
    }

    @Test
    public void testFromCieKnownGood4() {
        TradfriColor color = new TradfriColor(11413, 31334, 181);
        Assert.assertEquals(11413, ((int) (color.xyX)));
        Assert.assertEquals(31334, ((int) (color.xyY)));
        Assert.assertEquals(181, ((int) (color.brightness)));
        HSBType hsbType = color.getHSB();
        Assert.assertNotNull(hsbType);
        Assert.assertEquals(158, hsbType.getHue().intValue());
        Assert.assertEquals(100, hsbType.getSaturation().intValue());
        Assert.assertEquals(72, hsbType.getBrightness().intValue());
    }

    @Test
    public void testFromHSBTypeKnownGood1() {
        TradfriColor color = new TradfriColor(HSBType.RED);
        Assert.assertEquals(41947, ((int) (color.xyX)));
        Assert.assertEquals(21625, ((int) (color.xyY)));
        Assert.assertEquals(254, ((int) (color.brightness)));
        HSBType hsbType = color.getHSB();
        Assert.assertNotNull(hsbType);
        Assert.assertEquals(0, hsbType.getHue().intValue());
        Assert.assertEquals(100, hsbType.getSaturation().intValue());
        Assert.assertEquals(100, hsbType.getBrightness().intValue());
    }

    @Test
    public void testFromHSBTypeKnownGood2() {
        TradfriColor color = new TradfriColor(new HSBType("0,100,1"));
        Assert.assertEquals(41947, ((int) (color.xyX)));
        Assert.assertEquals(21625, ((int) (color.xyY)));
        Assert.assertEquals(2, ((int) (color.brightness)));
        HSBType hsbType = color.getHSB();
        Assert.assertNotNull(hsbType);
        Assert.assertEquals(0, hsbType.getHue().intValue());
        Assert.assertEquals(100, hsbType.getSaturation().intValue());
        Assert.assertEquals(1, hsbType.getBrightness().intValue());
    }

    @Test
    public void testConversionReverse() {
        // convert from HSBType
        TradfriColor color = new TradfriColor(HSBType.GREEN);
        Assert.assertEquals(19660, ((int) (color.xyX)));
        Assert.assertEquals(39321, ((int) (color.xyY)));
        Assert.assertEquals(254, ((int) (color.brightness)));
        HSBType hsbType = color.getHSB();
        Assert.assertNotNull(hsbType);
        Assert.assertEquals(120, hsbType.getHue().intValue());
        Assert.assertEquals(100, hsbType.getSaturation().intValue());
        Assert.assertEquals(100, hsbType.getBrightness().intValue());
        // convert the result again based on the XY values
        TradfriColor reverse = new TradfriColor(color.xyX, color.xyY, color.brightness);
        Assert.assertEquals(19660, ((int) (reverse.xyX)));
        Assert.assertEquals(39321, ((int) (reverse.xyY)));
        Assert.assertEquals(254, ((int) (reverse.brightness)));
        HSBType hsbTypeReverse = color.getHSB();
        Assert.assertNotNull(hsbTypeReverse);
        Assert.assertEquals(120, hsbTypeReverse.getHue().intValue());
        Assert.assertEquals(100, hsbTypeReverse.getSaturation().intValue());
        Assert.assertEquals(100, hsbTypeReverse.getBrightness().intValue());
    }

    @Test
    public void testFromColorTemperatureMinMiddleMax() {
        // coldest color temperature -> preset 1
        TradfriColor colorMin = new TradfriColor(PercentType.ZERO);
        Assert.assertNotNull(colorMin);
        Assert.assertEquals(24933, ((int) (colorMin.xyX)));
        Assert.assertEquals(24691, ((int) (colorMin.xyY)));
        // middle color temperature -> preset 2
        TradfriColor colorMiddle = new TradfriColor(new PercentType(50));
        Assert.assertNotNull(colorMiddle);
        Assert.assertEquals(30138, ((int) (colorMiddle.xyX)));
        Assert.assertEquals(26909, ((int) (colorMiddle.xyY)));
        // warmest color temperature -> preset 3
        TradfriColor colorMax = new TradfriColor(PercentType.HUNDRED);
        Assert.assertNotNull(colorMax);
        Assert.assertEquals(33137, ((int) (colorMax.xyX)));
        Assert.assertEquals(27211, ((int) (colorMax.xyY)));
    }

    @Test
    public void testFromColorTemperatureInbetween() {
        // 30 percent must be between preset 1 and 2
        TradfriColor color2 = new TradfriColor(new PercentType(30));
        Assert.assertNotNull(color2);
        Assert.assertEquals(28056, ((int) (color2.xyX)));
        Assert.assertEquals(26022, ((int) (color2.xyY)));
        // 70 percent must be between preset 2 and 3
        TradfriColor color3 = new TradfriColor(new PercentType(70));
        Assert.assertNotNull(color3);
        Assert.assertEquals(31338, ((int) (color3.xyX)));
        Assert.assertEquals(27030, ((int) (color3.xyY)));
    }

    @Test
    public void testCalculateColorTemperature() {
        // preset 1 -> coldest -> 0 percent
        PercentType preset1 = new TradfriColor(24933, 24691, null).getColorTemperature();
        Assert.assertEquals(0, preset1.intValue());
        // preset 2 -> middle -> 50 percent
        PercentType preset2 = new TradfriColor(30138, 26909, null).getColorTemperature();
        Assert.assertEquals(50, preset2.intValue());
        // preset 3 -> warmest -> 100 percent
        PercentType preset3 = new TradfriColor(33137, 27211, null).getColorTemperature();
        Assert.assertEquals(100, preset3.intValue());
        // preset 3 -> warmest -> 100 percent
        PercentType colder = new TradfriColor(22222, 23333, null).getColorTemperature();
        Assert.assertEquals(0, colder.intValue());
        // preset 3 -> warmest -> 100 percent
        PercentType temp3 = new TradfriColor(34000, 34000, null).getColorTemperature();
        Assert.assertEquals(100, temp3.intValue());
        // mixed case 1
        PercentType mixed1 = new TradfriColor(0, 1000000, null).getColorTemperature();
        Assert.assertEquals(0, mixed1.intValue());
        // mixed case 1
        PercentType mixed2 = new TradfriColor(1000000, 0, null).getColorTemperature();
        Assert.assertEquals(100, mixed2.intValue());
    }
}

