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


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.util.Objects;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.guava.GuavaPlugin;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestGuavaCollectors {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin()).withPlugin(new GuavaPlugin());

    Handle h;

    @Test
    public void testImmutableList() {
        assertThat(h.attach(TestGuavaCollectors.UserDao.class).list()).containsExactly(new TestGuavaCollectors.User(1, "Alice"), new TestGuavaCollectors.User(2, "Bob"));
    }

    @Test
    public void testOptional() {
        assertThat(h.attach(TestGuavaCollectors.UserDao.class).getById(1)).contains(new TestGuavaCollectors.User(1, "Alice"));
    }

    // tag::returnTypes[]
    @RegisterConstructorMapper(TestGuavaCollectors.User.class)
    public interface UserDao {
        @SqlQuery("select * from users order by name")
        ImmutableList<TestGuavaCollectors.User> list();

        @SqlQuery("select * from users where id = :id")
        Optional<TestGuavaCollectors.User> getById(long id);
    }

    // end::returnTypes[]
    public static class User {
        private final int id;

        private final String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            TestGuavaCollectors.User user = ((TestGuavaCollectors.User) (o));
            return ((id) == (user.id)) && (Objects.equals(name, user.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return ((((("User{" + "id=") + (id)) + ", name='") + (name)) + '\'') + '}';
        }
    }
}

