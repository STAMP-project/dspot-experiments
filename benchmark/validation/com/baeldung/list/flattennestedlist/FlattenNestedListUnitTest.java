package com.baeldung.list.flattennestedlist;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Test;


public class FlattenNestedListUnitTest {
    private List<List<String>> lol = Arrays.asList(Arrays.asList("one:one"), Arrays.asList("two:one", "two:two", "two:three"), Arrays.asList("three:one", "three:two", "three:three", "three:four"));

    @Test
    public void givenNestedList_thenFlattenImperatively() {
        List<String> ls = flattenListOfListsImperatively(lol);
        Assert.assertNotNull(ls);
        Assert.assertTrue(((ls.size()) == 8));
        // assert content
        Assert.assertThat(ls, IsIterableContainingInOrder.contains("one:one", "two:one", "two:two", "two:three", "three:one", "three:two", "three:three", "three:four"));
    }

    @Test
    public void givenNestedList_thenFlattenFunctionally() {
        List<String> ls = flattenListOfListsStream(lol);
        Assert.assertNotNull(ls);
        Assert.assertTrue(((ls.size()) == 8));
        // assert content
        Assert.assertThat(ls, IsIterableContainingInOrder.contains("one:one", "two:one", "two:two", "two:three", "three:one", "three:two", "three:three", "three:four"));
    }
}

