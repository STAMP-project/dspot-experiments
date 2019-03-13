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
package samples.junit4.constructor;


import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.reflect.exceptions.ConstructorNotFoundException;
import samples.Service;
import samples.constructor.PublicConstructorWithDependencyDemo;


/**
 * Verifies that error messages are correct when the constructor cannot be found
 * with partial mocking. This test asserts that the
 * http://code.google.com/p/powertest/issues/detail?id=59 has been fixed.
 */
public class PublicConstructorWithDependencyDemoTest {
    private Service serviceMock;

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConstructorFound() throws Exception {
        PublicConstructorWithDependencyDemo tested = PowerMock.createPartialMock(PublicConstructorWithDependencyDemo.class, new String[]{ "aMethod" }, serviceMock);
        Assert.assertSame(serviceMock, tested.getService());
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConstructorNotFound() throws Exception {
        try {
            PowerMock.createPartialMock(PublicConstructorWithDependencyDemo.class, new String[]{ "aMethod" }, serviceMock, "bad argument");
            Assert.fail("Should throw ConstructorNotFoundException.");
        } catch (ConstructorNotFoundException e) {
            Assert.assertEquals((((((("No constructor found in class '" + (PublicConstructorWithDependencyDemo.class.getName())) + "' with parameter types: [ ") + (Service.class.getName())) + ", ") + (String.class.getName())) + " ]."), e.getMessage());
        }
    }
}

