package com.stackify.stream;


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EmployeeTest {
    private String fileName = "src/test/resources/test.txt";

    private static Employee[] arrayOfEmps = new Employee[]{ new Employee(1, "Jeff Bezos", 100000.0), new Employee(2, "Bill Gates", 200000.0), new Employee(3, "Mark Zuckerberg", 300000.0) };

    private static List<Employee> empList = Arrays.asList(EmployeeTest.arrayOfEmps);

    private static EmployeeRepository employeeRepository = new EmployeeRepository(EmployeeTest.empList);

    @Test
    public void whenGetStreamFromList_ObtainStream() {
        assert (EmployeeTest.empList.stream()) instanceof Stream<?>;
    }

    @Test
    public void whenGetStreamFromArray_ObtainStream() {
        assert (Stream.of(EmployeeTest.arrayOfEmps)) instanceof Stream<?>;
    }

    @Test
    public void whenGetStreamFromElements_ObtainStream() {
        assert (Stream.of(EmployeeTest.arrayOfEmps[0], EmployeeTest.arrayOfEmps[1], EmployeeTest.arrayOfEmps[2])) instanceof Stream<?>;
    }

    @Test
    public void whenBuildStreamFromElements_ObtainStream() {
        Stream.Builder<Employee> empStreamBuilder = Stream.builder();
        empStreamBuilder.accept(EmployeeTest.arrayOfEmps[0]);
        empStreamBuilder.accept(EmployeeTest.arrayOfEmps[1]);
        empStreamBuilder.accept(EmployeeTest.arrayOfEmps[2]);
        Stream<Employee> empStream = empStreamBuilder.build();
        assert empStream instanceof Stream<?>;
    }

    @Test
    public void whenIncrementSalaryForEachEmployee_thenApplyNewSalary() {
        Employee[] arrayOfEmps = new Employee[]{ new Employee(1, "Jeff Bezos", 100000.0), new Employee(2, "Bill Gates", 200000.0), new Employee(3, "Mark Zuckerberg", 300000.0) };
        List<Employee> empList = Arrays.asList(arrayOfEmps);
        empList.stream().forEach(( e) -> e.salaryIncrement(10.0));
        MatcherAssert.assertThat(empList, Matchers.contains(hasProperty("salary", CoreMatchers.equalTo(110000.0)), hasProperty("salary", CoreMatchers.equalTo(220000.0)), hasProperty("salary", CoreMatchers.equalTo(330000.0))));
    }

    @Test
    public void whenIncrementSalaryUsingPeek_thenApplyNewSalary() {
        Employee[] arrayOfEmps = new Employee[]{ new Employee(1, "Jeff Bezos", 100000.0), new Employee(2, "Bill Gates", 200000.0), new Employee(3, "Mark Zuckerberg", 300000.0) };
        List<Employee> empList = Arrays.asList(arrayOfEmps);
        empList.stream().peek(( e) -> e.salaryIncrement(10.0)).peek(System.out::println).collect(Collectors.toList());
        MatcherAssert.assertThat(empList, Matchers.contains(hasProperty("salary", CoreMatchers.equalTo(110000.0)), hasProperty("salary", CoreMatchers.equalTo(220000.0)), hasProperty("salary", CoreMatchers.equalTo(330000.0))));
    }

    @Test
    public void whenMapIdToEmployees_thenGetEmployeeStream() {
        Integer[] empIds = new Integer[]{ 1, 2, 3 };
        List<Employee> employees = Stream.of(empIds).map(EmployeeTest.employeeRepository::findById).collect(Collectors.toList());
        Assert.assertEquals(employees.size(), empIds.length);
    }

    @Test
    public void whenFlatMapEmployeeNames_thenGetNameStream() {
        List<List<String>> namesNested = Arrays.asList(Arrays.asList("Jeff", "Bezos"), Arrays.asList("Bill", "Gates"), Arrays.asList("Mark", "Zuckerberg"));
        List<String> namesFlatStream = namesNested.stream().flatMap(Collection::stream).collect(Collectors.toList());
        Assert.assertEquals(namesFlatStream.size(), ((namesNested.size()) * 2));
    }

    @Test
    public void whenFilterEmployees_thenGetFilteredStream() {
        Integer[] empIds = new Integer[]{ 1, 2, 3, 4 };
        List<Employee> employees = Stream.of(empIds).map(EmployeeTest.employeeRepository::findById).filter(( e) -> e != null).filter(( e) -> (e.getSalary()) > 200000).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList(EmployeeTest.arrayOfEmps[2]), employees);
    }

    @Test
    public void whenFindFirst_thenGetFirstEmployeeInStream() {
        Integer[] empIds = new Integer[]{ 1, 2, 3, 4 };
        Employee employee = Stream.of(empIds).map(EmployeeTest.employeeRepository::findById).filter(( e) -> e != null).filter(( e) -> (e.getSalary()) > 100000).findFirst().orElse(null);
        Assert.assertEquals(employee.getSalary(), new Double(200000));
    }

    @Test
    public void whenCollectStreamToList_thenGetList() {
        List<Employee> employees = EmployeeTest.empList.stream().collect(Collectors.toList());
        Assert.assertEquals(EmployeeTest.empList, employees);
    }

    @Test
    public void whenStreamToArray_thenGetArray() {
        Employee[] employees = EmployeeTest.empList.stream().toArray(Employee[]::new);
        MatcherAssert.assertThat(EmployeeTest.empList.toArray(), CoreMatchers.equalTo(employees));
    }

    @Test
    public void whenStreamCount_thenGetElementCount() {
        Long empCount = EmployeeTest.empList.stream().filter(( e) -> (e.getSalary()) > 200000).count();
        Assert.assertEquals(empCount, new Long(1));
    }

    @Test
    public void whenLimitInfiniteStream_thenGetFiniteElements() {
        Stream<Integer> infiniteStream = Stream.iterate(2, ( i) -> i * 2);
        List<Integer> collect = infiniteStream.skip(3).limit(5).collect(Collectors.toList());
        Assert.assertEquals(collect, Arrays.asList(16, 32, 64, 128, 256));
    }

    @Test
    public void whenSortStream_thenGetSortedStream() {
        List<Employee> employees = EmployeeTest.empList.stream().sorted(( e1, e2) -> e1.getName().compareTo(e2.getName())).collect(Collectors.toList());
        Assert.assertEquals(employees.get(0).getName(), "Bill Gates");
        Assert.assertEquals(employees.get(1).getName(), "Jeff Bezos");
        Assert.assertEquals(employees.get(2).getName(), "Mark Zuckerberg");
    }

    @Test
    public void whenFindMin_thenGetMinElementFromStream() {
        Employee firstEmp = EmployeeTest.empList.stream().min(( e1, e2) -> (e1.getId()) - (e2.getId())).orElseThrow(NoSuchElementException::new);
        Assert.assertEquals(firstEmp.getId(), new Integer(1));
    }

    @Test
    public void whenFindMax_thenGetMaxElementFromStream() {
        Employee maxSalEmp = EmployeeTest.empList.stream().max(Comparator.comparing(Employee::getSalary)).orElseThrow(NoSuchElementException::new);
        Assert.assertEquals(maxSalEmp.getSalary(), new Double(300000.0));
    }

    @Test
    public void whenApplyDistinct_thenRemoveDuplicatesFromStream() {
        List<Integer> intList = Arrays.asList(2, 5, 3, 2, 4, 3);
        List<Integer> distinctIntList = intList.stream().distinct().collect(Collectors.toList());
        Assert.assertEquals(distinctIntList, Arrays.asList(2, 5, 3, 4));
    }

    @Test
    public void whenApplyMatch_thenReturnBoolean() {
        List<Integer> intList = Arrays.asList(2, 4, 5, 6, 8);
        boolean allEven = intList.stream().allMatch(( i) -> (i % 2) == 0);
        boolean oneEven = intList.stream().anyMatch(( i) -> (i % 2) == 0);
        boolean noneMultipleOfThree = intList.stream().noneMatch(( i) -> (i % 3) == 0);
        Assert.assertEquals(allEven, false);
        Assert.assertEquals(oneEven, true);
        Assert.assertEquals(noneMultipleOfThree, false);
    }

    @Test
    public void whenFindMaxOnIntStream_thenGetMaxInteger() {
        Integer latestEmpId = EmployeeTest.empList.stream().mapToInt(Employee::getId).max().orElseThrow(NoSuchElementException::new);
        Assert.assertEquals(latestEmpId, new Integer(3));
    }

    @Test
    public void whenApplySumOnIntStream_thenGetSum() {
        Double avgSal = EmployeeTest.empList.stream().mapToDouble(Employee::getSalary).average().orElseThrow(NoSuchElementException::new);
        Assert.assertEquals(avgSal, new Double(200000));
    }

    @Test
    public void whenApplyReduceOnStream_thenGetValue() {
        Double sumSal = EmployeeTest.empList.stream().map(Employee::getSalary).reduce(0.0, Double::sum);
        Assert.assertEquals(sumSal, new Double(600000));
    }

    @Test
    public void whenCollectByJoining_thenGetJoinedString() {
        String empNames = EmployeeTest.empList.stream().map(Employee::getName).collect(Collectors.joining(", ")).toString();
        Assert.assertEquals(empNames, "Jeff Bezos, Bill Gates, Mark Zuckerberg");
    }

    @Test
    public void whenCollectBySet_thenGetSet() {
        Set<String> empNames = EmployeeTest.empList.stream().map(Employee::getName).collect(Collectors.toSet());
        Assert.assertEquals(empNames.size(), 3);
    }

    @Test
    public void whenToVectorCollection_thenGetVector() {
        Vector<String> empNames = EmployeeTest.empList.stream().map(Employee::getName).collect(Collectors.toCollection(Vector::new));
        Assert.assertEquals(empNames.size(), 3);
    }

    @Test
    public void whenApplySummarizing_thenGetBasicStats() {
        DoubleSummaryStatistics stats = EmployeeTest.empList.stream().collect(Collectors.summarizingDouble(Employee::getSalary));
        Assert.assertEquals(stats.getCount(), 3);
        Assert.assertEquals(stats.getSum(), 600000.0, 0);
        Assert.assertEquals(stats.getMin(), 100000.0, 0);
        Assert.assertEquals(stats.getMax(), 300000.0, 0);
        Assert.assertEquals(stats.getAverage(), 200000.0, 0);
    }

    @Test
    public void whenApplySummaryStatistics_thenGetBasicStats() {
        DoubleSummaryStatistics stats = EmployeeTest.empList.stream().mapToDouble(Employee::getSalary).summaryStatistics();
        Assert.assertEquals(stats.getCount(), 3);
        Assert.assertEquals(stats.getSum(), 600000.0, 0);
        Assert.assertEquals(stats.getMin(), 100000.0, 0);
        Assert.assertEquals(stats.getMax(), 300000.0, 0);
        Assert.assertEquals(stats.getAverage(), 200000.0, 0);
    }

    @Test
    public void whenStreamPartition_thenGetMap() {
        List<Integer> intList = Arrays.asList(2, 4, 5, 6, 8);
        Map<Boolean, List<Integer>> isEven = intList.stream().collect(Collectors.partitioningBy(( i) -> (i % 2) == 0));
        Assert.assertEquals(isEven.get(true).size(), 4);
        Assert.assertEquals(isEven.get(false).size(), 1);
    }

    @Test
    public void whenStreamGroupingBy_thenGetMap() {
        Map<Character, List<Employee>> groupByAlphabet = EmployeeTest.empList.stream().collect(Collectors.groupingBy(( e) -> new Character(e.getName().charAt(0))));
        Assert.assertEquals(groupByAlphabet.get('B').get(0).getName(), "Bill Gates");
        Assert.assertEquals(groupByAlphabet.get('J').get(0).getName(), "Jeff Bezos");
        Assert.assertEquals(groupByAlphabet.get('M').get(0).getName(), "Mark Zuckerberg");
    }

    @Test
    public void whenStreamMapping_thenGetMap() {
        Map<Character, List<Integer>> idGroupedByAlphabet = EmployeeTest.empList.stream().collect(Collectors.groupingBy(( e) -> new Character(e.getName().charAt(0)), Collectors.mapping(Employee::getId, Collectors.toList())));
        Assert.assertEquals(idGroupedByAlphabet.get('B').get(0), new Integer(2));
        Assert.assertEquals(idGroupedByAlphabet.get('J').get(0), new Integer(1));
        Assert.assertEquals(idGroupedByAlphabet.get('M').get(0), new Integer(3));
    }

    @Test
    public void whenStreamReducing_thenGetValue() {
        Double percentage = 10.0;
        Double salIncrOverhead = EmployeeTest.empList.stream().collect(Collectors.reducing(0.0, ( e) -> ((e.getSalary()) * percentage) / 100, ( s1, s2) -> s1 + s2));
        Assert.assertEquals(salIncrOverhead, 60000.0, 0);
    }

    @Test
    public void whenStreamGroupingAndReducing_thenGetMap() {
        Comparator<Employee> byNameLength = Comparator.comparing(Employee::getName);
        Map<Character, Optional<Employee>> longestNameByAlphabet = EmployeeTest.empList.stream().collect(Collectors.groupingBy(( e) -> new Character(e.getName().charAt(0)), Collectors.reducing(BinaryOperator.maxBy(byNameLength))));
        Assert.assertEquals(longestNameByAlphabet.get('B').get().getName(), "Bill Gates");
        Assert.assertEquals(longestNameByAlphabet.get('J').get().getName(), "Jeff Bezos");
        Assert.assertEquals(longestNameByAlphabet.get('M').get().getName(), "Mark Zuckerberg");
    }

    @Test
    public void whenParallelStream_thenPerformOperationsInParallel() {
        Employee[] arrayOfEmps = new Employee[]{ new Employee(1, "Jeff Bezos", 100000.0), new Employee(2, "Bill Gates", 200000.0), new Employee(3, "Mark Zuckerberg", 300000.0) };
        List<Employee> empList = Arrays.asList(arrayOfEmps);
        empList.stream().parallel().forEach(( e) -> e.salaryIncrement(10.0));
        MatcherAssert.assertThat(empList, Matchers.contains(hasProperty("salary", CoreMatchers.equalTo(110000.0)), hasProperty("salary", CoreMatchers.equalTo(220000.0)), hasProperty("salary", CoreMatchers.equalTo(330000.0))));
    }

    @Test
    public void whenGenerateStream_thenGetInfiniteStream() {
        Stream.generate(Math::random).limit(5).forEach(System.out::println);
    }

    @Test
    public void whenIterateStream_thenGetInfiniteStream() {
        Stream<Integer> evenNumStream = Stream.iterate(2, ( i) -> i * 2);
        List<Integer> collect = evenNumStream.limit(5).collect(Collectors.toList());
        Assert.assertEquals(collect, Arrays.asList(2, 4, 8, 16, 32));
    }

    @Test
    public void whenStreamToFile_thenGetFile() throws IOException {
        String[] words = new String[]{ "hello", "refer", "world", "level" };
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(fileName)))) {
            Stream.of(words).forEach(pw::println);
        }
    }

    @Test
    public void whenFileToStream_thenGetStream() throws IOException {
        whenStreamToFile_thenGetFile();
        List<String> str = getPalindrome(Files.lines(Paths.get(fileName)), 5);
        MatcherAssert.assertThat(str, Matchers.contains("refer", "level"));
    }
}

