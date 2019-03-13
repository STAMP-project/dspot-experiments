/**
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package powermock.modules.test.mockito.junit4.delegate.parameterized;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import samples.finalmocking.FinalDemo;
import samples.privateandfinal.PrivateFinal;


/**
 * Test class to demonstrate non-static final mocking with Mockito.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({ FinalDemo.class, PrivateFinal.class })
public class FinalDemoTest {
    @Parameterized.Parameter(0)
    public String expected;

    @Test
    public void assertMockFinalWithNoExpectationsWorks() throws Exception {
        final String argument = "hello";
        FinalDemo tested = mock(FinalDemo.class);
        Assert.assertNull(tested.say(argument));
        Mockito.verify(tested).say(argument);
    }

    @Test
    public void assertMockFinalWithExpectationsWorks() throws Exception {
        final String argument = "hello";
        FinalDemo tested = mock(FinalDemo.class);
        when(tested.say(argument)).thenReturn(expected);
        final String actual = "" + (tested.say(argument));
        Mockito.verify(tested).say(argument);
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void assertFinalNativeWithExpectationsWorks() throws Exception {
        final String argument = "hello";
        FinalDemo tested = mock(FinalDemo.class);
        when(tested.sayFinalNative(argument)).thenReturn(expected);
        String actual = "" + (tested.sayFinalNative(argument));
        Mockito.verify(tested).sayFinalNative(argument);
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void assertSpyingOnFinalInstanceMethodWorks() throws Exception {
        FinalDemo tested = new FinalDemo();
        FinalDemo spy = spy(tested);
        final String argument = "PowerMock";
        Assert.assertEquals(("Hello " + argument), spy.say(argument));
        when(spy.say(argument)).thenReturn(expected);
        Assert.assertEquals(expected, ("" + (spy.say(argument))));
    }

    @Test(expected = ArrayStoreException.class)
    public void assertSpyingOnFinalVoidInstanceMethodWorks() throws Exception {
        FinalDemo tested = new FinalDemo();
        FinalDemo spy = spy(tested);
        doThrow(new ArrayStoreException()).when(spy).finalVoidCallee();
        spy.finalVoidCaller();
    }

    @Test
    public void assertSpyingOnPrivateFinalInstanceMethodWorks() throws Exception {
        PrivateFinal spy = spy(new PrivateFinal());
        Assert.assertEquals(("Hello " + (expected)), spy.say(expected));
        when(spy, "sayIt", ArgumentMatchers.isA(String.class)).thenReturn(expected);
        Assert.assertEquals(expected, ("" + (spy.say(expected))));
        verifyPrivate(spy, Mockito.times(2)).invoke("sayIt", expected);
    }

    @Test
    public void assertSpyingOnPrivateFinalInstanceMethodWorksWhenUsingJavaLangReflectMethod() throws Exception {
        PrivateFinal spy = spy(new PrivateFinal());
        Assert.assertEquals(("Hello " + (expected)), spy.say(expected));
        final Method methodToExpect = method(PrivateFinal.class, "sayIt");
        when(spy, methodToExpect).withArguments(ArgumentMatchers.isA(String.class)).thenReturn(expected);
        Assert.assertEquals(expected, ("" + (spy.say(expected))));
        verifyPrivate(spy, Mockito.times(2)).invoke(methodToExpect).withArguments(expected);
    }
}

