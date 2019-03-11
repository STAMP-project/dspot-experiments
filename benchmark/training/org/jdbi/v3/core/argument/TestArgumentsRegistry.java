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
import java.util.Optional;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleAccess;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.generic.GenericTypes;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.statement.StatementContextAccess;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class TestArgumentsRegistry {
    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    private static final String I_AM_A_STRING = "I am a String";

    private final Handle handle = HandleAccess.createHandle();

    private final StatementContext ctx = StatementContextAccess.createContext(handle);

    @Mock
    public PreparedStatement stmt;

    @Test
    public void testWaffleLong() throws Exception {
        ctx.findArgumentFor(Object.class, 3L).get().apply(1, stmt, null);
        Mockito.verify(stmt).setLong(1, 3);
    }

    @Test
    public void testWaffleShort() throws Exception {
        ctx.findArgumentFor(Object.class, ((short) (2000))).get().apply(2, stmt, null);
        Mockito.verify(stmt).setShort(2, ((short) (2000)));
    }

    @Test
    public void testWaffleString() throws Exception {
        ctx.findArgumentFor(Object.class, TestArgumentsRegistry.I_AM_A_STRING).get().apply(3, stmt, null);
        Mockito.verify(stmt).setString(3, TestArgumentsRegistry.I_AM_A_STRING);
    }

    @Test
    public void testExplicitWaffleLong() throws Exception {
        ctx.findArgumentFor(Long.class, 3L).get().apply(1, stmt, null);
        Mockito.verify(stmt).setLong(1, 3);
    }

    @Test
    public void testExplicitWaffleShort() throws Exception {
        ctx.findArgumentFor(short.class, ((short) (2000))).get().apply(2, stmt, null);
        Mockito.verify(stmt).setShort(2, ((short) (2000)));
    }

    @Test
    public void testExplicitWaffleString() throws Exception {
        ctx.findArgumentFor(String.class, TestArgumentsRegistry.I_AM_A_STRING).get().apply(3, stmt, null);
        Mockito.verify(stmt).setString(3, TestArgumentsRegistry.I_AM_A_STRING);
    }

    @Test
    public void testPull88WeirdClassArgumentFactory() {
        handle.registerArgument(new TestArgumentsRegistry.WeirdClassArgumentFactory());
        assertThat(ctx.findArgumentFor(TestArgumentsRegistry.Weird.class, new TestArgumentsRegistry.Weird())).hasValueSatisfying(( a) -> assertThat(a).isInstanceOf(.class));
        assertThat(ctx.findArgumentFor(TestArgumentsRegistry.Weird.class, null)).hasValueSatisfying(( a) -> assertThat(a).isInstanceOf(.class));
        assertThat(ctx.findArgumentFor(Object.class, new TestArgumentsRegistry.Weird())).isEmpty();
        assertThat(ctx.findArgumentFor(Object.class, null)).hasValueSatisfying(( a) -> assertThat(a).isInstanceOf(.class));
    }

    @Test
    public void testPull88WeirdValueArgumentFactory() {
        handle.registerArgument(new TestArgumentsRegistry.WeirdValueArgumentFactory());
        assertThat(ctx.findArgumentFor(TestArgumentsRegistry.Weird.class, new TestArgumentsRegistry.Weird())).hasValueSatisfying(( a) -> assertThat(a).isInstanceOf(.class));
        assertThat(ctx.findArgumentFor(Object.class, new TestArgumentsRegistry.Weird())).hasValueSatisfying(( a) -> assertThat(a).isInstanceOf(.class));
        assertThat(ctx.findArgumentFor(TestArgumentsRegistry.Weird.class, null)).hasValueSatisfying(( a) -> assertThat(a).isInstanceOf(.class));
        assertThat(ctx.findArgumentFor(Object.class, null)).hasValueSatisfying(( a) -> assertThat(a).isInstanceOf(.class));
    }

    private static class Weird {}

    private static class WeirdClassArgumentFactory implements ArgumentFactory {
        @Override
        public Optional<Argument> build(Type expectedType, Object value, ConfigRegistry config) {
            return (GenericTypes.getErasedType(expectedType)) == (TestArgumentsRegistry.Weird.class) ? Optional.of(new TestArgumentsRegistry.WeirdArgument()) : Optional.empty();
        }
    }

    private static class WeirdValueArgumentFactory implements ArgumentFactory {
        @Override
        public Optional<Argument> build(Type expectedType, Object value, ConfigRegistry config) {
            return value instanceof TestArgumentsRegistry.Weird ? Optional.of(new TestArgumentsRegistry.WeirdArgument()) : Optional.empty();
        }
    }

    private static class WeirdArgument implements Argument {
        @Override
        public void apply(int position, PreparedStatement statement, StatementContext ctx) {
        }
    }
}

