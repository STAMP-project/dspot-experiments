package org.baeldung.mockito;


import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.TooLittleActualInvocations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class MockitoMockIntegrationTest {
    private static class CustomAnswer implements Answer<Boolean> {
        @Override
        public Boolean answer(InvocationOnMock invocation) throws Throwable {
            return false;
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void whenUsingSimpleMock_thenCorrect() {
        MyList listMock = Mockito.mock(MyList.class);
        Mockito.when(listMock.add(ArgumentMatchers.anyString())).thenReturn(false);
        boolean added = listMock.add(RandomStringUtils.randomAlphabetic(6));
        Mockito.verify(listMock).add(ArgumentMatchers.anyString());
        Assert.assertThat(added, Matchers.is(false));
    }

    @Test
    public void whenUsingMockWithName_thenCorrect() {
        MyList listMock = Mockito.mock(MyList.class, "myMock");
        Mockito.when(listMock.add(ArgumentMatchers.anyString())).thenReturn(false);
        listMock.add(RandomStringUtils.randomAlphabetic(6));
        thrown.expect(TooLittleActualInvocations.class);
        thrown.expectMessage(Matchers.containsString("myMock.add"));
        Mockito.verify(listMock, Mockito.times(2)).add(ArgumentMatchers.anyString());
    }

    @Test
    public void whenUsingMockWithAnswer_thenCorrect() {
        MyList listMock = Mockito.mock(MyList.class, new MockitoMockIntegrationTest.CustomAnswer());
        boolean added = listMock.add(RandomStringUtils.randomAlphabetic(6));
        Mockito.verify(listMock).add(ArgumentMatchers.anyString());
        Assert.assertThat(added, Matchers.is(false));
    }

    @Test
    public void whenUsingMockWithSettings_thenCorrect() {
        MockSettings customSettings = Mockito.withSettings().defaultAnswer(new MockitoMockIntegrationTest.CustomAnswer());
        MyList listMock = Mockito.mock(MyList.class, customSettings);
        boolean added = listMock.add(RandomStringUtils.randomAlphabetic(6));
        Mockito.verify(listMock).add(ArgumentMatchers.anyString());
        Assert.assertThat(added, Matchers.is(false));
    }
}

