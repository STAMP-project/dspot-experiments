package com.baeldung.generics;


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class GenericsUnitTest {
    // testing the generic method with Integer
    @Test
    public void givenArrayOfIntegers_thanListOfIntegersReturnedOK() {
        Integer[] intArray = new Integer[]{ 1, 2, 3, 4, 5 };
        List<Integer> list = Generics.fromArrayToList(intArray);
        MatcherAssert.assertThat(list, CoreMatchers.hasItems(intArray));
    }

    // testing the generic method with Integer and String type
    @Test
    public void givenArrayOfIntegers_thanListOfStringReturnedOK() {
        Integer[] intArray = new Integer[]{ 1, 2, 3, 4, 5 };
        List<String> stringList = Generics.fromArrayToList(intArray, Object::toString);
        MatcherAssert.assertThat(stringList, CoreMatchers.hasItems("1", "2", "3", "4", "5"));
    }

    // testing the generic method with String
    @Test
    public void givenArrayOfStrings_thanListOfStringsReturnedOK() {
        String[] stringArray = new String[]{ "hello1", "hello2", "hello3", "hello4", "hello5" };
        List<String> list = Generics.fromArrayToList(stringArray);
        MatcherAssert.assertThat(list, CoreMatchers.hasItems(stringArray));
    }

    // testing the generic method with Number as upper bound with Integer
    // if we test fromArrayToListWithUpperBound with any type that doesn't
    // extend Number it will fail to compile
    @Test
    public void givenArrayOfIntegersAndNumberUpperBound_thanListOfIntegersReturnedOK() {
        Integer[] intArray = new Integer[]{ 1, 2, 3, 4, 5 };
        List<Integer> list = Generics.fromArrayToListWithUpperBound(intArray);
        MatcherAssert.assertThat(list, CoreMatchers.hasItems(intArray));
    }

    // testing paintAllBuildings method with a subtype of Building, the method
    // will work with all subtypes of Building
    @Test
    public void givenSubTypeOfWildCardBoundedGenericType_thanPaintingOK() {
        try {
            List<Building> subBuildingsList = new ArrayList<>();
            subBuildingsList.add(new Building());
            subBuildingsList.add(new House());
            // prints
            // Painting Building
            // Painting House
            Generics.paintAllBuildings(subBuildingsList);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}

