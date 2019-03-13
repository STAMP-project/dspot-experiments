package com.fishercoder;


import _690.Solution1;
import com.fishercoder.common.classes.Employee;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Created by fishercoder on 5/18/17.
 */
public class _690Test {
    private static Solution1 solution1;

    private static List<Employee> employees;

    private static int id;

    @Test
    public void test1() {
        _690Test.employees = new ArrayList(Arrays.asList(new Employee(1, 5, Arrays.asList(2, 3)), new Employee(2, 3, Arrays.asList()), new Employee(3, 3, Arrays.asList())));
        _690Test.id = 1;
        TestCase.assertEquals(11, _690Test.solution1.getImportance(_690Test.employees, _690Test.id));
    }

    @Test
    public void test2() {
        _690Test.employees = new ArrayList(Arrays.asList(new Employee(1, 5, Arrays.asList(2, 3)), new Employee(2, 3, Arrays.asList(4)), new Employee(3, 4, Arrays.asList()), new Employee(4, 1, Arrays.asList())));
        _690Test.id = 1;
        TestCase.assertEquals(13, _690Test.solution1.getImportance(_690Test.employees, _690Test.id));
    }
}

