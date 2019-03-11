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
package org.jdbi.v3.json;


import java.util.List;
import java.util.Objects;
import org.assertj.core.groups.Tuple;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.qualifier.QualifiedType;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Test;


public abstract class AbstractJsonMapperTest {
    protected Jdbi jdbi;

    @Test
    public void testSqlObject() {
        jdbi.useHandle(( h) -> {
            h.execute("create table subjects (id serial primary key, subject json not null)");
            org.jdbi.v3.json.JsonDao dao = h.attach(.class);
            dao.insert(new org.jdbi.v3.json.JsonBean("yams", 42));
            dao.insert(new org.jdbi.v3.json.JsonBean("apples", 24));
            assertThat(dao.select()).extracting("food", "bitcoins").containsExactlyInAnyOrder(new Tuple("yams", 42), new Tuple("apples", 24));
        });
    }

    @Test
    public void testFluentApi() {
        jdbi.useHandle(( h) -> {
            h.execute("create table subjects (id serial primary key, subject json not null)");
            org.jdbi.v3.json.JsonBean in = new org.jdbi.v3.json.JsonBean("nom", 10);
            h.createUpdate("insert into subjects(id, subject) values(1, :bean)").bindByType("bean", in, QualifiedType.of(.class).with(.class)).execute();
            org.jdbi.v3.json.JsonBean out = h.createQuery("select subject from subjects").mapTo(QualifiedType.of(.class).with(.class)).findOnly();
            assertThat(out).isEqualTo(in);
        });
    }

    @Test
    public void testFluentApiWithNesting() {
        jdbi.useHandle(( h) -> {
            h.execute("create table bean (id serial primary key, nested1 json, nested2 json)");
            assertThat(h.createUpdate("insert into bean(id, nested1, nested2) values (:id, :nested1, :nested2)").bindBean(new org.jdbi.v3.json.NestedJsonBean(42, 64, "quux")).execute()).isEqualTo(1);
            org.jdbi.v3.json.NestedJsonBean beany = h.createQuery("select * from bean").mapToBean(.class).findOnly();
            assertThat(beany.getId()).isEqualTo(42);
            assertThat(beany.getNested1().getA()).isEqualTo(64);
            assertThat(beany.getNested2().getA()).isEqualTo("quux");
        });
    }

    @Test
    public void testNull() {
        jdbi.useHandle(( h) -> {
            h.execute("create table subjects (id serial primary key, subject json)");
            org.jdbi.v3.json.JsonDao dao = h.attach(.class);
            dao.insert(null);
            assertThat(h.createQuery("select subject from subjects").mapTo(.class).findOnly()).isNull();
            assertThat(dao.select()).containsExactly(((org.jdbi.v3.json.JsonBean) (null)));
        });
    }

    public static class JsonBean {
        private final String food;

        private final int bitcoins;

        public JsonBean(String food, int bitcoins) {
            this.food = food;
            this.bitcoins = bitcoins;
        }

        public String getFood() {
            return food;
        }

        public int getBitcoins() {
            return bitcoins;
        }

        @Override
        public boolean equals(Object x) {
            AbstractJsonMapperTest.JsonBean other = ((AbstractJsonMapperTest.JsonBean) (x));
            return ((bitcoins) == (other.bitcoins)) && (Objects.equals(food, other.food));
        }

        @Override
        public int hashCode() {
            return Objects.hash(food, bitcoins);
        }
    }

    public interface JsonDao {
        @SqlUpdate("insert into subjects (subject) values(?)")
        int insert(@Json
        AbstractJsonMapperTest.JsonBean value);

        @SqlQuery("select subject from subjects")
        @Json
        List<AbstractJsonMapperTest.JsonBean> select();
    }

    public static class NestedJsonBean {
        private int id;

        private AbstractJsonMapperTest.Nested1 nested1;

        private AbstractJsonMapperTest.Nested2 nested2;

        public NestedJsonBean() {
        }

        private NestedJsonBean(int id, int a, String b) {
            this.id = id;
            this.nested1 = new AbstractJsonMapperTest.Nested1(a, "1");
            this.nested2 = new AbstractJsonMapperTest.Nested2(b, 2);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Json
        public AbstractJsonMapperTest.Nested1 getNested1() {
            return nested1;
        }

        public void setNested1(AbstractJsonMapperTest.Nested1 nested1) {
            this.nested1 = nested1;
        }

        @Json
        public AbstractJsonMapperTest.Nested2 getNested2() {
            return nested2;
        }

        public void setNested2(AbstractJsonMapperTest.Nested2 nested2) {
            this.nested2 = nested2;
        }
    }

    public static class Nested1 {
        private final int a;

        private final String b;

        public Nested1(int a, String b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public String getB() {
            return b;
        }
    }

    public static class Nested2 {
        private final String a;

        private final int b;

        public Nested2(String a, int b) {
            this.a = a;
            this.b = b;
        }

        public String getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }
}

