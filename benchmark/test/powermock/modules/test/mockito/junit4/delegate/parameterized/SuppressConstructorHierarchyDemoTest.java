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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import samples.suppressconstructor.InvokeConstructor;
import samples.suppressconstructor.SuppressConstructorHierarchy;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest(SuppressConstructorHierarchy.class)
public class SuppressConstructorHierarchyDemoTest {
    @Parameterized.Parameter
    public boolean suppress;

    @Test
    public void directConstructorUsage() throws Exception {
        System.out.println(("ClassLoader: " + (getClass().getClassLoader())));
        try {
            SuppressConstructorHierarchy tested = new SuppressConstructorHierarchy("message");
            if (suppress) {
                Assert.assertNull("Message should have been null since we\'re skipping the execution of the constructor code. Message was \"message\".", tested.getMessage());
                Assert.assertEquals("getNumber() value", 42, tested.getNumber());
            } else {
                Assert.fail("Expected RuntimeException");
            }
        } catch (RuntimeException ex) {
            if (suppress) {
                throw ex;
            } else {
                Assert.assertEquals("This should be suppressed!!", ex.getMessage());
            }
        }
    }

    @Test
    public void useConstructorInvoker() throws Exception {
        System.out.println(("ClassLoader: " + (getClass().getClassLoader())));
        try {
            final String message = new InvokeConstructor().doStuff("qwe");
            if (suppress) {
                Assert.assertNull((("Message should have been null since we\'re skipping the execution of the constructor code. Message was \"" + message) + "\"."), message);
            } else {
                Assert.fail("Expected RuntimeException");
            }
        } catch (RuntimeException ex) {
            if (suppress) {
                throw ex;
            } else {
                Assert.assertEquals("This should be suppressed!!", ex.getMessage());
            }
        }
    }

    @Test
    @PrepareForTest
    public void suppressWithoutByteCodeManipulation() throws Exception {
        System.out.println(("ClassLoader: " + (getClass().getClassLoader())));
        try {
            new InvokeConstructor().doStuff("qwe");
            Assert.fail("Should throw RuntimeException since we're running this test with a new class loader!");
        } catch (RuntimeException ex) {
            Assert.assertEquals("This should be suppressed!!", ex.getMessage());
        }
    }
}

