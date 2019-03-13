package com.baeldung.properties;


import java.util.Properties;
import org.junit.Test;


public class MergePropertiesUnitTest {
    @Test
    public void givenTwoProperties_whenMergedUsingIteration_thenAllPropertiesInResult() {
        Properties globalProperties = mergePropertiesByIteratingKeySet(propertiesA(), propertiesB());
        testMergedProperties(globalProperties);
    }

    @Test
    public void givenTwoProperties_whenMergedUsingPutAll_thenAllPropertiesInResult() {
        Properties globalProperties = mergePropertiesByUsingPutAll(propertiesA(), propertiesB());
        testMergedProperties(globalProperties);
    }

    @Test
    public void givenTwoProperties_whenMergedUsingStreamAPI_thenAllPropertiesInResult() {
        Properties globalProperties = mergePropertiesByUsingStreamApi(propertiesB(), propertiesA());
        testMergedProperties(globalProperties);
    }
}

