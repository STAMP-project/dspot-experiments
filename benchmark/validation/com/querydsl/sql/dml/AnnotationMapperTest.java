package com.querydsl.sql.dml;


import AnnotationMapper.DEFAULT;
import com.querydsl.core.types.Path;
import com.querydsl.sql.domain.QEmployee;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AnnotationMapperTest extends AbstractMapperTest {
    private static final QEmployee emp = QEmployee.employee;

    @Test
    public void extract_success() {
        AbstractMapperTest.EmployeeNames names = new AbstractMapperTest.EmployeeNames();
        names._id = 9;
        names._firstname = "A";
        names._lastname = "B";
        Map<Path<?>, Object> values = DEFAULT.createMap(AnnotationMapperTest.emp, names);
        Assert.assertEquals(3, values.size());
        Assert.assertEquals(names._id, values.get(AnnotationMapperTest.emp.id));
        Assert.assertEquals(names._firstname, values.get(AnnotationMapperTest.emp.firstname));
        Assert.assertEquals(names._lastname, values.get(AnnotationMapperTest.emp.lastname));
    }

    @Test
    public void extract_failure() {
        Map<Path<?>, Object> values = DEFAULT.createMap(AnnotationMapperTest.emp, employee);
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void extract2() {
        Map<Path<?>, Object> values = DEFAULT.createMap(AnnotationMapperTest.emp, new AbstractMapperTest.EmployeeX());
        Assert.assertTrue(values.isEmpty());
    }
}

