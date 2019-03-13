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


import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.MapTo;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestPolymorphicReturn {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    private TestPolymorphicReturn.SheepDao dao;

    @Test
    public void testPolymorphicReturnSuperclass() {
        TestPolymorphicReturn.Sheep normalSheep = dao.get(TestPolymorphicReturn.Sheep.class, "Fluffy");
        assertThat(normalSheep.getName()).isEqualTo("Fluffy");
    }

    @Test
    public void testPolymorphicReturnSubclass() {
        TestPolymorphicReturn.FlyingSheep flyingSheep = dao.get(TestPolymorphicReturn.FlyingSheep.class, "Fluffy");
        assertThat(flyingSheep.getName()).isEqualTo("Fluffy");
        assertThat(flyingSheep.getNumWings()).isEqualTo(5);
    }

    @Test
    public void testBadArg() {
        assertThatThrownBy(() -> dao.getBad("Fluffy is sad :(")).isInstanceOf(UnsupportedOperationException.class);
    }

    @RegisterBeanMapper(TestPolymorphicReturn.Sheep.class)
    public interface SheepDao {
        @RegisterBeanMapper(TestPolymorphicReturn.FlyingSheep.class)
        @SqlQuery("select name, intValue as numWings from something where name=:name")
        <T extends TestPolymorphicReturn.Sheep> T get(@MapTo
        Class<T> klass, String name);

        @SqlQuery("baaaaaa")
        TestPolymorphicReturn.Sheep getBad(@MapTo
        String baaaaa);
    }

    public static class Sheep {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class FlyingSheep extends TestPolymorphicReturn.Sheep {
        private int numWings;

        public int getNumWings() {
            return numWings;
        }

        public void setNumWings(int numWings) {
            this.numWings = numWings;
        }
    }
}

