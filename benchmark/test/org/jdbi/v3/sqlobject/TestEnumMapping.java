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


import org.jdbi.v3.core.mapper.SomethingMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Rule;
import org.junit.Test;


public class TestEnumMapping {
    @Rule
    public JdbiRule dbRule = JdbiRule.h2().withPlugin(new SqlObjectPlugin());

    @Test
    public void testEnums() {
        TestEnumMapping.Spiffy spiffy = dbRule.attach(TestEnumMapping.Spiffy.class);
        int bobId = spiffy.addCoolName(TestEnumMapping.CoolName.BOB);
        int joeId = spiffy.addCoolName(TestEnumMapping.CoolName.JOE);
        assertThat(spiffy.findById(bobId)).isSameAs(TestEnumMapping.CoolName.BOB);
        assertThat(spiffy.findById(joeId)).isSameAs(TestEnumMapping.CoolName.JOE);
    }

    @RegisterRowMapper(SomethingMapper.class)
    public interface Spiffy {
        @SqlUpdate("insert into something(name) values(:name)")
        @GetGeneratedKeys
        int addCoolName(@Bind("name")
        TestEnumMapping.CoolName coolName);

        @SqlQuery("select name from something where id = :id")
        TestEnumMapping.CoolName findById(@Bind("id")
        int id);
    }

    public enum CoolName {

        BOB,
        FRANK,
        JOE;}
}

