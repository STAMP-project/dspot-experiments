package com.baeldung.list;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class AddElementsUnitTest {
    List<Flower> flowers;

    @Test
    public void givenAList_whenTargetListIsEmpty_thenReturnTargetListWithNewItems() {
        List<Flower> anotherList = new ArrayList<>();
        anotherList.addAll(flowers);
        Assert.assertEquals(anotherList.size(), flowers.size());
        Assert.assertTrue(anotherList.containsAll(flowers));
    }

    @Test
    public void givenAList_whenTargetListIsEmpty_thenReturnTargetListWithOneModifiedElementByConstructor() {
        List<Flower> anotherList = new ArrayList<>();
        anotherList.addAll(flowers);
        Flower flower = anotherList.get(0);
        flower.setPetals(((flowers.get(0).getPetals()) * 3));
        Assert.assertEquals(anotherList.size(), flowers.size());
        Assert.assertTrue(anotherList.containsAll(flowers));
    }

    @Test
    public void givenAListAndElements_whenUseCollectionsAddAll_thenAddElementsToTargetList() {
        List<Flower> target = new ArrayList<>();
        Collections.addAll(target, flowers.get(0), flowers.get(1), flowers.get(2), flowers.get(0));
        Assert.assertEquals(target.size(), 4);
    }

    @Test
    public void givenTwoList_whenSourceListDoesNotHaveNullElements_thenAddElementsToTargetListSkipFirstElementByStreamProcess() {
        List<Flower> flowerVase = new ArrayList<>();
        flowers.stream().skip(1).forEachOrdered(flowerVase::add);
        Assert.assertEquals(((flowerVase.size()) + 1), flowers.size());
        Assert.assertFalse(flowerVase.containsAll(flowers));
    }

    @Test
    public void givenTwoList_whenSourceListDoesNotHaveNullElements_thenAddElementsToTargetListFilteringElementsByStreamProcess() {
        List<Flower> flowerVase = new ArrayList<>();
        flowers.stream().filter(( f) -> (f.getPetals()) > 10).forEachOrdered(flowerVase::add);
        Assert.assertEquals(((flowerVase.size()) + 1), flowers.size());
        Assert.assertFalse(flowerVase.containsAll(flowers));
    }

    @Test
    public void givenAList_whenListIsNotNull_thenAddElementsToListByStreamProcessWihtOptional() {
        List<Flower> target = new ArrayList<>();
        Optional.ofNullable(flowers).ifPresent(target::addAll);
        Assert.assertNotNull(target);
        Assert.assertEquals(target.size(), 3);
    }
}

