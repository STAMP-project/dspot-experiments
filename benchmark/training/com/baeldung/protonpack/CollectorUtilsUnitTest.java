package com.baeldung.protonpack;


import com.codepoetics.protonpack.collectors.CollectorUtils;
import com.codepoetics.protonpack.collectors.NonUniqueValueException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class CollectorUtilsUnitTest {
    @Test
    public void givenIntegerStream_whenCollectOnMaxByProjection_shouldReturnOptionalMaxValue() {
        Stream<String> integerStream = Stream.of("a", "bb", "ccc", "1");
        Optional<String> max = integerStream.collect(maxBy(String::length));
        MatcherAssert.assertThat(max.get(), CoreMatchers.is("ccc"));
    }

    @Test
    public void givenIntegerStream_whenCollectOnMinByProjection_shouldReturnOptionalMinValue() {
        Stream<String> integerStream = Stream.of("abc", "bb", "ccc", "1");
        Optional<String> max = integerStream.collect(minBy(String::length));
        MatcherAssert.assertThat(max.get(), CoreMatchers.is("1"));
    }

    @Test
    public void givenEmptyStream_withCollectorUnique_shouldReturnEmpty() {
        MatcherAssert.assertThat(Stream.empty().collect(CollectorUtils.unique()), CoreMatchers.equalTo(Optional.empty()));
    }

    @Test
    public void givenIntegerStream_withCollectorUnique_shouldReturnUniqueValue() {
        MatcherAssert.assertThat(Stream.of(1, 2, 3).filter(( i) -> i > 2).collect(CollectorUtils.unique()), CoreMatchers.equalTo(Optional.of(3)));
    }

    @Test
    public void givenIntegerStream_withUniqueNullable_shouldReturnUniqueValue() {
        MatcherAssert.assertThat(Stream.of(1, 2, 3).filter(( i) -> i > 2).collect(CollectorUtils.uniqueNullable()), CoreMatchers.equalTo(3));
    }

    @Test(expected = NonUniqueValueException.class)
    public void givenIntegerStream_withCollectorUnique_shouldThrowNonUniqueValueException() {
        Stream.of(1, 2, 3).filter(( i) -> i > 1).collect(CollectorUtils.unique());
    }
}

