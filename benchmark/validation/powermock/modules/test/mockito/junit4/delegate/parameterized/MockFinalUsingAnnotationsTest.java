/**
 * Copyright 2014 the original author or authors.
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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import samples.finalmocking.FinalDemo;


/**
 * Test class to demonstrate non-static final mocking with Mockito and PowerMock
 * annotations.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest(FinalDemo.class)
public class MockFinalUsingAnnotationsTest {
    @Mock
    private FinalDemo usingMockitoMockAnnotation;

    @Mock
    private FinalDemo usingDeprecatedMockitoMockAnnotation;

    @Parameterized.Parameter(0)
    public MockFinalUsingAnnotationsTest.MockField field2test;

    @Test
    public void testMockFinal() throws Exception {
        final String argument = "hello";
        System.out.println(field2test);
        FinalDemo mockedFinal = field2test.inTest(this);
        Assert.assertNull(mockedFinal.say(argument));
        Mockito.verify(mockedFinal).say(argument);
    }

    enum MockField {

        usingMockitoMockAnnotation,
        usingDeprecatedMockitoMockAnnotation;
        <T> T inTest(Object test) {
            try {
                return ((T) (Whitebox.getInternalState(test, name())));
            } catch (Exception ex) {
                throw new Error(ex);
            }
        }
    }
}

