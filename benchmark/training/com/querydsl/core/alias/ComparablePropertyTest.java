/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.alias;


import org.junit.Test;


public class ComparablePropertyTest {
    public static class Entity {
        private ComparablePropertyTest.ComparableType property;

        public ComparablePropertyTest.ComparableType getProperty() {
            return property;
        }

        public void setProperty(ComparablePropertyTest.ComparableType property) {
            this.property = property;
        }
    }

    public static class ComparableType implements Comparable<ComparablePropertyTest.ComparableType> {
        @Override
        public int compareTo(ComparablePropertyTest.ComparableType o) {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == (this)) {
                return true;
            } else
                if (o instanceof ComparablePropertyTest.ComparableType) {
                    return true;
                } else {
                    return false;
                }

        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    @Test
    public void test() {
        ComparablePropertyTest.Entity entity = Alias.alias(ComparablePropertyTest.Entity.class);
        Alias.$(entity.getProperty());
    }
}

