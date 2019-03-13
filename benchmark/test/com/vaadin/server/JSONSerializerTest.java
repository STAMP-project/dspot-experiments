package com.vaadin.server;


import com.vaadin.shared.communication.UidlValue;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonException;
import elemental.json.JsonValue;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link JsonCodec}
 *
 * @author Vaadin Ltd
 * @since 7.0
 */
public class JSONSerializerTest {
    Map<String, AbstractSplitPanelState> stringToStateMap;

    Map<AbstractSplitPanelState, String> stateToStringMap;

    @Test
    public void testStringToBeanMapSerialization() throws Exception {
        Type mapType = getClass().getDeclaredField("stringToStateMap").getGenericType();
        stringToStateMap = new HashMap();
        AbstractSplitPanelState s = new AbstractSplitPanelState();
        AbstractSplitPanelState s2 = new AbstractSplitPanelState();
        s.caption = "State 1";
        s.id = "foo";
        s2.caption = "State 2";
        s2.id = "bar";
        stringToStateMap.put("string - state 1", s);
        stringToStateMap.put("String - state 2", s2);
        JsonValue encodedMap = JsonCodec.encode(stringToStateMap, null, mapType, null).getEncodedValue();
        ensureDecodedCorrectly(stringToStateMap, encodedMap, mapType);
    }

    @Test
    public void testBeanToStringMapSerialization() throws Exception {
        Type mapType = getClass().getDeclaredField("stateToStringMap").getGenericType();
        stateToStringMap = new HashMap();
        AbstractSplitPanelState s = new AbstractSplitPanelState();
        AbstractSplitPanelState s2 = new AbstractSplitPanelState();
        s.caption = "State 1";
        s2.caption = "State 2";
        stateToStringMap.put(s, "string - state 1");
        stateToStringMap.put(s2, "String - state 2");
        JsonValue encodedMap = JsonCodec.encode(stateToStringMap, null, mapType, null).getEncodedValue();
        ensureDecodedCorrectly(stateToStringMap, encodedMap, mapType);
    }

    @Test
    public void testNullLegacyValue() throws JsonException {
        JsonArray inputArray = Json.createArray();
        inputArray.set(0, "n");
        inputArray.set(1, Json.createNull());
        UidlValue decodedObject = ((UidlValue) (JsonCodec.decodeInternalType(UidlValue.class, true, inputArray, null)));
        Assert.assertNull(decodedObject.getValue());
    }

    @Test(expected = JsonException.class)
    public void testNullTypeOtherValue() {
        JsonArray inputArray = Json.createArray();
        inputArray.set(0, "n");
        inputArray.set(1, "a");
        UidlValue decodedObject = ((UidlValue) (JsonCodec.decodeInternalType(UidlValue.class, true, inputArray, null)));
    }
}

