package com.pi4j.component.servo.impl;


import RaspiPin.GPIO_21;
import RaspiPin.GPIO_22;
import RaspiPin.GPIO_23;
import RaspiPin.GPIO_24;
import RaspiPin.GPIO_25;
import RaspiPin.GPIO_26;
import RaspiPin.GPIO_27;
import RaspiPin.GPIO_28;
import RaspiPin.GPIO_29;
import RaspiPin.GPIO_30;
import RaspiPin.GPIO_31;
import org.junit.Assert;
import org.junit.Test;


/* #%L
**********************************************************************
ORGANIZATION  :  Pi4J
PROJECT       :  Pi4J :: Device Abstractions
FILENAME      :  RPIServoBlasterProviderTest.java

This file is part of the Pi4J project. More information about
this project can be found here:  https://www.pi4j.com/
**********************************************************************
%%
Copyright (C) 2012 - 2019 Pi4J
%%
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Lesser Public License for more details.

You should have received a copy of the GNU General Lesser Public
License along with this program.  If not, see
<http://www.gnu.org/licenses/lgpl-3.0.html>.
#L%
 */
/* Class:     RPIServoBlasterProviderTest
Created:   5th February 2017

@author Sam Hough
@version 1.1, 5th February 2017
 */
public class RPIServoBlasterProviderTest {
    @Test
    public void wiringPi30Mapping() {
        Assert.assertEquals("P1-27", resolvePhysical(GPIO_30));
    }

    @Test
    public void wiringPi31Mapping() {
        Assert.assertEquals("P1-28", resolvePhysical(GPIO_31));
    }

    @Test
    public void wiringPi21Mapping() {
        Assert.assertEquals("P1-29", resolvePhysical(GPIO_21));
    }

    @Test
    public void wiringPi22Mapping() {
        Assert.assertEquals("P1-31", resolvePhysical(GPIO_22));
    }

    @Test
    public void wiringPi26Mapping() {
        Assert.assertEquals("P1-32", resolvePhysical(GPIO_26));
    }

    @Test
    public void wiringPi23Mapping() {
        Assert.assertEquals("P1-33", resolvePhysical(GPIO_23));
    }

    @Test
    public void wiringPi24Mapping() {
        Assert.assertEquals("P1-35", resolvePhysical(GPIO_24));
    }

    @Test
    public void wiringPi27Mapping() {
        Assert.assertEquals("P1-36", resolvePhysical(GPIO_27));
    }

    @Test
    public void wiringPi25Mapping() {
        Assert.assertEquals("P1-37", resolvePhysical(GPIO_25));
    }

    @Test
    public void wiringPi28Mapping() {
        Assert.assertEquals("P1-38", resolvePhysical(GPIO_28));
    }

    @Test
    public void wiringPi29Mapping() {
        Assert.assertEquals("P1-40", resolvePhysical(GPIO_29));
    }
}

