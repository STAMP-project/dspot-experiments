/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.springdata;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeSet;
import org.apache.ignite.springdata.misc.Person;
import org.apache.ignite.springdata.misc.PersonRepository;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 *
 */
public class IgniteSpringDataCrudSelfTest extends GridCommonAbstractTest {
    /**
     * Repository.
     */
    private static PersonRepository repo;

    /**
     * Context.
     */
    private static AnnotationConfigApplicationContext ctx;

    /**
     * Number of entries to store
     */
    private static int CACHE_SIZE = 1000;

    /**
     *
     */
    @Test
    public void testPutGet() {
        Person person = new Person("some_name", "some_surname");
        int id = (IgniteSpringDataCrudSelfTest.CACHE_SIZE) + 1;
        assertEquals(person, IgniteSpringDataCrudSelfTest.repo.save(id, person));
        assertTrue(exists(id));
        assertEquals(person, findOne(id));
        try {
            IgniteSpringDataCrudSelfTest.repo.save(person);
            fail("Managed to save a Person without ID");
        } catch (UnsupportedOperationException e) {
            // excepted
        }
    }

    /**
     *
     */
    @Test
    public void testPutAllGetAll() {
        LinkedHashMap<Integer, Person> map = new LinkedHashMap<>();
        for (int i = IgniteSpringDataCrudSelfTest.CACHE_SIZE; i < ((IgniteSpringDataCrudSelfTest.CACHE_SIZE) + 50); i++)
            map.put(i, new Person(("some_name" + i), ("some_surname" + i)));

        Iterator<Person> persons = IgniteSpringDataCrudSelfTest.repo.save(map).iterator();
        assertEquals(((IgniteSpringDataCrudSelfTest.CACHE_SIZE) + 50), count());
        Iterator<Person> origPersons = map.values().iterator();
        while (persons.hasNext())
            assertEquals(origPersons.next(), persons.next());

        try {
            save(map.values());
            fail("Managed to save a list of Persons with ids");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        persons = IgniteSpringDataCrudSelfTest.repo.findAll(map.keySet()).iterator();
        int counter = 0;
        while (persons.hasNext()) {
            persons.next();
            counter++;
        } 
        assertEquals(map.size(), counter);
    }

    /**
     *
     */
    @Test
    public void testGetAll() {
        assertEquals(IgniteSpringDataCrudSelfTest.CACHE_SIZE, count());
        Iterator<Person> persons = findAll().iterator();
        int counter = 0;
        while (persons.hasNext()) {
            persons.next();
            counter++;
        } 
        assertEquals(count(), counter);
    }

    /**
     *
     */
    @Test
    public void testDelete() {
        assertEquals(IgniteSpringDataCrudSelfTest.CACHE_SIZE, count());
        IgniteSpringDataCrudSelfTest.repo.delete(0);
        assertEquals(((IgniteSpringDataCrudSelfTest.CACHE_SIZE) - 1), count());
        assertNull(findOne(0));
        try {
            IgniteSpringDataCrudSelfTest.repo.delete(new Person("", ""));
            fail("Managed to delete a Person without id");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    /**
     *
     */
    @Test
    public void testDeleteSet() {
        assertEquals(IgniteSpringDataCrudSelfTest.CACHE_SIZE, count());
        TreeSet<Integer> ids = new TreeSet<>();
        for (int i = 0; i < ((IgniteSpringDataCrudSelfTest.CACHE_SIZE) / 2); i++)
            ids.add(i);

        IgniteSpringDataCrudSelfTest.repo.deleteAll(ids);
        assertEquals(((IgniteSpringDataCrudSelfTest.CACHE_SIZE) / 2), count());
        try {
            ArrayList<Person> persons = new ArrayList<>();
            for (int i = 0; i < 3; i++)
                persons.add(new Person(String.valueOf(i), String.valueOf(i)));

            delete(persons);
            fail("Managed to delete Persons without ids");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    /**
     *
     */
    @Test
    public void testDeleteAll() {
        assertEquals(IgniteSpringDataCrudSelfTest.CACHE_SIZE, count());
        deleteAll();
        assertEquals(0, count());
    }
}

