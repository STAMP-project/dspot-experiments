/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package dev.morphia;


import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.validation.ConstraintViolationException;
import dev.morphia.query.FindOptions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Scott Hernandez
 */
public class TestInheritanceMappings extends TestBase {
    @Test(expected = ConstraintViolationException.class)
    public void testMapEntity() {
        getMorphia().map(TestInheritanceMappings.MapLike.class);
        TestInheritanceMappings.MapLike m = new TestInheritanceMappings.MapLike();
        m.put("Name", "Scott");
        getDs().save(m);
        Assert.assertNotNull(m.id);
        Assert.assertEquals(1, getDs().getCount(TestInheritanceMappings.MapLike.class));
        m = getDs().find(TestInheritanceMappings.MapLike.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(m.id);
        Assert.assertTrue(m.containsKey("Name"));
        Assert.assertEquals("Scott", m.get("Name"));
    }

    @Test
    public void testParamEntity() {
        getMorphia().map(TestInheritanceMappings.ParameterizedEntity.class);
        TestInheritanceMappings.ParameterizedEntity c = new TestInheritanceMappings.ParameterizedEntity();
        c.setId("foo");
        c.b = "eh";
        c.setK(12L);
        getDs().save(c);
        c = getDs().get(TestInheritanceMappings.ParameterizedEntity.class, "foo");
        Assert.assertNotNull(c.getId());
        Assert.assertNotNull(c.b);
        Assert.assertNotNull(c.getK());
        Assert.assertEquals("foo", c.getId());
        Assert.assertEquals("eh", c.b);
        Assert.assertEquals(12, c.getK().longValue());
        Assert.assertEquals(1, getDs().getCount(TestInheritanceMappings.ParameterizedEntity.class));
    }

    @Test
    public void testParamIdEntity() {
        getMorphia().map(TestInheritanceMappings.ParameterizedIdEntity.class);
        TestInheritanceMappings.ParameterizedIdEntity c = new TestInheritanceMappings.ParameterizedIdEntity();
        c.setId("foo");
        getDs().save(c);
        c = getDs().get(TestInheritanceMappings.ParameterizedIdEntity.class, "foo");
        Assert.assertNotNull(c.getId());
        Assert.assertEquals("foo", c.getId());
        Assert.assertEquals(1, getDs().getCount(TestInheritanceMappings.ParameterizedIdEntity.class));
    }

    @Test
    public void testParamIdEntity2() {
        getMorphia().map(TestInheritanceMappings.ParameterizedIdEntity2.class);
        TestInheritanceMappings.ParameterizedIdEntity2 c = new TestInheritanceMappings.ParameterizedIdEntity2();
        c.setId("foo");
        getDs().save(c);
        c = getDs().get(TestInheritanceMappings.ParameterizedIdEntity2.class, "foo");
        Assert.assertNotNull(c.getId());
        Assert.assertEquals("foo", c.getId());
        Assert.assertEquals(1, getDs().getCount(TestInheritanceMappings.ParameterizedIdEntity2.class));
    }

    @Test
    public void testSuperclassEntity() {
        final TestInheritanceMappings.Car c = new TestInheritanceMappings.Car();
        getDs().save(c);
        Assert.assertNotNull(c.getId());
        Assert.assertEquals(1, getDs().getCount(TestInheritanceMappings.Car.class));
        Assert.assertEquals(1, getDs().getCount(TestInheritanceMappings.AbstractVehicle.class));
    }

    private enum VehicleClass {

        Bicycle,
        Moped,
        MiniCar,
        Car,
        Truck;}

    private interface Vehicle {
        String getId();

        TestInheritanceMappings.VehicleClass getVehicleClass();

        int getWheelCount();
    }

    private interface MapPlusIterableStringString extends Iterable<Map.Entry<String, String>> , Map<String, String> {}

    @Entity("vehicles")
    private abstract static class AbstractVehicle implements TestInheritanceMappings.Vehicle {
        @Id
        private ObjectId id;

        @Override
        public String getId() {
            return id.toString();
        }
    }

    private static class Car extends TestInheritanceMappings.AbstractVehicle {
        @Override
        public TestInheritanceMappings.VehicleClass getVehicleClass() {
            return TestInheritanceMappings.VehicleClass.Car;
        }

        @Override
        public int getWheelCount() {
            return 4;
        }
    }

    private static class FlyingCar extends TestInheritanceMappings.AbstractVehicle {
        @Override
        public TestInheritanceMappings.VehicleClass getVehicleClass() {
            return TestInheritanceMappings.VehicleClass.Car;
        }

        @Override
        public int getWheelCount() {
            return 0;
        }
    }

    public static class GenericIdPlus<T, K> {
        @Id
        private T id;

        private K k;

        public T getId() {
            return id;
        }

        public void setId(final T id) {
            this.id = id;
        }

        public K getK() {
            return k;
        }

        public void setK(final K k) {
            this.k = k;
        }
    }

    private static class ParameterizedEntity extends TestInheritanceMappings.GenericIdPlus<String, Long> {
        private String b;
    }

    private static class GenericId<T> {
        @Id
        private T id;

        public T getId() {
            return id;
        }

        public void setId(final T id) {
            this.id = id;
        }
    }

    private static class GenericIdSub<V> extends TestInheritanceMappings.GenericId<V> {}

    private static class ParameterizedIdEntity2 extends TestInheritanceMappings.GenericIdSub<String> {}

    private static class ParameterizedIdEntity extends TestInheritanceMappings.GenericId<String> {}

    @Entity(noClassnameStored = true)
    public static class MapLike implements TestInheritanceMappings.MapPlusIterableStringString {
        private final HashMap<String, String> realMap = new HashMap<String, String>();

        @Id
        private ObjectId id;

        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return realMap.entrySet().iterator();
        }

        @Override
        public int size() {
            return realMap.size();
        }

        @Override
        public boolean isEmpty() {
            return realMap.isEmpty();
        }

        @Override
        public boolean containsKey(final Object key) {
            return realMap.containsKey(key);
        }

        @Override
        public boolean containsValue(final Object value) {
            return realMap.containsValue(value);
        }

        @Override
        public String get(final Object key) {
            return realMap.get(key);
        }

        @Override
        public String put(final String key, final String value) {
            return realMap.put(key, value);
        }

        @Override
        public String remove(final Object key) {
            return realMap.remove(key);
        }

        @Override
        public void putAll(final Map<? extends String, ? extends String> m) {
            realMap.putAll(m);
        }

        @Override
        public void clear() {
            realMap.clear();
        }

        @Override
        public Set<String> keySet() {
            return realMap.keySet();
        }

        @Override
        public Collection<String> values() {
            return realMap.values();
        }

        @Override
        public Set<Map.Entry<String, String>> entrySet() {
            return realMap.entrySet();
        }
    }
}

