/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Device Abstractions
 * FILENAME      :  ServoBaseTest.java
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
package com.pi4j.component.servo;


import GenericServo.PWM_NEUTRAL;
import Servo.PROP_END_POINT_LEFT;
import Servo.PROP_END_POINT_RIGHT;
import Servo.PROP_IS_REVERSE;
import Servo.PROP_SUBTRIM;
import com.pi4j.component.servo.impl.GenericServo;
import org.junit.Test;
import org.mockito.Mockito;


/* Class:     ServoBaseTest
Created:   May 1, 2013

@author Christian Wehrli
@version 1.0, May 1, 2013
 */
public class ServoBaseTest {
    // ------------------------------------------------------------------------------------------------------------------
    // calculatePwmDuration()
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void testCalculatePwmDurationTESTmidLeft() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        int positionMidLeft = -50;
        // Exercise SUT
        sut.setPosition(positionMidLeft);
        // Verify outcome
        int expectedPwmValue = 1300;// 1500 + ((1500-900) / 150*100 / 100*(-50))

        Mockito.verify(mockServoDriver).setServoPulseWidth(expectedPwmValue);
    }

    @Test
    public void testCalculatePwmDurationTESTfullRight() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        int positionFullRight = 100;
        // Exercise SUT
        sut.setPosition(positionFullRight);
        // Verify outcome
        int expectedPwmValue = 1900;// 1500 + ((2100-1500) / 150*100 / 100*100)

        Mockito.verify(mockServoDriver).setServoPulseWidth(expectedPwmValue);
    }

    @Test
    public void testCalculatePwmDurationTESTsubtrimAndMaxTravel() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_RIGHT, "150");
        sut.setProperty(PROP_SUBTRIM, "-200");
        int positionMidRight = 50;
        // Exercise SUT
        sut.setPosition(positionMidRight);
        // Verify outcome
        int expectedPwmValue = 1600;// 1300 + ((2100-1500) / 150*150 / 100*50)

        Mockito.verify(mockServoDriver).setServoPulseWidth(expectedPwmValue);
    }

    @Test
    public void testCalculatePwmDurationTESTisReversed() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_IS_REVERSE, Boolean.TRUE.toString());
        int positionMidLeft = -50;
        // Exercise SUT
        sut.setPosition(positionMidLeft);
        // Verify outcome
        int expectedPwmValue = 1700;// 1500 + ((2100-1500) / 150*100 / 100*50)

        Mockito.verify(mockServoDriver).setServoPulseWidth(expectedPwmValue);
    }

    @Test
    public void testCalculatePwmDurationTESTisReversedAndSubtrim() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_IS_REVERSE, Boolean.TRUE.toString());
        sut.setProperty(PROP_SUBTRIM, "-125");
        int positionMidRight = 50;
        // Exercise SUT
        sut.setPosition(positionMidRight);
        // Verify outcome
        int expectedPwmValue = 1175;// 1375 + ((2100-1500) / 150*100 / 100*(-50))

        Mockito.verify(mockServoDriver).setServoPulseWidth(expectedPwmValue);
    }

    @Test
    public void testCalculatePwmDurationTESTisReversedAndSubtrimAndCustomEndpoint() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_IS_REVERSE, Boolean.TRUE.toString());
        sut.setProperty(PROP_SUBTRIM, "-125");
        sut.setProperty(PROP_END_POINT_LEFT, "125");
        // -> this combination of subtrim and endpoint would lead into a left-side endpoint-overflow, therefore
        // max. left position is limited to 900us!
        int positionMidRight = 50;
        // Exercise SUT
        sut.setPosition(positionMidRight);
        // Verify outcome
        int expectedPwmValue = 1137;// 1375 + ((1375-900) / 100*(-50))

        Mockito.verify(mockServoDriver).setServoPulseWidth(expectedPwmValue);
    }

    @Test
    public void testCalculatePwmDurationTESTsmallTravel() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_LEFT, "10");
        int positionFullLeft = -100;
        // Exercise SUT
        sut.setPosition(positionFullLeft);
        // Verify outcome
        int expectedPwmValue = 1460;// 1500 + ((1500-900) / 150*10 / 100*(-100))

        Mockito.verify(mockServoDriver).setServoPulseWidth(expectedPwmValue);
    }

    // ------------------------------------------------------------------------------------------------------------------
    // calculateNeutralPwmDuration()
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void testCalculateNeutralPwmDurationTESTnoSubtrim() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_SUBTRIM, "0");
        // Exercise SUT
        sut.setPosition(0);
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(((int) (PWM_NEUTRAL)));
    }

    @Test
    public void testCalculateNeutralPwmDurationTESTsubtrimMaxLeft() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_SUBTRIM, "-200");
        // Exercise SUT
        sut.setPosition(0);
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth((((int) (GenericServo.PWM_NEUTRAL)) - 200));
    }

    @Test
    public void testCalculateNeutralPwmDurationTESTsubtrimMaxRight() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_SUBTRIM, "200");
        // Exercise SUT
        sut.setPosition(0);
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth((((int) (GenericServo.PWM_NEUTRAL)) + 200));
    }

    // ------------------------------------------------------------------------------------------------------------------
    // calculateEndPointPwmDuration()
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void testCalculateEndPointPwmDurationTESTfullLeft() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_LEFT, "150");
        // Exercise SUT
        sut.setPosition((-100));
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(900);
    }

    @Test
    public void testCalculateEndPointPwmDurationTEST100PercentLeft() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_LEFT, "100");
        // Exercise SUT
        sut.setPosition((-100));
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(1100);
    }

    @Test
    public void testCalculateEndPointPwmDurationTEST33PercentLeft() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_LEFT, "33");
        // Exercise SUT
        sut.setPosition((-100));
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(1368);
    }

    @Test
    public void testCalculateEndPointPwmDurationTESTzeroTravelLeft() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_LEFT, "0");
        // Exercise SUT
        sut.setPosition((-100));
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(1500);
    }

    @Test
    public void testCalculateEndPointPwmDurationTESTzeroTravelRight() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_RIGHT, "0");
        // Exercise SUT
        sut.setPosition(100);
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(1500);
    }

    @Test
    public void testCalculateEndPointPwmDurationTEST100PercentRight() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_RIGHT, "100");
        // Exercise SUT
        sut.setPosition(100);
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(1900);
    }

    @Test
    public void testCalculateEndPointPwmDurationTEST66PercentRight() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_RIGHT, "66");
        // Exercise SUT
        sut.setPosition(100);
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(1764);
    }

    @Test
    public void testCalculateEndPointPwmDurationTESTfullRight() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_END_POINT_RIGHT, "150");
        // Exercise SUT
        sut.setPosition(100);
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(2100);
    }

    @Test
    public void testCalculateEndPointPwmDurationTESTsubtrimLeft() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_SUBTRIM, "-200");
        // Exercise SUT
        sut.setPosition((-100));
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(900);
        sut.setPosition(100);
        Mockito.verify(mockServoDriver).setServoPulseWidth(1700);
    }

    @Test
    public void testCalculateEndPointPwmDurationTESTsubtrimRight() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_SUBTRIM, "50");
        // Exercise SUT
        sut.setPosition((-100));
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(1150);
        sut.setPosition(100);
        Mockito.verify(mockServoDriver).setServoPulseWidth(1950);
    }

    @Test
    public void testCalculateEndPointPwmDurationTESTmaxSubtrimLeftMaxEndpoints() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_SUBTRIM, "-200");
        sut.setProperty(PROP_END_POINT_LEFT, "150");
        sut.setProperty(PROP_END_POINT_RIGHT, "150");
        // Exercise SUT
        sut.setPosition((-100));
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(900);
        sut.setPosition(100);
        Mockito.verify(mockServoDriver).setServoPulseWidth(1900);
    }

    @Test
    public void testCalculateEndPointPwmDurationTESTsubtrimRightCustomEndpoints() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_SUBTRIM, "75");
        sut.setProperty(PROP_END_POINT_LEFT, "125");
        sut.setProperty(PROP_END_POINT_RIGHT, "80");
        // Exercise SUT
        sut.setPosition((-100));
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(1075);
        sut.setPosition(100);
        Mockito.verify(mockServoDriver).setServoPulseWidth(1895);
    }

    @Test
    public void testCalculateEndPointPwmDurationTESTsubtrimRightCausesEndpointOverflow() {
        Mockito.when(mockServoDriver.getServoPulseResolution()).thenReturn(1000);
        // Setup fixture
        sut.setProperty(PROP_SUBTRIM, "150");
        sut.setProperty(PROP_END_POINT_RIGHT, "140");
        // Exercise SUT
        sut.setPosition(100);
        // Verify outcome
        Mockito.verify(mockServoDriver).setServoPulseWidth(2100);
    }

    // ------------------------------------------------------------------------------------------------------------------
    // Setup
    // ------------------------------------------------------------------------------------------------------------------
    private GenericServo sut;

    private ServoDriver mockServoDriver;
}

