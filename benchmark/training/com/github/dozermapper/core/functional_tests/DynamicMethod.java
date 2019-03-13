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


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DynamicMethod extends AbstractFunctionalTest {
    /**
     * Caused by: java.lang.IllegalAccessException - with modifiers "public" #153
     *
     * @see <a href="https://github.com/DozerMapper/dozer/issues/153"/>
     */
    @Test
    public void testCanMapAnonymousClass() {
        // Arrange: Create an anonymous class, overriding the default getter
        final String expectedName = "Helbert Rios";
        DynamicMethod.Person anonymousPerson = new DynamicMethod.Person() {
            public String getName() {
                return expectedName;
            }
        };
        anonymousPerson.setId(1);
        // Act: Map the anonymous class to the base class
        DynamicMethod.Person mappedPerson = mapper.map(anonymousPerson, DynamicMethod.Person.class);
        // Assert: Mapping should work instead of mapper.map throwing an exception
        // (see issue #153)
        Assert.assertThat(mappedPerson.id, CoreMatchers.is(1));
        Assert.assertThat(mappedPerson.name, CoreMatchers.is(expectedName));
    }

    public static class Person {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

