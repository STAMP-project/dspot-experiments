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


import io.vavr.Lazy;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import java.lang.reflect.Type;
import java.sql.Types;
import java.util.Optional;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.junit.Test;


public class TestVavrValueArgumentFactory {
    private static final Type TRY_INTEGER = getType();

    private static final Type OPTION_INTEGER = getType();

    private static final Type LAZY_INTEGER = getType();

    private static final Type LAZY_WILDCARD = getType();

    private static final Type EITHER_STRING_INTEGER = getType();

    private static final Type EITHER_WILDCARD = getType();

    private static final Type VALIDATION_STRING_INT = getType();

    private ConfigRegistry configRegistry = new ConfigRegistry();

    private VavrValueArgumentFactory unit = new VavrValueArgumentFactory();

    @Test
    public void testGetNonValueArgumentShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.OPTION_INTEGER, Option.of(1), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetArgumentOfNoneShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.OPTION_INTEGER, Option.none(), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetLazyArgumentShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.LAZY_INTEGER, Lazy.of(() -> 1), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetLazyArgumentInferredShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.LAZY_WILDCARD, Lazy.of(() -> 1), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetBadLazyArgumentShouldThrow() {
        Lazy<Object> badEvaluatingLazy = Lazy.of(() -> {
            throw new org.jdbi.v3.vavr.TestSpecificException();
        });
        assertThatThrownBy(() -> unit.build(LAZY_INTEGER, badEvaluatingLazy, configRegistry)).isInstanceOf(TestVavrValueArgumentFactory.TestSpecificException.class);
    }

    @Test
    public void testGetFailedTryArgumentShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.TRY_INTEGER, Try.failure(new TestVavrValueArgumentFactory.TestSpecificException()), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetSuccessTryArgumentShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.TRY_INTEGER, Try.failure(new TestVavrValueArgumentFactory.TestSpecificException()), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetLeftEitherArgumentShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.EITHER_STRING_INTEGER, Either.left("error"), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetRightEitherArgumentShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.EITHER_STRING_INTEGER, Either.right(1), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetRightEitherArgumentInferredShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.EITHER_WILDCARD, Either.right(1), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetValidValidationArgumentShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.VALIDATION_STRING_INT, Validation.valid(1), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetInvalidValidationArgumentShouldNotBeEmpty() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.VALIDATION_STRING_INT, Validation.invalid("error"), configRegistry);
        assertThat(arg).isNotEmpty();
    }

    @Test
    public void testGetArgumentForNull() {
        Optional<Argument> arg = unit.build(TestVavrValueArgumentFactory.OPTION_INTEGER, null, configRegistry);
        assertThat(getSqlType()).isEqualTo(Types.INTEGER);
    }

    @Test
    public void testGetArgumentNotPartOfFactoryShouldBeEmpty() {
        Optional<Argument> arg = unit.build(new org.jdbi.v3.core.generic.GenericType<Integer>() {}.getType(), null, configRegistry);
        assertThat(arg).isEmpty();
    }

    private static class TestSpecificException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}

