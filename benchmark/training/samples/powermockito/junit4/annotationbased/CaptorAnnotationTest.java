/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samples.powermockito.junit4.annotationbased;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.finalmocking.FinalDemo;
import samples.privateandfinal.PrivateFinal;
import samples.privateandfinal.PrivateFinalOverload;


/**
 * Asserts that {@link Captor} with PowerMock.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FinalDemo.class, PrivateFinal.class, PrivateFinalOverload.class })
public class CaptorAnnotationTest {
    @Captor
    private ArgumentCaptor<String> captor;

    @Test
    public void captorAnnotationWorks() throws Exception {
        final String expected = "testing";
        FinalDemo demo = mock(FinalDemo.class);
        demo.say(expected);
        Mockito.verify(demo).say(captor.capture());
        Assert.assertEquals(expected, captor.getValue());
    }

    @Test
    public void captorAnnotationWorksOnPrivateMethods() throws Exception {
        final String expected = "testing";
        PrivateFinal demo = spy(new PrivateFinal());
        demo.say(expected);
        verifyPrivate(demo).invoke("sayIt", captor.capture());
        Assert.assertEquals(expected, captor.getValue());
    }

    @Test
    public void captorAnnotationWorksOnPrivateOverriddenMethods() throws Exception {
        final String expected = "testing";
        PrivateFinalOverload demo = spy(new PrivateFinalOverload());
        demo.say(expected);
        verifyPrivate(demo).invoke(method(PrivateFinalOverload.class, "say", String.class, String.class)).withArguments(ArgumentMatchers.anyString(), captor.capture());
        Assert.assertEquals(expected, captor.getValue());
    }
}

