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
package org.openhab.binding.avmfritz.internal.ahamodel;


import SwitchModel.OFF;
import SwitchModel.ON;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for {@link DeviceListModel}.
 *
 * @author Christoph Weitkamp - Initial contribution
 */
public class AVMFritzModelTest {
    private final Logger logger = LoggerFactory.getLogger(AVMFritzModelTest.class);

    private DeviceListModel devices;

    @Test
    public void validateDeviceListModel() {
        Assert.assertNotNull(devices);
        Assert.assertEquals(11, devices.getDevicelist().size());
        Assert.assertEquals("1", devices.getXmlApiVersion());
    }

    @Test
    public void validateDECTRepeater100Model() {
        Optional<AVMFritzBaseModel> optionalDevice = findModelByIdentifier("087610000439");
        Assert.assertTrue(optionalDevice.isPresent());
        Assert.assertTrue(((optionalDevice.get()) instanceof DeviceModel));
        DeviceModel device = ((DeviceModel) (optionalDevice.get()));
        Assert.assertEquals("FRITZ!DECT Repeater 100", device.getProductName());
        Assert.assertEquals("087610000439", device.getIdentifier());
        Assert.assertEquals("40", device.getDeviceId());
        Assert.assertEquals("03.86", device.getFirmwareVersion());
        Assert.assertEquals("AVM", device.getManufacturer());
        Assert.assertEquals(1, device.getPresent());
        Assert.assertEquals("FRITZ!DECT Repeater 100 #5", device.getName());
        Assert.assertFalse(device.isButton());
        Assert.assertFalse(device.isAlarmSensor());
        Assert.assertTrue(device.isDectRepeater());
        Assert.assertFalse(device.isSwitchableOutlet());
        Assert.assertTrue(device.isTempSensor());
        Assert.assertFalse(device.isPowermeter());
        Assert.assertFalse(device.isHeatingThermostat());
        Assert.assertNull(device.getSwitch());
        Assert.assertNotNull(device.getTemperature());
        Assert.assertEquals(new BigDecimal("23.0"), device.getTemperature().getCelsius());
        Assert.assertEquals(new BigDecimal("0.0"), device.getTemperature().getOffset());
        Assert.assertNull(device.getPowermeter());
        Assert.assertNull(device.getHkr());
    }

    @Test
    public void validateDECT200Model() {
        Optional<AVMFritzBaseModel> optionalDevice = findModel("FRITZ!DECT 200");
        Assert.assertTrue(optionalDevice.isPresent());
        Assert.assertTrue(((optionalDevice.get()) instanceof DeviceModel));
        DeviceModel device = ((DeviceModel) (optionalDevice.get()));
        Assert.assertEquals("FRITZ!DECT 200", device.getProductName());
        Assert.assertEquals("087610000434", device.getIdentifier());
        Assert.assertEquals("17", device.getDeviceId());
        Assert.assertEquals("03.83", device.getFirmwareVersion());
        Assert.assertEquals("AVM", device.getManufacturer());
        Assert.assertEquals(1, device.getPresent());
        Assert.assertEquals("FRITZ!DECT 200 #1", device.getName());
        Assert.assertFalse(device.isButton());
        Assert.assertFalse(device.isAlarmSensor());
        Assert.assertFalse(device.isDectRepeater());
        Assert.assertTrue(device.isSwitchableOutlet());
        Assert.assertTrue(device.isTempSensor());
        Assert.assertTrue(device.isPowermeter());
        Assert.assertFalse(device.isHeatingThermostat());
        Assert.assertNotNull(device.getSwitch());
        Assert.assertEquals(ON, device.getSwitch().getState());
        Assert.assertEquals(MODE_MANUAL, device.getSwitch().getMode());
        Assert.assertEquals(BigDecimal.ZERO, device.getSwitch().getLock());
        Assert.assertEquals(BigDecimal.ZERO, device.getSwitch().getDevicelock());
        Assert.assertNotNull(device.getTemperature());
        Assert.assertEquals(new BigDecimal("25.5"), device.getTemperature().getCelsius());
        Assert.assertEquals(new BigDecimal("0.0"), device.getTemperature().getOffset());
        validatePowerMeter(device.getPowermeter());
        Assert.assertNull(device.getHkr());
    }

    @Test
    public void validateDECT210Model() {
        Optional<AVMFritzBaseModel> optionalDevice = findModel("FRITZ!DECT 210");
        Assert.assertTrue(optionalDevice.isPresent());
        Assert.assertTrue(((optionalDevice.get()) instanceof DeviceModel));
        DeviceModel device = ((DeviceModel) (optionalDevice.get()));
        Assert.assertEquals("FRITZ!DECT 210", device.getProductName());
        Assert.assertEquals("087610000438", device.getIdentifier());
        Assert.assertEquals("18", device.getDeviceId());
        Assert.assertEquals("03.83", device.getFirmwareVersion());
        Assert.assertEquals("AVM", device.getManufacturer());
        Assert.assertEquals(1, device.getPresent());
        Assert.assertEquals("FRITZ!DECT 210 #8", device.getName());
        Assert.assertFalse(device.isButton());
        Assert.assertFalse(device.isAlarmSensor());
        Assert.assertFalse(device.isDectRepeater());
        Assert.assertTrue(device.isSwitchableOutlet());
        Assert.assertTrue(device.isTempSensor());
        Assert.assertTrue(device.isPowermeter());
        Assert.assertFalse(device.isHeatingThermostat());
        Assert.assertNotNull(device.getSwitch());
        Assert.assertEquals(ON, device.getSwitch().getState());
        Assert.assertEquals(MODE_MANUAL, device.getSwitch().getMode());
        Assert.assertEquals(BigDecimal.ZERO, device.getSwitch().getLock());
        Assert.assertEquals(BigDecimal.ZERO, device.getSwitch().getDevicelock());
        Assert.assertNotNull(device.getTemperature());
        Assert.assertEquals(new BigDecimal("25.5"), device.getTemperature().getCelsius());
        Assert.assertEquals(new BigDecimal("0.0"), device.getTemperature().getOffset());
        validatePowerMeter(device.getPowermeter());
        Assert.assertNull(device.getHkr());
    }

    @Test
    public void validateDECT300Model() {
        Optional<AVMFritzBaseModel> optionalDevice = findModel("FRITZ!DECT 300");
        Assert.assertTrue(optionalDevice.isPresent());
        Assert.assertTrue(((optionalDevice.get()) instanceof DeviceModel));
        DeviceModel device = ((DeviceModel) (optionalDevice.get()));
        Assert.assertEquals("FRITZ!DECT 300", device.getProductName());
        Assert.assertEquals("087610000437", device.getIdentifier());
        Assert.assertEquals("20", device.getDeviceId());
        Assert.assertEquals("03.50", device.getFirmwareVersion());
        Assert.assertEquals("AVM", device.getManufacturer());
        Assert.assertEquals(0, device.getPresent());
        Assert.assertEquals("FRITZ!DECT 300 #1", device.getName());
        Assert.assertFalse(device.isButton());
        Assert.assertFalse(device.isAlarmSensor());
        Assert.assertFalse(device.isDectRepeater());
        Assert.assertFalse(device.isSwitchableOutlet());
        Assert.assertTrue(device.isTempSensor());
        Assert.assertFalse(device.isPowermeter());
        Assert.assertTrue(device.isHeatingThermostat());
        Assert.assertNull(device.getSwitch());
        Assert.assertNotNull(device.getTemperature());
        Assert.assertEquals(new BigDecimal("22.0"), device.getTemperature().getCelsius());
        Assert.assertEquals(new BigDecimal("-1.0"), device.getTemperature().getOffset());
        Assert.assertNull(device.getPowermeter());
        validateHeatingModel(device.getHkr());
    }

    @Test
    public void validateDECT301Model() {
        Optional<AVMFritzBaseModel> optionalDevice = findModel("FRITZ!DECT 301");
        Assert.assertTrue(optionalDevice.isPresent());
        Assert.assertTrue(((optionalDevice.get()) instanceof DeviceModel));
        DeviceModel device = ((DeviceModel) (optionalDevice.get()));
        Assert.assertEquals("FRITZ!DECT 301", device.getProductName());
        Assert.assertEquals("087610000436", device.getIdentifier());
        Assert.assertEquals("21", device.getDeviceId());
        Assert.assertEquals("03.50", device.getFirmwareVersion());
        Assert.assertEquals("AVM", device.getManufacturer());
        Assert.assertEquals(0, device.getPresent());
        Assert.assertEquals("FRITZ!DECT 301 #1", device.getName());
        Assert.assertFalse(device.isButton());
        Assert.assertFalse(device.isAlarmSensor());
        Assert.assertFalse(device.isDectRepeater());
        Assert.assertFalse(device.isSwitchableOutlet());
        Assert.assertTrue(device.isTempSensor());
        Assert.assertFalse(device.isPowermeter());
        Assert.assertTrue(device.isHeatingThermostat());
        Assert.assertNull(device.getSwitch());
        Assert.assertNotNull(device.getTemperature());
        Assert.assertEquals(new BigDecimal("22.0"), device.getTemperature().getCelsius());
        Assert.assertEquals(new BigDecimal("-1.0"), device.getTemperature().getOffset());
        Assert.assertNull(device.getPowermeter());
        validateHeatingModel(device.getHkr());
    }

    @Test
    public void validateCometDECTModel() {
        Optional<AVMFritzBaseModel> optionalDevice = findModel("Comet DECT");
        Assert.assertTrue(optionalDevice.isPresent());
        Assert.assertTrue(((optionalDevice.get()) instanceof DeviceModel));
        DeviceModel device = ((DeviceModel) (optionalDevice.get()));
        Assert.assertEquals("Comet DECT", device.getProductName());
        Assert.assertEquals("087610000435", device.getIdentifier());
        Assert.assertEquals("22", device.getDeviceId());
        Assert.assertEquals("03.50", device.getFirmwareVersion());
        Assert.assertEquals("AVM", device.getManufacturer());
        Assert.assertEquals(0, device.getPresent());
        Assert.assertEquals("Comet DECT #1", device.getName());
        Assert.assertFalse(device.isButton());
        Assert.assertFalse(device.isAlarmSensor());
        Assert.assertFalse(device.isDectRepeater());
        Assert.assertFalse(device.isSwitchableOutlet());
        Assert.assertTrue(device.isTempSensor());
        Assert.assertFalse(device.isPowermeter());
        Assert.assertTrue(device.isHeatingThermostat());
        Assert.assertNull(device.getSwitch());
        Assert.assertNotNull(device.getTemperature());
        Assert.assertEquals(new BigDecimal("22.0"), device.getTemperature().getCelsius());
        Assert.assertEquals(new BigDecimal("-1.0"), device.getTemperature().getOffset());
        Assert.assertNull(device.getPowermeter());
        validateHeatingModel(device.getHkr());
    }

    @Test
    public void validatePowerline546EModel() {
        Optional<AVMFritzBaseModel> optionalDevice = findModel("FRITZ!Powerline 546E");
        Assert.assertTrue(optionalDevice.isPresent());
        Assert.assertTrue(((optionalDevice.get()) instanceof DeviceModel));
        DeviceModel device = ((DeviceModel) (optionalDevice.get()));
        Assert.assertEquals("FRITZ!Powerline 546E", device.getProductName());
        Assert.assertEquals("5C:49:79:F0:A3:84", device.getIdentifier());
        Assert.assertEquals("30", device.getDeviceId());
        Assert.assertEquals("06.92", device.getFirmwareVersion());
        Assert.assertEquals("AVM", device.getManufacturer());
        Assert.assertEquals(1, device.getPresent());
        Assert.assertEquals("FRITZ!Powerline 546E #1", device.getName());
        Assert.assertFalse(device.isButton());
        Assert.assertFalse(device.isAlarmSensor());
        Assert.assertFalse(device.isDectRepeater());
        Assert.assertTrue(device.isSwitchableOutlet());
        Assert.assertFalse(device.isTempSensor());
        Assert.assertTrue(device.isPowermeter());
        Assert.assertFalse(device.isHeatingThermostat());
        Assert.assertNotNull(device.getSwitch());
        Assert.assertEquals(OFF, device.getSwitch().getState());
        Assert.assertEquals(MODE_MANUAL, device.getSwitch().getMode());
        Assert.assertEquals(BigDecimal.ZERO, device.getSwitch().getLock());
        Assert.assertEquals(BigDecimal.ONE, device.getSwitch().getDevicelock());
        Assert.assertNull(device.getTemperature());
        validatePowerMeter(device.getPowermeter());
        Assert.assertNull(device.getHkr());
    }

    @Test
    public void validateHANFUNContactModel() {
        Optional<AVMFritzBaseModel> optionalDevice = findModelByIdentifier("119340059978-1");
        Assert.assertTrue(optionalDevice.isPresent());
        Assert.assertTrue(((optionalDevice.get()) instanceof DeviceModel));
        DeviceModel device = ((DeviceModel) (optionalDevice.get()));
        Assert.assertEquals("HAN-FUN", device.getProductName());
        Assert.assertEquals("119340059978-1", device.getIdentifier());
        Assert.assertEquals("2000", device.getDeviceId());
        Assert.assertEquals("0.0", device.getFirmwareVersion());
        Assert.assertEquals("0x0feb", device.getManufacturer());
        Assert.assertEquals(0, device.getPresent());
        Assert.assertEquals("HAN-FUN #2: Unit #2", device.getName());
        Assert.assertFalse(device.isButton());
        Assert.assertTrue(device.isAlarmSensor());
        Assert.assertFalse(device.isDectRepeater());
        Assert.assertFalse(device.isSwitchableOutlet());
        Assert.assertFalse(device.isTempSensor());
        Assert.assertFalse(device.isPowermeter());
        Assert.assertFalse(device.isHeatingThermostat());
        Assert.assertNull(device.getButton());
        Assert.assertNotNull(device.getAlert());
        Assert.assertEquals(BigDecimal.ONE, device.getAlert().getState());
        Assert.assertNull(device.getSwitch());
        Assert.assertNull(device.getTemperature());
        Assert.assertNull(device.getPowermeter());
        Assert.assertNull(device.getHkr());
    }

    @Test
    public void validateHANFUNSwitchModel() {
        Optional<AVMFritzBaseModel> optionalDevice = findModelByIdentifier("119340059979-1");
        Assert.assertTrue(optionalDevice.isPresent());
        Assert.assertTrue(((optionalDevice.get()) instanceof DeviceModel));
        DeviceModel device = ((DeviceModel) (optionalDevice.get()));
        Assert.assertEquals("HAN-FUN", device.getProductName());
        Assert.assertEquals("119340059979-1", device.getIdentifier());
        Assert.assertEquals("2001", device.getDeviceId());
        Assert.assertEquals("0.0", device.getFirmwareVersion());
        Assert.assertEquals("0x0feb", device.getManufacturer());
        Assert.assertEquals(0, device.getPresent());
        Assert.assertEquals("HAN-FUN #2: Unit #2", device.getName());
        Assert.assertTrue(device.isButton());
        Assert.assertFalse(device.isAlarmSensor());
        Assert.assertFalse(device.isDectRepeater());
        Assert.assertFalse(device.isSwitchableOutlet());
        Assert.assertFalse(device.isTempSensor());
        Assert.assertFalse(device.isPowermeter());
        Assert.assertFalse(device.isHeatingThermostat());
        Assert.assertNotNull(device.getButton());
        Assert.assertEquals(1529590797, device.getButton().getLastpressedtimestamp());
        Assert.assertNull(device.getAlert());
        Assert.assertNull(device.getSwitch());
        Assert.assertNull(device.getTemperature());
        Assert.assertNull(device.getPowermeter());
        Assert.assertNull(device.getHkr());
    }

    @Test
    public void validateHeatingGroupModel() {
        Optional<AVMFritzBaseModel> optionalGroup = findModelByIdentifier("F0:A3:7F-901");
        Assert.assertTrue(optionalGroup.isPresent());
        Assert.assertTrue(((optionalGroup.get()) instanceof GroupModel));
        GroupModel group = ((GroupModel) (optionalGroup.get()));
        Assert.assertEquals("", group.getProductName());
        Assert.assertEquals("F0:A3:7F-901", group.getIdentifier());
        Assert.assertEquals("20001", group.getDeviceId());
        Assert.assertEquals("1.0", group.getFirmwareVersion());
        Assert.assertEquals("AVM", group.getManufacturer());
        Assert.assertEquals(1, group.getPresent());
        Assert.assertEquals("Schlafzimmer", group.getName());
        Assert.assertFalse(group.isButton());
        Assert.assertFalse(group.isAlarmSensor());
        Assert.assertFalse(group.isDectRepeater());
        Assert.assertFalse(group.isSwitchableOutlet());
        Assert.assertFalse(group.isTempSensor());
        Assert.assertFalse(group.isPowermeter());
        Assert.assertTrue(group.isHeatingThermostat());
        Assert.assertNull(group.getSwitch());
        Assert.assertNull(group.getPowermeter());
        validateHeatingModel(group.getHkr());
        Assert.assertNotNull(group.getGroupinfo());
        Assert.assertEquals("0", group.getGroupinfo().getMasterdeviceid());
        Assert.assertEquals("20,21,22", group.getGroupinfo().getMembers());
    }

    @Test
    public void validateSwitchGroupModel() {
        Optional<AVMFritzBaseModel> optionalGroup = findModelByIdentifier("F0:A3:7F-900");
        Assert.assertTrue(optionalGroup.isPresent());
        Assert.assertTrue(((optionalGroup.get()) instanceof GroupModel));
        GroupModel group = ((GroupModel) (optionalGroup.get()));
        Assert.assertEquals("", group.getProductName());
        Assert.assertEquals("F0:A3:7F-900", group.getIdentifier());
        Assert.assertEquals("20000", group.getDeviceId());
        Assert.assertEquals("1.0", group.getFirmwareVersion());
        Assert.assertEquals("AVM", group.getManufacturer());
        Assert.assertEquals(1, group.getPresent());
        Assert.assertEquals("Schlafzimmer", group.getName());
        Assert.assertFalse(group.isButton());
        Assert.assertFalse(group.isAlarmSensor());
        Assert.assertFalse(group.isDectRepeater());
        Assert.assertTrue(group.isSwitchableOutlet());
        Assert.assertFalse(group.isTempSensor());
        Assert.assertTrue(group.isPowermeter());
        Assert.assertFalse(group.isHeatingThermostat());
        Assert.assertNotNull(group.getSwitch());
        Assert.assertEquals(ON, group.getSwitch().getState());
        Assert.assertEquals(MODE_MANUAL, group.getSwitch().getMode());
        Assert.assertEquals(BigDecimal.ZERO, group.getSwitch().getLock());
        Assert.assertEquals(BigDecimal.ZERO, group.getSwitch().getDevicelock());
        validatePowerMeter(group.getPowermeter());
        Assert.assertNull(group.getHkr());
        Assert.assertNotNull(group.getGroupinfo());
        Assert.assertEquals("17", group.getGroupinfo().getMasterdeviceid());
        Assert.assertEquals("17,18", group.getGroupinfo().getMembers());
    }
}

