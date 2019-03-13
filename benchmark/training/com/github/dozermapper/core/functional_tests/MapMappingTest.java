/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.map.MapToMap;
import com.github.dozermapper.core.vo.map.MapToMapPrime;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MapMappingTest extends AbstractFunctionalTest {
    @Test
    public void testMapWithNullEntries_NullPointer() {
        MapToMap source = newInstance(MapToMap.class);
        HashMap<String, TestObject> map = newInstance(HashMap.class);
        map.put("A", null);
        source.setStandardMap(map);
        MapToMapPrime destination = newInstance(MapToMapPrime.class);
        HashMap<String, Serializable> map2 = newInstance(HashMap.class);
        map2.put("B", Boolean.TRUE);
        destination.setStandardMap(map2);
        mapper.map(source, destination);
        Map<?, ?> resultingMap = destination.getStandardMap();
        Assert.assertNotNull(resultingMap);
        Assert.assertEquals(2, resultingMap.size());
        Assert.assertNull(resultingMap.get("A"));
        Assert.assertEquals(Boolean.TRUE, resultingMap.get("B"));
    }

    @Test
    public void testMapNullEntry_MultipleEntries() {
        MapToMap source = newInstance(MapToMap.class);
        HashMap<String, Boolean> map = newInstance(HashMap.class);
        map.put("A", null);
        map.put("B", null);
        map.put("C", null);
        source.setStandardMap(map);
        MapToMapPrime destination = newInstance(MapToMapPrime.class);
        HashMap<String, Serializable> map2 = newInstance(HashMap.class);
        destination.setStandardMap(map2);
        mapper.map(source, destination);
        Map<?, ?> resultingMap = destination.getStandardMap();
        Assert.assertNotNull(resultingMap);
        Assert.assertEquals(3, resultingMap.size());
        Assert.assertNull(resultingMap.get("A"));
        Assert.assertNull(resultingMap.get("B"));
        Assert.assertNull(resultingMap.get("C"));
    }

    @Test
    public void testMapMapWithList_Simple() {
        MapToMap source = newInstance(MapToMap.class);
        HashMap<String, List> map = newInstance(HashMap.class);
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(Boolean.TRUE);
        map.put("A", list);
        source.setStandardMap(map);
        MapToMapPrime destination = mapper.map(source, MapToMapPrime.class);
        Map<String, List> resultingMap = destination.getStandardMap();
        Assert.assertNotNull(resultingMap);
        Assert.assertEquals(1, resultingMap.size());
        Assert.assertNotNull(resultingMap.get("A"));
        Assert.assertEquals(1, resultingMap.get("A").size());
        Assert.assertEquals(Boolean.TRUE, resultingMap.get("A").iterator().next());
    }

    @Test
    public void testSimple() {
        MapMappingTest.Simpler input = new MapMappingTest.Simpler();
        input.getValues().put("f", Integer.valueOf(5));
        MapMappingTest.Simpler output = mapper.map(input, MapMappingTest.Simpler.class);
        Assert.assertEquals(input.getValues(), output.getValues());
    }

    @Test
    public void testMapListOfPrimitives() {
        MapMappingTest.DTO input = new MapMappingTest.DTO();
        input.getValues().put("e", Collections.singletonList(Integer.valueOf(3)));
        MapMappingTest.DTO output = mapper.map(input, MapMappingTest.DTO.class);
        Assert.assertTrue(((output.getValues().get("e")) != null));
        Assert.assertTrue(output.getValues().get("e").contains("3"));
    }

    public static class DTO {
        private Map<String, List<Integer>> values = new HashMap<>();

        public Map<String, List<Integer>> getValues() {
            return values;
        }

        public void setValues(Map<String, List<Integer>> values) {
            this.values = values;
        }
    }

    public static class Simpler {
        private Map<String, Integer> values = new HashMap<>();

        public Map<String, Integer> getValues() {
            return values;
        }

        public void setValues(Map<String, Integer> values) {
            this.values = values;
        }
    }
}

