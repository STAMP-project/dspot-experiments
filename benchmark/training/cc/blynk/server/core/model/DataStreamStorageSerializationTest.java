package cc.blynk.server.core.model;


import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.storage.key.DashPinPropertyStorageKey;
import cc.blynk.server.core.model.storage.key.DashPinStorageKey;
import cc.blynk.server.core.model.storage.value.MultiPinStorageValue;
import cc.blynk.server.core.model.storage.value.MultiPinStorageValueType;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.storage.value.SinglePinStorageValue;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.11.16.
 */
public class DataStreamStorageSerializationTest {
    @Test
    public void testMigrationOfOldDataIsCorrect() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json_old_pinstorage.txt");
        FileManager fileManager = new FileManager("123", "host");
        User user = new User();
        user.profile = DataStreamValuesUpdateCorrectTest.parseProfile(is);
        fileManager.makeProfileChanges(user);
        Assert.assertEquals(1, user.profile.dashBoards.length);
        Assert.assertNotNull(user.profile.dashBoards[0].pinsStorage);
        Assert.assertEquals(0, user.profile.dashBoards[0].pinsStorage.size());
        Assert.assertEquals(3, user.profile.pinsStorage.size());
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        DashPinStorageKey pinStorageKey2 = new DashPinStorageKey(1, 0, PinType.DIGITAL, ((short) (111)));
        DashPinPropertyStorageKey pinStorageKey3 = new DashPinPropertyStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)), WidgetProperty.LABEL);
        Assert.assertEquals("1", ((SinglePinStorageValue) (user.profile.pinsStorage.get(pinStorageKey))).value);
        Assert.assertEquals("2", ((SinglePinStorageValue) (user.profile.pinsStorage.get(pinStorageKey2))).value);
        Assert.assertEquals("3", ((SinglePinStorageValue) (user.profile.pinsStorage.get(pinStorageKey3))).value);
    }

    @Test
    public void testSerializeSingleEmptyValue() {
        User user = new User();
        user.email = "123";
        user.profile = new Profile();
        user.profile.dashBoards = new DashBoard[]{ new DashBoard() };
        user.lastModifiedTs = 0;
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        user.profile.pinsStorage.put(pinStorageKey, new SinglePinStorageValue());
        String result = user.toString();
        Assert.assertTrue(result.contains("\"1-0-v0\":\"\""));
    }

    @Test
    public void testSerializeSingleValue() {
        User user = new User();
        user.email = "123";
        user.profile = new Profile();
        user.profile.dashBoards = new DashBoard[]{ new DashBoard() };
        user.lastModifiedTs = 0;
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        DashPinStorageKey pinStorageKey2 = new DashPinStorageKey(1, 0, PinType.DIGITAL, ((short) (1)));
        DashPinPropertyStorageKey pinStorageKey3 = new DashPinPropertyStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)), WidgetProperty.LABEL);
        user.profile.pinsStorage.put(pinStorageKey, new SinglePinStorageValue("1"));
        user.profile.pinsStorage.put(pinStorageKey2, new SinglePinStorageValue("2"));
        user.profile.pinsStorage.put(pinStorageKey3, new SinglePinStorageValue("3"));
        String result = user.toString();
        Assert.assertTrue(result.contains("\"1-0-v0\":\"1\""));
        Assert.assertTrue(result.contains("\"1-0-d1\":\"2\""));
        Assert.assertTrue(result.contains("\"1-0-v0-label\":\"3\""));
    }

    @Test
    public void testSerializeMultiValueEmpty() {
        User user = new User();
        user.email = "123";
        user.profile = new Profile();
        user.profile.dashBoards = new DashBoard[]{ new DashBoard() };
        user.lastModifiedTs = 0;
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        PinStorageValue pinStorageValue = new MultiPinStorageValue(MultiPinStorageValueType.LCD);
        user.profile.pinsStorage.put(pinStorageKey, pinStorageValue);
        String result = user.toString();
        Assert.assertTrue(result.contains("\"1-0-v0\":{\"type\":\"LCD\"}"));
    }

    @Test
    public void testSerializeMultiValueWithSingleValue() {
        User user = new User();
        user.email = "123";
        user.profile = new Profile();
        user.profile.dashBoards = new DashBoard[]{ new DashBoard() };
        user.lastModifiedTs = 0;
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        PinStorageValue pinStorageValue = new MultiPinStorageValue(MultiPinStorageValueType.LCD);
        pinStorageValue.update("1");
        user.profile.pinsStorage.put(pinStorageKey, pinStorageValue);
        String result = user.toString();
        Assert.assertTrue(result.contains("\"1-0-v0\":{\"type\":\"LCD\",\"values\":[\"1\"]}"));
    }

    @Test
    public void testSerializeMultiValueWithMultipleValues() {
        User user = new User();
        user.email = "123";
        user.profile = new Profile();
        user.profile.dashBoards = new DashBoard[]{ new DashBoard() };
        user.lastModifiedTs = 0;
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        PinStorageValue pinStorageValue = new MultiPinStorageValue(MultiPinStorageValueType.LCD);
        pinStorageValue.update("1");
        pinStorageValue.update("2");
        user.profile.pinsStorage.put(pinStorageKey, pinStorageValue);
        String result = user.toString();
        Assert.assertTrue(result.contains("\"1-0-v0\":{\"type\":\"LCD\",\"values\":[\"1\",\"2\"]}"));
    }

    @Test
    public void testSerializeMultiValueWithMultipleValuesAndLimit() {
        User user = new User();
        user.email = "123";
        user.profile = new Profile();
        user.profile.dashBoards = new DashBoard[]{ new DashBoard() };
        user.lastModifiedTs = 0;
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        PinStorageValue pinStorageValue = new MultiPinStorageValue(MultiPinStorageValueType.LCD);
        pinStorageValue.update("1");
        pinStorageValue.update("2");
        pinStorageValue.update("3");
        pinStorageValue.update("4");
        pinStorageValue.update("5");
        pinStorageValue.update("6");
        pinStorageValue.update("7");
        user.profile.pinsStorage.put(pinStorageKey, pinStorageValue);
        String result = user.toString();
        Assert.assertTrue(result.contains("\"1-0-v0\":{\"type\":\"LCD\",\"values\":[\"2\",\"3\",\"4\",\"5\",\"6\",\"7\"]}"));
    }

    @Test
    public void testSerializeMultiValueWithNilValue() {
        User user = new User();
        user.email = "123";
        user.profile = new Profile();
        user.profile.dashBoards = new DashBoard[]{ new DashBoard() };
        user.lastModifiedTs = 0;
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        PinStorageValue pinStorageValue = new MultiPinStorageValue(MultiPinStorageValueType.LCD);
        pinStorageValue.update("\u0000");
        user.profile.pinsStorage.put(pinStorageKey, pinStorageValue);
        String result = user.toString();
        Assert.assertTrue(result.contains("\"1-0-v0\":{\"type\":\"LCD\",\"values\":[\"\\u0000\"]}"));
    }

    @Test
    public void testDeserializeSingleValue() throws Exception {
        String expectedString = "{\"email\":\"123\",\"appName\":\"Blynk\",\"lastModifiedTs\":0,\"lastLoggedAt\":0," + (("\"profile\":{\"dashBoards\":[{\"id\":0,\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isShared\":false,\"isActive\":false}]," + "\"pinsStorage\":{\"1-0-v0\":\"1\",\"1-0-d111\":\"2\", \"1-0-v0-label\":\"3\"}") + "},\"isFacebookUser\":false,\"energy\":2000,\"id\":\"123-Blynk\"}");
        User user = JsonParser.parseUserFromString(expectedString);
        Assert.assertNotNull(user);
        Assert.assertEquals(3, user.profile.pinsStorage.size());
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        DashPinStorageKey pinStorageKey2 = new DashPinStorageKey(1, 0, PinType.DIGITAL, ((short) (111)));
        DashPinPropertyStorageKey pinStorageKey3 = new DashPinPropertyStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)), WidgetProperty.LABEL);
        Assert.assertEquals("1", ((SinglePinStorageValue) (user.profile.pinsStorage.get(pinStorageKey))).value);
        Assert.assertEquals("2", ((SinglePinStorageValue) (user.profile.pinsStorage.get(pinStorageKey2))).value);
        Assert.assertEquals("3", ((SinglePinStorageValue) (user.profile.pinsStorage.get(pinStorageKey3))).value);
    }

    @Test
    public void testDeserializeMultiValue() throws Exception {
        String expectedString = "{\"email\":\"123\",\"appName\":\"Blynk\",\"lastModifiedTs\":0,\"lastLoggedAt\":0," + (("\"profile\":{\"dashBoards\":[{\"id\":0,\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isShared\":false,\"isActive\":false}]," + "\"pinsStorage\":{\"1-0-v0\":{\"type\":\"LCD\",\"values\":[\"1\",\"2\"]}}") + "},\"isFacebookUser\":false,\"energy\":2000,\"id\":\"123-Blynk\"}");
        User user = JsonParser.parseUserFromString(expectedString);
        Assert.assertNotNull(user);
        Assert.assertEquals(1, user.profile.pinsStorage.size());
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(1, 0, PinType.VIRTUAL, ((short) (0)));
        Assert.assertEquals("1", ((MultiPinStorageValue) (user.profile.pinsStorage.get(pinStorageKey))).values.poll());
        Assert.assertEquals("2", ((MultiPinStorageValue) (user.profile.pinsStorage.get(pinStorageKey))).values.poll());
    }

    @Test
    public void testDeserializeEmptyMultiValue() throws Exception {
        String expectedString = "{\"email\":\"123\",\"appName\":\"Blynk\",\"lastModifiedTs\":0,\"lastLoggedAt\":0," + ((("\"profile\":{" + "\"dashBoards\":[{\"id\":0,\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isShared\":false,\"isActive\":false}],") + "\"pinsStorage\":{\"0-0-v0\":{\"type\":\"LCD\"}}") + "},\"isFacebookUser\":false,\"energy\":2000,\"id\":\"123-Blynk\"}");
        User user = JsonParser.parseUserFromString(expectedString);
        Assert.assertNotNull(user);
        Assert.assertEquals(1, user.profile.pinsStorage.size());
        DashPinStorageKey pinStorageKey = new DashPinStorageKey(0, 0, PinType.VIRTUAL, ((short) (0)));
        Assert.assertNotNull(user.profile.pinsStorage.get(pinStorageKey));
        Assert.assertEquals(0, ((MultiPinStorageValue) (user.profile.pinsStorage.get(pinStorageKey))).values.size());
    }
}

