package com.baeldung.list.removeall;


import java.util.ConcurrentModificationException;
import java.util.List;
import org.junit.Test;


public class RemoveAllUnitTest {
    @Test
    public void givenAList_whenRemovingElementsWithWhileLoopUsingPrimitiveElement_thenTheResultCorrect() {
        // given
        List<Integer> list = list(1, 2, 3);
        int valueToRemove = 1;
        // when
        assertThatThrownBy(() -> removeWithWhileLoopPrimitiveElement(list, valueToRemove)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void givenAList_whenRemovingElementsWithWhileLoopUsingNonPrimitiveElement_thenTheResultCorrect() {
        // given
        List<Integer> list = list(1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithWhileLoopNonPrimitiveElement(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithWhileLoopStoringFirstOccurrenceIndex_thenTheResultCorrect() {
        // given
        List<Integer> list = list(1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithWhileLoopStoringFirstOccurrenceIndex(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithCallingRemoveUntilModifies_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithCallingRemoveUntilModifies(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAListWithoutDuplication_whenRemovingElementsWithStandardForLoopUsingIndex_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithStandardForLoopUsingIndex(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAListWithAdjacentElements_whenRemovingElementsWithStandardForLoop_thenTheResultIsInCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithStandardForLoopUsingIndex(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(1, 2, 3));
    }

    @Test
    public void givenAListWithAdjacentElements_whenRemovingElementsWithForLoopAndDecrementOnRemove_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithForLoopDecrementOnRemove(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAListWithAdjacentElements_whenRemovingElementsWithForLoopAndIncrementIfRemains_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithForLoopIncrementIfRemains(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithForEachLoop_thenExceptionIsThrown() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        assertThatThrownBy(() -> removeWithForEachLoop(list, valueToRemove)).isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    public void givenAList_whenRemovingElementsWithIterator_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithIterator(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithCollectingAndReturningRemainingElements_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        List<Integer> result = RemoveAll.removeWithCollectingAndReturningRemainingElements(list, valueToRemove);
        // then
        assertThat(result).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithCollectingRemainingAndAddingToOriginalList_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithCollectingRemainingElementsAndAddingToOriginalList(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithStreamFilter_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        List<Integer> result = RemoveAll.removeWithStreamFilter(list, valueToRemove);
        // then
        assertThat(result).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithCallingRemoveIf_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;
        // when
        RemoveAll.removeWithRemoveIf(list, valueToRemove);
        // then
        assertThat(list).isEqualTo(list(2, 3));
    }
}

