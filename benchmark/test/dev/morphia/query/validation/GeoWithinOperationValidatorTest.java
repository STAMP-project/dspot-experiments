package dev.morphia.query.validation;


import com.mongodb.BasicDBObject;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import dev.morphia.query.FilterOperator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GeoWithinOperationValidatorTest {
    @Test
    public void shouldAllowGeoWithinOperatorForGeoEntityWithListOfIntegers() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        MappedClass mappedClass = new MappedClass(GeoWithinOperationValidatorTest.GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("list");
        Assert.assertThat(GeoWithinOperationValidator.getInstance().apply(mappedField, FilterOperator.GEO_WITHIN, new BasicDBObject("$box", 1), validationFailures), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowGeoWithinOperatorWithAllAppropriateTrimmings() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        MappedClass mappedClass = new MappedClass(GeoWithinOperationValidatorTest.GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        // when
        Assert.assertThat(GeoWithinOperationValidator.getInstance().apply(mappedField, FilterOperator.GEO_WITHIN, new BasicDBObject("$box", 1), validationFailures), CoreMatchers.is(true));
    }

    @Test
    public void shouldNotApplyValidationWhenOperatorIsNotGeoWithin() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = GeoWithinOperationValidator.getInstance().apply(null, FilterOperator.EQUAL, null, validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(false));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(0));
    }

    @Test
    public void shouldRejectGeoWithinOperatorWhenMappedFieldIsArrayThatDoesNotContainNumbers() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        MappedClass mappedClass = new MappedClass(GeoWithinOperationValidatorTest.InvalidGeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayOfStrings");
        // when
        boolean validationApplied = GeoWithinOperationValidator.getInstance().apply(mappedField, FilterOperator.GEO_WITHIN, new BasicDBObject("$box", 1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("is an array or iterable it should have numeric values"));
    }

    @Test
    public void shouldRejectGeoWithinOperatorWhenMappedFieldIsListThatDoesNotContainNumbers() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        MappedClass mappedClass = new MappedClass(GeoWithinOperationValidatorTest.InvalidGeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("listOfStrings");
        // when
        boolean validationApplied = GeoWithinOperationValidator.getInstance().apply(mappedField, FilterOperator.GEO_WITHIN, new BasicDBObject("$box", 1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("is an array or iterable it should have numeric values"));
    }

    @Test
    public void shouldRejectGeoWithinWhenValueDoesNotContainKeyword() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        MappedClass mappedClass = new MappedClass(GeoWithinOperationValidatorTest.GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        // when
        boolean validationApplied = GeoWithinOperationValidator.getInstance().apply(mappedField, FilterOperator.GEO_WITHIN, new BasicDBObject("notValidKey", 1), validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("For a $geoWithin operation, the value should be a valid geo query"));
    }

    @Test
    public void shouldRejectGeoWithinWhenValueIsNotADBObject() {
        // given
        List<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        MappedClass mappedClass = new MappedClass(GeoWithinOperationValidatorTest.GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        // when
        boolean validationApplied = GeoWithinOperationValidator.getInstance().apply(mappedField, FilterOperator.GEO_WITHIN, "NotAGeoQuery", validationFailures);
        // then
        Assert.assertThat(validationApplied, CoreMatchers.is(true));
        Assert.assertThat(validationFailures.size(), CoreMatchers.is(1));
        Assert.assertThat(validationFailures.get(0).toString(), CoreMatchers.containsString("For a $geoWithin operation, the value should be a valid geo query"));
    }

    @SuppressWarnings("unused")
    private static class GeoEntity {
        private final int[] array = new int[]{ 1 };

        private final List<Integer> list = Arrays.asList(1);
    }

    @SuppressWarnings("unused")
    private static class InvalidGeoEntity {
        private final String[] arrayOfStrings = new String[]{ "1" };

        private final List<String> listOfStrings = Arrays.asList("1");
    }
}

