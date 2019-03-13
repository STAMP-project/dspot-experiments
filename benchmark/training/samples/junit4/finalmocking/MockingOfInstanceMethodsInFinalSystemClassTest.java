/**
 * Copyright 2009 the original author or authors.
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
package samples.junit4.finalmocking;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Test class to demonstrate non-static final mocking of instance methods in
 * system classes.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MockingOfInstanceMethodsInFinalSystemClassTest.class)
public class MockingOfInstanceMethodsInFinalSystemClassTest {
    @Test
    public void assertThatMockingOfInstanceMethodsInFinalSystemClassesWorks() throws Exception {
        Long tested = createMock(Long.class);
        expect(tested.longValue()).andReturn(22L);
        replayAll();
        Assert.assertEquals(22L, tested.longValue());
        verifyAll();
    }

    @Test
    public void assertThatMockingOfInstanceMethodsInStringWorks() throws Exception {
        String tested = createMock(String.class);
        expect(tested.charAt(2)).andReturn('A');
        replayAll();
        Assert.assertEquals('A', tested.charAt(2));
        verifyAll();
    }

    @Test
    public void assertThatPartialMockingOfInstanceMethodsInFinalSystemClassesWhenNotInvokingConstructorWorks() throws Exception {
        Long tested = createPartialMock(Long.class, "doubleValue");
        expect(tested.doubleValue()).andReturn(54.0);
        replayAll();
        Assert.assertEquals(0, tested.longValue());
        Assert.assertEquals(54.0, tested.doubleValue(), 0.0);
        verifyAll();
    }

    @Test
    public void assertThatPartialMockingOfInstanceMethodsInFinalSystemClassesWhenNotInvokingNonDefaultConstructorWorks() throws Exception {
        Long tested = createPartialMock(Long.class, new String[]{ "doubleValue" }, 27L);
        expect(tested.doubleValue()).andReturn(54.0);
        replayAll();
        Assert.assertEquals(27L, tested.longValue());
        Assert.assertEquals(54.0, tested.doubleValue(), 0.0);
        verifyAll();
    }
}

