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
package samples.junit4.staticandinstance;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.staticandinstance.StaticAndInstanceDemo;
import samples.staticandinstance.StaticAndInstanceWithConstructorCodeDemo;


/**
 * The purpose of this test class is to verify that the
 * http://code.google.com/p/powermock/issues/detail?id=4 is fixed.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticAndInstanceDemo.class)
public class StaticAndInstanceWithConstructorCodeDemoTest {
    @Test
    public void testStaticAndInstanceWithConstructor() throws Exception {
        StaticAndInstanceDemo staticAndInstanceDemoMock = createMock(StaticAndInstanceDemo.class);
        StaticAndInstanceWithConstructorCodeDemo tested = new StaticAndInstanceWithConstructorCodeDemo(staticAndInstanceDemoMock);
        niceReplayAndVerify();
        mockStaticPartial(StaticAndInstanceDemo.class, "getStaticMessage");
        final String instanceExpected = "value";
        expect(staticAndInstanceDemoMock.getMessage()).andReturn(instanceExpected);
        final String staticExpected = "a static message";
        expect(StaticAndInstanceDemo.getStaticMessage()).andReturn(staticExpected);
        replay(StaticAndInstanceDemo.class, staticAndInstanceDemoMock, tested);
        String actual = tested.getMessage();
        verify(StaticAndInstanceDemo.class, staticAndInstanceDemoMock, tested);
        Assert.assertEquals((staticExpected + instanceExpected), actual);
    }
}

