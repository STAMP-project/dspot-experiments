package org.baeldung.mockito;


import com.google.common.collect.Lists;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NoInteractionsWanted;


public class MockitoVerifyExamplesIntegrationTest {
    // tests
    @Test
    public final void givenInteractionWithMockOccurred_whenVerifyingInteraction_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.size();
        Mockito.verify(mockedList).size();
    }

    @Test
    public final void givenOneInteractionWithMockOccurred_whenVerifyingNumberOfInteractions_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.size();
        Mockito.verify(mockedList, Mockito.times(1)).size();
    }

    @Test
    public final void givenNoInteractionWithMockOccurred_whenVerifyingInteractions_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        Mockito.verifyZeroInteractions(mockedList);
    }

    @Test
    public final void givenNoInteractionWithMethodOfMockOccurred_whenVerifyingInteractions_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        Mockito.verify(mockedList, Mockito.times(0)).size();
    }

    @Test(expected = NoInteractionsWanted.class)
    public final void givenUnverifiedInteraction_whenVerifyingNoUnexpectedInteractions_thenFail() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.size();
        mockedList.clear();
        Mockito.verify(mockedList).size();
        Mockito.verifyNoMoreInteractions(mockedList);
    }

    @Test
    public final void whenVerifyingOrderOfInteractions_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.size();
        mockedList.add("a parameter");
        mockedList.clear();
        final InOrder inOrder = Mockito.inOrder(mockedList);
        inOrder.verify(mockedList).size();
        inOrder.verify(mockedList).add("a parameter");
        inOrder.verify(mockedList).clear();
    }

    @Test
    public final void whenVerifyingAnInteractionHasNotOccurred_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.size();
        Mockito.verify(mockedList, Mockito.never()).clear();
    }

    @Test
    public final void whenVerifyingAnInteractionHasOccurredAtLeastOnce_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.clear();
        mockedList.clear();
        mockedList.clear();
        Mockito.verify(mockedList, Mockito.atLeast(1)).clear();
        Mockito.verify(mockedList, Mockito.atMost(10)).clear();
    }

    // with arguments
    @Test
    public final void whenVerifyingAnInteractionWithExactArgument_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.add("test");
        Mockito.verify(mockedList).add("test");
    }

    @Test
    public final void whenVerifyingAnInteractionWithAnyArgument_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.add("test");
        Mockito.verify(mockedList).add(ArgumentMatchers.anyString());
    }

    @Test
    public final void whenVerifyingAnInteractionWithArgumentCapture_thenCorrect() {
        final List<String> mockedList = Mockito.mock(MyList.class);
        mockedList.addAll(Lists.<String>newArrayList("someElement"));
        final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mockedList).addAll(argumentCaptor.capture());
        final List<String> capturedArgument = argumentCaptor.<List<String>>getValue();
        Assert.assertThat(capturedArgument, Matchers.hasItem("someElement"));
    }
}

