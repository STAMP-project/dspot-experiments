package com.pushtorefresh.storio3.internal;


import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;


// endregion}
public class InternalQueriesTest {
    @Test
    public void constructorShouldBePrivateAndThrowException() {
        PrivateConstructorChecker.forClass(InternalQueries.class).expectedTypeOfException(IllegalStateException.class).expectedExceptionMessage("No instances please").check();
    }

    // region Tests for Queries.unmodifiableNonNullListOfStrings() from array of objects
    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = null;
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(array)).isSameAs(Collections.emptyList());
    }

    @Test
    public void emptyArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = new Object[]{  };
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(array)).isSameAs(Collections.emptyList());
    }

    @Test
    public void nonEmptyArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = new Object[]{ "1", "2", "3" };
        List<String> list = InternalQueries.unmodifiableNonNullListOfStrings(array);
        assertThat(list).containsExactly("1", "2", "3");
    }

    @Test
    public void nullItemInArrayToUnmodifiableNonNullListOfStrings() {
        Object[] array = new Object[]{ "1", null, "3" };
        List<String> strings = InternalQueries.unmodifiableNonNullListOfStrings(array);
        assertThat(strings).containsExactly("1", "null", "3");
    }

    // endregion
    // region Tests for Queries.unmodifiableNonNullListOfStrings() from list of objects
    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListToUnmodifiableNonNullListOfStrings() {
        List<Object> list = null;
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(list)).isSameAs(Collections.emptyList());
    }

    @Test
    public void emptyListToUnmodifiableNonNullListOfStrings() {
        List<Object> list = new ArrayList<Object>();
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(list)).isSameAs(Collections.emptyList());
    }

    @Test
    public void nonEmptyListToUnmodifiableNonNullListOfStrings() {
        List<String> strings = Arrays.asList("1", "2", "3");
        assertThat(InternalQueries.unmodifiableNonNullListOfStrings(strings)).isEqualTo(strings);
    }

    @Test
    public void nonEmptyListWithNullElementToUnmodifiableNonNullListOfStrings() {
        List<String> strings = Arrays.asList("1", null, "3");
        List<String> result = InternalQueries.unmodifiableNonNullListOfStrings(strings);
        assertThat(result).containsExactly("1", "null", "3");
    }

    // endregion
    // region Tests for Queries.unmodifiableNonNullList() from array of objects
    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullArrayToUnmodifiableNonNullList() {
        Object[] array = null;
        assertThat(InternalQueries.unmodifiableNonNullList(array)).isSameAs(Collections.emptyList());
    }

    @Test
    public void emptyArrayToUnmodifiableNonNullList() {
        Object[] array = new Object[]{  };
        assertThat(InternalQueries.unmodifiableNonNullList(array)).isSameAs(Collections.emptyList());
    }

    @Test
    public void nonEmptyArrayToUnmodifiableNonNullList() {
        Object[] array = new Object[]{ "1", "2", "3" };
        List<Object> list = InternalQueries.unmodifiableNonNullList(array);
        assertThat(list).containsExactly(array);
    }

    @Test
    public void nullItemInArrayToUnmodifiableNonNullList() {
        Object[] array = new Object[]{ 1, null, 3 };
        List<Object> objects = InternalQueries.unmodifiableNonNullList(array);
        assertThat(objects).containsExactly(array);
    }

    // endregion
    // region Tests for Queries.unmodifiableNonNullList() from list of objects
    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListToUnmodifiableNonNullList() {
        List<String> list = null;
        assertThat(InternalQueries.unmodifiableNonNullList(list)).isSameAs(Collections.emptyList());
    }

    @Test
    public void emptyListToUnmodifiableNonNullList() {
        List<String> list = new ArrayList<String>();
        assertThat(InternalQueries.unmodifiableNonNullList(list)).isSameAs(Collections.emptyList());
    }

    @Test
    public void nonEmptyListToUnmodifiableNonNullList() {
        List<String> list = Arrays.asList("1", "2", "3");
        List<String> unmodifiableList = InternalQueries.unmodifiableNonNullList(list);
        assertThat(unmodifiableList).containsExactly("1", "2", "3");
    }

    @Test
    public void listToUnmodifiableNonNullListIsReallyUnmodifiable() {
        List<String> unmodifiableList = InternalQueries.unmodifiableNonNullList(Arrays.asList("1", "2", "3"));
        assertThatListIsImmutable(unmodifiableList);
    }

    // endregion
    // region Tests for Queries.unmodifiableNonNullSet()
    @Test
    public void nullSetToUnmodifiableNonNullSet() {
        assertThat(InternalQueries.unmodifiableNonNullSet(null)).isSameAs(Collections.emptySet());
    }

    @Test
    public void emptySetToUnmodifiableNonNullSet() {
        assertThat(InternalQueries.unmodifiableNonNullSet(new HashSet<Object>())).isSameAs(Collections.emptySet());
    }

    @Test
    public void nonEmptySetToUnmodifiableNonNullSet() {
        Set<String> testSet = new HashSet<String>();
        testSet.add("1");
        testSet.add("2");
        testSet.add("3");
        Set<String> unmodifiableSet = InternalQueries.unmodifiableNonNullSet(testSet);
        assertThat(unmodifiableSet).isEqualTo(testSet);
    }

    // endregion
    // region Tests for Queries.nonNullArrayOfStrings()
    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullListOfStringsToNonNullArrayOfStrings() {
        List<String> list = null;
        assertThat(Arrays.equals(new String[]{  }, InternalQueries.nonNullArrayOfStrings(list))).isTrue();
    }

    @Test
    public void emptyListOfStringsToNonNullArrayOfStrings() {
        List<String> list = new ArrayList<String>();
        assertThat(Arrays.equals(new String[]{  }, InternalQueries.nonNullArrayOfStrings(list))).isTrue();
    }

    @Test
    public void nonEmptyListOfStringsToNonNullArrayOfStrings() {
        List<String> list = Arrays.asList("1", "2", "3");
        String[] array = InternalQueries.nonNullArrayOfStrings(list);
        assertThat(array).containsExactly("1", "2", "3");
    }

    // endregion
    // region Tests for Queries.nullableArrayOfStringsFromListOfStrings()
    @Test
    public void nullListOfStringsToNullableArrayOfStringFromListOfStrings() {
        assertThat(InternalQueries.nullableArrayOfStringsFromListOfStrings(null)).isNull();
    }

    @Test
    public void emptyListOfStringsToNullableArrayOfStringFromListOfStrings() {
        List<String> list = new ArrayList<String>();
        assertThat(InternalQueries.nullableArrayOfStringsFromListOfStrings(list)).isNull();
    }

    @Test
    public void nonEmptyListOfStringsToNullableArrayOfStringsFromListOfStrings() {
        List<String> list = Arrays.asList("1", "2", "3");
        String[] array = InternalQueries.nullableArrayOfStringsFromListOfStrings(list);
        assertThat(array).containsExactly("1", "2", "3");
    }

    // endregion
    // region Tests for Queries.nullableArrayOfStrings()
    @Test
    public void nullListOfObjectsToNullableArrayOfStrings() {
        assertThat(InternalQueries.nullableArrayOfStrings(null)).isNull();
    }

    @Test
    public void emptyListOfObjectsToNullableArrayOfStrings() {
        List<Object> list = new ArrayList<Object>();
        assertThat(InternalQueries.nullableArrayOfStrings(list)).isNull();
    }

    @Test
    public void nonEmptyListOfObjectsToNullableArrayOfStrings() {
        List<Object> list = new ArrayList<Object>(3);
        list.add(1);
        list.add(null);
        list.add(3);
        String[] array = InternalQueries.nullableArrayOfStrings(list);
        assertThat(array).containsExactly("1", "null", "3");
    }

    // endregion
    // region Tests for Queries.nonNullString()
    @Test
    public void nonNullStringFromNull() {
        assertThat(InternalQueries.nonNullString(null)).isEqualTo("");
    }

    @Test
    public void nonNullStringFromEmptyString() {
        assertThat(InternalQueries.nonNullString("")).isEqualTo("");
    }

    @Test
    public void nonNullStringFromNormalString() {
        assertThat(InternalQueries.nonNullString("123")).isEqualTo("123");
    }

    // endregion
    // region Tests for Queries.nullableString()
    @Test
    public void nullableStringFromNull() {
        assertThat(InternalQueries.nullableString(null)).isNull();
    }

    @Test
    public void nullableStringFromEmptyString() {
        assertThat(InternalQueries.nullableString("")).isNull();
    }

    @Test
    public void nullableStringFromNormalString() {
        assertThat(InternalQueries.nullableString("123")).isEqualTo("123");
    }

    // endregion
    // region Tests for Queries.nonNullSet()
    @Test
    public void nonNullSetFromNullAsCollection() {
        assertThat(InternalQueries.nonNullSet(((Collection<String>) (null)))).isSameAs(Collections.emptySet());
    }

    @Test
    public void nonNullSetFromEmptyCollection() {
        // noinspection ArraysAsListWithZeroOrOneArgument
        assertThat(InternalQueries.nonNullSet(Arrays.<String>asList())).isSameAs(Collections.emptySet());
    }

    @Test
    public void nonNullSetFromCollection() {
        List<String> values = Arrays.asList("one", "two");
        assertThat(InternalQueries.nonNullSet(values)).isEqualTo(new HashSet<String>(values));
    }

    @Test
    public void nonNullSetFromNullAsArray() {
        assertThat(InternalQueries.nonNullSet(((String[]) (null)))).isSameAs(Collections.emptySet());
    }

    @Test
    public void nonNullSetFromEmptyArray() {
        assertThat(InternalQueries.nonNullSet(new String[0])).isSameAs(Collections.emptySet());
    }

    @Test
    public void nonNullSetFromArray() {
        String[] values = new String[]{ "one", "two" };
        assertThat(InternalQueries.nonNullSet(values)).isEqualTo(new HashSet<String>(Arrays.asList(values)));
    }

    @Test
    public void nonNullSetWithFirstItemAndNullArray() {
        assertThat(InternalQueries.nonNullSet("one", null)).isEqualTo(new HashSet<String>(Arrays.asList("one")));
    }

    @Test
    public void nonNullSetWithFirstItemAndNotEmptyArray() {
        String[] values = new String[]{ "two" };
        assertThat(InternalQueries.nonNullSet("one", values)).isEqualTo(new HashSet<String>() {
            {
                add("one");
                add("two");
            }
        });
    }
}

