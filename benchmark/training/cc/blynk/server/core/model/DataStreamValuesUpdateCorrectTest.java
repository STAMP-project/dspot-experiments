package cc.blynk.server.core.model;


import PinType.DIGITAL;
import StringUtils.BODY_SEPARATOR_STRING;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.controls.Button;
import cc.blynk.server.core.model.widgets.controls.RGB;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.InputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 02.11.15.
 */
public class DataStreamValuesUpdateCorrectTest {
    private static final ObjectReader profileReader = JsonParser.init().readerFor(Profile.class);

    @Test
    public void testHas1Pin() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json.txt");
        Profile profile = DataStreamValuesUpdateCorrectTest.parseProfile(is);
        DashBoard dash = profile.dashBoards[0];
        dash.isActive = true;
        Button button = dash.getWidgetByType(Button.class);
        Assert.assertEquals(1, button.pin);
        Assert.assertEquals(DIGITAL, button.pinType);
        Assert.assertEquals("1", button.value);
        DataStreamValuesUpdateCorrectTest.update(profile, 0, "dw 1 0".replaceAll(" ", BODY_SEPARATOR_STRING));
        Assert.assertEquals("0", button.value);
        DataStreamValuesUpdateCorrectTest.update(profile, 0, "aw 1 1".replaceAll(" ", BODY_SEPARATOR_STRING));
        Assert.assertEquals("0", button.value);
        DataStreamValuesUpdateCorrectTest.update(profile, 0, "dw 1 1".replaceAll(" ", BODY_SEPARATOR_STRING));
        Assert.assertEquals("1", button.value);
        RGB rgb = new RGB();
        rgb.dataStreams = new DataStream[3];
        rgb.dataStreams[0] = new DataStream(((short) (0)), false, false, PinType.VIRTUAL, null, 0, 255, null);
        rgb.dataStreams[1] = new DataStream(((short) (1)), false, false, PinType.VIRTUAL, null, 0, 255, null);
        rgb.dataStreams[2] = new DataStream(((short) (2)), false, false, PinType.VIRTUAL, null, 0, 255, null);
        dash.widgets = ArrayUtils.add(dash.widgets, rgb);
        DataStreamValuesUpdateCorrectTest.update(profile, 0, "vw 0 100".replaceAll(" ", BODY_SEPARATOR_STRING));
        DataStreamValuesUpdateCorrectTest.update(profile, 0, "vw 1 101".replaceAll(" ", BODY_SEPARATOR_STRING));
        DataStreamValuesUpdateCorrectTest.update(profile, 0, "vw 2 102".replaceAll(" ", BODY_SEPARATOR_STRING));
        for (int i = 0; i < (rgb.dataStreams.length); i++) {
            Assert.assertEquals(("10" + i), rgb.dataStreams[i].value);
        }
        rgb = new RGB();
        rgb.dataStreams = new DataStream[3];
        rgb.dataStreams[0] = new DataStream(((short) (4)), false, false, PinType.VIRTUAL, null, 0, 255, null);
        rgb.dataStreams[1] = new DataStream(((short) (4)), false, false, PinType.VIRTUAL, null, 0, 255, null);
        rgb.dataStreams[2] = new DataStream(((short) (4)), false, false, PinType.VIRTUAL, null, 0, 255, null);
        dash.widgets = ArrayUtils.add(dash.widgets, rgb);
        DataStreamValuesUpdateCorrectTest.update(profile, 0, "vw 4 100 101 102".replaceAll(" ", BODY_SEPARATOR_STRING));
        Assert.assertEquals("100 101 102".replaceAll(" ", BODY_SEPARATOR_STRING), rgb.dataStreams[0].value);
    }
}

