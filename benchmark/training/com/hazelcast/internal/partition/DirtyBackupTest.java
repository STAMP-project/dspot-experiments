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
package com.hazelcast.internal.partition;


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.tcp.OperationPacketFilter;
import com.hazelcast.nio.tcp.PacketFilter;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.hazelcast.nio.tcp.PacketFilter.Action.ALLOW;
import static com.hazelcast.nio.tcp.PacketFilter.Action.DELAY;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DirtyBackupTest extends PartitionCorrectnessTestSupport {
    @Test
    public void testPartitionData_withoutAntiEntropy() {
        startInstancesAndFillPartitions(false);
        assertSizeAndDataEventually(true);
    }

    @Test
    public void testPartitionData_withAntiEntropy() {
        startInstancesAndFillPartitions(true);
        assertSizeAndDataEventually(false);
    }

    private static class BackupPacketReorderFilter extends OperationPacketFilter implements PacketFilter {
        BackupPacketReorderFilter(InternalSerializationService serializationService) {
            super(serializationService);
        }

        @Override
        protected PacketFilter.Action filterOperation(Address endpoint, int factory, int type) {
            boolean isBackup = (factory == (SpiDataSerializerHook.F_ID)) && (type == (SpiDataSerializerHook.BACKUP));
            return isBackup ? DELAY : ALLOW;
        }
    }
}

