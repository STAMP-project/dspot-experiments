package org.baeldung.mockito;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MockitoExceptionIntegrationTest {
    @Test(expected = NullPointerException.class)
    public void whenConfigNonVoidRetunMethodToThrowEx_thenExIsThrown() {
        MyDictionary dictMock = Mockito.mock(MyDictionary.class);
        Mockito.when(dictMock.getMeaning(ArgumentMatchers.anyString())).thenThrow(NullPointerException.class);
        dictMock.getMeaning("word");
    }

    @Test(expected = IllegalStateException.class)
    public void whenConfigVoidRetunMethodToThrowEx_thenExIsThrown() {
        MyDictionary dictMock = Mockito.mock(MyDictionary.class);
        Mockito.doThrow(IllegalStateException.class).when(dictMock).add(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        dictMock.add("word", "meaning");
    }

    @Test(expected = NullPointerException.class)
    public void whenConfigNonVoidRetunMethodToThrowExWithNewExObj_thenExIsThrown() {
        MyDictionary dictMock = Mockito.mock(MyDictionary.class);
        Mockito.when(dictMock.getMeaning(ArgumentMatchers.anyString())).thenThrow(new NullPointerException("Error occurred"));
        dictMock.getMeaning("word");
    }

    @Test(expected = IllegalStateException.class)
    public void whenConfigVoidRetunMethodToThrowExWithNewExObj_thenExIsThrown() {
        MyDictionary dictMock = Mockito.mock(MyDictionary.class);
        Mockito.doThrow(new IllegalStateException("Error occurred")).when(dictMock).add(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        dictMock.add("word", "meaning");
    }

    // =====
    @Test(expected = NullPointerException.class)
    public void givenSpy_whenConfigNonVoidRetunMethodToThrowEx_thenExIsThrown() {
        MyDictionary dict = new MyDictionary();
        MyDictionary spy = Mockito.spy(dict);
        Mockito.when(spy.getMeaning(ArgumentMatchers.anyString())).thenThrow(NullPointerException.class);
        spy.getMeaning("word");
    }
}

