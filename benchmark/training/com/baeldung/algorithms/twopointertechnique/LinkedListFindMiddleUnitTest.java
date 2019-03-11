package com.baeldung.algorithms.twopointertechnique;


import org.junit.Test;


public class LinkedListFindMiddleUnitTest {
    LinkedListFindMiddle linkedListFindMiddle = new LinkedListFindMiddle();

    @Test
    public void givenLinkedListOfMyNodes_whenLinkedListFindMiddle_thenCorrect() {
        MyNode<String> head = LinkedListFindMiddleUnitTest.createNodesList(8);
        assertThat(linkedListFindMiddle.findMiddle(head)).isEqualTo("4");
        head = LinkedListFindMiddleUnitTest.createNodesList(9);
        assertThat(linkedListFindMiddle.findMiddle(head)).isEqualTo("5");
    }
}

