package com.baeldung.reflection;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class PersonAndEmployeeReflectionUnitTest {
    // Fields names
    private static final String LAST_NAME_FIELD = "lastName";

    private static final String FIRST_NAME_FIELD = "firstName";

    private static final String EMPLOYEE_ID_FIELD = "employeeId";

    private static final String MONTH_EMPLOYEE_REWARD_FIELD = "reward";

    @Test
    public void givenPersonClass_whenGetDeclaredFields_thenTwoFields() {
        // When
        Field[] allFields = Person.class.getDeclaredFields();
        // Then
        Assert.assertEquals(2, allFields.length);
        Assert.assertTrue(Arrays.stream(allFields).anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.LAST_NAME_FIELD)) && (field.getType().equals(String.class))));
        Assert.assertTrue(Arrays.stream(allFields).anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.FIRST_NAME_FIELD)) && (field.getType().equals(String.class))));
    }

    @Test
    public void givenEmployeeClass_whenGetDeclaredFields_thenOneField() {
        // When
        Field[] allFields = Employee.class.getDeclaredFields();
        // Then
        Assert.assertEquals(1, allFields.length);
        Assert.assertTrue(Arrays.stream(allFields).anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.EMPLOYEE_ID_FIELD)) && (field.getType().equals(int.class))));
    }

    @Test
    public void givenEmployeeClass_whenSuperClassGetDeclaredFields_thenOneField() {
        // When
        Field[] allFields = Employee.class.getSuperclass().getDeclaredFields();
        // Then
        Assert.assertEquals(2, allFields.length);
        Assert.assertTrue(Arrays.stream(allFields).anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.LAST_NAME_FIELD)) && (field.getType().equals(String.class))));
        Assert.assertTrue(Arrays.stream(allFields).anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.FIRST_NAME_FIELD)) && (field.getType().equals(String.class))));
    }

    @Test
    public void givenEmployeeClass_whenGetDeclaredFieldsOnBothClasses_thenThreeFields() {
        // When
        Field[] personFields = Employee.class.getSuperclass().getDeclaredFields();
        Field[] employeeFields = Employee.class.getDeclaredFields();
        Field[] allFields = new Field[(employeeFields.length) + (personFields.length)];
        Arrays.setAll(allFields, ( i) -> i < (personFields.length) ? personFields[i] : employeeFields[(i - (personFields.length))]);
        // Then
        Assert.assertEquals(3, allFields.length);
        Assert.assertTrue(Arrays.stream(allFields).anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.LAST_NAME_FIELD)) && (field.getType().equals(String.class))));
        Assert.assertTrue(Arrays.stream(allFields).anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.FIRST_NAME_FIELD)) && (field.getType().equals(String.class))));
        Assert.assertTrue(Arrays.stream(allFields).anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.EMPLOYEE_ID_FIELD)) && (field.getType().equals(int.class))));
    }

    @Test
    public void givenEmployeeClass_whenGetDeclaredFieldsOnEmployeeSuperclassWithModifiersFilter_thenOneFields() {
        // When
        List<Field> personFields = Arrays.stream(Employee.class.getSuperclass().getDeclaredFields()).filter(( f) -> (Modifier.isPublic(f.getModifiers())) || (Modifier.isProtected(f.getModifiers()))).collect(Collectors.toList());
        // Then
        Assert.assertEquals(1, personFields.size());
        Assert.assertTrue(personFields.stream().anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.LAST_NAME_FIELD)) && (field.getType().equals(String.class))));
    }

    @Test
    public void givenMonthEmployeeClass_whenGetAllFields_thenThreeFields() {
        // When
        List<Field> allFields = getAllFields(MonthEmployee.class);
        // Then
        Assert.assertEquals(3, allFields.size());
        Assert.assertTrue(allFields.stream().anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.LAST_NAME_FIELD)) && (field.getType().equals(String.class))));
        Assert.assertTrue(allFields.stream().anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.EMPLOYEE_ID_FIELD)) && (field.getType().equals(int.class))));
        Assert.assertTrue(allFields.stream().anyMatch(( field) -> (field.getName().equals(PersonAndEmployeeReflectionUnitTest.MONTH_EMPLOYEE_REWARD_FIELD)) && (field.getType().equals(double.class))));
    }
}

