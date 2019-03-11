package com.baeldung.vavr.collections;


import io.vavr.API;
import io.vavr.Tuple3;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Assert;
import org.junit.Test;


public class CollectionFactoryMethodsUnitTest {
    @Test
    public void givenANoneOptionElement_whenCreated_thenCorrect() {
        Option<Integer> none = API.None();
        Assert.assertFalse((none == null));
        Assert.assertEquals(none, Option.none());
    }

    @Test
    public void givenASomeOptionElement_whenCreated_thenCorrect() {
        Option<Integer> some = API.Some(1);
        Assert.assertFalse((some == null));
        Assert.assertTrue(some.contains(1));
    }

    @Test
    public void givenATupleElement_whenCreated_thenCorrect() {
        Tuple3<Character, String, Integer> tuple = API.Tuple('a', "chain", 2);
        Assert.assertTrue((tuple != null));
        Assert.assertEquals(tuple._1(), new Character('a'));
        Assert.assertEquals(tuple._2(), "chain");
        Assert.assertEquals(tuple._3().intValue(), 2);
    }

    @Test
    public void givenASuccessObject_whenEvaluated_thenSuccess() {
        Try<Integer> integer = API.Success(55);
        Assert.assertEquals(integer.get().intValue(), 55);
    }

    @Test
    public void givenAFailureObject_whenEvaluated_thenExceptionThrown() {
        Try<Integer> failure = API.Failure(new Exception("Exception X encapsulated here"));
        try {
            Integer i = failure.get();// evaluate a failure raise the exception

            System.out.println(i);// not executed

        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "Exception X encapsulated here");
        }
    }

    @Test
    public void givenAList_whenCreated_thenCorrect() {
        API.List<Integer> list = API.List(1, 2, 3, 4, 5);
        Assert.assertEquals(list.size(), 5);
        Assert.assertEquals(list.get(0).intValue(), 1);
    }

    @Test
    public void givenAnEmptyList_whenCreated_thenCorrect() {
        API.List<Integer> empty = API.List();
        Assert.assertEquals(empty.size(), 0);
        Assert.assertEquals(empty, API.List.empty());
    }

    @Test
    public void givenAnArray_whenCreated_thenCorrect() {
        API.Array<Integer> array = API.Array(1, 2, 3, 4, 5);
        Assert.assertEquals(array.size(), 5);
        Assert.assertEquals(array.get(0).intValue(), 1);
    }

    @Test
    public void givenAStream_whenCreated_thenCorrect() {
        API.Stream<Integer> stream = API.Stream(1, 2, 3, 4, 5);
        Assert.assertEquals(stream.size(), 5);
        Assert.assertEquals(stream.get(0).intValue(), 1);
    }

    @Test
    public void givenAVector_whenCreated_thenCorrect() {
        API.Vector<Integer> vector = API.Vector(1, 2, 3, 4, 5);
        Assert.assertEquals(vector.size(), 5);
        Assert.assertEquals(vector.get(0).intValue(), 1);
    }
}

