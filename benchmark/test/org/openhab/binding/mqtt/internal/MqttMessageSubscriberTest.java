/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mqtt.internal;


import DecimalType.ZERO;
import MessageType.COMMAND;
import MessageType.STATE;
import OnOffType.ON;
import PercentType.HUNDRED;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.LocationItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.PointType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.transform.TransformationService;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 *
 *
 * @author Davy Vanherbergen
 * @since 1.3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class MqttMessageSubscriberTest {
    @Mock
    private TransformationService transformer;

    @Test
    public void canParseValidConfigurations() throws BindingConfigParseException {
        try {
            validateConfig("mybroker:/mytopic:command:1", "mybroker", "/mytopic", COMMAND, "1");
            validateConfig("mybroker1:/a/long/topic/goes/here:command:default", "mybroker1", "/a/long/topic/goes/here", COMMAND, "default");
            validateConfig("mybroker1:/a/long/topic/goes/here:command:dummyfunction", "mybroker1", "/a/long/topic/goes/here", COMMAND, "dummyfunction");
            validateConfig("mybroker1:/wildcard/+/topic/#:command:xslt(myfile.xslt)", "mybroker1", "/wildcard/+/topic/#", COMMAND, "xslt(myfile.xslt)");
            validateConfig("testbroker:/mytopic:state:json(/group/person[2]/value)", "testbroker", "/mytopic", STATE, "json(/group/person[2]/value)");
            validateConfig("mybroker:/mytopic:command:file(/tmp/myfile.txt)", "mybroker", "/mytopic", COMMAND, "file(/tmp/myfile.txt)");
            validateConfig("mybroker:/mytopic:command:file(/tmp/myfile.txt)", "mybroker", "/mytopic", COMMAND, "file(/tmp/myfile.txt)");
            validateConfig("mybroker:/mytopic:command:file(c\\:\\tmp\\myfile.txt)", "mybroker", "/mytopic", COMMAND, "file(c:\\tmp\\myfile.txt)");
        } catch (BindingConfigParseException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void canDetectInvalidConfigurations() {
        validateBadConfig(" :/mytopic:command:ON:.*:1");
        validateBadConfig("mybroker:/mytopic:command:ON:.*:1");
        validateBadConfig("mybroker:/mytopic:command:ON:1:99");
        validateBadConfig("mybroker::/mytopic:command:ON:1");
        validateBadConfig("mybroker:command:ON:1");
        validateBadConfig("mybroker:/mytopic:command:ON:.*:1");
        validateBadConfig(null);
        validateBadConfig("");
        validateBadConfig("  mybroker : /mytopic : command : * : .* : file(/tmp/myfile.txt)");
        validateBadConfig("mybroker:/mytopic:comand:ON:1");
    }

    @Test
    public void canParseCommand() throws Exception {
        ColorItem colorItem = new ColorItem("ColorItem");
        DimmerItem dimmerItem = new DimmerItem("DimmerItem");
        LocationItem locationItem = new LocationItem("LocationItem");
        NumberItem numberItem = new NumberItem("NumberItem");
        RollershutterItem rollershutterItem = new RollershutterItem("SetpointItem");
        StringItem stringItem = new StringItem("StringItem");
        SwitchItem switchItem = new SwitchItem("SwitchItem");
        MqttMessageSubscriber subscriber = new MqttMessageSubscriber("mybroker:/mytopic:command:default");
        Assert.assertEquals(StringType.valueOf("test"), subscriber.getCommand("test", stringItem.getAcceptedCommandTypes()));
        Assert.assertEquals(StringType.valueOf("{\"person\"{\"name\":\"me\"}}"), subscriber.getCommand("{\"person\"{\"name\":\"me\"}}", stringItem.getAcceptedCommandTypes()));
        Assert.assertEquals(StringType.valueOf(""), subscriber.getCommand("", stringItem.getAcceptedCommandTypes()));
        Assert.assertEquals(ON, subscriber.getCommand("ON", switchItem.getAcceptedCommandTypes()));
        Assert.assertEquals(HSBType.valueOf("5,6,5"), subscriber.getCommand("5,6,5", colorItem.getAcceptedCommandTypes()));
        Assert.assertEquals(ZERO, subscriber.getCommand(ZERO.toString(), numberItem.getAcceptedCommandTypes()));
        Assert.assertEquals(HUNDRED, subscriber.getCommand(HUNDRED.toString(), dimmerItem.getAcceptedCommandTypes()));
        Assert.assertEquals(PercentType.valueOf("80"), subscriber.getCommand(PercentType.valueOf("80").toString(), rollershutterItem.getAcceptedCommandTypes()));
        Assert.assertEquals(PointType.valueOf("53.3239919,-6.5258807"), subscriber.getCommand(PointType.valueOf("53.3239919,-6.5258807").toString(), locationItem.getAcceptedCommandTypes()));
    }

    @Test
    public void canParseState() throws Exception {
        LocationItem locationItem = new LocationItem("LocationItem");
        StringItem stringItem = new StringItem("StringItem");
        SwitchItem switchItem = new SwitchItem("SwitchItem");
        MqttMessageSubscriber subscriber = new MqttMessageSubscriber("mybroker:/mytopic:state:default");
        Assert.assertEquals(ON, subscriber.getState("ON", switchItem.getAcceptedDataTypes()));
        Assert.assertEquals(StringType.valueOf(""), subscriber.getState("", stringItem.getAcceptedDataTypes()));
        Assert.assertEquals(StringType.valueOf("test"), subscriber.getState("test", stringItem.getAcceptedDataTypes()));
        Assert.assertEquals(StringType.valueOf("{\"person\"{\"name\":\"me\"}}"), subscriber.getState("{\"person\"{\"name\":\"me\"}}", stringItem.getAcceptedDataTypes()));
        Assert.assertEquals(PointType.valueOf("53.3239919,-6.5258807"), subscriber.getState(PointType.valueOf("53.3239919,-6.5258807").toString(), locationItem.getAcceptedDataTypes()));
    }
}

