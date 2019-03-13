package com.baeldung.stream;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class StreamApiUnitTest {
    @Test
    public void givenList_whenGetLastElementUsingReduce_thenReturnLastElement() {
        List<String> valueList = new ArrayList<>();
        valueList.add("Joe");
        valueList.add("John");
        valueList.add("Sean");
        String last = StreamApi.getLastElementUsingReduce(valueList);
        Assert.assertEquals("Sean", last);
    }

    @Test
    public void givenInfiniteStream_whenGetInfiniteStreamLastElementUsingReduce_thenReturnLastElement() {
        int last = StreamApi.getInfiniteStreamLastElementUsingReduce();
        Assert.assertEquals(19, last);
    }

    @Test
    public void givenListAndCount_whenGetLastElementUsingSkip_thenReturnLastElement() {
        List<String> valueList = new ArrayList<>();
        valueList.add("Joe");
        valueList.add("John");
        valueList.add("Sean");
        String last = StreamApi.getLastElementUsingSkip(valueList);
        Assert.assertEquals("Sean", last);
    }
}

