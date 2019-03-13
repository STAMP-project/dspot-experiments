/**
 * Copyright 2008 the original author or authors.
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
package samples.powermockito.junit4.simplemix;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.testlisteners.FieldDefaulter;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import samples.simplemix.SimpleMix;
import samples.simplemix.SimpleMixCollaborator;
import samples.simplemix.SimpleMixConstruction;
import samples.simplemix.SimpleMixUtilities;


/**
 * Demonstrates PowerMockito features such as static mocking, final mocking,
 * partial mocking and setting internal state. It also demonstrates
 * test-chunking and final system class mocking.
 */
@RunWith(PowerMockRunner.class)
@PowerMockListener(FieldDefaulter.class)
public class SimpleMixTest {
    @PrepareForTest({ SimpleMixUtilities.class, SimpleMixCollaborator.class, SimpleMix.class })
    @Test
    public void staticPartialFinalMocking() throws Exception {
        SimpleMix tested = spy(new SimpleMix());
        when(tested, "getValue").thenReturn(0);
        SimpleMixCollaborator simpleMixCollaboratorMock = mock(SimpleMixCollaborator.class);
        mockStatic(SimpleMixUtilities.class);
        SimpleMixConstruction simpleMixConstructionMock = mock(SimpleMixConstruction.class);
        Whitebox.setInternalState(tested, simpleMixCollaboratorMock);
        when(SimpleMixUtilities.getRandomInteger()).thenReturn(10);
        when(simpleMixCollaboratorMock.getRandomInteger()).thenReturn(6);
        whenNew(SimpleMixConstruction.class).withNoArguments().thenReturn(simpleMixConstructionMock);
        when(simpleMixConstructionMock.getMyValue()).thenReturn(1);
        Assert.assertEquals(4, tested.calculate());
        verifyStatic(SimpleMixUtilities.class);
        SimpleMixUtilities.getRandomInteger();
        verifyNew(SimpleMixConstruction.class).withNoArguments();
        verifyPrivate(tested).invoke(method(SimpleMix.class, "getValue")).withNoArguments();
    }

    @PrepareForTest({ SimpleMix.class })
    @Test
    public void finalSystemClassMocking() throws Exception {
        SimpleMix tested = new SimpleMix();
        mockStatic(System.class);
        when(System.currentTimeMillis()).thenReturn(2000L);
        Assert.assertEquals(2, Whitebox.invokeMethod(tested, "getValue"));
    }
}

