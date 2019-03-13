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
import samples.suppressconstructor.SuppressConstructorDemo;
import samples.suppressconstructor.SuppressConstructorSubclassDemo;


/**
 * This test demonstrates how to tell PowerMock to avoid executing constructor
 * code for a certain class. This is crucial in certain tests where the
 * constructor or a subclass's constructor performs operations that are of no
 * concern to the unit test of the actual class or if the constructor performs
 * operations, such as getting services from a runtime environment that has not
 * been initialized. In normal situations you're forced to create an integration
 * or function test for the class instead (and thus the runtime environment is
 * available). This is not particularly good when it comes to testing method
 * logic. PowerMock solves these problems by letting you specify the
 * {@link PowerMock#suppressConstructor(Class...)} method
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressConstructorDemo.class)
public class SuppressConstructorDemoTest {
    /**
     * This test makes sure that the real constructor has never been called.
     */
    @Test
    public void testSuppressConstructor() throws Exception {
        MemberModifier.suppress(MemberMatcher.constructor(SuppressConstructorDemo.class));
        final SuppressConstructorDemo tested = new SuppressConstructorDemo("a message");
        Assert.assertNull("Message should have been null since we're skipping the execution of the constructor code.", tested.getMessage());
    }

    /**
     * This test makes sure that the real parent constructor has never been
     * called.
     */
    @Test
    public void testSuppressParentConstructor() throws Exception {
        MemberModifier.suppress(MemberMatcher.constructor(SuppressConstructorSubclassDemo.class));
        final SuppressConstructorDemo tested = new SuppressConstructorDemo("a message");
        Assert.assertNull("Message should have been null since we're skipping the execution of the constructor code.", tested.getMessage());
    }

    /**
     * This test makes sure that it's possible to also mock methods from the
     * class under test at the same time as skipping constructor execution.
     */
    @Test
    public void testPartialMockingAndSuppressParentConstructor() throws Exception {
        MemberModifier.suppress(MemberMatcher.constructor(SuppressConstructorSubclassDemo.class));
        final SuppressConstructorDemo tested = createPartialMock(SuppressConstructorDemo.class, "returnAMessage");
        final String expected = "Hello world!";
        expectPrivate(tested, "returnAMessage").andReturn(expected);
        replay(tested);
        final String actual = tested.getMyOwnMessage();
        verify(tested);
        Assert.assertEquals(expected, actual);
        Assert.assertNull("Message should have been null since we're skipping the execution of the constructor code.", tested.getMessage());
    }
}

