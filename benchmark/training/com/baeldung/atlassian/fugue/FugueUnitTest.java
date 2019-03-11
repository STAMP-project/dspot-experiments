package com.baeldung.atlassian.fugue;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


public class FugueUnitTest {
    @Test
    public void whenSome_thenDefined() {
        Option<String> some = Option.some("value");
        Assert.assertTrue(some.isDefined());
        Assert.assertEquals("value", some.get());
    }

    @Test
    public void whenNone_thenNotDefined() {
        Option<Object> none = Option.none();
        Assert.assertFalse(none.isDefined());
        Assert.assertEquals(1, none.getOrElse(1));
    }

    @Test
    public void whenSomeNull_thenException() {
        try {
            Option.some(null);
            Assert.fail("some(null) should throw");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void whenNullOption_thenSome() {
        Option<String> some = Option.some("value").map(String::toUpperCase);
        Assert.assertEquals("VALUE", some.get());
        some = some.map(( x) -> null);
        Assert.assertNull(some.get());
        some.forEach(Assert::assertNull);
        for (Object value : some) {
            Assert.assertNull(value);
        }
        Assert.assertEquals(1, some.toStream().count());
        Option<Object> none = Option.some("value").flatMap(( x) -> Option.none());
        Assert.assertFalse(none.isDefined());
        none = Option.some("value").flatMap(Options.nullSafe(( x) -> null));
        Assert.assertFalse(none.isDefined());
    }

    @Test
    public void whenNone_thenEmptyOptional() {
        Optional<Object> optional = Option.none().toOptional();
        Assert.assertFalse(optional.isPresent());
        Assert.assertTrue(Option.fromOptional(optional).isEmpty());
    }

    @Test
    public void whenOption_thenIterable() {
        Option<String> some = Option.some("value");
        Iterable<String> strings = Iterables.concat(some, Arrays.asList("a", "b", "c"));
        List<String> stringList = new ArrayList<>();
        Iterables.addAll(stringList, strings);
        Assert.assertEquals(4, stringList.size());
    }

    @Test
    public void whenOption_thenStream() {
        Assert.assertEquals(0, Option.none().toStream().count());
        Assert.assertEquals(1, Option.some("value").toStream().count());
    }

    @Test
    public void whenLift_thenPartialFunction() {
        Function<Integer, Integer> f = (Integer x) -> x > 0 ? x + 1 : null;
        Function<Option<Integer>, Option<Integer>> lifted = Options.lift(f);
        Assert.assertEquals(2, ((long) (lifted.apply(Option.some(1)).get())));
        Assert.assertTrue(lifted.apply(Option.none()).isEmpty());
        Assert.assertEquals(null, lifted.apply(Option.some(0)).get());
    }

    @Test
    public void whenLeft_thenEither() {
        Either<Integer, String> right = Either.right("value");
        Either<Integer, String> left = Either.left((-1));
        if (right.isLeft()) {
            Assert.fail();
        }
        if (left.isRight()) {
            Assert.fail();
        }
        String s = right.map(String::toUpperCase).getOrNull();
        Assert.assertEquals("VALUE", s);
        Either<String, String> either = right.left().map(( x) -> decodeSQLErrorCode(x));
        Assert.assertTrue(either.isRight());
        Assert.assertEquals("value", either.right().get());
        either.right().forEach(( x) -> assertEquals("value", x));
    }

    @Test
    public void whenTryIsFailure_thenIsFailureReturnsTrue() {
        Assert.assertTrue(Try.failure(new Exception("Fail!")).isFailure());
    }

    @Test
    public void whenTryIsSuccess_thenIsSuccessReturnsTrue() {
        Assert.assertTrue(Try.successful("OK").isSuccess());
    }

    @Test
    public void givenFunctionReturning_whenCheckedOf_thenSuccess() {
        Assert.assertTrue(Checked.of(() -> "ok").isSuccess());
    }

    @Test
    public void givenFunctionThrowing_whenCheckedOf_thenFailure() {
        Assert.assertTrue(Checked.of(() -> {
            throw new Exception("ko");
        }).isFailure());
    }

    @Test
    public void givenFunctionThrowing_whenCheckedLift_thenFailure() {
        Checked.Function<String, Object, Exception> throwException = (String x) -> {
            throw new Exception(x);
        };
        Assert.assertTrue(Checked.lift(throwException).apply("ko").isFailure());
    }

    @Test
    public void whenRecover_thenSuccessfulTry() {
        Try<Object> recover = Try.failure(new Exception("boo!")).recover((Exception e) -> (e.getMessage()) + " recovered.");
        Assert.assertTrue(recover.isSuccess());
        Assert.assertEquals("boo! recovered.", recover.getOrElse(() -> null));
        recover = Try.failure(new Exception("boo!")).recoverWith((Exception e) -> Try.successful("recovered again!"));
        Assert.assertTrue(recover.isSuccess());
        Assert.assertEquals("recovered again!", recover.getOrElse(() -> null));
    }

    @Test
    public void whenFailure_thenMapNotCalled() {
        Try<Object> recover = Try.failure(new Exception("boo!")).map(( x) -> {
            fail("Oh, no!");
            return null;
        }).recover(Function.identity());
        Exception exception = ((Exception) (recover.toOption().get()));
        Assert.assertTrue(recover.isSuccess());
        Assert.assertEquals("boo!", exception.getMessage());
    }

    @Test
    public void whenException_thenTryThrows() {
        Try<Object> checked = Checked.of(() -> {
            throw new Exception("Aaargh!");
        });
        Either<Exception, Object> either = checked.toEither();
        Assert.assertTrue(checked.isFailure());
        Assert.assertTrue(either.isLeft());
        Assert.assertEquals(42, checked.getOrElse(() -> 42));
        try {
            checked.getOrElse(() -> {
                throw new NoSuchElementException("fail");
            });
            Assert.fail("Was expecting exception");
        } catch (Exception e) {
            Assert.assertEquals("fail", e.getMessage());
        }
    }

    @Test
    public void whenRecoverThrows_thenFailure() {
        Try<Object> failure = Try.failure(new Exception("boo!")).recover(( x) -> {
            throw new RuntimeException(x);
        });
        Assert.assertTrue(failure.isFailure());
    }

    @Test
    public void whenPair_thenLeftAndRight() {
        Pair<Integer, String> pair = Pair.pair(1, "a");
        Assert.assertEquals(1, ((int) (pair.left())));
        Assert.assertEquals("a", pair.right());
    }
}

