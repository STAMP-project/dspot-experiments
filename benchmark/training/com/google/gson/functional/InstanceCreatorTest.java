/**
 * Copyright (C) 2009 Google Inc.
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
import com.google.gson.InstanceCreator;
import com.google.gson.common.TestTypes;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

import static com.google.gson.common.TestTypes.Sub.SUB_NAME;


/**
 * Functional Test exercising custom serialization only. When test applies to both
 * serialization and deserialization then add it to CustomTypeAdapterTest.
 *
 * @author Inderjeet Singh
 */
public class InstanceCreatorTest extends TestCase {
    public void testInstanceCreatorReturnsBaseType() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.Base.class, new InstanceCreator<TestTypes.Base>() {
            @Override
            public TestTypes.Base createInstance(Type type) {
                return new TestTypes.Base();
            }
        }).create();
        String json = "{baseName:'BaseRevised',subName:'Sub'}";
        TestTypes.Base base = gson.fromJson(json, TestTypes.Base.class);
        TestCase.assertEquals("BaseRevised", base.baseName);
    }

    public void testInstanceCreatorReturnsSubTypeForTopLevelObject() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.Base.class, new InstanceCreator<TestTypes.Base>() {
            @Override
            public TestTypes.Base createInstance(Type type) {
                return new TestTypes.Sub();
            }
        }).create();
        String json = "{baseName:'Base',subName:'SubRevised'}";
        TestTypes.Base base = gson.fromJson(json, TestTypes.Base.class);
        TestCase.assertTrue((base instanceof TestTypes.Sub));
        TestTypes.Sub sub = ((TestTypes.Sub) (base));
        TestCase.assertFalse("SubRevised".equals(sub.subName));
        TestCase.assertEquals(SUB_NAME, sub.subName);
    }

    public void testInstanceCreatorReturnsSubTypeForField() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.Base.class, new InstanceCreator<TestTypes.Base>() {
            @Override
            public TestTypes.Base createInstance(Type type) {
                return new TestTypes.Sub();
            }
        }).create();
        String json = "{base:{baseName:'Base',subName:'SubRevised'}}";
        TestTypes.ClassWithBaseField target = gson.fromJson(json, TestTypes.ClassWithBaseField.class);
        TestCase.assertTrue(((target.base) instanceof TestTypes.Sub));
        TestCase.assertEquals(SUB_NAME, ((TestTypes.Sub) (target.base)).subName);
    }

    // This regressed in Gson 2.0 and 2.1
    public void testInstanceCreatorForCollectionType() {
        @SuppressWarnings("serial")
        class SubArrayList<T> extends ArrayList<T> {}
        InstanceCreator<List<String>> listCreator = new InstanceCreator<List<String>>() {
            @Override
            public List<String> createInstance(Type type) {
                return new SubArrayList<String>();
            }
        };
        Type listOfStringType = new TypeToken<List<String>>() {}.getType();
        Gson gson = new GsonBuilder().registerTypeAdapter(listOfStringType, listCreator).create();
        List<String> list = gson.fromJson("[\"a\"]", listOfStringType);
        TestCase.assertEquals(SubArrayList.class, list.getClass());
    }
}

