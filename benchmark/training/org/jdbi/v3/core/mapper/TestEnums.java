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
package org.jdbi.v3.core.mapper;


import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.UnableToProduceResultException;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestEnums {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething();

    public static class SomethingElse {
        public enum Name {

            eric,
            brian;}

        private int id;

        private TestEnums.SomethingElse.Name name;

        public TestEnums.SomethingElse.Name getName() {
            return name;
        }

        public void setName(TestEnums.SomethingElse.Name name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @Test
    public void testMapEnumValues() {
        Handle h = dbRule.openHandle();
        h.createUpdate("insert into something (id, name) values (1, 'eric')").execute();
        h.createUpdate("insert into something (id, name) values (2, 'brian')").execute();
        List<TestEnums.SomethingElse> results = h.createQuery("select * from something order by id").mapToBean(TestEnums.SomethingElse.class).list();
        assertThat(results).extracting(( se) -> se.name).containsExactly(TestEnums.SomethingElse.Name.eric, TestEnums.SomethingElse.Name.brian);
    }

    @Test
    public void testMapToEnum() {
        Handle h = dbRule.openHandle();
        h.createUpdate("insert into something (id, name) values (1, 'eric')").execute();
        h.createUpdate("insert into something (id, name) values (2, 'brian')").execute();
        List<TestEnums.SomethingElse.Name> results = h.createQuery("select name from something order by id").mapTo(TestEnums.SomethingElse.Name.class).list();
        assertThat(results).containsExactly(TestEnums.SomethingElse.Name.eric, TestEnums.SomethingElse.Name.brian);
    }

    @Test
    public void testMapInvalidEnumValue() {
        Handle h = dbRule.openHandle();
        h.createUpdate("insert into something (id, name) values (1, 'joe')").execute();
        assertThatThrownBy(() -> h.createQuery("select * from something order by id").mapToBean(.class).findFirst()).isInstanceOf(UnableToProduceResultException.class);
    }

    @Test
    public void testEnumCaseInsensitive() {
        assertThat(dbRule.getSharedHandle().createQuery("select 'BrIaN'").mapTo(TestEnums.SomethingElse.Name.class).findOnly()).isEqualTo(TestEnums.SomethingElse.Name.brian);
    }
}

