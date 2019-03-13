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
package samples.junit4.partialmocking;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.partialmocking.MockSelfDemoWithSubClass;


@RunWith(PowerMockRunner.class)
@PrepareForTest(MockSelfDemoWithSubClass.class)
public class MockSelfDemoWithSubClassTest {
    @Test
    public void testMockPartialMethodInChildClass() throws Exception {
        MockSelfDemoWithSubClass tested = createPartialMock(MockSelfDemoWithSubClass.class, "getAMessage", "getInternalMessage");
        final String getAMessageMock = "Hello ";
        final String getInternalMessageMock = "World!";
        final String expected = getInternalMessageMock + getAMessageMock;
        expect(tested.getAMessage()).andReturn(getAMessageMock);
        expectPrivate(tested, "getInternalMessage").andReturn(getInternalMessageMock);
        replay(tested);
        String actual = tested.getMessage();
        verify(tested);
        Assert.assertEquals(expected, actual);
    }
}

