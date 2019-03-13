package com.baeldung.jackson.xmlToJson;


import Color.RED;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class XmlToJsonUnitTest {
    @Test
    public void givenAnXML_whenUseDataBidingToConvertToJSON_thenReturnDataOK() throws IOException {
        String flowerXML = "<Flower><name>Poppy</name><color>RED</color><petals>9</petals></Flower>";
        XmlMapper xmlMapper = new XmlMapper();
        Flower poppy = xmlMapper.readValue(flowerXML, Flower.class);
        Assert.assertEquals(poppy.getName(), "Poppy");
        Assert.assertEquals(poppy.getColor(), RED);
        Assert.assertEquals(poppy.getPetals(), new Integer(9));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(poppy);
        Assert.assertEquals(json, "{\"name\":\"Poppy\",\"color\":\"RED\",\"petals\":9}");
    }

    @Test
    public void givenAnXML_whenUseATreeConvertToJSON_thenReturnDataOK() throws IOException {
        String flowerXML = "<Flower><name>Poppy</name><color>RED</color><petals>9</petals></Flower>";
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(flowerXML.getBytes());
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(node);
        Assert.assertEquals(json, "{\"name\":\"Poppy\",\"color\":\"RED\",\"petals\":\"9\"}");
    }
}

