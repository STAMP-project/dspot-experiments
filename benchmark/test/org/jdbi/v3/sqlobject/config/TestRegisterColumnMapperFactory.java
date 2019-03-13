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


import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestRegisterColumnMapperFactory {
    @Rule
    public H2DatabaseRule rule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    Handle handle;

    @Test
    public void registerColumnMapperFactories() {
        TestRegisterColumnMapperFactory.TestDao dao = handle.attach(TestRegisterColumnMapperFactory.TestDao.class);
        assertThat(dao.listStringValues()).containsExactly(StringValue.of("foo"), StringValue.of("bar"));
        assertThat(dao.listLongValues()).containsExactly(LongValue.of(1L), LongValue.of(2L));
        assertThat(dao.list()).containsExactly(new ValueTypeEntity(StringValue.of("foo"), LongValue.of(1L)), new ValueTypeEntity(StringValue.of("bar"), LongValue.of(2L)));
    }

    public interface TestDao {
        @SqlQuery("select string_value from column_mappers")
        @RegisterColumnMapperFactory(StringValueColumnMapperFactory.class)
        List<StringValue> listStringValues();

        @SqlQuery("select long_value from column_mappers")
        @RegisterColumnMapperFactory(LongValueColumnMapperFactory.class)
        List<LongValue> listLongValues();

        @SqlQuery("select * from column_mappers")
        @RegisterColumnMapperFactory(StringValueColumnMapperFactory.class)
        @RegisterColumnMapperFactory(LongValueColumnMapperFactory.class)
        @RegisterConstructorMapper(ValueTypeEntity.class)
        List<ValueTypeEntity> list();
    }
}

