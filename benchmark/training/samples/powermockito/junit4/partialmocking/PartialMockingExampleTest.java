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
package samples.powermockito.junit4.partialmocking;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.partialmocking.PartialMockingExample;


/**
 * Asserts that partial mocking (spying) with PowerMockito works for non-final
 * methods.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PartialMockingExample.class)
public class PartialMockingExampleTest {
    @Test
    public void validatingSpiedObjectGivesCorrectNumberOfExpectedInvocations() throws Exception {
        final String expected = "TEST VALUE";
        PartialMockingExample underTest = spy(new PartialMockingExample());
        doReturn(expected).when(underTest).methodToMock();
        Assert.assertEquals(expected, underTest.methodToTest());
        Mockito.verify(underTest).methodToTest();
        Mockito.verify(underTest).methodToMock();
    }

    @Test
    public void validatingSpiedObjectGivesCorrectNumberOfExpectedInvocationsForMockito() throws Exception {
        final String expected = "TEST VALUE";
        PartialMockingExample underTest = Mockito.spy(new PartialMockingExample());
        doReturn(expected).when(underTest).methodToMock();
        Assert.assertEquals(expected, underTest.methodToTest());
        Mockito.verify(underTest).methodToTest();
        Mockito.verify(underTest).methodToMock();
    }

    @Test
    public void should_verify_spied_object_used_in_other_threads() {
        final String expected = "TEST VALUE";
        final PartialMockingExample underTest = spy(new PartialMockingExample());
        doReturn(expected).when(underTest).methodToMock();
        final int threadCounts = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCounts);
        final List<String> values = new CopyOnWriteArrayList<String>();
        for (int i = 0; i < threadCounts; i++) {
            createAndStartThread(i, underTest, startLatch, endLatch, values);
        }
        startLatch.countDown();
        awaitThreads(endLatch);
        assertThat(values).as("All threads have called method and get expected result").hasSize(threadCounts).containsOnly(expected);
        Mockito.verify(underTest, Mockito.times(threadCounts)).methodToTest();
        Mockito.verify(underTest, Mockito.times(threadCounts)).methodToMock();
    }
}

