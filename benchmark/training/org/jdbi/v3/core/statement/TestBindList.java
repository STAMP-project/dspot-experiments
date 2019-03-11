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
package org.jdbi.v3.core.statement;


import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.FieldMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestBindList {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    private Handle handle;

    @Test
    public void testBindList() {
        handle.createUpdate("insert into thing (<columns>) values (<values>)").defineList("columns", "id", "foo").bindList("values", 3, "abc").execute();
        List<TestBindList.Thing> list = handle.createQuery("select id, foo from thing where id in (<ids>)").bindList("ids", 1, 3).mapTo(TestBindList.Thing.class).list();
        assertThat(list).extracting(TestBindList.Thing::getId, TestBindList.Thing::getFoo, TestBindList.Thing::getBar, TestBindList.Thing::getBaz).containsExactly(tuple(1, "foo1", null, null), tuple(3, "abc", null, null));
    }

    @Test
    public void testBindListWithHashPrefixParser() {
        Jdbi jdbi = Jdbi.create(dbRule.getConnectionFactory());
        jdbi.setSqlParser(new HashPrefixSqlParser());
        jdbi.useHandle(( handle) -> {
            handle.registerRowMapper(FieldMapper.factory(.class));
            handle.createUpdate("insert into thing (<columns>) values (<values>)").defineList("columns", "id", "foo").bindList("values", 3, "abc").execute();
            List<org.jdbi.v3.core.statement.Thing> list = handle.createQuery("select id, foo from thing where id in (<ids>)").bindList("ids", 1, 3).mapTo(.class).list();
            assertThat(list).extracting(org.jdbi.v3.core.statement.Thing::getId, org.jdbi.v3.core.statement.Thing::getFoo, org.jdbi.v3.core.statement.Thing::getBar, org.jdbi.v3.core.statement.Thing::getBaz).containsExactly(tuple(1, "foo1", null, null), tuple(3, "abc", null, null));
        });
    }

    public static class Thing {
        public int id;

        public String foo;

        public String bar;

        public String baz;

        public int getId() {
            return id;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }

        public String getBaz() {
            return baz;
        }
    }
}

