package org.baeldung.hamcrest;


import com.google.common.collect.Lists;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringEndsWith;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;


public class HamcrestCoreMatchersUnitTest {
    @Test
    public void givenTestInput_WhenUsingIsForMatch() {
        // GIVEN
        String testString = "hamcrest core";
        // ASSERT
        MatcherAssert.assertThat(testString, CoreMatchers.is("hamcrest core"));
        MatcherAssert.assertThat(testString, CoreMatchers.is(CoreMatchers.equalTo("hamcrest core")));
    }

    @Test
    public void givenDifferentStaticTypeTestInput_WhenUsingEqualToObject_ThenCorrect() {
        // GIVEN
        Object original = 100;
        // ASSERT
        MatcherAssert.assertThat(original, equalToObject(100));
    }

    @Test
    public void givenTestInput_WhenUsingInstanceOfForClassTypeCheck() {
        MatcherAssert.assertThat("hamcrest", CoreMatchers.is(CoreMatchers.instanceOf(String.class)));
    }

    @Test
    public void givenTestInput_WhenUsingIsA_ThenAssertType() {
        MatcherAssert.assertThat("hamcrest core", CoreMatchers.isA(String.class));
    }

    @Test
    public void givenTestInput_WhenUsingEqualToMatcherForEquality() {
        // GIVEN
        String actualString = "Hamcrest Core";
        List<String> actualList = Lists.newArrayList("hamcrest", "core");
        // ASSERT
        MatcherAssert.assertThat(actualString, CoreMatchers.is(CoreMatchers.equalTo("Hamcrest Core")));
        MatcherAssert.assertThat(actualList, CoreMatchers.is(CoreMatchers.equalTo(Lists.newArrayList("hamcrest", "core"))));
    }

    @Test
    public void givenTestInput_WhenUsingNotForMatch() {
        // GIVEN
        String testString = "hamcrest";
        // ASSERT
        MatcherAssert.assertThat(testString, CoreMatchers.not("hamcrest core"));
        MatcherAssert.assertThat(testString, CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo("hamcrest core"))));
        MatcherAssert.assertThat(testString, CoreMatchers.is(CoreMatchers.not(CoreMatchers.instanceOf(Integer.class))));
    }

    @Test
    public void givenTestInput_WhenUsingNullValueForNullCheck() {
        // GIVEN
        Integer nullObject = null;
        // ASSERT
        MatcherAssert.assertThat(nullObject, CoreMatchers.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(nullObject, CoreMatchers.is(CoreMatchers.nullValue(Integer.class)));
    }

    @Test
    public void givenTestInput_WhenUsingNotNullValueForNotNullCheck() {
        // GIVEN
        Integer testNumber = 123;
        // ASSERT
        MatcherAssert.assertThat(testNumber, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(testNumber, CoreMatchers.is(CoreMatchers.notNullValue(Integer.class)));
    }

    @Test
    public void givenString_WhenStartsWith_ThenCorrect() {
        // GIVEN
        String testString = "hamcrest core";
        // ASSERT
        MatcherAssert.assertThat(testString, StringStartsWith.startsWith("hamcrest"));
    }

    @Test
    public void giveString_WhenStartsWithIgnoringCase_ThenCorrect() {
        // GIVEN
        String testString = "hamcrest core";
        // ASSERT
        MatcherAssert.assertThat(testString, startsWithIgnoringCase("HAMCREST"));
    }

    @Test
    public void givenString_WhenEndsWith_ThenCorrect() {
        // GIVEN
        String testString = "hamcrest core";
        // ASSERT
        MatcherAssert.assertThat(testString, StringEndsWith.endsWith("core"));
    }

    @Test
    public void givenString_WhenEndsWithIgnoringCase_ThenCorrect() {
        // GIVEN
        String testString = "hamcrest core";
        // ASSERT
        MatcherAssert.assertThat(testString, endsWithIgnoringCase("CORE"));
    }

    @Test
    public void givenString_WhenContainsString_ThenCorrect() {
        // GIVEN
        String testString = "hamcrest core";
        // ASSERT
        MatcherAssert.assertThat(testString, CoreMatchers.containsString("co"));
    }

    @Test
    public void givenString_WhenContainsStringIgnoringCase_ThenCorrect() {
        // GIVEN
        String testString = "hamcrest core";
        // ASSERT
        MatcherAssert.assertThat(testString, containsStringIgnoringCase("CO"));
    }

    @Test
    public void givenTestInput_WhenUsingHasItemInCollection() {
        // GIVEN
        List<String> list = Lists.newArrayList("java", "spring", "baeldung");
        // ASSERT
        MatcherAssert.assertThat(list, CoreMatchers.hasItem("java"));
        MatcherAssert.assertThat(list, CoreMatchers.hasItem(CoreMatchers.isA(String.class)));
    }

    @Test
    public void givenTestInput_WhenUsingHasItemsInCollection() {
        // GIVEN
        List<String> list = Lists.newArrayList("java", "spring", "baeldung");
        // ASSERT
        MatcherAssert.assertThat(list, CoreMatchers.hasItems("java", "baeldung"));
        MatcherAssert.assertThat(list, CoreMatchers.hasItems(CoreMatchers.isA(String.class), StringEndsWith.endsWith("ing")));
    }

    @Test
    public void givenTestInput_WhenUsingAnyForClassType() {
        MatcherAssert.assertThat("hamcrest", CoreMatchers.is(CoreMatchers.any(String.class)));
        MatcherAssert.assertThat("hamcrest", CoreMatchers.is(CoreMatchers.any(Object.class)));
    }

    @Test
    public void givenTestInput_WhenUsingAllOfForAllMatchers() {
        // GIVEN
        String testString = "Hamcrest Core";
        // ASSERT
        MatcherAssert.assertThat(testString, CoreMatchers.allOf(StringStartsWith.startsWith("Ham"), StringEndsWith.endsWith("ore"), CoreMatchers.containsString("Core")));
    }

    @Test
    public void givenTestInput_WhenUsingAnyOfForAnyMatcher() {
        // GIVEN
        String testString = "Hamcrest Core";
        // ASSERT
        MatcherAssert.assertThat(testString, CoreMatchers.anyOf(StringStartsWith.startsWith("Ham"), CoreMatchers.containsString("baeldung")));
    }

    @Test
    public void givenTestInput_WhenUsingBothForMatcher() {
        // GIVEN
        String testString = "Hamcrest Core Matchers";
        // ASSERT
        MatcherAssert.assertThat(testString, CoreMatchers.both(StringStartsWith.startsWith("Ham")).and(CoreMatchers.containsString("Core")));
    }

    @Test
    public void givenTestInput_WhenUsingEitherForMatcher() {
        // GIVEN
        String testString = "Hamcrest Core Matchers";
        // ASSERT
        MatcherAssert.assertThat(testString, CoreMatchers.either(StringStartsWith.startsWith("Bael")).or(CoreMatchers.containsString("Core")));
    }

    @Test
    public void givenTestInput_WhenUsingEveryItemForMatchInCollection() {
        // GIVEN
        List<String> testItems = Lists.newArrayList("Common", "Core", "Combinable");
        // ASSERT
        MatcherAssert.assertThat(testItems, CoreMatchers.everyItem(StringStartsWith.startsWith("Co")));
    }

    @Test
    public void givenTwoTestInputs_WhenUsingSameInstanceForMatch() {
        // GIVEN
        String string1 = "hamcrest";
        String string2 = string1;
        // ASSERT
        MatcherAssert.assertThat(string1, CoreMatchers.is(CoreMatchers.sameInstance(string2)));
    }

    @Test
    public void givenTwoTestInputs_WhenUsingTheInstanceForMatch() {
        // GIVEN
        String string1 = "hamcrest";
        String string2 = string1;
        // ASSERT
        MatcherAssert.assertThat(string1, CoreMatchers.is(CoreMatchers.theInstance(string2)));
    }
}

