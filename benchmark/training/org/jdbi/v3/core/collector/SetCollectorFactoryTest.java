/**
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
package org.jdbi.v3.core.collector;


import org.junit.Test;


public class SetCollectorFactoryTest {
    private CollectorFactory factory = new SetCollectorFactory();

    @Test
    public void collections() {
        testCollectionType(new org.jdbi.v3.core.generic.GenericType<java.util.Set<String>>() {});
        testCollectionType(new org.jdbi.v3.core.generic.GenericType<java.util.HashSet<String>>() {});
        testCollectionType(new org.jdbi.v3.core.generic.GenericType<java.util.SortedSet<String>>() {});
        testCollectionType(new org.jdbi.v3.core.generic.GenericType<java.util.TreeSet<String>>() {});
    }
}

