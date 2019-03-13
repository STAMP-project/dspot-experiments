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
package org.jdbi.v3.postgres;


import Value.Immutable;
import Value.Style;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.immutables.JdbiImmutables;
import org.jdbi.v3.core.rule.PgDatabaseRule;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.BindPojo;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Rule;
import org.junit.Test;

import static ImmutableMappy.Builder.<init>;


public class TestImmutablesHStore {
    @Rule
    public PgDatabaseRule dbRule = new PgDatabaseRule().withPlugin(new PostgresPlugin()).withPlugin(new SqlObjectPlugin()).withConfig(JdbiImmutables.class, ( c) -> c.registerImmutable(.class));

    private Handle h;

    @Value.Immutable
    @Style(overshadowImplementation = true)
    public interface Mappy {
        @HStore
        Map<String, String> numbers();

        class Builder extends ImmutableMappy.Builder {}

        static TestImmutablesHStore.Mappy.Builder builder() {
            return new TestImmutablesHStore.Mappy.Builder();
        }
    }

    @Test
    public void testMap() {
        final TestImmutablesHStore.MappyDao dao = h.attach(TestImmutablesHStore.MappyDao.class);
        final TestImmutablesHStore.Mappy row1 = putNumbers("two", "2").build();
        dao.insert(row1);
        final TestImmutablesHStore.Mappy row2 = putNumbers("five", "5").build();
        dao.insert(row2);
        assertThat(dao.select()).containsExactlyInAnyOrder(row1, row2);
    }

    public interface MappyDao {
        @SqlUpdate("insert into mappy (numbers) values (:numbers)")
        int insert(@BindPojo
        TestImmutablesHStore.Mappy mappy);

        @SqlQuery("select numbers from mappy")
        List<TestImmutablesHStore.Mappy> select();
    }
}

