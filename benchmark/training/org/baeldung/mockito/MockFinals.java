package org.baeldung.mockito;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MockFinals {
    @Test
    public void whenMockFinalClassMockWorks() {
        FinalList finalList = new FinalList();
        FinalList mock = Mockito.mock(FinalList.class);
        Mockito.when(mock.size()).thenReturn(2);
        Assert.assertNotEquals(mock.size(), finalList.size());
    }

    @Test
    public void whenMockFinalMethodMockWorks() {
        MyList myList = new MyList();
        MyList mock = Mockito.mock(MyList.class);
        Mockito.when(mock.finalMethod()).thenReturn(1);
        Assert.assertNotEquals(mock.finalMethod(), myList.finalMethod());
    }
}

