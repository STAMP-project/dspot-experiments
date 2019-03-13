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
import samples.constructor.PrivateConstructorInstantiationDemo;


public class PrivateConstructorInstantiationDemoTest {
    @Test
    public void testGetState_noArgConstructor() throws Exception {
        final int expected = 42;
        PrivateConstructorInstantiationDemo tested = invokeConstructor(PrivateConstructorInstantiationDemo.class);
        int actual = tested.getState();
        Assert.assertEquals("Expected and actual did not match.", expected, actual);
    }

    @Test
    public void testGetState_intConstructor() throws Exception {
        final int expected = 12;
        PrivateConstructorInstantiationDemo tested = invokeConstructor(PrivateConstructorInstantiationDemo.class, expected);
        int actual = tested.getState();
        Assert.assertEquals("Expected and actual did not match.", expected, actual);
    }
}

