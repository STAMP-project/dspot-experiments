package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author scott hernandez
 * @author RainoBoy97
 */
public class MapperOptionsTest extends TestBase {
    @Test
    public void emptyListStoredWithOptions() throws Exception {
        final MapperOptionsTest.HasList hl = new MapperOptionsTest.HasList();
        hl.names = new ArrayList<String>();
        // Test default behavior
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hl);
        // Test default storing empty list/array with storeEmpties option
        getMorphia().getMapper().getOptions().setStoreEmpties(true);
        shouldFindField(hl, new ArrayList<String>());
        // Test opposite from above
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hl);
        hl.names = null;
        // Test default behavior
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hl);
        // Test default storing empty list/array with storeEmpties option
        getMorphia().getMapper().getOptions().setStoreEmpties(true);
        shouldNotFindField(hl);
        // Test opposite from above
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hl);
    }

    @Test
    public void emptyMapStoredWithOptions() throws Exception {
        final MapperOptionsTest.HasMap hm = new MapperOptionsTest.HasMap();
        hm.properties = new HashMap<String, String>();
        // Test default behavior
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hm);
        // Test default storing empty map with storeEmpties option
        getMorphia().getMapper().getOptions().setStoreEmpties(true);
        shouldFindField(hm, new HashMap<String, String>());
        // Test opposite from above
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hm);
    }

    @Test
    public void emptyCollectionValuedMapStoredWithOptions() throws Exception {
        final MapperOptionsTest.HasCollectionValuedMap hm = new MapperOptionsTest.HasCollectionValuedMap();
        hm.properties = new HashMap<String, Collection<String>>();
        // Test default behavior
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hm);
        // Test default storing empty map with storeEmpties option
        getMorphia().getMapper().getOptions().setStoreEmpties(true);
        shouldFindField(hm, new HashMap<String, Collection<String>>());
        // Test opposite from above
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hm);
    }

    @Test
    public void emptyComplexObjectValuedMapStoredWithOptions() throws Exception {
        final MapperOptionsTest.HasComplexObjectValuedMap hm = new MapperOptionsTest.HasComplexObjectValuedMap();
        hm.properties = new HashMap<String, MapperOptionsTest.ComplexObject>();
        // Test default behavior
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hm);
        // Test default storing empty map with storeEmpties option
        getMorphia().getMapper().getOptions().setStoreEmpties(true);
        shouldFindField(hm, new HashMap<String, MapperOptionsTest.ComplexObject>());
        // Test opposite from above
        getMorphia().getMapper().getOptions().setStoreEmpties(false);
        shouldNotFindField(hm);
    }

    @Test
    public void lowercaseDefaultCollection() {
        MapperOptionsTest.DummyEntity entity = new MapperOptionsTest.DummyEntity();
        String collectionName = getMorphia().getMapper().getCollectionName(entity);
        Assert.assertEquals("uppercase", "DummyEntity", collectionName);
        getMorphia().getMapper().getOptions().setUseLowerCaseCollectionNames(true);
        collectionName = getMorphia().getMapper().getCollectionName(entity);
        Assert.assertEquals("lowercase", "dummyentity", collectionName);
    }

    @Test
    public void nullListStoredWithOptions() throws Exception {
        final MapperOptionsTest.HasList hl = new MapperOptionsTest.HasList();
        hl.names = null;
        // Test default behavior
        getMorphia().getMapper().getOptions().setStoreNulls(false);
        shouldNotFindField(hl);
        // Test default storing null list/array with storeNulls option
        getMorphia().getMapper().getOptions().setStoreNulls(true);
        shouldFindField(hl, null);
        // Test opposite from above
        getMorphia().getMapper().getOptions().setStoreNulls(false);
        shouldNotFindField(hl);
    }

    @Test
    public void nullMapStoredWithOptions() throws Exception {
        final MapperOptionsTest.HasMap hm = new MapperOptionsTest.HasMap();
        hm.properties = null;
        // Test default behavior
        getMorphia().getMapper().getOptions().setStoreNulls(false);
        shouldNotFindField(hm);
        // Test default storing empty map with storeEmpties option
        getMorphia().getMapper().getOptions().setStoreNulls(true);
        shouldFindField(hm, null);
        // Test opposite from above
        getMorphia().getMapper().getOptions().setStoreNulls(false);
        shouldNotFindField(hm);
    }

    private static class HasList implements Serializable {
        @Id
        private ObjectId id = new ObjectId();

        private List<String> names;

        HasList() {
        }
    }

    private static class HasMap implements Serializable {
        @Id
        private ObjectId id = new ObjectId();

        private Map<String, String> properties;

        HasMap() {
        }
    }

    private static class HasCollectionValuedMap implements Serializable {
        @Id
        private ObjectId id = new ObjectId();

        private Map<String, Collection<String>> properties;

        HasCollectionValuedMap() {
        }
    }

    private static class HasComplexObjectValuedMap implements Serializable {
        @Id
        private ObjectId id = new ObjectId();

        private Map<String, MapperOptionsTest.ComplexObject> properties;

        HasComplexObjectValuedMap() {
        }
    }

    @Entity
    private static class DummyEntity {}

    private static class ComplexObject {
        private String stringVal;

        private int intVal;
    }
}

