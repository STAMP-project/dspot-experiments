package com.baeldung.guava;


import com.google.common.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TypeTokenUnitTest {
    @Test
    public void whenCheckingIsAssignableFrom_shouldReturnTrueEvenIfGenericIsSpecified() throws Exception {
        ArrayList<String> stringList = new ArrayList<>();
        ArrayList<Integer> intList = new ArrayList<>();
        boolean isAssignableFrom = stringList.getClass().isAssignableFrom(intList.getClass());
        Assert.assertTrue(isAssignableFrom);
    }

    @Test
    public void whenCheckingIsSupertypeOf_shouldReturnFalseIfGenericIsSpecified() throws Exception {
        TypeToken<ArrayList<String>> listString = new TypeToken<ArrayList<String>>() {};
        TypeToken<ArrayList<Integer>> integerString = new TypeToken<ArrayList<Integer>>() {};
        boolean isSupertypeOf = listString.isSupertypeOf(integerString);
        Assert.assertFalse(isSupertypeOf);
    }

    @Test
    public void whenCheckingIsSubtypeOf_shouldReturnTrueIfClassIsExtendedFrom() throws Exception {
        TypeToken<ArrayList<String>> stringList = new TypeToken<ArrayList<String>>() {};
        TypeToken<List> list = new TypeToken<List>() {};
        boolean isSubtypeOf = stringList.isSubtypeOf(list);
        Assert.assertTrue(isSubtypeOf);
    }
}

