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
package org.jdbi.v3.guava;


import com.google.common.base.Optional;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.Optional.empty;
import static java.util.Optional.of;


public class TestGuavaOptional {
    private static final String SELECT_BY_NAME = "select * from something " + ("where :name is null or name = :name " + "order by id");

    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugins();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    Handle handle;

    @Test
    public void testDynamicBindOptionalPresent() {
        Something result = handle.createQuery(TestGuavaOptional.SELECT_BY_NAME).bindByType("name", Optional.of("eric"), new org.jdbi.v3.core.generic.GenericType<Optional<String>>() {}).mapToBean(Something.class).findOnly();
        assertThat(result).isEqualTo(new Something(1, "eric"));
    }

    @Test
    public void testDynamicBindOptionalEmpty() {
        List<Something> result = handle.createQuery(TestGuavaOptional.SELECT_BY_NAME).bindByType("name", Optional.absent(), new org.jdbi.v3.core.generic.GenericType<Optional<String>>() {}).mapToBean(Something.class).list();
        assertThat(result).containsExactly(new Something(1, "eric"), new Something(2, "brian"));
    }

    @Test
    public void testDynamicBindOptionalOfCustomType() {
        handle.registerArgument(new TestGuavaOptional.NameArgumentFactory());
        handle.createQuery(TestGuavaOptional.SELECT_BY_NAME).bindByType("name", Optional.of(new TestGuavaOptional.Name("eric")), new org.jdbi.v3.core.generic.GenericType<Optional<TestGuavaOptional.Name>>() {}).mapToBean(Something.class).list();
    }

    @Test
    public void testDynamicBindOptionalOfUnregisteredCustomType() {
        exception.expect(UnsupportedOperationException.class);
        handle.createQuery(TestGuavaOptional.SELECT_BY_NAME).bindByType("name", Optional.of(new TestGuavaOptional.Name("eric")), new org.jdbi.v3.core.generic.GenericType<Optional<TestGuavaOptional.Name>>() {}).mapToBean(Something.class).list();
    }

    @Test
    public void testBindOptionalPresent() {
        Something result = handle.createQuery(TestGuavaOptional.SELECT_BY_NAME).bind("name", Optional.of("brian")).mapToBean(Something.class).findOnly();
        assertThat(result).isEqualTo(new Something(2, "brian"));
    }

    @Test
    public void testBindOptionalEmpty() {
        List<Something> result = handle.createQuery(TestGuavaOptional.SELECT_BY_NAME).bind("name", Optional.absent()).mapToBean(Something.class).list();
        assertThat(result).containsExactly(new Something(1, "eric"), new Something(2, "brian"));
    }

    @Test
    public void testBindOptionalOfCustomType() {
        handle.registerArgument(new TestGuavaOptional.NameArgumentFactory());
        List<Something> result = handle.createQuery(TestGuavaOptional.SELECT_BY_NAME).bind("name", Optional.of(new TestGuavaOptional.Name("eric"))).mapToBean(Something.class).list();
        assertThat(result).containsExactly(new Something(1, "eric"));
    }

    @Test
    public void testBindOptionalOfUnregisteredCustomType() {
        exception.expect(UnsupportedOperationException.class);
        handle.createQuery(TestGuavaOptional.SELECT_BY_NAME).bind("name", Optional.of(new TestGuavaOptional.Name("eric"))).mapToBean(Something.class).list();
    }

    class Name {
        final String value;

        Name(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestGuavaOptional.Name)) {
                return false;
            }
            TestGuavaOptional.Name that = ((TestGuavaOptional.Name) (obj));
            return this.value.equals(that.value);
        }
    }

    class NameArgumentFactory implements ArgumentFactory {
        @Override
        public java.util.Optional<Argument> build(Type expectedType, Object value, ConfigRegistry config) {
            if (expectedType == (TestGuavaOptional.Name.class)) {
                TestGuavaOptional.Name nameValue = ((TestGuavaOptional.Name) (value));
                return of(( pos, stmt, c) -> stmt.setString(pos, nameValue.value));
            }
            return empty();
        }
    }
}

