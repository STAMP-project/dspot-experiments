package com.baeldung.string;


import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StringToCharStreamUnitTest {
    private String testString = "Tests";

    @Test
    public void givenTestString_whenChars_thenReturnIntStream() {
        Assert.assertThat(testString.chars(), CoreMatchers.instanceOf(IntStream.class));
    }

    @Test
    public void givenTestString_whenCodePoints_thenReturnIntStream() {
        Assert.assertThat(testString.codePoints(), CoreMatchers.instanceOf(IntStream.class));
    }

    @Test
    public void givenTestString_whenCodePoints_thenShowOccurences() throws Exception {
        Map<Character, Integer> map = testString.codePoints().mapToObj(( c) -> ((char) (c))).filter(Character::isLetter).collect(Collectors.toMap(( c) -> c, ( c) -> 1, Integer::sum));
        System.out.println(map);
    }

    @Test
    public void givenIntStream_whenMapToObj_thenReturnCharacterStream() {
        Stream<Character> characterStream = testString.chars().mapToObj(( c) -> ((char) (c)));
        Stream<Character> characterStream1 = testString.codePoints().mapToObj(( c) -> ((char) (c)));
        Assert.assertNotNull("IntStream returned by chars() did not map to Stream<Character>", characterStream);
        Assert.assertNotNull("IntStream returned by codePoints() did not map to Stream<Character>", characterStream1);
    }

    @Test
    public void givenIntStream_whenMapToObj_thenReturnStringStream() {
        List<String> strings = testString.codePoints().mapToObj(( c) -> String.valueOf(((char) (c)))).collect(Collectors.toList());
        Assert.assertEquals(strings.size(), 5);
    }
}

