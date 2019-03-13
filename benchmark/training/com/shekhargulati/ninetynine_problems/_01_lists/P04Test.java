package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P04Test {
    @Test
    public void listOfEmptyListShouldBe0() throws Exception {
        int length = P04.length(Collections.emptyList());
        Assert.assertThat(length, CoreMatchers.is(CoreMatchers.equalTo(0)));
    }

    @Test
    public void shouldFindListOfNonEmptyList() throws Exception {
        Assert.assertThat(P04.length(Arrays.asList(1, 2, 3, 4, 5)), CoreMatchers.is(CoreMatchers.equalTo(5)));
    }

    @Test
    public void listOfEmptyListShouldBe0_Recursive() throws Exception {
        int length = P04.lengthRecursive(Collections.emptyList());
        Assert.assertThat(length, CoreMatchers.is(CoreMatchers.equalTo(0)));
    }

    @Test
    public void shouldFindListOfNonEmptyList_Recursive() throws Exception {
        Assert.assertThat(P04.lengthRecursive(Arrays.asList(1, 2, 3, 4, 5)), CoreMatchers.is(CoreMatchers.equalTo(5)));
    }
}

