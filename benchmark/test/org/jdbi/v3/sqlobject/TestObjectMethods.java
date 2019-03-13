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
import org.jdbi.v3.core.mapper.SomethingMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Rule;
import org.junit.Test;


public class TestObjectMethods {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    @Test
    public void testToString() {
        TestObjectMethods.DAO dao = handle.attach(TestObjectMethods.DAO.class);
        assertThat(dao.toString()).contains(TestObjectMethods.DAO.class.getName());
    }

    @Test
    public void testEquals() {
        TestObjectMethods.DAO dao = handle.attach(TestObjectMethods.DAO.class);
        assertThat(dao).isEqualTo(dao);
    }

    @Test
    public void testNotEquals() {
        TestObjectMethods.DAO dao = handle.attach(TestObjectMethods.DAO.class);
        TestObjectMethods.DAO oad = handle.attach(TestObjectMethods.DAO.class);
        assertThat(dao).isNotEqualTo(oad);
    }

    @Test
    public void testHashCodeDiff() {
        TestObjectMethods.DAO dao = handle.attach(TestObjectMethods.DAO.class);
        TestObjectMethods.DAO oad = handle.attach(TestObjectMethods.DAO.class);
        assertThat(dao.hashCode()).isNotEqualTo(oad.hashCode());
    }

    @Test
    public void testHashCodeMatch() {
        TestObjectMethods.DAO dao = handle.attach(TestObjectMethods.DAO.class);
        assertThat(dao.hashCode()).isEqualTo(dao.hashCode());
    }

    @RegisterRowMapper(SomethingMapper.class)
    public interface DAO {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(@Bind("id")
        long id, @Bind("name")
        String name);
    }
}

