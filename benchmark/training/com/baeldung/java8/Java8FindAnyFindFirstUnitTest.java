package com.baeldung.java8;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class Java8FindAnyFindFirstUnitTest {
    @Test
    public void createStream_whenFindAnyResultIsPresent_thenCorrect() {
        List<String> list = Arrays.asList("A", "B", "C", "D");
        Optional<String> result = list.stream().findAny();
        Assert.assertTrue(result.isPresent());
        Assert.assertThat(result.get(), Matchers.anyOf(Matchers.is("A"), Matchers.is("B"), Matchers.is("C"), Matchers.is("D")));
    }

    @Test
    public void createParallelStream_whenFindAnyResultIsPresent_thenCorrect() throws Exception {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> result = list.stream().parallel().filter(( num) -> num < 4).findAny();
        Assert.assertTrue(result.isPresent());
        Assert.assertThat(result.get(), Matchers.anyOf(Matchers.is(1), Matchers.is(2), Matchers.is(3)));
    }

    @Test
    public void createStream_whenFindFirstResultIsPresent_thenCorrect() {
        List<String> list = Arrays.asList("A", "B", "C", "D");
        Optional<String> result = list.stream().findFirst();
        Assert.assertTrue(result.isPresent());
        Assert.assertThat(result.get(), Matchers.is("A"));
    }
}

