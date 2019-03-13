package com.baeldung.immutableobjects;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ImmutableObjectsUnitTest {
    @Test
    public void whenCallingStringReplace_thenStringDoesNotMutate() {
        // 2. What's an Immutable Object?
        final String name = "baeldung";
        final String newName = name.replace("dung", "----");
        Assert.assertEquals("baeldung", name);
        Assert.assertEquals("bael----", newName);
    }

    @Test
    public void whenAddingElementToList_thenSizeChange() {
        // 3. The?final Keyword?in Java (2)
        final List<String> strings = new ArrayList<>();
        Assert.assertEquals(0, strings.size());
        strings.add("baeldung");
        Assert.assertEquals(1, strings.size());
    }
}

