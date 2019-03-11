/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.sync;


import org.junit.Assert;
import org.junit.Test;

import static SyncStatus.SUCCESS;


public class ServerSyncTest {
    private static ProfileServerSync profileServerSync;

    private static ConfigurationServerSync configurationServerSync;

    private static NotificationServerSync notificationServerSync;

    private static UserServerSync userServerSync;

    private static EventServerSync eventServerSync;

    private static RedirectServerSync redirectServerSync;

    private static LogServerSync logServerSync = new LogServerSync();

    @Test
    public void deepCopyNullServerSyncTest() {
        ServerSync copiedServerSync = ServerSync.deepCopy(null);
        Assert.assertNull(copiedServerSync);
    }

    @Test
    public void deepCopyServerSyncTest() {
        ServerSync serverSync = new ServerSync(1, SUCCESS, ServerSyncTest.profileServerSync, ServerSyncTest.configurationServerSync, ServerSyncTest.notificationServerSync, ServerSyncTest.userServerSync, ServerSyncTest.eventServerSync, ServerSyncTest.redirectServerSync, ServerSyncTest.logServerSync);
        ServerSync serverSyncCopy = ServerSync.deepCopy(serverSync);
        Assert.assertEquals(serverSync, serverSyncCopy);
        Assert.assertFalse((serverSync == serverSyncCopy));
    }
}

