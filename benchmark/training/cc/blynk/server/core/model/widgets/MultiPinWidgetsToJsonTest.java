package cc.blynk.server.core.model.widgets;


import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.controls.RGB;
import cc.blynk.server.core.model.widgets.controls.TwoAxisJoystick;
import cc.blynk.server.core.model.widgets.outputs.LCD;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.11.17.
 */
public class MultiPinWidgetsToJsonTest {
    @Test
    public void testJoystick() {
        TwoAxisJoystick joystick = new TwoAxisJoystick();
        joystick.dataStreams = new DataStream[]{ new DataStream(((short) (1)), false, false, PinType.VIRTUAL, "value", 0, 250, "label"), new DataStream(((short) (2)), false, false, PinType.VIRTUAL, "value2", 0, 250, "label") };
        Assert.assertEquals("[\"value\"]", joystick.getJsonValue());
        joystick.split = true;
        Assert.assertEquals("[\"value\",\"value2\"]", joystick.getJsonValue());
    }

    @Test
    public void testRGB() {
        RGB rgb = new RGB();
        rgb.dataStreams = new DataStream[]{ new DataStream(((short) (1)), false, false, PinType.VIRTUAL, "value", 0, 250, "label"), new DataStream(((short) (2)), false, false, PinType.VIRTUAL, "value2", 0, 250, "label") };
        Assert.assertEquals("[\"value\"]", rgb.getJsonValue());
        rgb.splitMode = true;
        Assert.assertEquals("[\"value\",\"value2\"]", rgb.getJsonValue());
    }

    @Test
    public void testLCD() {
        LCD lcd = new LCD();
        lcd.advancedMode = true;
        lcd.dataStreams = new DataStream[]{ new DataStream(((short) (1)), false, false, PinType.VIRTUAL, "value", 0, 250, "label"), new DataStream(((short) (2)), false, false, PinType.VIRTUAL, "value2", 0, 250, "label") };
        Assert.assertEquals("[\"value\"]", lcd.getJsonValue());
        lcd.advancedMode = false;
        Assert.assertEquals("[\"value\",\"value2\"]", lcd.getJsonValue());
    }

    @Test
    public void testJoystickMultiValue() {
        TwoAxisJoystick joystick = new TwoAxisJoystick();
        joystick.dataStreams = new DataStream[]{ new DataStream(((short) (1)), false, false, PinType.VIRTUAL, "value\u0000value2", 0, 250, "label") };
        Assert.assertEquals("[\"value\",\"value2\"]", joystick.getJsonValue());
    }
}

