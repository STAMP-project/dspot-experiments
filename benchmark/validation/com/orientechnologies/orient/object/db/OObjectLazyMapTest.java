package com.orientechnologies.orient.object.db;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author JN <a href="mailto:jn@brain-activit.com">Julian Neuhaus</a>
 * @since 21.08.2014
 */
public class OObjectLazyMapTest {
    private final int idOfRootEntity = 0;

    private final int idOfFirstMapEntry = 1;

    private final int idOfSecondMapEntry = 2;

    private final int invalidId = 3;

    private OObjectDatabaseTx databaseTx;

    @Test
    public void isEmptyTest() {
        Map<String, OObjectLazyMapTest.EntityWithMap> testMap = getMapWithPersistedEntries();
        Assert.assertTrue(((testMap.size()) > 0));
        testMap.clear();
        Assert.assertTrue(((testMap.size()) == 0));
        Assert.assertTrue(((testMap.get(String.valueOf(idOfFirstMapEntry))) == null));
    }

    @Test
    public void getContainsValueTest() {
        Map<String, OObjectLazyMapTest.EntityWithMap> testMap = getMapWithPersistedEntries();
        Assert.assertFalse(testMap.containsValue(null));
        Assert.assertFalse(testMap.containsValue(String.valueOf(invalidId)));
        Assert.assertTrue(testMap.containsValue(testMap.get(String.valueOf(idOfFirstMapEntry))));
        Assert.assertTrue(testMap.containsValue(testMap.get(String.valueOf(idOfSecondMapEntry))));
    }

    @Test
    public void getContainsKeyTest() {
        Map<String, OObjectLazyMapTest.EntityWithMap> testMap = getMapWithPersistedEntries();
        Assert.assertFalse(testMap.containsKey(null));
        Assert.assertFalse(testMap.containsKey(String.valueOf(invalidId)));
        // should fail because the keys will be automatically converted to string
        Assert.assertFalse(testMap.containsKey(idOfFirstMapEntry));
        Assert.assertTrue(testMap.containsKey(String.valueOf(idOfFirstMapEntry)));
        Assert.assertTrue(testMap.containsKey(String.valueOf(idOfSecondMapEntry)));
    }

    @Test
    public void getTest() {
        Map<String, OObjectLazyMapTest.EntityWithMap> testMap = getMapWithPersistedEntries();
        Assert.assertTrue(((testMap.get(String.valueOf(invalidId))) == null));
        // should fail because the keys will be automatically converted to string
        try {
            testMap.get(idOfFirstMapEntry);
            Assert.fail("Expected ClassCastException");
        } catch (ClassCastException e) {
        }
        Assert.assertTrue(((testMap.get(String.valueOf(idOfFirstMapEntry))) != null));
        Assert.assertTrue(((testMap.get(String.valueOf(idOfSecondMapEntry))) != null));
    }

    @Test
    public void getOrDefaultTest() {
        Object toCast = getMapWithPersistedEntries();
        Assert.assertTrue((toCast instanceof OObjectLazyMap));
        @SuppressWarnings({ "unchecked", "rawtypes" })
        OObjectLazyMap<OObjectLazyMapTest.EntityWithMap> testMap = ((OObjectLazyMap) (toCast));
        Assert.assertTrue(((testMap.getClass()) == (OObjectLazyMap.class)));
        Assert.assertTrue(((testMap.getOrDefault(String.valueOf(idOfFirstMapEntry), null)) != null));
        Assert.assertTrue(((testMap.getOrDefault(String.valueOf(idOfSecondMapEntry), null)) != null));
        Assert.assertTrue(((testMap.getOrDefault(String.valueOf(invalidId), null)) == null));
        Assert.assertTrue(((testMap.getOrDefault(String.valueOf(invalidId), testMap.get(String.valueOf(idOfFirstMapEntry)))) == (testMap.get(String.valueOf(idOfFirstMapEntry)))));
    }

    public class EntityWithMap {
        private int id;

        private Map<String, OObjectLazyMapTest.EntityWithMap> map;

        public Map<String, OObjectLazyMapTest.EntityWithMap> getMap() {
            return map;
        }

        public void setMap(Map<String, OObjectLazyMapTest.EntityWithMap> map) {
            this.map = map;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

