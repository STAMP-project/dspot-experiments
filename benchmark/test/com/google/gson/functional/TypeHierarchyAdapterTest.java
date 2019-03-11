/**
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import junit.framework.TestCase;


/**
 * Test that the hierarchy adapter works when subtypes are used.
 */
public final class TypeHierarchyAdapterTest extends TestCase {
    public void testTypeHierarchy() {
        TypeHierarchyAdapterTest.Manager andy = new TypeHierarchyAdapterTest.Manager();
        andy.userid = "andy";
        andy.startDate = 2005;
        andy.minions = new TypeHierarchyAdapterTest.Employee[]{ new TypeHierarchyAdapterTest.Employee("inder", 2007), new TypeHierarchyAdapterTest.Employee("joel", 2006), new TypeHierarchyAdapterTest.Employee("jesse", 2006) };
        TypeHierarchyAdapterTest.CEO eric = new TypeHierarchyAdapterTest.CEO();
        eric.userid = "eric";
        eric.startDate = 2001;
        eric.assistant = new TypeHierarchyAdapterTest.Employee("jerome", 2006);
        eric.minions = new TypeHierarchyAdapterTest.Employee[]{ new TypeHierarchyAdapterTest.Employee("larry", 1998), new TypeHierarchyAdapterTest.Employee("sergey", 1998), andy };
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(TypeHierarchyAdapterTest.Employee.class, new TypeHierarchyAdapterTest.EmployeeAdapter()).setPrettyPrinting().create();
        TypeHierarchyAdapterTest.Company company = new TypeHierarchyAdapterTest.Company();
        company.ceo = eric;
        String json = gson.toJson(company, TypeHierarchyAdapterTest.Company.class);
        TestCase.assertEquals(("{\n" + (((((((((((((((((((((((((((((((((((("  \"ceo\": {\n" + "    \"userid\": \"eric\",\n") + "    \"startDate\": 2001,\n") + "    \"minions\": [\n") + "      {\n") + "        \"userid\": \"larry\",\n") + "        \"startDate\": 1998\n") + "      },\n") + "      {\n") + "        \"userid\": \"sergey\",\n") + "        \"startDate\": 1998\n") + "      },\n") + "      {\n") + "        \"userid\": \"andy\",\n") + "        \"startDate\": 2005,\n") + "        \"minions\": [\n") + "          {\n") + "            \"userid\": \"inder\",\n") + "            \"startDate\": 2007\n") + "          },\n") + "          {\n") + "            \"userid\": \"joel\",\n") + "            \"startDate\": 2006\n") + "          },\n") + "          {\n") + "            \"userid\": \"jesse\",\n") + "            \"startDate\": 2006\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"assistant\": {\n") + "      \"userid\": \"jerome\",\n") + "      \"startDate\": 2006\n") + "    }\n") + "  }\n") + "}")), json);
        TypeHierarchyAdapterTest.Company copied = gson.fromJson(json, TypeHierarchyAdapterTest.Company.class);
        TestCase.assertEquals(json, gson.toJson(copied, TypeHierarchyAdapterTest.Company.class));
        TestCase.assertEquals(copied.ceo.userid, company.ceo.userid);
        TestCase.assertEquals(copied.ceo.assistant.userid, company.ceo.assistant.userid);
        TestCase.assertEquals(copied.ceo.minions[0].userid, company.ceo.minions[0].userid);
        TestCase.assertEquals(copied.ceo.minions[1].userid, company.ceo.minions[1].userid);
        TestCase.assertEquals(copied.ceo.minions[2].userid, company.ceo.minions[2].userid);
        TestCase.assertEquals(((TypeHierarchyAdapterTest.Manager) (copied.ceo.minions[2])).minions[0].userid, ((TypeHierarchyAdapterTest.Manager) (company.ceo.minions[2])).minions[0].userid);
        TestCase.assertEquals(((TypeHierarchyAdapterTest.Manager) (copied.ceo.minions[2])).minions[1].userid, ((TypeHierarchyAdapterTest.Manager) (company.ceo.minions[2])).minions[1].userid);
    }

    public void testRegisterSuperTypeFirst() {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(TypeHierarchyAdapterTest.Employee.class, new TypeHierarchyAdapterTest.EmployeeAdapter()).registerTypeHierarchyAdapter(TypeHierarchyAdapterTest.Manager.class, new TypeHierarchyAdapterTest.ManagerAdapter()).create();
        TypeHierarchyAdapterTest.Manager manager = new TypeHierarchyAdapterTest.Manager();
        manager.userid = "inder";
        String json = gson.toJson(manager, TypeHierarchyAdapterTest.Manager.class);
        TestCase.assertEquals("\"inder\"", json);
        TypeHierarchyAdapterTest.Manager copied = gson.fromJson(json, TypeHierarchyAdapterTest.Manager.class);
        TestCase.assertEquals(manager.userid, copied.userid);
    }

    /**
     * This behaviour changed in Gson 2.1; it used to throw.
     */
    public void testRegisterSubTypeFirstAllowed() {
        new GsonBuilder().registerTypeHierarchyAdapter(TypeHierarchyAdapterTest.Manager.class, new TypeHierarchyAdapterTest.ManagerAdapter()).registerTypeHierarchyAdapter(TypeHierarchyAdapterTest.Employee.class, new TypeHierarchyAdapterTest.EmployeeAdapter()).create();
    }

    static class ManagerAdapter implements JsonDeserializer<TypeHierarchyAdapterTest.Manager> , JsonSerializer<TypeHierarchyAdapterTest.Manager> {
        @Override
        public TypeHierarchyAdapterTest.Manager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            TypeHierarchyAdapterTest.Manager result = new TypeHierarchyAdapterTest.Manager();
            result.userid = json.getAsString();
            return result;
        }

        @Override
        public JsonElement serialize(TypeHierarchyAdapterTest.Manager src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.userid);
        }
    }

    static class EmployeeAdapter implements JsonDeserializer<TypeHierarchyAdapterTest.Employee> , JsonSerializer<TypeHierarchyAdapterTest.Employee> {
        @Override
        public JsonElement serialize(TypeHierarchyAdapterTest.Employee employee, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("userid", context.serialize(employee.userid, String.class));
            result.add("startDate", context.serialize(employee.startDate, long.class));
            if (employee instanceof TypeHierarchyAdapterTest.Manager) {
                result.add("minions", context.serialize(((TypeHierarchyAdapterTest.Manager) (employee)).minions, TypeHierarchyAdapterTest.Employee[].class));
                if (employee instanceof TypeHierarchyAdapterTest.CEO) {
                    result.add("assistant", context.serialize(((TypeHierarchyAdapterTest.CEO) (employee)).assistant, TypeHierarchyAdapterTest.Employee.class));
                }
            }
            return result;
        }

        @Override
        public TypeHierarchyAdapterTest.Employee deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            TypeHierarchyAdapterTest.Employee result = null;
            // if the employee has an assistant, she must be the CEO
            JsonElement assistant = object.get("assistant");
            if (assistant != null) {
                result = new TypeHierarchyAdapterTest.CEO();
                ((TypeHierarchyAdapterTest.CEO) (result)).assistant = context.deserialize(assistant, TypeHierarchyAdapterTest.Employee.class);
            }
            // only managers have minions
            JsonElement minons = object.get("minions");
            if (minons != null) {
                if (result == null) {
                    result = new TypeHierarchyAdapterTest.Manager();
                }
                ((TypeHierarchyAdapterTest.Manager) (result)).minions = context.deserialize(minons, TypeHierarchyAdapterTest.Employee[].class);
            }
            if (result == null) {
                result = new TypeHierarchyAdapterTest.Employee();
            }
            result.userid = context.deserialize(object.get("userid"), String.class);
            result.startDate = context.<Long>deserialize(object.get("startDate"), long.class);
            return result;
        }
    }

    static class Employee {
        String userid;

        long startDate;

        Employee(String userid, long startDate) {
            this.userid = userid;
            this.startDate = startDate;
        }

        Employee() {
        }
    }

    static class Manager extends TypeHierarchyAdapterTest.Employee {
        TypeHierarchyAdapterTest.Employee[] minions;
    }

    static class CEO extends TypeHierarchyAdapterTest.Manager {
        TypeHierarchyAdapterTest.Employee assistant;
    }

    static class Company {
        TypeHierarchyAdapterTest.CEO ceo;
    }
}

