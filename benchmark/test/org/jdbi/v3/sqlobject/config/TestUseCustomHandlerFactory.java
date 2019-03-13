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
package org.jdbi.v3.sqlobject.config;


import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.mapper.SomethingMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.junit.Rule;
import org.junit.Test;


public class TestUseCustomHandlerFactory {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    @Test
    public void shouldUseConfiguredDefaultHandler() {
        TestUseCustomHandlerFactory.SomethingDao h = handle.attach(TestUseCustomHandlerFactory.SomethingDao.class);
        Something s = h.insertAndFind(new Something(1, "Joy"));
        assertThat(s.getName()).isEqualTo("Joy");
    }

    @RegisterRowMapper(SomethingMapper.class)
    public interface SomethingDao {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(@BindBean
        Something s);

        @SqlQuery("select id, name from something where id = :id")
        Something findById(@Bind("id")
        int id);

        @Transaction
        Something insertAndFind(Something s);

        @SuppressWarnings("unused")
        class DefaultImpls {
            private DefaultImpls() {
            }

            public static Something insertAndFind(TestUseCustomHandlerFactory.SomethingDao dao, Something s) {
                dao.insert(s);
                return dao.findById(s.getId());
            }
        }
    }
}

