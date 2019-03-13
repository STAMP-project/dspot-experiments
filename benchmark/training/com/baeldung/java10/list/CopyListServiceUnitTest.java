package com.baeldung.java10.list;


import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class CopyListServiceUnitTest {
    @Test(expected = UnsupportedOperationException.class)
    public void whenModifyCopyOfList_thenThrowsException() {
        List<Integer> copyList = List.copyOf(List, Arrays.asList(1, 2, 3, 4));
    }
}

