/**
 * __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2019 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.test;


import CheckResult.Erroneous;
import CheckResult.Falsified;
import CheckResult.Satisfied;
import io.vavr.Tuple;
import io.vavr.control.Option;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CheckResultTest {
    static final Satisfied SATISFIED = new CheckResult.Satisfied("test", 0, false);

    static final Falsified FALSIFIED = new CheckResult.Falsified("test", 0, Tuple.of(1));

    static final Erroneous ERRONEOUS = new CheckResult.Erroneous("test", 0, new Error("test"), Option.none());

    // -- Satisfied
    @Test
    public void shouldBeSatisfiedWhenIsSatisfied() {
        assertThat(CheckResultTest.SATISFIED.isSatisfied()).isTrue();
    }

    @Test
    public void shouldNotBeFalsifiedWhenIsSatisfied() {
        assertThat(CheckResultTest.SATISFIED.isFalsified()).isFalse();
    }

    @Test
    public void shouldNotBeErroneousWhenIsSatisfied() {
        assertThat(CheckResultTest.SATISFIED.isErroneous()).isFalse();
    }

    @Test
    public void shouldBeExhaustedWhenIsSatisfiedAndExhausted() {
        Assertions.assertThat(new CheckResult.Satisfied("test", 0, true).isExhausted()).isTrue();
    }

    @Test
    public void shouldNotBeExhaustedWhenIsSatisfiedAndNotExhausted() {
        assertThat(CheckResultTest.SATISFIED.isExhausted()).isFalse();
    }

    @Test
    public void shouldGetPropertyNameOfSatisfied() {
        assertThat(CheckResultTest.SATISFIED.propertyName()).isEqualTo("test");
    }

    @Test
    public void shouldGetCountOfSatisfied() {
        assertThat(CheckResultTest.SATISFIED.count()).isEqualTo(0);
    }

    @Test
    public void shouldHaveNoSampleWhenIsSatisfied() {
        assertThat(CheckResultTest.SATISFIED.sample()).isEqualTo(Option.none());
    }

    @Test
    public void shouldHaveNoErrorWhenIsSatisfied() {
        assertThat(CheckResultTest.SATISFIED.error()).isEqualTo(Option.none());
    }

    @Test
    public void shouldIdentififyEqualSatisfiedObjectsWhenObjectsAreIdentical() {
        final CheckResult.Satisfied satisfied = CheckResultTest.SATISFIED;
        // noinspection EqualsWithItself
        assertThat(satisfied.equals(satisfied)).isTrue();
    }

    @Test
    public void shouldIdentififyEqualSatisfiedObjectsWhenObjectsHaveSameTypeAndEqualValues() {
        final CheckResult.Satisfied satisfied1 = CheckResultTest.SATISFIED;
        final CheckResult.Satisfied satisfied2 = new CheckResult.Satisfied("test", 0, false);
        assertThat(satisfied1.equals(satisfied2)).isTrue();
    }

    @Test
    public void shouldIdentififyUnequalSatisfiedObjectsWhenTypesAreUnequal() {
        final CheckResult.Satisfied satisfied = CheckResultTest.SATISFIED;
        assertThat(satisfied.equals(new Object())).isFalse();
    }

    @Test
    public void shouldIdentififyUnequalSatisfiedObjectsWhenValuesAreUnequal() {
        final CheckResult.Satisfied satisfied = new CheckResult.Satisfied("test", 1, true);
        assertThat(satisfied.equals(new CheckResult.Satisfied("x", 1, true))).isFalse();
        assertThat(satisfied.equals(new CheckResult.Satisfied("test", (-1), true))).isFalse();
        assertThat(satisfied.equals(new CheckResult.Satisfied("test", 1, false))).isFalse();
    }

    @Test
    public void shouldComputeHashCodeOfSatisfied() {
        assertThat(CheckResultTest.SATISFIED.hashCode()).isEqualTo(Objects.hash("test", 0, false));
    }

    @Test
    public void shouldComputeToStringOfSatisfied() {
        assertThat(CheckResultTest.SATISFIED.toString()).isEqualTo("Satisfied(propertyName = test, count = 0, exhausted = false)");
    }

    // -- Falsified
    @Test
    public void shouldNotBeSatisfiedWhenIsFalsified() {
        assertThat(CheckResultTest.FALSIFIED.isSatisfied()).isFalse();
    }

    @Test
    public void shouldBeFalsifiedWhenIsFalsified() {
        assertThat(CheckResultTest.FALSIFIED.isFalsified()).isTrue();
    }

    @Test
    public void shouldNotBeErroneousWhenIsFalsified() {
        assertThat(CheckResultTest.FALSIFIED.isErroneous()).isFalse();
    }

    @Test
    public void shouldNotBeExhaustedWhenIsFalsified() {
        assertThat(CheckResultTest.FALSIFIED.isExhausted()).isFalse();
    }

    @Test
    public void shouldGetPropertyNameOfFalsified() {
        assertThat(CheckResultTest.FALSIFIED.propertyName()).isEqualTo("test");
    }

    @Test
    public void shouldGetCountOfFalsified() {
        assertThat(CheckResultTest.FALSIFIED.count()).isEqualTo(0);
    }

    @Test
    public void shouldHaveASampleWhenIsFalsified() {
        assertThat(CheckResultTest.FALSIFIED.sample().get()).isEqualTo(Tuple.of(1));
    }

    @Test
    public void shouldHaveNoErrorWhenIsFalsified() {
        assertThat(CheckResultTest.FALSIFIED.error()).isEqualTo(Option.none());
    }

    @Test
    public void shouldIdentififyEqualFalsifiedObjectsWhenObjectsAreIdentical() {
        final CheckResult.Falsified falsified = CheckResultTest.FALSIFIED;
        // noinspection EqualsWithItself
        assertThat(falsified.equals(falsified)).isTrue();
    }

    @Test
    public void shouldIdentififyEqualFalsifiedObjectsWhenObjectsHaveSameTypeAndEqualValues() {
        final CheckResult.Falsified falsified1 = CheckResultTest.FALSIFIED;
        final CheckResult.Falsified falsified2 = new CheckResult.Falsified("test", 0, Tuple.of(1));
        assertThat(falsified1.equals(falsified2)).isTrue();
    }

    @Test
    public void shouldIdentififyUnequalFalsifiedObjectsWhenTypesAreUnequal() {
        final CheckResult.Falsified falsified = CheckResultTest.FALSIFIED;
        assertThat(falsified.equals(new Object())).isFalse();
    }

    @Test
    public void shouldIdentififyUnequalFalsifiedObjectsWhenValuesAreUnequal() {
        final CheckResult.Falsified falsified = new CheckResult.Falsified("test", 1, Tuple.of(2));
        assertThat(falsified.equals(new CheckResult.Falsified("x", 1, Tuple.of(2)))).isFalse();
        assertThat(falsified.equals(new CheckResult.Falsified("test", (-1), Tuple.of(2)))).isFalse();
        assertThat(falsified.equals(new CheckResult.Falsified("test", 1, Tuple.of((-1))))).isFalse();
    }

    @Test
    public void shouldComputeHashCodeOfFalsified() {
        assertThat(CheckResultTest.FALSIFIED.hashCode()).isEqualTo(Objects.hash("test", 0, Tuple.of(1)));
    }

    @Test
    public void shouldComputeToStringOfFalsified() {
        assertThat(CheckResultTest.FALSIFIED.toString()).isEqualTo("Falsified(propertyName = test, count = 0, sample = (1))");
    }

    // -- Erroneous
    @Test
    public void shouldNotBeSatisfiedWhenIsErroneous() {
        assertThat(CheckResultTest.ERRONEOUS.isSatisfied()).isFalse();
    }

    @Test
    public void shouldNotBeFalsifiedWhenIsErroneous() {
        assertThat(CheckResultTest.ERRONEOUS.isFalsified()).isFalse();
    }

    @Test
    public void shouldBeErroneousWhenIsErroneous() {
        assertThat(CheckResultTest.ERRONEOUS.isErroneous()).isTrue();
    }

    @Test
    public void shouldNotBeExhaustedWhenIsErroneous() {
        assertThat(CheckResultTest.ERRONEOUS.isExhausted()).isFalse();
    }

    @Test
    public void shouldGetPropertyNameOfErroneous() {
        assertThat(CheckResultTest.ERRONEOUS.propertyName()).isEqualTo("test");
    }

    @Test
    public void shouldGetCountOfErroneous() {
        assertThat(CheckResultTest.ERRONEOUS.count()).isEqualTo(0);
    }

    @Test
    public void shouldHaveNoSampleWhenIsErroneousWithoutSample() {
        assertThat(CheckResultTest.ERRONEOUS.sample()).isEqualTo(Option.none());
    }

    @Test
    public void shouldHaveSampleWhenIsErroneousWithSample() {
        final CheckResult.Erroneous erroneous = new CheckResult.Erroneous("test", 1, new Error("test"), Option.of(Tuple.of(1)));
        assertThat(erroneous.sample().get()).isEqualTo(Tuple.of(1));
    }

    @Test
    public void shouldHaveAnErrorWhenIsErroneous() {
        assertThat(CheckResultTest.ERRONEOUS.error().get().getMessage()).isEqualTo("test");
    }

    @Test
    public void shouldIdentififyEqualErroneousObjectsWhenObjectsAreIdentical() {
        final CheckResult.Erroneous erroneous = CheckResultTest.ERRONEOUS;
        // noinspection EqualsWithItself
        assertThat(erroneous.equals(erroneous)).isTrue();
    }

    @Test
    public void shouldIdentififyEqualErroneousObjectsWhenObjectsHaveSameTypeAndEqualValues() {
        final CheckResult.Erroneous erroneous1 = CheckResultTest.ERRONEOUS;
        final CheckResult.Erroneous erroneous2 = new CheckResult.Erroneous("test", 0, new Error("test"), Option.none());
        assertThat(erroneous1.equals(erroneous2)).isTrue();
    }

    @Test
    public void shouldIdentififyUnequalErroneousObjectsWhenTypesAreUnequal() {
        final CheckResult.Erroneous erroneous = CheckResultTest.ERRONEOUS;
        assertThat(erroneous.equals(new Object())).isFalse();
    }

    @Test
    public void shouldIdentififyUnequalErroneousObjectsWhenValuesAreUnequal() {
        final CheckResult.Erroneous erroneous = new CheckResult.Erroneous("test", 1, new Error("error"), Option.none());
        assertThat(erroneous.equals(new CheckResult.Erroneous("x", 1, new Error("error"), Option.none()))).isFalse();
        assertThat(erroneous.equals(new CheckResult.Erroneous("test", (-1), new Error("error"), Option.none()))).isFalse();
        assertThat(erroneous.equals(new CheckResult.Erroneous("test", 1, new Error("x"), Option.none()))).isFalse();
        assertThat(erroneous.equals(new CheckResult.Erroneous("test", 1, new Error("error"), Option.some(Tuple.of(1))))).isFalse();
    }

    @Test
    public void shouldCheckDeepEqualityOfErroneousErrors() {
        Assertions.assertThat(new CheckResult.Erroneous("test", 1, null, Option.none())).isEqualTo(new CheckResult.Erroneous("test", 1, null, Option.none()));
        Assertions.assertThat(new CheckResult.Erroneous("test", 1, new Error("test"), Option.none())).isNotEqualTo(new CheckResult.Erroneous("test", 1, null, Option.none()));
        Assertions.assertThat(new CheckResult.Erroneous("test", 1, null, Option.none())).isNotEqualTo(new CheckResult.Erroneous("test", 1, new Error("test"), Option.none()));
        Assertions.assertThat(new CheckResult.Erroneous("test", 1, new Error("test"), Option.none())).isEqualTo(new CheckResult.Erroneous("test", 1, new Error("test"), Option.none()));
        Assertions.assertThat(new CheckResult.Erroneous("test", 1, new Error("test"), Option.none())).isNotEqualTo(new CheckResult.Erroneous("test", 1, new Error("x"), Option.none()));
        Assertions.assertThat(new CheckResult.Erroneous("test", 1, new Error("test", new Error("test2")), Option.none())).isEqualTo(new CheckResult.Erroneous("test", 1, new Error("test", new Error("test2")), Option.none()));
        Assertions.assertThat(new CheckResult.Erroneous("test", 1, new Error("test", new Error("test2")), Option.none())).isNotEqualTo(new CheckResult.Erroneous("test", 1, new Error("test"), Option.none()));
        Assertions.assertThat(new CheckResult.Erroneous("test", 1, new Error("test", new Error("test2")), Option.none())).isNotEqualTo(new CheckResult.Erroneous("test", 1, new Error("test", new Error("x")), Option.none()));
    }

    @Test
    public void shouldComputeHashCodeOfErroneous() {
        assertThat(CheckResultTest.ERRONEOUS.hashCode()).isEqualTo(Objects.hash("test", 0, CheckResultTest.ERRONEOUS.deepHashCode(new Error("test")), Option.none()));
    }

    @Test
    public void shouldComputeToStringOfErroneous() {
        assertThat(CheckResultTest.ERRONEOUS.toString()).isEqualTo("Erroneous(propertyName = test, count = 0, error = test, sample = None)");
    }

    // Assertions
    // -- satisfied
    @Test
    public void shouldAssertThatCheckResultIsSatisfied() {
        new CheckResult.Satisfied("test", 0, false).assertIsSatisfied();
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowWhenAssertThatNonSatisfiedCheckResultIsSatisfied() {
        assertIsSatisfied();
    }

    // -- satisfiedWithExhaustion
    @Test
    public void shouldAssertThatCheckResultIsSatisfiedWithExhaustionTrue() {
        new CheckResult.Satisfied("test", 0, true).assertIsSatisfiedWithExhaustion(true);
    }

    @Test
    public void shouldAssertThatCheckResultIsSatisfiedWithExhaustionFalse() {
        new CheckResult.Satisfied("test", 0, false).assertIsSatisfiedWithExhaustion(false);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowWhenAssertThatNonSatisfiedCheckResultIsSatisfiedWithExhaustionTrue() {
        assertIsSatisfiedWithExhaustion(true);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowWhenAssertThatNonSatisfiedCheckResultIsSatisfiedWithExhaustionFalse() {
        assertIsSatisfiedWithExhaustion(false);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowWhenAssertThatSatisfiedNonExhaustedCheckResultIsSatisfiedWithExhaustionTrue() {
        new CheckResult.Satisfied("test", 0, false).assertIsSatisfiedWithExhaustion(true);
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowWhenAssertThatSatisfiedExhaustedCheckResultIsSatisfiedWithExhaustionFalse() {
        new CheckResult.Satisfied("test", 0, true).assertIsSatisfiedWithExhaustion(false);
    }

    // -- falsified
    @Test
    public void shouldAssertThatCheckResultIsFalsified() {
        assertIsFalsified();
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowWhenAssertThatNonFalsifiedCheckResultIsFalsified() {
        new CheckResult.Satisfied("test", 0, false).assertIsFalsified();
    }

    // -- erroneous
    @Test
    public void shouldAssertThatCheckResultIsErroneous() {
        assertIsErroneous();
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowWhenAssertThatNonErroneousCheckResultIsErroneous() {
        assertIsErroneous();
    }
}

