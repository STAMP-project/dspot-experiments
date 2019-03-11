/**
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.persistence.typeHandling.gson;


import com.google.gson.Gson;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.persistence.typeHandling.extensionTypes.ColorTypeHandler;
import org.terasology.rendering.nui.Color;


public class GsonTypeHandlerAdapterTest {
    private static final String OBJECT_JSON_ARRAY = "{\"color\":[222,173,190,239],\"i\":-123}";

    private static final String OBJECT_JSON_HEX = "{\"color\":DEADBEEF,\"i\":-123}";

    private static final GsonTypeHandlerAdapterTest.TestClass OBJECT = new GsonTypeHandlerAdapterTest.TestClass(new Color(-559038737), (-123));

    private final Gson gson = GsonBuilderFactory.createGsonBuilderWithTypeHandlers(TypeHandlerEntry.of(Color.class, new ColorTypeHandler())).create();

    /**
     * {@link GsonTypeHandlerAdapter#read(JsonReader)} is tested by deserializing an object from JSON
     * via Gson with a registered {@link GsonTypeHandlerAdapterFactory} which creates instances of
     * {@link GsonTypeHandlerAdapter}.
     */
    @Test
    public void testRead() {
        // Deserialize object with color as JSON array
        GsonTypeHandlerAdapterTest.TestClass deserializedObject = gson.fromJson(GsonTypeHandlerAdapterTest.OBJECT_JSON_ARRAY, GsonTypeHandlerAdapterTest.TestClass.class);
        Assert.assertEquals(GsonTypeHandlerAdapterTest.OBJECT, deserializedObject);
        // Deserialize object with color as hex string
        deserializedObject = gson.fromJson(GsonTypeHandlerAdapterTest.OBJECT_JSON_HEX, GsonTypeHandlerAdapterTest.TestClass.class);
        Assert.assertEquals(GsonTypeHandlerAdapterTest.OBJECT, deserializedObject);
    }

    /**
     * {@link GsonTypeHandlerAdapter#write(JsonWriter, Object)} is tested by serializing an object to JSON
     * via Gson with a registered {@link GsonTypeHandlerAdapterFactory} which creates instances of
     * {@link GsonTypeHandlerAdapter}.
     */
    @Test
    public void testWrite() {
        String serializedObject = gson.toJson(GsonTypeHandlerAdapterTest.OBJECT);
        Assert.assertEquals(GsonTypeHandlerAdapterTest.OBJECT_JSON_ARRAY, serializedObject);
    }

    private static class TestClass {
        private final Color color;

        private final int i;

        private TestClass(Color color, int i) {
            this.color = color;
            this.i = i;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            GsonTypeHandlerAdapterTest.TestClass testClass = ((GsonTypeHandlerAdapterTest.TestClass) (o));
            return ((i) == (testClass.i)) && (Objects.equals(color, testClass.color));
        }
    }
}

