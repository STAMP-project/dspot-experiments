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
package samples.junit4.privateandfinal;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.privateandfinal.PrivateFinal;


/**
 * Test class to demonstrate private+final method mocking.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PrivateFinal.class)
public class PrivateFinalTest {
    @Test
    public void testMockPrivatAndFinal() throws Exception {
        PrivateFinal tested = PowerMock.createPartialMock(PrivateFinal.class, "sayIt");
        String expected = "Hello altered World";
        PowerMock.expectPrivate(tested, "sayIt", "name").andReturn(expected);
        replay(tested);
        String actual = tested.say("name");
        verify(tested);
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void testMultiplePartialMocksOfSameType() throws Exception {
        PrivateFinal tested1 = PowerMock.createPartialMock(PrivateFinal.class, "sayIt");
        String expected1 = "Hello altered World";
        PowerMock.expectPrivate(tested1, "sayIt", "name").andReturn(expected1);
        replay(tested1);
        PrivateFinal tested2 = PowerMock.createPartialMock(PrivateFinal.class, "sayIt");
        String expected2 = "Hello qweqweqwe";
        PowerMock.expectPrivate(tested2, "sayIt", "name").andReturn(expected2);
        replay(tested2);
        String actual1 = tested1.say("name");
        verify(tested1);
        Assert.assertEquals("Expected and actual did not match", expected1, actual1);
        String actual2 = tested2.say("name");
        verify(tested2);
        Assert.assertEquals("Expected and actual did not match", expected2, actual2);
    }
}

