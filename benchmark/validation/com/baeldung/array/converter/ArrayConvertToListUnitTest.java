package com.baeldung.array.converter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ArrayConvertToListUnitTest {
    @Test
    public void givenAnStringArray_whenConvertArrayToList_thenListCreated() {
        String[] flowers = new String[]{ "Ageratum", "Allium", "Poppy", "Catmint" };
        List<String> flowerList = Arrays.asList(flowers);
        Assert.assertNotNull(flowerList);
        Assert.assertEquals(flowerList.size(), 4);
        Assert.assertEquals(flowerList.get(0), "Ageratum");
        Assert.assertEquals(flowerList.get(1), "Allium");
        Assert.assertEquals(flowerList.get(2), "Poppy");
        Assert.assertEquals(flowerList.get(3), "Catmint");
    }

    @Test
    public void givenAnIntArray_whenConvertArrayToList_thenListWithOneElementCreated() {
        int[] primitives = new int[]{ 1, 2, 3, 4 };
        List numbers = Arrays.asList(primitives);
        Assert.assertNotNull(numbers);
        Assert.assertEquals(numbers.size(), 1);
        Assert.assertEquals(numbers.get(0), primitives);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void givenAnStringArray_whenConvertArrayToListAndAddAnElement_thenThrowUnsupportedOperationException() {
        String[] flowers = new String[]{ "Ageratum", "Allium", "Poppy", "Catmint" };
        List<String> flowerList = Arrays.asList(flowers);
        Assert.assertNotNull(flowerList);
        Assert.assertEquals(flowerList.size(), 4);
        Assert.assertEquals(flowerList.get(0), "Ageratum");
        Assert.assertEquals(flowerList.get(1), "Allium");
        Assert.assertEquals(flowerList.get(2), "Poppy");
        Assert.assertEquals(flowerList.get(3), "Catmint");
        flowerList.add("Celosia");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void givenAnStringArray_whenConvertArrayToListAndRemoveAnElement_thenThrowUnsupportedOperationException() {
        String[] flowers = new String[]{ "Ageratum", "Allium", "Poppy", "Catmint" };
        List<String> flowerList = Arrays.asList(flowers);
        Assert.assertNotNull(flowerList);
        Assert.assertEquals(flowerList.size(), 4);
        Assert.assertEquals(flowerList.get(0), "Ageratum");
        Assert.assertEquals(flowerList.get(1), "Allium");
        Assert.assertEquals(flowerList.get(2), "Poppy");
        Assert.assertEquals(flowerList.get(3), "Catmint");
        flowerList.remove("Poppy");
    }

    @Test
    public void givenAnStringArray_whenCreateListFromArrayAndAddAnElement_thenListOk() {
        String[] flowers = new String[]{ "Ageratum", "Allium", "Poppy", "Catmint" };
        List<String> flowerList = Arrays.asList(flowers);
        Assert.assertNotNull(flowerList);
        Assert.assertEquals(flowerList.size(), 4);
        Assert.assertEquals(flowerList.get(0), "Ageratum");
        Assert.assertEquals(flowerList.get(1), "Allium");
        Assert.assertEquals(flowerList.get(2), "Poppy");
        Assert.assertEquals(flowerList.get(3), "Catmint");
        List<String> newflowerList = new ArrayList<>(flowerList);
        Assert.assertNotNull(newflowerList);
        Assert.assertEquals(newflowerList.size(), 4);
        Assert.assertEquals(newflowerList.get(0), "Ageratum");
        Assert.assertEquals(newflowerList.get(1), "Allium");
        Assert.assertEquals(newflowerList.get(2), "Poppy");
        Assert.assertEquals(newflowerList.get(3), "Catmint");
        newflowerList.add("Celosia");
        Assert.assertEquals(newflowerList.size(), 5);
        Assert.assertEquals(newflowerList.get(4), "Celosia");
    }

    @Test
    public void givenAnStringArray_whenIterateArrayAndAddTheElementsToNewListAndAddAnElement_thenListOk() {
        String[] flowers = new String[]{ "Ageratum", "Allium", "Poppy", "Catmint" };
        List<String> flowerList = new ArrayList<>();
        for (String flower : flowers) {
            flowerList.add(flower);
        }
        Assert.assertNotNull(flowerList);
        Assert.assertEquals(flowerList.size(), 4);
        Assert.assertEquals(flowerList.get(0), "Ageratum");
        Assert.assertEquals(flowerList.get(1), "Allium");
        Assert.assertEquals(flowerList.get(2), "Poppy");
        Assert.assertEquals(flowerList.get(3), "Catmint");
        flowerList.add("Celosia");
        Assert.assertEquals(flowerList.size(), 5);
        Assert.assertEquals(flowerList.get(4), "Celosia");
    }
}

