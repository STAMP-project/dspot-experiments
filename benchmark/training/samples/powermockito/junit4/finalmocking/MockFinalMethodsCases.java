package samples.powermockito.junit4.finalmocking;


import java.lang.reflect.Method;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import samples.finalmocking.FinalDemo;
import samples.privateandfinal.PrivateFinal;


/**
 * Cases to check mocking final non-static methods and final class
 */
public class MockFinalMethodsCases {
    @Test
    public void shouldMockFinalMethodWithNoExpectations() throws Exception {
        final String argument = "hello";
        FinalDemo tested = mock(FinalDemo.class);
        assertThat(tested.say(argument)).isNull();
        Mockito.verify(tested).say(argument);
    }

    @Test
    public void shouldMockFinalMethodWithExpectations() throws Exception {
        final String argument = "hello";
        final String expected = "Hello altered World";
        FinalDemo tested = mock(FinalDemo.class);
        when(tested.say(argument)).thenReturn(expected);
        final String actual = tested.say(argument);
        Mockito.verify(tested).say(argument);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMockFinalNativeMethodWithExpectations() throws Exception {
        final String expected = "Hello altered World";
        final String argument = "hello";
        FinalDemo tested = mock(FinalDemo.class);
        when(tested.sayFinalNative(argument)).thenReturn("Hello altered World");
        String actual = tested.sayFinalNative(argument);
        Mockito.verify(tested).sayFinalNative(argument);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldSpyingOnFinalInstanceMethod() throws Exception {
        FinalDemo tested = new FinalDemo();
        FinalDemo spy = spy(tested);
        final String argument = "PowerMock";
        final String expected = "something";
        assertThat(spy.say(argument)).isEqualTo(("Hello " + argument));
        when(spy.say(argument)).thenReturn(expected);
        assertThat(spy.say(argument)).isEqualTo(expected);
    }

    @Test(expected = ArrayStoreException.class)
    public void shouldSpyingOnFinalVoidInstanceMethod() throws Exception {
        FinalDemo tested = new FinalDemo();
        FinalDemo spy = spy(tested);
        doThrow(new ArrayStoreException()).when(spy).finalVoidCallee();
        spy.finalVoidCaller();
    }

    @Test
    public void shouldSpyingOnPrivateFinalInstanceMethod() throws Exception {
        PrivateFinal spy = spy(new PrivateFinal());
        final String expected = "test";
        assertThat(spy.say(expected)).isEqualTo(("Hello " + expected));
        when(spy, "sayIt", ArgumentMatchers.isA(String.class)).thenReturn(expected);
        assertThat(spy.say(expected)).isEqualTo(expected);
        verifyPrivate(spy, Mockito.times(2)).invoke("sayIt", expected);
    }

    @Test
    public void shouldSpyingOnPrivateFinalInstanceMethodWhenUsingJavaLangReflectMethod() throws Exception {
        PrivateFinal spy = spy(new PrivateFinal());
        final String expected = "test";
        assertThat(spy.say(expected)).isEqualTo(("Hello " + expected));
        final Method methodToExpect = method(PrivateFinal.class, "sayIt");
        when(spy, methodToExpect).withArguments(ArgumentMatchers.isA(String.class)).thenReturn(expected);
        assertThat(spy.say(expected)).isEqualTo(expected);
        verifyPrivate(spy, Mockito.times(2)).invoke(methodToExpect).withArguments(expected);
    }
}

