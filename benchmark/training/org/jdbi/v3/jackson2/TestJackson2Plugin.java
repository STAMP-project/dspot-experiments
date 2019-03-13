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
package org.jdbi.v3.jackson2;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jdbi.v3.json.AbstractJsonMapperTest;
import org.jdbi.v3.json.Json;
import org.jdbi.v3.postgres.PostgresDbRule;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Rule;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;


public class TestJackson2Plugin extends AbstractJsonMapperTest {
    @Rule
    public JdbiRule db = PostgresDbRule.rule();

    @Test
    public void testGenericPolymorphicType() {
        db.getJdbi().useHandle(( h) -> {
            org.jdbi.v3.jackson2.ContainerDao dao = h.attach(.class);
            dao.table();
            Container<org.jdbi.v3.jackson2.Contained> c1 = new Container<>();
            c1.setContained(new org.jdbi.v3.jackson2.A());
            dao.insert(c1);
            assertThat(dao.get().getContained()).isInstanceOf(.class);
        });
    }

    private interface ContainerDao {
        @SqlUpdate("create table json(contained varchar)")
        void table();

        @SqlUpdate("insert into json(contained) values(:json)")
        void insert(@Bind("json")
        @Json
        TestJackson2Plugin.Container<TestJackson2Plugin.Contained> json);

        @SqlQuery("select * from json limit 1")
        @Json
        TestJackson2Plugin.Container<TestJackson2Plugin.Contained> get();
    }

    @JsonTypeInfo(use = CLASS)
    public interface Contained {}

    public static class A implements TestJackson2Plugin.Contained {}

    public static class B implements TestJackson2Plugin.Contained {}

    public static class Container<T> {
        private T contained;

        public T getContained() {
            return contained;
        }

        public void setContained(T contained) {
            this.contained = contained;
        }
    }
}

