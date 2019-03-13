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
package io.grpc.grpclb;


import io.grpc.ChannelLogger;
import io.grpc.LoadBalancer.Helper;
import io.grpc.SynchronizationContext;
import io.grpc.internal.FakeClock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Unit test for {@link GrpclbLoadBalancerFactory}.
 */
@RunWith(JUnit4.class)
public class GrpclbLoadBalancerFactoryTest {
    private final FakeClock clock = new FakeClock();

    private final SynchronizationContext syncContext = new SynchronizationContext(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            throw new AssertionError(e);
        }
    });

    @SuppressWarnings("deprecation")
    @Test
    public void getInstance() {
        Helper helper = Mockito.mock(Helper.class);
        Mockito.when(helper.getSynchronizationContext()).thenReturn(syncContext);
        Mockito.when(helper.getScheduledExecutorService()).thenReturn(clock.getScheduledExecutorService());
        Mockito.when(helper.getAuthority()).thenReturn("fakeauthority");
        Mockito.when(helper.getChannelLogger()).thenReturn(Mockito.mock(ChannelLogger.class));
        assertThat(GrpclbLoadBalancerFactory.getInstance().newLoadBalancer(helper)).isInstanceOf(GrpclbLoadBalancer.class);
        Mockito.verify(helper).getSynchronizationContext();
        Mockito.verify(helper).getScheduledExecutorService();
        Mockito.verify(helper).getAuthority();
        Mockito.verify(helper).getChannelLogger();
        Mockito.verifyNoMoreInteractions(helper);
    }
}

