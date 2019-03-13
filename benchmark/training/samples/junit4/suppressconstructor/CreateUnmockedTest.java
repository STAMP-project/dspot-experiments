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
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import samples.suppressconstructor.SuppressConstructorDemo;
import samples.suppressconstructor.SuppressSpecificConstructorDemo;


@RunWith(PowerMockRunner.class)
public class CreateUnmockedTest {
    @Test
    public void testUnmockedWithNoConstructorAndReplayVerify() throws Exception {
        SuppressSpecificConstructorDemo object = Whitebox.newInstance(SuppressSpecificConstructorDemo.class);
        PowerMock.niceReplayAndVerify();
        PowerMock.replay(object);
        Assert.assertEquals("Hello", object.getHello());
        PowerMock.verify(object);
    }

    @Test
    public void testUnmockedWithConstructorAndAllowReplay() throws Exception {
        PowerMock.niceReplayAndVerify();
        SuppressConstructorDemo object = new SuppressConstructorDemo("Hello");
        PowerMock.replay(object);
        Assert.assertEquals("Hello", object.getMessage());
        PowerMock.verify(object);
    }

    @Test
    public void testUnmockedWithReplayCausesException() throws Exception {
        SuppressConstructorDemo object = new SuppressConstructorDemo("Hello");
        try {
            PowerMock.replay(object);
            Assert.fail("Replay should only work on mocks");
        } catch (RuntimeException e) {
            // ignore
        }
    }
}

