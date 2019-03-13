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


import java.sql.Types;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.ValueType;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.ValueTypeMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Rule;
import org.junit.Test;


public class TestBindBean {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    private TestBindBean.Dao dao;

    @Test
    public void testBindBean() {
        handle.execute("insert into something (id, name) values (1, 'Alice')");
        assertThat(dao.getName(1)).isEqualTo("Alice");
        dao.update(new Something(1, "Alicia"));
        assertThat(dao.getName(1)).isEqualTo("Alicia");
    }

    @Test
    public void testBindBeanPrefix() {
        handle.execute("insert into something (id, name) values (2, 'Bob')");
        assertThat(dao.getName(2)).isEqualTo("Bob");
        dao.updatePrefix(new Something(2, "Rob"));
        assertThat(dao.getName(2)).isEqualTo("Rob");
    }

    public interface Dao {
        @SqlUpdate("update something set name=:name where id=:id")
        void update(@BindBean
        Something thing);

        @SqlUpdate("update something set name=:thing.name where id=:thing.id")
        void updatePrefix(@BindBean("thing")
        Something thing);

        @SqlQuery("select name from something where id = :id")
        String getName(long id);
    }

    @Test
    public void testNoArgumentFactoryRegisteredForProperty() {
        handle.execute("create table beans (id integer, value_type varchar)");
        assertThatThrownBy(() -> handle.attach(.class).insert(new org.jdbi.v3.sqlobject.Bean(1, ValueType.valueOf("foo")))).hasMessageContaining("No argument factory registered");
    }

    @Test
    public void testArgumentFactoryRegisteredForProperty() {
        handle.execute("create table beans (id integer, value_type varchar, fromField varchar, fromGetter varchar)");
        handle.registerArgument(new TestBindBean.ValueTypeArgumentFactory());
        TestBindBean.BeanDao dao = handle.attach(TestBindBean.BeanDao.class);
        dao.insert(new TestBindBean.Bean(1, ValueType.valueOf("foo")));
        assertThat(dao.getById(1)).extracting(TestBindBean.Bean::getId, TestBindBean.Bean::getValueType).containsExactly(1, ValueType.valueOf("foo"));
    }

    public static class Bean {
        private int id;

        private ValueType valueType;

        public Bean(int id, ValueType valueType) {
            this.id = id;
            this.valueType = valueType;
        }

        public int getId() {
            return id;
        }

        public ValueType getValueType() {
            return valueType;
        }
    }

    public static class ValueTypeArgumentFactory extends AbstractArgumentFactory<ValueType> {
        public ValueTypeArgumentFactory() {
            super(Types.VARCHAR);
        }

        @Override
        protected Argument build(ValueType value, ConfigRegistry config) {
            return ( pos, stmt, ctx) -> stmt.setString(pos, value.getValue());
        }
    }

    public interface BeanDao {
        @SqlUpdate("insert into beans (id, value_type) values (:id, :valueType)")
        void insert(@BindBean
        TestBindBean.Bean bean);

        @SqlQuery("select * from beans where id = :id")
        @RegisterConstructorMapper(TestBindBean.Bean.class)
        @RegisterColumnMapper(ValueTypeMapper.class)
        TestBindBean.Bean getById(int id);
    }
}

