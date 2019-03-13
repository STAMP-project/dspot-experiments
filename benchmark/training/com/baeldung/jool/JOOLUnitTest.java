package com.baeldung.jool;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.jooq.lambda.Seq;
import org.jooq.lambda.function.Function1;
import org.jooq.lambda.function.Function2;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.junit.Test;


public class JOOLUnitTest {
    @Test
    public void givenSeq_whenCheckContains_shouldReturnTrue() {
        List<Integer> concat = Seq.of(1, 2, 3).concat(Seq.of(4, 5, 6)).toList();
        TestCase.assertEquals(concat, Arrays.asList(1, 2, 3, 4, 5, 6));
        Assert.assertTrue(Seq.of(1, 2, 3, 4).contains(2));
        Assert.assertTrue(Seq.of(1, 2, 3, 4).containsAll(2, 3));
        Assert.assertTrue(Seq.of(1, 2, 3, 4).containsAny(2, 5));
    }

    @Test
    public void givenStreams_whenJoin_shouldHaveElementsFromTwoStreams() {
        // given
        Stream<Integer> left = Stream.of(1, 2, 4);
        Stream<Integer> right = Stream.of(1, 2, 3);
        // when
        List<Integer> rightCollected = right.collect(Collectors.toList());
        List<Integer> collect = left.filter(rightCollected::contains).collect(Collectors.toList());
        // then
        TestCase.assertEquals(collect, Arrays.asList(1, 2));
    }

    @Test
    public void givenSeq_whenJoin_shouldHaveElementsFromBothSeq() {
        TestCase.assertEquals(Seq.of(1, 2, 4).innerJoin(Seq.of(1, 2, 3), Objects::equals).toList(), Arrays.asList(tuple(1, 1), tuple(2, 2)));
        TestCase.assertEquals(Seq.of(1, 2, 4).leftOuterJoin(Seq.of(1, 2, 3), Objects::equals).toList(), Arrays.asList(tuple(1, 1), tuple(2, 2), tuple(4, null)));
        TestCase.assertEquals(Seq.of(1, 2, 4).rightOuterJoin(Seq.of(1, 2, 3), Objects::equals).toList(), Arrays.asList(tuple(1, 1), tuple(2, 2), tuple(null, 3)));
        TestCase.assertEquals(Seq.of(1, 2).crossJoin(Seq.of("A", "B")).toList(), Arrays.asList(tuple(1, "A"), tuple(1, "B"), tuple(2, "A"), tuple(2, "B")));
    }

    @Test
    public void givenSeq_whenManipulateSeq_seqShouldHaveNewElementsInIt() {
        TestCase.assertEquals(Seq.of(1, 2, 3).cycle().limit(9).toList(), Arrays.asList(1, 2, 3, 1, 2, 3, 1, 2, 3));
        TestCase.assertEquals(Seq.of(1, 2, 3).duplicate().map(( first, second) -> tuple(first.toList(), second.toList())), tuple(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3)));
        TestCase.assertEquals(Seq.of(1, 2, 3, 4).intersperse(0).toList(), Arrays.asList(1, 0, 2, 0, 3, 0, 4));
        TestCase.assertEquals(Seq.of(1, 2, 3, 4, 5).shuffle().toList().size(), 5);
        TestCase.assertEquals(Seq.of(1, 2, 3, 4).partition(( i) -> i > 2).map(( first, second) -> tuple(first.toList(), second.toList())), tuple(Arrays.asList(3, 4), Arrays.asList(1, 2)));
        TestCase.assertEquals(Seq.of(1, 2, 3, 4).reverse().toList(), Arrays.asList(4, 3, 2, 1));
    }

    @Test
    public void givenSeq_whenGroupByAndFold_shouldReturnProperSeq() {
        Map<Integer, List<Integer>> expectedAfterGroupBy = new HashMap<>();
        expectedAfterGroupBy.put(1, Arrays.asList(1, 3));
        expectedAfterGroupBy.put(0, Arrays.asList(2, 4));
        TestCase.assertEquals(Seq.of(1, 2, 3, 4).groupBy(( i) -> i % 2), expectedAfterGroupBy);
        TestCase.assertEquals(Seq.of("a", "b", "c").foldLeft("!", ( u, t) -> u + t), "!abc");
        TestCase.assertEquals(Seq.of("a", "b", "c").foldRight("!", ( t, u) -> t + u), "abc!");
    }

    @Test
    public void givenSeq_whenUsingSeqWhile_shouldBehaveAsWhileLoop() {
        TestCase.assertEquals(Seq.of(1, 2, 3, 4, 5).skipWhile(( i) -> i < 3).toList(), Arrays.asList(3, 4, 5));
        TestCase.assertEquals(Seq.of(1, 2, 3, 4, 5).skipUntil(( i) -> i == 3).toList(), Arrays.asList(3, 4, 5));
    }

    @Test
    public void givenSeq_whenZip_shouldHaveZippedSeq() {
        TestCase.assertEquals(Seq.of(1, 2, 3).zip(Seq.of("a", "b", "c")).toList(), Arrays.asList(tuple(1, "a"), tuple(2, "b"), tuple(3, "c")));
        TestCase.assertEquals(Seq.of(1, 2, 3).zip(Seq.of("a", "b", "c"), ( x, y) -> (x + ":") + y).toList(), Arrays.asList("1:a", "2:b", "3:c"));
        TestCase.assertEquals(Seq.of("a", "b", "c").zipWithIndex().toList(), Arrays.asList(tuple("a", 0L), tuple("b", 1L), tuple("c", 2L)));
    }

    @Test
    public void givenOperationThatThrowsCheckedException_whenExecuteAndNeedToWrapCheckedIntoUnchecked_shouldPass() {
        // when
        List<Integer> collect = Stream.of("a", "b", "c").map(( elem) -> {
            try {
                return methodThatThrowsChecked(elem);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        // then
        TestCase.assertEquals(collect, Arrays.asList(1, 1, 1));
    }

    @Test
    public void givenOperationThatThrowsCheckedException_whenExecuteUsingUncheckedFuction_shouldPass() {
        // when
        List<Integer> collect = Stream.of("a", "b", "c").map(org.jooq.lambda.Unchecked.function(this::methodThatThrowsChecked)).collect(Collectors.toList());
        // then
        TestCase.assertEquals(collect, Arrays.asList(1, 1, 1));
    }

    @Test
    public void givenFunction_whenAppliedPartially_shouldAddNumberToPartialArgument() {
        // given
        Function2<Integer, Integer, Integer> addTwoNumbers = ( v1, v2) -> v1 + v2;
        addTwoNumbers.toBiFunction();
        Function1<Integer, Integer> addToTwo = addTwoNumbers.applyPartially(2);
        // when
        Integer result = addToTwo.apply(5);
        // then
        TestCase.assertEquals(result, ((Integer) (7)));
    }

    @Test
    public void givenSeqOfTuples_whenTransformToLowerNumberOfTuples_shouldHaveProperResult() {
        // given
        Seq<Tuple3<String, String, Integer>> personDetails = Seq.of(tuple("michael", "similar", 49), tuple("jodie", "variable", 43));
        Tuple2<String, String> tuple = tuple("winter", "summer");
        // when
        List<Tuple4<String, String, String, String>> result = personDetails.map(( t) -> t.limit2().concat(tuple)).toList();
        // then
        TestCase.assertEquals(result, Arrays.asList(tuple("michael", "similar", "winter", "summer"), tuple("jodie", "variable", "winter", "summer")));
    }
}

