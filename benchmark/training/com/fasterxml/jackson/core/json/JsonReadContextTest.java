package com.fasterxml.jackson.core.json;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for class {@link JsonReadContext}.
 */
// extends BaseMapTest
public class JsonReadContextTest {
    @Test(expected = JsonParseException.class)
    public void testSetCurrentNameTwiceWithSameNameRaisesJsonParseException() throws JsonProcessingException {
        DupDetector dupDetector = DupDetector.rootDetector(((JsonGenerator) (null)));
        JsonReadContext jsonReadContext = new JsonReadContext(((JsonReadContext) (null)), dupDetector, 2441, 2441, 2441);
        jsonReadContext.setCurrentName("4'Du>icate field'");
        jsonReadContext.setCurrentName("4'Du>icate field'");
    }

    @Test
    public void testSetCurrentName() throws JsonProcessingException {
        JsonReadContext jsonReadContext = JsonReadContext.createRootContext(0, 0, ((DupDetector) (null)));
        jsonReadContext.setCurrentName("asd / \" \u20ac < - _");
        Assert.assertEquals("asd / \" \u20ac < - _", currentName());
        jsonReadContext.setCurrentName(null);
        Assert.assertNull(currentName());
    }

    @Test
    public void testReset() {
        DupDetector dupDetector = DupDetector.rootDetector(((JsonGenerator) (null)));
        JsonReadContext jsonReadContext = JsonReadContext.createRootContext(dupDetector);
        Assert.assertTrue(jsonReadContext.inRoot());
        Assert.assertEquals("root", jsonReadContext.typeDesc());
        Assert.assertEquals(1, jsonReadContext.getStartLocation(jsonReadContext).getLineNr());
        Assert.assertEquals(0, jsonReadContext.getStartLocation(jsonReadContext).getColumnNr());
        jsonReadContext.reset(200, 500, 200);
        Assert.assertFalse(jsonReadContext.inRoot());
        Assert.assertEquals("?", jsonReadContext.typeDesc());
        Assert.assertEquals(500, jsonReadContext.getStartLocation(jsonReadContext).getLineNr());
        Assert.assertEquals(200, jsonReadContext.getStartLocation(jsonReadContext).getColumnNr());
    }
}

