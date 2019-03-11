/**
 * Copyright 2015 Victor Albertos
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
package io.rx_cache2.internal.migration;


import io.reactivex.observers.TestObserver;
import io.rx_cache2.internal.common.BaseTest;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class UpgradeCacheVersionTest extends BaseTest {
    private UpgradeCacheVersion upgradeCacheVersionUT;

    private GetCacheVersion getCacheVersion;

    private TestObserver<Integer> upgradeTestObserver;

    private TestObserver<Integer> versionTestObserver;

    @Test
    public void When_Upgrade_Version_Upgrade_It() {
        upgradeCacheVersionUT.with(migrations()).react().subscribe(upgradeTestObserver);
        upgradeTestObserver.awaitTerminalEvent();
        upgradeTestObserver.assertNoErrors();
        upgradeTestObserver.assertComplete();
        getCacheVersion.react().subscribe(versionTestObserver);
        versionTestObserver.awaitTerminalEvent();
        int currentVersion = versionTestObserver.values().get(0);
        Assert.assertThat(currentVersion, Is.is(5));
    }

    private static class Mock1 {}
}

