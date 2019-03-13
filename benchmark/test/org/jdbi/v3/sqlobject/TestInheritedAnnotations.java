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


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Rule;
import org.junit.Test;


public class TestInheritedAnnotations {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    private MockClock mockClock = MockClock.now();

    @Test
    public void testCrud() {
        Instant inserted = mockClock.instant();
        TestInheritedAnnotations.CharacterDao dao = dbRule.getJdbi().onDemand(TestInheritedAnnotations.CharacterDao.class);
        dao.insert(new TestInheritedAnnotations.Character(1, "Moiraine Sedai"));
        assertThat(dao.findById(1)).contains(new TestInheritedAnnotations.Character(1, "Moiraine Sedai", inserted, inserted));
        Instant modified = mockClock.advance(10, ChronoUnit.SECONDS);
        assertThat(inserted).isBefore(modified);
        dao.update(new TestInheritedAnnotations.Character(1, "Mistress Alys"));
        assertThat(dao.findById(1)).contains(new TestInheritedAnnotations.Character(1, "Mistress Alys", inserted, modified));
        dao.delete(1);
        assertThat(dao.findById(1)).isEmpty();
    }

    // configuring annotation
    // sql statement customizing annotation
    @UseClasspathSqlLocator
    @BindTime
    public interface CrudDao<T, ID> {
        @SqlUpdate
        void insert(@BindBean
        T entity);

        @SqlQuery
        Optional<T> findById(ID id);

        @SqlUpdate
        void update(@BindBean
        T entity);

        @SqlUpdate
        void delete(ID id);
    }

    @RegisterConstructorMapper(TestInheritedAnnotations.Character.class)
    public interface CharacterDao extends TestInheritedAnnotations.CrudDao<TestInheritedAnnotations.Character, Integer> {}

    public static class Character {
        public final int id;

        public final String name;

        private final Instant created;

        private final Instant modified;

        public Character(int id, String name) {
            this(id, name, null, null);
        }

        @JdbiConstructor
        public Character(int id, String name, Instant created, Instant modified) {
            this.id = id;
            this.name = name;
            this.created = created;
            this.modified = modified;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Instant getCreated() {
            return created;
        }

        public Instant getModified() {
            return modified;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            TestInheritedAnnotations.Character character = ((TestInheritedAnnotations.Character) (o));
            return ((((id) == (character.id)) && (Objects.equals(name, character.name))) && (Objects.equals(created, character.created))) && (Objects.equals(modified, character.modified));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, created, modified);
        }

        @Override
        public String toString() {
            return ((((((((("Character{" + "id=") + (id)) + ", name='") + (name)) + '\'') + ", created=") + (created)) + ", modified=") + (modified)) + '}';
        }
    }
}

