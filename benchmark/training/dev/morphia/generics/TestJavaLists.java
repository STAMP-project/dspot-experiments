/**
 * Copyright (c) 2008-2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.morphia.generics;


import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestJavaLists extends TestBase {
    @Test
    public void emptyModel() {
        getMorphia().getMapper().getOptions().setStoreEmpties(true);
        getMorphia().getMapper().getOptions().setStoreNulls(false);
        TestJavaLists.TestEmptyModel model = new TestJavaLists.TestEmptyModel();
        model.text = "text";
        model.wrapped = new TestJavaLists.TestEmptyModel.Wrapped();
        model.wrapped.text = "textWrapper";
        getDs().save(model);
        TestJavaLists.TestEmptyModel model2 = getDs().find(TestJavaLists.TestEmptyModel.class).filter("id", model.id).find(new FindOptions().limit(1)).next();
        Assert.assertNull(model.wrapped.others);
        Assert.assertNull(model2.wrapped.others);
    }

    @Test
    public void jsonObjects() {
        getMorphia().map(JsonList.class);
        populate(new com.mongodb.BasicDBObject(Document.parse("{\"jsonObject\": { \"array\": [{ \"foo\": \"bar\" }]},}")));
        populate(new com.mongodb.BasicDBObject(Document.parse("{\"jsonList\" : [ 1, \"string\", true, null ],}")));
        populate(new com.mongodb.BasicDBObject(Document.parse("{\"jsonList\" : [ {  \"foo\" : \"bar\" }],}")));
    }

    @Test
    public void mapperTest() {
        getMorphia().map(TestJavaLists.Employee.class);
        for (boolean nulls : new boolean[]{ true, false }) {
            for (boolean empties : new boolean[]{ true, false }) {
                getMorphia().getMapper().getOptions().setStoreNulls(nulls);
                getMorphia().getMapper().getOptions().setStoreEmpties(empties);
                empties();
            }
        }
    }

    @Entity
    static class TestEmptyModel {
        @Id
        private ObjectId id;

        private String text;

        private TestJavaLists.TestEmptyModel.Wrapped wrapped;

        private static class Wrapped {
            private List<TestJavaLists.TestEmptyModel.Wrapped> others;

            private String text;
        }
    }

    @Entity("employees")
    static class Employee {
        @Id
        private ObjectId id;

        private List<Float> floatList;

        private List<Byte> byteList;
    }
}

