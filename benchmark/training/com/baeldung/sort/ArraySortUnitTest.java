package com.baeldung.sort;


import com.baeldung.arraycopy.model.Employee;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public class ArraySortUnitTest {
    private Employee[] employees;

    private int[] numbers;

    private String[] strings;

    private Employee john = new Employee(6, "John");

    private Employee mary = new Employee(3, "Mary");

    private Employee david = new Employee(4, "David");

    @Test
    public void givenIntArray_whenSortingAscending_thenCorrectlySorted() {
        Arrays.sort(numbers);
        Assert.assertArrayEquals(new int[]{ -8, -2, 3, 5, 7, 9, 10 }, numbers);
    }

    @Test
    public void givenIntArray_whenSortingDescending_thenCorrectlySorted() {
        numbers = IntStream.of(numbers).boxed().sorted(Comparator.reverseOrder()).mapToInt(( i) -> i).toArray();
        Assert.assertArrayEquals(new int[]{ 10, 9, 7, 5, 3, -2, -8 }, numbers);
    }

    @Test
    public void givenStringArray_whenSortingAscending_thenCorrectlySorted() {
        Arrays.sort(strings);
        Assert.assertArrayEquals(new String[]{ "baeldung", "java", "learning", "with" }, strings);
    }

    @Test
    public void givenStringArray_whenSortingDescending_thenCorrectlySorted() {
        Arrays.sort(strings, Comparator.reverseOrder());
        Assert.assertArrayEquals(new String[]{ "with", "learning", "java", "baeldung" }, strings);
    }

    @Test
    public void givenObjectArray_whenSortingAscending_thenCorrectlySorted() {
        Arrays.sort(employees, Comparator.comparing(Employee::getName));
        Assert.assertArrayEquals(new Employee[]{ david, john, mary }, employees);
    }

    @Test
    public void givenObjectArray_whenSortingDescending_thenCorrectlySorted() {
        Arrays.sort(employees, Comparator.comparing(Employee::getName).reversed());
        Assert.assertArrayEquals(new Employee[]{ mary, john, david }, employees);
    }

    @Test
    public void givenObjectArray_whenSortingMultipleAttributesAscending_thenCorrectlySorted() {
        Arrays.sort(employees, Comparator.comparing(Employee::getName).thenComparing(Employee::getId));
        Assert.assertArrayEquals(new Employee[]{ david, john, mary }, employees);
    }
}

