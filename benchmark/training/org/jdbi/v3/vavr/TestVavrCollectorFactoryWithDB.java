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
package org.jdbi.v3.vavr;


import io.vavr.collection.HashMap;
import io.vavr.collection.HashMultimap;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.LinkedHashMultimap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Multimap;
import io.vavr.collection.Seq;
import io.vavr.collection.TreeMap;
import io.vavr.collection.TreeMultimap;
import org.jdbi.v3.core.result.ResultSetException;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestVavrCollectorFactoryWithDB {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugins();

    private Seq<Integer> expected = List.range(0, 10);

    private Map<Integer, String> expectedMap = expected.toMap(( i) -> new Tuple2<>(i, (i + "asString")));

    @Test
    public void testToConcreteCollectionTypesShouldSucceed() {
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.Array<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.Vector<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<List<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.Stream<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.Queue<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.PriorityQueue<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.HashSet<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.LinkedHashSet<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.TreeSet<Integer>>() {});
    }

    @Test
    public void testToAbstractCollectionTypesShouldSucceed() {
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.Traversable<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<Seq<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.IndexedSeq<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.LinearSeq<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.Set<Integer>>() {});
        testType(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.SortedSet<Integer>>() {});
    }

    @Test
    public void testMapCollector() {
        testMapType(new org.jdbi.v3.core.generic.GenericType<HashMap<Integer, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<LinkedHashMap<Integer, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<TreeMap<Integer, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<HashMultimap<Integer, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<LinkedHashMultimap<Integer, String>>() {});
        testMapType(new org.jdbi.v3.core.generic.GenericType<TreeMultimap<Integer, String>>() {});
    }

    @Test
    public void testMapCollectorReversedShouldFail() {
        assertThatThrownBy(() -> dbRule.getSharedHandle().createQuery("select intValue, name from something").collectInto(new GenericType<HashMap<String, Integer>>() {})).isInstanceOf(ResultSetException.class);
    }

    @Test
    public void testMultimapValuesAddAnotherDataSetShouldHave2ValuesForEachKey() {
        final int offset = 10;
        for (Integer i : expected) {
            dbRule.getSharedHandle().execute("insert into something(name, intValue) values (?, ?)", ((Integer.toString((i + offset))) + "asString"), i);
        }
        Multimap<Integer, String> result = dbRule.getSharedHandle().createQuery("select intValue, name from something").collectInto(new org.jdbi.v3.core.generic.GenericType<Multimap<Integer, String>>() {});
        assertThat(result).hasSize(((expected.size()) * 2));
        expected.forEach(( i) -> assertThat(result.apply(i)).containsOnlyElementsOf(List.of((i + "asString"), ((i + 10) + "asString"))));
    }
}

