/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.wan;


import com.hazelcast.internal.ascii.HTTPCommunicator;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class WanRESTTest extends HazelcastTestSupport {
    private WanReplicationService wanServiceMock;

    private HTTPCommunicator communicator;

    @Test
    public void pauseSuccess() throws Exception {
        assertSuccess(communicator.wanPausePublisher("atob", "B"));
        Mockito.verify(wanServiceMock, Mockito.times(1)).pause("atob", "B");
    }

    @Test
    public void stopSuccess() throws Exception {
        assertSuccess(communicator.wanStopPublisher("atob", "B"));
        Mockito.verify(wanServiceMock, Mockito.times(1)).stop("atob", "B");
    }

    @Test
    public void resumeSuccess() throws Exception {
        assertSuccess(communicator.wanResumePublisher("atob", "B"));
        Mockito.verify(wanServiceMock, Mockito.times(1)).resume("atob", "B");
    }

    @Test
    public void consistencyCheckSuccess() throws Exception {
        assertSuccess(communicator.wanMapConsistencyCheck("atob", "B", "mapName"));
        Mockito.verify(wanServiceMock, Mockito.times(1)).consistencyCheck("atob", "B", "mapName");
    }

    @Test
    public void pauseFail() throws Exception {
        Mockito.doThrow(new RuntimeException("Error occurred")).when(wanServiceMock).pause("atob", "B");
        assertFail(communicator.wanPausePublisher("atob", "B"));
        Mockito.verify(wanServiceMock, Mockito.times(1)).pause("atob", "B");
    }

    @Test
    public void stopFail() throws Exception {
        Mockito.doThrow(new RuntimeException("Error occurred")).when(wanServiceMock).stop("atob", "B");
        assertFail(communicator.wanStopPublisher("atob", "B"));
        Mockito.verify(wanServiceMock, Mockito.times(1)).stop("atob", "B");
    }

    @Test
    public void resumeFail() throws Exception {
        Mockito.doThrow(new RuntimeException("Error occurred")).when(wanServiceMock).resume("atob", "B");
        assertFail(communicator.wanResumePublisher("atob", "B"));
        Mockito.verify(wanServiceMock, Mockito.times(1)).resume("atob", "B");
    }

    @Test
    public void consistencyCheckFail() throws Exception {
        Mockito.doThrow(new RuntimeException("Error occurred")).when(wanServiceMock).consistencyCheck("atob", "B", "mapName");
        assertFail(communicator.wanMapConsistencyCheck("atob", "B", "mapName"));
        Mockito.verify(wanServiceMock, Mockito.times(1)).consistencyCheck("atob", "B", "mapName");
    }
}

