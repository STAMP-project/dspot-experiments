package com.baeldung.findItems;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class FindItemsBasedOnOtherStreamUnitTest {
    private List<Employee> employeeList = new ArrayList<Employee>();

    private List<Department> departmentList = new ArrayList<Department>();

    @Test
    public void givenDepartmentList_thenEmployeeListIsFilteredCorrectly() {
        Integer expectedId = 1002;
        populate(employeeList, departmentList);
        List<Employee> filteredList = employeeList.stream().filter(( empl) -> departmentList.stream().anyMatch(( dept) -> (dept.getDepartment().equals("sales")) && (empl.getEmployeeId().equals(dept.getEmployeeId())))).collect(Collectors.toList());
        Assert.assertEquals(expectedId, filteredList.get(0).getEmployeeId());
    }
}

