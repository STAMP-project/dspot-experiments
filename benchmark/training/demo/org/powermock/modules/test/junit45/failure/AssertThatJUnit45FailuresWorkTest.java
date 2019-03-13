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
package demo.org.powermock.modules.test.junit45.failure;


import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * This test asserts that JUnit 4.5 failures works as expected. Previously the
 * {@link PowerMockJUnit44RunnerDelegateImpl} got a {@link NoClassDefFoundError}
 * when trying to load JUnit 4.4's {@link AssumptionViolatedException} which has
 * been moved in JUnit 4.5. Thanks to Manuel Fern?ndez S?nchez de la Blanca for
 * creating this test case to prove the issue.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MyUtils.class)
public class AssertThatJUnit45FailuresWorkTest {
    @Test(expected = AssumptionViolatedException.class)
    public void testAssumptionViolatedException() throws MyException {
        throw new AssumptionViolatedException("Not true!");
    }

    @Test(expected = MyException.class)
    public void testSum() throws MyException {
        PowerMock.mockStatic(MyUtils.class);
        EasyMock.expect(MyUtils.isValid(1)).andReturn(true);
        PowerMock.replay(MyUtils.class);
        MyClass myclass = new MyClass();
        int result = myclass.sum(1, 2);
        PowerMock.verify(MyUtils.class);
        Assert.assertTrue((result == 3));
    }

    @Test(expected = MyException.class)
    public void testSum2() throws MyException {
        MyClass myclass = new MyClass();
        myclass.sum(100, 2);
    }
}

