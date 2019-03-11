package cc.blynk.test.utils;


import JsonParser.MAPPER;
import PinType.DIGITAL;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.controls.Button;
import cc.blynk.server.core.model.widgets.controls.RGB;
import cc.blynk.server.core.model.widgets.controls.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.InputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:27
 */
public class JsonParsingTest {
    private static final ObjectReader profileReader = JsonParser.init().readerFor(Profile.class);

    @Test
    public void testParseUserProfile() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json.txt");
        Profile profile = JsonParsingTest.parseProfile(is);
        Assert.assertNotNull(profile);
        Assert.assertNotNull(profile.dashBoards);
        Assert.assertEquals(profile.dashBoards.length, 1);
        DashBoard dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        Assert.assertEquals("My Dashboard", dashBoard.name);
        Assert.assertNotNull(dashBoard.widgets);
        Assert.assertEquals(dashBoard.widgets.length, 8);
        for (Widget widget : dashBoard.widgets) {
            Assert.assertNotNull(widget);
            Assert.assertEquals(1, widget.x);
            Assert.assertEquals(1, widget.y);
            Assert.assertEquals(1, widget.id);
            Assert.assertEquals("Some Text", widget.label);
        }
    }

    @Test
    public void testUserProfileToJson() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json.txt");
        Profile profile = JsonParsingTest.parseProfile(is);
        String userProfileString = profile.toString();
        Assert.assertNotNull(userProfileString);
        Assert.assertTrue(userProfileString.contains("dashBoards"));
    }

    @Test
    public void testParseIOSProfile() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_ios_profile_json.txt");
        Profile profile = JsonParsingTest.parseProfile(is);
        Assert.assertNotNull(profile);
        Assert.assertNotNull(profile.dashBoards);
        Assert.assertEquals(1, profile.dashBoards.length);
        Assert.assertNotNull(profile.dashBoards[0].widgets);
        Assert.assertNotNull(profile.dashBoards[0].widgets[0]);
        Assert.assertNotNull(profile.dashBoards[0].widgets[1]);
        Assert.assertTrue(((Button) (profile.dashBoards[0].widgets[0])).pushMode);
        Assert.assertFalse(((Button) (profile.dashBoards[0].widgets[1])).pushMode);
    }

    @Test
    public void testJoystickAndFieldAreParsed() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json_with_outdated_widget.txt");
        Profile profile = JsonParsingTest.parseProfile(is);
        Assert.assertNotNull(profile);
        Assert.assertNotNull(profile.dashBoards);
        Assert.assertEquals(1, profile.dashBoards.length);
        Assert.assertNotNull(profile.dashBoards[0].widgets);
        Assert.assertNotNull(profile.dashBoards[0].widgets[0]);
        Assert.assertNotNull(profile.dashBoards[0].widgets[1]);
        Assert.assertTrue(((profile.dashBoards[0].widgets[0]) instanceof Button));
        Assert.assertTrue(((profile.dashBoards[0].widgets[1]) instanceof Button));
    }

    @Test
    public void testJSONToRGB() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json_RGB.txt");
        Profile profile = JsonParsingTest.parseProfile(is);
        Assert.assertNotNull(profile);
        Assert.assertNotNull(profile.dashBoards);
        Assert.assertEquals(1, profile.dashBoards.length);
        Assert.assertNotNull(profile.dashBoards[0]);
        Assert.assertNotNull(profile.dashBoards[0].widgets);
        Assert.assertEquals(1, profile.dashBoards[0].widgets.length);
        RGB rgb = ((RGB) (profile.dashBoards[0].widgets[0]));
        Assert.assertNotNull(rgb.dataStreams);
        Assert.assertEquals(2, rgb.dataStreams.length);
        DataStream dataStream1 = rgb.dataStreams[0];
        DataStream dataStream2 = rgb.dataStreams[1];
        Assert.assertNotNull(dataStream1);
        Assert.assertNotNull(dataStream2);
        Assert.assertEquals(1, dataStream1.pin);
        Assert.assertEquals(2, dataStream2.pin);
        Assert.assertEquals("1", dataStream1.value);
        Assert.assertEquals("2", dataStream2.value);
        Assert.assertEquals(DIGITAL, dataStream1.pinType);
        Assert.assertEquals(DIGITAL, dataStream2.pinType);
        Assert.assertFalse(dataStream1.pwmMode);
        Assert.assertTrue(dataStream2.pwmMode);
    }

    @Test
    public void testUserProfileToJson2() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json_2.txt");
        Profile profile = JsonParsingTest.parseProfile(is);
        String userProfileString = profile.toString();
        Assert.assertNotNull(userProfileString);
        Assert.assertTrue(userProfileString.contains("dashBoards"));
    }

    @Test
    public void testUserProfileToJson3() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json_3.txt");
        Profile profile = JsonParsingTest.parseProfile(is);
        String userProfileString = profile.toString();
        Assert.assertNotNull(userProfileString);
        Assert.assertTrue(userProfileString.contains("dashBoards"));
    }

    @Test
    public void testUserProfileToJsonWithTimer() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_with_timer.txt");
        Profile profile = JsonParsingTest.parseProfile(is);
        String userProfileString = profile.toString();
        Assert.assertNotNull(userProfileString);
        Assert.assertTrue(userProfileString.contains("dashBoards"));
        List<Timer> timers = getActiveTimerWidgets(profile);
        Assert.assertNotNull(timers);
        Assert.assertEquals(1, timers.size());
    }

    @Test
    public void correctSerializedObject() throws JsonProcessingException {
        Button button = new Button();
        button.id = 1;
        button.label = "MyButton";
        button.x = 2;
        button.y = 2;
        button.color = 0;
        button.width = 2;
        button.height = 2;
        button.pushMode = false;
        String result = MAPPER.writeValueAsString(button);
        Assert.assertEquals("{\"type\":\"BUTTON\",\"id\":1,\"x\":2,\"y\":2,\"color\":0,\"width\":2,\"height\":2,\"tabId\":0,\"label\":\"MyButton\",\"isDefaultColor\":false,\"deviceId\":0,\"pin\":-1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"pushMode\":false}", result);
    }
}

