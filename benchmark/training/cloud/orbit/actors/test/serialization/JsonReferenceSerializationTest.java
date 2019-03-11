/**
 * Copyright (C) 2016 Electronic Arts Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2.  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
 * its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package cloud.orbit.actors.test.serialization;


import cloud.orbit.actors.Actor;
import cloud.orbit.actors.runtime.DefaultDescriptorFactory;
import cloud.orbit.actors.runtime.DescriptorFactory;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class JsonReferenceSerializationTest {
    private DescriptorFactory factory = DefaultDescriptorFactory.get();

    public static class RefHolder {
        JsonReferenceSerializationTest.SomeActor actor;
    }

    @Test
    public void testSerialize() throws Exception {
        String json = "{\"actor\":\"123\"}";
        JsonReferenceSerializationTest.SomeActor actor = factory.getReference(JsonReferenceSerializationTest.SomeActor.class, "123");
        JsonReferenceSerializationTest.RefHolder ref = new JsonReferenceSerializationTest.RefHolder();
        ref.actor = actor;
        ObjectMapper mapper = createMapper();
        Assert.assertEquals(json, mapper.writeValueAsString(ref));
    }

    @Test(expected = JsonMappingException.class)
    public void testInvalidSerialize() throws Exception {
        String json = "\"123\"";
        JsonReferenceSerializationTest.SomeActor actor = factory.getReference(JsonReferenceSerializationTest.SomeActor.class, "123");
        ObjectMapper mapper = createMapper();
        Assert.assertEquals(json, mapper.writeValueAsString(actor));
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "\"123\"";
        JsonReferenceSerializationTest.SomeActor actor = factory.getReference(JsonReferenceSerializationTest.SomeActor.class, "123");
        ObjectMapper mapper = createMapper();
        Assert.assertEquals(actor, mapper.readValue(json, JsonReferenceSerializationTest.SomeActor.class));
    }

    public static class ListHolder {
        List<JsonReferenceSerializationTest.SomeActor> actors;
    }

    @Test
    public void testList() throws Exception {
        String json = "{\"actors\":[\"1\",\"2\"]}";
        JsonReferenceSerializationTest.SomeActor actor1 = factory.getReference(JsonReferenceSerializationTest.SomeActor.class, "1");
        JsonReferenceSerializationTest.SomeActor actor2 = factory.getReference(JsonReferenceSerializationTest.SomeActor.class, "2");
        ObjectMapper mapper = createMapper();
        JsonReferenceSerializationTest.ListHolder holder = new JsonReferenceSerializationTest.ListHolder();
        holder.actors = Arrays.asList(actor1, actor2);
        String listJson = mapper.writeValueAsString(holder);
        Assert.assertEquals(json, listJson);
        Assert.assertEquals(holder.actors, mapper.readValue(listJson, JsonReferenceSerializationTest.ListHolder.class).actors);
    }

    @Test(expected = JsonMappingException.class)
    public void testInvalidList() throws Exception {
        JsonReferenceSerializationTest.SomeActor actor1 = factory.getReference(JsonReferenceSerializationTest.SomeActor.class, "1");
        JsonReferenceSerializationTest.SomeActor actor2 = factory.getReference(JsonReferenceSerializationTest.SomeActor.class, "2");
        ObjectMapper mapper = createMapper();
        final List<JsonReferenceSerializationTest.SomeActor> actors = Arrays.asList(actor1, actor2);
        mapper.writeValueAsString(actors);
    }

    public static class ComplexData {
        JsonReferenceSerializationTest.SomeActor a1;

        JsonReferenceSerializationTest.SomeActor a2;

        List<JsonReferenceSerializationTest.SomeActor> list;

        @Override
        public boolean equals(final Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof JsonReferenceSerializationTest.ComplexData))
                return false;

            final JsonReferenceSerializationTest.ComplexData that = ((JsonReferenceSerializationTest.ComplexData) (o));
            if (!(a1.equals(that.a1)))
                return false;

            if (!(a2.equals(that.a2)))
                return false;

            if (!(list.equals(that.list)))
                return false;

            return true;
        }
    }

    @Test
    public void testClassWithInnerValues() throws Exception {
        String json = "{\"a1\":\"1\",\"a2\":\"2\",\"list\":[\"1\",\"2\"]}";
        JsonReferenceSerializationTest.ComplexData data = new JsonReferenceSerializationTest.ComplexData();
        data.a1 = factory.getReference(JsonReferenceSerializationTest.SomeActor.class, "1");
        data.a2 = factory.getReference(JsonReferenceSerializationTest.SomeActor.class, "2");
        data.list = Arrays.asList(data.a1, data.a2);
        ObjectMapper mapper = createMapper();
        Assert.assertEquals(json, mapper.writeValueAsString(data));
        Assert.assertEquals(data, mapper.readValue(json, JsonReferenceSerializationTest.ComplexData.class));
    }

    public static class Data1 {
        public JsonReferenceSerializationTest.SomeActor ref;

        public Actor ref2;

        public JsonReferenceSerializationTest.SomeActorBaseActor ref3;

        public Set<JsonReferenceSerializationTest.SomeActor> set1;

        public Set<Actor> set2;

        public Set<JsonReferenceSerializationTest.SomeActorBaseActor> set3;
    }

    public static class Data2 {
        public Object ref;
    }

    public interface SomeActorBaseActor extends Actor {}

    public interface SomeActor extends Actor , JsonReferenceSerializationTest.SomeActorBaseActor {}

    @Test
    public void testMultipleTypesOfRef() throws IOException {
        final JsonReferenceSerializationTest.Data1 data = new JsonReferenceSerializationTest.Data1();
        final JsonReferenceSerializationTest.SomeActor ref = Actor.getReference(JsonReferenceSerializationTest.SomeActor.class, "a");
        data.ref = ref;
        data.ref2 = ref;
        data.ref3 = ref;
        data.set1 = Collections.singleton(ref);
        data.set2 = Collections.singleton(ref);
        data.set3 = Collections.singleton(ref);
        final ObjectMapper mapper = createMapper();
        final String str = mapper.writeValueAsString(data);
        final JsonReferenceSerializationTest.Data1 data2 = mapper.readValue(str, JsonReferenceSerializationTest.Data1.class);
        Assert.assertEquals(data.ref, data2.ref);
        Assert.assertEquals(data.ref2, data2.ref2);
        Assert.assertEquals(data.ref3, data2.ref3);
        Assert.assertEquals(data.set1, data2.set1);
        Assert.assertEquals(data.set2, data2.set2);
        Assert.assertEquals(data.set3, data2.set3);
        final Map raw = mapper.readValue(str, LinkedHashMap.class);
        Assert.assertEquals("a", raw.get("ref").toString());
        Assert.assertEquals("!!cloud.orbit.actors.test.serialization.JsonReferenceSerializationTest$SomeActor a", raw.get("ref2").toString());
        Assert.assertEquals("!!cloud.orbit.actors.test.serialization.JsonReferenceSerializationTest$SomeActor a", raw.get("ref3").toString());
        Assert.assertEquals("[a]", raw.get("set1").toString());
        Assert.assertEquals("[!!cloud.orbit.actors.test.serialization.JsonReferenceSerializationTest$SomeActor a]", raw.get("set2").toString());
        Assert.assertEquals("[!!cloud.orbit.actors.test.serialization.JsonReferenceSerializationTest$SomeActor a]", raw.get("set3").toString());
    }

    @Test(expected = JsonMappingException.class)
    public void testIllegalRef() throws IOException {
        final JsonReferenceSerializationTest.Data2 data = new JsonReferenceSerializationTest.Data2();
        final JsonReferenceSerializationTest.SomeActor ref = Actor.getReference(JsonReferenceSerializationTest.SomeActor.class, "a");
        data.ref = ref;
        final ObjectMapper mapper = createMapper();
        final String str = mapper.writeValueAsString(data);
        final JsonReferenceSerializationTest.Data2 data2 = mapper.readValue(str, JsonReferenceSerializationTest.Data2.class);
        Assert.assertEquals(data.ref, data2.ref);
    }
}

