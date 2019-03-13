/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.stiebelheatpump.protocol;


import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.stiebelheatpump.internal.ConfigParserTest;
import org.openhab.binding.stiebelheatpump.internal.StiebelHeatPumpException;


public class StiebelHeatPumpDataParserTest {
    private DataParser parser = new DataParser();

    private ConfigParserTest configParserTest = new ConfigParserTest();

    private List<Request> configuration = null;

    // request parser tests
    // request FD
    @Test
    public void testParseVersion() throws StiebelHeatPumpException {
        // verify the version from heat pump
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "Version";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (181)), ((byte) (253)), ((byte) (1)), ((byte) (182)), ((byte) (16)), ((byte) (3)) };
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        data.get("Version");
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Assert.assertEquals("4.38", data.get("Version"));
    }

    // request FC
    @Test
    public void testParseTime() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "Time";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (121)), ((byte) (252)), ((byte) (0)), ((byte) (2)), ((byte) (10)), ((byte) (33)), ((byte) (36)), ((byte) (14)), ((byte) (0)), ((byte) (3)), ((byte) (26)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("2", data.get("WeekDay"));
        Assert.assertEquals("10", data.get("Hours"));
        Assert.assertEquals("33", data.get("Minutes"));
        Assert.assertEquals("36", data.get("Seconds"));
        Assert.assertEquals("14", data.get("Year"));
        Assert.assertEquals("3", data.get("Month"));
        Assert.assertEquals("26", data.get("Day"));
    }

    // request O9
    @Test
    public void testParseOperationCounters() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "OperationCounters";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (234)), ((byte) (9)), ((byte) (0)), ((byte) (26)), ((byte) (0)), ((byte) (25)), ((byte) (12)), ((byte) (119)), ((byte) (2)), ((byte) (40)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("26", data.get("CompressorA"));
        Assert.assertEquals("25", data.get("CompressorB"));
        Assert.assertEquals("3191", data.get("HeatingMode"));
        Assert.assertEquals("552", data.get("DHWMode"));
        Assert.assertEquals("0", data.get("CoolingMode"));
    }

    // request FB
    @Test
    public void testParseCurrentValues() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "CurrentValues";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (72)), ((byte) (251)), ((byte) (1)), ((byte) (170)), ((byte) (0)), ((byte) (114)), ((byte) (1)), ((byte) (58)), ((byte) (0)), ((byte) (162)), ((byte) (2)), ((byte) (220)), ((byte) (1)), ((byte) (206)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (89)), ((byte) (1)), ((byte) (115)), ((byte) (17)), ((byte) (24)), ((byte) (39)), ((byte) (39)), ((byte) (244)), ((byte) (202)), ((byte) (12)), ((byte) (34)), ((byte) (29)), ((byte) (0)), ((byte) (0)), ((byte) (87)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("42.6", data.get("CollectorTemperatur"));
        Assert.assertEquals("11.4", data.get("OutsideTemperature"));
        Assert.assertEquals("31.4", data.get("FlowTemperature"));
        Assert.assertEquals("16.2", data.get("ReturnTemperature"));
        Assert.assertEquals("73.2", data.get("HotGasTemperature"));
        Assert.assertEquals("46.2", data.get("CylinderTemperature"));
        Assert.assertEquals("8.9", data.get("EvaporatorTemperature"));
        Assert.assertEquals("37.1", data.get("CondenserTemperature"));
        Assert.assertEquals("8.9", data.get("EvaporatorTemperature"));
        Assert.assertEquals("12", data.get("ExtractFanSpeed"));
        Assert.assertEquals("34", data.get("SupplyFanSpeed"));
        Assert.assertEquals("29", data.get("ExhaustFanSpeed"));
        Assert.assertEquals("8.7", data.get("FilteredOutsideTemperature"));
    }

    // request 17
    @Test
    public void testParseSettingsNominalValues() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsNominalValues";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (236)), ((byte) (23)), ((byte) (0)), ((byte) (165)), ((byte) (0)), ((byte) (157)), ((byte) (0)), ((byte) (100)), ((byte) (1)), ((byte) (194)), ((byte) (1)), ((byte) (224)), ((byte) (0)), ((byte) (100)), ((byte) (1)), ((byte) (1)), ((byte) (0)), ((byte) (1)), ((byte) (94)), ((byte) (1)), ((byte) (194)), ((byte) (2)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("16.5", data.get("P01RoomTemperatureStandardMode"));
        Assert.assertEquals("15.7", data.get("P02RoomTemperatureSetbackMode"));
        Assert.assertEquals("10.0", data.get("P03RoomTemperatureStandby"));
        Assert.assertEquals("45.0", data.get("P04DHWTemperatureStandardMode"));
        Assert.assertEquals("48.0", data.get("P05DHWTemperaturSetbackMode"));
        Assert.assertEquals("10.0", data.get("P06DHWTemperatureStandby"));
        Assert.assertEquals("1", data.get("P07FanStageStandardMode"));
        Assert.assertEquals("1", data.get("P08FanStageSetbackMode"));
        Assert.assertEquals("0", data.get("P09FanStageStandby"));
        Assert.assertEquals("35.0", data.get("P10HeatingCircuitTemperatureManualMode"));
        Assert.assertEquals("45.0", data.get("P11DHWTemperatureManualMode"));
        Assert.assertEquals("2", data.get("P12FanStageManualMode"));
    }

    // request 01
    @Test
    public void testParseSettingsVentilation() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsVentilation";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (47)), ((byte) (1)), ((byte) (0)), ((byte) (115)), ((byte) (0)), ((byte) (119)), ((byte) (0)), ((byte) (166)), ((byte) (0)), ((byte) (115)), ((byte) (0)), ((byte) (134)), ((byte) (0)), ((byte) (196)), ((byte) (0)), ((byte) (56)), ((byte) (0)), ((byte) (56)), ((byte) (0)), ((byte) (56)), ((byte) (0)), ((byte) (56)), ((byte) (0)), ((byte) (0)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("115", data.get("P37FanStageSupplyAir1"));
        Assert.assertEquals("119", data.get("P38FanStageSupplyAir2"));
        Assert.assertEquals("166", data.get("P39FanStageSupplyAir3"));
        Assert.assertEquals("115", data.get("P40FanStageExtractyAir1"));
        Assert.assertEquals("134", data.get("P41FanStageExtractyAir2"));
        Assert.assertEquals("196", data.get("P42FanStageExtractyAir3"));
        Assert.assertEquals("56", data.get("P43VentilationTimeUnscheduledStage3"));
        Assert.assertEquals("56", data.get("P44VentilationTimeUnscheduledStage2"));
        Assert.assertEquals("56", data.get("P45VentilationTimeUnscheduledStage1"));
        Assert.assertEquals("56", data.get("P46VentilationTimeUnscheduledStage0"));
        Assert.assertEquals("0", data.get("P75OperatingModePassiveCooling"));
        Assert.assertEquals("0", data.get("StoveFireplaceOperation"));
    }

    // request O6
    @Test
    public void testParseSettingsHeating1() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsHeating1";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (172)), ((byte) (6)), ((byte) (40)), ((byte) (30)), ((byte) (30)), ((byte) (20)), ((byte) (10)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (100)), ((byte) (3)), ((byte) (2)), ((byte) (238)), ((byte) (0)), ((byte) (200)), ((byte) (0)), ((byte) (10)), ((byte) (0)), ((byte) (1)), ((byte) (255)), ((byte) (206)), ((byte) (16)), ((byte) (16)), ((byte) (26)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("4.0", data.get("P21HysteresisHeating1"));
        Assert.assertEquals("3.0", data.get("P22HysteresisHeating2"));
        Assert.assertEquals("3.0", data.get("P23HysteresisHeating3"));
        Assert.assertEquals("2.0", data.get("P24HysteresisHeating4"));
        Assert.assertEquals("1.0", data.get("P25HysteresisHeating5"));
        Assert.assertEquals("0.0", data.get("P26HysteresisHeating6"));
        Assert.assertEquals("0.0", data.get("P27HysteresisHeating7"));
        Assert.assertEquals("0.0", data.get("P28HysteresisHeating8"));
        Assert.assertEquals("2", data.get("P29SwitchingHysteresisAsymmetry"));
        Assert.assertEquals("100", data.get("P30SwitchingValueIntegralPortionHeating"));
        Assert.assertEquals("3", data.get("P31AmountOfUnlockedElectricalBoosterStages"));
        Assert.assertEquals("75.0", data.get("MaximumFlowTemperatureHeatingMode"));
        Assert.assertEquals("20.0", data.get("P49ChangeoverTemperatureSummerWinter"));
        Assert.assertEquals("1.0", data.get("P50HysteresisChangeoverTemperatureSummerWinter"));
        Assert.assertEquals("1", data.get("P77OutsideTemperatureAdjustment"));
        Assert.assertEquals("-5.0", data.get("P78BivalencePoint"));
        Assert.assertEquals("16", data.get("P79DelayedEnableReheating"));
        Assert.assertEquals("2.6", data.get("OutputElectricalHeatingStage1"));
    }

    // request O5
    @Test
    public void testParseSettingsHeating2() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsHeating2";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (124)), ((byte) (5)), ((byte) (0)), ((byte) (4)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (5)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (30)), ((byte) (0)), ((byte) (100)), ((byte) (2)), ((byte) (38)), ((byte) (0)), ((byte) (50)), ((byte) (1)), ((byte) (94)), ((byte) (0)), ((byte) (50)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("0.4", data.get("P13IncreaseHeatingHC1"));
        Assert.assertEquals("0.0", data.get("P14LowEndPointHeatingHC1"));
        Assert.assertEquals("0.0", data.get("P15RoomInfluenceHeatingHC1"));
        Assert.assertEquals("0.5", data.get("P16IncreaseHeatingHC2"));
        Assert.assertEquals("0.0", data.get("P17LowEndPointHeatingHC2"));
        Assert.assertEquals("0.0", data.get("P18RoomInfluenceHeatingHC2"));
        Assert.assertEquals("30", data.get("P19TemperatureCaptureReturnFlowHC1"));
        Assert.assertEquals("100", data.get("P20TemperatureCaptureReturnFlowHC2"));
        Assert.assertEquals("55.0", data.get("MaxSetHeatingCircuitTemperatureHC1"));
        Assert.assertEquals("5.0", data.get("MinSetHeatingCircuitTemperatureHC1"));
        Assert.assertEquals("35.0", data.get("MaxSetHeatingCircuitTemperatureHC2"));
        Assert.assertEquals("5.0", data.get("MinSetHeatingCircuitTemperatureHC2"));
    }

    // request OC
    @Test
    public void testParseSettingsDomesticWater() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsDomesticHotWater";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (142)), ((byte) (7)), ((byte) (20)), ((byte) (90)), ((byte) (255)), ((byte) (156)), ((byte) (30)), ((byte) (7)), ((byte) (0)), ((byte) (100)), ((byte) (3)), ((byte) (2)), ((byte) (238)), ((byte) (1)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("2.0", data.get("P32StartupHysteresisDHWTemperature"));
        Assert.assertEquals("90", data.get("P33TimeDelayElectricalReheating"));
        Assert.assertEquals("-10.0", data.get("P34OutsideTemperatureLimitForImmElectricalReheating"));
        Assert.assertEquals("30", data.get("P35PasteurisationInterval"));
        Assert.assertEquals("7", data.get("P36MaxDurationDHWLoading"));
        Assert.assertEquals("10.0", data.get("PasteurisationHeatupTemperature"));
        Assert.assertEquals("3", data.get("NoOfEnabledElectricalReheatStagesDHWLoading"));
        Assert.assertEquals("75.0", data.get("MaxFlowTemperatureDHWMode"));
        Assert.assertEquals("1", data.get("CompressorShutdownDHWLoading"));
    }

    // request O3
    @Test
    public void testParseSettingsEvaporator1() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsEvaporator1";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (14)), ((byte) (3)), ((byte) (0)), ((byte) (150)), ((byte) (0)), ((byte) (10)), ((byte) (0)), ((byte) (150)), ((byte) (0)), ((byte) (100)), ((byte) (16)), ((byte) (16)), ((byte) (96)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("15.0", data.get("UpperLimitEvaporatorTemperatureForDefrostEnd"));
        Assert.assertEquals("10", data.get("MaxEvaporatorDefrostTime"));
        Assert.assertEquals("15.0", data.get("LimitTemperatureCondenserElectricalReheating"));
        Assert.assertEquals("10.0", data.get("LimitTemperatureCondenserDefrostTermination"));
        Assert.assertEquals("16", data.get("P47CompressorRestartDelay"));
        Assert.assertEquals("96", data.get("P48ExhaustFanSpeed"));
    }

    // request O4
    @Test
    public void testParseSettingsEvaporator2() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsEvaporator2";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (0)), ((byte) (4)), ((byte) (60)), ((byte) (0)), ((byte) (100)), ((byte) (0)), ((byte) (90)), ((byte) (1)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("60", data.get("MaxDefrostDurationAAExchanger"));
        Assert.assertEquals("10.0", data.get("DefrostStartThreshold"));
        Assert.assertEquals("90", data.get("VolumeFlowFilterReplacement"));
        Assert.assertEquals("1", data.get("P85DefrostModeAAHE"));
    }

    // request 10
    @Test
    public void testParseSettingsDryHeatingProgram() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsDryHeatingProgram";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (168)), ((byte) (16)), ((byte) (16)), ((byte) (0)), ((byte) (0)), ((byte) (250)), ((byte) (1)), ((byte) (144)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (10)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("0", data.get("P70Start"));
        Assert.assertEquals("25.0", data.get("P71BaseTemperature"));
        Assert.assertEquals("40.0", data.get("P72PeakTemperature"));
        Assert.assertEquals("2", data.get("P73BaseTemperatureDuration"));
        Assert.assertEquals("1.0", data.get("P74Increase"));
    }

    // request 10
    @Test
    public void testAddDuplicatesInRequest1() throws StiebelHeatPumpException {
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (16)), ((byte) (15)), ((byte) (16)), ((byte) (3)) };
        byte[] newResponse = parser.addDuplicatedBytes(response);
        byte[] resultingBytes = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (16)), ((byte) (16)), ((byte) (15)), ((byte) (16)), ((byte) (3)) };
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        for (int i = 0; i < (newResponse.length); i++) {
            Assert.assertEquals(resultingBytes[i], newResponse[i]);
        }
    }

    // request 10
    @Test
    public void testAddDuplicatesInRequest2() throws StiebelHeatPumpException {
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (17)), ((byte) (16)), ((byte) (16)), ((byte) (3)) };
        byte[] newResponse = parser.addDuplicatedBytes(response);
        byte[] resultingBytes = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (17)), ((byte) (16)), ((byte) (16)), ((byte) (16)), ((byte) (3)) };
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        for (int i = 0; i < (newResponse.length); i++) {
            Assert.assertEquals(resultingBytes[i], newResponse[i]);
        }
    }

    // request OA
    @Test
    public void testParseSettingsCirculationPump() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsCirculationPump";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (235)), ((byte) (10)), ((byte) (1)), ((byte) (1)), ((byte) (32)), ((byte) (0)), ((byte) (186)), ((byte) (0)), ((byte) (200)), ((byte) (0)), ((byte) (60)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("1", data.get("P54minStartupCycles"));
        Assert.assertEquals("288", data.get("P55maxStartupCycles"));
        Assert.assertEquals("18.6", data.get("P56OutsideTemperatureMinHeatingCycles"));
        Assert.assertEquals("20.0", data.get("P57OutsideTemperatureMaxHeatingCycles"));
        Assert.assertEquals("60", data.get("P58SuppressTemperatureCaptureDuringPumpStart"));
    }

    // request OB
    @Test
    public void testParseSettingsHeatingProgram() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsHeatingProgram";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (57)), ((byte) (11)), ((byte) (8)), ((byte) (152)), ((byte) (2)), ((byte) (88)), ((byte) (127)), ((byte) (0)), ((byte) (3)), ((byte) (232)), ((byte) (6)), ((byte) (164)), ((byte) (31)), ((byte) (0)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("2200", data.get("HP1StartTime"));
        Assert.assertEquals("600", data.get("HP1StopTime"));
        Assert.assertEquals("1", data.get("HP1Monday"));
        Assert.assertEquals("1", data.get("HP1Tuesday"));
        Assert.assertEquals("1", data.get("HP1Wednesday"));
        Assert.assertEquals("1", data.get("HP1Thusday"));
        Assert.assertEquals("1", data.get("HP1Friday"));
        Assert.assertEquals("1", data.get("HP1Saturday"));
        Assert.assertEquals("1", data.get("HP1Sunday"));
        Assert.assertEquals("0", data.get("HP1Enabled"));
        Assert.assertEquals("1000", data.get("HP2StartTime"));
        Assert.assertEquals("1700", data.get("HP2StopTime"));
        Assert.assertEquals("1", data.get("HP2Monday"));
        Assert.assertEquals("1", data.get("HP2Tuesday"));
        Assert.assertEquals("1", data.get("HP2Wednesday"));
        Assert.assertEquals("1", data.get("HP2Thusday"));
        Assert.assertEquals("1", data.get("HP2Friday"));
        Assert.assertEquals("0", data.get("HP2Saturday"));
        Assert.assertEquals("0", data.get("HP2Sunday"));
        Assert.assertEquals("0", data.get("HP2Enabled"));
    }

    // request OC
    @Test
    public void testParseSettingsDomesticWaterProgram() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsDomesticHotWaterProgram";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (29)), ((byte) (12)), ((byte) (8)), ((byte) (152)), ((byte) (1)), ((byte) (244)), ((byte) (123)), ((byte) (0)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("2200", data.get("BP1StartTime"));
        Assert.assertEquals("500", data.get("BP1StopTime"));
        Assert.assertEquals("1", data.get("BP1Monday"));
        Assert.assertEquals("1", data.get("BP1Tuesday"));
        Assert.assertEquals("0", data.get("BP1Wednesday"));
        Assert.assertEquals("1", data.get("BP1Thusday"));
        Assert.assertEquals("1", data.get("BP1Friday"));
        Assert.assertEquals("1", data.get("BP1Saturday"));
        Assert.assertEquals("1", data.get("BP1Sunday"));
        Assert.assertEquals("0", data.get("BP1Enabled"));
    }

    // request OD
    @Test
    public void testParseSettingsVentilationProgram() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsVentilationProgram";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (51)), ((byte) (13)), ((byte) (8)), ((byte) (152)), ((byte) (2)), ((byte) (88)), ((byte) (123)), ((byte) (0)), ((byte) (3)), ((byte) (232)), ((byte) (6)), ((byte) (164)), ((byte) (27)), ((byte) (0)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("2200", data.get("LP1StartTime"));
        Assert.assertEquals("600", data.get("LP1StopTime"));
        Assert.assertEquals("1", data.get("LP1Monday"));
        Assert.assertEquals("1", data.get("LP1Tuesday"));
        Assert.assertEquals("0", data.get("LP1Wednesday"));
        Assert.assertEquals("1", data.get("LP1Thusday"));
        Assert.assertEquals("1", data.get("LP1Friday"));
        Assert.assertEquals("1", data.get("LP1Saturday"));
        Assert.assertEquals("1", data.get("LP1Sunday"));
        Assert.assertEquals("0", data.get("LP1Enabled"));
        Assert.assertEquals("1000", data.get("LP2StartTime"));
        Assert.assertEquals("1700", data.get("LP2StopTime"));
        Assert.assertEquals("1", data.get("LP2Monday"));
        Assert.assertEquals("1", data.get("LP2Tuesday"));
        Assert.assertEquals("0", data.get("LP2Wednesday"));
        Assert.assertEquals("1", data.get("LP2Thusday"));
        Assert.assertEquals("1", data.get("LP2Friday"));
        Assert.assertEquals("0", data.get("LP2Saturday"));
        Assert.assertEquals("0", data.get("LP2Sunday"));
        Assert.assertEquals("0", data.get("LP2Enabled"));
    }

    // request OD
    @Test
    public void testParseSettingsAbsenceProgram() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsAbsenceProgram";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (16)), ((byte) (16)), ((byte) (15)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("0.0", data.get("AP0DurationUntilAbsenceStart"));
        Assert.assertEquals("0.0", data.get("AP0AbsenceDuration"));
        Assert.assertEquals("0", data.get("AP0EnableAbsenceProgram"));
    }

    // request 0E
    @Test
    public void testParseSettingsRestartAndMixerTime() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "SettingsRestartAndMixerTime";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (9)), ((byte) (14)), ((byte) (0)), ((byte) (120)), ((byte) (0)), ((byte) (100)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (30)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("120", data.get("P59RestartBeforSetbackEnd"));
        Assert.assertEquals("10.0", data.get("MixerProportionalRange"));
        Assert.assertEquals("0.0", data.get("DerivativeMixerTime"));
        Assert.assertEquals("30", data.get("MixerTimeInterval"));
    }

    // write new value for short byte value use case
    @Test
    public void testWriteTime() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return (r.getName()) == "Time";
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (121)), ((byte) (252)), ((byte) (0)), ((byte) (2)), ((byte) (10)), ((byte) (33)), ((byte) (36)), ((byte) (14)), ((byte) (0)), ((byte) (3)), ((byte) (26)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("2", data.get("WeekDay"));
        Assert.assertEquals("10", data.get("Hours"));
        Assert.assertEquals("33", data.get("Minutes"));
        Assert.assertEquals("36", data.get("Seconds"));
        Assert.assertEquals("14", data.get("Year"));
        Assert.assertEquals("3", data.get("Month"));
        Assert.assertEquals("26", data.get("Day"));
        byte[] resultingBytes = new byte[]{ ((byte) (1)), ((byte) (128)), ((byte) (241)), ((byte) (252)), ((byte) (0)), ((byte) (2)), ((byte) (10)), ((byte) (34)), ((byte) (27)), ((byte) (14)), ((byte) (0)), ((byte) (3)), ((byte) (26)), ((byte) (16)), ((byte) (3)) };
        Request request = result.get(0);
        RecordDefinition recordDefinition = null;
        for (RecordDefinition record : request.getRecordDefinitions()) {
            if ((record.getName()) == "Minutes") {
                recordDefinition = record;
                break;
            }
        }
        byte[] newResponse = parser.composeRecord("34", response, recordDefinition);
        for (RecordDefinition record : request.getRecordDefinitions()) {
            if ((record.getName()) == "Seconds") {
                recordDefinition = record;
                break;
            }
        }
        Throwable e = null;
        try {
            newResponse = parser.composeRecord("90", newResponse, recordDefinition);
        } catch (Throwable ex) {
            e = ex;
        }
        Assert.assertTrue((e instanceof StiebelHeatPumpException));
        newResponse = parser.composeRecord("27", newResponse, recordDefinition);
        // update the checksum
        newResponse[2] = parser.calculateChecksum(newResponse);
        for (int i = 0; i < (newResponse.length); i++) {
            Assert.assertEquals(resultingBytes[i], newResponse[i]);
        }
    }

    // write new value for P04DHWTemperatureStandardMode use case
    @Test
    public void testwriteP04DHWTemperatureStandardMode() throws StiebelHeatPumpException {
        List<Request> result = Requests.searchIn(configuration, new org.openhab.binding.stiebelheatpump.protocol.Requests.Matcher<Request>() {
            @Override
            public boolean matches(Request r) {
                return r.getName().equals("SettingsNominalValues");
            }
        });
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (240)), ((byte) (23)), ((byte) (0)), ((byte) (162)), ((byte) (0)), ((byte) (165)), ((byte) (0)), ((byte) (100)), ((byte) (1)), ((byte) (194)), ((byte) (1)), ((byte) (224)), ((byte) (0)), ((byte) (100)), ((byte) (1)), ((byte) (1)), ((byte) (0)), ((byte) (1)), ((byte) (94)), ((byte) (1)), ((byte) (194)), ((byte) (1)), ((byte) (16)), ((byte) (3)) };
        response = parser.fixDuplicatedBytes(response);
        Assert.assertEquals(response[3], result.get(0).getRequestByte());
        Assert.assertEquals(response[2], parser.calculateChecksum(response));
        Map<String, String> data = parser.parseRecords(response, result.get(0));
        Assert.assertEquals("45.0", data.get("P04DHWTemperatureStandardMode"));
        Request request = result.get(0);
        RecordDefinition recordDefinition = null;
        for (RecordDefinition record : request.getRecordDefinitions()) {
            if (record.getName().equals("P04DHWTemperatureStandardMode")) {
                recordDefinition = record;
                break;
            }
        }
        byte[] newResponse = parser.composeRecord("45.5", response, recordDefinition);
        // update the checksum
        newResponse[2] = parser.calculateChecksum(newResponse);
        data = parser.parseRecords(newResponse, request);
        Assert.assertEquals("45.5", data.get("P04DHWTemperatureStandardMode"));
    }

    // utility protocol tests
    @Test
    public void testParseFindAndReplace() throws StiebelHeatPumpException {
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (172)), ((byte) (6)), ((byte) (40)), ((byte) (30)), ((byte) (30)), ((byte) (20)), ((byte) (10)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (100)), ((byte) (3)), ((byte) (2)), ((byte) (238)), ((byte) (0)), ((byte) (200)), ((byte) (0)), ((byte) (10)), ((byte) (0)), ((byte) (1)), ((byte) (255)), ((byte) (206)), ((byte) (16)), ((byte) (16)), ((byte) (26)), ((byte) (16)), ((byte) (3)) };
        int originalLength = response.length;
        response = parser.findReplace(response, new byte[]{ ((byte) (16)), ((byte) (16)) }, new byte[]{ ((byte) (16)) });
        response = parser.findReplace(response, new byte[]{ ((byte) (43)), ((byte) (24)) }, new byte[]{ ((byte) (43)) });
        Assert.assertEquals(1, (originalLength - (response.length)));
    }

    @Test
    public void testDataAvailable() {
        try {
            Assert.assertTrue(parser.dataAvailable(new byte[]{ 16, 2 }));
        } catch (Exception e) {
            Assert.fail("unexpected exception");
        }
        try {
            parser.dataAvailable(new byte[]{  });
            Assert.fail("expected exception");
        } catch (Exception e) {
        }
    }

    @Test
    public void testHeader() {
        try {
            Assert.assertFalse(parser.headerCheck(new byte[]{ ((byte) (1)), ((byte) (1)), ((byte) (254)), ((byte) (252)), ((byte) (16)), ((byte) (3)) }));
            Assert.assertTrue(parser.headerCheck(new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (204)), ((byte) (253)), ((byte) (0)), ((byte) (206)), ((byte) (16)), ((byte) (3)) }));
            Assert.assertFalse(parser.headerCheck(new byte[]{ ((byte) (1)), ((byte) (3)), ((byte) (128)), ((byte) (124)), ((byte) (16)), ((byte) (3)) }));
        } catch (Exception e) {
            Assert.fail("unexpected exception");
        }
    }

    @Test
    public void testDataSet() {
        try {
            Assert.assertFalse(parser.setDataCheck(new byte[]{ ((byte) (1)), ((byte) (128)), ((byte) (254)), ((byte) (252)), ((byte) (16)), ((byte) (3)) }));
            Assert.assertTrue(parser.setDataCheck(new byte[]{ ((byte) (1)), ((byte) (128)), ((byte) (125)), ((byte) (252)), ((byte) (16)), ((byte) (3)) }));
        } catch (Exception e) {
            Assert.fail("unexpected exception");
        }
    }

    @Test
    public void testParseByteToHex() throws StiebelHeatPumpException {
        byte[] response = new byte[]{ ((byte) (1)), ((byte) (0)), ((byte) (181)), ((byte) (253)), ((byte) (1)), ((byte) (182)), ((byte) (255)), ((byte) (16)), ((byte) (3)) };
        String hex = DataParser.bytesToHex(response);
        Assert.assertEquals("(00)01 00 B5 FD (04)01 B6 FF 10 (08)03 ", hex);
    }
}

