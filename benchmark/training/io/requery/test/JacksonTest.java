/**
 * Copyright 2017 requery.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.requery.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import io.requery.test.model3.Event;
import io.requery.test.model3.LocationEntity;
import io.requery.test.model3.Models;
import io.requery.test.model3.Place;
import io.requery.test.model3.Tag;
import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class JacksonTest {
    private EntityDataStore<Persistable> data;

    @Test
    public void testOneToManySerialize() {
        Event event = new Event();
        UUID id = UUID.randomUUID();
        event.setId(id);
        event.setName("test");
        Tag t1 = new Tag();
        t1.setId(UUID.randomUUID());
        Tag t2 = new Tag();
        t2.setId(UUID.randomUUID());
        event.getTags().add(t1);
        event.getTags().add(t2);
        Place p = new Place();
        p.setId("SF");
        p.setName("San Francisco, CA");
        event.setPlace(p);
        data.insert(event);
        ObjectMapper mapper = new io.requery.jackson.EntityMapper(Models.MODEL3, data);
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String value = writer.toString();
        System.out.println(value);
        try {
            Event read = mapper.readValue(value, Event.class);
            Assert.assertSame(event, read);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testManyToManySerialize() {
        Tag t1 = new Tag();
        t1.setId(UUID.randomUUID());
        for (int i = 0; i < 3; i++) {
            Event event = new Event();
            UUID id = UUID.randomUUID();
            event.setId(id);
            event.setName(("event" + i));
            t1.getEvents().add(event);
        }
        data.insert(t1);
        ObjectMapper mapper = new io.requery.jackson.EntityMapper(Models.MODEL3, data);
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, t1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String value = writer.toString();
        System.out.println(value);
        try {
            Tag tag = mapper.readValue(value, Tag.class);
            Assert.assertSame(t1, tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEmbedSerialize() {
        LocationEntity t1 = new LocationEntity();
        t1.setId(1);
        data.insert(t1);
        ObjectMapper mapper = new io.requery.jackson.EntityMapper(Models.MODEL3, data);
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, t1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String value = writer.toString();
        System.out.println(value);
        try {
            LocationEntity location = mapper.readValue(value, LocationEntity.class);
            Assert.assertSame(t1, location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ValueInstantiator s;
}

