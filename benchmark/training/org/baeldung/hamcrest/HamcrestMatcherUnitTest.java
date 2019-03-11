package org.baeldung.hamcrest;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.Every;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.IsSame;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringEndsWith;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;


public class HamcrestMatcherUnitTest {
    @Test
    public void given2Strings_whenEqual_thenCorrect() {
        String a = "foo";
        String b = "FOO";
        MatcherAssert.assertThat(a, equalToIgnoringCase(b));
    }

    @Test
    public void givenBean_whenHasValue_thenCorrect() {
        Person person = new Person("Baeldung", "New York");
        MatcherAssert.assertThat(person, hasProperty("name"));
    }

    @Test
    public void givenBean_whenHasCorrectValue_thenCorrect() {
        Person person = new Person("Baeldung", "New York");
        MatcherAssert.assertThat(person, hasProperty("address", equalTo("New York")));
    }

    @Test
    public void given2Beans_whenHavingSameValues_thenCorrect() {
        Person person1 = new Person("Baeldung", "New York");
        Person person2 = new Person("Baeldung", "New York");
        MatcherAssert.assertThat(person1, samePropertyValuesAs(person2));
    }

    @Test
    public void givenAList_whenChecksSize_thenCorrect() {
        List<String> hamcrestMatchers = Arrays.asList("collections", "beans", "text", "number");
        MatcherAssert.assertThat(hamcrestMatchers, hasSize(4));
    }

    @Test
    public void givenArray_whenChecksSize_thenCorrect() {
        String[] hamcrestMatchers = new String[]{ "collections", "beans", "text", "number" };
        MatcherAssert.assertThat(hamcrestMatchers, arrayWithSize(4));
    }

    @Test
    public void givenAListAndValues_whenChecksListForGivenValues_thenCorrect() {
        List<String> hamcrestMatchers = Arrays.asList("collections", "beans", "text", "number");
        MatcherAssert.assertThat(hamcrestMatchers, containsInAnyOrder("beans", "text", "collections", "number"));
    }

    @Test
    public void givenAListAndValues_whenChecksListForGivenValuesWithOrder_thenCorrect() {
        List<String> hamcrestMatchers = Arrays.asList("collections", "beans", "text", "number");
        MatcherAssert.assertThat(hamcrestMatchers, contains("collections", "beans", "text", "number"));
    }

    @Test
    public void givenArrayAndValue_whenValueFoundInArray_thenCorrect() {
        String[] hamcrestMatchers = new String[]{ "collections", "beans", "text", "number" };
        MatcherAssert.assertThat(hamcrestMatchers, hasItemInArray("text"));
    }

    @Test
    public void givenValueAndArray_whenValueIsOneOfArrayElements_thenCorrect() {
        String[] hamcrestMatchers = new String[]{ "collections", "beans", "text", "number" };
        MatcherAssert.assertThat("text", isOneOf(hamcrestMatchers));
    }

    @Test
    public void givenArrayAndValues_whenValuesFoundInArray_thenCorrect() {
        String[] hamcrestMatchers = new String[]{ "collections", "beans", "text", "number" };
        MatcherAssert.assertThat(hamcrestMatchers, arrayContainingInAnyOrder("beans", "collections", "number", "text"));
    }

    @Test
    public void givenArrayAndValues_whenValuesFoundInArrayInOrder_thenCorrect() {
        String[] hamcrestMatchers = new String[]{ "collections", "beans", "text", "number" };
        MatcherAssert.assertThat(hamcrestMatchers, arrayContaining("collections", "beans", "text", "number"));
    }

    @Test
    public void givenCollection_whenEmpty_thenCorrect() {
        List<String> emptyList = new ArrayList<>();
        MatcherAssert.assertThat(emptyList, empty());
    }

    @Test
    public void givenValueAndArray_whenValueFoundInArray_thenCorrect() {
        String[] array = new String[]{ "collections", "beans", "text", "number" };
        MatcherAssert.assertThat("beans", isIn(array));
    }

    @Test
    public void givenMapAndKey_whenKeyFoundInMap_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("blogname", "baeldung");
        MatcherAssert.assertThat(map, hasKey("blogname"));
    }

    @Test
    public void givenMapAndEntry_whenEntryFoundInMap_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("blogname", "baeldung");
        MatcherAssert.assertThat(map, hasEntry("blogname", "baeldung"));
    }

    @Test
    public void givenMapAndValue_whenValueFoundInMap_thenCorrect() {
        Map<String, String> map = new HashMap<>();
        map.put("blogname", "baeldung");
        MatcherAssert.assertThat(map, hasValue("baeldung"));
    }

    @Test
    public void givenString_whenEmpty_thenCorrect() {
        String str = "";
        MatcherAssert.assertThat(str, isEmptyString());
    }

    @Test
    public void givenString_whenEmptyOrNull_thenCorrect() {
        String str = null;
        MatcherAssert.assertThat(str, isEmptyOrNullString());
    }

    @Test
    public void given2Strings_whenEqualRegardlessWhiteSpace_thenCorrect() {
        String str1 = "text";
        String str2 = " text ";
        MatcherAssert.assertThat(str1, equalToIgnoringWhiteSpace(str2));
    }

    @Test
    public void givenString_whenContainsGivenSubstring_thenCorrect() {
        String str = "calligraphy";
        MatcherAssert.assertThat(str, stringContainsInOrder(Arrays.asList("call", "graph")));
    }

    @Test
    public void givenBean_whenToStringReturnsRequiredString_thenCorrect() {
        Person person = new Person("Barrack", "Washington");
        String str = person.toString();
        MatcherAssert.assertThat(person, hasToString(str));
    }

    @Test
    public void given2Classes_whenOneInheritsFromOther_thenCorrect() {
        MatcherAssert.assertThat(Cat.class, typeCompatibleWith(Animal.class));
    }

    @Test
    public void given2Strings_whenIsEqualRegardlessWhiteSpace_thenCorrect() {
        String str1 = "text";
        String str2 = " text ";
        MatcherAssert.assertThat(str1, Is.is(equalToIgnoringWhiteSpace(str2)));
    }

    @Test
    public void given2Strings_whenIsNotEqualRegardlessWhiteSpace_thenCorrect() {
        String str1 = "text";
        String str2 = " texts ";
        MatcherAssert.assertThat(str1, IsNot.not(equalToIgnoringWhiteSpace(str2)));
    }

    @Test
    public void given2Strings_whenNotEqual_thenCorrect() {
        String str1 = "text";
        String str2 = "texts";
        MatcherAssert.assertThat(str1, IsNot.not(str2));
    }

    @Test
    public void given2Strings_whenIsEqual_thenCorrect() {
        String str1 = "text";
        String str2 = "text";
        MatcherAssert.assertThat(str1, Is.is(str2));
    }

    @Test
    public void givenAStrings_whenContainsAnotherGivenString_thenCorrect() {
        String str1 = "calligraphy";
        String str2 = "call";
        MatcherAssert.assertThat(str1, StringContains.containsString(str2));
    }

    @Test
    public void givenAString_whenEndsWithAnotherGivenString_thenCorrect() {
        String str1 = "calligraphy";
        String str2 = "phy";
        MatcherAssert.assertThat(str1, StringEndsWith.endsWith(str2));
    }

    @Test
    public void givenAString_whenStartsWithAnotherGivenString_thenCorrect() {
        String str1 = "calligraphy";
        String str2 = "call";
        MatcherAssert.assertThat(str1, StringStartsWith.startsWith(str2));
    }

    @Test
    public void given2Objects_whenSameInstance_thenCorrect() {
        Cat cat = new Cat();
        MatcherAssert.assertThat(cat, IsSame.sameInstance(cat));
    }

    @Test
    public void givenAnObject_whenInstanceOfGivenClass_thenCorrect() {
        Cat cat = new Cat();
        MatcherAssert.assertThat(cat, IsInstanceOf.instanceOf(Cat.class));
    }

    @Test
    public void givenList_whenEachElementGreaterThan0_thenCorrect() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        int baseCase = 0;
        MatcherAssert.assertThat(list, Every.everyItem(greaterThan(baseCase)));
    }

    @Test
    public void givenString_whenNotNull_thenCorrect() {
        String str = "notnull";
        MatcherAssert.assertThat(str, IsNull.notNullValue());
    }

    @Test
    public void givenString_whenMeetsAnyOfGivenConditions_thenCorrect() {
        String str = "calligraphy";
        String start = "call";
        String end = "foo";
        MatcherAssert.assertThat(str, AnyOf.anyOf(StringStartsWith.startsWith(start), StringContains.containsString(end)));
    }

    @Test
    public void givenString_whenMeetsAllOfGivenConditions_thenCorrect() {
        String str = "calligraphy";
        String start = "call";
        String end = "phy";
        MatcherAssert.assertThat(str, AllOf.allOf(StringStartsWith.startsWith(start), StringEndsWith.endsWith(end)));
    }

    @Test
    public void givenInteger_whenAPositiveValue_thenCorrect() {
        int num = 1;
        MatcherAssert.assertThat(num, IsPositiveInteger.isAPositiveInteger());
    }

    @Test
    public void givenAnInteger_whenGreaterThan0_thenCorrect() {
        int num = 1;
        MatcherAssert.assertThat(num, greaterThan(0));
    }

    @Test
    public void givenAnInteger_whenGreaterThanOrEqTo5_thenCorrect() {
        int num = 5;
        MatcherAssert.assertThat(num, greaterThanOrEqualTo(5));
    }

    @Test
    public void givenAnInteger_whenLessThan0_thenCorrect() {
        int num = -1;
        MatcherAssert.assertThat(num, lessThan(0));
    }

    @Test
    public void givenAnInteger_whenLessThanOrEqTo5_thenCorrect() {
        MatcherAssert.assertThat((-1), lessThanOrEqualTo(5));
    }

    @Test
    public void givenADouble_whenCloseTo_thenCorrect() {
        MatcherAssert.assertThat(1.2, closeTo(1, 0.5));
    }
}

