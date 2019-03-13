package com.querydsl.sql.dml;


import BeanMapper.DEFAULT;
import com.querydsl.core.types.Path;
import com.querydsl.sql.domain.QEmployee;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class BeanMapperTest extends AbstractMapperTest {
    private static final QEmployee emp = QEmployee.employee;

    @Test
    public void extract() {
        Map<Path<?>, Object> values = DEFAULT.createMap(BeanMapperTest.emp, employee);
        Assert.assertEquals(employee.getDatefield(), values.get(BeanMapperTest.emp.datefield));
        Assert.assertEquals(employee.getFirstname(), values.get(BeanMapperTest.emp.firstname));
        Assert.assertEquals(employee.getLastname(), values.get(BeanMapperTest.emp.lastname));
        Assert.assertEquals(employee.getSalary(), values.get(BeanMapperTest.emp.salary));
        Assert.assertEquals(employee.getSuperiorId(), values.get(BeanMapperTest.emp.superiorId));
        Assert.assertEquals(employee.getTimefield(), values.get(BeanMapperTest.emp.timefield));
    }

    @Test
    public void extract2() {
        Map<Path<?>, Object> values = DEFAULT.createMap(BeanMapperTest.emp, new AbstractMapperTest.EmployeeX());
        Assert.assertTrue(values.isEmpty());
    }
}

