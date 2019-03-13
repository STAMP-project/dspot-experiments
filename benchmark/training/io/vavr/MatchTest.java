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
package io.vavr;


import Match.Case;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Option.Some;
import io.vavr.control.Validation;
import io.vavr.match.annotation.Patterns;
import io.vavr.match.annotation.Unapply;
import java.math.BigDecimal;
import java.time.Year;
import java.util.function.Predicate;
import org.junit.Test;


public class MatchTest {
    // -- MatchError
    @Test(expected = MatchError.class)
    public void shouldThrowIfNotMatching() {
        Match(new Object()).of(Case(API.$(( ignored) -> false), ( o) -> null));
    }

    // -- $()
    @Test
    public void shouldMatchNullWithAnyReturningValue() {
        final Case<Object, Integer> _case = Case(API.$(), 1);
        final Object obj = null;
        assertThat(_case.isDefinedAt(obj)).isTrue();
        assertThat(_case.apply(obj)).isEqualTo(1);
    }

    @Test
    public void shouldMatchAnyReturningValue() {
        final Case<Object, Integer> _case = Case(API.$(), 1);
        final Object obj = new Object();
        assertThat(_case.isDefinedAt(obj)).isTrue();
        assertThat(_case.apply(obj)).isEqualTo(1);
    }

    @Test
    public void shouldMatchNullWithAnyReturningAppliedFunction() {
        final Case<Object, Integer> _case = Case(API.$(), ( o) -> 1);
        final Object obj = null;
        assertThat(_case.isDefinedAt(obj)).isTrue();
        assertThat(_case.apply(obj)).isEqualTo(1);
    }

    @Test
    public void shouldMatchAnyReturningAppliedFunction() {
        final Case<Object, Integer> _case = Case(API.$(), ( o) -> 1);
        final Object obj = new Object();
        assertThat(_case.isDefinedAt(obj)).isTrue();
        assertThat(_case.apply(obj)).isEqualTo(1);
    }

    @Test
    public void shouldTakeFirstMatch() {
        final String actual = Match(new Object()).of(Case(API.$(), "first"), Case(API.$(), "second"));
        assertThat(actual).isEqualTo("first");
    }

    // -- $(value)
    @Test
    public void shouldMatchValueReturningValue() {
        final Object obj = new Object();
        final Case<Object, Integer> _case = Case(API.$(obj), 1);
        assertThat(_case.isDefinedAt(obj)).isTrue();
        assertThat(_case.apply(obj)).isEqualTo(1);
    }

    @Test
    public void shouldMatchValueReturningValue_NegativeCase() {
        final Object obj = new Object();
        final Case<Object, Integer> _case = Case(API.$(obj), 1);
        assertThat(_case.isDefinedAt(new Object())).isFalse();
    }

    @Test
    public void shouldMatchValueReturningAppliedFunction() {
        final Object obj = new Object();
        final Case<Object, Integer> _case = Case(API.$(obj), ( o) -> 1);
        assertThat(_case.isDefinedAt(obj)).isTrue();
        assertThat(_case.apply(obj)).isEqualTo(1);
    }

    @Test
    public void shouldMatchValueReturningAppliedFunction_NegativeCase() {
        final Object obj = new Object();
        final Case<Object, Integer> _case = Case(API.$(obj), ( o) -> 1);
        assertThat(_case.isDefinedAt(new Object())).isFalse();
    }

    // -- $(predicate)
    @Test
    public void shouldMatchPredicateReturningValue() {
        final Object obj = new Object();
        final Case<Object, Integer> _case = Case(API.$(is(obj)), 1);
        assertThat(_case.isDefinedAt(obj)).isTrue();
        assertThat(_case.apply(obj)).isEqualTo(1);
    }

    @Test
    public void shouldMatchPredicateReturningValue_NegativeCase() {
        final Object obj = new Object();
        final Case<Object, Integer> _case = Case(API.$(is(obj)), 1);
        assertThat(_case.isDefinedAt(new Object())).isFalse();
    }

    @Test
    public void shouldMatchPredicateReturningAppliedFunction() {
        final Object obj = new Object();
        final Case<Object, Integer> _case = Case(API.$(is(obj)), ( o) -> 1);
        assertThat(_case.isDefinedAt(obj)).isTrue();
        assertThat(_case.apply(obj)).isEqualTo(1);
    }

    @Test
    public void shouldMatchPredicateReturningAppliedFunction_NegativeCase() {
        final Object obj = new Object();
        final Case<Object, Integer> _case = Case(API.$(is(obj)), ( o) -> 1);
        assertThat(_case.isDefinedAt(new Object())).isFalse();
    }

    // -- multiple cases
    // i match {
    // case 1 => "one"
    // case 2 => "two"
    // case _ => "many"
    // }
    @Test
    public void shouldMatchIntUsingPatterns() {
        final String actual = Match(3).of(Case(API.$(1), "one"), Case(API.$(2), "two"), Case(API.$(), "many"));
        assertThat(actual).isEqualTo("many");
    }

    @Test
    public void shouldMatchIntUsingPredicates() {
        final String actual = Match(3).of(Case(API.$(is(1)), "one"), Case(API.$(is(2)), "two"), Case(API.$(), "many"));
        assertThat(actual).isEqualTo("many");
    }

    @Test
    public void shouldComputeUpperBoundOfReturnValue() {
        final Number num = Match(3).of(Case(API.$(is(1)), 1), Case(API.$(is(2)), 2.0), Case(API.$(), ( i) -> new BigDecimal(("" + i))));
        assertThat(num).isEqualTo(new BigDecimal("3"));
    }

    // -- instanceOf
    @Test
    public void shouldMatchUsingInstanceOf() {
        final Object obj = 1;
        final int actual = Match(obj).of(Case(API.$(instanceOf(Year.class)), ( y) -> 0), Case(API.$(instanceOf(Integer.class)), ( i) -> 1));
        assertThat(actual).isEqualTo(1);
    }

    // -- Either
    @Test
    public void shouldMatchLeft() {
        final Either<Integer, String> either = Either.left(1);
        final String actual = Match(either).of(Case($Left(API.$()), ( l) -> "left: " + l), Case($Right(API.$()), ( r) -> "right: " + r));
        assertThat(actual).isEqualTo("left: 1");
    }

    @Test
    public void shouldMatchRight() {
        final Either<Integer, String> either = Either.right("a");
        final String actual = Match(either).of(Case($Left(API.$()), ( l) -> "left: " + l), Case($Right(API.$()), ( r) -> "right: " + r));
        assertThat(actual).isEqualTo("right: a");
    }

    // -- Option
    @Test
    public void shouldMatchSome() {
        final Option<Integer> opt = Option.some(1);
        final String actual = Match(opt).of(Case($None(), "no value"), Case($Some(API.$()), String::valueOf));
        assertThat(actual).isEqualTo("1");
    }

    @Test
    public void shouldMatchNone() {
        final Option<Integer> opt = Option.none();
        final String actual = Match(opt).of(Case($Some(API.$()), String::valueOf), Case($None(), "no value"));
        assertThat(actual).isEqualTo("no value");
    }

    @Test
    public void shouldDecomposeSomeTuple() {
        final Option<Tuple2<String, Integer>> tuple2Option = Option.of(Tuple.of("Test", 123));
        final Tuple2<String, Integer> actual = Match(tuple2Option).of(Case($Some(API.$()), ( value) -> {
            @SuppressWarnings("UnnecessaryLocalVariable")
            final Tuple2<String, Integer> tuple2 = value;// types are inferred correctly!

            return tuple2;
        }));
        assertThat(actual).isEqualTo(Tuple.of("Test", 123));
    }

    @Test
    public void shouldDecomposeSomeSomeTuple() {
        final Option<Option<Tuple2<String, Integer>>> tuple2OptionOption = Option.of(Option.of(Tuple.of("Test", 123)));
        final Some<Tuple2<String, Integer>> actual = Match(tuple2OptionOption).of(Case($Some($Some(API.$(Tuple.of("Test", 123)))), ( value) -> {
            @SuppressWarnings("UnnecessaryLocalVariable")
            final Some<Tuple2<String, Integer>> some = value;// types are inferred correctly!

            return some;
        }));
        assertThat(actual).isEqualTo(Option.of(Tuple.of("Test", 123)));
    }

    // -- List
    @Test
    public void shouldDecomposeEmptyList() {
        final List<Integer> list = List.empty();
        final boolean isEmpty = Match(list).of(Case($Cons(API.$(), API.$()), ( x, xs) -> false), Case($Nil(), true));
        assertThat(isEmpty).isTrue();
    }

    @Test
    public void shouldDecomposeNonEmptyList() {
        final List<Integer> list = List.of(1);
        final boolean isNotEmpty = Match(list).of(Case($Nil(), false), Case($Cons(API.$(), API.$()), ( x, xs) -> true));
        assertThat(isNotEmpty).isTrue();
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void shouldDecomposeListOfTuple3() {
        final List<Tuple3<String, Integer, Double>> tuple3List = List.of(Tuple.of("begin", 10, 4.5), Tuple.of("middle", 11, 0.0), Tuple.of("end", 12, 1.2));
        final String actual = Match(tuple3List).of(Case($Cons(API.$(), API.$()), ( x, xs) -> {
            // types are inferred correctly!
            final Tuple3<String, Integer, Double> head = x;
            final List<Tuple3<String, Integer, Double>> tail = xs;
            return (head + "::") + tail;
        }));
        assertThat(actual).isEqualTo("(begin, 10, 4.5)::List((middle, 11, 0.0), (end, 12, 1.2))");
    }

    /* JDK 9 compiler errors:

    [ERROR] incompatible types: inferred type does not conform to equality constraint(s)
    inferred: io.vavr.control.Option.Some<java.lang.Number>
    equality constraints(s): io.vavr.control.Option.Some<java.lang.Integer>

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void shouldDecomposeListWithNonEmptyTail() {
    final List<Option<Number>> numberOptionList = List.of(Option.some(1), Option.some(2.0));
    final String actual = Match(numberOptionList).of(
    Case($Cons($Some($(1)), $Cons($Some($(2.0)), $())),  (x, xs) -> {
    final Option<Number> head = x;
    final List<Option<Number>> tail = xs;
    return head + "::" + tail;
    })
    );
    assertThat(actual).isEqualTo("Some(1)::List(Some(2.0))");
    }
     */
    // -- Set
    @Test
    public void shouldDecomposeSet() {
        final Set<String> abc = Set("abc");
        final Set<String> result = // Does not compile: the Java inference engine sees abc as a Function1<String, Boolean> before a Set<String> thus expects result to be of type Boolean
        Match(abc).of(Case(API.$(), () -> abc));
        assertThat(result).isEqualTo(abc);
    }

    // -- Validation
    @Test
    public void shouldDecomposeValid() {
        final Validation<String, Integer> valid = Validation.valid(1);
        final String actual = Match(valid).of(Case($Valid(API.$(1)), ( i) -> "ok"), Case($Invalid(API.$()), ( error) -> error));
        assertThat(actual).isEqualTo("ok");
    }

    @Test
    public void shouldDecomposeInvalid() {
        final Validation<String, Integer> valid = Validation.invalid("ok");
        final String actual = Match(valid).of(Case($Valid(API.$()), ( i) -> "error"), Case($Invalid(API.$("ok")), ( error) -> error));
        assertThat(actual).isEqualTo("ok");
    }

    // -- run
    @Test
    public void shouldRunUnitOfWork() {
        class OuterWorld {
            String effect = null;

            void displayHelp() {
                effect = "help";
            }

            void displayVersion() {
                effect = "version";
            }
        }
        final OuterWorld outerWorld = new OuterWorld();
        Match("-v").of(Case(API.$(isIn("-h", "--help")), ( o) -> run(outerWorld::displayHelp)), Case(API.$(isIn("-v", "--version")), ( o) -> run(outerWorld::displayVersion)), Case(API.$(), ( o) -> {
            throw new IllegalArgumentException();
        }));
        assertThat(outerWorld.effect).isEqualTo("version");
    }

    @Test
    public void shouldRunWithInferredArguments() {
        class OuterWorld {
            Number effect = null;

            void writeInt(int i) {
                effect = i;
            }

            void writeDouble(double d) {
                effect = d;
            }
        }
        final OuterWorld outerWorld = new OuterWorld();
        final Object obj = 0.1;
        Match(obj).of(Case(API.$(instanceOf(Integer.class)), ( i) -> run(() -> outerWorld.writeInt(i))), Case(API.$(instanceOf(Double.class)), ( d) -> run(() -> outerWorld.writeDouble(d))), Case(API.$(), ( o) -> {
            throw new NumberFormatException();
        }));
        assertThat(outerWorld.effect).isEqualTo(0.1);
    }

    // -- Developer
    @Test
    public void shouldMatchCustomTypeWithUnapplyMethod() {
        final MatchTest.Person person = new MatchTest.Developer("Daniel", true, Option.some(13));
        final String actual = Match(person).of(Case(MatchTest_DeveloperPatterns.$Developer(API.$("Daniel"), API.$(true), API.$()), MatchTest.Person.Util::devInfo), Case(API.$(), ( p) -> "Unknown person: " + (p.getName())));
        assertThat(actual).isEqualTo("Daniel is caffeinated.");
    }

    interface Person {
        String getName();

        class Util {
            static String devInfo(String name, boolean isCaffeinated, Option<Number> number) {
                return ((name + " is ") + (isCaffeinated ? "" : "not ")) + "caffeinated.";
            }
        }
    }

    static final class Developer implements MatchTest.Person {
        private final String name;

        private final boolean isCaffeinated;

        private final Option<Number> number;

        Developer(String name, boolean isCaffeinated, Option<Number> number) {
            this.name = name;
            this.isCaffeinated = isCaffeinated;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public boolean isCaffeinated() {
            return isCaffeinated;
        }

        public Option<Number> number() {
            return number;
        }

        @Patterns
        static class $ {
            @Unapply
            static Tuple3<String, Boolean, Option<Number>> Developer(MatchTest.Developer dev) {
                return Tuple.of(dev.getName(), dev.isCaffeinated(), dev.number());
            }
        }
    }

    // Ambiguity check
    @Test
    public void shouldNotAmbiguous() {
        {
            // value
            // Case("1", o -> "ok"); // Not possible, would lead to ambiguities (see below)
            assertThat(Case(API.$("1"), () -> "ok").apply("1")).isEqualTo("ok");
            assertThat(Case(API.$("1"), "ok").apply("1")).isEqualTo("ok");
        }
        {
            // predicate as variable
            Predicate<String> p = ( s) -> true;
            assertThat(Case(API.$(p), ( o) -> "ok").apply("1")).isEqualTo("ok");// ambiguous, if Case(T, Function<T, R>) present

            assertThat(Case(API.$(p), () -> "ok").apply("1")).isEqualTo("ok");
            assertThat(Case(API.$(p), "ok").apply("1")).isEqualTo("ok");
        }
        {
            // $(predicate)
            assertThat(Case(API.$(( o) -> true), ( o) -> "ok").apply("1")).isEqualTo("ok");// ambiguous, if Case(T, Function<T, R>) present

            assertThat(Case(API.$(( o) -> true), () -> "ok").apply("1")).isEqualTo("ok");
            assertThat(Case(API.$(( o) -> true), "ok").apply("1")).isEqualTo("ok");
        }
        {
            // $(value)
            assertThat(Case(API.$("1"), ( o) -> "ok").apply("1")).isEqualTo("ok");// ambiguous, if Case(T, Function<T, R>) present

            assertThat(Case(API.$("1"), () -> "ok").apply("1")).isEqualTo("ok");
            assertThat(Case(API.$("1"), "ok").apply("1")).isEqualTo("ok");
        }
    }
}

