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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Collection of functional tests for DOM tree based type adapters.
 */
public class TreeTypeAdaptersTest extends TestCase {
    private static final TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Student> STUDENT1_ID = new TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Student>("5", TreeTypeAdaptersTest.Student.class);

    private static final TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Student> STUDENT2_ID = new TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Student>("6", TreeTypeAdaptersTest.Student.class);

    private static final TreeTypeAdaptersTest.Student STUDENT1 = new TreeTypeAdaptersTest.Student(TreeTypeAdaptersTest.STUDENT1_ID, "first");

    private static final TreeTypeAdaptersTest.Student STUDENT2 = new TreeTypeAdaptersTest.Student(TreeTypeAdaptersTest.STUDENT2_ID, "second");

    private static final Type TYPE_COURSE_HISTORY = new TypeToken<TreeTypeAdaptersTest.Course<TreeTypeAdaptersTest.HistoryCourse>>() {}.getType();

    private static final TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Course<TreeTypeAdaptersTest.HistoryCourse>> COURSE_ID = new TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Course<TreeTypeAdaptersTest.HistoryCourse>>("10", TreeTypeAdaptersTest.TYPE_COURSE_HISTORY);

    private Gson gson;

    private TreeTypeAdaptersTest.Course<TreeTypeAdaptersTest.HistoryCourse> course;

    public void testSerializeId() {
        String json = gson.toJson(course, TreeTypeAdaptersTest.TYPE_COURSE_HISTORY);
        TestCase.assertTrue(json.contains(String.valueOf(TreeTypeAdaptersTest.COURSE_ID.getValue())));
        TestCase.assertTrue(json.contains(String.valueOf(TreeTypeAdaptersTest.STUDENT1_ID.getValue())));
        TestCase.assertTrue(json.contains(String.valueOf(TreeTypeAdaptersTest.STUDENT2_ID.getValue())));
    }

    public void testDeserializeId() {
        String json = "{courseId:1,students:[{id:1,name:'first'},{id:6,name:'second'}]," + "numAssignments:4,assignment:{}}";
        TreeTypeAdaptersTest.Course<TreeTypeAdaptersTest.HistoryCourse> target = gson.fromJson(json, TreeTypeAdaptersTest.TYPE_COURSE_HISTORY);
        TestCase.assertEquals("1", target.getStudents().get(0).id.getValue());
        TestCase.assertEquals("6", target.getStudents().get(1).id.getValue());
        TestCase.assertEquals("1", target.getId().getValue());
    }

    private static final class Id<R> {
        final String value;

        @SuppressWarnings("unused")
        final Type typeOfId;

        private Id(String value, Type typeOfId) {
            this.value = value;
            this.typeOfId = typeOfId;
        }

        public String getValue() {
            return value;
        }
    }

    private static final class IdTreeTypeAdapter implements JsonDeserializer<TreeTypeAdaptersTest.Id<?>> , JsonSerializer<TreeTypeAdaptersTest.Id<?>> {
        @SuppressWarnings("rawtypes")
        @Override
        public TreeTypeAdaptersTest.Id<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!(typeOfT instanceof ParameterizedType)) {
                throw new JsonParseException(("Id of unknown type: " + typeOfT));
            }
            ParameterizedType parameterizedType = ((ParameterizedType) (typeOfT));
            // Since Id takes only one TypeVariable, the actual type corresponding to the first
            // TypeVariable is the Type we are looking for
            Type typeOfId = parameterizedType.getActualTypeArguments()[0];
            return new TreeTypeAdaptersTest.Id(json.getAsString(), typeOfId);
        }

        @Override
        public JsonElement serialize(TreeTypeAdaptersTest.Id<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }

    @SuppressWarnings("unused")
    private static class Student {
        TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Student> id;

        String name;

        private Student() {
            this(null, null);
        }

        public Student(TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Student> id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @SuppressWarnings("unused")
    private static class Course<T> {
        final List<TreeTypeAdaptersTest.Student> students;

        private final TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Course<T>> courseId;

        private final int numAssignments;

        private final TreeTypeAdaptersTest.Assignment<T> assignment;

        private Course() {
            this(null, 0, null, new ArrayList<TreeTypeAdaptersTest.Student>());
        }

        public Course(TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Course<T>> courseId, int numAssignments, TreeTypeAdaptersTest.Assignment<T> assignment, List<TreeTypeAdaptersTest.Student> players) {
            this.courseId = courseId;
            this.numAssignments = numAssignments;
            this.assignment = assignment;
            this.students = players;
        }

        public TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Course<T>> getId() {
            return courseId;
        }

        List<TreeTypeAdaptersTest.Student> getStudents() {
            return students;
        }
    }

    @SuppressWarnings("unused")
    private static class Assignment<T> {
        private final TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Assignment<T>> id;

        private final T data;

        private Assignment() {
            this(null, null);
        }

        public Assignment(TreeTypeAdaptersTest.Id<TreeTypeAdaptersTest.Assignment<T>> id, T data) {
            this.id = id;
            this.data = data;
        }
    }

    @SuppressWarnings("unused")
    private static class HistoryCourse {
        int numClasses;
    }
}

