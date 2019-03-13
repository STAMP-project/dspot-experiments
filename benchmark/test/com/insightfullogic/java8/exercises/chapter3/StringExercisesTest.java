package com.insightfullogic.java8.exercises.chapter3;


import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class StringExercisesTest {
    @Test
    public void noLowercaseLettersInAnEmptyString() {
        Assert.assertEquals(0, StringExercises.countLowercaseLetters(""));
    }

    @Test
    public void countsLowercaseLetterExample() {
        Assert.assertEquals(3, StringExercises.countLowercaseLetters("aBcDeF"));
    }

    @Test
    public void suppoertsNoLowercaseLetters() {
        Assert.assertEquals(0, StringExercises.countLowercaseLetters("ABCDEF"));
    }

    @Test
    public void noStringReturnedForEmptyList() {
        Assert.assertFalse(StringExercises.mostLowercaseString(Collections.<String>emptyList()).isPresent());
    }

    @Test
    public void findsMostLowercaseString() {
        Optional<String> result = StringExercises.mostLowercaseString(Arrays.asList("a", "abc", "ABCde"));
        Assert.assertEquals(result, Optional.of("abc"));
    }
}

