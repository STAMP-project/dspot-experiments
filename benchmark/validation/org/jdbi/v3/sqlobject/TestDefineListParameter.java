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
package org.jdbi.v3.sqlobject;


import java.util.ArrayList;
import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.mapper.SomethingMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.customizer.DefineList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Rule;
import org.junit.Test;


public class TestDefineListParameter {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    private List<String> testColumns;

    @Test
    public void testWithBindList() {
        TestDefineListParameter.TestDao testDao = handle.attach(TestDefineListParameter.TestDao.class);
        List<Object> values = new ArrayList<>();
        values.add(1);
        values.add("Some Pig");
        List<Object> valuesNull = new ArrayList<>();
        valuesNull.add(2);
        valuesNull.add(null);
        testDao.insert("test", testColumns, values);
        testDao.insert("testNullable", testColumns, valuesNull);
        Something something = new Something(1, "Some Pig");
        Something nothing = new Something(2, null);
        assertThat(testDao.findById(testColumns, "test", 1)).isEqualTo(something);
        assertThat(testDao.findById(testColumns, "testNullable", 1)).isNull();
        assertThat(testDao.findById(testColumns, "test", 2)).isNull();
        assertThat(testDao.findById(testColumns, "testNullable", 2)).isEqualTo(nothing);
    }

    @Test
    public void testWithBindBean() {
        TestDefineListParameter.TestDao testDao = handle.attach(TestDefineListParameter.TestDao.class);
        Something something = new Something(1, "Some Pig");
        Something nothing = new Something(2, null);
        testDao.insert("test", testColumns, something);
        testDao.insert("testNullable", testColumns, nothing);
        assertThat(testDao.findById(testColumns, "test", 1)).isEqualTo(something);
        assertThat(testDao.findById(testColumns, "testNullable", 1)).isNull();
        assertThat(testDao.findById(testColumns, "test", 2)).isNull();
        assertThat(testDao.findById(testColumns, "testNullable", 2)).isEqualTo(nothing);
    }

    @Test
    public void testArray() {
        TestDefineListParameter.TestDao testDao = handle.attach(TestDefineListParameter.TestDao.class);
        String[] columnsArray = new String[]{ "id", "name" };
        List<Object> values = new ArrayList<>();
        values.add(1);
        values.add("Some Pig");
        testDao.insert("test", columnsArray, values);
    }

    @Test
    public void testDefaultedNameInsert() {
        TestDefineListParameter.TestDao testDao = handle.attach(TestDefineListParameter.TestDao.class);
        List<Object> values = new ArrayList<>();
        values.add(1);
        values.add("Some Pig");
        testDao.defaultedInsert("test", testColumns, values);
    }

    @Test
    public void testEmptyList() {
        TestDefineListParameter.TestDao testDao = handle.attach(TestDefineListParameter.TestDao.class);
        List<String> noColumns = new ArrayList<>();
        Something something = new Something(1, "Some Pig");
        assertThatThrownBy(() -> testDao.insert("test", noColumns, something)).isInstanceOf(IllegalArgumentException.class);
    }

    @RegisterRowMapper(SomethingMapper.class)
    public interface TestDao {
        @SqlUpdate("insert into <table> (<columns>) values (<values>)")
        void insert(@Define("table")
        String table, @DefineList("columns")
        List<String> columns, @BindList
        List<Object> values);

        @SqlUpdate("insert into <table> (<columns>) values (:id, :name)")
        void insert(@Define("table")
        String table, @DefineList("columns")
        List<String> columns, @BindBean
        Something s);

        @SqlUpdate("insert into <table> (<columns>) values (<values>)")
        void insert(@Define("table")
        String table, @DefineList("columns")
        Object[] columns, @BindList
        List<Object> values);

        @SqlUpdate("insert into <table> (<columns>) values (<values>)")
        void defaultedInsert(@Define("table")
        String table, @DefineList
        List<String> columns, @BindList
        List<Object> values);

        @SqlQuery("select <columns> from <table> where id = :id")
        Something findById(@DefineList("columns")
        List<String> columns, @Define("table")
        String table, @Bind("id")
        long id);
    }
}

