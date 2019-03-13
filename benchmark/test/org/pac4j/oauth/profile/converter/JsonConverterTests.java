package org.pac4j.oauth.profile.converter;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.facebook.FacebookObject;


/**
 * Tests the {@link JsonConverter}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class JsonConverterTests implements TestsConstants {
    private static final JsonConverter OBJECT_CONVERTER = new JsonConverter(FacebookObject.class);

    private static final JsonConverter LIST_CONVERTER = new JsonConverter(List.class, new TypeReference<List<FacebookObject>>() {});

    private static final String ONE_JSON = "{ \"id\": \"x\", \"name\": \"y\" }";

    private static final String JSON = ("[" + (JsonConverterTests.ONE_JSON)) + "]";

    @Test
    public void testNull() {
        Assert.assertNull(JsonConverterTests.OBJECT_CONVERTER.convert(null));
        Assert.assertNull(JsonConverterTests.LIST_CONVERTER.convert(null));
    }

    @Test
    public void testBadType() {
        Assert.assertNull(JsonConverterTests.OBJECT_CONVERTER.convert(1));
        Assert.assertNull(JsonConverterTests.LIST_CONVERTER.convert(1));
    }

    @Test
    public void testString() {
        final FacebookObject object = ((FacebookObject) (JsonConverterTests.OBJECT_CONVERTER.convert(JsonConverterTests.ONE_JSON)));
        Assert.assertNotNull(object);
        Assert.assertEquals("x", object.getId());
        Assert.assertEquals("y", object.getName());
    }

    @Test
    public void testJsonNode() {
        final JsonNode node = JsonHelper.getFirstNode(JsonConverterTests.ONE_JSON);
        final FacebookObject object = ((FacebookObject) (JsonConverterTests.OBJECT_CONVERTER.convert(node)));
        Assert.assertNotNull(object);
        Assert.assertEquals("x", object.getId());
        Assert.assertEquals("y", object.getName());
    }

    @Test
    public void testObject() {
        Assert.assertNotNull(JsonConverterTests.OBJECT_CONVERTER.convert(new FacebookObject()));
    }

    @Test
    public void testStringForListConverter() {
        final List<FacebookObject> objects = ((List<FacebookObject>) (JsonConverterTests.LIST_CONVERTER.convert(JsonConverterTests.JSON)));
        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        final FacebookObject object = objects.get(0);
        Assert.assertEquals("x", object.getId());
        Assert.assertEquals("y", object.getName());
    }

    @Test
    public void testJsonNodeForListConverter() {
        final JsonNode node = JsonHelper.getFirstNode(JsonConverterTests.JSON);
        final List<FacebookObject> objects = ((List<FacebookObject>) (JsonConverterTests.LIST_CONVERTER.convert(node)));
        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        final FacebookObject object = objects.get(0);
        Assert.assertEquals("x", object.getId());
        Assert.assertEquals("y", object.getName());
    }

    @Test
    public void testListObjectForListConverter() {
        final List<FacebookObject> list = new ArrayList<>();
        list.add(new FacebookObject());
        final List<FacebookObject> objects = ((List<FacebookObject>) (JsonConverterTests.LIST_CONVERTER.convert(list)));
        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertNotNull(objects.get(0));
    }
}

