package com.baeldung.stringduplicates;


import org.junit.Assert;
import org.junit.Test;


public class RemoveDuplicateFromStringUnitTest {
    private static final String STR1 = "racecar";

    private static final String STR2 = "J2ee programming";

    private static final String STR_EMPTY = "";

    private RemoveDuplicateFromString removeDuplicateFromString;

    @Test
    public void whenUsingCharArray_DuplicatesShouldBeRemovedWithoutKeepingStringOrder() {
        String str1 = removeDuplicateFromString.removeDuplicatesUsingCharArray(RemoveDuplicateFromStringUnitTest.STR1);
        String str2 = removeDuplicateFromString.removeDuplicatesUsingCharArray(RemoveDuplicateFromStringUnitTest.STR2);
        String strEmpty = removeDuplicateFromString.removeDuplicatesUsingCharArray(RemoveDuplicateFromStringUnitTest.STR_EMPTY);
        Assert.assertEquals("", strEmpty);
        Assert.assertEquals("ecar", str1);
        Assert.assertEquals("J2e poraming", str2);
    }

    @Test
    public void whenUsingLinkedHashSet_DuplicatesShouldBeRemovedAndItKeepStringOrder() {
        String str1 = removeDuplicateFromString.removeDuplicatesUsinglinkedHashSet(RemoveDuplicateFromStringUnitTest.STR1);
        String str2 = removeDuplicateFromString.removeDuplicatesUsinglinkedHashSet(RemoveDuplicateFromStringUnitTest.STR2);
        String strEmpty = removeDuplicateFromString.removeDuplicatesUsinglinkedHashSet(RemoveDuplicateFromStringUnitTest.STR_EMPTY);
        Assert.assertEquals("", strEmpty);
        Assert.assertEquals("race", str1);
        Assert.assertEquals("J2e progamin", str2);
    }

    @Test
    public void whenUsingSorting_DuplicatesShouldBeRemovedWithoutKeepingStringOrder() {
        String str1 = removeDuplicateFromString.removeDuplicatesUsingSorting(RemoveDuplicateFromStringUnitTest.STR1);
        String str2 = removeDuplicateFromString.removeDuplicatesUsingSorting(RemoveDuplicateFromStringUnitTest.STR2);
        String strEmpty = removeDuplicateFromString.removeDuplicatesUsingSorting(RemoveDuplicateFromStringUnitTest.STR_EMPTY);
        Assert.assertEquals("", strEmpty);
        Assert.assertEquals("acer", str1);
        Assert.assertEquals(" 2Jaegimnopr", str2);
    }

    @Test
    public void whenUsingHashSet_DuplicatesShouldBeRemovedWithoutKeepingStringOrder() {
        String str1 = removeDuplicateFromString.removeDuplicatesUsingHashSet(RemoveDuplicateFromStringUnitTest.STR1);
        String str2 = removeDuplicateFromString.removeDuplicatesUsingHashSet(RemoveDuplicateFromStringUnitTest.STR2);
        String strEmpty = removeDuplicateFromString.removeDuplicatesUsingHashSet(RemoveDuplicateFromStringUnitTest.STR_EMPTY);
        Assert.assertEquals("", strEmpty);
        Assert.assertEquals("arce", str1);
        Assert.assertEquals(" pa2regiJmno", str2);
    }

    @Test
    public void whenUsingIndexOf_DuplicatesShouldBeRemovedWithoutKeepingStringOrder() {
        String str1 = removeDuplicateFromString.removeDuplicatesUsingIndexOf(RemoveDuplicateFromStringUnitTest.STR1);
        String str2 = removeDuplicateFromString.removeDuplicatesUsingIndexOf(RemoveDuplicateFromStringUnitTest.STR2);
        String strEmpty = removeDuplicateFromString.removeDuplicatesUsingIndexOf(RemoveDuplicateFromStringUnitTest.STR_EMPTY);
        Assert.assertEquals("", strEmpty);
        Assert.assertEquals("ecar", str1);
        Assert.assertEquals("J2e poraming", str2);
    }

    @Test
    public void whenUsingJava8_DuplicatesShouldBeRemovedAndItKeepStringOrder() {
        String str1 = removeDuplicateFromString.removeDuplicatesUsingDistinct(RemoveDuplicateFromStringUnitTest.STR1);
        String str2 = removeDuplicateFromString.removeDuplicatesUsingDistinct(RemoveDuplicateFromStringUnitTest.STR2);
        String strEmpty = removeDuplicateFromString.removeDuplicatesUsingDistinct(RemoveDuplicateFromStringUnitTest.STR_EMPTY);
        Assert.assertEquals("", strEmpty);
        Assert.assertEquals("race", str1);
        Assert.assertEquals("J2e progamin", str2);
    }
}

