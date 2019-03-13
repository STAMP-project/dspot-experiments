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


import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Multimap;
import io.vavr.collection.Seq;
import java.util.Objects;
import java.util.Optional;
import junit.framework.TestCase;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.MapEntryMappers;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestVavrMapCollectorWithDB {
    private static final String VAL_PREFIX = "valCol";

    private static final String KEY_PREFIX = "keyCol";

    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugins();

    private Seq<Integer> expected = List.range(0, 9);

    private Map<String, String> expectedMap = expected.toMap(( i) -> new Tuple2<>(("keyCol" + i), ("valCol" + (i + 1))));

    @Test
    public void testMapCollectorWithGlobalKeyValueShouldSucceed() {
        Jdbi jdbiWithKeyColAndValCol = dbRule.getJdbi().setMapKeyColumn("key_c").setMapValueColumn("val_c");
        Boolean executed = jdbiWithKeyColAndValCol.withHandle(( h) -> {
            HashMap<String, String> valueMap = h.createQuery("select val_c, key_c from keyval").collectInto(new GenericType<HashMap<String, String>>() {});
            assertThat(valueMap).containsOnlyElementsOf(expectedMap);
            return true;
        });
        TestCase.assertTrue(executed);
    }

    @Test
    public void testMapCollectorWithTupleConfigShouldSucceed() {
        io.vavr.collection.HashMap<String, String> valueMap = dbRule.getSharedHandle().configure(TupleMappers.class, ( c) -> c.setKeyColumn("key_c").setValueColumn("val_c")).createQuery("select val_c, key_c from keyval").collectInto(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.HashMap<String, String>>() {});
        assertThat(valueMap).containsOnlyElementsOf(expectedMap);
    }

    @Test
    public void testMapCollectorWithCorrespondingTupleColsShouldSucceed() {
        io.vavr.collection.HashMap<String, String> valueMap = dbRule.getSharedHandle().configure(TupleMappers.class, ( c) -> c.setColumn(1, "key_c").setColumn(2, "val_c")).createQuery("select val_c, key_c from keyval").collectInto(new org.jdbi.v3.core.generic.GenericType<io.vavr.collection.HashMap<String, String>>() {});
        assertThat(valueMap).containsOnlyElementsOf(expectedMap);
    }

    @Test
    public void testSingleInstanceAssignmentWithSelectedKeyValueShouldSucceed() {
        Handle handle = dbRule.getSharedHandle().configure(MapEntryMappers.class, ( c) -> c.setKeyColumn("key_c").setValueColumn("val_c"));
        Optional<Tuple2<String, String>> valueMap = handle.createQuery("select val_c, key_c from keyval").mapTo(new org.jdbi.v3.core.generic.GenericType<Tuple2<String, String>>() {}).findFirst();
        assertThat(valueMap).isNotEmpty();
        assertThat(valueMap.get()).isEqualTo(Tuple.of(((TestVavrMapCollectorWithDB.KEY_PREFIX) + 0), ((TestVavrMapCollectorWithDB.VAL_PREFIX) + 1)));
    }

    /**
     * from {@link org.jdbi.v3.core.mapper.MapEntryMapperTest}
     */
    @Test
    public void uniqueIndex() {
        Handle h = dbRule.getSharedHandle();
        h.execute("create table user (id int, name varchar)");
        h.prepareBatch("insert into user (id, name) values (?, ?)").add(1, "alice").add(2, "bob").add(3, "cathy").add(4, "dilbert").execute();
        Map<Integer, TestVavrMapCollectorWithDB.User> map = h.createQuery("select * from user").setMapKeyColumn("id").registerRowMapper(ConstructorMapper.factory(TestVavrMapCollectorWithDB.User.class)).collectInto(new org.jdbi.v3.core.generic.GenericType<Map<Integer, TestVavrMapCollectorWithDB.User>>() {});
        assertThat(map).containsOnly(Tuple.of(1, new TestVavrMapCollectorWithDB.User(1, "alice")), Tuple.of(2, new TestVavrMapCollectorWithDB.User(2, "bob")), Tuple.of(3, new TestVavrMapCollectorWithDB.User(3, "cathy")), Tuple.of(4, new TestVavrMapCollectorWithDB.User(4, "dilbert")));
    }

    @Test
    public void testNonUniqueIndexWithMultimap() {
        Handle h = dbRule.getSharedHandle();
        h.execute("create table user (id int, name varchar)");
        h.prepareBatch("insert into user (id, name) values (?, ?)").add(1, "alice").add(2, "bob").add(3, "alice").execute();
        Multimap<String, TestVavrMapCollectorWithDB.User> usersByName = h.createQuery("select * from user").setMapKeyColumn("name").registerRowMapper(ConstructorMapper.factory(TestVavrMapCollectorWithDB.User.class)).collectInto(new org.jdbi.v3.core.generic.GenericType<Multimap<String, TestVavrMapCollectorWithDB.User>>() {});
        assertThat(usersByName.apply("alice")).hasSize(2).containsExactly(new TestVavrMapCollectorWithDB.User(1, "alice"), new TestVavrMapCollectorWithDB.User(3, "alice"));
        assertThat(usersByName.apply("bob")).hasSize(1).containsExactly(new TestVavrMapCollectorWithDB.User(2, "bob"));
    }

    public static class User {
        private final int id;

        private final String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            TestVavrMapCollectorWithDB.User user = ((TestVavrMapCollectorWithDB.User) (o));
            return ((id) == (user.id)) && (Objects.equals(name, user.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return ((((("User{" + "id=") + (id)) + ", name='") + (name)) + '\'') + '}';
        }
    }
}

