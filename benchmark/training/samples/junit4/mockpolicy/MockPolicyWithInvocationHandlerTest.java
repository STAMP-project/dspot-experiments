/**
 * Copyright 2009 the original author or authors.
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
package samples.junit4.mockpolicy;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import samples.mockpolicy.ResultCalculator;
import samples.mockpolicy.SimpleClassWithADependency;


@RunWith(PowerMockRunner.class)
@MockPolicy(MockPolicyInvocationHandlerExample.class)
public class MockPolicyWithInvocationHandlerTest {
    @Test
    public void mockPolicyWithInvocationHandlerWorks() {
        final SimpleClassWithADependency tested = new SimpleClassWithADependency();
        Whitebox.setInternalState(tested, new ResultCalculator(0));
        Assert.assertEquals(1.0, tested.getResult(), 0.0);
        PowerMock.verifyAll();
    }
}

