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
package org.jdbi.v3.sqlobject.customizer;


import java.util.List;
import java.util.Map;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.MapMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestMaxRows {
    private static final String QUERY = "select bar from foo";

    private static final String CREATE_INSERT = "create table foo(bar int primary key);" + (("insert into foo(bar) values(1);" + "insert into foo(bar) values(2);") + "insert into foo(bar) values(3);");

    private static final int ROWS = 1;

    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    @Test
    public void testMethodWrong() {
        assertThatThrownBy(() -> handle.attach(.class)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no value given");
    }

    @Test
    public void testParamWrong() {
        assertThatThrownBy(() -> handle.attach(.class)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("value won't do anything");
    }

    @Test
    public void testMethodRight() {
        TestMaxRows.FooMethodRight sqlObject = handle.attach(TestMaxRows.FooMethodRight.class);
        handle.createScript(TestMaxRows.CREATE_INSERT).execute();
        assertThat(sqlObject.bar()).hasSize(TestMaxRows.ROWS);
    }

    @Test
    public void testParamRight() {
        TestMaxRows.FooParamRight sqlObject = handle.attach(TestMaxRows.FooParamRight.class);
        handle.createScript(TestMaxRows.CREATE_INSERT).execute();
        assertThat(sqlObject.bar(TestMaxRows.ROWS)).hasSize(TestMaxRows.ROWS);
    }

    @Test
    public void testParamNonsense() {
        TestMaxRows.FooParamRight sqlObject = handle.attach(TestMaxRows.FooParamRight.class);
        handle.createScript(TestMaxRows.CREATE_INSERT).execute();
        assertThatThrownBy(() -> sqlObject.bar(0)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("is 0, which is negative or 0");
    }

    @Test
    public void testMethodNonsense() {
        assertThatThrownBy(() -> handle.attach(.class)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("is 0, which is negative or 0");
    }

    @Test
    public void testControlGroup() {
        TestMaxRows.NoMaxRows sqlObject = handle.attach(TestMaxRows.NoMaxRows.class);
        handle.createScript(TestMaxRows.CREATE_INSERT).execute();
        assertThat(sqlObject.bar()).hasSize(3);
    }

    public interface FooMethodWrong extends SqlObject {
        @MaxRows
        @SqlQuery(TestMaxRows.QUERY)
        List<Map<String, Object>> bar();
    }

    public interface FooParamWrong extends SqlObject {
        @SqlQuery(TestMaxRows.QUERY)
        List<Map<String, Object>> bar(@MaxRows(TestMaxRows.ROWS)
        int rows);
    }

    public interface FooMethodRight extends SqlObject {
        @MaxRows(TestMaxRows.ROWS)
        @SqlQuery(TestMaxRows.QUERY)
        @RegisterRowMapper(MapMapper.class)
        List<Map<String, Object>> bar();
    }

    public interface FooParamRight extends SqlObject {
        @SqlQuery(TestMaxRows.QUERY)
        @RegisterRowMapper(MapMapper.class)
        List<Map<String, Object>> bar(@MaxRows
        int rows);
    }

    public interface NoMaxRows extends SqlObject {
        @SqlQuery(TestMaxRows.QUERY)
        @RegisterRowMapper(MapMapper.class)
        List<Map<String, Object>> bar();
    }

    public interface FooMethodNonsenseValue extends SqlObject {
        @MaxRows(0)
        @SqlQuery(TestMaxRows.QUERY)
        @RegisterRowMapper(MapMapper.class)
        List<Map<String, Object>> bar();
    }
}

