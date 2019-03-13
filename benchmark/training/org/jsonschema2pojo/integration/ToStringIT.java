/**
 * Copyright ? 2017 David Ehrmann
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
package org.jsonschema2pojo.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ToStringIT {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Rule
    public Jsonschema2PojoRule schemaRule = new Jsonschema2PojoRule();

    @Test
    public void testScalars() throws Exception {
        Class<?> scalarTypesClass = schemaRule.generateAndCompile("/schema/toString/scalarTypes.json", "com.example").loadClass("com.example.ScalarTypes");
        Assert.assertEquals("com.example.ScalarTypes@<ref>[stringField=<null>,numberField=<null>,integerField=<null>,booleanField=<null>,nullField=<null>,bytesField=<null>]", ToStringIT.toStringAndReplaceAddress(Collections.emptyMap(), scalarTypesClass));
        Map<String, Object> scalarTypes = new HashMap<>();
        scalarTypes.put("stringField", "hello");
        scalarTypes.put("numberField", 4.25);
        scalarTypes.put("integerField", 42);
        scalarTypes.put("booleanField", true);
        scalarTypes.put("bytesField", "YWJj");
        scalarTypes.put("nullField", null);
        Assert.assertEquals("com.example.ScalarTypes@<ref>[stringField=hello,numberField=4.25,integerField=42,booleanField=true,nullField=<null>,bytesField={97,98,99}]", ToStringIT.toStringAndReplaceAddress(scalarTypes, scalarTypesClass));
    }

    @Test
    public void testComposites() throws Exception {
        Class<?> compositeTypesClass = schemaRule.generateAndCompile("/schema/toString/compositeTypes.json", "com.example").loadClass("com.example.CompositeTypes");
        Assert.assertEquals("com.example.CompositeTypes@<ref>[mapField=<null>,objectField=<null>,arrayField=[],uniqueArrayField=[]]", ToStringIT.toStringAndReplaceAddress(Collections.emptyMap(), compositeTypesClass));
        Map<String, Integer> intPair = new HashMap<>();
        intPair.put("l", 0);
        intPair.put("r", 1);
        Map<String, Object> compositeTypes = new HashMap<>();
        compositeTypes.put("mapField", Collections.singletonMap("intPair", intPair));
        compositeTypes.put("objectField", intPair);
        compositeTypes.put("arrayField", Collections.singleton(intPair));
        compositeTypes.put("uniqueArrayField", Collections.singleton(intPair));
        Assert.assertEquals(("com.example.CompositeTypes@<ref>" + ((("[mapField={intPair=com.example.IntPair@<ref>[l=0,r=1]}" + ",objectField=com.example.IntPair@<ref>[l=0,r=1]") + ",arrayField=[com.example.IntPair@<ref>[l=0,r=1]]") + ",uniqueArrayField=[com.example.IntPair@<ref>[l=0,r=1]]]")), ToStringIT.toStringAndReplaceAddress(compositeTypes, compositeTypesClass));
    }

    @Test
    public void testArrayOfArrays() throws Exception {
        Class<?> arrayOfArraysClass = schemaRule.generateAndCompile("/schema/toString/arrayOfArrays.json", "com.example").loadClass("com.example.ArrayOfArrays");
        Map<String, ?> arrayOfNullArrays = Collections.singletonMap("grid", Arrays.asList(null, null));
        Assert.assertEquals("com.example.ArrayOfArrays@<ref>[grid=[null, null]]", ToStringIT.toStringAndReplaceAddress(arrayOfNullArrays, arrayOfArraysClass));
        Map<String, ?> arrayOfArrays = Collections.singletonMap("grid", Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9)));
        Assert.assertEquals("com.example.ArrayOfArrays@<ref>[grid=[[1.0, 2.0, 3.0], [4.0, 5.0, 6.0], [7.0, 8.0, 9.0]]]", ToStringIT.toStringAndReplaceAddress(arrayOfArrays, arrayOfArraysClass));
    }

    @Test
    public void testInheritance() throws Exception {
        Class<?> squareClass = schemaRule.generateAndCompile("/schema/toString/square.json", "com.example").loadClass("com.example.Square");
        Map<String, Object> square = new HashMap<>();
        square.put("sides", 4);
        square.put("diagonals", Arrays.asList(Math.sqrt(2.0), Math.sqrt(2.0)));
        square.put("length", 1.0);
        Assert.assertEquals("com.example.Square@<ref>[sides=4,diagonals=[1.4142135623730951, 1.4142135623730951],length=1.0]", ToStringIT.toStringAndReplaceAddress(square, squareClass));
    }
}

