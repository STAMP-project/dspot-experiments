package com.baeldung.allequalelements;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class VerifyAllEqualListElementsUnitTest {
    private static List<String> notAllEqualList = new ArrayList<>();

    private static List<String> emptyList = new ArrayList<>();

    private static List<String> allEqualList = new ArrayList<>();

    static {
        VerifyAllEqualListElementsUnitTest.notAllEqualList = Arrays.asList("Jack", "James", "Sam", "James");
        VerifyAllEqualListElementsUnitTest.emptyList = Arrays.asList();
        VerifyAllEqualListElementsUnitTest.allEqualList = Arrays.asList("Jack", "Jack", "Jack", "Jack");
    }

    private static VerifyAllEqualListElements verifyAllEqualListElements = new VerifyAllEqualListElements();

    @Test
    public void givenNotAllEqualList_whenUsingALoop_thenReturnFalse() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingALoop(VerifyAllEqualListElementsUnitTest.notAllEqualList);
        Assert.assertFalse(allEqual);
    }

    @Test
    public void givenEmptyList_whenUsingALoop_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingALoop(VerifyAllEqualListElementsUnitTest.emptyList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenAllEqualList_whenUsingALoop_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingALoop(VerifyAllEqualListElementsUnitTest.allEqualList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenNotAllEqualList_whenUsingHashSet_thenReturnFalse() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingHashSet(VerifyAllEqualListElementsUnitTest.notAllEqualList);
        Assert.assertFalse(allEqual);
    }

    @Test
    public void givenEmptyList_whenUsingHashSet_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingHashSet(VerifyAllEqualListElementsUnitTest.emptyList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenAllEqualList_whenUsingHashSet_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingHashSet(VerifyAllEqualListElementsUnitTest.allEqualList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenNotAllEqualList_whenUsingFrequency_thenReturnFalse() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingFrequency(VerifyAllEqualListElementsUnitTest.notAllEqualList);
        Assert.assertFalse(allEqual);
    }

    @Test
    public void givenEmptyList_whenUsingFrequency_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingFrequency(VerifyAllEqualListElementsUnitTest.emptyList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenAllEqualList_whenUsingFrequency_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingFrequency(VerifyAllEqualListElementsUnitTest.allEqualList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenNotAllEqualList_whenUsingStream_thenReturnFalse() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingStream(VerifyAllEqualListElementsUnitTest.notAllEqualList);
        Assert.assertFalse(allEqual);
    }

    @Test
    public void givenEmptyList_whenUsingStream_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingStream(VerifyAllEqualListElementsUnitTest.emptyList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenAllEqualList_whenUsingStream_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingStream(VerifyAllEqualListElementsUnitTest.allEqualList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenNotAllEqualList_whenUsingAnotherStream_thenReturnFalse() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualAnotherUsingStream(VerifyAllEqualListElementsUnitTest.notAllEqualList);
        Assert.assertFalse(allEqual);
    }

    @Test
    public void givenEmptyList_whenUsingAnotherStream_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualAnotherUsingStream(VerifyAllEqualListElementsUnitTest.emptyList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenAllEqualList_whenUsingAnotherStream_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualAnotherUsingStream(VerifyAllEqualListElementsUnitTest.allEqualList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenNotAllEqualList_whenUsingGuava_thenReturnFalse() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingGuava(VerifyAllEqualListElementsUnitTest.notAllEqualList);
        Assert.assertFalse(allEqual);
    }

    @Test
    public void givenEmptyList_whenUsingGuava_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingGuava(VerifyAllEqualListElementsUnitTest.emptyList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenAllEqualList_whenUsingGuava_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingGuava(VerifyAllEqualListElementsUnitTest.allEqualList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenNotAllEqualList_whenUsingApacheCommon_thenReturnFalse() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingApacheCommon(VerifyAllEqualListElementsUnitTest.notAllEqualList);
        Assert.assertFalse(allEqual);
    }

    @Test
    public void givenEmptyList_whenUsingApacheCommon_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingApacheCommon(VerifyAllEqualListElementsUnitTest.emptyList);
        Assertions.assertTrue(allEqual);
    }

    @Test
    public void givenAllEqualList_whenUsingApacheCommon_thenReturnTrue() {
        boolean allEqual = VerifyAllEqualListElementsUnitTest.verifyAllEqualListElements.verifyAllEqualUsingApacheCommon(VerifyAllEqualListElementsUnitTest.allEqualList);
        Assertions.assertTrue(allEqual);
    }
}

