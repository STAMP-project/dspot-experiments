package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public class AmplCustomTypeAdaptersTest extends TestCase {
    private GsonBuilder builder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        builder = new GsonBuilder();
    }

    public void disable_testCustomSerializersOfSelf() {
        Gson gson = createGsonObjectWithFooTypeAdapter();
        Gson basicGson = new Gson();
        AmplCustomTypeAdaptersTest.Foo newFooObject = new AmplCustomTypeAdaptersTest.Foo(1, 2L);
        String jsonFromCustomSerializer = gson.toJson(newFooObject);
        String jsonFromGson = basicGson.toJson(newFooObject);
        TestCase.assertEquals(jsonFromGson, jsonFromCustomSerializer);
    }

    public void disable_testCustomDeserializersOfSelf() {
        Gson gson = createGsonObjectWithFooTypeAdapter();
        Gson basicGson = new Gson();
        AmplCustomTypeAdaptersTest.Foo expectedFoo = new AmplCustomTypeAdaptersTest.Foo(1, 2L);
        String json = basicGson.toJson(expectedFoo);
        AmplCustomTypeAdaptersTest.Foo newFooObject = gson.fromJson(json, AmplCustomTypeAdaptersTest.Foo.class);
        TestCase.assertEquals(expectedFoo.key, newFooObject.key);
        TestCase.assertEquals(expectedFoo.value, newFooObject.value);
    }

    private static class Base {
        int baseValue = 2;
    }

    private static class Derived extends AmplCustomTypeAdaptersTest.Base {
        @SuppressWarnings("unused")
        int derivedValue = 3;
    }

    private Gson createGsonObjectWithFooTypeAdapter() {
        return new GsonBuilder().registerTypeAdapter(AmplCustomTypeAdaptersTest.Foo.class, new AmplCustomTypeAdaptersTest.FooTypeAdapter()).create();
    }

    public static class Foo {
        private final int key;

        private final long value;

        public Foo() {
            this(0, 0L);
        }

        public Foo(int key, long value) {
            this.key = key;
            this.value = value;
        }
    }

    public static final class FooTypeAdapter implements JsonDeserializer<AmplCustomTypeAdaptersTest.Foo> , JsonSerializer<AmplCustomTypeAdaptersTest.Foo> {
        @Override
        public AmplCustomTypeAdaptersTest.Foo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return context.deserialize(json, typeOfT);
        }

        @Override
        public JsonElement serialize(AmplCustomTypeAdaptersTest.Foo src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src, typeOfSrc);
        }
    }

    private static final class StringHolder {
        String part1;

        String part2;

        public StringHolder(String string) {
            String[] parts = string.split(":");
            part1 = parts[0];
            part2 = parts[1];
        }

        public StringHolder(String part1, String part2) {
            this.part1 = part1;
            this.part2 = part2;
        }
    }

    private static class StringHolderTypeAdapter implements InstanceCreator<AmplCustomTypeAdaptersTest.StringHolder> , JsonDeserializer<AmplCustomTypeAdaptersTest.StringHolder> , JsonSerializer<AmplCustomTypeAdaptersTest.StringHolder> {
        @Override
        public AmplCustomTypeAdaptersTest.StringHolder createInstance(Type type) {
            return new AmplCustomTypeAdaptersTest.StringHolder("unknown:thing");
        }

        @Override
        public AmplCustomTypeAdaptersTest.StringHolder deserialize(JsonElement src, Type type, JsonDeserializationContext context) {
            return new AmplCustomTypeAdaptersTest.StringHolder(src.getAsString());
        }

        @Override
        public JsonElement serialize(AmplCustomTypeAdaptersTest.StringHolder src, Type typeOfSrc, JsonSerializationContext context) {
            String contents = ((src.part1) + ':') + (src.part2);
            return new JsonPrimitive(contents);
        }
    }

    public void testCustomAdapterInvokedForCollectionElementSerialization_add6956null7722() throws Exception {
        Gson gson = new GsonBuilder().registerTypeAdapter(AmplCustomTypeAdaptersTest.StringHolder.class, new AmplCustomTypeAdaptersTest.StringHolderTypeAdapter()).create();
        AmplCustomTypeAdaptersTest.StringHolder holder = new AmplCustomTypeAdaptersTest.StringHolder("Jacob", "Tomaw");
        Set<AmplCustomTypeAdaptersTest.StringHolder> setOfHolders = new HashSet<AmplCustomTypeAdaptersTest.StringHolder>();
        boolean o_testCustomAdapterInvokedForCollectionElementSerialization_add6956__10 = setOfHolders.add(holder);
        boolean o_testCustomAdapterInvokedForCollectionElementSerialization_add6956__11 = setOfHolders.add(null);
        String json = gson.toJson(setOfHolders);
        TestCase.assertEquals("[null,\"Jacob:Tomaw\"]", json);
        boolean o_testCustomAdapterInvokedForCollectionElementSerialization_add6956__14 = json.contains("Jacob:Tomaw");
        TestCase.assertEquals("[null,\"Jacob:Tomaw\"]", json);
    }

    private static class DataHolder {
        final String data;

        public DataHolder(String data) {
            this.data = data;
        }
    }

    private static class DataHolderWrapper {
        final AmplCustomTypeAdaptersTest.DataHolder wrappedData;

        public DataHolderWrapper(AmplCustomTypeAdaptersTest.DataHolder data) {
            this.wrappedData = data;
        }
    }

    private static class DataHolderSerializer implements JsonSerializer<AmplCustomTypeAdaptersTest.DataHolder> {
        @Override
        public JsonElement serialize(AmplCustomTypeAdaptersTest.DataHolder src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("myData", src.data);
            return obj;
        }
    }

    private static class DataHolderDeserializer implements JsonDeserializer<AmplCustomTypeAdaptersTest.DataHolder> {
        @Override
        public AmplCustomTypeAdaptersTest.DataHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            JsonElement jsonElement = jsonObj.get("data");
            if ((jsonElement == null) || (jsonElement.isJsonNull())) {
                return new AmplCustomTypeAdaptersTest.DataHolder(null);
            }
            return new AmplCustomTypeAdaptersTest.DataHolder(jsonElement.getAsString());
        }
    }

    private static class DateTypeAdapter implements JsonDeserializer<Date> , JsonSerializer<Date> {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return typeOfT == (Date.class) ? new Date(json.getAsLong()) : new java.sql.Date(json.getAsLong());
        }

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime());
        }
    }
}

