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
package org.jdbi.v3.core.statement;


import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.rule.DatabaseRule;
import org.jdbi.v3.core.rule.SqliteDatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestSqlLoggerToString {
    private static final String INSERT_POSITIONAL = "insert into foo(bar) values(?)";

    private static final String INSERT_NAMED = "insert into foo(bar) values(:x)";

    private static final String NAME = "x";

    @Rule
    public DatabaseRule db = new SqliteDatabaseRule();

    private Handle handle;

    private String positional = null;

    private String named = null;

    // basic types
    @Test
    public void testInt() {
        handle.createUpdate(TestSqlLoggerToString.INSERT_POSITIONAL).bind(0, 1).execute();
        assertThat(positional).isEqualTo("1");
    }

    @Test
    public void testString() {
        handle.createUpdate(TestSqlLoggerToString.INSERT_POSITIONAL).bind(0, "herp").execute();
        assertThat(positional).isEqualTo("herp");
    }

    @Test
    public void testBean() {
        handle.createUpdate(TestSqlLoggerToString.INSERT_NAMED).bindBean(new TestSqlLoggerToString.StringBean("herp")).execute();
        assertThat(named).isEqualTo("herp");
    }

    // Arguments
    @Test
    public void testArgumentWithoutToString() {
        handle.createUpdate(TestSqlLoggerToString.INSERT_POSITIONAL).bind(0, ( position, statement, ctx) -> statement.setString(1, "derp")).execute();
        assertThat(positional).isNotEqualTo("derp");
    }

    @Test
    public void testArgumentWithToString() {
        handle.createUpdate(TestSqlLoggerToString.INSERT_POSITIONAL).bind(0, new Argument() {
            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException {
                statement.setString(1, "derp");
            }

            @Override
            public String toString() {
                return "toString for derp";
            }
        }).execute();
        assertThat(positional).isEqualTo("toString for derp");
    }

    // factories
    @Test
    public void testNeitherHasToString() {
        handle.registerArgument(new TestSqlLoggerToString.FooArgumentFactory());
        handle.createUpdate(TestSqlLoggerToString.INSERT_POSITIONAL).bind(0, new TestSqlLoggerToString.Foo()).execute();
        assertThat(positional).containsPattern("@[0-9a-f]{1,8}$");
    }

    @Test
    public void testObjectHasToString() {
        handle.registerArgument(new TestSqlLoggerToString.FooArgumentFactory());
        handle.createUpdate(TestSqlLoggerToString.INSERT_POSITIONAL).bind(0, new TestSqlLoggerToString.ToStringFoo()).execute();
        assertThat(positional).isEqualTo("I'm a Foo");
    }

    @Test
    public void testArgumentHasToString() {
        handle.registerArgument(new TestSqlLoggerToString.ToStringFooArgumentFactory());
        handle.createUpdate(TestSqlLoggerToString.INSERT_POSITIONAL).bind(0, new TestSqlLoggerToString.Foo()).execute();
        assertThat(positional).isEqualTo("this is a Foo");
    }

    @Test
    public void testBothHaveToStringAndArgumentWins() {
        handle.registerArgument(new TestSqlLoggerToString.ToStringFooArgumentFactory());
        handle.createUpdate(TestSqlLoggerToString.INSERT_POSITIONAL).bind(0, new TestSqlLoggerToString.ToStringFoo()).execute();
        assertThat(positional).isEqualTo("this is a Foo");
    }

    public static class StringBean {
        private final String x;

        private StringBean(String x) {
            this.x = x;
        }

        public String getX() {
            return x;
        }
    }

    private static class Foo {}

    private static class ToStringFoo extends TestSqlLoggerToString.Foo {
        @Override
        public String toString() {
            return "I'm a Foo";
        }
    }

    private static class FooArgumentFactory implements ArgumentFactory {
        @Override
        public Optional<Argument> build(Type type, Object value, ConfigRegistry config) {
            if (value instanceof TestSqlLoggerToString.Foo) {
                return Optional.of(( position, statement, ctx) -> statement.setObject(1, value));
            } else {
                return Optional.empty();
            }
        }
    }

    private static class ToStringFooArgumentFactory implements ArgumentFactory {
        @Override
        public Optional<Argument> build(Type type, Object value, ConfigRegistry config) {
            if (value instanceof TestSqlLoggerToString.Foo) {
                return Optional.of(new Argument() {
                    @Override
                    public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException {
                        statement.setObject(1, value);
                    }

                    @Override
                    public String toString() {
                        return "this is a Foo";
                    }
                });
            } else {
                return Optional.empty();
            }
        }
    }
}

