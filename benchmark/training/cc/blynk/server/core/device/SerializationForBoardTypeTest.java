package cc.blynk.server.core.device;


import BoardType.Arduino_UNO;
import BoardType.Generic_Board;
import JsonParser.MAPPER;
import cc.blynk.server.core.model.device.BoardType;
import org.junit.Assert;
import org.junit.Test;


public class SerializationForBoardTypeTest {
    @Test
    public void someTEst() throws Exception {
        Assert.assertEquals("\"Arduino UNO\"", MAPPER.writeValueAsString(Arduino_UNO));
        Assert.assertEquals(Arduino_UNO, MAPPER.readValue("\"Arduino UNO\"", BoardType.class));
    }

    @Test
    public void testUnknownProperty() throws Exception {
        Assert.assertEquals(Generic_Board, MAPPER.readValue("\"\"", BoardType.class));
        Assert.assertEquals(Generic_Board, MAPPER.readValue("\"aaaa\"", BoardType.class));
    }
}

