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
package org.jdbi.v3.vavr;


import io.vavr.collection.Set;
import io.vavr.control.Option;
import java.util.Objects;
import org.jdbi.v3.core.mapper.NoSuchMapperException;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestVavrOptionMapperWithDB {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugins();

    @Test
    public void testOptionMappedShouldSucceed() {
        final Set<Option<String>> result = dbRule.getSharedHandle().createQuery("select name from something").collectInto(new org.jdbi.v3.core.generic.GenericType<Set<Option<String>>>() {});
        assertThat(result).hasSize(2);
        assertThat(result).contains(Option.none(), Option.of("eric"));
    }

    @Test
    public void testOptionMappedWithoutGenericParameterShouldFail() {
        assertThatThrownBy(() -> dbRule.getSharedHandle().registerRowMapper(ConstructorMapper.factory(.class)).createQuery("select name from something").collectInto(new GenericType<Set<Option>>() {})).isInstanceOf(NoSuchMapperException.class).hasMessageContaining("raw");
    }

    @Test
    public void testOptionMappedWithoutNestedMapperShouldFail() {
        assertThatThrownBy(() -> dbRule.getSharedHandle().createQuery("select id, name from something").collectInto(new GenericType<Set<Option<org.jdbi.v3.vavr.SomethingWithOption>>>() {})).isInstanceOf(NoSuchMapperException.class).hasMessageContaining("nested");
    }

    @Test
    public void testOptionMappedWithinObjectIfPresentShouldContainValue() {
        final TestVavrOptionMapperWithDB.SomethingWithOption result = dbRule.getSharedHandle().registerRowMapper(ConstructorMapper.factory(TestVavrOptionMapperWithDB.SomethingWithOption.class)).createQuery("select id, name from something where id = 1").mapTo(TestVavrOptionMapperWithDB.SomethingWithOption.class).findOnly();
        assertThat(result.getName()).isInstanceOf(Option.class);
        assertThat(result).isEqualTo(new TestVavrOptionMapperWithDB.SomethingWithOption(1, Option.of("eric")));
    }

    @Test
    public void testOptionWithinObjectIfMissingShouldBeNone() {
        final TestVavrOptionMapperWithDB.SomethingWithOption result = dbRule.getSharedHandle().registerRowMapper(ConstructorMapper.factory(TestVavrOptionMapperWithDB.SomethingWithOption.class)).createQuery("select id, name from something where id = 2").mapTo(TestVavrOptionMapperWithDB.SomethingWithOption.class).findOnly();
        assertThat(result.getName()).isInstanceOf(Option.class);
        assertThat(result).isEqualTo(new TestVavrOptionMapperWithDB.SomethingWithOption(2, Option.none()));
    }

    public static class SomethingWithOption {
        private int id;

        private Option<String> name;

        public SomethingWithOption(int id, Option<String> name) {
            this.id = id;
            this.name = name;
        }

        public Option<String> getName() {
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
            TestVavrOptionMapperWithDB.SomethingWithOption that = ((TestVavrOptionMapperWithDB.SomethingWithOption) (o));
            return ((id) == (that.id)) && (Objects.equals(name, that.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }
}

