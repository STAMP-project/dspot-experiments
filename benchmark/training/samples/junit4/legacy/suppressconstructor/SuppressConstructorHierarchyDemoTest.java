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
package samples.junit4.legacy.suppressconstructor;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.legacy.PowerMockRunner;
import samples.suppressconstructor.SuppressConstructorHeirarchyEvilGrandParent;
import samples.suppressconstructor.SuppressConstructorHierarchy;
import samples.suppressconstructor.SuppressConstructorHierarchyParent;


@PrepareForTest({ SuppressConstructorHierarchy.class, SuppressConstructorHierarchyParent.class, SuppressConstructorHeirarchyEvilGrandParent.class })
@RunWith(PowerMockRunner.class)
public class SuppressConstructorHierarchyDemoTest {
    @Test
    public void testSuppressConstructor() throws Exception {
        suppress(constructor(SuppressConstructorHierarchy.class));
        SuppressConstructorHierarchy tested = new SuppressConstructorHierarchy("message");
        final String message = tested.getMessage();
        Assert.assertNull((("Message should have been null since we\'re skipping the execution of the constructor code. Message was \"" + message) + "\"."), message);
    }

    @Test
    @PrepareForTest
    public void testNotSuppressConstructor() throws Exception {
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
    public void testGetNumber() throws Exception {
        suppress(constructor(SuppressConstructorHierarchy.class));
        SuppressConstructorHierarchy tested = new SuppressConstructorHierarchy("message");
        Assert.assertThat(tested.getNumber()).isEqualTo(42);
    }
}

