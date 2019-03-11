package com.querydsl.sql.dml;


import DefaultMapper.DEFAULT;
import com.querydsl.core.types.Path;
import com.querydsl.sql.domain.QEmployee;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DefaultMapperTest extends AbstractMapperTest {
    private static final QEmployee emp = QEmployee.employee;

    @Test
    public void extract() {
        Map<Path<?>, Object> values = DEFAULT.createMap(DefaultMapperTest.emp, employee);
        Assert.assertEquals(employee.getDatefield(), values.get(DefaultMapperTest.emp.datefield));
        Assert.assertEquals(employee.getFirstname(), values.get(DefaultMapperTest.emp.firstname));
        Assert.assertEquals(employee.getLastname(), values.get(DefaultMapperTest.emp.lastname));
        Assert.assertEquals(employee.getSalary(), values.get(DefaultMapperTest.emp.salary));
        Assert.assertEquals(employee.getSuperiorId(), values.get(DefaultMapperTest.emp.superiorId));
        Assert.assertEquals(employee.getTimefield(), values.get(DefaultMapperTest.emp.timefield));
    }

    @Test
    public void extract2() {
        Map<Path<?>, Object> values = DEFAULT.createMap(DefaultMapperTest.emp, new AbstractMapperTest.EmployeeX());
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void preservedColumnOrder() {
        final Map<String, Path<?>> columns = DEFAULT.getColumns(DefaultMapperTest.emp);
        final List<String> expectedKeySet = Arrays.asList("id", "firstname", "lastname", "salary", "datefield", "timefield", "superiorId");
        Assert.assertEquals(expectedKeySet, new ArrayList<String>(columns.keySet()));
    }
}

