package org.baeldung.mockito;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class MockitoVoidMethodsUnitTest {
    @Test
    public void whenAddCalledVerified() {
        MyList mockVoid = Mockito.mock(MyList.class);
        mockVoid.add(0, "");
        Mockito.verify(mockVoid, Mockito.times(1)).add(0, "");
    }

    @Test(expected = Exception.class)
    public void givenNull_addThrows() {
        MyList mockVoid = Mockito.mock(MyList.class);
        Mockito.doThrow().when(mockVoid).add(ArgumentMatchers.isA(Integer.class), ArgumentMatchers.isNull());
        mockVoid.add(0, null);
    }

    @Test
    public void whenAddCalledValueCaptured() {
        MyList mockVoid = Mockito.mock(MyList.class);
        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        Mockito.doNothing().when(mockVoid).add(ArgumentMatchers.any(Integer.class), valueCapture.capture());
        mockVoid.add(0, "captured");
        Assert.assertEquals("captured", valueCapture.getValue());
    }

    @Test
    public void whenAddCalledAnswered() {
        MyList mockVoid = Mockito.mock(MyList.class);
        Mockito.doAnswer(((Answer<Void>) (( invocation) -> {
            Object arg0 = invocation.getArgument(0);
            Object arg1 = invocation.getArgument(1);
            // do something with the arguments here
            Assert.assertEquals(3, arg0);
            Assert.assertEquals("answer me", arg1);
            return null;
        }))).when(mockVoid).add(ArgumentMatchers.any(Integer.class), ArgumentMatchers.any(String.class));
        mockVoid.add(3, "answer me");
    }

    @Test
    public void whenAddCalledRealMethodCalled() {
        MyList mockVoid = Mockito.mock(MyList.class);
        Mockito.doCallRealMethod().when(mockVoid).add(ArgumentMatchers.any(Integer.class), ArgumentMatchers.any(String.class));
        mockVoid.add(1, "real");
        Mockito.verify(mockVoid, Mockito.times(1)).add(1, "real");
    }
}

