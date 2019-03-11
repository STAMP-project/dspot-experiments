/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mqtt.internal;


import org.junit.Assert;
import org.junit.Test;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 *
 *
 * @author Davy Vanherbergen
 * @since 1.3.0
 */
public class MqttItemConfigTest {
    @Test
    public void canParseInboundConfig() throws BindingConfigParseException {
        MqttItemConfig c = new MqttItemConfig(new NumberItem("myItem"), "<[publicweatherservice:/london-city/temperature:state:default]");
        Assert.assertEquals(0, c.getMessagePublishers().size());
        Assert.assertEquals(1, c.getMessageSubscribers().size());
    }

    @Test
    public void canParseOutboundConfig() throws BindingConfigParseException {
        MqttItemConfig c = new MqttItemConfig(new SwitchItem("myItem"), ">[mybroker:/mytopic:command:ON:1]");
        Assert.assertEquals(1, c.getMessagePublishers().size());
        Assert.assertEquals(0, c.getMessageSubscribers().size());
    }

    @Test
    public void canParseMultipleInboundConfigs() throws BindingConfigParseException {
        MqttItemConfig c = new MqttItemConfig(new SwitchItem("myItem"), "<[mybroker:/myHome/doorbell:state:XSLT(doorbell.xslt)], <[mybroker:/myHome/doorbell:command:ON], <[mybroker:/myHome/doorbell:state:XSLT(doorbell.xslt)]");
        Assert.assertEquals(0, c.getMessagePublishers().size());
        Assert.assertEquals(3, c.getMessageSubscribers().size());
    }

    @Test
    public void canParseMultipleOutboundConfigs() throws BindingConfigParseException {
        MqttItemConfig c = new MqttItemConfig(new SwitchItem("myItem"), ">[mybroker:/mytopic:command:ON:1],>[mybroker:/mytopic:command:OFF:0]");
        Assert.assertEquals(2, c.getMessagePublishers().size());
        Assert.assertEquals(0, c.getMessageSubscribers().size());
    }

    @Test
    public void canParseMultipleConfigs() throws BindingConfigParseException {
        MqttItemConfig c = new MqttItemConfig(new SwitchItem("myItem"), ">[mybroker:/mytopic:command:ON:1],>[mybroker:/mytopic:command:OFF:0],<[mybroker:/myHome/doorbell:state:XSLT(doorbell.xslt)], <[mybroker:/myHome/doorbell:command:ON], <[mybroker:/myHome/doorbell:state:XSLT(doorbell.xslt)]");
        Assert.assertEquals(2, c.getMessagePublishers().size());
        Assert.assertEquals(3, c.getMessageSubscribers().size());
    }
}

