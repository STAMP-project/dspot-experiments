/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.beans.support;


import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link PropertyComparator}.
 *
 * @author Keith Donald
 * @author Chris Beams
 */
public class PropertyComparatorTests {
    @Test
    public void testPropertyComparator() {
        PropertyComparatorTests.Dog dog = new PropertyComparatorTests.Dog();
        dog.setNickName("mace");
        PropertyComparatorTests.Dog dog2 = new PropertyComparatorTests.Dog();
        dog2.setNickName("biscy");
        PropertyComparator<PropertyComparatorTests.Dog> c = new PropertyComparator("nickName", false, true);
        Assert.assertTrue(((c.compare(dog, dog2)) > 0));
        Assert.assertTrue(((c.compare(dog, dog)) == 0));
        Assert.assertTrue(((c.compare(dog2, dog)) < 0));
    }

    @Test
    public void testPropertyComparatorNulls() {
        PropertyComparatorTests.Dog dog = new PropertyComparatorTests.Dog();
        PropertyComparatorTests.Dog dog2 = new PropertyComparatorTests.Dog();
        PropertyComparator<PropertyComparatorTests.Dog> c = new PropertyComparator("nickName", false, true);
        Assert.assertTrue(((c.compare(dog, dog2)) == 0));
    }

    @Test
    public void testChainedComparators() {
        Comparator<PropertyComparatorTests.Dog> c = new PropertyComparator("lastName", false, true);
        PropertyComparatorTests.Dog dog1 = new PropertyComparatorTests.Dog();
        dog1.setFirstName("macy");
        dog1.setLastName("grayspots");
        PropertyComparatorTests.Dog dog2 = new PropertyComparatorTests.Dog();
        dog2.setFirstName("biscuit");
        dog2.setLastName("grayspots");
        Assert.assertTrue(((c.compare(dog1, dog2)) == 0));
        c = c.thenComparing(new PropertyComparator("firstName", false, true));
        Assert.assertTrue(((c.compare(dog1, dog2)) > 0));
        dog2.setLastName("konikk dog");
        Assert.assertTrue(((c.compare(dog2, dog1)) > 0));
    }

    @Test
    public void testChainedComparatorsReversed() {
        Comparator<PropertyComparatorTests.Dog> c = new PropertyComparator<PropertyComparatorTests.Dog>("lastName", false, true).thenComparing(new PropertyComparator("firstName", false, true));
        PropertyComparatorTests.Dog dog1 = new PropertyComparatorTests.Dog();
        dog1.setFirstName("macy");
        dog1.setLastName("grayspots");
        PropertyComparatorTests.Dog dog2 = new PropertyComparatorTests.Dog();
        dog2.setFirstName("biscuit");
        dog2.setLastName("grayspots");
        Assert.assertTrue(((c.compare(dog1, dog2)) > 0));
        c = c.reversed();
        Assert.assertTrue(((c.compare(dog1, dog2)) < 0));
    }

    private static class Dog implements Comparable<Object> {
        private String nickName;

        private String firstName;

        private String lastName;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public int compareTo(Object o) {
            return this.nickName.compareTo(((PropertyComparatorTests.Dog) (o)).nickName);
        }
    }
}

