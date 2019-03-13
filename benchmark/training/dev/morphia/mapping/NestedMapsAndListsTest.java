package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.query.FindOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author scotthernandez
 */
public class NestedMapsAndListsTest extends TestBase {
    @Test
    public void testListOfList() {
        getMorphia().map(NestedMapsAndListsTest.ListOfList.class);
        NestedMapsAndListsTest.ListOfList list = new NestedMapsAndListsTest.ListOfList();
        list.list.add(Arrays.asList("a", "b", "c"));
        list.list.add(Arrays.asList("123", "456"));
        getDs().save(list);
        NestedMapsAndListsTest.ListOfList listOfList = getDs().find(NestedMapsAndListsTest.ListOfList.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertEquals(list, listOfList);
    }

    @Test
    public void testListOfListOfPerson() {
        getMorphia().map(NestedMapsAndListsTest.ListListPerson.class);
        NestedMapsAndListsTest.ListListPerson list = new NestedMapsAndListsTest.ListListPerson();
        list.list.add(Arrays.asList(new NestedMapsAndListsTest.Person("Peter"), new NestedMapsAndListsTest.Person("Paul"), new NestedMapsAndListsTest.Person("Mary")));
        list.list.add(Arrays.asList(new NestedMapsAndListsTest.Person("Crosby"), new NestedMapsAndListsTest.Person("Stills"), new NestedMapsAndListsTest.Person("Nash")));
        getDs().save(list);
        NestedMapsAndListsTest.ListListPerson result = getDs().find(NestedMapsAndListsTest.ListListPerson.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertEquals(list, result);
    }

    @Test
    public void testListOfMap() {
        getMorphia().map(NestedMapsAndListsTest.ListOfMap.class);
        NestedMapsAndListsTest.ListOfMap entity = new NestedMapsAndListsTest.ListOfMap();
        HashMap<String, String> mapA = new HashMap<String, String>();
        mapA.put("a", "b");
        entity.listOfMap.add(mapA);
        final Map<String, String> mapC = new HashMap<String, String>();
        mapC.put("c", "d");
        entity.listOfMap.add(mapC);
        getDs().save(entity);
        NestedMapsAndListsTest.ListOfMap object = getDs().find(NestedMapsAndListsTest.ListOfMap.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(object);
        Assert.assertEquals(entity, object);
    }

    @Test
    public void testListOfMapOfEntity() {
        getMorphia().map(NestedMapsAndListsTest.ListMapPerson.class);
        NestedMapsAndListsTest.ListMapPerson listMap = new NestedMapsAndListsTest.ListMapPerson();
        listMap.list.add(map("Rick", new NestedMapsAndListsTest.Person("Richard")));
        listMap.list.add(map("Bill", new NestedMapsAndListsTest.Person("William")));
        getDs().save(listMap);
        Assert.assertEquals(listMap, getDs().find(NestedMapsAndListsTest.ListMapPerson.class).find(new FindOptions().limit(1)).tryNext());
    }

    @Test
    public void testMapOfList() throws Exception {
        NestedMapsAndListsTest.HashMapOfList map = new NestedMapsAndListsTest.HashMapOfList();
        map.mol.put("entry1", Collections.singletonList("val1"));
        map.mol.put("entry2", Collections.singletonList("val2"));
        getDs().save(map);
        map = getDs().find(NestedMapsAndListsTest.HashMapOfList.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(map.mol);
        Assert.assertNotNull(map.mol.get("entry1"));
        Assert.assertNotNull(map.mol.get("entry1").get(0));
        Assert.assertEquals("val1", map.mol.get("entry1").get(0));
        Assert.assertNotNull("val2", map.mol.get("entry2").get(0));
    }

    @Test
    public void testUserData() {
        getMorphia().map(NestedMapsAndListsTest.MapOfListString.class);
        NestedMapsAndListsTest.MapOfListString ud = new NestedMapsAndListsTest.MapOfListString();
        ud.id = "123";
        ArrayList<String> d = new ArrayList<String>();
        d.add("1");
        d.add("2");
        d.add("3");
        ud.data.put("123123", d);
        getDs().save(ud);
    }

    @Test
    public void testMapOfListOfMapMap() throws Exception {
        final NestedMapsAndListsTest.HashMapOfMap mapOfMap = new NestedMapsAndListsTest.HashMapOfMap();
        final Map<String, String> map = new HashMap<String, String>();
        mapOfMap.mom.put("root", map);
        map.put("deep", "values");
        map.put("peer", "lame");
        NestedMapsAndListsTest.HashMapOfListOfMapMap mapMap = new NestedMapsAndListsTest.HashMapOfListOfMapMap();
        mapMap.mol.put("r1", Collections.singletonList(mapOfMap));
        mapMap.mol.put("r2", Collections.singletonList(mapOfMap));
        getDs().save(mapMap);
        mapMap = getDs().find(NestedMapsAndListsTest.HashMapOfListOfMapMap.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(mapMap.mol);
        Assert.assertNotNull(mapMap.mol.get("r1"));
        Assert.assertNotNull(mapMap.mol.get("r1").get(0));
        Assert.assertNotNull(mapMap.mol.get("r1").get(0).mom);
        Assert.assertEquals("values", mapMap.mol.get("r1").get(0).mom.get("root").get("deep"));
        Assert.assertEquals("lame", mapMap.mol.get("r1").get(0).mom.get("root").get("peer"));
        Assert.assertEquals("values", mapMap.mol.get("r2").get(0).mom.get("root").get("deep"));
        Assert.assertEquals("lame", mapMap.mol.get("r2").get(0).mom.get("root").get("peer"));
    }

    @Test
    public void testMapOfMap() throws Exception {
        NestedMapsAndListsTest.HashMapOfMap mapOfMap = new NestedMapsAndListsTest.HashMapOfMap();
        final Map<String, String> map = new HashMap<String, String>();
        mapOfMap.mom.put("root", map);
        map.put("deep", "values");
        map.put("peer", "lame");
        getDs().save(mapOfMap);
        mapOfMap = getDs().find(NestedMapsAndListsTest.HashMapOfMap.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(mapOfMap.mom);
        Assert.assertNotNull(mapOfMap.mom.get("root"));
        Assert.assertNotNull(mapOfMap.mom.get("root").get("deep"));
        Assert.assertEquals("values", mapOfMap.mom.get("root").get("deep"));
        Assert.assertNotNull("lame", mapOfMap.mom.get("root").get("peer"));
    }

    @Entity
    private static class ListOfMap {
        @Property
        private final List<Map<String, String>> listOfMap = new ArrayList<Map<String, String>>();

        @Id
        private long id;

        @Override
        public int hashCode() {
            int result = ((int) ((id) ^ ((id) >>> 32)));
            result = (31 * result) + (listOfMap.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return String.format("ListOfMap{id=%d, listOfMap=%s}", id, listOfMap);
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final NestedMapsAndListsTest.ListOfMap listOfMap1 = ((NestedMapsAndListsTest.ListOfMap) (o));
            if ((id) != (listOfMap1.id)) {
                return false;
            }
            if (!(listOfMap.equals(listOfMap1.listOfMap))) {
                return false;
            }
            return true;
        }
    }

    @Entity
    private static class ListOfList {
        @Property
        private final List<List<String>> list = new ArrayList<List<String>>();

        @Id
        private long id;

        @Override
        public String toString() {
            return String.format("ListOfList{id=%d, list=%s}", id, list);
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof NestedMapsAndListsTest.ListOfList)) {
                return false;
            }
            final NestedMapsAndListsTest.ListOfList that = ((NestedMapsAndListsTest.ListOfList) (o));
            return ((id) == (that.id)) && (list.equals(that.list));
        }

        @Override
        public int hashCode() {
            int result = ((int) ((id) ^ ((id) >>> 32)));
            result = (31 * result) + (list.hashCode());
            return result;
        }
    }

    @Entity
    private static class ListListPerson {
        @Embedded
        private final List<List<NestedMapsAndListsTest.Person>> list = new ArrayList<List<NestedMapsAndListsTest.Person>>();

        @Id
        private long id;

        @Override
        public String toString() {
            return String.format("ListListPerson{id=%d, list=%s}", id, list);
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof NestedMapsAndListsTest.ListListPerson)) {
                return false;
            }
            final NestedMapsAndListsTest.ListListPerson that = ((NestedMapsAndListsTest.ListListPerson) (o));
            if ((id) != (that.id)) {
                return false;
            }
            return list.equals(that.list);
        }

        @Override
        public int hashCode() {
            int result = ((int) ((id) ^ ((id) >>> 32)));
            result = (31 * result) + (list.hashCode());
            return result;
        }
    }

    @Entity
    private static class ListMapPerson {
        @Id
        private ObjectId id;

        private List<Map<String, NestedMapsAndListsTest.Person>> list = new ArrayList<Map<String, NestedMapsAndListsTest.Person>>();

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof NestedMapsAndListsTest.ListMapPerson)) {
                return false;
            }
            final NestedMapsAndListsTest.ListMapPerson that = ((NestedMapsAndListsTest.ListMapPerson) (o));
            if ((id) != null ? !(id.equals(that.id)) : (that.id) != null) {
                return false;
            }
            return list.equals(that.list);
        }

        @Override
        public int hashCode() {
            int result = ((id) != null) ? id.hashCode() : 0;
            result = (31 * result) + (list.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return String.format("ListMapPerson{id=%s, list=%s}", id, list);
        }
    }

    @Embedded
    private static class Person {
        private String name;

        Person() {
        }

        Person(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("Person{name='%s'}", name);
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof NestedMapsAndListsTest.Person)) {
                return false;
            }
            final NestedMapsAndListsTest.Person person = ((NestedMapsAndListsTest.Person) (o));
            return !((name) != null ? !(name.equals(person.name)) : (person.name) != null);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    private static class HashMapOfMap {
        @Embedded
        private final Map<String, Map<String, String>> mom = new HashMap<String, Map<String, String>>();

        @Id
        private ObjectId id;
    }

    private static class HashMapOfList {
        private final Map<String, List<String>> mol = new HashMap<String, List<String>>();

        @Id
        private ObjectId id;
    }

    private static class HashMapOfListOfMapMap {
        @Embedded
        private final Map<String, List<NestedMapsAndListsTest.HashMapOfMap>> mol = new HashMap<String, List<NestedMapsAndListsTest.HashMapOfMap>>();

        @Id
        private ObjectId id;
    }

    public static class MapOfListString {
        @Id
        private String id;

        private Map<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();

        MapOfListString() {
        }
    }
}

