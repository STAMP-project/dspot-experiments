package dev.morphia.generics;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import dev.morphia.testutil.TestEntity;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestJavaMaps extends TestBase {
    @Test
    public void mapperTest() {
        getMorphia().map(TestJavaMaps.Employee.class);
        for (boolean nulls : new boolean[]{ true, false }) {
            for (boolean empties : new boolean[]{ true, false }) {
                getMorphia().getMapper().getOptions().setStoreNulls(nulls);
                getMorphia().getMapper().getOptions().setStoreEmpties(empties);
                empties();
            }
        }
    }

    @Test
    public void emptyModel() {
        getMorphia().getMapper().getOptions().setStoreEmpties(true);
        getMorphia().getMapper().getOptions().setStoreNulls(false);
        TestJavaMaps.TestEmptyModel model = new TestJavaMaps.TestEmptyModel();
        model.text = "text";
        model.wrapped = new TestJavaMaps.TestEmptyModel.Wrapped();
        model.wrapped.text = "textWrapper";
        getDs().save(model);
        TestJavaMaps.TestEmptyModel model2 = getDs().find(TestJavaMaps.TestEmptyModel.class).filter("id", model.id).find(new FindOptions().limit(1)).next();
        Assert.assertNull(model.wrapped.others);
        Assert.assertNull(model2.wrapped.others);
    }

    @Test
    public void testKeyOrdering() {
        getMorphia().map(TestJavaMaps.LinkedHashMapTestEntity.class);
        final TestJavaMaps.LinkedHashMapTestEntity expectedEntity = new TestJavaMaps.LinkedHashMapTestEntity();
        for (int i = 100; i >= 0; i--) {
            expectedEntity.getLinkedHashMap().put(i, ("a" + i));
        }
        getDs().save(expectedEntity);
        TestJavaMaps.LinkedHashMapTestEntity storedEntity = getDs().find(TestJavaMaps.LinkedHashMapTestEntity.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(storedEntity);
        Assert.assertEquals(expectedEntity.getLinkedHashMap(), storedEntity.getLinkedHashMap());
    }

    @Entity
    static class TestEmptyModel {
        @Id
        private ObjectId id;

        private String text;

        private TestJavaMaps.TestEmptyModel.Wrapped wrapped;

        private static class Wrapped {
            private Map<String, TestJavaMaps.TestEmptyModel.Wrapped> others;

            private String text;
        }
    }

    @Entity("employees")
    static class Employee {
        @Id
        private ObjectId id;

        private Map<String, Float> floatMap;

        private Map<String, Byte> byteMap;
    }

    @Entity
    static class LinkedHashMapTestEntity extends TestEntity {
        @Embedded(concreteClass = LinkedHashMap.class)
        private final Map<Integer, String> linkedHashMap = new LinkedHashMap<Integer, String>();

        private Map<Integer, String> getLinkedHashMap() {
            return linkedHashMap;
        }
    }
}

