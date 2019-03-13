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


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.Objects;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.config.ValueColumn;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Rule;
import org.junit.Test;


public class TestReturningMap {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugins();

    private Handle h;

    @Test
    public void keyValueColumns() {
        TestReturningMap.KeyValueDao dao = h.attach(TestReturningMap.KeyValueDao.class);
        dao.createTable();
        dao.insert("foo", "123");
        dao.insert("bar", "xyz");
        assertThat(dao.getAll()).containsOnly(entry("foo", "123"), entry("bar", "xyz"));
    }

    // end::keyValue[]
    public interface KeyValueDao {
        @SqlUpdate("create table config (key varchar, value varchar)")
        void createTable();

        @SqlUpdate("insert into config (key, value) values (:key, :value)")
        void insert(String key, String value);

        // tag::keyValue[]
        @SqlQuery("select key, value from config")
        @KeyColumn("key")
        @ValueColumn("value")
        Map<String, String> getAll();
    }

    @Test
    public void uniqueIndex() {
        TestReturningMap.UniqueIndexDao dao = h.attach(TestReturningMap.UniqueIndexDao.class);
        dao.createTable();
        dao.insert(new TestReturningMap.User(1, "alice"), new TestReturningMap.User(2, "bob"), new TestReturningMap.User(3, "cathy"), new TestReturningMap.User(4, "dilbert"));
        assertThat(dao.getAll()).containsOnly(entry(1, new TestReturningMap.User(1, "alice")), entry(2, new TestReturningMap.User(2, "bob")), entry(3, new TestReturningMap.User(3, "cathy")), entry(4, new TestReturningMap.User(4, "dilbert")));
    }

    // end::uniqueIndex[]
    public interface UniqueIndexDao {
        @SqlUpdate("create table user (id int, name varchar)")
        void createTable();

        @SqlBatch("insert into user (id, name) values (:id, :name)")
        void insert(@BindBean
        TestReturningMap.User... users);

        // tag::uniqueIndex[]
        @SqlQuery("select * from user")
        @KeyColumn("id")
        @RegisterConstructorMapper(TestReturningMap.User.class)
        Map<Integer, TestReturningMap.User> getAll();
    }

    @Test
    public void joinRow() {
        TestReturningMap.JoinRowDao dao = h.attach(TestReturningMap.JoinRowDao.class);
        dao.createUserTable();
        dao.createPhoneTable();
        dao.insertUsers(new TestReturningMap.User(1, "alice"), new TestReturningMap.User(2, "bob"), new TestReturningMap.User(3, "cathy"));
        dao.insertPhone(1, new TestReturningMap.Phone(10, "555-0001"));
        dao.insertPhone(2, new TestReturningMap.Phone(20, "555-0002"));
        dao.insertPhone(3, new TestReturningMap.Phone(30, "555-0003"));
        assertThat(dao.getMap()).containsOnly(entry(new TestReturningMap.User(1, "alice"), new TestReturningMap.Phone(10, "555-0001")), entry(new TestReturningMap.User(2, "bob"), new TestReturningMap.Phone(20, "555-0002")), entry(new TestReturningMap.User(3, "cathy"), new TestReturningMap.Phone(30, "555-0003")));
    }

    @Test
    public void multimapJoin() {
        TestReturningMap.JoinRowDao dao = h.attach(TestReturningMap.JoinRowDao.class);
        dao.createUserTable();
        dao.createPhoneTable();
        dao.insertUsers(new TestReturningMap.User(1, "alice"), new TestReturningMap.User(2, "bob"), new TestReturningMap.User(3, "cathy"));
        dao.insertPhone(1, new TestReturningMap.Phone(10, "555-0001"), new TestReturningMap.Phone(11, "555-0021"));
        dao.insertPhone(2, new TestReturningMap.Phone(20, "555-0002"), new TestReturningMap.Phone(21, "555-0022"));
        dao.insertPhone(3, new TestReturningMap.Phone(30, "555-0003"), new TestReturningMap.Phone(31, "555-0023"));
        assertThat(dao.getMultimap()).hasSameEntriesAs(ImmutableMultimap.<TestReturningMap.User, TestReturningMap.Phone>builder().putAll(new TestReturningMap.User(1, "alice"), new TestReturningMap.Phone(10, "555-0001"), new TestReturningMap.Phone(11, "555-0021")).putAll(new TestReturningMap.User(2, "bob"), new TestReturningMap.Phone(20, "555-0002"), new TestReturningMap.Phone(21, "555-0022")).putAll(new TestReturningMap.User(3, "cathy"), new TestReturningMap.Phone(30, "555-0003"), new TestReturningMap.Phone(31, "555-0023")).build());
    }

    // end::joinRowMultimap[]
    public interface JoinRowDao {
        @SqlUpdate("create table user (id int, name varchar)")
        void createUserTable();

        @SqlUpdate("create table phone (id int, user_id int, phone varchar)")
        void createPhoneTable();

        @SqlBatch("insert into user (id, name) values (:id, :name)")
        void insertUsers(@BindBean
        TestReturningMap.User... users);

        @SqlBatch("insert into phone (id, user_id, phone) values (:id, :userId, :phone)")
        void insertPhone(int userId, @BindBean
        TestReturningMap.Phone... phones);

        // tag::joinRow[]
        @SqlQuery("select u.id u_id, u.name u_name, p.id p_id, p.phone p_phone " + "from user u left join phone p on u.id = p.user_id")
        @RegisterConstructorMapper(value = TestReturningMap.User.class, prefix = "u")
        @RegisterConstructorMapper(value = TestReturningMap.Phone.class, prefix = "p")
        Map<TestReturningMap.User, TestReturningMap.Phone> getMap();

        // end::joinRow[]
        // tag::joinRowMultimap[]
        @SqlQuery("select u.id u_id, u.name u_name, p.id p_id, p.phone p_phone " + "from user u left join phone p on u.id = p.user_id")
        @RegisterConstructorMapper(value = TestReturningMap.User.class, prefix = "u")
        @RegisterConstructorMapper(value = TestReturningMap.Phone.class, prefix = "p")
        Multimap<TestReturningMap.User, TestReturningMap.Phone> getMultimap();
    }

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
            TestReturningMap.User user = ((TestReturningMap.User) (o));
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

    public static class Phone {
        private final int id;

        private final String phone;

        public Phone(int id, String phone) {
            this.id = id;
            this.phone = phone;
        }

        public int getId() {
            return id;
        }

        public String getPhone() {
            return phone;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            TestReturningMap.Phone phone1 = ((TestReturningMap.Phone) (o));
            return ((id) == (phone1.id)) && (Objects.equals(phone, phone1.phone));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, phone);
        }

        @Override
        public String toString() {
            return ((((("Phone{" + "id=") + (id)) + ", phone='") + (phone)) + '\'') + '}';
        }
    }
}

