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


import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.mapper.SomethingMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlCall;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestSqlCall {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    @Test
    public void testFoo() {
        TestSqlCall.Dao dao = handle.attach(TestSqlCall.Dao.class);
        // OutParameters out = handle.createCall(":num = call stored_insert(:id, :name)")
        // .bind("id", 1)
        // .bind("name", "Jeff")
        // .registerOutParameter("num", Types.INTEGER)
        // .invoke();
        dao.insert(1, "Jeff");
        assertThat(handle.attach(TestSqlCall.Dao.class).findById(1)).isEqualTo(new Something(1, "Jeff"));
    }

    public interface Dao {
        @SqlCall("call stored_insert(:id, :name)")
        void insert(@Bind("id")
        int id, @Bind("name")
        String name);

        @SqlQuery("select id, name from something where id = :id")
        @RegisterRowMapper(SomethingMapper.class)
        Something findById(@Bind("id")
        int id);
    }
}

