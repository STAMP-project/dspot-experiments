/**
 * Copyright (C) 2011 Google Inc.
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
package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public final class StreamingTypeAdaptersTest extends TestCase {
    private Gson miniGson = new GsonBuilder().create();

    private TypeAdapter<StreamingTypeAdaptersTest.Truck> truckAdapter = miniGson.getAdapter(StreamingTypeAdaptersTest.Truck.class);

    private TypeAdapter<Map<String, Double>> mapAdapter = miniGson.getAdapter(new TypeToken<Map<String, Double>>() {});

    public void testSerialize() {
        StreamingTypeAdaptersTest.Truck truck = new StreamingTypeAdaptersTest.Truck();
        truck.passengers = Arrays.asList(new StreamingTypeAdaptersTest.Person("Jesse", 29), new StreamingTypeAdaptersTest.Person("Jodie", 29));
        truck.horsePower = 300;
        TestCase.assertEquals(("{'horsePower':300.0," + "'passengers':[{'age':29,'name':'Jesse'},{'age':29,'name':'Jodie'}]}"), truckAdapter.toJson(truck).replace('\"', '\''));
    }

    public void testDeserialize() throws IOException {
        String json = "{'horsePower':300.0," + "'passengers':[{'age':29,'name':'Jesse'},{'age':29,'name':'Jodie'}]}";
        StreamingTypeAdaptersTest.Truck truck = truckAdapter.fromJson(json.replace('\'', '\"'));
        TestCase.assertEquals(300.0, truck.horsePower);
        TestCase.assertEquals(Arrays.asList(new StreamingTypeAdaptersTest.Person("Jesse", 29), new StreamingTypeAdaptersTest.Person("Jodie", 29)), truck.passengers);
    }

    public void testSerializeNullField() {
        StreamingTypeAdaptersTest.Truck truck = new StreamingTypeAdaptersTest.Truck();
        truck.passengers = null;
        TestCase.assertEquals("{'horsePower':0.0,'passengers':null}", truckAdapter.toJson(truck).replace('\"', '\''));
    }

    public void testDeserializeNullField() throws IOException {
        StreamingTypeAdaptersTest.Truck truck = truckAdapter.fromJson("{'horsePower':0.0,'passengers':null}".replace('\'', '\"'));
        TestCase.assertNull(truck.passengers);
    }

    public void testSerializeNullObject() {
        StreamingTypeAdaptersTest.Truck truck = new StreamingTypeAdaptersTest.Truck();
        truck.passengers = Arrays.asList(((StreamingTypeAdaptersTest.Person) (null)));
        TestCase.assertEquals("{'horsePower':0.0,'passengers':[null]}", truckAdapter.toJson(truck).replace('\"', '\''));
    }

    public void testDeserializeNullObject() throws IOException {
        StreamingTypeAdaptersTest.Truck truck = truckAdapter.fromJson("{'horsePower':0.0,'passengers':[null]}".replace('\'', '\"'));
        TestCase.assertEquals(Arrays.asList(((StreamingTypeAdaptersTest.Person) (null))), truck.passengers);
    }

    public void testSerializeWithCustomTypeAdapter() {
        usePersonNameAdapter();
        StreamingTypeAdaptersTest.Truck truck = new StreamingTypeAdaptersTest.Truck();
        truck.passengers = Arrays.asList(new StreamingTypeAdaptersTest.Person("Jesse", 29), new StreamingTypeAdaptersTest.Person("Jodie", 29));
        TestCase.assertEquals("{'horsePower':0.0,'passengers':['Jesse','Jodie']}", truckAdapter.toJson(truck).replace('\"', '\''));
    }

    public void testDeserializeWithCustomTypeAdapter() throws IOException {
        usePersonNameAdapter();
        StreamingTypeAdaptersTest.Truck truck = truckAdapter.fromJson("{'horsePower':0.0,'passengers':['Jesse','Jodie']}".replace('\'', '\"'));
        TestCase.assertEquals(Arrays.asList(new StreamingTypeAdaptersTest.Person("Jesse", (-1)), new StreamingTypeAdaptersTest.Person("Jodie", (-1))), truck.passengers);
    }

    public void testSerializeMap() {
        Map<String, Double> map = new LinkedHashMap<String, Double>();
        map.put("a", 5.0);
        map.put("b", 10.0);
        TestCase.assertEquals("{'a':5.0,'b':10.0}", mapAdapter.toJson(map).replace('"', '\''));
    }

    public void testDeserializeMap() throws IOException {
        Map<String, Double> map = new LinkedHashMap<String, Double>();
        map.put("a", 5.0);
        map.put("b", 10.0);
        TestCase.assertEquals(map, mapAdapter.fromJson("{'a':5.0,'b':10.0}".replace('\'', '\"')));
    }

    public void testSerialize1dArray() {
        TypeAdapter<double[]> arrayAdapter = miniGson.getAdapter(new TypeToken<double[]>() {});
        TestCase.assertEquals("[1.0,2.0,3.0]", arrayAdapter.toJson(new double[]{ 1.0, 2.0, 3.0 }));
    }

    public void testDeserialize1dArray() throws IOException {
        TypeAdapter<double[]> arrayAdapter = miniGson.getAdapter(new TypeToken<double[]>() {});
        double[] array = arrayAdapter.fromJson("[1.0,2.0,3.0]");
        TestCase.assertTrue(Arrays.toString(array), Arrays.equals(new double[]{ 1.0, 2.0, 3.0 }, array));
    }

    public void testSerialize2dArray() {
        TypeAdapter<double[][]> arrayAdapter = miniGson.getAdapter(new TypeToken<double[][]>() {});
        double[][] array = new double[][]{ new double[]{ 1.0, 2.0 }, new double[]{ 3.0 } };
        TestCase.assertEquals("[[1.0,2.0],[3.0]]", arrayAdapter.toJson(array));
    }

    public void testDeserialize2dArray() throws IOException {
        TypeAdapter<double[][]> arrayAdapter = miniGson.getAdapter(new TypeToken<double[][]>() {});
        double[][] array = arrayAdapter.fromJson("[[1.0,2.0],[3.0]]");
        double[][] expected = new double[][]{ new double[]{ 1.0, 2.0 }, new double[]{ 3.0 } };
        TestCase.assertTrue(Arrays.toString(array), Arrays.deepEquals(expected, array));
    }

    public void testNullSafe() {
        TypeAdapter<StreamingTypeAdaptersTest.Person> typeAdapter = new TypeAdapter<StreamingTypeAdaptersTest.Person>() {
            @Override
            public StreamingTypeAdaptersTest.Person read(JsonReader in) throws IOException {
                String[] values = in.nextString().split(",");
                return new StreamingTypeAdaptersTest.Person(values[0], Integer.parseInt(values[1]));
            }

            public void write(JsonWriter out, StreamingTypeAdaptersTest.Person person) throws IOException {
                out.value((((person.name) + ",") + (person.age)));
            }
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(StreamingTypeAdaptersTest.Person.class, typeAdapter).create();
        StreamingTypeAdaptersTest.Truck truck = new StreamingTypeAdaptersTest.Truck();
        truck.horsePower = 1.0;
        truck.passengers = new ArrayList<StreamingTypeAdaptersTest.Person>();
        truck.passengers.add(null);
        truck.passengers.add(new StreamingTypeAdaptersTest.Person("jesse", 30));
        try {
            gson.toJson(truck, StreamingTypeAdaptersTest.Truck.class);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        String json = "{horsePower:1.0,passengers:[null,'jesse,30']}";
        try {
            gson.fromJson(json, StreamingTypeAdaptersTest.Truck.class);
            TestCase.fail();
        } catch (JsonSyntaxException expected) {
        }
        gson = new GsonBuilder().registerTypeAdapter(StreamingTypeAdaptersTest.Person.class, typeAdapter.nullSafe()).create();
        TestCase.assertEquals("{\"horsePower\":1.0,\"passengers\":[null,\"jesse,30\"]}", gson.toJson(truck, StreamingTypeAdaptersTest.Truck.class));
        truck = gson.fromJson(json, StreamingTypeAdaptersTest.Truck.class);
        TestCase.assertEquals(1.0, truck.horsePower);
        TestCase.assertNull(truck.passengers.get(0));
        TestCase.assertEquals("jesse", truck.passengers.get(1).name);
    }

    public void testSerializeRecursive() {
        TypeAdapter<StreamingTypeAdaptersTest.Node> nodeAdapter = miniGson.getAdapter(StreamingTypeAdaptersTest.Node.class);
        StreamingTypeAdaptersTest.Node root = new StreamingTypeAdaptersTest.Node("root");
        root.left = new StreamingTypeAdaptersTest.Node("left");
        root.right = new StreamingTypeAdaptersTest.Node("right");
        TestCase.assertEquals(("{'label':'root'," + ("'left':{'label':'left','left':null,'right':null}," + "'right':{'label':'right','left':null,'right':null}}")), nodeAdapter.toJson(root).replace('"', '\''));
    }

    public void testFromJsonTree() {
        JsonObject truckObject = new JsonObject();
        truckObject.add("horsePower", new JsonPrimitive(300));
        JsonArray passengersArray = new JsonArray();
        JsonObject jesseObject = new JsonObject();
        jesseObject.add("age", new JsonPrimitive(30));
        jesseObject.add("name", new JsonPrimitive("Jesse"));
        passengersArray.add(jesseObject);
        truckObject.add("passengers", passengersArray);
        StreamingTypeAdaptersTest.Truck truck = truckAdapter.fromJsonTree(truckObject);
        TestCase.assertEquals(300.0, truck.horsePower);
        TestCase.assertEquals(Arrays.asList(new StreamingTypeAdaptersTest.Person("Jesse", 30)), truck.passengers);
    }

    static class Truck {
        double horsePower;

        List<StreamingTypeAdaptersTest.Person> passengers = Collections.emptyList();
    }

    static class Person {
        int age;

        String name;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            return ((o instanceof StreamingTypeAdaptersTest.Person) && (((StreamingTypeAdaptersTest.Person) (o)).name.equals(name))) && ((((StreamingTypeAdaptersTest.Person) (o)).age) == (age));
        }

        @Override
        public int hashCode() {
            return (name.hashCode()) ^ (age);
        }
    }

    static class Node {
        String label;

        StreamingTypeAdaptersTest.Node left;

        StreamingTypeAdaptersTest.Node right;

        Node(String label) {
            this.label = label;
        }
    }
}

