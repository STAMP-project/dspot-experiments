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


import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.ValueType;
import org.jdbi.v3.core.mapper.ValueTypeMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapper;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestFieldMapper {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    public static class TestObject {
        ValueType valueType;

        public ValueType getValueType() {
            return valueType;
        }
    }

    @RegisterColumnMapper(ValueTypeMapper.class)
    public interface TestDao {
        @SqlQuery("select * from testBean")
        @RegisterFieldMapper(TestFieldMapper.TestObject.class)
        List<TestFieldMapper.TestObject> listBeans();

        @SqlQuery("select valueType as obj_value_type from testBean")
        @RegisterFieldMapper(value = TestFieldMapper.TestObject.class, prefix = "obj_")
        List<TestFieldMapper.TestObject> listBeansPrefix();
    }

    Handle h;

    TestFieldMapper.TestDao dao;

    @Test
    public void testMapFields() {
        h.createUpdate("insert into testBean (valueType) values ('foo')").execute();
        List<TestFieldMapper.TestObject> beans = dao.listBeans();
        assertThat(beans).extracting(TestFieldMapper.TestObject::getValueType).containsExactly(ValueType.valueOf("foo"));
    }

    @Test
    public void testMapFieldsPrefix() {
        h.createUpdate("insert into testBean (valueType) values ('foo')").execute();
        List<TestFieldMapper.TestObject> beans = dao.listBeansPrefix();
        assertThat(beans).extracting(TestFieldMapper.TestObject::getValueType).containsExactly(ValueType.valueOf("foo"));
    }
}

