package com.baeldung.java8.optional;


import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class OptionalChainingUnitTest {
    private boolean getEmptyEvaluated;

    private boolean getHelloEvaluated;

    private boolean getByeEvaluated;

    @Test
    public void givenThreeOptionals_whenChaining_thenFirstNonEmptyIsReturned() {
        Optional<String> found = Stream.of(getEmpty(), getHello(), getBye()).filter(Optional::isPresent).map(Optional::get).findFirst();
        Assert.assertEquals(getHello(), found);
    }

    @Test
    public void givenTwoEmptyOptionals_whenChaining_thenEmptyOptionalIsReturned() {
        Optional<String> found = Stream.of(getEmpty(), getEmpty()).filter(Optional::isPresent).map(Optional::get).findFirst();
        Assert.assertFalse(found.isPresent());
    }

    @Test
    public void givenTwoEmptyOptionals_whenChaining_thenDefaultIsReturned() {
        String found = Stream.<Supplier<Optional<String>>>of(() -> createOptional("empty"), () -> createOptional("empty")).map(Supplier::get).filter(Optional::isPresent).map(Optional::get).findFirst().orElseGet(() -> "default");
        Assert.assertEquals("default", found);
    }

    @Test
    public void givenThreeOptionals_whenChaining_thenFirstNonEmptyIsReturnedAndRestNotEvaluated() {
        Optional<String> found = Stream.<Supplier<Optional<String>>>of(this::getEmpty, this::getHello, this::getBye).map(Supplier::get).filter(Optional::isPresent).map(Optional::get).findFirst();
        Assert.assertTrue(this.getEmptyEvaluated);
        Assert.assertTrue(this.getHelloEvaluated);
        Assert.assertFalse(this.getByeEvaluated);
        Assert.assertEquals(getHello(), found);
    }

    @Test
    public void givenTwoOptionalsReturnedByOneArgMethod_whenChaining_thenFirstNonEmptyIsReturned() {
        Optional<String> found = Stream.<Supplier<Optional<String>>>of(() -> createOptional("empty"), () -> createOptional("hello")).map(Supplier::get).filter(Optional::isPresent).map(Optional::get).findFirst();
        Assert.assertEquals(createOptional("hello"), found);
    }
}

