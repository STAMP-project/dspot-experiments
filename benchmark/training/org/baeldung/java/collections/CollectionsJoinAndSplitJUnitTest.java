package org.baeldung.java.collections;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class CollectionsJoinAndSplitJUnitTest {
    private ArrayList<String> sauces = new ArrayList<>();

    private ArrayList<String> cheeses = new ArrayList<>();

    private ArrayList<String> vegetables = new ArrayList<>();

    private ArrayList<ArrayList<String>> ingredients = new ArrayList<>();

    @Test
    public void givenThreeArrayLists_whenJoiningIntoOneArrayList_shouldSucceed() {
        ArrayList<ArrayList<String>> toppings = new ArrayList<>();
        toppings.add(sauces);
        toppings.add(cheeses);
        toppings.add(vegetables);
        Assert.assertTrue(((toppings.size()) == 3));
        Assert.assertTrue(toppings.contains(sauces));
        Assert.assertTrue(toppings.contains(cheeses));
        Assert.assertTrue(toppings.contains(vegetables));
    }

    @Test
    public void givenOneArrayList_whenSplittingIntoTwoArrayLists_shouldSucceed() {
        ArrayList<ArrayList<String>> removedToppings = new ArrayList<>();
        removedToppings.add(ingredients.remove(ingredients.indexOf(vegetables)));
        Assert.assertTrue(removedToppings.contains(vegetables));
        Assert.assertTrue(((removedToppings.size()) == 1));
        Assert.assertTrue(((ingredients.size()) == 2));
        Assert.assertTrue(ingredients.contains(sauces));
        Assert.assertTrue(ingredients.contains(cheeses));
    }
}

