package com.baeldung.typeinference;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class TypeInferenceUnitTest {
    @Test
    public void givenNoTypeInference_whenInvokingGenericMethodsWithTypeParameters_ObjectsAreCreated() {
        // Without type inference. code is verbose.
        Map<String, Map<String, String>> mapOfMaps = new HashMap<String, Map<String, String>>();
        List<String> strList = Collections.<String>emptyList();
        List<Integer> intList = Collections.<Integer>emptyList();
        Assert.assertTrue(mapOfMaps.isEmpty());
        Assert.assertTrue(strList.isEmpty());
        Assert.assertTrue(intList.isEmpty());
    }

    @Test
    public void givenTypeInference_whenInvokingGenericMethodsWithoutTypeParameters_ObjectsAreCreated() {
        // With type inference. code is concise.
        List<String> strListInferred = Collections.emptyList();
        List<Integer> intListInferred = Collections.emptyList();
        Assert.assertTrue(strListInferred.isEmpty());
        Assert.assertTrue(intListInferred.isEmpty());
    }

    @Test
    public void givenJava7_whenInvokingCostructorWithoutTypeParameters_ObjectsAreCreated() {
        // Type Inference for constructor using diamond operator.
        Map<String, Map<String, String>> mapOfMapsInferred = new HashMap<>();
        Assert.assertTrue(mapOfMapsInferred.isEmpty());
        Assertions.assertEquals("public class java.util.HashMap<K,V>", mapOfMapsInferred.getClass().toGenericString());
    }

    @Test
    public void givenGenericMethod_WhenInvokedWithoutExplicitTypes_TypesAreInferred() {
        // Generalized target-type inference
        List<String> strListGeneralized = TypeInferenceUnitTest.add(new ArrayList<>(), "abc", "def");
        List<Integer> intListGeneralized = TypeInferenceUnitTest.add(new ArrayList<>(), 1, 2);
        List<Number> numListGeneralized = TypeInferenceUnitTest.add(new ArrayList<>(), 1, 2.0);
        Assertions.assertEquals("public class java.util.ArrayList<E>", strListGeneralized.getClass().toGenericString());
        Assert.assertFalse(intListGeneralized.isEmpty());
        Assertions.assertEquals(2, numListGeneralized.size());
    }

    @Test
    public void givenLambdaExpressions_whenParameterTypesNotSpecified_ParameterTypesAreInferred() {
        // Type Inference and Lambda Expressions.
        List<Integer> intList = Arrays.asList(5, 3, 4, 2, 1);
        Collections.sort(intList, ( a, b) -> {
            Assertions.assertEquals("java.lang.Integer", a.getClass().getName());
            return a.compareTo(b);
        });
        Assertions.assertEquals("[1, 2, 3, 4, 5]", Arrays.toString(intList.toArray()));
        List<String> strList = Arrays.asList("Red", "Blue", "Green");
        Collections.sort(strList, ( a, b) -> {
            Assertions.assertEquals("java.lang.String", a.getClass().getName());
            return a.compareTo(b);
        });
        Assertions.assertEquals("[Blue, Green, Red]", Arrays.toString(strList.toArray()));
    }
}

