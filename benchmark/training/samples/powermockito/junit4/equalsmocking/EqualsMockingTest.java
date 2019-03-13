/**
 * Copyright 2010 the original author or authors.
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
package samples.powermockito.junit4.equalsmocking;


import javax.naming.InitialContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(InitialContext.class)
public class EqualsMockingTest {
    @Test
    public void shouldStubEquals() throws Exception {
        stub(method(InitialContext.class, "equals")).toReturn(true);
        final InitialContext context = new InitialContext();
        Assert.assertTrue(context.equals(new Object()));
    }

    @Test
    public void shouldMockEquals() throws Exception {
        Object object = new Object();
        final InitialContext context = mock(InitialContext.class);
        when(context.equals(object)).thenReturn(true);
        Assert.assertTrue(context.equals(object));
    }
}

