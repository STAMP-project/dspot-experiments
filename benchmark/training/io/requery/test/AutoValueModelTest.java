/**
 * Copyright 2016 requery.io
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


import PersonType.ID;
import PersonType.NAME;
import PhoneType.NORMALIZED;
import PhoneType.OWNER_ID;
import PhoneType.PHONE_NUMBER;
import io.requery.query.Result;
import io.requery.query.Tuple;
import io.requery.sql.EntityDataStore;
import io.requery.test.autovalue.Person;
import io.requery.test.autovalue.Phone;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class AutoValueModelTest {
    protected EntityDataStore<Object> data;

    @Test
    public void testSelectAll() {
        List<Integer> added = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            added.add(randomPerson());
        }
        Result<Person> result = data.select(Person.class).get();
        for (Person p : result) {
            Assert.assertTrue(added.contains(p.getId()));
        }
    }

    @Test
    public void testInsert() {
        Set<Integer> ids = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Person p = Person.builder().setName(("person" + i)).setAge(30).setEmail("test@example.com").setUUID(UUID.randomUUID()).setBirthday(new Date()).setAbout("About me").build();
            Integer key = data.insert(p, Integer.class);
            Assert.assertTrue((key > 0));
            ids.add(key);
        }
        final Set<Integer> selected = new HashSet<>();
        data.select(ID).get().each(new io.requery.util.function.Consumer<Tuple>() {
            @Override
            public void accept(Tuple tuple) {
                selected.add(tuple.get(ID));
            }
        });
        Assert.assertEquals(ids, selected);
    }

    @Test
    public void testUpdate() {
        Person person = Person.builder().setName("Bob").setAge(30).setEmail("test@example.com").setUUID(UUID.randomUUID()).setBirthday(new Date()).setAbout("About me").build();
        Integer key = data.insert(person, Integer.class);
        Person renamed = person.toBuilder().setId(key).setName("Bobby").build();
        data.update(renamed);
        person = data.findByKey(Person.class, key);
        Assert.assertTrue(person.getName().equals("Bobby"));
    }

    @Test
    public void testDelete() throws MalformedURLException {
        Integer key = randomPerson();
        Person p = data.findByKey(Person.class, key);
        Assert.assertNotNull(p);
        data.delete(p);
        p = data.findByKey(Person.class, key);
        Assert.assertNull(p);
    }

    @Test
    public void testRefresh() throws MalformedURLException {
        Integer key = randomPerson();
        Integer count = data.update(Person.class).set(NAME, "Unknown").get().value();
        Assert.assertTrue((count > 0));
        Person p = data.findByKey(Person.class, key);
        data.refresh(p);
        Assert.assertEquals(p.getName(), "Unknown");
    }

    @Test
    public void testInsertReference() {
        randomPerson();
        Result<Person> result = data.select(Person.class).get();
        Person person = result.first();
        int id = data.insert(Phone.class).value(PHONE_NUMBER, "5555555").value(NORMALIZED, false).value(OWNER_ID, person.getId()).get().first().get(PhoneType.ID);
        Assert.assertTrue((id != 0));
        Phone phone = data.select(Phone.class).get().first();
        Assert.assertSame(phone.getOwnerId(), person.getId());
    }
}

