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
package org.jdbi.v3.json;


import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Rule;
import org.junit.Test;


public class StubJsonMapperTest {
    @Rule
    public final JdbiRule h2 = JdbiRule.h2().withPlugin(new SqlObjectPlugin()).withPlugin(new JsonPlugin());

    @Test
    public void defaultFactoriesAreWorkingForSqlObject() {
        h2.getJdbi().useHandle(( h) -> {
            org.jdbi.v3.json.FooDao dao = h.attach(.class);
            dao.table();
            assertThatThrownBy(() -> dao.insert(new org.jdbi.v3.json.Foo())).isInstanceOf(.class).hasMessageContaining("need to install").hasMessageContaining("a JsonMapper");
            assertThatThrownBy(dao::get).isInstanceOf(.class).hasMessageContaining("need to install").hasMessageContaining("a JsonMapper");
        });
    }

    public static class Foo {}

    private interface FooDao {
        @SqlUpdate("create table json(val varchar)")
        void table();

        @SqlUpdate("insert into json(val) values(:json)")
        void insert(@Json
        StubJsonMapperTest.Foo json);

        @SqlQuery("select '{}'")
        @Json
        StubJsonMapperTest.Foo get();
    }
}

