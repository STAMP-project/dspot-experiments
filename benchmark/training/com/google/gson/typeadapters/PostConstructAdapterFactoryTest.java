/**
 * Copyright (C) 2016 Gson Authors
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
package com.google.gson.typeadapters;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import junit.framework.TestCase;


public class PostConstructAdapterFactoryTest extends TestCase {
    public void test() throws Exception {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new PostConstructAdapterFactory()).create();
        gson.fromJson("{\"bread\": \"white\", \"cheese\": \"cheddar\"}", PostConstructAdapterFactoryTest.Sandwich.class);
        try {
            gson.fromJson("{\"bread\": \"cheesey bread\", \"cheese\": \"swiss\"}", PostConstructAdapterFactoryTest.Sandwich.class);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
            TestCase.assertEquals("too cheesey", expected.getMessage());
        }
    }

    public void testList() {
        PostConstructAdapterFactoryTest.MultipleSandwiches sandwiches = new PostConstructAdapterFactoryTest.MultipleSandwiches(Arrays.asList(new PostConstructAdapterFactoryTest.Sandwich("white", "cheddar"), new PostConstructAdapterFactoryTest.Sandwich("whole wheat", "swiss")));
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new PostConstructAdapterFactory()).create();
        // Throws NullPointerException without the fix in https://github.com/google/gson/pull/1103
        String json = gson.toJson(sandwiches);
        TestCase.assertEquals("{\"sandwiches\":[{\"bread\":\"white\",\"cheese\":\"cheddar\"},{\"bread\":\"whole wheat\",\"cheese\":\"swiss\"}]}", json);
        PostConstructAdapterFactoryTest.MultipleSandwiches sandwichesFromJson = gson.fromJson(json, PostConstructAdapterFactoryTest.MultipleSandwiches.class);
        TestCase.assertEquals(sandwiches, sandwichesFromJson);
    }

    static class Sandwich {
        public String bread;

        public String cheese;

        public Sandwich(String bread, String cheese) {
            this.bread = bread;
            this.cheese = cheese;
        }

        @PostConstruct
        private void validate() {
            if ((bread.equals("cheesey bread")) && ((cheese) != null)) {
                throw new IllegalArgumentException("too cheesey");
            }
        }

        public boolean equals(Object o) {
            if (o == (this)) {
                return true;
            }
            if (!(o instanceof PostConstructAdapterFactoryTest.Sandwich)) {
                return false;
            }
            final PostConstructAdapterFactoryTest.Sandwich other = ((PostConstructAdapterFactoryTest.Sandwich) (o));
            if ((this.bread) == null ? (other.bread) != null : !(this.bread.equals(other.bread))) {
                return false;
            }
            if ((this.cheese) == null ? (other.cheese) != null : !(this.cheese.equals(other.cheese))) {
                return false;
            }
            return true;
        }
    }

    static class MultipleSandwiches {
        public List<PostConstructAdapterFactoryTest.Sandwich> sandwiches;

        public MultipleSandwiches(List<PostConstructAdapterFactoryTest.Sandwich> sandwiches) {
            this.sandwiches = sandwiches;
        }

        public boolean equals(Object o) {
            if (o == (this)) {
                return true;
            }
            if (!(o instanceof PostConstructAdapterFactoryTest.MultipleSandwiches)) {
                return false;
            }
            final PostConstructAdapterFactoryTest.MultipleSandwiches other = ((PostConstructAdapterFactoryTest.MultipleSandwiches) (o));
            if ((this.sandwiches) == null ? (other.sandwiches) != null : !(this.sandwiches.equals(other.sandwiches))) {
                return false;
            }
            return true;
        }
    }
}

