/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.knx.internal.dpt;


import DPTXlator1BitControlled.DPT_ALARM_CONTROL;
import DPTXlator1BitControlled.DPT_BINARY_CONTROL;
import DPTXlator1BitControlled.DPT_BOOL_CONTROL;
import DPTXlator1BitControlled.DPT_ENABLE_CONTROL;
import DPTXlator1BitControlled.DPT_INVERT_CONTROL;
import DPTXlator1BitControlled.DPT_OPENCLOSE_CONTROL;
import DPTXlator1BitControlled.DPT_RAMP_CONTROL;
import DPTXlator1BitControlled.DPT_START_CONTROL;
import DPTXlator1BitControlled.DPT_STATE_CONTROL;
import DPTXlator1BitControlled.DPT_STEP_CONTROL;
import DPTXlator1BitControlled.DPT_SWITCH_CONTROL;
import DPTXlator1BitControlled.DPT_UPDOWN_CONTROL;
import DPTXlator2ByteFloat.DPT_AIRQUALITY;
import DPTXlator2ByteFloat.DPT_AIR_PRESSURE;
import DPTXlator2ByteFloat.DPT_HUMIDITY;
import DPTXlator2ByteFloat.DPT_INTENSITY_OF_LIGHT;
import DPTXlator2ByteFloat.DPT_KELVIN_PER_PERCENT;
import DPTXlator2ByteFloat.DPT_POWER;
import DPTXlator2ByteFloat.DPT_POWERDENSITY;
import DPTXlator2ByteFloat.DPT_RAIN_AMOUNT;
import DPTXlator2ByteFloat.DPT_TEMPERATURE;
import DPTXlator2ByteFloat.DPT_TEMPERATURE_DIFFERENCE;
import DPTXlator2ByteFloat.DPT_TEMPERATURE_GRADIENT;
import DPTXlator2ByteFloat.DPT_TEMP_F;
import DPTXlator2ByteFloat.DPT_TIME_DIFFERENCE1;
import DPTXlator2ByteFloat.DPT_TIME_DIFFERENCE2;
import DPTXlator2ByteFloat.DPT_VOLTAGE;
import DPTXlator2ByteFloat.DPT_VOLUME_FLOW;
import DPTXlator2ByteFloat.DPT_WIND_SPEED;
import DPTXlator2ByteFloat.DPT_WIND_SPEED_KMH;
import DPTXlator2ByteUnsigned.DPT_BRIGHTNESS;
import DPTXlator2ByteUnsigned.DPT_ELECTRICAL_CURRENT;
import DPTXlator2ByteUnsigned.DPT_LENGTH;
import DPTXlator2ByteUnsigned.DPT_PROP_DATATYPE;
import DPTXlator2ByteUnsigned.DPT_TIMEPERIOD;
import DPTXlator2ByteUnsigned.DPT_TIMEPERIOD_HOURS;
import DPTXlator2ByteUnsigned.DPT_TIMEPERIOD_MIN;
import DPTXlator2ByteUnsigned.DPT_TIMEPERIOD_SEC;
import DPTXlator2ByteUnsigned.DPT_VALUE_2_UCOUNT;
import DPTXlator3BitControlled.DPT_CONTROL_DIMMING;
import DPTXlator4ByteSigned.DPT_ACTIVE_ENERGY;
import DPTXlator4ByteSigned.DPT_ACTIVE_ENERGY_KWH;
import DPTXlator4ByteSigned.DPT_APPARENT_ENERGY;
import DPTXlator4ByteSigned.DPT_APPARENT_ENERGY_KVAH;
import DPTXlator4ByteSigned.DPT_COUNT;
import DPTXlator4ByteSigned.DPT_DELTA_TIME;
import DPTXlator4ByteSigned.DPT_FLOWRATE;
import DPTXlator4ByteSigned.DPT_REACTIVE_ENERGY;
import DPTXlator4ByteSigned.DPT_REACTIVE_ENERGY_KVARH;
import DPTXlator8BitUnsigned.DPT_SCALING;
import DPTXlatorBoolean.DPT_ALARM;
import DPTXlatorBoolean.DPT_BINARYVALUE;
import DPTXlatorBoolean.DPT_BOOL;
import DPTXlatorBoolean.DPT_DIMSENDSTYLE;
import DPTXlatorBoolean.DPT_ENABLE;
import DPTXlatorBoolean.DPT_INPUTSOURCE;
import DPTXlatorBoolean.DPT_INVERT;
import DPTXlatorBoolean.DPT_LOGICAL_FUNCTION;
import DPTXlatorBoolean.DPT_OCCUPANCY;
import DPTXlatorBoolean.DPT_OPENCLOSE;
import DPTXlatorBoolean.DPT_RAMP;
import DPTXlatorBoolean.DPT_RESET;
import DPTXlatorBoolean.DPT_SCENE_AB;
import DPTXlatorBoolean.DPT_SHUTTER_BLINDS_MODE;
import DPTXlatorBoolean.DPT_START;
import DPTXlatorBoolean.DPT_STATE;
import DPTXlatorBoolean.DPT_STEP;
import DPTXlatorBoolean.DPT_SWITCH;
import DPTXlatorBoolean.DPT_UPDOWN;
import DPTXlatorBoolean.DPT_WINDOW_DOOR;
import DPTXlatorRGB.DPT_RGB;
import DPTXlatorString.DPT_STRING_8859_1;
import DPTXlatorTime.DPT_TIMEOFDAY;
import java.util.Calendar;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StopMoveType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.types.Type;
import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlator4ByteUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitSigned;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlatorDate;
import tuwien.auto.calimero.dptxlator.DPTXlatorDateTime;
import tuwien.auto.calimero.dptxlator.DPTXlatorSceneNumber;
import tuwien.auto.calimero.dptxlator.DPTXlatorString;
import tuwien.auto.calimero.dptxlator.DPTXlatorTime;
import tuwien.auto.calimero.exception.KNXFormatException;


/**
 * This class provides test of the KNXCoreTyperMapper .
 * Tests datapoint types according KNX Association System Specification AS v1.07.00
 *
 * @author Kai Kreuzer
 * @author Volker Daube
 * @author Helmut Lehmeyer
 */
public class KNXCoreTypeMapperTest {
    private final KNXCoreTypeMapper knxCoreTypeMapper = new KNXCoreTypeMapper();

    /**
     * KNXCoreTypeMapper tests method typeMapper.toDPTid()
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testToDPTid() throws KNXFormatException {
        // Test mapping of org.openhab.core.library.types.OnOffType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (OnOffType.class)) + "\""), DPT_SWITCH.getID(), knxCoreTypeMapper.toDPTid(OnOffType.class));
        // Test mapping of org.openhab.core.library.types.IncreaseDecreaseType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (IncreaseDecreaseType.class)) + "\""), DPT_CONTROL_DIMMING.getID(), knxCoreTypeMapper.toDPTid(IncreaseDecreaseType.class));
        // Test mapping of org.openhab.core.library.types.UpDownType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (UpDownType.class)) + "\""), DPT_UPDOWN.getID(), knxCoreTypeMapper.toDPTid(UpDownType.class));
        // Test mapping of org.openhab.core.library.types.StopMoveType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (StopMoveType.class)) + "\""), DPT_START.getID(), knxCoreTypeMapper.toDPTid(StopMoveType.class));
        // Test mapping of org.openhab.core.library.types.OpenClosedType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (OpenClosedType.class)) + "\""), DPT_WINDOW_DOOR.getID(), knxCoreTypeMapper.toDPTid(OpenClosedType.class));
        // Test mapping of org.openhab.core.library.types.PercentType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (PercentType.class)) + "\""), DPT_SCALING.getID(), knxCoreTypeMapper.toDPTid(PercentType.class));
        // Test mapping of org.openhab.core.library.types.DecimalType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (DecimalType.class)) + "\""), DPT_TEMPERATURE.getID(), knxCoreTypeMapper.toDPTid(DecimalType.class));
        // Test mapping of org.openhab.core.library.types.DateTimeType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (DateTimeType.class)) + "\""), DPT_TIMEOFDAY.getID(), knxCoreTypeMapper.toDPTid(DateTimeType.class));
        // Test mapping of org.openhab.core.library.types.StringType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (StringType.class)) + "\""), DPT_STRING_8859_1.getID(), knxCoreTypeMapper.toDPTid(StringType.class));
        // Test mapping of org.openhab.core.library.types.HSBType
        Assert.assertEquals((("knxCoreTypeMapper.toDPTid returned datapoint type for class  \"" + (HSBType.class)) + "\""), DPT_RGB.getID(), knxCoreTypeMapper.toDPTid(HSBType.class));
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.001 DPT_SWITCH
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_001() throws KNXFormatException {
        testTypeMappingB1(DPT_SWITCH, OnOffType.class, "off", "on");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.002 DPT_BOOL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_002() throws KNXFormatException {
        testTypeMappingB1(DPT_BOOL, OnOffType.class, "false", "true");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.003 DPT_ENABLE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_003() throws KNXFormatException {
        testTypeMappingB1(DPT_ENABLE, OnOffType.class, "disable", "enable");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.004 DPT_RAMP
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_004() throws KNXFormatException {
        testTypeMappingB1(DPT_RAMP, OnOffType.class, "no ramp", "ramp");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.005 DPT_ALARM
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_005() throws KNXFormatException {
        testTypeMappingB1(DPT_ALARM, OnOffType.class, "no alarm", "alarm");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.006 DPT_BINARYVALUE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_006() throws KNXFormatException {
        testTypeMappingB1(DPT_BINARYVALUE, OnOffType.class, "low", "high");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.007 DPT_STEP
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_007() throws KNXFormatException {
        testTypeMappingB1(DPT_STEP, OnOffType.class, "decrease", "increase");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.008 DPT_UPDOWN
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_008() throws KNXFormatException {
        testTypeMappingB1(DPT_UPDOWN, UpDownType.class, "up", "down");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.009 DPT_UPDOWN
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_009() throws KNXFormatException {
        testTypeMappingB1(DPT_OPENCLOSE, OpenClosedType.class, "open", "close");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.010 DPT_START
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_010() throws KNXFormatException {
        testTypeMappingB1(DPT_START, StopMoveType.class, "stop", "start");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.011 DPT_STATE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_011() throws KNXFormatException {
        testTypeMappingB1(DPT_STATE, OnOffType.class, "inactive", "active");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.012 DPT_INVERT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_012() throws KNXFormatException {
        testTypeMappingB1(DPT_INVERT, OnOffType.class, "not inverted", "inverted");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.013 DPT_DIMSENDSTYLE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_013() throws KNXFormatException {
        testTypeMappingB1(DPT_DIMSENDSTYLE, OnOffType.class, "start/stop", "cyclic");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.014 DPT_INPUTSOURCE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_014() throws KNXFormatException {
        testTypeMappingB1(DPT_INPUTSOURCE, OnOffType.class, "fixed", "calculated");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.015 DPT_RESET
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_015() throws KNXFormatException {
        testTypeMappingB1(DPT_RESET, OnOffType.class, "no action", "reset");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.018 DPT_OCCUPANCY
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_018() throws KNXFormatException {
        testTypeMappingB1(DPT_OCCUPANCY, OnOffType.class, "not occupied", "occupied");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.019 DPT_WINDOW_DOOR
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_019() throws KNXFormatException {
        testTypeMappingB1(DPT_WINDOW_DOOR, OpenClosedType.class, "closed", "open");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.021
     * DPT_LOGICAL_FUNCTION
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_021() throws KNXFormatException {
        testTypeMappingB1(DPT_LOGICAL_FUNCTION, OnOffType.class, "OR", "AND");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.022 DPT_SCENE_AB
     * Remark: mapped to DecimalType
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_022() throws KNXFormatException {
        testTypeMappingB1(DPT_SCENE_AB, DecimalType.class, "0", "1");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B1" KNX ID: 1.023
     * DPT_SHUTTER_BLINDS_MODE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB1_1_023() throws KNXFormatException {
        testTypeMappingB1(DPT_SHUTTER_BLINDS_MODE, OnOffType.class, "only move up/down", "move up/down + step-stop");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.001 DPT_SWITCH_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_001() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_SWITCH_CONTROL, "off", "on");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.002 DPT_BOOL_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_002() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_BOOL_CONTROL, "false", "true");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.003 DPT_ENABLE_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_003() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_ENABLE_CONTROL, "disable", "enable");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.004 DPT_RAMP_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_004() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_RAMP_CONTROL, "no ramp", "ramp");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.005 DPT_ALARM_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_005() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_ALARM_CONTROL, "no alarm", "alarm");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.006 DPT_BINARY_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_006() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_BINARY_CONTROL, "low", "high");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.007 DPT_STEP_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_007() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_STEP_CONTROL, "decrease", "increase");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.008 DPT_UPDOWN_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_008() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_UPDOWN_CONTROL, "up", "down");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.009 DPT_SWITCH_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_009() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_OPENCLOSE_CONTROL, "open", "close");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.010 DPT_START_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_010() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_START_CONTROL, "stop", "start");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.011 DPT_STATE_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_011() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_STATE_CONTROL, "inactive", "active");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Datapoint Types B2" KNX ID: 2.012 DPT_INVERT_CONTROL
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingB2_2_012() throws KNXFormatException {
        testTypeMappingB1_Controlled(DPT_INVERT_CONTROL, "not inverted", "inverted");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?8-Bit Unsigned Value" KNX ID: 5.001 DPT_SCALING
     *
     * This data type is a ?Multi-state? type, according KNX spec. No exact linear conversion from value to byte(s) and
     * reverse is required, since rounding is
     * involved.
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping8BitUnsigned_5_001() throws KNXFormatException {
        DPT dpt = DPTXlator8BitUnsigned.DPT_SCALING;
        testToTypeClass(dpt, PercentType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, PercentType.class));
        Type type = testToType(dpt, new byte[]{ 0 }, PercentType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (128)) }, PercentType.class);
        testToDPTValue(dpt, type, "50");
        type = testToType(dpt, new byte[]{ ((byte) (255)) }, PercentType.class);
        testToDPTValue(dpt, type, "100");
        // Use a too long byte array expecting that additional bytes will be ignored
        type = testToType(dpt, new byte[]{ ((byte) (255)), 0 }, PercentType.class);
        testToDPTValue(dpt, type, "100");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?8-Bit Unsigned Value" KNX ID: 5.003 DPT_ANGLE
     *
     * This data type is a ?Multi-state? type, according KNX spec. No exact linear conversion from value to byte(s) and
     * reverse is required, since rounding is
     * involved.
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping8BitUnsigned_5_003() throws KNXFormatException {
        DPT dpt = DPTXlator8BitUnsigned.DPT_ANGLE;
        testToTypeClass(dpt, DecimalType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DecimalType.class));
        Type type = testToType(dpt, new byte[]{ 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (127)) }, DecimalType.class);
        testToDPTValue(dpt, type, "179");
        type = testToType(dpt, new byte[]{ ((byte) (128)) }, DecimalType.class);
        testToDPTValue(dpt, type, "181");
        type = testToType(dpt, new byte[]{ ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "360");
        // Use a too long byte array expecting that additional bytes will be ignored
        type = testToType(dpt, new byte[]{ ((byte) (255)), 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "360");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType()for type ?8-Bit Unsigned Value" KNX ID: 5.004 DPT_PERCENT_U8
     * (previously name DPT_RelPos_Valve)
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping8BitUnsigned_5_004() throws KNXFormatException {
        DPT dpt = DPTXlator8BitUnsigned.DPT_PERCENT_U8;
        testToTypeClass(dpt, PercentType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, PercentType.class));
        Type type = testToType(dpt, new byte[]{ 0 }, PercentType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ 50 }, PercentType.class);
        testToDPTValue(dpt, type, "50");
        type = testToType(dpt, new byte[]{ 100 }, PercentType.class);
        testToDPTValue(dpt, type, "100");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?8-Bit Unsigned Value" KNX ID: 5.005
     * DPT_DECIMALFACTOR
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping8BitUnsigned_5_005() throws KNXFormatException {
        DPT dpt = DPTXlator8BitUnsigned.DPT_DECIMALFACTOR;
        testToTypeClass(dpt, DecimalType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DecimalType.class));
        Type type = testToType(dpt, new byte[]{ 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "255");
        // Use a too long byte array expecting that additional bytes will be ignored
        type = testToType(dpt, new byte[]{ ((byte) (255)), 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "255");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?8-Bit Unsigned Value" KNX ID: 5.006 DPT_TARRIF
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping8BitUnsigned_5_006() throws KNXFormatException {
        DPT dpt = DPTXlator8BitUnsigned.DPT_TARIFF;
        testToTypeClass(dpt, DecimalType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DecimalType.class));
        Type type = testToType(dpt, new byte[]{ 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "255");
        // Use a too long byte array expecting that additional bytes will be ignored
        type = testToType(dpt, new byte[]{ ((byte) (255)), 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "255");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?8-Bit Unsigned Value" KNX ID: 5.010
     * DPT_VALUE_1_UCOUNT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping8BitUnsigned_5_010() throws KNXFormatException {
        DPT dpt = DPTXlator8BitUnsigned.DPT_VALUE_1_UCOUNT;
        testToTypeClass(dpt, DecimalType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DecimalType.class));
        Type type = testToType(dpt, new byte[]{ 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "255");
        // Use a too long byte array expecting that additional bytes will be ignored
        type = testToType(dpt, new byte[]{ ((byte) (255)), 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "255");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?8-Bit Signed Value" KNX ID: 6.001
     * DPT_PERCENT_V8
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping8BitSigned_6_001() throws KNXFormatException {
        DPT dpt = DPTXlator8BitSigned.DPT_PERCENT_V8;
        testToTypeClass(dpt, PercentType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, PercentType.class));
        Type type = testToType(dpt, new byte[]{ 0 }, PercentType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (100)) }, PercentType.class);
        testToDPTValue(dpt, type, "100");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?8-Bit Signed Value" KNX ID: 6.010
     * DPT_VALUE_1_UCOUNT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping8BitSigned_6_010() throws KNXFormatException {
        DPT dpt = DPTXlator8BitSigned.DPT_VALUE_1_UCOUNT;
        testToTypeClass(dpt, DecimalType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DecimalType.class));
        Type type = testToType(dpt, new byte[]{ 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (127)) }, DecimalType.class);
        testToDPTValue(dpt, type, "127");
        type = testToType(dpt, new byte[]{ ((byte) (128)) }, DecimalType.class);
        testToDPTValue(dpt, type, "-128");
        type = testToType(dpt, new byte[]{ ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "-1");
        // Use a too long byte array expecting that additional bytes will be ignored
        type = testToType(dpt, new byte[]{ ((byte) (255)), 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "-1");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Unsigned Value" KNX ID: 7.001
     * DPT_VALUE_2_UCOUNT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_001() throws KNXFormatException {
        testTypeMapping2ByteUnsigned(DPT_VALUE_2_UCOUNT);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Unsigned Value" KNX ID: 7.002 DPT_TIMEPERIOD
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_002() throws KNXFormatException {
        testTypeMapping2ByteUnsigned(DPT_TIMEPERIOD);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Unsigned Value" KNX ID: 7.003
     * DPT_TIMEPERIOD_10
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_003() throws KNXFormatException {
        DPT dpt = DPTXlator2ByteUnsigned.DPT_TIMEPERIOD_10;
        testToTypeClass(dpt, DecimalType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DecimalType.class));
        Type type = testToType(dpt, new byte[]{ 0, 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (255)), 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "652800");
        type = testToType(dpt, new byte[]{ ((byte) (255)), ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "655350");
        // Use a too long byte array expecting that additional bytes will be ignored
        type = testToType(dpt, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "655350");
    }

    /**
     * KNXCoreTypeMapper tests for method typeMapper.toType() type ?2-Octet Unsigned Value" KNX ID: 7.004
     * DPT_TIMEPERIOD_100
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_004() throws KNXFormatException {
        DPT dpt = DPTXlator2ByteUnsigned.DPT_TIMEPERIOD_100;
        testToTypeClass(dpt, DecimalType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DecimalType.class));
        Type type = testToType(dpt, new byte[]{ 0, 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (255)), 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "6528000");
        type = testToType(dpt, new byte[]{ ((byte) (255)), ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "6553500");
        // Use a too long byte array expecting that additional bytes will be ignored
        type = testToType(dpt, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "6553500");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() method typeMapper.toType() for type ?2-Octet Unsigned Value"
     * KNX ID: 7.005 DPT_TIMEPERIOD_SEC
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_005() throws KNXFormatException {
        testTypeMapping2ByteUnsigned(DPT_TIMEPERIOD_SEC);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() method typeMapper.toType() for type ?2-Octet Unsigned Value"
     * KNX ID: 7.006 DPT_TIMEPERIOD_MIN
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_006() throws KNXFormatException {
        testTypeMapping2ByteUnsigned(DPT_TIMEPERIOD_MIN);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Unsigned Value" KNX ID: 7.007
     * DPT_TIMEPERIOD_HOURS
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_007() throws KNXFormatException {
        testTypeMapping2ByteUnsigned(DPT_TIMEPERIOD_HOURS);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Unsigned Value" KNX ID: 7.010
     * DPT_PROP_DATATYPE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_010() throws KNXFormatException {
        testTypeMapping2ByteUnsigned(DPT_PROP_DATATYPE);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Unsigned Value" KNX ID: 7.011 DPT_LENGTH
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_011() throws KNXFormatException {
        testTypeMapping2ByteUnsigned(DPT_LENGTH);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Unsigned Value" KNX ID: 7.012
     * DPT_ELECTRICAL_CURRENT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_012() throws KNXFormatException {
        testTypeMapping2ByteUnsigned(DPT_ELECTRICAL_CURRENT);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Unsigned Value" KNX ID: 7.013 DPT_BRIGHTNESS
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteUnsigned_7_013() throws KNXFormatException {
        testTypeMapping2ByteUnsigned(DPT_BRIGHTNESS);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.001. DPT_TEMPERATURE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_001() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_TEMPERATURE);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.002.
     * DPT_TEMPERATURE_DIFFERENCE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_002() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_TEMPERATURE_DIFFERENCE);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.003.
     * DPT_TEMPERATURE_GRADIENT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_003() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_TEMPERATURE_GRADIENT);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.004.
     * DPT_INTENSITY_OF_LIGHT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_004() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_INTENSITY_OF_LIGHT);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.005. DPT_WIND_SPEED
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_005() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_WIND_SPEED);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.006.
     * DPT_AIR_PRESSURE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_006() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_AIR_PRESSURE);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.007. DPT_HUMIDITY
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_007() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_HUMIDITY, PercentType.class);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.008. DPT_AIRQUALITY
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_008() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_AIRQUALITY);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.010.
     * DPT_TIME_DIFFERENCE1
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_010() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_TIME_DIFFERENCE1);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.011.
     * DPT_TIME_DIFFERENCE2
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_011() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_TIME_DIFFERENCE2);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.020. DPT_VOLTAGE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_020() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_VOLTAGE);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.021.
     * DPT_ELECTRICAL_CURRENT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_021() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPTXlator2ByteFloat.DPT_ELECTRICAL_CURRENT);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.022.
     * DPT_POWERDENSITY
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_022() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_POWERDENSITY);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.023.
     * DPT_KELVIN_PER_PERCENT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_023() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_KELVIN_PER_PERCENT);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.024. DPT_POWER
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_024() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_POWER);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.025. DPT_VOLUME_FLOW
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_025() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_VOLUME_FLOW);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.026. DPT_RAIN_AMOUNT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_026() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_RAIN_AMOUNT);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.027. DPT_TEMP_F
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_027() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_TEMP_F);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?2-Octet Float Value". KNX ID: 9.028.
     * DPT_WIND_SPEED_KMH
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping2ByteFloat_9_028() throws KNXFormatException {
        testTypeMapping2ByteFloat(DPT_WIND_SPEED_KMH);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Time" KNX ID: 10.001 DPT_TIMEOFDAY
     *
     * Test case: positive tests
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingTime_10_001() throws KNXFormatException {
        DPT dpt = DPTXlatorTime.DPT_TIMEOFDAY;
        testToTypeClass(dpt, DateTimeType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DateTimeType.class));
        // Use a too long byte array expecting that additional bytes will be ignored
        Type type = testToType(dpt, new byte[]{ 32, 0, 0, ((byte) (255)) }, DateTimeType.class);
        testToDPTValue(dpt, type, "Mon, 00:00:00");
        /* Set day to no day, 0 hours, 0 minutes and 0 seconds */
        type = testToType(dpt, new byte[]{ 0, 0, 0 }, DateTimeType.class);
        String today = String.format(Locale.US, "%1$ta", Calendar.getInstance());
        testToDPTValue(dpt, type, (today + ", 00:00:00"));
        /* Set day to Monday, 0 hours, 0 minutes and 0 seconds January 5th, 1970 was a Monday */
        type = testToType(dpt, new byte[]{ 32, 0, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Mon, 00:00:00");
        /* * Set day to Tuesday, 0 hours, 0 minutes and 0 seconds January 6th, 1970 was a Tuesday */
        type = testToType(dpt, new byte[]{ 64, 0, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Tue, 00:00:00");
        /* Set day to Wednesday, 0 hours, 0 minutes and 0 seconds January 7th, 1970 was a Wednesday */
        type = testToType(dpt, new byte[]{ 96, 0, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Wed, 00:00:00");
        /* Set day to Thursday, 0 hours, 0 minutes and 0 seconds January 1st, 1970 was a Thursday */
        type = testToType(dpt, new byte[]{ ((byte) (128)), 0, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Thu, 00:00:00");
        /* Set day to Friday, 0 hours, 0 minutes and 0 seconds January 2nd, 1970 was a Friday */
        type = testToType(dpt, new byte[]{ ((byte) (160)), 0, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Fri, 00:00:00");
        /* Set day to Saturday, 0 hours, 0 minutes and 0 seconds January 3rd, 1970 was a Saturday */
        type = testToType(dpt, new byte[]{ ((byte) (192)), 0, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Sat, 00:00:00");
        /* Set day to Sunday, 0 hours, 0 minutes and 0 seconds January 4th, 1970 was a Sunday */
        type = testToType(dpt, new byte[]{ ((byte) (224)), 0, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Sun, 00:00:00");
        /* Set day to Monday, 1 hour, 0 minutes and 0 seconds */
        type = testToType(dpt, new byte[]{ 33, 0, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Mon, 01:00:00");
        /* Set day to Monday, 0 hour, 1 minute and 0 seconds */
        type = testToType(dpt, new byte[]{ 32, 1, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Mon, 00:01:00");
        /* Set day to Monday, 0 hour, 0 minute and 1 seconds */
        type = testToType(dpt, new byte[]{ 32, 0, 1 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Mon, 00:00:01");
        /* Set day to Monday, 23 hours, 59 minutes and 59 seconds */
        type = testToType(dpt, new byte[]{ 55, 59, 59 }, DateTimeType.class);
        testToDPTValue(dpt, type, "Mon, 23:59:59");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Time" KNX ID: 10.001 DPT_TIMEOFDAY
     *
     * Test case: Set day to Monday, 24 hours, 59 minutes and 59 seconds
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingTime_10_001_HoursOutOfBounds() throws KNXFormatException {
        DPT dpt = DPTXlatorTime.DPT_TIMEOFDAY;
        testToTypeClass(dpt, DateTimeType.class);
        Assert.assertNull("knxCoreTypeMapper.toType() should return null", testToType(dpt, new byte[]{ 56, 59, 59 }, DateTimeType.class));
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Time" KNX ID: 10.001 DPT_TIMEOFDAY
     *
     * Set day to Monday, 23 hours, 60 minutes and 59 seconds
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingTime_10_001_MinutesOutOfBounds() throws KNXFormatException {
        DPT dpt = DPTXlatorTime.DPT_TIMEOFDAY;
        testToTypeClass(dpt, DateTimeType.class);
        Assert.assertNull("knxCoreTypeMapper.toType() should return null", testToType(dpt, new byte[]{ 55, 60, 59 }, DateTimeType.class));
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Time" KNX ID: 10.001 DPT_TIMEOFDAY
     *
     * Set day to Monday, 23 hours, 59 minutes and 60 seconds
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingTime_10_001_SecondsOutOfBounds() throws KNXFormatException {
        DPT dpt = DPTXlatorTime.DPT_TIMEOFDAY;
        testToTypeClass(dpt, DateTimeType.class);
        Assert.assertNull("knxCoreTypeMapper.toType() should return null", testToType(dpt, new byte[]{ 55, 59, 60 }, DateTimeType.class));
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Time" KNX ID: 11.001 DPT_DATE
     *
     * Test illegal data (day and month cannot be 0) This should throw an KNXIllegalArgumentException
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingDate_11_001_ZeroDay() throws KNXFormatException {
        DPT dpt = DPTXlatorDate.DPT_DATE;
        testToTypeClass(dpt, DateTimeType.class);
        Assert.assertNull("knxCoreTypeMapper.toType() should return null", testToType(dpt, new byte[]{ 0, 0, 0 }, DateTimeType.class));
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Time" KNX ID: 11.001 DPT_DATE
     *
     * Test illegal day (cannot be 0) This should throw an KNXIllegalArgumentException
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingDate_11_001__DayZero() throws KNXFormatException {
        DPT dpt = DPTXlatorDate.DPT_DATE;
        testToTypeClass(dpt, DateTimeType.class);
        Assert.assertNull("knxCoreTypeMapper.toType() should return null", testToType(dpt, new byte[]{ 0, 1, 0 }, DateTimeType.class));
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Time" KNX ID: 11.001 DPT_DATE
     *
     * Test illegal month (cannot be 0) This should throw an KNXIllegalArgumentException
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingDate_11_001__ZeroMonth() throws KNXFormatException {
        DPT dpt = DPTXlatorDate.DPT_DATE;
        testToTypeClass(dpt, DateTimeType.class);
        Assert.assertNull("knxCoreTypeMapper.toType() should return null", testToType(dpt, new byte[]{ 1, 0, 0 }, DateTimeType.class));
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Time" KNX ID: 11.001 DPT_DATE
     *
     * Test correct year evaluation according KNX spec.
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingDate_11_001() throws KNXFormatException {
        DPT dpt = DPTXlatorDate.DPT_DATE;
        testToTypeClass(dpt, DateTimeType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DateTimeType.class));
        // Use a too long byte array expecting that additional bytes will be ignored
        Type type = testToType(dpt, new byte[]{ 1, 1, 0, ((byte) (255)) }, DateTimeType.class);
        testToDPTValue(dpt, type, "2000-01-01");
        type = testToType(dpt, new byte[]{ 1, 1, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "2000-01-01");
        type = testToType(dpt, new byte[]{ 1, 1, 99 }, DateTimeType.class);
        testToDPTValue(dpt, type, "1999-01-01");
        type = testToType(dpt, new byte[]{ 1, 1, 90 }, DateTimeType.class);
        testToDPTValue(dpt, type, "1990-01-01");
        type = testToType(dpt, new byte[]{ 1, 1, 89 }, DateTimeType.class);
        testToDPTValue(dpt, type, "2089-01-01");
        // Test roll over (which is actually not in the KNX spec)
        type = testToType(dpt, new byte[]{ 31, 2, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "2000-03-02");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Unsigned Value" KNX ID: 12.001
     * DPT_VALUE_4_UCOUNT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteUnsigned_12_001() throws KNXFormatException {
        DPT dpt = DPTXlator4ByteUnsigned.DPT_VALUE_4_UCOUNT;
        testToTypeClass(dpt, DecimalType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DecimalType.class));
        // Use a too long byte array expecting that additional bytes will be ignored
        Type type = testToType(dpt, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "4294967295");
        type = testToType(dpt, new byte[]{ 0, 0, 0, 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "4294967295");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Signed Value" KNX ID: 13.001 DPT_COUNT
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteSigned_13_001() throws KNXFormatException {
        testTypeMapping4ByteSigned(DPT_COUNT);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Signed Value" KNX ID: 13.002 DPT_FLOWRATE
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteSigned_13_002() throws KNXFormatException {
        testTypeMapping4ByteSigned(DPT_FLOWRATE);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Signed Value" KNX ID: 13.010
     * DPT_ACTIVE_ENERGY
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteSigned_13_010() throws KNXFormatException {
        testTypeMapping4ByteSigned(DPT_ACTIVE_ENERGY);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Signed Value" KNX ID: 13.011
     * DPT_APPARENT_ENERGY
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteSigned_13_011() throws KNXFormatException {
        testTypeMapping4ByteSigned(DPT_APPARENT_ENERGY);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Signed Value" KNX ID: 13.012
     * DPT_REACTIVE_ENERGY
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteSigned_13_012() throws KNXFormatException {
        testTypeMapping4ByteSigned(DPT_REACTIVE_ENERGY);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Signed Value" KNX ID: 13.013
     * DPT_ACTIVE_ENERGY_KWH
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteSigned_13_013() throws KNXFormatException {
        testTypeMapping4ByteSigned(DPT_ACTIVE_ENERGY_KWH);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Signed Value" KNX ID: 13.014
     * DPT_APPARENT_ENERGY_KVAH
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteSigned_13_014() throws KNXFormatException {
        testTypeMapping4ByteSigned(DPT_APPARENT_ENERGY_KVAH);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Signed Value" KNX ID: 13.015
     * DPT_REACTIVE_ENERGY_KVARH
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteSigned_13_015() throws KNXFormatException {
        testTypeMapping4ByteSigned(DPT_REACTIVE_ENERGY_KVARH);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?4-Octet Signed Value" KNX ID: 13.100 DPT_DELTA_TIME
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMapping4ByteSigned_13_100() throws KNXFormatException {
        testTypeMapping4ByteSigned(DPT_DELTA_TIME);
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?String" KNX ID: 16.001 DPT_STRING_8859_1
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingString() throws KNXFormatException {
        DPT dpt = DPTXlatorString.DPT_STRING_8859_1;
        testToTypeClass(dpt, StringType.class);
        /* According to spec the length of this DPT is fixed to 14 bytes.

        Test the that a too short array results in an <null> string. There should be an error logged by calimero lib
        (V2.2.0).
         */
        Type type = testToType(dpt, new byte[]{ 97, 98 }, StringType.class);
        testToDPTValue(dpt, type, null);
        /* FIXME: According to spec the length of this DPT is fixed to 14 bytes. Calimero lib (V 2.2.0) isn't checking
        this correctly and has a bug in
        tuwien.auto.calimero.dptxlator.DPTXlatorString.toDPT(final byte[] buf, final int offset). Calimero accepts
        any byte array larger or equal to 14 bytes
        without error. As a result: anything less then 14 bytes and above a multiple of 14 bytes will be accepted but
        cutoff. Even for the failed check (less
        then 14 bytes) calimero is not throwing an exception but is logging an error which we cannot check for here.

        Test the erroneous behavior that a too long arrays result in a cutoff string. There probably won't be an
        error logged by calimero lib (V2.2.0).
         */
        type = testToType(dpt, new byte[]{ 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111 }, StringType.class);
        testToDPTValue(dpt, type, "abcdefghijklmn");
        /* Test a 14 byte array. */
        type = testToType(dpt, new byte[]{ 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110 }, StringType.class);
        testToDPTValue(dpt, type, "abcdefghijklmn");
        /* Test that a byte array filled with 0 and correct length is resulting in an empty string. */
        type = testToType(dpt, new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, StringType.class);
        testToDPTValue(dpt, type, "");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Scene Number" KNX ID: 17.001 DPT_SCENE_NUMBER
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingSceneNumber_17_001() throws KNXFormatException {
        DPT dpt = DPTXlatorSceneNumber.DPT_SCENE_NUMBER;
        testToTypeClass(dpt, DecimalType.class);
        // Use a too short byte array
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (required data length too short)", testToType(dpt, new byte[]{  }, DecimalType.class));
        // Use a too long byte array expecting that additional bytes will be ignored
        Type type = testToType(dpt, new byte[]{ ((byte) (255)), 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "63");
        type = testToType(dpt, new byte[]{ 0 }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        type = testToType(dpt, new byte[]{ 63 }, DecimalType.class);
        testToDPTValue(dpt, type, "63");
        // Test that the 2 msb (reserved) are ignored
        type = testToType(dpt, new byte[]{ ((byte) (192)) }, DecimalType.class);
        testToDPTValue(dpt, type, "0");
        // Test that the 2 msb (reserved) are ignored
        type = testToType(dpt, new byte[]{ ((byte) (255)) }, DecimalType.class);
        testToDPTValue(dpt, type, "63");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Date Time" KNX ID: 19.001 DPT_DATE_TIME
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingDateTime_19_001() throws KNXFormatException {
        DPT dpt = DPTXlatorDateTime.DPT_DATE_TIME;
        testToTypeClass(dpt, DateTimeType.class);
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (no-day)", testToType(dpt, new byte[]{ 0, 1, 1, 0, 0, 0, 0, 0 }, DateTimeType.class));
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (illegal date)", testToType(dpt, new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, DateTimeType.class));
        /* Reference testcase
        Monday, January 1st, 1900 01:02:03, Fault: Normal (no fault), Working Day: Bank day (No working day), Working
        Day Field: valid,
        Year Field valid, Months and Day fields valid, Day of week field valid, Hour of day, Minutes and Seconds
        fields valid,
        Standard Summer Time: Time = UT+X, Quality of Clock: clock without ext. sync signal
         */
        Type type = testToType(dpt, new byte[]{ 0, 1, 1, 33, 2, 3, 0, 0 }, DateTimeType.class);
        Assert.assertNotNull((("Failed to get a type for the datapoint '" + dpt) + "'"), type);
        testToDPTValue(dpt, type, "1900-01-01 01:02:03");
        /* Reference testcase + Fault: Fault => not supported */
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (faulty clock)", testToType(dpt, new byte[]{ 0, 1, 1, 32, 0, 0, ((byte) (128)), 0 }, DateTimeType.class));
        /* Reference testcase + Year Field invalid => not supported */
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (date but no year)", testToType(dpt, new byte[]{ 0, 1, 1, 32, 0, 0, 16, 0 }, DateTimeType.class));
        /* Reference testcase + Months and Day fields invalid => not supported */
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (date but no day and month)", testToType(dpt, new byte[]{ 0, 1, 1, 32, 0, 0, 8, 0 }, DateTimeType.class));
        /* Reference testcase + Year, Months and Day fields invalid */
        type = testToType(dpt, new byte[]{ 0, 1, 1, 33, 2, 3, 24, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "1970-01-01 01:02:03");
        /* Reference testcase + Year , Months and Day fields invalid + Day of week field invalid */
        type = testToType(dpt, new byte[]{ 0, 1, 1, 33, 2, 3, 28, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "1970-01-01 01:02:03");
        /* Reference testcase + Year, Months and Day fields invalid + Day of week field invalid
        Working day field invalid
         */
        type = testToType(dpt, new byte[]{ 0, 1, 1, 33, 2, 3, 60, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "1970-01-01 01:02:03");
        /* Reference testcase + Year Field invalid + Months and Day fields invalid + Day of week field invalid
        Working day field invalid + Hour of day, Minutes and Seconds fields invalid
         */
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (neither date nor time)", testToType(dpt, new byte[]{ 0, 1, 1, 32, 0, 0, 62, 0 }, DateTimeType.class));
        /* Reference testcase + Year, Months and Day fields invalid + Day of week field invalid
        Working day field invalid + Hour of day, Minutes and Seconds fields invalid, Standard Summer Time: Time =
        UT+X+1
         */
        Assert.assertNull("knxCoreTypeMapper.toType() should return null (neither date nor time, but summertime flag)", (type = testToType(dpt, new byte[]{ 0, 1, 1, 32, 0, 0, 63, 0 }, DateTimeType.class)));
        /* Reference testcase + day of week=Any day, Day of week field invalid */
        type = testToType(dpt, new byte[]{ 0, 1, 1, 0, 0, 0, 4, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "1900-01-01 00:00:00");
        /* Reference testcase + Day of week field invalid */
        type = testToType(dpt, new byte[]{ 0, 1, 1, 32, 0, 0, 4, 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "1900-01-01 00:00:00");
        /* Reference testcase + day of week=Any day, Day of week field invalid, working day, working day field invalid */
        type = testToType(dpt, new byte[]{ 0, 1, 1, 32, 0, 0, ((byte) (96)), 0 }, DateTimeType.class);
        testToDPTValue(dpt, type, "1900-01-01 00:00:00");
        /* December 31st, 2155 day of week=Any day, Day of week field invalid */
        type = testToType(dpt, new byte[]{ ((byte) (255)), 12, 31, 23, 59, 59, ((byte) (4)), ((byte) (0)) }, DateTimeType.class);
        testToDPTValue(dpt, type, "2155-12-31 23:59:59");
        /* December 31st, 2155, 24:00:00, day of week=Any day, Day of week field invalid

        TODO: this test case should test for "2155-12-31 24:00:00" since that is what the (valid) KNX bytes
        represent.
        Nevertheless, calimero is "cheating" by using the milliseconds such that "23:59:59.999" is interpreted as
        "23:59:59"
        OpenHAB's DateTimeType doesn't support milliseconds (at least not when parsing from a String), hence 24:00:00
        cannot be mapped.
         */
        type = testToType(dpt, new byte[]{ ((byte) (255)), 12, 31, 24, 0, 0, ((byte) (4)), ((byte) (0)) }, DateTimeType.class);
        testToDPTValue(dpt, type, "2155-12-31 23:59:59");
        /* December 31st, 2014 24:00:00, day of week=Any day, Day of week field invalid

        TODO: this test case should test for "2155-12-31 24:00:00" since that is what the (valid) KNX bytes
        represent.
        Nevertheless, calimero is "cheating" by using the milliseconds such that "23:59:59.999" is interpreted as
        "23:59:59"
        OpenHAB's DateTimeType doesn't support milliseconds (at least not when parsing from a String), hence 24:00:00
        cannot be mapped.
         */
        type = testToType(dpt, new byte[]{ ((byte) (114)), 12, 31, 24, 0, 0, ((byte) (4)), ((byte) (0)) }, DateTimeType.class);
        testToDPTValue(dpt, type, "2014-12-31 23:59:59");
    }

    /**
     * KNXCoreTypeMapper tests method typeMapper.toType() for type ?Date Time" KNX ID: 19.001 DPT_DATE_TIME
     * Testcase tests handling of Daylight Savings Time flag (DST).
     * Interpretation of DST is depending on default timezone, hence we're trying to test using
     * different timezones: default, New York, Berlin and Shanghai. Shanghai not having a DST.
     *
     * @throws KNXFormatException
     * 		
     */
    @Test
    public void testTypeMappingDateTime_19_001_DST() throws KNXFormatException {
        DPT dpt = DPTXlatorDateTime.DPT_DATE_TIME;
        // 2014-07-31 00:00:00 DST flag set
        byte[] testDataDST = new byte[]{ 114, 7, 31, 0, 0, 0, ((byte) (5)), ((byte) (0)) };
        // 2014-07-31 00:00:00 DST flag cleared
        byte[] testDataNoDST = new byte[]{ 114, 7, 31, 0, 0, 0, ((byte) (4)), ((byte) (0)) };
        testToTypeClass(dpt, DateTimeType.class);
        Calendar c = Calendar.getInstance();
        c.set(2014, 7, 31);
        if ((c.get(Calendar.DST_OFFSET)) > 0) {
            // Should be null since we have a DST timezone but non-DST data: should be rejected
            Assert.assertNull(testToType(dpt, testDataNoDST, DateTimeType.class));
            Type type = testToType(dpt, testDataDST, DateTimeType.class);
            testToDPTValue(dpt, type, "2014-07-31 00:00:00");
        } else {
            // Should be null since we don't have a non-DST timezone but DST data: should be rejected
            Assert.assertNull(testToType(dpt, testDataDST, DateTimeType.class));
            Type type = testToType(dpt, testDataNoDST, DateTimeType.class);
            testToDPTValue(dpt, type, "2014-07-31 00:00:00");
        }
    }
}

