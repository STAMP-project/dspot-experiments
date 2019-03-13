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


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Test;


public class MapCollectorFactoryTest {
    private CollectorFactory factory = new MapCollectorFactory();

    @Test
    public void maps() {
        testMapType(new org.jdbi.v3.core.generic.GenericType<Map<Long, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<HashMap<Long, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<LinkedHashMap<Long, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<SortedMap<Long, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<TreeMap<Long, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<ConcurrentMap<Long, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<ConcurrentHashMap<Long, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<WeakHashMap<Long, String>>() {});
    }
}

