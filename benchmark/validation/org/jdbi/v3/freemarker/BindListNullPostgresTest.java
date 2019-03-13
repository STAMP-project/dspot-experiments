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
package org.jdbi.v3.freemarker;


import BindList.EmptyHandling;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.rule.PgDatabaseRule;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class BindListNullPostgresTest {
    @Rule
    public PgDatabaseRule dbRule = new PgDatabaseRule().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    @Test
    public void testSomethingByIterableHandleNullWithNull() {
        final BindListNullPostgresTest.SomethingByIterableHandleNull s = handle.attach(BindListNullPostgresTest.SomethingByIterableHandleNull.class);
        final List<Something> out = s.get(null);
        assertThat(out).isEmpty();
    }

    @Test
    public void testSomethingByIterableHandleNullWithEmptyList() {
        final BindListNullPostgresTest.SomethingByIterableHandleNull s = handle.attach(BindListNullPostgresTest.SomethingByIterableHandleNull.class);
        final List<Something> out = s.get(new ArrayList<Object>());
        assertThat(out).isEmpty();
    }

    @Test
    public void testSomethingByIterableHandleNormalList() {
        final BindListNullPostgresTest.SomethingByIterableHandleNull s = handle.attach(BindListNullPostgresTest.SomethingByIterableHandleNull.class);
        final List<Something> out = s.get(Arrays.asList("bla", "null"));
        assertThat(out).hasSize(2);
    }

    @UseFreemarkerEngine
    @RegisterRowMapper(FreemarkerSqlLocatorTest.SomethingMapper.class)
    public interface SomethingByIterableHandleNull {
        @SqlQuery("select id, name from something where name in (${names})")
        List<Something> get(@BindList(value = "names", onEmpty = EmptyHandling.NULL_STRING)
        Iterable<Object> ids);
    }
}

