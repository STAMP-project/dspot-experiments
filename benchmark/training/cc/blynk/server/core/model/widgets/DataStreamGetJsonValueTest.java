package cc.blynk.server.core.model.widgets;


import StringUtils.BODY_SEPARATOR_STRING;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.widgets.controls.RGB;
import cc.blynk.server.core.model.widgets.outputs.ValueDisplay;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.12.15.
 */
public class DataStreamGetJsonValueTest {
    @Test
    public void testSinglePin() {
        OnePinWidget onePinWidget = new ValueDisplay();
        onePinWidget.value = null;
        Assert.assertEquals("[]", onePinWidget.getJsonValue());
        onePinWidget.value = "1.0";
        Assert.assertEquals("[\"1.0\"]", onePinWidget.getJsonValue());
    }

    @Test
    public void testMultiPinSplit() {
        RGB multiPinWidget = new RGB();
        multiPinWidget.dataStreams = null;
        multiPinWidget.splitMode = true;
        Assert.assertEquals("[]", multiPinWidget.getJsonValue());
        multiPinWidget.dataStreams = new DataStream[3];
        multiPinWidget.dataStreams[0] = DataStreamGetJsonValueTest.createPinWithValue("1");
        multiPinWidget.dataStreams[1] = DataStreamGetJsonValueTest.createPinWithValue("2");
        multiPinWidget.dataStreams[2] = DataStreamGetJsonValueTest.createPinWithValue("3");
        Assert.assertEquals("[\"1\",\"2\",\"3\"]", multiPinWidget.getJsonValue());
    }

    @Test
    public void testMultiPinMerge() {
        RGB multiPinWidget = new RGB();
        multiPinWidget.dataStreams = null;
        multiPinWidget.splitMode = false;
        Assert.assertEquals("[]", multiPinWidget.getJsonValue());
        multiPinWidget.dataStreams = new DataStream[3];
        multiPinWidget.dataStreams[0] = DataStreamGetJsonValueTest.createPinWithValue("1 2 3".replaceAll(" ", BODY_SEPARATOR_STRING));
        Assert.assertEquals("[\"1\",\"2\",\"3\"]", multiPinWidget.getJsonValue());
    }
}

