package cc.blynk.server.core.model.widgets.others;


import JsonParser.MAPPER;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.others.rtc.RTC;
import cc.blynk.utils.DateTimeUtils;
import java.time.ZoneId;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 30.03.16.
 */
public class RTCSerializationTest {
    @Test
    public void testDeSerializationIsCorrect() {
        String widgetString = "{\"id\":1, \"x\":1, \"y\":1, \"type\":\"RTC\", \"tzName\":\"Australia/Sydney\"}";
        Widget widget = JsonParser.parseWidget(widgetString, 0);
        Assert.assertNotNull(widget);
        RTC rtc = ((RTC) (widget));
        Assert.assertNotNull(rtc.tzName);
        Assert.assertEquals(ZoneId.of("Australia/Sydney"), rtc.tzName);
    }

    @Test
    public void unsupportedTimeZoneForKnownLocationHanoiTest() {
        String widgetString = "{\"id\":1, \"x\":1, \"y\":1, \"type\":\"RTC\", \"tzName\":\"Asia/Hanoi\"}";
        Widget widget = JsonParser.parseWidget(widgetString, 0);
        Assert.assertNotNull(widget);
        RTC rtc = ((RTC) (widget));
        Assert.assertNotNull(rtc.tzName);
        Assert.assertEquals(ZoneId.of("Asia/Ho_Chi_Minh"), rtc.tzName);
    }

    @Test
    public void unsupportedTimeZoneTest() {
        String widgetString = "{\"id\":1, \"x\":1, \"y\":1, \"type\":\"RTC\", \"tzName\":\"Canada/East-xxx\"}";
        RTC rtc = ((RTC) (JsonParser.parseWidget(widgetString, 0)));
        Assert.assertNotNull(rtc);
        Assert.assertEquals(ZoneId.of("UTC"), rtc.tzName);
    }

    @Test
    public void testDeSerializationIsCorrectForNull() {
        String widgetString = "{\"id\":1, \"x\":1, \"y\":1, \"type\":\"RTC\"}";
        Widget widget = JsonParser.parseWidget(widgetString, 0);
        Assert.assertNotNull(widget);
        RTC rtc = ((RTC) (widget));
        Assert.assertNull(rtc.tzName);
    }

    @Test
    public void testSerializationIsCorrect() throws Exception {
        RTC rtc = new RTC();
        rtc.tzName = ZoneId.of("Australia/Sydney");
        String widgetString = MAPPER.writeValueAsString(rtc);
        Assert.assertNotNull(widgetString);
        Assert.assertEquals("{\"type\":\"RTC\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isDefaultColor\":false,\"tzName\":\"Australia/Sydney\"}", widgetString);
    }

    @Test
    public void testSerializationIsCorrectUTC() throws Exception {
        RTC rtc = new RTC();
        rtc.tzName = DateTimeUtils.UTC;
        String widgetString = MAPPER.writeValueAsString(rtc);
        Assert.assertNotNull(widgetString);
        Assert.assertEquals("{\"type\":\"RTC\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isDefaultColor\":false,\"tzName\":\"UTC\"}", widgetString);
    }

    @Test
    public void testSerializationIsCorrectForNull() throws Exception {
        RTC rtc = new RTC();
        rtc.tzName = null;
        String widgetString = MAPPER.writeValueAsString(rtc);
        Assert.assertNotNull(widgetString);
        Assert.assertEquals("{\"type\":\"RTC\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isDefaultColor\":false}", widgetString);
    }
}

