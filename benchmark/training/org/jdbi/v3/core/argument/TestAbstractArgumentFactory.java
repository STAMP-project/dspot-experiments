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
package org.jdbi.v3.core.argument;


import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.statement.StatementContext;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class TestAbstractArgumentFactory {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    ConfigRegistry config;

    @Mock
    PreparedStatement statement;

    @Mock
    StatementContext ctx;

    static class SimpleType {
        final String value;

        SimpleType(String value) {
            this.value = value;
        }
    }

    static class SimpleArgumentFactory extends AbstractArgumentFactory<TestAbstractArgumentFactory.SimpleType> {
        SimpleArgumentFactory() {
            super(Types.VARCHAR);
        }

        @Override
        protected Argument build(TestAbstractArgumentFactory.SimpleType value, ConfigRegistry config) {
            return ( pos, stmt, ctx) -> stmt.setString(pos, value.value);
        }
    }

    @Test
    public void testExpectedClass() throws SQLException {
        Argument argument = new TestAbstractArgumentFactory.SimpleArgumentFactory().build(TestAbstractArgumentFactory.SimpleType.class, new TestAbstractArgumentFactory.SimpleType("foo"), config).orElse(null);
        assertThat(argument).isNotNull();
        argument.apply(1, statement, ctx);
        Mockito.verify(statement).setString(1, "foo");
    }

    @Test
    public void testObjectClassWithInstanceOfExpectedType() throws SQLException {
        Argument argument = new TestAbstractArgumentFactory.SimpleArgumentFactory().build(Object.class, new TestAbstractArgumentFactory.SimpleType("bar"), config).orElse(null);
        assertThat(argument).isNotNull();
        argument.apply(2, statement, ctx);
        Mockito.verify(statement).setString(2, "bar");
    }

    @Test
    public void testNullOfExpectedClass() throws SQLException {
        Argument argument = new TestAbstractArgumentFactory.SimpleArgumentFactory().build(TestAbstractArgumentFactory.SimpleType.class, null, config).orElse(null);
        assertThat(argument).isNotNull();
        argument.apply(3, statement, ctx);
        Mockito.verify(statement).setNull(3, Types.VARCHAR);
    }

    @Test
    public void testValueOfDifferentType() {
        assertThat(new TestAbstractArgumentFactory.SimpleArgumentFactory().build(int.class, 1, config)).isEmpty();
    }

    @Test
    public void testNullOfDifferentType() {
        assertThat(new TestAbstractArgumentFactory.SimpleArgumentFactory().build(Integer.class, null, config)).isEmpty();
    }

    static class Box<T> {
        final T value;

        Box(T value) {
            this.value = value;
        }
    }

    static class BoxOfStringArgumentFactory extends AbstractArgumentFactory<TestAbstractArgumentFactory.Box<String>> {
        BoxOfStringArgumentFactory() {
            super(Types.VARCHAR);
        }

        @Override
        protected Argument build(TestAbstractArgumentFactory.Box<String> value, ConfigRegistry config) {
            return ( pos, stmt, ctx) -> stmt.setString(pos, value.value);
        }
    }

    private static final Type BOX_OF_STRING_TYPE = getType();

    private static final Type BOX_OF_OBJECT_TYPE = getType();

    @Test
    public void testExpectedGenericType() throws SQLException {
        Argument argument = new TestAbstractArgumentFactory.BoxOfStringArgumentFactory().build(TestAbstractArgumentFactory.BOX_OF_STRING_TYPE, new TestAbstractArgumentFactory.Box("foo"), config).orElse(null);
        assertThat(argument).isNotNull();
        argument.apply(1, statement, ctx);
        Mockito.verify(statement).setString(1, "foo");
    }

    @Test
    public void testExpectedGenericTypeWithDifferentParameter() {
        assertThat(new TestAbstractArgumentFactory.BoxOfStringArgumentFactory().build(TestAbstractArgumentFactory.BOX_OF_OBJECT_TYPE, new TestAbstractArgumentFactory.Box<Object>("foo"), config)).isEmpty();
    }

    @Test
    public void testNullOfExpectedGenericType() throws SQLException {
        Argument argument = new TestAbstractArgumentFactory.BoxOfStringArgumentFactory().build(TestAbstractArgumentFactory.BOX_OF_STRING_TYPE, null, config).orElse(null);
        assertThat(argument).isNotNull();
        argument.apply(2, statement, ctx);
        Mockito.verify(statement).setNull(2, Types.VARCHAR);
    }
}

