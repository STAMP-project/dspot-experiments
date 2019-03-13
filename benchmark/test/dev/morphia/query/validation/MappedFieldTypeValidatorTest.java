package dev.morphia.query.validation;


import dev.morphia.entities.EntityWithListsAndArrays;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MappedFieldTypeValidatorTest {
    @Test
    public void shouldAllowAListThatDoesNotContainNumbers() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("listOfIntegers");
        // expect
        Assert.assertThat(MappedFieldTypeValidator.isIterableOfNumbers(mappedField), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowArraysOfNumbers() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayOfInts");
        // expect
        Assert.assertThat(MappedFieldTypeValidator.isArrayOfNumbers(mappedField), CoreMatchers.is(true));
    }

    @Test
    public void shouldRejectAListThatDoesNotContainNumbers() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("listOfStrings");
        // expect
        Assert.assertThat(MappedFieldTypeValidator.isIterableOfNumbers(mappedField), CoreMatchers.is(false));
    }

    @Test
    public void shouldRejectArraysOfStrings() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayOfStrings");
        // expect
        Assert.assertThat(MappedFieldTypeValidator.isArrayOfNumbers(mappedField), CoreMatchers.is(false));
    }
}

