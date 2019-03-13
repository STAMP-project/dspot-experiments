package dev.morphia.query;


import com.mongodb.BasicDBObject;
import dev.morphia.Key;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Serialized;
import dev.morphia.entities.EntityWithListsAndArrays;
import dev.morphia.entities.SimpleEntity;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class QueryValidatorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldAllowAllOperatorForIterableMapAndArrayValues() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.ALL, Arrays.asList(1, 2), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.ALL, Collections.emptySet(), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.ALL, new HashMap<String, String>(), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.ALL, new int[0], new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    // All of the following tests are whitebox, as I have retrofitted them afterwards.  I have no idea if this is the required
    // functionality or not
    @Test
    public void shouldAllowBooleanValuesForExistsOperator() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.EXISTS, true, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowGeoWithinOperatorWithAllAppropriateTrimmings() {
        // expect
        MappedClass mappedClass = new MappedClass(QueryValidatorTest.GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, FilterOperator.GEO_WITHIN, new BasicDBObject("$box", 1), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowInOperatorForIterableMapAndArrayValues() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.IN, Arrays.asList(1, 2), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.IN, Collections.emptySet(), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.IN, new HashMap<String, String>(), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.IN, new int[0], new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowModOperatorForArrayOfIntegerValues() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.MOD, new int[2], new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowNotInOperatorForIterableMapAndArrayValues() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.NOT_IN, Arrays.asList(1, 2), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.NOT_IN, Collections.emptySet(), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.NOT_IN, new HashMap<String, String>(), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.NOT_IN, new int[0], new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    // this used to fail
    @Test
    public void shouldAllowSizeOperatorForArrayListTypesAndIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayListOfIntegers");
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, QueryValidatorTest.NullClass.class, FilterOperator.SIZE, 3, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowSizeOperatorForArraysAndIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayOfInts");
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, QueryValidatorTest.NullClass.class, FilterOperator.SIZE, 3, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowSizeOperatorForListTypesAndIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("listOfIntegers");
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, QueryValidatorTest.NullClass.class, FilterOperator.SIZE, 3, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowTypeThatMatchesKeyTypeValue() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("integer");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, Integer.class, FilterOperator.EQUAL, new Key<Number>(Integer.class, "Integer", new ObjectId()), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowValueOfPatternWithTypeOfString() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, String.class, FilterOperator.EQUAL, Pattern.compile("."), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowValueWithEntityAnnotationAndTypeOfKey() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, Key.class, FilterOperator.EQUAL, new SimpleEntity(), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowValuesOfIntegerIfTypeIsDouble() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, Double.class, FilterOperator.EQUAL, 1, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, double.class, FilterOperator.EQUAL, 1, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowValuesOfIntegerIfTypeIsInteger() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, int.class, FilterOperator.EQUAL, 1, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, Integer.class, FilterOperator.EQUAL, 1, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowValuesOfIntegerOrLongIfTypeIsLong() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, Long.class, FilterOperator.EQUAL, 1, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, long.class, FilterOperator.EQUAL, 1, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, Long.class, FilterOperator.EQUAL, 1L, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, long.class, FilterOperator.EQUAL, 1L, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowValuesOfList() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, FilterOperator.EQUAL, new ArrayList<String>(), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldAllowValuesOfLongIfTypeIsDouble() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, Double.class, FilterOperator.EQUAL, 1L, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, double.class, FilterOperator.EQUAL, 1L, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldBeCompatibleIfTypeIsNull() {
        // expect
        // frankly not sure we should just let nulls through
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, null, FilterOperator.EQUAL, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldBeCompatibleIfValueIsNull() {
        // expect
        // frankly not sure we should just let nulls through
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.EQUAL, null, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(true));
    }

    @Test
    public void shouldNotAllowGeoOperatorIfValueDoesNotContainCorrectField() {
        // expect
        MappedClass mappedClass = new MappedClass(QueryValidatorTest.GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, FilterOperator.GEO_WITHIN, new BasicDBObject("name", "value"), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowGeoOperatorIfValueIsNotDBObject() {
        // expect
        MappedClass mappedClass = new MappedClass(QueryValidatorTest.GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, FilterOperator.GEO_WITHIN, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowGeoWithinWhenValueDoesNotContainKeyword() {
        // expect
        MappedClass mappedClass = new MappedClass(QueryValidatorTest.GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, FilterOperator.GEO_WITHIN, new BasicDBObject("notValidKey", 1), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    // this used to fail
    @Test
    public void shouldNotAllowModOperatorWithNonArrayValue() {
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, String.class, FilterOperator.MOD, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowModOperatorWithNonIntegerArray() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.MOD, new String[]{ "value" }, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowNonBooleanValuesForExistsOperator() {
        // given
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, FilterOperator.EXISTS, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowNonIntegerTypeIfValueIsInt() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, FilterOperator.EQUAL, 1, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowNonIntegerValueIfTypeIsInt() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, int.class, FilterOperator.EQUAL, "some non int value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowNonKeyTypeWithKeyValue() {
        // expect
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("listOfIntegers");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, FilterOperator.EQUAL, new Key<String>(String.class, "kind", new ObjectId()), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    // this used to fail
    @Test
    public void shouldNotAllowNonStringTypeWithValueOfPattern() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, Pattern.class, FilterOperator.EQUAL, Pattern.compile("."), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowOtherValuesForAllOperator() {
        // given
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, FilterOperator.ALL, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowOtherValuesForInOperator() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, String.class, FilterOperator.IN, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowOtherValuesForNotInOperator() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, FilterOperator.NOT_IN, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowSizeOperatorForNonIntegerValues() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, ArrayList.class, FilterOperator.SIZE, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowSizeOperatorForNonListTypes() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("notAnArrayOrList");
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, QueryValidatorTest.NullClass.class, FilterOperator.SIZE, 3, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowStringValueWithTypeThatIsNotString() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, Integer.class, FilterOperator.EQUAL, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowTypeThatDoesNotMatchKeyTypeValue() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, String.class, FilterOperator.EQUAL, new Key<Number>(Integer.class, "Integer", new ObjectId()), new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotAllowValueWithoutEntityAnnotationAndTypeOfKey() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, Key.class, FilterOperator.EQUAL, "value", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    // this used to fail
    @Test
    public void shouldNotErrorIfModOperatorIsUsedWithZeroLengthArrayOfIntegerValues() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.MOD, new int[0], new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    // this used to fail
    @Test
    public void shouldNotErrorModOperatorWithArrayOfNullValues() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, FilterOperator.MOD, new String[1], new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotErrorWhenValidateQueryCalledWithNullValue() {
        // this unit test is to drive fixing a null pointer in the logging code.  It's a bit stupid but it's an edge case that wasn't
        // caught.
        // when this is called, don't error
        new dev.morphia.internal.PathTarget(new Mapper(), SimpleEntity.class, "name");
    }

    @Test
    public void shouldRejectNonDoubleValuesIfTypeIsDouble() {
        // expect
        Assert.assertThat(QueryValidator.isCompatibleForOperator(null, null, Double.class, FilterOperator.EQUAL, "Not a double", new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldRejectTypesAndValuesThatDoNotMatch() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name");
        Assert.assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, String.class, FilterOperator.EQUAL, 1, new ArrayList<dev.morphia.query.validation.ValidationFailure>()), CoreMatchers.is(false));
    }

    @Test
    public void shouldReferToMappedClassInExceptionWhenFieldNotFound() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("Could not resolve path '_id.notAField' against 'dev.morphia.entities.SimpleEntity'");
        getTarget();
    }

    @Test
    public void shouldReferToMappedClassInExceptionWhenQueryingPastReferenceField() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("Could not resolve path 'reference.name' against 'dev.morphia.query.QueryValidatorTest$WithReference'");
        getTarget();
    }

    @Test
    public void shouldReferToMappedClassInExceptionWhenQueryingPastSerializedField() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage(("Could not resolve path 'serialized.name' against " + "'dev.morphia.query.QueryValidatorTest$WithSerializedField'"));
        getTarget();
    }

    private static class GeoEntity {
        private final int[] array = new int[]{ 1 };
    }

    private static class NullClass {}

    private static class WithReference {
        @Reference
        private SimpleEntity reference;
    }

    private static class SerializableClass implements Serializable {
        private String name;
    }

    private static class WithSerializedField {
        @Serialized
        private QueryValidatorTest.SerializableClass serialized;
    }
}

