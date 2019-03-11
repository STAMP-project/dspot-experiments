package com.pushtorefresh.storio3.test;


import android.net.Uri;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.mockito.internal.util.MockUtil;


public class ToStringCheckerTest {
    static class ClassWithCorrectToString {
        int a;

        String b;

        @Override
        public String toString() {
            return ((((("ClassWithCorrectToString{" + "a=") + (a)) + ", b='") + (b)) + '\'') + '}';
        }
    }

    @Test
    public void shouldCheckSuccessfully() {
        ToStringChecker.forClass(ToStringCheckerTest.ClassWithCorrectToString.class).check();
    }

    static class ClassWithIncorrectToString {
        int a;

        String b;

        @Override
        public String toString() {
            return ((("ClassWithIncorrectToString{" + "b='") + (b)) + '\'') + '}';
        }
    }

    @Test
    public void shouldThrowExceptionBecauseOneOfTheFieldsIsNotIncludedIntoToString() {
        try {
            ToStringChecker.forClass(ToStringCheckerTest.ClassWithIncorrectToString.class).check();
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError expected) {
            assertThat(expected).hasMessage(("toString() does not contain field = a, " + "object = ClassWithIncorrectToString{b='some_string'}"));
        }
    }

    @Test
    public void shouldCreateSampleValueOfPrimitiveInt() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(int.class);
        assertThat(sample).isEqualTo(1);
    }

    @Test
    public void shouldCreateSampleValueOfInteger() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(Integer.class);
        assertThat(sample).isEqualTo(1);
    }

    @Test
    public void shouldCreateSampleValueOfPrimitiveLong() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(long.class);
        assertThat(sample).isEqualTo(1L);
    }

    @Test
    public void shouldCreateSampleValueOfLong() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(Long.class);
        assertThat(sample).isEqualTo(1L);
    }

    @Test
    public void shouldCreateSampleValueOfString() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(String.class);
        assertThat(sample).isEqualTo("some_string");
    }

    @Test
    public void shouldCreateSampleValueOfList() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(List.class);
        // noinspection AssertEqualsBetweenInconvertibleTypes
        assertThat(sample).isEqualTo(Arrays.asList("1", "2", "3"));
    }

    @Test
    public void shouldCreateSampleValueOfMap() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(Map.class);
        // noinspection AssertEqualsBetweenInconvertibleTypes
        assertThat(sample).isEqualTo(Collections.singletonMap("map_key", "map_value"));
    }

    @Test
    public void shouldCreateSampleValueOfSet() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(Set.class);
        // noinspection AssertEqualsBetweenInconvertibleTypes
        assertThat(sample).isEqualTo(Collections.singleton("set_item"));
    }

    @Test
    public void shouldCreateSampleValueOfUri() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(Uri.class);
        // We can not check equality of Uri instance here?
        assertThat(sample).isInstanceOf(Uri.class);
    }

    @Test
    public void shouldCreateSampleValueOfAnyObject() {
        Object sample = new ToStringChecker<Object>(Object.class).createSampleValueOfType(Object.class);
        MockUtil.isMock(sample);
        assertThat(sample.getClass().getSuperclass()).isEqualTo(Object.class);
    }
}

