/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.dmx.internal.config;


import OnOffType.OFF;
import OnOffType.ON;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.openhab.binding.dmx.DmxService;
import org.openhab.binding.dmx.internal.cmd.DmxCommand;
import org.openhab.binding.dmx.internal.cmd.DmxFadeCommand;
import org.openhab.binding.dmx.internal.cmd.DmxSuspendingFadeCommand;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * DmxSwitchItem configuration tests.
 *
 * @author Davy Vanherbergen
 * @since 1.2.0
 */
public class DmxSwitchItemTest {
    @Test
    public void canHaveSingleChannelConfiguration() throws BindingConfigParseException {
        // test valid configurations
        DmxItem item = getItemInstance("CHANNEL[7:1000]");
        Assert.assertEquals(7, item.getChannel());
        Assert.assertEquals(1000, item.getUpdateDelay());
        if (item instanceof DmxColorItem) {
            Assert.assertEquals(3, item.getChannels().length);
        } else {
            Assert.assertEquals(1, item.getChannels().length);
        }
        item = getItemInstance("CHANNEL[1]");
        Assert.assertEquals(1, item.getChannel());
        Assert.assertEquals(0, item.getUpdateDelay());
        if (item instanceof DmxColorItem) {
            Assert.assertEquals(3, item.getChannels().length);
        } else {
            Assert.assertEquals(1, item.getChannels().length);
        }
        // test invalid configurations
        try {
            item = getItemInstance("CHANNEL[71000]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[A1-00]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[a:B]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void canHaveMultiChannelConfigurations() throws BindingConfigParseException {
        // test valid configurations
        DmxItem item = getItemInstance("CHANNEL[1,2,3,4,5,6:500]");
        Assert.assertTrue(arraysAreEqual(new int[]{ 1, 2, 3, 4, 5, 6 }, item.getChannels()));
        Assert.assertEquals(500, item.getUpdateDelay());
        Assert.assertEquals(6, item.getChannels().length);
        item = getItemInstance("CHANNEL[1,2,3,4,5]");
        Assert.assertTrue(arraysAreEqual(new int[]{ 1, 2, 3, 4, 5 }, item.getChannels()));
        Assert.assertEquals(0, item.getUpdateDelay());
        Assert.assertEquals(5, item.getChannels().length);
        // test invalid configurations
        try {
            item = getItemInstance("CHANNEL[71000,2]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[A1,00:1]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[1,2,3,4:A]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void canHaveShortHandMultiChannelConfigurations() throws BindingConfigParseException {
        // test valid configurations
        DmxItem item = getItemInstance("CHANNEL[4/3:250]");
        Assert.assertTrue(arraysAreEqual(new int[]{ 4, 5, 6 }, item.getChannels()));
        Assert.assertEquals(250, item.getUpdateDelay());
        Assert.assertEquals(3, item.getChannels().length);
        item = getItemInstance("CHANNEL[4/6:125]");
        Assert.assertTrue(arraysAreEqual(new int[]{ 4, 5, 6, 7, 8, 9 }, item.getChannels()));
        Assert.assertEquals(125, item.getUpdateDelay());
        Assert.assertEquals(6, item.getChannels().length);
        // test invalid configurations
        try {
            item = getItemInstance("CHANNEL[71000/5,2]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[5,6/300:100]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[5,6/300]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[5,6/A]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void failsOnMissingChannelConfig() {
        try {
            getItemInstance("KNX[7:1000]");
            Assert.fail("No exception got thrown..");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Wrong exception thrown");
        }
    }

    @Test
    public void switchesOffWhenOffCommandReceived() throws BindingConfigParseException {
        DmxItem item = getValidInstance();
        DmxService service = Mockito.mock(DmxService.class);
        item.processCommand(service, OFF);
        Mockito.verify(service).disableChannel(3);
        Mockito.verify(service).disableChannel(4);
    }

    @Test
    public void switchesOnWhenOnCommandReceived() throws BindingConfigParseException {
        DmxItem item = getValidInstance();
        DmxService service = Mockito.mock(DmxService.class);
        item.processCommand(service, ON);
        Mockito.verify(service).enableChannel(3);
        Mockito.verify(service).enableChannel(4);
    }

    @Test
    public void switchesOnToMaxValueWhenOnCommandReceivedAndNoChannelValues() throws BindingConfigParseException {
        DmxItem item = getValidInstance();
        DmxService service = Mockito.mock(DmxService.class);
        Mockito.when(service.getChannelValue(3)).thenReturn(0);
        Mockito.when(service.getChannelValue(4)).thenReturn(0);
        item.processCommand(service, ON);
        Mockito.verify(service).enableChannel(3);
        Mockito.verify(service).enableChannel(4);
        Mockito.verify(service).setChannelValue(3, 255);
        Mockito.verify(service).setChannelValue(4, 255);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canCallCustomCommand() throws BindingConfigParseException {
        DmxItem item = getValidInstance();
        DmxService service = Mockito.mock(DmxService.class);
        DmxCommand cmd = Mockito.mock(DmxCommand.class);
        Map<String, DmxCommand> commands = ((Map<String, DmxCommand>) (Whitebox.getInternalState(item, "customCommands")));
        commands.put("ON", cmd);
        item.processCommand(service, ON);
        Mockito.verify(cmd).execute(service);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canHaveCustomCommandConfiguration() throws BindingConfigParseException {
        // test valid configurations
        DmxItem item = getItemInstance("CHANNEL[7/3:1000] ON[FADE|0:255,255,255:30000|5000:0,0,0:-1]");
        DmxCommand cmd = ((Map<String, DmxCommand>) (Whitebox.getInternalState(item, "customCommands"))).get("ON");
        Assert.assertTrue((cmd instanceof DmxFadeCommand));
        getItemInstance("CHANNEL[7/4] ,ON[SFADE|0:255,255,255:30000|5000:0,0,0:-1]");
        cmd = ((Map<String, DmxCommand>) (Whitebox.getInternalState(item, "customCommands"))).get("ON");
        Assert.assertTrue((cmd instanceof DmxFadeCommand));
        item = getItemInstance("CHANNEL[1/18], ON[FADE|0:255,255,255:125|0:0,0,255:125|0:255,255,255:125|0:0,0,255:125|0:255,255,255:125|0:0,0,255:125|0:255,255,255:125|0:0,0,255:125|0:255,255,255:125|0:0,0,255:125|0:0,0,255:-1]");
        cmd = ((Map<String, DmxCommand>) (Whitebox.getInternalState(item, "customCommands"))).get("ON");
        Assert.assertTrue((cmd instanceof DmxFadeCommand));
        item = getItemInstance("CHANNEL[1/18], ON[SFADE|0:255,255,255:125|0:0,0,255:125|0:255,255,255:125|0:0,0,255:125|0:255,255,255:125|0:0,0,255:125|0:255,255,255:125|0:0,0,255:125|0:255,255,255:125|0:0,0,255:125|0:0,0,255:-1]");
        cmd = ((Map<String, DmxCommand>) (Whitebox.getInternalState(item, "customCommands"))).get("ON");
        Assert.assertTrue((cmd instanceof DmxSuspendingFadeCommand));
        item = getItemInstance("CHANNEL[13/3], 0[FADE|2000:127,36,127:0|2000:0,0,127:0|2000:127,0,0:0], 1[SFADE|500:127,36,127:0|500:0,0,127:0|500:127,0,0:0], 2[FADE|200:127,36,127:300|200:0,0,127:300|200:127,0,0:300]");
        cmd = ((Map<String, DmxCommand>) (Whitebox.getInternalState(item, "customCommands"))).get("0");
        Assert.assertTrue((cmd instanceof DmxFadeCommand));
        cmd = ((Map<String, DmxCommand>) (Whitebox.getInternalState(item, "customCommands"))).get("1");
        Assert.assertTrue((cmd instanceof DmxSuspendingFadeCommand));
        // test invalid configurations
        try {
            item = getItemInstance("CHANNEL[7:1000], ON[FADE|1,2,5]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[7:1000] ON[FADE|1,2,5");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[7:1000]ONFADE|1,2,5]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[7:1000] ON[FADE|1,2,5]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
        try {
            item = getItemInstance("CHANNEL[7/3:1000], OR[FADE|0:255,255,255|5000:0,0,0:-1]");
            Assert.fail("Missing exception");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
        }
    }
}

