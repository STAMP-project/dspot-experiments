package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.common.TestTypes;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


/**
 * Functional tests for {@link Gson#toJsonTree(Object)} and
 * {@link Gson#toJsonTree(Object, java.lang.reflect.Type)}
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class JsonTreeTest extends TestCase {
    private Gson gson;

    public void testToJsonTree() {
        TestTypes.BagOfPrimitives bag = new TestTypes.BagOfPrimitives(10L, 5, false, "foo");
        JsonElement json = gson.toJsonTree(bag);
        TestCase.assertTrue(json.isJsonObject());
        JsonObject obj = json.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> children = obj.entrySet();
        TestCase.assertEquals(4, children.size());
        assertContains(obj, new JsonPrimitive(10L));
        assertContains(obj, new JsonPrimitive(5));
        assertContains(obj, new JsonPrimitive(false));
        assertContains(obj, new JsonPrimitive("foo"));
    }

    public void testToJsonTreeObjectType() {
        JsonTreeTest.SubTypeOfBagOfPrimitives bag = new JsonTreeTest.SubTypeOfBagOfPrimitives(10L, 5, false, "foo", 1.4F);
        JsonElement json = gson.toJsonTree(bag, TestTypes.BagOfPrimitives.class);
        TestCase.assertTrue(json.isJsonObject());
        JsonObject obj = json.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> children = obj.entrySet();
        TestCase.assertEquals(4, children.size());
        assertContains(obj, new JsonPrimitive(10L));
        assertContains(obj, new JsonPrimitive(5));
        assertContains(obj, new JsonPrimitive(false));
        assertContains(obj, new JsonPrimitive("foo"));
    }

    public void testJsonTreeToString() {
        JsonTreeTest.SubTypeOfBagOfPrimitives bag = new JsonTreeTest.SubTypeOfBagOfPrimitives(10L, 5, false, "foo", 1.4F);
        String json1 = gson.toJson(bag);
        JsonElement jsonElement = gson.toJsonTree(bag, JsonTreeTest.SubTypeOfBagOfPrimitives.class);
        String json2 = gson.toJson(jsonElement);
        TestCase.assertEquals(json1, json2);
    }

    public void testJsonTreeNull() {
        TestTypes.BagOfPrimitives bag = new TestTypes.BagOfPrimitives(10L, 5, false, null);
        JsonObject jsonElement = ((JsonObject) (gson.toJsonTree(bag, TestTypes.BagOfPrimitives.class)));
        TestCase.assertFalse(jsonElement.has("stringValue"));
    }

    private static class SubTypeOfBagOfPrimitives extends TestTypes.BagOfPrimitives {
        @SuppressWarnings("unused")
        float f = 1.2F;

        public SubTypeOfBagOfPrimitives(long l, int i, boolean b, String string, float f) {
            super(l, i, b, string);
            this.f = f;
        }
    }
}

