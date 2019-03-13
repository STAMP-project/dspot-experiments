package cc.blynk.server.api.http.pojo;


import JsonParser.MAPPER;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31.08.16.
 */
public class TestDataStreamDataJson {
    @Test
    public void testParseString() throws Exception {
        String pinDataString = "[{\"timestamp\" : 123, \"value\":\"100\"}]";
        PinData[] pinData = MAPPER.readValue(pinDataString, PinData[].class);
        Assert.assertNotNull(pinData);
    }
}

