/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homematic.test.state;


import IncreaseDecreaseType.DECREASE;
import IncreaseDecreaseType.INCREASE;
import OnOffType.OFF;
import OnOffType.ON;
import OpenClosedType.CLOSED;
import OpenClosedType.OPEN;
import UpDownType.DOWN;
import UpDownType.UP;
import junit.framework.Assert;
import org.junit.Test;
import org.openhab.binding.homematic.internal.converter.state.DecimalTypeConverter;
import org.openhab.binding.homematic.internal.converter.state.OnOffTypeConverter;
import org.openhab.binding.homematic.internal.converter.state.OpenClosedTypeConverter;
import org.openhab.binding.homematic.internal.converter.state.PercentTypeConverter;
import org.openhab.binding.homematic.internal.converter.state.StringTypeConverter;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StringType;


/**
 * Testcases for the converter framework of the Homematic binding.
 *
 * @author Gerhard Riegler
 * @since 1.5.0
 */
public class ConverterTest {
    @Test
    public void testOnOffTypeConverterFromBinding() throws Exception {
        OnOffTypeConverter converter = new OnOffTypeConverter();
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("PRESS_SHORT", true)));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("PRESS_SHORT", false)));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("SENSOR", false)));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("SENSOR", true)));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("LEVEL", true)));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("LEVEL", false)));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("LEVEL", 1.0)));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("LEVEL", 1)));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("LEVEL", 0)));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", true)));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", false)));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "HM-Sec-SC")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "HM-Sec-SC")));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "HM-Sec-SC-2")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "HM-Sec-SC-2")));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "ZEL STG RM FFK")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "ZEL STG RM FFK")));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "HM-Sec-TiS")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "HM-Sec-TiS")));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "14", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "14", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "15", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "15", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "15", "BC-SC-Rd-WM")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "15", "BC-SC-Rd-WM")));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "15", "BC-SC-Rd-WM-2")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "15", "BC-SC-Rd-WM-2")));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "HM-SCI-3-FM")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "HM-SCI-3-FM")));
        Assert.assertEquals(ON, converter.convertFromBinding(getDatapoint("LEVEL", "on")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getDatapoint("LEVEL", "off")));
        Assert.assertEquals(OFF, converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 1.0)));
        Assert.assertEquals(ON, converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(ON, converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 0.1)));
        Assert.assertEquals(ON, converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 0.9)));
    }

    @Test
    public void testOnOffTypeConverterToBinding() throws Exception {
        OnOffTypeConverter converter = new OnOffTypeConverter();
        Assert.assertEquals(true, converter.convertToBinding(ON, getDatapoint("PRESS_SHORT", true)));
        Assert.assertEquals(false, converter.convertToBinding(OFF, getDatapoint("PRESS_SHORT", false)));
        Assert.assertEquals("ON", converter.convertToBinding(ON, getDatapoint("LEVEL", "")));
        Assert.assertEquals("OFF", converter.convertToBinding(OFF, getDatapoint("LEVEL", "")));
        Assert.assertEquals(1, converter.convertToBinding(ON, getDatapoint("LEVEL", 1, 0, 1)));
        Assert.assertEquals(0, converter.convertToBinding(OFF, getDatapoint("LEVEL", 0)));
        Assert.assertEquals(5, converter.convertToBinding(ON, getDatapoint("LEVEL", 5, 0, 5)));
        Assert.assertEquals(0, converter.convertToBinding(OFF, getDatapoint("LEVEL", 0)));
        Assert.assertEquals(0, converter.convertToBinding(OFF, getDatapoint("LEVEL", 1, 0, 5)));
        Assert.assertEquals(0, converter.convertToBinding(OFF, getDatapoint("LEVEL", 4, 0, 5)));
        Assert.assertEquals(true, converter.convertToBinding(ON, getDatapoint("STATE", true)));
        Assert.assertEquals(false, converter.convertToBinding(OFF, getDatapoint("STATE", false)));
        Assert.assertEquals(false, converter.convertToBinding(ON, getDatapoint("SENSOR", true)));
        Assert.assertEquals(true, converter.convertToBinding(OFF, getDatapoint("SENSOR", false)));
        Assert.assertEquals(true, converter.convertToBinding(ON, getDatapoint("STATE", true)));
        Assert.assertEquals(false, converter.convertToBinding(OFF, getDatapoint("STATE", false)));
        Assert.assertEquals(false, converter.convertToBinding(ON, getDatapoint("STATE", true, 0, 0, "HM-Sec-SC")));
        Assert.assertEquals(true, converter.convertToBinding(OFF, getDatapoint("STATE", false, 0, 0, "HM-Sec-SC")));
        Assert.assertEquals(false, converter.convertToBinding(ON, getDatapoint("STATE", true, 0, 0, "HM-Sec-SC-2")));
        Assert.assertEquals(true, converter.convertToBinding(OFF, getDatapoint("STATE", false, 0, 0, "HM-Sec-SC-2")));
        Assert.assertEquals(false, converter.convertToBinding(ON, getDatapoint("STATE", true, 0, 0, "ZEL STG RM FFK")));
        Assert.assertEquals(true, converter.convertToBinding(OFF, getDatapoint("STATE", false, 0, 0, "ZEL STG RM FFK")));
        Assert.assertEquals(false, converter.convertToBinding(ON, getDatapoint("STATE", true, 0, 0, "HM-Sec-TiS")));
        Assert.assertEquals(true, converter.convertToBinding(OFF, getDatapoint("STATE", false, 0, 0, "HM-Sec-TiS")));
        Assert.assertEquals(true, converter.convertToBinding(ON, getDatapoint("STATE", true, 0, 0, "14", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(false, converter.convertToBinding(OFF, getDatapoint("STATE", false, 0, 0, "14", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(false, converter.convertToBinding(ON, getDatapoint("STATE", true, 0, 0, "15", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(true, converter.convertToBinding(OFF, getDatapoint("STATE", false, 0, 0, "15", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(false, converter.convertToBinding(ON, getDatapoint("STATE", true, 0, 0, "BC-SC-Rd-WM")));
        Assert.assertEquals(true, converter.convertToBinding(OFF, getDatapoint("STATE", false, 0, 0, "BC-SC-Rd-WM")));
        Assert.assertEquals(false, converter.convertToBinding(ON, getDatapoint("STATE", true, 0, 0, "BC-SC-Rd-WM-2")));
        Assert.assertEquals(true, converter.convertToBinding(OFF, getDatapoint("STATE", false, 0, 0, "BC-SC-Rd-WM-2")));
        Assert.assertEquals(false, converter.convertToBinding(ON, getDatapoint("STATE", true, 0, 0, "HM-SCI-3-FM")));
        Assert.assertEquals(true, converter.convertToBinding(OFF, getDatapoint("STATE", false, 0, 0, "HM-SCI-3-FM")));
        Assert.assertEquals(1.0, converter.convertToBinding(ON, getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(0.0, converter.convertToBinding(OFF, getRollerShutterDatapoint("LEVEL", 0.0)));
    }

    @Test
    public void testOpenClosedTypeConverterFromBinding() throws Exception {
        OpenClosedTypeConverter converter = new OpenClosedTypeConverter();
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("PRESS_SHORT", true)));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("PRESS_SHORT", false)));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("SENSOR", false)));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("SENSOR", true)));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", true)));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", false)));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "HM-Sec-SC")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "HM-Sec-SC")));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "HM-Sec-SC-2")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "HM-Sec-SC-2")));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "ZEL STG RM FFK")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "ZEL STG RM FFK")));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "HM-Sec-TiS")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "HM-Sec-TiS")));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "14", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "14", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "15", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "15", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "15", "BC-SC-Rd-WM")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "15", "BC-SC-Rd-WM")));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "15", "BC-SC-Rd-WM-2")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "15", "BC-SC-Rd-WM-2")));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", false, 0, 0, "15", "HM-SCI-3-FM")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", true, 0, 0, "15", "HM-SCI-3-FM")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("STATE", false)));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("STATE", true)));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("LEVEL", true)));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("LEVEL", false)));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("LEVEL", 1.0)));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("LEVEL", 1)));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("LEVEL", 0)));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getDatapoint("LEVEL", "closed")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getDatapoint("LEVEL", "open")));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 1.0)));
        Assert.assertEquals(CLOSED, converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 0.1)));
        Assert.assertEquals(OPEN, converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 0.9)));
    }

    @Test
    public void testOpenClosedTypeConverterToBinding() throws Exception {
        OpenClosedTypeConverter converter = new OpenClosedTypeConverter();
        Assert.assertEquals(true, converter.convertToBinding(CLOSED, getDatapoint("PRESS_SHORT", true)));
        Assert.assertEquals(false, converter.convertToBinding(OPEN, getDatapoint("PRESS_SHORT", false)));
        Assert.assertEquals("CLOSED", converter.convertToBinding(CLOSED, getDatapoint("LEVEL", "")));
        Assert.assertEquals("OPEN", converter.convertToBinding(OPEN, getDatapoint("LEVEL", "")));
        Assert.assertEquals(1, converter.convertToBinding(OPEN, getDatapoint("LEVEL", 1, 0, 1)));
        Assert.assertEquals(0, converter.convertToBinding(CLOSED, getDatapoint("LEVEL", 0)));
        Assert.assertEquals(5, converter.convertToBinding(OPEN, getDatapoint("LEVEL", 5, 0, 5)));
        Assert.assertEquals(0, converter.convertToBinding(CLOSED, getDatapoint("LEVEL", 0)));
        Assert.assertEquals(5, converter.convertToBinding(OPEN, getDatapoint("LEVEL", 1, 0, 5)));
        Assert.assertEquals(5, converter.convertToBinding(OPEN, getDatapoint("LEVEL", 4, 0, 5)));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("SENSOR", true)));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("SENSOR", false)));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("STATE", true, 0, 0, "HM-Sec-SC")));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("STATE", false, 0, 0, "HM-Sec-SC")));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("STATE", true, 0, 0, "HM-Sec-SC-2")));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("STATE", false, 0, 0, "HM-Sec-SC")));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("STATE", true, 0, 0, "ZEL STG RM FFK")));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("STATE", false, 0, 0, "ZEL STG RM FFK")));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("STATE", true, 0, 0, "HM-Sec-TiS")));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("STATE", false, 0, 0, "HM-Sec-TiS")));
        Assert.assertEquals(true, converter.convertToBinding(CLOSED, getDatapoint("STATE", true, 0, 0, "14", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(false, converter.convertToBinding(OPEN, getDatapoint("STATE", false, 0, 0, "14", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("STATE", true, 0, 0, "15", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("STATE", false, 0, 0, "15", "HMW-IO-12-Sw14-DR")));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("STATE", true, 0, 0, "BC-SC-Rd-WM")));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("STATE", false, 0, 0, "BC-SC-Rd-WM")));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("STATE", true, 0, 0, "BC-SC-Rd-WM-2")));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("STATE", false, 0, 0, "BC-SC-Rd-WM-2")));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("STATE", true, 0, 0, "HM-SCI-3-FM")));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("STATE", false, 0, 0, "HM-SCI-3-FM")));
        Assert.assertEquals(true, converter.convertToBinding(CLOSED, getDatapoint("STATE", true)));
        Assert.assertEquals(false, converter.convertToBinding(OPEN, getDatapoint("STATE", false)));
        Assert.assertEquals(false, converter.convertToBinding(CLOSED, getDatapoint("SENSOR", false)));
        Assert.assertEquals(true, converter.convertToBinding(OPEN, getDatapoint("SENSOR", true)));
        Assert.assertEquals(1.0, converter.convertToBinding(OPEN, getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(0.0, converter.convertToBinding(CLOSED, getRollerShutterDatapoint("LEVEL", 0.0)));
    }

    @Test
    public void testDecimalTypeConverterFromBinding() throws Exception {
        DecimalTypeConverter converter = new DecimalTypeConverter();
        Assert.assertEquals(new DecimalType(1), converter.convertFromBinding(getDatapoint("PRESS_SHORT", 1)));
        Assert.assertEquals(new DecimalType(0), converter.convertFromBinding(getDatapoint("PRESS_SHORT", 0)));
        Assert.assertEquals(new DecimalType(1), converter.convertFromBinding(getDatapoint("PRESS_SHORT", true, 0, 1)));
        Assert.assertEquals(new DecimalType(5), converter.convertFromBinding(getDatapoint("PRESS_SHORT", true, 0, 5)));
        Assert.assertEquals(new DecimalType(5.4).doubleValue(), converter.convertFromBinding(getDatapoint("PRESS_SHORT", true, 0.0, 5.4)).doubleValue());
        Assert.assertEquals(new DecimalType(0), converter.convertFromBinding(getDatapoint("PRESS_SHORT", false, 0, 1)));
        Assert.assertEquals(new DecimalType(1), converter.convertFromBinding(getDatapoint("SENSOR", 1)));
        Assert.assertEquals(new DecimalType(0), converter.convertFromBinding(getDatapoint("SENSOR", 0)));
        Assert.assertEquals(new DecimalType(1.0).doubleValue(), converter.convertFromBinding(getDatapoint("LEVEL", 1.0)).doubleValue());
        Assert.assertEquals(new DecimalType(3.4).doubleValue(), converter.convertFromBinding(getDatapoint("LEVEL", 3.4)).doubleValue());
        Assert.assertEquals(new DecimalType(9876.678957).doubleValue(), converter.convertFromBinding(getDatapoint("LEVEL", 9876.6789568)).doubleValue());
        Assert.assertEquals(new DecimalType(5.3).doubleValue(), converter.convertFromBinding(getDatapoint("LEVEL", "true", 0.0, 5.3)).doubleValue());
        Assert.assertEquals(new DecimalType(0.0).doubleValue(), converter.convertFromBinding(getDatapoint("LEVEL", "false", 0.0, 5.3)).doubleValue());
        Assert.assertEquals(new DecimalType(1.0).doubleValue(), converter.convertFromBinding(getDatapoint("LEVEL", "1")).doubleValue());
        Assert.assertEquals(new DecimalType(1.0).doubleValue(), converter.convertFromBinding(getDatapoint("LEVEL", "1.0")).doubleValue());
        Assert.assertEquals(new DecimalType(9876.678957).doubleValue(), converter.convertFromBinding(getDatapoint("LEVEL", "9876.6789568")).doubleValue());
        Assert.assertEquals(new DecimalType(1.0).doubleValue(), converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 1.0)).doubleValue());
        Assert.assertEquals(new DecimalType(0.0).doubleValue(), converter.convertFromBinding(getDatapoint("LEVEL", 0.0)).doubleValue());
    }

    @Test
    public void testDecimalTypeConverterToBinding() throws Exception {
        DecimalTypeConverter converter = new DecimalTypeConverter();
        Assert.assertEquals(true, converter.convertToBinding(new DecimalType(1), getDatapoint("PRESS_SHORT", true, 0, 1)));
        Assert.assertEquals(false, converter.convertToBinding(new DecimalType(0), getDatapoint("PRESS_SHORT", true, 0, 1)));
        Assert.assertEquals(true, converter.convertToBinding(new DecimalType(5), getDatapoint("LEVEL", true, 0, 5)));
        Assert.assertEquals(false, converter.convertToBinding(new DecimalType(4), getDatapoint("LEVEL", true, 0, 5)));
        Assert.assertEquals(false, converter.convertToBinding(new DecimalType(0), getDatapoint("LEVEL", true, 0, 1)));
        Assert.assertEquals(true, converter.convertToBinding(new DecimalType(1), getDatapoint("LEVEL", "true", 0, 1)));
        Assert.assertEquals(false, converter.convertToBinding(new DecimalType(0), getDatapoint("LEVEL", "false", 0, 1)));
        Assert.assertEquals(1, converter.convertToBinding(new DecimalType(1), getDatapoint("LEVEL", 0)));
        Assert.assertEquals(1.0, converter.convertToBinding(new DecimalType(1), getDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(9876.678957, converter.convertToBinding(new DecimalType(9876.6789568), getDatapoint("LEVEL", 0.0)));
        Assert.assertEquals("1.0", converter.convertToBinding(new DecimalType(1), getDatapoint("LEVEL", "text")));
        Assert.assertEquals(0.5, converter.convertToBinding(new DecimalType(0.5), getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(0.4, converter.convertToBinding(new DecimalType(0.4), getRollerShutterDatapoint("LEVEL", 0.0)));
    }

    @Test
    public void testPercentTypeConverterFromBinding() throws Exception {
        PercentTypeConverter converter = new PercentTypeConverter();
        Assert.assertEquals(new PercentType(100), converter.convertFromBinding(getDatapoint("PRESS_SHORT", 1, 0, 1)));
        Assert.assertEquals(new PercentType(0), converter.convertFromBinding(getDatapoint("PRESS_SHORT", 0, 0, 1)));
        Assert.assertEquals(new PercentType(100), converter.convertFromBinding(getDatapoint("PRESS_SHORT", true, 0, 1)));
        Assert.assertEquals(new PercentType(100), converter.convertFromBinding(getDatapoint("PRESS_SHORT", true, 0, 5)));
        Assert.assertEquals(new PercentType(100), converter.convertFromBinding(getDatapoint("PRESS_SHORT", true, 0.0, 5.4)));
        Assert.assertEquals(new PercentType(0), converter.convertFromBinding(getDatapoint("PRESS_SHORT", false, 0, 1)));
        Assert.assertEquals(new PercentType(100), converter.convertFromBinding(getDatapoint("SENSOR", 1, 0, 1)));
        Assert.assertEquals(new PercentType(0), converter.convertFromBinding(getDatapoint("SENSOR", 0, 0, 1)));
        Assert.assertEquals(new PercentType(100), converter.convertFromBinding(getDatapoint("LEVEL", 1.0, 0.0, 1.0)));
        Assert.assertEquals(new PercentType(100), converter.convertFromBinding(getDatapoint("LEVEL", 3.4, 0.0, 3.4)));
        Assert.assertEquals(new PercentType(40), converter.convertFromBinding(getDatapoint("LEVEL", 0.4, 0.0, 1.0)));
        Assert.assertEquals(new PercentType(80), converter.convertFromBinding(getDatapoint("LEVEL", 0.8, 0.0, 1.0)));
        Assert.assertEquals(new PercentType(60), converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 0.4)));
        Assert.assertEquals(new PercentType(20), converter.convertFromBinding(getRollerShutterDatapoint("LEVEL", 0.8)));
        Assert.assertEquals(new PercentType(20), converter.convertFromBinding(getDatapoint("LEVEL", 50, 0, 250)));
        Assert.assertEquals(new PercentType(20), converter.convertFromBinding(getDatapoint("LEVEL", 50.0, 0.0, 250.0)));
        Assert.assertEquals(new PercentType(20), converter.convertFromBinding(getDatapoint("LEVEL", "50", 0, 250)));
        Assert.assertEquals(new PercentType(20), converter.convertFromBinding(getDatapoint("LEVEL", "50.0", 0.0, 250.0)));
    }

    @Test
    public void testPercentTypeConverterToBinding() throws Exception {
        PercentTypeConverter converter = new PercentTypeConverter();
        Assert.assertEquals(true, converter.convertToBinding(new PercentType(100), getDatapoint("PRESS_SHORT", true, 0, 1)));
        Assert.assertEquals(false, converter.convertToBinding(new PercentType(0), getDatapoint("PRESS_SHORT", false, 0, 1)));
        Assert.assertEquals(1, converter.convertToBinding(new PercentType(100), getDatapoint("SENSOR", 1, 0, 1)));
        Assert.assertEquals(0, converter.convertToBinding(new PercentType(0), getDatapoint("SENSOR", 0, 0, 1)));
        Assert.assertEquals(0.5, converter.convertToBinding(new PercentType(50), getDatapoint("LEVEL", 0.0, 0, 1)));
        Assert.assertEquals(0.2, converter.convertToBinding(new PercentType(20), getDatapoint("LEVEL", 0.0, 0, 1)));
        Assert.assertEquals(50, converter.convertToBinding(new PercentType(20), getDatapoint("LEVEL", 0, 0, 250)));
        Assert.assertEquals(50, converter.convertToBinding(new PercentType(20), getDatapoint("LEVEL", 0, 0, 250)));
        Assert.assertEquals("50.0", converter.convertToBinding(new PercentType(20), getDatapoint("LEVEL", "text", 0, 250)));
        Assert.assertEquals("50.0", converter.convertToBinding(new PercentType(20), getDatapoint("LEVEL", "text", 0, 250)));
        Assert.assertEquals(0.0, converter.convertToBinding(new PercentType(100), getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(1.0, converter.convertToBinding(new PercentType(0), getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(0.2, converter.convertToBinding(new PercentType(80), getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(0.0, converter.convertToBinding(INCREASE, getRollerShutterDatapoint("LEVEL", 0.1)));
        Assert.assertEquals(0.3, converter.convertToBinding(DECREASE, getRollerShutterDatapoint("LEVEL", 0.2)));
        Assert.assertEquals(20, converter.convertToBinding(INCREASE, getDatapoint("LEVEL", 10, 0, 100)));
        Assert.assertEquals(40, converter.convertToBinding(DECREASE, getDatapoint("LEVEL", 50, 0, 100)));
        Assert.assertEquals(100, converter.convertToBinding(INCREASE, getDatapoint("LEVEL", 100, 0, 100)));
        Assert.assertEquals(0, converter.convertToBinding(DECREASE, getDatapoint("LEVEL", 0, 0, 100)));
        Assert.assertEquals(100, converter.convertToBinding(ON, getDatapoint("LEVEL", 10, 0, 100)));
        Assert.assertEquals(90, converter.convertToBinding(ON, getDatapoint("LEVEL", 10, 0, 90)));
        Assert.assertEquals(0, converter.convertToBinding(OFF, getDatapoint("LEVEL", 10, 0, 90)));
        Assert.assertEquals(0.0, converter.convertToBinding(ON, getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(1.0, converter.convertToBinding(OFF, getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(100, converter.convertToBinding(UP, getDatapoint("LEVEL", 10, 0, 100)));
        Assert.assertEquals(90, converter.convertToBinding(UP, getDatapoint("LEVEL", 10, 0, 90)));
        Assert.assertEquals(0, converter.convertToBinding(DOWN, getDatapoint("LEVEL", 10, 0, 90)));
        Assert.assertEquals(0.0, converter.convertToBinding(DOWN, getRollerShutterDatapoint("LEVEL", 0.0)));
        Assert.assertEquals(1.0, converter.convertToBinding(UP, getRollerShutterDatapoint("LEVEL", 0.0)));
    }

    @Test
    public void testValueListByString() throws Exception {
        StringTypeConverter converter = new StringTypeConverter();
        Assert.assertEquals(new StringType("0"), converter.convertFromBinding(getValueListVariable("0", "0;10;20;30;40;50")));
        Assert.assertEquals(new StringType("10"), converter.convertFromBinding(getValueListVariable("1", "0;10;20;30;40;50")));
        Assert.assertEquals(new StringType("50"), converter.convertFromBinding(getValueListVariable("5", "0;10;20;30;40;50")));
        Assert.assertEquals(new StringType("6"), converter.convertFromBinding(getValueListVariable("6", "0;10;20;30;40;50")));
        Assert.assertEquals(new StringType("10"), converter.convertFromBinding(getValueListVariable(1, "0;10;20;30;40;50")));
        Assert.assertEquals(new StringType("6"), converter.convertFromBinding(getValueListVariable(6, "0;10;20;30;40;50")));
        Assert.assertEquals(new StringType("two"), converter.convertFromBinding(getValueListVariable(1, "one;two")));
        Assert.assertEquals(new StringType("one"), converter.convertFromBinding(getValueListVariable(false, "one;two")));
        Assert.assertEquals(new StringType("two"), converter.convertFromBinding(getValueListVariable(true, "one;two")));
        Assert.assertEquals("0", converter.convertToBinding(new StringType("0"), getValueListVariable("", "0;10;20;30;40;50")));
        Assert.assertEquals("1", converter.convertToBinding(new StringType("10"), getValueListVariable("", "0;10;20;30;40;50")));
        Assert.assertEquals("5", converter.convertToBinding(new StringType("50"), getValueListVariable("", "0;10;20;30;40;50")));
        Assert.assertEquals("2", converter.convertToBinding(new StringType("three"), getValueListVariable("", "one;two;three")));
        Assert.assertEquals("2", converter.convertToBinding(new StringType("three"), getValueListVariable("1", "one;two;three")));
        Assert.assertEquals(2, converter.convertToBinding(new StringType("three"), getValueListVariable(1, "one;two;three")));
        Assert.assertEquals(false, converter.convertToBinding(new StringType("one"), getValueListVariable(false, "one;two")));
        Assert.assertEquals(true, converter.convertToBinding(new StringType("two"), getValueListVariable(true, "one;two")));
    }

    @Test
    public void testValueListByNumber() throws Exception {
        DecimalTypeConverter converter = new DecimalTypeConverter();
        Assert.assertEquals(new DecimalType(0), converter.convertFromBinding(getValueListVariable(0, "0;10;20;30;40;50")));
        Assert.assertEquals(new DecimalType(1), converter.convertFromBinding(getValueListVariable(1, "0;10;20;30;40;50")));
        Assert.assertEquals(new DecimalType(5), converter.convertFromBinding(getValueListVariable(5, "0;10;20;30;40;50")));
        Assert.assertEquals(new DecimalType(6), converter.convertFromBinding(getValueListVariable(6, "0;10;20;30;40;50")));
        Assert.assertEquals(new DecimalType(1), converter.convertFromBinding(getValueListVariable(1, "one;two;three")));
        Assert.assertEquals(new DecimalType(1), converter.convertFromBinding(getValueListVariable("1", "one;two;three")));
        Assert.assertEquals(new DecimalType(1), converter.convertFromBinding(getValueListVariable(true, "one;two;three")));
        Assert.assertEquals(new DecimalType(0), converter.convertFromBinding(getValueListVariable(false, "one;two;three")));
        Assert.assertEquals(0, converter.convertToBinding(new DecimalType("0"), getValueListVariable(0, "0;10;20;30;40;50")));
        Assert.assertEquals(10, converter.convertToBinding(new DecimalType("10"), getValueListVariable(0, "0;10;20;30;40;50")));
        Assert.assertEquals(50, converter.convertToBinding(new DecimalType("50"), getValueListVariable(0, "0;10;20;30;40;50")));
        Assert.assertEquals(60, converter.convertToBinding(new DecimalType("60"), getValueListVariable(0, "0;10;20;30;40;50")));
        Assert.assertEquals(60, converter.convertToBinding(new DecimalType("60"), getValueListVariable(1, "0;10;20;30;40;50")));
        Assert.assertEquals(1, converter.convertToBinding(new DecimalType("1"), getValueListVariable(1, "one;two;three")));
        Assert.assertEquals("1.0", converter.convertToBinding(new DecimalType("1"), getValueListVariable("1", "one;two;three")));
        Assert.assertEquals(true, converter.convertToBinding(new DecimalType("1"), getValueListVariable(true, "one;two;three")));
        Assert.assertEquals(false, converter.convertToBinding(new DecimalType("0"), getValueListVariable(true, "one;two;three")));
    }
}

