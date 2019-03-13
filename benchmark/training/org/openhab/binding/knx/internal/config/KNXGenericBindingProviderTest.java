/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.knx.internal.config;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.openhab.binding.knx.internal.config.KNXGenericBindingProvider.KNXBindingConfig;
import org.openhab.binding.knx.internal.config.KNXGenericBindingProvider.KNXBindingConfigItem;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.DateTimeItem;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StopMoveType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;
import org.openhab.model.item.binding.BindingConfigParseException;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.CommandDP;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.datapoint.StateDP;
import tuwien.auto.calimero.exception.KNXFormatException;


/**
 *
 *
 * @author Thomas.Eichstaedt-Engelen
 * @author Kai Kreuzer
 * @since 1.0.0
 */
public class KNXGenericBindingProviderTest {
    private KNXGenericBindingProvider provider;

    private Item item1;

    private Item item2;

    @Test(expected = BindingConfigParseException.class)
    public void testParseBindingConfig_tooManyArguments() throws BindingConfigParseException {
        provider.parseBindingConfigString(new KNXGenericBindingProviderTest.TestItem(), "0/0/0, 0/0/0, 0/0/0, 0/0/0, 0/0/0");
    }

    @Test
    public void testParseBindingConfig_DateTimeAcceptsTwoGAs() throws BindingConfigParseException {
        provider.parseBindingConfigString(new DateTimeItem("DateTest"), "11.001:15/7/11, 10.001:15/7/10");
    }

    @Test
    public void testParseBindingConfig() throws BindingConfigParseException, KNXFormatException {
        // method under Test
        KNXBindingConfig bindingConfigs = provider.parseBindingConfigString(item1, "<4/2/10+0/2/10, 5.005:4/2/11+0/2/11, +4/2/12, 4/2/13");
        // Assertions
        Assert.assertEquals(4, bindingConfigs.size());
        for (KNXBindingConfigItem bindingConfig : bindingConfigs) {
            Assert.assertEquals("item1", bindingConfig.itemName);
        }
        Assert.assertNotNull(bindingConfigs.get(0).readableDataPoint);
        Assert.assertNull(bindingConfigs.get(1).readableDataPoint);
        Assert.assertNull(bindingConfigs.get(2).readableDataPoint);
        Assert.assertNull(bindingConfigs.get(3).readableDataPoint);
        Assert.assertTrue(bindingConfigs.get(0).allDataPoints.contains(new GroupAddress("4/2/10")));
        Assert.assertTrue(bindingConfigs.get(0).allDataPoints.contains(new GroupAddress("0/2/10")));
        Assert.assertTrue(bindingConfigs.get(1).allDataPoints.contains(new GroupAddress("4/2/11")));
        Assert.assertTrue(bindingConfigs.get(1).allDataPoints.contains(new GroupAddress("0/2/11")));
        Assert.assertTrue(bindingConfigs.get(2).allDataPoints.contains(new GroupAddress("4/2/12")));
        Assert.assertTrue(bindingConfigs.get(3).allDataPoints.contains(new GroupAddress("4/2/13")));
        Assert.assertEquals(true, ((bindingConfigs.get(0).mainDataPoint) instanceof CommandDP));
        Assert.assertEquals(true, ((bindingConfigs.get(1).mainDataPoint) instanceof CommandDP));
        Assert.assertEquals(true, ((bindingConfigs.get(2).mainDataPoint) instanceof StateDP));
        Assert.assertEquals(true, ((bindingConfigs.get(3).mainDataPoint) instanceof CommandDP));
    }

    @Test
    public void testIsCommandGA() throws BindingConfigParseException, KNXFormatException {
        provider.processBindingConfiguration("text", item1, "<4/2/10+0/2/10, 5.005:4/2/11+0/2/11, +4/2/12, 4/2/13");
        // method under Test
        Assert.assertEquals(true, provider.isCommandGA(new GroupAddress("4/2/10")));
        Assert.assertEquals(true, provider.isCommandGA(new GroupAddress("4/2/11")));
        Assert.assertEquals(true, provider.isCommandGA(new GroupAddress("4/2/13")));
        Assert.assertEquals(false, provider.isCommandGA(new GroupAddress("0/2/10")));
        Assert.assertEquals(false, provider.isCommandGA(new GroupAddress("0/2/11")));
        Assert.assertEquals(false, provider.isCommandGA(new GroupAddress("4/2/12")));
    }

    @Test
    public void testReadFlagWithDPT() throws BindingConfigParseException, KNXFormatException {
        provider.processBindingConfiguration("text", item1, "<5.001:4/2/10");
        // method under Test
        Iterator<Datapoint> readableDatapoints = provider.getReadableDatapoints().iterator();
        Assert.assertEquals(true, readableDatapoints.hasNext());
        Assert.assertEquals(true, readableDatapoints.next().getDPT().equals("5.001"));
    }

    @Test
    public void testReadFromThirdGA() throws BindingConfigParseException, KNXFormatException {
        provider.processBindingConfiguration("text", item1, "2/1/5+2/4/5, 2/2/5, <0/3/5");
        // method under Test
        Iterator<Datapoint> readableDatapoints = provider.getReadableDatapoints().iterator();
        Assert.assertEquals(true, readableDatapoints.hasNext());
        Assert.assertEquals(0, readableDatapoints.next().getMainAddress().getMainGroup());
    }

    @Test
    public void testReadFromListeningGA() throws BindingConfigParseException, KNXFormatException {
        provider.processBindingConfiguration("text", item1, "2/1/5+<0/4/5");
        // method under Test
        Iterator<Datapoint> readableDatapoints = provider.getReadableDatapoints().iterator();
        Assert.assertEquals(true, readableDatapoints.hasNext());
        Assert.assertEquals(0, readableDatapoints.next().getMainAddress().getMainGroup());
    }

    @Test
    public void testAutoUpdate() throws BindingConfigParseException, KNXFormatException {
        provider.processBindingConfiguration("text", item1, "<4/2/10+0/2/10, 5.005:4/2/11+0/2/11, +4/2/12, 4/2/13");
        provider.processBindingConfiguration("text", item2, "<4/2/10, 5.005:4/2/11,, 4/2/13");
        // method under Test
        Assert.assertEquals(Boolean.FALSE, provider.autoUpdate(item1.getName()));
        Assert.assertEquals(null, provider.autoUpdate(item2.getName()));
    }

    @Test
    public void testProvidesBindingFor() throws BindingConfigParseException, KNXFormatException {
        provider.processBindingConfiguration("text", item1, "<4/2/10+0/2/10, 5.005:4/2/11+0/2/11, +4/2/12, 4/2/13");
        provider.processBindingConfiguration("text", item2, "<4/2/10, 5.005:4/2/11,, 4/2/13");
        // method under Test
        Assert.assertEquals(true, provider.providesBindingFor(item1.getName()));
        Assert.assertEquals(true, provider.providesBindingFor(item1.getName()));
        Assert.assertEquals(false, provider.providesBindingFor("someotheritem"));
    }

    @Test
    public void testIsStartStopGA() throws BindingConfigParseException, KNXFormatException {
        provider.processBindingConfiguration("text", item1, "<4/2/10+0/2/10, 5.005:4/2/11+0/2/11, +4/2/12ss, 4/2/13ss");
        // method under Test
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("4/2/10")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("0/2/10")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("4/2/11")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("0/2/11")));
        Assert.assertTrue(provider.isStartStopGA(new GroupAddress("4/2/12")));
        Assert.assertTrue(provider.isStartStopGA(new GroupAddress("4/2/13")));
        provider.processBindingConfiguration("text", item1, "<4/2/10ss+0/2/10, 5.005:4/2/11+0/2/11ss, +4/2/12, 4/2/13");
        // method under Test
        Assert.assertTrue(provider.isStartStopGA(new GroupAddress("4/2/10")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("0/2/10")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("4/2/11")));
        Assert.assertTrue(provider.isStartStopGA(new GroupAddress("0/2/11")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("4/2/12")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("4/2/13")));
        provider.processBindingConfiguration("text", item1, "<4/2/10+0/2/10ss, 5.005:4/2/11ss+0/2/11, +4/2/12, 4/2/13");
        // method under Test
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("4/2/10")));
        Assert.assertTrue(provider.isStartStopGA(new GroupAddress("0/2/10")));
        Assert.assertTrue(provider.isStartStopGA(new GroupAddress("4/2/11")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("0/2/11")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("4/2/12")));
        Assert.assertFalse(provider.isStartStopGA(new GroupAddress("4/2/13")));
    }

    private class TestItem extends GenericItem {
        private List<Class<? extends State>> acceptedDataTypes = new ArrayList<Class<? extends State>>();

        private List<Class<? extends Command>> acceptedCommandTypes = new ArrayList<Class<? extends Command>>();

        public TestItem() {
            this("TestItem");
        }

        public TestItem(String itemName) {
            super(itemName);
            acceptedDataTypes.add(UnDefType.class);
            acceptedDataTypes.add(UpDownType.class);
            acceptedDataTypes.add(PercentType.class);
            acceptedDataTypes.add(StringType.class);
            acceptedCommandTypes.add(UpDownType.class);
            acceptedCommandTypes.add(StopMoveType.class);
            acceptedCommandTypes.add(PercentType.class);
            acceptedCommandTypes.add(StringType.class);
        }

        @Override
        public List<Class<? extends State>> getAcceptedDataTypes() {
            return acceptedDataTypes;
        }

        @Override
        public List<Class<? extends Command>> getAcceptedCommandTypes() {
            return acceptedCommandTypes;
        }
    }
}

