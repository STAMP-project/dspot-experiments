package com.baeldung.java.list;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CopyListServiceUnitTest {
    List<Flower> flowers;

    private CopyListService copyListService;

    @Test
    public void givenAList_whenListDoesNotHaveNullElements_thenReturnAnotherListWithTheSameElementsByConstructor() {
        List<Flower> copy = copyListService.copyListByConstructor(flowers);
        Assert.assertEquals(copy.size(), flowers.size());
        Assert.assertTrue(copy.containsAll(flowers));
    }

    @Test
    public void givenAList_whenListDoesNotHaveNullElements_thenReturnAnotherListWithOneModifiedElementByConstructor() {
        List<Flower> copy = copyListService.copyListByConstructorAndEditOneFlowerInTheNewList(flowers);
        Assert.assertEquals(copy.size(), flowers.size());
        Assert.assertTrue(copy.containsAll(flowers));
    }

    @Test
    public void givenAList_whenListDoesNotHaveNullElements_thenReturnAnotherListWithTheSameElementsByAddAllmethod() {
        List<Flower> copy = copyListService.copyListByAddAllMethod(flowers);
        Assert.assertEquals(copy.size(), flowers.size());
        Assert.assertTrue(copy.containsAll(flowers));
    }

    @Test
    public void givenAList_whenListDoesNotHaveNullElements_thenReturnAnotherListWithOneModifiedElementByAddAllmethod() {
        List<Flower> copy = copyListService.copyListByAddAllMethodAndEditOneFlowerInTheNewList(flowers);
        Assert.assertEquals(copy.size(), flowers.size());
        Assert.assertTrue(copy.containsAll(flowers));
    }

    @Test
    public void givenAList_whenListsHaveSameSize_thenReturnAnotherListWithTheSameElementsByCopyMethod() {
        List<Integer> source = Arrays.asList(1, 2, 3);
        List<Integer> dest = Arrays.asList(4, 5, 6);
        dest = copyListService.copyListByCopyMethod(source, dest);
        Assert.assertEquals(dest.size(), source.size());
        Assert.assertTrue(dest.containsAll(source));
    }

    @Test
    public void givenAList_whenListsHaveDifferentSize_thenReturnAnotherListWithTheSameElementsByCopyMethod() {
        List<Integer> source = Arrays.asList(1, 2, 3);
        List<Integer> dest = Arrays.asList(5, 6, 7, 8, 9, 10);
        dest = copyListService.copyListByCopyMethod(source, dest);
        Assert.assertNotEquals(dest.size(), source.size());
        Assert.assertTrue(dest.containsAll(source));
    }

    @Test
    public void givenAList_whenListDoesNotHaveNullElements_thenReturnAnotherListWithTheSameElementsByStreamProcess() {
        List<Flower> copy = copyListService.copyListByStream(flowers);
        Assert.assertEquals(copy.size(), flowers.size());
        Assert.assertTrue(copy.containsAll(flowers));
    }

    @Test
    public void givenAList_whenListDoesNotHaveNullElements_thenReturnAnotherListWithOneElementLessByStreamProcess() {
        List<Flower> copy = copyListService.copyListByStreamAndSkipFirstElement(flowers);
        Assert.assertNotEquals(copy.size(), flowers.size());
        Assert.assertEquals(((copy.size()) + 1), flowers.size());
        Assert.assertFalse(copy.containsAll(flowers));
    }

    @Test
    public void givenAList_whenListDoesNotHaveNullElements_thenReturnAnotherListWithFilterElementsByStreamProcess() {
        List<Flower> copy = copyListService.copyListByStreamWithFilter(flowers, 5);
        Assert.assertNotEquals(copy.size(), flowers.size());
        Assert.assertEquals(((copy.size()) + 3), flowers.size());
        Assert.assertFalse(copy.containsAll(flowers));
    }

    @Test
    public void givenAList_whenListIsNull_thenReturnEmptyListByStreamProcess() {
        List<Flower> copy = copyListService.copyListByStreamWithOptional(null);
        Assert.assertNotNull(copy);
        Assert.assertEquals(copy.size(), 0);
    }

    @Test
    public void givenAList_whenListIsNotNull_thenReturnAnotherListWithTheElementsByStreamProcess() {
        List<Flower> copy = copyListService.copyListByStreamWithOptional(flowers);
        Assert.assertEquals(copy.size(), flowers.size());
        Assert.assertTrue(copy.containsAll(flowers));
    }

    @Test
    public void givenAList_whenListIsNotNull_thenReturnAnotherListWithOneElementLessByStreamProcess() {
        List<Flower> copy = copyListService.copyListByStreamWithOptionalAndSkip(flowers);
        Assert.assertNotEquals(copy.size(), flowers.size());
        Assert.assertEquals(((copy.size()) + 1), flowers.size());
        Assert.assertFalse(copy.containsAll(flowers));
    }
}

