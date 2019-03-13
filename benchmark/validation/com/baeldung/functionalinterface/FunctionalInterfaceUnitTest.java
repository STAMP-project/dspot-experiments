package com.baeldung.functionalinterface;


import com.google.common.util.concurrent.Uninterruptibles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FunctionalInterfaceUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(FunctionalInterfaceUnitTest.class);

    @Test
    public void whenPassingLambdaToComputeIfAbsent_thenTheValueGetsComputedAndPutIntoMap() {
        Map<String, Integer> nameMap = new HashMap<>();
        Integer value = nameMap.computeIfAbsent("John", String::length);
        Assert.assertEquals(new Integer(4), nameMap.get("John"));
        Assert.assertEquals(new Integer(4), value);
    }

    @Test
    public void whenPassingMethodReferenceToComputeIfAbsent_thenTheValueGetsComputedAndPutIntoMap() {
        Map<String, Integer> nameMap = new HashMap<>();
        Integer value = nameMap.computeIfAbsent("John", String::length);
        Assert.assertEquals(new Integer(4), nameMap.get("John"));
        Assert.assertEquals(new Integer(4), value);
    }

    @Test
    public void whenUsingCustomFunctionalInterfaceForPrimitives_thenCanUseItAsLambda() {
        short[] array = new short[]{ ((short) (1)), ((short) (2)), ((short) (3)) };
        byte[] transformedArray = transformArray(array, ( s) -> ((byte) (s * 2)));
        byte[] expectedArray = new byte[]{ ((byte) (2)), ((byte) (4)), ((byte) (6)) };
        Assert.assertArrayEquals(expectedArray, transformedArray);
    }

    @Test
    public void whenUsingBiFunction_thenCanUseItToReplaceMapValues() {
        Map<String, Integer> salaries = new HashMap<>();
        salaries.put("John", 40000);
        salaries.put("Freddy", 30000);
        salaries.put("Samuel", 50000);
        salaries.replaceAll(( name, oldValue) -> name.equals("Freddy") ? oldValue : oldValue + 10000);
        Assert.assertEquals(new Integer(50000), salaries.get("John"));
        Assert.assertEquals(new Integer(30000), salaries.get("Freddy"));
        Assert.assertEquals(new Integer(60000), salaries.get("Samuel"));
    }

    @Test
    public void whenPassingLambdaToThreadConstructor_thenLambdaInferredToRunnable() {
        Thread thread = new Thread(() -> FunctionalInterfaceUnitTest.LOG.debug("Hello From Another Thread"));
        thread.start();
    }

    @Test
    public void whenUsingSupplierToGenerateNumbers_thenCanUseItInStreamGenerate() {
        int[] fibs = new int[]{ 0, 1 };
        Stream<Integer> fibonacci = Stream.generate(() -> {
            int result = fibs[1];
            int fib3 = (fibs[0]) + (fibs[1]);
            fibs[0] = fibs[1];
            fibs[1] = fib3;
            return result;
        });
        List<Integer> fibonacci5 = fibonacci.limit(5).collect(Collectors.toList());
        Assert.assertEquals(new Integer(1), fibonacci5.get(0));
        Assert.assertEquals(new Integer(1), fibonacci5.get(1));
        Assert.assertEquals(new Integer(2), fibonacci5.get(2));
        Assert.assertEquals(new Integer(3), fibonacci5.get(3));
        Assert.assertEquals(new Integer(5), fibonacci5.get(4));
    }

    @Test
    public void whenUsingConsumerInForEach_thenConsumerExecutesForEachListElement() {
        List<String> names = Arrays.asList("John", "Freddy", "Samuel");
        names.forEach(( name) -> FunctionalInterfaceUnitTest.LOG.debug(("Hello, " + name)));
    }

    @Test
    public void whenUsingBiConsumerInForEach_thenConsumerExecutesForEachMapElement() {
        Map<String, Integer> ages = new HashMap<>();
        ages.put("John", 25);
        ages.put("Freddy", 24);
        ages.put("Samuel", 30);
        ages.forEach(( name, age) -> FunctionalInterfaceUnitTest.LOG.debug((((name + " is ") + age) + " years old")));
    }

    @Test
    public void whenUsingPredicateInFilter_thenListValuesAreFilteredOut() {
        List<String> names = Arrays.asList("Angela", "Aaron", "Bob", "Claire", "David");
        List<String> namesWithA = names.stream().filter(( name) -> name.startsWith("A")).collect(Collectors.toList());
        Assert.assertEquals(2, namesWithA.size());
        Assert.assertTrue(namesWithA.contains("Angela"));
        Assert.assertTrue(namesWithA.contains("Aaron"));
    }

    @Test
    public void whenUsingUnaryOperatorWithReplaceAll_thenAllValuesInTheListAreReplaced() {
        List<String> names = Arrays.asList("bob", "josh", "megan");
        names.replaceAll(String::toUpperCase);
        Assert.assertEquals("BOB", names.get(0));
        Assert.assertEquals("JOSH", names.get(1));
        Assert.assertEquals("MEGAN", names.get(2));
    }

    @Test
    public void whenUsingBinaryOperatorWithStreamReduce_thenResultIsSumOfValues() {
        List<Integer> values = Arrays.asList(3, 5, 8, 9, 12);
        int sum = values.stream().reduce(0, ( i1, i2) -> i1 + i2);
        Assert.assertEquals(37, sum);
    }

    @Test
    public void whenComposingTwoFunctions_thenFunctionsExecuteSequentially() {
        Function<Integer, String> intToString = Object::toString;
        Function<String, String> quote = ( s) -> ("'" + s) + "'";
        Function<Integer, String> quoteIntToString = quote.compose(intToString);
        Assert.assertEquals("'5'", quoteIntToString.apply(5));
    }

    @Test
    public void whenUsingSupplierToGenerateValue_thenValueIsGeneratedLazily() {
        Supplier<Double> lazyValue = () -> {
            Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
            return 9.0;
        };
        double valueSquared = squareLazy(lazyValue);
        Assert.assertEquals(81.0, valueSquared, 0);
    }
}

