package com.github.javafaker;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class OptionsTest extends AbstractFakerTest {
    private String[] options;

    @Test
    public void testOptionWithArray() {
        Assert.assertThat(faker.options().option(options), Matchers.isOneOf(options));
    }

    @Test
    public void testOptionWithVarargsString() {
        Assert.assertThat(faker.options().option("A", "B", "C"), Matchers.isOneOf(options));
    }

    @Test
    public void testOptionWithVarargsInteger() {
        Integer[] integerOptions = new Integer[]{ 1, 3, 4, 5 };
        Assert.assertThat(faker.options().option(1, 3, 4, 5), Matchers.isOneOf(integerOptions));
    }

    @Test
    public void testOptionWithEnum() {
        Assert.assertThat(faker.options().option(OptionsTest.Day.class), Matchers.isOneOf(OptionsTest.Day.values()));
    }

    @Test
    public void testNextArrayElement() {
        Integer[] array = new Integer[]{ 1, 2, 3, 5, 8, 13, 21 };
        for (int i = 1; i < 10; i++) {
            Assert.assertThat(faker.options().nextElement(array), Matchers.isIn(array));
        }
    }

    @Test
    public void testNextListElement() {
        List<Integer> list = Arrays.asList(1, 2, 3, 5, 8, 13, 21);
        for (int i = 1; i < 10; i++) {
            Assert.assertThat(faker.options().nextElement(list), Matchers.isIn(list));
        }
    }

    public enum Day {

        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY;}
}

