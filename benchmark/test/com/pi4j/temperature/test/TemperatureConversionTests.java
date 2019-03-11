/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  TemperatureConversionTests.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package com.pi4j.temperature.test;


import TemperatureScale.CELSIUS;
import TemperatureScale.FARENHEIT;
import TemperatureScale.KELVIN;
import TemperatureScale.RANKINE;
import com.pi4j.temperature.TemperatureConversion;
import org.junit.Assert;
import org.junit.Test;


public class TemperatureConversionTests {
    // **********************************************
    // FARENHEIT CONVERSION TESTS
    // **********************************************
    @Test
    public void testFarenheitToCelsius() {
        Assert.assertEquals(300, TemperatureConversion.convert(FARENHEIT, CELSIUS, 572), 0);
    }

    @Test
    public void testFarenheitToKelvin() {
        Assert.assertEquals(573.15, TemperatureConversion.convert(FARENHEIT, KELVIN, 572), 0.001);
    }

    @Test
    public void testFarenheitToRankine() {
        Assert.assertEquals(1031.67, TemperatureConversion.convert(FARENHEIT, RANKINE, 572), 0);
    }

    // **********************************************
    // CELSIUS CONVERSION TESTS
    // **********************************************
    @Test
    public void testCelsiusToFarenheit() {
        Assert.assertEquals(572, TemperatureConversion.convert(CELSIUS, FARENHEIT, 300), 0);
    }

    @Test
    public void testCelsiusToKelvin() {
        Assert.assertEquals(573.15, TemperatureConversion.convert(CELSIUS, KELVIN, 300), 0.001);
    }

    @Test
    public void testCelsiusToRankine() {
        Assert.assertEquals(1031.67, TemperatureConversion.convert(CELSIUS, RANKINE, 300), 0.001);
    }

    // **********************************************
    // KELVIN CONVERSION TESTS
    // **********************************************
    @Test
    public void testKelvinToFarenheit() {
        Assert.assertEquals(338, TemperatureConversion.convert(KELVIN, FARENHEIT, 443.15), 0.001);
    }

    @Test
    public void testKelvinToCelsius() {
        Assert.assertEquals(170, TemperatureConversion.convert(KELVIN, CELSIUS, 443.15), 0.001);
    }

    @Test
    public void testKelvinToRankine() {
        Assert.assertEquals(797.67, TemperatureConversion.convert(KELVIN, RANKINE, 443.15), 0.001);
    }

    // **********************************************
    // RANKINE CONVERSION TESTS
    // **********************************************
    @Test
    public void testRankineToFarenheit() {
        Assert.assertEquals((-459.67), TemperatureConversion.convert(RANKINE, FARENHEIT, 0), 0.001);
    }

    @Test
    public void testRankineToCelsius() {
        Assert.assertEquals((-273.15), TemperatureConversion.convert(RANKINE, CELSIUS, 0), 0.001);
    }

    @Test
    public void testRankineToKelvin() {
        Assert.assertEquals(0, TemperatureConversion.convert(RANKINE, KELVIN, 0), 0.001);
    }
}

