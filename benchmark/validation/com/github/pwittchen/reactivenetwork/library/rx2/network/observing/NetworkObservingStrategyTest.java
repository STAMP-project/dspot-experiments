/**
 * Copyright (C) 2016 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pwittchen.reactivenetwork.library.rx2.network.observing;


import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.LollipopNetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.PreLollipopNetworkObservingStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("NullAway")
public class NetworkObservingStrategyTest {
    @Test
    public void lollipopObserveNetworkConnectivityShouldBeConnectedWhenNetworkIsAvailable() {
        // given
        final NetworkObservingStrategy strategy = new LollipopNetworkObservingStrategy();
        // when
        assertThatIsConnected(strategy);
    }

    @Test
    public void preLollipopObserveNetworkConnectivityShouldBeConnectedWhenNetworkIsAvailable() {
        // given
        final NetworkObservingStrategy strategy = new PreLollipopNetworkObservingStrategy();
        // when
        assertThatIsConnected(strategy);
    }
}

