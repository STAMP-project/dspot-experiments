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
package org.jdbi.v3.core.result;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestReducing {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething();

    @Test
    public void testReduceRowsWithSeed() {
        Map<Integer, TestReducing.SomethingWithLocations> result = dbRule.getSharedHandle().createQuery("SELECT something.id, name, location FROM something NATURAL JOIN something_location").reduceRows(new HashMap<Integer, TestReducing.SomethingWithLocations>(), ( map, rr) -> {
            map.computeIfAbsent(rr.getColumn("id", .class), ( id) -> new org.jdbi.v3.core.result.SomethingWithLocations(rr.getRow(.class))).locations.add(rr.getColumn("location", .class));
            return map;
        });
        assertThat(result).hasSize(2).containsEntry(1, new TestReducing.SomethingWithLocations(new Something(1, "tree")).at("outside")).containsEntry(2, new TestReducing.SomethingWithLocations(new Something(2, "apple")).at("tree").at("pie"));
    }

    @Test
    public void testCollectRows() {
        Iterable<TestReducing.SomethingWithLocations> result = dbRule.getSharedHandle().createQuery("SELECT something.id, name, location FROM something NATURAL JOIN something_location").collectRows(Collector.<RowView, Map<Integer, TestReducing.SomethingWithLocations>, Iterable<TestReducing.SomethingWithLocations>>of(LinkedHashMap::new, (Map<Integer, TestReducing.SomethingWithLocations> map,RowView rv) -> map.computeIfAbsent(rv.getColumn("id", Integer.class), ( id) -> new org.jdbi.v3.core.result.SomethingWithLocations(rv.getRow(.class))).locations.add(rv.getColumn("location", String.class)), ( a, b) -> {
            throw new UnsupportedOperationException("shouldn't use combiner");
        }, Map::values));
        assertThat(result).containsExactly(new TestReducing.SomethingWithLocations(new Something(1, "tree")).at("outside"), new TestReducing.SomethingWithLocations(new Something(2, "apple")).at("tree").at("pie"));
    }

    @Test
    public void testReduceRows() {
        List<TestReducing.SomethingWithLocations> result = dbRule.getSharedHandle().createQuery("SELECT something.id, name, location FROM something NATURAL JOIN something_location").reduceRows((Map<Integer, TestReducing.SomethingWithLocations> map,RowView rv) -> map.computeIfAbsent(rv.getColumn("id", .class), ( id) -> new org.jdbi.v3.core.result.SomethingWithLocations(rv.getRow(.class))).locations.add(rv.getColumn("location", .class))).collect(Collectors.toList());
        assertThat(result).containsExactly(new TestReducing.SomethingWithLocations(new Something(1, "tree")).at("outside"), new TestReducing.SomethingWithLocations(new Something(2, "apple")).at("tree").at("pie"));
    }

    @Test
    public void testReduceResultSet() {
        Map<Integer, TestReducing.SomethingWithLocations> result = dbRule.getSharedHandle().createQuery("SELECT something.id, name, location FROM something NATURAL JOIN something_location").reduceResultSet(new HashMap<Integer, TestReducing.SomethingWithLocations>(), ( map, rs, ctx) -> {
            final String name = rs.getString("name");
            map.computeIfAbsent(rs.getInt("id"), ( id) -> new org.jdbi.v3.core.result.SomethingWithLocations(new Something(id, name))).at(rs.getString("location"));
            return map;
        });
        assertThat(result).hasSize(2).containsEntry(1, new TestReducing.SomethingWithLocations(new Something(1, "tree")).at("outside")).containsEntry(2, new TestReducing.SomethingWithLocations(new Something(2, "apple")).at("tree").at("pie"));
    }

    static class SomethingWithLocations {
        final Something something;

        final List<String> locations = new ArrayList<>();

        SomethingWithLocations(Something something) {
            this.something = something;
        }

        TestReducing.SomethingWithLocations at(String where) {
            locations.add(where);
            return this;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TestReducing.SomethingWithLocations)) {
                return false;
            }
            TestReducing.SomethingWithLocations o = ((TestReducing.SomethingWithLocations) (other));
            return (o.something.equals(something)) && (o.locations.equals(locations));
        }

        @Override
        public int hashCode() {
            return something.hashCode();
        }

        @Override
        public String toString() {
            return String.format("Something %s with locations %s", something, locations);
        }
    }
}

