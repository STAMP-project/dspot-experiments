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


import Cache.Entry;
import Sort.Direction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.ignite.springdata.misc.Person;
import org.apache.ignite.springdata.misc.PersonRepository;
import org.apache.ignite.springdata.misc.PersonSecondRepository;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;


/**
 *
 */
public class IgniteSpringDataQueriesSelfTest extends GridCommonAbstractTest {
    /**
     * Repository.
     */
    private static PersonRepository repo;

    /**
     * Repository 2.
     */
    private static PersonSecondRepository repo2;

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
    public void testExplicitQuery() {
        List<Person> persons = IgniteSpringDataQueriesSelfTest.repo.simpleQuery("person4a");
        assertFalse(persons.isEmpty());
        for (Person person : persons)
            assertEquals("person4a", person.getFirstName());

    }

    /**
     *
     */
    @Test
    public void testEqualsPart() {
        List<Person> persons = IgniteSpringDataQueriesSelfTest.repo.findByFirstName("person4e");
        assertFalse(persons.isEmpty());
        for (Person person : persons)
            assertEquals("person4e", person.getFirstName());

    }

    /**
     *
     */
    @Test
    public void testContainingPart() {
        List<Person> persons = IgniteSpringDataQueriesSelfTest.repo.findByFirstNameContaining("person4");
        assertFalse(persons.isEmpty());
        for (Person person : persons)
            assertTrue(person.getFirstName().startsWith("person4"));

    }

    /**
     *
     */
    @Test
    public void testTopPart() {
        Iterable<Person> top = IgniteSpringDataQueriesSelfTest.repo.findTopByFirstNameContaining("person4");
        Iterator<Person> iter = top.iterator();
        Person person = iter.next();
        assertFalse(iter.hasNext());
        assertTrue(person.getFirstName().startsWith("person4"));
    }

    /**
     *
     */
    @Test
    public void testLikeAndLimit() {
        Iterable<Person> like = IgniteSpringDataQueriesSelfTest.repo.findFirst10ByFirstNameLike("person");
        int cnt = 0;
        for (Person next : like) {
            assertTrue(next.getFirstName().contains("person"));
            cnt++;
        }
        assertEquals(10, cnt);
    }

    /**
     *
     */
    @Test
    public void testCount() {
        int cnt = IgniteSpringDataQueriesSelfTest.repo.countByFirstNameLike("person");
        assertEquals(1000, cnt);
    }

    /**
     *
     */
    @Test
    public void testCount2() {
        int cnt = IgniteSpringDataQueriesSelfTest.repo.countByFirstNameLike("person4");
        assertTrue((cnt < 1000));
    }

    /**
     *
     */
    @Test
    public void testPageable() {
        PageRequest pageable = new PageRequest(1, 5, Direction.DESC, "firstName");
        HashSet<String> firstNames = new HashSet<>();
        List<Person> pageable1 = IgniteSpringDataQueriesSelfTest.repo.findByFirstNameRegex("^[a-z]+$", pageable);
        assertEquals(5, pageable1.size());
        for (Person person : pageable1) {
            firstNames.add(person.getFirstName());
            assertTrue(person.getFirstName().matches("^[a-z]+$"));
        }
        List<Person> pageable2 = IgniteSpringDataQueriesSelfTest.repo.findByFirstNameRegex("^[a-z]+$", pageable.next());
        assertEquals(5, pageable2.size());
        for (Person person : pageable2) {
            firstNames.add(person.getFirstName());
            assertTrue(person.getFirstName().matches("^[a-z]+$"));
        }
        assertEquals(10, firstNames.size());
    }

    /**
     *
     */
    @Test
    public void testAndAndOr() {
        int cntAnd = IgniteSpringDataQueriesSelfTest.repo.countByFirstNameLikeAndSecondNameLike("person1", "lastName1");
        int cntOr = IgniteSpringDataQueriesSelfTest.repo.countByFirstNameStartingWithOrSecondNameStartingWith("person1", "lastName1");
        assertTrue((cntAnd <= cntOr));
    }

    /**
     *
     */
    @Test
    public void testQueryWithSort() {
        List<Person> persons = IgniteSpringDataQueriesSelfTest.repo.queryWithSort("^[a-z]+$", new org.springframework.data.domain.Sort(Direction.DESC, "secondName"));
        Person previous = persons.get(0);
        for (Person person : persons) {
            assertTrue(((person.getSecondName().compareTo(previous.getSecondName())) <= 0));
            assertTrue(person.getFirstName().matches("^[a-z]+$"));
            previous = person;
        }
    }

    /**
     *
     */
    @Test
    public void testQueryWithPaging() {
        List<Person> persons = IgniteSpringDataQueriesSelfTest.repo.queryWithPageable("^[a-z]+$", new PageRequest(1, 7, Direction.DESC, "secondName"));
        assertEquals(7, persons.size());
        Person previous = persons.get(0);
        for (Person person : persons) {
            assertTrue(((person.getSecondName().compareTo(previous.getSecondName())) <= 0));
            assertTrue(person.getFirstName().matches("^[a-z]+$"));
            previous = person;
        }
    }

    /**
     *
     */
    @Test
    public void testQueryFields() {
        List<String> persons = IgniteSpringDataQueriesSelfTest.repo.selectField("^[a-z]+$", new PageRequest(1, 7, Direction.DESC, "secondName"));
        assertEquals(7, persons.size());
    }

    /**
     *
     */
    @Test
    public void testFindCacheEntries() {
        List<Entry<Integer, Person>> cacheEntries = findBySecondNameLike("stName1");
        assertFalse(cacheEntries.isEmpty());
        for (Entry<Integer, Person> entry : cacheEntries)
            assertTrue(entry.getValue().getSecondName().contains("stName1"));

    }

    /**
     *
     */
    @Test
    public void testFindOneCacheEntry() {
        Entry<Integer, Person> cacheEntry = findTopBySecondNameLike("tName18");
        assertNotNull(cacheEntry);
        assertTrue(cacheEntry.getValue().getSecondName().contains("tName18"));
    }

    /**
     *
     */
    @Test
    public void testFindOneValue() {
        Person person = IgniteSpringDataQueriesSelfTest.repo.findTopBySecondNameStartingWith("lastName18");
        assertNotNull(person);
        assertTrue(person.getSecondName().startsWith("lastName18"));
    }

    /**
     *
     */
    @Test
    public void testSelectSeveralFields() {
        List<List> lists = IgniteSpringDataQueriesSelfTest.repo.selectSeveralField("^[a-z]+$", new PageRequest(2, 6));
        assertEquals(6, lists.size());
        for (List list : lists) {
            assertEquals(2, list.size());
            assertTrue(((list.get(0)) instanceof Integer));
        }
    }

    /**
     *
     */
    @Test
    public void testCountQuery() {
        int cnt = IgniteSpringDataQueriesSelfTest.repo.countQuery(".*");
        assertEquals(256, cnt);
    }

    /**
     *
     */
    @Test
    public void testSliceOfCacheEntries() {
        Slice<Entry<Integer, Person>> slice = IgniteSpringDataQueriesSelfTest.repo2.findBySecondNameIsNot("lastName18", new PageRequest(3, 4));
        assertEquals(4, slice.getSize());
        for (Entry<Integer, Person> entry : slice)
            assertFalse("lastName18".equals(entry.getValue().getSecondName()));

    }

    /**
     *
     */
    @Test
    public void testSliceOfLists() {
        Slice<List> lists = IgniteSpringDataQueriesSelfTest.repo2.querySliceOfList("^[a-z]+$", new PageRequest(0, 3));
        assertEquals(3, lists.getSize());
        for (List list : lists) {
            assertEquals(2, list.size());
            assertTrue(((list.get(0)) instanceof Integer));
        }
    }

    /**
     * Tests the repository method with a custom query which takes no parameters.
     */
    @Test
    public void testCountAllPersons() {
        int cnt = IgniteSpringDataQueriesSelfTest.repo.countAllPersons();
        assertEquals(IgniteSpringDataQueriesSelfTest.CACHE_SIZE, cnt);
    }
}

