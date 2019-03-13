package com.baeldung.list.removefirst;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class RemoveFirstElementUnitTest {
    private List<String> list = new ArrayList<>();

    private LinkedList<String> linkedList = new LinkedList<>();

    @Test
    public void givenList_whenRemoveFirst_thenRemoved() {
        list.remove(0);
        MatcherAssert.assertThat(list, hasSize(4));
        MatcherAssert.assertThat(list, not(contains("cat")));
    }

    @Test
    public void givenLinkedList_whenRemoveFirst_thenRemoved() {
        linkedList.removeFirst();
        MatcherAssert.assertThat(linkedList, hasSize(4));
        MatcherAssert.assertThat(linkedList, not(contains("cat")));
    }

    @Test
    public void givenStringArray_whenRemovingFirstElement_thenArrayIsSmallerAndElementRemoved() {
        String[] stringArray = new String[]{ "foo", "bar", "baz" };
        String[] modifiedArray = Arrays.copyOfRange(stringArray, 1, stringArray.length);
        MatcherAssert.assertThat(modifiedArray.length, is(2));
        MatcherAssert.assertThat(modifiedArray[0], is("bar"));
    }
}

