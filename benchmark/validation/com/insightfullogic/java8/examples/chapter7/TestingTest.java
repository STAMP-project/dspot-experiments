package com.insightfullogic.java8.examples.chapter7;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestingTest {
    // BEGIN to_uppercase
    @Test
    public void multipleWordsToUppercase() {
        List<String> input = Arrays.asList("a", "b", "hello");
        List<String> result = Testing.allToUpperCase(input);
        Assert.assertEquals(Arrays.asList("A", "B", "HELLO"), result);
    }

    // END to_uppercase
    // BEGIN twoLetterStringConvertedToUppercaseLambdas
    @Test
    public void twoLetterStringConvertedToUppercaseLambdas() {
        List<String> input = Arrays.asList("ab");
        List<String> result = Testing.elementFirstToUpperCaseLambdas(input);
        Assert.assertEquals(Arrays.asList("Ab"), result);
    }

    // END twoLetterStringConvertedToUppercaseLambdas
    // BEGIN twoLetterStringConvertedToUppercase
    @Test
    public void twoLetterStringConvertedToUppercase() {
        String input = "ab";
        String result = Testing.firstToUppercase(input);
        Assert.assertEquals("Ab", result);
    }

    // END twoLetterStringConvertedToUppercase
    private List<Integer> otherList = Arrays.asList(1, 2, 3);

    @Test
    public void mockitoLambdas() {
        // BEGIN mockito_lambdas
        List<String> list = Mockito.mock(List.class);
        Mockito.when(list.size()).thenAnswer(( inv) -> otherList.size());
        Assert.assertEquals(3, list.size());
        // END mockito_lambdas
    }
}

