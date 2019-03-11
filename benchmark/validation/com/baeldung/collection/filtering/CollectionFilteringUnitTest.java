package com.baeldung.collection.filtering;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Various filtering examples.
 *
 * @author Rodolfo Felipe
 */
public class CollectionFilteringUnitTest {
    @Test
    public void givenEmployeeList_andNameFilterList_thenObtainFilteredEmployeeList_usingForEachLoop() {
        List<Employee> filteredList = new ArrayList<>();
        List<Employee> originalList = buildEmployeeList();
        List<String> nameFilter = employeeNameFilter();
        for (Employee employee : originalList) {
            for (String name : nameFilter) {
                if (employee.getName().equalsIgnoreCase(name)) {
                    filteredList.add(employee);
                }
            }
        }
        Assert.assertThat(filteredList.size(), Matchers.is(nameFilter.size()));
    }

    @Test
    public void givenEmployeeList_andNameFilterList_thenObtainFilteredEmployeeList_usingLambda() {
        List<Employee> filteredList;
        List<Employee> originalList = buildEmployeeList();
        List<String> nameFilter = employeeNameFilter();
        filteredList = originalList.stream().filter(( employee) -> nameFilter.contains(employee.getName())).collect(Collectors.toList());
        Assert.assertThat(filteredList.size(), Matchers.is(nameFilter.size()));
    }

    @Test
    public void givenEmployeeList_andNameFilterList_thenObtainFilteredEmployeeList_usingLambdaAndHashSet() {
        List<Employee> filteredList;
        List<Employee> originalList = buildEmployeeList();
        Set<String> nameFilterSet = employeeNameFilter().stream().collect(Collectors.toSet());
        filteredList = originalList.stream().filter(( employee) -> nameFilterSet.contains(employee.getName())).collect(Collectors.toList());
        Assert.assertThat(filteredList.size(), Matchers.is(nameFilterSet.size()));
    }
}

