/**
 * Copyright 2011 the original author or authors.
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
package samples.powermockito.junit4.agent;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.suppressconstructor.SuppressConstructorHierarchy;


@PrepareForTest(SuppressConstructorHierarchy.class)
public class SuppressConstructorHierarchyDemoTest {
    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

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
        suppress(constructor(SuppressConstructorHierarchy.class));
        SuppressConstructorHierarchy tested = new SuppressConstructorHierarchy("message");
        Assert.assertEquals(42, tested.getNumber());
    }
}

