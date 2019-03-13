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
package samples.junit4.suppressconstructor;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.suppressconstructor.InvokeConstructor;
import samples.suppressconstructor.SuppressConstructorHierarchy;


@PrepareForTest(SuppressConstructorHierarchy.class)
@RunWith(PowerMockRunner.class)
public class SuppressConstructorHierarchyDemoTest {
    @Test
    public void testSuppressConstructorHierarchy() throws Exception {
        MemberModifier.suppress(MemberMatcher.constructor(SuppressConstructorHierarchy.class));
        final String message = new InvokeConstructor().doStuff("qwe");
        Assert.assertNull((("Message should have been null since we\'re skipping the execution of the constructor code. Message was \"" + message) + "\"."), message);
    }

    @Test
    @PrepareForTest
    public void testNotSuppressConstructorWithoutByteCodeManipulation() throws Exception {
        try {
            new SuppressConstructorHierarchy("message");
            Assert.fail("Should throw RuntimeException since we're running this test with a new class loader!");
        } catch (RuntimeException e) {
            Assert.assertEquals("This should be suppressed!!", e.getMessage());
        }
    }

    @Test
    public void testNotSuppressConstructorWithByteCodeManipulation() throws Exception {
        try {
            new SuppressConstructorHierarchy("message");
            Assert.fail("Should throw RuntimeException since we're running this test with a new class loader!");
        } catch (RuntimeException e) {
            Assert.assertEquals("This should be suppressed!!", e.getMessage());
        }
    }

    /**
     * This simple test demonstrate that it's possible to continue execution
     * with the default {@code PrepareForTest} settings (i.e. using a
     * byte-code manipulated version of the SuppressConstructorHierarchyDemo
     * class).
     */
    @Test
    public void testSuppressConstructorHierarchyAgain() throws Exception {
        MemberModifier.suppress(MemberMatcher.constructor(SuppressConstructorHierarchy.class));
        SuppressConstructorHierarchy tested = new SuppressConstructorHierarchy("message");
        Assert.assertEquals(42, tested.getNumber());
    }
}

