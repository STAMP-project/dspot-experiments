package com.baeldung.vavr;


import io.vavr.CheckedFunction1;
import io.vavr.test.Arbitrary;
import io.vavr.test.CheckResult;
import io.vavr.test.Property;
import java.util.function.Predicate;
import org.junit.Test;


public class PropertyBasedLongRunningUnitTest {
    private static Predicate<Integer> divisibleByTwo = ( i) -> (i % 2) == 0;

    private static Predicate<Integer> divisibleByFive = ( i) -> (i % 5) == 0;

    @Test
    public void givenArbitrarySeq_whenCheckThatEverySecondElementIsEqualToString_thenTestPass() {
        // given
        Arbitrary<Integer> multiplesOf2 = Arbitrary.integer().filter(( i) -> i > 0).filter(( i) -> ((i % 2) == 0) && ((i % 5) != 0));
        // when
        CheckedFunction1<Integer, Boolean> mustEquals = ( i) -> stringsSupplier().get(i).equals("DividedByTwoWithoutRemainder");
        // then
        CheckResult result = Property.def("Every second element must equal to DividedByTwoWithoutRemainder").forAll(multiplesOf2).suchThat(mustEquals).check(10000, 100);
        result.assertIsSatisfied();
    }

    @Test
    public void givenArbitrarySeq_whenCheckThatEveryFifthElementIsEqualToString_thenTestPass() {
        // given
        Arbitrary<Integer> multiplesOf5 = Arbitrary.integer().filter(( i) -> i > 0).filter(( i) -> ((i % 5) == 0) && ((i % 2) == 0));
        // when
        CheckedFunction1<Integer, Boolean> mustEquals = ( i) -> stringsSupplier().get(i).endsWith("DividedByTwoAndFiveWithoutRemainder");
        // then
        Property.def("Every fifth element must equal to DividedByTwoAndFiveWithoutRemainder").forAll(multiplesOf5).suchThat(mustEquals).check(10000, 1000).assertIsSatisfied();
    }
}

