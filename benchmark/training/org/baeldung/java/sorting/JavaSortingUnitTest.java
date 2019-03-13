package org.baeldung.java.sorting;


import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;


public class JavaSortingUnitTest {
    private int[] toSort;

    private int[] sortedInts;

    private int[] sortedRangeInts;

    private Employee[] employees;

    private Employee[] employeesSorted;

    private Employee[] employeesSortedByAge;

    private HashMap<Integer, String> map;

    @Test
    public void givenIntArray_whenUsingSort_thenSortedArray() {
        Arrays.sort(toSort);
        Assert.assertTrue(Arrays.equals(toSort, sortedInts));
    }

    @Test
    public void givenIntegerArray_whenUsingSort_thenSortedArray() {
        Integer[] integers = ArrayUtils.toObject(toSort);
        Arrays.sort(integers, Comparator.comparingInt(( a) -> a));
        Assert.assertTrue(Arrays.equals(integers, ArrayUtils.toObject(sortedInts)));
    }

    @Test
    public void givenArray_whenUsingSortWithLambdas_thenSortedArray() {
        Integer[] integersToSort = ArrayUtils.toObject(toSort);
        Arrays.sort(integersToSort, Comparator.comparingInt(( a) -> a));
        Assert.assertTrue(Arrays.equals(integersToSort, ArrayUtils.toObject(sortedInts)));
    }

    @Test
    public void givenEmpArray_SortEmpArray_thenSortedArrayinNaturalOrder() {
        Arrays.sort(employees);
        Assert.assertTrue(Arrays.equals(employees, employeesSorted));
    }

    @Test
    public void givenIntArray_whenUsingRangeSort_thenRangeSortedArray() {
        Arrays.sort(toSort, 3, 7);
        Assert.assertTrue(Arrays.equals(toSort, sortedRangeInts));
    }

    @Test
    public void givenIntArray_whenUsingParallelSort_thenArraySorted() {
        Arrays.parallelSort(toSort);
        Assert.assertTrue(Arrays.equals(toSort, sortedInts));
    }

    @Test
    public void givenArrayObjects_whenUsingComparing_thenSortedArrayObjects() {
        List<Employee> employeesList = Arrays.asList(employees);
        employeesList.sort(Comparator.comparing(Employee::getAge));// .thenComparing(Employee::getName));

        Assert.assertTrue(Arrays.equals(employeesList.toArray(), employeesSortedByAge));
    }

    @Test
    public void givenList_whenUsingSort_thenSortedList() {
        List<Integer> toSortList = Ints.asList(toSort);
        Collections.sort(toSortList);
        Assert.assertTrue(Arrays.equals(toSortList.toArray(), ArrayUtils.toObject(sortedInts)));
    }

    @Test
    public void givenMap_whenSortingByKeys_thenSortedMap() {
        Integer[] sortedKeys = new Integer[]{ 6, 12, 22, 55, 66, 77 };
        List<Map.Entry<Integer, String>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));
        HashMap<Integer, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        Assert.assertTrue(Arrays.equals(sortedMap.keySet().toArray(), sortedKeys));
    }

    @Test
    public void givenMap_whenSortingByValues_thenSortedMap() {
        String[] sortedValues = new String[]{ "Apple", "Earl", "George", "John", "Pearl", "Rocky" };
        List<Map.Entry<Integer, String>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getValue));
        HashMap<Integer, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        Assert.assertTrue(Arrays.equals(sortedMap.values().toArray(), sortedValues));
    }

    @Test
    public void givenSet_whenUsingSort_thenSortedSet() {
        HashSet<Integer> integersSet = new LinkedHashSet<>(Ints.asList(toSort));
        HashSet<Integer> descSortedIntegersSet = new LinkedHashSet<>(Arrays.asList(255, 200, 123, 89, 88, 66, 7, 5, 1));
        ArrayList<Integer> list = new ArrayList<>(integersSet);
        list.sort(( i1, i2) -> i2 - i1);
        integersSet = new LinkedHashSet<>(list);
        Assert.assertTrue(Arrays.equals(integersSet.toArray(), descSortedIntegersSet.toArray()));
    }
}

