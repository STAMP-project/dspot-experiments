/**
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.util;


import io.grpc.LoadBalancer.Helper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Unit test for {@link RoundRobinLoadBalancerFactory}.
 */
@RunWith(JUnit4.class)
public class RoundRobinLoadBalancerFactoryTest {
    @SuppressWarnings("deprecation")
    @Test
    public void getInstance() {
        Helper helper = Mockito.mock(Helper.class);
        assertThat(RoundRobinLoadBalancerFactory.getInstance().newLoadBalancer(helper).getClass().getName()).isEqualTo("io.grpc.util.RoundRobinLoadBalancer");
        Mockito.verifyZeroInteractions(helper);
    }
}

