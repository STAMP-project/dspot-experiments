/**
 * Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package samples.junit4.largemethod;


import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.largemethod.MethodExceedingJvmLimit;


@RunWith(PowerMockRunner.class)
@PrepareForTest(MethodExceedingJvmLimit.class)
public class LargeMethodTest {
    @Test
    public void largeMethodShouldBeOverridden() {
        try {
            MethodExceedingJvmLimit.init();
            Assert.fail("Method should be overridden and exception should be thrown");
        } catch (Exception e) {
            Assert.assertSame(IllegalAccessException.class, e.getClass());
            Assert.assertTrue(e.getMessage().contains("Method was too large and after instrumentation exceeded JVM limit"));
        }
    }

    @Test
    public void largeMethodShouldBeAbleToBeSuppressed() {
        PowerMock.suppress(PowerMock.method(MethodExceedingJvmLimit.class, "init"));
        Assert.assertNull("Suppressed method should return: null", MethodExceedingJvmLimit.init());
    }

    @Test
    public void largeMethodShouldBeAbleToBeMocked() {
        PowerMock.mockStatic(MethodExceedingJvmLimit.class);
        expect(MethodExceedingJvmLimit.init()).andReturn("ok");
        PowerMock.replayAll();
        Assert.assertEquals("Mocked method should return: ok", "ok", MethodExceedingJvmLimit.init());
    }

    @Test(expected = IllegalStateException.class)
    public void largeMethodShouldBeAbleToBeMockedAndThrowException() {
        PowerMock.mockStatic(MethodExceedingJvmLimit.class);
        expect(MethodExceedingJvmLimit.init()).andThrow(new IllegalStateException());
        PowerMock.replayAll();
        MethodExceedingJvmLimit.init();
    }
}

