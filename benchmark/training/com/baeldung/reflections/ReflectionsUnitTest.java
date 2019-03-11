package com.baeldung.reflections;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReflectionsUnitTest {
    @Test
    public void givenTypeThenGetAllSubTypes() {
        ReflectionsApp reflectionsApp = new ReflectionsApp();
        Assertions.assertFalse(reflectionsApp.getReflectionsSubTypes().isEmpty());
    }

    @Test
    public void givenTypeAndUsingBuilderThenGetAllSubTypes() {
        ReflectionsApp reflectionsApp = new ReflectionsApp();
        Assertions.assertFalse(reflectionsApp.getReflectionsSubTypesUsingBuilder().isEmpty());
    }

    @Test
    public void givenAnnotationThenGetAllAnnotatedMethods() {
        ReflectionsApp reflectionsApp = new ReflectionsApp();
        Assertions.assertFalse(reflectionsApp.getDateDeprecatedMethods().isEmpty());
    }

    @Test
    public void givenAnnotationThenGetAllAnnotatedConstructors() {
        ReflectionsApp reflectionsApp = new ReflectionsApp();
        Assertions.assertFalse(reflectionsApp.getDateDeprecatedConstructors().isEmpty());
    }

    @Test
    public void givenParamTypeThenGetAllMethods() {
        ReflectionsApp reflectionsApp = new ReflectionsApp();
        Assertions.assertFalse(reflectionsApp.getMethodsWithDateParam().isEmpty());
    }

    @Test
    public void givenReturnTypeThenGetAllMethods() {
        ReflectionsApp reflectionsApp = new ReflectionsApp();
        Assertions.assertFalse(reflectionsApp.getMethodsWithVoidReturn().isEmpty());
    }
}

