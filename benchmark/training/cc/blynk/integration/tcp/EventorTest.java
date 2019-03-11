package cc.blynk.integration.tcp;


import PinType.VIRTUAL;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.others.eventor.Eventor;
import cc.blynk.server.core.model.widgets.others.eventor.Rule;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.BaseAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPinAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPinActionType;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPropertyPinAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.ValueChanged;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.Between;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.GreaterThan;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.NotBetween;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.string.StringEqual;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.string.StringNotEqual;
import cc.blynk.server.notifications.push.android.AndroidGCMMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventorTest extends SingleServerInstancePerTest {
    @Test
    public void testSimpleRule1() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 > 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 38");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 38"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testInactiveEventsNotTriggered() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 > 37 then setpin v2 123", false);
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 38");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 38"));
        clientPair.hardwareClient.never(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.never(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testSimpleRule1AndDashUpdatedValue() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 > 37 then setpin v4 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 38");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 38"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 4 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 4 123"));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Assert.assertNotNull(profile);
        OnePinWidget widget = ((OnePinWidget) (profile.dashBoards[0].findWidgetByPin(0, ((short) (4)), VIRTUAL)));
        Assert.assertNotNull(widget);
        Assert.assertEquals("123", widget.value);
    }

    @Test
    public void testSimpleRule2() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 >= 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 37");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 37"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testSimpleRule3() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 <= 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 37");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 37"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testSimpleRule4() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 = 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 37");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 37"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testSimpleRule5() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 < 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 36");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 36"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testSimpleRule6() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 != 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 36");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 36"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testSimpleRule7() throws Exception {
        DataStream triggerDataStream = new DataStream(((short) (1)), PinType.VIRTUAL);
        DataStream dataStream = new DataStream(((short) (2)), PinType.VIRTUAL);
        SetPinAction setPinAction = new SetPinAction(dataStream, "123", SetPinActionType.CUSTOM);
        Rule rule = new Rule(triggerDataStream, null, new Between(10, 12), new BaseAction[]{ setPinAction }, true);
        Eventor eventor = new Eventor(new Rule[]{ rule });
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 11");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 11"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testSimpleRule8() throws Exception {
        DataStream triggerDataStream = new DataStream(((short) (1)), PinType.VIRTUAL);
        DataStream dataStream = new DataStream(((short) (2)), PinType.VIRTUAL);
        SetPinAction setPinAction = new SetPinAction(dataStream, "123", SetPinActionType.CUSTOM);
        Rule rule = new Rule(triggerDataStream, null, new NotBetween(10, 12), new BaseAction[]{ setPinAction }, true);
        Eventor eventor = new Eventor(new Rule[]{ rule });
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 9");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 9"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testSimpleRule8Notify() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 = 37 then notify Yo!!!!!");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 37");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 37"));
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(EventorTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testSimpleRule8NotifyAndFormat() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 = 37 then notify Temperatureis:/pin/.");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 37");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 37"));
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(EventorTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testSimpleRule9Twit() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 = 37 then twit Yo!!!!!");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 37");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 37"));
        Mockito.verify(EventorTest.holder.twitterWrapper, Mockito.timeout(500)).send(ArgumentMatchers.eq("token"), ArgumentMatchers.eq("secret"), ArgumentMatchers.eq("Yo!!!!!"), ArgumentMatchers.any());
    }

    @Test
    public void testSimpleRule8Email() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 = 37 then mail Yo!!!!!");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.createWidget(1, "{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
        clientPair.hardwareClient.send("hardware vw 1 37");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 37"));
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(EventorTest.holder.mailWrapper, Mockito.timeout(500).times(1)).sendText(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("Subj"), ArgumentMatchers.eq("Yo!!!!!"));
    }

    @Test
    public void testSimpleRule8EmailAndFormat() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 = 37 then mail Yo/pin/!!!!!");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.createWidget(1, "{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
        clientPair.hardwareClient.send("hardware vw 1 37");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 37"));
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(EventorTest.holder.mailWrapper, Mockito.timeout(500).times(1)).sendText(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("Subj"), ArgumentMatchers.eq("Yo37!!!!!"));
    }

    @Test
    public void testSimpleRuleCreateUpdateConditionWorks() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 >= 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 37");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 37"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
        eventor = EventorTest.oneRuleEventor("if v1 >= 37 then setpin v2 124");
        clientPair.appClient.updateWidget(1, eventor);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
        clientPair.hardwareClient.send("hardware vw 1 36");
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 1 36"));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 2 124"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 vw 2 124"))));
        clientPair.hardwareClient.send("hardware vw 1 37");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(3, HARDWARE, TestUtil.b("1-0 vw 1 37"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 2 124"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 vw 2 124"))));
    }

    @Test
    public void testPinModeForEventorAndSetPinAction() throws Exception {
        clientPair.appClient.activate(1);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("pm 1 out 2 out 3 out 5 out 6 in 7 in 30 in 8 in"))));
        Mockito.reset(clientPair.hardwareClient.responseMock);
        Eventor eventor = EventorTest.oneRuleEventor("if v1 > 37 then setpin d9 1");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.activate(1);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("pm 1 out 2 out 3 out 5 out 6 in 7 in 30 in 8 in 9 out"))));
        // reset(clientPair.hardwareClient.responseMock);
        clientPair.hardwareClient.send("hardware vw 1 38");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 38"));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("dw 9 1"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 dw 9 1"))));
    }

    @Test
    public void testTriggerOnlyOnceOnCondition() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 < 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 36");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 36"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
        clientPair.hardwareClient.reset();
        clientPair.appClient.reset();
        clientPair.hardwareClient.send("hardware vw 1 36");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 36"));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 2 123"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 vw 2 123"))));
        clientPair.hardwareClient.send("hardware vw 1 36");
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 1 36"));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 2 123"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 vw 2 123"))));
        clientPair.hardwareClient.send("hardware vw 1 38");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(3, HARDWARE, TestUtil.b("1-0 vw 1 38"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 2 123"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 vw 2 123"))));
        clientPair.hardwareClient.send("hardware vw 1 36");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(4, HARDWARE, TestUtil.b("1-0 vw 1 36"))));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testEventorWorksForMultipleHardware() throws Exception {
        TestHardClient hardClient = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        hardClient.login(clientPair.token);
        hardClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        Eventor eventor = EventorTest.oneRuleEventor("if v1 < 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 36");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 36"));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
        Mockito.verify(hardClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 2 123"))));
    }

    @Test
    public void testPinModeForPWMPinForEventorAndSetPinAction() throws Exception {
        clientPair.appClient.activate(1);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("pm 1 out 2 out 3 out 5 out 6 in 7 in 30 in 8 in"))));
        Mockito.reset(clientPair.hardwareClient.responseMock);
        Eventor eventor = EventorTest.oneRuleEventor("if v1 > 37 then setpin d9 255");
        // here is special case. right now eventor for digital pins supports only LOW/HIGH values
        // that's why eventor doesn't work with PWM pins, as they handled as analog, where HIGH doesn't work.
        SetPinAction setPinAction = ((SetPinAction) (eventor.rules[0].actions[0]));
        DataStream dataStream = setPinAction.dataStream;
        eventor.rules[0].actions[0] = new SetPinAction(new DataStream(dataStream.pin, true, false, dataStream.pinType, null, 0, 255, null), setPinAction.value, SetPinActionType.CUSTOM);
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.activate(1);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("pm 1 out 2 out 3 out 5 out 6 in 7 in 30 in 8 in 9 out"))));
        clientPair.hardwareClient.send("hardware vw 1 38");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 38"));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("aw 9 255"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 aw 9 255"))));
    }

    @Test
    public void testSimpleRule2WorksFromAppSide() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 >= 37 then setpin v2 123");
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("hardware 1-0 vw 1 37");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("vw 1 37"))));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testSimpleRuleWith2Actions() throws Exception {
        DataStream triggerDataStream = new DataStream(((short) (1)), PinType.VIRTUAL);
        Rule rule = new Rule(triggerDataStream, null, new GreaterThan(37), new BaseAction[]{ new SetPinAction(((short) (0)), PinType.VIRTUAL, "0"), new SetPinAction(((short) (1)), PinType.VIRTUAL, "1") }, true);
        Eventor eventor = new Eventor(new Rule[]{ rule });
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 38");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 38"));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 0 0"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 1 1"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 vw 0 0"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 vw 1 1"))));
    }

    @Test
    public void testEventorHasWrongDeviceId() throws Exception {
        Eventor eventor = EventorTest.oneRuleEventor("if v1 != 37 then setpin v2 123");
        eventor.deviceId = 1;
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 36");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 36"));
        clientPair.hardwareClient.never(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.never(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testStringEqualsRule() throws Exception {
        DataStream triggerStream = new DataStream(((short) (1)), PinType.VIRTUAL);
        SetPinAction setPinAction = new SetPinAction(new DataStream(((short) (2)), PinType.VIRTUAL), "123", SetPinActionType.CUSTOM);
        Eventor eventor = new Eventor(new Rule[]{ new Rule(triggerStream, null, new StringEqual("abc"), new BaseAction[]{ setPinAction }, true) });
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 abc");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 1 abc"))));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testValueChangedRule() throws Exception {
        DataStream triggerStream = new DataStream(((short) (1)), PinType.VIRTUAL);
        SetPinAction setPinAction = new SetPinAction(new DataStream(((short) (2)), PinType.VIRTUAL), "123", SetPinActionType.CUSTOM);
        Eventor eventor = new Eventor(new Rule[]{ new Rule(triggerStream, null, new ValueChanged("abc"), new BaseAction[]{ setPinAction }, true) });
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 changed");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 1 changed"))));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
        clientPair.hardwareClient.reset();
        clientPair.appClient.reset();
        clientPair.hardwareClient.send("hardware vw 1 changed");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 1 changed"))));
        clientPair.hardwareClient.never(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.never(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testStringNotEqualsRule() throws Exception {
        DataStream triggerStream = new DataStream(((short) (1)), PinType.VIRTUAL);
        SetPinAction setPinAction = new SetPinAction(new DataStream(((short) (2)), PinType.VIRTUAL), "123", SetPinActionType.CUSTOM);
        Eventor eventor = new Eventor(new Rule[]{ new Rule(triggerStream, null, new StringNotEqual("abc"), new BaseAction[]{ setPinAction }, true) });
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 ABC");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 1 ABC"))));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
    }

    @Test
    public void testStringEqualsRuleWrongTrigger() throws Exception {
        DataStream triggerStream = new DataStream(((short) (1)), PinType.VIRTUAL);
        SetPinAction setPinAction = new SetPinAction(new DataStream(((short) (2)), PinType.VIRTUAL), "123", SetPinActionType.CUSTOM);
        Eventor eventor = new Eventor(new Rule[]{ new Rule(triggerStream, null, new StringEqual("abc"), new BaseAction[]{ setPinAction }, true) });
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 1 ABC");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 1 ABC"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 2 123"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 vw 2 123"))));
    }

    @Test
    public void testSetWidgetPropertyViaEventor() throws Exception {
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Widget widget = profile.dashBoards[0].findWidgetByPin(0, ((short) (4)), VIRTUAL);
        Assert.assertNotNull(widget);
        Assert.assertEquals("Some Text", widget.label);
        DataStream triggerStream = new DataStream(((short) (43)), PinType.VIRTUAL);
        SetPropertyPinAction setPropertyPinAction = new SetPropertyPinAction(new DataStream(((short) (4)), PinType.VIRTUAL), WidgetProperty.LABEL, "MyNewLabel");
        Eventor eventor = new Eventor(new Rule[]{ new Rule(triggerStream, null, new StringEqual("abc"), new BaseAction[]{ setPropertyPinAction }, true) });
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.hardwareClient.send("hardware vw 43 abc");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 43 abc"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("vw 4 label MyNewLabel"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 4 label MyNewLabel"))));
        clientPair.appClient.verifyResult(produce(888, SET_WIDGET_PROPERTY, TestUtil.b("1-0 4 label MyNewLabel")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        widget = profile.dashBoards[0].findWidgetByPin(0, ((short) (4)), VIRTUAL);
        Assert.assertNotNull(widget);
        Assert.assertEquals("MyNewLabel", widget.label);
    }
}

