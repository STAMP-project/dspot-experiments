package org.baeldung.mockito;


import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class MockitoConfigExamplesIntegrationTest {
    // tests
    @Test
    public final void whenMockReturnBehaviorIsConfigured_thenBehaviorIsVerified() {
        final MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.add(ArgumentMatchers.anyString())).thenReturn(false);
        final boolean added = listMock.add(RandomStringUtils.randomAlphabetic(6));
        Assert.assertThat(added, Matchers.is(false));
    }

    @Test
    public final void whenMockReturnBehaviorIsConfigured2_thenBehaviorIsVerified() {
        final MyList listMock = Mockito.mock(MyList.class);
        Mockito.doReturn(false).when(listMock).add(ArgumentMatchers.anyString());
        final boolean added = listMock.add(RandomStringUtils.randomAlphabetic(6));
        Assert.assertThat(added, Matchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public final void givenMethodIsConfiguredToThrowException_whenCallingMethod_thenExceptionIsThrown() {
        final MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.add(ArgumentMatchers.anyString())).thenThrow(IllegalStateException.class);
        listMock.add(RandomStringUtils.randomAlphabetic(6));
    }

    @Test(expected = NullPointerException.class)
    public final void whenMethodHasNoReturnType_whenConfiguringBehaviorOfMethod_thenPossible() {
        final MyList listMock = Mockito.mock(MyList.class);
        Mockito.doThrow(NullPointerException.class).when(listMock).clear();
        listMock.clear();
    }

    @Test
    public final void givenBehaviorIsConfiguredToThrowExceptionOnSecondCall_whenCallingOnlyOnce_thenNoExceptionIsThrown() {
        final MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.add(ArgumentMatchers.anyString())).thenReturn(false).thenThrow(IllegalStateException.class);
        listMock.add(RandomStringUtils.randomAlphabetic(6));
    }

    @Test(expected = IllegalStateException.class)
    public final void givenBehaviorIsConfiguredToThrowExceptionOnSecondCall_whenCallingTwice_thenExceptionIsThrown() {
        final MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.add(ArgumentMatchers.anyString())).thenReturn(false).thenThrow(IllegalStateException.class);
        listMock.add(RandomStringUtils.randomAlphabetic(6));
        listMock.add(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    public final void whenMockMethodCallIsConfiguredToCallTheRealMethod_thenRealMethodIsCalled() {
        final MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.size()).thenCallRealMethod();
        Assert.assertThat(listMock.size(), Matchers.equalTo(1));
    }

    @Test
    public final void whenMockMethodCallIsConfiguredWithCustomAnswer_thenRealMethodIsCalled() {
        final MyList listMock = Mockito.mock(MyList.class);
        Mockito.doAnswer(( invocation) -> "Always the same").when(listMock).get(ArgumentMatchers.anyInt());
        final String element = listMock.get(1);
        Assert.assertThat(element, Matchers.is(Matchers.equalTo("Always the same")));
    }

    @Test(expected = NullPointerException.class)
    public final void givenSpy_whenConfiguringBehaviorOfSpy_thenCorrectlyConfigured() {
        final MyList instance = new MyList();
        final MyList spy = Mockito.spy(instance);
        Mockito.doThrow(NullPointerException.class).when(spy).size();
        spy.size();
    }
}

