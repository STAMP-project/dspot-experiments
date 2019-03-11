package com.baeldung.java8.comparator;


import java.util.Arrays;
import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;


public class Java8ComparatorUnitTest {
    private Employee[] employees;

    private Employee[] employeesArrayWithNulls;

    private Employee[] sortedEmployeesByName;

    private Employee[] sortedEmployeesByNameDesc;

    private Employee[] sortedEmployeesByAge;

    private Employee[] sortedEmployeesByMobile;

    private Employee[] sortedEmployeesBySalary;

    private Employee[] sortedEmployeesArray_WithNullsFirst;

    private Employee[] sortedEmployeesArray_WithNullsLast;

    private Employee[] sortedEmployeesByNameAge;

    private Employee[] someMoreEmployees;

    private Employee[] sortedEmployeesByAgeName;

    @Test
    public void whenComparing_thenSortedByName() {
        Comparator<Employee> employeeNameComparator = Comparator.comparing(Employee::getName);
        Arrays.sort(employees, employeeNameComparator);
        // System.out.println(Arrays.toString(employees));
        Assert.assertTrue(Arrays.equals(employees, sortedEmployeesByName));
    }

    @Test
    public void whenComparingWithComparator_thenSortedByNameDesc() {
        Comparator<Employee> employeeNameComparator = Comparator.comparing(Employee::getName, ( s1, s2) -> {
            return compareTo(s1);
        });
        Arrays.sort(employees, employeeNameComparator);
        // System.out.println(Arrays.toString(employees));
        Assert.assertTrue(Arrays.equals(employees, sortedEmployeesByNameDesc));
    }

    @Test
    public void whenReversed_thenSortedByNameDesc() {
        Comparator<Employee> employeeNameComparator = Comparator.comparing(Employee::getName);
        Comparator<Employee> employeeNameComparatorReversed = employeeNameComparator.reversed();
        Arrays.sort(employees, employeeNameComparatorReversed);
        // System.out.println(Arrays.toString(employees));
        Assert.assertTrue(Arrays.equals(employees, sortedEmployeesByNameDesc));
    }

    @Test
    public void whenComparingInt_thenSortedByAge() {
        Comparator<Employee> employeeAgeComparator = Comparator.comparingInt(Employee::getAge);
        Arrays.sort(employees, employeeAgeComparator);
        // System.out.println(Arrays.toString(employees));
        Assert.assertTrue(Arrays.equals(employees, sortedEmployeesByAge));
    }

    @Test
    public void whenComparingLong_thenSortedByMobile() {
        Comparator<Employee> employeeMobileComparator = Comparator.comparingLong(Employee::getMobile);
        Arrays.sort(employees, employeeMobileComparator);
        // System.out.println(Arrays.toString(employees));
        Assert.assertTrue(Arrays.equals(employees, sortedEmployeesByMobile));
    }

    @Test
    public void whenComparingDouble_thenSortedBySalary() {
        Comparator<Employee> employeeSalaryComparator = Comparator.comparingDouble(Employee::getSalary);
        Arrays.sort(employees, employeeSalaryComparator);
        // System.out.println(Arrays.toString(employees));
        Assert.assertTrue(Arrays.equals(employees, sortedEmployeesBySalary));
    }

    @Test
    public void whenNaturalOrder_thenSortedByName() {
        Comparator<Employee> employeeNameComparator = Comparator.<Employee>naturalOrder();
        Arrays.sort(employees, employeeNameComparator);
        // System.out.println(Arrays.toString(employees));
        Assert.assertTrue(Arrays.equals(employees, sortedEmployeesByName));
    }

    @Test
    public void whenReverseOrder_thenSortedByNameDesc() {
        Comparator<Employee> employeeNameComparator = Comparator.<Employee>reverseOrder();
        Arrays.sort(employees, employeeNameComparator);
        // System.out.println(Arrays.toString(employees));
        Assert.assertTrue(Arrays.equals(employees, sortedEmployeesByNameDesc));
    }

    @Test
    public void whenNullsFirst_thenSortedByNameWithNullsFirst() {
        Comparator<Employee> employeeNameComparator = Comparator.comparing(Employee::getName);
        Comparator<Employee> employeeNameComparator_nullFirst = Comparator.nullsFirst(employeeNameComparator);
        Arrays.sort(employeesArrayWithNulls, employeeNameComparator_nullFirst);
        // System.out.println(Arrays.toString(employeesArrayWithNulls));
        Assert.assertTrue(Arrays.equals(employeesArrayWithNulls, sortedEmployeesArray_WithNullsFirst));
    }

    @Test
    public void whenNullsLast_thenSortedByNameWithNullsLast() {
        Comparator<Employee> employeeNameComparator = Comparator.comparing(Employee::getName);
        Comparator<Employee> employeeNameComparator_nullLast = Comparator.nullsLast(employeeNameComparator);
        Arrays.sort(employeesArrayWithNulls, employeeNameComparator_nullLast);
        // System.out.println(Arrays.toString(employeesArrayWithNulls));
        Assert.assertTrue(Arrays.equals(employeesArrayWithNulls, sortedEmployeesArray_WithNullsLast));
    }

    @Test
    public void whenThenComparing_thenSortedByAgeName() {
        Comparator<Employee> employee_Age_Name_Comparator = Comparator.comparing(Employee::getAge).thenComparing(Employee::getName);
        Arrays.sort(someMoreEmployees, employee_Age_Name_Comparator);
        // System.out.println(Arrays.toString(someMoreEmployees));
        Assert.assertTrue(Arrays.equals(someMoreEmployees, sortedEmployeesByAgeName));
    }

    @Test
    public void whenThenComparing_thenSortedByNameAge() {
        Comparator<Employee> employee_Name_Age_Comparator = Comparator.comparing(Employee::getName).thenComparingInt(Employee::getAge);
        Arrays.sort(someMoreEmployees, employee_Name_Age_Comparator);
        // System.out.println(Arrays.toString(someMoreEmployees));
        Assert.assertTrue(Arrays.equals(someMoreEmployees, sortedEmployeesByNameAge));
    }
}

