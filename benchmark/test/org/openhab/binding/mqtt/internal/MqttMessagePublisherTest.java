/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mqtt.internal;


import HSBType.BLUE;
import MessageType.COMMAND;
import MessageType.STATE;
import OnOffType.OFF;
import OnOffType.ON;
import OpenClosedType.CLOSED;
import PercentType.HUNDRED;
import PercentType.ZERO;
import UpDownType.UP;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
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
public class MqttMessagePublisherTest {
    @Mock
    private TransformationService transformer;

    @Test
    public void canParseValidConfigurations() throws BindingConfigParseException {
        validateConfig("mybroker:/mytopic:command:ON:1", "mybroker", "/mytopic", COMMAND, "ON", "1");
        validateConfig("mybroker1:/a/long/topic/goes/here:command:ON:default", "mybroker1", "/a/long/topic/goes/here", COMMAND, "ON", "default");
        validateConfig("mybroker1:/wildcard/not/topic:command:ON:xslt(myfile.xslt)", "mybroker1", "/wildcard/not/topic", COMMAND, "ON", "xslt(myfile.xslt)");
        validateConfig("mybroker:/mytopic:command:*:file(/tmp/myfile.txt)", "mybroker", "/mytopic", COMMAND, "*", "file(/tmp/myfile.txt)");
        validateConfig("mybroker:/mytopic:command:*:file(/tmp/myfile.txt)", "mybroker", "/mytopic", COMMAND, "*", "file(/tmp/myfile.txt)");
        String jsonMessage = "{\"message\"\\: \"command\",\"origin\"\\: \"openhab\",\"data\"\\: {\"lamp1\"\\: \"ON\"}} ";
        String jsonMessageNoEsc = "{\"message\": \"command\",\"origin\": \"openhab\",\"data\": {\"lamp1\": \"ON\"}}";
        validateConfig((("testbroker:/mytopic:state:OFF:" + jsonMessage) + ""), "testbroker", "/mytopic", STATE, "OFF", jsonMessageNoEsc);
    }

    @Test
    public void canDetectInvalidConfigurations() {
        validateBadConfig(":/mytopic:command:ON:1");
        validateBadConfig("mybroker::command:ON:1:99");
        validateBadConfig("mybroker:/mytopic:command:ON:");
        validateBadConfig("mybroker:command:ON:1");
        validateBadConfig("mybroker:/mytopic:command2:ON:1");
        validateBadConfig("mybroker:/test:/mytopic:command:ON:1");
        validateBadConfig(null);
        validateBadConfig("");
        validateBadConfig(" mybroker ; /mytopic : command : * : file(/tmp/myfile.txt)");
    }

    @Test
    public void canDetectSupportForStates() throws BindingConfigParseException {
        testStateSupport("broker:/topic:state:OFF:0", OFF, true);
        testStateSupport("broker:/topic:state:OFF:0", ON, false);
        testStateSupport("broker:/topic:state:*:0", OFF, true);
        testStateSupport("broker:/topic:state:off:0", OFF, true);
        testStateSupport("broker:/topic:stAte:100:0", DecimalType.valueOf("100"), true);
        testStateSupport("broker:/topic:state:100:0", DecimalType.valueOf("99"), false);
        testStateSupport("broker:/topic:state:20:0", HUNDRED, false);
        testStateSupport("broker:/topic:state:100:0", HUNDRED, true);
        testStateSupport("broker:/topic:state:100:0", ZERO, false);
        testStateSupport("broker:/topic:state:0:0", ZERO, true);
        testStateSupport("broker:/topic:state:CLOSED:0", CLOSED, true);
        testStateSupport("broker:/topic:state:OPEN:0", CLOSED, false);
        testStateSupport("broker:/topic:command:*:0", CLOSED, false);
        testStateSupport("broker:/topic:command:CLOSED:0", CLOSED, false);
        testStateSupport("broker:/topic:state:240.0,100.0,100.0:0", BLUE, true);
    }

    @Test
    public void canDetectSupportForCommands() throws BindingConfigParseException {
        testCommandSupport("broker:/topic:command:OFF:0", OFF, true);
        testCommandSupport("broker:/topic:command:OFF:0", ON, false);
        testCommandSupport("broker:/topic:command:*:0", OFF, true);
        testCommandSupport("broker:/topic:command:off:0", OFF, true);
        testCommandSupport("broker:/topic:commAnd:100:0", DecimalType.valueOf("100"), true);
        testCommandSupport("broker:/topic:command:100:0", DecimalType.valueOf("99"), false);
        testCommandSupport("broker:/topic:command:20:0", HUNDRED, false);
        testCommandSupport("broker:/topic:command:100:0", HUNDRED, true);
        testCommandSupport("broker:/topic:command:100:0", ZERO, false);
        testCommandSupport("broker:/topic:command:5:0", ZERO, false);
        testCommandSupport("broker:/topic:command:CLOSED:0", CLOSED, true);
        testCommandSupport("broker:/topic:command:OPEN:0", CLOSED, false);
        testCommandSupport("broker:/topic:state:*:0", CLOSED, false);
        testCommandSupport("broker:/topic:state:CLOSED:0", CLOSED, false);
        System.out.println(BLUE);
        testCommandSupport("broker:/topic:command:240.0,100.0,100.0:0", BLUE, true);
    }

    @Test
    public void canCreateMessageFromCommandOrStateWithStaticTransformation() throws Exception {
        Assert.assertEquals("test", getPublishedMessage("broker:/topic:command:*:test", "dummy"));
        Assert.assertEquals("test", getPublishedMessage("broker:/topic:state:*:test", "dummy"));
        Assert.assertEquals("eisen", getPublishedMessage("broker:/topic:state:*:eisen", ON.toString()));
        Assert.assertEquals("funk", getPublishedMessage("broker:/topic:state:*:funk", ON.toString()));
        Assert.assertEquals("{\"person\"{\"name\":\"me\"}}", getPublishedMessage("broker:/topic:state:*:{\"person\"{\"name\"\\:\"me\"}}", ON.toString()));
        Assert.assertEquals("ON", getPublishedMessage("broker:/topic:command:*:default", ON.toString()));
        Assert.assertEquals("OFF", getPublishedMessage("broker:/topic:command:*:default", OFF.toString()));
        Assert.assertEquals("UP", getPublishedMessage("broker:/topic:command:*:default", UP.toString()));
        Assert.assertEquals("100", getPublishedMessage("broker:/topic:command:*:default", HUNDRED.toString()));
        Assert.assertEquals("80,81,82", getPublishedMessage("broker:/topic:command:*:default", HSBType.valueOf("80,81,82").toString()));
        Assert.assertEquals("0", getPublishedMessage("broker:/topic:command:*:default", DecimalType.ZERO.toString()));
        Assert.assertEquals("ahah", getPublishedMessage("broker:/topic:command:*:default", StringType.valueOf("ahah").toString()));
    }
}

