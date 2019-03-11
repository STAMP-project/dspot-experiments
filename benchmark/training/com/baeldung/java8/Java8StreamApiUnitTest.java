package com.baeldung.java8;


import com.baeldung.stream.Product;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Java8StreamApiUnitTest {
    private long counter;

    private static Logger log = LoggerFactory.getLogger(Java8StreamApiUnitTest.class);

    private List<Product> productList;

    @Test
    public void checkPipeline_whenStreamOneElementShorter_thenCorrect() {
        List<String> list = Arrays.asList("abc1", "abc2", "abc3");
        long size = list.stream().skip(1).map(( element) -> element.substring(0, 3)).count();
        Assert.assertEquals(((list.size()) - 1), size);
    }

    @Test
    public void checkOrder_whenChangeQuantityOfMethodCalls_thenCorrect() {
        List<String> list = Arrays.asList("abc1", "abc2", "abc3");
        counter = 0;
        long sizeFirst = list.stream().skip(2).map(( element) -> {
            wasCalled();
            return element.substring(0, 3);
        }).count();
        Assert.assertEquals(1, counter);
        counter = 0;
        long sizeSecond = list.stream().map(( element) -> {
            wasCalled();
            return element.substring(0, 3);
        }).skip(2).count();
        Assert.assertEquals(3, counter);
    }

    @Test
    public void createEmptyStream_whenEmpty_thenCorrect() {
        Stream<String> streamEmpty = Stream.empty();
        Assert.assertEquals(0, streamEmpty.count());
        List<String> names = Collections.emptyList();
        Stream<String> streamOf = Product.streamOf(names);
        Assert.assertTrue(((streamOf.count()) == 0));
    }

    @Test
    public void createStream_whenCreated_thenCorrect() {
        Collection<String> collection = Arrays.asList("a", "b", "c");
        Stream<String> streamOfCollection = collection.stream();
        Assert.assertEquals(3, streamOfCollection.count());
        Stream<String> streamOfArray = Stream.of("a", "b", "c");
        Assert.assertEquals(3, streamOfArray.count());
        String[] arr = new String[]{ "a", "b", "c" };
        Stream<String> streamOfArrayPart = Arrays.stream(arr, 1, 3);
        Assert.assertEquals(2, streamOfArrayPart.count());
        IntStream intStream = IntStream.range(1, 3);
        LongStream longStream = LongStream.rangeClosed(1, 3);
        Random random = new Random();
        DoubleStream doubleStream = random.doubles(3);
        Assert.assertEquals(2, intStream.count());
        Assert.assertEquals(3, longStream.count());
        Assert.assertEquals(3, doubleStream.count());
        IntStream streamOfChars = "abc".chars();
        IntStream str = "".chars();
        Assert.assertEquals(3, streamOfChars.count());
        Stream<String> streamOfString = Pattern.compile(", ").splitAsStream("a, b, c");
        Assert.assertEquals("a", streamOfString.findFirst().get());
        Path path = getPath();
        Stream<String> streamOfStrings = null;
        try {
            streamOfStrings = Files.lines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            Java8StreamApiUnitTest.log.error("Error creating streams from paths {}", path, e.getMessage(), e);
        }
        Assert.assertEquals("a", streamOfStrings.findFirst().get());
        Stream<String> streamBuilder = Stream.<String>builder().add("a").add("b").add("c").build();
        Assert.assertEquals(3, streamBuilder.count());
        Stream<String> streamGenerated = Stream.generate(() -> "element").limit(10);
        Assert.assertEquals(10, streamGenerated.count());
        Stream<Integer> streamIterated = Stream.iterate(40, ( n) -> n + 2).limit(20);
        Assert.assertTrue((40 <= (streamIterated.findAny().get())));
    }

    @Test
    public void runStreamPipeline_whenOrderIsRight_thenCorrect() {
        List<String> list = Arrays.asList("abc1", "abc2", "abc3");
        Optional<String> stream = list.stream().filter(( element) -> {
            Java8StreamApiUnitTest.log.info("filter() was called");
            return element.contains("2");
        }).map(( element) -> {
            Java8StreamApiUnitTest.log.info("map() was called");
            return element.toUpperCase();
        }).findFirst();
    }

    @Test
    public void reduce_whenExpected_thenCorrect() {
        OptionalInt reduced = IntStream.range(1, 4).reduce(( a, b) -> a + b);
        Assert.assertEquals(6, reduced.getAsInt());
        int reducedTwoParams = IntStream.range(1, 4).reduce(10, ( a, b) -> a + b);
        Assert.assertEquals(16, reducedTwoParams);
        int reducedThreeParams = Stream.of(1, 2, 3).reduce(10, ( a, b) -> a + b, ( a, b) -> {
            Java8StreamApiUnitTest.log.info("combiner was called");
            return a + b;
        });
        Assert.assertEquals(16, reducedThreeParams);
        int reducedThreeParamsParallel = Arrays.asList(1, 2, 3).parallelStream().reduce(10, ( a, b) -> a + b, ( a, b) -> {
            Java8StreamApiUnitTest.log.info("combiner was called");
            return a + b;
        });
        Assert.assertEquals(36, reducedThreeParamsParallel);
    }

    @Test
    public void collecting_whenAsExpected_thenCorrect() {
        List<String> collectorCollection = productList.stream().map(Product::getName).collect(Collectors.toList());
        Assert.assertTrue((collectorCollection instanceof List));
        Assert.assertEquals(5, collectorCollection.size());
        String listToString = productList.stream().map(Product::getName).collect(Collectors.joining(", ", "[", "]"));
        Assert.assertTrue((((listToString.contains(",")) && (listToString.contains("["))) && (listToString.contains("]"))));
        double averagePrice = productList.stream().collect(Collectors.averagingInt(Product::getPrice));
        Assert.assertTrue((17.2 == averagePrice));
        int summingPrice = productList.stream().collect(Collectors.summingInt(Product::getPrice));
        Assert.assertEquals(86, summingPrice);
        IntSummaryStatistics statistics = productList.stream().collect(Collectors.summarizingInt(Product::getPrice));
        Assert.assertEquals(23, statistics.getMax());
        Map<Integer, List<Product>> collectorMapOfLists = productList.stream().collect(Collectors.groupingBy(Product::getPrice));
        Assert.assertEquals(3, collectorMapOfLists.keySet().size());
        Map<Boolean, List<Product>> mapPartioned = productList.stream().collect(Collectors.partitioningBy(( element) -> (element.getPrice()) > 15));
        Assert.assertEquals(2, mapPartioned.keySet().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void collect_whenThrows_thenCorrect() {
        Set<Product> unmodifiableSet = productList.stream().collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
        unmodifiableSet.add(new Product(4, "tea"));
    }

    @Test
    public void customCollector_whenResultContainsAllElementsFrSource_thenCorrect() {
        Collector<Product, ?, LinkedList<Product>> toLinkedList = Collector.of(LinkedList::new, LinkedList::add, ( first, second) -> {
            addAll(second);
            return first;
        });
        LinkedList<Product> linkedListOfPersons = productList.stream().collect(toLinkedList);
        Assert.assertTrue(linkedListOfPersons.containsAll(productList));
    }

    @Test
    public void parallelStream_whenWorks_thenCorrect() {
        Stream<Product> streamOfCollection = productList.parallelStream();
        boolean isParallel = streamOfCollection.isParallel();
        boolean haveBigPrice = streamOfCollection.map(( product) -> (product.getPrice()) * 12).anyMatch(( price) -> price > 200);
        Assert.assertTrue((isParallel && haveBigPrice));
    }

    @Test
    public void parallel_whenIsParallel_thenCorrect() {
        IntStream intStreamParallel = IntStream.range(1, 150).parallel().map(( element) -> element * 34);
        boolean isParallel = intStreamParallel.isParallel();
        Assert.assertTrue(isParallel);
    }

    @Test
    public void parallel_whenIsSequential_thenCorrect() {
        IntStream intStreamParallel = IntStream.range(1, 150).parallel().map(( element) -> element * 34);
        IntStream intStreamSequential = intStreamParallel.sequential();
        boolean isParallel = intStreamParallel.isParallel();
        Assert.assertFalse(isParallel);
    }
}

