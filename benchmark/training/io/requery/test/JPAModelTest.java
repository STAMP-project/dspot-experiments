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


import AddressType.HOME;
import GroupType.PRIVATE;
import PersonEntity.GROUPS;
import io.requery.query.Result;
import io.requery.sql.EntityDataStore;
import io.requery.test.jpa.AddressEntity;
import io.requery.test.jpa.Group;
import io.requery.test.jpa.GroupEntity;
import io.requery.test.jpa.Person;
import io.requery.test.jpa.PersonEntity;
import io.requery.test.jpa.PhoneEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JPAModelTest {
    protected EntityDataStore<Serializable> data;

    @Test
    public void testInsertManyToMany() {
        PersonEntity person = JPAModelTest.randomPerson();
        data.insert(person);
        Assert.assertTrue(person.getGroups().toList().isEmpty());
        List<Group> added = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GroupEntity group = new GroupEntity();
            group.setName(("Group" + i));
            group.setDescription("Some description");
            group.setType(PRIVATE);
            data.insert(group);
            person.getGroups().add(group);
            added.add(group);
        }
        data.update(person);
        data.refresh(person, GROUPS);
        Assert.assertTrue(added.containsAll(person.getGroups().toList()));
    }

    @Test
    public void testDeleteManyToMany() {
        final PersonEntity person = JPAModelTest.randomPerson();
        data.insert(person);
        final Collection<Group> groups = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GroupEntity group = new GroupEntity();
            group.setName(("DeleteGroup" + i));
            data.insert(group);
            person.getGroups().add(group);
            groups.add(group);
        }
        data.update(person);
        for (Group g : groups) {
            person.getGroups().remove(g);
        }
        data.update(person);
    }

    @Test
    public void testInsertOneToMany() {
        PersonEntity person = JPAModelTest.randomPerson();
        PhoneEntity phone = new PhoneEntity();
        phone.setPhoneNumber("+1800123456");
        phone.setOwner(person);
        person.getPhoneNumbers().add(phone);
        data.insert(person);
    }

    @Test
    public void testSingleQueryExecute() {
        data.insert(JPAModelTest.randomPerson());
        Result<Person> result = data.select(Person.class).get();
        Assert.assertEquals(1, result.toList().size());
        PersonEntity person = JPAModelTest.randomPerson();
        data.insert(person);
        Assert.assertEquals(2, result.toList().size());
    }

    @Test
    public void testGroupWithOptionalDescription() {
        GroupEntity group = new GroupEntity();
        group.setName("group1");
        Assert.assertNotNull(group.getDescription());
        Assert.assertFalse(group.getDescription().isPresent());
        group.setDescription("text");
        data.insert(group);
        Assert.assertEquals("text", group.getDescription().get());
    }

    @Test
    public void testInsertEmbedded() {
        PersonEntity person = JPAModelTest.randomPerson();
        AddressEntity address = new AddressEntity();
        address.setLine1("Market St");
        address.setCity("San Francisco");
        address.setState("California");
        address.getCoordinate().setLatitude(37.7749F);
        address.getCoordinate().setLongitude(122.4194F);
        address.setType(HOME);
        person.setAddress(address);
        data.insert(person);
    }
}

