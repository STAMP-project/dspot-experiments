package cc.blynk.integration.tcp;


import JsonParser.MAPPER;
import ProvisionType.DYNAMIC;
import ProvisionType.STATIC;
import Theme.Blynk;
import Theme.BlynkLight;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.enums.ProvisionType;
import cc.blynk.server.core.model.enums.Theme;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppWorkflowTest extends SingleServerInstancePerTest {
    @Test
    public void testPrintApp() throws Exception {
        App app = new App("1", Theme.Blynk, ProvisionType.STATIC, 0, false, "My App", "myIcon", new int[]{ 1 });
        System.out.println(MAPPER.writeValueAsString(app));
    }

    @Test
    public void testAppCreated() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"isMultiFace\":true,\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        Assert.assertEquals(13, app.id.length());
        Assert.assertEquals(Blynk, app.theme);
        Assert.assertEquals(STATIC, app.provisionType);
        Assert.assertEquals(0, app.color);
        Assert.assertEquals("My App", app.name);
        Assert.assertEquals("myIcon", app.icon);
        Assert.assertTrue(app.isMultiFace);
        Assert.assertArrayEquals(new int[]{ 1 }, app.projectIds);
    }

    @Test
    public void testAppCreated2() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[2]}");
        app = clientPair.appClient.parseApp(2);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
    }

    @Test
    public void testUnicodeName() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"\u041c\u043e\u044f \u0430\u043f\u043a\u0430\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        Assert.assertEquals("??? ????", app.name);
    }

    @Test
    public void testCantCreateWithSameId() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.send((("createApp {\"id\":\"" + (app.id)) + "\",\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[2]}"));
        app = clientPair.appClient.parseApp(2);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
    }

    @Test
    public void testAppUpdated() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.send((("updateApp {\"id\":\"" + (app.id)) + "\",\"theme\":\"BlynkLight\",\"provisionType\":\"DYNAMIC\",\"color\":1,\"name\":\"My App 2\",\"icon\":\"myIcon2\",\"projectIds\":[1,2]}"));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(3);
        Assert.assertNotNull(profile);
        Assert.assertNotNull(profile.apps);
        Assert.assertEquals(1, profile.apps.length);
        App app2 = profile.apps[0];
        Assert.assertEquals(app.id, app2.id);
        Assert.assertEquals(BlynkLight, app2.theme);
        Assert.assertEquals(DYNAMIC, app2.provisionType);
        Assert.assertEquals(1, app2.color);
        Assert.assertEquals("My App 2", app2.name);
        Assert.assertEquals("myIcon2", app2.icon);
        Assert.assertArrayEquals(new int[]{ 1, 2 }, app2.projectIds);
    }

    @Test
    public void testAppDelete() throws Exception {
        clientPair.appClient.send("createApp {\"id\":1,\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.send(("deleteApp " + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Assert.assertNotNull(profile);
        Assert.assertNotNull(profile.apps);
        Assert.assertEquals(0, profile.apps.length);
        Assert.assertEquals(0, profile.dashBoards.length);
    }
}

