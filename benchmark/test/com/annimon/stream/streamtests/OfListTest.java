package com.annimon.stream.streamtests;


import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class OfListTest {
    @Test
    public void testStreamOfList() {
        final List<String> list = new ArrayList<String>(4);
        list.add("This");
        list.add(" is ");
        list.add("a");
        list.add(" test");
        String result = Stream.of(list).collect(Collectors.joining());
        Assert.assertThat(result, Matchers.is("This is a test"));
    }
}

